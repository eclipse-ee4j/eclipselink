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
 *               Specification and licensing terms available from
 *               http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence.metamodel;

/**
 * A member of a Java type
 * 
 * @param <X>
 *            The represented type that contains the member
 * @param <Y>
 *            The type of the represented member
 */
public interface Member<X, Y> {
    /**
     * Return the name of the member.
     * 
     * @return name
     */
    String getName();

    /**
     * Return the managed type representing the type in which the member was
     * declared.
     * 
     * @return declaring type
     */
    ManagedType<X> getDeclaringType();

    /**
     * Return the Java type of the represented member.
     * 
     * @return Java type
     */
    Class<Y> getMemberJavaType();

    /**
     * Return the java.lang.reflect.Member for the represented member.
     * 
     * @return corresponding java.lang.reflect.Member
     */
    java.lang.reflect.Member getJavaMember();

    /**
     * Is the member an association.
     * 
     * @return whether an association
     */
    boolean isAssociation();

    /**
     * Is the member collection-valued.
     * 
     * @return whether a collection
     */
    boolean isCollection();
}