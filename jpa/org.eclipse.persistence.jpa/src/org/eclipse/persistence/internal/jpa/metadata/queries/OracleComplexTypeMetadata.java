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
//     David McCann - Jan.10, 2013 - 2.5.0 - initial API and implementation
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto Oracle complex type meta-data.
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
 * @author David McCann
 * @since EclipseLink 2.5
 */
public abstract class OracleComplexTypeMetadata extends ComplexTypeMetadata {
    private String javaType;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public OracleComplexTypeMetadata(String element) {
        super(element);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public OracleComplexTypeMetadata(MetadataAnnotation record, MetadataAccessor accessor) {
        super(record, accessor);
        this.javaType = record.getAttributeString("javaType");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) &&
                objectToCompare instanceof OracleComplexTypeMetadata &&
                valuesMatch(this.javaType, ((OracleComplexTypeMetadata) objectToCompare).getJavaType());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        return result;
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

        if (this.javaType != null) {
            type.setJavaType(getJavaClass(getMetadataClass(this.javaType)));
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    /**
     * Indicates an instance of OracleComplexTypeMetadata.
     */
    @Override
    public boolean isOracleComplexTypeMetadata() {
        return true;
    }

    /**
     * Indicates an instance of OracleArrayTypeMetadata.
     * @return
     */
    public boolean isOracleArrayTypeMetadata() {
        return false;
    }

    /**
     * Indicates an instance of OracleObjectTypeMetadata.
     * @return
     */
    public boolean isOracleObjectTypeMetadata() {
        return false;
    }
}
