/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.xml.accessors;

import org.eclipse.persistence.internal.jpa.xml.XMLHelper;

/**
 * An XMLAccessor should implement this interface.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public interface XMLAccessor {
    /**
     * INTERNAL:
     */
    public String getCatalog();
    
    /**
     * INTERNAL:
     */
    public String getDocumentName();
    
    /**
     * INTERNAL:
     */
    public String getSchema();
    
    /**
     * INTERNAL:
     */
    public XMLHelper getHelper();
}
    