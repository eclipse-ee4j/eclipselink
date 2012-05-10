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

import java.net.URL;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class ClasspathResourceSpecification
	extends ResourceSpecification 
{
	private static String KEY = "CLASSPATH_RESOURCE";
	
	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ClasspathResourceSpecification.class);
		
		descriptor.getInheritancePolicy().setParentClass(ResourceSpecification.class);
		
		return descriptor;
	}
		
	
	// **************** Constructors ******************************************
	
	/** For TopLink only */
	private ClasspathResourceSpecification() {
		super();
	}
	
	public ClasspathResourceSpecification(MWModel parent, String resourceName) {
		super(parent, resourceName);
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
		
		URL resourceUrl = this.getRepository().findResource(this.location);
		
		if (resourceUrl == null) {
			throw ResourceException.nonexistentResourceException(null);
		}
		
		return resourceUrl;
	}
}
