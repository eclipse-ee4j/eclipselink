/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.nosql;

//import org.eclipse.persistence.annotations.DataFormatType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Defines the metadata for the @EIS annotation for mapping
 * an EISDescriptor.
 * 
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
public class NoSqlMetadata extends ORMetadata {
    private String dataType;
    private String dataFormat;
    
    /**
     * Used for XML loading.
     */
    public NoSqlMetadata() {
        super("<eis>");
    }
    
    /**
     * Used for annotation loading.
     */
    public NoSqlMetadata(MetadataAnnotation struct, MetadataAccessor accessor) {
        super(struct, accessor);

        this.dataType = (String)struct.getAttribute("dataType");
        this.dataFormat = (String)struct.getAttribute("dataFormat");
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

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
    
    /**
     * Switch the descriptor to the correct type and
     * set the data-type name and format.
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
}
    
