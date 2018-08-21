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
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     11/06/2009-2.0 Guy Pelletier
//       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

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
public class BasicMapAccessor extends BasicCollectionAccessor {
    private ColumnMetadata m_keyColumn;
    private String m_keyConverter;
    private String m_valueConverter;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public BasicMapAccessor() {
        super("<basic-map>");
    }

    /**
     * INTERNAL:
     */
    public BasicMapAccessor(MetadataAnnotation basicMap, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(basicMap, accessibleObject, classAccessor);

        m_keyColumn = new ColumnMetadata(basicMap.getAttributeAnnotation("keyColumn"), this);

        MetadataAnnotation keyConvert = basicMap.getAttributeAnnotation("keyConverter");
        if (keyConvert != null) {
            m_keyConverter = keyConvert.getAttributeString("value");
        }

        MetadataAnnotation valueConvert = basicMap.getAttributeAnnotation("valueConverter");
        if (valueConvert != null) {
            m_valueConverter = valueConvert.getAttributeString("value");
        }

        setValueColumn(new ColumnMetadata(basicMap.getAttributeAnnotation("valueColumn"), this));
        setFetch(basicMap.getAttributeString("fetch"));
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof BasicMapAccessor) {

            BasicMapAccessor basicMapAccessor = (BasicMapAccessor) objectToCompare;

            if (! valuesMatch(m_keyColumn, basicMapAccessor.getKeyColumn())) {
                return false;
            }

            if (! valuesMatch(m_keyConverter, basicMapAccessor.getKeyConverter())) {
                return false;
            }

            return valuesMatch(m_valueConverter, basicMapAccessor.getValueConverter());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_keyColumn != null ? m_keyColumn.hashCode() : 0);
        result = 31 * result + (m_keyConverter != null ? m_keyConverter.hashCode() : 0);
        result = 31 * result + (m_valueConverter != null ? m_valueConverter.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     */
    @Override
    protected ColumnMetadata getColumn(String loggingCtx) {
        if (loggingCtx.equals(MetadataLogger.VALUE_COLUMN)) {
            return super.getColumn(loggingCtx);
        } else {
            return m_keyColumn == null ? super.getColumn(loggingCtx) : m_keyColumn;
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    protected String getDefaultCollectionTableName() {
        if (m_keyColumn != null && m_keyColumn.getTable() != null && ! m_keyColumn.getTable().equals("")) {
            return m_keyColumn.getTable();
        } else {
            return super.getDefaultCollectionTableName();
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getKeyColumn() {
        return m_keyColumn;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    @Override
    public String getKeyConverter() {
        return m_keyConverter;
    }

    /**
     * INTERNAL:
     * Return the reference class for this accessor.
     */
    @Override
    public MetadataClass getReferenceClass() {
        return getReferenceClassFromGeneric();
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    @Override
    public String getValueConverter() {
        return m_valueConverter;
    }

    /**
     * INTERNAL:
     * For all intent and purpose a basic map accessor always uses a map key.
     * It is used to correctly set the indirection policy. This method is kinda
     * overkill to make sure isMappedKeyMapAccessor returns false for
     * BasicMapAccessors.
     */
    @Override
    public boolean hasMapKey() {
        return true;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize single ORMetadata objects.
        initXMLObject(m_keyColumn, accessibleObject);
    }


    /**
     * INTERNAL:
     * Return true if this accessor represents a basic map mapping.
     */
    @Override
    public boolean isBasicMap() {
        return true;
    }

    /**
     * INTERNAL:
     * The processing of JPA converters for a basic map has been disabled, since
     * we will not know which part of the map (key or value) to apply the JPA
     * converter. See isLob, isTemporal and isEnumerated calls for log warning
     * details.
     */
    @Override
    public void process() {
        if (isValidDirectMapType()) {
            processDirectMapMapping();
        } else {
            throw ValidationException.invalidTypeForBasicMapAttribute(getAttributeName(), getRawClass(), getJavaClass());
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setKeyColumn(ColumnMetadata keyColumn) {
        m_keyColumn = keyColumn;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setKeyConverter(String keyConverter) {
        m_keyConverter = keyConverter;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setValueConverter(String valueConverter) {
        m_valueConverter = valueConverter;
    }
}
