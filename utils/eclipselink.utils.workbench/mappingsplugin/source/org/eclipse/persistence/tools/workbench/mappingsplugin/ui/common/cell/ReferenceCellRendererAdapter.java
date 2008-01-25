/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.NoneSelectedCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;


public class ReferenceCellRendererAdapter extends NoneSelectedCellRendererAdapter {

	public ReferenceCellRendererAdapter(ResourceRepository repository) {
		super(repository);
	}

	protected String buildNonNullValueText(Object value) {
		MWReference reference = (MWReference) value;
		String sourceTableName = reference.getSourceTable().getName();
		String targetTableName = reference.getTargetTable().getName();

		return resourceRepository().getString("REFERENCE_SIGNATURE", reference.getName(), sourceTableName, targetTableName);
	}

}