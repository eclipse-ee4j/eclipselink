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
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     		     Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import javax.persistence.CascadeType;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.FetchType.LAZY;

/**
 * Defines a many-valued association with one-to-many multiplicity.
 * 
 * <p>
 * If the collection is defined using generics to specify the element type, the
 * associated target entity type need not be specified; otherwise the target
 * entity class must be specified.
 * 
 * <pre>
 *    Example 1: One-to-Many association using generics
 *    In Customer class:
 *     @OneToMany(cascade=ALL, mappedBy=&quot;customer&quot;)
 *    public Set&lt;Order&gt; getOrders() { return orders; }
 *    In Order class:
 *     @ManyToOne
 *     @JoinColumn(name=&quot;CUST_ID&quot;, nullable=false)
 *    public Customer getCustomer() { return customer; }
 *    Example 2: One-to-Many association without using generics
 *    In Customer class:
 *     @OneToMany(targetEntity=com.acme.Order.class, cascade=ALL,
 *            mappedBy=&quot;customer&quot;)
 *    public Set getOrders() { return orders; }
 *    In Order class:
 *     @ManyToOne
 *     @JoinColumn(name=&quot;CUST_ID&quot;, nullable=false)
 *    public Customer getCustomer() { return customer; }
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target( { METHOD, FIELD })
@Retention(RUNTIME)
public @interface OneToMany {

	/**
	 * (Optional) The entity class that is the target of the association.
	 * Optional only if the collection property is defined using Java generics.
	 * Must be specified otherwise.
	 * 
	 * <p>
	 * Defaults to the parameterized type of the collection when defined using
	 * generics.
	 */
	Class targetEntity() default void.class;

	/**
	 * (Optional) The operations that must be cascaded to the target of the
	 * association.
	 * <p>
	 * Defaults to no operations being cascaded.
	 */
	CascadeType[] cascade() default {};

	/**
	 * (Optional) Whether the association should be lazily loaded or must be
	 * eagerly fetched. The {@link FetchType#EAGER EAGER} strategy is a
	 * requirement on the persistence provider runtime that the
	 * associated entities must be eagerly fetched. The
	 * {@link FetchType#LAZY LAZY} strategy is a hint to the persistence
	 * provider runtime.
	 */
	FetchType fetch() default LAZY;

	/**
	 * The field that owns the relationship. Required unless the relationship is
	 * unidirectional.
	 */
	String mappedBy() default "";

	/**
	 * (Optional) Whether to apply the remove operation to entities that have
	 * been removed from the relationship and to cascade the remove operation to
	 * those entities.
	 * @since Java Persistence API 2.0
	 */
	boolean orphanRemoval() default false;
}
