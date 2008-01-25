/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.uitools.cell;

/**
 * make the tree read-only so only the renderers are used
 */
public class TreeCellRendererUITest extends TreeCellEditorUITest {

	public static void main(String[] args) throws Exception {
		new TreeCellRendererUITest().exec(args);
	}

	protected TreeCellRendererUITest() {
		super();
	}

	protected boolean isEditable() {
		return false;
	}

}
