/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     Flaivio Stutz - bug fix for getEntryAsURL
package org.eclipse.persistence.internal.jpa.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.persistence.jpa.Archive;

/**
 * This is an implementation of {@link Archive} when container returns a url
 * that is not one of the familiar URL types like file or jar URLs. So, we can
 * not recursively walk thru' its hierarchy. As a result {@link #getEntries()}
 * returns an empty collection.
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class URLArchive extends ArchiveBase implements Archive {
    /*
     * Implementation Note: This class does not have any dependency on either
     * EclipseLink or GlassFish implementation classes. Please retain this separation.
     */

    public URLArchive(URL url, String descriptorLocation) {
        super(url, descriptorLocation);
    }


    public Iterator<String> getEntries() {
        return Collections.EMPTY_LIST.iterator();
    }

    public InputStream getEntry(String entryPath) throws IOException {
        URL subEntry = new URL(rootURL, entryPath);
        InputStream is = null;
        try {
            is = subEntry.openStream();
        } catch (IOException ioe) {
            // we return null when entry does not exist
        }
        return is;
    }

    public URL getEntryAsURL(String entryPath) throws IOException {
        URL subEntry = new URL(rootURL, entryPath);
        try {
            InputStream is = subEntry.openStream();
            if (is == null){
                return null;
            }
            is.close();
        } catch (IOException ioe) {
            return null; // return null when entry does not exist
        }
        return subEntry;
    }

    public void close() {
        // nothing to close. it's caller's responsibility to close
        // any InputStream returned by getEntry().
    }
}
