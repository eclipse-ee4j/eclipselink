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
package org.eclipse.persistence.tools.workbench.scplugin.ui.login;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.text.Document;

import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.BooleanCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.Transformer;

/**
 * This page shows the information regarding the Database login which is
 * specific for {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter DatabaseLoginAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * ---------------------------------------------
 * |                    ---------------------- |  --------------------
 * | Database Driver:   |                  |v| |<-| Driver Manager   |
 * |                    ---------------------- |  | J2EE Data Source |
 * | -PanelSwitcher--------------------------- |  --------------------
 * | |                                       | |
 * | | Driver Manager widgets                | |
 * | |  or                                   | |
 * | | J2EE Data Source widgets              | |
 * | |                                       | |
 * | ----------------------------------------- |
 * |                      -------------------- |
 * | Username (Optional): | I                | |
 * |                      -------------------- |
 * |                      -------------------- |
 * | Password (Optional): | I                | |
 * |                      -------------------- |
 * ---------------------------------------------</pre>
 *
 * Known containers of this pane:<br>
 * - {@link RdbmsConnectionPropertiesPage}
 * - {@link RdbmsPoolLoginPropertiesPage}
 *
 * @see DatabaseLoginAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
abstract class AbstractRdbmsLoginPane extends AbstractLoginPane
{
	/**
	 * The sub-pane containing the Database Source widgets.
	 */
	private JComponent databaseSourcePane;

	/**
	 * The sub-pane containing the Driver Manager widgets.
	 */
	private JComponent driverManagerPane;

	/**
	 * Creates a new <code>AbstractRdbmsLoginPane</code>.
	 *
	 * @param subjectHolder The holder of {@link DatabaseLoginAdapter}
	 * @param context The plug-in context to be used, such as <code>ResourceRepository</code>
	 */
	public AbstractRdbmsLoginPane(PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing the actual items
	 * to be shown in the Clustering combo box.
	 *
	 * @return The <code>CollectionValueModel</code> containing the items
	 */
	private CollectionValueModel buildDatabaseDriverCollectionHolder()
	{
		Vector booleanValues = new Vector();
		booleanValues.add(Boolean.TRUE);
		booleanValues.add(Boolean.FALSE);

		return new ReadOnlyCollectionValueModel(booleanValues);
	}

	/**
	 * Creates the <code>ComboBoxModel</code> that keeps the selected item in the
	 * combo box in sync with the value in the model and vice versa.
	 *
	 * @return The model showing two choices: "Driver Manager (True)" and "J2EE
	 * Data Source (False)"
	 */
	private ComboBoxModel buildDatabaseDriverComboBoxAdapter()
	{
		return new ComboBoxModelAdapter(buildDatabaseDriverCollectionHolder(),
												  buildDatabaseDriverSelectionHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Database Driver property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildDatabaseDriverHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), DatabaseLoginAdapter.USE_DRIVER_MANAGER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.databaseDriverIsDriverManager());
			}
		};
	}

	/**
	 * Creates the decorator responsible to format the <code>Boolean</code>
	 * values in the Clustering combo box.
	 * 
	 * @return {@link SessionClusteringPropertiesPage.BooleanLabelDecorator}
	 */
	private CellRendererAdapter buildDatabaseDriverLabelDecorator()
	{
		ResourceRepository resourceRepository = resourceRepository();

		return new BooleanCellRendererAdapter(resourceRepository.getString("CONNECTION_RDBMS_DRIVER_MANAGER_CHOICE"),
													resourceRepository.getString("CONNECTION_RDBMS_DATA_SOURCE_CHOICE"));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to listen to
	 * changes made to the type of Database Driver to be used, which is either
	 * Driver Manager or J2EE Data Source.
	 *
	 * @return {@link PropertyAspectAdapter}
	 */
	protected abstract PropertyValueModel buildDatabaseDriverSelectionHolder();

	/**
	 * Creates the <code>SwitcherPanel</code> ...
	 *
	 * @return A new <code>SwitcherPanel</code>
	 */
	private SwitcherPanel buildDatabaseDriverSwitcherPanel()
	{
		getDriverManagerPane();
		getDataSourcePane();

		return new SwitcherPanel(buildDatabaseDriverHolder(),
										 buildDatabaseDriverTransformer());
	}

	/**
	 * Creates the <code>Transformer</code> responsible to convert the Clustering
	 * type into the corresponding <code>Component</code>.
	 *
	 * @return A new <code>Transformer</code>
	 */
	private Transformer buildDatabaseDriverTransformer()
	{
		return new Transformer()
		{
			public Object transform(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject();

				if ((value == null) || (login == null))
					return null;

				if (Boolean.TRUE.equals(value))
					return getDriverManagerPane();

				return getDataSourcePane();
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Data Source Name value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildDataSourceNameDocumentAdapter()
	{
		return new DocumentAdapter(buildDataSourceNameHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Data Source Name property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildDataSourceNameHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), DatabaseLoginAdapter.DATA_SOURCE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				return adapter.getDataSourceName();
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				adapter.setDataSourceName((String) value);
			}
		};
	}

	private JComponent buildDataSourcePane()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		Pane panel = new Pane(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		JComponent lookupTypeWidgets = buildLabeledComboBox
		(
			"CONNECTION_RDBMS_LOOKUP_TYPE_FIELD",
			buildLookupTypeComboModel(),
			buildLookupTypeCellRenderer()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(lookupTypeWidgets, constraints);

		JComponent nameWidgets = buildLabeledTextField
		(
			"CONNECTION_RDBMS_DATA_SOURCE_FIELD",
		   buildDataSourceNameDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(nameWidgets, constraints);

		return panel;
	}

	/**
	 * Creates a Browse button that will take care to show the class chooser.
	 *
	 * @return A new <code>JButton</code>
	 */
	private JButton buildDriverClassBrowseButton()
	{
		return ClassChooserTools.buildBrowseButton
		(
			getWorkbenchContextHolder(),
			"CONNECTION_RDBMS_DRIVER_CLASS_BROWSE_BUTTON",
			buildClassRepositoryHolder(),
			buildDriverClassHolder()
		);
	}

	/**
	 * Creates the <code>ListValueModel</code> containing all the items to be
	 * shown in the Driver Class combo box.
	 *
	 * @return A new <code>ListValueModel</code>
	 */
	private ListValueModel buildDriverClassCollectionHolder()
	{
		Collection driverClasses = CollectionTools.collection(DatabaseLoginAdapter.driverClasses());
		ReadOnlyCollectionValueModel collectionHolder = new ReadOnlyCollectionValueModel(driverClasses);
		return new SortedListValueModelAdapter(collectionHolder);
	}

	/**
	 * Creates the <code>ComboBoxModel</code> that keeps the value from the combo
	 * box in sync with the Driver Class value in the model and vice versa.
	 *
	 * @return A new <code>ComboBoxModel</code>
	 */
	private ComboBoxModel buildDriverClassComboAdapter()
	{
		return new ComboBoxModelAdapter(buildDriverClassCollectionHolder(),
												  buildDriverClassHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Driver Class property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildDriverClassHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), DatabaseLoginAdapter.DRIVER_CLASS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				return adapter.getDriverClassName();
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				adapter.setDriverClassName((String) value);
			}
		};
	}

	/**
	 * Creates the sub-pane that contains the widgets that edit the Driver
	 * Manager specific information.
	 *
	 * @return The initialized sub-pane
	 */
	private JComponent buildDriverManagerPane()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		Pane panel = new Pane(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		// Driver Class widgets
		JComboBox driverClassComboBox = new JComboBox(buildDriverClassComboAdapter());
		driverClassComboBox.setEditable(true);
		driverClassComboBox.setPrototypeDisplayValue("m");

		JTextField textField = (JTextField) driverClassComboBox.getEditor().getEditorComponent();
		textField.setDocument(RegexpDocument.buildDocument(RegexpDocument.RE_FULLY_QUALIFIED_CLASS_NAME));

		// Driver Class browse button
		Component driverClassWidgets = buildLabeledComponent
		(
			"CONNECTION_RDBMS_DRIVER_CLASS_COMBO_BOX",
			driverClassComboBox,
			buildDriverClassBrowseButton()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(driverClassWidgets, constraints);

		// Driver URL combo box
		Component driverURLWidgets = buildLabeledEditableComboBox
		(
			"CONNECTION_RDBMS_DRIVER_URL_COMBO_BOX",
			buildDriverURLComboAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(driverURLWidgets, constraints);

		return panel;
	}

	/**
	 * Creates the <code>ListValueModel</code> containing all the items to
	 * be shown in the Driver URL combo box.
	 *
	 * @return A new <code>ListValueModel</code>
	 */
	private ListValueModel buildDriverURLCollectionHolder()
	{
		return new SortedListValueModelAdapter
		(
			new CollectionAspectAdapter(buildDriverClassHolder())
			{
				protected Iterator getValueFromSubject()
				{
					return DatabaseLoginAdapter.driverURLs((String) subject);
				}
			}
		);
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * combo box in sync with the Driver URL value in the model and vice versa.
	 *
	 * @return A new <code>ComboBoxModel</code>
	 */
	private ComboBoxModel buildDriverURLComboAdapter()
	{
		return new ComboBoxModelAdapter(buildDriverURLCollectionHolder(),
												  buildDriverURLHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Driver URL property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildDriverURLHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), DatabaseLoginAdapter.CONNECTION_URL_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return login.getConnectionURL();
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setConnectionURL((String) value);
			}
		};
	}

	private ListCellRenderer buildLookupTypeCellRenderer()
	{
		return new AdaptableListCellRenderer(new AbstractCellRendererAdapter()
		{
			public String buildText(Object value)
			{
				LookupType lookupType = (LookupType) value;
				return lookupType.displayString();
			}
		});
	}

	private ListValueModel buildLookupTypeCollectionHolder(List lookupChoices)
	{
		return new ReadOnlyListValueModel(lookupChoices);
	}

	private ComboBoxModel buildLookupTypeComboModel()
	{
		LookupType string = new LookupType
		(
			resourceRepository().getString("CONNECTION_RDBMS_LOOKUP_TYPE_STRING_CHOICE"),
			new Integer(JNDIConnector.STRING_LOOKUP)
		);
		LookupType compound = new LookupType
		(
			resourceRepository().getString("CONNECTION_RDBMS_LOOKUP_TYPE_COMPOUND_NAME_CHOICE"),
			new Integer(JNDIConnector.COMPOUND_NAME_LOOKUP)
		);
		LookupType composite = new LookupType
		(
			resourceRepository().getString("CONNECTION_RDBMS_LOOKUP_TYPE_COMPOSITE_NAME_CHOICE"),
			new Integer(JNDIConnector.COMPOSITE_NAME_LOOKUP)
		);

		List lookupTypes = new Vector(3);
		lookupTypes.add(string);
		lookupTypes.add(compound);
		lookupTypes.add(composite);
		CollectionTools.sort(lookupTypes, buildLookupTypeComparator());

		return new ComboBoxModelAdapter(buildLookupTypeCollectionHolder(lookupTypes),
		                                buildLookupTypeHolder(lookupTypes));
	}

	private Comparator buildLookupTypeComparator()
	{
		return new Comparator()
		{
			public int compare(Object object1, Object object2)
			{
				LookupType lookupType1 = (LookupType) object1;
				LookupType lookupType2 = (LookupType) object2;
				return Displayable.DEFAULT_COLLATOR.compare(lookupType1.displayString(), lookupType2.displayString());
			}
		};
	}

	private PropertyValueModel buildLookupTypeHolder(final List lookupChoices)
	{
		return new PropertyAspectAdapter(getSubjectHolder(), DatabaseLoginAdapter.LOOKUP_TYPE_PROPERTY)
		{
			private LookupType findLookupType(List lookupChoices, Integer lookupValue)
			{
				for (int index = lookupChoices.size(); --index >= 0; )
				{
					LookupType choice = (LookupType) lookupChoices.get(index);

					if (choice.lookupValue().intValue() == lookupValue.intValue())
						return choice;
				}

				return null; // Never go here
			}

			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				return findLookupType(lookupChoices, adapter.getLookupType());
			}

			protected void setValueOnSubject(Object value)
			{
				LookupType lookupType = (LookupType) value;
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject;
				adapter.setLookupType(lookupType.lookupValue());
			}
		};
	}

	/**
	 * Returns the sub-pane containing the Data Source specific widgets.
	 *
	 * @return The initialized container
	 */
	private Component getDataSourcePane()
	{
		if (this.databaseSourcePane == null)
		{
			this.databaseSourcePane = buildDataSourcePane();
		}

		return this.databaseSourcePane;
	}

	/**
	 * Returns the sub-pane containing the Driver Manager specific widgets.
	 *
	 * @return The initialize container
	 * @see #buildDriverManagerPane
	 */
	private JComponent getDriverManagerPane()
	{
		if (this.driverManagerPane == null)
			this.driverManagerPane = buildDriverManagerPane();

		return this.driverManagerPane;
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Database Driver widgets
		Component databaseDriverWidgets = buildLabeledComboBox
		(
			"CONNECTION_RDBMS_DATABASE_DRIVER_COMBO_BOX",
			buildDatabaseDriverComboBoxAdapter(),
			new AdaptableListCellRenderer(buildDatabaseDriverLabelDecorator())
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(databaseDriverWidgets, constraints);

		// PanelSwitcher for Driver Manager and Data Source panes
		SwitcherPanel panelSwitcher = buildDatabaseDriverSwitcherPanel();

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(panelSwitcher, constraints);

		// Create the Save username check box
		JCheckBox saveUsernameCheckBox = buildSaveUsernameCheckBox();

		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(2, 0, 0, 0);

		add(saveUsernameCheckBox, constraints);

		// Username widgets
		Component usernameWidgets = buildUserNameWidgets();

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 10, 0, 0);

		add(usernameWidgets, constraints);
		
		new ComponentEnabler(buildSaveUsernameHolder(), usernameWidgets);

		// Create the Save Password check box
		JCheckBox savePasswordCheckBox = buildSavePasswordCheckBox();

		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(2, 0, 0, 0);

		add(savePasswordCheckBox, constraints);

		// Password widgets
		Component passwordWidgets = buildPasswordWidgets();

		constraints.gridx       = 0;
		constraints.gridy       = 5;
		constraints.gridwidth   = 3;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 10, 0, 0);

		add(passwordWidgets, constraints);
		
		new ComponentEnabler(buildSavePasswordHolder(), passwordWidgets);
		
	}

	/**
	 * Updates the enable state of the children of this pane.
	 *
    * @param enabled <code>true<code> if this component and its children should
    * be enabled, <code>false<code> otherwise
	 */
	protected void updateEnableStateOfChildren(boolean enabled)
	{
		super.updateEnableStateOfChildren(enabled);

		if (this.databaseSourcePane != null)
			this.databaseSourcePane.setEnabled(enabled);

		if (this.driverManagerPane != null)
			this.driverManagerPane.setEnabled(enabled);
	}

	private class LookupType
	{
		private String displayString;
		private Integer lookupValue;

		LookupType(String displayString, Integer lookupValue)
		{
			super();
			this.displayString = displayString;
			this.lookupValue = lookupValue;
		}

		public String displayString()
		{
			return displayString;
		}

		public Integer lookupValue()
		{
			return lookupValue;
		}
	}
}
