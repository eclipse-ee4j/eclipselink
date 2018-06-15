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
package org.eclipse.persistence.jpa;

import java.util.Iterator;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Provides an abstraction to deal with various kinds of URLs that can
 * be returned by
 * {@link javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitRootUrl()}
 *
 * @see org.eclipse.persistence.internal.jpa.deployment.ArchiveFactoryImpl ArchiveFactoryImpl
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public interface Archive {
    /*
     * Implementation Note: This class does not have any dependency on either
     * EclipseLink or GlassFish implementation classes. Please retain this separation.
     */

    /**
     * Returns an {@link java.util.Iterator} of the file entries. Each String represents
     * a file name relative to the root of the module.
     */
    Iterator<String> getEntries();

    /**
     * Returns the InputStream for the given entry name. Returns null if no such
     * entry exists. The entry name must be relative to the root of the module.
     *
     * @param entryPath the file name relative to the root of the module.
     * @return the InputStream for the given entry name or null if not found.
     */
    InputStream getEntry(String entryPath) throws IOException;

    /**
     * Returns the URL for the given entry name. Returns null if no such
     * entry exists. The entry name must be relative to the root of the module.
     *
     * @param entryPath the file name relative to the root of the module.
     * @return the URL for the given entry name or null if not found.
     */
    URL getEntryAsURL(String entryPath) throws IOException;

    /**
     * @return the URL that this archive represents.
     */
    URL getRootURL();

    /**
     * @return an input stream on the persistence descriptor.
     */
    InputStream getDescriptorStream() throws IOException;

    /**
     * Close this archive and associated InputStream.
     */
    void close();
}

