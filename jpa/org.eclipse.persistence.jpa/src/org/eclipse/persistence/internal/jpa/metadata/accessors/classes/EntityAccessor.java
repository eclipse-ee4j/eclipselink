/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Guy Pelletier - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     05/23/2008-1.0M8 Guy Pelletier
//       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
//     05/30/2008-1.0M8 Guy Pelletier
//       - 230213: ValidationException when mapping to attribute in MappedSuperClass
//     06/20/2008-1.0M9 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     07/15/2008-1.0.1 Guy Pelletier
//       - 240679: MappedSuperclass Id not picked when on get() method accessor
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     10/01/2008-1.1 Guy Pelletier
//       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
//     12/12/2008-1.1 Guy Pelletier
//       - 249860: Implement table per class inheritance support.
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/03/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/24/2009-2.0 Guy Pelletier
//       - 270011: JPA 2.0 MappedById support
//     04/30/2009-2.0 Michael O'Brien
//       - 266912: JPA 2.0 Metamodel API (part of Criteria API)
//     06/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
//     06/25/2009-2.0 Michael O'Brien
//       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
//          in support of the custom descriptors holding mappings required by the Metamodel.
//          processAccessType() is now public and is overridden in the superclass
//     09/29/2009-2.0 Guy Pelletier
//       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
//     10/21/2009-2.0 Guy Pelletier
//       - 290567: mappedbyid support incomplete
//     12/2/2009-2.1 Guy Pelletier
//       - 296289: Add current annotation metadata support on mapped superclasses to EclipseLink-ORM.XML Schema
//     12/18/2009-2.1 Guy Pelletier
//       - 211323: Add class extractor support to the EclipseLink-ORM.XML Schema
//     04/09/2010-2.1 Guy Pelletier
//       - 307050: Add defaults for access methods of a VIRTUAL access type
//     05/04/2010-2.1 Guy Pelletier
//       - 309373: Add parent class attribute to EclipseLink-ORM
//     05/14/2010-2.1 Guy Pelletier
//       - 253083: Add support for dynamic persistence using ORM.xml/eclipselink-orm.xml
//     06/01/2010-2.1 Guy Pelletier
//       - 315195: Add new property to avoid reading XML during the canonical model generation
//     06/09/2010-2.0.3 Guy Pelletier
//       - 313401: shared-cache-mode defaults to NONE when the element value is unrecognized
//     06/14/2010-2.2 Guy Pelletier
//       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
//     06/18/2010-2.2 Guy Pelletier
//       - 300458: EclispeLink should throw a more specific exception than NPE
//     06/22/2010-2.2 Guy Pelletier
//       - 308729: Persistent Unit deployment exception when mappedsuperclass has no annotations but has lifecycle callbacks
//     07/05/2010-2.1.1 Guy Pelletier
//       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
//     08/11/2010-2.2 Guy Pelletier
//       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
//     09/03/2010-2.2 Guy Pelletier
//       - 317286: DB column lenght not in sync between @Column and @JoinColumn
//     09/16/2010-2.2 Guy Pelletier
//       - 283028: Add support for letting an @Embeddable extend a @MappedSuperclass
//     12/01/2010-2.2 Guy Pelletier
//       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification
//     01/04/2011-2.3 Guy Pelletier
//       - 330628: @PrimaryKeyJoinColumn(...) is not working equivalently to @JoinColumn(..., insertable = false, updatable = false)
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/05/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 3)
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 8)
//     07/03/2011-2.3.1 Guy Pelletier
//       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     10/25/2012-2.5 Guy Pelletier
//       - 3746888: JPA 2.1 Converter support
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     11/29/2012-2.5 Guy Pelletier
//       - 395406: Fix nightly static weave test errors
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     09 Jan 2013-2.5 Gordon Yorke
//       - 397772: JPA 2.1 Entity Graph Support
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
//     02/20/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     05/19/2014-2.6 Tomas Kraus
//       - 437578: @Cacheable annotation value is passed to CachePolicy for ENABLE_SELECTIVE and DISABLE_SELECTIVE shared cache mode
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.ClassExtractor;
import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;
import org.eclipse.persistence.annotations.VirtualAccessMethods;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.columns.DiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;

import org.eclipse.persistence.internal.jpa.metadata.graphs.NamedEntityGraphMetadata;
import org.eclipse.persistence.internal.jpa.metadata.inheritance.InheritanceMetadata;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityClassListenerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.AccessMethodsMetadata;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

import org.eclipse.persistence.internal.jpa.metadata.tables.IndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.SecondaryTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ACCESS_FIELD;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ACCESS_PROPERTY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CONVERT;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CONVERTS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENTITY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_DISCRIMINATOR_COLUMN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_DISCRIMINATOR_VALUE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_INHERITANCE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PRIMARY_KEY_JOIN_COLUMN;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_PRIMARY_KEY_JOIN_COLUMNS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_SECONDARY_TABLE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_SECONDARY_TABLES;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_TABLE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENTITY_GRAPH;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENTITY_GRAPHS;

/**
 * An entity accessor.
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
 * @since EclipseLink 1.0
 */
public class EntityAccessor extends MappedSuperclassAccessor {
    private Boolean m_cascadeOnDelete;

    private InheritanceMetadata m_inheritance;
    private DiscriminatorColumnMetadata m_discriminatorColumn;
    private PrimaryKeyForeignKeyMetadata m_primaryKeyForeignKey;

    private List<ConvertMetadata> m_converts = new ArrayList<ConvertMetadata>();
    private List<PrimaryKeyJoinColumnMetadata> m_primaryKeyJoinColumns = new ArrayList<PrimaryKeyJoinColumnMetadata>();
    private List<SecondaryTableMetadata> m_secondaryTables = new ArrayList<SecondaryTableMetadata>();
    private List<IndexMetadata> m_indexes = new ArrayList<IndexMetadata>();
    private List<NamedEntityGraphMetadata> m_namedEntityGraphs = new ArrayList<NamedEntityGraphMetadata>();

    private MetadataClass m_classExtractor;

    private String m_classExtractorName;
    private String m_discriminatorValue;
    private String m_entityName;

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
    public EntityAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataProject project) {
        super(annotation, cls, project);
    }

    /**
     * INTERNAL:
     * Add multiple fields to the descriptor. Called from either Inheritance
     * or SecondaryTable context.
     */
    protected void addMultipleTableKeyFields(List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns, DatabaseTable targetTable, String PK_CTX, String FK_CTX) {
        // ProcessPrimaryKeyJoinColumns will validate the primary key join
        // columns passed in and will return a list of PrimaryKeyJoinColumnMetadata.
        for (PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn : processPrimaryKeyJoinColumns(primaryKeyJoinColumns)) {
            // Look up the primary key field from the referenced column name.
            DatabaseField pkField = getReferencedField(primaryKeyJoinColumn.getReferencedColumnName(), getDescriptor(), PK_CTX);

            DatabaseField fkField = primaryKeyJoinColumn.getForeignKeyField(pkField);
            setFieldName(fkField, pkField.getName(), FK_CTX);
            fkField.setTable(targetTable);

            // Add the foreign key field to the descriptor.
            getDescriptor().addForeignKeyFieldForMultipleTable(fkField, pkField);
        }
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
     *  - save mapped-superclass descriptors on the project for later use
     *    by the Metamodel API
     *
     * Note: The list is rebuilt every time this method is called since
     * it is called both during pre-deploy and deploy where the class loader
     * dependencies change.
     */
    protected void discoverMappedSuperclassesAndInheritanceParents(boolean addMappedSuperclassAccessors) {
        // Clear any previous discovery.
        clearMappedSuperclassesAndInheritanceParents();

        EntityAccessor currentEntityAccessor = this;
        MetadataClass parentClass = getJavaClass().getSuperclass();
        List<String> genericTypes = getJavaClass().getGenericType();

        // We keep a list of potential subclass accessors to ensure they
        // have their root parent descriptor set correctly.
        List<EntityAccessor> subclassEntityAccessors = new ArrayList<EntityAccessor>();
        subclassEntityAccessors.add(currentEntityAccessor);

        if (! parentClass.isObject()) {
            while (parentClass != null && ! parentClass.isObject()) {
                if (getProject().hasEntity(parentClass)) {
                    // Our parent is an entity.
                    EntityAccessor parentEntityAccessor = getProject().getEntityAccessor(parentClass);

                    // Set the current entity's inheritance parent descriptor.
                    currentEntityAccessor.getDescriptor().setInheritanceParentDescriptor(parentEntityAccessor.getDescriptor());

                    // Update the current entity accessor.
                    currentEntityAccessor = parentEntityAccessor;

                    // Clear out any previous mapped superclasses and inheritance
                    // parents that were discovered. We're going to re-discover
                    // them now.
                    currentEntityAccessor.clearMappedSuperclassesAndInheritanceParents();

                    // If we found an entity with inheritance metadata, set the
                    // root descriptor on its subclasses.
                    if (currentEntityAccessor.hasInheritance()) {
                        for (EntityAccessor subclassEntityAccessor : subclassEntityAccessors) {
                            subclassEntityAccessor.getDescriptor().setInheritanceRootDescriptor(currentEntityAccessor.getDescriptor());
                        }

                        // Clear the subclass list, we'll keep looking but the
                        // inheritance strategy may have changed so we need to
                        // build a new list of subclasses.
                        subclassEntityAccessors.clear();
                    }

                    // Add the descriptor to the subclass list
                    subclassEntityAccessors.add(currentEntityAccessor);
                } else {
                    // Our parent might be a mapped superclass, check and add
                    // as needed.
                    currentEntityAccessor.addPotentialMappedSuperclass(parentClass, addMappedSuperclassAccessors);
                }

                // Resolve any generic types from the generic parent onto the
                // current entity accessor.
                currentEntityAccessor.resolveGenericTypes(genericTypes, parentClass);

                // Grab the generic types from the parent class.
                genericTypes = parentClass.getGenericType();

                // Finally, get the next parent and keep processing ...
                parentClass = parentClass.getSuperclass();
            }
        } else {
            // Resolve any generic types we have (we may be an inheritance root).
            currentEntityAccessor.resolveGenericTypes(genericTypes, parentClass);
        }

        // Set our root descriptor of the inheritance hierarchy on all the
        // subclass descriptors. The root may not have explicit inheritance
        // metadata therefore, make the current descriptor of the entity
        // hierarchy the root.
        if (! subclassEntityAccessors.isEmpty()) {
            for (EntityAccessor subclassEntityAccessor : subclassEntityAccessors) {
                if (subclassEntityAccessor != currentEntityAccessor) {
                    subclassEntityAccessor.getDescriptor().setInheritanceRootDescriptor(currentEntityAccessor.getDescriptor());
                }
            }
        }
    }


    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getCascadeOnDelete() {
        return m_cascadeOnDelete;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getClassExtractorName() {
        return m_classExtractorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ConvertMetadata> getConverts() {
        return m_converts;
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
    public List<IndexMetadata> getIndexes() {
        return m_indexes;
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
    public List<NamedEntityGraphMetadata> getNamedEntityGraphs() {
        return m_namedEntityGraphs;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public PrimaryKeyForeignKeyMetadata getPrimaryKeyForeignKey() {
        return m_primaryKeyForeignKey;
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
    public TableMetadata getTable() {
        return m_table;
    }

    /**
     * INTERNAL:
     * This method is a little involved since a class extractor is mutually
     * exclusive with a discriminator column.
     *
     * Within one xml file it is impossible to have both specified since they
     * are within a choice tag. However, if one is specified in the orm.xml
     * and the other in the eclipselink-orm.xml, after the merge both will be
     * set on this accessor, so we need to check which came from the
     * eclipselink-orm.xml because it is the one we need to use.
     */
    public boolean hasClassExtractor() {
        if (m_classExtractorName != null && m_discriminatorColumn != null) {
            // If we have both a classExtractorName and a discriminatorColumn
            // then the only way this is possible is if the user has both an
            // orm.xml and an eclipselink-orm.xml and one specifies the
            // discriminator column and the other specifies the class extractor.
            // We must use the one from the eclipselink-orm.xml.
            return ! m_discriminatorColumn.loadedFromEclipseLinkXML();
        } else if (m_classExtractorName != null) {
            // We have a class extractor name and we don't care where it came
            // from. It must be used and override any annotations.
            return true;
        } else if (m_discriminatorColumn != null) {
            // We have a discriminator column and we don't care where it came
            // from. It must be used and override any annotations.
            return false;
        } else {
            // Nothing was specified in XML. Must look at the annotations now.
            if (isAnnotationPresent(ClassExtractor.class) && (isAnnotationPresent(JPA_DISCRIMINATOR_COLUMN) || isAnnotationPresent(JPA_DISCRIMINATOR_VALUE))) {
                throw ValidationException.classExtractorCanNotBeSpecifiedWithDiscriminatorMetadata(getJavaClassName());
            }

            return isAnnotationPresent(ClassExtractor.class);
        }
    }

    /**
     * INTERNAL:
     * Return true if this class has an inheritance specifications.
     */
    public boolean hasInheritance() {
        if (m_inheritance == null) {
            return isAnnotationPresent(JPA_INHERITANCE);
        } else {
            return true;
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize simple class objects.
        m_classExtractor = initXMLClassName(m_classExtractorName);

        // Initialize single objects.
        initXMLObject(m_inheritance, accessibleObject);
        initXMLObject(m_discriminatorColumn, accessibleObject);
        initXMLObject(m_table, accessibleObject);
        initXMLObject(m_primaryKeyForeignKey, accessibleObject);

        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_secondaryTables, accessibleObject);
        initXMLObjects(m_primaryKeyJoinColumns, accessibleObject);
        initXMLObjects(m_indexes, accessibleObject);
        initXMLObjects(m_converts, accessibleObject);
        initXMLObjects(m_namedEntityGraphs, accessibleObject);
    }

    /**
     * INTERNAL:
     */
    public boolean isCascadeOnDelete() {
        return (m_cascadeOnDelete == null) ? isAnnotationPresent(CascadeOnDelete.class) : m_cascadeOnDelete.booleanValue();
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents an entity class.
     */
    @Override
    public boolean isEntityAccessor() {
        return true;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isMappedSuperclass() {
        return false;
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
        m_cascadeOnDelete = (Boolean) mergeSimpleObjects(m_cascadeOnDelete, accessor.getCascadeOnDelete(), accessor, "<cascade-on-delete>");
        m_entityName = (String) mergeSimpleObjects(m_entityName, accessor.getEntityName(), accessor, "@name");
        m_discriminatorValue = (String) mergeSimpleObjects(m_discriminatorValue, accessor.getDiscriminatorValue(), accessor, "<discriminator-value>");
        m_classExtractorName = (String) mergeSimpleObjects(m_classExtractorName, accessor.getClassExtractorName(), accessor, "<class-extractor>");

        // ORMetadata object merging.
        m_discriminatorColumn = (DiscriminatorColumnMetadata) mergeORObjects(m_discriminatorColumn, accessor.getDiscriminatorColumn());
        m_inheritance = (InheritanceMetadata) mergeORObjects(m_inheritance, accessor.getInheritance());
        m_table = (TableMetadata) mergeORObjects(m_table, accessor.getTable());
        m_primaryKeyForeignKey = (PrimaryKeyForeignKeyMetadata) mergeORObjects(m_primaryKeyForeignKey, accessor.getPrimaryKeyForeignKey());

        // ORMetadata list merging.
        m_secondaryTables = mergeORObjectLists(m_secondaryTables, accessor.getSecondaryTables());
        m_primaryKeyJoinColumns = mergeORObjectLists(m_primaryKeyJoinColumns, accessor.getPrimaryKeyJoinColumns());
        m_indexes = mergeORObjectLists(m_indexes, accessor.getIndexes());
        m_converts = mergeORObjectLists(m_converts, accessor.getConverts());
        m_namedEntityGraphs = mergeORObjectLists(m_namedEntityGraphs, accessor.getNamedEntityGraphs());
    }

    /**
     * INTERNAL:
     * The pre-process method is called during regular deployment and metadata
     * processing and will pre-process the items of interest on an entity class.
     *
     * The order of processing is important, care must be taken if changes must
     * be made.
     */
    @Override
    public void preProcess() {
        // If we are not already an inheritance subclass (meaning we were not
        // discovered through a subclass entity discovery) then perform the
        // discovery process before processing any further. We traverse the
        // chain upwards and we can not guarantee which entity will be processed
        // first in an inheritance hierarchy.
        if (! getDescriptor().isInheritanceSubclass()) {
            discoverMappedSuperclassesAndInheritanceParents(true);
        }

        // If we are an inheritance subclass process out root first.
        if (getDescriptor().isInheritanceSubclass()) {
            // Ensure our parent accessors are processed first. Top->down.
            // An inheritance subclass can no longer blindly inherit a default
            // access type from the root of the inheritance hierarchy since
            // that root may explicitly set an access type which applies only
            // to itself. The first entity in the hierarchy (going down) that
            // does not specify an explicit type will be used to determine
            // the default access type.
            EntityAccessor parentAccessor = (EntityAccessor) getDescriptor().getInheritanceParentDescriptor().getClassAccessor();
            if (! parentAccessor.isPreProcessed()) {
                parentAccessor.preProcess();
            }
        }

        // Process the correct access type before any other processing.
        processAccessType();

        // Process a virtual class specification after determining access type.
        processVirtualClass();

        // Process the default access methods after determining access type.
        processAccessMethods();

        // Process a @Struct and @EIS annotation to create the correct type of descriptor.
        processStruct();
        processNoSql();

        // Process our parents metadata after processing our own.
        super.preProcess();
    }

    /**
     * INTERNAL:
     * The pre-process for canonical model method is called (and only called)
     * during the canonical model generation. The use of this pre-process allows
     * us to remove some items from the regular pre-process that do not apply
     * to the canonical model generation.
     *
     * The order of processing is important, care must be taken if changes must
     * be made.
     */
    @Override
    public void preProcessForCanonicalModel() {
        setIsPreProcessed();

        // If we are not already an inheritance subclass (meaning we were not
        // discovered through a subclass entity discovery) then perform
        // the discovery process before processing any further. We traverse
        // the chain upwards and we can not guarantee which entity will be
        // processed first in an inheritance hierarchy.
        if (! getDescriptor().isInheritanceSubclass()) {
            discoverMappedSuperclassesAndInheritanceParents(false);
        }

        // If we are an inheritance subclass process out root first.
        if (getDescriptor().isInheritanceSubclass()) {
            // Ensure our parent accessors are processed first. Top->down.
            // An inheritance subclass can no longer blindly inherit a default
            // access type from the root of the inheritance hierarchy since
            // that root may explicitly set an access type which applies only
            // to itself. The first entity in the hierarchy (going down) that
            // does not specify an explicit type will be used to determine
            // the default access type.
            EntityAccessor parentAccessor = (EntityAccessor) getDescriptor().getInheritanceParentDescriptor().getClassAccessor();
            if (! parentAccessor.isPreProcessed()) {
                parentAccessor.preProcessForCanonicalModel();
            }
        }

        // Process our parents metadata after processing our own.
        super.preProcessForCanonicalModel();
    }

    /**
     * INTERNAL:
     * Process the items of interest on an entity class. The order of processing
     * is important, care must be taken if changes must be made.
     */
    @Override
    public void process() {
        // Process the entity annotation.
        processEntity();

        // If we are an inheritance subclass process our root first.
        if (getDescriptor().isInheritanceSubclass()) {
            ClassAccessor parentAccessor = getDescriptor().getInheritanceParentDescriptor().getClassAccessor();
            if (! parentAccessor.isProcessed()) {
                parentAccessor.process();
            }
        }

        // Process the Table and Inheritance metadata.
        processTableAndInheritance();

        // Process the indices metadata.
        processIndexes();

        // Process the cascade on delete metadata.
        processCascadeOnDelete();

        // Process the JPA converts metadata.
        processConverts();

        // Process our parents metadata after processing our own.
        super.process();

        // Finally, process the mapping accessors on this entity (and all those
        // from super classes that apply to us).
        processMappingAccessors();

        // Process the entity graph metadata.
        processEntityGraphs();
    }

    /**
     * INTERNAL:
     * For VIRTUAL access we need to look for default access methods that we
     * need to use with our mapping attributes.
     */
    public void processAccessMethods() {
        // If we use virtual access and do not have any access methods
        // specified then go look for access methods on a mapped superclass
        // or inheritance parent.
        if (hasAccessMethods()) {
            getDescriptor().setDefaultAccessMethods(getAccessMethods());
        } else {
            MetadataAnnotation virtualAccessMethods = getAnnotation(VirtualAccessMethods.class);
            if (virtualAccessMethods != null){
                getDescriptor().setDefaultAccessMethods(new AccessMethodsMetadata(virtualAccessMethods, this));
                return;
            }
            // Go through the mapped superclasses.
            for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
                if (mappedSuperclass.hasAccessMethods()) {
                    getDescriptor().setDefaultAccessMethods(mappedSuperclass.getAccessMethods());
                    return;
                }
                virtualAccessMethods = mappedSuperclass.getAnnotation(VirtualAccessMethods.class);
                if (virtualAccessMethods != null){
                    getDescriptor().setDefaultAccessMethods(new AccessMethodsMetadata(virtualAccessMethods, this));
                    return;
                }
            }

            // Go through the inheritance parents.
            if (getDescriptor().isInheritanceSubclass()) {
                MetadataDescriptor parentDescriptor = getDescriptor().getInheritanceParentDescriptor();
                while (parentDescriptor.isInheritanceSubclass()) {
                    if (parentDescriptor.getClassAccessor().hasAccessMethods()) {
                        getDescriptor().setDefaultAccessMethods(parentDescriptor.getClassAccessor().getAccessMethods());
                        return;
                    }
                    virtualAccessMethods = parentDescriptor.getClassAccessor().getAnnotation(VirtualAccessMethods.class);
                    if (virtualAccessMethods != null){
                        getDescriptor().setDefaultAccessMethods(new AccessMethodsMetadata(virtualAccessMethods, this));
                        return;
                    }
                    parentDescriptor = parentDescriptor.getInheritanceParentDescriptor();
                }
            }

        }
    }

    /**
     * INTERNAL:
     * Figure out the access type for this entity. It works as follows:
     * 1 - check for an explicit access type specification
     * 2 - check our inheritance parents (ignoring explicit specifications)
     * 3 - check our mapped superclasses (ignoring explicit specifications) for
     *     the location of annotations
     * 4 - check the entity itself for the location of annotations
     * 5 - check for an xml default from a persistence-unit-metadata-defaults or
     *     entity-mappings setting.
     * 6 - we have exhausted our search, default to FIELD.
     */
    @Override
    public void processAccessType() {
        // This function has been overridden in the MappedSuperclassAccessor
        // parent. Do not call this superclass method.

        // Step 1 - Check for an explicit setting.
        String explicitAccessType = getAccess();

        // Step 2, regardless if there is an explicit access type we still
        // want to determine the default access type for this entity since
        // any embeddable, mapped superclass or id class that depends on it
        // will have it available.
        String defaultAccessType = null;

        // 1 - Check the access types from our parents if we are an inheritance
        // sub-class. Inheritance hierarchies are processed top->down so our
        // first parent that does not define an explicit access type will have
        // the type we would want to default to.
        if (getDescriptor().isInheritanceSubclass()) {
            MetadataDescriptor parent = getDescriptor().getInheritanceParentDescriptor();
            while (parent != null) {
                if (! parent.getClassAccessor().hasAccess()) {
                    defaultAccessType = parent.getDefaultAccess();
                    break;
                }

                parent = parent.getInheritanceParentDescriptor();
            }
        }

        // 2 - If there is no inheritance or no inheritance parent that does not
        // explicitly define an access type. Let's check this entities mapped
        // superclasses now.
        if (defaultAccessType == null) {
            for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
                if (! mappedSuperclass.hasAccess()) {
                    if (mappedSuperclass.hasObjectRelationalFieldMappingAnnotationsDefined()) {
                        defaultAccessType = JPA_ACCESS_FIELD;
                    } else if (mappedSuperclass.hasObjectRelationalMethodMappingAnnotationsDefined()) {
                        defaultAccessType = JPA_ACCESS_PROPERTY;
                    }

                    break;
                }
            }

            // 3 - If there are no mapped superclasses or no mapped superclasses
            // without an explicit access type. Check where the annotations are
            // defined on this entity class.
            if (defaultAccessType == null) {
                if (hasObjectRelationalFieldMappingAnnotationsDefined()) {
                    defaultAccessType = JPA_ACCESS_FIELD;
                } else if (hasObjectRelationalMethodMappingAnnotationsDefined()) {
                    defaultAccessType = JPA_ACCESS_PROPERTY;
                } else {
                    // 4 - If there are no annotations defined on either the
                    // fields or properties, check for an xml default from
                    // persistence-unit-metadata-defaults or entity-mappings.
                    if (getDescriptor().getDefaultAccess() != null) {
                        defaultAccessType = getDescriptor().getDefaultAccess();
                    } else {
                        // 5 - We've exhausted our search, set the access type
                        // to FIELD.
                        defaultAccessType = JPA_ACCESS_FIELD;
                    }
                }
            }
        }

        // Finally set the default access type on the descriptor and log a
        // message to the user if we are defaulting the access type for this
        // entity to use that default.
        getDescriptor().setDefaultAccess(defaultAccessType);

        if (explicitAccessType == null) {
            getLogger().logConfigMessage(MetadataLogger.ACCESS_TYPE, defaultAccessType, getJavaClass());
        }

        // This access type setting on the class descriptor will be used to
        // weave the class properly.
        getDescriptor().setAccessTypeOnClassDescriptor(getAccessType());
    }

    /**
     * INTERNAL:
     * Process a caching metadata for this entity accessor logging ignore
     * warnings where necessary.
     */
    @Override
    protected void processCaching() {
        if (getProject().isSharedCacheModeAll()) {
           if (getDescriptor().isCacheableFalse()) {
               // The persistence unit has an ALL caching type and the user
               // specified Cacheable(false). Log a warning message that it is
               // being ignored. If Cacheable(true) was specified then it is
               // redundant and we just carry on.
               getLogger().logConfigMessage(MetadataLogger.IGNORE_CACHEABLE_FALSE, getJavaClass());
           }

           // Process the cache metadata.
           processCachingMetadata();
        } else if (getProject().isSharedCacheModeNone()) {
            if (getDescriptor().isCacheableTrue()) {
                // The persistence unit has a NONE caching type and the the user
                // specified Cacheable(true). Log a warning message that it is being
                // ignored. If Cacheable(false) was specified then it is redundant
                // and we just carry on.
                getLogger().logConfigMessage(MetadataLogger.IGNORE_CACHEABLE_TRUE, getJavaClass());
            }

            // Turn off the cache.
            getDescriptor().useNoCache();
        } else if (getProject().isSharedCacheModeEnableSelective()) {
            if (!getDescriptor().isCacheableTrue()) {
                // ENABLE_SELECTIVE and Cacheable(false) or no setting, turn off the cache.
                getDescriptor().useNoCache();
            }
            // Cacheable annotation in current class can override default or inherited settings.
            getDescriptor().setCacheableInDescriptor();
            // ENABLE_SELECTIVE and Cacheable(true), process the cache metadata.
            processCachingMetadata();
        } else if (getProject().isSharedCacheModeDisableSelective() || getProject().isSharedCacheModeUnspecified()) {
            if (getDescriptor().isCacheableFalse()) {
                // DISABLE_SELECTIVE and Cacheable(false), turn off cache.
                getDescriptor().useNoCache();
            }
            // Cacheable annotation in current class can override default or inherited settings.
            getDescriptor().setCacheableInDescriptor();
            // DISABLE_SELECTIVE and Cacheable(true) or no setting, process the cache metadata.
            processCachingMetadata();
        }
    }

    /**
     * INTERNAL:
     * Check if CascadeOnDelete was set on the Entity.
     */
    protected void processCascadeOnDelete() {
        getDescriptor().getClassDescriptor().setIsCascadeOnDeleteSetOnDatabaseOnSecondaryTables(isCascadeOnDelete());
    }

    /**
     * INTERNAL:
     * Return the user defined class extractor class for this entity. Assumes
     * hasClassExtractor has been called beforehand (meaning we either have
     * an annotation or XML definition.
     */
    public String processClassExtractor() {
        MetadataAnnotation classExtractor = getAnnotation(ClassExtractor.class);

        if (m_classExtractor == null) {
            m_classExtractor = getMetadataClass(classExtractor.getAttributeString("value"));
        } else {
            getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, classExtractor, getJavaClassName(), getLocation());
        }

        return m_classExtractor.getName();
    }

    /**
     * INTERNAL:
     * Add a convert metadata to the descriptor convert map.
     */
    public void processConvert(ConvertMetadata convert) {
        if (convert.hasAttributeName()) {
            if (convert.getAttributeName().indexOf(".") > -1) {
                // We have a dot notation name.
                String dotNotationName = convert.getAttributeName();
                int dotIndex = dotNotationName.indexOf(".");
                String attributeName = dotNotationName.substring(0, dotIndex);
                String remainder = dotNotationName.substring(dotIndex + 1);
                // Update the convert attribute name for correct convert processing.
                convert.setAttributeName(remainder);
                getDescriptor().addConvert(attributeName, convert);
            } else {
                // Simple single name.
                String attributeName = convert.getAttributeName();
                convert.setAttributeName("");
                getDescriptor().addConvert(attributeName, convert);
            }
        } else {
            throw ValidationException.missingConvertAttributeName(getJavaClassName());
        }
    }

    /**
     * INTERNAL:
     * Process the convert metadata for this entity accessor logging ignore
     * warnings where necessary.
     */
    public void processConverts() {
        if (m_converts.isEmpty()) {
            // Look for a Converts annotation.
            if (isAnnotationPresent(JPA_CONVERTS)) {
                for (Object convert : (Object[]) getAnnotation(JPA_CONVERTS).getAttributeArray("value")) {
                    processConvert(new ConvertMetadata((MetadataAnnotation) convert, this));
                }
            } else {
                // Look for a Convert annotation
                if (isAnnotationPresent(JPA_CONVERT)) {
                    processConvert(new ConvertMetadata(getAnnotation(JPA_CONVERT), this));
                }
            }
        } else {
            if (isAnnotationPresent(JPA_CONVERT)) {
                getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, getAnnotation(JPA_CONVERT), getJavaClassName(), getLocation());
            }

            if (isAnnotationPresent(JPA_CONVERTS)) {
                getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, getAnnotation(JPA_CONVERTS), getJavaClassName(), getLocation());
            }

            for (ConvertMetadata convert : m_converts) {
                processConvert(convert);
            }
        }
    }

    /**
     * INTERNAL:
     * Allows for processing DerivedIds.  All referenced accessors are processed
     * first to ensure the necessary fields are set before this derivedId is processed
     */
    @Override
    public void processDerivedId(HashSet<ClassAccessor> processing, HashSet<ClassAccessor> processed) {
        if (! processed.contains(this)) {
            super.processDerivedId(processing, processed);

            // Validate we found a primary key.
            validatePrimaryKey();

            // Primary key has been validated, let's process those items that
            // depend on it now.

            // Process the SecondaryTable(s) metadata.
            processSecondaryTables();
        }
    }

    /**
     * INTERNAL:
     * Process the discriminator column metadata (defaulting if necessary),
     * and return the EclipseLink database field.
     */
    public DatabaseField processDiscriminatorColumn() {
        MetadataAnnotation discriminatorColumn = getAnnotation(JPA_DISCRIMINATOR_COLUMN);

        if (m_discriminatorColumn == null) {
            m_discriminatorColumn = new DiscriminatorColumnMetadata(discriminatorColumn, this);
        } else {
            if (isAnnotationPresent(JPA_DISCRIMINATOR_COLUMN)) {
                getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, discriminatorColumn, getJavaClassName(), getLocation());
            }
        }

        return m_discriminatorColumn.process(getDescriptor(), MetadataLogger.INHERITANCE_DISCRIMINATOR_COLUMN);
    }

    /**
     * INTERNAL:
     * Process a discriminator value to set the class indicator on the root
     * descriptor of the inheritance hierarchy.
     *
     * If there is no discriminator value, the class indicator defaults to
     * the class name.
     */
    public String processDiscriminatorValue() {
        if (! Modifier.isAbstract(getJavaClass().getModifiers())) {
            // Add the indicator to the inheritance root class' descriptor. The
            // default is the short class name.
            if (m_discriminatorValue == null) {
                MetadataAnnotation discriminatorValue = getAnnotation(JPA_DISCRIMINATOR_VALUE);

                if (discriminatorValue == null) {
                    // By default return the alias (i.e. entity name if provided
                    // otherwise the short java class name)
                    return getDescriptor().getAlias();
                } else {
                    return discriminatorValue.getAttributeString("value");
                }
            } else {
                return m_discriminatorValue;
            }
        }

        return null;
    }

    /**
     * INTERNAL:
     * Process the entity metadata.
     */
    protected void processEntity() {
        // Process the entity name (alias) and default if necessary.
        if (m_entityName == null) {
            m_entityName = (getAnnotation(JPA_ENTITY) == null) ? "" : getAnnotation(JPA_ENTITY).getAttributeString("name");
        }

        if (m_entityName == null || m_entityName.equals("")) {
            m_entityName = Helper.getShortClassName(getJavaClassName());
            getLogger().logConfigMessage(MetadataLogger.ALIAS, getDescriptor(), m_entityName);
        }

        getProject().addAlias(m_entityName, getDescriptor());
    }

    /**
     * INTERNAL:
     * Process the entity graph metadata on this entity accessor.
     */
    protected void processEntityGraphs() {
        if (m_namedEntityGraphs.isEmpty()) {
            // Look for an NamedEntityGraphs annotation.
            if (isAnnotationPresent(JPA_ENTITY_GRAPHS)) {
                for (Object entityGraph : (Object[]) getAnnotation(JPA_ENTITY_GRAPHS).getAttributeArray("value")) {
                    new NamedEntityGraphMetadata((MetadataAnnotation) entityGraph, this).process(this);
                }
            } else {
                // Look for a NamedEntityGraph annotation
                if (isAnnotationPresent(JPA_ENTITY_GRAPH)) {
                    new NamedEntityGraphMetadata(getAnnotation(JPA_ENTITY_GRAPH), this).process(this);
                }
            }
        } else {
            // Process the named entity graphs from XML.
            if (isAnnotationPresent(JPA_ENTITY_GRAPH)) {
                getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, getAnnotation(JPA_ENTITY_GRAPH), getJavaClassName(), getLocation());
            }

            if (isAnnotationPresent(JPA_ENTITY_GRAPHS)) {
                getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, getAnnotation(JPA_ENTITY_GRAPHS), getJavaClassName(), getLocation());
            }

            for (NamedEntityGraphMetadata entityGraph : m_namedEntityGraphs) {
                entityGraph.process(this);
            }
        }
    }

    /**
     * INTERNAL:
     * Process index information for the given metadata descriptor.
     */
    protected void processIndexes() {
        // TODO: This method is adding annotation metadata to XML metadata. This
        // is wrong and does not follow the spec ideology. XML metadata should
        // override not merge with annotations.
        if (isAnnotationPresent(Index.class)) {
            m_indexes.add(new IndexMetadata(getAnnotation(Index.class), this));
        }

        if (isAnnotationPresent(Indexes.class)) {
            for (Object index : getAnnotation(Indexes.class).getAttributeArray("value")) {
                m_indexes.add(new IndexMetadata((MetadataAnnotation) index, this));
            }
        }

        for (IndexMetadata indexMetadata : m_indexes) {
            indexMetadata.process(getDescriptor(), null);
        }
    }

    /**
     * INTERNAL:
     * Process the Inheritance metadata for a root of an inheritance
     * hierarchy. One may or may not be specified for the entity class that is
     * the root of the entity class hierarchy, so we need to default in this
     * case. This method should only be called on the root of the inheritance
     * hierarchy.
     */
    protected void processInheritance() {
        // Process the inheritance metadata first. Create one if one does not
        // exist.
        if (m_inheritance == null) {
            m_inheritance = new InheritanceMetadata(getAnnotation(JPA_INHERITANCE), this);
        }

        m_inheritance.process(getDescriptor());
    }

    /**
     * INTERNAL:
     * Process the inheritance metadata for an inheritance subclass. The
     * parent descriptor must be provided.
     */
    public void processInheritancePrimaryKeyJoinColumns() {
        // If there are no primary key join columns specified in XML, look for
        // some defined through annotations.
        if (m_primaryKeyJoinColumns.isEmpty()) {
            // Process all the primary key join columns first.
            if (isAnnotationPresent(JPA_PRIMARY_KEY_JOIN_COLUMNS)) {
                MetadataAnnotation primaryKeyJoinColumns = getAnnotation(JPA_PRIMARY_KEY_JOIN_COLUMNS);

                for (Object primaryKeyJoinColumn : primaryKeyJoinColumns.getAttributeArray("value")) {
                    m_primaryKeyJoinColumns.add(new PrimaryKeyJoinColumnMetadata((MetadataAnnotation) primaryKeyJoinColumn, this));
                }

                // Set the primary key foreign key metadata if one is specified.
                if (primaryKeyJoinColumns.hasAttribute("foreignKey")) {
                    setPrimaryKeyForeignKey(new PrimaryKeyForeignKeyMetadata(primaryKeyJoinColumns.getAttributeAnnotation("foreignKey"), this));
                }
            }

            // Process the single primary key join column second.
            if (isAnnotationPresent(JPA_PRIMARY_KEY_JOIN_COLUMN)) {
                PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn = new PrimaryKeyJoinColumnMetadata(getAnnotation(JPA_PRIMARY_KEY_JOIN_COLUMN), this);
                m_primaryKeyJoinColumns.add(primaryKeyJoinColumn);

                // Set the primary key foreign key metadata if specified.
                if (primaryKeyJoinColumn.hasForeignKey()) {
                    setPrimaryKeyForeignKey(new PrimaryKeyForeignKeyMetadata(primaryKeyJoinColumn.getForeignKey()));
                }
            }
        }

        // Process the primary key join columns into multiple table key fields.
        addMultipleTableKeyFields(m_primaryKeyJoinColumns, getDescriptor().getPrimaryTable(), MetadataLogger.INHERITANCE_PK_COLUMN, MetadataLogger.INHERITANCE_FK_COLUMN);

        // Process the primary key foreign key.
        if (m_primaryKeyForeignKey != null) {
            m_primaryKeyForeignKey.process(getDescriptor().getPrimaryTable());
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
            ((EntityListenerMetadata) defaultListener.clone()).process(this, loader, true);
        }

        // Step 2 - process the entity listeners that are defined on the entity
        // class and mapped superclasses (taking metadata-complete into
        // consideration). Go through the mapped superclasses first, top->down
        // only if the exclude superclass listeners flag is not set.
        discoverMappedSuperclassesAndInheritanceParents(true);

        if (! getDescriptor().excludeSuperclassListeners()) {
            int mappedSuperclassesSize = getMappedSuperclasses().size();

            for (int i = mappedSuperclassesSize - 1; i >= 0; i--) {
                getMappedSuperclasses().get(i).processEntityListeners(loader);
            }
        }

        processEntityListeners(loader);

        // Step 3 - process the entity class for lifecycle callback methods. Go
        // through the mapped superclasses as well.
        new EntityClassListenerMetadata(this).process(getMappedSuperclasses(), loader);
    }

    /**
     * INTERNAL:
     * Process the accessors for the given class.
     * If we are within a TABLE_PER_CLASS inheritance hierarchy, our parent
     * accessors will already have been added at this point.
     * @see InheritanceMetadata process()
     */
    @Override
    public void processMappingAccessors() {
        // Process our mapping accessors, and then perform the necessary
        // validation checks for this entity.
        super.processMappingAccessors();

        // Validate the optimistic locking setting.
        validateOptimisticLocking();

        // Check that we have a simple pk. If it is a derived id, this will be
        // run in a second pass
        if (! hasDerivedId()){
            // Validate we found a primary key.
            validatePrimaryKey();

            // Primary key has been validated, let's process those items that
            // depend on it now.

            // Process the SecondaryTable(s) metadata.
            processSecondaryTables();
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
        addMultipleTableKeyFields(secondaryTable.getPrimaryKeyJoinColumns(), secondaryTable.getDatabaseTable(), MetadataLogger.SECONDARY_TABLE_PK_COLUMN, MetadataLogger.SECONDARY_TABLE_FK_COLUMN);
    }

    /**
     * INTERNAL:
     * Process secondary-table(s) for a given entity.
     */
    protected void processSecondaryTables() {
        // Ignore secondary tables when the descriptor is an EIS descriptor.
        if (! getDescriptor().getClassDescriptor().isEISDescriptor()) {
            MetadataAnnotation secondaryTable = getAnnotation(JPA_SECONDARY_TABLE);
            MetadataAnnotation secondaryTables = getAnnotation(JPA_SECONDARY_TABLES);

            if (m_secondaryTables.isEmpty()) {
                // Look for a SecondaryTables annotation.
                if (secondaryTables != null) {
                    for (Object table : secondaryTables.getAttributeArray("value")) {
                        processSecondaryTable(new SecondaryTableMetadata((MetadataAnnotation)table, this));
                    }
                } else {
                    // Look for a SecondaryTable annotation
                    if (secondaryTable != null) {
                        processSecondaryTable(new SecondaryTableMetadata(secondaryTable, this));
                    }
                }
            } else {
                if (secondaryTable != null) {
                    getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, secondaryTable, getJavaClassName(), getLocation());
                }

                if (secondaryTables != null) {
                    getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, secondaryTables, getJavaClassName(), getLocation());
                }

                for (SecondaryTableMetadata table : m_secondaryTables) {
                    processSecondaryTable(table);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Process table information for the given metadata descriptor.
     */
    protected void processTable() {
        MetadataAnnotation table = getAnnotation(JPA_TABLE);

        if (m_table == null) {
            // Check for a table annotation. If no annotation is defined, the
            // table will default.
            processTable(new TableMetadata(table, this));
        } else {
            if (table != null) {
                getLogger().logConfigMessage(MetadataLogger.OVERRIDE_ANNOTATION_WITH_XML, table, getJavaClassName(), getLocation());
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
        // Ignore secondary tables when the descriptor is an EIS descriptor.
        if (! getDescriptor().getClassDescriptor().isEISDescriptor()) {
            // Process any table defaults and log warning messages.
            processTable(table, getDescriptor().getDefaultTableName());

            // Set the table on the descriptor.
            getDescriptor().setPrimaryTable(table.getDatabaseTable());
        }
    }

    /**
     * INTERNAL:
     * Process any inheritance specifics. NOTE: Inheritance hierarchies are
     * always processed from the top down so it is safe to assume that all
     * our parents have already been processed fully. The only exception being
     * when a root accessor doesn't know they are a root (defaulting case). In
     * this case we'll tell the root accessor to process the inheritance
     * metadata before continuing with our own processing.
     */
    protected void processTableAndInheritance() {
        // If we are an inheritance subclass, ensure our root is processed
        // first since it has information its subclasses depend on.
        if (getDescriptor().isInheritanceSubclass()) {
            MetadataDescriptor rootDescriptor = getDescriptor().getInheritanceRootDescriptor();
            EntityAccessor rootAccessor = (EntityAccessor) rootDescriptor.getClassAccessor();

            // An entity who didn't know they were the root of an inheritance
            // hierarchy (that is, does not define inheritance metadata) must
            // process and default the inheritance metadata.
            if (! rootAccessor.hasInheritance()) {
                rootAccessor.processInheritance();
            }

            // Process the table metadata for this descriptor if either there
            // is an inheritance specification (we're a new root) or if we are
            // part of a joined or table per class inheritance strategy.
            if (hasInheritance() || ! rootDescriptor.usesSingleTableInheritanceStrategy()) {
                processTable();
            }

            // If this entity has inheritance metadata as well, then the
            // inheritance strategy is mixed and we need to process the
            // inheritance metadata to ensure this entity's subclasses process
            // correctly.
            if (hasInheritance()) {
                // Process the inheritance metadata.
                processInheritance();
            } else {
                // Process the inheritance details using the inheritance
                // metadata from our parent. This will put the right
                // inheritance policy on our descriptor.
                rootAccessor.getInheritance().process(getDescriptor());
            }
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
     * Used for OX mapping.
     */
    public void setCascadeOnDelete(Boolean cascadeOnDelete) {
        m_cascadeOnDelete = cascadeOnDelete;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setClassExtractorName(String classExtractorName) {
        m_classExtractorName = classExtractorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setConverts(List<ConvertMetadata> converts) {
        m_converts = converts;
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
    public void setIndexes(List<IndexMetadata> indexes) {
        m_indexes = indexes;
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
    public void setNamedEntityGraphs(List<NamedEntityGraphMetadata> namedEntityGraphs) {
        m_namedEntityGraphs = namedEntityGraphs;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setPrimaryKeyForeignKey(PrimaryKeyForeignKeyMetadata primaryKeyForeignKey) {
        m_primaryKeyForeignKey = primaryKeyForeignKey;
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
    public void setSecondaryTables(List<SecondaryTableMetadata> secondaryTables) {
        m_secondaryTables = secondaryTables;
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
     * Call this method after a primary key should have been found.
     */
    protected void validatePrimaryKey() {
        // If this descriptor has a composite primary key, check that all
        // our composite primary key attributes were validated.
        if (getDescriptor().hasCompositePrimaryKey()) {
            if (getDescriptor().pkClassWasNotValidated()) {
                throw ValidationException.invalidCompositePKSpecification(getJavaClass(), getDescriptor().getPKClassName());
            }

            // Log a warning to the user that they have specified multiple id
            // fields without an id class specification. If they are using a
            // @PrimaryKey specification don't issue the warning.
            if (! getDescriptor().hasPKClass() && ! getDescriptor().hasPrimaryKey()) {
                getLogger().logWarningMessage(MetadataLogger.MULTIPLE_ID_FIELDS_WITHOUT_ID_CLASS, getJavaClassName());
            }
        } else {
            // Descriptor has a single primary key. Validate an id attribute was
            // found, unless we are an inheritance subclass or an aggregate descriptor.
            if (! getDescriptor().hasPrimaryKeyFields() && ! getDescriptor().isInheritanceSubclass()) {
                throw ValidationException.noPrimaryKeyAnnotationsFound(getJavaClass());
            }
        }
    }
}
