/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/09/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterClass;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

/**
 * Object to represent a converter class.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level) 
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject method.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5
 */
public class ConverterAccessor extends ORMetadata {
    protected String className;
    protected Boolean autoApply;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ConverterAccessor() {
        super("<converter>");
    }
    
    /**
     * INTERNAL:
     */
    public ConverterAccessor(MetadataAnnotation converter, MetadataClass metadataClass, MetadataProject project) {
        super(converter, metadataClass, project);
        
        autoApply = (Boolean) converter.getAttributeBooleanDefaultFalse("autoApply");
    }
    
    /**
     * INTERNAL:
     * Return true if this converter should auto apply
     */
    public boolean autoApply() {
        return autoApply != null && autoApply;
    }
    
    /**
     * INTERNAL:
     * Used for metadata merging.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ConverterAccessor) {
            ConverterAccessor converter = (ConverterAccessor) objectToCompare;
            return valuesMatch(autoApply, converter.getAutoApply());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getAutoApply() {
        return autoApply;
    }

    /**
     * INTERNAL:
     * Return the type this converter will auto apply to.
     */
    public MetadataClass getAutoApplyConvertType() {
        // TODO: Should make sure it implements attribute accessor and has generic types.
        String type = ((MetadataClass) getAccessibleObject()).getGenericType().get(2);
        
        return getMetadataClass(type);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * INTERNAL:
     * To satisfy the abstract getIdentifier() method from ORMetadata.
     */
    @Override
    public String getIdentifier() {
        return getAccessibleObjectName();
    }
    
    /**
     * INTERNAL:
     */
    public String getJavaClassName() {
        return getAccessibleObjectName();
    }

    /**
     * INTERNAL:
     * Entity level merging details.
     */
    @Override
    public void merge(ORMetadata metadata) {
        super.merge(metadata);
        
        ConverterAccessor accessor = (ConverterAccessor) metadata;
        
        // Simple object merging.
        autoApply = (Boolean) mergeSimpleObjects(autoApply, accessor.getAutoApply(), accessor, "@auto-apply");
    }
    
    /**
     * INTERNAL:
     * Process this converter for the given mapping.
     */
    public void process(DatabaseMapping mapping, boolean isForMapKey) {
        if (mapping.isDirectMapMapping() && isForMapKey) {
            ((DirectMapMapping) mapping).setKeyConverter(new ConverterClass(getJavaClassName()));
        } else if (mapping.isDirectCollectionMapping()) {
            ((DirectCollectionMapping) mapping).setValueConverter(new ConverterClass(getJavaClassName()));
        } else if (mapping.isAggregateCollectionMapping()) {
            // TODO: Be nice to support converters on AggregateCollections keys.
            // For now they are silently ignored.
        } else if (mapping.isDirectToFieldMapping()) {
            ((AbstractDirectMapping) mapping).setConverter(new ConverterClass(getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAutoApply(Boolean autoApply) {
        this.autoApply = autoApply;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
