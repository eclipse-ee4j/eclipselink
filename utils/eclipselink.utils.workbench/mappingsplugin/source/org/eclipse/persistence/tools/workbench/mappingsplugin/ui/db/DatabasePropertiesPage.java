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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ProjectCreationDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.NameTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


public class DatabasePropertiesPage extends TitledPropertiesPage {

	ObjectListSelectionModel loginSpecSelectionModel;
	private PropertyValueModel selectedLoginSpecHolder;

	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiDbBundle.class
	};


	public DatabasePropertiesPage(WorkbenchContext context) {
		super(context);
	}

	private MWDatabase getDatabase() {
		return (MWDatabase) this.getSelectionHolder().getValue();
	}
	
	private String helpTopicId() {
		return "database";
	}
	
	protected Component buildPage() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// platform chooser
		JTextField platformNameTextField = this.buildPlatformNameTextField();

		JButton changePlatformButton = this.buildButton("CHANGE_DATABASE_PLATFORM_BUTTON_TEXT");
		changePlatformButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DatabasePropertiesPage.this.promptToChangePlatform();
			}
		});

		JComponent platformNameWidgets = this.buildLabeledComponent(
			"DATABASE_PLATFORM_LABEL",
			platformNameTextField,
			changePlatformButton
		);

		((JLabel) platformNameWidgets.getComponent(0)).setLabelFor(changePlatformButton);
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 3;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		panel.add(platformNameWidgets, constraints);
		this.addHelpTopicId(platformNameWidgets, this.helpTopicId() + ".platform");

		// login spec list label
		JLabel loginSpecListLabel = this.buildLabel("DEFINED_LOGINS_LIST_LABEL_TEXT");

		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);

		panel.add(loginSpecListLabel, constraints);
		this.addAlignLeft(loginSpecListLabel);

		// login spec list
		JList loginSpecList = this.buildLoginSpecListBox();

		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 3;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 0);

		JScrollPane scrollPane = new JScrollPane(loginSpecList);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setMinimumSize(new Dimension(50, 100));
		scrollPane.setPreferredSize(new Dimension(50, 100));
		scrollPane.setMaximumSize(new Dimension(50, 100));

		panel.add(scrollPane, constraints);
		loginSpecListLabel.setLabelFor(loginSpecList);
		this.addHelpTopicId(loginSpecList, this.helpTopicId() + ".login");

		// Add button
		JButton addButton = this.buildButton("ADD_LOGIN_BUTTON_TEXT");
		addButton.addActionListener(this.buildAddButtonActionListener());
		addButton.setToolTipText(this.resourceRepository().getString("ADD_LOGIN_TOOL_TIP_TEXT"));

		constraints.gridx			= 2;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 0);

		panel.add(addButton, constraints);
		this.addAlignRight(addButton);

		// Rename button
		JButton renameButton = this.buildButton("RENAME_LOGIN_BUTTON_TEXT");
		renameButton.setToolTipText(this.resourceRepository().getString("RENAME_LOGIN_TOOL_TIP_TEXT"));
		renameButton.setEnabled(false);
		renameButton.addActionListener(this.buildRenameButtonActionListener());
		this.loginSpecSelectionModel.addListSelectionListener(this.buildLoginSpecListSelectionListener(renameButton));

		constraints.gridx			= 2;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 0);

		panel.add(renameButton, constraints);
		this.addAlignRight(renameButton);

		// Remove button
		JButton removeButton = this.buildButton("REMOVE_LOGIN_BUTTON_TEXT");
		removeButton.setToolTipText(this.resourceRepository().getString("REMOVE_LOGIN_TOOL_TIP_TEXT"));
		removeButton.setEnabled(false);
		removeButton.addActionListener(this.buildRemoveButtonActionListener());
		this.loginSpecSelectionModel.addListSelectionListener(this.buildLoginSpecListSelectionListener(removeButton));

		constraints.gridx			= 2;
		constraints.gridy			= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);

		panel.add(removeButton, constraints);
		this.addAlignRight(removeButton);

		// login spec panel
		this.selectedLoginSpecHolder = this.buildSelectedLoginSpecHolder(this.loginSpecSelectionModel);
		LoginSpecPanel loginSpecPanel = new LoginSpecPanel(this.selectedLoginSpecHolder, getApplicationContext());
		
		constraints.gridx			= 0;
		constraints.gridy			= 4;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(10, 0, 0, 0);

		panel.add(loginSpecPanel, constraints);
		this.addPaneForAlignment(loginSpecPanel);
		this.addHelpTopicId(loginSpecPanel, this.helpTopicId() + ".loginInfo");

		// development login
		JComponent developmentLoginWidgets = this.buildDevelopmentLoginWidgets();

		constraints.gridx			= 0;
		constraints.gridy			= 5;
		constraints.gridwidth	= 3;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(10, 0, 0, 0);

		panel.add(developmentLoginWidgets, constraints);
		this.addHelpTopicId(developmentLoginWidgets, this.helpTopicId() + ".devLogin");

		// deployment login
		JComponent deploymentLoginWidgets = this.buildDeploymentLoginWidgets();

		constraints.gridx			= 0;
		constraints.gridy			= 6;
		constraints.gridwidth	= 3;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 0, 5, 0);

		panel.add(deploymentLoginWidgets, constraints);
		this.addHelpTopicId(deploymentLoginWidgets, this.helpTopicId() + ".depLogin");

		this.addHelpTopicId(panel, this.helpTopicId());		
		return panel;
	}

	
	// ********* platform name ***********
	
	private JTextField buildPlatformNameTextField() {
		JTextField textField = new JTextField(this.buildPlatformNameDocument(), null, 1);
		textField.setEditable(false);
		return textField;
	}

	private Document buildPlatformNameDocument() {
		return new DocumentAdapter(this.buildPlatformNameHolder());
	}

	private PropertyValueModel buildPlatformNameHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWDatabase.DATABASE_PLATFORM_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDatabase) this.subject).getDatabasePlatform().getName();
			}
		};
	}

	void promptToChangePlatform() {
		if (this.getNode().isDirty()) {
			int option = JOptionPane.showConfirmDialog(this.getWorkbenchContext().getCurrentWindow(),
											this.resourceRepository().getString("CHANGE_DATABASE_PLATFORM.message", StringTools.CR),
											this.resourceRepository().getString("CHANGE_DATABASE_PLATFORM.title"),
											JOptionPane.YES_NO_CANCEL_OPTION,
											JOptionPane.WARNING_MESSAGE);
											
											
			if (option == JOptionPane.CANCEL_OPTION) {
				return;
			}
			if (option == JOptionPane.YES_OPTION) {
				this.getNode().save(null, this.getWorkbenchContext());
			}
		}

		if (this.getDatabase().getDevelopmentLoginSpec() != null) {
			if (this.getDatabase().isConnected()) {
				int option = JOptionPane.showConfirmDialog(this.getWorkbenchContext().getCurrentWindow(),
						this.resourceRepository().getString("CLEAR_LOGIN_AND_LOGOUT.message", StringTools.CR),
						this.resourceRepository().getString("CLEAR_LOGIN_AND_LOGOUT.title"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
						
				if(option == JOptionPane.YES_OPTION) {
					if (this.getDatabase().isConnected()) {
						try {
							this.getDatabase().logout();
						} catch (SQLException exception) {
							throw new RuntimeException(exception);
						}
					}
					this.getDatabase().setDevelopmentLoginSpec(null);
				}
			} else {
				int option = JOptionPane.showConfirmDialog(this.getWorkbenchContext().getCurrentWindow(),
						this.resourceRepository().getString("CLEAR_LOGIN.message", StringTools.CR),
						this.resourceRepository().getString("CLEAR_LOGIN.title"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
						
				if(option == JOptionPane.YES_OPTION) {
					this.getDatabase().setDevelopmentLoginSpec(null);
				}
			}
		}

		SimplePropertyValueModel selectionHolder = new SimplePropertyValueModel(this.getDatabase().getDatabasePlatform());
		DatabasePlatformChooserDialog platformChooser = new DatabasePlatformChooserDialog(this.getWorkbenchContext(), selectionHolder);
		platformChooser.show();
		if (platformChooser.wasConfirmed()) {
			DatabasePlatform newPlatform = (DatabasePlatform) selectionHolder.getValue();
			this.getDatabase().setDatabasePlatform(newPlatform);
			this.preferences().put(ProjectCreationDialog.DATABASE_PLATFORM_PREFERENCE, newPlatform.getName());
		}
	}


	private JList buildLoginSpecListBox() {
		JList listBox = SwingComponentFactory.buildList(this.buildLoginSpecListModel());
		this.loginSpecSelectionModel = new ObjectListSelectionModel(listBox.getModel());
		listBox.setSelectionModel(this.loginSpecSelectionModel);
		listBox.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listBox.setDoubleBuffered(true);
		listBox.setCellRenderer(this.buildLoginSpecCellRenderer());
		return listBox;
	}
	
	private ListModel buildLoginSpecListModel() {
		return new ListModelAdapter(this.buildLoginSpecsNameAdapter());
	}

	private ListValueModel buildLoginSpecsNameAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildSortedLoginSpecsAdapter(), MWLoginSpec.NAME_PROPERTY);
	}

	private ListValueModel buildSortedLoginSpecsAdapter() {
		return new SortedListValueModelAdapter(this.buildLoginSpecsAdapter());
	}

	private CollectionValueModel buildLoginSpecsAdapter() {
		return new CollectionAspectAdapter(this.getSelectionHolder(), MWDatabase.LOGIN_SPECS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWDatabase) this.subject).loginSpecs();
			}
			protected int sizeFromSubject() {
				return ((MWDatabase) this.subject).loginSpecsSize();
			}
		};
	}

	private ListCellRenderer buildLoginSpecCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				// need null check for combo-box
				return (value == null) ? "" : ((MWLoginSpec) value).getName();
			}
		};
	}

	private PropertyValueModel buildSelectedLoginSpecHolder(ObjectListSelectionModel selectionModel) {
		PropertyValueModel loginSpecHolder = new SimplePropertyValueModel(null);
		selectionModel.addListSelectionListener(this.buildLoginSpecListSelectionListener(selectionModel, loginSpecHolder));
		return loginSpecHolder;
	}
	
	private ListSelectionListener buildLoginSpecListSelectionListener(final ObjectListSelectionModel loginSpecListSelectionModel, final PropertyValueModel loginSpecHolder) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				loginSpecHolder.setValue(loginSpecListSelectionModel.getSelectedValue());			
			}
		}; 
	}

	private ListSelectionListener buildLoginSpecListSelectionListener(final JButton button) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				button.setEnabled(DatabasePropertiesPage.this.loginSpecSelectionModel.getSelectedValue() != null);		
			}
		}; 
	}

	// ********** add login spec **********
	
	private ActionListener buildAddButtonActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DatabasePropertiesPage.this.addLoginSpec();
			}
		};
	}
	
	void addLoginSpec() {
		NewNameDialog dialog = this.buildLoginSpecDialog();
		dialog.show();

		if (dialog.wasCanceled()) {
			return;
		}

		String loginName = dialog.getNewName();
		MWLoginSpec loginSpec = this.getDatabase().addLoginSpec(loginName);
		
		this.loginSpecSelectionModel.setSelectedValue(loginSpec);
	}

	private NewNameDialog buildLoginSpecDialog() {
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setExistingNames(this.getDatabase().loginSpecNames());
		builder.setOriginalName(NameTools.uniqueNameFor(resourceRepository().getString("NEW_LOGIN_NAME"), this.getDatabase().loginSpecNames()));
		builder.setTextFieldDescription(resourceRepository().getString("NEW_LOGIN_DIALOG.message"));
		builder.setTitle(resourceRepository().getString("NEW_LOGIN_DIALOG.title"));
		builder.setHelpTopicId("dialog.newLogin");
		return builder.buildDialog(getWorkbenchContext());
	}

	
	// ********** remove login spec **********
	
	private ActionListener buildRemoveButtonActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DatabasePropertiesPage.this.removeLoginSpec();
			}
		};
	}
	
	void removeLoginSpec() {
		int selectedOption = JOptionPane.showConfirmDialog(this,
										resourceRepository().getString("REMOVE_LOGIN_INFO_DIALOG.message"),
										resourceRepository().getString("REMOVE_LOGIN_INFO_DIALOG.title"),
										JOptionPane.YES_NO_OPTION);
	
		if (selectedOption == JOptionPane.NO_OPTION) {
			return;
		}
	
		this.getDatabase().removeLoginSpec((MWLoginSpec) this.loginSpecSelectionModel.getSelectedValue());
	}

	
	// ********** rename login spec **********
	
	private ActionListener buildRenameButtonActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DatabasePropertiesPage.this.renameLoginSpec();
			}
		};
	}
	
	void renameLoginSpec() {
		MWLoginSpec login = (MWLoginSpec) this.loginSpecSelectionModel.getSelectedValue();

		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setExistingNames(this.getDatabase().loginSpecNames());
		builder.setOriginalName(login.getName());
		builder.setTextFieldDescription(resourceRepository().getString("RENAME_LOGIN_INFO_DIALOG.message"));
		builder.setTitle(resourceRepository().getString("RENAME_LOGIN_INFO_DIALOG.title", login.getName()));
		builder.setHelpTopicId("dialog.loginInfoRename");
		
		NewNameDialog dialog = builder.buildDialog(getWorkbenchContext());
		dialog.setVisible(true);

		if (dialog.wasConfirmed()) {
			login.setName(dialog.getNewName());
		}
	}

	
	// ********** development login **********

	private JComponent buildDevelopmentLoginWidgets() {
		return buildLabeledComboBox(
			"DEVELOPMENT_LOGIN_LABEL",
			new ComboBoxModelAdapter(this.buildSortedLoginsHolder(), this.buildDevelopmentLoginAdapter()),
			this.buildLoginSpecCellRenderer()
		);
	}

	private ListValueModel buildSortedLoginsHolder() {
		return new SortedListValueModelAdapter(this.buildAllLoginsAdapter());
	}

	private CollectionValueModel buildAllLoginsAdapter() {
		return new CollectionAspectAdapter(this.getSelectionHolder(), MWDatabase.LOGIN_SPECS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWDatabase) this.subject).loginSpecs();
			}
			protected int sizeFromSubject() {
				return ((MWDatabase) this.subject).loginSpecsSize();
			}
		};
	}

	private PropertyValueModel buildDevelopmentLoginAdapter() {
		PropertyValueModel adapter = new PropertyAspectAdapter(this.getSelectionHolder(), MWDatabase.DEVELOPMENT_LOGIN_SPEC_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDatabase) this.subject).getDevelopmentLoginSpec();
			}	
			protected void setValueOnSubject(Object value) {
				((MWDatabase) this.subject).setDevelopmentLoginSpec((MWLoginSpec) value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(adapter, MWLoginSpec.NAME_PROPERTY);
	}


	// ********** deployment login **********

	private JComponent buildDeploymentLoginWidgets() {
		return buildLabeledComboBox(
			"DEPLOYMENT_LOGIN_LABEL",
			new ComboBoxModelAdapter(this.buildAllLoginsAdapter(), this.buildDeploymentLoginAdapter()),
			this.buildLoginSpecCellRenderer()
		);
	}

	private PropertyValueModel buildDeploymentLoginAdapter() {
		PropertyValueModel adapter = new PropertyAspectAdapter(this.getSelectionHolder(), MWDatabase.DEPLOYMENT_LOGIN_SPEC_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDatabase) this.subject).getDeploymentLoginSpec();
			}	
			protected void setValueOnSubject(Object value) {
				((MWDatabase) this.subject).setDeploymentLoginSpec((MWLoginSpec) value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(adapter, MWLoginSpec.NAME_PROPERTY);
	}

}
