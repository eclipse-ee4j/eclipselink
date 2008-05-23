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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     05/23/2008-1.0M8 Guy Pelletier 
 *       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Basic;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.BasicMap;
import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.CopyPolicy;
import org.eclipse.persistence.annotations.InstantiationCopyPolicy;
import org.eclipse.persistence.annotations.CloneCopyPolicy;
import org.eclipse.persistence.annotations.Properties;
import org.eclipse.persistence.annotations.Property;
import org.eclipse.persistence.annotations.Transformation;
import org.eclipse.persistence.annotations.VariableOneToOne;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedIdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransformationAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VariableOneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
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
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

/**
 * INTERNAL:
 * A abstract class accessor. Holds common metadata for entities, embeddables
 * and mapped superclasses.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class ClassAccessor extends MetadataAccessor {
    private Boolean m_excludeDefaultMappings;
    private Boolean m_metadataComplete;
    
    private ChangeTrackingMetadata m_changeTracking;
    private Class m_customizerClass;
    
    // Various copy policies. Represented individually to facilitate XML writing.
    private CloneCopyPolicyMetadata m_cloneCopyPolicy;
    private CustomCopyPolicyMetadata m_customCopyPolicy;
    private InstantiationCopyPolicyMetadata m_instantiationCopyPolicy;
    
    private String m_access;
    private String m_className;
    private String m_customizerClassName;
    private String m_description;
    
    private XMLAttributes m_attributes;
    
    /**
     * INTERNAL:
     */
    protected ClassAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public ClassAccessor(Annotation annotation, Class cls, MetadataProject project) {
        super(annotation, new MetadataClass(cls), new MetadataDescriptor(cls), project);
        
        // Set the class accessor reference on the descriptor.
        getDescriptor().setClassAccessor(this);
    }
    
    /**
     * INTERNAL:
     * Called from MappedSuperclassAccessor. We want to avoid setting the
     * class accessor on the descriptor to be the MappedSuperclassAccessor.
     */
    protected ClassAccessor(Annotation annotation, Class cls, MetadataDescriptor descriptor, MetadataProject project) {
        super(annotation, new MetadataClass(cls), descriptor, project);
    }
    
    /**
     * INTERNAL:
     * Create and return the appropriate accessor based on the accessible 
     * object given. Order of checking is important, careful when modifying
     * or adding, check what the isXyz call does to determine if the accessor
     * is of type xyz.
     */
    protected MappingAccessor buildAccessor(MetadataAnnotatedElement accessibleObject) {
        MappingAccessor accessor = getDescriptor().getAccessorFor(accessibleObject.getAttributeName());
        
        if (accessor == null) {
            if (accessibleObject.isBasicCollection(getDescriptor())) {
                return new BasicCollectionAccessor(accessibleObject.getAnnotation(BasicCollection.class), accessibleObject, this);
            } else if (accessibleObject.isBasicMap(getDescriptor())) {
                return new BasicMapAccessor(accessibleObject.getAnnotation(BasicMap.class), accessibleObject, this);
            } else if (accessibleObject.isId(getDescriptor())) {
                return new IdAccessor(accessibleObject.getAnnotation(Id.class), accessibleObject, this);
            } else if (accessibleObject.isVersion(getDescriptor())) {
                return new VersionAccessor(accessibleObject.getAnnotation(Version.class), accessibleObject, this);
            } else if (accessibleObject.isBasic(getDescriptor())) {
                return new BasicAccessor(accessibleObject.getAnnotation(Basic.class), accessibleObject, this);
            } else if (accessibleObject.isEmbedded(getDescriptor())) {
                return new EmbeddedAccessor(accessibleObject.getAnnotation(Embedded.class), accessibleObject, this);
            } else if (accessibleObject.isEmbeddedId(getDescriptor())) {
                return new EmbeddedIdAccessor(accessibleObject.getAnnotation(EmbeddedId.class), accessibleObject, this);
            } else if (accessibleObject.isTransformation(getDescriptor())) { 
                return new TransformationAccessor(accessibleObject.getAnnotation(Transformation.class), accessibleObject, this);
            } else if (accessibleObject.isManyToMany(getDescriptor())) {
                return new ManyToManyAccessor(accessibleObject.getAnnotation(ManyToMany.class), accessibleObject, this);
            } else if (accessibleObject.isManyToOne(getDescriptor())) {
                return new ManyToOneAccessor(accessibleObject.getAnnotation(ManyToOne.class), accessibleObject, this);
            } else if (accessibleObject.isOneToMany(getDescriptor())) {
                // A OneToMany can default, that is, doesn't require an 
                // annotation to be present.
                return new OneToManyAccessor(accessibleObject.getAnnotation(OneToMany.class), accessibleObject, this);
            } else if (accessibleObject.isOneToOne(getDescriptor())) {
                // A OneToOne can default, that is, doesn't require an 
                // annotation to be present.
                return new OneToOneAccessor(accessibleObject.getAnnotation(OneToOne.class), accessibleObject, this);
            } else if (accessibleObject.isVariableOneToOne(getDescriptor())) {
                // A VariableOneToOne can default, that is, doesn't require
                // an annotation to be present.
                return new VariableOneToOneAccessor(accessibleObject.getAnnotation(VariableOneToOne.class), accessibleObject, this);
            } else if (getDescriptor().ignoreDefaultMappings()) {
                return null;
            } else {
                // Default case (everything else falls into a Basic)
                return new BasicAccessor(accessibleObject.getAnnotation(Basic.class), accessibleObject, this);
            }
        } else {
            return accessor;
        }
    }
    
    /**
     * INTERNAL:
     */
    public boolean excludeDefaultMappings() {
        return m_excludeDefaultMappings != null && m_excludeDefaultMappings;
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
    
    /**
     * INTERNAL:
     */
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
     * Used for OX mapping.
     */
    public Boolean getExcludeDefaultMappings() {
        return m_excludeDefaultMappings;
    }
    
    /**
     * INTERNAL:
     * To satisfy the abstract getIdentifier() method from ORMetadata.
     */
    @Override
    public String getIdentifier() {
        return getJavaClassName();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public InstantiationCopyPolicyMetadata getInstantiationCopyPolicy(){
        return m_instantiationCopyPolicy;
    }
    
    /**
     * INTERNAL:
     * Return the java class that defines this accessor. It may be an
     * entity, embeddable or mapped superclass.
     */
    @Override
    public Class getJavaClass() {
        return (Class) getAnnotatedElement();
    }
    
    /**
     * INTERNAL:
     * Return the java class name that defines this accessor. It may be an
     * entity, embeddable or mapped superclass.
     */
    @Override
    public String getJavaClassName() {
        return getJavaClass().getName();
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
     */
    public boolean isMetadataComplete() {
        return m_metadataComplete != null && m_metadataComplete;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
        
        // Initialize single objects.
        initXMLObject(m_changeTracking, accessibleObject);
        initXMLObject(m_cloneCopyPolicy, accessibleObject);
        initXMLObject(m_customCopyPolicy, accessibleObject);
        initXMLObject(m_instantiationCopyPolicy, accessibleObject);
        initXMLObject(m_attributes, accessibleObject);
        
        // Initialize simple class objects.
        m_customizerClass = initXMLClassName(m_customizerClassName);
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
     * Generic class level merging details for entities, mapped superclasses
     * and embeddables.
     */
    @Override
    public void merge(ORMetadata metadata) {
        super.merge(metadata);
        
        ClassAccessor accessor = (ClassAccessor) metadata;
        
        // Simple object merging.
        m_access = (String) mergeSimpleObjects(m_access, accessor.getAccess(), accessor.getAccessibleObject(), "@access");
        m_customizerClass = (Class) mergeSimpleObjects(m_customizerClass, accessor.getCustomizerClass(), accessor.getAccessibleObject(), "<customizer>");
        m_description = (String) mergeSimpleObjects(m_description, accessor.getDescription(), accessor.getAccessibleObject(), "<description>");
        m_metadataComplete = (Boolean) mergeSimpleObjects(m_metadataComplete, accessor.getMetadataComplete(), accessor.getAccessibleObject(), "@metadata-complete");
        m_excludeDefaultMappings = (Boolean) mergeSimpleObjects(m_excludeDefaultMappings, accessor.getExcludeDefaultMappings(), accessor.getAccessibleObject(), "@exclude-default-mappings");
        
        // ORMetadata object merging.        
        m_cloneCopyPolicy = (CloneCopyPolicyMetadata) mergeORObjects(m_cloneCopyPolicy, accessor.getCloneCopyPolicy());
        m_customCopyPolicy = (CustomCopyPolicyMetadata) mergeORObjects(m_customCopyPolicy, accessor.getCustomCopyPolicy());
        m_instantiationCopyPolicy = (InstantiationCopyPolicyMetadata) mergeORObjects(m_instantiationCopyPolicy, accessor.getInstantiationCopyPolicy());
        m_changeTracking = (ChangeTrackingMetadata) mergeORObjects(m_changeTracking, accessor.getChangeTracking());
        
        // ORObjects that merge further ...
        if (m_attributes == null) {
            m_attributes = accessor.getAttributes();
        } else {
            m_attributes.merge(accessor.getAttributes());
        }
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
    protected void processAccessor(MappingAccessor accessor) {
        if (accessor != null && ! accessor.isProcessed()) {
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
            MetadataField metadataField = new MetadataField(field, getLogger());
            if (metadataField.isAnnotationPresent(Transient.class)) {
                if (metadataField.hasMoreThanOneDeclaredAnnotation(getDescriptor())) {
                    throw ValidationException.mappingAnnotationsAppliedToTransientAttribute(field);
                }
            } else {
                // The is valid check will throw an exception if needed.
                if (metadataField.isValidPersistenceField(getDescriptor())) {
                    processAccessor(buildAccessor(metadataField));
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
            MetadataMethod metadataMethod = new MetadataMethod(method, getLogger());
            if (metadataMethod.isAnnotationPresent(Transient.class)) {    
                if (metadataMethod.hasMoreThanOneDeclaredAnnotation(getDescriptor())) {
                    throw ValidationException.mappingAnnotationsAppliedToTransientAttribute(method);
                }
            } else {
                // The is valid check will throw an exception if needed.
                if (metadataMethod.isValidPersistenceMethod(getDescriptor())) {
                    // We have a valid metadata method.
                    processAccessor(buildAccessor(metadataMethod));
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
            for (MappingAccessor accessor : m_attributes.getAccessors()) {
                // Load the accessible object from the class.
                MetadataAccessibleObject accessibleObject = null;
            
                if (accessor.usesPropertyAccess(getDescriptor())) {
                    if (accessor.getAccessMethods() != null) {
                        // Can't rely on MappingAccessor's getGetMethodName methods 
                        // as they could result in NPE if accessibleObject isn't 
                        // set first
                        String getMethodName = accessor.getAccessMethods().getGetMethodName();
                        Method getMethod = MetadataHelper.getMethod(getMethodName, getJavaClass(), new Class[]{});
                        String setMethodName = accessor.getAccessMethods().getSetMethodName();
                        Method setMethod = MetadataHelper.getMethod(setMethodName, getJavaClass(), new Class[]{getMethod.getReturnType()});
                        
                        accessibleObject = new MetadataMethod(getMethod, setMethod, accessor.getName(), getEntityMappings());
                    } else {
                        Method method = MetadataHelper.getMethodForPropertyName(accessor.getName(), getJavaClass());
                        
                        if (method == null) {
                            throw ValidationException.invalidPropertyForClass(accessor.getName(), getJavaClass());
                        } else {
                            MetadataMethod metadataMethod = new MetadataMethod(method, getEntityMappings());
                            
                            // True will force an exception to be thrown if it is 
                            // not a valid method.
                            if (metadataMethod.isValidPersistenceMethod(getDescriptor(), true)) {
                                accessibleObject = metadataMethod;
                            }
                        }  
                    }
                } else {
                    Field field = MetadataHelper.getFieldForName(accessor.getName(), getJavaClass());
                
                    if (field == null) {
                        throw ValidationException.invalidFieldForClass(accessor.getName(), getJavaClass());
                    } else {
                        MetadataField metadataField = new MetadataField(field, getEntityMappings());
                    
                        // True will force an exception to be thrown if it is 
                        // not a valid field.
                        if (metadataField.isValidPersistenceField(getDescriptor(), true)) {
                            accessibleObject = metadataField;
                        }
                    }
                }
                
                // Re-initialize the accessor with its real accessible object,
                // that is a field or method since it will currently hold a 
                // reference to its owning class' accesible object.
                accessor.initXMLAccessor(accessibleObject, getDescriptor(), getProject());
                
                // Now process the accessor ...
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
        Annotation changeTracking = getAnnotation(ChangeTracking.class);
        
        if (m_changeTracking != null || changeTracking != null) {
            if (getDescriptor().hasChangeTracking()) {    
                // We must be processing a mapped superclass setting for an
                // entity that has its own change tracking setting. Ignore it 
                // and log a warning.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CHANGE_TRACKING, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_changeTracking == null) {
                    new ChangeTrackingMetadata(changeTracking, getAccessibleObject()).process(getDescriptor());
                } else {
                    if (changeTracking != null) {
                        getLogger().logWarningMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, changeTracking, getJavaClassName(), getLocation());
                    }
                    
                    m_changeTracking.process(getDescriptor());
                }
            }
        }   
    }
    
    /**
     * INTERNAL:
     */
    protected void processCopyPolicy(){
        Annotation copyPolicy = getAnnotation(CopyPolicy.class);
        Annotation instantiationCopyPolicy = getAnnotation(InstantiationCopyPolicy.class);
        Annotation cloneCopyPolicy = getAnnotation(CloneCopyPolicy.class);

        if (getCopyPolicy() != null || copyPolicy != null || instantiationCopyPolicy != null || cloneCopyPolicy != null) {
            if (getDescriptor().hasCopyPolicy()){
                // We must be processing a mapped superclass ...
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_COPY_POLICY, getDescriptor().getJavaClass(), getJavaClass());
            }
            
            if (getCopyPolicy() == null) {
                // Look at the annotations.
                if (copyPolicy != null) {
                    if (instantiationCopyPolicy != null || cloneCopyPolicy != null) {
                        throw ValidationException.multipleCopyPolicyAnnotationsOnSameClass(getJavaClassName());
                    }

                    new CustomCopyPolicyMetadata(copyPolicy, getAccessibleObject()).process(getDescriptor());
                }
                
                if (instantiationCopyPolicy != null){
                    if (cloneCopyPolicy != null) {
                        throw ValidationException.multipleCopyPolicyAnnotationsOnSameClass(getJavaClassName());
                    }
                    
                    new InstantiationCopyPolicyMetadata(instantiationCopyPolicy, getAccessibleObject()).process(getDescriptor());
                }
                
                if (cloneCopyPolicy != null){
                    new CloneCopyPolicyMetadata(cloneCopyPolicy, getAccessibleObject()).process(getDescriptor());
                }
                
            } else {
                // We have a copy policy specified in XML.
                if (copyPolicy != null) {
                    getLogger().logWarningMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, copyPolicy, getJavaClassName(), getLocation());
                }
                
                if (instantiationCopyPolicy != null) {
                    getLogger().logWarningMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, instantiationCopyPolicy, getJavaClassName(), getLocation());
                }
                
                if (cloneCopyPolicy != null) {
                    getLogger().logWarningMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, cloneCopyPolicy, getJavaClassName(), getLocation());
                }
                
                getCopyPolicy().process(getDescriptor());
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void processCustomizer() {
        Annotation customizer = getAnnotation(Customizer.class);
        
        if ((m_customizerClass != null && ! m_customizerClass.equals(void.class)) || customizer != null) {
            if (getDescriptor().hasCustomizer()) {
                // We must be processing a mapped superclass and its subclass
                // override the customizer class, that is, defined its own. Log 
                // a warning that we are ignoring the Customizer metadata on the 
                // mapped superclass for the descriptor's java class.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CUSTOMIZER, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_customizerClass == null || m_customizerClass.equals(void.class)) { 
                    // Use the annotation value.
                    m_customizerClass = (Class) MetadataHelper.invokeMethod("value", customizer);
                } else {
                    // Use the xml value and log a message if necessary.
                    if (customizer != null) {
                        getLogger().logWarningMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, customizer, getJavaClassName(), getLocation());
                    }
                }
                
                getProject().addAccessorWithCustomizer(this);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Adds properties to the descriptor.
     */
    protected void processProperties() {        
        // Add the XML properties first.
        for (PropertyMetadata property : getProperties()) {
            getDescriptor().addProperty(property);
        }

        // Now add the properties defined in annotations.
        Annotation properties = getAnnotation(Properties.class);
        if (properties != null) {
            for (Annotation property : (Annotation[]) MetadataHelper.invokeMethod("value", properties)) {
                getDescriptor().addProperty(new PropertyMetadata(property, getAccessibleObject()));
            }
        }
        
        Annotation property = getAnnotation(Property.class);
        if (property != null) {
            getDescriptor().addProperty(new PropertyMetadata(property, getAccessibleObject()));
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
     * Used for OX mapping.
     */
    public void setExcludeDefaultMappings(Boolean excludeDefaultMappings) {
        m_excludeDefaultMappings = excludeDefaultMappings;
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
     * Set the java class for this accessor. This is currently called after
     * the class loader has changed and we are adding entity listeners.
     */
    public void setJavaClass(Class cls) {
        getAccessibleObject().setAnnotatedElement(cls);
        getDescriptor().setJavaClass(cls);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMetadataComplete(Boolean metadataComplete) {
        m_metadataComplete = metadataComplete;
    }
}
