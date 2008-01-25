/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader;

import java.lang.reflect.Constructor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor;


/**
 * Wrap a java.lang.reflect.Constructor.
 */
final class CLExternalConstructor
	extends CLExternalMember
	implements ExternalConstructor
{

	/**
	 * Constructor.
	 */
	CLExternalConstructor(Constructor constructor, CLExternalClass declaringExternalClass) {
		super(constructor, declaringExternalClass);
	}


	// ********** ExternalConstructor implementation **********
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor#getExceptionTypes()
	 */
	public ExternalClassDescription[] getExceptionTypes() {
		return this.buildClassDescriptionArray(this.getConstructor().getExceptionTypes());
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor#getParameterTypes()
	 */
	public ExternalClassDescription[] getParameterTypes() {
		return this.buildClassDescriptionArray(this.getConstructor().getParameterTypes());
	}


	// ********** internal methods **********
	
	/**
	 * Convenience method.
	 */
	private Constructor getConstructor() {
		return (Constructor) this.member;
	}
	
}
