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
package org.eclipse.persistence.tools.workbench.scplugin.ui.preferences;

// JDK
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.PlatformComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.EisPlatformManager;
import org.eclipse.persistence.tools.workbench.scplugin.model.ServerPlatformManager;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.NullServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


/**
 * Preferences page for SC platform settings used by the Sessions Configuration.
 *
 * @author Pascal Filion
 * @version 10.1.3
 */
final class PlatformPreferencesPage extends AbstractPanel
{
	private String previousServerClassName;
	private JComboBox serverPlatformChooser;
	private BufferedPropertyValueModel serverPlatformHolder;
	private JComponent serverPlatformWidgets;
	private JCheckBox useServerPlatformCheckBox;

	PlatformPreferencesPage(PreferencesContext context)
	{
		super(new BorderLayout(), context);
		intializeLayout();
		addHelpTopicId(this, "preferences.sessions.platform");
	}

	private void buildChooserComponentEnabler(JRadioButton radioButton,
															JComponent widgets)
	{
		final SimplePropertyValueModel booleanHolder =
			new SimplePropertyValueModel(Boolean.valueOf(radioButton.isSelected()));

		radioButton.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				JRadioButton radioButton = (JRadioButton) e.getSource();
				Object value = Boolean.valueOf(radioButton.isSelected());
				booleanHolder.setValue(value);
			}
		});

		new ComponentEnabler(booleanHolder, Collections.singleton(widgets));
	}

	private ListDataListener buildComboBoxListDataListener()
	{
		return new ListDataListener()
		{
			public void contentsChanged(ListDataEvent e)
			{
				if ((e.getIndex0() == -1) && (e.getIndex1() == -1))
				{
					String serverClassName = (String) PlatformPreferencesPage.this.serverPlatformChooser.getSelectedItem();
					updateServerPlatformWidgets(serverClassName);
				}
			}

			public void intervalAdded(ListDataEvent e)
			{
			}

			public void intervalRemoved(ListDataEvent e)
			{
			}
		};
	}

	private PropertyValueModel buildDatabaseTypeHolder()
	{
		return new BufferedPropertyValueModel
		(
			buildDatabaseTypeHolderImp(),
			getPreferencesContext().getBufferTrigger()
		);
	}

	private PropertyValueModel buildDatabaseTypeHolderImp()
	{
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel
		(
			preferences(),
			SCPlugin.DATA_SOURCE_TYPE_PREFERENCE,
			SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_RELATIONAL_CHOICE
		);

		adapter.setConverter(new BidiStringConverter()
		{
			public Object convertToObject(String value)
			{
				return convertToString(value);
			}

			public String convertToString(Object value)
			{
				if (SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_RELATIONAL_CHOICE.equals(value))
					return SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_RELATIONAL_CHOICE;

				if (SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_EIS_CHOICE.equals(value))
					return SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_EIS_CHOICE;

				if (SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_XML_CHOICE.equals(value))
					return SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_XML_CHOICE;

				return (value != null) ? value.toString() : null;
			}
		});

		return adapter;
	}

	private JComponent buildDefaultDatabaseSourceTypePane()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		PropertyValueModel databaseTypeHolder = buildDatabaseTypeHolder();
		int offset = SwingTools.checkBoxIconWidth();

		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("PREFERENCES_DEFAULT_DATA_SOURCE_TYPE_GROUP_BOX"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		// Relational choice
		JRadioButton relationalRadioButton = buildRadioButton
		(
			"RELATIONAL_RADIO_BUTTON",
			buildRelationalButtonModelAdapter(databaseTypeHolder)
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(relationalRadioButton, constraints);

		// Relational chooser
		JComponent relationalChooserWidgets = buildRelationalPlatformWidgets();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, offset, 0, 0);

		container.add(relationalChooserWidgets, constraints);
		buildChooserComponentEnabler(relationalRadioButton, relationalChooserWidgets);

		// EIS choice
		JRadioButton eisRadioButton = buildRadioButton
		(
			"EIS_RADIO_BUTTON",
			buildEisButtonModelAdapter(databaseTypeHolder)
		);

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		container.add(eisRadioButton, constraints);

		// Eis chooser
		JComponent eisChooserWidgets = buildEisPlatformWidgets();

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, offset, 0, 0);

		container.add(eisChooserWidgets, constraints);
		buildChooserComponentEnabler(eisRadioButton, eisChooserWidgets);

		// XML choice
		JRadioButton xmlRadioButton = buildRadioButton
		(
			"XML_RADIO_BUTTON",
			buildXmlButtonModelAdapter(databaseTypeHolder)
		);

		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		container.add(xmlRadioButton, constraints);

		return container;
	}

	private JPanel buildDefaultServerPlatformPane()
	{
		// Use Server Platform check box
		this.useServerPlatformCheckBox = buildCheckBox
		(
			"USE_SERVER_PLATFORM_CHECK_BOX",
			new DefaultButtonModel()
		);
		this.useServerPlatformCheckBox.addActionListener(buildServerPlatformAction());

		// Platform chooser
		this.serverPlatformChooser = new JComboBox(buildServerPlatformComboBoxModel());
		this.serverPlatformChooser.setRenderer(buildServerPlatformRenderer());
		this.serverPlatformChooser.getModel().addListDataListener(buildComboBoxListDataListener());

		// Plaftorm widgets
		this.serverPlatformWidgets = buildLabeledComponent
		(
			"PREFERENCES_DEFAULT_SERVER_PLATFORM_COMBO_BOX",
			this.serverPlatformChooser
		);
		this.serverPlatformWidgets.setBorder(BorderFactory.createEmptyBorder(0, SwingTools.checkBoxIconWidth(), 0, 0));

		// Make sure the widgets are up to date based on the selected item since
		// the listener was added after the ComboBoxModel was created
		String serverClassName = (String) this.serverPlatformChooser.getSelectedItem();
		updateServerPlatformWidgets(serverClassName);

		return new GroupBox(this.useServerPlatformCheckBox, this.serverPlatformWidgets);
	}

	private ButtonModel buildEisButtonModelAdapter(PropertyValueModel databaseTypeHolder)
	{
		return new RadioButtonModelAdapter
		(
			databaseTypeHolder,
			SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_EIS_CHOICE
		);
	}

	private ListCellRenderer buildEisNameRenderer()
	{
		return new SimpleListCellRenderer()
		{
			protected String buildText(Object cellValue)
			{
				if (((String) cellValue).equals("AQPlatform"))
				{
					return EisPlatformManager.AQ_ID;
				}
				else if (((String)cellValue).equals("JMSPlatform"))
				{
					return EisPlatformManager.JMS_ID;
				}
				else if (((String)cellValue).equals("MQPlatform"))
				{
					return EisPlatformManager.MQ_ID;
				} 
				else 
				{
					return EisPlatformManager.XML_ID;
				}
			}
		};
	}

	private ComboBoxModel buildEisPlatformCollectionHolder()
	{
	    Collection platforms = CollectionTools.sortedSet(EisPlatformManager.instance().platformShortNames());
		
		return new ComboBoxModelAdapter
		(
			new SimpleCollectionValueModel(platforms), 
			buildEisPlatformHolder()
		);
	}

	private PropertyValueModel buildEisPlatformHolder()
	{
		return new BufferedPropertyValueModel
		(
			buildEisPlatformHolderImp(),
			getPreferencesContext().getBufferTrigger()
		);
	}

	private PropertyValueModel buildEisPlatformHolderImp()
	{
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel
		(
			preferences(),
			SCPlugin.EIS_PLATFORM_PREFERENCE,
			SCPlugin.EIS_PLATFORM_PREFERENCE_DEFAULT
		);

		adapter.setConverter(BidiStringConverter.DEFAULT_INSTANCE);

		return adapter;
	}

	private JComponent buildEisPlatformWidgets()
	{
		JComboBox eisPlatformChooser = new JComboBox(buildEisPlatformCollectionHolder());
		eisPlatformChooser.setRenderer(buildEisNameRenderer());

		return buildLabeledComponent
		(
			"EIS_PLATFORM_LABEL",
			eisPlatformChooser
		);
	}

	private ButtonModel buildRelationalButtonModelAdapter(PropertyValueModel databaseTypeHolder)
	{
		return new RadioButtonModelAdapter
		(
			databaseTypeHolder,
			SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_RELATIONAL_CHOICE
		);
	}

	private PropertyValueModel buildRelationalPlatformHolder()
	{
		return new BufferedPropertyValueModel
		(
			buildRelationalPlatformHolderImp(),
			getPreferencesContext().getBufferTrigger()
		);
	}

	private PropertyValueModel buildRelationalPlatformHolderImp()
	{
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel
		(
			preferences(),
			SCPlugin.DATABASE_PLATFORM_PREFERENCE,
			DatabasePlatformRepository.getDefault().platformNamed(SCPlugin.DATABASE_PLATFORM_PREFERENCE_DEFAULT)
		);

		adapter.setConverter(new BidiStringConverter()
		{
			public Object convertToObject(String value)
			{
				if (value == null)
					return null;

				return DatabasePlatformRepository.getDefault().platformNamed(value);
			}

			public String convertToString(Object value)
			{
				if (value == null)
					return null;

				DatabasePlatform platform = (DatabasePlatform) value;
				return platform.getName();
			}
		});

		return adapter;
	}

	private JComponent buildRelationalPlatformWidgets()
	{
		PropertyValueModel databasePlatformHolder = buildRelationalPlatformHolder();
		JComboBox databasePlatformComboBox = PlatformComponentFactory.buildPlatformChooser(databasePlatformHolder);

		return buildLabeledComponent
		(
			"DATABASE_PLATFORM_LABEL",
			databasePlatformComboBox
		);
	}

	private ActionListener buildServerPlatformAction()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				boolean checked = ((JCheckBox) e.getSource()).isSelected();
				String serverClassName;

				if (checked)
				{
					PlatformPreferencesPage.this.previousServerClassName = (String) PlatformPreferencesPage.this.serverPlatformHolder.getValue();

					serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
					serverClassName = ClassTools.shortNameForClassNamed(serverClassName);
				}
				else
				{
					if (PlatformPreferencesPage.this.previousServerClassName == null)
					{
						PlatformPreferencesPage.this.previousServerClassName = SCPlugin.SERVER_PLATFORM_PREFERENCE_DEFAULT;
					}

					serverClassName = PlatformPreferencesPage.this.previousServerClassName;
				}

				PlatformPreferencesPage.this.serverPlatformHolder.setValue(serverClassName);
			}
		};
	}

	private CollectionValueModel buildServerPlatformCollectionHolder()
	{
		return new SimpleCollectionValueModel(buildServerPlatformNames());
	}

	private ComboBoxModel buildServerPlatformComboBoxModel()
	{
		return new ComboBoxModelAdapter(buildServerPlatformCollectionHolder(),
												  buildServerPlatformSelectionHolder());
	}

	private Collection buildServerPlatformNames()
	{
		Collection servers = CollectionTools.sortedSet(ServerPlatformManager.instance().platformShortNames());
		String serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
		servers.remove(ClassTools.shortNameForClassNamed(serverClassName));
		return servers;
	}

	private ListCellRenderer buildServerPlatformRenderer()
	{
		return new SimpleListCellRenderer()
		{
			protected String buildText(Object value)
			{
				if (value == null)
					return "";

				return resourceRepository().getString((String) value);
			}
		};
	}

	private PropertyValueModel buildServerPlatformSelectionHolder()
	{
		if (this.serverPlatformHolder == null)
		{
			this.serverPlatformHolder = new BufferedPropertyValueModel
			(
				buildServerPlatformSelectionHolderImp(),
				getPreferencesContext().getBufferTrigger()
			);
		}

		return this.serverPlatformHolder;
	}

	private PropertyValueModel buildServerPlatformSelectionHolderImp()
	{
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel
		(
			preferences(),
			SCPlugin.SERVER_PLATFORM_PREFERENCE
		);

		adapter.setConverter(BidiStringConverter.DEFAULT_INSTANCE);

		return adapter;
	}

	private ButtonModel buildXmlButtonModelAdapter(PropertyValueModel databaseTypeHolder)
	{
		return new RadioButtonModelAdapter
		(
			databaseTypeHolder,
			SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_XML_CHOICE
		);
	}

	private void intializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel container = new JPanel(new GridBagLayout());

		JScrollPane scrollPane = new JScrollPane(container);
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);

		add(scrollPane, BorderLayout.CENTER);

		// Default Server Platform
		JComponent defaultServerPlatformPane = buildDefaultServerPlatformPane();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 5, 0, 5);

		container.add(defaultServerPlatformPane, constraints);

		// Default Data Source Type
		JComponent defaultDataSourceTypePane = buildDefaultDatabaseSourceTypePane();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 5, 5, 5);

		container.add(defaultDataSourceTypePane, constraints);
	}

	private void updateServerPlatformWidgets(String serverClassName)
	{
		boolean selected = (serverClassName != null);

		if (selected)
		{
			ServerPlatformAdapter noServerPlatform = NullServerPlatformAdapter.instance();
			String noServerPlatformClassName = ClassTools.shortNameForClassNamed(noServerPlatform.getServerClassName());
			selected = !serverClassName.equals(noServerPlatformClassName);
		}

		this.useServerPlatformCheckBox.setSelected(selected);
		this.serverPlatformWidgets.setEnabled(selected);
	}
}
