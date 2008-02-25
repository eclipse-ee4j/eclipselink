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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

/**
 * Object to hold onto join column metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class JoinColumnsMetadata {
    private List<JoinColumnMetadata> m_joinColumns;
    
    /**
     * INTERNAL:
     */
    public JoinColumnsMetadata(List<JoinColumnMetadata> joinColumns) {
    	// A @JoinTable may default so may send in null.
    	if (joinColumns == null) {
    		m_joinColumns = new ArrayList<JoinColumnMetadata>();	
    	} else {
    		m_joinColumns = joinColumns;
    	}
    }
    
    /**
     * INTERNAL:
     */
    public JoinColumnsMetadata(Object joinColumns, Object joinColumn) {
    	m_joinColumns = new ArrayList<JoinColumnMetadata>();
        
        // Process all the join columns first.
        if (joinColumns != null) {
            for (Object jColumn : (Object[])MetadataHelper.invokeMethod("value", joinColumns, (Object[])null)) {
                m_joinColumns.add(new JoinColumnMetadata(jColumn));
            }
        }
        
        // Process the single key join column second.
        if (joinColumn != null) {
            m_joinColumns.add(new JoinColumnMetadata(joinColumn));
        }
    }
    
    /**
     * INTERNAL:
     */
    public boolean loadedFromXML() {
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public List<JoinColumnMetadata> values(MetadataDescriptor descriptor) {
        // If no join columns are specified ...
        if (m_joinColumns.isEmpty()) {
            if (descriptor.hasCompositePrimaryKey()) {
                // Add a default one for each part of the composite primary
                // key. Foreign and primary key to have the same name.
                for (String primaryKeyField : descriptor.getPrimaryKeyFieldNames()) {
                	JoinColumnMetadata joinColumn = new JoinColumnMetadata();
                	joinColumn.setReferencedColumnName(primaryKeyField);
                	joinColumn.setName(primaryKeyField);
                    m_joinColumns.add(joinColumn);
                }
            } else {
                // Add a default one for the single case, not setting any
                // foreign and primary key names. They will default based
                // on which accessor is using them.
                m_joinColumns.add(new JoinColumnMetadata());
            }
        } else {
            // Need to update any join columns that use a foreign key name
            // for the primary key name. E.G. User specifies the renamed id
            // field name from a primary key join column as the primary key in
            // an inheritance subclass.
            for (JoinColumnMetadata joinColumn : m_joinColumns) {
            	// Doing this could potentially change a value entered in XML.
            	// However, in this case I think that is ok since in theory we 
            	// are writing out the correct value that EclipseLink needs to 
            	// form valid queries.
            	joinColumn.setReferencedColumnName(descriptor.getPrimaryKeyJoinColumnAssociation(joinColumn.getReferencedColumnName()));
            }
        }
        
        return m_joinColumns;
    }
}
