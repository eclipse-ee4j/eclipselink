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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// JDK
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.Property;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.PropertyAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.*;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTableCellRenderer;


/**
 * This pane shows a table of keys and values.
 * <p>
 * Here the layout of this pane:
 * <pre>
 * _________________________________________________
 * | _________________________________ ___________ |
 * | | |    Name      |    Value     | | Add...  | |<- Shows the {@link PropertyEditor}
 * | |¯|¯¯¯¯¯¯¯¯¯¯¯¯¯¯|¯¯¯¯¯¯¯¯¯¯¯¯¯¯| ¯¯¯¯¯¯¯¯¯¯¯ |   to add a new key/value
 * | |-|--------------|--------------| ___________ |
 * | | |              |              | | Edit... | |<- Shows the {@link PropertyEditor}
 * | |-|--------------|--------------| ¯¯¯¯¯¯¯¯¯¯¯ |   to edit the selected row
 * | | |              |              | ___________ |
 * | |-|--------------|--------------| | Remove  | |
 * | | |              |              | ¯¯¯¯¯¯¯¯¯¯¯ |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯             |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @see Property
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class PropertyPane extends AbstractSubjectPanel
{
	/**
	 * Keeps a reference on this component in order to update its enable state
	 * whenever the enable state of this pane changes.
	 */
	private JButton addButton;

	/**
	 * Keeps a reference on this component in order to update its enable state
	 * whenever the enable state of this pane changes.
	 */
	private JButton editButton;

	/**
	 * Keeps a reference on this component in order to update its enable state
	 * whenever the enable state of this pane changes.
	 */
	private JButton removeButton;

	/**
	 * Keeps a reference on this component in order to update its enable state
	 * whenever the enable state of this pane changes.
	 */
	private JScrollPane scrollPane;

	/**
	 * Keeps a reference for easy access on this selection model.
	 */
	private ObjectListSelectionModel selectionModel;

	/**
	 * Keeps a reference on this component in order to update its enable state
	 * whenever the enable state of this pane changes.
	 */
	private JTable table;
	
	/**
	 * Creates a new <code>LoginPropertiesPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link Property}
	 * @param context
	 */
	public PropertyPane(ValueModel subjectHolder,
							  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Create a new <code>ActionListener</code> that will perform the action of
	 * adding a new {@link PropertyAdapter} to {@link LoginAdapter}.
	 *
	 * @return A new <code>ActionListener</code>
	 */
	private ActionListener buildAddActionListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PropertyEditor editor;

				if (currentWindow() instanceof Dialog)
					editor = new PropertyEditor("LOGIN_PROPERTY_EDITOR_ADD_TITLE", (Dialog) currentWindow());
				else
					editor = new PropertyEditor("LOGIN_PROPERTY_EDITOR_ADD_TITLE");

				editor.setVisible(true);

				if (editor.wasConfirmed())
					editor.apply();
			}
		};
	}

	/**
	 * Create a new <code>ActionListener</code> that will perform the action of
	 * editing the unique selected {@link PropertyAdapter}.
	 *
	 * @param model The table selection model used to retrieve the selected value
	 * @return A new <code>ActionListener</code>
	 */
	private ActionListener buildEditActionListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PropertyEditor editor;

				if (currentWindow() instanceof Dialog)
				{
					editor = new PropertyEditor
					(
						"LOGIN_PROPERTY_EDITOR_EDIT_TITLE",
						(Dialog) currentWindow(),
						(PropertyAdapter) selectionModel.getSelectedValue()
					);
				}
				else
				{
					editor = new PropertyEditor
					(
						"LOGIN_PROPERTY_EDITOR_EDIT_TITLE",
						(PropertyAdapter) selectionModel.getSelectedValue()
					);
				}

				editor.setVisible(true);

				if (editor.wasConfirmed())
					editor.apply();
			}
		};
	}

	/**
	 * Adds a <code>ListSelectionListener</code> to the given selection model in
	 * order to keep the enable state of the given edit button in sync with the
	 * selection.
	 */
	private void buildEditButtonEnabler()
	{
		selectionModel.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					editButton.setEnabled(selectionModel.getSelectedValues().length == 1);
				}
			}
		});
	}

	/**
	 * Creates a new <code>CollectionValueModel</code>
	 *
	 * @return A new <code>CollectionValueModel</code>
	 */
	private CollectionValueModel buildPropertyCollectionHolder()
	{
		return new CollectionAspectAdapter(getSubjectHolder(), Property.PROPERTY_COLLECTION)
		{
			protected Iterator getValueFromSubject()
			{
				Property property = (Property) subject;
				return property.properties();
			}

			protected int sizeFromSubject()
			{
				Property property = (Property) subject;
				return property.propertySize();
			}
		};
	}

	/**
	 * Creates a new <code>ObjectListSelectionModel</code> that will handle the
	 * table selection.
	 *
	 * @return A new <code>ObjectListSelectionModel</code>
	 */
	private ObjectListSelectionModel buildPropertySelectionModel()
	{
		ListModelAdapter listModel = new ListModelAdapter(buildPropertyCollectionHolder());
		return new ObjectListSelectionModel(listModel);
	}
	
	/**
	 * Creates a new <code>TableModel</code> that handles the 
	 *
	 * @return A new <code>TableModel</code>
	 */
	private TableModel buildPropertyTableAdapter()
	{
		return new TableModelAdapter(buildPropertyCollectionHolder(),
											  new PropertyColumnAdapter());
	}

	/**
	 * Create a new <code>ActionListener</code> that will perform the action of
	 * removing the selected {@link PropertyAdapter}s from {@link LoginAdapter}.
	 *
	 * @param model The table selection model used to retrieve the selected values
	 * @return A new <code>ActionListener</code>
	 */
	private ActionListener buildRemoveActionListener(final ObjectListSelectionModel model)
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object[] selectedValues = model.getSelectedValues();
				Property property = (Property) subject();

				for (int index = selectedValues.length; --index >= 0;)
				{
					property.removeProperty((PropertyAdapter) selectedValues[index]);
				}
			}
		};
	}

	/**
	 * Adds a <code>ListSelectionListener</code> to the given selection model in
	 * order to keep the enable state of the given remove button in sync with the
	 * selection.
	 */
	private void buildRemoveButtonEnabler()
	{
		selectionModel.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					removeButton.setEnabled(!selectionModel.isSelectionEmpty());
				}
			}
		});
	}

	/**
	 * Creates a new <code>TableColumnModel</code> along with its
	 * <code>TableColumn</code>s: Selection, Name and Value columns.
	 *
	 * @return A new <code>TableColumnModel</code>
	 */
	private TableColumnModel buildTableColumnModel()
	{
		DefaultTableColumnModel model = new DefaultTableColumnModel();

		// Selection column
		TableColumn selectionColumn = new TableColumn(PropertyColumnAdapter.SELECTION_COLUMN);
		selectionColumn.setCellRenderer(new SelectionTableCellRenderer());
		selectionColumn.setHeaderValue(" ");
		selectionColumn.setMaxWidth(20);
		selectionColumn.setMinWidth(20);
		selectionColumn.setPreferredWidth(20);
		selectionColumn.setResizable(false);
		model.addColumn(selectionColumn);

		// Name column
		TableColumn nameColumn = new TableColumn(PropertyColumnAdapter.NAME_COLUMN);
		nameColumn.setHeaderValue(resourceRepository().getString("LOGIN_PROPERTY_NAME_COLUMN"));
		nameColumn.setMinWidth(50);
		nameColumn.setResizable(true);
		model.addColumn(nameColumn);

		// Value column
		TableColumn valueColumn = new TableColumn(PropertyColumnAdapter.VALUE_COLUMN);
		valueColumn.setHeaderValue(resourceRepository().getString("LOGIN_PROPERTY_VALUE_COLUMN"));
		valueColumn.setMinWidth(50);
		valueColumn.setResizable(true);
		model.addColumn(valueColumn);

		return model;
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		selectionModel = buildPropertySelectionModel();

		// Property table
		table = SwingComponentFactory.buildTable(
			this.buildPropertyTableAdapter(),
			this.buildTableColumnModel(),
			this.selectionModel
		);
		table.setAutoCreateColumnsFromModel(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoscrolls(true);
		table.setSurrendersFocusOnKeystroke(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.addPropertyChangeListener("enabled", new PropertyChangeHandler());

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 3;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.BOTH;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		scrollPane = new JScrollPane(table);
		scrollPane.setMinimumSize(new Dimension(0, 0));
		scrollPane.setPreferredSize(new Dimension(0, 0));
		scrollPane.getViewport().setBackground(table.getBackground());

		add(scrollPane, constraints);

		// Add button
		addButton = buildButton("LOGIN_PROPERTY_ADD_BUTTON");
		addButton.addActionListener(buildAddActionListener());

		constraints.gridx	      = 1;
		constraints.gridy	      = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 5, 0, 0);

		add(addButton, constraints);
		
		// Edit button
		editButton = buildButton("LOGIN_PROPERTY_EDIT_BUTTON");
		editButton.addActionListener(buildEditActionListener());
		editButton.setEnabled(false);

		constraints.gridx	      = 1;
		constraints.gridy	      = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 5, 0, 0);

		add(editButton, constraints);
		buildEditButtonEnabler();

		// Remove button
		removeButton = buildButton("LOGIN_PROPERTY_REMOVE_BUTTON");
		removeButton.addActionListener(buildRemoveActionListener(selectionModel));
		removeButton.setEnabled(false);

		constraints.gridx	      = 1;
		constraints.gridy	      = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(5, 5, 0, 0);

		add(removeButton, constraints);
		buildRemoveButtonEnabler();

		addHelpTopicId(this, "session.login.properties");
	}

	/**
    * Sets whether or not this component is enabled. Disabling this pane will
    * also disable its children.
    *
    * @param enabled <code>true<code> if this component and its children should
    * be enabled, <code>false<code> otherwise
    */
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		addButton.setEnabled(enabled);
		table.setEnabled(enabled);
		table.getTableHeader().setEnabled(enabled);

		if (enabled)
		{
			editButton.setEnabled(selectionModel.getSelectedValues().length == 1);
			removeButton.setEnabled(!selectionModel.isSelectionEmpty());
		}
		else
		{
			table.clearSelection();

			editButton.setEnabled(enabled);
			removeButton.setEnabled(enabled);
		}
	}

	/**
	 * This handler listens to the enabled state change in the table and update
	 * the background color of the viewport.
	 */
	private class PropertyChangeHandler implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			if (table.isEnabled())
				scrollPane.getViewport().setBackground(table.getBackground());
			else
				scrollPane.getViewport().setBackground(UIManager.getColor("control"));
		}
	}

	/**
	 * 
	 */
	private static class PropertyColumnAdapter implements ColumnAdapter
	{
		/** The count of column. */
		public static final int COLUMN_COUNT = 3;

		/** The index of the Name column. */
		public static final int NAME_COLUMN = 1;

		/** The index of the Selection column. */
		public static final int SELECTION_COLUMN = 0;

		/** The index of the Value column. */
		public static final int VALUE_COLUMN = 2;

		/**
		 * Creates
		 *
		 * @param property
		 * @return
		 */
		private PropertyValueModel buildNameAdapter(final PropertyAdapter property)
		{
			return new PropertyAspectAdapter(PropertyAdapter.NAME_PROPERTY, property)
			{
				protected Object getValueFromSubject()
				{
					return property.getName();
				}
			};
		}

		/**
		 * Creates
		 *
		 * @param property
		 * @return
		 */
		private PropertyValueModel buildValueAdapter(final PropertyAdapter property)
		{
			return new PropertyAspectAdapter(PropertyAdapter.VALUE_PROPERTY, property)
			{
				protected Object getValueFromSubject()
				{
					return property.getValue();
				}
			};
		}

		/**
		 * Creates
		 *
		 * @param subject
		 * @return
		 */
		public PropertyValueModel[] cellModels(Object subject)
		{
			PropertyAdapter property = (PropertyAdapter) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[SELECTION_COLUMN] = new SimplePropertyValueModel();
			result[NAME_COLUMN]      = buildNameAdapter(property);
			result[VALUE_COLUMN]     = buildValueAdapter(property);

			return result;
		}

		/**
		 * Creates
		 *
		 * @param index
		 * @return
		 */
		public Class getColumnClass(int index)
		{
			switch (index)
			{
				case NAME_COLUMN:
				case VALUE_COLUMN:
					return String.class;
		
				default:
					return Object.class;
			}
		}

		/**
		 * Returns
		 *
		 * @return
		 */
		public int getColumnCount()
		{
			return COLUMN_COUNT;
		}

		/**
		 * Returns
		 *
		 * @param index
		 * @return
		 */
		public String getColumnName(int index)
		{
			return null;
		}

		/**
		 * Determines
		 *
		 * @param index
		 * @return
		 */
		public boolean isColumnEditable(int index)
		{
			return false;
		}
	}

	/**
	 * This dialog shows the {@link PropertyEditorView} in order to either add a
	 * new Key/Value or to edit the selected one.
	 */
	private class PropertyEditor extends AbstractValidatingDialog
	{
		/**
		 * The component to be given focus when the dialog is opened.
		 */
		private Component initialFocusComponent;

		/**
		 * This virtual {@link PropertyAdapter} is used by the {@link PropertyEditor}
		 * for editing purposes without changing the underlying model.
		 */
		private final VirtualProperty virtualProperty;

		/**
		 * Creates a new <code>PropertyEditor</code>.
		 *
		 * @param titleKey The key used to retrieve the localized title
		 */
		public PropertyEditor(String titleKey)
		{
			this(titleKey, (PropertyAdapter) null);
		}

		/**
		 * Creates a new <code>PropertyEditor</code>.
		 *
		 * @param titleKey The key used to retrieve the localized title
		 * @param parentWindow The owner of this dialog
		 */
		public PropertyEditor(String titleKey,
									 Dialog parentWindow)
		{
			this(titleKey, parentWindow, null);
		}

		/**
		 * Creates a new <code>PropertyEditor</code>.
		 *
		 * @param titleKey The key used to retrieve the localized title
		 * @param parentWindow The owner of this dialog
		 * @param property The {@link PropertyAdapter} to be edited
		 */
		public PropertyEditor(String titleKey,
									 Dialog parentWindow,
									 PropertyAdapter propertyAdapter)
		{
			super(PropertyPane.this.getWorkbenchContext(),
					PropertyPane.this.resourceRepository().getString(titleKey),
					parentWindow);

			virtualProperty = new VirtualProperty(propertyAdapter);
		}

		/**
		 * Creates a new <code>PropertyEditor</code>.
		 *
		 * @param titleKey The key used to retrieve the localized title
		 * @param property The {@link PropertyAdapter} to be edited
		 */
		public PropertyEditor(String titleKey,
									 PropertyAdapter propertyAdapter)
		{
			super(PropertyPane.this.getWorkbenchContext(),
					PropertyPane.this.resourceRepository().getString(titleKey));

			virtualProperty = new VirtualProperty(propertyAdapter);
		}

		/**
		 * Applies the changes made to the edited value into the model.
		 */
		public void apply()
		{
			if (wasConfirmed())
				virtualProperty.apply();
		}

		/**
		 * Initializes the layout of the editor's pane.
		 *
		 * @return {@link PropertyEditorView}
		 */
		protected Component buildMainPanel()
		{
			PropertyEditorView view = new PropertyEditorView(new SimplePropertyValueModel(virtualProperty),
																			 getApplicationContext());

			// Engage a listener that will udpate the message area and the enable
			// state of the OK button when the entry in the name or value field is
			// changed
			PropertyChangeListener listener = buildValidationPropertyListener();
			view.addPropertyChangeListener(PropertyEditorView.NAME_PROPERTY,  listener);
			view.addPropertyChangeListener(PropertyEditorView.VALUE_PROPERTY, listener);

			initialFocusComponent = view.getInitialFocusComponent();

			return view;
		}

		/**
		 * Creates a new <code>PropertyChangeListener</code> that will listens for
		 * changes made to the view and update the error message accordingly.
		 *
		 * @return A new <code>PropertyChangeListener</code>
		 */
		private PropertyChangeListener buildValidationPropertyListener()
		{
			return new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent e)
				{
					validate(e.getPropertyName());
				}
			};
		}

		/**
		 * Returns the topic ID used to display the proper help.
		 *
		 * @return session.login.properties.propertyEditor
		 */
		protected String helpTopicId()
		{
			return "session.login.properties.propertyEditor";
		}

		/**
		 * Returns the component to be given focus when the dialog is opened.
		 * 
		 * @return The first focusable component of this dialog
		 */
		protected Component initialFocusComponent()
		{
			return initialFocusComponent;
		}

		/**
		 * Makes sure the OK button is disabled.
		 */
		protected void prepareToShow()
		{
			super.prepareToShow();
			((JTextField) initialFocusComponent).selectAll();
			getOKAction().setEnabled(virtualProperty.propertyAdapter != null);
		}

		/**
		 * Validates the given value and update the enable state of the OK button.
		 *
		 * @param propertyName Either {@link PropertyEditorView.VALUE_PROPERTY}
		 * or {@link PropertyEditorView.NAME_PROPERTY}
		 */
		private void validate(String propertyName)
		{
			String valueErrorKey = (virtualProperty.value.length() == 0) ? "_EMPTY_VALUE" : null;
			String nameErrorKey = null;

			// A name is required
			if (virtualProperty.name.length() == 0)
			{
				nameErrorKey = "_EMPTY_NAME";
			}
			// Loop through the existing PropertyAdapters and check if one
			// already exists with the specified name, at the exception of the
			// edited one (during editing and not creation)
			else
			{
				Property property = (Property) subject();

				for (Iterator iter = property.properties(); iter.hasNext();)
				{
					PropertyAdapter propertyAdapter = (PropertyAdapter) iter.next();

					if ((propertyAdapter != virtualProperty.propertyAdapter) &&
						  propertyAdapter.getName().equalsIgnoreCase(virtualProperty.name))
					{
						nameErrorKey = "_INVALID_NAME";
						break;
					}
				}
			}

			// Create the key
			String errorKey = "LOGIN_PROPERTY_EDITOR";

			if (nameErrorKey != null)
				errorKey += nameErrorKey;

			if (valueErrorKey != null)
				errorKey += valueErrorKey;

			// Update the UI
			if ((nameErrorKey == null) && (valueErrorKey == null))
			{
				clearErrorMessage();
				getOKAction().setEnabled(true);
			}
			else
			{
				setErrorMessage(resourceRepository().getString(errorKey));
				getOKAction().setEnabled(false);
			}
		}
	}

	/**
	 * This view edits a {@link VirtualProperty}.
	 * <p>
	 * Here the layout of this view:
	 * <pre>
	 * ____________________________
	 * |         ________________ |
	 * | Name:   | I            | |
	 * |         ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
	 * |         ________________ |
	 * | Value:  | I            | |
	 * |         ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
	 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
	 */
	private class PropertyEditorView extends AbstractSubjectPanel
	{
		static final String VALUE_PROPERTY = "value";
		static final String NAME_PROPERTY = "name";

		/**
		 * Creates a new <code>PropertyEditorView</code>.
		 *
		 * @param subjectHolder The holder of the value that will be edited by
		 * this view
		 * @param context The context ...
		 */
		public PropertyEditorView(PropertyValueModel subjectHolder,
										  ApplicationContext context)
		{
			super(subjectHolder, context);
		}

		/**
		 * Creates the <code>Document</code> that keeps the value from the text
		 * field in sync with the Name value in the model and vice versa.
		 * 
		 * @return A new <code>Document</code>
		 */
		private Document buildNameDocumentAdapter()
		{
			return new DocumentAdapter(buildNameHolder());
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to handle the
		 * Name property.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildNameHolder()
		{
			return new SimplePropertyValueModel()
			{
				public Object getValue()
				{
					VirtualProperty property = (VirtualProperty) subject();
					return property.name;
				}

				public void setValue(Object value)
				{
					VirtualProperty property = (VirtualProperty) subject();
					String oldName = property.name;
					property.name = ((String) value).trim();
					PropertyEditorView.this.firePropertyChange(NAME_PROPERTY, oldName, property.name);
				}
			};
		}

		/**
		 * Creates the <code>Document</code> that keeps the value from the text
		 * field in sync with the Value value in the model and vice versa.
		 * 
		 * @return A new <code>Document</code>
		 */
		private Document buildValueDocumentAdapter()
		{
			return new DocumentAdapter(buildValueHolder());
		}

		/**
		 * Creates the <code>PropertyValueModel</code> responsible to handle the
		 * Value property.
		 *
		 * @return A new <code>PropertyValueModel</code>
		 */
		private PropertyValueModel buildValueHolder()
		{
			return new SimplePropertyValueModel()
			{
				public Object getValue()
				{
					VirtualProperty property = (VirtualProperty) subject();
					return property.value;
				}

				public void setValue(Object value)
				{
					VirtualProperty property = (VirtualProperty) subject();
					String oldValue = property.value;
					property.value = ((String) value).trim();
					PropertyEditorView.this.firePropertyChange(VALUE_PROPERTY, oldValue, property.value);
				}
			};
		}

		/**
		 * Returns the component to receive the focus when the dialog is shown.
		 *
		 * @return The first component in this view that should take the focus
		 */
		public Component getInitialFocusComponent()
		{
			return (Component) getClientProperty("initialFocus");
		}

		/**
		 * Initializes the layout of this view.
		 */
		protected void initializeLayout()
		{
			GridBagConstraints constraints = new GridBagConstraints();

			// Name widgets
			JComponent nameWidgets = buildLabeledTextField
			(
				"LOGIN_PROPERTY_EDITOR_NAME_FIELD",
				buildNameDocumentAdapter()
			);

			constraints.gridx			= 0;
			constraints.gridy			= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill			= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, 0, 0, 0);

			add(nameWidgets, constraints);
			putClientProperty("initialFocus", nameWidgets.getComponent(1));

			// Value widgets
			JComponent valueWidgets = buildLabeledTextField
			(
				"LOGIN_PROPERTY_EDITOR_VALUE_FIELD",
				buildValueDocumentAdapter()
			);

			constraints.gridx			= 0;
			constraints.gridy			= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 1;
			constraints.fill			= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.PAGE_START;
			constraints.insets		= new Insets(5, 0, 0, 0);

			add(valueWidgets, constraints);
		}
	}

	/**
	 * This <code>TableCellRenderer</code> makes sure the selection cell is
	 * painted the same color than the header.
	 */
	private class SelectionTableCellRenderer extends SimpleTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table,
																	  Object value,
																	  boolean selected,
																	  boolean hasFocus,
																	  int rowIndex,
																	  int columnIndex)
		{
			Component component = super.getTableCellRendererComponent(table, value, selected, hasFocus, rowIndex, columnIndex);

			if (!selected)
				component.setBackground(table.getTableHeader().getBackground());

			return component;
		}
	}

	/**
	 * This virtual {@link PropertyAdapter} is used by the {@link PropertyEditor}
	 * for editing purposes without changing the underlying model.
	 */
	private class VirtualProperty
	{
		/**
		 * The cached name of the property.
		 */
		public String name;

		/**
		 * The <code>PropertyAdapter</code> to be edited if not <code>null</code>.
		 */
		private PropertyAdapter propertyAdapter;

		/**
		 * The cached value of the property.
		 */
		public String value;

		/**
		 * Creates a new <code>VirtualProperty</code> for the given property to be
		 * encapsulated.
		 *
		 * @param property The actual <code>PropertyAdapter</code> to be edited if
		 * not <code>null</code>
		 */
		VirtualProperty(PropertyAdapter propertyAdapter)
		{
			super();
			initialize(propertyAdapter);
		}

		/**
		 * Applies the changes made to the edited value into the model. If the
		 * {@link #property} is <code>null</code>, a new <code>PropertyAdater</code>
		 * will be created, otherwise it will be updated.
		 */
		void apply()
		{
			if (propertyAdapter == null)
			{
				Property property = (Property) subject();
				propertyAdapter = property.addProperty(name, value);
			}
			else
			{
				propertyAdapter.setName(name);
				propertyAdapter.setValue(value);
			}
		}

		/**
		 * Initializes this <code>VirtualProperty</code>.
		 *
		 * @param property The actual <code>PropertyAdapter</code> to be edited if
		 * not <code>null</code>
		 */
		private void initialize(PropertyAdapter propertyAdapter)
		{
			this.propertyAdapter = propertyAdapter;

			if (propertyAdapter != null)
			{
				name = propertyAdapter.getName();
				value = propertyAdapter.getValue();
			}
			else
			{
				name = "";
				value = "";
			}
		}
	}
}
