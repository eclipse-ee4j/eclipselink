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
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ConverterAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.MapKeyMapping;

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
     * TODO: don't think we need is forMapKey passed in here .. figure it out
     * in the convert metadata attribute name.
     */
    // Likely have to pass in the mapping accessor.
    public void process(DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        // If this convert metadata doesn't have text then process it. Otherwise,
        // return since this is a legacy specificiation that will have been 
        // processed already.
        if (! hasText()) {
            boolean applyToKey = false;
            DatabaseMapping mappingToApplyConvert = mapping;
            
            // Process the attribute name first if there is one.
            if (hasAttributeName()) {
                // The attributeName element must be specified unless the 
                // Convert annotation is on an attribute of basic type or on an 
                // element collection of basic type.  In these cases, the 
                // attributeName element must not be specified.
                if (mapping.isAggregateMapping()) {
                    throw new RuntimeException("Work in progress. Converts for embedded mappings not supported yet.");
                } else if (mapping.isMapKeyMapping()) {
                    MappedKeyMapContainerPolicy policy = (MappedKeyMapContainerPolicy) mapping.getContainerPolicy();

                    // TODO: look up the 
                    if (getAttributeName().startsWith(KEY + ".")) {
                        mappingToApplyConvert = (DatabaseMapping) policy.getKeyMapping();
                        
                        // may have to dig further if the mapping is embedded and more attribute dot name to look at.
                    } else {
                        // may have to dig further if the mapping is embedded and more attribute dot name to look at.
                        mappingToApplyConvert = (DatabaseMapping) policy.getValueMapping();
                    }
                    
                    // If either is an aggregate do more shit ...
                } else {
                    // Likely could just silenty ignore.
                    throw new RuntimeException("Attribute name should not be specified.");
                }
            } else {
                // No attribute name specified, make some validation checks.
                if (mapping.isAggregateMapping()) {
                    throw new RuntimeException("Convert applied to embedded mapping with no attribute name specified");
                }
            }
            
            if (hasConverterClass()) {
                // TODO: Check the attribute type (might be for a key or an embedded attribute)
                ConverterAccessor converterAccessor = getProject().getConverterAccessor(m_converterClass);
                
                if (converterAccessor == null) {
                    // TODO: new exception
                    throw new RuntimeException("Converter class  not found: " + m_converterClass);
                } else {
                    converterAccessor.process(mappingToApplyConvert, applyToKey);
                }
            } else {
                if (getProject().hasAutoApplyConverter(referenceClass) && ! disableConversion()) {
                    getProject().getAutoApplyConverter(referenceClass).process(mapping, applyToKey);
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
