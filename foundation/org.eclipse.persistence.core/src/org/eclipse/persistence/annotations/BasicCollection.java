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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.persistence.Column;
import javax.persistence.FetchType;

import static javax.persistence.FetchType.LAZY;
 
/** 
 * A BasicCollection is used to map an 
 * org.eclipse.persistence.mappings.DirectCollectionMapping, which stores a collection 
 * of simple types (String, Number, Date, etc.). It is used in conjunction with 
 * a CollectionTable which stores the value and a foreign key to the source 
 * object.
 * 
 * @see org.eclipse.persistence.annotations.CollectionTable
 * 
 * Converters may be used if the desired object type and the data type do not 
 * match.
 * 
 * @see org.eclipse.persistence.annotations.Convert
 * @see org.eclipse.persistence.annotations.Converter
 * @see org.eclipse.persistence.annotations.ObjectTypeConverter
 * @see org.eclipse.persistence.annotations.TypeConverter
 * 
 * A BasicCollection can be specified on within an Entity, MappedSuperclass 
 * and Embeddable class.
 * 
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 * 
 * @deprecated
 * @see javax.persistence.ElementCollection
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Deprecated
public @interface BasicCollection {
    /**
     * (Optional) Defines whether the value of the field or property should
     * be lazily loaded or must be eagerly fetched. The EAGER strategy is a 
     * requirement on the persistence provider runtime that the value must be 
     * eagerly fetched. The LAZY strategy is a hint to the persistence provider 
     * runtime. If not specified, defaults to LAZY.
     */
    FetchType fetch() default LAZY; 
 
    /**
     * (Optional) The name of the value column that holds the direct collection 
     * data. Defaults to the property or field name.
     */
    Column valueColumn() default @Column;
}
