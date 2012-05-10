/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.deployment;

import java.net.URL;
import java.net.JarURLConnection;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.persistence.jpa.Archive;

/**
 * This is an implementation of {@link Archive} which is used when container
 * returns a jar: URL. e.g. jar:file:/tmp/a_ear/b.war!/WEB-INF/classes/
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class DirectoryInsideJarURLArchive extends ArchiveBase implements Archive {
    /*
     * Implementation Note: This class does not have any dependency on either
     * EclipseLink or GlassFish implementation classes. Please retain this separation.
     */

    private JarFile jarFile;

    // distance from top of the JarFile to the entry
    private String relativeRootPath;

    private List<String> entries = new ArrayList<String>();

    @SuppressWarnings("unused")
    private Logger logger;

    @SuppressWarnings("deprecation")
    public DirectoryInsideJarURLArchive(URL url, String descriptorLocation) throws IOException {
        this(url, descriptorLocation, Logger.global);
    }

    public DirectoryInsideJarURLArchive(URL url, String descriptorLocation, Logger logger)
            throws IOException {
        super();
        logger.entering("DirectoryInsideJarURLArchive", "DirectoryInsideJarURLArchive",  // NOI18N
                new Object[]{url});
        this.logger = logger;
        assert(url.getProtocol().equals("jar")); // NOI18N
        rootURL = url;
        this.descriptorLocation = descriptorLocation;
        JarURLConnection conn =
                JarURLConnection.class.cast(url.openConnection());
        jarFile = conn.getJarFile();
        logger.logp(Level.FINER, "DirectoryInsideJarURLArchive",
                "DirectoryInsideJarURLArchive", "jarFile = {0}", jarFile);
        relativeRootPath = conn.getEntryName();
        init();
    }
    
    private void init() {
        for(Enumeration<JarEntry> jarEntries = jarFile.entries();
            jarEntries.hasMoreElements();) {
            JarEntry jarEntry = jarEntries.nextElement();
            if(jarEntry.isDirectory()) {
                continue;
            }
            String jarEntryName = jarEntry.getName();
            if (relativeRootPath==null) {
                entries.add(jarEntryName);
            } else if (jarEntryName.startsWith(relativeRootPath)) {
                entries.add(jarEntryName.substring(relativeRootPath.length()));
            }
        }
    }

    public Iterator<String> getEntries() {
        return entries.iterator();
    }

    public InputStream getEntry(String entryPath) throws IOException {
        InputStream is = entries.contains(entryPath) ?
            jarFile.getInputStream(jarFile.getEntry(relativeRootPath + entryPath)) : null;
        return is;
    }

    public URL getEntryAsURL(String entryPath) throws IOException {
        URL result = entries.contains(entryPath) ?
            new URL("jar:"+rootURL+"!/"+ relativeRootPath + entryPath) : null; // NOI18N
        return result;
    }

    public void close() {
        try {
            jarFile.close();
        } catch (IOException e) {
            // ignore
        }
    }
}
