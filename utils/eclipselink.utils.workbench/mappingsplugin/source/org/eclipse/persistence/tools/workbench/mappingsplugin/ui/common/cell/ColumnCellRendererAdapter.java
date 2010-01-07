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

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.NoneSelectedCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;


public class ColumnCellRendererAdapter 
	extends NoneSelectedCellRendererAdapter 
{
	private final boolean printQualifiedName;
	private final boolean appendType;
	
	public ColumnCellRendererAdapter(ResourceRepository repository) {
		this(repository, true);
	}
	
	public ColumnCellRendererAdapter(ResourceRepository repository, boolean printQualifiedName) {
		this(repository, printQualifiedName, true);
	}
	
	/**
	 * if you would prefer not to display the appended type, use this constructor w/ false.
	 */
	public ColumnCellRendererAdapter(ResourceRepository repository, boolean printQualifiedName, boolean appendType) {
		super(repository);
		this.printQualifiedName = printQualifiedName;
		this.appendType = appendType;
	}
	
	protected String buildNonNullValueText(Object value) {
		MWColumn col = (MWColumn) value;
		StringBuffer sb = new StringBuffer(100);
		sb.append(this.printQualifiedName ? col.qualifiedName() : col.getName());
		if (this.appendType) {
			sb.append(" : ");
			sb.append(col.getDatabaseType().getName());
		}
		return sb.toString();
	}

}
