/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.NoneSelectedCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;


public class ProjectCellRendererAdapter extends NoneSelectedCellRendererAdapter
{
	public ProjectCellRendererAdapter(ResourceRepository repository) {
		super(repository);
	}

	public String buildAccessibleName(Object value) {
		if (value instanceof MWRelationalProject)
			return resourceRepository().getString("ACCESSIBLE_RELATIONAL_PROJECT_NODE", ((MWRelationalProject)value).displayString());

		if (value instanceof MWEisProject)
			return resourceRepository().getString("ACCESSIBLE_EIS_PROJECT_NODE", ((MWEisProject)value).displayString());

		if (value instanceof MWOXProject)
			return resourceRepository().getString("ACCESSIBLE_OX_PROJECT_NODE", ((MWOXProject)value).displayString());

		return null;
	}

    protected Icon buildNonNullValueIcon(Object value) {
		if (value instanceof MWRelationalProject)
			return resourceRepository().getIcon("project.relational");

		if (value instanceof MWEisProject)
			return resourceRepository().getIcon("project.eis");

		if (value instanceof MWOXProject)
			return resourceRepository().getIcon("project.ox");

		return resourceRepository().getIcon("warning");
	}

	protected String buildNonNullValueText(Object value) {
		return ((MWProject) value).getName();
	}
}
