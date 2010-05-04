/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     06/25/2009-2.0 Michael O'Brien 
 *       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
 *          in support of the custom descriptors holding mappings required by the Metamodel 
 *     09/24//2009-2.0 Michael O'Brien 
 *       - 266912: In initIdClass() store IdClass names for use by the Metamodel API  
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     12/2/2009-2.1 Guy Pelletier 
 *       - 296289: Add current annotation metadata support on mapped superclasses to EclipseLink-ORM.XML Schema
 *     01/19/2010-2.1 Guy Pelletier 
 *       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
 *     05/04/2010-2.1 Guy Pelletier 
 *       - 309373: Add parent class attribute to EclipseLink-ORM
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.EntityListeners;
import javax.persistence.ExcludeDefaultListeners;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.IdClass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheInterceptor;
import org.eclipse.persistence.annotations.FetchGroup;
import org.eclipse.persistence.annotations.FetchGroups;
import org.eclipse.persistence.annotations.PrimaryKey;
import org.eclipse.persistence.annotations.QueryRedirectors;
import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.ReadOnly;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheInterceptorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheMetadata;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

import org.eclipse.persistence.internal.jpa.metadata.locking.OptimisticLockingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.PrimaryKeyMetadata;

import org.eclipse.persistence.internal.jpa.metadata.queries.FetchGroupMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.QueryRedirectorsMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * A mapped superclass accessor.
 * 
 * When adding new metadata objects, be sure to include their initialization in 
 * initXMLObject. This sets the accessible object and the location of the 
 * ORMetadata which is used when merging. Also new member metadata variables 
 * need to be added to the merge method.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MappedSuperclassAccessor extends ClassAccessor {
    private boolean m_excludeDefaultListeners;
    private boolean m_excludeSuperclassListeners;
    
    private Boolean m_cacheable;
    private Boolean m_readOnly;
    
    private CacheMetadata m_cache;
    private CacheInterceptorMetadata m_cacheInterceptor;
    
    private List<AssociationOverrideMetadata> m_associationOverrides = new ArrayList<AssociationOverrideMetadata>();
    private List<AttributeOverrideMetadata> m_attributeOverrides = new ArrayList<AttributeOverrideMetadata>();
    private List<EntityListenerMetadata> m_entityListeners = new ArrayList<EntityListenerMetadata>();
    private List<FetchGroupMetadata> m_fetchGroups = new ArrayList<FetchGroupMetadata>();
    private List<NamedQueryMetadata> m_namedQueries = new ArrayList<NamedQueryMetadata>();
    private List<NamedNativeQueryMetadata> m_namedNativeQueries = new ArrayList<NamedNativeQueryMetadata>();
    private List<NamedStoredProcedureQueryMetadata> m_namedStoredProcedureQueries = new ArrayList<NamedStoredProcedureQueryMetadata>();
    private List<SQLResultSetMappingMetadata> m_sqlResultSetMappings = new ArrayList<SQLResultSetMappingMetadata>();
    
    private MetadataClass m_idClass;

    private OptimisticLockingMetadata m_optimisticLocking;
    private PrimaryKeyMetadata m_primaryKey;
    private QueryRedirectorsMetadata m_queryRedirectors;
    private SequenceGeneratorMetadata m_sequenceGenerator;
    private TableGeneratorMetadata m_tableGenerator;
    
    private String m_existenceChecking;
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
     * Used for OX mapping.
     */
    public MappedSuperclassAccessor() {
        super("<mapped-superclass>");
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public MappedSuperclassAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public MappedSuperclassAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataProject project) {
        super(annotation, cls, project);
    }
    
    /**
     * INTERNAL:
     */
    public MappedSuperclassAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataDescriptor descriptor) {        
        super(annotation, cls, descriptor);
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
    public List<AssociationOverrideMetadata> getAssociationOverrides() {
        return m_associationOverrides;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AttributeOverrideMetadata> getAttributeOverrides() {
        return m_attributeOverrides;
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
    public Boolean getCacheable() {
        return m_cacheable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public CacheInterceptorMetadata getCacheInterceptor() {
        return m_cacheInterceptor;
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
    public String getExistenceChecking() {
        return m_existenceChecking;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<FetchGroupMetadata> getFetchGroups() {
        return m_fetchGroups;
    }
    
    /**
     * INTERNAL:
     */
    protected MetadataClass getIdClass() {
        return m_idClass;
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
    public List<NamedNativeQueryMetadata> getNamedNativeQueries() {
        return m_namedNativeQueries;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<NamedQueryMetadata> getNamedQueries() {
        return m_namedQueries;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<NamedStoredProcedureQueryMetadata> getNamedStoredProcedureQueries() {
        return m_namedStoredProcedureQueries;
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
    public PrimaryKeyMetadata getPrimaryKey() {
        return m_primaryKey;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public QueryRedirectorsMetadata getQueryRedirectors() {
        return m_queryRedirectors;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getReadOnly() {
        return m_readOnly;
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
    public List<SQLResultSetMappingMetadata> getSqlResultSetMappings() {
        return m_sqlResultSetMappings;
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
     * This method is called in the pre-processing stage since we want to
     * gather a list of id classes used throughout the persistence unit. This
     * will help us build accessors, namely, mappedById accessors that can
     * reference an id class type.
     */
    protected void initIdClass() {
        if (m_idClass == null || m_idClass.equals(void.class)) {
            // Check for an IdClass annotation.
            if (isAnnotationPresent(IdClass.class)) {
                m_idClass = getMetadataClass((String)getAnnotation(IdClass.class).getAttribute("value"));
            }
        } else {
            // We have an XML specification. Log a message if an annotation has also been defined.
            if (isAnnotationPresent(IdClass.class)) {
                getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, getAnnotation(IdClass.class), getJavaClassName(), getLocation());
            }
        }
        
        // Add the id class to the known list of id classes for this project.
        if (m_idClass != null && ! m_idClass.equals(void.class)) {
            getProject().addIdClass(m_idClass.getName());
            // 266912: We store the IdClass (not an EmbeddableId) for use by the Metamodel API
            getProject().getProject().addMetamodelIdClassMapEntry(getAccessibleObject().getName(), m_idClass.getName());
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize single objects.
        initXMLObject(m_cache, accessibleObject);
        initXMLObject(m_cacheInterceptor, accessibleObject);
        initXMLObject(m_optimisticLocking, accessibleObject);
        initXMLObject(m_primaryKey, accessibleObject);
        initXMLObject(m_queryRedirectors, accessibleObject);
        initXMLObject(m_sequenceGenerator, accessibleObject);
        initXMLObject(m_tableGenerator, accessibleObject);
        
        // Initialize lists of objects.
        initXMLObjects(m_associationOverrides, accessibleObject);
        initXMLObjects(m_attributeOverrides, accessibleObject);
        initXMLObjects(m_entityListeners, accessibleObject);
        initXMLObjects(m_fetchGroups, accessibleObject);
        initXMLObjects(m_namedQueries, accessibleObject);
        initXMLObjects(m_namedNativeQueries, accessibleObject);
        initXMLObjects(m_namedStoredProcedureQueries, accessibleObject);
        initXMLObjects(m_sqlResultSetMappings, accessibleObject);
        
        // Simple class object
        m_idClass = initXMLClassName(m_idClassName);
    }
    
    /**
     * INTERNAL:
     * Return whether this accessor represents a MappedSuperclass 
     */
    @Override
    public boolean isMappedSuperclass() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Mapped-superclass level merging details. Merging is only done on
     * accessors from XML. Since entities and embeddables are initialized
     * before merging, for any class name specifications we can not only merge
     * the class names but must also merge the initialized classes since we 
     * do not re-initialize class accessors after a merge. Mapped superclasses 
     * are not initialized till they are reloaded therefore merging only the 
     * class names is needed, however merging their respective MetadataClasses 
     * will do nothing since merging null with null yields null.
     */
    @Override
    public void merge(ORMetadata metadata) {
        super.merge(metadata);
        
        MappedSuperclassAccessor accessor = (MappedSuperclassAccessor) metadata;
        
        // Primitive boolean merging.
        m_excludeDefaultListeners = mergePrimitiveBoolean(m_excludeDefaultListeners, accessor.excludeDefaultListeners(), accessor, "<exclude-default-listeners>");
        m_excludeSuperclassListeners = mergePrimitiveBoolean(m_excludeSuperclassListeners, accessor.excludeSuperclassListeners(), accessor, "<exclude-superclass-listeners>");
        
        // Simple object merging.
        m_cacheable = (Boolean) mergeSimpleObjects(m_cacheable, accessor.getCacheable(), accessor, "@cacheable");
        m_readOnly = (Boolean) mergeSimpleObjects(m_readOnly, accessor.getReadOnly(), accessor, "@read-only");
        m_idClass = (MetadataClass) mergeSimpleObjects(m_idClass, accessor.getIdClass(), accessor, "<id-class>"); 
        m_idClassName = (String) mergeSimpleObjects(m_idClassName, accessor.getIdClassName(), accessor, "<id-class>");
        m_prePersist = (String) mergeSimpleObjects(m_prePersist, accessor.getPrePersist(), accessor, "<pre-persist>");
        m_postPersist = (String) mergeSimpleObjects(m_postPersist, accessor.getPostPersist(), accessor, "<post-persist>");
        m_preRemove = (String) mergeSimpleObjects(m_preRemove, accessor.getPreRemove(), accessor, "<pre-remove>");
        m_postRemove = (String) mergeSimpleObjects(m_postRemove, accessor.getPostRemove(), accessor, "<post-remove>");
        m_preUpdate = (String) mergeSimpleObjects(m_preUpdate, accessor.getPreUpdate(), accessor, "<pre-update>");
        m_postUpdate = (String) mergeSimpleObjects(m_postUpdate, accessor.getPostUpdate(), accessor, "<post-update>");
        m_postLoad = (String) mergeSimpleObjects(m_postLoad, accessor.getPostLoad(), accessor, "<post-load>");
        m_existenceChecking = (String) mergeSimpleObjects(m_existenceChecking, accessor.getExistenceChecking(), accessor, "@existence-checking");
        
        // ORMetadata object merging.
        m_cache = (CacheMetadata) mergeORObjects(m_cache, accessor.getCache());
        m_cacheInterceptor = (CacheInterceptorMetadata) mergeORObjects(m_cacheInterceptor, accessor.getCacheInterceptor());
        m_optimisticLocking = (OptimisticLockingMetadata) mergeORObjects(m_optimisticLocking, accessor.getOptimisticLocking());
        m_primaryKey = (PrimaryKeyMetadata) mergeORObjects(m_primaryKey, accessor.getPrimaryKey());
        m_queryRedirectors = (QueryRedirectorsMetadata) mergeORObjects(m_queryRedirectors, accessor.getQueryRedirectors());
        m_sequenceGenerator = (SequenceGeneratorMetadata) mergeORObjects(m_sequenceGenerator, accessor.getSequenceGenerator());
        m_tableGenerator = (TableGeneratorMetadata) mergeORObjects(m_tableGenerator, accessor.getTableGenerator());
        
        // ORMetadata list merging. 
        m_associationOverrides = mergeORObjectLists(m_associationOverrides, accessor.getAssociationOverrides());
        m_attributeOverrides = mergeORObjectLists(m_attributeOverrides, accessor.getAttributeOverrides());
        m_entityListeners = mergeORObjectLists(m_entityListeners, accessor.getEntityListeners());
        m_fetchGroups = mergeORObjectLists(m_fetchGroups, accessor.getFetchGroups());
        m_namedQueries = mergeORObjectLists(m_namedQueries, accessor.getNamedQueries());
        m_namedNativeQueries = mergeORObjectLists(m_namedNativeQueries, accessor.getNamedNativeQueries());
        m_namedStoredProcedureQueries = mergeORObjectLists(m_namedStoredProcedureQueries, accessor.getNamedStoredProcedureQueries());
        m_sqlResultSetMappings = mergeORObjectLists(m_sqlResultSetMappings, accessor.getSqlResultSetMappings());
    }
    
    /**
     * INTERNAL:
     * The pre-process method is called during regular deployment and metadata
     * processing. 
     * 
     * This method will pre-process the items of interest on this mapped 
     * superclass for each entity class that inherits from it. The order of 
     * processing is important, care must be taken if changes must be made.
     */
    @Override
    public void preProcess() {
        setIsPreProcessed();
        
        // Process the parent class if access is VIRTUAL.
        processParentClass();
        
        // Add any id class definition to the project.
        initIdClass();
        
        // Set a cacheable flag if specified on the entity's descriptor.
        processCacheable();
        
        // Add the accessors and converters from this mapped superclass.
        addAccessors();
        addConverters();
    }
    
    /**
     * INTERNAL:
     * The pre-process for canonical model method is called (and only called) 
     * during the canonical model generation. The use of this pre-process allows
     * us to remove some items from the regular pre-process that do not apply
     * to the canonical model generation.
     */
    @Override
    public void preProcessForCanonicalModel() {
        setIsPreProcessed();
        
        // Process the correct access type before any other processing.
        processAccessType();
        
        // Process the parent class if access is VIRTUAL.
        processParentClass();
        
        // Add the accessors from this mapped superclass.
        addAccessors();
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on a mapped superclass.
     */
    @Override
    public void process() {
        setIsProcessed();
        
        // Process the common class level attributes that an entity or
        // mapped superclass may define.
        processClassMetadata();
    }
    
    /**
     * INTERNAL:
     * Process the accessType for a MappedSuperclass.
     * This function is referenced by MetadataProject.addMetamodelMappedSuperclass().
     * The overridden function on the subclass must be used in all other cases.
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation
     */
    @Override
    public void processAccessType() {
        // 266912: Note: this function is a port of the subclass protected EntityAccessor.processAccessType() minus step 1 and 2
        String explicitAccessType = getAccess(); 
        String defaultAccessType = null;
        // 3 - If there are no mapped superclasses or no mapped superclasses
        // without an explicit access type. Check where the annotations are
        // defined on this entity class.
        if (havePersistenceFieldAnnotationsDefined(getJavaClass().getFields().values())) {
            defaultAccessType = MetadataConstants.FIELD;
        } else if (havePersistenceMethodAnnotationsDefined(getJavaClass().getMethods().values())) {
            defaultAccessType = MetadataConstants.PROPERTY;
        } else {
            // 4 - If there are no annotations defined on either the
            // fields or properties, check for an xml default from
            // persistence-unit-metadata-defaults or entity-mappings.
            if (getDescriptor().getDefaultAccess() != null) {
                defaultAccessType = getDescriptor().getDefaultAccess();
            } else {
                // 5 - We've exhausted our search, set the access type to FIELD.
                defaultAccessType = MetadataConstants.FIELD;
            }
        }
        
        // Finally set the default access type on the descriptor and log a 
        // message to the user if we are defaulting the access type for this
        // entity to use that default.
        getDescriptor().setDefaultAccess(defaultAccessType);
        
        if (explicitAccessType == null) {
            getLogger().logConfigMessage(MetadataLogger.ACCESS_TYPE, defaultAccessType, getJavaClass());
        }
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
        if (associationOverride.shouldOverride(getDescriptor().getAssociationOverrideFor(associationOverride.getName()), getLogger(), getDescriptor().getJavaClassName())) {
            getDescriptor().addAssociationOverride(associationOverride);
        }
    }
    
    /**
     * INTERNAL:
     * Process the association override metadata specified on an entity or 
     * mapped superclass. Once the association overrides are processed from
     * XML process the association overrides from annotations. This order of
     * processing must be maintained.
     */
    protected void processAssociationOverrides() {
        // Process the XML association override elements first.
        for (AssociationOverrideMetadata associationOverride : m_associationOverrides) {
            // Process the association override.
            processAssociationOverride(associationOverride);
        }
        
        // Process the association override annotations.
        // Look for an @AssociationOverrides.
        MetadataAnnotation associationOverrides = getAnnotation(AssociationOverrides.class);
        if (associationOverrides != null) {
            for (Object associationOverride : (Object[]) associationOverrides.getAttributeArray("value")) {
                processAssociationOverride(new AssociationOverrideMetadata((MetadataAnnotation) associationOverride, getAccessibleObject()));
            }
        }
        
        // Look for an @AssociationOverride.
        MetadataAnnotation associationOverride = getAnnotation(AssociationOverride.class);
        if (associationOverride != null) {
            processAssociationOverride(new AssociationOverrideMetadata(associationOverride, getAccessibleObject()));
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
        if (attributeOverride.shouldOverride(getDescriptor().getAttributeOverrideFor(attributeOverride.getName()), getLogger(), getDescriptor().getJavaClassName())) {
            getDescriptor().addAttributeOverride(attributeOverride);
        }
    }
    
    /**
     * INTERNAL:
     * Process the attribute override metadata specified on an entity or 
     * mapped superclass. Once the attribute overrides are processed from
     * XML process the attribute overrides from annotations. This order of 
     * processing must be maintained.
     */
    protected void processAttributeOverrides() {
        // Process the XML attribute overrides first.
        for (AttributeOverrideMetadata attributeOverride : m_attributeOverrides) {
            // Process the attribute override.
            processAttributeOverride(attributeOverride);
        }
        
        // Process the attribute override annotations.
        // Look for an @AttributeOverrides.
        MetadataAnnotation attributeOverrides = getAnnotation(AttributeOverrides.class);    
        if (attributeOverrides != null) {
            for (Object attributeOverride : (Object[]) attributeOverrides.getAttribute("value")){ 
                processAttributeOverride(new AttributeOverrideMetadata((MetadataAnnotation)attributeOverride, getAccessibleObject()));
            }
        }
        
        // Look for an @AttributeOverride.
        MetadataAnnotation attributeOverride = getAnnotation(AttributeOverride.class);
        if (attributeOverride != null) {
            processAttributeOverride(new AttributeOverrideMetadata(attributeOverride, getAccessibleObject()));
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
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CACHE, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_cache == null) {
                    new CacheMetadata(getAnnotation(Cache.class), getAccessibleObject()).process(getDescriptor(), getJavaClass());
                } else {
                    m_cache.process(getDescriptor(), getJavaClass());
                }
            }
        }        
    }
    
    /**
     * INTERNAL:
     * Called in pre-process. It is called from an entity accessor and in 
     * turn is called on the mapped-superclasses of that entity.
     */
    protected void processCacheable() {
        if (m_cacheable != null || isAnnotationPresent(Cacheable.class)) {
            if (getDescriptor().hasCacheable()) {
                // Ignore cacheable on mapped superclass if cacheable is already 
                // defined on the entity.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CACHE, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                // Set the cacheable setting on the descriptor.
                if (m_cacheable == null) {
                    m_cacheable = (Boolean) getAnnotation(Cacheable.class).getAttributeBooleanDefaultTrue("value");
                } 
                
                getDescriptor().setCacheable(m_cacheable);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process a cache interceptor metadata. 
     */
    protected void processCacheInterceptor() {
        if (m_cacheInterceptor != null || isAnnotationPresent(CacheInterceptor.class)) {
            if (getDescriptor().isInheritanceSubclass()) {
                // Ignore cache interceptor if specified on an inheritance subclass.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_INHERITANCE_SUBCLASS_CACHE_INTERCEPTOR, getJavaClass());
            } else if (getDescriptor().hasCacheInterceptor()) {
                // Ignore cache interceptor on mapped superclass if cache 
                // interceptor is already defined on the entity.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CACHE_INTERCEPTOR, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_cacheInterceptor == null) {
                    new CacheInterceptorMetadata(getAnnotation(CacheInterceptor.class), getAccessibleObject()).process(getDescriptor(), getJavaClass());
                } else {
                    m_cacheInterceptor.process(getDescriptor(), getJavaClass());
                }
            }
        }        
    }
    
    /**
     * INTERNAL:
     * Process a caching metadata. This method will be called on an entity's
     * mapped superclasses (bottom --> up). We go through the mapped 
     * superclasses to not only apply a cache setting but log ignore messages.
     */
    protected void processCaching() {
        // The settings for processing cache metadata are as follows:
        // ALL or no setting
        // ENABLE_SELECTIVE and Cacheable(true)
        // DISABLE_SELECTIVE and Cacheable(true) 
        if (getProject().isCacheAll() || 
           (getProject().isCacheEnableSelective() && getDescriptor().isCacheableTrue()) ||
           (getProject().isCacheDisableSelective() && ! getDescriptor().isCacheableFalse())) {
            
            processCachingMetadata();
        } 
    }
    
    /**
     * INTERNAL:
     * Process a caching metadata. These are the items we process to configure
     * the entity's cache settings.
     */
    protected void processCachingMetadata() {    
        processCache();
        processCacheInterceptor(); 
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on an entity or mapped superclass class. 
     */
    protected void processClassMetadata() {
        // Process the attribute override metadata.
        processAttributeOverrides();
                    
        // Process the association override metadata.
        processAssociationOverrides();
        
        // Process the fetch group metadata.
        processFetchGroups();
        
        // Process the named query metadata.
        processNamedQueries();
                    
        // Process the named native query metadata.
        processNamedNativeQueries();
        
        // Process the named stored procedure query metadata
        processNamedStoredProcedureQueries();
                    
        // Process the sql result set mapping metadata
        processSqlResultSetMappings();
                    
        // Process the table generator metadata.
        processTableGenerator();
            
        // Process the sequence generator metadata.
        processSequenceGenerator();
                    
        // Process the id class metadata.
        processIdClass();
        
        // Process the primary key metadata.
        processPrimaryKey();
        
        // Process the exclude default listeners metadata.
        processExcludeDefaultListeners();
        
        // Process the exclude superclass listeners metadata.
        processExcludeSuperclassListeners();
        
        // Process the optimistic locking policy metadata.
        processOptimisticLocking();
        
        // Process any cache metadata (Cache and CacheInterceptor) taking the 
        // cacheable and persistence unit global setting into consideration.
        processCaching();
        
        // Process the Default Redirectors
        processDefaultRedirectors();
            
        // Process the change tracking metadata.
        processChangeTracking();
        
        // Process the read only metadata.
        processReadOnly();
        
        // Process the customizer metadata.
        processCustomizer();
        
        // Process the copy policy metadata.
        processCopyPolicy();
        
        // Process the existence checking metadata.
        processExistenceChecking();
        
        // Process the property metadata.
        processProperties();
    }
    
    /**
     * INTERNAL:
     * Process a default redirector metadata. 
     */
    protected void processDefaultRedirectors() {
        if (m_queryRedirectors != null || isAnnotationPresent(QueryRedirectors.class)) {
            if (getDescriptor().isInheritanceSubclass()) {
                // Ignore query redirector if specified on an inheritance subclass.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_INHERITANCE_SUBCLASS_DEFAULT_REDIRECTORS, getJavaClass());
            } else if (getDescriptor().hasDefaultRedirectors()) {
                // Ignore query redirector on mapped superclass if query 
                // redirector is already defined on the entity.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_DEFAULT_REDIRECTORS, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_queryRedirectors == null) {
                    new QueryRedirectorsMetadata(getAnnotation(QueryRedirectors.class), getAccessibleObject()).process(getDescriptor(), getJavaClass());
                } else {
                    m_queryRedirectors.process(getDescriptor(), getJavaClass());
                }
            }
        }        
    }
    
    /**
     * INTERNAL:
     * Process the entity listeners for this class accessor. Entity listeners
     * defined in XML will override those specified on the class.
     */
    public void processEntityListeners(ClassLoader loader) {
        if (m_entityListeners.isEmpty()) {
            // Look for an annotation.
            MetadataAnnotation entityListeners = getAnnotation(EntityListeners.class);
            
            if (entityListeners != null) {
                for (Object entityListenerClass : (Object[]) entityListeners.getAttribute("value")) {
                    EntityListenerMetadata listener = new EntityListenerMetadata(entityListeners, getMetadataClass((String)entityListenerClass), getAccessibleObject());
                    listener.process(getDescriptor(), loader, false);
                }
            }
        } else {
            // Process the listeners defined in XML.
            for (EntityListenerMetadata listener : m_entityListeners) {
                listener.process(getDescriptor(), loader, false);               
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
            // that already excluded them.
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
        MetadataAnnotation existenceChecking = getAnnotation(ExistenceChecking.class);
        
        if (m_existenceChecking != null || existenceChecking != null) {
            if (getDescriptor().hasExistenceChecking()) {
                // Ignore existence-checking on mapped superclass if existence
                // checking is already defined on the entity.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_EXISTENCE_CHECKING, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_existenceChecking == null) {
                    getDescriptor().setExistenceChecking((String) existenceChecking.getAttribute("value"));
                } else {
                    if (existenceChecking != null) {
                        getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, existenceChecking, getJavaClassName(), getLocation());
                    }
                    
                    getDescriptor().setExistenceChecking(m_existenceChecking);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void processFetchGroup(FetchGroupMetadata fetchGroup, Map<String, FetchGroupMetadata> fetchGroups) {
        if (fetchGroup.shouldOverride(fetchGroups.get(fetchGroup.getName()))) {
            fetchGroups.put(fetchGroup.getName(), fetchGroup);
        }
    }
    
    /**
     * INTERNAL:
     * Process the fetch groups for this class. We need to go through both
     * the XML list and those defined in annotations. Must look for multiple
     * same named fetch groups within XML and annotations and XML named fetch
     * groups must override a similar named fetch group defined within an
     * annotation.
     * 
     * This method will be called from both Entity And MappedSuperclass. The
     * fetch groups from the entity are added first followed by those from its 
     * mapped superclass(es).
     */
    protected void processFetchGroups() {
        Map<String, FetchGroupMetadata> fetchGroups = new HashMap<String, FetchGroupMetadata>();
        
        // Process the XML fetch groups first.
        for (FetchGroupMetadata fetchGroup : m_fetchGroups) {
            processFetchGroup(fetchGroup, fetchGroups);
        }
        
        // Process the fetch group annotations.
        // Look for a @FetchGroup.
        if (isAnnotationPresent(FetchGroups.class)) {
            for (Object fetchGroup : (Object[]) getAnnotation(FetchGroups.class).getAttributeArray("value")) {
                processFetchGroup(new FetchGroupMetadata((MetadataAnnotation) fetchGroup, getAccessibleObject()), fetchGroups);
            }
        }
        
        // Look for a @FetchGroup.
        if (isAnnotationPresent(FetchGroup.class)) {
            processFetchGroup(new FetchGroupMetadata(getAnnotation(FetchGroup.class), getAccessibleObject()), fetchGroups);
        }
        
        // Now process all the fetch groups we found to the descriptor only
        // if weaving is enabled or if the descriptors java class for this 
        // accessor implements the FetchGroupTracker interface.
        if (getProject().isWeavingEnabled() || getDescriptor().getJavaClass().extendsInterface(org.eclipse.persistence.queries.FetchGroupTracker.class)) {
            for (FetchGroupMetadata fetchGroup : fetchGroups.values()) {
                fetchGroup.process(this);  
            }
        } else if (! fetchGroups.isEmpty()) {
            getLogger().logWarningMessage(MetadataLogger.IGNORE_FETCH_GROUP, getJavaClass(), getDescriptor().getJavaClass());
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
        if (m_idClass != null && !m_idClass.equals(void.class)) {
            getDescriptor().setPKClass(m_idClass);
            
            if (getDescriptor().usesDefaultPropertyAccess()) {
                for (MetadataMethod method : m_idClass.getMethods().values()) {
                    // The is valid check will throw an exception if needed.
                    if (method.isValidPersistenceMethod(false, getDescriptor())) {
                        getDescriptor().addPKClassId(method.getAttributeName(), method.getType());
                    }
                }
            } else {
                for (MetadataField field : m_idClass.getFields().values()) {
                    // The is valid check will throw an exception if needed.
                    if (field.isValidPersistenceField(false, getDescriptor())) {
                        getDescriptor().addPKClassId(field.getName(), field.getType());
                    }
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Used to process mapped superclasses when creating descriptors for a 
     * metamodel. The MappedSuperclass Descriptors here are separate from 
     * non-MappedSuperclass Descriptors.
     * @since EclipseLink 1.2 for the JPA 2.0 Reference Implementation
     */
    public void processMetamodelDescriptor() {        
        for (MappedSuperclassAccessor mappedSuperclass : getProject().getMetamodelMappedSuperclasses()) {
            mappedSuperclass.processAccessors();
        }
    }
    
    /**
     * INTERNAL:
     * Process/collect the named native queries on this accessor and add them
     * to the project for later processing.
     */
    protected void processNamedNativeQueries() {
        // Process the XML named native queries first.
        for (NamedNativeQueryMetadata namedNativeQuery : m_namedNativeQueries) {
            getProject().addQuery(namedNativeQuery);
        }
        
        // Process the named native query annotations.
        // Look for a @NamedNativeQueries.
        MetadataAnnotation namedNativeQueries = getAnnotation(NamedNativeQueries.class);
        if (namedNativeQueries != null) {
            for (Object namedNativeQuery : (Object[]) namedNativeQueries.getAttribute("value")) { 
                getProject().addQuery(new NamedNativeQueryMetadata((MetadataAnnotation)namedNativeQuery, getAccessibleObject()));
            }
        }
        
        // Look for a @NamedNativeQuery.
        MetadataAnnotation namedNativeQuery = getAnnotation(NamedNativeQuery.class);
        if (namedNativeQuery != null) {
            getProject().addQuery(new NamedNativeQueryMetadata(namedNativeQuery, getAccessibleObject()));
        }
    }
    
    /**
     * INTERNAL:
     * Process/collect the named queries on this accessor and add them to the 
     * project for later processing.
     */
    protected void processNamedQueries() {
        // Process the XML named queries first.
        for (NamedQueryMetadata namedQuery : m_namedQueries) {
            getProject().addQuery(namedQuery);
        }
        
        // Process the named query annotations.
        // Look for a @NamedQueries.
        MetadataAnnotation namedQueries = getAnnotation(NamedQueries.class);
        if (namedQueries != null) {
            for (Object namedQuery : (Object[]) namedQueries.getAttributeArray("value")) { 
                getProject().addQuery(new NamedQueryMetadata((MetadataAnnotation)namedQuery, getAccessibleObject()));
            }
        }
        
        // Look for a @NamedQuery.
        MetadataAnnotation namedQuery = getAnnotation(NamedQuery.class);
        if (namedQuery != null) {
            getProject().addQuery(new NamedQueryMetadata(namedQuery, getAccessibleObject()));
        }
    }
    
    /**
     * INTERNAL:
     * Process/collect the named stored procedure queries on this accessor and 
     * add them to the project for later processing.
     */
    protected void processNamedStoredProcedureQueries() {
        // Process the XML named queries first.
        for (NamedStoredProcedureQueryMetadata namedStoredProcedureQuery : m_namedStoredProcedureQueries) {
            getProject().addQuery(namedStoredProcedureQuery);
        }
        
        // Process the named stored procedure query annotations.
        // Look for a @NamedStoredProcedureQueries.
        MetadataAnnotation namedStoredProcedureQueries = getAnnotation(NamedStoredProcedureQueries.class);
        if (namedStoredProcedureQueries != null) {
            for (Object namedStoredProcedureQuery : (Object[]) namedStoredProcedureQueries.getAttribute("value")) { 
                getProject().addQuery(new NamedStoredProcedureQueryMetadata((MetadataAnnotation)namedStoredProcedureQuery, getAccessibleObject()));
            }
        }
        
        // Look for a @NamedStoredProcedureQuery.
        MetadataAnnotation namedStoredProcedureQuery = getAnnotation(NamedStoredProcedureQuery.class);
        if (namedStoredProcedureQuery != null) {
            getProject().addQuery(new NamedStoredProcedureQueryMetadata(namedStoredProcedureQuery, getAccessibleObject()));
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
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_OPTIMISTIC_LOCKING, getDescriptor().getJavaClass(), getJavaClass());
            }
        } else {
            MetadataAnnotation optimisticLocking = getAnnotation(OptimisticLocking.class);
            
            if (m_optimisticLocking == null) {
                if (optimisticLocking != null) {
                    // Process the meta data for this accessor's descriptor.
                    new OptimisticLockingMetadata(optimisticLocking, getAccessibleObject()).process(getDescriptor());
                }
            } else {
                // If there is an annotation log a warning that we are 
                // ignoring it.
                if (optimisticLocking != null) {
                    getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, optimisticLocking, getJavaClassName(), getLocation());
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
        MetadataAnnotation readOnly = getAnnotation(ReadOnly.class);
        
        if (m_readOnly != null || readOnly != null) {
            if (getDescriptor().isInheritanceSubclass()) {
                // Ignore read only if specified on an inheritance subclass.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_INHERITANCE_SUBCLASS_READ_ONLY, getJavaClass());
            } else if (getDescriptor().hasReadOnly()) {
                // Ignore read only on mapped superclass if read only is already 
                // defined on the entity.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_READ_ONLY, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_readOnly == null) {
                    getDescriptor().setReadOnly(true);
                } else {
                    if (readOnly != null) {
                        getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, readOnly, getJavaClassName(), getLocation());
                    }
                    
                    getDescriptor().setReadOnly(m_readOnly);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the primary key annotation.
     */
    protected void processPrimaryKey() {
        MetadataAnnotation primaryKey = getAnnotation(PrimaryKey.class);
        
        if (m_primaryKey == null) {
            if (primaryKey != null) {
                // Process the meta data for this accessor's descriptor.
                new PrimaryKeyMetadata(primaryKey, getAccessibleObject()).process(getDescriptor());
            }
        } else {
            // If there is an annotation log a warning that we are 
            // ignoring it.
            if (primaryKey != null) {
                getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, primaryKey, getJavaClassName(), getLocation());
            }
        
            // Process the meta data for this accessor's descriptor.
            m_primaryKey.process(getDescriptor());
        }
    }
    
    /**
     * INTERNAL:
     * Process a SequenceGenerator annotation into a common metadata sequence 
     * generator and add it to the project.
     */
    protected void processSequenceGenerator() {
        // Process the xml defined sequence generator first.
        if (m_sequenceGenerator != null) {
            getProject().addSequenceGenerator(m_sequenceGenerator, getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
        }
        
        if (isAnnotationPresent(SequenceGenerator.class)) {
            // Ask the common processor to process what we found.
            getProject().addSequenceGenerator(new SequenceGeneratorMetadata(getAnnotation(SequenceGenerator.class), getAccessibleObject()), getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
        }
    }
    
    /**
     * INTERNAL:
     * Process the sql result set mappings for the given class which could be 
     * an entity or a mapped superclass.
     */
    protected void processSqlResultSetMappings() {
        // Process the XML sql result set mapping elements first.
        for (SQLResultSetMappingMetadata sqlResultSetMapping : m_sqlResultSetMappings) {
            getProject().addSQLResultSetMapping(sqlResultSetMapping);
        }
        
        // Process the sql result set mapping query annotations.
        // Look for a @SqlResultSetMappings.
        MetadataAnnotation sqlResultSetMappings = getAnnotation(SqlResultSetMappings.class);

        if (sqlResultSetMappings != null) {
            for (Object sqlResultSetMapping : (Object[]) sqlResultSetMappings.getAttribute("value")) {
                getProject().addSQLResultSetMapping(new SQLResultSetMappingMetadata((MetadataAnnotation)sqlResultSetMapping, getAccessibleObject()));
            }
        } else {
            // Look for a @SqlResultSetMapping.
            MetadataAnnotation sqlResultSetMapping = getAnnotation(SqlResultSetMapping.class);
            
            if (sqlResultSetMapping != null) {
                getProject().addSQLResultSetMapping(new SQLResultSetMappingMetadata(sqlResultSetMapping, getAccessibleObject()));
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process a TableGenerator annotation into a common metadata table 
     * generator and add it to the project.
     */
    protected void processTableGenerator() {
        // Process the xml defined table generator first.
        if (m_tableGenerator != null) {
            getProject().addTableGenerator(m_tableGenerator, getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
        }
        
        if (isAnnotationPresent(TableGenerator.class)) {
            getProject().addTableGenerator(new TableGeneratorMetadata(getAnnotation(TableGenerator.class), getAccessibleObject()), getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
        }
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAssociationOverrides(List<AssociationOverrideMetadata> associationOverrides) {
        m_associationOverrides = associationOverrides;
    } 
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides) {
        m_attributeOverrides = attributeOverrides;
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
    public void setCacheable(Boolean cacheable) {
        m_cacheable = cacheable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCacheInterceptor(CacheInterceptorMetadata cacheInterceptor) {
        m_cacheInterceptor = cacheInterceptor;
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
    public void setExistenceChecking(String checking) {
        m_existenceChecking = checking;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setFetchGroups(List<FetchGroupMetadata> fetchGroups) {
        m_fetchGroups = fetchGroups;
    }
    
    /**
     * INTERNAL:
     */
    protected void setIdClass(MetadataClass idClass) {
        m_idClass = idClass;
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
    public void setNamedNativeQueries(List<NamedNativeQueryMetadata> namedNativeQueries) {
        m_namedNativeQueries = namedNativeQueries;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setNamedQueries(List<NamedQueryMetadata> namedQueries) {
        m_namedQueries = namedQueries;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setNamedStoredProcedureQueries(List<NamedStoredProcedureQueryMetadata> namedStoredProcedureQueries) {
        m_namedStoredProcedureQueries = namedStoredProcedureQueries;
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
     * Used for OX mapping.
     */
    public void setPostLoad(String postLoad) {
        m_postLoad = postLoad;
    }
    
    /**
     * INTERNAL:
     */
    public void setPostPersist(String postPersist) {
        m_postPersist = postPersist;
    }
    
    /**
     * INTERNAL:
     */
    public void setPostRemove(String postRemove) {
        m_postRemove = postRemove;
    }
    
    /**
     * INTERNAL:
     */
    public void setPostUpdate(String postUpdate) {
        m_postUpdate = postUpdate;
    }
    
    /**
     * INTERNAL:
     */
    public void setPrePersist(String prePersist) {
        m_prePersist = prePersist;
    }
    
    /**
     * INTERNAL:
     */
    public void setPreRemove(String preRemove) {
        m_preRemove = preRemove;
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
    public void setQueryRedirectors(QueryRedirectorsMetadata redirectors) {
        m_queryRedirectors = redirectors;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReadOnly(Boolean readOnly) {
        m_readOnly = readOnly;
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
    public void setSqlResultSetMappings(List<SQLResultSetMappingMetadata> sqlResultSetMappings) {
        m_sqlResultSetMappings = sqlResultSetMappings;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTableGenerator(TableGeneratorMetadata tableGenerator) {
        m_tableGenerator = tableGenerator;
    }
}

