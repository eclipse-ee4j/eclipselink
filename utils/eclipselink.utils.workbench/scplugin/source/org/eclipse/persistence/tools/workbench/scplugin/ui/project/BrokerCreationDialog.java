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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.CheckList;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.ServerPlatformManager;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.NullServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.SessionCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;


final class BrokerCreationDialog extends AbstractValidatingDialog {
	
	private JCheckBox useServerPlatformCheckBox;
	private ComboBoxModel serverPlatformComboBoxModel;
	private String previousServerClassName;
	private JTextField nameTextField;
	private final PropertyValueModel stringHolder;
	private final PropertyValueModel serverPlatformHolder;
	private final CollectionValueModel itemHolder;
	private final ObjectListSelectionModel selectionModel;
	private final Collection sessionBrokerNames;

//	   ************ constructors / initialization ****************

	BrokerCreationDialog( WorkbenchContext context,
								 CollectionValueModel itemHolder,
								 ObjectListSelectionModel selectionModel,
								 PropertyValueModel stringHolder,
								 PropertyValueModel serverPlatformHolder,
								 Collection sessionBrokerNames) {
		super(context);
		this.stringHolder = stringHolder;
		this.serverPlatformHolder = serverPlatformHolder;
		this.itemHolder = itemHolder;
		this.selectionModel = selectionModel;
		this.sessionBrokerNames = sessionBrokerNames;
	}

	protected void initialize() {
		super.initialize();
		getOKAction().setEnabled( false);
	}

	private Document buildNameDocumentAdapter() {
		return new DocumentAdapter(this.stringHolder);
	}

	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		setTitle( resourceRepository().getString( "BROKER_CREATION_DIALOG_TITLE"));
		boolean showSessionsList = ((Iterator) this.itemHolder.getValue()).hasNext();

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());

		// Name label
		JLabel nameLabel = SwingComponentFactory.buildLabel("BROKER_CREATION_DIALOG_NAME_LABEL", resourceRepository());

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 5, 0, 0);

		panel.add(nameLabel, constraints);

		// Name text field
		this.nameTextField = new JTextField(buildNameDocumentAdapter(), null, 20);
		this.nameTextField.getDocument().addDocumentListener(buildNameFieldDocumentListener());

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 5, 0, 5);

		panel.add(this.nameTextField, constraints);
		nameLabel.setLabelFor(this.nameTextField);

		// Server Platform
		JPanel serverPlatformPanel = buildServerPlatformPanel();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(10, 0, 0, 0);

		helpManager().addTopicID(serverPlatformPanel, helpTopicId() + ".serverPlatform");
		panel.add(serverPlatformPanel, constraints);

		if (showSessionsList) {
			buildSessionsList(panel);
		}

		helpManager().addTopicID(panel, helpTopicId() + ".name");

		return panel;
	}

	private DocumentListener buildNameFieldDocumentListener() {
		return new DocumentListener() {
		   public void insertUpdate(DocumentEvent e) {
			   updateOKAction();
		   }
		   public void removeUpdate(DocumentEvent e) {
			   updateOKAction();
		   }
		   public void changedUpdate(DocumentEvent e) {
		   }
		};
	}

	private ComboBoxModel buildServerPlatformComboBoxModel() {
		SimpleCollectionValueModel platformsHolder = new SimpleCollectionValueModel(CollectionTools.vector(serverPlatforms()));
		return new ComboBoxModelAdapter(platformsHolder, this.serverPlatformHolder);
	}

	private Iterator serverPlatforms() {
		Collection servers = CollectionTools.sortedSet(ServerPlatformManager.instance().platformShortNames());
		String serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
		servers.remove(ClassTools.shortNameForClassNamed(serverClassName));
		return servers.iterator();
	}

	boolean usesServerPlatform() {
		return this.useServerPlatformCheckBox.isSelected();
	}

	private ActionListener buildServerPlatformAction() {

		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean checked = ((JCheckBox) e.getSource()).isSelected();
				String serverClassName;

				if (!checked) {
					BrokerCreationDialog.this.previousServerClassName = (String) BrokerCreationDialog.this.serverPlatformComboBoxModel.getSelectedItem();

					serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
					serverClassName = ClassTools.shortNameForClassNamed(serverClassName);
				}
				else {
					if (BrokerCreationDialog.this.previousServerClassName == null) {
						BrokerCreationDialog.this.previousServerClassName = SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT;
					}
					serverClassName = BrokerCreationDialog.this.previousServerClassName;
				}

				BrokerCreationDialog.this.serverPlatformComboBoxModel.setSelectedItem(serverClassName);
			}
		};
	}

	private JPanel buildServerPlatformPanel() {

		this.useServerPlatformCheckBox = new JCheckBox();
		this.useServerPlatformCheckBox.setText(resourceRepository().getString( "USE_SERVER_PLATFORM_CHECK_BOX"));
		this.useServerPlatformCheckBox.setMnemonic(resourceRepository().getMnemonic( "USE_SERVER_PLATFORM_CHECK_BOX"));
		this.useServerPlatformCheckBox.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex( "USE_SERVER_PLATFORM_CHECK_BOX"));
		this.useServerPlatformCheckBox.addActionListener(buildServerPlatformAction());

		final JPanel subPane = buildServerPlatformSubPane();
		subPane.setEnabled(false);

		this.useServerPlatformCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				JCheckBox checkBox = (JCheckBox) e.getSource();
				subPane.setEnabled(checkBox.isSelected());
			}
		});

		updateServerPlatformWidgets();
		return new GroupBox(this.useServerPlatformCheckBox, subPane);
	}

	private void updateServerPlatformWidgets() {

		String serverClassName = (String) this.serverPlatformComboBoxModel.getSelectedItem();
		boolean selected = (serverClassName != null);

		if (selected) {
			ServerPlatformAdapter noServerPlatform = NullServerPlatformAdapter.instance();
			String noServerPlatformClassName = ClassTools.shortNameForClassNamed(noServerPlatform.getServerClassName());
			selected = !serverClassName.equals(noServerPlatformClassName);
		}

		this.useServerPlatformCheckBox.setSelected(selected);
	}

	private ListCellRenderer buildServerPlatformRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				if (value == null)
					return "";

				return resourceRepository().getString((String) value);
			}
		};
	}

	private JPanel buildServerPlatformSubPane() {
		GridBagConstraints constraints = new GridBagConstraints();
		Pane container = new Pane(new GridBagLayout());

		// Platform label
		JLabel platformLabel = SwingComponentFactory.buildLabel("SERVER_PLATFORM_COMBO_BOX", resourceRepository());

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(platformLabel, constraints);

		// Platform combo box
		this.serverPlatformComboBoxModel = buildServerPlatformComboBoxModel();
		JComboBox serverPlatformComboBox = new JComboBox(this.serverPlatformComboBoxModel);
		serverPlatformComboBox.setRenderer(buildServerPlatformRenderer());

		String platformName = preferences().get(SCPlugin.SERVER_PLATFORM_PREFERENCE, null);
		serverPlatformComboBox.setSelectedItem(platformName);

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		container.add(serverPlatformComboBox, constraints);
		platformLabel.setLabelFor(serverPlatformComboBox);

		return container;
	}

	private void buildSessionsList(JPanel panel) {

		GridBagConstraints constraints = new GridBagConstraints();

		// Sessions list label
		JLabel sessionsListLabel = SwingComponentFactory.buildLabel( "BROKER_CREATION_DIALOG_SESSIONS_LIST", resourceRepository());

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(10, 0, 0, 0);

		panel.add(sessionsListLabel, constraints);

		// Sessions List
		CheckList sessionsList = new CheckList
		(
			this.itemHolder,
			this.selectionModel,
			buildLabelDecorator()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(1, 0, 0, 0);

		panel.add(sessionsList, constraints);
		sessionsListLabel.setLabelFor(sessionsList);
	}

	private CellRendererAdapter buildLabelDecorator() {
		return new SessionCellRendererAdapter(resourceRepository());
	}

	private void updateOKAction() {

		String sessionName = this.nameTextField.getText().trim();
		boolean valid = ( sessionName.length() > 0) && !this.sessionBrokerNames.contains(sessionName);

		getOKAction().setEnabled( valid);

		if( valid)
			clearErrorMessage();
		else
			setErrorMessageKey( "BROKER_CREATION_DIALOG_INVALID_NAME");
	}

//	   ********** opening **********

	private String buildUniqueSessionBrokerName() {
		String sessionBrokerName = preferences().get(SCPlugin.NEW_NAME_BROKER_PREFERENCE, resourceRepository().getString("BROKER_CREATION_DIALOG_NEW_NAME"));
		return NameTools.uniqueNameFor(sessionBrokerName, this.sessionBrokerNames);
	}

	protected void prepareToShow() {
		super.prepareToShow();
		this.nameTextField.setText(buildUniqueSessionBrokerName());
		this.nameTextField.selectAll();
	}

	protected String helpTopicId() {
		return "dialog.sessionBrokerCreation";
	}

	protected Component initialFocusComponent() {
		return this.nameTextField;
	}
}
