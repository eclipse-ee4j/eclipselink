/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsio.legacy;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import deprecated.sdk.SDKDataStoreException;
import deprecated.xml.XMLLogin;

/**
 * This is just a cut-and-paste of the original XMLFileLogin class.
 * This is only used for backward compatibility reading of 3.5-4.5 projects.
 */
class MWXMLFileLogin45 extends XMLLogin {
	/** The name of the base directory for the tree of XML subdirectories/files. */
	private String baseDirectoryName;

	/** The extension to be used for the XML files. */
	private String fileNameExtension;

	/** This tells the accessor to create directories if they are missing. */
	private boolean createsDirectoriesAsNeeded;

	/**
	 * Default constructor.
	 */
	MWXMLFileLogin45() {
		super();
	}
	/**
	 * Constructor. Specify the name of the directory holding
	 * all the subdirectories with the XML documents.
	 */
	MWXMLFileLogin45(String baseDirectoryName) {
		this();
		this.initialize(baseDirectoryName);
	}
	/**
	 * Constructor.
	 */
	MWXMLFileLogin45(DatabasePlatform platform) {
		super(platform);
	}
	/**
	 * Build and return an appropriate Accessor.
	 */
	private MWXMLFileAccessor45 buildAccessorInternal() {
		try {
			return (MWXMLFileAccessor45) this.getAccessorClass().newInstance();
		} catch (InstantiationException ie) {
			throw SDKDataStoreException.instantiationExceptionWhenInstantiatingAccessor(ie, this.getAccessorClass());
		} catch (IllegalAccessException iae) {
			throw SDKDataStoreException.illegalAccessExceptionWhenInstantiatingAccessor(iae, this.getAccessorClass());
		}
	}
	/**
	 * Build and return an appropriate Accessor.
	 */
	public Accessor buildAccessor() {
		MWXMLFileAccessor45 accessor = this.buildAccessorInternal();
		accessor.setBaseDirectoryName(this.getBaseDirectoryName());
		accessor.setFileExtension(this.getFileNameExtension());
		accessor.setCreatesDirectoriesAsNeeded(this.createsDirectoriesAsNeeded());
		return accessor;
	}
	/**
	 * Set whether directories will be created
	 * as needed, if they do not exist already.
	 */
	void createDirectoriesAsNeeded() {
		this.setCreatesDirectoriesAsNeeded(true);
	}
	/**
	 * Return whether directories will be created
	 * as needed, if they do not exist already.
	 */
	boolean createsDirectoriesAsNeeded() {
		return createsDirectoriesAsNeeded;
	}
	/**
	 * Set whether directories will be created
	 * as needed, if they do not exist already.
	 */
	void dontCreateDirectoriesAsNeeded() {
		this.setCreatesDirectoriesAsNeeded(false);
	}
	/**
	 * Return the name of the base directory
	 * for the tree of XML subdirectories/files.
	 */
	String getBaseDirectoryName() {
		return baseDirectoryName;
	}
	/**
	 * Return the name of the temporary directory.
	 */
	protected String tempDirectoryName() {
		return Helper.tempDirectory();
	}
	/**
	 * Return the default Accessor Class.
	 */
	protected Class getDefaultAccessorClass() {
		return ClassConstants.XMLFileAccessor_Class;
	}
	/**
	 * Return the default standard file extension that will be
	 * appended to the XML file names.
	 */
	protected String getDefaultFileNameExtension() {
		return ".xml";
	}
	/**
	 * Return the extension to be used for the XML files.
	 */
	String getFileNameExtension() {
		return fileNameExtension;
	}
	/**
	 * Initialize the login.
	 */
	protected void initialize() {
		super.initialize();
		baseDirectoryName = this.tempDirectoryName();
		fileNameExtension = this.getDefaultFileNameExtension();
		createsDirectoriesAsNeeded = false;
	}
	/**
	 * Initialize the login.
	 */
	protected void initialize(String baseDirectoryName) {
		if (baseDirectoryName != null ) {
			this.setBaseDirectoryName(baseDirectoryName);
		}
	}
	/**
	 * Set the name of the base directory
	 * for the tree of XML subdirectories/files.
	 */
	void setBaseDirectoryName(String baseDirectoryName) {
		this.baseDirectoryName = baseDirectoryName;
	}
	/**
	 * Set whether directories will be created
	 * as needed, if they do not exist already.
	 */
	void setCreatesDirectoriesAsNeeded(boolean createsDirectoriesAsNeeded) {
		this.createsDirectoriesAsNeeded = createsDirectoriesAsNeeded;
	}
	/**
	 * Set the extension to be used for the XML files.
	 */
	void setFileNameExtension(String fileNameExtension) {
		this.fileNameExtension = fileNameExtension;
	}
}
