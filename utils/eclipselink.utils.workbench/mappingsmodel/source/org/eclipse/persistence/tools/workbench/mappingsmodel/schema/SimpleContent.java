/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class SimpleContent
	extends Content 
{	
	// **************** Constructors ******************************************
	
	/** Toplink use only */
	protected SimpleContent() {
		super();
	}

	SimpleContent(ExplicitComplexTypeDefinition parent) {
		super(parent);
	}
	
	
	// **************** Behavior **********************************************
	
	boolean hasTextContent() {
		return true;
	}
	
	boolean containsWildcard() {
		return false;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(SimpleContent.class);
		descriptor.getInheritancePolicy().setParentClass(Content.class);
		
		return descriptor;
	}
}
