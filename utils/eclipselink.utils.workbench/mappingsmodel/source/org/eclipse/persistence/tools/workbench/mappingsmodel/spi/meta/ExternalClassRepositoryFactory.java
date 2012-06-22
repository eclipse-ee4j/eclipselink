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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Simple factory interface for building external
 * class repositories.
 */
public interface ExternalClassRepositoryFactory {

	/**
	 * Builds and returns an ExternalClassRepository for
	 * the project classpath made up of the specified files.
	 * The files in the classpath will be fully-qualified, with
	 * any relative paths resolved off of the project's save
	 * directory. The classpath may be used or ignored, as
	 * appropriate.
	 */
	ExternalClassRepository buildClassRepository(java.io.File[] classpath);

}
