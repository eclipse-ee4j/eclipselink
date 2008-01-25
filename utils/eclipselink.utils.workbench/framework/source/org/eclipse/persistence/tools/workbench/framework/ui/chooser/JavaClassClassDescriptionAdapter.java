/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;


/**
 * The Java class adapter assumes that the user-supplied "class descriptions"
 * are a collection of Java classes.
 */
public class JavaClassClassDescriptionAdapter
	extends DefaultClassDescriptionAdapter
{
	private static ClassDescriptionAdapter INSTANCE;

	public static synchronized ClassDescriptionAdapter instance() {
		if (INSTANCE == null) {
			INSTANCE = new JavaClassClassDescriptionAdapter();
		}
		return INSTANCE;
	}

	/**
	 * Assume the "class description" is a Java Class object.
	 */
	public String className(Object classDescription) {
		return ((Class) classDescription).getName();
	}

}
	
