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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     08/27/2008-1.1 Guy Pelletier
//       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     10/21/2009-2.0 Guy Pelletier
//       - 290567: mappedbyid support incomplete
//     12/2/2009-2.1 Guy Pelletier
//       - 296612:  Add current annotation only metadata support of return insert/update to the EclipseLink-ORM.XML Schema
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     07/05/2010-2.1.1 Guy Pelletier
//       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     07/03/2011-2.3.1 Guy Pelletier
//       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.Collection;
import java.util.Map;

import org.eclipse.persistence.annotations.CacheIndex;
import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.ReturnUpdate;
import org.eclipse.persistence.annotations.UuidGenerator;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheIndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.LobMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.ReturnInsertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.UuidGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.IndexMetadata;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_BASIC;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_COLUMN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_FETCH_EAGER;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_GENERATED_VALUE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_SEQUENCE_GENERATOR;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_TABLE_GENERATOR;

/**
 * INTERNAL:
 * A relational accessor. A Basic annotation may or may not be present on the
 * accessible object.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class BasicAccessor extends DirectAccessor {
    private Boolean m_mutable;
    private Boolean m_returnUpdate;
    private ColumnMetadata m_column;
    private DatabaseField m_databaseField;
    private GeneratedValueMetadata m_generatedValue;
    private ReturnInsertMetadata m_returnInsert;
    private SequenceGeneratorMetadata m_sequenceGenerator;
    private TableGeneratorMetadata m_tableGenerator;
    private UuidGeneratorMetadata m_uuidGenerator;
    private IndexMetadata m_index;
    private CacheIndexMetadata m_cacheIndex;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public BasicAccessor() {
        super("<basic>");
    }

    /**
     * INTERNAL:
     */
    public BasicAccessor(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    public BasicAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);

        // Set the basic metadata if one is present.
        MetadataAnnotation basic = getAnnotation(JPA_BASIC);
        if (basic != null) {
            setFetch(basic.getAttributeString("fetch"));
            setOptional(basic.getAttributeBooleanDefaultTrue("optional"));
        }

        // Set the column metadata if one if present.
        m_column = new ColumnMetadata(getAnnotation(JPA_COLUMN), this);

        // Set the mutable value if one is present.
        if (isAnnotationPresent(Mutable.class)) {
            m_mutable = getAnnotation(Mutable.class).getAttributeBooleanDefaultTrue("value");
        }

        // Set the generated value if one is present.
        if (isAnnotationPresent(JPA_GENERATED_VALUE)) {
            m_generatedValue = new GeneratedValueMetadata(getAnnotation(JPA_GENERATED_VALUE), this);
        }

        // Set the sequence generator if one is present.
        if (isAnnotationPresent(JPA_SEQUENCE_GENERATOR)) {
            m_sequenceGenerator = new SequenceGeneratorMetadata(getAnnotation(JPA_SEQUENCE_GENERATOR), this);
        }

        // Set the table generator if one is present.
        if (isAnnotationPresent(JPA_TABLE_GENERATOR)) {
            m_tableGenerator = new TableGeneratorMetadata(getAnnotation(JPA_TABLE_GENERATOR), this);
        }

        // Set the table generator if one is present.
        if (isAnnotationPresent(UuidGenerator.class)) {
            m_uuidGenerator = new UuidGeneratorMetadata(getAnnotation(UuidGenerator.class), this);
        }

        // Set the return insert if one is present.
        if (isAnnotationPresent(ReturnInsert.class)) {
            m_returnInsert = new ReturnInsertMetadata(getAnnotation(ReturnInsert.class), this);
        }

        // Set the return update if one is present.
        m_returnUpdate = isAnnotationPresent(ReturnUpdate.class);

        // Set the index annotation if one is present.
        if (isAnnotationPresent(Index.class)) {
            m_index = new IndexMetadata(getAnnotation(Index.class), this);
        }

        // Set the cache index annotation if one is present.
        if (isAnnotationPresent(CacheIndex.class)) {
            m_cacheIndex = new CacheIndexMetadata(getAnnotation(CacheIndex.class), this);
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof BasicAccessor) {
            BasicAccessor basicAccessor = (BasicAccessor) objectToCompare;

            if (! valuesMatch(m_mutable, basicAccessor.getMutable())) {
                return false;
            }

            if (! valuesMatch(m_returnUpdate, basicAccessor.getReturnUpdate())) {
                return false;
            }

            if (! valuesMatch(m_column, basicAccessor.getColumn())) {
                return false;
            }

            if (! valuesMatch(m_generatedValue, basicAccessor.getGeneratedValue())) {
                return false;
            }

            if (! valuesMatch(m_returnInsert, basicAccessor.getReturnInsert())) {
                return false;
            }

            if (! valuesMatch(m_sequenceGenerator, basicAccessor.getSequenceGenerator())) {
                return false;
            }

            if (! valuesMatch(m_uuidGenerator, basicAccessor.getUuidGenerator())) {
                return false;
            }

            return valuesMatch(m_tableGenerator, basicAccessor.getTableGenerator());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_mutable != null ? m_mutable.hashCode() : 0);
        result = 31 * result + (m_returnUpdate != null ? m_returnUpdate.hashCode() : 0);
        result = 31 * result + (m_column != null ? m_column.hashCode() : 0);
        result = 31 * result + (m_generatedValue != null ? m_generatedValue.hashCode() : 0);
        result = 31 * result + (m_returnInsert != null ? m_returnInsert.hashCode() : 0);
        result = 31 * result + (m_sequenceGenerator != null ? m_sequenceGenerator.hashCode() : 0);
        result = 31 * result + (m_tableGenerator != null ? m_tableGenerator.hashCode() : 0);
        result = 31 * result + (m_uuidGenerator != null ? m_uuidGenerator.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getColumn() {
        return m_column;
    }

    /**
     * INTERNAL:
     * Return the column from xml if there is one, otherwise look for an
     * annotation.
     */
    protected ColumnMetadata getColumn(String loggingCtx) {
        return m_field == null ? (m_column == null ? super.getColumn(loggingCtx) : m_column) : m_field;
    }

    /**
     * INTERNAL:
     */
    public String getDefaultFetchType() {
        return JPA_FETCH_EAGER;
    }

    /**
     * INTERNAL:
     */
    protected DatabaseField getDatabaseField() {
        return m_databaseField;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public GeneratedValueMetadata getGeneratedValue() {
        return m_generatedValue;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getMutable() {
        return m_mutable;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ReturnInsertMetadata getReturnInsert() {
        return m_returnInsert;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getReturnUpdate() {
        return m_returnUpdate;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public SequenceGeneratorMetadata getSequenceGenerator() {
        return m_sequenceGenerator;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public TableGeneratorMetadata getTableGenerator() {
        return m_tableGenerator;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public UuidGeneratorMetadata getUuidGenerator() {
        return m_uuidGenerator;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Default a column if necessary.
        if (m_column == null) {
            m_column = new ColumnMetadata(this);
        } else {
            // Initialize single objects.
            initXMLObject(m_column, accessibleObject);
        }

        // Initialize single objects.
        initXMLObject(m_generatedValue, accessibleObject);
        initXMLObject(m_returnInsert, accessibleObject);
        initXMLObject(m_sequenceGenerator, accessibleObject);
        initXMLObject(m_tableGenerator, accessibleObject);
        initXMLObject(m_uuidGenerator, accessibleObject);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a basic mapping.
     */
    @Override
    public boolean isBasic() {
        return true;
    }

    /**
     * INTERNAL:
     * Method to return whether a class is a collection or not.
     */
    protected boolean isCollectionClass(MetadataClass cls) {
        return cls.extendsInterface(Collection.class);
    }

    /**
     * INTERNAL:
     * Method to return whether a class is a map or not.
     */
    protected boolean isMapClass(MetadataClass cls) {
        return cls.extendsInterface(Map.class);
    }

    /**
     * INTERNAL:
     * USed for OX mapping
     */
    public Boolean isReturnUpdate() {
        return m_returnUpdate != null && m_returnUpdate.booleanValue();
    }

    /**
     * INTERNAL:
     * Process a basic accessor.
     */
    public void process() {
        // Process a DirectToFieldMapping, that is a Basic that could
        // be used in conjunction with a Lob, Temporal, Enumerated
        // or inferred to be used with a serialized mapping.
        AbstractDirectMapping mapping = getOwningDescriptor().getClassDescriptor().newDirectMapping();
        setMapping(mapping);

        // Process the @Column or column element if there is one.
        // A number of methods depend on this field so it must be
        // initialized before any further processing can take place.
        m_databaseField = getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN);

        // To resolve any generic types (or respect an attribute type
        // specification) we need to set the attribute classification on the
        // mapping to ensure we do the right conversions.
        if (hasAttributeType() || getAccessibleObject().isGenericType()) {
            mapping.setAttributeClassificationName(getReferenceClassName());
        }

        mapping.setField(m_databaseField);
        mapping.setIsReadOnly(m_databaseField.isReadOnly());
        mapping.setAttributeName(getAttributeName());
        mapping.setIsOptional(isOptional());
        mapping.setIsLazy(usesIndirection());

        // Will check for PROPERTY access.
        setAccessorMethods(mapping);

        // Process a converter for this mapping. We will look for an eclipselink
        // convert value first. If none is found then we'll look for a JPA
        // converter, that is, Convert, Enumerated, Lob and Temporal. With
        // everything falling into a serialized mapping if no converter
        // whatsoever is found.
        processMappingValueConverter(mapping, getConvert(), getConverts(), getReferenceClass(), getReferenceClassWithGenerics());

        // Process a mutable setting.
        if (m_mutable != null) {
            mapping.setIsMutable(m_mutable.booleanValue());
        }

        // Process the @ReturnInsert and @ReturnUpdate annotations.
        processReturnInsertAndUpdate();

        // Process a generated value setting.
        processGeneratedValue();

        // Add the table generator to the project if one is set.
        if (m_tableGenerator != null) {
            getProject().addTableGenerator(m_tableGenerator, getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
        }

        // Add the sequence generator to the project if one is set.
        if (m_sequenceGenerator != null) {
            getProject().addSequenceGenerator(m_sequenceGenerator, getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
        }

        // Add the uuid generator to the project if one is set.
        if (m_uuidGenerator != null) {
            getProject().addUuidGenerator(m_uuidGenerator);
        }

        // Process the index metadata.
        processIndex();
        processCacheIndex();
    }

    /**
     * INTERNAL:
     * Process an Enumerated annotation. The method may still be called if no
     * Enumerated annotation has been specified but the accessor's reference
     * class is a valid enumerated type.
     */
    @Override
    protected void processEnumerated(EnumeratedMetadata enumerated, DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        // If the raw class is a collection or map (with generics or not), we
        // don't want to put a TypeConversionConverter on the mapping. Instead,
        // we will want a serialized converter. For example, we could have
        // an EnumSet<Enum> relation type.
        if (isCollectionClass(referenceClass) || isMapClass(referenceClass)) {
            processSerialized(mapping, referenceClass, isForMapKey);
        } else {
            super.processEnumerated(enumerated, mapping, referenceClass, isForMapKey);
        }
    }

    /**
     * INTERNAL:
     * Process the generated value metadata.
     */
    protected void processGeneratedValue() {
        if (m_generatedValue != null) {
            // Set the sequence number field on all the owning descriptors.
            for (MetadataDescriptor owningDescriptor : getOwningDescriptors()) {
                DatabaseField existingSequenceNumberField = owningDescriptor.getSequenceNumberField();

                if (existingSequenceNumberField == null) {
                    owningDescriptor.setSequenceNumberField(m_databaseField);
                    getProject().addGeneratedValue(m_generatedValue, owningDescriptor.getJavaClass());
                } else {
                    throw ValidationException.onlyOneGeneratedValueIsAllowed(owningDescriptor.getJavaClass(), existingSequenceNumberField.getQualifiedName(), m_databaseField.getQualifiedName());
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Process index information for the given mapping.
     */
    protected void processIndex() {
        if (m_index != null) {
            m_index.process(getDescriptor(), m_databaseField.getName());
        }
    }

    /**
     * INTERNAL:
     * Process cache index.
     */
    protected void processCacheIndex() {
        if (m_cacheIndex != null) {
            m_cacheIndex.process(getDescriptor(), m_databaseField.getName());
        }
    }

    /**
     * INTERNAL:
     * Process a Lob metadata. The lob must be specified to process and
     * create a lob type mapping.
     */
    @Override
    protected void processLob(LobMetadata lob, DatabaseMapping mapping, MetadataClass referenceClass, boolean isForMapKey) {
        // If the raw class is a collection or map (with generics or not), we
        // don't want to put a TypeConversionConverter on the mapping. Instead,
        // we will want a serialized converter.
        if (isCollectionClass(referenceClass) || isMapClass(referenceClass)) {
            processSerialized(mapping, referenceClass, getMetadataClass(java.sql.Blob.class), isForMapKey);
        } else {
            super.processLob(lob, mapping, referenceClass, isForMapKey);
        }
    }

    /**
     * INTERNAL:
     * Process a ReturnInsert annotation.
     */
    @Override
    protected void processReturnInsert() {
        if (m_returnInsert != null) {
            m_returnInsert.process(getDescriptor(), m_databaseField);
        }
    }

    /**
     * INTERNAL:
     * Process a return update setting.
     */
    @Override
    protected void processReturnUpdate() {
        if (isReturnUpdate()) {
            getDescriptor().addFieldForUpdate(m_databaseField);
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public IndexMetadata getIndex() {
        return m_index;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setIndex(IndexMetadata index) {
        m_index = index;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public CacheIndexMetadata getCacheIndex() {
        return m_cacheIndex;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCacheIndex(CacheIndexMetadata index) {
        m_cacheIndex = index;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setGeneratedValue(GeneratedValueMetadata value) {
        m_generatedValue = value;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMutable(Boolean mutable) {
        m_mutable = mutable;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReturnInsert(ReturnInsertMetadata returnInsert) {
        m_returnInsert = returnInsert;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReturnUpdate(Boolean returnUpdate) {
        m_returnUpdate = returnUpdate;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSequenceGenerator(SequenceGeneratorMetadata sequenceGenerator) {
        m_sequenceGenerator = sequenceGenerator;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTableGenerator(TableGeneratorMetadata tableGenerator) {
        m_tableGenerator = tableGenerator;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setUuidGenerator(UuidGeneratorMetadata uuidGenerator) {
        m_uuidGenerator = uuidGenerator;
    }
}
