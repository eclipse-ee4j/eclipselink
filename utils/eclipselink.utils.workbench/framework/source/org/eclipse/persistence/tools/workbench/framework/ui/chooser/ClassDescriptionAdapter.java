/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 * Used by the UI to extract the package names, class names,
 * and additional information from arbitrary, user-supplied "class descriptions".
 * This can be used with the "class descriptions" returned by a
 * ClassDescriptionRepository.
 * @see ClassDescriptionRepository
 */
public interface ClassDescriptionAdapter {

	/**
	 * Return the fully-qualified class name for the specified "class description"
	 * (e.g. the class name for java.lang.Object could be "java.lang.Object").
	 */
	String className(Object classDescription);

	/**
	 * Return the package name for the specified "class description"
	 * (e.g. the package name for java.lang.Object could be "java.lang").
	 */
	String packageName(Object classDescription);

	/**
	 * Return the short class name for the specified "class description";
	 * i.e. the name of the class stripped of its package name
	 * (e.g. the short name for java.lang.Object could be "Object").
	 */
	String shortClassName(Object classDescription);

	/**
	 * Return "additional information" for the specified "class description".
	 * (e.g. the additional information for java.lang.Object might indicate
	 * which jar it came from: "rt.jar").
	 * If there is no "additional information", return null, since an empty string
	 * is valid additional information.
	 */
	String additionalInfo(Object classDescription);

}
