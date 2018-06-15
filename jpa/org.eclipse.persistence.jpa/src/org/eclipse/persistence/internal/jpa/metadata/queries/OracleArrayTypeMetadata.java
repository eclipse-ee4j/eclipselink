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

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleArrayType;

/**
 * INTERNAL:
 * Object to hold onto Oracle array type meta-data.
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
public class OracleArrayTypeMetadata extends OracleComplexTypeMetadata {
    private String nestedType;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public OracleArrayTypeMetadata() {
        super("<oracle-array>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public OracleArrayTypeMetadata(MetadataAnnotation record, MetadataAccessor accessor) {
        super(record, accessor);
        this.nestedType = record.getAttributeString("nestedType");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return super.equals(objectToCompare) &&
                objectToCompare instanceof OracleArrayTypeMetadata &&
                valuesMatch(this.nestedType, ((OracleArrayTypeMetadata) objectToCompare).getNestedType());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (nestedType != null ? nestedType.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getNestedType() {
        return nestedType;
    }

    /**
     * INTERNAL:
     * Build a runtime OracleArrayType from the meta-data.
     */
    @Override
    public OracleArrayType process() {
        OracleArrayType array = new OracleArrayType();
        super.process(array);
        array.setNestedType(getDatabaseTypeEnum(getNestedType()));
        return array;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setNestedType(String nestedType) {
        this.nestedType = nestedType;
    }

    /**
     * Indicates an instance of OracleArrayTypeMetadata.
     * @return
     */
    @Override
    public boolean isOracleArrayTypeMetadata() {
        return true;
    }
}
