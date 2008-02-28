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
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ArrayList;

/**
 * INTERNAL:
 * Object to hold onto an entity result metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class EntityResultMetadata {
	private Class m_entityClass; // Required in both XML and annotations.
	private List<FieldResultMetadata> m_fieldResults;
	private String m_discriminatorColumn;
	private String m_entityClassName;
    
    /**
     * INTERNAL:
     */
    public EntityResultMetadata() {}

    /**
     * INTERNAL:
     */
    public EntityResultMetadata(Annotation entityResult) {
        m_entityClass = (Class) MetadataHelper.invokeMethod("entityClass", entityResult); 
        m_discriminatorColumn = (String) MetadataHelper.invokeMethod("discriminatorColumn", entityResult);
        
        setFieldResults((Annotation[]) MetadataHelper.invokeMethod("fields", entityResult));
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDiscriminatorColumn() {
    	return m_discriminatorColumn;
    }
    
    /**
     * INTERNAL:
     */
    public Class getEntityClass() {
        return m_entityClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getEntityClassName() {
        return m_entityClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<FieldResultMetadata> getFieldResults() {
        return m_fieldResults;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasFieldResults() {
        return m_fieldResults != null && ! m_fieldResults.isEmpty();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDiscriminatorColumn(String discriminatorColumn) {
    	m_discriminatorColumn = discriminatorColumn;
    }
    
    /**
     * INTERNAL:
     */
    public void setEntityClass(Class entityClass) {
        m_entityClass = entityClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEntityClassName(String entityClassName) {
        m_entityClassName = entityClassName;
    }
    
    /**
     * INTERNAL:
     * Used for population from annotations.
     */
    protected void setFieldResults(Annotation[] fieldResults) {
    	m_fieldResults = new ArrayList<FieldResultMetadata>();
    	
    	for (Annotation fieldResult : fieldResults) {
    		m_fieldResults.add(new FieldResultMetadata(fieldResult));
    	}
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setFieldResults(List<FieldResultMetadata> fieldResults) {
    	m_fieldResults = fieldResults; 
    }
}
