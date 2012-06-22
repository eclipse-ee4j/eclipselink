/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.querydef;

/**
 * <p>
 * <b>Purpose</b>: Represents a Selection in the Criteria API implementation hierarchy.
 * <p>
 * <b>Description</b>: An InternalSelection has the EclipseLink expression representation of the
 * Criteria API expressions.  A special interface was created because Subqueries can be selections but are not in the
 * ExpressionImpl hierarchy
 * <p>
 * 
 * @see javax.persistence.criteria Expression
 * 
 * @author gyorke
 * @since EclipseLink 1.2
 */
public interface InternalSelection{

    public void findRootAndParameters(AbstractQueryImpl criteriaQuery);
    
    public org.eclipse.persistence.expressions.Expression getCurrentNode();
    
    public boolean isFrom();
    public boolean isRoot();
    public boolean isConstructor();

}
