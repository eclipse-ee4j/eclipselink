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
package org.eclipse.persistence.testing.models.mapping;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * SecureSystem holds just a manufacturer and a OneToOneMapping to it's Identification
 * It is designed to have an attribute of the same name as an attribute in it's id attribute.
 * Note, the id foreign key is also a component of SecureSystem's primary key.
 * @author tware
 *
 */
public class SecureSystem {

    private String manufacturer;
    private Identification id;

    public SecureSystem() {
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Identification getId() {
        return id;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;

    }

    public void setId(Identification id) {
        this.id = id;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_SECURE");

        definition.addIdentityField("MANUFACTURER", String.class, 30);
        definition.addField("IDENTIFICATION_ID", java.math.BigDecimal.class, 15);
        return definition;
    }
}
