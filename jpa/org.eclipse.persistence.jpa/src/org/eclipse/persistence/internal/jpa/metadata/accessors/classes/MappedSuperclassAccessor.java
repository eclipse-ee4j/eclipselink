/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
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
 *     06/01/2010-2.1 Guy Pelletier 
 *       - 315195: Add new property to avoid reading XML during the canonical model generation
 *     06/09/2010-2.0.3 Guy Pelletier 
 *       - 313401: shared-cache-mode defaults to NONE when the element value is unrecognized
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     06/22/2010-2.2 Guy Pelletier 
 *       - 308729: Persistent Unit deployment exception when mappedsuperclass has no annotations but has lifecycle callbacks
 *     07/05/2010-2.1.1 Guy Pelletier 
 *       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
 *     08/04/2010-2.1.1 Guy Pelletier
 *       - 315782: JPA2 derived identity metadata processing validation doesn't account for autoboxing
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     10/28/2010-2.2 Guy Pelletier 
 *       - 3223850: Primary key metadata issues
 *     12/01/2010-2.2 Guy Pelletier 
 *       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification 
 *     03/08/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 8)
 *     07/03/2011-2.3.1 Guy Pelletier 
 *       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.NamedStoredProcedureQueries;

import org.eclipse.persistence.annotations.AdditionalCriteria;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheIndex;
import org.eclipse.persistence.annotations.CacheIndexes;
import org.eclipse.persistence.annotations.CacheInterceptor;
import org.eclipse.persistence.annotations.FetchGroup;
import org.eclipse.persistence.annotations.FetchGroups;
import org.eclipse.persistence.annotations.NamedStoredFunctionQueries;
import org.eclipse.persistence.annotations.NamedStoredFunctionQuery;
import org.eclipse.persistence.annotations.PrimaryKey;
import org.eclipse.persistence.annotations.QueryRedirectors;
import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.ReadOnly;
import org.eclipse.persistence.annotations.UuidGenerator;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.metadata.additionalcriteria.AdditionalCriteriaMetadata;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheIndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheInterceptorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.locking.OptimisticLockingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.multitenant.MultitenantMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.FetchGroupMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.QueryRedirectorsMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.UuidGeneratorMetadata;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredFunctionQueries;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredFunctionQuery;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredProcedureQueries;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredProcedureQuery;
import org.eclipse.persistence.queries.FetchGroupTracker;

/**
 * INTERNAL:
 * A mapped superclass accessor.
 * 
 * When adding new metadata objects, be sure to include their initialization in 
 * initXMLObject. This sets the accessible object and the location of the 
 * ORMetadata which is used when merging. Also new member metadata variables 
 * need to be added to the merge method.
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
public class MappedSuperclassAccessor extends ClassAccessor {
    private Boolean m_excludeDefaultListeners;
    private Boolean m_excludeSuperclassListeners;
    
    private AdditionalCriteriaMetadata m_additionalCriteria;
    
    private Boolean m_cacheable;
    private Boolean m_readOnly;
    
    private CacheMetadata m_cache;
    private CacheInterceptorMetadata m_cacheInterceptor;
    private List<CacheIndexMetadata> m_cacheIndexes = new ArrayList<CacheIndexMetadata>();
    
    private List<EntityListenerMetadata> m_entityListeners = new ArrayList<EntityListenerMetadata>();
    private List<FetchGroupMetadata> m_fetchGroups = new ArrayList<FetchGroupMetadata>();
    private List<NamedQueryMetadata> m_namedQueries = new ArrayList<NamedQueryMetadata>();
    private List<NamedNativeQueryMetadata> m_namedNativeQueries = new ArrayList<NamedNativeQueryMetadata>();
    private List<NamedStoredFunctionQueryMetadata> m_namedStoredFunctionQueries = new ArrayList<NamedStoredFunctionQueryMetadata>();
    private List<NamedStoredProcedureQueryMetadata> m_namedStoredProcedureQueries = new ArrayList<NamedStoredProcedureQueryMetadata>();
    private List<NamedPLSQLStoredFunctionQueryMetadata> m_namedPLSQLStoredFunctionQueries = new ArrayList<NamedPLSQLStoredFunctionQueryMetadata>();
    private List<NamedPLSQLStoredProcedureQueryMetadata> m_namedPLSQLStoredProcedureQueries = new ArrayList<NamedPLSQLStoredProcedureQueryMetadata>();
    private List<SQLResultSetMappingMetadata> m_sqlResultSetMappings = new ArrayList<SQLResultSetMappingMetadata>();
    
    private MetadataClass m_idClass;
    private MultitenantMetadata m_multitenant;
    private OptimisticLockingMetadata m_optimisticLocking;
    private PrimaryKeyMetadata m_primaryKey;
    private QueryRedirectorsMetadata m_queryRedirectors;
    private SequenceGeneratorMetadata m_sequenceGenerator;
    private TableGeneratorMetadata m_tableGenerator;
    private UuidGeneratorMetadata m_uuidGenerator;
    
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
        return m_excludeDefaultListeners != null && m_excludeDefaultListeners.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean excludeSuperclassListeners() {
        return m_excludeSuperclassListeners != null && m_excludeSuperclassListeners.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public AdditionalCriteriaMetadata getAdditionalCriteria() {
        return m_additionalCriteria;
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
    public List<CacheIndexMetadata> getCacheIndexes() {
        return m_cacheIndexes;
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
    public Boolean getExcludeDefaultListeners() {
        return m_excludeDefaultListeners;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getExcludeSuperclassListeners() {
        return m_excludeSuperclassListeners;
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
    public MultitenantMetadata getMultitenant() {
        return m_multitenant;
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
    public List<NamedPLSQLStoredFunctionQueryMetadata> getNamedPLSQLStoredFunctionQueries() {
        return m_namedPLSQLStoredFunctionQueries;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<NamedPLSQLStoredProcedureQueryMetadata> getNamedPLSQLStoredProcedureQueries() {
        return m_namedPLSQLStoredProcedureQueries;
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
    public List<NamedStoredFunctionQueryMetadata> getNamedStoredFunctionQueries() {
        return m_namedStoredFunctionQueries;
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
     * Used for OX mapping.
     */
    public UuidGeneratorMetadata getUuidGenerator() {
        return m_uuidGenerator;
    }
    
    /**
     * INTERNAL:
     * Return true if any given field defines object relational persistence
     * mapping annotations. This method is used when determining the access type 
     * of this accessor. Note: Through the annotation target, it is invalid to 
     * specify a lifecycle annotation on a field so we don't need to check as we 
     * do when checking the methods.
     * 
     * @see processAccessType()
     */
    protected boolean hasObjectRelationalFieldMappingAnnotationsDefined() {
        Collection<MetadataField> fields = getJavaClass().getFields().values();
        
        for (MetadataField field : fields) {
            if (field.hasDeclaredAnnotations(this)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Return true if any given method defines object relational persistence
     * mapping annotations. This method is used when determining the access type 
     * of this accessor. Note: life cycle annotations are NOT object relational 
     * mappings therefore should not influence the decision.
     * 
     * @see processAccessType()
     */
    protected boolean hasObjectRelationalMethodMappingAnnotationsDefined() {
        Collection<MetadataMethod> methods = getJavaClass().getMethods().values();
        
        for (MetadataMethod method : methods) {
            if (method.hasDeclaredAnnotations(this) && ! method.isALifeCycleCallbackMethod()) {
                return true;
            }
        }
        
        return false;
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
        initXMLObject(m_additionalCriteria, accessibleObject);
        initXMLObject(m_cache, accessibleObject);
        initXMLObject(m_cacheInterceptor, accessibleObject);
        initXMLObject(m_optimisticLocking, accessibleObject);
        initXMLObject(m_primaryKey, accessibleObject);
        initXMLObject(m_queryRedirectors, accessibleObject);
        initXMLObject(m_sequenceGenerator, accessibleObject);
        initXMLObject(m_tableGenerator, accessibleObject);
        initXMLObject(m_uuidGenerator, accessibleObject);
        initXMLObject(m_multitenant, accessibleObject);
        
        // Initialize lists of objects.
        initXMLObjects(m_entityListeners, accessibleObject);
        initXMLObjects(m_fetchGroups, accessibleObject);
        initXMLObjects(m_namedQueries, accessibleObject);
        initXMLObjects(m_namedNativeQueries, accessibleObject);
        initXMLObjects(m_namedStoredFunctionQueries, accessibleObject);
        initXMLObjects(m_namedStoredProcedureQueries, accessibleObject);
        initXMLObjects(m_namedPLSQLStoredFunctionQueries, accessibleObject);
        initXMLObjects(m_namedPLSQLStoredProcedureQueries, accessibleObject);
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
         
        // Simple object merging.
        m_excludeDefaultListeners = (Boolean) mergeSimpleObjects(m_excludeDefaultListeners, accessor.excludeDefaultListeners(), accessor, "<exclude-default-listeners>");
        m_excludeSuperclassListeners = (Boolean) mergeSimpleObjects(m_excludeSuperclassListeners, accessor.excludeSuperclassListeners(), accessor, "<exclude-superclass-listeners>");
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
        m_additionalCriteria = (AdditionalCriteriaMetadata) mergeORObjects(m_additionalCriteria, accessor.getAdditionalCriteria());
        m_cache = (CacheMetadata) mergeORObjects(m_cache, accessor.getCache());
        m_cacheInterceptor = (CacheInterceptorMetadata) mergeORObjects(m_cacheInterceptor, accessor.getCacheInterceptor());
        m_optimisticLocking = (OptimisticLockingMetadata) mergeORObjects(m_optimisticLocking, accessor.getOptimisticLocking());
        m_primaryKey = (PrimaryKeyMetadata) mergeORObjects(m_primaryKey, accessor.getPrimaryKey());
        m_queryRedirectors = (QueryRedirectorsMetadata) mergeORObjects(m_queryRedirectors, accessor.getQueryRedirectors());
        m_sequenceGenerator = (SequenceGeneratorMetadata) mergeORObjects(m_sequenceGenerator, accessor.getSequenceGenerator());
        m_tableGenerator = (TableGeneratorMetadata) mergeORObjects(m_tableGenerator, accessor.getTableGenerator());
        m_uuidGenerator = (UuidGeneratorMetadata) mergeORObjects(m_uuidGenerator, accessor.getUuidGenerator());
        m_multitenant = (MultitenantMetadata) mergeORObjects(m_multitenant, accessor.getMultitenant());
        
        // ORMetadata list merging.
        m_entityListeners = mergeORObjectLists(m_entityListeners, accessor.getEntityListeners());
        m_fetchGroups = mergeORObjectLists(m_fetchGroups, accessor.getFetchGroups());
        m_namedQueries = mergeORObjectLists(m_namedQueries, accessor.getNamedQueries());
        m_namedNativeQueries = mergeORObjectLists(m_namedNativeQueries, accessor.getNamedNativeQueries());
        m_namedStoredFunctionQueries = mergeORObjectLists(m_namedStoredFunctionQueries, accessor.getNamedStoredFunctionQueries());
        m_namedStoredProcedureQueries = mergeORObjectLists(m_namedStoredProcedureQueries, accessor.getNamedStoredProcedureQueries());
        m_namedPLSQLStoredFunctionQueries = mergeORObjectLists(m_namedPLSQLStoredFunctionQueries, accessor.getNamedPLSQLStoredFunctionQueries());
        m_namedPLSQLStoredProcedureQueries = mergeORObjectLists(m_namedPLSQLStoredProcedureQueries, accessor.getNamedPLSQLStoredProcedureQueries());
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
        // Add any id class definition to the project.
        initIdClass();
        
        // Set a cacheable flag if specified on the entity's descriptor.
        processCacheable();
        
        // Process our parents metadata after processing our own.
        super.preProcess();
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on a mapped superclass.
     */
    @Override
    public void process() {
        // Process the fetch group metadata.
        processFetchGroups();
        
        // Process the named query metadata.
        processNamedQueries();
                    
        // Process the named native query metadata.
        processNamedNativeQueries();
        
        // Process the named stored procedure query metadata
        processNamedStoredProcedureQueries();
        
        // Process the named stored function query metadata
        processNamedStoredFunctionQueries();
        
        // Process the named PLSQL stored procedure query metadata
        processNamedPLSQLStoredProcedureQueries();
        
        // Process the named PLSQL stored function query metadata
        processNamedPLSQLStoredFunctionQueries();
                    
        // Process the sql result set mapping metadata
        processSqlResultSetMappings();
                    
        // Process the table generator metadata.
        processTableGenerator();
                    
        // Process the uuid generator metadata.
        processUuidGenerator();
            
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
        
        // Process the read only metadata.
        processReadOnly();
        
        // Process the existence checking metadata.
        processExistenceChecking();
        
        // Process the additional criteria metadata.
        processAdditionalCriteria();
       
        // Process the multitenant metadata
        processMultitenant();
        
        // Process our parents metadata after processing our own.
        super.process();
    }
        
    /**
     * INTERNAL:
     * Process the additional criteria metadata specified on an entity or 
     * mapped superclass. Once the additional criteria are processed from
     * XML process the additional criteria from annotations. This order of 
     * processing must be maintained.
     */
    protected void processAdditionalCriteria() {
        if (m_additionalCriteria != null || isAnnotationPresent(AdditionalCriteria.class)) {
            // We have additional criteria available. If the descriptor already
            // has additional criteria, then we are processing a mapped 
            // superclass and should ignore the additional criteria for this 
            // descriptor.
            if (getDescriptor().hasAdditionalCriteria()) {
                // Ignore additional criteria on mapped superclass if additional
                // criteria is already defined on the entity.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_ADDITIONAL_CRITERIA, getDescriptor().getJavaClass(), getJavaClass());    
            } else if (m_additionalCriteria != null) {
                // Process the additional criteria that was specified in XML.
                m_additionalCriteria.process(getDescriptor());

            } else {
                // Process the additional criteria from the annotation.
                new AdditionalCriteriaMetadata(getAnnotation(AdditionalCriteria.class), this).process(getDescriptor());
            }
        }
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
        // 1 - If there are no mapped superclasses or no mapped superclasses
        // without an explicit access type. Check where the annotations are
        // defined on this entity class.
        if (hasObjectRelationalFieldMappingAnnotationsDefined()) {
            defaultAccessType = MetadataConstants.FIELD;
        } else if (hasObjectRelationalMethodMappingAnnotationsDefined()) {
            defaultAccessType = MetadataConstants.PROPERTY;
        } else {
            // 2 - If there are no annotations defined on either the
            // fields or properties, check for an xml default from
            // persistence-unit-metadata-defaults or entity-mappings.
            if (getDescriptor().getDefaultAccess() != null) {
                defaultAccessType = getDescriptor().getDefaultAccess();
            } else {
                // 3 - We've exhausted our search, set the access type to FIELD.
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
                    new CacheMetadata(getAnnotation(Cache.class), this).process(getDescriptor(), getJavaClass());
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
                    new CacheInterceptorMetadata(getAnnotation(CacheInterceptor.class), this).process(getDescriptor(), getJavaClass());
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
        if (getProject().isSharedCacheModeAll() || 
            getProject().isSharedCacheModeEnableSelective()||
            getProject().isSharedCacheModeDisableSelective()) {
            
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
        processCacheIndexes();
    }
    
    /**
     * INTERNAL:
     * Process cache index information for the given metadata descriptor.
     */
    protected void processCacheIndexes() {
        MetadataAnnotation index = getAnnotation(CacheIndex.class);
        
        if (index != null) {
            m_cacheIndexes.add(new CacheIndexMetadata(index, this));
        }
        
        MetadataAnnotation indexes = getAnnotation(CacheIndexes.class);
        if (indexes != null) {
            Object[] indexArray = (Object[])indexes.getAttributeArray("value");
            for (Object eachIndex : indexArray) {
                m_cacheIndexes.add(new CacheIndexMetadata((MetadataAnnotation) eachIndex, this));            
            }
        }
        
        for (CacheIndexMetadata indexMetadata : m_cacheIndexes) {
            indexMetadata.process(getDescriptor(), null);
        }
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
                    new QueryRedirectorsMetadata(getAnnotation(QueryRedirectors.class), this).process(getDescriptor(), getJavaClass());
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
                    EntityListenerMetadata listener = new EntityListenerMetadata(entityListeners, getMetadataClass((String) entityListenerClass, false), this);
                    listener.process(this, loader, false);
                }
            }
        } else {
            // Process the listeners defined in XML.
            for (EntityListenerMetadata listener : m_entityListeners) {
                listener.process(this, loader, false);
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
                processFetchGroup(new FetchGroupMetadata((MetadataAnnotation) fetchGroup, this), fetchGroups);
            }
        }
        
        // Look for a @FetchGroup.
        if (isAnnotationPresent(FetchGroup.class)) {
            processFetchGroup(new FetchGroupMetadata(getAnnotation(FetchGroup.class), this), fetchGroups);
        }
        
        // Now process all the fetch groups we found to the descriptor only
        // if weaving is enabled or if the descriptors java class for this 
        // accessor implements the FetchGroupTracker interface.
        if (getProject().isWeavingFetchGroupsEnabled() || getDescriptor().getJavaClass().extendsInterface(FetchGroupTracker.class)) {
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
                    if (method.isValidPersistenceMethod(false, this)) {
                        getDescriptor().addPKClassId(method.getAttributeName(), getBoxedType(method.getType()));
                    }
                }
            } else {
                for (MetadataField field : m_idClass.getFields().values()) {
                    // The is valid check will throw an exception if needed.
                    if (field.isValidPersistenceField(false, this)) {
                        getDescriptor().addPKClassId(field.getName(), getBoxedType(field.getType()));
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
            mappedSuperclass.processMappingAccessors();
        }
    }
    
    /**
     * INTERNAL:
     * Process the multitenant metadata specified on a mapped superclass and 
     * apply it to a sub-entity that has no multitenant metadata specified.
     */
    protected void processMultitenant() {
        if (m_multitenant != null || isAnnotationPresent(Multitenant.class)) {
            // We have multitenant metadata available. If the descriptor already
            // has multi-tenant settings ignore the multi-tenant metadata.
            if (getDescriptor().hasMultitenant()) {
                // Ignore multitenant on mapped superclass if multitenant is 
                // already defined on the entity.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_MULTITENANT, getDescriptor().getJavaClass(), getJavaClass());    
            } else if (m_multitenant != null) {
                // We have multitenant metadata loaded from XML. Log a warning
                // if equivalent annotation metadata is specified.
                if (isAnnotationPresent(Multitenant.class)) {
                    getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, getAnnotation(Multitenant.class), getJavaClassName(), getLocation());
                }
                
                // Process the multitenant metadata that was specified in XML.
                m_multitenant.process(getDescriptor());
            } else {
                // Process the multitenant from the annotation.
                new MultitenantMetadata(getAnnotation(Multitenant.class), this).process(getDescriptor());
            }
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
                getProject().addQuery(new NamedNativeQueryMetadata((MetadataAnnotation) namedNativeQuery, this));
            }
        }
        
        // Look for a @NamedNativeQuery.
        MetadataAnnotation namedNativeQuery = getAnnotation(NamedNativeQuery.class);
        if (namedNativeQuery != null) {
            getProject().addQuery(new NamedNativeQueryMetadata(namedNativeQuery, this));
        }
    }
    
    /**
     * INTERNAL:
     * Process/collect the named PLSQL stored function queries on this accessor 
     * and  add them to the project for later processing.
     */
    protected void processNamedPLSQLStoredFunctionQueries() {
        // Process the XML named PLSQL stored function queries first.
        for (NamedPLSQLStoredFunctionQueryMetadata namedPLSQLStoredFunctionQuery : m_namedPLSQLStoredFunctionQueries) {
            getProject().addQuery(namedPLSQLStoredFunctionQuery);
        }
        
        // Process the named PLSQL stored function query annotations.
        // Look for a @NamedPLSQLStoredFunctionQueries.
        MetadataAnnotation namedPLSQLStoredFunctionQueries = getAnnotation(NamedPLSQLStoredFunctionQueries.class);
        if (namedPLSQLStoredFunctionQueries != null) {
            for (Object namedPLSQLStoredFunctionQuery : (Object[]) namedPLSQLStoredFunctionQueries.getAttribute("value")) { 
                getProject().addQuery(new NamedPLSQLStoredFunctionQueryMetadata((MetadataAnnotation) namedPLSQLStoredFunctionQuery, this));
            }
        }
        
        // Look for a @NamedPLSQLStoredFunctionQuery.
        MetadataAnnotation namedPLSQLStoredFunctionQuery = getAnnotation(NamedPLSQLStoredFunctionQuery.class);
        if (namedPLSQLStoredFunctionQuery != null) {
            getProject().addQuery(new NamedPLSQLStoredFunctionQueryMetadata(namedPLSQLStoredFunctionQuery, this));
        }
    }
    
    /**
     * INTERNAL:
     * Process/collect the named PLSQL stored procedure queries on this accessor 
     * and  add them to the project for later processing.
     */
    protected void processNamedPLSQLStoredProcedureQueries() {
        // Process the XML named PLSQL stored procedure queries queries first.
        for (NamedPLSQLStoredProcedureQueryMetadata namedPLSQLStoredProcedureQuery : m_namedPLSQLStoredProcedureQueries) {
            getProject().addQuery(namedPLSQLStoredProcedureQuery);
        }
        
        // Process the named PLSQL stored procedure query annotations.
        // Look for a @NamedPLSQLStoredProcedureQueries.
        MetadataAnnotation namedPLSQLStoredProcedureQueries = getAnnotation(NamedPLSQLStoredProcedureQueries.class);
        if (namedPLSQLStoredProcedureQueries != null) {
            for (Object namedPLSQLStoredProcedureQuery : (Object[]) namedPLSQLStoredProcedureQueries.getAttribute("value")) { 
                getProject().addQuery(new NamedPLSQLStoredProcedureQueryMetadata((MetadataAnnotation) namedPLSQLStoredProcedureQuery, this));
            }
        }
        
        // Look for a @NamedPLSQLStoredProcedureQuery.
        MetadataAnnotation namedPLSQLStoredProcedureQuery = getAnnotation(NamedPLSQLStoredProcedureQuery.class);
        if (namedPLSQLStoredProcedureQuery != null) {
            getProject().addQuery(new NamedPLSQLStoredProcedureQueryMetadata(namedPLSQLStoredProcedureQuery, this));
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
                getProject().addQuery(new NamedQueryMetadata((MetadataAnnotation) namedQuery, this));
            }
        }
        
        // Look for a @NamedQuery.
        MetadataAnnotation namedQuery = getAnnotation(NamedQuery.class);
        if (namedQuery != null) {
            getProject().addQuery(new NamedQueryMetadata(namedQuery, this));
        }
    }
    
    /**
     * INTERNAL:
     * Process/collect the named stored procedure queries on this accessor and 
     * add them to the project for later processing.
     */
    protected void processNamedStoredFunctionQueries() {
        // Process the XML named stored function queries first.
        for (NamedStoredFunctionQueryMetadata namedStoredFunctionQuery : m_namedStoredFunctionQueries) {
            getProject().addQuery(namedStoredFunctionQuery);
        }
        
        // Process the named stored function query annotations.
        // Look for a @NamedStoredFunctionQueries.
        MetadataAnnotation namedStoredFunctionQueries = getAnnotation(NamedStoredFunctionQueries.class);
        if (namedStoredFunctionQueries != null) {
            for (Object namedStoredFunctionQuery : (Object[]) namedStoredFunctionQueries.getAttribute("value")) { 
                getProject().addQuery(new NamedStoredFunctionQueryMetadata((MetadataAnnotation) namedStoredFunctionQuery, this));
            }
        }
        
        // Look for a @NamedStoredFunctionQuery.
        MetadataAnnotation namedStoredFunctionQuery = getAnnotation(NamedStoredFunctionQuery.class);
        if (namedStoredFunctionQuery != null) {
            getProject().addQuery(new NamedStoredFunctionQueryMetadata(namedStoredFunctionQuery, this));
        }
    }
    
    /**
     * INTERNAL:
     * Process/collect the named stored function queries on this accessor and 
     * add them to the project for later processing.
     */
    protected void processNamedStoredProcedureQueries() {
        // Process the XML named stored procedure queries first.
        for (NamedStoredProcedureQueryMetadata namedStoredProcedureQuery : m_namedStoredProcedureQueries) {
            getProject().addQuery(namedStoredProcedureQuery);
        }
        
        // Process the JPA named stored procedure query annotations.
        // Look for a JPA @NamedStoredProcedureQueries.
        MetadataAnnotation jpaNamedStoredProcedureQueries = getAnnotation(NamedStoredProcedureQueries.class);
        if (jpaNamedStoredProcedureQueries != null) {
            for (Object jpaNamedStoredProcedureQuery : (Object[]) jpaNamedStoredProcedureQueries.getAttribute("value")) { 
                getProject().addQuery(new NamedStoredProcedureQueryMetadata((MetadataAnnotation) jpaNamedStoredProcedureQuery, this));
            }
        }
        
        // Look for a JPA @NamedStoredProcedureQuery.
        MetadataAnnotation jpaNamedStoredProcedureQuery = getAnnotation(NamedStoredProcedureQuery.class);
        if (jpaNamedStoredProcedureQuery != null) {
            getProject().addQuery(new NamedStoredProcedureQueryMetadata(jpaNamedStoredProcedureQuery, this));
        }
        
        // Process the named stored procedure query annotations.
        // Look for a @NamedStoredProcedureQueries.
        MetadataAnnotation namedStoredProcedureQueries = getAnnotation(org.eclipse.persistence.annotations.NamedStoredProcedureQueries.class);
        if (namedStoredProcedureQueries != null) {
            for (Object namedStoredProcedureQuery : (Object[]) namedStoredProcedureQueries.getAttribute("value")) { 
                getProject().addQuery(new NamedStoredProcedureQueryMetadata((MetadataAnnotation) namedStoredProcedureQuery, this));
            }
        }
        
        // Look for a @NamedStoredProcedureQuery.
        MetadataAnnotation namedStoredProcedureQuery = getAnnotation(org.eclipse.persistence.annotations.NamedStoredProcedureQuery.class);
        if (namedStoredProcedureQuery != null) {
            getProject().addQuery(new NamedStoredProcedureQueryMetadata(namedStoredProcedureQuery, this));
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
                    new OptimisticLockingMetadata(optimisticLocking, this).process(getDescriptor());
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
        if (m_primaryKey != null || isAnnotationPresent(PrimaryKey.class)) {
            if (getDescriptor().hasPrimaryKey()) {
                // Ignore primary key on mapped superclass if primary key 
                // metadata is already defined on the entity.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_PRIMARY_KEY, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_primaryKey == null) {
                    new PrimaryKeyMetadata(getAnnotation(PrimaryKey.class), this).process(getDescriptor());
                } else {                    
                    if (isAnnotationPresent(PrimaryKey.class)) {
                        getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, getAnnotation(PrimaryKey.class), getJavaClassName(), getLocation());
                    }
                    
                    m_primaryKey.process(getDescriptor());
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
        // Process the xml defined sequence generator first.
        if (m_sequenceGenerator != null) {
            getProject().addSequenceGenerator(m_sequenceGenerator, getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
        }
        
        if (isAnnotationPresent(SequenceGenerator.class)) {
            // Ask the common processor to process what we found.
            getProject().addSequenceGenerator(new SequenceGeneratorMetadata(getAnnotation(SequenceGenerator.class), this), getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
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
                getProject().addSQLResultSetMapping(new SQLResultSetMappingMetadata((MetadataAnnotation)sqlResultSetMapping, this));
            }
        } else {
            // Look for a @SqlResultSetMapping.
            MetadataAnnotation sqlResultSetMapping = getAnnotation(SqlResultSetMapping.class);
            
            if (sqlResultSetMapping != null) {
                getProject().addSQLResultSetMapping(new SQLResultSetMappingMetadata(sqlResultSetMapping, this));
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
            getProject().addTableGenerator(new TableGeneratorMetadata(getAnnotation(TableGenerator.class), this), getDescriptor().getDefaultCatalog(), getDescriptor().getDefaultSchema());
        }
    }
    
    /**
     * INTERNAL:
     * Process a TableGenerator annotation into a common metadata table 
     * generator and add it to the project.
     */
    protected void processUuidGenerator() {
        // Process the xml defined table generator first.
        if (m_uuidGenerator != null) {
            getProject().addUuidGenerator(m_uuidGenerator);
        }
        
        if (isAnnotationPresent(UuidGenerator.class)) {
            getProject().addUuidGenerator(new UuidGeneratorMetadata(getAnnotation(UuidGenerator.class), this));
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAdditionalCriteria(AdditionalCriteriaMetadata additionalCriteria) {
        m_additionalCriteria = additionalCriteria;
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
    public void setCacheIndexes(List<CacheIndexMetadata> indexes) {
        m_cacheIndexes = indexes;
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
    public void setExcludeDefaultListeners(Boolean excludeDefaultListeners) {
        m_excludeDefaultListeners = excludeDefaultListeners;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping
     */
    public void setExcludeSuperclassListeners(Boolean excludeSuperclassListeners) {
        m_excludeSuperclassListeners = excludeSuperclassListeners;
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
    public void setMultitenant(MultitenantMetadata multitenant) {
        m_multitenant = multitenant;
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
    public void setNamedPLSQLStoredFunctionQueries(List<NamedPLSQLStoredFunctionQueryMetadata> namedPLSQLStoredFunctionQueries) {
        m_namedPLSQLStoredFunctionQueries = namedPLSQLStoredFunctionQueries;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setNamedPLSQLStoredProcedureQueries(List<NamedPLSQLStoredProcedureQueryMetadata> namedPLSQLStoredProcedureQueries) {
        m_namedPLSQLStoredProcedureQueries = namedPLSQLStoredProcedureQueries;
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
    public void setNamedStoredFunctionQueries(List<NamedStoredFunctionQueryMetadata> namedStoredFunctionQueries) {
        m_namedStoredFunctionQueries = namedStoredFunctionQueries;
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
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setUuidGenerator(UuidGeneratorMetadata uuidGenerator) {
        m_uuidGenerator = uuidGenerator;
    }
}

