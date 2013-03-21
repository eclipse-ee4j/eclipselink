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

import javax.persistence.Column;
import javax.persistence.FetchType;

import static javax.persistence.FetchType.LAZY;

/**
 * A BasicMap is used to map an org.eclipse.persistence.mappings.DirectMapMapping, which 
 * stores a collection of key-value pairs of simple types (String, Number, Date, 
 * etc.). It is used in conjunction with a CollectionTable which stores the key,
 * the value and a foreign key to the source object.
 * 
 * @see org.eclipse.persistence.annotations.CollectionTable
 * 
 * A converter may be used if the desired object type and the data type do not 
 * match. This applied to both the key and value of the map.
 * 
 * @see org.eclipse.persistence.annotations.Convert
 * @see org.eclipse.persistence.annotations.Converter
 * @see org.eclipse.persistence.annotations.ObjectTypeConverter
 * @see org.eclipse.persistence.annotations.TypeConverter
 * 
 * A BasicMap can be specified within an Entity, MappedSuperclass and Embeddable 
 * class.
 * 
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 * 
 * @deprecated
 * @see javax.persistence.ElementCollection
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface BasicMap {
    /**
     * (Optional) Defines whether the value of the field or property should
     * be lazily loaded or must be eagerly fetched. The EAGER strategy is a 
     * requirement on the persistence provider runtime that the value must be 
     * eagerly fetched. The LAZY strategy is a hint to the persistence provider 
     * runtime. If not specified, defaults to LAZY.
     */
    FetchType fetch() default LAZY;

    /**
     * (Optional) The name of the data column that holds the direct map 
     * key. If the name on the key column is "", the name will default to:
     * the name of the property or field; "_KEY".
     */
    Column keyColumn() default @Column;

    /**
     * (Optional) Specify the key converter. Default is equivalent to specifying
     * @Convert("none"), meaning no converter will be added to the direct map key.
     */
    Convert keyConverter() default @Convert;

    /**
     * (Optional) The name of the data column that holds the direct collection data.
     * Defaults to the property or field name.
     */
    Column valueColumn() default @Column;

    /**
     * (Optional) Specify the value converter. Default is equivalent to specifying 
     * @Convert("none"), meaning no converter will be added to the value column mapping.
     */
    Convert valueConverter() default @Convert;
}
