/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;

public final class MWLegacyUnmappedMapping extends MWMapping {

	private MWLegacyUnmappedMapping() {
		super();
	}

	public MWLegacyUnmappedMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}
	
	protected void addChildrenTo(List children) {
	}

	
	protected void initializeOn(MWMapping newMapping) {

	}

	public void addWrittenFieldsTo(Collection writtenFields) {
	}

	protected DatabaseMapping buildRuntimeMapping() {
		return null;
	}
}
