/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     07/01/2014-2.5.3  Rick Curtis
//       - 375101: Date and Calendar should not require @Temporal.
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.LobMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENUMERATED;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_FETCH_LAZY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_LOB;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_TEMPORAL;

/**
 * A direct accessor.
 *
 * Subclasses: BasicAccessor, BasicCollectionAccessor, BasicMapAccessor.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject method.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public abstract class DirectAccessor extends MappingAccessor {
    private Boolean m_optional;
    private EnumeratedMetadata m_enumerated;
    private List<ConvertMetadata> m_converts;
    private LobMetadata m_lob;

    private String m_fetch;
    private String m_convert;

    private TemporalMetadata m_temporal;

    /**
     * INTERNAL:
     */
    protected DirectAccessor(String xmlElement) {
        super(xmlElement);
    }

    /**
     * INTERNAL:
     */
    protected DirectAccessor(MetadataAnnotation annotation, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(annotation, accessibleObject, classAccessor);

        // Set the lob if one is present.
        if (isAnnotationPresent(JPA_LOB)) {
            m_lob = new LobMetadata(getAnnotation(JPA_LOB), this);
        }

        // Set the enumerated if one is present.
        if (isAnnotationPresent(JPA_ENUMERATED)) {
            m_enumerated = new EnumeratedMetadata(getAnnotation(JPA_ENUMERATED), this);
        }

        // Set the temporal type if one is present.
        if (isAnnotationPresent(JPA_TEMPORAL)) {
            m_temporal = new TemporalMetadata(getAnnotation(JPA_TEMPORAL), this);
        }

        // Set the convert value if one is present.
        if (isAnnotationPresent(Convert.class)) {
            m_convert = getAnnotation(Convert.class).getAttributeString("value");
        }
    }

    /**
     * INTERNAL:
     * Subclasses that support key converts need to override this method.
     */
    @Override
    protected void addConvert(ConvertMetadata convert) {
        // Shall always call getter to lazy initialize.
        getConverts().add(convert);
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof DirectAccessor) {
            DirectAccessor directAccessor = (DirectAccessor) objectToCompare;

            if (! valuesMatch(m_optional, directAccessor.getOptional())) {
                return false;
            }

            if (! valuesMatch(m_enumerated, directAccessor.getEnumerated())) {
                return false;
            }

            if (! valuesMatch(m_lob, directAccessor.getLob())) {
                return false;
            }

            if (! valuesMatch(m_fetch, directAccessor.getFetch())) {
                return false;
            }

            if (! valuesMatch(m_converts, directAccessor.getConverts())) {
                return false;
            }

            return valuesMatch(m_temporal, directAccessor.getTemporal());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (m_optional != null ? m_optional.hashCode() : 0);
        result = 31 * result + (m_enumerated != null ? m_enumerated.hashCode() : 0);
        result = 31 * result + (m_converts != null ? m_converts.hashCode() : 0);
        result = 31 * result + (m_lob != null ? m_lob.hashCode() : 0);
        result = 31 * result + (m_fetch != null ? m_fetch.hashCode() : 0);
        result = 31 * result + (m_temporal != null ? m_temporal.hashCode() : 0);
        return result;
    }

    /**
     * INTERNAL:
     */
    public String getConvert() {
        return m_convert;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<ConvertMetadata> getConverts() {
        if (m_converts == null) {
            m_converts = new ArrayList<ConvertMetadata>();
        }
        return m_converts;
    }

    /**
     * INTERNAL:
     */
    @Override
    public abstract String getDefaultFetchType();

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public EnumeratedMetadata getEnumerated() {
        return m_enumerated;
    }

    /**
     * INTERNAL:
     * Return the enumerated metadata for this accessor.
     */
    @Override
    public EnumeratedMetadata getEnumerated(boolean isForMapKey) {
        return getEnumerated();
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
    public LobMetadata getLob() {
        return m_lob;
    }

    /**
     * INTERNAL:
     * Return the lob metadata for this accessor.
     */
    @Override
    public LobMetadata getLob(boolean isForMapKey) {
        if (isForMapKey) {
            return super.getLob(isForMapKey);
        } else {
            return getLob();
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getOptional() {
        return m_optional;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public TemporalMetadata getTemporal() {
        return m_temporal;
    }

    @Override
    public void setTemporal(TemporalMetadata metadata, boolean isForMapKey) {
        m_temporal = metadata;
    }

    /**
     * INTERNAL:
     * Return the temporal metadata for this accessor.
     */
    @Override
    public TemporalMetadata getTemporal(boolean isForMapKey) {
        return getTemporal();
    }

    /**
     * INTERNAL:
     * Return true if this accessor has enumerated metadata.
     */
    @Override
    public boolean hasEnumerated(boolean isForMapKey) {
        return m_enumerated != null;
    }

    /**
     * INTERNAL:
     * Return true if this accessor has lob metadata.
     */
    @Override
    public boolean hasLob(boolean isForMapKey) {
        return m_lob != null;
    }

    /**
     * INTERNAL:
     * Return true if this accessor has temporal metadata.
     */
    @Override
    public boolean hasTemporal(boolean isForMapKey) {
        return m_temporal != null;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize single objects.
        initXMLObject(m_enumerated, accessibleObject);
        initXMLObject(m_lob, accessibleObject);
        initXMLObject(m_temporal, accessibleObject);

        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_converts, accessibleObject);

        // Initialize a previous text element from a list of elements (legacy)
        m_convert = initXMLTextObject(m_converts);
    }

    /**
     * INTERNAL:
     */
    public boolean isOptional() {
        if (m_optional == null) {
            return true;
        } else {
            return m_optional.booleanValue();
        }
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
    public void setEnumerated(EnumeratedMetadata enumerated) {
        m_enumerated = enumerated;
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
    public void setLob(LobMetadata lob) {
        m_lob = lob;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setOptional(Boolean optional) {
        m_optional = optional;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTemporal(TemporalMetadata temporalType) {
        m_temporal = temporalType;
    }

    /**
     * INTERNAL:
     */
    @Override
    protected boolean usesIndirection() {
        String fetchType = getFetch();

        if (fetchType == null) {
            fetchType = getDefaultFetchType();
        }

        return fetchType.equals(JPA_FETCH_LAZY);
    }
}
