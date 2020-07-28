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
package org.eclipse.persistence.testing.tests.returning.model;

public class Class2 extends BaseClass {
    public Class2() {
        super();
    }

    public Class2(double a, double b) {
        super(a, b);
    }

    public Class2(double a, double b, double c) {
        super(a, b, c);
    }

    public Class2(double c) {
        super(c);
    }

    public String getFieldAName() {
        return "A2";
    }

    public String getFieldBName() {
        return "B2";
    }
}
