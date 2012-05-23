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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalDescriptor;

/**
 * Describes the Project Defaults for a Transactional Project.
 * 
 *  @version 10.1.3
 */
public abstract class MWTransactionalProjectDefaultsPolicy extends MWProjectDefaultsPolicy
{
	public static final String EVENTS_POLICY = "Events";
	public static final String RETURNING_POLICY = "Returning";
	
	protected MWTransactionalProjectDefaultsPolicy()
	{
		super();
	}

	public MWTransactionalProjectDefaultsPolicy(MWModel parent)
	{
		super(parent);
	}
	
	protected MWCachingPolicy buildCachingPolicy()
	{
		return new MWTransactionalProjectCachingPolicy(this);
	}
	
	protected void initializePolicyDescriptors()
	{
		super.initializePolicyDescriptors();
		addPolicyDescriptor(EVENTS_POLICY, new EventsPolicyDescriptor());
		addPolicyDescriptor(RETURNING_POLICY, new ReturningPolicyDescriptor());
	}

	private class EventsPolicyDescriptor implements PolicyDescriptor
	{
		public void applyPolicyToDescriptor(MWMappingDescriptor descriptor) {
			if (!descriptor.getEventsPolicy().isActive()) {
				descriptor.addEventsPolicy();
			}
		}	
	}
	
	private class ReturningPolicyDescriptor implements PolicyDescriptor
	{
		public void applyPolicyToDescriptor(MWMappingDescriptor descriptor) {
			if (descriptor.supportsReturningPolicy()) {
				((MWTransactionalDescriptor) descriptor).addReturningPolicy();
			}
		}
	}

}
