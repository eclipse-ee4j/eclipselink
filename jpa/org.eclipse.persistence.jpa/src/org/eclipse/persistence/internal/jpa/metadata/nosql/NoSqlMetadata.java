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
//     Oracle - initial implementation
package org.eclipse.persistence.internal.jpa.metadata.nosql;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Defines the metadata for the @EIS annotation for mapping an EISDescriptor.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
public class NoSqlMetadata extends ORMetadata {
    private String dataType;
    private String dataFormat;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public NoSqlMetadata() {
        super("<eis>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public NoSqlMetadata(MetadataAnnotation struct, MetadataAccessor accessor) {
        super(struct, accessor);

        this.dataType = struct.getAttributeString("dataType");
        this.dataFormat = struct.getAttributeString("dataFormat");
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDataFormat() {
        return dataFormat;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * INTERNAL:
     * Used for XML merging.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof NoSqlMetadata) {
            NoSqlMetadata eis = (NoSqlMetadata) objectToCompare;
            if (! valuesMatch(this.dataType, eis.getDataType())) {
                return false;
            }
            return valuesMatch(this.dataFormat, eis.getDataFormat());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = dataType != null ? dataType.hashCode() : 0;
        result = 31 * result + (dataFormat != null ? dataFormat.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Switch the descriptor to the correct type and set the data-type name and
     * format.
     */
    public void process(MetadataDescriptor descriptor) {
        ClassDescriptor oldDesriptor = descriptor.getClassDescriptor();
        EISDescriptor newDescriptor = new EISDescriptor();
        newDescriptor.setJavaClassName(oldDesriptor.getJavaClassName());
        if (this.dataType != null) {
            newDescriptor.setDataTypeName(getDataType());
        } else {
            String defaultName = Helper.getShortClassName(descriptor.getJavaClassName());
            defaultName = getProject().useDelimitedIdentifier() ? defaultName : defaultName.toUpperCase();
            newDescriptor.setDataTypeName(defaultName);
        }
        if (this.dataFormat != null) {
            if (this.dataFormat.equals("XML")) {
                newDescriptor.setDataFormat(EISDescriptor.XML);
            } else if (this.dataFormat.equals("MAPPED")) {
                newDescriptor.setDataFormat(EISDescriptor.MAPPED);
            } else if (this.dataFormat.equals("INDEXED")) {
                newDescriptor.setDataFormat(EISDescriptor.INDEXED);
            }
        }
        newDescriptor.setAlias("");
        // This is the default, set it in case no existence-checking is set.
        newDescriptor.getQueryManager().checkDatabaseForDoesExist();
        if (oldDesriptor.isAggregateDescriptor()) {
            newDescriptor.descriptorIsAggregate();
        }
        descriptor.setDescriptor(newDescriptor);
        // Also need to switch the descriptor in the project.
        descriptor.getProject().getProject().getOrderedDescriptors().remove(oldDesriptor);
        descriptor.getProject().getProject().getOrderedDescriptors().add(newDescriptor);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}

