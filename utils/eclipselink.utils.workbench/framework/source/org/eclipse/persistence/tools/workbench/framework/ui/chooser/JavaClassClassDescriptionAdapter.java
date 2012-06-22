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
	
