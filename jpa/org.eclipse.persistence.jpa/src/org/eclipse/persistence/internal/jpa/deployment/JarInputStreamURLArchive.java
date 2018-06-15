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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import org.eclipse.persistence.jpa.Archive;

/**
 * This is an implementation of {@link Archive} which is used when container
 * returns some form of URL from which an InputStream in jar format can be
 * obtained. e.g. jar:file:/tmp/a_ear/b.war!/WEB-INF/lib/pu.jar
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class JarInputStreamURLArchive extends ArchiveBase implements Archive {
    /*
     * Implementation Note: This class does not have any dependency on either
     * EclipseLink or GlassFish implementation classes. Please retain this separation.
     */

    private List<String> entries = new ArrayList<String>();

    @SuppressWarnings("unused")
    private Logger logger;

    @SuppressWarnings("deprecation")
    public JarInputStreamURLArchive(URL url, String descriptorLocation) throws IOException {
        this(url, descriptorLocation, Logger.global);
    }

    public JarInputStreamURLArchive(URL url, String descriptorLocation, Logger logger) throws IOException {
        super(url, descriptorLocation);
        logger.entering("JarInputStreamURLArchive", "JarInputStreamURLArchive", // NOI18N
                new Object[]{url});
        this.logger = logger;
        init();
    }

    private void init() throws IOException {
        JarInputStream jis = new JarInputStream(
                new BufferedInputStream(rootURL.openStream()));
        try {
            do {
                ZipEntry ze = jis.getNextEntry();
                if (ze == null) {
                    break;
                }
                if (!ze.isDirectory()) {
                    entries.add(ze.getName());
                }
            } while (true);
        } finally {
            jis.close();
        }
    }

    @Override
    public Iterator<String> getEntries() {
        return entries.iterator();
    }

    @Override
    public InputStream getEntry(String entryPath) throws IOException {
        if (!entries.contains(entryPath)) {
            return null;
        }
        JarInputStream jis = new JarInputStream(
                new BufferedInputStream(rootURL.openStream()));
        do {
            ZipEntry ze = jis.getNextEntry();
            if (ze == null) {
                break;
            }
            if (ze.getName().equals(entryPath)) {
                return jis;
            }
        } while (true);

        // don't close the stream, as the caller has to read from it.

        assert(false); // should not reach here
        return null;
    }

    @Override
    public URL getEntryAsURL(String entryPath) throws IOException {
        URL result = entries.contains(entryPath) ?
            result = new URL("jar:"+rootURL+"!/"+entryPath) : null; // NOI18N
        return result;
    }

    @Override
    public void close() {
        // nothing to close. it's caller's responsibility to close
        // any InputStream returned by getEntry().
    }
}
