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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


final class ReadOnlyFileDialog extends AbstractDialog {
	private Vector files;
	private boolean saveAsWasPressed;


	ReadOnlyFileDialog(WorkbenchContext context, Collection files) {
		super(context);
		this.files = (Vector) CollectionTools.sort(new Vector(files));
	}

	protected String helpTopicId() {
		return "dialog.readOnly";
	}

	protected void initialize() {
		super.initialize();
		this.setTitle(this.resourceRepository().getString("versionControlAssistance.title"));
	}

	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
 
		JTextArea descriptionLabel = new JTextArea(this.resourceRepository().getString("versionControlAssistance.message"), 0, 50);
		descriptionLabel.setLineWrap(true);
		descriptionLabel.setWrapStyleWord(true);
		descriptionLabel.setBackground(this.getBackground());
		descriptionLabel.setFont(this.getFont());
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.anchor = 21;
		constraints.fill = 0;
		constraints.insets = new Insets(2, 2, 2, 2);
		panel.add(descriptionLabel, constraints);

		JList fileListBox = SwingComponentFactory.buildList(this.files);

		JLabel title = new JLabel(this.resourceRepository().getString("readOnlyFiles"));
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1.0D;
		constraints.weighty = 0.0D;
		constraints.anchor = 21;
		constraints.fill = 0;
		constraints.insets = new Insets(2, 2, 2, 2);
		panel.add(title, constraints);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.weightx = 1.0D;
		constraints.weighty = 1.0D;
		constraints.anchor = 18;
		constraints.fill = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		panel.add(new JScrollPane(fileListBox), constraints);

		return panel;
	}

	protected String buildOKText() {
		return this.resourceRepository().getString("save");
	}

	protected Iterator buildCustomActions() {
		return new SingleElementIterator(this.buildSaveAsAction());
	}

	private Action buildSaveAsAction() {
		return new AbstractFrameworkAction(this.getWorkbenchContext()) {
			protected void initialize() {
				this.initializeTextAndMnemonic("saveAs");
			}
			protected void execute() {
				ReadOnlyFileDialog.this.saveAsPressed();
			}
		};
	}

	void saveAsPressed() {
		if (this.preConfirm()) {
			this.saveAsWasPressed = true;
			this.dispose();
		}
	}

	boolean saveAsWasPressed() {
		return this.saveAsWasPressed;
	}

	boolean saveWasPressed() {
		return this.wasConfirmed();
	}

}
