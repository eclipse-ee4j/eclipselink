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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;

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
    private boolean m_privateOwned;
    private CascadeTypes m_cascadeTypes;
  
    protected Class m_referenceClass;
    private Class m_targetEntity;
    
    private Enum m_fetch;
    private Enum m_joinFetch;
    
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
        Annotation joinFetch = getAnnotation(JoinFetch.class);            
        if (joinFetch != null) {
            m_joinFetch = (Enum) MetadataHelper.invokeMethod("value", joinFetch);
        }
        
        // Set the private owned if one is present.
        m_privateOwned = isAnnotationPresent(PrivateOwned.class);
        
        // Set the join columns if some are present. 
        // Process all the join columns first.
        Annotation joinColumns = getAnnotation(JoinColumns.class);
        if (joinColumns != null) {
            for (Annotation jColumn : (Annotation[]) MetadataHelper.invokeMethod("value", joinColumns)) {
                m_joinColumns.add(new JoinColumnMetadata(jColumn, accessibleObject));
            }
        }
        
        // Process the single key join column second.
        Annotation joinColumn = getAnnotation(JoinColumn.class);
        if (joinColumn != null) {
            m_joinColumns.add(new JoinColumnMetadata(joinColumn, accessibleObject));
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
     * Return the logging context for this accessor.
     */
    protected abstract String getLoggingContext();
    
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
     * Process the join column metadata. Will look for association overrides.
     */    
    protected List<JoinColumnMetadata> processJoinColumns() { 
        if (getDescriptor().hasAssociationOverrideFor(getAttributeName())) {
            return processJoinColumns(getDescriptor().getAssociationOverrideFor(getAttributeName()).getJoinColumns(), getReferenceDescriptor());
        } else {
            return processJoinColumns(m_joinColumns, getReferenceDescriptor());
        }
    }
    
    /**
     * INTERNAL:
     * Process the join column metadata. Note: if an accessor requires a check 
     * for association overrides, it should call processJoinColumns(),
     * otherwise, calling this method directly assumes your accessor doesn't 
     * support association overrides.
     */    
    protected List<JoinColumnMetadata> processJoinColumns(List<JoinColumnMetadata> joinColumns, MetadataDescriptor descriptor) {
        if (joinColumns.isEmpty()) {
            if (descriptor.hasCompositePrimaryKey()) {
                // Add a default one for each part of the composite primary
                // key. Foreign and primary key to have the same name.
                for (String primaryKeyField : descriptor.getPrimaryKeyFieldNames()) {
                    JoinColumnMetadata joinColumn = new JoinColumnMetadata();
                    joinColumn.setReferencedColumnName(primaryKeyField);
                    joinColumn.setName(primaryKeyField);
                    joinColumns.add(joinColumn);
                }
            } else {
                // Add a default one for the single case, not setting any
                // foreign and primary key names. They will default based
                // on which accessor is using them.
                joinColumns.add(new JoinColumnMetadata());
            }
        } else {
            // Need to update any join columns that use a foreign key name
            // for the primary key name. E.G. User specifies the renamed id
            // field name from a primary key join column as the primary key in
            // an inheritance subclass.
            for (JoinColumnMetadata joinColumn : joinColumns) {
                // Doing this could potentially change a value entered in XML.
                // However, in this case I think that is ok since in theory we 
                // are writing out the correct value that EclipseLink needs to 
                // form valid queries.
                joinColumn.setReferencedColumnName(descriptor.getPrimaryKeyJoinColumnAssociation(joinColumn.getReferencedColumnName()));
            }
        }
        
        if (descriptor.hasCompositePrimaryKey()) {
            // The number of join columns should equal the number of primary key fields.
            if (joinColumns.size() != descriptor.getPrimaryKeyFields().size()) {
                throw ValidationException.incompleteJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
            }
            
            // All the primary and foreign key field names should be specified.
            for (JoinColumnMetadata joinColumn : joinColumns) {
                if (joinColumn.isPrimaryKeyFieldNotSpecified() || joinColumn.isForeignKeyFieldNotSpecified()) {
                    throw ValidationException.incompleteJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
                }
            }
        } else {
            if (joinColumns.size() > 1) {
                throw ValidationException.excessiveJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
            }
        }
        
        return joinColumns;
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
            if (hasConvert()) {
                throw ValidationException.invalidMappingForConverter(getJavaClass(), getAttributeName());
            }
                
            // Process the relationship accessor only if the target entity
            // is not a ValueHolderInterface.
            if (getTargetEntity() == ValueHolderInterface.class || (getTargetEntity() == void.class && getReferenceClass().getName().equalsIgnoreCase(ValueHolderInterface.class.getName()))) {
                // do nothing ... I'm too lazy (or too stupid) to do the negation of this expression :-)
            } else { 
                process();
            }
            
            // Set its processing completed flag to avoid double processing.
            setIsProcessed();
        }
    }

    /**
     * INTERNAL:
     * Set the cascade type on a mapping.
     */
    protected void setCascadeType(Enum type, ForeignReferenceMapping mapping) {
        if(type.name().equals(CascadeType.ALL.name())) {
            mapping.setCascadeAll(true);
        }else if(type.name().equals(CascadeType.MERGE.name())) {
            mapping.setCascadeMerge(true);
        }else if(type.name().equals(CascadeType.PERSIST.name())) {
            mapping.setCascadePersist(true);
        }else if(type.name().equals(CascadeType.REFRESH.name())) {
            mapping.setCascadeRefresh(true);
        }else if(type.name().equals(CascadeType.REMOVE.name())) {
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
    public boolean usesIndirection() {
        // If eager weaving is enabled, indirection is always used.
        if (getProject().weaveEager()) {
            return true;
        }
        
        return isLazy();
    }
}
