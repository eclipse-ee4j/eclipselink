/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;

/**
 * INTERNAL:
 * Object to hold onto a PLSQL record meta-data.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class PLSQLRecordMetadata extends PLSQLComplexTypeMetadata {
    private List<PLSQLParameterMetadata> fields = new ArrayList<PLSQLParameterMetadata>();
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public PLSQLRecordMetadata() {
        super("<plsql-record>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public PLSQLRecordMetadata(MetadataAnnotation record, MetadataAccessor accessor) {
        super(record, accessor);
                
        for (Object field : (Object[]) record.getAttributeArray("fields")) {
            this.fields.add(new PLSQLParameterMetadata((MetadataAnnotation)field, accessor));
        }
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PLSQLRecordMetadata) {
            PLSQLRecordMetadata parameter = (PLSQLRecordMetadata) objectToCompare;            
            
            if (! valuesMatch(this.fields, parameter.getFields())) {
                return false;
            }
        }
        
        return super.equals(objectToCompare);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<PLSQLParameterMetadata> getFields() {
        return fields;
    }
    
    /**
     * Build a runtime record type from the meta-data.
     */
    public PLSQLrecord process(MetadataProject project) {
        PLSQLrecord record = new PLSQLrecord();
        super.process(record, project);
        for (PLSQLParameterMetadata field : this.fields) {
            PLSQLargument argument = new PLSQLargument();
            argument.name = field.getName();
            argument.databaseType = field.getDatabaseTypeEnum(field.getDatabaseType(), project);
            if (field.getLength() != null) {
                argument.length = field.getLength();
            }
            if (field.getPrecision() != null) {
                argument.precision = field.getPrecision();
            }
            if (field.getScale() != null) {
                argument.scale = field.getScale();
            }
            record.addField(argument);
        }
        return record;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setFields(List<PLSQLParameterMetadata> fields) {
        this.fields = fields;
    }
}
