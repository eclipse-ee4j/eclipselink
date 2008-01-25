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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;

public interface MWContainerMapping extends MWNode {

	MWContainerPolicy getContainerPolicy();
		String CONTAINER_POLICY_PROPERTY = "containerPolicy";

	MWCollectionContainerPolicy setCollectionContainerPolicy();
	MWListContainerPolicy setListContainerPolicy();
	MWSetContainerPolicy setSetContainerPolicy();
    
	boolean usesTransparentIndirection();
	
}
