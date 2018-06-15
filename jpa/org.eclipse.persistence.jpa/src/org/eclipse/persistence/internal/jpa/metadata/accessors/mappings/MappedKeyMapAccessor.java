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
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/03/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     11/06/2009-2.0 Guy Pelletier
//       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.MapKeyMetadata;

/**
 * Interface class to define the common map mapping metadata.
 *
 * @see CollectionAccessor
 * @see ElementCollectionAccessor
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public interface MappedKeyMapAccessor {
    /**
     * INTERNAL:
     */
    public List<AssociationOverrideMetadata> getMapKeyAssociationOverrides();

    /**
     * INTERNAL:
     */
    public List<AttributeOverrideMetadata> getMapKeyAttributeOverrides();

    /**
     * INTERNAL:
     */
    public MapKeyMetadata getMapKey();

    /**
     * INTERNAL:
     */
    public MetadataClass getMapKeyClass();

    /**
     * INTERNAL:
     */
    public MetadataClass getMapKeyClassWithGenerics();

    /**
     * INTERNAL:
     */
    public String getMapKeyConvert();

    /**
     * INTERNAL:
     */
    public List<ConvertMetadata> getMapKeyConverts();

    /**
     * INTERNAL:
     */
    public ColumnMetadata getMapKeyColumn();

    /**
     * INTERNAL:
     */
    public ForeignKeyMetadata getMapKeyForeignKey();

    /**
     * INTERNAL:
     */
    public List<JoinColumnMetadata> getMapKeyJoinColumns();

    /**
     * INTERNAL:
     */
    public void setMapKeyClass(MetadataClass cls);
}
