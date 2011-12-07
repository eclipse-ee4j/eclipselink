/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     12/2/2009-2.1 Guy Pelletier 
 *       - 296612:  Add current annotation only metadata support of return insert/update to the EclipseLink-ORM.XML Schema
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     07/05/2010-2.1.1 Guy Pelletier 
 *       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     07/03/2011-2.3.1 Guy Pelletier 
 *       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.Collection;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.ReturnUpdate;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.LobMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.ReturnInsertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.IndexMetadata;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

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
    private DatabaseField m_field; 
    private GeneratedValueMetadata m_generatedValue;
    private ReturnInsertMetadata m_returnInsert;
    private SequenceGeneratorMetadata m_sequenceGenerator;
    private TableGeneratorMetadata m_tableGenerator;
    private IndexMetadata m_index;
    
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
        MetadataAnnotation basic = getAnnotation(Basic.class);
        if (basic != null) {
            setFetch((String) basic.getAttribute("fetch"));
            setOptional((Boolean) basic.getAttribute("optional"));
        }
        
        // Set the column metadata if one if present.
        m_column = new ColumnMetadata(getAnnotation(Column.class), this);
        
        // Set the mutable value if one is present.
        if (isAnnotationPresent(Mutable.class)) {
            m_mutable = (Boolean) getAnnotation(Mutable.class).getAttributeBooleanDefaultTrue("value");
        }
        
        // Set the generated value if one is present.
        if (isAnnotationPresent(GeneratedValue.class)) {
            m_generatedValue = new GeneratedValueMetadata(getAnnotation(GeneratedValue.class), this);
        }
        
        // Set the sequence generator if one is present.        
        if (isAnnotationPresent(SequenceGenerator.class)) {
            m_sequenceGenerator = new SequenceGeneratorMetadata(getAnnotation(SequenceGenerator.class), this);
        }
        
        // Set the table generator if one is present.        
        if (isAnnotationPresent(TableGenerator.class)) {
            m_tableGenerator = new TableGeneratorMetadata(getAnnotation(TableGenerator.class), this);
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
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
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
            
            return valuesMatch(m_tableGenerator, basicAccessor.getTableGenerator());
        }
        
        return false;
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
        return m_column == null ? super.getColumn(loggingCtx) : m_column;
    }
    
    /**
     * INTERNAL:
     */
    public String getDefaultFetchType() {
        return FetchType.EAGER.name(); 
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
    @Override
    public void process() {
        // Process a DirectToFieldMapping, that is a Basic that could
        // be used in conjunction with a Lob, Temporal, Enumerated
        // or inferred to be used with a serialized mapping.
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        setMapping(mapping);
        
        // Process the @Column or column element if there is one.
        // A number of methods depend on this field so it must be
        // initialized before any further processing can take place.
        m_field = getDatabaseField(getDescriptor().getPrimaryTable(), MetadataLogger.COLUMN);
        
        // To resolve any generic types (or respect an attribute type 
        // specification) we need to set the attribute classification on the 
        // mapping to ensure we do the right conversions.
        if (hasAttributeType() || getAccessibleObject().isGenericType()) {
            mapping.setAttributeClassificationName(getReferenceClassName());
        }
        
        mapping.setField(m_field);
        mapping.setIsReadOnly(m_field.isReadOnly());
        mapping.setAttributeName(getAttributeName());
        mapping.setIsOptional(isOptional());
        mapping.setIsLazy(usesIndirection());

        // Will check for PROPERTY access.
        setAccessorMethods(mapping);
        
        // Process a converter for this mapping. We will look for a convert
        // value first. If none is found then we'll look for a JPA converter, 
        // that is, Enumerated, Lob and Temporal. With everything falling into 
        // a serialized mapping if no converter whatsoever is found.
        processMappingValueConverter(mapping, getConvert(), getReferenceClass());

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
        
        // Process the index metadata.
        processIndex();
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
                    owningDescriptor.setSequenceNumberField(m_field);
                    getProject().addGeneratedValue(m_generatedValue, owningDescriptor.getJavaClass());
                } else {
                    throw ValidationException.onlyOneGeneratedValueIsAllowed(owningDescriptor.getJavaClass(), existingSequenceNumberField.getQualifiedName(), m_field.getQualifiedName());
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
            m_index.process(getDescriptor(), m_field.getName());
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
            m_returnInsert.process(getDescriptor(), m_field);
        }
    }

    /**
     * INTERNAL:
     * Process a return update setting.
     */
    @Override
    protected void processReturnUpdate() {
        if (isReturnUpdate()) {
            getDescriptor().addFieldForUpdate(m_field);
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
}
