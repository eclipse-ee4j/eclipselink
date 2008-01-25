/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.utility.Model;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Describes the core behavior that a policy for a descriptor should
 * contain.
 * 
 * @version 10.1.3 
 */
public interface MWDescriptorPolicy
	extends MWNode, Model
{
	MWMappingDescriptor getOwningDescriptor();

	void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor);
	
	void dispose();
	
	MWDescriptorPolicy getPersistedPolicy();
	
	boolean isActive();
}
