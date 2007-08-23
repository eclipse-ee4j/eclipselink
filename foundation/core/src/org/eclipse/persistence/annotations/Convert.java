/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
 * The Convert annotation specifies that a named converter should be used with 
 * the corresponding mapped attribute. The Convert annotation has the following 
 * reserved names:
 *  - serialized: Will use an org.eclipse.persistence.mappings.converters.SerializedObjectConverter 
 *  on the associated mapping. 
 *  - none - Will place no converter on the associated mapping. 
 * 
 * @see org.eclipse.persistence.annotations.Converter
 * @see org.eclipse.persistence.annotations.ObjectTypeConverter
 * @see org.eclipse.persistence.annotations.TypeConverter
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
