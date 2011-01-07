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
package org.eclipse.persistence.tools.workbench.uitools.app.swing;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * This adapter is used by the table model adapter to
 * convert a model object into the models used for each of
 * the cells for the object's corresponding row in the table.
 */
public interface ColumnAdapter {
	/**
	 * Return the number of columns in the table.
	 * Typically this is static.
	 */
	int getColumnCount();

	/**
	 * Return the name of the specified column.
	 */
	String getColumnName(int index);

	/**
	 * Return the class of the specified column.
	 */
	Class getColumnClass(int index);

	/**
	 * Return whether the specified column is editable.
	 * Typically this is the same for every row.
	 */
	boolean isColumnEditable(int index);

	/**
	 * Return the cell models for the specified subject
	 * that corresponds to a single row in the table.
	 */
	PropertyValueModel[] cellModels(Object subject);

}
