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

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;


public class SimpleContactProject extends LegacySimpleContactProject {

	public SimpleContactProject() {
		super();
	}

	@Override
	public MWTableDescriptor initializePersonDescriptor(){
		MWTableDescriptor descriptor = super.initializePersonDescriptor();
        ((MWVariableOneToOneMapping) descriptor.mappingNamed("contact")).setUseProxyIndirection();
        
        return descriptor;
	}
 
}
