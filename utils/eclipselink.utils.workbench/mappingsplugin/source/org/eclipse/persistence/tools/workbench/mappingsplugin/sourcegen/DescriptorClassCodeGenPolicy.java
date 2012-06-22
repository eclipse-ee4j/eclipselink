/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.sourcegen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWDefaultMethodCodeGenPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethodCodeGenPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWZeroArgumentConstructorCodeGenPolicy;


abstract class DescriptorClassCodeGenPolicy 
	extends AbstractClassCodeGenPolicy 
{
	private MWDescriptor descriptor;
	
	private Map accessorCodeGenPolicies;
	
	protected DescriptorClassCodeGenPolicy(MWDescriptor descriptor,  ApplicationContext context) 
	{
		super(context);
		this.descriptor = descriptor;
		this.accessorCodeGenPolicies = new HashMap();
		initialize();
	}
	
	protected MWClass getMWClass() {
		return getDescriptor().getMWClass();
	}
	
	public MWMethodCodeGenPolicy getMethodCodeGenPolicy(MWMethod method)
	{
		if (method.isZeroArgumentConstructor())
			return new MWZeroArgumentConstructorCodeGenPolicy(method, getDescriptor(), this);
		else if (this.accessorCodeGenPolicies.get(method) != null)
			return (MWMethodCodeGenPolicy) this.accessorCodeGenPolicies.get(method);
		else 
			return new MWDefaultMethodCodeGenPolicy(method, this);
	}
	
	protected MWDescriptor getDescriptor()
	{
		return this.descriptor;
	}
	
	protected void initialize()
	{
		// first, add code gen policies for the methods that are accessors
		for (Iterator it = getDescriptor().getMWClass().attributes(); it.hasNext(); )
			((MWClassAttribute) it.next()).addAccessorCodeGenPoliciesTo(this);
		
		// next, replace the code gen policies for the methods that are accessors for 
		// attributes with extra mapping input
		for (Iterator it = getDescriptor().mappings(); it.hasNext(); )
		{
			MWMapping mapping = (MWMapping) it.next();
			mapping.getInstanceVariable().addAccessorCodeGenPoliciesTo(this, mapping);
		}
	}
	
	public void addAccessorCodeGenPolicy(MWMethod method, MWMethodCodeGenPolicy methodCodeGenPolicy)
	{
		this.accessorCodeGenPolicies.put(method, methodCodeGenPolicy);
	}
}
