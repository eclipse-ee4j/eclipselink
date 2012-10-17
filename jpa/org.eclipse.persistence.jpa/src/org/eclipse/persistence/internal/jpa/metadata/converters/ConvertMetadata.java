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
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Object to hold onto convert metadata.
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
 * @author Guy Pelletier
 * @since EclipseLink 2.5
 */
public class ConvertMetadata extends ORMetadata {
    public static final String KEY = "key";
    
    private String m_text;
    
    private Boolean m_disableConversion;
    private MetadataClass m_converterClass;
    
    private String m_converterClassName;
    private String m_attributeName;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ConvertMetadata() {
        super("<convert");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ConvertMetadata(MetadataAnnotation convert, MetadataAccessor accessor) {
        super(convert, accessor);
        
        m_converterClass = getMetadataClass((String) convert.getAttributeClass("converter", Void.class));
        m_attributeName = (String) convert.getAttributeString("attributeName");
        m_disableConversion = (Boolean) convert.getAttributeBooleanDefaultFalse("disableConversion");  
    }
    
    /**
     * INTERNAL:
     * Return true if any auto apply converter should be disabled.
     */
    public boolean disableConversion() {
        return m_disableConversion != null && m_disableConversion;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ConvertMetadata) {
            ConvertMetadata convert = (ConvertMetadata) objectToCompare;
            
            if (! valuesMatch(m_text, convert.getText())) {
                return false;
            }
            
            if (! valuesMatch(m_converterClassName, convert.getConverterClassName())) {
                return false;
            }
            
            if (! valuesMatch(m_attributeName, convert.getAttributeName())) {
                return false;
            }
            
            return valuesMatch(m_disableConversion, convert.getDisableConversion());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getAttributeName() {
        return m_attributeName;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getConverterClass() {
        return m_converterClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getConverterClassName() {
        return m_converterClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getDisableConversion() {
        return m_disableConversion;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     * Any ORMetadata that supported mixed types, that is, text or other
     * metadata should override this method.
     */
    @Override
    protected String getText() {
        return m_text;
    }

    /**
     * INTERNAL:
     */
    public boolean hasAttributeName() {
        return m_attributeName != null && ! m_attributeName.equals("");
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasConverterClass() {
        return m_converterClass != null && ! m_converterClass.isVoid();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize the converter class name.
        m_converterClass = initXMLClassName(m_converterClassName);
    }
    
    /**
     * INTERNAL:
     */
    public void process(DatabaseMapping mapping, MetadataClass referenceClass, ClassAccessor accessor) {
        // If the convert metadata doesn't have text then process it. Otherwise, 
        // return  since this is a legacy specification that will have already 
        // been processed.
        if (! hasText()) {
            boolean isForMapKey = false;
            DatabaseMapping mappingToApplyConvert = null;
            
            // Process the attribute name first if there is one.
            if (hasAttributeName()) {                
                String attributeName = getAttributeName();
                
                if (attributeName.equals(KEY) || attributeName.startsWith(KEY + ".")) {
                    attributeName = getAttributeName().equals(KEY) ? "" : attributeName.substring(KEY.length() + 1);
                    
                    isForMapKey = true;
                    
                    if (! mapping.isMapKeyMapping() && ! mapping.isDirectMapMapping()) {
                        throw ValidationException.invalidMappingForKeyAttributeNameConvert(accessor.getJavaClassName(), mapping.getAttributeName());
                    }
                } 
                
                // If map key mapping, get the key or value mapping that will 
                // receive the converter.
                if (mapping.isMapKeyMapping()) {
                    MappedKeyMapContainerPolicy policy = (MappedKeyMapContainerPolicy) mapping.getContainerPolicy();
                    mappingToApplyConvert = (DatabaseMapping) ((isForMapKey) ? policy.getKeyMapping() : policy.getValueMapping());
                } else {
                    mappingToApplyConvert = mapping;
                }
                
                // If the mapping is an aggregate object mapping, validate the 
                // attribute name existing on the embeddable.
                if (mappingToApplyConvert.isAggregateObjectMapping()) {
                    ClassAccessor embeddableAccessor = getProject().getEmbeddableAccessor(referenceClass);
                    MappingAccessor mappingAccessor = embeddableAccessor.getDescriptor().getMappingAccessor(attributeName);
                    
                    if (mappingAccessor == null) {
                        throw ValidationException.embeddableAttributeNameForConvertNotFound(accessor.getJavaClassName(), mapping.getAttributeName(), embeddableAccessor.getJavaClassName(), attributeName);
                    }
                }
            } else {
                // In an aggregate object case, the attribute name must be specified.
                if (mapping.isAggregateObjectMapping()) {
                    throw ValidationException.missingMappingConvertAttributeName(accessor.getJavaClassName(), mapping.getAttributeName());
                }
                
                mappingToApplyConvert = mapping;
            }
            
            // If we have a converter class, validate its existence and apply.
            if (hasConverterClass()) {
                if (getProject().hasConverterAccessor(getConverterClass())) {
                    getProject().getConverterAccessor(getConverterClass()).process(mappingToApplyConvert, isForMapKey, getAttributeName());
                } else {
                    throw ValidationException.converterClassNotFound(accessor.getJavaClassName(), mapping.getAttributeName(), getConverterClass().getName());
                }
            } else {
                // Check for any auto apply converters.
                if (getProject().hasAutoApplyConverter(referenceClass) && ! disableConversion()) {
                    getProject().getAutoApplyConverter(referenceClass).process(mappingToApplyConvert, isForMapKey, getAttributeName());
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributeName(String attributeName) {
        m_attributeName = attributeName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConverterClassName(String converterClassName) {
        m_converterClassName = converterClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDisableConversion(Boolean disableConversion) {
        m_disableConversion = disableConversion;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setText(String text) {
        m_text = text;
    }
}
