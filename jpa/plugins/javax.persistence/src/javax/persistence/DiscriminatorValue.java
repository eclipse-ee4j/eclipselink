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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Is used to specify the value of the discriminator column for 
 * entities of the given type. The <code>DiscriminatorValue</code> 
 * annotation can only be specified on a concrete entity 
 * class. If the <code>DiscriminatorValue</code> annotation is not 
 * specified and a discriminator column is used, a provider-specific 
 * function will be used to generate a value representing the 
 * entity type.  If the {@link DiscriminatorType} is {@link 
 * DiscriminatorType#STRING STRING}, the discriminator value 
 * default is the entity name. 
 *
 * <p> The inheritance strategy and the discriminator column 
 * are only specified in the root of an entity class hierarchy 
 * or subhierarchy in which a different inheritance strategy is 
 * applied. The discriminator value, if not defaulted, should be 
 * specified for each entity class in the hierarchy.
 *
 * <pre>
 *
 *    Example:
 *
 *    &#064;Entity
 *    &#064;Table(name="CUST")
 *    &#064;Inheritance(strategy=SINGLE_TABLE)
 *    &#064;DiscriminatorColumn(name="DISC", discriminatorType=STRING,length=20)
 *    &#064;DiscriminatorValue("CUSTOMER")
 *    public class Customer { ... }
 *
 *    &#064;Entity
 *    &#064;DiscriminatorValue("VCUSTOMER")
 *    public class ValuedCustomer extends Customer { ... }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE}) 
@Retention(RUNTIME)

public @interface DiscriminatorValue {

    /**
     * (Optional) The value that indicates that the
     * row is an entity of the annotated entity type.
     *
     * <p> If the <code>DiscriminatorValue</code> annotation is not 
     * specified and a discriminator column is used, a provider-specific 
     * function will be used to generate a value representing the 
     * entity type.  If the DiscriminatorType is {@link 
     * DiscriminatorType#STRING STRING}, the discriminator value 
     * default is the entity name. 
     */
    String value();
}
