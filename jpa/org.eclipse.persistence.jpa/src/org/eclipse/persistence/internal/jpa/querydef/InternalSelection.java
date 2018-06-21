/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - Initial development
//
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

    public void findRootAndParameters(CommonAbstractCriteriaImpl criteriaQuery);

    public org.eclipse.persistence.expressions.Expression getCurrentNode();

    public boolean isFrom();
    public boolean isRoot();
    public boolean isConstructor();

}
