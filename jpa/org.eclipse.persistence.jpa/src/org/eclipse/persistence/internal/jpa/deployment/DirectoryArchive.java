/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.deployment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.persistence.jpa.Archive;

/**
 * This is an implementation of {@link Archive} when container returns a file:
 * url that refers to a directory that contains an exploded jar file.
 * e.g. file:/tmp/a_ear/ejb_jar
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class DirectoryArchive extends ArchiveBase implements Archive {
    /*
     * Implementation Note: This class does not have any dependency on either
     * EclipseLink or GlassFish implementation classes. Please retain this separation.
     */

    /**
     * The directory this archive represents.
     */
    private File directory;

    /**
     * The file entries that this archive contains.
     */
    private List<String> entries = new ArrayList<String>();

    @SuppressWarnings("unused")
    private Logger logger;

    @SuppressWarnings("deprecation")
    public DirectoryArchive(File directory, String descriptorLocation) throws MalformedURLException {
        this(directory, descriptorLocation, Logger.global);
    }

    public DirectoryArchive(File directory, String descriptorLocation, Logger logger)
            throws MalformedURLException {
        super();
        logger.entering("DirectoryArchive", "DirectoryArchive",
                        new Object[]{directory});
        this.logger = logger;
        if (!directory.isDirectory()) {
            // should never reach here, hence the msg is not internationalized.
            throw new IllegalArgumentException(directory +
                    " is not a directory." + // NOI18N
                    "If it is a jar file, then use JarFileArchive."); // NOI18N
        }
        this.directory = directory;
        rootURL = directory.toURI().toURL();
        this.descriptorLocation = descriptorLocation;
        logger.logp(Level.FINER, "DirectoryArchive", "DirectoryArchive",
                "rootURL = {0}", rootURL);
        init(this.directory, this.directory); // initialize entries
    }

    private void init(File top, File directory) {
        File[] dirFiles = directory.listFiles();
        if (dirFiles != null) {
            for (File file : dirFiles) {
                if (file.isDirectory()) {
                    continue; // exclude dir entries
                }

                // add only the relative path from the top.
                // note: we use unix style path
                String entryName = file.getPath().replace(File.separator, "/") // NOI18N
                    .substring(top.getPath().length() + 1);
                entries.add(entryName);
            }
            File[] subDirs = directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if (subDirs != null) {
                for (File subDir : subDirs) {
                    init(top, subDir); // recursion
                }
            }
        }
    }

    @Override
    public Iterator<String> getEntries() {
        return entries.iterator();
    }

    @Override
    public InputStream getEntry(String entryPath) throws IOException {
        File f = getFile(entryPath);
        InputStream is = f.exists() ? new FileInputStream(f) : null;
        return is;
    }

    @Override
    public URL getEntryAsURL(String entryPath) throws IOException {
        File f = getFile(entryPath);
        URL url = f.exists() ? f.toURI().toURL() : null;
        return url;
    }

    private File getFile(String entryPath) {
        File f = new File(directory, entryPath);
        return f;
    }

    @Override
    public void close() {
        // nothing to close. it's caller's responsibility to close
        // any InputStream returned by getEntry().
    }

}

