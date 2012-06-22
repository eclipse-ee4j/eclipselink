/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader;

import java.lang.reflect.Member;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Common behavior for CLExternalField, CLExternalConstructor,
 * and CLExternalMethod.
 */
abstract class CLExternalMember
	implements ExternalMember
{

	/** The wrapped member. */
	final Member member;	// private-protected

	/** The external class that declares the member. */
	final CLExternalClass declaringClass;	// private-protected


	// ********** Constructors **********

	/**
	 * Useful constructor.
	 */
	CLExternalMember(Member member, CLExternalClass declaringClass) {	// private-protected
		super();
		this.member = member;
		this.declaringClass = declaringClass;
	}


	// ********** ExternalMember implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#getDeclaringClass()
	 */
	public ExternalClassDescription getDeclaringClass() {
		return this.classDescriptionFor(this.member.getDeclaringClass());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#getModifiers()
	 */
	public int getModifiers() {
		return this.member.getModifiers();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#getName()
	 */
	public String getName() {
		return this.member.getName();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#isSynthetic()
	 * This is just a best guess at which members are synthetic.
	 */
	public boolean isSynthetic() {
		return this.getName().startsWith("class$");
	}


	// ********** standard methods **********

	public String toString() {
		return StringTools.buildToStringFor(this, this.getName());
	}


	// ********** private-protected methods **********

	ExternalClassDescription classDescriptionFor(Class javaClass) {	// private-protected
		return this.declaringClass.classDescriptionFor(javaClass);
	}

	ExternalClassDescription[] buildClassDescriptionArray(Class[] classes) {	// private-protected
		ExternalClassDescription[] externalClassDescriptions = new ExternalClassDescription[classes.length];
		for (int i = classes.length; i-- > 0; ) {
			externalClassDescriptions[i] = this.classDescriptionFor(classes[i]);
		}
		return externalClassDescriptions;
	}

}
