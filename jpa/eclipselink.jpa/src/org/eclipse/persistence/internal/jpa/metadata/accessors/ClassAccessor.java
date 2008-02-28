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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ExcludeDefaultListeners;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.ReadOnly;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataField;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;

import org.eclipse.persistence.internal.jpa.metadata.cache.CacheMetadata;
import org.eclipse.persistence.internal.jpa.metadata.changetracking.ChangeTrackingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.DiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.inheritance.InheritanceMetadata;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityClassListenerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

import org.eclipse.persistence.internal.jpa.metadata.locking.OptimisticLockingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;

import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;

import org.eclipse.persistence.internal.jpa.metadata.tables.SecondaryTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLAttributes;

/**
 * A class accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class ClassAccessor extends NonRelationshipAccessor {
	private boolean m_excludeDefaultListeners;
    private boolean m_excludeSuperclassListeners;
    private Boolean m_metadataComplete; // EntityMappings init will process this value.
    private Boolean m_readOnly;
    
    private CacheMetadata m_cache;
    private ChangeTrackingMetadata m_changeTracking;
    private Class m_customizerClass;
    private DiscriminatorColumnMetadata m_discriminatorColumn;
    private InheritanceMetadata m_inheritance;
    
    private List<AssociationOverrideMetadata> m_associationOverrides;
    private List<AttributeOverrideMetadata> m_attributeOverrides;
    private List<NamedQueryMetadata> m_namedQueries;
    private List<NamedNativeQueryMetadata> m_namedNativeQueries;
    private List<SecondaryTableMetadata> m_secondaryTables;
    private List<SQLResultSetMappingMetadata> m_sqlResultSetMappings;
    private List<EntityListenerMetadata> m_entityListeners;
    
    private OptimisticLockingMetadata m_optimisticLocking;
    
    private String m_access; // EntityMappings init will process this value.
    private String m_className;
    private String m_customizerClassName;
    private String m_description;
    private String m_discriminatorValue;
    private String m_entityName;
    private String m_idClassName;
    private String m_mappingFile;
	private String m_prePersist;
	private String m_postPersist;
	private String m_preRemove;
	private String m_postRemove;
	private String m_preUpdate;
	private String m_postUpdate;
	private String m_postLoad;
	
	private TableMetadata m_table;
	private XMLAttributes m_attributes;
	
    /**
     * INTERNAL:
     */
    public ClassAccessor() {}
    
    /**
     * INTERNAL:
     */
    public ClassAccessor(Class cls, MetadataProject project) {
    	super(new MetadataClass(cls), new MetadataDescriptor(cls), project);
    	
    	// Set the class accessor reference on the descriptor.
    	getDescriptor().setClassAccessor(this);
    }
    
    /**
     * INTERNAL:
     * Called from MappedSuperclassAccessor. We want to avoid setting the
     * class accessor on the descriptor to be the MappedSuperclassAccessor.
     */
    protected ClassAccessor(Class cls, MetadataDescriptor descriptor, MetadataProject project) {
    	super(new MetadataClass(cls), descriptor, project);
    }
    
    /**
     * INTERNAL:
	 * Add multiple fields to the descriptor. Called from either Inheritance 
     * or SecondaryTable context.
	 */
    protected void addMultipleTableKeyFields(PrimaryKeyJoinColumnsMetadata primaryKeyJoinColumns, DatabaseTable sourceTable, DatabaseTable targetTable, String PK_CTX, String FK_CTX) {
        // ProcessPrimaryKeyJoinColumns will validate the primary key join
        // columns passed in and will return a list of 
        // PrimaryKeyJoinColumnMetadata.
        for (PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn : processPrimaryKeyJoinColumns(primaryKeyJoinColumns)) {
            // In an inheritance case this call will return the pk field on the
            // root class of the inheritance hierarchy. Otherwise in a secondary
            // table case it's the primary key field name off our own descriptor.
            String defaultPKFieldName = getDescriptor().getPrimaryKeyFieldName();

            DatabaseField pkField = primaryKeyJoinColumn.getPrimaryKeyField();
            pkField.setName(getName(pkField, defaultPKFieldName, PK_CTX));
            pkField.setTable(sourceTable);

            DatabaseField fkField = primaryKeyJoinColumn.getForeignKeyField();
            fkField.setName(getName(fkField, pkField.getName(), FK_CTX));
            fkField.setTable(targetTable);

            // Add the foreign key field to the descriptor.
            getDescriptor().addForeignKeyFieldForMultipleTable(fkField, pkField);
        }
    }
    
    /**
     * INTERNAL:
     * Create and return the appropriate accessor based on the accessible 
     * object given. Order of checking is important, careful when modifying
     * or adding, check what the isXyz call does to determine if the accessor
     * is of type xyz.
     */
    protected MetadataAccessor buildAccessor(MetadataAccessibleObject accessibleObject) {
        MetadataAccessor accessor = getDescriptor().getAccessorFor(accessibleObject.getAttributeName());
        
        if (accessor == null) {
            if (MetadataHelper.isBasicCollection(accessibleObject, getDescriptor())) {
                return new BasicCollectionAccessor(accessibleObject, this);
            } else if (MetadataHelper.isBasicMap(accessibleObject, getDescriptor())) {
                return new BasicMapAccessor(accessibleObject, this);
            } else if (MetadataHelper.isId(accessibleObject, getDescriptor())) {
            	return new IdAccessor(accessibleObject, this);
            } else if (MetadataHelper.isVersion(accessibleObject, getDescriptor())) {
            	return new VersionAccessor(accessibleObject, this);
            } else if (MetadataHelper.isBasic(accessibleObject, getDescriptor())) {
                return new BasicAccessor(accessibleObject, this);
            } else if (MetadataHelper.isEmbedded(accessibleObject, getDescriptor())) {
                return new EmbeddedAccessor(accessibleObject, this);
            } else if (MetadataHelper.isEmbeddedId(accessibleObject, getDescriptor())) {
                return new EmbeddedIdAccessor(accessibleObject, this);
            } else if (MetadataHelper.isManyToMany(accessibleObject, getDescriptor())) {
                return new ManyToManyAccessor(accessibleObject, this);
            } else if (MetadataHelper.isManyToOne(accessibleObject, getDescriptor())) {
                return new ManyToOneAccessor(accessibleObject, this);
            } else if (MetadataHelper.isOneToMany(accessibleObject, getDescriptor())) {
                // A OneToMany can currently default, that is, doesn't require
                // an annotation to be present.
                return new OneToManyAccessor(accessibleObject, this);
            } else if (MetadataHelper.isOneToOne(accessibleObject, getDescriptor())) {
                // A OneToOne can currently default, that is, doesn't require
                // an annotation to be present.
                return new OneToOneAccessor(accessibleObject, this);
            } else {
                // Default case (everything else currently falls into this)
                return new BasicAccessor(accessibleObject, this);
            }
        } else {
            return accessor;
        }
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
    public String getAccess() {
    	return m_access;
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
    public XMLAttributes getAttributes() {
    	return m_attributes;
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
    public DiscriminatorColumnMetadata getDiscriminatorColumn() {
        return m_discriminatorColumn;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDiscriminatorValue() {
    	return m_discriminatorValue;
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
    public String getEntityName() {
    	return m_entityName;
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
	public String getIdClassName() {
		return m_idClassName;
	}
	
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public InheritanceMetadata getInheritance() {
        return m_inheritance;
    }
    
    /**
     * INTERNAL: (OVERRIDE)
     * Return the java class that defines this accessor. It may be an
     * entity, embeddable or mapped superclass.
     */
    public Class getJavaClass() {
        return (Class) getAnnotatedElement();
    }
    
    /**
     * INTERNAL:
     * Return the java class name that defines this accessor. It may be an
     * entity, embeddable or mapped superclass.
     */
    public String getJavaClassName() {
    	return getJavaClass().getName();
    }
    
    /**
     * INTERNAL:
     * Build a list of classes that are decorated with a MappedSuperclass
     * annotation or that are tagged as a mapped-superclass in an XML document.
     */
	public List<MappedSuperclassAccessor> getMappedSuperclasses() {
		// The list is currently rebuilt every time this method is called since 
		// it is potentially called both during pre-deploy and deploy where
		// where the class loader dependencies change.
        ArrayList<MappedSuperclassAccessor> mappedSuperclasses = new ArrayList<MappedSuperclassAccessor>();
        Class parent = getJavaClass().getSuperclass();
        
        while (parent != Object.class) {
        	if (getDescriptor().isInheritanceSubclass() && getProject().hasEntity(parent)) {
        		// In an inheritance case we don't want to keep looking
                // for mapped superclasses if they are not directly above
                // us before the next entity in the hierarchy.
                break;
        	} else {
        		MappedSuperclassAccessor accessor = getProject().getMappedSuperclass(parent);

                // If the mapped superclass was not defined in XML then check 
        		// for a MappedSuperclass annotation.
                if (accessor == null) {
                	if (MetadataHelper.isAnnotationPresent(MappedSuperclass.class, parent)) {
                		mappedSuperclasses.add(new MappedSuperclassAccessor(parent, getDescriptor(), getProject()));
                	}
                } else {
                	mappedSuperclasses.add(accessor.getEntityMappings().reloadMappedSuperclass(accessor, getDescriptor(), getJavaClass().getClassLoader()));
                }
        	}
                
            parent = parent.getSuperclass();
        }
                
        return mappedSuperclasses;
    }
	
    /**
     * INTERNAL:
     */
    public String getMappingFile() {
    	return m_mappingFile;
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
     * Check to see if this is a valid method to process for persistence. More
     * validation is done on the method to ensure it is a valid persistence
     * method. User specified means we either have annotation on the method
     * or the method was referenced in XML. Otherwise, we are looking for
     * defaults (and therefore should avoid throwing exceptions). Callers of
     * this method must handle a null return.
     */
    protected MetadataMethod getMetadataMethod(Method method, boolean userSpecified) {
    	MetadataMethod metadataMethod = null;
    	
    	// Look for methods that begin with "get" or "is", ignore all others.
    	if (MetadataHelper.isValidPersistenceMethodName(method.getName())) {
    		// Check if the method has parameters.
    		if (method.getParameterTypes().length > 0) {
    			if (userSpecified) {
    				throw ValidationException.mappingMetadataAppliedToMethodWithArguments(method, getJavaClass());
    			}
    		} else {
    			Method setMethod = MetadataHelper.getSetMethod(method, getJavaClass());
           
    			if (setMethod == null) {
    				if (userSpecified) {
    					throw ValidationException.noCorrespondingSetterMethodDefined(getJavaClass(), method);
    				}
    			} else {
    				metadataMethod = new MetadataMethod(method, setMethod);
    			}
    		}
    	}
        
    	return metadataMethod;
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
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<SecondaryTableMetadata> getSecondaryTables() {
    	return m_secondaryTables;
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
    public TableMetadata getTable() {
    	return m_table;
    }
    
    /**
     * INTERNAL: 
     * Return true if this class has an inheritance specifications.
     */
    protected boolean hasInheritance() {
    	if (m_inheritance == null) {
    		return isAnnotationPresent(Inheritance.class);
    	} else {
    		return true;
    	}
    }
    
    /**
     * INTERNAL:
     * This method will do a couple things. Most importantly will return 
     * true if this class is an inheritance subclass, but will also set the
     * inheritance parent descriptor (the first parent entity that defines the 
     * inheritance strategy) on this class accessor's descriptor along
     * with the immediate parent class.
     */
    public boolean isInheritanceSubclass() {
    	ClassAccessor lastParent = null;
        Class parent = getJavaClass().getSuperclass();
    
        while (parent != Object.class) {
        	ClassAccessor parentAccessor = getProject().getAccessor(parent.getName());
        	
            if (parentAccessor != null) {
                if (lastParent == null) {
                    // Set the immediate parent class on the descriptor.
                    getDescriptor().setParentClass(parent);
                }
                
                lastParent = parentAccessor;
                
                if (parentAccessor.hasInheritance()) {
                	break; // stop looking.
                }
            } 
            
            parent = parent.getSuperclass();
        }
    
        if (lastParent == null) {
        	return false;
        } else {
        	getDescriptor().setInheritanceParentDescriptor(lastParent.getDescriptor());	
        	return true;
        }
    }
    
    /**
     * INTERNAL:
     */
    public boolean isMetadataComplete() {
    	return m_metadataComplete != null && m_metadataComplete;
    }

    /**
     * INTERNAL:
     * Return true is this annotated element is not transient, static or 
     * abstract.
     */
    protected boolean isValidPersistenceElement(AnnotatedElement annotatedElement, int modifiers) {  
        return ! (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers));
    }
    
    /**
     * INTERNAL: (Overridden in EmbeddableAccessor and MappedSuperclassAccessor)
     * Process the items of interest on an entity class. The order of processing 
     * is important, care must be taken if changes must be made.
     */
    public void process() {
        // Process the Entity metadata.
        processEntity();
            
        // Process the Table and Inheritance metadata.
        processTableAndInheritance();
            
        // Process the common class level attributes that an entity or
        // mapped superclass may define. This should be done before the
        // processMappedSuperclasses call since it will call this method 
        // also. We want to be able to grab the metadata off the actual 
        // entity class first because it needs to override any settings 
        // from the mapped superclass and may need to log a warning.
        processClassMetadata();
    	
        // Process the MappedSuperclass(es) metadata.
        processMappedSuperclasses();
        
        // Process the accessors on this entity.
        processAccessors();
            
        // Validate we found a primary key.
        validatePrimaryKey();
            
        // Validate the optimistic locking setting.
        validateOptimisticLocking();
            
        // Process those items that depend on a primary key now ...
            
        // Process any BasicCollection and BasicMap metadata we found.
        processBasicCollectionAccessors();
            
        // Process the SecondaryTable(s) metadata.
        processSecondaryTables();
    }
    
    /**
     * INTERNAL:
     * Process an accessor method or field. Relationship accessors will be 
     * stored for later processing.
     */
    protected void processAccessor(MetadataAccessor accessor) {
        if (! accessor.isProcessed()) {
            // Store the accessor for later retrieval.
        	getDescriptor().addAccessor(accessor);
        
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
        	if (isAnnotationPresent(Transient.class, field)) {
                if (MetadataHelper.getDeclaredAnnotationsCount(field, getDescriptor()) > 1) {
                	throw ValidationException.mappingAnnotationsAppliedToTransientAttribute(field);
                }
            } else {
            	if (isValidPersistenceElement(field, field.getModifiers())) {
            		processAccessor(buildAccessor(new MetadataField(field)));
            	} else {
            		// The field is either marked static, transient or abstract.
            		if (MetadataHelper.getDeclaredAnnotationsCount(field, getDescriptor()) > 0) {
            			// The user decorated the field with other annotations,
            			// throw an exception.
            			throw ValidationException.mappingMetadataAppliedToInvalidAttribute(field, getJavaClass());
            		}
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
        	if (isAnnotationPresent(Transient.class, method)) {
                if (MetadataHelper.getDeclaredAnnotationsCount(method, getDescriptor()) > 1) {
                	throw ValidationException.mappingAnnotationsAppliedToTransientAttribute(method);
                }
            } else {
            	if (isValidPersistenceElement(method, method.getModifiers())) {
            		MetadataMethod metadataMethod = getMetadataMethod(method, MetadataHelper.getDeclaredAnnotationsCount(method, getDescriptor()) > 0);
                	
                	if (metadataMethod != null) {
                		// We have a valid metadata method.
                		processAccessor(buildAccessor(metadataMethod));
                	}
            	} else {
            		// The method is either marked static, transient or abstract.
            		if (MetadataHelper.getDeclaredAnnotationsCount(method, getDescriptor()) > 0) {
            			// The user decorated the field with other annotations,
            			// throw an exception.
            			throw ValidationException.mappingMetadataAppliedToInvalidAttribute(method, getJavaClass());
            		}
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
    		for (MetadataAccessor accessor : m_attributes.getAccessors()) {
    			// Load the accessible object from the class.
    			MetadataAccessibleObject accessibleObject;
    		
    			if (getDescriptor().usesPropertyAccess()) {
    				Method method = MetadataHelper.getMethodForPropertyName(accessor.getName(), getJavaClass());
    				
    				if (method == null) {
    					throw ValidationException.invalidPropertyForClass(accessor.getName(), getJavaClass());
    				}
    				
    				if (! isValidPersistenceElement(method, method.getModifiers())) {
    					throw ValidationException.mappingMetadataAppliedToInvalidAttribute(method, getJavaClass());
    				}
    				
    				accessibleObject = getMetadataMethod(method, true);	
    			} else {
    				Field field = MetadataHelper.getFieldForName(accessor.getName(), getJavaClass());
                
    				if (field == null) {
    					throw ValidationException.invalidFieldForClass(accessor.getName(), getJavaClass());
    				}
    				
    				if (! isValidPersistenceElement(field, field.getModifiers())) {
    					throw ValidationException.mappingMetadataAppliedToInvalidAttribute(field, getJavaClass());
    				}
                
    				accessibleObject = new MetadataField(field);
    			}
            
    			accessor.init(accessibleObject, this);
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
     * INTERNAL:
     * Process the association override metadata specified on an entity or 
     * mapped superclass. Once the association overrides are processed from
     * XML process the association overrides from annotations. This order of
     * processing must be maintained.
     */
    protected void processAssociationOverrides() {
        // Process the XML association override elements first.
    	if (m_associationOverrides != null) {
    		for (AssociationOverrideMetadata associationOverride : m_associationOverrides) {
    			// Set the extra metadata needed for processing that could not
    			// be set during OX loading.
    			associationOverride.setJavaClassName(getJavaClassName());
    			associationOverride.setLocation(getEntityMappings().getMappingFileName());
    			
    			// Process the association override.
    			processAssociationOverride(associationOverride);
    		}
    	}
        
        // Process the association override annotations second.
    	// Look for an @AssociationOverrides.
        Object associationOverrides = getAnnotation(AssociationOverrides.class);
        if (associationOverrides != null) {
            for (Object associationOverride : (Object[])invokeMethod("value", associationOverrides)) {
                processAssociationOverride(new AssociationOverrideMetadata((Annotation) associationOverride, getJavaClassName()));
            }
        }
        
        // Look for an @AssociationOverride.
        Object associationOverride = getAnnotation(AssociationOverride.class);
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
     * INTERNAL:
	 * Process the attribute override metadata specified on an entity or 
     * mapped superclass. Once the attribute overrides are processed from
     * XML process the attribute overrides from annotations. This order of 
     * processing must be maintained.
	 */
    protected void processAttributeOverrides() {
    	// Process the XML attribute overrides first.
    	if (m_attributeOverrides != null) {
    		for (AttributeOverrideMetadata attributeOverride : m_attributeOverrides) {
    			// Set the extra metadata needed for processing that could not
    			// be set during OX loading.
    			attributeOverride.setLocation(getEntityMappings().getMappingFileName());
    			attributeOverride.setJavaClassName(getJavaClassName());    			
    			attributeOverride.getColumn().setAttributeName(attributeOverride.getName());
    			
    			// Process the attribute override.
    			processAttributeOverride(attributeOverride);
    		}
    	}
        
        // Process the attribute override annotations second.
    	// Look for an @AttributeOverrides.
        Annotation attributeOverrides = getAnnotation(AttributeOverrides.class);	
        if (attributeOverrides != null) {
            for (Annotation attributeOverride : (Annotation[])invokeMethod("value", attributeOverrides)){ 
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
     * Process a cache metadata. 
     */
    protected void processCache() {
        if (m_cache != null || isAnnotationPresent(Cache.class)) {
            if (getDescriptor().isEmbeddable()) {
                throw ValidationException.cacheNotSupportedWithEmbeddable(getJavaClass());
            } else if (getDescriptor().isInheritanceSubclass()) {
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
     * Process the change tracking setting for this accessor.
     */
    protected void processChangeTracking() {
        if (m_changeTracking != null || isAnnotationPresent(ChangeTracking.class)) {
            if (getDescriptor().hasChangeTracking()) {    
                // We must be processing a mapped superclass setting for an
                // entity that has its own change tracking setting. Ignore it 
                // and log a warning.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CHANGE_TRACKING, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_changeTracking == null) {
                    new ChangeTrackingMetadata(getAnnotation(ChangeTracking.class)).process(getDescriptor());
                } else {
                    if (isAnnotationPresent(ChangeTracking.class)) {
                        getLogger().logWarningMessage(MetadataLogger.IGNORE_CHANGE_TRACKING_ANNOTATION, getDescriptor().getJavaClass(), getJavaClass());
                    }
                    
                    m_changeTracking.process(getDescriptor());
                }
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
    }
    
    /**
     * INTERNAL:
     */
    protected void processCustomizer() {
        if (m_customizerClassName != null || isAnnotationPresent(Customizer.class)) {
            if (getDescriptor().hasCustomizer()) {
                // We must be processing a mapped superclass and its subclass
                // override the customizer class, that is, defined its own. Log 
                // a warning that we are ignoring the Customizer metadata on the 
                // mapped superclass for the descriptor's java class.
                getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CUSTOMIZER, getDescriptor().getJavaClass(), getJavaClass());
            } else {
                if (m_customizerClassName == null) { 
                    // Look for an annotation
                    Annotation customizer = getAnnotation(Customizer.class);
                    
                    if (customizer != null) {
                        m_customizerClass = (Class) invokeMethod("value", customizer);
                        getProject().addAccessorWithCustomizer(this);
                    }
                } else {
                    if (isAnnotationPresent(Customizer.class)) {
                        getLogger().logWarningMessage(MetadataLogger.IGNORE_CUSTOMIZER_ANNOTATION, getDescriptor().getJavaClass(), getJavaClass());
                    }
                    
                    m_customizerClass = getEntityMappings().getClassForName(m_customizerClassName);
                    getProject().addAccessorWithCustomizer(this);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the default listeners defined in XML. This method will process 
     * the class for additional lifecycle callback methods that are decorated 
     * with annotations.
     * 
     * NOTE: We add the default listeners regardless if the exclude default 
     * listeners flag is set. This allows the user to change the exclude flag 
     * at runtime and have the default listeners available to them.
     */
    protected void processDefaultListeners() {
        for (EntityListenerMetadata defaultListener : getProject().getDefaultListeners().values()) {
        	// We need to clone the default listeners. Can't re-use the 
        	// same one for all the entities in the persistence unit.
        	EntityListenerMetadata listener = (EntityListenerMetadata) defaultListener.clone();
        	
        	// Initialize the listener class
        	listener.setEntityClass(getJavaClass());
        	listener.initializeListenerClass(MetadataHelper.getClassForName(listener.getClassName(), getJavaClass().getClassLoader()));
                
            // Process the lifecycle callback events from XML.
        	Method[] candidateMethods = MetadataHelper.getCandidateCallbackMethodsForDefaultListener(listener);
        	
        	// Process the callback methods defined in XML
        	processCallbackMethodNames(candidateMethods, listener);
                
            // Process the candidate callback methods on this listener for
            // additional callback methods decorated with annotations.
            processCallbackMethods(candidateMethods, listener);
        
            // Add the listener to the descriptor.
            getDescriptor().addDefaultEventListener(listener);
		}
    }
	
    /**
     * INTERNAL:
	 * Process a discriminator value to set the class indicator on the root 
     * descriptor of the inheritance hierarchy. 
     * 
     * If there is no discriminator value, the class indicator defaults to 
     * the class name.
	 */
	protected void processDiscriminatorValue() {
        if (! Modifier.isAbstract(getJavaClass().getModifiers())) {
            // Add the indicator to the inheritance root class' descriptor. The
            // default is the short class name.
            if (m_discriminatorValue == null) {
            	Annotation discriminatorValue = getAnnotation(DiscriminatorValue.class);
                
                if (discriminatorValue == null) {
                	getDescriptor().addClassIndicator(getJavaClass(), Helper.getShortClassName(getJavaClassName()));
                } else {
                	getDescriptor().addClassIndicator(getJavaClass(),(String) invokeMethod("value", discriminatorValue)); 
                }
            } else {
            	getDescriptor().addClassIndicator(getJavaClass(), m_discriminatorValue);
            }  
        }
    }
    
    /**
	 * INTERNAL:
	 * Process an entity.
	 */
	protected void processEntity() {
        // Don't override existing alias.
        if (getDescriptor().getAlias().equals("")) {
            String alias;
            if (m_entityName == null) {
            	Annotation entity = getAnnotation(Entity.class);
                alias = (entity == null) ? "" : (String)invokeMethod("name", entity);
            } else {
                alias = m_entityName;
            }
            
            if (alias.equals("")) {
                alias = Helper.getShortClassName(getJavaClassName());
                getLogger().logConfigMessage(MetadataLogger.ALIAS, getDescriptor(), alias);
            }

            // Verify that the alias is not a duplicate.
            ClassDescriptor existingDescriptor = getProject().getSession().getProject().getDescriptorForAlias(alias);
            if (existingDescriptor != null) {
            	throw ValidationException.nonUniqueEntityName(existingDescriptor.getJavaClassName(), getDescriptor().getJavaClassName(), alias);
            }

            // Set the alias on the descriptor and add it to the project.
            getDescriptor().setAlias(alias);
            getProject().getSession().getProject().addAlias(alias, getDescriptor().getClassDescriptor());
        }
	}
    
    /**
     * INTERNAL:
     * Process the entity class for lifecycle callback event methods.
     */
    public EntityListenerMetadata processEntityEventListener() {
    	EntityClassListenerMetadata listener = new EntityClassListenerMetadata(this);
        
        // Build a list of candidate callback methods.
        Method[] candidateMethods = MetadataHelper.getCandidateCallbackMethodsForEntityClass(getJavaClass());
        
        // 1 - Process the callback methods as defined in XML.
        processCallbackMethodNames(candidateMethods, listener);
        
        // 2 - Check the entity class for lifecycle callback annotations.
        processCallbackMethods(candidateMethods, listener);
        
        return listener;
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
            	for (Class entityListener : (Class[])invokeMethod("value", entityListeners)) {
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
            	pkClass = (Class)invokeMethod("value", idClass); 
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
    			String methodName = method.getName();
                    
    			if (MetadataHelper.isValidPersistenceMethodName(methodName)) {
    				getDescriptor().addPKClassId(MetadataHelper.getAttributeNameFromMethodName(methodName), MetadataHelper.getGenericReturnType(method));
    			}
    		}    
    	} else {
    		for (Field field : MetadataHelper.getFields(pkClass)) {
    			getDescriptor().addPKClassId(field.getName(), MetadataHelper.getGenericType(field));
    		}
    	} 
    }
    
    /**
     * INTERNAL:
     * Process the Inheritance metadata for a parent of an inheritance 
     * hierarchy. One may or may not be specified for the entity class that is 
     * the root of the entity class hierarchy, so we need to default in this 
     * case. This method should only be called on the root of the inheritance 
     * hierarchy.
     */
    protected void processInheritance() {
        // Process the inheritance metadata.
        if (m_inheritance == null) {
            new InheritanceMetadata(getAnnotation(Inheritance.class)).process(getDescriptor(), getJavaClass());
        } else {
            m_inheritance.process(getDescriptor(), getJavaClass());
        }
                    
        // Process the discriminator column metadata.
        if (m_discriminatorColumn == null) {
            new DiscriminatorColumnMetadata(getAnnotation(DiscriminatorColumn.class)).process(getDescriptor(), getAnnotatedElementName());
        } else {
            // Future log a warning if we are ignoring an annotation.
            m_discriminatorColumn.process(getDescriptor(), getAnnotatedElementName());
        }
                    
        // Process the discriminator value metadata.
        processDiscriminatorValue();
    }
    
    /**
     * INTERNAL:
     * 
     * Process the inheritance metadata for an inheritance subclass. The
     * parent descriptor must be provided.
     */
    protected void processInheritanceSubclass(MetadataDescriptor parentDescriptor) {
    	// Inheritance.stategy() = SINGLE_TABLE, set the flag. Unless this
        // descriptor has its own inheritance.
        if (parentDescriptor.usesSingleTableInheritanceStrategy() && ! hasInheritance()) {
        	getDescriptor().setSingleTableInheritanceStrategy();
        } else {
        	// Inheritance.stategy() = JOINED, look for primary key join 
            // column(s) and add multiple table key fields.
            PrimaryKeyJoinColumnsMetadata pkJoinColumns;
            	
            if (getPrimaryKeyJoinColumns() == null) {
            	// Look for annotations.
                Annotation primaryKeyJoinColumn = getAnnotation(PrimaryKeyJoinColumn.class);
                Annotation primaryKeyJoinColumns = getAnnotation(PrimaryKeyJoinColumns.class);
                    
                pkJoinColumns = new PrimaryKeyJoinColumnsMetadata(primaryKeyJoinColumns, primaryKeyJoinColumn);
            } else {
            	// Used what is specified in XML.
            	pkJoinColumns = new PrimaryKeyJoinColumnsMetadata(getPrimaryKeyJoinColumns());
            }
            
            addMultipleTableKeyFields(pkJoinColumns, getDescriptor().getPrimaryKeyTable(), getDescriptor().getPrimaryTable(), MetadataLogger.INHERITANCE_PK_COLUMN, MetadataLogger.INHERITANCE_FK_COLUMN);
        }    
            
        // Process the discriminator value, unless this descriptor has its own 
        // inheritance.
        if (! hasInheritance()) {
        	processDiscriminatorValue();
        }
            
        // If the root descriptor has an id class, we need to set the same id 
        // class on our descriptor.
        if (parentDescriptor.hasCompositePrimaryKey()) {
        	getDescriptor().setPKClass(parentDescriptor.getPKClassName());
        }
    }
    
    /**
     * INTERNAL:
     * Process and array of @JoinColumn into a list of metadata join column.
     */
    protected List<JoinColumnMetadata> processJoinColumns(JoinColumn[] joinColumns) {
        ArrayList<JoinColumnMetadata> list = new ArrayList<JoinColumnMetadata>();
        
        for (JoinColumn joinColumn : joinColumns) {
            list.add(new JoinColumnMetadata(joinColumn));
        }
        
        return list;
    }
   
    /**
     * INTERNAL:
     * Process the listeners for this class.
     */
    public void processListeners(ClassLoader loader) {
        // Step 1 - process the default listeners.
        processDefaultListeners();

        // Step 2 - process the entity listeners that are defined on the entity 
        // class and mapped superclasses (taking metadata-complete into 
        // consideration). Go through the mapped superclasses first, top->down 
        // only if the exclude superclass listeners flag is not set.    
        if (! getDescriptor().excludeSuperclassListeners()) {
            List<MappedSuperclassAccessor> mappedSuperclasses = getMappedSuperclasses();
            int mappedSuperclassesSize = mappedSuperclasses.size();
            
            for (int i = mappedSuperclassesSize - 1; i >= 0; i--) {
                mappedSuperclasses.get(i).processEntityListeners(getJavaClass());
            }
        }
        
        processEntityListeners(getJavaClass()); 
        
        // Step 3 - process the entity class for lifecycle callback methods. Go
        // through the mapped superclasses as well.
        EntityListenerMetadata listener = processEntityEventListener();
        
        if (! getDescriptor().excludeSuperclassListeners()) {
            for (ClassAccessor mappedSuperclass : getMappedSuperclasses()) {
                mappedSuperclass.processMappedSuperclassEventListener(listener, getJavaClass(), loader);
            }
        }
        
        // Add the listener only if we actually found callback methods.
        if (listener.hasCallbackMethods()) {
            getDescriptor().setEntityEventListener(listener);
        }
    }
    
    /**
     * INTERNAL:
     * Process the MappedSuperclass(es) if there are any. There may be
     * several MappedSuperclasses for any given Entity.
     */
    protected void processMappedSuperclasses() {
        for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
            mappedSuperclass.process();
        }
    }
    
    /**
     * INTERNAL:
     * Process the mapped superclass class for lifecycle callback event methods.
     */
    public void processMappedSuperclassEventListener(EntityListenerMetadata listener, Class entityClass, ClassLoader classLoader) {
    	// Process the lifecycle callback events from XML.
        Method[] candidateMethods = MetadataHelper.getCandidateCallbackMethodsForMappedSuperclass(getJavaClass(), entityClass);
        
        // Process the XML defined callback methods.
        processCallbackMethodNames(candidateMethods, listener);
        
    	// Check the mapped superclass for lifecycle callback annotations.
        processCallbackMethods(candidateMethods, listener);
    }
    
    /**
     * INTERNAL:
     * Process the named native queries on this accessor.
     */
    protected void processNamedNativeQueries() {
    	// Process the XML named native queries first.
    	if (m_namedNativeQueries != null) {
    		getEntityMappings().processNamedNativeQueries(m_namedNativeQueries, getJavaClassName());
    	}
        
        // Process the named native query annotations second.
    	// Look for a @NamedNativeQueries.
    	Annotation namedNativeQueries = getAnnotation(NamedNativeQueries.class);
        if (namedNativeQueries != null) {
            for (Annotation namedNativeQuery : (Annotation[])invokeMethod("value", namedNativeQueries)) { 
            	getProject().processNamedNativeQuery(new NamedNativeQueryMetadata(namedNativeQuery, getJavaClassName()));
            }
        }
        
        // Look for a @NamedNativeQuery.
        Annotation namedNativeQuery = getAnnotation(NamedNativeQuery.class);
        if (namedNativeQuery != null) {
        	getProject().processNamedNativeQuery(new NamedNativeQueryMetadata(namedNativeQuery, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL:
     * Process the named queries on this accessor.
     */
    protected void processNamedQueries() {
		// Process the XML named queries first.
    	if (m_namedQueries != null) {
    		getEntityMappings().processNamedQueries(m_namedQueries, getJavaClassName());
    	}
    	
        // Process the named query annotations second.
    	// Look for a @NamedQueries.
    	Annotation namedQueries = getAnnotation(NamedQueries.class);
        
        if (namedQueries != null) {
            for (Annotation namedQuery : (Annotation[])invokeMethod("value", namedQueries)) { 
            	getProject().processNamedQuery(new NamedQueryMetadata(namedQuery, getJavaClassName()));
            }
        }
        
        // Look for a @NamedQuery.
        Annotation namedQuery = getAnnotation(NamedQuery.class);
        
        if (namedQuery != null) {
        	getProject().processNamedQuery(new NamedQueryMetadata(namedQuery, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL:
     * Process the NamedStoredProcedureQueries. The method will also look for 
     * a NamedStoredProcedureQuery. This method currently only stores the 
     * queries if there are some. The actually query processing isn't done 
     * till addNamedQueriesToSession is called.
     */
    protected void processNamedStoredProcedureQueries() {
        // Look for a @NamedStoredProcedureQueries.
        Annotation namedStoredProcedureQueries = getAnnotation(NamedStoredProcedureQueries.class);
        if (namedStoredProcedureQueries != null) {
            for (Annotation namedStoredProcedureQuery : (Annotation[])invokeMethod("value", namedStoredProcedureQueries)) { 
                getProject().processNamedStoredProcedureQuery(new NamedStoredProcedureQueryMetadata(namedStoredProcedureQuery, getJavaClass()));
            }
        }
        
        // Look for a @NamedStoredProcedureQuery.
        Annotation namedStoredProcedureQuery = getAnnotation(NamedStoredProcedureQuery.class);
        if (namedStoredProcedureQuery != null) {
            getProject().processNamedStoredProcedureQuery(new NamedStoredProcedureQueryMetadata(namedStoredProcedureQuery, getJavaClass()));
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
     * Process a MetadataSecondaryTable. Do all the table name defaulting and 
     * set the correct, fully qualified name on the TopLink DatabaseTable.
     */
    protected void processSecondaryTable(SecondaryTableMetadata secondaryTable) {
        // Process any table defaults and log warning messages.
        processTable(secondaryTable, secondaryTable.getName());
        
        // Add the table to the descriptor.
        getDescriptor().addTable(secondaryTable.getDatabaseTable());
        
        // Get the primary key join column(s) and add the multiple table key fields.
        addMultipleTableKeyFields(new PrimaryKeyJoinColumnsMetadata(secondaryTable.getPrimaryKeyJoinColumns()), getDescriptor().getPrimaryTable(), secondaryTable.getDatabaseTable(), MetadataLogger.SECONDARY_TABLE_PK_COLUMN, MetadataLogger.SECONDARY_TABLE_FK_COLUMN);
    }
    
    /**
     * INTERNAL:
     * Process secondary-table(s) for a given entity.
     */
    protected void processSecondaryTables() {
    	if (m_secondaryTables == null || m_secondaryTables.isEmpty()) {
    		// Look for a SecondaryTables annotation.
    	    Annotation secondaryTables = getAnnotation(SecondaryTables.class);
            if (secondaryTables != null) {
            	for (Annotation secondaryTable : (Annotation[])invokeMethod("value", secondaryTables)) { 
            		processSecondaryTable(new SecondaryTableMetadata(secondaryTable, getJavaClassName()));
                }
            } else {
                // Look for a SecondaryTable annotation
                Annotation secondaryTable = getAnnotation(SecondaryTable.class);
                if (secondaryTable != null) {    
                	processSecondaryTable(new SecondaryTableMetadata(secondaryTable, getJavaClassName()));
                }
            }
    	} else {
    		if (isAnnotationPresent(SecondaryTables.class) || isAnnotationPresent(SecondaryTable.class)) {
    			getLogger().logWarningMessage(MetadataLogger.IGNORE_SECONDARY_TABLE_ANNOTATION, getJavaClass());
    		}
    		
    		for (SecondaryTableMetadata secondaryTable : m_secondaryTables) {
    			// Set the location of this secondary table
    			secondaryTable.setLocation(getJavaClassName());
                	
                processSecondaryTable(secondaryTable);
            }
    	}
    }
    
    /**
     * INTERNAL:
     * Process the sql result set mappings for the given class which could be 
     * an entity or a mapped superclass.
     */
    protected void processSqlResultSetMappings() {
		// Process the XML sql result set mapping elements first.
    	if (m_sqlResultSetMappings != null) {
    		getEntityMappings().processSqlResultSetMappings(m_sqlResultSetMappings);
    	}
        
        // Process the sql result set mapping query annotations second.
        // Look for a @SqlResultSetMappings.
    	Annotation sqlResultSetMappings = getAnnotation(SqlResultSetMappings.class);

        if (sqlResultSetMappings != null) {
            for (Annotation sqlResultSetMapping : (Annotation[])invokeMethod("value", sqlResultSetMappings)) {
                getProject().processSqlResultSetMapping(new SQLResultSetMappingMetadata(sqlResultSetMapping));
            }
        } else {
            // Look for a @SqlResultSetMapping.
            Annotation sqlResultSetMapping = getAnnotation(SqlResultSetMapping.class);
            
            if (sqlResultSetMapping != null) {
                getProject().processSqlResultSetMapping(new SQLResultSetMappingMetadata(sqlResultSetMapping));
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process table information for the given metadata descriptor.
     */
    protected void processTable() {
    	if (m_table == null) {
    		// Check for a table annotation. If no annotation is defined, the 
            // table will default.
    	    Annotation table = getAnnotation(Table.class);
            processTable(new TableMetadata(table, getJavaClassName()));
    	} else {
    		if (isAnnotationPresent(Table.class)) {
    			getLogger().logWarningMessage(MetadataLogger.IGNORE_TABLE_ANNOTATION, getJavaClass());
    		}
    		
            // Set the location of this table.
            m_table.setLocation(getJavaClassName());
            processTable(m_table);                
	    }
    }
    
    /**
     * INTERNAL:
	 * Process a MetadataTable. Do all the table name defaulting and set the
     * correct, fully qualified name on the TopLink DatabaseTable.
	 */
    protected void processTable(TableMetadata table) {
        // Process any table defaults and log warning messages.
        processTable(table, getDescriptor().getDefaultTableName());
        
        // Set the table on the descriptor.
        getDescriptor().setPrimaryTable(table.getDatabaseTable());
    }
    
    /**
     * INTERNAL:
     * 
     * Process any inheritance specifics. This method will fast track any 
     * parent inheritance processing, be it specified or defaulted.
     */
    protected void processTableAndInheritance() {
        // If we are an inheritance subclass, ensure our parent is processed 
        // first since it has information its subclasses depend on.
		if (isInheritanceSubclass()) {
            MetadataDescriptor parentDescriptor = getDescriptor().getInheritanceParentDescriptor();
            
            // Process the parent class accessor if it hasn't already been done.
            ClassAccessor parentAccessor = parentDescriptor.getClassAccessor();
            if (! parentAccessor.isProcessed()) {
            	parentAccessor.process();
            	parentAccessor.setIsProcessed();
            }
            
            // A parent, who didn't know they were a parent (a root class of an 
            // inheritance hierarchy that does not have an  @Inheritance 
            // annotation or XML tag) must process and default the inheritance 
            // parent metadata.
            //if (! parentDescriptor.hasInheritance()) {
            if (! parentAccessor.hasInheritance()) {	
                parentAccessor.processInheritance();
            }
                
            // If this entity has inheritance metadata as well, then the 
            // inheritance stragety is mixed and we need to process the 
            // inheritance parent metadata for this entity's subclasses to 
            // process correctly.
            if (hasInheritance()) {
                // Process the table metadata if there is one, otherwise default.
                processTable();
                
                // Process the parent inheritance specifics.
                processInheritance();
                
                // Process the inheritance subclass metadata.
                processInheritanceSubclass(parentDescriptor);
            } else {
                // Process the table information for this descriptor (for a 
                // joined strategy), if there is one specified. Must be called
                // before processing the inheritance metadata.
                if (parentDescriptor.usesJoinedInheritanceStrategy()) {
                    processTable();
                }
                
                // Process the inheritance subclass metadata.
                processInheritanceSubclass(parentDescriptor);
            }
		} else {
            // Process the table metadata if there is one, otherwise default.
            processTable();
            
            // If we have inheritance metadata, then process it now. If we are 
            // an inheritance root class that doesn't know it, a subclass will 
            // force this processing to occur.
            if (hasInheritance()) {
                processInheritance();
            }
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
    public void setAttributes(XMLAttributes attributes) {
    	m_attributes = attributes;
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
    public void setDiscriminatorColumn(DiscriminatorColumnMetadata discriminatorColumn) {
        m_discriminatorColumn = discriminatorColumn;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDiscriminatorValue(String discriminatorValue) {
        m_discriminatorValue = discriminatorValue;
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
    public void setEntityName(String entityName) {
        m_entityName = entityName;
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
	public void setIdClassName(String idClassName) {
		m_idClassName = idClassName;
	}
	
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setInheritance(InheritanceMetadata inheritance) {
    	m_inheritance = inheritance;
    }
    
    /**
     * INTERNAL:
     * Return the annotated element for this accessor.
     */
    public void setJavaClass(Class cls) {
        setAnnotatedElement(cls);
        getDescriptor().setJavaClass(cls);
    }
    
    /**
     * INTERNAL:
     */
    public void setMappingFile(String mappingFile) {
    	m_mappingFile = mappingFile;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMetadataComplete(Boolean metadataComplete) {
    	m_metadataComplete = metadataComplete;
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
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSecondaryTables(List<SecondaryTableMetadata> secondaryTables) {
    	m_secondaryTables = secondaryTables;
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
    public void setTable(TableMetadata table) {
    	m_table = table;
    }
    
    /**
     * INTERNAL: 
     * Validate a OptimisticLocking(type=VERSION_COLUMN) setting. That is,
     * validate that we found a version field. If not, throw an exception.
     */
    protected void validateOptimisticLocking() {
        if (getDescriptor().usesVersionColumnOptimisticLocking() && ! getDescriptor().usesOptimisticLocking()) {
        	throw ValidationException.optimisticLockingVersionElementNotSpecified(getJavaClass());
        }
    }
    
    /**
     * INTERNAL:
     * 
     * Call this method after a primary key should have been found.
     */
    protected void validatePrimaryKey() {
        // If this descriptor has a composite primary key, check that all 
        // our composite primary key attributes were validated. 
        if (getDescriptor().hasCompositePrimaryKey()) {
            if (getDescriptor().pkClassWasNotValidated()) {
            	throw ValidationException.invalidCompositePKSpecification(getJavaClass(), getDescriptor().getPKClassName());
            }
        } else {
            // Descriptor has a single primary key. Validate an id 
            // attribute was found, unless we are an inheritance subclass
            // or an aggregate descriptor.
            if (! getDescriptor().hasPrimaryKeyFields() && ! getDescriptor().isInheritanceSubclass()) {
            	throw ValidationException.noPrimaryKeyAnnotationsFound(getJavaClass());
            }
        }  
    }
}
