/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories;


/**
 * INTERNAL:
 * <p><b>Purpose</b>: Provide a mechanism for retrieving the DTD file
 * from the classpath
 *
 * @see org.xml.sax.EntityResolver
 * @since TopLink 10.1.3
 * @author Guy Pelletier
 */
public class WASPersistenceEntityResolver extends PersistenceEntityResolver {
    protected static final String dtdFileName_903 = "toplink-was-ejb-jar_903.dtd";
    protected static final String dtdFileName_904 = "toplink-was-ejb-jar_904.dtd";
    protected static final String docTypeId_903 = "-//Oracle Corp.//DTD TopLink 4.5 CMP for WebSphere//EN";
    protected static final String docTypeId_904 = "-//Oracle Corp.//DTD TopLink CMP WebSphere 9.0.4//EN";

    /**
     * INTERNAL:
     */
    public WASPersistenceEntityResolver() {
        super();
    }

    /**
     * INTERNAL:
     */
    protected void populateLocalResources() {
        m_localResources.put(docTypeId_903, dtdFileName_903);
        m_localResources.put(docTypeId_904, dtdFileName_904);
    }
}