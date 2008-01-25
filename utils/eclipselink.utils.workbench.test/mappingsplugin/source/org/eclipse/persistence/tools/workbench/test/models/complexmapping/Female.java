/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.complexmapping;

/**
 * This type was created in VisualAge.
 */
public class Female extends Gender {
	public static Female female = new Female();

/**
 * Female constructor comment.
 */
private Female() {
	super();
}
	@Override
	public String printGender() {
		return "Female";
	}
}
