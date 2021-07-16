/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.platform.database.oracle.Oracle11Platform;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;

public class ExtractValueTest extends TestCase {
    Employee_XML employee = null;

    public ExtractValueTest() {
        setDescription("Tests the use of the extractVale function");
    }

    @Override
    public void setup() {
        if (!(getSession().getPlatform() instanceof Oracle11Platform)) {
            throw new TestWarningException("This test is intended for the Oracle 9 platform");
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public void test() {
        employee = (Employee_XML)getSession().readObject(Employee_XML.class, new ExpressionBuilder().get("resume").extractValue("/resume/first-name/text()").equal("Bob"));
    }

    @Override
    public void verify() {
        if ((employee == null) || !(employee.firstName.equals("Bob"))) {
            throw new TestErrorException("Wrong or No Employee returned");
        }
    }
}
