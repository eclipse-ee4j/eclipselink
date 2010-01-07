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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.EventObject;

/**
 * A "DescriptorCreationFailure" event gets delivered whenever a project is unable
 * to create a descriptor for a particular class.
 */
public class DescriptorCreationFailureEvent extends EventObject {

	/** the name of the class that that a descriptor could not be created for*/
	private volatile String className;
	
	/** the cause of the creation failure */
	private volatile String resourceStringKey;


	public DescriptorCreationFailureEvent(Object source, String className, String resourceStringKey) {
		super(source);
		this.className = className;
		this.resourceStringKey = resourceStringKey;
	}

	public String getClassName() {
		return this.className;
	}

	public String getResourceStringKey() {
		return this.resourceStringKey;
	}

}
