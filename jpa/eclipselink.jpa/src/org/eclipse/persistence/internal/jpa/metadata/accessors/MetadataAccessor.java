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
import javax.persistence.FetchType;
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
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;

import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.converters.AbstractConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ObjectTypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TypeConverterMetadata;

import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;

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
   
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns;
    
    private MetadataAccessibleObject m_accessibleObject;
    private MetadataDescriptor m_descriptor;
    private MetadataProject m_project;
    
    private String m_name;
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public MetadataAccessor() {}
    
    /**
     * INTERNAL:
     */
    public MetadataAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        init(accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL:
     */
    public MetadataAccessor(MetadataAccessibleObject accessibleObject, MetadataDescriptor descriptor, MetadataProject project) {
    	init(accessibleObject, descriptor, project);
    }
    
    /**
     * INTERNAL: 
     * Overridden in those XML accessor classes that need to initialize
     * any classes or other metadata before processing.
     * - RelationshipAccessor - target entity name
     */
    public void init(MetadataAccessibleObject accessibleObject, ClassAccessor accessor) {
    	init(accessibleObject, accessor.getDescriptor(), accessor.getProject());
    }
    
    /**
     * INTERNAL: 
     */
    public void init(MetadataAccessibleObject accessibleObject, MetadataDescriptor descriptor, MetadataProject project) {
    	m_isProcessed = false;
    	m_project = project;
        m_descriptor = descriptor;
        m_accessibleObject = accessibleObject;
    }
    
    /**
     * INTERNAL:
     * Returns the name of the accessible object. If it is a field, it will 
     * return the field name. For a method it will return the method name.
     */
    public String getAccessibleObjectName() {
        return m_accessibleObject.getName();
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
     * Return the annotated element name for this accessor.
     */
    public String getAnnotatedElementName() {
        return getAnnotatedElement().toString();
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
     * INTERNAL:
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
     * INTERNAL:
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
     * INTERNAL:
     */
    public FetchType getDefaultFetchType() {
    	return FetchType.EAGER; 
    }
    
    /**
     * INTERNAL:
     * Return the MetadataDescriptor for this accessor.
     */
    public MetadataDescriptor getDescriptor() {
        return m_descriptor;
    }
        
    /**
     * INTERNAL: (Overridden in ClassAccessor)
     * Return the java class associated with this accessor's descriptor.
     */
    public Class getJavaClass() {
        return m_descriptor.getJavaClass();
    }
    
    /**
     * INTERNAL:
     * Return the java class that defines this accessor.
     */
    protected String getJavaClassName() {
        return getJavaClass().getName();
    }

    /**
     * INTERNAL:
     * Return the metadata logger.
     */
    public MetadataLogger getLogger() {
        return m_project.getLogger();
    }
    
    /**
     * INTERNAL:
     * Return the map key class from a generic Map type. If it is not generic,
     * then null is returned.
     */
    protected Class getMapKeyClass() {
        return m_accessibleObject.getMapKeyClass();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
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
     * INTERNAL:
     * Helper method to return a field name from a candidate field name and a 
     * default field name.
     * 
     * Requires the context from where this method is called to output the 
     * correct logging message when defaulting the field name.
     *
     * In some cases, both the name and defaultName could be "" or null,
     * therefore, don't log a message and return name.
     */
    protected String getName(String name, String defaultName, String context) {
    	return MetadataHelper.getName(name, defaultName, context, getLogger(), getAnnotatedElement().toString());
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */    
    public List<PrimaryKeyJoinColumnMetadata> getPrimaryKeyJoinColumns() {
    	return m_primaryKeyJoinColumns;
    }
    
    /**
     * INTERNAL:
     * Return the MetadataProject.
     */
    public MetadataProject getProject() {
        return m_project;
    }

    /**
     * INTERNAL:
     * Return the raw class for this accessor. 
     * Eg. For an accessor with a type of java.util.Collection<Employee>, this 
     * method will return java.util.Collection
     */
    public Class getRawClass() {
        return m_accessibleObject.getRawClass();   
    }
    
    /**
     * INTERNAL: (Overridden in CollectionAccessor, ObjectAccessor, 
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
     * INTERNAL:
     * Attempts to return a reference class from a generic specification. Note,
     * this method may return null.
     */
    public Class getReferenceClassFromGeneric() {
        return m_accessibleObject.getReferenceClassFromGeneric();
    }

    /**
     * INTERNAL:
     * Return the reference class name for this accessor.
     */
    public String getReferenceClassName() {
        return getReferenceClass().getName();
    }
    
    /**
     * INTERNAL:
     * Return the reference metadata descriptor for this accessor.
     */
    public MetadataDescriptor getReferenceDescriptor() {
    	ClassAccessor accessor = m_project.getAccessor(getReferenceClassName());
    	
        if (accessor == null) {
        	throw ValidationException.classNotListedInPersistenceUnit(getReferenceClassName());
        }
        
        return accessor.getDescriptor();
    }
    
    /**
     * INTERNAL:
     * Return the relation type of this accessor.
     */
    protected Type getRelationType() {
        return m_accessibleObject.getRelationType();
    }
    
    /**
     * INTERNAL:
     * Returns the set method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    protected String getSetMethodName() {
        return ((MetadataMethod) m_accessibleObject).getSetMethodName();
    }
    
    /**
     * INTERNAL:
     * Return the upper cased attribute name for this accessor. Used when
     * defaulting.
     */
    protected String getUpperCaseAttributeName() {
        return getAttributeName().toUpperCase();
    }
    
    /**
     * INTERNAL:
     * Return the upper case java class that defines this accessor.
     */
    protected String getUpperCaseShortJavaClassName() {
        return Helper.getShortClassName(getJavaClassName()).toUpperCase();
    }
    
    /**
     * INTERNAL:
     * Helper method to return a string value if specified, otherwise returns
     * the default value.
     */
    protected String getValue(String value, String defaultValue) {
        // Check if a candidate was specified otherwise use the default.
        if (value != null && ! value.equals("")) {
            return value;
        } else {
        	// Future: log a defaulting message
            return defaultValue;
        }
    }
    
    /**
     * INTERNAL:
     * Helper method to return a string value if specified, otherwise returns
     * the default value. 
     */
    protected Integer getValue(Integer value, Integer defaultValue) {
        // Check if a candidate was specified otherwise use the default.
        if (value == null) {
        	return defaultValue;
        } else {
        	// Future: log a defaulting message
            return value;
        } 
    }
    
    /**
     * INTERNAL:
     * Method to check if an annotated element has a @Column.
     */
    protected boolean hasColumn() {
        return isAnnotationPresent(Column.class);
    }
    
    /**
     * INTERNAL: (Overridden in BasicMapAccessor)
     * Method to check if an annotated element has a @Convert.
     */
    protected boolean hasConvert() {
        return isAnnotationPresent(Convert.class);
    }
    
    /**
     * INTERNAL:
     * Method to check if this accessor has @PrimaryKeyJoinColumns.
     */
    protected boolean hasPrimaryKeyJoinColumns() {
        return isAnnotationPresent(PrimaryKeyJoinColumns.class);
    }
    
    /**
     * INTERNAL: (Overridden in ManyToOneAccessor)
     * Method to check if this accesosr has a @PrivateOwned.
     */
    protected boolean hasPrivateOwned() {
        return isAnnotationPresent(PrivateOwned.class);
    }
    
    /**
     * INTERNAL:
     * Method to check if this accesosr has a ReturnInsert annotation.
     */
    protected boolean hasReturnInsert() {
        return isAnnotationPresent(ReturnInsert.class);
    }
    
    /**
     * INTERNAL:
     * Method to check if this accesosr has a ReturnUpdate annotation.
     */
    protected boolean hasReturnUpdate() {
        return isAnnotationPresent(ReturnUpdate.class);
    }
    
    /** 
     * INTERNAL:
     * Indicates whether the specified annotation is present on the annotated
     * element for this accessor. Method checks against the metadata complete
     * flag.
     */
    protected boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return isAnnotationPresent(annotation, getAnnotatedElement());
    }
    
    /** 
     * INTERNAL:
     * Indicates whether the specified annotation is present on the annotated
     * element for this accessor. Method checks against the metadata complete
     * flag.
     */
    protected boolean isAnnotationPresent(Class<? extends Annotation> annotation, AnnotatedElement annotatedElement) {
        return MetadataHelper.isAnnotationPresent(annotation, annotatedElement, m_descriptor);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a basic collection mapping.
     */
    public boolean isBasicCollection() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a basic map mapping.
     */
    public boolean isBasicMap() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a class.
     */
    public boolean isClass() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents an aggregate mapping.
     */
    public boolean isEmbedded() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents an aggregate id mapping.
     */
    public boolean isEmbeddedId() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a m-m relationship.
     */
    public boolean isManyToMany() {
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a m-1 relationship.
     */
    public boolean isManyToOne() {
        return false;
    }
    
    /**
     * INTERNAL:
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
     * Subclasses that support processing an optional setting should override 
     * this  method, otherwise a runtime development exception is thrown for 
     * those accessors who call this method and don't implement it themselves.
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
    protected void processAbstractConverter(AbstractConverterMetadata converter) {
        if (m_project.hasStructConverter(converter.getName())){
        	throw ValidationException.structConverterOfSameNameAsConverter(converter.getName());
        } else if (m_project.hasConverter(converter.getName())) {
        	AbstractConverterMetadata existingConverter = m_project.getConverter(converter.getName());
            
            if (existingConverter.loadedFromAnnotations() && converter.loadedFromXML()) {
                // Override the existing converter.
                m_project.addConverter(converter);
            } else {
            	throw ValidationException.multipleConvertersOfTheSameName(converter.getName());
            }
        } else {
            m_project.addConverter(converter);
        }
    }
    
    /**
     * INTERNAL: (Overridden in DirectAccessor)
     * If this method is called on an accessor that does not override this
     * method (that is, doesn't support this functionality) then throw an
     * exception.
     */
    public void processConvert() {
    	throw ValidationException.invalidMappingForConverter(getJavaClass(), getAnnotatedElement());
    }
    
    /**
     * INTERNAL:
     * Process a Convert annotation which specifies the name of an EclipseLink
     * converter to process with this accessor's mapping. EclipseLink converters 
     * (which are global to the persistent unit) can not be processed till we 
     * have processed all the classes in the persistence unit. So for now, add 
     * this accessor to the project list of convert dependant accessors, and 
     * process it in stage 2, that is, during the project process.
     * 
     * Those accessor's that which to have an exception thrown if a Convert
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
     * Process a Converter annotation if defined for this accessor. 
     */
    protected void processConverter() {
        // Will check against metadata complete.
        Converter converter = getAnnotation(Converter.class);
            
        if (converter != null) {
            processAbstractConverter(new ConverterMetadata(converter));
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
     * Process an ObjectTypeConverter annotation if defined for this accessor. 
     */
    protected void processObjectTypeConverter() {        
        // Will check against metadata complete.
        ObjectTypeConverter converter = getAnnotation(ObjectTypeConverter.class);
            
        if (converter != null) {
            processAbstractConverter(new ObjectTypeConverterMetadata(converter));
        }
    }
    
    /**
     * INTERNAL:
     * Return the mapping joinFetch constant for the JoinFetch annotation if 
     * present.
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
     * Process the primary key join columms for this accessors annotated element.
     */	
    protected List<PrimaryKeyJoinColumnMetadata> processPrimaryKeyJoinColumns(PrimaryKeyJoinColumnsMetadata primaryKeyJoinColumns) {
    	// If the primary key join columns were not specified (that is empty),
    	// this call will add any defaulted columns as necessary.
    	 List<PrimaryKeyJoinColumnMetadata> pkJoinColumns = primaryKeyJoinColumns.values(m_descriptor);
        
        if (m_descriptor.hasCompositePrimaryKey()) {
            // Validate the number of primary key fields defined.
            if (pkJoinColumns.size() != m_descriptor.getPrimaryKeyFields().size()) {
            	throw ValidationException.incompletePrimaryKeyJoinColumnsSpecified(getAnnotatedElement());
            }
            
            // All the primary and foreign key field names should be specified.
            for (PrimaryKeyJoinColumnMetadata pkJoinColumn : pkJoinColumns) {
                if (pkJoinColumn.isPrimaryKeyFieldNotSpecified() || pkJoinColumn.isForeignKeyFieldNotSpecified()) {
                	throw ValidationException.incompletePrimaryKeyJoinColumnsSpecified(getAnnotatedElement());
                }
            }
        } else {
            if (pkJoinColumns.size() > 1) {
            	throw ValidationException.excessivePrimaryKeyJoinColumnsSpecified(getAnnotatedElement());
            }
        }
        
        return pkJoinColumns;
    }
    
    /**
     * INTERNAL:
     * Subclasses should call this method if they want the warning message.
     */
    protected void processReturnInsert() {
        if (hasReturnInsert()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_RETURN_INSERT_ANNOTATION, getAnnotatedElement());
        }
    }
    
    /**
     * INTERNAL:
     * Subclasses should call this method if they want the warning message.
     */
    protected void processReturnInsertAndUpdate() {
        processReturnInsert();
        processReturnUpdate();
    }
    
    /**
     * INTERNAL:
     * Subclasses should call this method if they want the warning message.
     */
    protected void processReturnUpdate() {
        if (hasReturnUpdate()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_RETURN_UPDATE_ANNOTATION, getAnnotatedElement());
        }
    }
      
    /**
     * INTERNAL:
     * Process a TypeConverter annotation if defined for this accessor. 
     */
    protected void processStructConverter() {
        // Will check against metadata complete.
        StructConverter converter = getAnnotation(StructConverter.class);
            
        if (converter != null) {
            processStructConverter(new StructConverterMetadata(converter));
        }
    }
    
    /**
     * INTERNAL:
     * Process an AbstractMetadataConverter and add it to the project. 
     */
    protected void processStructConverter(StructConverterMetadata converter) {
        if (m_project.hasConverter(converter.getName())) {
        	throw ValidationException.structConverterOfSameNameAsConverter(converter.getName());
        } else if (m_project.hasStructConverter(converter.getName())){
        	StructConverterMetadata existingConverter = m_project.getStructConverter(converter.getName());
            
            if (existingConverter.loadedFromAnnotations() && converter.loadedFromXML()) {
                // Override the existing converter.
                m_project.addStructConverter(converter);
            } else {
            	throw ValidationException.multipleStructConvertersOfTheSameName(converter.getName());
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
    protected void processTable(TableMetadata table, String defaultName) {
    	getProject().processTable(table, defaultName, m_descriptor.getXMLCatalog(), m_descriptor.getXMLSchema());
    }
    
    /**
     * INTERNAL:
     * Process a TypeConverter annotation if defined for this accessor. 
     */
    protected void processTypeConverter() {
        // Will check against metadata complete.
        TypeConverter converter = getAnnotation(TypeConverter.class);
            
        if (converter != null) {
            processAbstractConverter(new TypeConverterMetadata(converter));
        }
    }
   
    /**
     * INTERNAL:
     * Set the accessible object for this accessor.
     */
    public void setAccessibleObject(MetadataAccessibleObject accessibleObject) {
    	m_accessibleObject = accessibleObject;
    }
    
    /**
     * INTERNAL:
     * Set the getter and setter access methods for this accessor.
     */
    protected void setAccessorMethods(DatabaseMapping mapping) {
        if (m_descriptor.usesPropertyAccess()) {
            mapping.setGetMethodName(getAccessibleObjectName());
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
     * Set the metadata descriptor for this accessor.
     */
    public void setDescriptor(MetadataDescriptor descriptor) {
    	m_descriptor = descriptor;
    }
    
    /** 
     * INTERNAL:
	 * Set the correct indirection policy on a collection mapping. Method
     * assume that the reference class has been set on the mapping before
     * calling this method.
	 */
	public void setIndirectionPolicy(CollectionMapping mapping, String mapKey, boolean usesIndirection) {
		Class rawClass = getRawClass();
		
        if (usesIndirection) {            
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
     * Used for OX mapping.
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
    	m_primaryKeyJoinColumns = primaryKeyJoinColumns;
    }
}
