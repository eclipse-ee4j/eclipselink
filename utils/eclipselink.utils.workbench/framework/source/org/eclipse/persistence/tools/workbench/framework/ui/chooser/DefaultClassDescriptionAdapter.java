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

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * The default adapter assumes that the user-supplied "class descriptions"
 * are simply a collection of fully-qualified class names.
 * Subclasses of this adapter need only override
 * #className(Object) to return the fully-qualified class name and this
 * implementation will extract the short class name and package name
 * as required. The description defaults to null.
 */
public class DefaultClassDescriptionAdapter
	implements ClassDescriptionAdapter
{

	/**
	 * provide a Singleton
	 */
	private static ClassDescriptionAdapter INSTANCE;

	public static synchronized ClassDescriptionAdapter instance() {
		if (INSTANCE == null) {
			INSTANCE = new DefaultClassDescriptionAdapter();
		}
		return INSTANCE;
	}

	/**
	 * The default behavior assumes that the "class description"
	 * is simply the fully-qualified class name.
	 * @see ClassDescriptionAdapter#className(Object)
	 */
	public String className(Object classDescription) {
		return (String) classDescription;
	}

	/**
	 * @see ClassDescriptionAdapter#packageName(Object)
	 */
	public String packageName(Object classDescription) {
		return ClassTools.packageNameForClassNamed(this.className(classDescription));
	}

	/**
	 * @see ClassDescriptionAdapter#shortClassName(Object)
	 */
	public String shortClassName(Object classDescription) {
		return ClassTools.shortNameForClassNamed(this.className(classDescription));
	}

	/**
	 * @see ClassDescriptionAdapter#additionalInfo(Object)
	 */
	public String additionalInfo(Object classDescription) {
		return null;
	}

}
