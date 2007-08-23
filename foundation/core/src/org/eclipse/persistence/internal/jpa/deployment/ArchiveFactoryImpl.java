/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.deployment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class is written to deal with various URLs that can be returned by
 * {@link javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitRootUrl()}
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class ArchiveFactoryImpl {
    /*
     * Implementation Note: This class does not have any dependency on TopLink
     * or GlassFish implementation classes. Please retain this searation.
     */

    private Logger logger;

    public ArchiveFactoryImpl() {
        this(Logger.global);
    }

    public ArchiveFactoryImpl(Logger logger) {
        this.logger = logger;
    }

    public Archive createArchive(URL url) throws URISyntaxException, IOException {
        logger.entering("ArchiveFactoryImpl", "createArchive", new Object[]{url});
        Archive result;
        String protocol = url.getProtocol();
        logger.logp(Level.FINER, "ArchiveFactoryImpl", "createArchive", "protocol = {0}", protocol);
        
        if ("file".equals(protocol)) {
            URI uri = null;
            try {
                // Attempt to use url.toURI since it will deal with all urls 
                // without special characters and URISyntaxException allows us 
                // to catch issues with special characters. This will handle 
                // URLs that already have special characters replaced such as 
                // URLS derived from searches for persistence.xml on the Java 
                // System class loader
                uri = url.toURI();
            } catch (URISyntaxException exception) {
                // Use multi-argument constructor for URI since single-argument 
                // constructor and URL.toURI() do not deal with special 
                // characters in path
                uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null);
            }
            
            File f = new File(uri);
        
            if (f.isDirectory()) {
                // e.g. file:/tmp/a_ear/ejb_jar
                result = new DirectoryArchive(f);
            } else {
                // e.g. file:/tmp/a_ear/lib/pu.jar
                // It's not a directory. Then it must be a jar file.
                result = new JarFileArchive(new JarFile(f));
            }
        } else if ("jar".equals(protocol)) { // NOI18N
            JarURLConnection conn = JarURLConnection.class.cast(url.openConnection());
            JarEntry je = conn.getJarEntry();
            if (je == null) {
                // e.g. jar:file:/tmp/a_ear/lib/pu.jar!/
                // No entryName specified, hence URL points to a JAR file and
                // not to any entry inside it. Ideally this should have been
                // file:/tmp/a_ear/lib/pu.jar,
                // but containers (e.g.) WebLogic return this kind of URL,
                // so we better handle this in our code to imrove pluggability.
                // Read the entire jar file.
                result = new JarFileArchive(conn.getJarFile());
            } else if (je.isDirectory()) {
                // e.g. jar:file:/tmp/a_ear/b.war!/WEB-INF/classes/
                // entryName [je.getName()] is a directory
                result = new DirectoryInsideJarURLArchive(url);
            } else {
                // some URL (e.g.) jar:file:/tmp/a_ear/b.war!/WEB-INF/lib/pu.jar
                // entryName [je.getName()] is a file, so treat this URL as a
                // URL from which  a JAR format InputStream can be obtained.
                result = new JarInputStreamURLArchive(url);
            }
        } else if (isJarInputStream(url)){
            result = new JarInputStreamURLArchive(url);
        } else {
            result = new URLArchive(url);
        }
        logger.exiting("ArchiveFactoryImpl", "createArchive", result);
        return result;
    }

    /**
     * This method is called for a URL which has neither jar nor file protocol.
     * This attempts to find out if we can treat it as a URL from which a JAR
     * format InputStream can be obtained.
     * @param url
     */
    private boolean isJarInputStream(URL url) throws IOException {
        InputStream in = null;
        try {
        	in = url.openStream();
            if (in == null) { // for directories, we may get InputStream as null
            	return false;
            }
            JarInputStream jis = new JarInputStream(in);
            jis.close();
            return true; // we are successful in creating a Jar format IS
        } catch (IOException ioe) {
            if (in != null) {
            	in.close();
            }
            return false;
        }
    }
}
