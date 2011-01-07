/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.indirection.*;

public class Employee implements ContactHolder, java.io.Serializable, Cloneable {
    public Number id;
    public String name;
    public Contact contact;
    public ValueHolderInterface secondaryContact = new ValueHolder();
    public Asset asset1;
    public Asset asset2;

    public static void addToDescriptor(ClassDescriptor des) {
        AggregateObjectMapping map = (AggregateObjectMapping)des.getMappingForAttributeName("asset2");
        map.addFieldNameTranslation("INT_EMP.ASSET2_TYPE", "INT_EMP.ASSET_TYPE");
    }
    
    /**
     * This method was written to do a shallow clone of the employee object
     */
    public Object clone() {
        Employee clone = new Employee();
        clone.id = this.id;
        clone.name = this.name;
        clone.contact = this.contact;
        clone.asset1 = (Asset)this.asset1.clone();
        clone.asset2 = (Asset)this.asset2.clone(); // added to test the V1-1 deep merge clone
        clone.secondaryContact = this.secondaryContact;
        return clone;
    }

    public static Employee example1() {
        Employee example = new Employee();
        example.setName("Betty");
        example.setContact(Phone.example1());
        example.getContact().setHolder(example);
        example.asset1 = new Asset();
        example.asset1.setAsset(Computer.example1());
        example.asset2 = new Asset();
        example.asset2.setAsset(Computer.example2());
        example.setSecondaryContact(Phone.example2());
        return example;
    }

    public static Employee example2() {
        Employee example = new Employee();
        example.setName("Chris");
        example.setContact(Phone.example1());
        example.getContact().setHolder(example);
        example.asset1 = new Asset();
        example.asset1.asset = Vehicle.example1();
        example.asset2 = new Asset();
        example.asset2.asset = Computer.example3();
        example.setSecondaryContact(Email.example3());

        return example;
    }

    public static Employee example3() {
        Employee example = new Employee();
        example.setName("Jude");
        example.setContact(Email.example2());
        example.getContact().setHolder(example);
        example.asset1 = new Asset();
        example.asset1.asset = Vehicle.example2();
        example.asset2 = new Asset();
        example.asset2.asset = Vehicle.example3();

        return example;
    }

    public static Employee example4() {
        Employee example = new Employee();
        example.setName("Brendan");
        example.setContact(null);
        example.asset1 = new Asset();
        example.asset1.asset = Vehicle.example2();
        example.asset2 = new Asset();
        example.asset2.asset = Vehicle.example3();

        return example;
    }

    public Contact getContact() {
        return contact;
    }

    public Number getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }

    public Contact getSecondaryContact() {
        return (Contact)secondaryContact.getValue();
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSecondaryContact(Contact aContact) {
        secondaryContact.setValue(aContact);
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();

        table.setName("INT_EMP");
        table.addField("ID", java.math.BigDecimal.class, 15);
        table.addField("NAME", String.class, 30);
        table.addField("CON_ID", java.math.BigDecimal.class, 15);
        //table.addField("CONTACT_TYPE",String.class, 5);
        table.addField("CONTACT_TYPE", Float.class, 5); // TO TEST NUMERIC TYPE INDICATOR

        table.addField("ASSET_ID", java.math.BigDecimal.class, 15);
        table.addField("ASSET_TYPE", String.class, 5);
        table.addField("ASSET2_ID", java.math.BigDecimal.class, 15);
        table.addField("ASSET2_TYPE", String.class, 5);
        table.addField("SEC_CON_ID", java.math.BigDecimal.class, 15);
        //table.addField("SEC_CONTACT_TYPE",String.class, 5);
        table.addField("SEC_CONTACT_TYPE", Float.class, 5); // TO TEST NUMERIC TYPE INDICATOR

        return table;
    }

    public String toString() {
        return getName();
    }
}
