/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     10/30/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.internal.jpa.metadata.converters;

import jakarta.persistence.AttributeConverter;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.mappings.converters.AttributeNamePrefix;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Object to hold onto convert metadata.
 * <p>
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

    private String m_text;
    private Boolean m_isForMapKey;
    private Boolean m_disableConversion;
    private MetadataClass m_converterClass;

    private String m_converterClassName;
    private String m_attributeName;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ConvertMetadata() {
        super("<convert>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ConvertMetadata(MetadataAnnotation convert, MetadataAccessor accessor) {
        super(convert, accessor);

        m_converterClass = getMetadataClass(convert.getAttributeClass("converter", AttributeConverter.class));
        m_attributeName = convert.getAttributeString("attributeName");
        m_disableConversion = convert.getAttributeBooleanDefaultFalse("disableConversion");
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
        if (objectToCompare instanceof ConvertMetadata convert) {

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

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_text != null ? m_text.hashCode() : 0);
        result = 31 * result + (m_disableConversion != null ? m_disableConversion.hashCode() : 0);
        result = 31 * result + (m_converterClassName != null ? m_converterClassName.hashCode() : 0);
        result = 31 * result + (m_attributeName != null ? m_attributeName.hashCode() : 0);
        return result;
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
        return m_attributeName != null && !m_attributeName.isEmpty();
    }

    /**
     * INTERNAL:
     */
    public boolean hasConverterClass() {
        return m_converterClass != null && ! m_converterClass.isClass(AttributeConverter.class) && ! m_converterClass.isVoid();
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Trim any leading and trailing white spaces from the text if specified.
        if (m_text != null) {
            m_text = m_text.trim();
        }

        // Initialize the converter class name.
        m_converterClass = initXMLClassName(m_converterClassName);
    }

    /**
     * INTERNAL:
     * Return true if this convert metadata is for a map key. Way to tell is
     * if there is an attribute name that begins with "key".
     * <p>
     * Calling this method will also update the attribute name on the first call
     * to it. This call is made when sorting convert annotations. Unlike XML,
     * where the user can sort through &lt;convert&gt; and &lt;map-key-convert&gt; elements,
     * there is only a single Convert annotation that uses a "key" prefix on the
     * attribute name to signify a map key convert. An unforunate decision by
     * the JPA spec committee, but we can make it work of course.
     */
    public boolean isForMapKey() {
        if (m_isForMapKey == null) {
            final String key = AttributeNamePrefix.KEY.getName();
            if (m_attributeName != null && m_attributeName.startsWith(key)) {
                // Update the attribute name.
                m_attributeName = m_attributeName.equals(key) ? "" : m_attributeName.substring(key.length() + 1);
                m_isForMapKey = true;
            } else {
                m_isForMapKey = false;
            }
        }

        return m_isForMapKey;
    }

    /**
     * INTERNAL:
     * Verify mapping passed to register {@code AttributeConverter} class.
     * @param mapping        Database attribute mapping.
     * @param referenceClass JPA annotated class.
     * @param accessor       Class accessor.
     * @param embeddedAttributeName Content of {@code <name>}
     *        from {@code attributeName="value.<name>"}. This value shall never
     *        be {@code null} when {@code @ElementCollection} mapping is being
     *        processed.
     */
    private MetadataClass verify(final DatabaseMapping mapping, final MetadataClass referenceClass,
            final ClassAccessor accessor, final String embeddedAttributeName)
            throws ValidationException {
        // Validate the attribute name first if there is one.
        if (hasAttributeName()) {
            String attributeName;
            // Aggregate object mapping
            if (mapping.isAggregateObjectMapping()) {
                attributeName = getAttributeName();
            // Coming from @ElementCollection mapping with value.<name> attributeName.
            } else if (mapping.isAggregateCollectionMapping() && embeddedAttributeName != null
                    && !embeddedAttributeName.isEmpty()) {
                attributeName = embeddedAttributeName;
            // Unsupported mapping, throw an exception
            } else {
                throw ValidationException.invalidMappingForConvertWithAttributeName(
                        accessor.getJavaClassName(), mapping.getAttributeName());
            }
            // Validate the attribute name existing on the embeddable and update
            // the reference class.
            final ClassAccessor embeddableAccessor
                    = getProject().getEmbeddableAccessor(referenceClass);
            final MappingAccessor mappingAccessor = embeddableAccessor
                    .getDescriptor().getMappingAccessor(attributeName);

            if (mappingAccessor == null) {
                throw ValidationException.embeddableAttributeNameForConvertNotFound(
                        accessor.getJavaClassName(), mapping.getAttributeName(),
                        embeddableAccessor.getJavaClassName(), getAttributeName());
            }
            return mappingAccessor.getReferenceClass();
        } else {
            // In an aggregate object case, the attribute name must be specified.
            if (mapping.isAggregateObjectMapping()) {
                throw ValidationException.missingMappingConvertAttributeName(
                        accessor.getJavaClassName(), mapping.getAttributeName());
            }
        }
        return referenceClass;
    }

    /**
     * INTERNAL:
     * Apply converter class.
     * @param mapping        Database attribute mapping.
     * @param referenceClass JPA annotated class.
     * @param accessor       Class accessor.
     * @param isForMapKey    Is this converter for MapKey?
     */
    private void apply(final DatabaseMapping mapping, final MetadataClass referenceClass,
            final ClassAccessor accessor, final boolean isForMapKey) {
        // If we have a converter class, validate its existence and apply.
        if (hasConverterClass()) {
            if (getProject().hasConverterAccessor(getConverterClass())) {
                getProject().getConverterAccessor(getConverterClass()).process(
                        mapping, isForMapKey, getAttributeName());
            } else {
                throw ValidationException.converterClassNotFound(
                        accessor.getJavaClassName(), mapping.getAttributeName(),
                        getConverterClass().getName());
            }
        } else {
            // Check for an auto apply converter for the reference class.
            if (getProject().hasAutoApplyConverter(referenceClass)) {
                if (disableConversion()) {
                    // If we're dealing with an aggregate object mapping we need to ensure we add
                    // the converter to ensure we override an auto-apply converter to the mapping.
                    // This converter will update the embedded mapping after it is cloned during
                    // descriptor initialization.
                    // All other mappings can just avoid adding the converter all together.
                    if (mapping.isAggregateObjectMapping()) {
                        getProject().getAutoApplyConverter(referenceClass).process(
                                mapping, isForMapKey, getAttributeName(), true);
                    }
                } else {
                    // Apply the converter to the mapping.
                    getProject().getAutoApplyConverter(referenceClass).process(
                            mapping, isForMapKey, getAttributeName());
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Apply convert for {@code @ElementCollection} mapping where we expect
     * {@code @Convert} annotation to contain {@code attributeName="value.<name>"}.
     * @param mapping        Database attribute mapping.
     * @param referenceClass JPA annotated class.
     * @param accessor       Class accessor.
     * @param embeddedAttributeName Content of {@code <name>}
     *                              from {@code attributeName="value.<name>"}.
     */
    public void process(final DatabaseMapping mapping, final MetadataClass referenceClass,
            final ClassAccessor accessor, final String embeddedAttributeName) {
        // This code path is always coming from @ElementCollection mapping.
        final MetadataClass targetReferenceClass
                = verify(mapping, referenceClass, accessor, embeddedAttributeName);
        apply(mapping, targetReferenceClass, accessor, false);
    }

    /**
     * INTERNAL:
     * By the time we get here, we have the mapping that needs to have the
     * convert applied to. Do some validation checks along with some embedded
     * mapping traversing if need be and apply the converter. Will look an
     * auto-apply converter as well if one is not specified directly.
     * @param mapping        Database attribute mapping.
     * @param referenceClass JPA annotated class.
     * @param accessor       Class accessor.
     * @param isForMapKey    Is this converter for MapKey?
     */
    public void process(final DatabaseMapping mapping, final MetadataClass referenceClass,
            final ClassAccessor accessor, final boolean isForMapKey) {
        // This code path is not coming from @ElementCollection mapping
        // so <name> from value.<name> attributeName is not expected here.
        final MetadataClass targetReferenceClass
                = verify(mapping, referenceClass, accessor, null);
        apply(mapping, targetReferenceClass, accessor, isForMapKey);
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
