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
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.accessors.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * A relational accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class RelationshipAccessor extends MetadataAccessor {
    private boolean m_privateOwned;
    private CascadeTypes m_cascadeTypes;
    protected Class m_referenceClass;
    private Class m_targetEntity;
    private Enum m_fetch;
    private Enum m_joinFetch;
    private List<JoinColumnMetadata> m_joinColumns;
    private String m_mappedBy; 
    private String m_targetEntityName;
	
	/**
	 * INTERNAL:
	 */
    protected RelationshipAccessor() {}
    
    /**
     * INTERNAL:
     */
    protected RelationshipAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
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
     * INTERNAL: (Overridden in ManyToOneAccessor)
     * Used for OX mapping.
     */
    public String getMappedBy() {
    	return m_mappedBy;
    }
    
    /**
     * INTERNAL:
     * Method to return an owner mapping. It will tell the owner class to
     * process itself if it hasn't already done so.
     */
    protected DatabaseMapping getOwningMapping() {
        MetadataDescriptor ownerDescriptor = getReferenceDescriptor();
        DatabaseMapping mapping = ownerDescriptor.getMappingForAttributeName(getMappedBy(), this);
        
        // If no mapping was found, there is an error in the mappedBy field, 
        // therefore, throw an exception.
        if (mapping == null) {
        	throw ValidationException.noMappedByAttributeFound(ownerDescriptor.getJavaClass(), getMappedBy(), getJavaClass(), getAttributeName());
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
	 * Method to check if an annotated element has a JoinColumn annotation.
     */
	public boolean hasJoinColumn() {
		return isAnnotationPresent(JoinColumn.class);
    }
    
    /**
     * INTERNAL:
	 * Method to check if an annotated element has a JoinColumns annotation.
     */
	public boolean hasJoinColumns() {
		return isAnnotationPresent(JoinColumns.class);
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor has any primary key join columns specified.
     */
	public boolean hasPrimaryKeyJoinColumns() {
		if (getPrimaryKeyJoinColumns() != null && ! getPrimaryKeyJoinColumns().isEmpty()) {
			return true;
		} else {
			return isAnnotationPresent(PrimaryKeyJoinColumns.class) || isAnnotationPresent(PrimaryKeyJoinColumn.class);
		}
    }
	
    /**
     * INTERNAL: (Overridden in ManyToOneAccessor and ManyToManyAccessor)
     * Method to check if this accessor is marked as private owned.
     */
    protected boolean hasPrivateOwned() {
    	return m_privateOwned || isAnnotationPresent(PrivateOwned.class);
    }
    
    /**
     * INTERNAL: (Override from MetadataAccessor)
     */
    public void init(MetadataAccessibleObject accessibleObject, ClassAccessor accessor) {
    	super.init(accessibleObject, accessor);
    	
    	// Initialize the target entity name we read from XML.
    	if (getTargetEntityName() != null) {
    		setTargetEntity(getEntityMappings().getClassForName(getTargetEntityName()));
    	} else {
    		setTargetEntity(void.class);
    	}
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents a 1-1 primary key relationship.
     */
	public boolean isOneToOnePrimaryKeyRelationship() {
        return isOneToOne() && hasPrimaryKeyJoinColumns();
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
     * Process a @JoinColumns or @JoinColumn. Will look for association
     * overrides.
     */	
    protected List<JoinColumnMetadata> processJoinColumns() { 
        if (getDescriptor().hasAssociationOverrideFor(getAttributeName())) {
            return processJoinColumns(new JoinColumnsMetadata(getDescriptor().getAssociationOverrideFor(getAttributeName()).getJoinColumns()), getReferenceDescriptor());
        } else {
        	if (m_joinColumns == null || m_joinColumns.isEmpty()) {
        		// Process the join columns from annotations.
        	    Annotation joinColumn = getAnnotation(JoinColumn.class);
        	    Annotation joinColumns = getAnnotation(JoinColumns.class);
            
        		return processJoinColumns(new JoinColumnsMetadata(joinColumns, joinColumn), getReferenceDescriptor());
        	} else {
        		// Process the join columns from XML.
        		return processJoinColumns(new JoinColumnsMetadata(m_joinColumns), getReferenceDescriptor());
        	}
        }
    }
    
    /**
     * INTERNAL:
     * 
     * Process JoinColumnsMetadata.
     */	
    protected List<JoinColumnMetadata> processJoinColumns(JoinColumnsMetadata joinColumns, MetadataDescriptor descriptor) {
    	List<JoinColumnMetadata> jColumns = joinColumns.values(descriptor);
        
        if (descriptor.hasCompositePrimaryKey()) {
            // The number of join columns should equal the number of primary key fields.
            if (jColumns.size() != descriptor.getPrimaryKeyFields().size()) {
            	throw ValidationException.incompleteJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
            }
            
            // All the primary and foreign key field names should be specified.
            for (JoinColumnMetadata jColumn : jColumns) {
                if (jColumn.isPrimaryKeyFieldNotSpecified() || jColumn.isForeignKeyFieldNotSpecified()) {
                	throw ValidationException.incompleteJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
                }
            }
        } else {
            if (jColumns.size() > 1) {
            	throw ValidationException.excessiveJoinColumnsSpecified(getAnnotatedElement(), getJavaClass());
            }
        }
        
        return jColumns;
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
            if (getDescriptor().hasMappingForAttributeName(getAttributeName())) {
                // Only true if there is one that came from Project.xml
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPING, this);
            } else {
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
        if(type.equals(CascadeType.ALL)) {
            mapping.setCascadeAll(true);
        }else if(type.equals(CascadeType.MERGE)) {
            mapping.setCascadeMerge(true);
        }else if(type.equals(CascadeType.PERSIST)) {
            mapping.setCascadePersist(true);
        }else if(type.equals(CascadeType.REFRESH)) {
            mapping.setCascadeRefresh(true);
        }else if(type.equals(CascadeType.REMOVE)) {
            mapping.setCascadeRemove(true);
        }
    }
    
    /**
     * INTERNAL:
     */
    public void setCascadeTypes(Enum[] cascadeTypes) {
    	m_cascadeTypes = new CascadeTypes(cascadeTypes);
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
    public void setMappedBy(String mappedBy) {
    	m_mappedBy = mappedBy;
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
    	Enum fetchType = getFetch();
    	
    	if (fetchType == null) {
    		fetchType = getDefaultFetchType();
    	}
    	
        return fetchType.equals(FetchType.LAZY);
    }
}
