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
package org.eclipse.persistence.tools.workbench.mappingsmodel.resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class FileResourceSpecification 
	extends ResourceSpecification 
{
	private static String KEY = "FILE_RESOURCE";
	
	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(FileResourceSpecification.class);
		
		descriptor.getInheritancePolicy().setParentClass(ResourceSpecification.class);

		return descriptor;
	}
	
	
	// **************** Constructors ******************************************
	
	/** For TopLink only */
	private FileResourceSpecification() {
		super();
	}
	
	public FileResourceSpecification(MWModel parent, String filePath) {
		super(parent, filePath);
	}
	
	
	// **************** ResourceSpecification contract ************************
	
	public String getSourceKey() {
		return KEY;
	}
	
	protected URL resourceUrl() 
		throws ResourceException 
	{
		if (this.location == null || this.location == "") {
			throw ResourceException.unspecifiedResourceException(null);
		}

		File absoluteFile = this.absoluteFile();
		if ( ! absoluteFile.exists()) {
			throw ResourceException.nonexistentResourceException(null);
		}
		
		try {
			return absoluteFile.toURL();
		}
		catch (MalformedURLException mue) {
			throw ResourceException.incorrectlySpecifiedResourceException(mue);
		}
	}
	
	
	// **************** Internal **********************************************
	
	private File absoluteFile() {
		File file = new File(this.location);
		if (file.isAbsolute()) {
			return file;
		}
		return new File(this.getProject().getSaveDirectory(), this.location);
	}


	// **************** TopLink **********************************************

	/**
	 * convert to platform-independent representation
	 */
	protected String getLocationForTopLink2() {
		return this.location.replace('\\', '/');
	}
	
	/**
	 * convert to platform-specific representation
	 */
	protected void setLocationForTopLink2(String fileName) {
		this.location = new File(fileName).getPath();
	}

}
