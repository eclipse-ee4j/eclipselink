/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.lang.reflect.Field;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;


/**
 * Wrap a java.lang.reflect.Field.
 */
final class CLExternalField
	extends CLExternalMember
	implements ExternalField
{

	/**
	 * Constructor.
	 */
	CLExternalField(Field field, CLExternalClass declaringExternalClass) {
		super(field, declaringExternalClass);
	}


	// ********** ExternalField implementation **********
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField#getType()
	 */
	public ExternalClassDescription getType() {
		return this.classDescriptionFor(this.getField().getType());
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMember#isSynthetic()
	 * This is just a best guess at which fields are synthetic.
	 */
	public boolean isSynthetic() {
		if (super.isSynthetic()) {
			return true;
		}
		// it appears that compiler-generated Fields hold only Classes...
		if ( ! this.getType().getName().equals(java.lang.Class.class.getName())) {
			return false;
		}
		if (this.getName().startsWith(this.getDeclaringClass().getName() + ".class$")) {
			return true;
		}
		return false;
	}


	// ********** internal methods **********
	
	/**
	 * Convenience method.
	 */
	private Field getField() {
		return (Field) this.member;
	}
	
}
