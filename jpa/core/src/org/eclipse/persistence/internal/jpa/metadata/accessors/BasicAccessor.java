/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataColumn;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataGeneratedValue;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.converters.Converter;

/**
 * A relational accessor. A @Basic annotation may or may not be present on the
 * accessible object.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class BasicAccessor extends DirectAccessor {
    private Basic m_basic;

    public BasicAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        m_basic = getAnnotation(Basic.class);
    }

    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     * 
     * Build a metadata column.
     */
    protected MetadataColumn getColumn(String loggingCtx) {
        Column column = getAnnotation(Column.class);
        return new MetadataColumn(column, this);
    }

    /**
     * Return the default table name to be used with the database field of this 
     * basic accessor.
     */
    protected DatabaseTable getDefaultTable() {
        return m_descriptor.getPrimaryTable();
    }

    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     */
    public String getFetchType() {
        return (m_basic == null) ? MetadataConstants.EAGER : m_basic.fetch().name();
    }

    /**
     * INTERNAL: (Override from MetadataAccessor)
     */
    public boolean isBasic() {
        return true;
    }

    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     * 
     * Return true if this accessor represents an id field.
     */
    public boolean isId() {
        return isAnnotationPresent(Id.class);
    }

    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     */
    public boolean isOptional() {
        return (m_basic == null) ? true : m_basic.optional();
    }

    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     * 
     * Return true if this accessor represents an optimistic locking field.
     */
    public boolean isVersion() {
        return isAnnotationPresent(Version.class);
    }

    /**
     * Process a basic accessor.
     */
    public void process() {
        // Process the @Column or column element if there is one.
        DatabaseField field = getDatabaseField(m_logger.COLUMN);

        // Process a @ReturnInsert
        processReturnInsert(field);

        // Process a @ReturnUpdate.
        processReturnUpdate(field);

        // Process an @Version or version element if there is one.
        if (isVersion()) {
            if (m_descriptor.usesOptimisticLocking()) {
                // Ignore the version locking if it is already set.
                m_logger.logWarningMessage(m_logger.IGNORE_VERSION_LOCKING, this);
            } else {
                processVersion(field);
            }
        } else if (isId()) {
            // Process an @Id or id element.
            processId(field);
        }

        if (m_descriptor.hasMappingForAttributeName(getAttributeName())) {
            // Ignore the mapping if one already exists for it.
            m_logger.logWarningMessage(m_logger.IGNORE_MAPPING, this);
        } else {
            // Process a DirectToFieldMapping, that is a Basic that could
            // be used in conjunction with a Lob, Temporal, Enumerated
            // or inferred to be used with a serialized mapping.
            processDirectToFieldMapping(field);
        }
    }

    /**
     * Process a Serialized or Basic into a DirectToFieldMapping. If neither 
     * is found a DirectToFieldMapping is created regardless.
     */
    protected void processDirectToFieldMapping(DatabaseField field) {
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        mapping.setField(field);
        mapping.setIsReadOnly(field.isReadOnly());
        mapping.setAttributeName(getAttributeName());
        mapping.setIsOptional(isOptional());
        if (usesIndirection()) {
            mapping.setIsLazy(true);
        }

        // Will check for PROPERTY access.
        setAccessorMethods(mapping);

        // Process a converter for this mapping. We will look for a @Convert
        // first. If none is found then we'll look for a JPA converter, that 
        // is, @Enumerated, @Lob and @Temporal. With everything falling into 
        // a serialized mapping if no converter whatsoever is found.
        processMappingConverter(mapping);

        processMutable(mapping);

        // Add the mapping to the descriptor.
        m_descriptor.addMapping(mapping);
    }

    /**
     * INTERNAL: (Override from DirectAccessor)
     * 
     * Process an @Enumerated. The method may still be called if no @Enumerated
     * has been specified but the accessor's reference class is a valid 
     * enumerated type.
     */
    protected void processEnumerated(DatabaseMapping mapping) {
        // If the raw class is a collection or map (with generics or not), we 
        // don't want to put a TypeConversionConverter on the mapping. Instead, 
        // we will want a serialized converter. For example, we could have 
        // an EnumSet<Enum> relation type.
        if (MetadataHelper.isCollectionClass(getReferenceClass()) || MetadataHelper.isMapClass(getReferenceClass())) {
            processSerialized(mapping);
        } else {
            super.processEnumerated(mapping);
        }
    }

    /**
     * INTERNAL: (Overridden In XMLBasicAccessor)
     * 
     * Process a @GeneratedValue.
     */
    protected void processGeneratedValue(DatabaseField field) {
        GeneratedValue generatedValue = getAnnotation(GeneratedValue.class);

        if (generatedValue != null) {
            processGeneratedValue(new MetadataGeneratedValue(generatedValue), field);
        }
    }

    protected void processGeneratedValue(MetadataGeneratedValue generatedValue, DatabaseField sequenceNumberField) {
        // Set the sequence number field on the descriptor.		
        DatabaseField existingSequenceNumberField = m_descriptor.getSequenceNumberField();

        if (existingSequenceNumberField == null) {
            m_descriptor.setSequenceNumberField(sequenceNumberField);
            getProject().addGeneratedValue(generatedValue, getJavaClass());
        } else {
            m_validator.throwOnlyOneGeneratedValueIsAllowed(getJavaClass(), existingSequenceNumberField.getQualifiedName(), sequenceNumberField.getQualifiedName());
        }
    }

    /**
     * Process an @Id or id element if there is one.
     */
    protected void processId(DatabaseField field) {
        if (m_descriptor.ignoreIDs()) {
            // Project XML merging. XML wins, ignore annotations/orm xml.
            m_logger.logWarningMessage(m_logger.IGNORE_PRIMARY_KEY, this);
        } else {
            String attributeName = getAttributeName();

            if (m_descriptor.hasEmbeddedIdAttribute()) {
                // We found both an Id and an EmbeddedId, throw an exception.
                m_validator.throwEmbeddedIdAndIdFound(getJavaClass(), m_descriptor.getEmbeddedIdAttributeName(), attributeName);
            }

            // If this entity has a pk class, we need to validate our ids. 
            m_descriptor.validatePKClassId(attributeName, getReferenceClass());

            // Store the Id attribute name. Used with validation and OrderBy.
            m_descriptor.addIdAttributeName(attributeName);

            // Add the primary key field to the descriptor.            
            m_descriptor.addPrimaryKeyField(field);

            // Process the generated value for this id.
            processGeneratedValue(field);

            // Process a table generator.
            processTableGenerator();

            // Process a sequence generator.
            processSequenceGenerator();
        }
    }

    /**
     * INTERNAL: (Override from DirectAccessor)
     * 
     * Process a @Lob or lob sub-element. The lob must be specified to process 
     * and create a lob type mapping.
     */
    protected void processLob(DatabaseMapping mapping) {
        // If the raw class is a collection or map (with generics or not), we 
        // don't want to put a TypeConversionConverter on the mapping. Instead, 
        // we will want a serialized converter.
        if (MetadataHelper.isCollectionClass(getReferenceClass()) || MetadataHelper.isMapClass(getReferenceClass())) {
            setFieldClassification(mapping, java.sql.Blob.class);
            processSerialized(mapping);
        } else {
            super.processLob(mapping);
        }
    }
            
    /**
     * Process the @Mutable annotation.
     */
    public void processMutable(DatabaseMapping mapping) {
        Boolean mutable = getMutableValue();
        if (mutable != null) {
            ((DirectToFieldMapping)mapping).setIsMutable(mutable.booleanValue());
        }
    }

    /**
     * INTERNAL: (Overridden in XMLBasicAccessor)
     *
     * Process a @ReturnInsert.
     */
    protected void processReturnInsert(DatabaseField field) {
        ReturnInsert returnInsert = getAnnotation(ReturnInsert.class);

        if (returnInsert != null) {
            // Process return only.
            processReturnInsert(field, returnInsert.returnOnly());
        }
    }

    /**
     * INTERNAL: (Override from MetadataAccessor)
     *
     * Process a return insert setting.
     */
    protected void processReturnInsert(DatabaseField field, boolean returnOnly) {
        if (returnOnly) {
            m_descriptor.addFieldForInsertReturnOnly(field);
        } else {
            m_descriptor.addFieldForInsert(field);
        }
    }

    /**
     * INTERNAL: (Override from MetadataAccessor)
     * 
     * Process a return update setting.
     */
    protected void processReturnUpdate(DatabaseField field) {
        if (hasReturnUpdate()) {
            m_descriptor.addFieldForUpdate(field);
        }
    }

    protected void processVersion(DatabaseField field) {
        Class lockType = getRawClass();
        field.setType(lockType);

        if (MetadataHelper.isValidVersionLockingType(lockType)) {
            m_descriptor.useVersionLockingPolicy(field);
        } else if (MetadataHelper.isValidTimstampVersionLockingType(lockType)) {
            m_descriptor.useTimestampLockingPolicy(field);
        } else {
            m_validator.throwInvalidTypeForVersionAttribute(getJavaClass(), getAttributeName(), lockType);
        }
    }

    public void setConverter(DatabaseMapping mapping, Converter converter) {
        ((DirectToFieldMapping)mapping).setConverter(converter);
    }

    public void setConverterClassName(DatabaseMapping mapping, String converterClassName) {
        ((DirectToFieldMapping)mapping).setConverterClassName(converterClassName);
    }

    public void setFieldClassification(DatabaseMapping mapping, Class classification) {
        ((DirectToFieldMapping)mapping).setFieldClassification(classification);
    }
}
