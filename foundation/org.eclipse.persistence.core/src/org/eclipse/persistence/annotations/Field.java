/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** 
 * Define a structured data type's field name for an object mapped to NoSql data.
 * This is a generic form of the @Column annotation, which is not specific to relational databases.
 * It can be use to map EIS and NoSQL data.
 * 
 * @see NoSql
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface Field {
    /**
     * (Optional) The data type's name of the field.
     */
    String name() default "";
}
