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

import java.awt.Font;

import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


/**
 * This subclass keeps its label in synch with the selected node.
 */
final class PreferencesEditorView
	extends AbstractEditorView
{
	/** this label is kept in synch with the selected node's display string */
	private JLabel label;


	PreferencesEditorView(ValueModel selectedNodeHolder) {
		super(selectedNodeHolder, null);
		this.label = new JLabel();
		Font font = this.label.getFont();
		this.label.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 2));
	}

	/**
	 * @see AbstractEditorView#buildLabel()
	 */
	JLabel buildLabel() {
		return this.label;
	}

	/**
	 * if we have a new properties page, we probably have a new label
	 * @see AbstractEditorView#installNewPropertiesPage()
	 */
	void installNewPropertiesPage() {
		this.label.setText(((PreferencesNode) this.node).displayString());
		super.installNewPropertiesPage();
	}

}