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
 *               Specification available from http://jcp.org/en/jsr/detail?id=317
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
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.FetchType.LAZY;

/**
 * Defines a many-valued association with many-to-many multiplicity. 
 * If the Collection is defined using generics to specify the element 
 * type, the associated target entity class does not need to be 
 * specified; otherwise it must be specified.
 *
 * <p> Every many-to-many association has two sides, the owning 
 * side and the non-owning, or inverse, side.  The join table is 
 * specified on the owning side. If the association is bidirectional, 
 * either side may be designated as the owning side.
 *
 * <p> The same annotation elements for the {@link OneToMany} 
 * annotation apply to the <code>ManyToMany</code> annotation. 
 *
 * <pre>
 *
 *    Example 1:
 *
 *    In Customer class:
 *
 *    &#064;ManyToMany
 *    &#064;JoinTable(name="CUST_PHONES")
 *    public Set<PhoneNumber> getPhones() { return phones; }
 *
 *    In PhoneNumber class:
 *
 *    &#064;ManyToMany(mappedBy="phones")
 *    public Set<Customer> getCustomers() { return customers; }
 *
 *    Example 2:
 *
 *    In Customer class:
 *
 *    &#064;ManyToMany(targetEntity=com.acme.PhoneNumber.class)
 *    public Set getPhones() { return phones; }
 *
 *    In PhoneNumber class:
 *
 *    &#064;ManyToMany(targetEntity=com.acme.Customer.class, mappedBy="phones")
 *    public Set getCustomers() { return customers; }
 *
 *    Example 3:
 *
 *    In Customer class:
 *
 *    &#064;ManyToMany
 *    &#064;JoinTable(name="CUST_PHONE",
 *        joinColumns=
 *            &#064;JoinColumn(name="CUST_ID", referencedColumnName="ID"),
 *        inverseJoinColumns=
 *            &#064;JoinColumn(name="PHONE_ID", referencedColumnName="ID")
 *        )
 *    public Set<PhoneNumber> getPhones() { return phones; }
 *
 *    In PhoneNumberClass:
 *
 *    &#064;ManyToMany(mappedBy="phones")
 *    public Set<Customer> getCustomers() { return customers; }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface ManyToMany {

    /**
     * (Optional) The entity class that is the target
     * of the association. Optional only if the collection
     * property is defined using Java generics.
     * Must be specified otherwise.
     *
     * <p> Defaults to the parameterized type of
     * the collection when defined using generics.
     */
    Class targetEntity() default void.class;

    /** 
     * (Optional) The operations that must be cascaded to 
     * the target of the association.
     * <p> Defaults to no operations being cascaded.
     */
    CascadeType[] cascade() default {};

    /** (Optional) Whether the association should be
     * lazily loaded or must be eagerly fetched. The
     * {@link FetchType#EAGER EAGER} strategy is a 
     * requirement on the persistence provider runtime 
     * that the associated entities must be eagerly fetched. 
     * The {@link FetchType#LAZY LAZY} strategy is a hint 
     * to the persistence provider runtime.
     */
    FetchType fetch() default LAZY;

    /** 
     * The field that owns the relationship. Required unless 
     * the relationship is unidirectional.
     */
    String mappedBy() default "";
}
