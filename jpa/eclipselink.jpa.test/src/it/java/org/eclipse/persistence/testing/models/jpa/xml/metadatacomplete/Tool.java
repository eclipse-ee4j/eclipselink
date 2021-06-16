/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     12/01/2010-2.2 Guy Pelletier
//       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification
package org.eclipse.persistence.testing.models.jpa.xml.metadatacomplete;

import jakarta.persistence.Column;

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
public class Tool {
    @Column(name="IGNORED_BRAND")
    public String brand;

    public int id;

    public Tool() {}
}
