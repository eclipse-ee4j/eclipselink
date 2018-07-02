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
package org.eclipse.persistence.testing.models.sequencing;

/**
 *  Simple Object for Sequence Testing
 */
public class SeqTestClass2 {
    public String test1;
    public String test2;
    public String pkey;// String for a Primary Key

    public SeqTestClass2() {
        super();
        test1 = "#Default";
        test2 = "#Default";
    }

    public String getPkey() {
        return pkey;
    }

    public String getTest1() {
        return test1;
    }

    public String getTest2() {
        return test2;
    }

    public void setPkey(String thekey) {
        pkey = thekey;
    }

    public void setTest1(String theFirstField) {
        test1 = theFirstField;
    }

    public void setTest2(String theFirstField) {
        test2 = theFirstField;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("SEQTESTTABLE2");
        definition.addPrimaryKeyField("PKEY", String.class, 15);
        definition.addField("FIRST_", String.class, 40);
        definition.addField("SECOND_", String.class, 40);
        return definition;
    }
}
