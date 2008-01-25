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

/**
 * Interface for use in AdvancedPolicyAction$InterfaceAliasPolicyHolder so
 * the advanced property can be easily manipulated for the two descriptors 
 * types that use the policy.
 */
public interface MWInterfaceAliasDescriptor {
	
	public MWDescriptorPolicy getInterfaceAliasPolicy();
	
	public void removeInterfaceAliasPolicy();
	
	public void addInterfaceAliasPolicy();
	
}
