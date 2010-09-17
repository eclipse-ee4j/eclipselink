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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     05/1/2009-2.0 Guy Pelletier 
 *       - 249033: JPA 2.0 Orphan removal
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     09/29/2009-2.0 Guy Pelletier 
 *       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     11/25/2009-2.0 Guy Pelletier 
 *       - 288955: EclipseLink 2.0.0.v20090821-r4934 (M7) throws EclipseLink-80/41 exceptions if InheritanceType.TABLE_PER_CLASS is used
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     08/11/2010-2.2 Guy Pelletier 
 *       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
 *     09/03/2010-2.2 Guy Pelletier 
 *       - 317286: DB column lenght not in sync between @Column and @JoinColumn
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.RelationTableMechanism;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.CascadeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.JoinTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * INTERNAL:
 * A relational accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class RelationshipAccessor extends MappingAccessor {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    private Boolean m_orphanRemoval;
    private boolean m_privateOwned;
    private boolean m_cascadeOnDelete;
    private CascadeMetadata m_cascade;
    protected MetadataClass m_referenceClass;
    private MetadataClass m_targetEntity;
    
    private String m_fetch;
    private String m_mappedBy;
    private String m_joinFetch;
    private String m_batchFetch;
    private Integer m_batchFetchSize;

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
    protected RelationshipAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        m_fetch = (annotation == null) ? getDefaultFetchType() : (String) annotation.getAttribute("fetch");
        m_targetEntity = getMetadataClass((annotation == null) ? "void" : (String) annotation.getAttributeString("targetEntity"));         
        m_cascade = (annotation == null) ? null : new CascadeMetadata((Object[]) annotation.getAttributeArray("cascade"), accessibleObject);
        
        // Set the join fetch if one is present.           
        MetadataAnnotation joinFetch = getAnnotation(JoinFetch.class);            
        if (joinFetch != null) {
            // Get attribute string will return the default ""
            m_joinFetch = (String) joinFetch.getAttributeString("value");
        }
        
        // Set the batch fetch if one is present.           
        MetadataAnnotation batchFetch = getAnnotation(BatchFetch.class);            
        if (batchFetch != null) {
            // Get attribute string will return the default ""
            m_batchFetch = (String) batchFetch.getAttributeString("value");
            m_batchFetchSize = (Integer) batchFetch.getAttribute("size");
        }
        
        // Set the private owned if one is present.
        m_privateOwned = isAnnotationPresent(PrivateOwned.class);
        
        m_cascadeOnDelete = isAnnotationPresent(CascadeOnDelete.class);
        
        // Set the join columns if some are present. 
        // Process all the join columns first.
        MetadataAnnotation joinColumns = getAnnotation(JoinColumns.class);
        if (joinColumns != null) {
            for (Object jColumn : (Object[]) joinColumns.getAttributeArray("value")) {
                m_joinColumns.add(new JoinColumnMetadata((MetadataAnnotation)jColumn, accessibleObject));
            }
        }
        
        // Process the single key join column second.
        MetadataAnnotation joinColumn = getAnnotation(JoinColumn.class);
        if (joinColumn != null) {
            m_joinColumns.add(new JoinColumnMetadata(joinColumn, accessibleObject));
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
    protected void addJoinTableRelationKeyFields(List<JoinColumnMetadata> joinColumns, RelationTableMechanism mechanism, String defaultFieldName, MetadataDescriptor descriptor, boolean isSource) {
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
            // Look up the primary key field from the referenced column name.
            DatabaseField pkField = getReferencedField(joinColumn.getReferencedColumnName(), descriptor, PK_CTX);
            
            // If the fk field (name) is not specified, it defaults to the 
            // name of the referencing relationship property or field of the 
            // referencing entity + "_" + the name of the referenced primary 
            // key column. If there is no such referencing relationship 
            // property or field in the entity (i.e., a join table is used), 
            // the join column name is formed as the concatenation of the 
            // following: the name of the entity + "_" + the name of the 
            // referenced primary key column.
            DatabaseField fkField = joinColumn.getForeignKeyField(pkField);
            String defaultFKFieldName = defaultFieldName + "_" + descriptor.getPrimaryKeyFieldName();
            setFieldName(fkField, defaultFKFieldName, FK_CTX);
            
            // Target table name here is the join table name.
            // If the user had specified a different table name in the join
            // column, it is ignored. Perhaps an error or warning should be
            // fired off.
            fkField.setTable(mechanism.getRelationTable());
            
            // Add a target relation key to the mapping.
            if (isSource) {
                mechanism.addSourceRelationKeyField(fkField, pkField);
            } else {
                mechanism.addTargetRelationKeyField(fkField, pkField);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof RelationshipAccessor) {
            RelationshipAccessor relationshipAccessor = (RelationshipAccessor) objectToCompare;
            
            if (! valuesMatch(m_orphanRemoval, relationshipAccessor.getOrphanRemoval())) {
                return false;
            }
            
            if (! valuesMatch(m_privateOwned, relationshipAccessor.isPrivateOwned())) {
                return false;
            }

            if (! valuesMatch(m_cascade, relationshipAccessor.getCascade())) {
                return false;
            }
            
            if (! valuesMatch(m_mappedBy, relationshipAccessor.getMappedBy())) {
                return false;
            }
            
            if (! valuesMatch(m_fetch, relationshipAccessor.getFetch())) {
                return false;
            }
            
            if (! valuesMatch(m_joinFetch, relationshipAccessor.getJoinFetch())) {
                return false;
            }
            
            if (! valuesMatch(m_batchFetch, relationshipAccessor.getBatchFetch())) {
                return false;
            }
            
            if (! valuesMatch(m_batchFetchSize, relationshipAccessor.getBatchFetchSize())) {
                return false;
            }
            
            if (! valuesMatch(m_joinTable, relationshipAccessor.getJoinTable())) {
                return false;
            }
            
            if (! valuesMatch(m_joinColumns, relationshipAccessor.getJoinColumns())) {
                return false;
            }
            
            return valuesMatch(m_targetEntityName, relationshipAccessor.getTargetEntityName());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getBatchFetch() {
        return m_batchFetch;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getBatchFetchSize() {
        return m_batchFetchSize;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public CascadeMetadata getCascade() {
        return m_cascade;
    }
    
    /**
     * INTERNAL:
     */
    public abstract String getDefaultFetchType();
    
    /**
     * INTERNAL:
     * Return the default table to hold the foreign key of a MapKey when
     * and Entity is used as the MapKey
     * @return
     */
    @Override
    protected DatabaseTable getDefaultTableForEntityMapKey(){
        if (getJoinTable() != null){
            return getJoinTable().getDatabaseTable();
        } else {
            return super.getDefaultTableForEntityMapKey();
        }
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public String getFetch() {
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
    public String getJoinFetch() {
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
     * This method will return the join table metadata to be processed with 
     * this relationship accessor. It will first check for a join table from 
     * an association override, followed by a join table defined directly on 
     * the accessor. If neither is present, a join table metadata is defaulted.
     */
    protected JoinTableMetadata getJoinTableMetadata() {
        if (getDescriptor().hasAssociationOverrideFor(getAttributeName())) {
            if (m_joinTable != null) {
                // TODO: Log an override message ...
            }
            m_joinTable = getDescriptor().getAssociationOverrideFor(getAttributeName()).getJoinTable();
        } else {
            if (m_joinTable == null) {
                // TODO: Log a defaulting message.
                m_joinTable = new JoinTableMetadata(null, getAccessibleObject());
            }
        }
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
    public String getMappedBy() {
        return m_mappedBy;
    }
    
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
     * value has been specified and that a check against mappedBy has been
     * done.
     */
    protected DatabaseMapping getOwningMappingAccessor() {
        MetadataDescriptor ownerDescriptor = getReferenceDescriptor();
        MappingAccessor mappingAccessor = ownerDescriptor.getMappingAccessor(getMappedBy());
        
        // If no mapping was found, there is an error in the mappedBy field, 
        // therefore, throw an exception.
        if (mappingAccessor == null) {
            throw ValidationException.noMappedByAttributeFound(ownerDescriptor.getJavaClass(), getMappedBy(), getJavaClass(), getAttributeName());
        } else if (mappingAccessor.isRelationship()) {
            RelationshipAccessor relationshipAccessor = (RelationshipAccessor) mappingAccessor;
            
            // Check that we don't have circular mappedBy values which will 
            // cause an infinite loop.
            String mappedBy = relationshipAccessor.getMappedBy();
                
            if (mappedBy != null && mappedBy.equals(getAttributeName())) {
                throw ValidationException.circularMappedByReferences(getJavaClass(), getAttributeName(), getJavaClass(), getMappedBy());
            }
        }
        
        return mappingAccessor.getMapping();
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
        MetadataDescriptor referenceDescriptor;
        
        // When processing metamodel mapped superclasses, we don't have the 
        // luxury of a full type context as we would during regular metadata
        // processing (e.g. to figure out generic types). The metamodel mapped
        // superclass descriptors must therefore rely on a real child descriptor
        // to extract this information and process correctly. When a type is 
        // unknown, the reference class name currently defaults to java.lang.string.
        // @see MetadataAnnotatedElement getRawClass(MetadataDescriptor)
        if (getDescriptor().isMappedSuperclass() && getReferenceClassName().equals(MetadataAnnotatedElement.DEFAULT_RAW_CLASS)) {
            MappingAccessor childMappingAccessor = getDescriptor().getMetamodelMappedSuperclassChildDescriptor().getMappingAccessor(getAttributeName());
            referenceDescriptor = childMappingAccessor.getReferenceDescriptor();
            
            if (referenceDescriptor.isInheritanceSubclass()) {
                referenceDescriptor = referenceDescriptor.getInheritanceRootDescriptor();
            }
        } else {
            ClassAccessor accessor = getProject().getAccessor(getReferenceClassName());
            referenceDescriptor = (accessor != null) ? accessor.getDescriptor() : null; 
        }
       
        // Validate the reference descriptor is valid.
        if (referenceDescriptor == null || referenceDescriptor.isEmbeddable() || referenceDescriptor.isEmbeddableCollection()) {
            throw ValidationException.nonEntityTargetInRelationship(getJavaClass(), getReferenceClass(), getAnnotatedElement());
        }
       
        return referenceDescriptor;
    }
    
    /**
     * INTERNAL:
     * Return the target entity for this accessor.
     */
    public MetadataClass getTargetEntity() {
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
     * Return true if a join table has been explicitly set on this accessor.
     */
    protected boolean hasJoinTable() {
        return m_joinTable != null;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor is the non owning side of the relationship,
     * that is, has a mapped by value. 
     */
    public boolean hasMappedBy() {
        return getMappedBy() != null && ! getMappedBy().equals("");
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Initialize lists of objects.
        initXMLObjects(m_joinColumns, accessibleObject);
        
        // Initialize single objects.
        initXMLObject(m_joinTable, accessibleObject);
        initXMLObject(m_cascade, accessibleObject);
        
        // Initialize the target entity name we read from XML.
        m_targetEntity = initXMLClassName(m_targetEntityName);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public boolean isCascadeOnDelete() {
        return m_cascadeOnDelete;
    }
    
    /**
     * INTERNAL:
     * Return if the accessor should be lazy fetched.
     */
    public boolean isLazy() {        
        String fetchType = getFetch();
        
        if (fetchType == null) {
            fetchType = getDefaultFetchType();
        }
        
        return fetchType.equals(FetchType.LAZY.name());
    }
  
    /**
     * INTERNAL:
     * Return true is this relationship employs orphanRemoval.
     */
    protected boolean isOrphanRemoval() {
        return m_orphanRemoval != null && m_orphanRemoval.booleanValue();
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
     * If somehow we are processing a class that was weaved to have value 
     * holders, we should ignore the processing of this mapping. 
     */
    public boolean isValueHolderInterface() {
        return getTargetEntity().getName().equals(ValueHolderInterface.class.getName()) || (getTargetEntity().getName().equals(void.class.getName()) && getReferenceClass().getName().equals(ValueHolderInterface.class.getName()));
    }
    
    /**
     * INTERNAL:
     * Common validation done by all relationship accessors.
     */
    public void process() {
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
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void processCascadeTypes(ForeignReferenceMapping mapping) {
        if (m_cascade != null) {
            for (String cascadeType : m_cascade.getTypes()) {
                setCascadeType(cascadeType, mapping);
            }
        }
        
        // Apply the persistence unit default cascade-persist if necessary.
        if (getDescriptor().isCascadePersist() && ! mapping.isCascadePersist()) {
            setCascadeType(CascadeType.PERSIST.name(), mapping);
        }
    }
    
    /**
     * INTERNAL:
     * Process a MetadataJoinTable.
     */
    protected void processJoinTable(ForeignReferenceMapping mapping, RelationTableMechanism mechanism, JoinTableMetadata joinTable) {
        // Build the default table name
        String defaultName = getOwningDescriptor().getPrimaryTableName() + "_" + getReferenceDescriptor().getPrimaryTableName();
        
        // Process any table defaults and log warning messages.
        processTable(joinTable, defaultName);
        
        // Set the table on the mapping.
        mechanism.setRelationTable(joinTable.getDatabaseTable());
        
        // Add all the joinColumns (source foreign keys) to the mapping.
        String defaultSourceFieldName;
        if (getReferenceDescriptor().hasBiDirectionalManyToManyAccessorFor(getJavaClassName(), getAttributeName())) {
            defaultSourceFieldName = getReferenceDescriptor().getBiDirectionalManyToManyAccessor(getJavaClassName(), getAttributeName()).getAttributeName();
        } else {
            defaultSourceFieldName = getOwningDescriptor().getAlias();
        }
        addJoinTableRelationKeyFields(getJoinColumnsAndValidate(joinTable.getJoinColumns(), getOwningDescriptor()), mechanism, defaultSourceFieldName, getOwningDescriptor(), true);
        
        // Add all the inverseJoinColumns (target foreign keys) to the mapping.
        String defaultTargetFieldName = getAttributeName();
        addJoinTableRelationKeyFields(getJoinColumnsAndValidate(joinTable.getInverseJoinColumns(), getReferenceDescriptor()), mechanism, defaultTargetFieldName, getReferenceDescriptor(), false);
    
        // The spec. requires pessimistic lock to be extend-able to JoinTable.
        mapping.setShouldExtendPessimisticLockScope(true);
    }
    
    /**
     * INTERNAL:
     */
    protected void processMappedByRelationTable(RelationTableMechanism ownerMechanism, RelationTableMechanism mechanism) {
        // Set the relation table name from the owner.
        mechanism.setRelationTable(ownerMechanism.getRelationTable());
             
        // In a table per class inheritance we need to update the target 
        // keys before setting them to mapping's source key fields.
        if (getDescriptor().usesTablePerClassInheritanceStrategy()) {
            // Update the target key fields.
            Vector<DatabaseField> targetKeyFields = new Vector<DatabaseField>();
            for (DatabaseField targetKeyField : ownerMechanism.getTargetKeyFields()) {
                DatabaseField newTargetKeyField = targetKeyField.clone();
                newTargetKeyField.setTable(getDescriptor().getPrimaryTable());
                targetKeyFields.add(newTargetKeyField);
            }
            
            mechanism.setSourceKeyFields(targetKeyFields);
        } else {
            // Add all the source foreign keys we found on the owner.
            mechanism.setSourceKeyFields(ownerMechanism.getTargetKeyFields());
        }
        
        // Add all the source relation key fields.
        mechanism.setSourceRelationKeyFields(ownerMechanism.getTargetRelationKeyFields());
        
        // Add all the target foreign keys we found on the owner.
        mechanism.setTargetKeyFields(ownerMechanism.getSourceKeyFields());
        mechanism.setTargetRelationKeyFields(ownerMechanism.getSourceRelationKeyFields());
    }
    
    /**
     * INTERNAL:
     * This method should be called for all mappings even though they may
     * not support. The reason is that we want to log a message for those 
     * mappings that try to employ a private owned setting when it is not 
     * supported on their mapping.
     * 
     * Order of checking is as follows:
     *  1 - check for orphanRemoval first. Through meta data, this can only 
     *      be true for 1-1, 1-M and V1-1
     *  2 - check for isPrivateOwned. Do no check the variable directly
     *      as the isPrivateOwned method is overridden in those classes that do 
     *      not support it (to check if the user decorated the mapping with a 
     *      private owned and log a warning message that we are ignoring it.)
     */
    protected void processOrphanRemoval(ForeignReferenceMapping mapping) {
        if (isOrphanRemoval()) {
            mapping.setIsPrivateOwned(true);
            mapping.setCascadeRemove(true);
        } else {
            mapping.setIsPrivateOwned(isPrivateOwned());   
        }
    }
    
    /**
     * Process settings common to ForeignReferenceMapping.
     */
    protected void processRelationshipMapping(ForeignReferenceMapping mapping) {
        // Set the mapping, this must be done first.
        setMapping(mapping);
        
        mapping.setIsLazy(isLazy());
        mapping.setAttributeName(getAttributeName());
        mapping.setReferenceClassName(getReferenceClassName());
        mapping.setIsCascadeOnDeleteSetOnDatabase(isCascadeOnDelete());
        
        // Process join fetch type.
        processJoinFetch(getJoinFetch(), mapping);
        
        // Process the batch fetch if specified.
        processBatchFetch(getBatchFetch(), mapping);
            
        // Process the orphanRemoval or PrivateOwned
        processOrphanRemoval(mapping);
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);

        // Process the cascade types.
        processCascadeTypes(mapping);        
    }
    
    /**
     * INTERNAL:
     * Set the getter and setter access methods for this accessor.
     */
    @Override
    protected void setAccessorMethods(DatabaseMapping mapping) {
        super.setAccessorMethods(mapping);
        
        // If we have property access and the owning class has field access, 
        // mark the mapping to weave transient field value holders (if it so 
        // applies at weaving time). Setting the accessor methods previously 
        // told us the type of access in turn indicating if we needed to weave
        // transient value holder fields on the class. With JPA 2.0 and the 
        // possibility of mixed access types this assumption no longer applies.
        ((ForeignReferenceMapping) mapping).setRequiresTransientWeavedFields(usesPropertyAccess() && ! getClassAccessor().usesPropertyAccess());
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setBatchFetch(String batchFetch) {
        m_batchFetch = batchFetch;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setBatchFetchSize(Integer batchFetchSize) {
        m_batchFetchSize = batchFetchSize;
    }
    
    /**
     * INTERNAL:
     * Set the cascade type on a mapping.
     */
    protected void setCascadeType(String type, ForeignReferenceMapping mapping) {
        if (type.equals(CascadeType.ALL.name())) {
            mapping.setCascadeAll(true);
        } else if(type.equals(CascadeType.MERGE.name())) {
            mapping.setCascadeMerge(true);
        } else if(type.equals(CascadeType.PERSIST.name())) {
            mapping.setCascadePersist(true);
        } else if(type.equals(CascadeType.REFRESH.name())) {
            mapping.setCascadeRefresh(true);
        } else if(type.equals(CascadeType.REMOVE.name())) {
            mapping.setCascadeRemove(true);
        } else if (type.equals(CascadeType.DETACH.name())) {
            mapping.setCascadeDetach(true);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascade(CascadeMetadata cascade) {
        m_cascade = cascade;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCascadeOnDelete(boolean cascadeOnDelete) {
        m_cascadeOnDelete = cascadeOnDelete;
    }
    
    /**
     * INTERNAL: 
     * Used for OX mapping.
     */
    public void setFetch(String fetch) {
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
    public void setJoinFetch(String joinFetch) {
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
    public void setMappedBy(String mappedBy) {
        m_mappedBy = mappedBy;
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
    public void setTargetEntity(MetadataClass targetEntity) {
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
