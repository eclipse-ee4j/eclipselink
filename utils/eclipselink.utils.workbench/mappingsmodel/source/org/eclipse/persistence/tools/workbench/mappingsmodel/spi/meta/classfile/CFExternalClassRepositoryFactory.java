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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile;

import java.io.File;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory;


/**
 * Not much to say here: This is a factory for generating instances of
 * the "classfile" implementation of ExternalClassRepository, which uses a
 * ClassFile for generating ExternalClasses etc.
 */
public final class CFExternalClassRepositoryFactory
	implements ExternalClassRepositoryFactory
{

	/** the singleton */
	private static ExternalClassRepositoryFactory INSTANCE;


	/**
	 * Singleton support.
	 */
	public static synchronized ExternalClassRepositoryFactory instance() {
		if (INSTANCE == null) {
			INSTANCE = new CFExternalClassRepositoryFactory();
		}
		return INSTANCE;
	}

	/**
	 * Private constructor - use the singleton.
	 */
	private CFExternalClassRepositoryFactory() {
		super();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory#buildExternalClassRepository(java.io.File[])
	 */
	public ExternalClassRepository buildClassRepository(File[] classpath) {
		return new CFExternalClassRepository(classpath);
	}

}
