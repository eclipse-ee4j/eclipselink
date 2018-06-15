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
package org.eclipse.persistence.internal.jpa.parsing;


/**
 * INTERNAL
 * <p><b>Purpose</b>: This is the superclass for all the Arithmetic functions in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> None, subclasses do all the work
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class ArithmeticFunctionNode extends FunctionalExpressionNode {

    /**
     * ArithmeticFunctionNode constructor comment.
     */
    public ArithmeticFunctionNode() {
        super();
    }
}
