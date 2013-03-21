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
 *     tware - March 13, 2008 - 1.0M6 - JavaDoc rewrite
 ******************************************************************************/  
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
 *  - serialized: Will use a SerializedObjectConverter 
 *  on the associated mapping. When using a SerializedObjectConverter the database representation is a
 *  binary field holding a serialized version of the object and the object-model representation is a the
 *  actual object
 *  - class-instance: Will use an ClassInstanceConverter
 *  on the associated mapping.  When using a ClassInstanceConverter the database representation is a 
 *  String representing the Class name and the object-model representation is an instance 
 *  of that class built with a no-args constructor
 *  - none - Will place no converter on the associated mapping. This can be used to override a situation where either 
 *  another converter is defaulted or another converter is set.
 *  
 *  When these reserved converters are not used, it is necessary to define a converter to use using the 
 *  @Converter annotation.
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
}
