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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.lang.annotation.Annotation;
import java.lang.Boolean;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import javax.persistence.Column;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.ReturnUpdate;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.annotations.StructConverter;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;

import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataPrimaryKeyJoinColumn;
import org.eclipse.persistence.internal.jpa.metadata.columns.MetadataPrimaryKeyJoinColumns;

import org.eclipse.persistence.internal.jpa.metadata.converters.MetadataAbstractConverter;
import org.eclipse.persistence.internal.jpa.metadata.converters.MetadataConverter;
import org.eclipse.persistence.internal.jpa.metadata.converters.MetadataObjectTypeConverter;
import org.eclipse.persistence.internal.jpa.metadata.converters.MetadataTypeConverter;
import org.eclipse.persistence.internal.jpa.metadata.converters.MetadataStructConverter;

import org.eclipse.persistence.internal.jpa.metadata.tables.MetadataTable;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.MetadataValidator;
import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;

import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * Top level metatata accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class MetadataAccessor  {
    private boolean m_isProcessed;
    private Boolean m_isRelationship;
    
    protected MetadataLogger m_logger;
    protected MetadataProject m_project;
    protected MetadataProcessor m_processor;
    protected MetadataValidator m_validator;
    protected MetadataDescriptor m_descriptor;
    protected MetadataAccessibleObject m_accessibleObject;
    
    /**
     * INTERNAL:
     */
    public MetadataAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        this(accessibleObject, classAccessor.getProcessor(), classAccessor.getDescriptor());
    }
    
    /**
     * INTERNAL:
     */
    public MetadataAccessor(MetadataAccessibleObject accessibleObject, MetadataProcessor processor, MetadataDescriptor descriptor) {
        m_isProcessed = false;
        
        m_processor = processor;
        m_descriptor = descriptor;
        m_logger = processor.getLogger();
        m_project = processor.getProject();
        m_validator = processor.getValidator();
        
        m_accessibleObject = accessibleObject;
    }
    
    /**
     * INTERNAL:
     * Return the annotated element for this accessor.
     */
    public AnnotatedElement getAnnotatedElement() {
        return m_accessibleObject.getAnnotatedElement();
    }
    
    /**
     * INTERNAL:
     * Return the annotated element for this accessor.
     */
    protected <T extends Annotation> T getAnnotation(Class annotation) {
        return (T) getAnnotation(annotation, getAnnotatedElement());
    }
    
    /**
     * INTERNAL:
     * Return the annotated element for this accessor.
     */
    protected <T extends Annotation> T getAnnotation(Class annotation, AnnotatedElement annotatedElement) {
        return (T) MetadataHelper.getAnnotation(annotation, annotatedElement, m_descriptor);
    }
    
    /**
     * INTERNAL:
     * Return the attribute name for this accessor.
     */
    public String getAttributeName() {
        return m_accessibleObject.getAttributeName();
    }
    
    /**
     * Return the value() of a @Convert if specified. Otherwise, return null.
     */
    protected String getConvertValue() {
        Convert convert = getAnnotation(Convert.class);
        
        if (convert == null) {
            return null;
        } else {
            return convert.value();
        }
    }
    
    /**
     * Return the value() of a @Mutable if specified. Otherwise, return null.
     */
    protected Boolean getMutableValue() {
        Mutable mutable = getAnnotation(Mutable.class);
        
        if (mutable == null) {
            return null;
        } else {
            return Boolean.valueOf(mutable.value());
        }
    }
    
    /**
     * Return the MetadataDescriptor for this accessor.
     */
    public MetadataDescriptor getDescriptor() {
        return m_descriptor;
    }
    
    /**
     * Subclasses that support processing a fetch type should override this 
     * method, otherwise a runtime development exception is thrown for those 
     * accessors who call this method and don't implement it themselves.
     */
    public String getFetchType() {
        throw new RuntimeException("Development exception. The accessor: [" + this + "] should not call the getFetchType method unless it overrides it.");
    }
        
    /**
     * (Overridden in ClassAccessor)
     * Return the java class associated with this accessor's descriptor.
     */
    public Class getJavaClass() {
        return m_descriptor.getJavaClass();
    }
    
    /**
     * Return the java class that defines this accessor.
     */
    protected String getJavaClassName() {
        return getJavaClass().getName();
    }

    /**
     * Return the metadata validator.
     */
    public MetadataLogger getLogger() {
        return m_logger;
    }
    
    /**
     * Return the map key class from a generic Map type. If it is not generic,
     * then null is returned.
     */
    protected Class getMapKeyClass() {
        return m_accessibleObject.getMapKeyClass();
    }
    
    /**
     * Returns the name of this accessor. If it is a field, it will return 
     * the field name. For a method it will return the method name.
     */
    public String getName() {
        return m_accessibleObject.getName();
    }
    
    /**
     * Helper method to return a field name from a candidate field name and a 
     * default field name.
     * 
     * Requires the context from where this method is called to output the 
     * correct logging message when defaulting the field name.
     */
    protected String getName(DatabaseField field, String defaultName, String context) {
        return getName(field.getName(), defaultName, context);
    }
    
    /**
     * Helper method to return a field name from a candidate field name and a 
     * default field name.
     * 
     * Requires the context from where this method is called to output the 
     * correct logging message when defaulting the field name.
     *
     * In some cases, both the name and defaultName could be "" or null,
     * therefore, don't log any message and return name.
     */
    protected String getName(String name, String defaultName, String context) {
        // Check if a candidate was specified otherwise use the default.
        if (name != null && !name.equals("")) {
            return name;
        } else if (defaultName == null || defaultName.equals("")) {
            return "";
        } else {
            // Log the defaulting field name based on the given context.
            m_logger.logConfigMessage(context, getAnnotatedElement(), defaultName);
            return defaultName;
        }
    }
    
    /**
     * (Overridden in XMLClassAccessor and XMLOneToOneAccessor)
     * Process the @PrimaryKeyJoinColumns and @PrimaryKeyJoinColumn.
     */    
    protected MetadataPrimaryKeyJoinColumns getPrimaryKeyJoinColumns(DatabaseTable sourceTable, DatabaseTable targetTable) {
        PrimaryKeyJoinColumn primaryKeyJoinColumn = getAnnotation(PrimaryKeyJoinColumn.class);
        PrimaryKeyJoinColumns primaryKeyJoinColumns = getAnnotation(PrimaryKeyJoinColumns.class);
        
        return new MetadataPrimaryKeyJoinColumns(primaryKeyJoinColumns, primaryKeyJoinColumn, sourceTable, targetTable);
    }
    
    /**
     * Return the MetadataProject.
     */
    public MetadataProject getProject() {
        return m_project;
    }
    
    /**
     * Return the MetadataProcessor.
     */
    public MetadataProcessor getProcessor() {
        return m_processor;
    }

    /**
     * Return the raw class for this accessor. 
     * Eg. For an accessor with a type of java.util.Collection<Employee>, this 
     * method will return java.util.Collection
     */
    public Class getRawClass() {
        return m_accessibleObject.getRawClass();   
    }
    
    /**
     * (Overridden in CollectionAccessor, ObjectAccessor, 
     * BasicCollectionAccessor and BasicMapAccessor)
     * 
     * Return the reference class for this accessor. By default the reference
     * class is the raw class. Some accessors may need to override this
     * method to drill down further. That is, try to extract a reference class
     * from generics.
     */
    public Class getReferenceClass() {
        return m_accessibleObject.getRawClass();
    }
    
    /**
     * Attempts to return a reference class from a generic specification. Note,
     * this method may return null.
     */
    public Class getReferenceClassFromGeneric() {
        return m_accessibleObject.getReferenceClassFromGeneric();
    }

    /**
     * Return the reference class name for this accessor.
     */
    public String getReferenceClassName() {
        return getReferenceClass().getName();
    }
    
    /**
     * Return the reference metadata descriptor for this accessor.
     */
    public MetadataDescriptor getReferenceDescriptor() {
        return m_project.getDescriptor(getReferenceClass());
    }
    
    /**
     * Return the relation type of this accessor.
     */
    protected Type getRelationType() {
        return m_accessibleObject.getRelationType();
    }
    
    /**
     * Returns the set method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    protected String getSetMethodName() {
        return ((MetadataMethod) m_accessibleObject).getSetMethodName();
    }
    
    /**
     * Return the upper cased attribute name for this accessor. Used when
     * defaulting.
     */
    protected String getUpperCaseAttributeName() {
        return getAttributeName().toUpperCase();
    }
    
    /**
     * Return the upper case java class that defines this accessor.
     */
    protected String getUpperCaseShortJavaClassName() {
        return Helper.getShortClassName(getJavaClassName()).toUpperCase();
    }
    
    /**
     * Return the metadata validator.
     */
    public MetadataValidator getValidator() {
        return m_validator;
    }
    
    /**
     * Method to check if an annotated element has a @Column.
     */
    protected boolean hasColumn() {
        return isAnnotationPresent(Column.class);
    }
    
    /**
     * (Overridden in BasicMapAccessor)
     * Method to check if an annotated element has a @Convert.
     */
    protected boolean hasConvert() {
        return isAnnotationPresent(Convert.class);
    }
    
    /**
     * Method to check if this accessor has @PrimaryKeyJoinColumns.
     */
    protected boolean hasPrimaryKeyJoinColumns() {
        return isAnnotationPresent(PrimaryKeyJoinColumns.class);
    }
    
    /**
     * (Overridden in ManyToOneAccessor)
     * Method to check if this accesosr has a @PrivateOwned.
     */
    protected boolean hasPrivateOwned() {
        return isAnnotationPresent(PrivateOwned.class);
    }
    
    /**
     * (Overridden in XMLBasicAccessor)
     * Method to check if this accesosr has a @ReturnInsert.
     */
    protected boolean hasReturnInsert() {
        return isAnnotationPresent(ReturnInsert.class);
    }
    
    /**
     * (Overridden in XMLBasicAccessor)
     * Method to check if this accesosr has a @ReturnUpdate.
     */
    protected boolean hasReturnUpdate() {
        return isAnnotationPresent(ReturnUpdate.class);
    }
    
    /** 
     * Indicates whether the specified annotation is present on the annotated
     * element for this accessor. Method checks against the metadata complete
     * flag.
     */
    protected boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return isAnnotationPresent(annotation, getAnnotatedElement());
    }
    
    /** 
     * Indicates whether the specified annotation is present on the annotated
     * element for this accessor. Method checks against the metadata complete
     * flag.
     */
    protected boolean isAnnotationPresent(Class<? extends Annotation> annotation, AnnotatedElement annotatedElement) {
        return MetadataHelper.isAnnotationPresent(annotation, annotatedElement, m_descriptor);
    }
    
    /**
     * Return true if this accessor represents a basic mapping.
     */
    public boolean isBasic() {
        return false;
    }
    
    /**
     * Return true if this accessor represents a basic collection mapping.
     */
    public boolean isBasicCollection() {
        return false;
    }
    
    /**
     * Return true if this accessor represents a basic map mapping.
     */
    public boolean isBasicMap() {
        return false;
    }
    
    /**
     * Return true if this accessor represents a class.
     */
    public boolean isClass() {
        return false;
    }
    
    /**
     * Return true if this accessor represents an aggregate mapping.
     */
    public boolean isEmbedded() {
        return false;
    }
    
    /**
     * Return true if this accessor represents an aggregate id mapping.
     */
    public boolean isEmbeddedId() {
        return false;
    }
    
    /**
     * Return true if this accessor represents a m-m relationship.
     */
    public boolean isManyToMany() {
        return false;
    }
    
    /**
     * Return true if this accessor represents a m-1 relationship.
     */
    public boolean isManyToOne() {
        return false;
    }
    
    /**
     * Return true if this accessor represents a 1-m relationship.
     */
    public boolean isOneToMany() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-1 relationship.
     */
    public boolean isOneToOne() {
        return false;
    }
    
    /**
     * INTERNAL:
     * 
     * Subclasses that support processing an optional setting should override 
     * this  method, otherwise a runtime development exception is thrown for 
     * those  accessors who call this method and don't implement it themselves.
     */
    public boolean isOptional() {
        throw new RuntimeException("Development exception. The accessor: [" + this + "] should not call the isOptional method unless it overrides it.");
    }
	
    /**
     * INTERNAL:
     * Return true if this accessor method represents a relationship. It will
     * cache the boolean value to avoid multiple checks and validation.
     */
    public boolean isRelationship() {
        if (m_isRelationship == null) {
            m_isRelationship = new Boolean(isManyToOne() || isManyToMany() || isOneToMany() || isOneToOne());
        }
        
        return m_isRelationship.booleanValue(); 
    }
    
    /**
     * INTERNAL:
     * Return true if this is an XML processing accessor.
     */
    public boolean isXMLAccessor() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has already been processed.
     */
    public boolean isProcessed() {
        return m_isProcessed;    
    }
    
    /**
     * INTERNAL:
     * Every accessor knows how to process themselves since they have all the
     * information they need.
     */
    public abstract void process();
    
    /**
     * INTERNAL:
     * Process an AbstractMetadataConverter and add it to the project. 
     */
    protected void processAbstractConverter(MetadataAbstractConverter converter) {
        if (m_project.hasStructConverter(converter.getName())){
           m_validator.throwStructConverterOfSameNameAsConverter(converter.getName());
        } else if (m_project.hasConverter(converter.getName())) {
            MetadataAbstractConverter existingConverter = m_project.getConverter(converter.getName());
            
            if (existingConverter.loadedFromAnnotations() && converter.loadedFromXML()) {
                // Override the existing converter.
                m_project.addConverter(converter);
            } else {
                m_validator.throwMultipleConvertersOfTheSameName(converter.getName());
            }
        } else {
            m_project.addConverter(converter);
        }
    }
    
    /**
     * INTERNAL: (Overidden in XMLClassAccessor and XMLEmbeddedAccessor)
     * Fast track processing a ClassAccessor for the given descriptor. 
     * Inheritance root classes and embeddables may be fast tracked.
     */
    protected ClassAccessor processAccessor(MetadataDescriptor descriptor) {
        ClassAccessor accessor = new ClassAccessor(new MetadataClass(descriptor.getJavaClass()), getProcessor(), descriptor);
        descriptor.setClassAccessor(accessor);
        accessor.process();
        return accessor;
    }
    
    /**
     * INTERNAL: (Overridden in DirectAccessor)
     * 
     * If this method is called on an accessor that does not override this
     * method (that is, doesn't support this functionality) then throw an
     * exception.
     */
    public void processConvert() {
        m_validator.throwInvalidMappingForConverter(getJavaClass(), getAnnotatedElement());
    }
    
    /**
     * INTERNAL:
     * 
     * Process a @Convert value which specifies the name of TopLink converter
     * to process with this accessor's mapping. TopLink converters (which are 
     * global to the persistent unit) can not be processed till we have
     * processed all the classes in the persistence unit. So for now, add this 
     * accessor to the project list of convert dependant accessors, and process 
     * it in stage 2, that is, during the project process.
     * 
     * Those accessor's that which to have an exception thrown if a @Convert
     * annotation is specified, should call this method in their process() 
     * method. That is, processConvert(getConvertValue());
     */
    protected void processConvert(String convertValue) {
        if (convertValue != null && !convertValue.equals(MetadataConstants.CONVERT_NONE)) {
            // TopLink converter specified, defer this accessors converter
            // processing to stage 2 project processing.
            m_project.addConvertAccessor(this);
        } 
    }
    
    /**
     * INTERNAL:
     * 
     * Process a @Converter if defined for this accessor. 
     */
    protected void processConverter() {
        // Will check against metadata complete.
        Converter converter = getAnnotation(Converter.class);
            
        if (converter != null) {
            processAbstractConverter(new MetadataConverter(converter));
        }
    }
    
    /**
     * INTERNAL:
     * Process the globally defined converters.
     */
    protected void processConverters() {
        // Process a custom converter if defined.
        processConverter();
        
        // Process an object type converter if defined.
        processObjectTypeConverter();
        
        // Process a type converter if defined.
        processTypeConverter();
        
        // Process struct converter if defined
        processStructConverter();
    }
    
    /**
     * INTERNAL:
     * 
     * Process an @ObjectTypeConverter if defined for this accessor. 
     */
    protected void processObjectTypeConverter() {        
        // Will check against metadata complete.
        ObjectTypeConverter converter = getAnnotation(ObjectTypeConverter.class);
            
        if (converter != null) {
            processAbstractConverter(new MetadataObjectTypeConverter(converter, m_validator, getJavaClass()));
        }
    }
    
    /**
     * Return the mapping joinFetch constant for the JoinFetch annotation if present.
     */
    protected int getMappingJoinFetchType() {        
        // Will check against metadata complete.
        JoinFetch joinFetch = getAnnotation(JoinFetch.class);            
        if (joinFetch == null) {
            return ForeignReferenceMapping.NONE;
        }
        if (joinFetch.value().equals(JoinFetchType.INNER)) {
            return ForeignReferenceMapping.INNER_JOIN;
        } else if (joinFetch.value().equals(JoinFetchType.OUTER)) {
            return ForeignReferenceMapping.OUTER_JOIN;
        }
        return ForeignReferenceMapping.NONE;
    }
    
    /**
     * INTERNAL:
     * 
     * Process the primary key join columms for this accessors annotated element.
     */	
    protected List<MetadataPrimaryKeyJoinColumn> processPrimaryKeyJoinColumns(MetadataPrimaryKeyJoinColumns primaryKeyJoinColumns) {
        // This call will add any defaulted columns as necessary.
        List<MetadataPrimaryKeyJoinColumn> pkJoinColumns = primaryKeyJoinColumns.values(m_descriptor);
        
        if (m_descriptor.hasCompositePrimaryKey()) {
            // Validate the number of primary key fields defined.
            if (pkJoinColumns.size() != m_descriptor.getPrimaryKeyFields().size()) {
                m_validator.throwIncompletePrimaryKeyJoinColumnsSpecified(getJavaClass(), getAnnotatedElement());
            }
            
            // All the primary and foreign key field names should be specified.
            for (MetadataPrimaryKeyJoinColumn pkJoinColumn : pkJoinColumns) {
                if (pkJoinColumn.isPrimaryKeyFieldNotSpecified() || pkJoinColumn.isForeignKeyFieldNotSpecified()) {
                    m_validator.throwIncompletePrimaryKeyJoinColumnsSpecified(getJavaClass(), getAnnotatedElement());
                }
            }
        } else {
            if (pkJoinColumns.size() > 1) {
                m_validator.throwExcessivePrimaryKeyJoinColumnsSpecified(getJavaClass(), getAnnotatedElement());
            }
        }
        
        return pkJoinColumns;
    }
    
    /**
     * INTERNAL:
     * 
     * Subclasses should call this method if they want the warning message.
     */
    protected void processReturnInsert() {
        if (hasReturnInsert()) {
            m_logger.logWarningMessage(MetadataLogger.IGNORE_RETURN_INSERT_ANNOTATION, getAnnotatedElement());
        }
    }
    
    /**
     * INTERNAL:
     * 
     * Subclasses should call this method if they want the warning message.
     */
    protected void processReturnInsertAndUpdate() {
        processReturnInsert();
        processReturnUpdate();
    }
    
    /**
     * INTERNAL:
     * 
     * Subclasses should call this method if they want the warning message.
     */
    protected void processReturnUpdate() {
        if (hasReturnUpdate()) {
            m_logger.logWarningMessage(MetadataLogger.IGNORE_RETURN_UPDATE_ANNOTATION, getAnnotatedElement());
        }
    }
      
    /**
     * INTERNAL:
     * 
     * Process a @TypeConverter if defined for this accessor. 
     */
    protected void processStructConverter() {
        // Will check against metadata complete.
        StructConverter converter = getAnnotation(StructConverter.class);
            
        if (converter != null) {
            processStructConverter(new MetadataStructConverter(converter));
        }
    }
    
    /**
     * INTERNAL:
     * Process an AbstractMetadataConverter and add it to the project. 
     */
    protected void processStructConverter(MetadataStructConverter converter) {
        if (m_project.hasConverter(converter.getName())) {
            m_validator.throwStructConverterOfSameNameAsConverter(converter.getName());
        } else if (m_project.hasStructConverter(converter.getName())){
            MetadataStructConverter existingConverter = m_project.getStructConverter(converter.getName());
            
            if (existingConverter.loadedFromAnnotations() && converter.loadedFromXML()) {
                // Override the existing converter.
                m_project.addStructConverter(converter);
            } else {
                m_validator.throwMultipleStructConvertersOfTheSameName(converter.getName());
            }          
        } else {
            m_project.addStructConverter(converter);
        }
    }
    
    /**
     * INTERNAL:
     * Common table processing for table, secondary table, join table and
     * collection table.
     */
    protected void processTable(MetadataTable table, String defaultName) {
        // Name could be "", need to check against the default name.
		String name = getName(table.getName(), defaultName, table.getNameContext());
        
        // Catalog could be "", need to check for an XML default.
        String catalog = getName(table.getCatalog(), m_descriptor.getCatalog(), table.getCatalogContext());
        
        // Schema could be "", need to check for an XML default.
        String schema = getName(table.getSchema(), m_descriptor.getSchema(), table.getSchemaContext());
        
        // Build a fully qualified name and set it on the table.
        table.setName(MetadataHelper.getFullyQualifiedTableName(name, catalog, schema));
    }
    
    /**
     * INTERNAL:
     * 
     * Process a @TypeConverter if defined for this accessor. 
     */
    protected void processTypeConverter() {
        // Will check against metadata complete.
        TypeConverter converter = getAnnotation(TypeConverter.class);
            
        if (converter != null) {
            processAbstractConverter(new MetadataTypeConverter(converter));
        }
    }
   
    /**
     * INTERNAL:
     * 
     * Set the getter and setter access methods for this accessor.
     */
    protected void setAccessorMethods(DatabaseMapping mapping) {
        if (m_descriptor.usesPropertyAccess()) {
            mapping.setGetMethodName(getName());
            mapping.setSetMethodName(getSetMethodName());
        }
    }
    
    /**
     * INTERNAL:
     * Return the annotated element for this accessor.
     */
    public void setAnnotatedElement(AnnotatedElement annotatedElement) {
        m_accessibleObject.setAnnotatedElement(annotatedElement);
    }
    
    /** 
     * INTERNAL:
	 * Set the correct indirection policy on a collection mapping. Method
     * assume that the reference class has been set on the mapping before
     * calling this method.
	 */
	protected void setIndirectionPolicy(CollectionMapping mapping, String mapKey) {
        Class rawClass = getRawClass();
        
        if (usesIndirection()) {            
            if (rawClass == Map.class) {
                mapping.useTransparentMap(mapKey);
            } else if (rawClass == List.class) {
                mapping.useTransparentList();
            } else if (rawClass == Collection.class) {
                mapping.useTransparentCollection();
                mapping.setContainerPolicy(new CollectionContainerPolicy(ClassConstants.IndirectList_Class));
            } else if (rawClass == Set.class) {
                mapping.useTransparentSet();
            } else {
                // Because of validation we should never get this far.
            }
        } else {
            mapping.dontUseIndirection();
            
            if (rawClass == Map.class) {
                mapping.useMapClass(java.util.Hashtable.class, mapKey);
            } else if (rawClass == Set.class) {
                mapping.useCollectionClass(java.util.HashSet.class);
            } else {
                mapping.useCollectionClass(java.util.Vector.class);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setIsProcessed() {
        m_isProcessed = true;	
	}
    
    /**
     * INTERNAL:
     */
    public boolean usesIndirection() {
        return getFetchType().equals(MetadataConstants.LAZY);
    }
}
