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
