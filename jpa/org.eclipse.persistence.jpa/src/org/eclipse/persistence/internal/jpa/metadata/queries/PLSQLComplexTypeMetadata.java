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
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto a PLSQL complex type meta-data.
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
public abstract class PLSQLComplexTypeMetadata extends ComplexTypeMetadata {
    private String compatibleType;
    private String javaType;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public PLSQLComplexTypeMetadata(String element) {
        super(element);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public PLSQLComplexTypeMetadata(MetadataAnnotation record, MetadataAccessor accessor) {
        super(record, accessor);

        this.compatibleType = record.getAttributeString("compatibleType");
        this.javaType = record.getAttributeString("javaType");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof PLSQLRecordMetadata) {
            PLSQLRecordMetadata parameter = (PLSQLRecordMetadata) objectToCompare;

            if (! valuesMatch(this.name, parameter.getName())) {
                return false;
            }

            if (! valuesMatch(this.compatibleType, parameter.getCompatibleType())) {
                return false;
            }

            return valuesMatch(this.javaType, parameter.getJavaType());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (compatibleType != null ? compatibleType.hashCode() : 0);
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCompatibleType() {
        return compatibleType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getJavaType() {
        return javaType;
    }

    /**
     * Build a runtime record type from the meta-data.
     */
    @Override
    protected void process(ComplexDatabaseType type) {
        type.setTypeName(this.name);
        type.setCompatibleType(this.compatibleType);

        if (this.javaType != null) {
            type.setJavaType(getJavaClass(getMetadataClass(this.javaType)));
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCompatibleType(String compatibleType) {
        this.compatibleType = compatibleType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    /**
     * Indicates an instance of PLSQLComplexTypeMetadata.
     */
    @Override
    public boolean isPLSQLComplexTypeMetadata() {
        return true;
    }

    /**
     * Indicates an instance of PLSQLRecordMetadata.
     */
    public boolean isPLSQLRecordMetadata() {
        return false;
    }

    /**
     * Indicates an instance of PLSQLTableMetadata.
     */
    public boolean isPLSQLTableMetadata() {
        return false;
    }
}
