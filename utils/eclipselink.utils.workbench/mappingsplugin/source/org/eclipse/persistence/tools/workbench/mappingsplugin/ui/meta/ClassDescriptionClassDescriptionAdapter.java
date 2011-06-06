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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta;

import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionAdapter;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultClassDescriptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription;


/**
 * This adapter assumes that the user-supplied "class descriptions"
 * are a collection of ClassDescription objects, supplied by a "combined"
 * class repository.
 */
public class ClassDescriptionClassDescriptionAdapter
	extends DefaultClassDescriptionAdapter
{
	/**
	 * provide a Singleton
	 */
	private static ClassDescriptionAdapter INSTANCE;

	public static synchronized ClassDescriptionAdapter instance() {
		if (INSTANCE == null) {
			INSTANCE = new ClassDescriptionClassDescriptionAdapter();
		}
		return INSTANCE;
	}

	/**
	 * Assume the "class description" is a ClassDescription object.
	 */
	public String className(Object classDescription) {
		return ((ClassDescription) classDescription).getName();
	}

	/**
	 * Assume the "class description" is a ClassDescription object.
	 */
	public String additionalInfo(Object classDescription) {
		return ((ClassDescription) classDescription).getAdditionalInfo();
	}

}
