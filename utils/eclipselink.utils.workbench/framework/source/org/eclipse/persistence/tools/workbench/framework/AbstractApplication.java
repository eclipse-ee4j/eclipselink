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
package org.eclipse.persistence.tools.workbench.framework;

import org.eclipse.persistence.tools.workbench.utility.ManifestInterrogator;

/**
 * Provide a partial implementation of the Application interface that 
 * uses a ManifestInterrogator to parse
 * most of the necessary information from the application's manifest.
 * The manifest is built and stored in a file (META_INF/MANIFEST.MF)
 * in the application's JAR during the build process.
 * 
 * Subclasses need only provide default values (by implementing the
 * ManifestInterrogator.Defaults interface)
 * for when the JAR and/or the manifest cannot be found.
 */
public abstract class AbstractApplication
	implements Application, ManifestInterrogator.Defaults
{

	/** Used to find product information from the manifest file */
	private ManifestInterrogator manifestInterrogator;


	// ********** constructors/initialization **********

	/**
	 * Default constructor.
	 */
	protected AbstractApplication() {
		super();
		this.initialize();
	}

	/**
	 * Read in the manifest and determine whether we are in development
	 * mode.
	 */
	protected void initialize() {
		this.manifestInterrogator = this.buildManifestInterrogator();
	}

	protected ManifestInterrogator buildManifestInterrogator() {
		return new ManifestInterrogator(this.getClass(), this);
	}


	// ********** Application implementation **********

	/**
	 * Concatenate the Specification Title, the Release Designation, and
	 * the Library Designation, as derived from the JAR file manifest.
	 * @see Application#getFullProductName()
	 */
	public String getFullProductName() {
		return this.manifestInterrogator.getFullProductName();
	}

	/**
	 * Return the Specification Title, as derived from the JAR file manifest.
	 * @see Application#getProductName()
	 */
	public String getProductName() {
		return this.manifestInterrogator.getProductName();
	}

	/**
	 * Return the Library Designation, as derived from the JAR file manifest.
	 * @see Application#getShortProductName()
	 */
	public String getShortProductName() {
		return this.manifestInterrogator.getShortProductName();
	}

	/**
	 * Return the Specification Version, as derived from the JAR file manifest.
	 * @see org.eclipse.persistence.tools.workbench.framework.Application#getVersionNumber()
	 */
	public String getVersionNumber() {
		return this.manifestInterrogator.getVersionNumber();
	}

	/**
	 * Concatenate the full product name and the version number.
	 * @see Application#getFullProductNameAndVersionNumber()
	 */
	public String getFullProductNameAndVersionNumber() {
		return this.getFullProductName() + " " + this.getVersionNumber();
	}

	/**
	 * Strip the build number off the end of the Implementation Version.
	 * @see Application#getBuildNumber()
	 */
	public String getBuildNumber() {
		return this.manifestInterrogator.getBuildNumber();
	}
	
	/**
	 * Return the Release Designation, as derived from the JAR file manifest.
	 */
	public String getReleaseDesignation() {
		return this.manifestInterrogator.getReleaseDesignation();
	}

	/**
	 * @see Application#isDevelopmentMode()
	 */
	public boolean isDevelopmentMode() {
		return this.manifestInterrogator.isDevelopmentMode();
	}

}
