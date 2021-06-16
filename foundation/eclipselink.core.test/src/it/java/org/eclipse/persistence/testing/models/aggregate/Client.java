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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Client implements Serializable {
    public Oid id;
    public String name;
    public AddressDescription addressDescription;
    public Version version;

    public Client() {
        super();
        setOid(new Oid());
        setVersion(new Version());
    }

    public static Client example1() {
        Client example = new Client();

        example.setName("Bob Smith");
        example.setAddressDescription(AddressDescription.example1());
        return example;
    }

    public static Client example2() {
        Client example = new Client();

        example.setName("Lisa Ray");
        example.setAddressDescription(AddressDescription.example2());
        return example;
    }

    public static Client example3() {
        Client example = new Client();

        example.setName("David Garner");
        example.setAddressDescription(AddressDescription.example3());
        return example;
    }

    public Oid getOid() {
        return id;
    }

    public Version getVersion() {
        return version;
    }

    public void setAddressDescription(AddressDescription aDescription) {
        addressDescription = aDescription;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setOid(Oid i) {
        id = i;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("AGG_CLNT");

        definition.addIdentityField("CL_ID", java.math.BigDecimal.class, 15);
        definition.addField("CL_NAME", String.class, 20);
        definition.addField("CL_ADD", java.math.BigDecimal.class, 15);
        definition.addField("CL_SDATE", java.sql.Date.class);
        definition.addField("CL_EDATE", java.sql.Date.class);
        definition.addField("TYPE", String.class, 10);
        definition.addField("CL_ESDATE", java.sql.Date.class);
        definition.addField("CL_EEDATE", java.sql.Date.class);
        definition.addField("VERSION", java.math.BigDecimal.class, 15);

        return definition;
    }
}
