/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.JoinTableMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * INTERNAL:
 * A relational accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class RelationshipAccessor extends MappingAccessor {
    private Boolean m_orphanRemoval; // TODO: Mapped but not processed. Supported on 1-1, 1-M and V1-1. Those accessors are responsible for processing the metadata from their annotations.
    private boolean m_privateOwned;
    private CascadeTypes m_cascadeTypes;
    protected Class m_referenceClass;
    private Class m_targetEntity;
    
    private Enum m_fetch;
    private Enum m_joinFetch;
   
    private JoinTableMetadata m_joinTable;
    private List<JoinColumnMetadata> m_joinColumns = new ArrayList<JoinColumnMetadata>();
  
    private String m_targetEntityName;
    
    /**
     * INTERNAL:
     */
    protected RelationshipAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected RelationshipAccessor(Annotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        m_fetch = (annotation == null) ? getDefaultFetchType() : (Enum) MetadataHelper.invokeMethod("fetch", annotation);
        m_targetEntity = (annotation == null) ? void.class : (Class) MetadataHelper.invokeMethod("targetEntity", annotation);         
        m_cascadeTypes = (annotation == null) ? null : new CascadeTypes((Enum[]) MetadataHelper.invokeMethod("cascade", annotation), accessibleObject);
        
        // Set the join fetch if one is present.           
        if (isAnnotationPresent(JoinFetch.class)) {
            m_joinFetch = (Enum) MetadataHelper.invokeMethod("value", getAnnotation(JoinFetch.class));
        }
        
        // Set the private owned if one is present.
        m_privateOwned = isAnnotationPresent(PrivateOwned.class);
        
        // Set the join columns if some are present. 
        // Process all the join columns first.
        if (isAnnotationPresent(JoinColumns.class)) {
            for (Annotation jColumn : (Annotation[]) MetadataHelper.invokeMethod("value", getAnnotation(JoinColumns.class))) {
                m_joinColumns.add(new JoinColumnMetadata(jColumn, accessibleObject));
            }
        }
        
        // Process the single key join column second.
        if (isAnnotationPresent(JoinColumn.class)) {
            m_joinColumns.add(new JoinColumnMetadata(getAnnotation(JoinColumn.class), accessibleObject));
        }
        
        // Set the join table if one is present.
        if (isAnnotationPresent(JoinTable.class)) {
            m_joinTable = new JoinTableMetadata(getAnnotation(JoinTable.class), accessibleObject);
        }
    }
    
    /**
     * INTERNAL:
     * 
     * Add the relation key fields to a many to many mapping.
     */
    protected void addManyToManyRelationKeyFields(List<JoinColumnMetadata> joinColumns, ManyToManyMapping mapping, String defaultFieldName, MetadataDescriptor descriptor, boolean isSource) {
        // Set the right context level.
        String PK_CTX, FK_CTX;
        if (isSource) {
            PK_CTX = MetadataLogger.SOURCE_PK_COLUMN;
            FK_CTX = MetadataLogger.SOURCE_FK_COLUMN;
        } else {
            PK_CTX = MetadataLogger.TARGET_PK_COLUMN;
            FK_CTX = MetadataLogger.TARGET_FK_COLUMN;
        }
        
        for (JoinColumnMetadata joinColumn : joinColumns) {
            // If the pk field (referencedColumnName) is not specified, it 
            // defaults to the primary key of the referenced table.
            String defaultPKFieldName = descriptor.getPrimaryKeyFieldName();
            DatabaseField pkField = joinColumn.getPrimaryKeyField();
            pkField.setName(getName(pkField, defaultPKFieldName, PK_CTX));
            pkField.setTable(descriptor.getPrimaryKeyTable());
            
            // If the fk field (name) is not specified, it defaults to the 
            // name of the referencing relationship property or field of the 
            // referencing entity + "_" + the name of the referenced primary 
            // key column. If there is no such referencing relationship 
            // property or field in the entity (i.e., a join table is used), 
            // the join column name is formed as the concatenation of the 
            // following: the name of the entity + "_" + the name of the 
            // referenced primary key column.
            DatabaseField fkField = joinColumn.getForeignKeyField();
            String defaultFKFieldName = defaultFieldName + "_" + defaultPKFieldName;
            fkField.setName(getName(fkField, defaultFKFieldName, FK_CTX));
            // Target table name here is the join table name.
            // If the user had specified a different table name in the join
            // column, it is ignored. Perhaps an error or warning should be
            // fired off.
            fkField.setTable(mapping.getRelationTable());
            
            // Add a target relation key to the mapping.
            if (isSource) {
                mapping.addSourceRelationKeyField(fkField, pkField);
            } else {
                mapping.addTargetRelationKeyField(fkField, pkField);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public CascadeTypes getCascadeTypes() {
        return m_cascadeTypes;
    }
    
    /**
     * INTERNAL:
     */
    public abstract Enum getDefaultFetchType();
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public Enum getFetch() {
        return m_fetch;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */    
    public List<JoinColumnMetadata> getJoinColumns() {
        return m_joinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getJoinFetch() {
        return m_joinFetch;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public JoinTableMetadata getJoinTable() {
        return m_joinTable;
    }
    
    /**
     * INTERNAL:
     * Return the logging context for this accessor.
     */
    protected abstract String getLoggingContext();
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getOrphanRemoval() {
        return m_orphanRemoval;
    }
    
    /**
     * INTERNAL:
     * Method to return an owner mapping. It will tell the owner class to
     * process itself if it hasn't already done so. Assumes that a mapped by
     * value has been specified and that a check against mappedByValue has been
     * done.
     */
    protected DatabaseMapping getOwningMapping(String mappedBy) {
        MetadataDescriptor ownerDescriptor = getReferenceDescriptor();
        DatabaseMapping mapping = ownerDescriptor.getMappingForAttributeName(mappedBy, this);
        
        // If no mapping was found, there is an error in the mappedBy field, 
        // therefore, throw an exception.
        if (mapping == null) {
            throw ValidationException.noMappedByAttributeFound(ownerDescriptor.getJavaClass(), mappedBy, getJavaClass(), getAttributeName());
        }
        
        return mapping;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getPrivateOwned() {
        return null;
    }
    
    /**
      * INTERNAL:
      * Return the reference metadata descriptor for this accessor.
      * This method does additional checks to make sure that the target
      * entity is indeed an entity class.
      */
    @Override
    public MetadataDescriptor getReferenceDescriptor() {
        MetadataDescriptor descriptor;
       
        try {
            descriptor = super.getReferenceDescriptor();
        } catch (Exception exception) {
            descriptor = null;
        }
       
        if (descriptor == null || descriptor.isEmbeddable() || descriptor.isEmbeddableCollection()) {
            throw ValidationException.nonEntityTargetInRelationship(getJavaClass(), getReferenceClass(), getAnnotatedElement());
        }
       
        return descriptor;
    }
    
    /**
     * INTERNAL:
     * Return the target entity for this accessor.
     */
    public Class getTargetEntity() {
        return m_targetEntity;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getTargetEntityName() {
        return m_targetEntityName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
        
        // Initialize lists of objects.
        initXMLObjects(m_joinColumns, accessibleObject);
        
        // Initialize single objects.
        initXMLObject(m_joinTable, accessibleObject);
        initXMLObject(m_cascadeTypes, accessibleObject);
        
        // Initialize the target entity name we read from XML.
        m_targetEntity = initXMLClassName(m_targetEntityName);
    }
    
    /**
     * INTERNAL:
     * Return if the accessor should be lazy fetched.
     */
    public boolean isLazy() {        
        Enum fetchType = getFetch();
        
        if (fetchType == null) {
            fetchType = getDefaultFetchType();
        }
        
        return fetchType.name().equals(FetchType.LAZY.name());
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isPrivateOwned() {
        return m_privateOwned;
    }
    
    /**
     * INTERNAL:
     */
    protected void processCascadeTypes(ForeignReferenceMapping mapping) {
        if (m_cascadeTypes != null) {
            for (Enum cascadeType : m_cascadeTypes.getTypes()) {
                setCascadeType(cascadeType, mapping);
            }
        }
        
        // Apply the persistence unit default cascade-persist if necessary.
        if (getDescriptor().isCascadePersist() && ! mapping.isCascadePersist()) {
            setCascadeType(CascadeType.PERSIST, mapping);
        }
    }
    
    /**
     * INTERNAL:
     * Process a MetadataJoinTable.
     */
    protected void processJoinTable(ManyToManyMapping mapping) {
        // Check that we loaded a join table otherwise default one.
        if (m_joinTable == null) {
            // TODO: Log a defaulting message.
            m_joinTable = new JoinTableMetadata(null, getAccessibleObject());
        }
        
        // Build the default table name
        String defaultName = getDescriptor().getPrimaryTableName() + "_" + getReferenceDescriptor().getPrimaryTableName();
        
        // Process any table defaults and log warning messages.
        processTable(m_joinTable, defaultName);
        
        // Set the table on the mapping.
        mapping.setRelationTable(m_joinTable.getDatabaseTable());
        
        // Add all the joinColumns (source foreign keys) to the mapping.
        String defaultSourceFieldName;
        if (getReferenceDescriptor().hasBiDirectionalManyToManyAccessorFor(getJavaClassName(), getAttributeName())) {
            defaultSourceFieldName = getReferenceDescriptor().getBiDirectionalManyToManyAccessor(getJavaClassName(), getAttributeName()).getAttributeName();
        } else {
            defaultSourceFieldName = getDescriptor().getAlias();
        }
        addManyToManyRelationKeyFields(getJoinColumns(m_joinTable.getJoinColumns(), getOwningDescriptor()), mapping, defaultSourceFieldName, getOwningDescriptor(), true);
        
        // Add all the inverseJoinColumns (target foreign keys) to the mapping.
        String defaultTargetFieldName = getAttributeName();
        addManyToManyRelationKeyFields(getJoinColumns(m_joinTable.getInverseJoinColumns(), getReferenceDescriptor()), mapping, defaultTargetFieldName, getReferenceDescriptor(), false);
    }
    
    /**
     * INTERNAL:
     * Front end validation before actually processing the relationship 
     * accessor. The process() method should not be called directly.
     */
    public void processRelationship() {
        // The processing of this accessor may have been fast tracked through a 
        // non-owning relationship. If so, no processing is required.
        if (! isProcessed()) {
            // If a Column annotation is specified then throw an exception.
            if (hasColumn()) {
                throw ValidationException.invalidColumnAnnotationOnRelationship(getJavaClass(), getAttributeName());
            }
                
            // If a Convert annotation is specified then throw an exception.
            if (hasConvert(false)) {
                throw ValidationException.invalidMappingForConverter(getJavaClass(), getAttributeName());
            }
                        
            // Process the relationship accessor only if the target entity
            // is not a ValueHolderInterface.
            if (getTargetEntity() == ValueHolderInterface.class || (getTargetEntity() == void.class && getReferenceClass().getName().equalsIgnoreCase(ValueHolderInterface.class.getName()))) {
                // do nothing ... I'm too lazy (or too stupid) to do the negation of this expression :-)
            } else { 
                process();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Set the getter and setter access methods for this accessor.
     */
    @Override
    protected void setAccessorMethods(DatabaseMapping mapping) {
        super.setAccessorMethods(mapping);
        
        // If we have property access and the owning class has field access, 
        // mark the mapping to weave transient field value holders (if it 
        // so applies at weaving time). Setting the accessor methods 
        // previously told us the type of access in turn indicating if we 
        // needed to weave  transient value holder fields on the class. 
        // With JPA 2.0 and the possibility of mixed access types this 
        // assumption no longer applies.
        ((ForeignReferenceMapping) mapping).setRequiresTransientWeavedFields(usesPropertyAccess(getDescriptor()) && ! getClassAccessor().usesPropertyAccess());
    }
    
    /**
     * INTERNAL:
     * Set the cascade type on a mapping.
     */
    protected void setCascadeType(Enum type, ForeignReferenceMapping mapping) {
        if (type.name().equals(CascadeType.ALL.name())) {
            mapping.setCascadeAll(true);
        } else if(type.name().equals(CascadeType.MERGE.name())) {
            mapping.setCascadeMerge(true);
        } else if(type.name().equals(CascadeType.PERSIST.name())) {
            mapping.setCascadePersist(true);
        } else if(type.name().equals(CascadeType.REFRESH.name())) {
            mapping.setCascadeRefresh(true);
        } else if(type.name().equals(CascadeType.REMOVE.name())) {
            mapping.setCascadeRemove(true);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeTypes(CascadeTypes cascadeTypes) {
        m_cascadeTypes = cascadeTypes;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setFetch(Enum fetch) {
        m_fetch = fetch;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setJoinColumns(List<JoinColumnMetadata> joinColumns) {
        m_joinColumns = joinColumns;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJoinFetch(Enum joinFetch) {
        m_joinFetch = joinFetch;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setJoinTable(JoinTableMetadata joinTable) {
        m_joinTable = joinTable;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOrphanRemoval(Boolean orphanRemoval) {
        m_orphanRemoval = orphanRemoval;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPrivateOwned(String ignore) {
        m_privateOwned = true;
    }
    
    /**
     * INTERNAL:
     */
    public void setTargetEntity(Class targetEntity) {
        m_targetEntity = targetEntity;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTargetEntityName(String targetEntityName) {
        m_targetEntityName = targetEntityName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    protected boolean usesIndirection() {
        // If eager weaving is enabled, indirection is always used.
        if (getProject().weaveEager()) {
            return true;
        }
        
        return isLazy();
    }
}
