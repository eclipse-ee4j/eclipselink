/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;

public class PhoneCompanyProject extends LegacyPhoneCompanyProject {


	/**
	 * You can create one of two phone company projects -
	 * withSharedAggregates - means that some of the mappings will
	 * be missing, but the Address aggregate is shared between Person and Company.
	 */
	public PhoneCompanyProject(boolean sharedAggregates) {
		super(sharedAggregates);
	}

	@Override
	protected MWTableDescriptor initializeServiceCallDescriptor() {
		MWTableDescriptor descriptor = super.initializeServiceCallDescriptor();
        ((MWVariableOneToOneMapping) descriptor.mappingNamed("serviceUser")).setUseProxyIndirection();
        
        return descriptor;
	}
	
	@Override
	protected MWAggregateDescriptor initializeServiceDescriptor() {
		MWAggregateDescriptor descriptor = super.initializeServiceDescriptor();
        ((MWVariableOneToOneMapping) descriptor.mappingNamed("primaryContact")).setUseProxyIndirection();
        
        return descriptor;
	}
}
