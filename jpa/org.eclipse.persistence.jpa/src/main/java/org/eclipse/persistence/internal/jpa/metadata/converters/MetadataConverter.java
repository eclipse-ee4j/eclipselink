/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//      //     30/05/2012-2.4 Guy Pelletier
//       - 354678: Temp classloader is still being used during metadata processing
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * INTERNAL:
 * Abstract converter class that parents both the JPA and Eclipselink
 * converters.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public abstract class MetadataConverter extends ORMetadata {
    /**
     * INTERNAL:
     * Used for defaulting case.
     */
    protected MetadataConverter() {}

    /**
     * INTERNAL:
     * Used for defaulting.
     */
    protected MetadataConverter(MetadataAccessor accessor) {
        super(null, accessor);
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    protected MetadataConverter(MetadataAnnotation converter, MetadataAccessor accessor) {
        super(converter, accessor);
    }

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    protected MetadataConverter(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        return objectToCompare instanceof MetadataConverter;
    }

    /**
     * INTERNAL:
     * Every converter needs to be able to process themselves.
     */
    public abstract void process(DatabaseMapping mapping, MappingAccessor accessor, MetadataClass referenceClass, boolean isForMapKey);

    /**
     * INTERNAL:
     */
    protected void setConverter(DatabaseMapping mapping, Converter converter, boolean isForMapKey) {
        if (mapping.isDirectMapMapping() && isForMapKey) {
            ((DirectMapMapping) mapping).setKeyConverter(converter);
        } else if (mapping.isDirectCollectionMapping()) {
            ((DirectCollectionMapping) mapping).setValueConverter(converter);
        } else if (mapping instanceof AbstractCompositeDirectCollectionMapping) {
            ((AbstractCompositeDirectCollectionMapping) mapping).setValueConverter(converter);
        } else if (mapping.isAggregateCollectionMapping()) {
            // TODO: Be nice to support converters on AggregateCollections keys.
            // For now they are silently ignored.
        } else if (mapping.isDirectToFieldMapping()) {
            ((AbstractDirectMapping) mapping).setConverter(converter);
        }
    }

    /**
     * INTERNAL:
     */
    protected void setConverterClassName(DatabaseMapping mapping, String converterClassName, boolean isForMapKey) {
        if (mapping.isDirectMapMapping() && isForMapKey) {
            ((DirectMapMapping) mapping).setKeyConverterClassName(converterClassName);
        } else if (mapping.isDirectCollectionMapping()) {
            ((DirectCollectionMapping) mapping).setValueConverterClassName(converterClassName);
        } else if (mapping.isAggregateCollectionMapping()) {
            // TODO: Be nice to support converters on AggregateCollections keys.
            // For now they are silently ignored.
        } else if (mapping.isDirectToFieldMapping()) {
            ((AbstractDirectMapping) mapping).setConverterClassName(converterClassName);
        }
    }

    /**
     * INTERNAL:
     * Wrapper method to make sure we always set the classification name and
     * not class. This class name will be initialized at run time with the real
     * class loader.
     */
    protected void setFieldClassification(DatabaseMapping mapping, Class classification, boolean isForMapKey) {
        setFieldClassification(mapping, classification.getName(), isForMapKey);
    }

    /**
     * INTERNAL:
     * Wrapper method to make sure we always set the classification name and
     * not class. This class name will be initialized at run time with the real
     * class loader.
     */
    protected void setFieldClassification(DatabaseMapping mapping, MetadataClass classification, boolean isForMapKey) {
        setFieldClassification(mapping, getJavaClassName(classification), isForMapKey);
    }

    /**
     * INTERNAL:
     * Users should call one of the wrapper methods, likely the one that accepts
     * a MetadataClass as we want to make sure getJavaClassName is called since
     * it does extra goodies to make sure we get a class name that can be loaded
     * via Class.forName()
     */
    private void setFieldClassification(DatabaseMapping mapping, String classificationName, boolean isForMapKey) {
        if (mapping.isDirectMapMapping() && isForMapKey) {
            ((DirectMapMapping) mapping).setDirectKeyFieldClassificationName(classificationName);
        } else if (mapping.isDirectCollectionMapping()) {
            ((DirectCollectionMapping) mapping).setDirectFieldClassificationName(classificationName);
        } else if (mapping.isAggregateCollectionMapping()) {
            // TODO: Future and AggregateCollections should only be able to get
            // in here for a key converter
        } else if (mapping.isDirectToFieldMapping()) {
            ((AbstractDirectMapping) mapping).setFieldClassificationClassName(classificationName);
        }
    }
}
