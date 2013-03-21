/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.GeneratedValue;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a primary key generator that may be 
 * referenced by name when a generator element is specified for 
 * the {@link GeneratedValue} annotation. A UUID generator 
 * may be specified on the entity class or on the primary key 
 * field or property. The scope of the generator name is global 
 * to the persistence unit (across all generator types).
 *
 * <pre>
 *    Example 1:
 *    
 *    &#064;Entity public class Employee {
 *        ...
 *        &#064;UuidGenerator(name="uuid")
 *        &#064;Id
 *        &#064;GeneratedValue(generator="uuid")
 *        int id;
 *        ...
 *    }
 * </pre>
 *
 * @see javax.persistence.GeneratedValue
 * @author James Sutherland
 * @since EclipseLink 2.4
 */ 
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface UuidGenerator {
    /**
     * The name of the UUID generator, names must be unique for the persistence unit.
     */
    String name();
    
}
