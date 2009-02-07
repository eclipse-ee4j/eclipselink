/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     08/27/2008-1.1 Guy Pelletier 
 *       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;

/**
 * INTERNAL:
 * A relational accessor. A Basic annotation may or may not be present on the
 * accessible object.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class BasicAccessor extends DirectAccessor {
    private Boolean m_mutable;
    private ColumnMetadata m_column;
    private DatabaseField m_field; 
    private GeneratedValueMetadata m_generatedValue;
    private SequenceGeneratorMetadata m_sequenceGenerator;
    private TableGeneratorMetadata m_tableGenerator;
    
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
    public BasicAccessor(Annotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        // Set the basic metadata if one is present.
        Annotation basic = getAnnotation(Basic.class);
        if (basic != null) {
            setFetch((Enum) MetadataHelper.invokeMethod("fetch", basic));
            setOptional((Boolean) MetadataHelper.invokeMethod("optional", basic));
        }
        
        // Set the column metadata if one if present.
        m_column = new ColumnMetadata(getAnnotation(Column.class), accessibleObject, getAttributeName());
        
        // Set the mutable value if one is present.
        Annotation mutable = getAnnotation(Mutable.class);
        if (mutable != null) {
            m_mutable = (Boolean) MetadataHelper.invokeMethod("value", mutable);
        }
        
        // Set the generated value if one is present.
        if (isAnnotationPresent(GeneratedValue.class)) {
            m_generatedValue = new GeneratedValueMetadata(getAnnotation(GeneratedValue.class));
        }
        
        // Set the sequence generator if one is present.        
        if (isAnnotationPresent(SequenceGenerator.class)) {
            m_sequenceGenerator = new SequenceGeneratorMetadata(getAnnotation(SequenceGenerator.class), accessibleObject);
        }
        
        // Set the table generator if one is present.        
        if (isAnnotationPresent(TableGenerator.class)) {
            m_tableGenerator = new TableGeneratorMetadata(getAnnotation(TableGenerator.class), accessibleObject);
        }
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
        if (m_column == null) {
            return new ColumnMetadata(getAccessibleObject(), getAttributeName());
        } else {
            return m_column;
        }
    }

    /**
     * INTERNAL:
     * Process column metadata details and resolve any generic specifications.
     */
    @Override
    protected DatabaseField getDatabaseField(DatabaseTable defaultTable, String loggingCtx) {
        // Get the actual database field and apply any defaults.
        DatabaseField field = super.getDatabaseField(defaultTable, loggingCtx);
        
        // To correctly resolve the generics at runtime, we need to set the 
        // field type.
        if (getAccessibleObject().isGenericType()) {
            field.setType(getReferenceClass());
        }
                    
        return field;
    }
    
    /**
     * INTERNAL:
     */
    public FetchType getDefaultFetchType() {
        return FetchType.EAGER; 
    }
    
    /**
     * INTERNAL:
     */
    protected DatabaseField getField() {
        return m_field;
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
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);

        // Default a column if necessary.
        if (m_column == null) {
            m_column = new ColumnMetadata(accessibleObject, getAttributeName());
        } else {
            // Initialize single objects.
            initXMLObject(m_column, accessibleObject);
        }
        
        // Initialize single objects.
        initXMLObject(m_sequenceGenerator, accessibleObject);
        initXMLObject(m_tableGenerator, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Method to return whether a class is a collection or not. 
     */
    protected boolean isCollectionClass(Class cls) {
        return Collection.class.isAssignableFrom(cls);
    }
    
    /**
     * INTERNAL:
     * Method to return whether a class is a map or not. 
     */
    protected boolean isMapClass(Class cls) {
        return Map.class.isAssignableFrom(cls);
    }
    
    /**
     * INTERNAL:
     * Process a basic accessor.
     */
    public void process() {
        // Process a DirectToFieldMapping, that is a Basic that could
        // be used in conjunction with a Lob, Temporal, Enumerated
        // or inferred to be used with a serialized mapping.
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        
        // Process the @Column or column element if there is one.
        // A number of methods depend on this field so it must be
        // initialized before any further processing can take place.
        m_field = getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN);
        
        // To resolve any generic types we need to set the attribute
        // classification on the mapping to ensure we do the right 
        // conversions.
        if (getAccessibleObject().isGenericType()) {
            mapping.setAttributeClassification(getReferenceClass());
        }
        
        mapping.setField(m_field);
        mapping.setIsReadOnly(m_field.isReadOnly());
        mapping.setAttributeName(getAttributeName());
        mapping.setIsOptional(isOptional());
        mapping.setIsLazy(usesIndirection());
        
        //Derived ID: set if this mapping has been marked as an ID 
        mapping.setIsIDMapping(isId());

        // Will check for PROPERTY access.
        setAccessorMethods(mapping);

        // Process a converter for this mapping. We will look for a convert
        // value first. If none is found then we'll look for a JPA converter, 
        // that is, Enumerated, Lob and Temporal. With everything falling into 
        // a serialized mapping if no converter whatsoever is found.
        processMappingConverter(mapping, getConvert());

        // Process a mutable setting.
        if (m_mutable != null) {
            mapping.setIsMutable(m_mutable.booleanValue());
        }

        // Process the @ReturnInsert and @ReturnUpdate annotations.
        // TODO: Expand this configuration to the eclipselink-orm.xsd
        processReturnInsertAndUpdate();
        
        // Process a generated value setting.
        processGeneratedValue();
        
        // Add the table generator to the project if one is set.
        if (m_tableGenerator != null) {
            getProject().addTableGenerator(m_tableGenerator, getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
        }

        // Add the sequence generator to the project if one is set.
        if (m_sequenceGenerator != null) {
            getProject().addSequenceGenerator(m_sequenceGenerator);
        }
        
        // Add the mapping to the descriptor.
        addMapping(mapping);
    }

    /**
     * INTERNAL:
     * Process an Enumerated annotation. The method may still be called if no 
     * Enumerated annotation has been specified but the accessor's reference 
     * class is a valid enumerated type.
     */
    @Override
    protected void processEnumerated(DatabaseMapping mapping) {
        // If the raw class is a collection or map (with generics or not), we 
        // don't want to put a TypeConversionConverter on the mapping. Instead, 
        // we will want a serialized converter. For example, we could have 
        // an EnumSet<Enum> relation type.
        if (isCollectionClass(getReferenceClass()) || isMapClass(getReferenceClass())) {
            processSerialized(mapping);
        } else {
            super.processEnumerated(mapping);
        }
    }

    /**
     * INTERNAL:
     * Process the generated value metadata.
     */
    protected void processGeneratedValue() {
        if (m_generatedValue != null) {
            // Set the sequence number field on the descriptor.        
            DatabaseField existingSequenceNumberField = getOwningDescriptor().getSequenceNumberField();

            if (existingSequenceNumberField == null) {
                getOwningDescriptor().setSequenceNumberField(m_field);
                getProject().addGeneratedValue(m_generatedValue, getOwningDescriptor().getJavaClass());
            } else {
                throw ValidationException.onlyOneGeneratedValueIsAllowed(getOwningDescriptor().getJavaClass(), existingSequenceNumberField.getQualifiedName(), m_field.getQualifiedName());
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process a Lob metadata. The lob must be specified to process and 
     * create a lob type mapping.
     */
    @Override
    protected void processLob(DatabaseMapping mapping) {
        // If the raw class is a collection or map (with generics or not), we 
        // don't want to put a TypeConversionConverter on the mapping. Instead, 
        // we will want a serialized converter.
        if (isCollectionClass(getReferenceClass()) || isMapClass(getReferenceClass())) {
            setFieldClassification(mapping, java.sql.Blob.class);
            processSerialized(mapping);
        } else {
            super.processLob(mapping);
        }
    }
    
    /**
     * INTERNAL:
     * Process a ReturnInsert annotation. NOTE: This is currently only
     * supported using annotations.
     * TODO: Add to the eclipselink-orm.xsd
     */
    @Override
    protected void processReturnInsert() {
        Object returnInsert = getAnnotation(ReturnInsert.class);

        if (returnInsert != null) {
            boolean returnOnly = (Boolean) MetadataHelper.invokeMethod("returnOnly", returnInsert);
            
            if (returnOnly) {
                getDescriptor().addFieldForInsertReturnOnly(m_field);
            } else {
                getDescriptor().addFieldForInsert(m_field);
            }
        }
    }

    /**
     * INTERNAL:
     * Process a return update setting.  NOTE: This is currently only
     * supported using annotations.
     * TODO: Add to the eclipselink-orm.xsd
     */
    @Override
    protected void processReturnUpdate() {
        if (hasReturnUpdate()) {
            getDescriptor().addFieldForUpdate(m_field);
        }
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
     */
    public void setConverter(DatabaseMapping mapping, Converter converter) {
        ((DirectToFieldMapping)mapping).setConverter(converter);
    }

    /**
     * INTERNAL:
     */
    public void setConverterClassName(DatabaseMapping mapping, String converterClassName) {
        ((DirectToFieldMapping)mapping).setConverterClassName(converterClassName);
    }

    /**
     * INTERNAL:
     */
    public void setFieldClassification(DatabaseMapping mapping, Class classification) {
        ((DirectToFieldMapping)mapping).setFieldClassification(classification);
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
}
