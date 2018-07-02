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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     06/02/2009-2.0 Guy Pelletier
//       - 278768: JPA 2.0 Association Override Join Table
//     09/29/2009-2.0 Guy Pelletier
//       - 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     09/03/2010-2.2 Guy Pelletier
//       - 317286: DB column lenght not in sync between @Column and @JoinColumn
//     01/04/2011-2.3 Guy Pelletier
//       - 330628: @PrimaryKeyJoinColumn(...) is not working equivalently to @JoinColumn(..., insertable = false, updatable = false)
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.annotations.CollectionTable;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;

import org.eclipse.persistence.internal.jpa.metadata.tables.CollectionTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;

/**
 * INTERNAL:
 * A basic collection accessor.
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
 * @since TopLink 11g
 */
@SuppressWarnings("deprecation")
public class BasicCollectionAccessor extends DirectCollectionAccessor {
    private ColumnMetadata m_valueColumn;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public BasicCollectionAccessor() {
        super("<basic-collection>");
    }

    /**
     * INTERNAL:
     */
    protected BasicCollectionAccessor(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    public BasicCollectionAccessor(MetadataAnnotation basicCollection, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(basicCollection, accessibleObject, classAccessor);

        // Must check, BasicMapAccessor calls this constructor ...
        if (basicCollection != null) {
            m_valueColumn = new ColumnMetadata(basicCollection.getAttributeAnnotation("valueColumn"), this);
        }

        // Set the collection table if one is present.
        if (isAnnotationPresent(CollectionTable.class)) {
            setCollectionTable(new CollectionTableMetadata(getAnnotation(CollectionTable.class), this));
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof BasicCollectionAccessor) {
            BasicCollectionAccessor basicCollectionAccessor = (BasicCollectionAccessor) objectToCompare;
            return valuesMatch(m_valueColumn, basicCollectionAccessor.getValueColumn());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_valueColumn != null ? m_valueColumn.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     * Method ignores logging context. Can't be anything but the value
     * column for a BasicCollection annotation. Used with the BasicMap
     * annotation however.
     */
    @Override
    protected ColumnMetadata getColumn(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.VALUE_COLUMN)) {
            return m_valueColumn == null ? super.getColumn(loggingCtx) : m_valueColumn;
        } else {
            return super.getColumn(loggingCtx);
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    protected String getDefaultCollectionTableName() {
        if (m_valueColumn != null && m_valueColumn.getTable() != null && !m_valueColumn.getTable().equals("")) {
            return m_valueColumn.getTable();
        } else {
            return super.getDefaultCollectionTableName();
        }
    }

    /**
     * INTERNAL:
     * A basic collection can not return a key converter value so return
     * null in this case.
     */
    @Override
    protected String getKeyConverter() {
        return null;
    }

    /**
     * INTERNAL:
     * Return the reference class for this accessor. It will try to extract
     * a reference class from a generic specification. If no generics are used,
     * then it will return void.class. This avoids NPE's when processing
     * JPA converters that can default (Enumerated and Temporal) based on the
     * reference class.
     */
    @Override
    public MetadataClass getReferenceClass() {
        MetadataClass cls = getReferenceClassFromGeneric();
        return (cls == null) ? getMetadataClass(void.class) : cls;
    }

    /**
     * INTERNAL:
     * Future: this method is where we would provide a more explicit reference
     * class to support an auto-apply jpa converter. Per the spec auto-apply
     * converters are applied against basics only.
     */
    @Override
    public MetadataClass getReferenceClassWithGenerics() {
        return getReferenceClass();
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getValueColumn() {
        return m_valueColumn;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize single ORMetadata objects.
        initXMLObject(m_valueColumn, accessibleObject);
    }

    /**
     * INTERNAL:
     * Return true if this accessor represents a basic collection mapping.
     */
    @Override
    public boolean isBasicCollection() {
        return true;
    }

    /**
     * INTERNAL:
     */
    public void process() {
        if (isValidDirectCollectionType()) {
            processDirectCollectionMapping();
        } else {
            throw ValidationException.invalidTypeForBasicCollectionAttribute(getAttributeName(), getRawClass(), getJavaClass());
        }
    }

    /**
     * INTERNAL:
     * Process a MetadataCollectionTable.
     */
    @Override
    protected void processCollectionTable(CollectionMapping mapping) {
        super.processCollectionTable(mapping);

        // Add all the primaryKeyJoinColumns (reference key fields) to the
        // mapping. Primary key join column validation is performed in the
        // processPrimaryKeyJoinColumns call.
        for (PrimaryKeyJoinColumnMetadata primaryKeyJoinColumn : processPrimaryKeyJoinColumns(getCollectionTable().getPrimaryKeyJoinColumns())) {
            // Look up the primary key field from the referenced column name.
            DatabaseField pkField = getReferencedField(primaryKeyJoinColumn.getReferencedColumnName(), getOwningDescriptor(), MetadataLogger.PK_COLUMN);

            // The default name is the primary key of the owning entity.
            DatabaseField fkField = primaryKeyJoinColumn.getForeignKeyField(pkField);
            setFieldName(fkField, getOwningDescriptor().getPrimaryKeyFieldName(), MetadataLogger.FK_COLUMN);
            fkField.setTable(getReferenceDatabaseTable());

            // Add the reference key field for the direct collection mapping.
            ((DirectCollectionMapping) mapping).addReferenceKeyField(fkField, pkField);
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    protected void setValueColumn(ColumnMetadata valueColumn) {
        m_valueColumn = valueColumn;
    }
}
