/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.jpa;


import javax.persistence.criteria.Expression;

/**
 * PUBLIC:
 * EclipseLInk specific JPA Criteria interface.  Provides the functionality defined in
 * javax.persistence.criteria.CriteriaBuilder and adds EclipseLink specific
 * functionality.
 */
public interface JpaCriteriaBuilder extends javax.persistence.criteria.CriteriaBuilder {

    /**
     * ADVANCED:
     * Allow a Criteria Expression to be built from a EclipseLink native API Expression object.
     * This allows for an extended functionality supported in EclipseLink Expressions to be used in Criteria.
     */
    <T> Expression<T> fromExpression(org.eclipse.persistence.expressions.Expression expression, Class<T> type);

    /**
     * ADVANCED:
     * Allow a Criteria Expression to be built from a EclipseLink native API Expression object.
     * This allows for an extended functionality supported in EclipseLink Expressions to be used in Criteria.
     */
    Expression fromExpression(org.eclipse.persistence.expressions.Expression expression);

    /**
     * ADVANCED:
     * Allow a Criteria Expression to be converted to a EclipseLink native API Expression object.
     * This allows for roots and paths defined in the Criteria to be used with EclipseLink native API Expresions.
     */
    org.eclipse.persistence.expressions.Expression toExpression(Expression expression);

}
