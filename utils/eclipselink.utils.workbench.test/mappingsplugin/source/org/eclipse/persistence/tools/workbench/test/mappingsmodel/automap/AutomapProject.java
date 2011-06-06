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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.automap;

import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

final class AutomapProject extends TestCase
{
	private MWProject project;

	AutomapProject(MWProject project)
	{
		super(project.getName());
		this.project = project;
	}

	void startTest(AutomapVerifier verifier) throws Exception
	{
		// First make sure the project has been unmapped
		unmapProject();

		// Perform the automap
		project.automap(CollectionTools.collection(project.descriptors()));

		// Test the validity of the automap
		verifier.verify(project);
	}

	private void unmapProject()
	{
		for (Iterator iter = project.descriptors(); iter.hasNext();)
		{
			MWDescriptor descriptor = (MWDescriptor) iter.next();
			unmapMappings(descriptor.mappings());

			if (descriptor instanceof MWTableDescriptor)
			{
				MWTableDescriptor tableDescriptor = (MWTableDescriptor) descriptor;
				tableDescriptor.setPrimaryTable(null);
			}
		}
	}

	private void unmapMappings(Iterator iter)
	{
		while (iter.hasNext())
		{
			MWMapping mapping = (MWMapping) iter.next();

			if (mapping instanceof MWManyToManyMapping)
			{
				MWManyToManyMapping manyToManyMapping = (MWManyToManyMapping) mapping;
				manyToManyMapping.setRelationTable(null);
				manyToManyMapping.setReferenceDescriptor(null);
				manyToManyMapping.setSourceReference(null);
				manyToManyMapping.setTargetReference(null);
			}
			else
			{
				mapping.getParentDescriptor().removeMapping(mapping);
			}
		}
	}
}
