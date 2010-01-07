/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa;

import java.util.Iterator;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import org.eclipse.persistence.internal.jpa.deployment.ArchiveFactoryImpl;

/**
 * Provides an abstraction to deal with various kinds of URLs that can
 * be returned by
 * {@link javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitRootUrl()}
 *
 * @see ArchiveFactoryImpl
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
     * 
     * This method is used for discovery of Entities and other parts of a persistence unit
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
     * Close this archive and associated InputStream.
     */
    void close();
}

