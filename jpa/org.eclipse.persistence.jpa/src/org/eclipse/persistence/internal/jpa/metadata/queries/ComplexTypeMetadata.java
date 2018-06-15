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
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to hold onto complex type meta-data, including PL/SQL records
 * and collections, as well as advanced Oracle JDBC types.
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
public abstract class ComplexTypeMetadata extends ORMetadata {
    protected String name;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ComplexTypeMetadata(String element) {
        super(element);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ComplexTypeMetadata(MetadataAnnotation record, MetadataAccessor accessor) {
        super(record, accessor);
        this.name = record.getAttributeString("name");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return objectToCompare instanceof ComplexTypeMetadata &&
                valuesMatch(this.name, ((ComplexTypeMetadata) objectToCompare).getName());
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    /**
     * INTERNAL:
     * The unique identifier of named subgraph metadata.
     */
    @Override
    public String getIdentifier() {
        return getName();
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return name;
    }

    /**
     * Build a runtime type from the meta-data.
     */
    public abstract ComplexDatabaseType process();

    /**
     * Build a runtime record type from the meta-data.
     */
    protected void process(ComplexDatabaseType type) {
        type.setTypeName(this.name);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Indicates an instance of OracleComplexTypeMetadata.
     */
    public boolean isOracleComplexTypeMetadata() {
        return false;
    }
    /**
     * Indicates an instance of PLSQLComplexTypeMetadata.
     */
    public boolean isPLSQLComplexTypeMetadata() {
        return false;
    }
}
