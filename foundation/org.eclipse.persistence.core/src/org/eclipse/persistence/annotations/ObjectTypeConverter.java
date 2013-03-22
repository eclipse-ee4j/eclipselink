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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * The ObjectTypeConverter annotation is used to specify a TopLink
 * org.eclipse.persistence.mappings.converters.ObjectTypeConverter that converts a fixed 
 * number of database data value(s) to Java object value(s) during the reading 
 * and writing of a mapped attribute
 * 
 * An ObjectTypeConverter must be be uniquely identified by name and can be 
 * defined at the class, field and property level and can be specified within 
 * an Entity, MappedSuperclass and Embeddable class.
 * 
 * The usage of an ObjectTypeConverter is specified via the Convert annotation 
 * and is supported on a Basic, BasicMap or BasicCollection mapping.
 * 
 * @see org.eclipse.persistence.annotations.Convert
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0 
 */ 
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface ObjectTypeConverter {
    /**
     * (Required) Name this converter. The name should be unique across the 
     * whole persistence unit.
     */
    String name();

    /**
     * (Optional) Specify the type stored on the database. The default is 
     * inferred from the type of the persistence field or property.
     */
    Class dataType() default void.class;

    /**
     * (Optional) Specify the type stored on the entity. The default is inferred 
     * from the type of the persistent field or property.
     */
    Class objectType() default void.class;

    /**
     * (Required) Specify the conversion values to be used with the object 
     * converter.
     */
    ConversionValue[] conversionValues();
    
    /**
     * (Optional) Specify a default object value. Used for legacy data if the 
     * data value is missing.
     */
    String defaultObjectValue() default "";
}
