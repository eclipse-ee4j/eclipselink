/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     tware - March 13, 2008 - 1.0M6 - JavaDoc rewrite
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The Convert annotation specifies that a named converter should be used with
 * the corresponding mapped attribute. The Convert annotation has the following
 * reserved names:
 *  <ul>
 *  <li> serialized: Will use a SerializedObjectConverter
 *  on the associated mapping. When using a SerializedObjectConverter the database representation is a
 *  binary field holding a serialized version of the object and the object-model representation is a the
 *  actual object.
 *  <li> class-instance: Will use an ClassInstanceConverter
 *  on the associated mapping.  When using a ClassInstanceConverter the database representation is a
 *  String representing the Class name and the object-model representation is an instance
 *  of that class built with a no-args constructor.<br>
 *  <li> xml: Will use an SerializedObjectConverter with the XMLSerializer
 *  on the associated mapping.  When using a XMLSerializer the database representation is a
 *  character field holding a serialized version of the object and the object-model representation is a the
 *  actual object.<br>
 *  <li> json: Will use an SerializedObjectConverter with the JSONSerializer
 *  on the associated mapping.  When using a JSONSerializer the database representation is a
 *  character field holding a serialized version of the object and the object-model representation is a the
 *  actual object.<br>
 *  <li> kryo: Will use an SerializedObjectConverter with the KryoSerializer
 *  on the associated mapping.  When using a KryoSerializer the database representation is a
 *  binary field holding a serialized version of the object and the object-model representation is a the
 *  actual object.<br>
 *  <li> none - Will place no converter on the associated mapping. This can be used to override a situation where either
 *  another converter is defaulted or another converter is set.
 *  </ul>
 *
 *  When these reserved converters are not used, it is necessary to define a converter to use using the
 *  {@literal @}Converter annotation.
 *
 * @see org.eclipse.persistence.annotations.Converter
 * @see org.eclipse.persistence.annotations.ObjectTypeConverter
 * @see org.eclipse.persistence.annotations.TypeConverter
 * @see org.eclipse.persistence.mappings.converters.SerializedObjectConverter
 * @see org.eclipse.persistence.mappings.converters.ClassInstanceConverter
 *
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Convert {
    /**
     * (Optional) The name of the converter to be used.
     */
    String value() default "none";

    /**
     * Constant name for the reserved Java serialization converter.
     * This will serialize the
     */
    public static final String SERIALIZED = "serialized";

    /**
     * Constant name for the reserved class instance converter.
     * This will store the object's class name, and create a new instance of the class on read.
     */
    public static final String CLASS_INSTANCE = "class-instance";

    /**
     * Constant name for the reserved XML converter.
     * This will use JAXB to convert the object to and from XML.
     */
    public static final String XML = "xml";

    /**
     * Constant name for the reserved JSON converter.
     * This will use EclipseLink Moxy JAXB to convert the object to and from JSON.
     */
    public static final String JSON = "json";

    /**
     * Constant name for the reserved Kryo converter.
     * This will use Kryo to convert the object to and from an optimized binary format.
     */
    public static final String KRYO = "kryo";

    /**
     * Constant name for no converter.
     */
    public static final String NONE = "none";
}
