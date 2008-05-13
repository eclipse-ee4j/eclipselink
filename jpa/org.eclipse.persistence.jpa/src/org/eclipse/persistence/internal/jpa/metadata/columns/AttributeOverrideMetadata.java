/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metadata.columns;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.internal.jpa.metadata.accessors.AccessMethodsMetadata;

/**
 * Object to hold onto an attribute override meta data.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class AttributeOverrideMetadata extends OverrideMetadata {
    private ColumnMetadata m_column;
    private AccessMethodsMetadata m_accessMethods;

    /**
     * INTERNAL:
     */
    public AttributeOverrideMetadata() {}
    
    /**
     * INTERNAL:
     */
    public AttributeOverrideMetadata(Annotation attributeOverride, String className) {
        super(className);

        setName((String) invokeMethod("name", attributeOverride));
        m_column = new ColumnMetadata((Annotation) invokeMethod("column", attributeOverride), getName());
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public AccessMethodsMetadata getAccessMethodsMetadata(){
        return m_accessMethods;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getColumn() {
        return m_column;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAccessMethodsMetadata(AccessMethodsMetadata accessMethodsMetadata){
        this.m_accessMethods = accessMethodsMetadata;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }
}
