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
import static javax.persistence.FetchType.EAGER;

/**
 * This annotation defines a single-valued association to another entity that
 * has one-to-one multiplicity. It is not normally necessary to specify the
 * associated target entity explicitly since it can usually be inferred from the
 * type of the object being referenced.
 * 
 * <pre>
 *    Example 1: One-to-one association that maps a foreign key column
 *    On Customer class:
 *     @OneToOne(optional=false)
 *     @JoinColumn(
 *    	name=&quot;CUSTREC_ID&quot;, unique=true, nullable=false, updatable=false)
 *    public CustomerRecord getCustomerRecord() { return customerRecord; }
 *    On CustomerRecord class:
 *     @OneToOne(optional=false, mappedBy=&quot;customerRecord&quot;)
 *    public Customer getCustomer() { return customer; }
 *    Example 2: One-to-one association that assumes both the source and target share the same primary key values. 
 *    On Employee class:
 *     @Entity
 *    public class Employee {
 *    	 @Id Integer id;
 *    
 *    	 @OneToOne  @PrimaryKeyJoinColumn
 *    	EmployeeInfo info;
 *    	...
 *    }
 *    On EmployeeInfo class:
 *     @Entity
 *    public class EmployeeInfo {
 *    	 @Id Integer id;
 *    	...
 *    }
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target( { METHOD, FIELD })
@Retention(RUNTIME)
public @interface OneToOne {

	/**
	 * (Optional) The entity class that is the target of the association.
	 * 
	 * <p>
	 * Defaults to the type of the field or property that stores the
	 * association.
	 */
	Class targetEntity() default void.class;

	/**
	 * (Optional) The operations that must be cascaded to the target of the
	 * association.
	 * 
	 * <p>
	 * By default no operations are cascaded.
	 */
	CascadeType[] cascade() default {};

	/**
	 * (Optional) Whether the association should be lazily loaded or must be
	 * eagerly fetched. The {@link FetchType#EAGER EAGER} strategy is a
	 * requirement on the persistence provider runtime that the associated
	 * entity must be eagerly fetched. The {@link FetchType#LAZY  LAZY} strategy
	 * is a hint to the persistence provider runtime.
	 */
	FetchType fetch() default EAGER;

	/**
	 * (Optional) Whether the association is optional. If set to false then a
	 * non-null relationship must always exist.
	 */
	boolean optional() default true;

	/**
	 * (Optional) The field that owns the relationship. This element is only
	 * specified on the inverse (non-owning) side of the association.
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
