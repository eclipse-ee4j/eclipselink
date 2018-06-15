/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     01/25/2011-2.3 Guy Pelletier
//       - 333913: @OrderBy and <order-by/> without arguments should order by primary
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * Parent object that is used to hold onto a valid JPA decorated file.
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class MetadataFile extends MetadataAccessibleObject {
    XMLEntityMappings m_entityMappings;

    /**
     * INTERNAL:
     */
    public MetadataFile(XMLEntityMappings entityMappings) {
        super(entityMappings.getMetadataFactory());
        m_entityMappings = entityMappings;
    }

    /**
     * INTERNAL:
     * Return the attribute name of this accessible object. Right now it's just
     * the location. This method is currently never called and is implemented
     * only to satisfy the abstract definition. An attribute name is very
     * important on the metadata annotated element side and describes the
     * attribute the metadata is tied to. This avoids unnecessary casting when
     * initializing XML objects. If in the future, some logging message (or
     * something else would like to use this method, feel free to modify it to
     * return whatever is necessary (and of course, change this comment to
     * reflect the new dependency).
     */
    @Override
    public String getAttributeName() {
        return getName();
    }

    /**
     * INTERNAL:
     * Return the element of this accessible object. Right now it's just the
     * top most entity mappings tag. At least it's something. Could probably
     * get more descriptive, right now it's only used in merging and for those
     * elements that do not tie to an annotated element (class, method or field)
     * That is, unnamed &lt;entity-mappings&gt; level elements and
     * &lt;persistence-unit-metadata&gt; and &lt;persistence-unit-defaults&gt; elements.
     * So this will have to do for now ... plus no one looks at the logging
     * messages anyway ... ;-)
     */
    public Object getElement() {
        return "<entity-mappings>";
    }

    /**
     * INTERNAL:
     * Returns the name/url of the metadata file.
     */
    @Override
    public String getName() {
        return m_entityMappings.getMappingFileOrURL();
    }
}
