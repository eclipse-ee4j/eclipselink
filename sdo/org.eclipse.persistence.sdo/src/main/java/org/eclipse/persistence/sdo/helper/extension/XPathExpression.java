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
package org.eclipse.persistence.sdo.helper.extension;

import java.util.List;
import java.util.Map;
import commonj.sdo.DataObject;

public class XPathExpression {
    private String expression;

    /**
     * This constructor sets the expression to the provided value.
     *
     * @param expression
     */
    public XPathExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Execute the xpath expression on the provided dataObject,
     * using the values in the map.
     *
     * @param dataObject
     * @param values
     * @return
     */
    public List evaluate(DataObject dataObject, Map values) {
        return XPathHelper.getInstance().evaluate(expression, dataObject);
    }
}
