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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;


public final class LegacyProjectMigrationDialog extends AbstractDialog {

	private boolean saveLater;
	
	// ************ constructors / initialization ****************
	
	LegacyProjectMigrationDialog(WorkbenchContext context) {
		super(context);
	}
		
	protected void initialize() {
		super.initialize();
		this.saveLater = false;
	}
		
	protected Component buildMainPanel() {
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		setTitle(resourceRepository().getString("PROJECT_LEGACY_MIGRATION_DIALOG_TITLE"));
		
		JLabel iconLabel = new JLabel(resourceRepository().getIcon("warning.large"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 10, 0, 0);
		mainPanel.add(iconLabel, constraints);
		
		LabelArea label = new LabelArea(resourceRepository().getString("PROJECT_LEGACY_MIGRATION_DIALOG_WARNING"));
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(10, 10, 0, 10);
		mainPanel.add(label, constraints);

		return mainPanel;
	}
	
	// ********** opening **********

	protected String helpTopicId() {
		return "dialog.projectLegacyMigration";
	}

	protected Component initialFocusComponent() {
		return getButtonFor(getOKAction());
	}
	
	protected Iterator buildCustomActions() {
		Collection customActions = new ArrayList();
		customActions.add(buildSaveLaterAction());
		return customActions.iterator();
	}

	// ********** OK action **********

	protected String buildOKText() {
		return this.resourceRepository().getString("PROJECT_LEGACY_MIGRATION_DIALOG_SAVE_NOW");
	}

	// ********** save later action **********

	protected Action buildSaveLaterAction() {
		return new AbstractAction(this.buildSaveLaterText()) {
			public void actionPerformed(ActionEvent e) {
				LegacyProjectMigrationDialog.this.saveLaterPressed();
			}
		};
	}	

	protected String buildSaveLaterText() {
		return this.resourceRepository().getString("PROJECT_LEGACY_MIGRATION_DIALOG_SAVE_LATER");
	}

	protected void saveLaterPressed() {
		this.saveLater = true;
		this.dispose();
		
	}
	
	public boolean wasConfirmed() {
		// return true if the user clicked either "save now" or "save later"
		return super.wasConfirmed() || this.saveLater;
	}
	
	public boolean saveLater() {
		return this.saveLater;
	}

}
