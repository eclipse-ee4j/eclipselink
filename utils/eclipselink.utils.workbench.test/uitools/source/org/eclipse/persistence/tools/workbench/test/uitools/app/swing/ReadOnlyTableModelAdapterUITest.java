/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TableModelAdapterTests.PersonColumnAdapter;

import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;


/**
 * Make it easy to test the table model adapter and
 * renderers without any editing allowed.
 */
public class ReadOnlyTableModelAdapterUITest extends TableModelAdapterUITest {

	public static void main(String[] args) throws Exception {
		new ReadOnlyTableModelAdapterUITest().exec(args);
	}

	protected ReadOnlyTableModelAdapterUITest() {
		super();
	}

	protected ColumnAdapter buildColumnAdapter() {
		return new PersonColumnAdapter() {
			public boolean isColumnEditable(int index) {
				return false;
			}
		};
	}

}
