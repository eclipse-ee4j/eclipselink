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

public final class MWNullClassIndicatorPolicy 
	extends MWAbstractClassIndicatorPolicy
{
	private MWNullClassIndicatorPolicy() {
		super();
	}
	
	public MWNullClassIndicatorPolicy(MWClassIndicatorPolicy.Parent parent) {
		super(parent);
	}
	
	public MWAbstractClassIndicatorPolicy getValueForTopLink() {
		return null;
	}
	
	public String getType() {
		return MWClassIndicatorPolicy.NULL_TYPE;
	}

}