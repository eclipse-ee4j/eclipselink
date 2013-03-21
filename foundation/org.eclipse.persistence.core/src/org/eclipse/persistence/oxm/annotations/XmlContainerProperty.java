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
* mmacivor - January 09, 2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates a transient property on the target object of this
 * field that refers back to the owning object. 
 * @author Matt
 * @deprecated  As of EclipseLink 2.0, replaced by XmlInverseReference.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface XmlContainerProperty {
	/**
	 * The name of the back pointer attribute on the target class
	 */
	String value();
	/**
	 * The get method to be invoked when accessing the back pointer
	 */
	String getMethodName() default "";
	/**
	 * The set method to be used when setting the back pointer
	 */	
	String setMethodName() default "";
}
