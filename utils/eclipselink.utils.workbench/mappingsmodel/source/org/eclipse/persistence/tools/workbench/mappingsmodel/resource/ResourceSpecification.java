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
package org.eclipse.persistence.tools.workbench.mappingsmodel.resource;

import java.io.IOException;
import java.net.URL;

import org.apache.xerces.util.URI;
import org.apache.xerces.util.URI.MalformedURIException;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

public abstract class ResourceSpecification 
	extends MWModel
{	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ResourceSpecification.class);
		
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(FileResourceSpecification.class, "file");
		ip.addClassIndicator(UrlResourceSpecification.class, "url");
		ip.addClassIndicator(ClasspathResourceSpecification.class, "classpath");
		
		descriptor.addDirectMapping("location", "getLocationForTopLink", "setLocationForTopLink", "location/text()");
		
		return descriptor;
	}
	
	
	// **************** Instance variables ************************************
	
	protected String location;
	
	
	// **************** Constructors ******************************************
	
	/** For TopLink only */
	protected ResourceSpecification() {
		super();
	}
	
	protected ResourceSpecification(MWModel parent, String resourceLocation) {
		super(parent);
		this.location = resourceLocation;
	}
	
	
	// **************** API ***************************************************
	
	public String getLocation() {
		return this.location;
	}
	
	/** A unique identifier used for internationalization and such. */
	public abstract String getSourceKey();
	
	/** Returns a URL to which a connection can be made. */
	public URL validResourceUrl()
		throws ResourceException
	{
		URL resourceUrl = this.resourceUrl();
		
		try {
			resourceUrl.openConnection();
		}
		catch (IOException ioe) {
			throw ResourceException.inaccessibleResourceException(ioe);
		}
		
		return resourceUrl;
	}
	
	public URI validResourceURI() throws ResourceException {
		URL url = this.validResourceUrl();
		URI uri = null;
		try {
			uri = new URI(url.toString().replace(" ", "%20"));
		} catch (MalformedURIException exception) {
			throw ResourceException.incorrectlySpecifiedResourceException(exception);
		}
		return uri;

	}
	
	/** Returns a URL based on the specification */
	protected abstract URL resourceUrl() throws ResourceException;

	public void toString(StringBuffer sb) {
		sb.append(this.location);
	}

	// **************** TopLink **********************************************

	/**
	 * put in hook for FileResourceSpecification
	 */
	private String getLocationForTopLink() {
		return this.getLocationForTopLink2();
	}
	protected String getLocationForTopLink2() {
		return this.location;
	}
	
	/**
	 * put in hook for FileResourceSpecification
	 */
	private void setLocationForTopLink(String loc) {
		this.setLocationForTopLink2(loc);
	}
	protected void setLocationForTopLink2(String loc) {
		this.location = loc;
	}

}
