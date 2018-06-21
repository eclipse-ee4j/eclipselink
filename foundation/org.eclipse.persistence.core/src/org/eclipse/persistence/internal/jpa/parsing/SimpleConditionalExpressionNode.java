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
package org.eclipse.persistence.internal.jpa.parsing;


/**
 * INTERNAL
 * <p><b>Purpose</b>: Superclass for Conditional expressions
 * BetweenNode, EmptyCollectionComparisonNode, InNode,
 * IsNode, LikeNode, NullComparisonNode
 * <p><b>Responsibilities</b>:<ul>
 * <li> None, just a place holder
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class SimpleConditionalExpressionNode extends Node {

    /**
     * SimpleConditionalExpressionNode constructor comment.
     */
    public SimpleConditionalExpressionNode() {
        super();
    }
}
