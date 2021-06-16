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
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.tools.schemaframework.*;

public class Vehicle implements CompanyAsset {
    public java.math.BigDecimal serNum;
    public String make;
    public String model;
    public String colour;

    public Vehicle() {
        super();
    }

    public Object clone() {
        Vehicle object = new Vehicle();
        object.serNum = this.serNum;
        object.make = this.make;
        object.model = this.model;
        object.colour = this.colour;
        return object;
    }

    public static Vehicle example1() {
        Vehicle example = new Vehicle();
        example.make = "Ford";
        example.model = "Windstar";
        example.colour = "Teal";
        return example;
    }

    public static Vehicle example2() {
        Vehicle example = new Vehicle();
        example.make = "Chrysler";
        example.model = "Concord";
        example.colour = "Ruby Red";
        return example;
    }

    public static Vehicle example3() {
        Vehicle example = new Vehicle();
        example.make = "Pontiac";
        example.model = "Fire Bird";
        example.colour = "Pearl Forest Green";
        return example;
    }

    public String getColour() {
        return this.colour;
    }

    public String getMake() {
        return this.make;
    }

    public String getModel() {
        return this.model;
    }

    public java.math.BigDecimal getSerNum() {
        return serNum;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();

        table.setName("INT_VEH");
        table.addField("SERNUM", java.math.BigDecimal.class, 15);
        table.addField("COLOUR", String.class, 50);
        table.addField("MAKE", String.class, 50);
        table.addField("MODEL", String.class, 50);

        return table;
    }
}
