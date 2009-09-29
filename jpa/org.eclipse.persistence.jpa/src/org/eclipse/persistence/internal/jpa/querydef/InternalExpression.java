/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Gordon Yorke - Initial development
 *
 ******************************************************************************/package org.eclipse.persistence.internal.jpa.querydef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;

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
public interface InternalExpression{

    public boolean isPredicate();

    public boolean isCompoundExpression();
    
    public boolean isExpression();
    
    public boolean isLiteral();
    
    public boolean isParameter();

    
}
