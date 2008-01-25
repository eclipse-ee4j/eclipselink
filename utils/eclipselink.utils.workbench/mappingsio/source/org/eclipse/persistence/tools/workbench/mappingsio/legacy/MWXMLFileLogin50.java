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
import deprecated.xml.XMLFileLogin;

/**
 * 
 */
class MWXMLFileLogin50 extends XMLFileLogin {

	MWXMLFileLogin50() {
		super();
	}

	MWXMLFileLogin50(String baseDirectoryName) {
		this();
		this.initialize(baseDirectoryName);
	}

	MWXMLFileLogin50(DatabasePlatform platform) {
		super(platform);
	}

	public Accessor buildAccessor() {
		MWAccessor50 accessor = new MWAccessor50();
		accessor.setBaseDirectoryNameOverride(this.getBaseDirectoryName());
		accessor.setFileNameExtensionOverride(this.getFileNameExtension());
		accessor.setCreatesDirectoriesAsNeededOverride(this.createsDirectoriesAsNeeded());
		return accessor;
	}

}
