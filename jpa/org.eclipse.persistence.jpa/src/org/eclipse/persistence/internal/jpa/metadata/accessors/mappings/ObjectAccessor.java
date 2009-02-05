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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.indirection.WeavedObjectBasicIndirectionPolicy;

import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * INTERNAL:
 * A single object relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class ObjectAccessor extends RelationshipAccessor {
    private Boolean m_isId;
    private Boolean m_isOptional;
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    private String m_mappedById;
    
    /**
     * INTERNAL:
     * Checks if this accessor is part of the Id
     */
    public boolean isId(){
        if (m_isId == null) {
            return false;
        } else {
            return m_isId.booleanValue();
        }
    }

    public void setIsId(Boolean isId){
        this.m_isId = isId;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected ObjectAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    protected ObjectAccessor(Annotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);
        
        m_isOptional = (annotation == null) ? true : (Boolean) MetadataHelper.invokeMethod("optional", annotation);
        
        // Process all the primary key join columns first.
        Annotation primaryKeyJoinColumns = getAnnotation(PrimaryKeyJoinColumns.class);
        if (primaryKeyJoinColumns != null) {
            for (Annotation primaryKeyJoinColumn : (Annotation[]) MetadataHelper.invokeMethod("value", primaryKeyJoinColumns)) { 
                m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata(primaryKeyJoinColumn, accessibleObject));
            }
        }
        
        // Process the single primary key join column second.
        Annotation primaryKeyJoinColumn = getAnnotation(PrimaryKeyJoinColumn.class);
        if (primaryKeyJoinColumn != null) {
            m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata(primaryKeyJoinColumn, accessibleObject));
        }
    }
    
    /**
     * INTERNAL:
     * Return the default fetch type for an object mapping.
     */
    public Enum getDefaultFetchType() {
        return FetchType.valueOf("EAGER");
    }
    
    public Boolean getIsId(){
        return this.m_isId;
    }
    
    /**
     * INTERNAL:
     */
    public String getMappedById(){
        return m_mappedById;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getOptional() {
        return m_isOptional;
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
     * If a target entity is specified in metadata, it will be set as the 
     * reference class, otherwise we will use the raw class.
     */
    @Override
    public Class getReferenceClass() {
        if (m_referenceClass == null) {
            m_referenceClass = getTargetEntity();
        
            if (m_referenceClass == void.class) {
                // Get the reference class from the accessible object and
                // log the defaulting contextual reference class.
                m_referenceClass = super.getReferenceClass();
                getLogger().logConfigMessage(getLoggingContext(), getAnnotatedElement(), m_referenceClass);
            } 
        }
        
        return m_referenceClass;
    }
    
    /**
     * INTERNAL:
     * Initialize a OneToOneMapping.
     */
    protected OneToOneMapping initOneToOneMapping() {
        OneToOneMapping mapping = new OneToOneMapping();
        mapping.setIsReadOnly(false);
        mapping.setIsPrivateOwned(isPrivateOwned());
        mapping.setJoinFetch(getMappingJoinFetchType(getJoinFetch()));
        mapping.setIsOptional(isOptional());
        mapping.setAttributeName(getAttributeName());
        mapping.setReferenceClassName(getReferenceClassName());
        //Derived ID: set if this mapping has been marked as an ID 
        mapping.setIsIDMapping(isId());
        
        // Process the indirection.
        processIndirection(mapping);
        
        // Set the getter and setter methods if access is PROPERTY.
        setAccessorMethods(mapping);
        
        // Process the cascade types.
        processCascadeTypes(mapping);
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        return mapping;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
    
        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_primaryKeyJoinColumns, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-1 primary key relationship.
     */
    public boolean isOneToOnePrimaryKeyRelationship() {
        return isOneToOne() && ! m_primaryKeyJoinColumns.isEmpty();
    }
    
    /**
     * INTERNAL:
     */
    public boolean isOptional() {
        if (m_isOptional == null) {
            return true;
        } else {
            return m_isOptional.booleanValue();
        }
    }
    
    /**
     * INTERNAL:
     * Process the indirection (aka fetch type)
     */
    protected void processIndirection(ObjectReferenceMapping mapping) {
        boolean usesIndirection = usesIndirection();
        // If weaving was disabled, and the class was not static weaved,
        // then disable indirection.
        if (usesIndirection && (!getProject().isWeavingEnabled()) && (!ClassConstants.PersistenceWeavedLazy_Class.isAssignableFrom(getDescriptor().getJavaClass()))) {
            usesIndirection = false;
        }
        if (usesIndirection && usesPropertyAccess(getDescriptor())) {
            mapping.setIndirectionPolicy(new WeavedObjectBasicIndirectionPolicy(getSetMethodName()));
        } else {
            mapping.setUsesIndirection(usesIndirection);
        }
        
        mapping.setIsLazy(isLazy());
    }
    
    /**
     * INTERNAL:
     * Process the join columns for the owning side of a one to one mapping.
     * The default pk and fk field names are used only with single primary key 
     * entities. The processor should never get as far as to use them with 
     * entities that have a composite primary key (validation exception will be 
     * thrown).
     */
    protected void processOneToOneForeignKeyRelationship(OneToOneMapping mapping) {         
        // If the pk field (referencedColumnName) is not specified, it 
        // defaults to the primary key of the referenced table.
        String defaultPKFieldName = getReferenceDescriptor().getPrimaryKeyFieldName();
        
        // If the fk field (name) is not specified, it defaults to the 
        // concatenation of the following: the name of the referencing 
        // relationship property or field of the referencing entity; "_"; 
        // the name of the referenced primary key column.
        String defaultFKFieldName = getUpperCaseAttributeName() + "_" + defaultPKFieldName;
            
        // Add the source foreign key fields to the mapping.
        for (JoinColumnMetadata joinColumn : processJoinColumns()) {
            DatabaseField pkField = joinColumn.getPrimaryKeyField();
            pkField.setName(getName(pkField, defaultPKFieldName, MetadataLogger.PK_COLUMN));
            pkField.setTable(getReferenceDescriptor().getPrimaryKeyTable());
            
            DatabaseField fkField = joinColumn.getForeignKeyField();
            fkField.setName(getName(fkField, defaultFKFieldName, MetadataLogger.FK_COLUMN));
            // Set the table name if one is not already set.
            if (fkField.getTableName().equals("")) {
                fkField.setTable(getDescriptor().getPrimaryTable());
            }
            
            // Add a source foreign key to the mapping.
            mapping.addForeignKeyField(fkField, pkField);
            
            // If any of the join columns is marked read-only then set the 
            // mapping to be read only.
            if (fkField.isReadOnly()) {
                mapping.setIsReadOnly(true);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the primary key join columns for the owning side of a one to one 
     * mapping. The default pk and pk field names are used only with single 
     * primary key entities. The processor should never get as far as to use 
     * them with entities that have a composite primary key (validation 
     * exception will be thrown).
     */
    protected void processOneToOnePrimaryKeyRelationship(OneToOneMapping mapping) {
        MetadataDescriptor referenceDescriptor = getReferenceDescriptor();
        List<PrimaryKeyJoinColumnMetadata> pkJoinColumns = processPrimaryKeyJoinColumns(new PrimaryKeyJoinColumnsMetadata(getPrimaryKeyJoinColumns()));

        // Add the source foreign key fields to the mapping.
        for (PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn : pkJoinColumns) {
            // The default primary key name is the primary key field name of the
            // referenced entity.
            DatabaseField pkField = primaryKeyJoinColumn.getPrimaryKeyField();
            pkField.setName(getName(pkField, referenceDescriptor.getPrimaryKeyFieldName(), MetadataLogger.PK_COLUMN));
            pkField.setTable(referenceDescriptor.getPrimaryTable());
            
            // The default foreign key name is the primary key of the
            // referencing entity.
            DatabaseField fkField = primaryKeyJoinColumn.getForeignKeyField();
            fkField.setName(getName(fkField, getDescriptor().getPrimaryKeyFieldName(), MetadataLogger.FK_COLUMN));
            fkField.setTable(getDescriptor().getPrimaryTable());
            
            // Add a source foreign key to the mapping.
            mapping.addForeignKeyField(fkField, pkField);
            
            // Mark the mapping read only
            mapping.setIsReadOnly(true);
        }
    }
    
    /**
     * INTERNAL:
     * Process the the correct metadata join column for the owning side of a 
     * one to one mapping.
     */
    protected void processOwningMappingKeys(OneToOneMapping mapping) {
        if (isOneToOnePrimaryKeyRelationship()) {
            processOneToOnePrimaryKeyRelationship(mapping);
        } else {
            processOneToOneForeignKeyRelationship(mapping);
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setMappedById(String mappedById){
        this.m_mappedById = mappedById;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOptional(Boolean isOptional) {
        m_isOptional = isOptional;
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
     * Used to process primary keys and DerivedIds.
     */
    public void keyProcessing(HashSet<ClassAccessor> processing, HashSet<ClassAccessor> processed){
        MetadataDescriptor referenceDescriptor = this.getReferenceDescriptor();
        org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor referenceAccessor;
        referenceAccessor = referenceDescriptor.getClassAccessor();
        
        if (!processed.contains(referenceAccessor)){
            referenceAccessor.processDerivedIDs(processing, processed);
        }

        this.processRelationship();
        String attributeName = getAttributeName();

        // If this entity has a pk class, we need to validate our ids. 
        String keyname = referenceDescriptor.getPKClassName();

        if (keyname !=null ){
            //They have a pk class
            String ourpkname = this.getDescriptor().getPKClassName();
            if (ourpkname == null){
                throw ValidationException.invalidCompositePKSpecification(getJavaClass(), ourpkname);
            }
            if (! ourpkname.equals(keyname)){
                //validate our pk contains their pk.
                getOwningDescriptor().validatePKClassId(attributeName, referenceDescriptor.getPKClass());
            } else {
                //this pk is the reference pk, so all pk attributes are accounted through this relationship
                getOwningDescriptor().m_pkClassIDs.clear();
            }
        } else {
            Type type = null;
            if (referenceAccessor.hasDerivedId()){
                //referenced object has a derived ID but no PK class defined
                //so it must be a simple pk type.  Recurse through to get the simple type
                ObjectAccessor refIdAccessor = (ObjectAccessor)referenceDescriptor.getAccessorFor( referenceDescriptor.getIdAttributeName() );
                type = refIdAccessor.getSimplePKType();
            } else {//validate on their basic mapping:
                type = referenceDescriptor.getAccessorFor(referenceDescriptor.getIdAttributeName()).getRawClass();
                //type = referenceDescriptor.m_accessors.get( referenceDescriptor.getIdAttributeName() ).getRawClass();
            }
            getOwningDescriptor().validatePKClassId(attributeName, type);
        }

        // Store the Id attribute name. Used with validation and OrderBy.
        getOwningDescriptor().addIdAttributeName(attributeName);

        // Add the primary key fields to the descriptor.  
        ObjectReferenceMapping mapping = (ObjectReferenceMapping)this.getMapping();
        for (DatabaseField pkField : mapping.getForeignKeyFields()){
            getOwningDescriptor().addPrimaryKeyField(pkField);
        }
    }
    
    /**
     * INTERNAL:
     * Used to process primary keys and DerivedIds.
     */
    public Class getSimplePKType(){
        MetadataDescriptor referenceDescriptor = this.getReferenceDescriptor();
        org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor referenceAccessor;
        referenceAccessor = referenceDescriptor.getClassAccessor();
        
        if (referenceAccessor.hasDerivedId()){
            //referenced object has a derived ID and must be a simple pk type.  Recurse through to get the simple type
            ObjectAccessor refIdAccessor = (ObjectAccessor)referenceDescriptor.m_accessors.get( referenceDescriptor.getIdAttributeName() );
            return refIdAccessor.getSimplePKType();
        } else {//validate on their basic mapping:
            return  referenceDescriptor.m_accessors.get( referenceDescriptor.getIdAttributeName() ).getRawClass();
        }
    }
}
