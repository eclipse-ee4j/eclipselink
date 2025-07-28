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

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

import java.io.Serializable;
import java.util.Vector;

public class ObjectD implements Serializable {
    public Number id;
    public String name;
    public ValueHolderInterface oneToMany;

    public ObjectD() {
        oneToMany = new ValueHolder();
    }

    public static ObjectD example1() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.add(ObjectE.example1(example));
        objects.add(ObjectE.example2(example));
        objects.add(ObjectE.example3(example));

        example.getOneToMany().setValue(objects);
        example.setName("D1");
        return example;
    }

    public static ObjectD example2() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.add(ObjectE.example4(example));
        objects.add(ObjectE.example5(example));
        objects.add(ObjectE.example6(example));

        example.getOneToMany().setValue(objects);
        example.setName("D2");
        return example;
    }

    public static ObjectD example3() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.add(ObjectE.example7(example));
        objects.add(ObjectE.example8(example));
        objects.add(ObjectE.example9(example));

        example.getOneToMany().setValue(objects);
        example.setName("D3");
        return example;
    }

    public static ObjectD example4() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.add(ObjectE.example10(example));
        objects.add(ObjectE.example11(example));
        objects.add(ObjectE.example12(example));

        example.getOneToMany().setValue(objects);
        example.setName("D4");
        return example;
    }

    public static ObjectD example5() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.add(ObjectE.example13(example));
        objects.add(ObjectE.example14(example));
        objects.add(ObjectE.example15(example));

        example.getOneToMany().setValue(objects);
        example.setName("D5");
        return example;
    }

    public static ObjectD example6() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.add(ObjectE.example16(example));
        objects.add(ObjectE.example17(example));
        objects.add(ObjectE.example18(example));

        example.getOneToMany().setValue(objects);
        example.setName("D6");
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

        definition.setName("OWNER_D");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 10);

        return definition;
    }
}
