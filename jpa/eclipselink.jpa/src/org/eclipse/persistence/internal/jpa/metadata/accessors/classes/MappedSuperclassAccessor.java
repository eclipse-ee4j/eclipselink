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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.EntityListeners;
import javax.persistence.ExcludeDefaultListeners;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.IdClass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.annotations.ExistenceType;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.ReadOnly;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheMetadata;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

import org.eclipse.persistence.internal.jpa.metadata.locking.OptimisticLockingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;

import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;

/**
 * A mapped superclass accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MappedSuperclassAccessor extends ClassAccessor {
    private boolean m_excludeDefaultListeners;
    private boolean m_excludeSuperclassListeners;
    
    private Boolean m_readOnly;
    private CacheMetadata m_cache;
    private ExistenceType m_existenceChecking;
    private List<EntityListenerMetadata> m_entityListeners;
    private OptimisticLockingMetadata m_optimisticLocking;
    
    private String m_idClassName;
    private String m_prePersist;
    private String m_postPersist;
    private String m_preRemove;
    private String m_postRemove;
    private String m_preUpdate;
    private String m_postUpdate;
    private String m_postLoad;
    
    /**
     * INTERNAL:
     */
    public MappedSuperclassAccessor() {}
    
    /**
     * INTERNAL:
     */
    protected MappedSuperclassAccessor(Class cls, MetadataProject project) {
        super(cls, project);
    }
    
    /**
     * INTERNAL:
     */
    public MappedSuperclassAccessor(Class cls, MetadataDescriptor descriptor, MetadataProject project) {
        super(cls, descriptor, project);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean excludeDefaultListeners() {
        return m_excludeDefaultListeners;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean excludeSuperclassListeners() {
        return m_excludeSuperclassListeners;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public CacheMetadata getCache() {
        return m_cache;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<EntityListenerMetadata> getEntityListeners() {
        return m_entityListeners;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getExcludeDefaultListeners() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getExcludeSuperclassListeners() {
        return null;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ExistenceType getExistenceChecking() {
        return m_existenceChecking;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getIdClassName() {
        return m_idClassName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public OptimisticLockingMetadata getOptimisticLocking() {
        return m_optimisticLocking;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPostLoad() {
        return m_postLoad;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPostPersist() {
        return m_postPersist;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPostRemove() {
        return m_postRemove;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPostUpdate() {
        return m_postUpdate;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPrePersist() {
        return m_prePersist;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPreRemove() {
        return m_preRemove;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPreUpdate() {
        return m_preUpdate;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getReadOnly() {
        return m_readOnly;
    }
    
    /**
     * INTERNAL: (Overridden in EntityAccessor)
     * Process the items of interest on a mapped superclass.
     */
    public void process() {
        // Process the common class level attributes that an entity or
        // mapped superclass may define.
        processClassMetadata();
            
        // Process the accessors from the mapped superclass.
        processAccessors();
    }
    
    /**
     * INTERNAL:
     * Process the association override metadata specified on an entity or 
     * mapped superclass. For any given class, XML association overrides are
     * always added first (see processAssociationOverrides()).
     */
    protected void processAssociationOverride(AssociationOverrideMetadata associationOverride) {
        // If an association override already exists, need to make some checks
        // to determine if we should throw an exception or log an ignore
        // message.
        if (getDescriptor().hasAssociationOverrideFor(associationOverride.getName())) {
            AssociationOverrideMetadata otherAssociationOverride = getDescriptor().getAssociationOverrideFor(associationOverride.getName());
            
            if (otherAssociationOverride.getJavaClassName().equals(associationOverride.getJavaClassName())) {
                // Both were loaded from the same class, check if an XML -
                // Annotation override applies.
                if (otherAssociationOverride.loadedFromXML()) {
                    if (associationOverride.loadedFromXML()) {
                        throw ValidationException.multipleAssociationOverrideWithSameNameFound(associationOverride.getName(), associationOverride.getJavaClassName(), associationOverride.getLocation());
                    } else {
                        // XML -> Annotation override, log a warning.
                        getLogger().logWarningMessage(MetadataLogger.IGNORE_ASSOCIATION_OVERRIDE, associationOverride.getName(), associationOverride.getJavaClassName(), associationOverride.getLocation());
                    }
                } else {
                    // If the otherAssociationOverride is not loaded from XML
                    // then associationOverride can not be loaded from XML 
                    // either since it would have been processed first.
                    // Therefore, we have multiple AssociationOverride 
                    // annotations with the same name.
                    throw ValidationException.multipleAssociationOverrideWithSameNameFound(associationOverride.getName(), associationOverride.getJavaClassName(), associationOverride.getLocation());
                }
            } else {
                // We already have an association override specified and the 
                // java class names are different. We must be processing
                // a mapped superclass therefore, ignore and log a message.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_ASSOCIATION_OVERRIDE_ON_MAPPED_SUPERCLASS, associationOverride.getName(), associationOverride.getJavaClassName(), getDescriptor().getJavaClassName());
            }
        } else {
            // Add the association override.
            getDescriptor().addAssociationOverride(associationOverride);
        }
    }
    
    /**
     * INTERNAL: (Overriden in EntityAccessor)
     * Process the association override metadata specified on an entity or 
     * mapped superclass. Once the association overrides are processed from
     * XML process the association overrides from annotations. This order of
     * processing must be maintained.
     */
    protected void processAssociationOverrides() {        
        // Process the association override annotations.
        // Look for an @AssociationOverrides.
        Annotation associationOverrides = getAnnotation(AssociationOverrides.class);
        if (associationOverrides != null) {
            for (Object associationOverride : (Annotation[]) MetadataHelper.invokeMethod("value", associationOverrides)) {
                processAssociationOverride(new AssociationOverrideMetadata((Annotation) associationOverride, getJavaClassName()));
            }
        }
        
        // Look for an @AssociationOverride.
        Annotation associationOverride = getAnnotation(AssociationOverride.class);
        if (associationOverride != null) {
            processAssociationOverride(new AssociationOverrideMetadata((Annotation) associationOverride, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL:
     * Process the attribute override metadata specified on an entity or 
     * mapped superclass. For any given class, XML attribute overrides are
     * always added first (see processAttributeOverrides()).
     */
    protected void processAttributeOverride(AttributeOverrideMetadata attributeOverride) {
        // If an attribute override already exists, need to make some checks
        // to determine if we should throw an exception or log an ignore
        // message.
        if (getDescriptor().hasAttributeOverrideFor(attributeOverride.getName())) {
            AttributeOverrideMetadata otherAttributeOverride = getDescriptor().getAttributeOverrideFor(attributeOverride.getName());

            if (otherAttributeOverride.getJavaClassName().equals(attributeOverride.getJavaClassName())) {
                // Both were loaded from the same class, check if an XML -
                // Annotation override applies.
                if (otherAttributeOverride.loadedFromXML()) {
                    if (attributeOverride.loadedFromXML()) {
                        throw ValidationException.multipleAttributeOverrideWithSameNameFound(attributeOverride.getName(), attributeOverride.getJavaClassName(), attributeOverride.getLocation());
                    } else {
                        // XML -> Annotation override, log a warning.
                        getLogger().logWarningMessage(MetadataLogger.IGNORE_ATTRIBUTE_OVERRIDE, attributeOverride.getName(), attributeOverride.getJavaClassName(), attributeOverride.getLocation());
                    }
                } else {
                    // If the otherAttributeOverride is not loaded from XML
                    // then attributeOverride can not be loaded from XML 
                    // either since it would have been processed first.
                    // Therefore, we have multiple AttributeOverride 
                    // annotations with the same name.
                    throw ValidationException.multipleAttributeOverrideWithSameNameFound(attributeOverride.getName(), attributeOverride.getJavaClassName(), attributeOverride.getLocation());
                }
            } else {
                // We already have an attribute override specified and the 
                // java class names are different. We must be processing
                // a mapped superclass therefore, ignore and log a message.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_ATTRIBUTE_OVERRIDE_ON_MAPPED_SUPERCLASS, attributeOverride.getName(), attributeOverride.getJavaClassName(), getDescriptor().getJavaClassName());
            }
        } else {
            // Add the attribute override.
            getDescriptor().addAttributeOverride(attributeOverride);
        }
    }
    
    /**
     * INTERNAL: (Overriden in EntityAccessor)
     * Process the attribute override metadata specified on an entity or 
     * mapped superclass. Once the attribute overrides are processed from
     * XML process the attribute overrides from annotations. This order of 
     * processing must be maintained.
     */
    protected void processAttributeOverrides() {        
        // Process the attribute override annotations.
        // Look for an @AttributeOverrides.
        Annotation attributeOverrides = getAnnotation(AttributeOverrides.class);    
        if (attributeOverrides != null) {
            for (Annotation attributeOverride : (Annotation[]) MetadataHelper.invokeMethod("value", attributeOverrides)){ 
                processAttributeOverride(new AttributeOverrideMetadata(attributeOverride, getJavaClassName()));
            }
        }
        
        // Look for an @AttributeOverride.
        Annotation attributeOverride = getAnnotation(AttributeOverride.class);
        if (attributeOverride != null) {
            processAttributeOverride(new AttributeOverrideMetadata(attributeOverride, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL:
     * Process a cache metadata. 
     */
    protected void processCache() {
        if (m_cache != null || isAnnotationPresent(Cache.class)) {
            if (getDescriptor().isInheritanceSubclass()) {
                // Ignore cache if specified on an inheritance subclass.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_INHERITANCE_SUBCLASS_CACHE, getJavaClass());
            } else if (getDescriptor().hasCache()) {
                // Ignore cache on mapped superclass if cache is already 
                // defined on the entity.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CACHE, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_cache == null) {
                    new CacheMetadata(getAnnotation(Cache.class)).process(getDescriptor(), getJavaClass());
                } else {
                    m_cache.process(getDescriptor(), getJavaClass());
                }
            }
        }        
    }
    
    /**
     * INTERNAL:
     * Find the method in the list where method.getName() == methodName.
     */
    protected Method processCallbackMethodName(EntityListenerMetadata listener, String methodName, Method[] methods) {
        Method method = MetadataHelper.getMethodForName(methods, methodName);
        
        if (method == null) {
            throw ValidationException.invalidCallbackMethod(listener.getListenerClass(), methodName);
        }
        
        return method;
    }
    
    /**
     * INTERNAL:
     * Process the XML defined call back methods.
     */
    protected void processCallbackMethodNames(Method[] methods, EntityListenerMetadata listener) {
        // Pre-persist
        if (listener.getPrePersist() != null) {
            setPrePersist(processCallbackMethodName(listener, listener.getPrePersist(), methods), listener);
        }
        
        // Post-persist
        if (listener.getPostPersist() != null) {
            setPostPersist(processCallbackMethodName(listener, listener.getPostPersist(), methods), listener);
        }
        
        // Pre-remove
        if (listener.getPreRemove() != null) {
            setPreRemove(processCallbackMethodName(listener, listener.getPreRemove(), methods), listener);
        }
        
        // Post-remove
        if (listener.getPostRemove() != null) {
            setPostRemove(processCallbackMethodName(listener, listener.getPostRemove(), methods), listener);
        }
        
        // Pre-update
        if (listener.getPreUpdate() != null) {
            setPreUpdate(processCallbackMethodName(listener, listener.getPreUpdate(), methods), listener);
        }
        
        // Post-update
        if (listener.getPostUpdate() != null) {
            setPostUpdate(processCallbackMethodName(listener, listener.getPostUpdate(), methods), listener);
        }
        
        // Post-load
        if (listener.getPostLoad() != null) {
            setPostLoad(processCallbackMethodName(listener, listener.getPostLoad(), methods), listener);
        }   
    }
    
    /**
     * INTERNAL:
     * Process the array of methods for lifecyle callback events and set them
     * on the given event listener.
     */
    protected void processCallbackMethods(Method[] candidateMethods, EntityListenerMetadata listener) {
        for (Method method : candidateMethods) {
            if (isAnnotationPresent(PostLoad.class, method)) {
                setPostLoad(method, listener);
            }
            
            if (isAnnotationPresent(PostPersist.class, method)) {
                setPostPersist(method, listener);
            }
            
            if (isAnnotationPresent(PostRemove.class, method)) {
                setPostRemove(method, listener);
            }
            
            if (isAnnotationPresent(PostUpdate.class, method)) {
                setPostUpdate(method, listener);
            }
            
            if (isAnnotationPresent(PrePersist.class, method)) {
                setPrePersist(method, listener);
            }
            
            if (isAnnotationPresent(PreRemove.class, method)) {
                setPreRemove(method, listener);
            }
            
            if (isAnnotationPresent(PreUpdate.class, method)) {
                setPreUpdate(method, listener);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on an entity or mapped superclass class. 
     */
    protected void processClassMetadata() {
        // Process the @AttributeOverrides and @AttributeOverride.
        processAttributeOverrides();
                    
        // Process the @AssociationOverrides and @AssociationOverride.
        processAssociationOverrides();
        
        // Process the @NamedQueries and @NamedQuery.
        processNamedQueries();
                    
        // Process the @NamedNativeQueries and @NamedNativeQuery.
        processNamedNativeQueries();
        
        // Process the @NamedStoredProcedureQuery's.
        processNamedStoredProcedureQueries();
                    
        // Process the @SqlRessultSetMapping.
        processSqlResultSetMappings();
                    
        // Process the @TableGenerator.
        processTableGenerator();
            
        // Process the @SequenceGenerator
        processSequenceGenerator();
                    
        // Process the @IdClass (pkClass).
        processIdClass();
        
        // Process the @ExcludeDefaultListeners.
        processExcludeDefaultListeners();
        
        // Process the @ExcludeSuperclassListeners.
        processExcludeSuperclassListeners();
        
        // Process the TopLink converters if specified.
        processConverters();
        
        // Process the optimistic locking policy.
        processOptimisticLocking();
        
        // Process the @Cache
        processCache();
            
        // Process the @ChangeTracking
        processChangeTracking();
        
        // Process the @ReadOnly
        processReadOnly();
        
        // Process @Customizer
        processCustomizer();
        
        // Process @CopyPolicy
        processCopyPolicy();
        
        // Process @ExistenceChecking
        processExistenceChecking();
    }
    
    /**
     * INTERNAL:
     * Process the entity listeners for this class accessor. Entity listeners
     * defined in XML will override those specified on the class.
     */
    public void processEntityListeners(Class entityClass) {
        if (m_entityListeners == null || m_entityListeners.isEmpty()) {
            // Look for an annotation.
            Annotation entityListeners = getAnnotation(EntityListeners.class);
            
            if (entityListeners != null) {
                for (Class entityListener : (Class[]) MetadataHelper.invokeMethod("value", entityListeners)) {
                    EntityListenerMetadata listener = new EntityListenerMetadata(entityListener, entityClass);
                       
                    // Process the candidate callback methods for this listener.
                    processCallbackMethods(MetadataHelper.getCandidateCallbackMethodsForEntityListener(listener), listener);
                       
                    // Add the entity listener to the descriptor event manager.    
                    getDescriptor().addEntityListenerEventListener(listener);
                }
            }
        } else {
            // Process the listeners defined in XML.
            for (EntityListenerMetadata listener : m_entityListeners) {
                // Perform any initialization we need to do before hand.
                listener.setEntityClass(entityClass);
                listener.initializeListenerClass(getEntityMappings().getClassForName(listener.getClassName()));
                
                // Build a list of candidate callback methods.
                Method[] candidateMethods = MetadataHelper.getCandidateCallbackMethodsForEntityListener(listener);
                
                // 1 - Process the callback methods as defined in XML.
                processCallbackMethodNames(candidateMethods, listener);
                
                // 2 - Process the candidate callback methods on this listener for
                // additional callback methods decorated with annotations.
                processCallbackMethods(candidateMethods, listener);
        
                // Add the listener to the descriptor.
                getDescriptor().addEntityListenerEventListener(listener);               
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the exclude-default-listeners value.
     */
    protected void processExcludeDefaultListeners() {
        if (excludeDefaultListeners()) {
            getDescriptor().setExcludeDefaultListeners(true);
        } else {
            // Don't overrite a true flag that could be set from a subclass
            // that already excluded them.
            if (isAnnotationPresent(ExcludeDefaultListeners.class)) {
                getDescriptor().setExcludeDefaultListeners(true);
            } 
        }
    }
    
    /**
     * INTERNAL:
     * Process the ExcludeSuperclassListeners value if one is specified (taking
     * metadata-complete into consideration).
     */
    protected void processExcludeSuperclassListeners() {
        if (excludeSuperclassListeners()) {
            getDescriptor().setExcludeSuperclassListeners(true);
        } else {
            // Don't overrite a true flag that could be set from a subclass
            // that already exlcuded them.
            if (isAnnotationPresent(ExcludeSuperclassListeners.class)) {
                getDescriptor().setExcludeSuperclassListeners(true);
            } 
        }
    }
    
    /**
     * INTERNAL:
     * Process the ExistenceChecking value if one is specified (taking
     * metadata-complete into consideration).
     */
    protected void processExistenceChecking() {
        if (m_existenceChecking != null || isAnnotationPresent(ExistenceChecking.class)) {
            if (getDescriptor().hasExistenceChecking()) {
                // Ignore existence-checking on mapped superclass if existence
                // checking is already defined on the entity.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_EXISTENCE_CHECKING, getJavaClass());
            } else {
                if (m_existenceChecking == null) {
                    getDescriptor().setExistenceChecking((ExistenceType) MetadataHelper.invokeMethod("value", getAnnotation(ExistenceChecking.class)));
                } else {
                    if (isAnnotationPresent(ExistenceChecking.class)) {
                        getLogger().logWarningMessage(MetadataLogger.IGNORE_EXISTENCE_CHECKING_ANNOTATION, getJavaClass());
                    }
                    
                    getDescriptor().setExistenceChecking(m_existenceChecking);
                }
            }
        }
    }
    
    /**
     * INTERNAL: 
     * Process an IdClass metadata. It is used to specify composite primary 
     * keys. The primary keys will be processed and stored from the PK class so 
     * that they may be validated against the fields or properties of the entity 
     * bean. The access type of a primary key class is determined by the access 
     * type of the entity for which it is the primary key.
     */
    protected void processIdClass() {
        Class pkClass;
        
        if (m_idClassName == null) {
            // Check for an IdClass annotation.
            Annotation idClass = getAnnotation(IdClass.class);
            
            if (idClass == null) {
                return;
            } else {
                pkClass = (Class) MetadataHelper.invokeMethod("value", idClass); 
            }
        } else {
            if (isAnnotationPresent(IdClass.class)) {
                getLogger().logWarningMessage(MetadataLogger.IGNORE_ID_CLASS_ANNOTATION, getDescriptor());
            }
            
            pkClass = getEntityMappings().getClassForName(m_idClassName);
        }
            
        getDescriptor().setPKClass(pkClass);
            
        if (getDescriptor().usesPropertyAccess()) {
            for (Method method : MetadataHelper.getDeclaredMethods(pkClass)) {
                if (isValidPersistenceElement(method, method.getModifiers())) {
                    MetadataMethod metadataMethod = new MetadataMethod(method);
                    
                    
                    if (metadataMethod.isValidPersistenceMethodName()) {
                        getDescriptor().addPKClassId(metadataMethod.getAttributeName(), metadataMethod.getRelationType());
                    }
                }
            }    
        } else {
            for (Field field : MetadataHelper.getFields(pkClass)) {
                if (isValidPersistenceElement(field, field.getModifiers())) {
                    getDescriptor().addPKClassId(field.getName(), field.getGenericType());
                }
            }
        } 
    }
    
    /**
     * INTERNAL:
     * Process the mapped superclass class for lifecycle callback event methods.
     */
    public void processEntityEventListener(EntityListenerMetadata listener, Class entityClass, ClassLoader classLoader) {
        // Process the lifecycle callback events from XML.
        Method[] candidateMethods = MetadataHelper.getCandidateCallbackMethodsForMappedSuperclass(getJavaClass(), entityClass);
        
        // Process the XML defined callback methods.
        processCallbackMethodNames(candidateMethods, listener);
        
        // Check the mapped superclass for lifecycle callback annotations.
        processCallbackMethods(candidateMethods, listener);
    }
    
    /**
     * INTERNAL: (Overridden in EntityAccessor)
     * Process/collect the named native queries on this accessor and add them
     * to the project for later processing.
     */
    protected void processNamedNativeQueries() {
        // Process the named native query annotations.
        // Look for a @NamedNativeQueries.
        Annotation namedNativeQueries = getAnnotation(NamedNativeQueries.class);
        if (namedNativeQueries != null) {
            for (Annotation namedNativeQuery : (Annotation[]) MetadataHelper.invokeMethod("value", namedNativeQueries)) { 
                getProject().addNamedNativeQuery(new NamedNativeQueryMetadata(namedNativeQuery, getJavaClassName()));
            }
        }
        
        // Look for a @NamedNativeQuery.
        Annotation namedNativeQuery = getAnnotation(NamedNativeQuery.class);
        if (namedNativeQuery != null) {
            getProject().addNamedNativeQuery(new NamedNativeQueryMetadata(namedNativeQuery, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL: (Overridden in EntityAccessor)
     * Process/collect the named queries on this accessor and add them to the 
     * project for later processing.
     */
    protected void processNamedQueries() {        
        // Process the named query annotations.
        // Look for a @NamedQueries.
        Annotation namedQueries = getAnnotation(NamedQueries.class);
        if (namedQueries != null) {
            for (Annotation namedQuery : (Annotation[]) MetadataHelper.invokeMethod("value", namedQueries)) { 
                getProject().addNamedQuery(new NamedQueryMetadata(namedQuery, getJavaClassName()));
            }
        }
        
        // Look for a @NamedQuery.
        Annotation namedQuery = getAnnotation(NamedQuery.class);
        if (namedQuery != null) {
            getProject().addNamedQuery(new NamedQueryMetadata(namedQuery, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL: (Overridden in EntityAccessor)
     * Process/collect the named stored procedure queries on this accessor and 
     * add them to the project for later processing.
     */
    protected void processNamedStoredProcedureQueries() {        
        // Process the named stored procedure query annotations.
        // Look for a @NamedStoredProcedureQueries.
        Annotation namedStoredProcedureQueries = getAnnotation(NamedStoredProcedureQueries.class);
        if (namedStoredProcedureQueries != null) {
            for (Annotation namedStoredProcedureQuery : (Annotation[]) MetadataHelper.invokeMethod("value", namedStoredProcedureQueries)) { 
                getProject().addNamedStoredProcedureQuery(new NamedStoredProcedureQueryMetadata(namedStoredProcedureQuery, getJavaClassName()));
            }
        }
        
        // Look for a @NamedStoredProcedureQuery.
        Annotation namedStoredProcedureQuery = getAnnotation(NamedStoredProcedureQuery.class);
        if (namedStoredProcedureQuery != null) {
            getProject().addNamedStoredProcedureQuery(new NamedStoredProcedureQueryMetadata(namedStoredProcedureQuery, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL:
     * Process an OptimisticLockingMetadata.
     */
    protected void processOptimisticLocking() {
        if (getDescriptor().usesOptimisticLocking()) {
            if (m_optimisticLocking != null || isAnnotationPresent(OptimisticLocking.class)) {
                // We must be processing a mapped superclass to an entity that
                // defined its own optimistic locking meta data. Ignore it and
                // log a warning.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_OPTIMISTIC_LOCKING, getDescriptor().getJavaClass(), getJavaClass());
            }
        } else {
            if (m_optimisticLocking == null) {
                Annotation optimisticLocking = getAnnotation(OptimisticLocking.class);
            
                if (optimisticLocking != null) {
                    // Process the meta data for this accessor's descriptor.
                    new OptimisticLockingMetadata(optimisticLocking).process(getDescriptor());
                }
            } else {
                // If there is an annotation log a warning that we are 
                // ignoring it.
                if (isAnnotationPresent(OptimisticLocking.class)) {
                    getLogger().logWarningMessage(MetadataLogger.IGNORE_OPTIMISTIC_LOCKING_ANNOTATION, getJavaClass(), getMappingFile());
                }
            
                // Process the meta data for this accessor's descriptor.
                m_optimisticLocking.process(getDescriptor());
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process a read only setting.
     */
    protected void processReadOnly() {
        if (m_readOnly != null || isAnnotationPresent(ReadOnly.class)) {
            if (getDescriptor().isInheritanceSubclass()) {
                // Ignore read only if specified on an inheritance subclass.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_INHERITANCE_SUBCLASS_READ_ONLY, getJavaClass());
            } else if (getDescriptor().hasReadOnly()) {
                // Ignore read only on mapped superclass if read only is already 
                // defined on the entity.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_READ_ONLY, getJavaClass());
            } else {
                if (m_readOnly == null) {
                    getDescriptor().setReadOnly(true);
                } else {
                    if (isAnnotationPresent(ReadOnly.class)) {
                        getLogger().logWarningMessage(MetadataLogger.IGNORE_READ_ONLY_ANNOTATION, getJavaClass());
                    }
                    
                    getDescriptor().setReadOnly(m_readOnly);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process a SequenceGenerator annotation into a common metadata sequence 
     * generator and add it to the project.
     */
    protected void processSequenceGenerator() {       
        if (isAnnotationPresent(SequenceGenerator.class)) {
            // Ask the common processor to process what we found.
            getProject().addSequenceGenerator(new SequenceGeneratorMetadata(getAnnotation(SequenceGenerator.class), getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL: (Overridden in EntityAccessor)
     * Process the sql result set mappings for the given class which could be 
     * an entity or a mapped superclass.
     */
    protected void processSqlResultSetMappings() {
        // Process the sql result set mapping query annotations.
        // Look for a @SqlResultSetMappings.
        Annotation sqlResultSetMappings = getAnnotation(SqlResultSetMappings.class);

        if (sqlResultSetMappings != null) {
            for (Annotation sqlResultSetMapping : (Annotation[]) MetadataHelper.invokeMethod("value", sqlResultSetMappings)) {
                new SQLResultSetMappingMetadata(sqlResultSetMapping).process(getProject());
            }
        } else {
            // Look for a @SqlResultSetMapping.
            Annotation sqlResultSetMapping = getAnnotation(SqlResultSetMapping.class);
            
            if (sqlResultSetMapping != null) {
                new SQLResultSetMappingMetadata(sqlResultSetMapping).process(getProject());
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process a TableGenerator annotation into a common metadata table 
     * generator and add it to the project.
     */
    protected void processTableGenerator() {
        if (isAnnotationPresent(TableGenerator.class)) {
            getProject().addTableGenerator(new TableGeneratorMetadata(getAnnotation(TableGenerator.class), getJavaClassName()), getDescriptor().getXMLCatalog(), getDescriptor().getXMLSchema());
        }
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCache(CacheMetadata cache) {
        m_cache = cache;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setEntityListeners(List<EntityListenerMetadata> entityListeners) {
        m_entityListeners = entityListeners;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setExcludeDefaultListeners(String ignore) {
        m_excludeDefaultListeners = true;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public void setExcludeSuperclassListeners(String ignore) {
        m_excludeSuperclassListeners = true;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setExistenceChecking(ExistenceType checking) {
        m_existenceChecking = checking;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setIdClassName(String idClassName) {
        m_idClassName = idClassName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOptimisticLocking(OptimisticLockingMetadata optimisticLocking) {
        m_optimisticLocking = optimisticLocking;
    }
    
    /**
     * INTERNAL:
     * Set the post load event method on the listener.
     */
    protected void setPostLoad(Method method, EntityListenerMetadata listener) {
        listener.setPostBuildMethod(method);
        listener.setPostCloneMethod(method);
        listener.setPostRefreshMethod(method);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPostLoad(String postLoad) {
        m_postLoad = postLoad;
    }
    
    /**
     * INTERNAL:
     * Set the post persist event method on the listener.
     */
    protected void setPostPersist(Method method, EntityListenerMetadata listener) {
        listener.setPostInsertMethod(method); 
    }
    
    /**
     * INTERNAL:
     */
    public void setPostPersist(String postPersist) {
        m_postPersist = postPersist;
    }
    
    /**
     * INTERNAL:
     * Set the post remove event method on the listener.
     */
    protected void setPostRemove(Method method, EntityListenerMetadata listener) {
        listener.setPostDeleteMethod(method);
    }
    
    /**
     * INTERNAL:
     */
    public void setPostRemove(String postRemove) {
        m_postRemove = postRemove;
    }
    
    /**
     * INTERNAL:
     * * Set the post update event method on the listener.
     */
    protected void setPostUpdate(Method method, EntityListenerMetadata listener) {
        listener.setPostUpdateMethod(method);
    }
    
    /**
     * INTERNAL:
     */
    public void setPostUpdate(String postUpdate) {
        m_postUpdate = postUpdate;
    }
    
    /**
     * INTERNAL:
     * Set the pre persist event method on the listener.
     */
    protected void setPrePersist(Method method, EntityListenerMetadata listener) {
        listener.setPrePersistMethod(method);
    }
    
    /**
     * INTERNAL:
     */
    public void setPrePersist(String prePersist) {
        m_prePersist = prePersist;
    }
    
    /**
     * INTERNAL:
     * Set the pre remove event method on the listener.
     */
    protected void setPreRemove(Method method, EntityListenerMetadata listener) {
        listener.setPreRemoveMethod(method);
    }
    
    /**
     * INTERNAL:
     */
    public void setPreRemove(String preRemove) {
        m_preRemove = preRemove;
    }
    
    /**
     * INTERNAL:
     * Set the pre update event method on the listener.
     */
    protected void setPreUpdate(Method method, EntityListenerMetadata listener) {
        listener.setPreUpdateWithChangesMethod(method);
    }

    /**
     * INTERNAL:
     */
    public void setPreUpdate(String preUpdate) {
        m_preUpdate = preUpdate;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReadOnly(Boolean readOnly) {
        m_readOnly = readOnly;
    }
}

