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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;

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
    
    // Future: Since we allow the annotations of these to be defined on a
    // mapped superclass, we should change the schema to allow that as well
    // and move these attributes up to MappedSuperclass. And remove their 
    // respective processing methods from this class.
    private List<AssociationOverrideMetadata> m_associationOverrides;
    private List<AttributeOverrideMetadata> m_attributeOverrides;
    private List<NamedQueryMetadata> m_namedQueries;
    private List<NamedNativeQueryMetadata> m_namedNativeQueries;
    private List<NamedStoredProcedureQueryMetadata> m_namedStoredProcedureQueries;
    private List<SQLResultSetMappingMetadata> m_sqlResultSetMappings;
    private List<SecondaryTableMetadata> m_secondaryTables;
    
    private SequenceGeneratorMetadata m_sequenceGenerator;
 
    private String m_discriminatorValue;
    private String m_entityName;
    
    private TableGeneratorMetadata m_tableGenerator;
    private TableMetadata m_table;
    
    /**
     * INTERNAL:
     */
    public EntityAccessor() {}
    
    /**
     * INTERNAL:
     */
    public EntityAccessor(Class cls, MetadataProject project) {
        super(cls, project);
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
           EntityAccessor parentAccessor = getProject().getEntityAccessor(parent.getName());
            
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
     * INTERNAL: (Override from MappesSuperclassAccessor)
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
        
        // Call the super class to look for annotations.
        super.processAssociationOverrides();
    }
    
    /**
     * INTERNAL: (Override from MappedSuperclassAccessor)
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
        
        super.processAttributeOverrides();
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
            Method[] candidateMethods = MetadataHelper.getCandidateCallbackMethodsForEntityListener(listener);
            
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
                    getDescriptor().addClassIndicator(getJavaClass(),(String) MetadataHelper.invokeMethod("value", discriminatorValue)); 
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
                alias = (entity == null) ? "" : (String) MetadataHelper.invokeMethod("name", entity);
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
            for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
                mappedSuperclass.processEntityEventListener(listener, getJavaClass(), loader);
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
     * INTERNAL: (Override from MappedSuperclassAccessor)
     * Process/collect the named native queries on this accessor and add them
     * to the project for later processing.
     */
    protected void processNamedNativeQueries() {
        // Process the XML named native queries first.
        if (m_namedNativeQueries != null) {
            getEntityMappings().processNamedNativeQueries(m_namedNativeQueries, getJavaClassName());
        }
        
        // Process the named native query annotations second.
        super.processNamedNativeQueries();
    }
    
    /**
     * INTERNAL: (Override from MappedSuperclassAccessor)
     * Process/collect the named queries on this accessor and add them to the 
     * project for later processing.
     */
    protected void processNamedQueries() {
        // Process the XML named queries first.
        if (m_namedQueries != null) {
            getEntityMappings().processNamedQueries(m_namedQueries, getJavaClassName());
        }
        
        // Process the named query annotations second.
        super.processNamedQueries();
    }
    
    /**
     * INTERNAL: (Override from MappedSuperclassAccessor)
     * Process/collect the named stored procedure queries on this accessor and 
     * add them to the project for later processing.
     */
    protected void processNamedStoredProcedureQueries() {
        // Process the XML named queries first.
        if (m_namedStoredProcedureQueries != null) {
            getEntityMappings().processNamedStoredProcedureQueries(m_namedStoredProcedureQueries, getJavaClassName());
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
        if (m_secondaryTables == null || m_secondaryTables.isEmpty()) {
            // Look for a SecondaryTables annotation.
            Annotation secondaryTables = getAnnotation(SecondaryTables.class);
            if (secondaryTables != null) {
                for (Annotation secondaryTable : (Annotation[]) MetadataHelper.invokeMethod("value", secondaryTables)) { 
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
     * Process the sequence generator metadata and add it to the project. 
     */
    protected void processSequenceGenerator() {
        // Process the xml defined sequence generator first.
        if (m_sequenceGenerator != null) {
            getEntityMappings().processSequenceGenerator(m_sequenceGenerator, getJavaClassName());
        }
        
        // Process the annotation defined sequence generator second.
        super.processSequenceGenerator();
    }
    
    /**
     * INTERNAL: (Override from MappedSuperclassAccessor)
     * Process the sql result set mappings for the given class which could be 
     * an entity or a mapped superclass.
     */
    protected void processSqlResultSetMappings() {
        // Process the XML sql result set mapping elements first.
        if (m_sqlResultSetMappings != null) {
            getEntityMappings().processSqlResultSetMappings(m_sqlResultSetMappings);
        }
        
        // Process the sql result set mapping query annotations second.
        super.processSqlResultSetMappings();
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
            EntityAccessor parentAccessor = (EntityAccessor) parentDescriptor.getClassAccessor();
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
     * Process the table generator metadata and add it to the project. 
     */
    protected void processTableGenerator() {
        // Process the xml defined table generator first.
        if (m_tableGenerator != null) {
            getEntityMappings().processTableGenerator(m_tableGenerator, getDescriptor().getXMLCatalog(), getDescriptor().getXMLSchema(), getJavaClassName());
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
