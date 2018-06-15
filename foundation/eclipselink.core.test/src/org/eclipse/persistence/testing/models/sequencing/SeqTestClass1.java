/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.sequencing;

import java.math.*;

/**
 *  Simple Object for Sequence Testing
 */
public class SeqTestClass1 {
    public String test1;
    public String test2;
    public BigDecimal pkey;

    public SeqTestClass1() {
        super();
        test1 = "#Default";
        test2 = "#Default";
    }

    public BigDecimal getPkey() {
        return pkey;
    }

    public String getTest1() {
        return test1;
    }

    public String getTest2() {
        return test2;
    }

    public void setPkey(BigDecimal thekey) {
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

        definition.setName("SEQTESTTABLE1");
        definition.addIdentityField("PKEY", BigDecimal.class, 15);
        definition.addField("FIRST_", String.class, 40);
        definition.addField("SECOND_", String.class, 40);
        return definition;
    }
}
