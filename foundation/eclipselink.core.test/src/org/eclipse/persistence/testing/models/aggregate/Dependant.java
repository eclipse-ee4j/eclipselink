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
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Dependant {
    private int age;
    private String firstName;

    public Dependant() {
        super();
    }

    public Dependant(String firstName, int age) {
        this.firstName = firstName;
        this.age = age;

    }

    public static Dependant example1() {
        Dependant example1 = new Dependant();

        example1.setFirstName("Linda");
        example1.setAge(7);

        return example1;
    }

    public static Dependant example2() {
        Dependant example2 = new Dependant();

        example2.setFirstName("Kyle");
        example2.setAge(3);

        return example2;
    }

    public static Dependant example3() {
        Dependant example3 = new Dependant();

        example3.setFirstName("Paige");
        example3.setAge(11);

        return example3;
    }

    public static Dependant example4() {
        Dependant example4 = new Dependant();

        example4.setFirstName("Williams");
        example4.setAge(15);

        return example4;
    }

    public static Dependant example5() {
        Dependant example5 = new Dependant();

        example5.setFirstName("Janice");
        example5.setAge(7);

        return example5;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/12/2000 9:51:38 AM)
     * @return int
     */
    public int getAge() {
        return age;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/12/2000 9:51:54 AM)
     * @return java.lang.String
     */
    public java.lang.String getFirstName() {
        return firstName;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/12/2000 9:51:38 AM)
     * @param newAge int
     */
    public void setAge(int newAge) {
        age = newAge;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04/12/2000 9:51:54 AM)
     * @param newName java.lang.String
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("DEPENDANT");

        definition.addPrimaryKeyField("CUSTOMER_ID", java.math.BigDecimal.class, 15);
        definition.addPrimaryKeyField("FIRST_NAME", String.class, 15);
        definition.addField("AGE", Integer.class, 20);
        return definition;
    }
}
