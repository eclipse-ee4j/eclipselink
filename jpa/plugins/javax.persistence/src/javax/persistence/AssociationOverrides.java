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
 * This annotation is used to override mappings of multiple 
 * many-to-one or one-to-one relationship properties or fields.
 *
 * <pre>
 *    
 *    Example:
 *    &#064;MappedSuperclass
 *    public class Employee {
 *    
 *        &#064;Id protected Integer id;
 *        &#064;Version protected Integer version;
 *        &#064;ManyToOne protected Address address;
 *        &#064;OneToOne protected Locker locker;
 *    
 *        public Integer getId() { ... }
 *        public void setId(Integer id) { ... }
 *        public Address getAddress() { ... }
 *        public void setAddress(Address address) { ... }
 *        public Locker getLocker() { ... }
 *        public void setLocker(Locker locker) { ... }
 *    
 *    }
 *    
 *    &#064;Entity
 *    &#064;AssociationOverrides({
 *    
 *    &#064;AssociationOverride(name="address", 
 *            joinColumns=&#064;JoinColumn("ADDR_ID")),
 *        &#064;AttributeOverride(name="locker", 
 *            joinColumns=&#064;JoinColumn("LCKR_ID"))})
 *    public PartTimeEmployee { ... }
 * </pre>
 *
 *@see AssociationOverride
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE, METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface AssociationOverrides {

    /** Mapping overrides of relationship properties or fields */
    AssociationOverride[] value();
}
