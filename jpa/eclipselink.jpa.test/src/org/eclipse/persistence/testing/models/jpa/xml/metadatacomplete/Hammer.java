/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     12/01/2010-2.2 Guy Pelletier
//       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification
package org.eclipse.persistence.testing.models.jpa.xml.metadatacomplete;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.AttributeOverride;
import javax.persistence.Table;

/**
 * Included in the following mapping files:
 * - eclipselink-xml-only-model/metadata-complete-mappings.
 * - eclipselink-xml-only-model/xml-mapping-metadata-complete.
 *
 * Which are used in two separate persistence units from:
 * - eclipselink-annotation-model/persistence.xml, namely:
 *   - METADATA_COMPLETE
 *   - XML_MAPPING_METADATA_COMPLETE
 *
 * @author gpelleti
 */
@Table(name="IGNORE_HAMMER_TABLE")
public class Hammer extends Tool {

    public int weight;

    // This column name should not get picked up and should default to color.
    @Column(name="IGNORED_COLOR")
    public String color;

    public Hammer() {}

}

