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
package org.eclipse.persistence.testing.models.ownership;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.tools.schemaframework.*;

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

        objects.addElement(ObjectC.example1(example));
        objects.addElement(ObjectC.example2(example));

        example.getOneToMany().setValue(objects);
        example.setName("B1");
        return example;
    }

    public static ObjectB example2() {
        ObjectB example = new ObjectB();
        Vector objects = new Vector();

        objects.addElement(ObjectC.example2(example));
        objects.addElement(ObjectC.example3(example));

        example.getOneToMany().setValue(objects);
        example.setName("B2");
        return example;
    }

    public static ObjectB example3() {
        ObjectB example = new ObjectB();
        Vector objects = new Vector();

        objects.addElement(ObjectC.example3(example));
        objects.addElement(ObjectC.example1(example));

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
