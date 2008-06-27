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
 *     Guy Pelletier - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     05/23/2008-1.0M8 Guy Pelletier 
 *       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
 *     05/30/2008-1.0M8 Guy Pelletier 
 *       - 230213: ValidationException when mapping to attribute in MappedSuperClass
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.DiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.inheritance.InheritanceMetadata;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityClassListenerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.SecondaryTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;

/**
 * An entity accessor.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class EntityAccessor extends MappedSuperclassAccessor {
    private DiscriminatorColumnMetadata m_discriminatorColumn;
    private InheritanceMetadata m_inheritance;
    
    // TODO: Since we allow the annotations of these to be defined on a
    // mapped superclass, we should change the schema to allow that as well
    // and move these attributes up to MappedSuperclass. And remove their 
    // respective processing methods from this class.
    private List<AssociationOverrideMetadata> m_associationOverrides = new ArrayList<AssociationOverrideMetadata>();
    private List<AttributeOverrideMetadata> m_attributeOverrides = new ArrayList<AttributeOverrideMetadata>();
    private List<NamedQueryMetadata> m_namedQueries = new ArrayList<NamedQueryMetadata>();
    private List<NamedNativeQueryMetadata> m_namedNativeQueries = new ArrayList<NamedNativeQueryMetadata>();
    private List<NamedStoredProcedureQueryMetadata> m_namedStoredProcedureQueries = new ArrayList<NamedStoredProcedureQueryMetadata>();
    private List<SQLResultSetMappingMetadata> m_sqlResultSetMappings = new ArrayList<SQLResultSetMappingMetadata>();
    private List<SecondaryTableMetadata> m_secondaryTables = new ArrayList<SecondaryTableMetadata>();
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    
    private SequenceGeneratorMetadata m_sequenceGenerator;
 
    private String m_discriminatorValue;
    private String m_entityName;
    
    private TableGeneratorMetadata m_tableGenerator;
    private TableMetadata m_table;
    
    /**
     * INTERNAL:
     */
    public EntityAccessor() {
        super("<entity>");
    }
    
    /**
     * INTERNAL:
     */
    public EntityAccessor(Annotation annotation, Class cls, MetadataProject project) {
        super(annotation, cls, project);
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
    public String getEntityName() {
        return m_entityName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public InheritanceMetadata getInheritance() {
        return m_inheritance;
    }
    
    /**
     * INTERNAL:
     * Build a list of classes that are decorated with a MappedSuperclass
     * annotation or that are tagged as a mapped-superclass in an XML document.
     * 
     * This method will also do a couple other things as well since we are
     * traversing the parent classes:
     *  - Build a map of generic types specified and will be used to resolve 
     *    actual class types for mappings.
     *  - Will discover and set the inheritance parent and root descriptors
     *    if this entity is part of an inheritance hierarchy.
     * 
     * Note: The list is rebuilt every time this method is called since
     * it is called both during pre-deploy and deploy where the class loader
     * dependencies change.
     */
    protected List<MappedSuperclassAccessor> getMappedSuperclasses() {
        Class parent = getJavaClass().getSuperclass();
        ClassAccessor lastParent = null;
        Type genericParent = getJavaClass().getGenericSuperclass();
        ArrayList<MappedSuperclassAccessor> mappedSuperclasses = new ArrayList<MappedSuperclassAccessor>();
        
        // Null out the inheritance parent and root descriptor before we start
        // since they will be recalculated and used to determine when to stop
        // looking for mapped superclasses.
        getDescriptor().setInheritanceParentDescriptor(null);
        getDescriptor().setInheritanceRootDescriptor(null);
        
        while (parent != Object.class) {
            EntityAccessor parentAccessor = getProject().getEntityAccessor(parent.getName());
            
            // We found a parent entity.
            if (parentAccessor != null) {
                if (lastParent == null) {
                    // Set the immediate parent's descriptor and class on this
                    // entity's descriptor.
                    getDescriptor().setInheritanceParentDescriptor(parentAccessor.getDescriptor());
                    getDescriptor().setParentClass(parent);
                }
                
                lastParent = parentAccessor;
                
                if (parentAccessor.hasInheritance()) {
                    break; // stop traversing the inheritance hierarchy.
                }
            }
            
            // In an inheritance case we don't want to look at mapped 
            // superclasses if they are not directly above us before the next 
            // entity in the hierarchy.
            if (! getDescriptor().isInheritanceSubclass()) {
                // If we have a generic parent we need to grab our generic types
                // that may be used (and therefore need to be resolved) to map
                // accessors correctly.
                if (genericParent instanceof ParameterizedType) {
                    Type[] actualTypeArguments = ((ParameterizedType) genericParent).getActualTypeArguments();
                    TypeVariable[] typeVariables = ((Class) ((ParameterizedType) genericParent).getRawType()).getTypeParameters();
                    
                    for (int i = 0; i < actualTypeArguments.length; i++) {
                        Type actualTypeArgument = actualTypeArguments[i];
                        TypeVariable variable = typeVariables[i];
                        
                        // We are building bottom up and need to link up
                        // any TypeVariables with the actual class from the
                        // originating entity.
                        if (actualTypeArgument instanceof TypeVariable) {
                            getDescriptor().addGenericType(variable.getName(), getDescriptor().getGenericType(((TypeVariable) actualTypeArgument).getName())); 
                        } else {
                            getDescriptor().addGenericType(variable.getName(), actualTypeArgument);
                        }
                    }
                }
                
                MappedSuperclassAccessor accessor = getProject().getMappedSuperclass(parent);

                // If the mapped superclass was not defined in XML then check 
                // for a MappedSuperclass annotation.
                if (accessor == null) {
                    MetadataClass metadataClass = new MetadataClass(parent);
                    if (metadataClass.isAnnotationPresent(MappedSuperclass.class)) {
                        mappedSuperclasses.add(new MappedSuperclassAccessor(metadataClass.getAnnotation(MappedSuperclass.class), parent, getDescriptor(), getProject()));
                    }
                } else {
                    mappedSuperclasses.add(initXMLMappedSuperclass(accessor, getDescriptor()));
                }
            }
            
            genericParent = parent.getGenericSuperclass();
            parent = parent.getSuperclass();
        }

        // Set our root descriptor of the inheritance hierarchy.
        if (lastParent != null) {
            getDescriptor().setInheritanceRootDescriptor(lastParent.getDescriptor()); 
        }
        
        return mappedSuperclasses;
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
    public List<PrimaryKeyJoinColumnMetadata> getPrimaryKeyJoinColumns() {
        return m_primaryKeyJoinColumns;
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
    public TableMetadata getTable() {
        return m_table;
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
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
        
        // Initialize single objects.
        initXMLObject(m_inheritance, accessibleObject);
        initXMLObject(m_discriminatorColumn, accessibleObject);
        initXMLObject(m_sequenceGenerator, accessibleObject);
        initXMLObject(m_tableGenerator, accessibleObject);
        initXMLObject(m_table, accessibleObject);
        
        // Initialize lists of objects.
        initXMLObjects(m_associationOverrides, accessibleObject);
        initXMLObjects(m_attributeOverrides, accessibleObject);
        initXMLObjects(m_namedQueries, accessibleObject);
        initXMLObjects(m_namedNativeQueries, accessibleObject);
        initXMLObjects(m_namedStoredProcedureQueries, accessibleObject);
        initXMLObjects(m_sqlResultSetMappings, accessibleObject);
        initXMLObjects(m_secondaryTables, accessibleObject);
        initXMLObjects(m_primaryKeyJoinColumns, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Entity level merging details.
     */
    @Override
    public void merge(ORMetadata metadata) {
        super.merge(metadata);
        
        EntityAccessor accessor = (EntityAccessor) metadata;
        
        // Simple object merging.
        m_entityName = (String) mergeSimpleObjects(m_entityName, accessor.getEntityName(), accessor.getAccessibleObject(), "@name");
        m_discriminatorValue = (String) mergeSimpleObjects(m_discriminatorValue, accessor.getDiscriminatorValue(), accessor.getAccessibleObject(), "<discriminator-value>");
        
        // ORMetadata object merging.
        m_discriminatorColumn = (DiscriminatorColumnMetadata) mergeORObjects(m_discriminatorColumn, accessor.getDiscriminatorColumn());
        m_inheritance = (InheritanceMetadata) mergeORObjects(m_inheritance, accessor.getInheritance());
        m_sequenceGenerator = (SequenceGeneratorMetadata) mergeORObjects(m_sequenceGenerator, accessor.getSequenceGenerator());
        m_tableGenerator = (TableGeneratorMetadata) mergeORObjects(m_tableGenerator, accessor.getTableGenerator());
        m_table = (TableMetadata) mergeORObjects(m_table, accessor.getTable());
        
        // ORMetadata list merging. 
        m_namedQueries = mergeORObjectLists(m_namedQueries, accessor.getNamedQueries());
        m_namedNativeQueries = mergeORObjectLists(m_namedNativeQueries, accessor.getNamedNativeQueries());
        m_namedStoredProcedureQueries = mergeORObjectLists(m_namedStoredProcedureQueries, accessor.getNamedStoredProcedureQueries());
        m_sqlResultSetMappings = mergeORObjectLists(m_sqlResultSetMappings, accessor.getSqlResultSetMappings());
        m_associationOverrides = mergeORObjectLists(m_associationOverrides, accessor.getAssociationOverrides());
        m_attributeOverrides = mergeORObjectLists(m_attributeOverrides, accessor.getAttributeOverrides());
        m_secondaryTables = mergeORObjectLists(m_secondaryTables, accessor.getSecondaryTables());
        m_primaryKeyJoinColumns = mergeORObjectLists(m_primaryKeyJoinColumns, accessor.getPrimaryKeyJoinColumns());
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on an entity class. The order of processing 
     * is important, care must be taken if changes must be made.
     */
    @Override
    public void process() {
        // Build our list of mapped superclasses and pass them around to
        // those methods that depend on them. Avoids rebuilding the list
        // multiple times.
        List<MappedSuperclassAccessor> mappedSuperclasses = getMappedSuperclasses();
        
        // Process the Entity metadata first. Need to ensure we determine the 
        // access, metadata complete and exclude default mappings before we 
        // process further.
        processEntity(mappedSuperclasses);
            
        // Process the Table and Inheritance metadata.
        processTableAndInheritance();
            
        // Process the common class level attributes that an entity or mapped 
        // superclass may define. This should be done before processing the
        // mapped superclasses call since it will call this method also. We 
        // want to be able to grab the metadata off the actual entity class 
        // first because it needs to override any settings from the mapped 
        // superclass and may need to log a warning.
        processClassMetadata();
        
        // Process the MappedSuperclass(es) metadata now. There may be
        // several MappedSuperclasses for any given Entity.
        for (MappedSuperclassAccessor mappedSuperclass : mappedSuperclasses) {
            mappedSuperclass.process();
        }
        
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
     * Process the association override metadata specified on an entity or 
     * mapped superclass. Once the association overrides are processed from
     * XML process the association overrides from annotations. This order of
     * processing must be maintained.
     */
    @Override
    protected void processAssociationOverrides() {
        // Process the XML association override elements first.
        for (AssociationOverrideMetadata associationOverride : m_associationOverrides) {
            // Set the extra metadata needed for processing that could not
            // be set during OX loading.
            associationOverride.setJavaClassName(getJavaClassName());
                
            // Process the association override.
            processAssociationOverride(associationOverride);
        }
        
        // Call the super class to look for annotations.
        super.processAssociationOverrides();
    }
    
    /**
     * INTERNAL:
     * Process the attribute override metadata specified on an entity or 
     * mapped superclass. Once the attribute overrides are processed from
     * XML process the attribute overrides from annotations. This order of 
     * processing must be maintained.
     */
    @Override
    protected void processAttributeOverrides() {
        // Process the XML attribute overrides first.
        for (AttributeOverrideMetadata attributeOverride : m_attributeOverrides) {
            // Set the extra metadata needed for processing that could not
            // be set during OX loading.
            attributeOverride.setJavaClassName(getJavaClassName());             
            attributeOverride.getColumn().setAttributeName(attributeOverride.getName());
                
            // Process the attribute override.
            processAttributeOverride(attributeOverride);
        }
        
        super.processAttributeOverrides();
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
                    getDescriptor().addClassIndicator(getJavaClass(),(String) MetadataHelper.invokeMethod("value", discriminatorValue)); 
                }
            } else {
                getDescriptor().addClassIndicator(getJavaClass(), m_discriminatorValue);
            }  
        }
    }
    
    /**
     * INTERNAL:
     * Process the entity metadata.
     */
    protected void processEntity(List<MappedSuperclassAccessor> mappedSuperclasses) {
        // Set an access type if specified on the entity class or a mapped
        // superclass.
        if (getAccess() != null) {
            getDescriptor().setXMLAccess(getAccess());
        } else {
            for (MappedSuperclassAccessor mappedSuperclass : mappedSuperclasses) {
                if (mappedSuperclass.getAccess() != null) {
                    getDescriptor().setXMLAccess(mappedSuperclass.getAccess());
                    break; // stop searching.
                }
            }
        }
        
        // Set a metadata complete flag if specified on the entity class or a 
        // mapped superclass.
        if (getMetadataComplete() != null) {
            getDescriptor().setIgnoreAnnotations(isMetadataComplete());
        } else {
            for (MappedSuperclassAccessor mappedSuperclass : mappedSuperclasses) {
                if (mappedSuperclass.getMetadataComplete() != null) {
                    getDescriptor().setIgnoreAnnotations(mappedSuperclass.isMetadataComplete());
                    break; // stop searching.
                }
            }
        }
        
        // Set an exclude default mappings flag if specified on the entity class
        // or a mapped superclass.
        if (getExcludeDefaultMappings() != null) {
            getDescriptor().setIgnoreDefaultMappings(excludeDefaultMappings());
        } else {
            for (MappedSuperclassAccessor mappedSuperclass : mappedSuperclasses) {
                if (mappedSuperclass.getExcludeDefaultMappings() != null) {
                    getDescriptor().setIgnoreDefaultMappings(mappedSuperclass.excludeDefaultMappings());
                    break; // stop searching.
                }
            }
        }
        
        // Process the entity name (alias) and default if necessary.
        if (m_entityName == null) {
            m_entityName = (getAnnotation(Entity.class) == null) ? "" : (String) MetadataHelper.invokeMethod("name", getAnnotation(Entity.class));
        }
            
        if (m_entityName.equals("")) {
            m_entityName = Helper.getShortClassName(getJavaClassName());
            getLogger().logConfigMessage(MetadataLogger.ALIAS, getDescriptor(), m_entityName);
        }

        getProject().addAlias(m_entityName, getDescriptor());
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
            new InheritanceMetadata(getAnnotation(Inheritance.class), getAccessibleObject()).process(getDescriptor());
        } else {
            m_inheritance.process(getDescriptor());
        }
                    
        // Process the discriminator column metadata.
        if (m_discriminatorColumn == null) {
            getDescriptor().setClassIndicatorField(new DiscriminatorColumnMetadata(getAnnotation(DiscriminatorColumn.class), getAccessibleObject()).process(getDescriptor(), getAnnotatedElementName()));
        } else {
            // Future log a warning if we are ignoring an annotation.
            getDescriptor().setClassIndicatorField(m_discriminatorColumn.process(getDescriptor(), getAnnotatedElementName()));
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
                
            if (m_primaryKeyJoinColumns.isEmpty()) {
                // Look for annotations.
                Annotation primaryKeyJoinColumn = getAnnotation(PrimaryKeyJoinColumn.class);
                Annotation primaryKeyJoinColumns = getAnnotation(PrimaryKeyJoinColumns.class);
                    
                pkJoinColumns = new PrimaryKeyJoinColumnsMetadata(primaryKeyJoinColumns, primaryKeyJoinColumn, getAccessibleObject());
            } else {
                // Used what is specified in XML.
                pkJoinColumns = new PrimaryKeyJoinColumnsMetadata(m_primaryKeyJoinColumns);
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
     * Process the listeners for this entity.
     */
    public void processListeners(ClassLoader loader) {
        // Step 1 - process the default listeners that were defined in XML.
        // Default listeners process the class for additional lifecycle 
        // callback methods that are decorated with annotations.
        // NOTE: We add the default listeners regardless if the exclude default 
        // listeners flag is set. This allows the user to change the exclude 
        // flag at runtime and have the default listeners available to them.
        for (EntityListenerMetadata defaultListener : getProject().getDefaultListeners()) {
            // We need to clone the default listeners. Can't re-use the 
            // same one for all the entities in the persistence unit.
            ((EntityListenerMetadata) defaultListener.clone()).process(getDescriptor(), loader, true);
        }

        // Step 2 - process the entity listeners that are defined on the entity 
        // class and mapped superclasses (taking metadata-complete into 
        // consideration). Go through the mapped superclasses first, top->down 
        // only if the exclude superclass listeners flag is not set.
        List<MappedSuperclassAccessor> mappedSuperclasses = getMappedSuperclasses();
        if (! getDescriptor().excludeSuperclassListeners()) {
            int mappedSuperclassesSize = mappedSuperclasses.size();
            
            for (int i = mappedSuperclassesSize - 1; i >= 0; i--) {
                mappedSuperclasses.get(i).processEntityListeners(loader);
            }
        }
        
        processEntityListeners(loader); 
        
        // Step 3 - process the entity class for lifecycle callback methods. Go
        // through the mapped superclasses as well.
        new EntityClassListenerMetadata(this).process(mappedSuperclasses);
    }
    
    /**
     * INTERNAL:
     * Process/collect the named native queries on this accessor and add them
     * to the project for later processing.
     */
    @Override
    protected void processNamedNativeQueries() {
        // Process the XML named native queries first.
        for (NamedNativeQueryMetadata namedNativeQuery : m_namedNativeQueries) {
            getProject().addQuery(namedNativeQuery);
        }
        
        // Process the named native query annotations second.
        super.processNamedNativeQueries();
    }
    
    /**
     * INTERNAL:
     * Process/collect the named queries on this accessor and add them to the 
     * project for later processing.
     */
    @Override
    protected void processNamedQueries() {
        // Process the XML named queries first.
        for (NamedQueryMetadata namedQuery : m_namedQueries) {
            getProject().addQuery(namedQuery);
        }
        
        // Process the named query annotations second.
        super.processNamedQueries();
    }
    
    /**
     * INTERNAL:
     * Process/collect the named stored procedure queries on this accessor and 
     * add them to the project for later processing.
     */
    @Override
    protected void processNamedStoredProcedureQueries() {
        // Process the XML named queries first.
        for (NamedStoredProcedureQueryMetadata namedStoredProcedureQuery : m_namedStoredProcedureQueries) {
            getProject().addQuery(namedStoredProcedureQuery);
        }
        
        // Process the named stored procedure query annotations second.
        super.processNamedStoredProcedureQueries();
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
        Annotation secondaryTable = getAnnotation(SecondaryTable.class);
        Annotation secondaryTables = getAnnotation(SecondaryTables.class);
        
        if (m_secondaryTables.isEmpty()) {
            // Look for a SecondaryTables annotation.
            if (secondaryTables != null) {
                for (Annotation table : (Annotation[]) MetadataHelper.invokeMethod("value", secondaryTables)) { 
                    processSecondaryTable(new SecondaryTableMetadata(table, getAccessibleObject()));
                }
            } else {
                // Look for a SecondaryTable annotation
                if (secondaryTable != null) {    
                    processSecondaryTable(new SecondaryTableMetadata(secondaryTable, getAccessibleObject()));
                }
            }
        } else {
            if (secondaryTable != null) {
                getLogger().logWarningMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, secondaryTable, getJavaClassName(), getLocation());
            }
            
            if (secondaryTables != null) {
                getLogger().logWarningMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, secondaryTables, getJavaClassName(), getLocation());
            }
            
            for (SecondaryTableMetadata table : m_secondaryTables) {
                processSecondaryTable(table);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the sequence generator metadata and add it to the project. 
     */
    @Override
    protected void processSequenceGenerator() {
        // Process the xml defined sequence generator first.
        if (m_sequenceGenerator != null) {
            getProject().addSequenceGenerator(m_sequenceGenerator);
        }
        
        // Process the annotation defined sequence generator second.
        super.processSequenceGenerator();
    }
    
    /**
     * INTERNAL:
     * Process the sql result set mappings for the given class which could be 
     * an entity or a mapped superclass.
     */
    @Override
    protected void processSqlResultSetMappings() {
        // Process the XML sql result set mapping elements first.
        for (SQLResultSetMappingMetadata sqlResultSetMapping : m_sqlResultSetMappings) {
            getProject().addSQLResultSetMapping(sqlResultSetMapping);
        }
        
        // Process the sql result set mapping query annotations second.
        super.processSqlResultSetMappings();
    }
    
    /**
     * INTERNAL:
     * Process table information for the given metadata descriptor.
     */
    protected void processTable() {
        Annotation table = getAnnotation(Table.class);
        
        if (m_table == null) {
            // Check for a table annotation. If no annotation is defined, the 
            // table will default.
            processTable(new TableMetadata(table, getAccessibleObject()));
        } else {
            if (table != null) {
                getLogger().logWarningMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, table, getJavaClassName(), getLocation());
            }

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
     * Process any inheritance specifics. This method will fast track any 
     * parent inheritance processing, be it specified or defaulted.
     */
    protected void processTableAndInheritance() {
        // If we are an inheritance subclass, ensure our root is processed 
        // first since it has information its subclasses depend on.
        if (getDescriptor().isInheritanceSubclass()) {
            MetadataDescriptor parentDescriptor = getDescriptor().getInheritanceRootDescriptor();
            
            // Process the root class accessor if it hasn't already been done.
            EntityAccessor parentAccessor = (EntityAccessor) parentDescriptor.getClassAccessor();
            if (! parentAccessor.isProcessed()) {
                parentAccessor.process();
                parentAccessor.setIsProcessed();
            }
            
            // An entity who didn't know they were the root of an inheritance 
            // hierarchy (that is, does not define inheritance metadata) must 
            // process and default the inheritance metadata.
            if (! parentAccessor.hasInheritance()) {    
                parentAccessor.processInheritance();
            }
                
            // If this entity has inheritance metadata as well, then the 
            // inheritance strategy is mixed and we need to process the 
            // inheritance metadata to ensure this entity's subclasses process 
            // correctly.
            if (hasInheritance()) {
                // Process the table metadata if there is some, otherwise default.
                processTable();
                
                // Process the inheritance metadata.
                processInheritance();    
            } else {
                // Process the table metadata for this descriptor (for a 
                // joined strategy), if there is some. Must be called
                // before processing the inheritance subclass metadata.
                if (parentDescriptor.usesJoinedInheritanceStrategy()) {
                    processTable();
                }
            }
            
            // Process the inheritance subclass metadata.
            processInheritanceSubclass(parentDescriptor);
        } else {
            // Process the table metadata if there is some, otherwise default.
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
     * Process the table generator metadata and add it to the project. 
     */
    @Override
    protected void processTableGenerator() {
        // Process the xml defined table generator first.
        if (m_tableGenerator != null) {
            getProject().addTableGenerator(m_tableGenerator, getDescriptor().getXMLCatalog(), getDescriptor().getXMLSchema());
        }
        
        // Process the annotation defined table generator second.
        super.processTableGenerator();
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
    public void setEntityName(String entityName) {
        m_entityName = entityName;
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
     * Used for OX mapping.
     */
    public void setPrimaryKeyJoinColumns(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns) {
        m_primaryKeyJoinColumns = primaryKeyJoinColumns;
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
    public void setSecondaryTables(List<SecondaryTableMetadata> secondaryTables) {
        m_secondaryTables = secondaryTables;
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
    public void setTable(TableMetadata table) {
        m_table = table;
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
