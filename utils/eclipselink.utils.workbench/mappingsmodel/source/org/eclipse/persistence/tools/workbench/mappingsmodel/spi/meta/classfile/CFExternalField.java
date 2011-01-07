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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.utility.classfile.Field;


/**
 * Wrap a ClassFileField.
 */
final class CFExternalField
	extends CFExternalMember
	implements ExternalField
{

	/**
	 * Constructor.
	 */
	CFExternalField(Field field, CFExternalClass declaringExternalClass) {
		super(field, declaringExternalClass);
	}


	// ********** ExternalField implementation **********
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField#getType()
	 */
	public ExternalClassDescription getType() {
		return this.classDescriptionNamed(this.getField().javaTypeName());
	}
	

	// ********** internal methods **********
	
	/**
	 * Convenience method.
	 */
	private Field getField() {
		return (Field) this.member;
	}
	
}
