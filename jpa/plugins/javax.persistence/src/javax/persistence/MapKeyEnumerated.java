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
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.EnumType.ORDINAL;

/**
 * Specifies that a persistent map key should be persisted as a enumerated type. 
 * 
 * The <code>MapKeyEnumerated</code> annotation can be applied to an 
 * element collection or relationship of type <code>java.util.Map</code>, in 
 * conjunction with the {@link ElementCollection}, {@link OneToMany}, or 
 * {@link ManyToMany} annotation.
 *
 * <pre>
 *   Example:
 *
 *   public enum ProjectStatus {COMPLETE, DELAYED, CANCELLED, IN_PROGRESS}
 *
 *   public enum SalaryRate {JUNIOR, SENIOR, MANAGER, EXECUTIVE}
 *
 *   &#064;Entity public class Employee {
 *       &#064;ManyToMany
 *       public Projects<ProjectStatus, Project> getProjects() {...}
 *       
 *       &#064;OneToMany
 *       &#064;Enumerated(STRING)
 *       public Map<SalaryRate, Employee> getEmployees() {...}
 *       ...
 *   }
 * </pre>
 *
 * @since Java Persistence 2.0
 */
@Target({METHOD, FIELD}) @Retention(RUNTIME)
public @interface MapKeyEnumerated {
    
    /** (Optional) The type used in mapping a map key enum type. */
    EnumType value() default ORDINAL;
}
