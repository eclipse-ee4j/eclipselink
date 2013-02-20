/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/30/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support       
 *     11/28/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import javax.persistence.AttributeConverter;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
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
    protected MetadataClass attributeClassification;
    protected MetadataClass fieldClassification;
    
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
        
        autoApply = converter.getAttributeBooleanDefaultFalse("autoApply");
        
        initClassificationClasses(metadataClass);
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
    public MetadataClass getAttributeClassification() {
        return attributeClassification;
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
     * Return the field classificaiton type for this converter accessor.
     */
    protected String getClassificationType(boolean disableConversion) {
        return (disableConversion) ? attributeClassification.getName() : fieldClassification.getName(); 
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
     * Do some validation and initialize the attribute converter classficiation
     * classes.
     */
    protected void initClassificationClasses(MetadataClass cls) {
        if (! cls.extendsInterface(AttributeConverter.class)) {
            throw ValidationException.converterClassMustImplementAttributeConverterInterface(cls.getName());
        }

        // By implementing the attribute converter interface, the generic types are confirmed through the compiler.
        attributeClassification = getMetadataClass(((MetadataClass) getAccessibleObject()).getGenericType().get(2));
        fieldClassification = getMetadataClass(((MetadataClass) getAccessibleObject()).getGenericType().get(3));
    }
    
    /**
     * INTERNAL:
     * Any subclass that cares to do any more initialization (e.g. initialize a
     * class) should override this method. 
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        initClassificationClasses((MetadataClass) accessibleObject);
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
    public void process(DatabaseMapping mapping, boolean isForMapKey, String attributeName) {
        process(mapping, isForMapKey, attributeName, false);
    }
    
    /**
     * INTERNAL:
     * Process this converter for the given mapping.
     */
    public void process(DatabaseMapping mapping, boolean isForMapKey, String attributeName, boolean disableConversion) {
        String fieldClassificationName = getClassificationType(disableConversion);
        ConverterClass converterClass = new ConverterClass(getJavaClassName(), isForMapKey, fieldClassificationName, disableConversion);
        
        if (mapping.isDirectMapMapping() && isForMapKey) {
            ((DirectMapMapping) mapping).setKeyConverter(converterClass);
            ((DirectMapMapping) mapping).setDirectKeyFieldClassificationName(fieldClassificationName);
        } else if (mapping.isDirectCollectionMapping()) {
            ((DirectCollectionMapping) mapping).setValueConverter(converterClass);
            ((DirectCollectionMapping) mapping).setDirectFieldClassificationName(fieldClassificationName);
        } else if (mapping.isDirectToFieldMapping()) {
            ((AbstractDirectMapping) mapping).setConverter(converterClass);
            ((AbstractDirectMapping) mapping).setFieldClassificationClassName(fieldClassificationName);
        } else if (mapping.isAggregateObjectMapping()) {
            ((AggregateObjectMapping) mapping).addConverter(converterClass, attributeName);
        }  else if (mapping.isAggregateCollectionMapping()) {
            // TODO: Be nice to support converters on AggregateCollections keys.
            // For now they are silently ignored.
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
