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

public class A_2_King2 extends A_King2 {
    private String foo;

    public A_2_King2() {
        super();
    }

    public static A_2_King2 exp4() {
        A_2_King2 a4 = new A_2_King2();
        a4.setIndex(4);
        a4.setBar("bar form A_2");
        a4.setFoo("foo from A_2");
        return a4;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String theFoo) {
        foo = theFoo;
    }
}
