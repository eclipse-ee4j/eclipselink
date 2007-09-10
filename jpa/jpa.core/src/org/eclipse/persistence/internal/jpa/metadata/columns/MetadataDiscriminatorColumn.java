/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorColumn;

/**
 * Object to hold onto discriminator column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataDiscriminatorColumn  {    
    public static final int DEFAULT_LENGTH = 31;
    public static final String DEFAULT_NAME = "DTYPE";
    public static final String DEFAULT_COLUMN_DEFINITION = "";
    public static final String DEFAULT_DISCRIMINATOR_TYPE = DiscriminatorType.STRING.name();
    
    protected DiscriminatorColumn m_discriminatorColumn;
    
    /**
     * INTERNAL:
     */
    protected MetadataDiscriminatorColumn() {}
    
    /**
     * INTERNAL:
     */
    public MetadataDiscriminatorColumn(DiscriminatorColumn discriminatorColumn) {
        m_discriminatorColumn = discriminatorColumn;
    }

    /**
     * INTERNAL:
     */
    public String getColumnDefinition() {
        return (m_discriminatorColumn == null) ? DEFAULT_COLUMN_DEFINITION : m_discriminatorColumn.columnDefinition();
    }
    
    /**
     * INTERNAL:
     */
    public String getDiscriminatorType() {
        return (m_discriminatorColumn == null) ? DEFAULT_DISCRIMINATOR_TYPE : m_discriminatorColumn.discriminatorType().name();
    }
    
    /**
     * INTERNAL:
     */
    public int getLength() {
        return (m_discriminatorColumn == null) ? DEFAULT_LENGTH : m_discriminatorColumn.length();
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return (m_discriminatorColumn == null) ? null : m_discriminatorColumn.name();
    }
}
