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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWError;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.StatusDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.DescriptorCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


abstract class AbstractMapInheritedAttributesAction extends AbstractFrameworkAction
{


	AbstractMapInheritedAttributesAction(WorkbenchContext context)
	{
		super(context);
	}


	protected final void execute()
	{
		Collection selectedDescriptors = CollectionTools.collection(selectedNodes());
		Vector status = new Vector();

		for (Iterator iter = selectedDescriptors.iterator(); iter.hasNext();)
		{
			ApplicationNode node = (ApplicationNode) iter.next();
			MWMappingDescriptor descriptor = (MWMappingDescriptor) node.getValue();

			try
			{
				execute(descriptor);
			}
			catch (ClassNotFoundException e)
			{
				MWError error = new MWError("MAP_INHERITED_ATTRIBUTES_ERROR", e.getLocalizedMessage());

				LinkedHashMap errors = new LinkedHashMap();
				errors.put(error, error);

				StatusDialog.Status modelStatus = StatusDialog.createStatus(descriptor, errors);
				status.add(modelStatus);
			}
		}

		if (!status.isEmpty())
			showProblems(status);
	}

	protected abstract void execute(MWMappingDescriptor descriptor) throws ClassNotFoundException;

	private void showProblems(Vector status)
	{
		StatusDialog dialog = new StatusDialog(
			getWorkbenchContext(),
			status,
			"MAP_INHERITED_ATTRIBUTES_STATUS_DIALOG_TITLE",
			"dialog.mapInheritedAttributes")
		{
			protected CellRendererAdapter buildNodeRenderer(Object value)
			{
				if (value instanceof MWDescriptor)
					return new DescriptorCellRendererAdapter(resourceRepository());

				return super.buildNodeRenderer(value);
			}
		};

		dialog.setVisible(true);
	}

}
