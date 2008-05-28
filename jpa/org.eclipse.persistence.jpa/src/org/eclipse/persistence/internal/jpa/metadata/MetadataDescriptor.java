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
package org.eclipse.persistence.internal.jpa.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.annotations.ExistenceType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.ReturningPolicy;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;

import org.eclipse.persistence.internal.jpa.CMP3Policy;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.CollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.RelationshipAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;

import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListener;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * INTERNAL:
 * Common metatata descriptor for the annotation and xml processors. This class
 * is a wrap on an actual EclipseLink descriptor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataDescriptor {
    // Access types
    private static final String FIELD = "FIELD";
    private static final String PROPERTY = "PROPERTY";
    
    private Class m_javaClass;
    private ClassAccessor m_accessor;
    private ClassDescriptor m_descriptor;
    private DatabaseTable m_primaryTable;
    private Enum m_existenceChecking;
    
    // This is the parent class that defines the inheritance strategy, and
    // not necessarily the immediate parent class.
    private MetadataDescriptor m_inheritanceParentDescriptor;
    
    private boolean m_isCascadePersist;
    private boolean m_ignoreAnnotations; // XML metadata complete
    private boolean m_ignoreDefaultMappings; // XML exclude default mappings
    private boolean m_hasCache;
    private boolean m_hasChangeTracking;
    private boolean m_hasCustomizer;
    private boolean m_hasReadOnly;
    private boolean m_hasCopyPolicy;
    private Boolean m_usesPropertyAccess;
    private Boolean m_usesCascadedOptimisticLocking;
    
    private String m_xmlAccess;
    private String m_xmlSchema;
    private String m_xmlCatalog;
    private String m_embeddedIdAttributeName;
    
    private List<String> m_idAttributeNames;
    private List<String> m_orderByAttributeNames;
    private List<String> m_idOrderByAttributeNames;
    private List<MetadataDescriptor> m_embeddableDescriptors;
    private List<RelationshipAccessor> m_relationshipAccessors;
    private List<BasicCollectionAccessor> m_basicCollectionAccessors;
    
    private Map<String, Type> m_pkClassIDs;
    private Map<String, MappingAccessor> m_accessors;
    private Map<String, PropertyMetadata> m_properties;
    private Map<String, String> m_pkJoinColumnAssociations;
    private Map<String, AttributeOverrideMetadata> m_attributeOverrides;
    private Map<String, AssociationOverrideMetadata> m_associationOverrides;
    private Map<String, Map<String, MetadataAccessor>> m_biDirectionalManyToManyAccessors;
    
    /**
     * INTERNAL: 
     */
    public MetadataDescriptor(Class javaClass) {
        m_xmlAccess = null;
        m_xmlSchema = null;
        m_xmlCatalog = null;
        
        m_inheritanceParentDescriptor = null;
        
        m_hasCache = false;
        m_hasChangeTracking = false;
        m_hasCustomizer = false;
        m_hasReadOnly = false;
        m_isCascadePersist = false;
        m_ignoreAnnotations = false;
        m_ignoreDefaultMappings = false;
        
        m_idAttributeNames = new ArrayList<String>();
        m_orderByAttributeNames = new ArrayList<String>();
        m_idOrderByAttributeNames = new ArrayList<String>();
        m_embeddableDescriptors = new ArrayList<MetadataDescriptor>();
        m_relationshipAccessors = new ArrayList<RelationshipAccessor>();
        m_basicCollectionAccessors = new ArrayList<BasicCollectionAccessor>();
        
        m_pkClassIDs = new HashMap<String, Type>();
        m_accessors = new HashMap<String, MappingAccessor>();
        m_properties = new HashMap<String, PropertyMetadata>();
        m_pkJoinColumnAssociations = new HashMap<String, String>();
        m_attributeOverrides = new HashMap<String, AttributeOverrideMetadata>();
        m_associationOverrides = new HashMap<String, AssociationOverrideMetadata>();
        m_biDirectionalManyToManyAccessors = new HashMap<String, Map<String, MetadataAccessor>>();
        
        m_descriptor = new RelationalDescriptor();
        m_descriptor.setAlias("");
        
        // This is the default, set it in case no existence-checking is set.
        m_descriptor.getQueryManager().checkDatabaseForDoesExist();
                
        setJavaClass(javaClass);
    }
    
    /**
     * INTERNAL: 
     */
    public MetadataDescriptor(Class javaClass, ClassAccessor classAccessor) {
        this(javaClass);
        setClassAccessor(classAccessor);
    }
     
     /**
      * INTERNAL:
      */
    public void addAccessor(MappingAccessor accessor) {
        m_accessors.put(accessor.getAttributeName(), accessor);
    }
    
    /**
     * INTERNAL:
     */
     public void addAssociationOverride(AssociationOverrideMetadata associationOverride) {
        m_associationOverrides.put(associationOverride.getName(), associationOverride);   
     }
    
    /**
     * INTERNAL:
     */
    public void addAttributeOverride(AttributeOverrideMetadata attributeOverride) {
        m_attributeOverrides.put(attributeOverride.getName(), attributeOverride);
    }

    /**
     * INTERNAL:
     * Store basic collection accessors for later processing and quick look up.
     */
    public void addBasicCollectionAccessor(MetadataAccessor accessor) {
        m_basicCollectionAccessors.add((BasicCollectionAccessor) accessor);
    }
    
    /**
     * INTERNAL:
     */
    public void addClassIndicator(Class entityClass, String value) {
        if (isInheritanceSubclass()) {
            getInheritanceParentDescriptor().addClassIndicator(entityClass, value);   
        } else {
            m_descriptor.getInheritancePolicy().addClassNameIndicator(entityClass.getName(), value);
        }
    }
    
    /** 
     * INTERNAL:
     */
    public void addDefaultEventListener(EntityListener listener) {
        m_descriptor.getEventManager().addDefaultEventListener(listener);
    }
    
    /**
     * INTERNAL:
     */
    public void addEmbeddableDescriptor(MetadataDescriptor embeddableDescriptor) {
        m_embeddableDescriptors.add(embeddableDescriptor);
    }
    
    /**
     * INTERNAL:
     */
    public void addEntityListenerEventListener(EntityListener listener) {
        m_descriptor.getEventManager().addEntityListenerEventListener(listener);
    }

    /**
     * INTERNAL:
     */
    public void addFieldForInsert(DatabaseField field) {
        getReturningPolicy().addFieldForInsert(field);
    }
    
    /**
     * INTERNAL:
     */
    public void addFieldForInsertReturnOnly(DatabaseField field) {
        getReturningPolicy().addFieldForInsertReturnOnly(field);
    }
    
    /**
     * INTERNAL:
     */
    public void addFieldForUpdate(DatabaseField field) {
        getReturningPolicy().addFieldForUpdate(field);
    }
    
    /**
     * INTERNAL:
     */
    public void addIdAttributeName(String idAttributeName) {
        m_idAttributeNames.add(idAttributeName);    
    }
    
    /**
     * INTERNAL:
     */
    public void addMapping(DatabaseMapping mapping) {
        m_descriptor.addMapping(mapping);
    }
    
    /**
     * INTERNAL:
     */
    public void addForeignKeyFieldForMultipleTable(DatabaseField fkField, DatabaseField pkField) {
        m_descriptor.addForeignKeyFieldForMultipleTable(fkField, pkField);
        m_pkJoinColumnAssociations.put(fkField.getName(), pkField.getName());
    }
    
    /**
     * INTERNAL:
     * We store these to validate the primary class when processing
     * the entity class.
     */
    public void addPKClassId(String attributeName, Type type) {
        m_pkClassIDs.put(attributeName, type);
    }
    
    /**
     * INTERNAL:
     * Add a property to the descriptor. Will check for an override/ignore case.
     */
    public void addProperty(PropertyMetadata property) {
        if (property.shouldOverride(m_properties.get(property.getName()))) {
            m_properties.put(property.getName(), property);
            m_descriptor.getProperties().put(property.getName(), property.getConvertedValue());
        }
    }
    
    /**
     * INTERNAL:
     */
    public void addPrimaryKeyField(DatabaseField field) {
        m_descriptor.addPrimaryKeyField(field);
    }
    
    /**
      * INTERNAL:
      * Store relationship accessors for later processing and quick look up.
      */
    public void addRelationshipAccessor(MetadataAccessor accessor) {
        m_relationshipAccessors.add((RelationshipAccessor) accessor);
        
        // Store bidirectional ManyToMany relationships so that we may look at 
        // attribute names when defaulting join columns.
        if (accessor.isManyToMany()) {
            String mappedBy = ((ManyToManyAccessor) accessor).getMappedBy();
            
            if (mappedBy != null && ! mappedBy.equals("")) {
                String referenceClassName = ((ManyToManyAccessor) accessor).getReferenceClassName();
                
                // Initialize the map of bi-directional mappings for this class.
                if (! m_biDirectionalManyToManyAccessors.containsKey(referenceClassName)) {
                    m_biDirectionalManyToManyAccessors.put(referenceClassName, new HashMap<String, MetadataAccessor>());
                }
            
                m_biDirectionalManyToManyAccessors.get(referenceClassName).put(mappedBy, accessor);
            }
        }
        
        m_accessor.getProject().addAccessorWithRelationships(m_accessor);
    }
    
    /**
     * INTERNAL:
     */
    public void addTable(DatabaseTable table) {
        m_descriptor.addTable(table);
    }
    
    /**
     * INTERNAL:
     */
    public boolean excludeSuperclassListeners() {
        return m_descriptor.getEventManager().excludeSuperclassListeners();
    }
    
    /**
     * INTERNAL:
     * This method will first check for an accessor with name equal to 
     * fieldOrPropertyName (that is, assumes it is a field name). If no accessor
     * is found than it assumes fieldOrPropertyName is a property name and 
     * converts it to its corresponding field name and looks for the accessor
     * again. If still no accessor is found and this descriptor metadata is
     * and an inheritance subclass, than it will then look on the root metadata
     * descriptor. Null is returned otherwise.
     */
    public MappingAccessor getAccessorFor(String fieldOrPropertyName) {
        MappingAccessor accessor = m_accessors.get(fieldOrPropertyName);
        
        if (accessor == null) {
            // Perhaps we have a property name ...
            accessor = m_accessors.get(MetadataMethod.getAttributeNameFromMethodName(fieldOrPropertyName));
           
            // If still no accessor and we are an inheritance subclass, check 
            // the root descriptor now.
            if (accessor == null && isInheritanceSubclass()) {
                accessor = getInheritanceParentDescriptor().getAccessorFor(fieldOrPropertyName);
            }
        }
        
        return accessor;
    }
    
    /**
     * INTERNAL:
     */
    public String getAlias() {
        return m_descriptor.getAlias();
    }
    
    /**
     * INTERNAL:
     */
    public AssociationOverrideMetadata getAssociationOverrideFor(String attributeName) {
        return m_associationOverrides.get(attributeName);
    }
    
    /**
     * INTERNAL:
     */
    public AttributeOverrideMetadata getAttributeOverrideFor(String attributeName) {
        return m_attributeOverrides.get(attributeName);
    }

    /**
     * INTERNAL:
     */
    public List<BasicCollectionAccessor> getBasicCollectionAccessors() {
        return m_basicCollectionAccessors;
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getClassIndicatorField() {
        if (isInheritanceSubclass()) {
            return getInheritanceParentDescriptor().getClassDescriptor().getInheritancePolicy().getClassIndicatorField();
        } else {
            if (getClassDescriptor().hasInheritance()) {
                return getClassDescriptor().getInheritancePolicy().getClassIndicatorField();
            } else {
                return null;
            }
        }
    }
    
    /**
     * INTERNAL:
     * The default table name is the descriptor alias, unless this descriptor 
     * metadata is an inheritance subclass with a SINGLE_TABLE strategy. Then 
     * it is the table name of the root descriptor metadata.
     */
    public String getDefaultTableName() {
        String defaultTableName = getAlias().toUpperCase();
        
        if (isInheritanceSubclass()) {    
            if (getInheritanceParentDescriptor().usesSingleTableInheritanceStrategy()) {
                defaultTableName = getInheritanceParentDescriptor().getPrimaryTableName();
            }
        }
        
        return defaultTableName;
    }
    
    /**
     * INTERNAL:
     */
    public ClassAccessor getClassAccessor() {
        return m_accessor;
    }
    
    /**
     * INTERNAL:
     */
    public ClassDescriptor getClassDescriptor() {
        return m_descriptor;
    }
    
    /**
     * INTERNAL:
     */
    public String getEmbeddedIdAttributeName() {
        return m_embeddedIdAttributeName;
    }
    
    /**
     * INTERNAL:
     * Return the primary key attribute name for this entity.
     */
    public String getIdAttributeName() {
        if (getIdAttributeNames().isEmpty()) {
            if (isInheritanceSubclass()) {
                return getInheritanceParentDescriptor().getIdAttributeName();
            } else {
                return "";
            }
        } else {
            return getIdAttributeNames().get(0);
        }
    }
    
    /**
     * INTERNAL:
     * Return the id attribute names declared on this descriptor metadata.
     */
    public List<String> getIdAttributeNames() {
        return m_idAttributeNames;
    }
    
    /**
     * INTERNAL:
     * Return the primary key attribute names for this entity. If there are no
     * id attribute names set then we are either:
     * 1) an inheritance subclass, get the id attribute names from the root
     *    of the inheritance structure.
     * 2) we have an embedded id. Get the id attribute names from the embedded
     *    descriptor metadata, which is equal the attribute names of all the
     *    direct to field mappings on that descriptor metadata. Currently does
     *    not traverse nested embeddables.
     */
    public List<String> getIdOrderByAttributeNames() {
        if (m_idOrderByAttributeNames.isEmpty()) {
            if (m_idAttributeNames.isEmpty()) {
                if (isInheritanceSubclass()) {  
                    // Get the id attribute names from our root parent.
                    m_idOrderByAttributeNames = getInheritanceParentDescriptor().getIdAttributeNames();
                } else {
                    // We must have a composite primary key as a result of an embedded id.
                    m_idOrderByAttributeNames = ((MappingAccessor) getAccessorFor(getEmbeddedIdAttributeName())).getReferenceDescriptor().getOrderByAttributeNames();
                } 
            } else {
                m_idOrderByAttributeNames = m_idAttributeNames;
            }
        }
            
        return m_idOrderByAttributeNames;
    }
    
    
    /**
     * INTERNAL:
     * Assumes hasBidirectionalManyToManyAccessorFor has been called before
     * hand. 
     */
     public MetadataAccessor getBiDirectionalManyToManyAccessor(String className, String attributeName) {
        return m_biDirectionalManyToManyAccessors.get(className).get(attributeName);
    }
    
    /**
     * INTERNAL:
     * This will return the attribute names for all the direct to field mappings 
     * on this descriptor metadata. This method will typically be called when an 
     * @Embedded or @EmbeddedId attribute has been specified in an @OrderBy.
     */
    public List<String> getOrderByAttributeNames() {
        if (m_orderByAttributeNames.isEmpty()) {
            for (DatabaseMapping mapping : getMappings()) {
                if (mapping.isDirectToFieldMapping()) {
                    m_orderByAttributeNames.add(mapping.getAttributeName());
                }
            }
        }
        
        return m_orderByAttributeNames;
    }

    /**
     * INTERNAL:
     */
    public Class getJavaClass() {
        return m_javaClass;
    }
    
    /**
     * INTERNAL:
     */
    public String getJavaClassName() {
        return m_descriptor.getJavaClassName();
    }
    
    /**
     * INTERNAL:
     */
    public MetadataLogger getLogger() {
        return getProject().getLogger();
    }
    
    /** 
     * INTERNAL:
     */
    public MetadataDescriptor getInheritanceParentDescriptor() {
        return m_inheritanceParentDescriptor;
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseMapping getMappingForAttributeName(String attributeName) {
        return getMappingForAttributeName(attributeName, null);
    } 
    
    /**
     * INTERNAL: 
     * Non-owning mappings that need to look up the owning mapping, should call 
     * this method with their respective accessor to check for circular mappedBy 
     * references. If the referencingAccessor is null, no check will be made.
     */
    public DatabaseMapping getMappingForAttributeName(String attributeName, MetadataAccessor referencingAccessor) {
        MetadataAccessor accessor = getAccessorFor(attributeName);
        
        if (accessor != null) {
            // If the accessor is a relationship accessor than it may or may
            // not have been processed yet. Fast track its processing if it
            // needs to be. The process call will do nothing if it has already
            // been processed.
            if (accessor.isRelationship()) {
                RelationshipAccessor relationshipAccessor = (RelationshipAccessor) accessor;
                
                // Check that we don't have circular mappedBy values which 
                // will cause an infinite loop.
                if (referencingAccessor != null && (relationshipAccessor.isOneToOne() || relationshipAccessor.isCollectionAccessor())) {
                    String mappedBy = null;
                    
                    if (relationshipAccessor.isOneToOne()) {
                        mappedBy = ((OneToOneAccessor) relationshipAccessor).getMappedBy();
                    } else {
                        mappedBy = ((CollectionAccessor) relationshipAccessor).getMappedBy();
                    }
                    
                    if (mappedBy != null && mappedBy.equals(referencingAccessor.getAttributeName())) {
                        throw ValidationException.circularMappedByReferences(referencingAccessor.getJavaClass(), referencingAccessor.getAttributeName(), getJavaClass(), attributeName);
                    }
                }
                
                relationshipAccessor.processRelationship();
            }
            
            return m_descriptor.getMappingForAttributeName(attributeName);
        }
        
        // We didn't find a mapping on this descriptor, check our aggregate 
        // descriptors now.
        for (MetadataDescriptor embeddableDescriptor : m_embeddableDescriptors) {
            DatabaseMapping mapping = embeddableDescriptor.getMappingForAttributeName(attributeName, referencingAccessor);
            
            if (mapping != null) {
                return mapping;
            }
        }
        
        // We didn't find a mapping on the aggregate descriptors. If we are an
        // inheritance subclass, check for a mapping on the inheritance root
        // descriptor metadata.
        if (isInheritanceSubclass()) {
            return getInheritanceParentDescriptor().getMappingForAttributeName(attributeName, referencingAccessor);
        }
        
        // Found nothing ... return null.
        return null;
    } 
    
    /**
     * INTERNAL:
     */
    public List<DatabaseMapping> getMappings() {
        return m_descriptor.getMappings();
    }
    
    /**
     * INTERNAL:
     */
    public String getPKClassName() {
        String pkClassName = null;
        
        if (m_descriptor.hasCMPPolicy()) {
            pkClassName = ((CMP3Policy) m_descriptor.getCMPPolicy()).getPKClassName();    
        }
        
        return pkClassName;
    }
    
    /**
     * INTERNAL:
     * Method to return the primary key field name for the given descriptor
     * metadata. Assumes there is one.
     */
    public String getPrimaryKeyFieldName() {
        return (getPrimaryKeyFields().iterator().next()).getName();
    }
    
    /**
     * INTERNAL:
     * Method to return the primary key field names for the given descriptor
     * metadata. getPrimaryKeyFieldNames() on ClassDescriptor returns qualified
     * names. We don't want that.
     */
    public List<String> getPrimaryKeyFieldNames() {
        List<DatabaseField> primaryKeyFields = getPrimaryKeyFields();
        List<String> primaryKeyFieldNames = new ArrayList<String>(primaryKeyFields.size());
        
        for (DatabaseField primaryKeyField : primaryKeyFields) {
            primaryKeyFieldNames.add(primaryKeyField.getName());
        }
        
        return primaryKeyFieldNames;
    }
    
    /**
     * INTERNAL:
     * Return the primary key fields for this descriptor metadata. If this is
     * an inheritance subclass and it has no primary key fields, then grab the 
     * primary key fields from the root.
     */
    public List<DatabaseField> getPrimaryKeyFields() {
        List<DatabaseField> primaryKeyFields = m_descriptor.getPrimaryKeyFields();
        
        if (primaryKeyFields.isEmpty() && isInheritanceSubclass()) {
            primaryKeyFields = getInheritanceParentDescriptor().getPrimaryKeyFields();
        }
        
        return primaryKeyFields;
    }

    /**
     * INTERNAL:
     * Recursively check the potential chaining of the primary key fields from 
     * a inheritance subclass, all the way to the root of the inheritance 
     * hierarchy.
     */
    public String getPrimaryKeyJoinColumnAssociation(String foreignKeyName) {
        String primaryKeyName = m_pkJoinColumnAssociations.get(foreignKeyName);

        if (primaryKeyName == null || ! isInheritanceSubclass()) {
            return foreignKeyName;
        } else {
            return getInheritanceParentDescriptor().getPrimaryKeyJoinColumnAssociation(primaryKeyName);
        } 
    }
    
    /**
     * INTERNAL:
     * Assumes there is one primary key field set. This method should be called 
     * when qualifying any primary key field (from a join column) for this 
     * descriptor. This method was created because in an inheritance hierarchy 
     * with a joined strategy we can't use getPrimaryTableName() since it would
     * return the wrong table name. From the spec, the primary key must be 
     * defined on the entity that is the root of the entity hierarchy or on a 
     * mapped superclass of the entity hierarchy. The primary key must be 
     * defined exactly once in an entity hierarchy.
     */
    public DatabaseTable getPrimaryKeyTable() {
        return ((getPrimaryKeyFields().iterator().next())).getTable();
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseTable getPrimaryTable() {
        if (m_primaryTable == null && isInheritanceSubclass()) {
            return getInheritanceParentDescriptor().getPrimaryTable();
        } else {
            if (m_descriptor.isAggregateDescriptor()) {
                // Aggregate descriptors don't have tables, just return a 
                // a default empty table.
                return new DatabaseTable();
            }
            
            return m_primaryTable;
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getPrimaryTableName() {
        return getPrimaryTable().getName();
    }

    /**
     * INTERNAL:
     */
    public MetadataProject getProject() {
        return getClassAccessor().getProject();
    }
    
    /**
     * INTERNAL:
     */
    public List<RelationshipAccessor> getRelationshipAccessors() {
        return m_relationshipAccessors;
    }
    
    /**
     * INTERNAL:
     */
    protected ReturningPolicy getReturningPolicy() {
        if (! m_descriptor.hasReturningPolicy()) {
            m_descriptor.setReturningPolicy(new ReturningPolicy());
        }
        
        return m_descriptor.getReturningPolicy();
    }
    
    /**
     * INTERNAL:
     */
    public DatabaseField getSequenceNumberField() {
        return m_descriptor.getSequenceNumberField();
    }
    
    /**
     * INTERNAL:
     */
    public String getXMLAccess() {
        return m_xmlAccess;
    }
    
    /**
     * INTERNAL:
     */
    public String getXMLCatalog() {
        return m_xmlCatalog;
    }
    
    /**
     * INTERNAL:
     */
    public String getXMLSchema() {
        return m_xmlSchema;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAssociationOverrideFor(String attributeName) {
        return m_associationOverrides.containsKey(attributeName);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasAttributeOverrideFor(String attributeName) {
        return m_attributeOverrides.containsKey(attributeName);
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasCompositePrimaryKey() {
        return getPrimaryKeyFields().size() > 1 || getPKClassName() != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasEmbeddedIdAttribute() {
        return m_embeddedIdAttributeName != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasExistenceChecking() {
        return m_existenceChecking != null;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasInheritance() {
        return m_descriptor.hasInheritance();
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasBiDirectionalManyToManyAccessorFor(String className, String attributeName) {
        if (m_biDirectionalManyToManyAccessors.containsKey(className)) {
            return m_biDirectionalManyToManyAccessors.get(className).containsKey(attributeName);
        }
        
        return false;
    }

    /**
     * INTERNAL:
     * Indicates that a Cache annotation or cache element has already been 
     * processed for this descriptor.
     */
    public boolean hasCache() {
        return m_hasCache;
    }
    
    /**
     * INTERNAL:
     * Indicates that a Change tracking annotation or change tracking element 
     * has already been processed for this descriptor.
     */
    public boolean hasChangeTracking() {
        return m_hasChangeTracking;
    }
    
    /**
     * INTERNAL:
     * Indicates that a copy Policy annotation or copy policy element 
     * has already been processed for this descriptor.
     */
    public boolean hasCopyPolicy() {
        return m_hasCopyPolicy;
    }

    /**
     * INTERNAL:
     * Indicates that a customizer annotation or customizer element has already 
     * been processed for this descriptor.
     */
    public boolean hasCustomizer() {
        return m_hasCustomizer;
    }
    
    /**
     * INTERNAL:
     * Indicates that a read only annotation or read only element has already 
     * been processed for this descriptor.
     */
    public boolean hasReadOnly() {
        return m_hasReadOnly;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean havePersistenceAnnotationsDefined(Field[] fields) {
        for (Field field : fields) {
            MetadataField metadataField = new MetadataField(field, getLogger());
            
            if (metadataField.hasDeclaredAnnotations(this)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    protected boolean havePersistenceAnnotationsDefined(Method[] methods) {
        for (Method method : methods) {
            MetadataMethod metadataMethod = new MetadataMethod(method, getLogger());
            
            if (metadataMethod.hasDeclaredAnnotations(this)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasMappingForAttributeName(String attributeName) {
        return m_descriptor.getMappingForAttributeName(attributeName) != null;
    }
    
    /**
     * INTERNAL:
     * Return true is the descriptor has primary key fields set.
     */
    public boolean hasPrimaryKeyFields() {
        return m_descriptor.getPrimaryKeyFields().size() > 0;
    }
 
    /**
     * INTERNAL:
     * Indicates whether or not annotations should be ignored. However, default 
     * mappings will still be processed unless an exclude-default-mappings 
     * setting is specified.
     * @see ignoreDefaultMappings()
     */
    public boolean ignoreAnnotations() {
        return m_ignoreAnnotations;
    }
    
    /**
     * INTERNAL:
     * Indicates whether or not default mappings should be ignored.
     */
    public boolean ignoreDefaultMappings() {
        return m_ignoreDefaultMappings;
    }
    
    /**
     * INTERNAL:
     * Indicates that cascade-persist should be applied to all relationship 
     * mappings for this entity.
     */
    public boolean isCascadePersist() {
        return m_isCascadePersist;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isEmbeddable() {
        return m_descriptor.isAggregateDescriptor();
    }
    
    /**
     * INTERNAL:
     */
    public boolean isEmbeddableCollection() {
        return m_descriptor.isAggregateCollectionDescriptor();
    }
    
    /**
     * INTERNAL:
     */
    public boolean isInheritanceSubclass() {
        return m_inheritanceParentDescriptor != null;
    }
    
    /**
     * INTERNAL:
     * Indicates that we found an XML field access type for this metadata
     * descriptor.
     */
    protected boolean isXmlFieldAccess() {
        return m_xmlAccess != null && m_xmlAccess.equals(FIELD);
    }
    
    /**
     * INTERNAL:
     * Indicates that we found an XML property access type for this metadata
     * descriptor.
     */
    protected boolean isXmlPropertyAccess() {
        return m_xmlAccess != null && m_xmlAccess.equals(PROPERTY);
    }
    
    /**
     * INTERNAL:
     */
    public boolean pkClassWasNotValidated() {
        return ! m_pkClassIDs.isEmpty();
    }
    
    /**
     * INTERNAL:
     */
    public void setAlias(String alias) {
        m_descriptor.setAlias(alias);
    }
            
    /**
     * INTERNAL:
     */
    public void setClassAccessor(ClassAccessor accessor) {
        m_accessor = accessor;
        accessor.setDescriptor(this);
    }
    
    /**
     * INTERNAL:
     */
    public void setClassIndicatorField(DatabaseField field) {
        m_descriptor.getInheritancePolicy().setClassIndicatorField(field);    
    }
    
    /**
     * INTERNAL:
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        m_descriptor = descriptor;
    }
    
    /**
     * INTERNAL:
     */
    public void setEmbeddedIdAttributeName(String embeddedIdAttributeName) {
        m_embeddedIdAttributeName = embeddedIdAttributeName;
    }
    
    /** 
     * INTERNAL:
     */
    public void setEntityEventListener(EntityListener listener) {
        m_descriptor.getEventManager().setEntityEventListener(listener);
    }
    
    /**
     * INTERNAL:
     */
    public void setExcludeDefaultListeners(boolean excludeDefaultListeners) {
        m_descriptor.getEventManager().setExcludeDefaultListeners(excludeDefaultListeners);
    }
    
    /**
     * INTERNAL:
     */
    public void setExcludeSuperclassListeners(boolean excludeSuperclassListeners) {
        m_descriptor.getEventManager().setExcludeSuperclassListeners(excludeSuperclassListeners);
    }
    
    /**
     * INTERNAL:
     */
    public void setExistenceChecking(Enum existenceChecking) {
        m_existenceChecking = existenceChecking;
        
        if (existenceChecking.equals(ExistenceType.CHECK_CACHE)) {
            m_descriptor.getQueryManager().checkCacheForDoesExist();
        } else if (existenceChecking.equals(ExistenceType.CHECK_DATABASE)) {
            m_descriptor.getQueryManager().checkDatabaseForDoesExist();
        } else if (existenceChecking.equals(ExistenceType.ASSUME_EXISTENCE)) {
            m_descriptor.getQueryManager().assumeExistenceForDoesExist();
        } else if (existenceChecking.equals(ExistenceType.ASSUME_NON_EXISTENCE)) {
            m_descriptor.getQueryManager().assumeNonExistenceForDoesExist();
        }
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a cache annotation or cache xml element.
     */
    public void setHasCache() {
        m_hasCache = true;
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a change tracking annotation or change
     * tracking xml element.
     */
    public void setHasChangeTracking() {
        m_hasChangeTracking = true;
    }
    
    /**
     * INTERNAL:
     * Indicates that we have processed a copy policy annotation or copy policy xml element.
     */
    public void setHasCopyPolicy() {
        m_hasCopyPolicy = true;
    }
    
    /**
     * INTERNAL:
     * Indicates that all annotations should be ignored. However, default 
     * mappings will still be processed unless an exclude-default-mappings 
     * setting is specified.
     * @see setIgnoreDefaultMappings()
     */
    public void setIgnoreAnnotations(boolean ignoreAnnotations) {
        m_ignoreAnnotations = ignoreAnnotations;
    }
    
    /**
     * INTERNAL:
     * Indicates that default mappings should be ignored.
     */
    public void setIgnoreDefaultMappings(boolean ignoreDefaultMappings) {
        m_ignoreDefaultMappings = ignoreDefaultMappings;
    }
    
    /**
     * INTERNAL:
     * Store the root class of an inheritance hierarchy.
     */
    public void setInheritanceParentDescriptor(MetadataDescriptor inheritanceParentDescriptor) {
        m_inheritanceParentDescriptor = inheritanceParentDescriptor;
    }
    
    /**
     * INTERNAL:
     * Indicates that cascade-persist should be added to the set of cascade 
     * values for all relationship mappings.
     */
    public void setIsCascadePersist(boolean isCascadePersist) {
        m_isCascadePersist = isCascadePersist;
    }
    
    /**
     * INTERNAL:
     */
    public void setIsEmbeddable() {
        m_descriptor.descriptorIsAggregate();
    }
    
    /**
     * INTERNAL:
     * Used to set this descriptors java class. 
     */
    public void setJavaClass(Class javaClass) {
        m_javaClass = javaClass;
        m_descriptor.setJavaClassName(javaClass.getName());
        
        // If the javaClass is an interface, add it to the java interface name
        // on the relational descriptor.
        if (javaClass.isInterface()) {
            m_descriptor.setJavaInterfaceName(javaClass.getName());
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setOptimisticLockingPolicy(OptimisticLockingPolicy policy) {
        m_descriptor.setOptimisticLockingPolicy(policy);
    }
    
    /**
     * INTERNAL:
     * Set the inheritance parent class for this class.
     */
    public void setParentClass(Class parent) {
        m_descriptor.getInheritancePolicy().setParentClassName(parent.getName());
    }
    
    /**
     * INTERNAL:
     */
    public void setPKClass(Class pkClass) {
        setPKClass(pkClass.getName());
    }
    
    /**
     * INTERNAL:
     */
    public void setPKClass(String pkClassName) {
        CMP3Policy policy = new CMP3Policy();
        policy.setPrimaryKeyClassName(pkClassName);
        m_descriptor.setCMPPolicy(policy);
    }
    
    /**
     * INTERNAL:
     */
    public void setPrimaryTable(DatabaseTable primaryTable) {
        addTable(primaryTable);
        m_primaryTable = primaryTable;
    }
    
    /**
     * INTERNAL:
     */
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            m_descriptor.setReadOnly();
        }
        
        m_hasReadOnly = true;
    }
    
    /**
     * INTERNAL:
     */
    public void setSequenceNumberField(DatabaseField field) {
        m_descriptor.setSequenceNumberField(field);
    }
    
    /**
     * INTERNAL:
     */
    public void setSequenceNumberName(String name) {
        m_descriptor.setSequenceNumberName(name);
    }
    
    /**
     * INTERNAL:
     * Sets the strategy on the descriptor's inheritance policy to SINGLE_TABLE.  
     * The default is JOINED.
     */
    public void setSingleTableInheritanceStrategy() {
        m_descriptor.getInheritancePolicy().setSingleTableStrategy();
    }
 
    /**
     * INTERNAL:
     */
    public void setUsesCascadedOptimisticLocking(Boolean usesCascadedOptimisticLocking) {
        m_usesCascadedOptimisticLocking = usesCascadedOptimisticLocking;
    }
    
    /**
     * INTERNAL:
     * 
     * Set the access-type while processing a class like Embeddable as it 
     * inherits the access-type from the referencing entity.
     */
    public void setUsesPropertyAccess(Boolean usesPropertyAccess) {
        m_usesPropertyAccess = usesPropertyAccess;
    }
    
    /**
     * INTERNAL:
     */
    public void setXMLAccess(String xmlAccess) {
        m_xmlAccess = xmlAccess;
    }
    
    /**
     * INTERNAL:
     */
    public void setXMLCatalog(String xmlCatalog) {
        m_xmlCatalog = xmlCatalog;
    }
    
    /**
     * INTERNAL:
     */
    public void setXMLSchema(String xmlSchema) {
        m_xmlSchema = xmlSchema;
    }
    
    /**
     * INTERNAL:
     */
    public boolean usesCascadedOptimisticLocking() {
        return m_usesCascadedOptimisticLocking != null && m_usesCascadedOptimisticLocking.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Indicates if the strategy on the descriptor's inheritance policy is 
     * JOINED.
     */
    public boolean usesJoinedInheritanceStrategy() {
        return m_descriptor.getInheritancePolicy().isJoinedStrategy();
    }
    
    /**
     * INTERNAL:
     */
    public boolean usesOptimisticLocking() {
        return m_descriptor.usesOptimisticLocking();
    }
    
    /**
     * INTERNAL:
     * Returns true if this class uses property access. In an inheritance 
     * hierarchy, the subclasses inherit their access type from the parent.
     * The metadata helper method caches the class access types for 
     * efficiency.
     */
    public boolean usesPropertyAccess() {
        if (isInheritanceSubclass()) {
            return getInheritanceParentDescriptor().usesPropertyAccess();
        } else {
            if (m_usesPropertyAccess == null) {
                if (havePersistenceAnnotationsDefined(MetadataHelper.getFields(getJavaClass())) || isXmlFieldAccess()) {
                    // We have persistence annotations defined on a field from 
                    // the entity or field access has been set via XML, set the 
                    // access to FIELD.
                    m_usesPropertyAccess = Boolean.FALSE;
                } else if (havePersistenceAnnotationsDefined(MetadataHelper.getDeclaredMethods(getJavaClass())) || isXmlPropertyAccess()) {
                    // We have persistence annotations defined on a method from 
                    // the entity or method access has been set via XML, set the 
                    // access to PROPERTY.
                    m_usesPropertyAccess = Boolean.TRUE;
                } else {
                    // We found nothing ... we could throw an exception 
                    // here, but for now, set the access to FIELD. The user 
                    // will eventually get an exception saying there is no 
                    // primary key set if field access is not actually the
                    // case.
                    m_usesPropertyAccess = Boolean.FALSE;
                }
            }
        
            return m_usesPropertyAccess;
        }
    }
    
    /**
     * INTERNAL:
     * Indicates if the strategy on the descriptor's inheritance policy is 
     * SINGLE_TABLE.
     */
    public boolean usesSingleTableInheritanceStrategy() {
        return ! usesJoinedInheritanceStrategy();
    }
    
    /**
     * INTERNAL:
     * Return true if this descriptors class processed OptimisticLocking 
     * meta data of type VERSION_COLUMN.
     */
    public boolean usesVersionColumnOptimisticLocking() {
        // If an optimistic locking metadata of type VERSION_COLUMN was not 
        // specified, then m_usesCascadedOptimisticLocking will be null, that 
        // is, we won't have processed the cascade value.
        return m_usesCascadedOptimisticLocking != null;
    }
    
    /**
     * INTERNAL:
     * This method is used only to validate id fields that were found on a
     * pk class were also found on the entity.
     */
    public void validatePKClassId(String attributeName, Type type) {
        if (m_pkClassIDs.containsKey(attributeName))  {
            Type expectedType =  m_pkClassIDs.get(attributeName);
            
            if (type == expectedType) {
                m_pkClassIDs.remove(attributeName);
            } else {
                throw ValidationException.invalidCompositePKAttribute(getJavaClass(), getPKClassName(), attributeName, expectedType, type);
            }
        }
    }
}
