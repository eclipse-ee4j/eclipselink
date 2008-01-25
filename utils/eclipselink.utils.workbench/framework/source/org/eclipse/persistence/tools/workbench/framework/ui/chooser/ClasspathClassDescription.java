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

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * Straightforward description of a class found on a classpath.
 */
public class ClasspathClassDescription {
	private String className;
	private String classpathEntry;

	ClasspathClassDescription(String className, String classpathEntry) {
		super();
		this.className = className;
		this.classpathEntry = classpathEntry;
	}

	public String getClassName() {
		return this.className;
	}

	public String getClasspathEntry() {
		return this.classpathEntry;
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.className + " - " + this.classpathEntry);
	}


	// ********** adapter **********

	public static class Adapter extends DefaultClassDescriptionAdapter {
		public String className(Object classDescription) {
			return ((ClasspathClassDescription) classDescription).getClassName();
		}
		public String additionalInfo(Object classDescription) {
			return ((ClasspathClassDescription) classDescription).getClasspathEntry();
		}
	}

}
