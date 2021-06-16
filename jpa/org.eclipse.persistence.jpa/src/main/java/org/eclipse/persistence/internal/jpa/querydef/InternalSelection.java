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
 * @see jakarta.persistence.criteria Expression
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
