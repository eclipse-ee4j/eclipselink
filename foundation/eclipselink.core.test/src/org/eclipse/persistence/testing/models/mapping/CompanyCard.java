/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class CompanyCard implements Serializable {
    public Address owner;
    public int limit;
    public String number;

    public CompanyCard() {
    }

    public static CompanyCard example1() {
        CompanyCard example = new CompanyCard();

        example.setLimit(15000);
        example.setNumber("51545450");
        return example;
    }

    public String getNumber() {
        return this.number;
    }

    public Address getOwner() {
        return this.owner;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setOwner(Address owner) {
        this.owner = owner;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_CARD");
        //LIMIT is a key word in MySQL.  Changed to AMOUNT_LIMIT
        definition.addField("AMOUNT_LIMIT", Integer.class);
        definition.addField("COM_ID", java.math.BigDecimal.class, 15);
        definition.addField("CARDNUMBER", String.class, 30);

        definition.addForeignKeyConstraint("MAP_CARD_MAP_ADD", "COM_ID", "A_ID", "MAP_ADD");

        return definition;
    }
}
