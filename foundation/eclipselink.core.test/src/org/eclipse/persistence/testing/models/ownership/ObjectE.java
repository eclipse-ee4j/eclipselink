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
package org.eclipse.persistence.testing.models.ownership;

import java.io.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class ObjectE implements Serializable {
    public Number id;
    public String name;
    public ValueHolderInterface oneToOneBack;

    public ObjectE() {
        oneToOneBack = new ValueHolder();
    }

    public ObjectE(ObjectD objectD) {
        oneToOneBack = new ValueHolder();
        oneToOneBack.setValue(objectD);
    }

    public static ObjectE example1(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E1");
        return example;
    }

    public static ObjectE example10(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E10");
        return example;
    }

    public static ObjectE example11(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E11");
        return example;
    }

    public static ObjectE example12(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E12");
        return example;
    }

    public static ObjectE example13(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E13");
        return example;
    }

    public static ObjectE example14(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E14");
        return example;
    }

    public static ObjectE example15(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E15");
        return example;
    }

    public static ObjectE example16(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E16");
        return example;
    }

    public static ObjectE example17(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E17");
        return example;
    }

    public static ObjectE example18(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E18");
        return example;
    }

    public static ObjectE example2(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E2");
        return example;
    }

    public static ObjectE example3(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E3");
        return example;
    }

    public static ObjectE example4(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E4");
        return example;
    }

    public static ObjectE example5(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E5");
        return example;
    }

    public static ObjectE example6(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E6");
        return example;
    }

    public static ObjectE example7(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E7");
        return example;
    }

    public static ObjectE example8(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E8");
        return example;
    }

    public static ObjectE example9(ObjectD anExample) {
        ObjectE example = new ObjectE();

        example.getOneToOneBack().setValue(anExample);
        example.setName("E9");
        return example;
    }

    public ValueHolderInterface getOneToOneBack() {
        return oneToOneBack;
    }

    public void setName(String aName) {
        name = aName;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("OWNER_E");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 10);
        definition.addField("BACK_REF", java.math.BigDecimal.class, 15);
        definition.addForeignKeyConstraint("OWNER_E_BACK_REF", "BACK_REF", "ID", "OWNER_D");

        return definition;
    }
}
