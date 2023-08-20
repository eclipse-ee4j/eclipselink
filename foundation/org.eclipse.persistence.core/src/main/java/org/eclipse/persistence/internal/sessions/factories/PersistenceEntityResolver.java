/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Provide a mechanism for retrieving the DTD file
 * from the classpath
 *
 * @see org.xml.sax.EntityResolver
 * @since TopLink 10.1.3
 * @author Gordon Yorke, Guy Pelletier
 */
public class PersistenceEntityResolver implements EntityResolver {
    protected Map<String, String> m_localResources;
    protected static final String dtdFileName40 = "sessions_4_0.dtd";
    protected static final String doctTypeId40 = "-//Oracle Corp.//DTD TopLink for JAVA 4.0//EN";
    protected static final String dtdFileName45 = "sessions_4_5.dtd";
    protected static final String doctTypeId45 = "-//Oracle Corp.//DTD TopLink for JAVA 4.5//EN";
    protected static final String dtdFileName904 = "sessions_9_0_4.dtd";
    protected static final String doctTypeId904 = "-//Oracle Corp.//DTD TopLink Sessions 9.0.4//EN";

    /**
     * INTERNAL:
     */
    public PersistenceEntityResolver() {
        m_localResources = new HashMap<>();
        populateLocalResources();
    }

    /**
     * INTERNAL:
     */
    protected void populateLocalResources() {
        m_localResources.put(doctTypeId40, dtdFileName40);
        m_localResources.put(doctTypeId45, dtdFileName45);
        m_localResources.put(doctTypeId904, dtdFileName904);
    }

    /**
     * INTERNAL:
     */
    protected String getDtdFileName(String docTypeId) {
        return getLocalResources().get(docTypeId);
    }

    /**
     * INTERNAL:
     */
    public Map<String, String> getLocalResources() {
        return m_localResources;
    }

    /**
     * INTERNAL:
     */
    public void setLocalResources(Map<String, String> ht) {
        m_localResources = ht;
    }

    /**
     * INTERNAL:
     */
    public void addLocalResource(String publicId, String localFileName) {
        m_localResources.put(publicId, localFileName);
    }

    /**
     * INTERNAL:
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        for (String docTypeId : m_localResources.keySet()) {
            if ((publicId != null) && publicId.equals(docTypeId)) {
                InputStream localDtdStream;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    try {
                        localDtdStream = AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(getClass())).getResourceAsStream(getDtdFileName(docTypeId));
                    } catch (PrivilegedActionException ex) {
                        throw (RuntimeException) ex.getCause();
                    }
                } else {
                    localDtdStream = PrivilegedAccessHelper.getClassLoaderForClass(getClass()).getResourceAsStream(getDtdFileName(docTypeId));

                }
                if (localDtdStream != null) {
                    return new InputSource(localDtdStream);
                }
            }
        }

        return null;
    }
}
