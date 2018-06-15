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
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.testing.models.inheritance.A_2_King2;

public class A_2_1_King2 extends A_2_King2 {
    private String ha;

    public A_2_1_King2() {
        super();
    }

    public static A_2_1_King2 exp5() {
        A_2_1_King2 a5 = new A_2_1_King2();
        a5.setIndex(5);
        a5.setBar("bar from A_2_1");
        a5.setFoo("foo from A_2_1");
        a5.setHa("Ha Ha Ha ...");
        return a5;
    }

    public String getHa() {
        return ha;
    }

    public void setHa(String theHa) {
        ha = theHa;
    }
}
