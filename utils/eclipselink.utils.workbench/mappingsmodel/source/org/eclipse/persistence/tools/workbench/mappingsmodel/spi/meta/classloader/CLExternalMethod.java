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

import java.lang.reflect.Method;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;


/**
 * Wrap a java.lang.reflect.Method.
 */
final class CLExternalMethod
	extends CLExternalMember
	implements ExternalMethod
{

	/**
	 * Constructor.
	 */
	CLExternalMethod(Method method, CLExternalClass declaringExternalClass) {
		super(method, declaringExternalClass);
	}


	// ********** ExternalMethod implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod#getExceptionTypes()
	 */
	public ExternalClassDescription[] getExceptionTypes() {
		return this.buildClassDescriptionArray(this.getMethod().getExceptionTypes());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod#getParameterTypes()
	 */
	public ExternalClassDescription[] getParameterTypes() {
		return this.buildClassDescriptionArray(this.getMethod().getParameterTypes());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod#getReturnType()
	 */
	public ExternalClassDescription getReturnType() {
		return this.classDescriptionFor(this.getMethod().getReturnType());
	}


	// ********** internal methods **********

	/**
	 * Convenience method.
	 */
	private Method getMethod() {
		return (Method) this.member;
	}

}
