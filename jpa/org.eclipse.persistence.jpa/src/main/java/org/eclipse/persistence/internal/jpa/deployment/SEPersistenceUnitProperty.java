/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     08/21/2009-2.0 Guy Pelletier
//       - 267391: JPA 2.0 Cache Usage Settings
package org.eclipse.persistence.internal.jpa.deployment;

/**
 * INTERNAL:
 * Object to hold onto persistence unit properties as specified in the
 * persistence.xml property. Currently only the CanonicalModelProcessor
 * populates the properties through instances of this class through an OX
 * mapping file. The existing persistence.xml reading and processing is done
 * via the old xml parser.
 *
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public class SEPersistenceUnitProperty {
    protected String name;
    protected String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
