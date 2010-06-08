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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.EisPlatformManager;
import org.eclipse.persistence.tools.workbench.scplugin.model.ServerPlatformManager;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DataSource;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.NullServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatform;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;


public final class SessionCreationDialog extends AbstractValidatingDialog {
	
	private JTextField nameTextField;
	private Collection sessionNames;

	private JCheckBox useServerPlatformCheckBox;
	private ComboBoxModel serverPlatformComboBoxModel;
	private String previousServerClassName;

	private JRadioButton serverRadioButton;
	private JRadioButton databaseRadioButton;

	private JRadioButton relationalRadioButton;
	private JRadioButton xmlRadioButton;
	private JRadioButton eisRadioButton;

	private PropertyValueModel databasePlatformHolder;

	private ComboBoxModel j2cAdapterComboBoxModel;
     		

//	   ************ constructors / initialization ****************

	public SessionCreationDialog( WorkbenchContext context, Collection sessionNames) {
			super(context);
			this.sessionNames = sessionNames;
	}
     
	protected void initialize() {
			super.initialize();
			getOKAction().setEnabled( false);
	}

	protected Component buildMainPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		setTitle(resourceRepository().getString( "SESSION_CREATION_DIALOG_TITLE"));

		// Create the container
		JPanel mainPanel = new JPanel( new GridBagLayout());

		// Session Name
		JPanel namePanel = buildSessionNamePanel();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 5, 0, 5);

		mainPanel.add(namePanel, constraints);
		helpManager().addTopicID(namePanel, helpTopicId() + ".name");

		// Server Platform
		JPanel serverPlatformPanel = buildServerPlatformPanel();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		helpManager().addTopicID(serverPlatformPanel, helpTopicId() + ".serverPlatform");
		mainPanel.add(serverPlatformPanel, constraints);

		// Data Source
		JPanel dataSourcePanel = buildDataSourcePanel();

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		helpManager().addTopicID(dataSourcePanel, helpTopicId() + ".datasource");
		mainPanel.add(dataSourcePanel, constraints);

		// Session Type
		JPanel sessionTypePanel = buildSessionTypePanelPanel();

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		mainPanel.add( sessionTypePanel, constraints);

		return mainPanel;
	}

	private JPanel buildSessionTypePanelPanel() {
		
		GridBagConstraints constraints = new GridBagConstraints();		
		ButtonGroup buttonGroup = new ButtonGroup();

		// Create the container
		JPanel sessionTypePanel = new JPanel( new GridBagLayout());
		sessionTypePanel.setBorder
		(
			BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(resourceRepository().getString("SESSION_TYPE_LABEL")),
														  BorderFactory.createEmptyBorder(0, 5, 5, 5))
		);

		// Server radio button
		this.serverRadioButton = new JRadioButton();
		this.serverRadioButton.setSelected( true);
		this.serverRadioButton.setText(resourceRepository().getString( "SERVER_SESSION_RADIO_BUTTON"));
		this.serverRadioButton.setMnemonic(resourceRepository().getMnemonic( "SERVER_SESSION_RADIO_BUTTON"));
		this.serverRadioButton.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex( "SERVER_SESSION_RADIO_BUTTON"));
     
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets( 0, 0, 0, 0);

		sessionTypePanel.add( this.serverRadioButton, constraints);
		buttonGroup.add( this.serverRadioButton);
 
		// Database radio button
		this.databaseRadioButton = new JRadioButton();
		this.databaseRadioButton.setText(resourceRepository().getString( "DATABASE_SESSION_RADIO_BUTTON"));
		this.databaseRadioButton.setMnemonic(resourceRepository().getMnemonic( "DATABASE_SESSION_RADIO_BUTTON"));
		this.databaseRadioButton.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex( "DATABASE_SESSION_RADIO_BUTTON"));

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets( 0, 0, 0, 0);

		sessionTypePanel.add( this.databaseRadioButton, constraints);
		buttonGroup.add( this.databaseRadioButton);

		return sessionTypePanel;
	}
	
	private ActionListener buildServerPlatformAction() {

		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean checked = ((JCheckBox) e.getSource()).isSelected();
				String serverClassName;

				if (!checked) {
					SessionCreationDialog.this.previousServerClassName = (String) SessionCreationDialog.this.serverPlatformComboBoxModel.getSelectedItem();

					serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
					serverClassName = ClassTools.shortNameForClassNamed(serverClassName);
				}
				else {
					if (SessionCreationDialog.this.previousServerClassName == null) {
						SessionCreationDialog.this.previousServerClassName = SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT;
					}
					serverClassName = SessionCreationDialog.this.previousServerClassName;
				}

				SessionCreationDialog.this.serverPlatformComboBoxModel.setSelectedItem(serverClassName);
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
		constraints.insets     = new Insets(0, SwingTools.checkBoxIconWidth(), 0, 0);

		container.add(platformLabel, constraints);

		// Platform combo box
		this.serverPlatformComboBoxModel = buildServerPlatformComboBoxModel();
		JComboBox serverPlatformComboBox = new JComboBox(this.serverPlatformComboBoxModel);
		serverPlatformComboBox.setRenderer(buildServerPlatformRenderer());

		String platformName = preferences().get(SCPlugin.SERVER_PLATFORM_PREFERENCE, null);

		if ("NoServerPlatform".equals(platformName))
			serverPlatformComboBox.setSelectedItem(null);
		else
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

	private ListCellRenderer buildServerPlatformRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				if (value == null)
					return "";

				return resourceRepository().getString((String) value);
			}
		};
	}

	private JPanel buildSessionNamePanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel namePanel = new JPanel(new GridBagLayout());

		// Session label
		JLabel sessionNameLabel = SwingComponentFactory.buildLabel("SESSION_NAME_LABEL", resourceRepository());

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		namePanel.add(sessionNameLabel, constraints);

		// Session text field
		this.nameTextField = new JTextField(20);

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		namePanel.add(this.nameTextField, constraints);
		sessionNameLabel.setLabelFor(this.nameTextField);

		this.nameTextField.getDocument().addDocumentListener(
		   new DocumentListener() {
			   public void insertUpdate(DocumentEvent e) {
				   updateOKAction();
			   }
			   public void removeUpdate(DocumentEvent e) {
				   updateOKAction();
			   }
			   public void changedUpdate(DocumentEvent e) {
				   updateOKAction();
			   }
		   }
		);

		return namePanel;
	}
	
	private JPanel buildDataSourcePanel() {

		GridBagConstraints constraints = new GridBagConstraints();		
 		ButtonGroup buttonGroup = new ButtonGroup();
 		int offset = SwingTools.checkBoxIconWidth();

 		// Create the container
		JPanel datasourcePanel = new JPanel(new GridBagLayout());
		datasourcePanel.setBorder
		(
			BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(resourceRepository().getString("DATASOURCE_LABEL")),
														  BorderFactory.createEmptyBorder(0, 5, 5, 5))
		);

		// Relation choice
 		this.relationalRadioButton = new JRadioButton();
		this.relationalRadioButton.setText(resourceRepository().getString("RELATIONAL_RADIO_BUTTON"));
		this.relationalRadioButton.setMnemonic(resourceRepository().getMnemonic("RELATIONAL_RADIO_BUTTON"));
		this.relationalRadioButton.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("RELATIONAL_RADIO_BUTTON"));
  
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		datasourcePanel.add(this.relationalRadioButton, constraints);
		buttonGroup.add(this.relationalRadioButton);
 
		// Relational combo box
		JPanel relationalPanel = buildRelationalPanel();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, offset, 0, 0);		

		datasourcePanel.add(relationalPanel, constraints);

		// EIS choice
		this.eisRadioButton = new JRadioButton();
		this.eisRadioButton.setText(resourceRepository().getString("EIS_RADIO_BUTTON"));
		this.eisRadioButton.setMnemonic(resourceRepository().getMnemonic("EIS_RADIO_BUTTON"));
		this.eisRadioButton.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("EIS_RADIO_BUTTON"));

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		datasourcePanel.add(this.eisRadioButton, constraints);
		buttonGroup.add(this.eisRadioButton);
 
		// EIS combo box
		JPanel eisPanel = buildEisPanel();

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
		constraints.insets     = new Insets(0, offset, 0, 0);		

		datasourcePanel.add(eisPanel, constraints);

		// XML choice
		this.xmlRadioButton = new JRadioButton();
		this.xmlRadioButton.setText(resourceRepository().getString("XML_RADIO_BUTTON"));
		this.xmlRadioButton.setMnemonic(resourceRepository().getMnemonic("XML_RADIO_BUTTON"));
		this.xmlRadioButton.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("XML_RADIO_BUTTON"));

		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		datasourcePanel.add(this.xmlRadioButton, constraints);
		buttonGroup.add(this.xmlRadioButton);

		this.xmlRadioButton.addChangeListener( new ChangeListener() {
		   public void stateChanged( ChangeEvent e) {
				SessionCreationDialog.this.databaseRadioButton.setSelected( true);
				SessionCreationDialog.this.serverRadioButton.setEnabled( !SessionCreationDialog.this.xmlRadioButton.isSelected());
		   }
		});
		
		return datasourcePanel;
	}
	
	private JPanel buildRelationalPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
 
		JPanel relationalPanel = new AccessibleTitledPanel(new GridBagLayout());
 
		// Database Platform label
		final JLabel platformLabel = SwingComponentFactory.buildLabel("DATABASE_PLATFORM_LABEL", resourceRepository());
		platformLabel.setEnabled(false);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		relationalPanel.add(platformLabel, constraints);

		// Database Platform drop-down
		String platformName = preferences().get(SCPlugin.DATABASE_PLATFORM_PREFERENCE, SCPlugin.DATABASE_PLATFORM_PREFERENCE_DEFAULT);
		this.databasePlatformHolder = new SimplePropertyValueModel(DatabasePlatformRepository.getDefault().platformNamed(platformName));
		final JComboBox databasePlatformComboBox = PlatformComponentFactory.buildPlatformChooser(this.databasePlatformHolder);
		databasePlatformComboBox.setEnabled(false);

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		relationalPanel.add(databasePlatformComboBox, constraints);
		platformLabel.setLabelFor(databasePlatformComboBox);

		this.relationalRadioButton.addChangeListener(new ChangeListener() {
		   public void stateChanged(ChangeEvent e) {
			   databasePlatformComboBox.setEnabled(SessionCreationDialog.this.relationalRadioButton.isSelected());
			   platformLabel.setEnabled(SessionCreationDialog.this.relationalRadioButton.isSelected());
		   }
		});
 
		return relationalPanel;
	}

	private JPanel buildEisPanel() {
		GridBagConstraints constraints = new GridBagConstraints();
 
		JPanel eisPanel = new AccessibleTitledPanel(new GridBagLayout());
 
		// EIS label
		final JLabel adapterLabel = new JLabel(resourceRepository().getString("EIS_PLATFORM_LABEL"));
		adapterLabel.setEnabled(false);
		adapterLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("EIS_PLATFORM_LABEL"));
		adapterLabel.setDisplayedMnemonicIndex(resourceRepository().getMnemonicIndex("EIS_PLATFORM_LABEL"));

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		eisPanel.add(adapterLabel, constraints);

		// EIS platform drop-down
		this.j2cAdapterComboBoxModel = buildJ2CAdaptersComboBoxModel();
		final JComboBox adapterComboBox = new JComboBox(this.j2cAdapterComboBoxModel);
		adapterComboBox.setEnabled(false);

		String platformName = preferences().get(SCPlugin.EIS_PLATFORM_PREFERENCE, SCPlugin.EIS_PLATFORM_PREFERENCE_DEFAULT);
		adapterComboBox.setSelectedItem(platformName);

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		eisPanel.add(adapterComboBox, constraints);
		adapterLabel.setLabelFor(adapterComboBox);

		this.eisRadioButton.addChangeListener(new ChangeListener() {
		   public void stateChanged(ChangeEvent e) {
			   adapterComboBox.setEnabled(SessionCreationDialog.this.eisRadioButton.isSelected());
			   adapterLabel.setEnabled(SessionCreationDialog.this.eisRadioButton.isSelected());
		   }
		});
		return eisPanel;
	}

	private void updateOKAction() {
		String sessionName = this.nameTextField.getText().trim();
		boolean valid = (sessionName.length() > 0) &&
							 !this.sessionNames.contains(sessionName);

		getOKAction().setEnabled(valid);

		if (valid)
			clearErrorMessage();
		else
			setErrorMessageKey("SESSION_CREATION_DIALOG_INVALID_NAME");
	}

	private ComboBoxModel buildJ2CAdaptersComboBoxModel() {
			return new DefaultComboBoxModel(CollectionTools.vector(supportedJ2CAdapters()));
	}
     
	private ComboBoxModel buildServerPlatformComboBoxModel() {
		return new DefaultComboBoxModel(CollectionTools.vector(serverPlatforms()));
	}

	private Iterator serverPlatforms() {
		Collection servers = CollectionTools.sortedSet(ServerPlatformManager.instance().platformShortNames());
		String serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
		servers.remove(ClassTools.shortNameForClassNamed(serverClassName));
		return servers.iterator();
	}

	public SessionAdapter addNewSessionTo( TopLinkSessionsAdapter topLinkSessions) {
		SessionAdapter session;
		DataSource ds;
		String sessionName = this.nameTextField.getText().trim();

		// Data Source
		if( this.relationalRadioButton.isSelected()) {
		    ds = new DataSource(( DatabasePlatform)this.databasePlatformHolder.getValue());
		}
		else if( this.eisRadioButton.isSelected()) {

		    ds = new DataSource(( String)this.j2cAdapterComboBoxModel.getSelectedItem());
		}
		else {
		    
		    ds = DataSource.buildXmlDataSource();
		}	

		// Server Platform
		ServerPlatform sp;

		if( this.useServerPlatformCheckBox.isSelected()) {
			sp = new ServerPlatform(( String)this.serverPlatformComboBoxModel.getSelectedItem()); 
		}
		else {
			String serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
			sp = new ServerPlatform( ClassTools.shortNameForClassNamed( serverClassName));
		}

		// Create the session
		if( this.serverRadioButton.isSelected()) {
			session = topLinkSessions.addServerSessionNamed( sessionName, sp, ds);
		}
		else {
			session = topLinkSessions.addDatabaseSessionNamed( sessionName, sp, ds);
		}		

		return session;
	}
     
	private Iterator supportedJ2CAdapters() {
		Collection platforms = CollectionTools.sortedSet(EisPlatformManager.instance().platformDisplayNames());
		return platforms.iterator();
	}

//	   ********** opening **********

	private String buildUniqueSessionName() {
		String sessionName = preferences().get(SCPlugin.NEW_NAME_SESSION_PREFERENCE, resourceRepository().getString("SESSION_CREATION_DIALOG_NEW_SESSION_NAME"));
		return NameTools.uniqueNameFor(sessionName, this.sessionNames);
	}

	protected String helpTopicId() {
		return "dialog.sessionCreation";
	}

	protected Component initialFocusComponent() {
		return this.nameTextField;
	}

	protected void prepareToShow() {
		super.prepareToShow();
		this.nameTextField.setText(buildUniqueSessionName());
		this.nameTextField.selectAll();

 		String dataSourceType = preferences().get(SCPlugin.DATA_SOURCE_TYPE_PREFERENCE, SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_RELATIONAL_CHOICE);
		this.relationalRadioButton.setSelected(SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_RELATIONAL_CHOICE.equals(dataSourceType));
		this.eisRadioButton.setSelected(SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_EIS_CHOICE.equals(dataSourceType));
		this.xmlRadioButton.setSelected(SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_XML_CHOICE.equals(dataSourceType));
	}
}
