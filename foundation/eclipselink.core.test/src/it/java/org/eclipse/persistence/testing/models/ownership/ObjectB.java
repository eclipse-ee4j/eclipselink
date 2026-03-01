/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.ownership;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.mappings.querykeys.OneToOneQueryKey;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

import java.io.Serializable;
import java.util.Vector;

public class ObjectB implements Serializable {
    public Number id;
    public String name;
    public ValueHolderInterface oneToMany;

    public ObjectB() {
        oneToMany = new ValueHolder();
    }

    public static void addQueryKeys(ClassDescriptor descriptor) {
        OneToOneQueryKey ownerQueryKey = new OneToOneQueryKey();
        ownerQueryKey.setName("owner");
        ownerQueryKey.setReferenceClass(ObjectA.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        ownerQueryKey.setJoinCriteria(builder.getField("OWNER_A.ONE_TO_ONE_").equal(builder.getParameter(new org.eclipse.persistence.internal.helper.DatabaseField("OWNER_B.ID"))));
        descriptor.addQueryKey(ownerQueryKey);
    }

    public static ObjectB example1() {
        ObjectB example = new ObjectB();
        Vector objects = new Vector();

        objects.add(ObjectC.example1(example));
        objects.add(ObjectC.example2(example));

        example.getOneToMany().setValue(objects);
        example.setName("B1");
        return example;
    }

    public static ObjectB example2() {
        ObjectB example = new ObjectB();
        Vector objects = new Vector();

        objects.add(ObjectC.example2(example));
        objects.add(ObjectC.example3(example));

        example.getOneToMany().setValue(objects);
        example.setName("B2");
        return example;
    }

    public static ObjectB example3() {
        ObjectB example = new ObjectB();
        Vector objects = new Vector();

        objects.add(ObjectC.example3(example));
        objects.add(ObjectC.example1(example));

        example.getOneToMany().setValue(objects);
        example.setName("B3");
        return example;
    }

    public ValueHolderInterface getOneToMany() {
        return oneToMany;
    }

    public void setName(String aName) {
        name = aName;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("OWNER_B");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 10);
        definition.addField("VERSION", java.math.BigDecimal.class, 10);

        return definition;
    }
}
