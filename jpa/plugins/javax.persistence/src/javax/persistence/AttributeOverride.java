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
*     pkrogh -        Java Persistence API 2.0 Public Draft
*                     Specification and licensing terms available from
*                     http://jcp.org/en/jsr/detail?id=317
*
* EARLY ACCESS - PUBLIC DRAFT
* This is an implementation of an early-draft specification developed under the 
* Java Community Process (JCP) and is made available for testing and evaluation 
* purposes only. The code is not compatible with any specification of the JCP.
******************************************************************************/
package javax.persistence;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The <code>AttributeOverride</code> annotation is used to 
 * override the mapping of a {@link Basic} (whether explicit or 
 * default) property or field or Id property or field.
 *
 * <p> The <code>AttributeOverride</code> annotation may be 
 * applied to an entity that extends a mapped superclass or to 
 * an embedded field or property to override a basic mapping 
 * defined by the mapped superclass or embeddable class. If the 
 * <code>AttributeOverride</code> annotation is not specified, 
 * the column is mapped the same as in the original mapping.
 *
 * <pre>
 * <p> Example:
 *
 *   &#064;MappedSuperclass
 *   public class Employee {
 *       &#064;Id protected Integer id;
 *       &#064;Version protected Integer version;
 *       protected String address;
 *       public Integer getId() { ... }
 *       public void setId(Integer id) { ... }
 *       public String getAddress() { ... }
 *       public void setAddress(String address) { ... }
 *   }
 *
 *   &#064;Entity
 *   &#064;AttributeOverride(name="address", column=&#064;Column(name="ADDR"))
 *   public class PartTimeEmployee extends Employee {
 *       // address field mapping overridden to ADDR
 *       protected Float wage();
 *       public Float getHourlyWage() { ... }
 *       public void setHourlyWage(Float wage) { ... }
 *   }
 * </pre>
 *
 * @see Embedded
 * @see Embeddable
 * @see MappedSuperclass
 *
 * @since Java Persistence API 1.0
 */
@Target({TYPE, METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface AttributeOverride {

    /**
     * (Required) The name of the property whose mapping is being 
     * overridden if property-based access is being used, or the 
     * name of the field if field-based access is used.
     */
    String name();

    /**
     * (Required) The column that is being mapped to the persistent 
     * attribute. The mapping type will remain the same as is 
     * defined in the embeddable class or mapped superclass.
     */
    Column column();
}
