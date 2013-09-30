/*
 * Copyright 2003-2004 Michael Franken, Zilverline.
 *
 * The contents of this file, or the files included with this file, are subject to
 * the current version of ZILVERLINE Collaborative Source License for the
 * Zilverline Search Engine (the "License"); You may not use this file except in
 * compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.zilverline.org.
 *
 * See the License for the rights, obligations and
 * limitations governing use of the contents of the file.
 *
 * The Original and Upgraded Code is the Zilverline Search Engine. The developer of
 * the Original and Upgraded Code is Michael Franken. Michael Franken owns the
 * copyrights in the portions it created. All Rights Reserved.
 *
 */

package org.zilverline.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import org.zilverline.extractors.HTMLExtractor;
import org.zilverline.util.StopWatch;

/**
 * A Collection is a number of documents in a directory that are indexed together.
 * 
 * @author Michael Franken
 * @version $Revision: 1.15 $
 */
public class IMAPCollection extends AbstractCollection {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(IMAPCollection.class);

    // field names
    private static final String F_CD = "content-description";

    private static final String F_CONTENTS = "contents";

    private static final String F_CT = "content-type";

    private static final String F_FOLDER = "folder";

    private static final String F_FROM = "from";

    private static final String F_RECEIVED = "received";

    private static final String F_REPLY_TO = "reply-to";

    private static final String F_SENT = "sent";

    private static final String F_SIZE = "size";

    private static final String F_SUBJECT = "subject";

    private static final String F_TO = "to";

    private static final String F_UID = "uid";

    private static final String F_URL = "url";

    // msg flags
    private static final Flags.Flag[] FLAGS = new Flags.Flag[] { Flags.Flag.ANSWERED, Flags.Flag.DELETED, Flags.Flag.DRAFT,
        Flags.Flag.FLAGGED, Flags.Flag.RECENT, Flags.Flag.SEEN };

    // no toString() in Flags.Flag :(
    private static final String[] SFLAGS = new String[] { "answered", "deleted", "draft", "flagged", "recent", "seen" };

    private static final FetchProfile PROFILE = new FetchProfile();

    static {
        PROFILE.add(FetchProfile.Item.ENVELOPE); // standard headers
        PROFILE.add(FetchProfile.Item.CONTENT_INFO);
        PROFILE.add(UIDFolder.FetchProfileItem.UID);
        PROFILE.add(com.sun.mail.imap.IMAPFolder.FetchProfileItem.HEADERS);
        PROFILE.add(com.sun.mail.imap.IMAPFolder.FetchProfileItem.SIZE);
    }

    private String folder;

    private String host;

    private String password;

    private String user;

    /**
     * Default Constructor setting all fields to non null defaults.
     */
    public IMAPCollection() {
        super();
        name = "";
        url = "";
        description = "";
        numberOfDocs = 0;
        version = 0;
        lastIndexed = null;
        existsOnDisk = false;
        keepCache = false;
        isKeepCacheSet = false;
    }

    /**
     * Gets the origin from where this collection's documents can be retrieved.
     * 
     * @return location such as e:/docs or InBox
     */
    public final String getRoot() {
        if (!StringUtils.hasText(folder)) {
            return "all folders";
        }
        return getFolder();
    }

    public String getFolder() {
        return folder;
    }

    /**
     * @return Returns the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return Returns the user.
     */
    public String getUser() {
        return user;
    }

    /**
     * Index the given Collection.
     * 
     * @param fullIndex indicates whether a full or incremental index should be created
     * @throws IndexException if the Collections can not be indexed
     */
    public final void index(final boolean fullIndex) throws IndexException {
        resetCache(fullIndex);
        doIndex(fullIndex);
    }

    /**
     * Index the given Collection.
     * 
     * @param fullIndex indicates whether a full or incremental index should be created
     * @throws IndexException if the Collections can not be indexed
     * @return true if succesfull
     */
    private final boolean doIndex(boolean fullIndex) throws IndexException {
        IndexWriter writer = null;
        Store store = null;
        try {
            // record start time
            StopWatch watch = new StopWatch();

            watch.start();

            // make sure the index exists
            File indexDirectory = this.getIndexDirWithManagerDefaults();

            // reindex if the index is not there or invalid
            boolean mustReindex = fullIndex;
            if (!this.isIndexValid()) {
                mustReindex = true;
                indexDirectory.mkdirs();
            }

            // create an index(writer)
            writer = new IndexWriter(indexDirectory, this.createAnalyzer(), mustReindex);
            // see whether there are specific indexing settings in manager
            if (manager.getMergeFactor() != null) {
                writer.setMergeFactor(manager.getMergeFactor().intValue());
            }
            if (manager.getMinMergeDocs() != null) {
                writer.setMaxBufferedDocs(manager.getMinMergeDocs().intValue());
            }

            if (manager.getMaxMergeDocs() != null) {
                writer.setMaxMergeDocs(manager.getMaxMergeDocs().intValue());
            }

            resetCache(fullIndex);
            // connect to IMAP
            log.debug("Connecting to IMAP server: " + host);
            Properties props = System.getProperties();
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imap");
            log.debug("Connecting to " + host + " as " + user);
            store.connect(host, user, password);
            log.debug("Connected");
            // start at the proper folder
            Folder topFolder = null;
            if (StringUtils.hasText(folder)) {
                topFolder = store.getFolder(folder);
            } else {
                topFolder = store.getDefaultFolder();
            }
            indexFolder(writer, topFolder);
            // record end time and report duration of indexing
            watch.stop();
            log.info("Indexed " + writer.docCount() + " documents in " + watch.elapsedTime());
            return true;
        }
        catch (NoSuchProviderException e) {
            throw new IndexException("Can't connect to " + host, e);
        }
        catch (MessagingException e) {
            throw new IndexException("Error while accessing IMAP server " + host, e);
        }
        catch (IOException e) {
            throw new IndexException("Error indexing '" + this.getName() + "'. Possibly unable to remove old index", e);
        }
        catch (Exception e) {
            throw new IndexException("Error indexing '" + this.getName() + "'", e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.optimize();
                    log.debug("Optimizing index for " + name);
                    writer.close();
                    log.debug("Closing index for " + name);
                }
                catch (IOException e1) {
                    log.error("Error closing Index for " + name, e1);
                }
            }
            if (store != null) {
                try {
                    store.close();
                }
                catch (MessagingException e1) {
                    log.error("Error closing IMAP server " + host, e1);
                }
            }
            init();
        }
    }

    private final boolean indexFolder(IndexWriter writer, Folder thisFolder) throws MessagingException {
        if (stopRequested) {
            log.info("Indexing stops, due to request");
            return false;
        }
        if ((thisFolder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            thisFolder.open(Folder.READ_ONLY);
            Message[] messages = thisFolder.getMessages(); // get refs to all msgs
            if (messages == null) {
                // dummy
                messages = new Message[0];
            }

            thisFolder.fetch(messages, PROFILE); // fetch headers

            log.debug("FOLDER: " + thisFolder.getFullName() + " messages=" + messages.length);

            for (int i = 0; i < messages.length; i++) {
                try {
                    String msgID = null;
                    if (messages[i] instanceof MimeMessage) {
                        MimeMessage mm = (MimeMessage) messages[i];
                        msgID = mm.getMessageID();
                    }
                    if (!md5DocumentCache.contains(msgID)) {
                        log.debug("new message added for message: " + msgID);
                        final Document doc = new Document();
                        doc.add(Field.Keyword(F_FOLDER, thisFolder.getFullName()));
                        doc.add(Field.Keyword("collection", name));
                        // index this message
                        indexMessage(doc, messages[i]);
                        // add it
                        writer.addDocument(doc);
                        md5DocumentCache.add(msgID);
                    } else {
                        log.debug("existing message skipped for message: " + msgID);
                    }
                }
                catch (Exception ioe) {
                    // can be side effect of hosed up mail headers
                    log.warn("Bad Message: " + messages[i], ioe);
                    continue;
                }
            }

        }
        // recurse if possible
        if ((thisFolder.getType() & Folder.HOLDS_FOLDERS) != 0) {
            Folder[] far = thisFolder.list();
            if (far != null) {
                for (int i = 0; i < far.length; i++) {
                    indexFolder(writer, far[i]);
                }
            }
        }
        if (thisFolder.isOpen()) {
            log.debug("Closing folder: " + thisFolder.getFullName());
            thisFolder.close(false); // false => do not expunge
        }

        return true;
    }

    /**
     * Index one message.
     */
    private void indexMessage(final Document doc, final Message m) throws MessagingException, IOException {
        if (stopRequested) {
            log.info("Indexing stops, due to request");
            return;
        }
        final long uid = ((UIDFolder) m.getFolder()).getUID(m);

        // form a URL that mozilla seems to accept. Couldn't get it to accept
        // what I thought was the standard

        String urlPrefix = "imap://" + user + "@" + host + ":143/fetch%3EUID%3E/";

        final String url = urlPrefix + m.getFolder().getFullName() + "%3E" + uid;
        doc.add(Field.Text("name", url));

        final String subject = m.getSubject();
        final Date recv = m.getReceivedDate();
        final Date sent = m.getSentDate();
        log.info("Folder: " + m.getFolder().getFullName() + ": Message received " + recv + ", subject: " + subject);
        // -------------------------------------------------------
        // data gathered, now add to doc

        if (subject != null) {
            doc.add(Field.Text(F_SUBJECT, m.getSubject()));
            doc.add(Field.Text("title", m.getSubject()));
        }

        if (recv != null) {
            doc.add(Field.Keyword(F_RECEIVED, DateTools.timeToString(recv.getTime(), DateTools.Resolution.SECOND)));
        }

        if (sent != null) {
            doc.add(Field.Keyword(F_SENT, DateTools.timeToString(sent.getTime(), DateTools.Resolution.SECOND)));
            // store date as yyyyMMdd
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            String dfString = df.format(new Date(sent.getTime()));
            doc.add(Field.Keyword("modified", dfString));
        }

        doc.add(Field.Keyword(F_URL, url));

        Address[] addrs = m.getAllRecipients();
        if (addrs != null) {
            for (int j = 0; j < addrs.length; j++) {
                doc.add(Field.Keyword(F_TO, "" + addrs[j]));
            }
        }

        addrs = m.getFrom();
        if (addrs != null) {
            for (int j = 0; j < addrs.length; j++) {
                doc.add(Field.Keyword(F_FROM, "" + addrs[j]));
                doc.add(Field.Keyword("author", "" + addrs[j]));
            }
        }
        addrs = m.getReplyTo();
        if (addrs != null) {
            for (int j = 0; j < addrs.length; j++) {
                doc.add(Field.Keyword(F_REPLY_TO, "" + addrs[j]));
            }
        }

        doc.add(Field.Keyword(F_UID, "" + uid));

        // could ignore docs that have the deleted flag set
        for (int j = 0; j < FLAGS.length; j++) {
            boolean val = m.isSet(FLAGS[j]);
            doc.add(Field.Keyword(SFLAGS[j], (val ? "true" : "false")));
        }

        // now special case for mime
        if (m instanceof MimeMessage) {
            // mime++;
            MimeMessage mm = (MimeMessage) m;
            log.debug("index, adding MimeMessage " + m.getFileName());
            indexMimeMessage(doc, mm);

        } else {
            // nmime++;

            final DataHandler dh = m.getDataHandler();
            log.debug("index, adding (non-MIME) Content " + m.getFileName());
            doc.add(Field.Text(F_CONTENTS, new InputStreamReader(dh.getInputStream())));
        }
    }

    /**
     * Index a MIME message, which seems to be all of them.
     */
    private void indexMimeMessage(final Document doc, final MimeMessage mm) throws MessagingException, IOException {
        // o.println( "\n\n[index mm]: " + mm.getSubject());

        long size = mm.getSize();
        int lines = mm.getLineCount();
        doc.add(Field.Keyword("hash", mm.getMessageID()));

        if (size > 0) {
            doc.add(Field.UnIndexed(F_SIZE, "" + size));
        } else {
            doc.add(Field.UnIndexed(F_SIZE, "" + 0));
        }
        indexPart(doc, mm);
    }

    /**
     * Index a part.
     */
    private void indexPart(final Document doc, final Part p) throws MessagingException, IOException {
        int size = p.getSize();
        String ct = p.getContentType();
        String cd = p.getDescription();
        log.debug("IndexContent, type: " + ct + ", description: " + cd);
        Object content = null;

        if (ct != null) {
            doc.add(Field.Keyword(F_CT, ct));
        }
        doc.add(Field.Keyword("type", "MAIL"));

        if (cd != null) {
            doc.add(Field.Keyword(F_CD, cd));
        }

        if (ct != null && ct.toLowerCase().startsWith("image/")) {
            // no point for now but maybe in the future we see if any forms such as jpegs have some strings
            return;
        }

        try {
            // get content object, indirectly calls into JAF which decodes based on MIME type and char
            content = p.getContent();
        }
        catch (IOException ioe) {
            log.warn("OUCH decoding attachment, p=" + p, ioe);
            doc.add(Field.Text(F_CONTENTS, new InputStreamReader(p.getInputStream())));
            return;
        }

        if (content instanceof MimeMultipart) {
            int n = ((MimeMultipart) content).getCount();
            for (int i = 0; i < n; i++) {
                BodyPart bp = ((MimeMultipart) content).getBodyPart(i);
                // same thing ends up happening regardless, if/else left it to show structure
                indexPart(doc, bp);
            }
        } else if (content instanceof MimePart) {
            indexPart(doc, (MimePart) content);
        } else if (content instanceof Part) {
            indexPart(doc, (Part) content);
        } else if (content instanceof String) {
            indexString(doc, (String) content, ct);
        } else if (content instanceof InputStream) {
            indexStream(doc, (InputStream) content, ct);
        } else {
            log.error("***** Strange content: " + content + "/" + content.getClass() + " ct=" + ct + " cd=" + cd);
        }
    }

    /**
     * Index a Stream.
     */
    private void indexStream(final Document doc, final InputStream content, final String type) throws MessagingException,
        IOException {
        log.debug("indexStream for type: " + type);
        ExtractorFactory ef = new ExtractorFactory();
        Extractor ex = ef.createExtractor(type);
        if (ex != null) {
            String parsedContent = ex.getContent(content);
            log.info("Adding content");
            doc.add(Field.Text(F_CONTENTS, new StringReader(parsedContent)));
        } else {
            log.warn("indexStream: Unknown mimetype: " + type);
        }
    }

    /**
     * Index a String.
     */
    private void indexString(final Document doc, final String content, final String type) throws MessagingException, IOException {
        log.debug("indexString for type: " + type);
        if (type.toLowerCase().startsWith("text/plain")) {
            log.info("Adding TEXT: ");
            doc.add(Field.Text(F_CONTENTS, new StringReader(content)));
        } else if (type.toLowerCase().startsWith("text/html")) {
            HTMLExtractor he = new HTMLExtractor();
            String parsedContent = he.getContent(content);
            log.info("Adding HTML: ");
            doc.add(Field.Text(F_CONTENTS, new StringReader(parsedContent)));
        } else {
            log.warn("indexString: Unknown mimetype: " + type);
        }
    }

    /**
     * Sets existsOnDisk based on whether the collection (contentDir) actually (now) sits on disk.
     * 
     * @todo the whole existsOnDisk construction is a little funny, refactor some time
     */
    protected void setExistsOnDisk() {
        existsOnDisk = false;
        Store store = null;
        try {
            // try to connect to server and find folder
            log.debug("Connecting to IMAP server: " + host);
            Properties props = System.getProperties();
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imap");
            log.debug("Connecting to " + host + " as " + user);
            store.connect(host, user, password);
            log.debug("Connected");
            // start at the proper folder
            Folder topFolder = null;
            if (StringUtils.hasText(folder)) {
                topFolder = store.getFolder(folder);
            } else {
                topFolder = store.getDefaultFolder();
            }
            existsOnDisk = (topFolder != null);
        }
        catch (NoSuchProviderException e) {
            log.warn("Can't connect to " + host, e);
        }
        catch (MessagingException e) {
            log.warn("Error while accessing IMAP server " + host, e);
        }
        finally {
            if (store != null) {
                try {
                    store.close();
                }
                catch (MessagingException e1) {
                    log.error("Error closing IMAP server " + host, e1);
                }
            }
        }
    }

    public void setFolder(String thisFolder) {
        this.folder = thisFolder;
    }

    /**
     * @param thisHost The host to set.
     */
    public void setHost(String thisHost) {
        this.host = thisHost;
    }

    /**
     * @param thisPassword The password to set.
     */
    public void setPassword(String thisPassword) {
        this.password = thisPassword;
    }

    /**
     * @param thisUser The user to set.
     */
    public void setUser(String thisUser) {
        this.user = thisUser;
    }

    /**
     * Prints Collection as String for logging.
     * 
     * @return pretty formatted information about the collection
     */
    public final String toString() {
        return "Collection(" + id + "), with name: " + name + ",\n\t\tdescription: " + description + ",\n\t\tcontentDir: "
            + contentDir + ",\n\t\turl: " + url + ",\n\t\texistsOnDisk: " + existsOnDisk + ",\n\t\tindexDir: " + indexDir
            + ",\n\t\tcacheDir: " + cacheDir + ",\n\t\tcacheUrl: " + cacheUrl + ",\n\t\tanalyzer: " + analyzer
            + ",\n\t\tkeepCache: " + keepCache + ",\n\t\tisKeepCacheSet: " + isKeepCacheSet + ",\n\t\tnumberOfDocs: "
            + numberOfDocs + ",\n\t\tmanager: " + manager + ",\n\t\tlastIndexed: " + lastIndexed;
        // +
        // ",\n\t\tmd5DocumentCache:
        // " + md5DocumentCache +
        // "\n\n";
    }
}
