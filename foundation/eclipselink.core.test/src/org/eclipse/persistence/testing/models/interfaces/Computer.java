/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.tools.schemaframework.*;

public class Computer implements CompanyAsset {
    public java.math.BigDecimal serNum;
    public String description;

    public Computer() {
        super();
    }

    public Object clone() {
        Computer object = new Computer();
        object.description = this.description;
        object.serNum = this.serNum;
        return object;
    }

    public static Computer example1() {
        Computer example = new Computer();
        example.description = "Pentuim 133 with 256MB RAM";
        return example;
    }

    public static Computer example2() {
        Computer example = new Computer();
        example.description = "Pentuim 266 with 32MB RAM 8MB Video";
        return example;
    }

    public static Computer example3() {
        Computer example = new Computer();
        example.description = "K6 166 48MB RAM";
        return example;
    }

    public String getDescrip() {
        return this.description;
    }

    public Number getId() {
        return serNum;
    }

    public java.math.BigDecimal getSerNum() {
        return serNum;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();

        table.setName("INT_COMP");
        table.addField("SERNUM", java.math.BigDecimal.class, 15);
        table.addField("DESCRIP", String.class, 50);

        return table;
    }
}
