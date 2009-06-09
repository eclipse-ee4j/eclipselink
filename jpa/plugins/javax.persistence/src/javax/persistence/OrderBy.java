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
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation specifies the ordering of the elements of a 
 * collection valued association at the point when the association 
 * is retrieved.
 * 
 * <p> The syntax of the <code>value</code> ordering element is an 
 * <code>orderby_list</code>, as follows:
 * 
 * <pre>
 *    orderby_list::= orderby_item [,orderby_item]*
 *    orderby_item::= property_or_field_name [ASC | DESC]
 * </pre>
 * 
 * <p> If <code>ASC</code> or <code>DESC</code> is not specified, 
 * <code>ASC</code> (ascending order) is assumed.
 *
 * <p> If the ordering element is not specified, ordering by 
 * the primary key of the associated entity is assumed.
 *
 * <p> The property or field name must correspond to that of a 
 * persistent property or field of the associated class. The 
 * properties or fields used in the ordering must correspond to 
 * columns for which comparison operators are supported.
 *
 * <pre>
 *    Example:
 *    
 *    &#064;Entity public class Course {
 *     ...
 *     &#064;ManyToMany
 *     &#064;OrderBy("lastname ASC")
 *     public List<Student> getStudents() {...};
 *     ...
 *    }
 *    
 *    &#064;Entity public class Student {
 *      ...
 *      &#064;ManyToMany(mappedBy="students")
 *      &#064;OrderBy // PK is assumed
 *      public List<Course> getCourses() {...};
 *      ...
 *    }
 * </pre>
 *
 * @since Java Persistence 1.0
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface OrderBy {

    /**
    * An <code>orderby_list</code>, specified as follows:
    *
    * <pre>
    *    orderby_list::= orderby_item [,orderby_item]*
    *    orderby_item::= property_or_field_name [ASC | DESC]
    * </pre>
    *
    * <p> If <code>ASC</code> or <code>DESC</code> is not specified,
    * <code>ASC</code> (ascending order) is assumed.
    *
    * <p> If the ordering element is not specified, ordering by
    * the primary key of the associated entity is assumed.
    */
    String value() default "";
}
