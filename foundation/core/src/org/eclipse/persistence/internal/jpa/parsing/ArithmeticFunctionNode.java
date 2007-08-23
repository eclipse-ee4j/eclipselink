/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
