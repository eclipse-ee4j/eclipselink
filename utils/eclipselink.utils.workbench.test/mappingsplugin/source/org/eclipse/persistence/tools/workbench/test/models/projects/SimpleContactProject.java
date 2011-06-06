/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
