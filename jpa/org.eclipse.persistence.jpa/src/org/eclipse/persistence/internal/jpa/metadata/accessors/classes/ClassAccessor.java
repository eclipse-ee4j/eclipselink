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
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.CopyPolicy;
import org.eclipse.persistence.annotations.InstantiationCopyPolicy;
import org.eclipse.persistence.annotations.CloneCopyPolicy;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedIdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransformationAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VariableOneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;

import org.eclipse.persistence.internal.jpa.metadata.changetracking.ChangeTrackingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CopyPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CustomCopyPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.InstantiationCopyPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CloneCopyPolicyMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLAttributes;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetField;

/**
 * A abstract class accessor. Holds common metadata for entities, embeddables
 * and mapped superclasses.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class ClassAccessor extends MetadataAccessor {
    private Boolean m_metadataComplete; // EntityMappings init will process this value.
    
    private ChangeTrackingMetadata m_changeTracking;
    private Class m_customizerClass;
    
    // various copy policies.  Represented individually to facilitate XML writing
    private CustomCopyPolicyMetadata m_customCopyPolicy;
    private InstantiationCopyPolicyMetadata m_instantiationCopyPolicy;
    private CloneCopyPolicyMetadata m_cloneCopyPolicy;
    
    private String m_access; // EntityMappings init will process this value.
    private String m_className;
    private String m_customizerClassName;
    private String m_description;
    private String m_mappingFile;
	
	private XMLAttributes m_attributes;
	
    /**
     * INTERNAL:
     */
    public ClassAccessor() {}
    
    /**
     * INTERNAL:
     */
    public ClassAccessor(Class cls, MetadataProject project) {
    	super(new MetadataClass(cls), new MetadataDescriptor(cls), project);
    	
    	// Set the class accessor reference on the descriptor.
    	getDescriptor().setClassAccessor(this);
    }
    
    /**
     * INTERNAL:
     * Called from MappedSuperclassAccessor. We want to avoid setting the
     * class accessor on the descriptor to be the MappedSuperclassAccessor.
     */
    protected ClassAccessor(Class cls, MetadataDescriptor descriptor, MetadataProject project) {
    	super(new MetadataClass(cls), descriptor, project);
    }
    
    /**
     * INTERNAL:
     * Create and return the appropriate accessor based on the accessible 
     * object given. Order of checking is important, careful when modifying
     * or adding, check what the isXyz call does to determine if the accessor
     * is of type xyz.
     */
    protected MetadataAccessor buildAccessor(MetadataAccessibleObject accessibleObject) {
        MetadataAccessor accessor = getDescriptor().getAccessorFor(accessibleObject.getAttributeName());
        
        if (accessor == null) {
            if (accessibleObject.isBasicCollection(getDescriptor())) {
                return new BasicCollectionAccessor(accessibleObject, this);
            } else if (accessibleObject.isBasicMap(getDescriptor())) {
                return new BasicMapAccessor(accessibleObject, this);
            } else if (accessibleObject.isId(getDescriptor())) {
            	return new IdAccessor(accessibleObject, this);
            } else if (accessibleObject.isVersion(getDescriptor())) {
            	return new VersionAccessor(accessibleObject, this);
            } else if (accessibleObject.isBasic(getDescriptor())) {
                return new BasicAccessor(accessibleObject, this);
            } else if (accessibleObject.isEmbedded(getDescriptor())) {
                return new EmbeddedAccessor(accessibleObject, this);
            } else if (accessibleObject.isEmbeddedId(getDescriptor())) {
                return new EmbeddedIdAccessor(accessibleObject, this);
            } else if (accessibleObject.isTransformation(getDescriptor())) { 
                return new TransformationAccessor(accessibleObject, this);
            } else if (accessibleObject.isManyToMany(getDescriptor())) {
                return new ManyToManyAccessor(accessibleObject, this);
            } else if (accessibleObject.isManyToOne(getDescriptor())) {
                return new ManyToOneAccessor(accessibleObject, this);
            } else if (accessibleObject.isVariableOneToOne(getDescriptor())) {
                // A VariableOneToOne can default, that is, doesn't require
                // an annotation to be present.
                return new VariableOneToOneAccessor(accessibleObject, this);
            } else if (accessibleObject.isOneToMany(getDescriptor())) {
                // A OneToMany can default, that is, doesn't require an 
                // annotation to be present.
                return new OneToManyAccessor(accessibleObject, this);
            } else if (accessibleObject.isOneToOne(getDescriptor())) {
                // A OneToOne can default, that is, doesn't require an 
                // annotation to be present.
                return new OneToOneAccessor(accessibleObject, this);
            } else {
                // Default case (everything else falls into a Basic)
                return new BasicAccessor(accessibleObject, this);
            }
        } else {
            return accessor;
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getAccess() {
    	return m_access;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public XMLAttributes getAttributes() {
    	return m_attributes;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public ChangeTrackingMetadata getChangeTracking() {
        return m_changeTracking;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getClassName() {
        return m_className;
    }
    
    public CopyPolicyMetadata getCopyPolicy(){
        if (m_cloneCopyPolicy != null){
            return m_cloneCopyPolicy;
        } else if (m_instantiationCopyPolicy != null){
            return m_instantiationCopyPolicy;
        } else {
            return m_customCopyPolicy;
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public CloneCopyPolicyMetadata getCloneCopyPolicy(){
        return m_cloneCopyPolicy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public CustomCopyPolicyMetadata getCustomCopyPolicy(){
        return m_customCopyPolicy;
    }
    
    /**
     * INTERNAL:
     */
    public Class getCustomizerClass() {
        return m_customizerClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getCustomizerClassName() {
        return m_customizerClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDescription() {
        return m_description;
    }
    
    /**
     * INTERNAL:
     * Helper method that will return a given field based on the provided attribute name.
     */
    protected Field getFieldForName(String fieldName, Class javaClass) {
        Field field = null;
        
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    field = (Field)AccessController.doPrivileged(new PrivilegedGetField(javaClass, fieldName, false));
                } catch (PrivilegedActionException exception) {
                    return null;
                }
            } else {
                field = PrivilegedAccessHelper.getField(javaClass, fieldName, false);
            }
        } catch (NoSuchFieldException nsfex) {
            return null;
        }
        
        return field;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public InstantiationCopyPolicyMetadata getInstantiationCopyPolicy(){
        return m_instantiationCopyPolicy;
    }
    
    /**
     * INTERNAL: (OVERRIDE)
     * Return the java class that defines this accessor. It may be an
     * entity, embeddable or mapped superclass.
     */
    public Class getJavaClass() {
        return (Class) getAnnotatedElement();
    }
    
    /**
     * INTERNAL:
     * Return the java class name that defines this accessor. It may be an
     * entity, embeddable or mapped superclass.
     */
    public String getJavaClassName() {
    	return getJavaClass().getName();
    }
	
    /**
     * INTERNAL:
     * Build a list of classes that are decorated with a MappedSuperclass
     * annotation or that are tagged as a mapped-superclass in an XML document.
     */
    public List<MappedSuperclassAccessor> getMappedSuperclasses() {
        // The list is currently rebuilt every time this method is called since 
        // it is potentially called both during pre-deploy and deploy where
        // where the class loader dependencies change.
        ArrayList<MappedSuperclassAccessor> mappedSuperclasses = new ArrayList<MappedSuperclassAccessor>();
        Class parent = getJavaClass().getSuperclass();
        
        while (parent != Object.class) {
            if (getDescriptor().isInheritanceSubclass() && getProject().hasEntity(parent)) {
                // In an inheritance case we don't want to keep looking
                // for mapped superclasses if they are not directly above
                // us before the next entity in the hierarchy.
                break;
            } else {
                MappedSuperclassAccessor accessor = getProject().getMappedSuperclass(parent);

                // If the mapped superclass was not defined in XML then check 
                // for a MappedSuperclass annotation.
                if (accessor == null) {
                    if (isAnnotationPresent(MappedSuperclass.class, parent)) {
                        mappedSuperclasses.add(new MappedSuperclassAccessor(parent, getDescriptor(), getProject()));
                    }
                } else {
                    mappedSuperclasses.add(accessor.getEntityMappings().reloadMappedSuperclass(accessor, getDescriptor()));
                }
            }
                
            parent = parent.getSuperclass();
        }
                
        return mappedSuperclasses;
    }
    
    /**
     * INTERNAL:
     */
    public String getMappingFile() {
    	return m_mappingFile;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getMetadataComplete() {
    	return m_metadataComplete;
    }
    
    /**
     * INTERNAL:
     * Check to see if this is a valid method to process for persistence. More
     * validation is done on the method to ensure it is a valid persistence
     * method. User specified means we either have annotation on the method
     * or the method was referenced in XML. Otherwise, we are looking for
     * defaults (and therefore should avoid throwing exceptions). Callers of
     * this method must handle a null return.
     */
    protected MetadataMethod getMetadataMethod(Method method, boolean userSpecified) {
    	MetadataMethod metadataMethod = new MetadataMethod(method);
    	
    	if (metadataMethod.isValidPersistenceMethodName()) {
    		// Check if the method has parameters.
    		if (metadataMethod.hasParameters()) {
    			if (userSpecified) {
    				throw ValidationException.mappingMetadataAppliedToMethodWithArguments(method, getJavaClass());
    			} else {
    			    // Valid name but has parameters ... ignore it.
    			}
    		} else if (metadataMethod.hasSetMethod()) {
    		   // Has a valid name and equivalent set method, it's a valid 
    		   // method for persistence so return it.
    		   return metadataMethod;
    		} else if (userSpecified) {
    		    // User decorated a valid persistence getMethod but does not
    		    // have an equivalent setMethod. Throw an exception.
    			throw ValidationException.noCorrespondingSetterMethodDefined(getJavaClass(), method);
    		} else {
    		    // Valid name, no parameters, but not equivalent set method. 
    		    // Since it is not tagged to be persistence, ignore it.
    		}
    	}
        
    	return null;
    }
    
    /**
     * INTERNAL:
     * Method to convert an xyz property name into a getXyz or isXyz method.
     */
    public Method getMethodForPropertyName(String propertyName, Class cls) {
        Method method;
        
        String leadingChar = String.valueOf(propertyName.charAt(0)).toUpperCase();
        String restOfName = propertyName.substring(1);
        
        // Look for a getPropertyName() method
        method = MetadataHelper.getMethod(MetadataMethod.GET_PROPERTY_METHOD_PREFIX.concat(leadingChar).concat(restOfName), cls, new Class[]{});
        
        if (method == null) {
            // Look for an isPropertyName() method
            method = MetadataHelper.getMethod(MetadataMethod.IS_PROPERTY_METHOD_PREFIX.concat(leadingChar).concat(restOfName), cls, new Class[]{});
        }
        
        return method;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isMetadataComplete() {
    	return m_metadataComplete != null && m_metadataComplete;
    }

    /** 
     * INTERNAL:
     * Return true if this accessor represents a class.
     */
    public boolean isClassAccessor() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Return true is this annotated element is not transient, static or 
     * abstract.
     */
    protected boolean isValidPersistenceElement(AnnotatedElement annotatedElement, int modifiers) {  
        return ! (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers));
    }
    
    /**
     * INTERNAL: Implemented by EntityAccessor, EmbeddableAccessor and 
     * MappedSuperclassAccessor
     */
    public abstract void process();
    
    /**
     * INTERNAL:
     * Process an accessor method or field. Relationship accessors will be 
     * stored for later processing.
     */
    protected void processAccessor(MetadataAccessor accessor) {
        if (! accessor.isProcessed()) {
            // Store the accessor for later retrieval.
        	getDescriptor().addAccessor(accessor);
        	
        	// The actual owning descriptor for this class accessor. In most
        	// cases this is the same as our descriptor. However in an
        	// embeddable class accessor, it will be the owning entities
        	// descriptor. This was introduced to support nesting embeddables
        	// to the nth level.
        	accessor.setOwningDescriptor(getOwningDescriptor());
        
            // Process any converters on this accessor.
            accessor.processConverters();
            
            if (accessor.isBasicCollection()) {
                // BasicCollection and BasicMaps rely on a primary key
                // having been processed before hand.
            	getDescriptor().addBasicCollectionAccessor(accessor);
            } else if (accessor.isRelationship()) {
                // Store the relationship accessors for later processing.
                // They get processed in stage 2, that is, MetadataProject
                // processing.
            	getDescriptor().addRelationshipAccessor(accessor);
            } else {
                accessor.process();
                accessor.setIsProcessed();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Create mappings from the fields directly.
     */
    protected void processAccessorFields() {
        for (Field field : MetadataHelper.getFields(getJavaClass())) {
        	if (isAnnotationPresent(Transient.class, field)) {
                if (MetadataHelper.getDeclaredAnnotationsCount(field, getDescriptor()) > 1) {
                	throw ValidationException.mappingAnnotationsAppliedToTransientAttribute(field);
                }
            } else {
            	if (isValidPersistenceElement(field, field.getModifiers())) {
            		processAccessor(buildAccessor(new MetadataField(field)));
            	} else {
            		// The field is either marked static, transient or abstract.
            		if (MetadataHelper.getDeclaredAnnotationsCount(field, getDescriptor()) > 0) {
            			// The user decorated the field with other annotations,
            			// throw an exception.
            			throw ValidationException.mappingMetadataAppliedToInvalidAttribute(field, getJavaClass());
            		}
            	}
            }
        }
    }
    
    /**
     * INTERNAL:
     * Create mappings via the class properties.
     */
    protected void processAccessorMethods() {
        for (Method method : MetadataHelper.getDeclaredMethods(getJavaClass())) {
        	if (isAnnotationPresent(Transient.class, method)) {
                if (MetadataHelper.getDeclaredAnnotationsCount(method, getDescriptor()) > 1) {
                	throw ValidationException.mappingAnnotationsAppliedToTransientAttribute(method);
                }
            } else {
            	if (isValidPersistenceElement(method, method.getModifiers())) {
            		MetadataMethod metadataMethod = getMetadataMethod(method, MetadataHelper.getDeclaredAnnotationsCount(method, getDescriptor()) > 0);
                	
                	if (metadataMethod != null) {
                		// We have a valid metadata method.
                		processAccessor(buildAccessor(metadataMethod));
                	}
            	} else {
            		// The method is either marked static, transient or abstract.
            		if (MetadataHelper.getDeclaredAnnotationsCount(method, getDescriptor()) > 0) {
            			// The user decorated the field with other annotations,
            			// throw an exception.
            			throw ValidationException.mappingMetadataAppliedToInvalidAttribute(method, getJavaClass());
            		}
            	}
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the accessors for the given class.
     */
    protected void processAccessors() {    	
    	if (m_attributes != null) {
    		for (MetadataAccessor accessor : m_attributes.getAccessors()) {
    			// Load the accessible object from the class.
    			MetadataAccessibleObject accessibleObject;
    		
    			if (getDescriptor().usesPropertyAccess()) {
    				Method method = getMethodForPropertyName(accessor.getName(), getJavaClass());
    				
    				if (method == null) {
    					throw ValidationException.invalidPropertyForClass(accessor.getName(), getJavaClass());
    				}
    				
    				if (! isValidPersistenceElement(method, method.getModifiers())) {
    					throw ValidationException.mappingMetadataAppliedToInvalidAttribute(method, getJavaClass());
    				}
    				
    				accessibleObject = getMetadataMethod(method, true);	
    			} else {
    				Field field = getFieldForName(accessor.getName(), getJavaClass());
                
    				if (field == null) {
    					throw ValidationException.invalidFieldForClass(accessor.getName(), getJavaClass());
    				}
    				
    				if (! isValidPersistenceElement(field, field.getModifiers())) {
    					throw ValidationException.mappingMetadataAppliedToInvalidAttribute(field, getJavaClass());
    				}
                
    				accessibleObject = new MetadataField(field);
    			}
    			
    			accessor.init(accessibleObject, this);
    			processAccessor(accessor);
    		}
    	}
    	
    	// Process the fields or methods on the class for annotations.
        if (getDescriptor().usesPropertyAccess()) {
            processAccessorMethods();
        } else {
            processAccessorFields();
        }
    }
    
    /**
     * INTERNAL:
     * Process any BasicCollection annotation and/or BasicMap annotation that 
     * were found. They are not processed till after an id has been processed 
     * since they rely on one to map the collection table.
     */
    protected void processBasicCollectionAccessors() {
        for (BasicCollectionAccessor accessor : getDescriptor().getBasicCollectionAccessors()) {
            accessor.process();
            accessor.setIsProcessed();
        }
    }
    
    /**
     * INTERNAL:
     * Process the change tracking setting for this accessor.
     */
    protected void processChangeTracking() {
        if (m_changeTracking != null || isAnnotationPresent(ChangeTracking.class)) {
            if (getDescriptor().hasChangeTracking()) {    
                // We must be processing a mapped superclass setting for an
                // entity that has its own change tracking setting. Ignore it 
                // and log a warning.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CHANGE_TRACKING, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_changeTracking == null) {
                    new ChangeTrackingMetadata(getAnnotation(ChangeTracking.class)).process(getDescriptor());
                } else {
                    if (isAnnotationPresent(ChangeTracking.class)) {
                        getLogger().logWarningMessage(MetadataLogger.IGNORE_CHANGE_TRACKING_ANNOTATION, getDescriptor().getJavaClass(), getJavaClass());
                    }
                    
                    m_changeTracking.process(getDescriptor());
                }
            }
        }   
    }
    
    
    protected void processCopyPolicy(){
        if (getCopyPolicy() != null || isAnnotationPresent(CopyPolicy.class) || isAnnotationPresent(InstantiationCopyPolicy.class) || isAnnotationPresent(CloneCopyPolicy.class)) {
            if (getDescriptor().hasCopyPolicy()){
                getLogger().logWarningMessage(MetadataLogger.IGNORE_EXISTING_COPY_POLICY, getDescriptor().getJavaClass(), getJavaClass());
            }
            if (getCopyPolicy() == null) {
                boolean foundCopyPolicy = false;
                Annotation copyPolicy = getAnnotation(CopyPolicy.class);
                if (copyPolicy != null){
                    foundCopyPolicy = true;
                    new CustomCopyPolicyMetadata(copyPolicy).process(getDescriptor(), getJavaClass());
                }
                copyPolicy = getAnnotation(InstantiationCopyPolicy.class);
                if (copyPolicy != null){
                    if (foundCopyPolicy){
                        throw ValidationException.multipleCopyPolicyAnnotationsOnSameClass(getJavaClass().getName());
                    }
                    foundCopyPolicy = true;
                    new InstantiationCopyPolicyMetadata(copyPolicy).process(getDescriptor(), getJavaClass());
                }
                copyPolicy = getAnnotation(CloneCopyPolicy.class);
                if (copyPolicy != null){
                    if (foundCopyPolicy){
                        throw ValidationException.multipleCopyPolicyAnnotationsOnSameClass(getJavaClass().getName());
                    }
                    new CloneCopyPolicyMetadata(copyPolicy).process(getDescriptor(), getJavaClass());
                }
            } else {
                if (isAnnotationPresent(CopyPolicy.class) || isAnnotationPresent(InstantiationCopyPolicy.class) || isAnnotationPresent(CloneCopyPolicy.class)){
                    getLogger().logWarningMessage(MetadataLogger.IGNORE_COPY_POLICY_ANNOTATION, getJavaClass());
                }
                getCopyPolicy().process(getDescriptor(), getJavaClass());
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void processCustomizer() {
        if (m_customizerClassName != null || isAnnotationPresent(Customizer.class)) {
            if (getDescriptor().hasCustomizer()) {
                // We must be processing a mapped superclass and its subclass
                // override the customizer class, that is, defined its own. Log 
                // a warning that we are ignoring the Customizer metadata on the 
                // mapped superclass for the descriptor's java class.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CUSTOMIZER, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_customizerClassName == null) { 
                    // Look for an annotation
                    Annotation customizer = getAnnotation(Customizer.class);
                    
                    if (customizer != null) {
                        m_customizerClass = (Class) MetadataHelper.invokeMethod("value", customizer);
                        getProject().addAccessorWithCustomizer(this);
                    }
                } else {
                    if (isAnnotationPresent(Customizer.class)) {
                        getLogger().logWarningMessage(MetadataLogger.IGNORE_CUSTOMIZER_ANNOTATION, getDescriptor().getJavaClass(), getJavaClass());
                    }
                    
                    m_customizerClass = getEntityMappings().getClassForName(m_customizerClassName);
                    getProject().addAccessorWithCustomizer(this);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAccess(String access) {
    	m_access = access;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributes(XMLAttributes attributes) {
    	m_attributes = attributes;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setChangeTracking(ChangeTrackingMetadata changeTracking) {
        m_changeTracking = changeTracking;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassName(String className) {
        m_className = className;
    }
    
    /**
     * INTERNAL:
     * set the copy policy metadata
     */
    public void setCloneCopyPolicy(CloneCopyPolicyMetadata copyPolicy){
        m_cloneCopyPolicy = copyPolicy;
    }
    
    /**
     * INTERNAL:
     * set the copy policy metadata
     */
    public void setCustomCopyPolicy(CustomCopyPolicyMetadata copyPolicy){
        m_customCopyPolicy = copyPolicy;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCustomizerClassName(String customizerClassName) {
        m_customizerClassName = customizerClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDescription(String description) {
        m_description = description;
    }
    
    /**
     * INTERNAL:
     * set the copy policy metadata
     */
    public void setInstantiationCopyPolicy(InstantiationCopyPolicyMetadata copyPolicy){
        m_instantiationCopyPolicy = copyPolicy;
    }
    
    /**
     * INTERNAL:
     * Return the annotated element for this accessor.
     */
    public void setJavaClass(Class cls) {
        setAnnotatedElement(cls);
        getDescriptor().setJavaClass(cls);
    }
    
    /**
     * INTERNAL:
     */
    public void setMappingFile(String mappingFile) {
    	m_mappingFile = mappingFile;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMetadataComplete(Boolean metadataComplete) {
    	m_metadataComplete = metadataComplete;
    }
}
