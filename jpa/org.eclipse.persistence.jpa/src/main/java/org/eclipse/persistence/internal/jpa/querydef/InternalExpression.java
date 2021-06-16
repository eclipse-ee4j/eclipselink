/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
 */package org.eclipse.persistence.internal.jpa.querydef;

/**
 * <p>
 * <b>Purpose</b>: Represents an Expression in the Criteria API implementation heirarchy.
 * <p>
 * <b>Description</b>: Expressions are expression nodes that can not be joined from
 * and may or not be the result of a Path expression.  The SubQuery is a special type of expression that
 * requires certain methods but can not extend ExpressionImpl.  This interface provide a common API.
 * <p>
 *
 * @see jakarta.persistence.criteria Expression
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */
// Contributors:
//     Gordon Yorke - Initial development
//
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////package org.eclipse.persistence.internal.jpa.querydef;


/////
// <p>
// <b>Purpose</b>: Represents an Expression in the Criteria API implementation heirarchy.
// <p>
// <b>Description</b>: Expressions are expression nodes that can not be joined from
// and may or not be the result of a Path expression.  The SubQuery is a special type of expression that
// requires certain methods but can not extend ExpressionImpl.  This interface provide a common API.
// <p>
//
// @see jakarta.persistence.criteria Expression
//
// @author gyorke
// @since EclipseLink 1.2
public interface InternalExpression{

    public boolean isPredicate();

    public boolean isCompoundExpression();

    public boolean isExpression();

    public boolean isLiteral();

    public boolean isParameter();

    public boolean isSubquery();

    //conjunction or disjunction
    public boolean isJunction();

}
