/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.internal;

import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * This subclass builds a label that simply says "Editor".
 */
final class EditorView
	extends AbstractEditorView
{

	/**
	 * The resource repository will provide the label's icon,
	 * text, and mnemonic.
	 */
	EditorView(PropertyValueModel nodeHolder, WorkbenchContext context) {
		super(nodeHolder, context);
	}

	/**
	 * The label is fixed for this view.
	 * @see AbstractEditorView#buildLabel()
	 */
	JLabel buildLabel() {
		JLabel label = new JLabel(this.resourceRepository().getString("EDITOR_LABEL"));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic("EDITOR_LABEL"));
		label.setIcon(this.resourceRepository().getIcon("editor"));
		return label;
	}

}
