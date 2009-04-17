/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence.criteria;

import javax.persistence.metamodel.Member;

/**
 * A join to an entity or embeddable type.
 * 
 * @param <Z>
 *            The source type of the join
 * @param <X>
 *            The target type of the join
 */
public interface Join<Z, X> extends From<Z, X> {
    /**
     * Return the metamodel member corresponding to the join.
     * 
     * @return metamodel member type for the join
     */
    Member<? extends Z, X> getMember();

    /**
     * Return the parent of the join.
     * 
     * @return join parent
     */
    From<?, Z> getParent();

    /**
     * Return the join type.
     * 
     * @return join type
     */
    JoinType getJoinType();
}