/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;


public class NamedConnectionPoolCreationDialog extends AbstractValidatingDialog {
	
	private JTextField nameTextField;
	private final Collection poolNames;
	private final PropertyValueModel stringHolder;
     		
//	   ************ constructors / initialization ****************

	public NamedConnectionPoolCreationDialog( WorkbenchContext context,
															PropertyValueModel stringHolder,
															Iterator poolNames) {
			super(context);
			this.stringHolder = stringHolder;
			this.poolNames = CollectionTools.collection( poolNames);
	}
     
	protected void initialize() {
			super.initialize();
			getOKAction().setEnabled( false);
	}

	private Document buildNameDocumentAdapter() {
		return new DocumentAdapter(stringHolder, new RegexpDocument(RegexpDocument.RE_SQL_RELATED));
	}

	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		setTitle( resourceRepository().getString( "NAMED_CONNECTION_POOL_CREATION_DIALOG_TITLE"));

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());

		// Name label
		JLabel nameLabel = new JLabel(resourceRepository().getString("POOL_NAME_LABEL"));
		nameLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("POOL_NAME_LABEL"));
		nameLabel.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("POOL_NAME_LABEL"));

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
			   public void insertUpdate(DocumentEvent e) {
				   updateOKAction();
			   }
			   public void removeUpdate(DocumentEvent e) {
				   updateOKAction();
			   }
			   public void changedUpdate(DocumentEvent e) {
			   }
		   }
		);

		helpManager().addTopicID(panel, helpTopicId() + ".name");

		return panel;
	}

	private void updateOKAction() {
		String poolName = nameTextField.getText().trim();
		boolean valid = (poolName.length() > 0) &&
							 !poolNames.contains(poolName);

		getOKAction().setEnabled(valid);

		if (valid)
			clearErrorMessage();
		else
			setErrorMessageKey("NAMED_CONNECTION_POOL_CREATION_DIALOG_INVALID_NAME");
	}

	private String buildUniquePoolName() {
		String poolName = preferences().get(SCPlugin.NEW_NAME_POOL_PREFERENCE, resourceRepository().getString("POOL_CREATION_DIALOG_NEW_POOL_NAME"));
		return NameTools.uniqueNameFor(poolName, poolNames);
	}

//	   ********** opening **********

	protected String helpTopicId() {
		return "dialog.namedConnectionPoolCreation";
	}

	protected void prepareToShow() {

		super.prepareToShow();
		nameTextField.setText(buildUniquePoolName());
		nameTextField.selectAll();
	}

	protected Component initialFocusComponent() {
		return nameTextField;
	}
}
