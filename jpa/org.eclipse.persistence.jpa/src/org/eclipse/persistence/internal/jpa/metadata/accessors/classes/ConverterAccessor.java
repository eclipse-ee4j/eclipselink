/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
//     06/03/2013-2.5.1 Guy Pelletier
//       - 402380: 3 jpa21/advanced tests failed on server with
//         "java.lang.NoClassDefFoundError: org/eclipse/persistence/testing/models/jpa21/advanced/enums/Gender"
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.util.List;

import javax.persistence.AttributeConverter;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.converters.ConverterClass;
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

    @Override
    public int hashCode() {
        return autoApply != null ? autoApply.hashCode() : 0;
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
        List<String> genericTypes = ((MetadataClass) getAccessibleObject()).getGenericType();
        int genericTypesSize = genericTypes.size();

        // Start building the attribute classification name that will include
        // any generic specifications, e.g. List<String>. We need to be able to
        // distinguish between List<String> and List<Integer> etc. to correctly
        // handle the auto-apply feature for basics.
        StringBuilder attributeClassificationName = new StringBuilder(32);
        for (int i = 2; i < genericTypesSize - 1; i++) {
            attributeClassificationName.append(genericTypes.get(i));
        }

        // Cache the classification classes.
        attributeClassification = getMetadataClass(attributeClassificationName.toString());
        fieldClassification = getMetadataClass(genericTypes.get(genericTypesSize - 1));
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
        ConverterClass converterClass = new ConverterClass(getJavaClassName(), isForMapKey, fieldClassification.getName(), disableConversion);
        converterClass.setSession(getProject().getSession());

        if (mapping.isDirectMapMapping() && isForMapKey) {
            ((DirectMapMapping) mapping).setKeyConverter(converterClass);
        } else if (mapping.isDirectCollectionMapping()) {
            ((DirectCollectionMapping) mapping).setValueConverter(converterClass);
        } else if (mapping.isDirectToFieldMapping()) {
            ((AbstractDirectMapping) mapping).setConverter(converterClass);
        } else if (mapping.isAggregateObjectMapping()) {
            ((AggregateObjectMapping) mapping).addConverter(converterClass, attributeName);
        } else if (mapping.isAggregateCollectionMapping()) {
            ((AggregateCollectionMapping) mapping).addConverter(converterClass, attributeName);
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
