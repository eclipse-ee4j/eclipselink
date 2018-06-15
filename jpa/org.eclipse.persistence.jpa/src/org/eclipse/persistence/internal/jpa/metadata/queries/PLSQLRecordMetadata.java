/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;

/**
 * INTERNAL:
 * Object to hold onto a PLSQL record meta-data.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - all metadata mapped from XML should be initialized in the initXMLObject
 *   method.
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

        for (Object field : record.getAttributeArray("fields")) {
            this.fields.add(new PLSQLParameterMetadata((MetadataAnnotation) field, accessor));
        }
    }

    /**
     * INTERNAL:
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

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<PLSQLParameterMetadata> getFields() {
        return fields;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize lists of ORMetadata objects.
        initXMLObjects(fields, accessibleObject);
    }

    /**
     * INTERNAL:
     * Build a runtime record type from the meta-data.
     */
    @Override
    public PLSQLrecord process() {
        PLSQLrecord record = new PLSQLrecord();
        super.process(record);

        for (PLSQLParameterMetadata field : this.fields) {
            PLSQLargument argument = new PLSQLargument();
            argument.name = field.getName();
            argument.databaseType = getDatabaseTypeEnum(field.getDatabaseType());

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

    /**
     * Indicates an instance of PLSQLRecordMetadata.
     */
    @Override
    public boolean isPLSQLRecordMetadata() {
        return true;
    }
}
