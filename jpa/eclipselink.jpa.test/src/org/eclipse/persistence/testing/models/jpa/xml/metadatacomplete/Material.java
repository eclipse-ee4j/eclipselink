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

import javax.persistence.Column;

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
public class Material {
    public int id;

    @Column(name="MUCHO_EXPENSIVE")
    public double cost;

    public Material() {}
}
