/*
 * Copyright 2003-2005 Michael Franken, Zilverline.
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

import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.analysis.Analyzer;

import org.zilverline.core.FileSystemCollection;

/**
 * @author michael
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TestClassFinder extends TestCase {

    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(TestClassFinder.class);

    private Class[] analyzers = null;

    public void testLocateClass() {
        Class pClass = FileSystemCollection.class;
        // get the name of the class (e.g. my.package.MyClass)
        String className = pClass.getName();

        // create the resource path for this class
        String resourcePath = "/" + className.replace('.', '/') + ".class";

        // get the resource as a url (e.g.
        // file:/C:/temp/my/package/MyClass.class
        URL classURL = pClass.getResource(resourcePath);
        log.debug(classURL);
        log.debug(classURL.getPath());
    }

    public void testClassFind() {
        try {
            ClassFinder.setJavaClassPath("lib\\lucene-analyzers-1.9-final.jar");
            analyzers = ClassFinder.getInstantiableSubclasses(Analyzer.class);
            for (int i = 0; i < analyzers.length; i++) {
                Class clazz = analyzers[i];
                log.debug(analyzers[i].getName());
            }
            assertEquals("Number of jars in analyzers is 9" , 9, analyzers.length);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
