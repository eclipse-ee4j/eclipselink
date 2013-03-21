/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * A ReturnInsert annotation allows for INSERT operations to return values back 
 * into the object being written. This allows for table default values, trigger 
 * or stored procedures computed values to be set back into the object.
 * 
 * A ReturnInsert annotation can only be specified on a Basic mapping. 
 * 
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ReturnInsert {
    
    /**
     * Set return only to true if you want the mapping field to be excluded 
     * from the INSERT clause during SQL generation.
     */
    boolean returnOnly() default false;
}
