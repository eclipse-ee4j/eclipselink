/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
 * @see javax.persistence.criteria Expression
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
// @see javax.persistence.criteria Expression
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
