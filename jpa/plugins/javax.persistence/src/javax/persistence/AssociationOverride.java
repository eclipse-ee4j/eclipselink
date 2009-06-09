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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is used to override a many-to-one or
 * one-to-one mapping of property or field for an entity relationship.
 *
 * <p> The <code>AssociationOverride</code> annotation may be applied
 * to an entity that extends a mapped superclass to override a many-to-one
 * or one-to-one mapping defined by the mapped superclass. If the
 * <code>AssociationOverride</code> annotation is not specified, the join
 * column is mapped the same as in the original mapping.
 * <pre>
 *    Example:
 *    &#064;MappedSuperclass
 *    public class Employee {
 *        ...
 *        &#064;ManyToOne
 *        protected Address address;
 *        ...
 *    }
 *    
 *    &#064;Entity 
 *    &#064;AssociationOverride(name="address", 
 *        joinColumns=&#064;JoinColumn(name="ADDR_ID"))
 *    // address field mapping overridden to ADDR_ID fk
 *    public class PartTimeEmployee extends Employee {
 *        ...
 *    }
 * </pre>
 *
 * @see OneToOne
 * @see ManyToOne
 * @see MappedSuperclass
 *
 * @since Java Persistence 1.0 
 */
@Target({TYPE, METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface AssociationOverride {

    /**
     * The name of the relationship property whose mapping is
     * being overridden if property-based access is being used,
     * or the name of the relationship field if field-based access is used.
     */
    String name();

    /**
     * The join column that is being mapped to the persistent
     * attribute. The mapping type will remain the same as is defined
     * in the mapped superclass.
     */
    JoinColumn[] joinColumns() default {};
    /**
     * The joinTable element is used to override the 
     * mapping of the join table and/or its join columns.
     * @since Java Persistence API 2.0
     **/
    JoinTable joinTable() default @JoinTable;
}
