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
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Is used to specify the map key for associations of type 
 * {@link java.util.Map java.util.Map} when the map key is itself the primary
 * key or a persistent field or property of the entity that is
 * the value of the map.
 * 
 * <p> If a persistent field or property other than the primary 
 * key is used as a map key then it is expected to have a 
 * uniqueness constraint associated with it.
 *
 * <p> The {@link MapKeyClass} annotation is not used when
 * <code>MapKey</code> is specified and vice versa.
 *
 * <pre>
 *
 *    Example 1:
 *
 *    &#064;Entity
 *    public class Department {
 *        ...
 *        &#064;OneToMany(mappedBy="department")
 *        &#064;MapKey(name="empId")
 *        public Map&#060;Integer, Employee&#062; getEmployees() {... }
 *        ...
 *    }
 *
 *    &#064;Entity
 *    public class Employee {
 *        ...
 *        &#064;Id Integer getEmpid() { ... }
 *        &#064;ManyToOne
 *        &#064;JoinColumn(name="dept_id")
 *        public Department getDepartment() { ... }
 *        ...
 *    }
 *
 *    Example 2:
 *
 *    &#064;Entity
 *        public class Department {
 *        ...
 *        &#064;OneToMany(mappedBy="department")
 *        &#064;MapKey(name="empPK")
 *        public Map&#060;EmployeePK, Employee&#062; getEmployees() {... }
 *        ...
 *    }
 *
 *    &#064;Entity
 *        public class Employee {
 *        &#064;EmbeddedId public EmployeePK getEmpPK() { ... }
 *        ...
 *        &#064;ManyToOne
 *        &#064;JoinColumn(name="dept_id")
 *        public Department getDepartment() { ... }
 *        ...
 *    }
 *
 *    &#064;Embeddable
 *    public class EmployeePK {
 *        String name;
 *        Date bday;
 *    }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface MapKey {

    /**
     * (Optional) The name of the persistent field or property of the 
     * associated entity that is used as the map key. 
     * <p> Default: If the 
     * <code>name</code> element is not specified, the primary key of the 
     * associated entity is used as the map key. If the 
     * primary key is a composite primary key and is mapped 
     * as <code>IdClass</code>, an instance of the primary key 
     * class is used as the key.
     */
    String name() default "";
}
