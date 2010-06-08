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
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import java.util.Vector;

public class SingleHouse extends House {
    private int numberOfGarages;

    /**
     * SingleHouse constructor comment.
     */
    public SingleHouse() {
        super();
    }

    public static SingleHouse example1() {
        SingleHouse example1 = new SingleHouse();

        Oid insurancePolicyId = new Oid();
        insurancePolicyId.setOid(new Integer(15));
        example1.setInsuranceId(insurancePolicyId);
        example1.setDescriptions("beautiful 4 bedroom single house");
        example1.setLocation("435 Carling Ave.");
        example1.setNumberOfGarages(11);
        Vector sellingPoints = example1.getSellingPoints();

        // CR#2896 - Get examples from RoomSellingPoint now - TW
        sellingPoints.add(RoomSellingPoint.example1());
        sellingPoints.add(RoomSellingPoint.example2());
        return example1;
    }

    public static SingleHouse example2() {
        SingleHouse example2 = new SingleHouse();

        Oid insurancePolicyId = new Oid();
        insurancePolicyId.setOid(new Integer(27));
        example2.setInsuranceId(insurancePolicyId);
        example2.setDescriptions("spacious 5 bedroom banglow");
        example2.setLocation("33D King Edward Street");
        example2.setNumberOfGarages(22);

        return example2;
    }

    public int getNumberOfGarages() {
        return numberOfGarages;
    }

    public void setNumberOfGarages(int newNumberOfGarage) {
        numberOfGarages = newNumberOfGarage;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("SINGLE_HOUSE");

        definition.addPrimaryKeyField("AGENT_ID", java.math.BigDecimal.class, 15);
        definition.addPrimaryKeyField("LOCATION", String.class, 200);
        definition.addField("GARAGE", Integer.class, 2);
        return definition;
    }
}
