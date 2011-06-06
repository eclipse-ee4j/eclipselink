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
package org.eclipse.persistence.tools.workbench.mappingsmodel.resource;

public final class ResourceException 
	extends Exception 
{
	private volatile String code;
	
	// *** Possible codes ***
	public final static String UNSPECIFIED_RESOURCE = "UNSPECIFIED_RESOURCE";
	public final static String INCORRECTLY_SPECIFIED_RESOURCE = "INCORRECTLY_SPECIFIED_RESOURCE";
	public final static String NONEXISTENT_RESOURCE = "NONEXISTENT_RESOURCE";
	public final static String INACCESSIBLE_RESOURCE = "INACCESSIBLE_RESOURCE";
	
	
	public static ResourceException unspecifiedResourceException(Throwable t) {
		return new ResourceException(t, UNSPECIFIED_RESOURCE);
	}
	
	public static ResourceException incorrectlySpecifiedResourceException(Throwable t) {
		return new ResourceException(t, INCORRECTLY_SPECIFIED_RESOURCE);
	}
	
	public static ResourceException nonexistentResourceException(Throwable t) {
		return new ResourceException(t, NONEXISTENT_RESOURCE);
	}
	
	public static ResourceException inaccessibleResourceException(Throwable t) {
		return new ResourceException(t, INACCESSIBLE_RESOURCE);
	}
	
	
	private ResourceException(Throwable t, String code) {
		super(t);
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
}
