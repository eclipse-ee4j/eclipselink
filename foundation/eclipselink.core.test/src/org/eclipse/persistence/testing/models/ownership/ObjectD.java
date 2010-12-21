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
import java.util.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.tools.schemaframework.*;

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

        objects.addElement(ObjectE.example1(example));
        objects.addElement(ObjectE.example2(example));
        objects.addElement(ObjectE.example3(example));

        example.getOneToMany().setValue(objects);
        example.setName("D1");
        return example;
    }

    public static ObjectD example2() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.addElement(ObjectE.example4(example));
        objects.addElement(ObjectE.example5(example));
        objects.addElement(ObjectE.example6(example));

        example.getOneToMany().setValue(objects);
        example.setName("D2");
        return example;
    }

    public static ObjectD example3() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.addElement(ObjectE.example7(example));
        objects.addElement(ObjectE.example8(example));
        objects.addElement(ObjectE.example9(example));

        example.getOneToMany().setValue(objects);
        example.setName("D3");
        return example;
    }

    public static ObjectD example4() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.addElement(ObjectE.example10(example));
        objects.addElement(ObjectE.example11(example));
        objects.addElement(ObjectE.example12(example));

        example.getOneToMany().setValue(objects);
        example.setName("D4");
        return example;
    }

    public static ObjectD example5() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.addElement(ObjectE.example13(example));
        objects.addElement(ObjectE.example14(example));
        objects.addElement(ObjectE.example15(example));

        example.getOneToMany().setValue(objects);
        example.setName("D5");
        return example;
    }

    public static ObjectD example6() {
        ObjectD example = new ObjectD();
        Vector objects = new Vector();

        objects.addElement(ObjectE.example16(example));
        objects.addElement(ObjectE.example17(example));
        objects.addElement(ObjectE.example18(example));

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
