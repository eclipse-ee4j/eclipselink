/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;


public interface MWReferenceMapping 
	extends MWReferenceObjectMapping, 
			 MWIndirectableMapping 
{

	// ************* Private Owned ************
	
	boolean isPrivateOwned();			
	void setPrivateOwned(boolean newValue);
		public final static String PRIVATE_OWNED_PROPERTY = "privateOwned";


}
