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
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi;

import java.lang.reflect.Method;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabaseFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.jdbc.JDBCExternalDatabaseFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile.CFExternalClassRepositoryFactory;


/**
 * 
 */
public class DefaultSPIManager implements SPIManager {

	/**
	 * preferences allow the user to override the default SPI implementations
	 */
	private Preferences preferences;
	public static final String SPI_PREFERENCES_NODE = "spi";
	public static final String DEFAULTS_PREFERENCES_NODE = "defaults";
	public static final String PROJECTS_PREFERENCES_NODE = "projects";

	/**
	 * the project's name is used to look up any project-specific settings
	 * in the preferences
	 */
	private String projectName;

	/**
	 * class repository
	 */
	private ExternalClassRepositoryFactory externalClassRepositoryFactory;
	public static final String EXTERNAL_CLASS_REPOSITORY_FACTORY_CLASS_NAME_PREFERENCE = "external class repository factory class";
		public static final String EXTERNAL_CLASS_REPOSITORY_FACTORY_CLASS_NAME_PREFERENCE_DEFAULT = CFExternalClassRepositoryFactory.class.getName();
	public static final String EXTERNAL_CLASS_REPOSITORY_FACTORY_STATIC_METHOD_NAME_PREFERENCE = "external class repository factory static method";
		public static final String EXTERNAL_CLASS_REPOSITORY_FACTORY_STATIC_METHOD_NAME_PREFERENCE_DEFAULT = "instance";

	/**
	 * database
	 */
	private ExternalDatabaseFactory externalDatabaseFactory;
	public static final String EXTERNAL_DATABASE_FACTORY_CLASS_NAME_PREFERENCE = "external database factory class";
		public static final String EXTERNAL_DATABASE_FACTORY_CLASS_NAME_PREFERENCE_DEFAULT = JDBCExternalDatabaseFactory.class.getName();
	public static final String EXTERNAL_DATABASE_FACTORY_STATIC_METHOD_NAME_PREFERENCE = "external database factory static method";
		public static final String EXTERNAL_DATABASE_FACTORY_STATIC_METHOD_NAME_PREFERENCE_DEFAULT = "instance";


	/**
	 * Construct an SPI manager for the specified project. The various
	 * factories can be configured via the specified preferences node.
	 */
	public DefaultSPIManager(Preferences preferences, String projectName) {
		super();
		this.preferences = preferences.node(SPI_PREFERENCES_NODE);
		this.projectName = projectName;
	}


	// ********** class repository **********

	/**
	 * @see SPIManager#getExternalClassRepositoryFactory()
	 */
	public ExternalClassRepositoryFactory getExternalClassRepositoryFactory() {
		if (this.externalClassRepositoryFactory == null) {
			this.externalClassRepositoryFactory = this.buildExternalClassRepositoryFactory();
		}
		return this.externalClassRepositoryFactory;
	}

	private ExternalClassRepositoryFactory buildExternalClassRepositoryFactory() {
		return (ExternalClassRepositoryFactory) this.buildFactory(this.externalClassRepositoryFactoryClassName(), this.externalClassRepositoryFactoryStaticMethodName());
	}

	private String externalClassRepositoryFactoryClassName() {
		return this.value(EXTERNAL_CLASS_REPOSITORY_FACTORY_CLASS_NAME_PREFERENCE, EXTERNAL_CLASS_REPOSITORY_FACTORY_CLASS_NAME_PREFERENCE_DEFAULT);
	}

	private String externalClassRepositoryFactoryStaticMethodName() {
		return this.value(EXTERNAL_CLASS_REPOSITORY_FACTORY_STATIC_METHOD_NAME_PREFERENCE, EXTERNAL_CLASS_REPOSITORY_FACTORY_STATIC_METHOD_NAME_PREFERENCE_DEFAULT);
	}


	// ********** database **********

	/**
	 * @see SPIManager#getExternalDatabaseFactory()
	 */
	public ExternalDatabaseFactory getExternalDatabaseFactory() {
		if (this.externalDatabaseFactory == null) {
			this.externalDatabaseFactory = this.buildExternalDatabaseFactory();
		}
		return this.externalDatabaseFactory;
	}

	private ExternalDatabaseFactory buildExternalDatabaseFactory() {
		return (ExternalDatabaseFactory) this.buildFactory(this.externalDatabaseFactoryClassName(), this.externalDatabaseFactoryStaticMethodName());
	}

	private String externalDatabaseFactoryClassName() {
		return this.value(EXTERNAL_DATABASE_FACTORY_CLASS_NAME_PREFERENCE, EXTERNAL_DATABASE_FACTORY_CLASS_NAME_PREFERENCE_DEFAULT);
	}

	private String externalDatabaseFactoryStaticMethodName() {
		return this.value(EXTERNAL_DATABASE_FACTORY_STATIC_METHOD_NAME_PREFERENCE, EXTERNAL_DATABASE_FACTORY_STATIC_METHOD_NAME_PREFERENCE_DEFAULT);
	}


	// ********** common code **********

	/**
	 * return the user-specified project-specific setting;
	 * absent that, return the user-specified default setting;
	 * absent that, return the system-specified default setting
	 */
	private String value(String key, String defaultValue) {
		String value = defaultValue;
		// spi/defaults/foo
		value = this.preferences.node(DEFAULTS_PREFERENCES_NODE).get(key, value);
		try {
			// spi/projects/Project Name/foo
			if (this.preferences.node(PROJECTS_PREFERENCES_NODE).nodeExists(this.projectName)) {
				value = this.preferences.node(PROJECTS_PREFERENCES_NODE).node(this.projectName).get(key, value);
			}
		} catch (BackingStoreException ex) {
			throw new RuntimeException(ex);
		}
		return value;
	}

	private Object buildFactory(String factoryClassName, String factoryStaticMethodName) {
		try {
			// first load the factory class
			// (for now, the factory class must be loadable by this class's classloader;
			// but we could configure a URLClassLoader with a classpath and use that...)
			Class factoryClass = Class.forName(factoryClassName);
			// then get the method
			Method factoryStaticMethod = factoryClass.getMethod(factoryStaticMethodName, new Class[0]);
			// then invoke the method and return the result
			return factoryStaticMethod.invoke(null, new Object[0]);
		} catch (Throwable t) {
			// if any of the above code fails, throw a runtime exception
			throw new RuntimeException(t);
		}
	}

}
