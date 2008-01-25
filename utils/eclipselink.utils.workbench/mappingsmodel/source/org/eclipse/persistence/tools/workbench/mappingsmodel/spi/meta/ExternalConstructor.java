/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Interface defining Java metadata required by the
 * TopLink Mapping Workbench.
 * 
 * @see java.lang.reflect.Constructor
 */
public interface ExternalConstructor extends ExternalMember {

	/**
	 * Returns an array of ExternalClassDescription objects that represent
	 * the types of of exceptions declared to be thrown by the
	 * underlying constructor represented by this
	 * ExternalConstructor object.
	 * 
	 * @see java.lang.reflect.Constructor#getExceptionTypes()
	 */
	ExternalClassDescription[] getExceptionTypes();

	/**
	 * Returns an array of ExternalClassDescription objects that represent
	 * the formal parameter types, in declaration order, of the
	 * constructor represented by this ExternalConstructor object.
	 * 
	 * @see java.lang.reflect.Constructor#getParameterTypes()
	 */
	ExternalClassDescription[] getParameterTypes();

}
