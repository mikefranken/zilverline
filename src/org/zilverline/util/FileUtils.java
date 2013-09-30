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

package org.zilverline.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Bunch of utilities for File manipulation.
 * 
 * @author Michael Franken
 * @version $Revision: 1.28 $
 */
public final class FileUtils {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(FileUtils.class);

    /**
     * Hidden default constructor.
     */
    private FileUtils() {
    }

    /**
     * Determine whether a file or directory is actually a symbolic link.
     * 
     * @param file the file or directory to check
     * @return true if so
     */
    public static boolean isLink(final File file) {
        try {
            String os = System.getProperty("os.name");
            if (os.indexOf("Windows") >= 0) {
                return false;
            }
            if (file == null || !file.exists()) {
                return false;
            } else {
                String cnnpath = file.getCanonicalPath();
                String abspath = file.getAbsolutePath();
                log.debug("comparing " + cnnpath + " and " + abspath);
                return !abspath.equals(cnnpath);
            }
        }
        catch (IOException e) {
            log.warn("could not determine whether " + file.getAbsolutePath() + " is a symbolic link", e);
            return false;
        }
    }

    /**
     * Recursively remove a directory.
     * 
     * @param sourceDir the Directory to be removed
     * 
     * @return true on success, false otherwise.
     *         <p>
     */
    public static boolean removeDir(final File sourceDir) {
        // try {
        // org.apache.commons.io.FileUtils.deleteDirectory(sourceDir);
        // } catch (IOException e) {
        // log.warn("could not delete " + sourceDir, e);
        // return false;
        // }
        // log.debug("Succesfully removed directory: " + sourceDir);
        // return true;

        if (sourceDir == null) {
            return false;
        }

        boolean allsuccess = true;
        boolean success = true;
        int nrOfFilesDeleted = 0;
        int nrOfDirsDeleted = 0;

        if (sourceDir.isDirectory()) {
            File[] files = sourceDir.listFiles();

            // I've seen listFiles return null, so be carefull, guess dir names too long for OS
            if (files == null) {
                log.warn("Something funny with '" + sourceDir + "'. Name or path too long?");
                log.warn("Could not delete '" + sourceDir + "' from cache");

                // see whether we can rename the dir
                if (sourceDir.renameTo(new File(sourceDir.getParent(), "1"))) {
                    log.warn("Renamed '" + sourceDir + "'");

                    return removeDir(sourceDir); // try again
                } else {
                    log.warn("Could not rename '" + sourceDir + "' to '" + sourceDir.getParent() + "1'");
                }

                return false;
            }

            log.debug(sourceDir + ": is a directory with " + files.length + " docs");

            for (int i = 0; i < files.length; i++) {
                log.debug("removing " + files[i]);

                if (files[i].isDirectory()) {
                    success = removeDir(files[i]);
                } else {
                    success = files[i].delete();
                }

                if (!success) {
                    log.warn("could not delete " + files[i] + " from cache");
                } else {
                    nrOfFilesDeleted++;
                }

                allsuccess = allsuccess && success;
            }

            log.debug("removing " + sourceDir);
            success = sourceDir.delete();

            if (!success) {
                log.warn("could not delete " + sourceDir + " from cache");
            } else {
                nrOfDirsDeleted++;
            }

            allsuccess = allsuccess && success;
        }

        // TODO: make this info at outer level of recursion
        log.debug("Deleted: " + nrOfDirsDeleted + " directories and " + nrOfFilesDeleted + " files from " + sourceDir);
        log.debug("Exiting removeDir for: " + sourceDir + ", " + allsuccess);

        return allsuccess;
    }

    /**
     * Determine whether File is somewhere within Directory.
     * 
     * @param file the File.
     * @param dir the Directory.
     * 
     * @return true, if so.
     */
    public static boolean isIn(final File file, final File dir) {
        if ((file == null) || !file.isFile()) {
            return false;
        }

        if ((dir == null) || !dir.isDirectory()) {
            return false;
        }

        String fileString;
        String directoryString;

        try {
            directoryString = dir.getCanonicalPath();
            fileString = file.getCanonicalPath();

            return fileString.startsWith(directoryString);
        }
        catch (IOException e) {
            log.error("Can't determine whether file is in Dir", e);
        }

        return false;
    }

    /**
     * Get the casesensitive extension (without the '.') of a file.
     * 
     * @param sourceFile the File the extension is extracted from.
     * 
     * @return extension, empty string if no extension.
     */
    public static String getExtension(final File sourceFile) {
        if (sourceFile == null) {
            return "";
        }

        // get the extension of the source file
        int index = sourceFile.getName().lastIndexOf('.');

        if (index != -1) {
            return sourceFile.getName().substring(index + 1);
        }

        return "";
    }

    /**
     * Create a new directory in the given directory, with prefix and postfix.
     * 
     * @param sourceFile the sourceFile to use for the new directory
     * @param dir the (existing) directory to create the directory in.
     * 
     * @return newly created Directory or null.
     * @throws IOException directory can't be created
     */
    public static File createTempDir(final File sourceFile, final File dir) throws IOException {
        File unZipDestinationDirectory = null;

        try {
            // get the full path (not just the name, since we could have recursed into newly created directory)
            String destinationDirectory = sourceFile.getCanonicalPath();

            log.debug("destinationDirectory: " + destinationDirectory);

            // change extension into _
            int index = destinationDirectory.lastIndexOf('.');
            String extension;

            if (index != -1) {
                extension = destinationDirectory.substring(index + 1);
                destinationDirectory = destinationDirectory.substring(0, index) + '_' + extension;
            }

            // actually create the directory
            unZipDestinationDirectory = new File(destinationDirectory);
            boolean canCreate = unZipDestinationDirectory.mkdirs();

            if (!canCreate) {
                log.warn("Could not create: " + unZipDestinationDirectory);
            }

            log.debug("Created: " + unZipDestinationDirectory + " from File: " + sourceFile);
        }
        catch (Exception e) {
            log.error("error creating directory from file: " + sourceFile, e);
        }

        return unZipDestinationDirectory;
    }

    /**
     * Get the casesensitive basename (without the '.') of a file.
     * 
     * @param sourceFile the File the basename is extracted from.
     * 
     * @return basename, entire name if no extension.
     */
    public static String getBasename(final File sourceFile) {
        if (sourceFile == null) {
            return "";
        }

        // get the basename of the source file
        int index = sourceFile.getName().lastIndexOf('.');

        if (index != -1) {
            return sourceFile.getName().substring(0, index);
        }

        return sourceFile.getName();
    }

    /**
     * Get the MD5 hash (unique identifier based on contents) of a file.
     * 
     * <p>
     * N.B. This is an expensive operation, since the entire file is read.
     * </p>
     * 
     * @param sourceFile the File the MD5 hash is created from, can take null or not a normalFile
     * 
     * @return MD5 hash of file as a String, null if it can't create a hash.
     */
    public static String getMD5Hash(final File sourceFile) {
        log.debug("Getting MD5 hash for " + sourceFile);

        final char[] HEX = "0123456789abcdef".toCharArray();

        if (sourceFile == null || !sourceFile.isFile()) {
            log.error("Error creating MD5 Hash for " + sourceFile);
            return null;
        }
        BufferedInputStream bis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // IMessageDigest md = HashFactory.getInstance("MD5");
            if (md == null) {
                log.error("Error creating MessageDigest for " + sourceFile);
                return null;
            }

            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            md.reset();
            int len = 0;
            byte[] buffer = new byte[8192];
            while ((len = bis.read(buffer)) > -1) {
                md.update(buffer, 0, len);
            }

            byte[] bytes = md.digest();
            if (bytes == null) {
                log.error("MessageDigest has no bytes for " + sourceFile);

                return null;
            }

            // base64? encode the digest
            StringBuffer sb = new StringBuffer(bytes.length * 2);
            int b;
            for (int i = 0; i < bytes.length; i++) {
                b = bytes[i] & 0xFF;
                sb.append(HEX[b >>> 4]);
                sb.append(HEX[b & 0x0F]);
            }

            log.debug("MD5 hash for " + sourceFile + " is " + sb);
            return sb.toString();
        }
        catch (Exception e) {
            log.error("Can't determine MD5 hash for " + sourceFile, e);

            return null;
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException e) {
                    log.warn("Can't close stream for " + sourceFile, e);
                }
            }
        }
    }
}
