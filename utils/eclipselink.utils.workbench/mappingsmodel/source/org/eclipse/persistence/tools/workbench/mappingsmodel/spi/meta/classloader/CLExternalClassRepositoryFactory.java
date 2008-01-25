/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classloader;

import java.io.File;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory;


/**
 * Not much to say here: This is a factory for generating instances of
 * the "classloader" implementation of ExternalClassRepository, which uses a
 * URLClassLoader for generating ExternalClasses etc.
 */
public final class CLExternalClassRepositoryFactory
	implements ExternalClassRepositoryFactory
{

	/** the singleton */
	private static ExternalClassRepositoryFactory INSTANCE;		// pseudo-final

	
	/**
	 * Singleton support.
	 */
	public static synchronized ExternalClassRepositoryFactory instance() {
		if (INSTANCE == null) {
			INSTANCE = new CLExternalClassRepositoryFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Private constructor - use the singleton.
	 */
	private CLExternalClassRepositoryFactory() {
		super();
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory#buildExternalClassRepository(java.io.File[])
	 */
	public ExternalClassRepository buildClassRepository(File[] classpath) {
		return new URLCLExternalClassRepository(classpath);
	}

}
