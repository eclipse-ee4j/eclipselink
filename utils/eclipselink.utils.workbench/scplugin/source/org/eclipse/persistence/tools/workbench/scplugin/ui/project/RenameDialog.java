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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


public class RenameDialog extends AbstractValidatingDialog {
	
	private JTextField nameTextField;
	private final Collection sessionNames;
	private final PropertyValueModel stringHolder;
     		
//	   ************ constructors / initialization ****************

	public RenameDialog( WorkbenchContext context, PropertyValueModel stringHolder, Collection sessionNames) {
			super(context);
			this.stringHolder = stringHolder;
			this.sessionNames = sessionNames;
	}
     
	protected void initialize() {
			super.initialize();
			getOKAction().setEnabled( false);
	}

	private Document buildNameDocumentAdapter() {
		return new DocumentAdapter(stringHolder);
	}

	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		setTitle( resourceRepository().getString( "RENAME_DIALOG_TITLE"));

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());

		// Name label
		JLabel nameLabel = new JLabel(resourceRepository().getString("SESSION_NAME_LABEL"));
		nameLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("SESSION_NAME_LABEL"));
		nameLabel.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("SESSION_NAME_LABEL"));

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		panel.add(nameLabel, constraints);

		// Name text field
		nameTextField = new JTextField(buildNameDocumentAdapter(), null, 20);

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		panel.add(nameTextField, constraints);
		nameLabel.setLabelFor(nameTextField);

		nameTextField.getDocument().addDocumentListener(
		   new DocumentListener() {
			   public void insertUpdate( DocumentEvent e) {
				   updateOKAction();
			   }
			   public void removeUpdate( DocumentEvent e) {
				   updateOKAction();
			   }
			   public void changedUpdate( DocumentEvent e) {
			   }
		   }
		);

		helpManager().addTopicID(panel, helpTopicId() + ".name");
		return panel;
	}
	
	protected String getNewName() {
		
		return nameTextField.getText().trim();
	}

	private void updateOKAction() {
		String sessionName = nameTextField.getText().trim();
		boolean valid = ( sessionName.length() > 0) &&
							 !sessionNames.contains( sessionName);

		getOKAction().setEnabled( valid);

		if( valid)
			clearErrorMessage();
		else
			setErrorMessageKey( "SESSION_CREATION_DIALOG_INVALID_NAME");
	}

//	   ********** opening **********

	protected String helpTopicId() {
		return "dialog.renameSession";
	}

	protected void prepareToShow() {

		super.prepareToShow();
		nameTextField.selectAll();
	}

	protected Component initialFocusComponent() {
		return nameTextField;
	}
}
