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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.sessionbroker;

import java.util.*;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.platform.database.DatabasePlatform;

/**
 * Database platform containing a custom platform function
 */
public class CustomDatabasePlatform extends DatabasePlatform {

    public static int OPERATOR_SELECTOR = 1234;

    public CustomDatabasePlatform() {
        super();
        initializePlatformOperators();
    }

    protected void initializePlatformOperators() {
        super.initializePlatformOperators();

        // Create user-defined function
        ExpressionOperator operator = new ExpressionOperator();
        operator.setSelector(OPERATOR_SELECTOR);

        Vector<String> v = new Vector<String>();
        v.addElement("upper(");
        v.addElement(")");

        operator.printsAs(v);
        operator.bePostfix();
        operator.setNodeClass(ClassConstants.FunctionExpression_Class);

        addOperator(operator);
    }

}
