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
package org.eclipse.persistence.testing.models.ownership;

import java.io.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class ObjectC implements Serializable {
    public Number id;
    public String name;
    public ValueHolderInterface oneToOne;
    public ValueHolderInterface oneToOneBack;

    public ObjectC() {
        oneToOne = new ValueHolder();
        oneToOneBack = new ValueHolder();
    }

    public static void addQueryKeys(ClassDescriptor descriptor) {
        OneToOneQueryKey ownerQueryKey = new OneToOneQueryKey();
        ownerQueryKey.setName("root");
        ownerQueryKey.setReferenceClass(ObjectA.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression tableB = builder.getTable("OWNER_B");
        ownerQueryKey.setJoinCriteria(builder.getField("OWNER_A.ONE_TO_ONE_").equal(tableB.getField("OWNER_B.ID")).and(tableB.getField("OWNER_B.ID").equal(builder.getParameter(new org.eclipse.persistence.internal.helper.DatabaseField("OWNER_C.BACK_REF")))));
        descriptor.addQueryKey(ownerQueryKey);
    }

    public static ObjectC example1(ObjectB anExample) {
        ObjectC example = new ObjectC();

        example.getOneToOne().setValue(ObjectD.example1());
        example.getOneToOneBack().setValue(anExample);
        example.setName("C1");
        return example;
    }

    public static ObjectC example2(ObjectB anExample) {
        ObjectC example = new ObjectC();

        example.getOneToOne().setValue(ObjectD.example2());
        example.getOneToOneBack().setValue(anExample);
        example.setName("C2");
        return example;
    }

    public static ObjectC example3(ObjectB anExample) {
        ObjectC example = new ObjectC();

        example.getOneToOne().setValue(ObjectD.example3());
        example.getOneToOneBack().setValue(anExample);
        example.setName("C3");
        return example;
    }

    public static ObjectC example4(ObjectB anExample) {
        ObjectC example = new ObjectC();

        example.getOneToOne().setValue(ObjectD.example4());
        example.getOneToOneBack().setValue(anExample);
        example.setName("C4");
        return example;
    }

    public static ObjectC example5(ObjectB anExample) {
        ObjectC example = new ObjectC();

        example.getOneToOne().setValue(ObjectD.example5());
        example.getOneToOneBack().setValue(anExample);
        example.setName("C5");
        return example;
    }

    public static ObjectC example6(ObjectB anExample) {
        ObjectC example = new ObjectC();

        example.getOneToOne().setValue(ObjectD.example6());
        example.getOneToOneBack().setValue(anExample);
        example.setName("C6");
        return example;
    }

    public ValueHolderInterface getOneToOne() {
        return oneToOne;
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

        definition.setName("OWNER_C");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 10);
        definition.addField("ONE_TO_ONE_", java.math.BigDecimal.class, 15);
        definition.addField("BACK_REF", java.math.BigDecimal.class, 15);
        definition.addForeignKeyConstraint("OWNER_C_ONE_TO_ONE", "ONE_TO_ONE_", "ID", "OWNER_D");
        definition.addForeignKeyConstraint("OWNER_C_BACK_REF", "BACK_REF", "ID", "OWNER_B");

        return definition;
    }
}
