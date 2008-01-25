/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

public abstract class MWAccessorCodeGenPolicy 
	extends MWMethodCodeGenPolicy
{
	private volatile MWClassAttribute accessedAttribute;
	
	
	MWAccessorCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassCodeGenPolicy classCodeGenPolicy)
	{
		super(method, classCodeGenPolicy);
		this.accessedAttribute = attribute;
	}
	
	MWClassAttribute getAccessedAttribute()
	{
		return this.accessedAttribute;
	}
	
	/**
	 * Return "this.<attribute name>" in case of non value holders.
	 * Return "<value get method name>()" in case of value holder, and the value get method exists.
	 * Return null otherwise.
	 */
	protected String attributeValueCode()
	{
		if (getAccessedAttribute().isValueHolder()) {
			if (getAccessedAttribute().getValueGetMethod() != null) {
				return getAccessedAttribute().getValueGetMethod().shortSignature();
			}
			return null;
		}
		return "this." + getAccessedAttribute().getName();
	}
}
