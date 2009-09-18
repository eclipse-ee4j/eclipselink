/*******************************************************************************
 * Copyright (c) 2008, 2009 Sun Microsystems. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     Linda DeMichiel -Java Persistence 2.0 - Proposed Final Draft, Version 2.0 (August 31, 2009)
 *     Specification available from http://jcp.org/en/jsr/detail?id=317
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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a class whose instances are stored as an intrinsic 
 * part of an owning entity and share the identity of the entity. 
 * Each of the persistent properties or fields of the embedded 
 * object is mapped to the database table for the entity. 
 *
 * <p> Note that the {@link Transient} annotation may be used to 
 * designate the non-persistent state of an embeddable class.
 *
 * <pre>
 *
 *    Example 1:
 *
 *    &#064;Embeddable public class EmploymentPeriod { 
 *       &#064;Temporal(DATE) java.util.Date startDate;
 *       &#064;Temporal(DATE) java.util.Date endDate;
 *      ... 
 *    }
 *
 *    Example 2:
 *
 *    &#064;Embeddable public class PhoneNumber {
 *        protected String areaCode;
 *        protected String localNumber;
 *        &#064;ManyToOne PhoneServiceProvider provider;
 *        ...
 *     }
 *
 *    &#064;Entity public class PhoneServiceProvider {
 *        &#064;Id protected String name;
 *         ...
 *     }
 *
 *    Example 3:
 *
 *    &#064;Embeddable public class Address {
 *       protected String street;
 *       protected String city;
 *       protected String state;
 *       &#064;Embedded protected Zipcode zipcode;
 *    }
 *
 *    &#064;Embeddable public class Zipcode {
 *       protected String zip;
 *       protected String plusFour;
 *     }


 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE}) 
@Retention(RUNTIME)
public @interface Embeddable {
}
