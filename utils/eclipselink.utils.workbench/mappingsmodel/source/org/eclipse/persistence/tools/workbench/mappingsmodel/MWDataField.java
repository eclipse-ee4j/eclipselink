/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel;

import org.eclipse.persistence.tools.workbench.utility.Model;

import org.eclipse.persistence.internal.helper.DatabaseField;

public interface MWDataField 
	extends MWNode, Model 
{	
	/** For UI functionality (display, problems, etc.) */
	String fieldName();
		String FIELD_NAME_PROPERTY = "fieldName";
	
	/** For runtime conversion */
	DatabaseField runtimeField();	
}
