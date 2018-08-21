/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Phone implements Serializable, Cloneable {
    public Number id;
    public String areaCode;
    public String number;

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            return null;
        }
    }

    public Object copy() {
        return clone();
    }

    public static Phone example1() {
        Phone example = new Phone();

        example.setAreaCode("613");
        example.setNumber("2258812");
        return example;
    }

    public static Phone example10() {
        Phone example = new Phone();

        example.setAreaCode("491");
        example.setNumber("125436");
        return example;
    }

    public static Phone example11() {
        Phone example = new Phone();

        example.setAreaCode("512");
        example.setNumber("125436");
        return example;
    }

    public static Phone example12() {
        Phone example = new Phone();

        example.setAreaCode("613");
        example.setNumber("5559988");
        return example;
    }

    public static Phone example13() {
        Phone example = new Phone();

        example.setAreaCode("013");
        example.setNumber("0130133");
        return example;
    }

    public static Phone example14() {
        Phone example = new Phone();

        example.setAreaCode("014");
        example.setNumber("0144444");
        return example;
    }

    public static Phone example15() {
        Phone example = new Phone();

        example.setAreaCode("015");
        example.setNumber("0155555");
        return example;
    }

    public static Phone example16() {
        Phone example = new Phone();

        example.setAreaCode("016");
        example.setNumber("0166666");
        return example;
    }

    public static Phone example17() {
        Phone example = new Phone();

        example.setAreaCode("017");
        example.setNumber("0177777");
        return example;
    }

    public static Phone example18() {
        Phone example = new Phone();

        example.setAreaCode("018");
        example.setNumber("0188888");
        return example;
    }

    public static Phone example2() {
        Phone example = new Phone();

        example.setAreaCode("613");
        example.setNumber("2253434");
        return example;
    }

    public static Phone example3() {
        Phone example = new Phone();

        example.setAreaCode("613");
        example.setNumber("4353111");
        return example;
    }

    public static Phone example4() {
        Phone example = new Phone();

        example.setAreaCode("613");
        example.setNumber("2381111");
        return example;
    }

    public static Phone example5() {
        Phone example = new Phone();

        example.setAreaCode("514");
        example.setNumber("5989890");
        return example;
    }

    public static Phone example6() {
        Phone example = new Phone();

        example.setAreaCode("514");
        example.setNumber("666666");
        return example;
    }

    public static Phone example7() {
        Phone example = new Phone();

        example.setAreaCode("514");
        example.setNumber("123456");
        return example;
    }

    public static Phone example8() {
        Phone example = new Phone();

        example.setAreaCode("514");
        example.setNumber("6867777");
        return example;
    }

    public static Phone example9() {
        Phone example = new Phone();

        example.setAreaCode("491");
        example.setNumber("3434433");
        return example;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_PHO");

        definition.addIdentityField("P_ID", java.math.BigDecimal.class, 15);
        definition.addField("AREACODE", String.class, 15);
        definition.addField("PNUMBER", String.class, 30);
        return definition;
    }
}
