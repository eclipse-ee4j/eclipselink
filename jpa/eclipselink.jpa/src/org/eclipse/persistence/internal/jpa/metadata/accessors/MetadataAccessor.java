/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
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
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.PrimaryKeyJoinColumns;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ReturnInsert;
import org.eclipse.persistence.annotations.ReturnUpdate;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.annotations.StructConverter;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;

import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ObjectTypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TypeConverterMetadata;

import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
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
   
    private List<ConverterMetadata> m_converters;
    private List<ObjectTypeConverterMetadata> m_objectTypeConverters;
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns;
    private List<StructConverterMetadata> m_structConverters;
    private List<TypeConverterMetadata> m_typeConverters;
    
    private MetadataAccessibleObject m_accessibleObject;
    private MetadataDescriptor m_descriptor;
    private MetadataProject m_project;
   
    private String m_name;
    
    private XMLEntityMappings m_entityMappings;
    
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
    	init(accessibleObject, descriptor, project,  null);
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
        Object loadedAnnotation = m_accessibleObject.getAnnotations().get(annotation.getName());
        
        if (loadedAnnotation != null && m_descriptor.ignoreAnnotations()) {
            m_descriptor.getLogger().logWarningMessage(MetadataLogger.IGNORE_ANNOTATION, annotation, m_accessibleObject.getAnnotatedElement());
            return null;
        } else {
            return (T) loadedAnnotation;
        }
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
     * Used for OX mapping.
     */
	public List<ConverterMetadata> getConverters() {
		return m_converters;
	}
    
    /**
     * INTERNAL:
     */
    public Enum getDefaultFetchType() {
    	return FetchType.valueOf("EAGER"); 
    }
    
    /**
     * INTERNAL:
     * Return the MetadataDescriptor for this accessor.
     */
    public MetadataDescriptor getDescriptor() {
        return m_descriptor;
    }
       
    /**
     * INTERNAL:
     */
    public XMLEntityMappings getEntityMappings() {
    	return m_entityMappings;
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
     * Return the mapping join fetch type.
     */
    protected int getMappingJoinFetchType(Enum joinFetchType) {
        if (joinFetchType == null) {
            // Will check against metadata complete.
            Object joinFetch = getAnnotation(JoinFetch.class);            
            if (joinFetch == null) {
               return ForeignReferenceMapping.NONE;	
            } else if (((Enum)invokeMethod("value", joinFetch)).equals(JoinFetchType.INNER)) {
               return ForeignReferenceMapping.INNER_JOIN;
            }
        } else if (joinFetchType.equals(JoinFetchType.INNER)) {
            return ForeignReferenceMapping.INNER_JOIN;
        }
        
        return ForeignReferenceMapping.OUTER_JOIN;
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
	public List<ObjectTypeConverterMetadata> getObjectTypeConverters() {
		return m_objectTypeConverters;
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
	 * Used for OX mapping.
	 */
	public List<StructConverterMetadata> getStructConverters() {
		return m_structConverters;
	}
	
	/**
     * INTERNAL:
     * Used for OX mapping.
     */
	public List<TypeConverterMetadata> getTypeConverters() {
		return m_typeConverters;
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
    protected Integer getValue(Integer value, Integer defaultValue) {
        return MetadataHelper.getValue(value, defaultValue);
    }
    
    /**
     * INTERNAL:
     * Helper method to return a string value if specified, otherwise returns
     * the default value.
     */
    protected String getValue(String value, String defaultValue) {
        return MetadataHelper.getValue(value, defaultValue);
    }

    /**
     * INTERNAL:
     * Method to check if an annotated element has a Column annotation.
     */
    protected boolean hasColumn() {
        return isAnnotationPresent(Column.class);
    }
    
    /**
     * INTERNAL: (Overridden in DirectAccessor and BasicMapAccessor)
     * Method to check if an annotated element has a convert specified.
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
     * Overridden in those accessor classes that need to initialize
     * any classes or other metadata before processing.
     * - RelationshipAccessor - target entity name
     * - BasicAccessor - attribute name on the column
     * - BasicCollectionAccessor - attribute name on the value column
     * - BasicMapAccessor - attribute name on the key column
     */
    public void init(MetadataAccessibleObject accessibleObject, ClassAccessor accessor) {
    	init(accessibleObject, accessor.getDescriptor(), accessor.getProject(), accessor.getEntityMappings());
    }
    
    /**
     * INTERNAL: 
     */
    public void init(MetadataAccessibleObject accessibleObject, MetadataDescriptor descriptor, MetadataProject project, XMLEntityMappings entityMappings) {
    	m_isProcessed = false;
    	m_project = project;
        m_descriptor = descriptor;
        m_entityMappings = entityMappings;
        m_accessibleObject = accessibleObject;
    }
    
    /** 
     * INTERNAL:
     * Invoke the specified named method on the object, handling the necessary 
     * exceptions.
     */
    Object invokeMethod(String methodName, Object target) {
        Method method = null;
        
        try {
            method = Helper.getDeclaredMethod(target.getClass(), methodName);            
        } catch (NoSuchMethodException e) {
            EntityManagerSetupException.methodInvocationFailed(method, target,e);
        }
        
        if (method != null) {
             try {
                 if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                     try {
                         return AccessController.doPrivileged(new PrivilegedMethodInvoker(method, target));
                     } catch (PrivilegedActionException exception) {
                         Exception throwableException = exception.getException();
                         if (throwableException instanceof IllegalAccessException) {
                             throw EntityManagerSetupException.cannotAccessMethodOnObject(method, target);
                         } else {
                             throw EntityManagerSetupException.methodInvocationFailed(method, target, throwableException);
                         }
                     }
                 } else {
                     return PrivilegedAccessHelper.invokeMethod(method, target);
                 }
             } catch (IllegalAccessException ex1) {
                 throw EntityManagerSetupException.cannotAccessMethodOnObject(method, target);
             } catch (InvocationTargetException ex2) {
                 throw EntityManagerSetupException.methodInvocationFailed(method, target, ex2);
             }
        } else {
            return null;
        }
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
     * Return true if this accessor has been processed.
     */
    public boolean isProcessed() {
        return m_isProcessed;
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
     * Every accessor knows how to process themselves since they have all the
     * information they need.
     */
    public abstract void process();
    
    /**
     * INTERNAL:
     * Process the globally defined converters.
     */
    protected void processConverters() {
        // Process the custom converters if defined.
        processCustomConverters();
        
        // Process the object type converters if defined.
        processObjectTypeConverters();
        
        // Process the type converters if defined.
        processTypeConverters();
        
        // Process the struct converters if defined
        processStructConverter();
    }
    
    /**
     * INTERNAL:
     * Process the XML defined converters and check for a Converter annotation. 
     */
    protected void processCustomConverters() {
    	// Check for XML defined converters.
    	if (m_converters != null) {
    		getEntityMappings().processConverters(m_converters);
    	}
    	
        // Check for a Converter annotation.
    	Annotation converter = getAnnotation(Converter.class);
        if (converter != null) {
            m_project.addConverter(new ConverterMetadata(converter, getAnnotatedElement()));
        }
    }
    
    /**
     * INTERNAL:
     * Process the XML defined object type converters and check for an 
     * ObjectTypeConverter annotation. 
     */
    protected void processObjectTypeConverters() {        
    	// Check for XML defined object type converters.
    	if (m_objectTypeConverters != null) {
    		getEntityMappings().processObjectTypeConverters(m_objectTypeConverters);
    	}
    	
        // Check for an ObjectTypeConverter annotation.
    	Annotation converter = getAnnotation(ObjectTypeConverter.class);
        if (converter != null) {
        	m_project.addConverter(new ObjectTypeConverterMetadata(converter, getAnnotatedElement()));
        }
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
     * Process the XML defined struct converters and check for a StructConverter 
     * annotation. 
     */
    protected void processStructConverter() {
    	// Check for XML defined struct converters.
    	if (m_structConverters != null) {
    		getEntityMappings().processStructConverters(m_structConverters);
    	}
    	
        // Check for a StructConverter annotation.
    	Annotation converter = getAnnotation(StructConverter.class);
        if (converter != null) {
            m_project.addStructConverter(new StructConverterMetadata(converter, getAnnotatedElement()));
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
     * Process a the XML defined type converters and check for a TypeConverter 
     * annotation. 
     */
    protected void processTypeConverters() {
    	// Check for XML defined type converters.
    	if (m_typeConverters != null) {
    		getEntityMappings().processTypeConverters(m_typeConverters);
    	}
    	
        // Check for an TypeConverter annotation.
    	Annotation converter = getAnnotation(TypeConverter.class);
        if (converter != null) {
        	m_project.addConverter(new TypeConverterMetadata(converter, getAnnotatedElement()));
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
     * Used for OX mapping.
     */
	public void setConverters(List<ConverterMetadata> converters) {
		m_converters = converters;
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
     */
    public void setEntityMappings(XMLEntityMappings entityMappings) {
    	m_entityMappings = entityMappings;
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
	public void setObjectTypeConverters(List<ObjectTypeConverterMetadata> objectTypeConverters) {
		m_objectTypeConverters = objectTypeConverters;
	}
	
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
    	m_primaryKeyJoinColumns = primaryKeyJoinColumns;
    }
    
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setStructConverters(List<StructConverterMetadata> structConverters) {
		m_structConverters = structConverters;
	}
	
	/**
     * INTERNAL:
     * Used for OX mapping.
     */
	public void setTypeConverters(List<TypeConverterMetadata> typeConverters) {
		m_typeConverters = typeConverters;
	}
	

}
