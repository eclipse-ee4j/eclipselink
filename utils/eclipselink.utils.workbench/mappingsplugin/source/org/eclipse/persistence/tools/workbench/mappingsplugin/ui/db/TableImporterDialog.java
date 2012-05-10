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
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.WaitDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.CursorConstants;
import org.eclipse.persistence.tools.workbench.framework.uitools.DualListSelectorPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This dialog has two functions:
 * First, it allows the user to query the database for tables that can
 * be imported. The user can specify various search criteria (catalog,
 * schema pattern, table name pattern, table type) to narrow the
 * list of tables.
 * Second, the user can select from this list of tables the tables to
 * be imported (or refreshed, if they are already present in the project).
 * 
 * It might take a while for this dialog to appear since it will query the
 * database for catalogs, schemas, and table types to populate the
 * combo-boxes in the search criteria panel.
 */
final class TableImporterDialog
	extends AbstractDialog
{
	MWDatabase database;
	
	private static final String DEFAULT_TABLE_NAME_PATTERN = "%";
	private PropertyValueModel tableNamePatternHolder;
	private Document tableNamePatternDocument;
	// initial focus component
	private JTextField tableNamePatternTextField;
	private JButton tablesButton;

	private String noCatalog;
	private String ignoreCatalog;
	private PropertyValueModel catalogHolder;
	private ComboBoxModel catalogComboBoxModel;

	private String noSchema;
	private String ignoreSchema;
	private PropertyValueModel schemaPatternHolder;
	private ComboBoxModel schemaPatternComboBoxModel;

	private static final String DEFAULT_TABLE_TYPE = "TABLE";
	private String allTableTypes;
	private PropertyValueModel tableTypeHolder;
	private ComboBoxModel tableTypeComboBoxModel;

	/**
	 * if the "fully-qualified" check-box is checked, the tables will
	 * be imported with their fully-qualified names; if it is not checked
	 * the tables will be imported with only their "short" names;
	 * the default is to use only the "short" names
	 */
	private PropertyValueModel fullyQualifiedHolder;
	private ButtonModel fullyQualifiedCheckBoxModel;

	SimpleCollectionValueModel availableExternalTableDescriptionsHolder;
	SimpleCollectionValueModel selectedExternalTableDescriptionsHolder;

	private static final Comparator EXTERNAL_TABLE_DESCRIPTION_COMPARATOR =
		new Comparator() {
			public int compare(Object o1, Object o2) {
				return Collator.getInstance().compare(((ExternalTableDescription) o1).getQualifiedName(), ((ExternalTableDescription) o2).getQualifiedName());
			}
		};


	TableImporterDialog(WorkbenchContext context, MWDatabase database) {
		super(context);
		this.initialize(database);
	}

	protected void initialize() {
		super.initialize();
		this.setTitle(this.resourceRepository().getString("IMPORT_TABLES_FROM_DATABASE_DIALOG.title"));
	}

	protected void prepareToShow() {
		this.setSize(700, 500);		
		this.setLocationRelativeTo(this.getParent());
		getRootPane().setDefaultButton(this.tablesButton);
	}

	private void initialize(MWDatabase db) {
		if ( ! db.isConnected()) {
			throw new IllegalStateException("database not connected");
		}
		this.database = db;

		// table name pattern
		this.tableNamePatternHolder = new SimplePropertyValueModel(DEFAULT_TABLE_NAME_PATTERN);
		this.tableNamePatternDocument = new DocumentAdapter(this.tableNamePatternHolder);
		this.tableNamePatternTextField = new JTextField(this.tableNamePatternDocument, null, 0);

		// catalog
		this.ignoreCatalog = this.resourceRepository().getString("IGNORE");
		this.noCatalog = this.resourceRepository().getString("NO_CATALOG");
		this.catalogHolder = new SimplePropertyValueModel(this.ignoreCatalog);
		this.catalogComboBoxModel = new ComboBoxModelAdapter(new ReadOnlyListValueModel(this.buildCatalogs()), this.catalogHolder);

		// schema pattern
		this.ignoreSchema = this.resourceRepository().getString("IGNORE");
		this.noSchema = this.resourceRepository().getString("NO_SCHEMA");
		this.schemaPatternHolder = new SimplePropertyValueModel(this.ignoreSchema);
		this.schemaPatternComboBoxModel = new ComboBoxModelAdapter(new ReadOnlyListValueModel(this.buildSchemas()), this.schemaPatternHolder);

		// table type
		this.allTableTypes = this.resourceRepository().getString("ALL_TYPES");
		List tableTypes = this.buildTableTypes();
		// some platforms do not return any table types, but most will return "TABLE"
		this.tableTypeHolder = new SimplePropertyValueModel(
					tableTypes.contains(DEFAULT_TABLE_TYPE) ? DEFAULT_TABLE_TYPE : this.allTableTypes
				);
		this.tableTypeComboBoxModel = new ComboBoxModelAdapter(new ReadOnlyListValueModel(tableTypes), this.tableTypeHolder);

		// fully-qualified
		this.fullyQualifiedHolder = new SimplePropertyValueModel(Boolean.FALSE);
		this.fullyQualifiedCheckBoxModel = new CheckBoxModelAdapter(this.fullyQualifiedHolder);

		this.availableExternalTableDescriptionsHolder = new SimpleCollectionValueModel();
		this.selectedExternalTableDescriptionsHolder = new SimpleCollectionValueModel();
	}

	private List buildCatalogs() {
		List catalogs = new ArrayList();
		catalogs.add(this.ignoreCatalog);
		catalogs.add(this.noCatalog);
		catalogs.addAll(CollectionTools.sortedSet(this.database.catalogNames()));
		return catalogs;
	}
	
	private List buildSchemas() {
		List schemas = new ArrayList();
		schemas.add(this.ignoreSchema);
		schemas.add(this.noSchema);
		schemas.addAll(CollectionTools.sortedSet(this.database.schemaNames()));
		return schemas;
	}
	
	private List buildTableTypes() {
		List schemas = new ArrayList();
		schemas.add(this.allTableTypes);
		schemas.addAll(CollectionTools.sortedSet(this.database.tableTypeNames()));
		return schemas;
	}
	
	protected String helpTopicId() {
		return "dialog.importingTables";
	}
	
	protected Component initialFocusComponent() {
		return this.tableNamePatternTextField;
	}
	
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// search criteria
		JPanel searchCriteriaPanel = this.buildSearchCriteriaPanel();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 0, 5);
		panel.add(searchCriteriaPanel, constraints);

		// dual list selection panel
		JPanel tableSelectionPanel = this.buildTableSelectionPanel();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 0, 5);
		panel.add(tableSelectionPanel, constraints);

		// import tables fully-qualified
		JCheckBox fullyQualifiedCheckBox = this.buildFullyQualifiedCheckBox();
		this.helpManager().addTopicID(fullyQualifiedCheckBox, this.helpTopicId() + ".fullyQualified");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(10, 0, 5, 0);
		panel.add(fullyQualifiedCheckBox, constraints);

		return panel;	
	}

	private JPanel buildSearchCriteriaPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// table name pattern text field
		JLabel tableLabel = new JLabel(this.resourceRepository().getString("TABLE_NAME_PATTERN_TEXT_FIELD_LABEL"));
		tableLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("TABLE_NAME_PATTERN_TEXT_FIELD_LABEL"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(tableLabel, constraints);
		
		// the table name patter text field is pre-built
		this.helpManager().addTopicID(this.tableNamePatternTextField, this.helpTopicId() + ".namePattern");
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(0, 5, 0, 0);
		panel.add(this.tableNamePatternTextField, constraints);
		tableLabel.setLabelFor(this.tableNamePatternTextField);
		
		// catalog combo-box (NOT editable)
		JLabel catalogLabel = new JLabel(this.resourceRepository().getString("CATALOG_COMBO_BOX_LABEL"));
		catalogLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("CATALOG_COMBO_BOX_LABEL"));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 0, 0, 0);
		panel.add(catalogLabel, constraints);
		
		JComboBox catalogComboBox = new JComboBox(this.catalogComboBoxModel);
		this.helpManager().addTopicID(catalogComboBox, this.helpTopicId() + ".catalog");
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 0);
		panel.add(catalogComboBox, constraints);
		catalogLabel.setLabelFor(catalogComboBox);
		
		// schema combo-box (editable)
		JLabel schemaLabel = new JLabel(this.resourceRepository().getString("SCHEMA_PATTERN_COMBO_BOX_LABEL"));
		schemaLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("SCHEMA_PATTERN_COMBO_BOX_LABEL"));
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 0, 0, 0);
		panel.add(schemaLabel, constraints);
		
		JComboBox schemaComboBox = new JComboBox(this.schemaPatternComboBoxModel);
		schemaComboBox.setEditable(true);
		this.helpManager().addTopicID(schemaComboBox, this.helpTopicId() + ".schemaPattern");
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 0);
		panel.add(schemaComboBox, constraints);
		schemaLabel.setLabelFor(schemaComboBox);
		
		// table type combo-box
		JLabel tableTypeLabel = new JLabel(this.resourceRepository().getString("TABLE_TYPE_COMBO_BOX_LABEL"));
		tableTypeLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("TABLE_TYPE_COMBO_BOX_LABEL"));
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 0, 0, 0);
		panel.add(tableTypeLabel, constraints);
		
		JComboBox tableTypeComboBox = new JComboBox(this.tableTypeComboBoxModel);
		this.helpManager().addTopicID(tableTypeComboBox, helpTopicId() + ".type");
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 0, 0);
		panel.add(tableTypeComboBox, constraints);
		tableTypeLabel.setLabelFor(tableTypeComboBox);
		
		this.tablesButton = new JButton(this.buildGetTableNamesAction());
		this.tablesButton.setMnemonic(this.resourceRepository().getMnemonic("GET_TABLE_NAMES_BUTTON_TEXT"));
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(10, 0, 5, 0);
		panel.add(this.tablesButton, constraints);

		return panel;
	}

	private Action buildGetTableNamesAction() {
		return new AbstractAction(this.resourceRepository().getString("GET_TABLE_NAMES_BUTTON_TEXT")) {
			public void actionPerformed(ActionEvent e) {
				TableImporterDialog.this.getTableNamesPressed();
			}
		};
	}

	private JCheckBox buildFullyQualifiedCheckBox() {
		return SwingComponentFactory.buildCheckBox("FULLY_QUALIFIED_CHECK_BOX", this.fullyQualifiedCheckBoxModel, resourceRepository());
	}

	private JPanel buildTableSelectionPanel() {
		DualListSelectorPanel.Builder builder = new DualListSelectorPanel.Builder();
		builder.setAvailableLVM(this.buildSortedExternalTableDescriptionsHolderAdapter(this.availableExternalTableDescriptionsHolder));
		builder.setSelectedLVM(this.buildSortedExternalTableDescriptionsHolderAdapter(this.selectedExternalTableDescriptionsHolder));
		builder.setAdapter(this.buildExternalTableDescriptionSelectionAdapter());
		builder.setListCellRenderer(this.buildExternalTableDescriptionRenderer());
		builder.setContext(this.getApplicationContext());
		builder.setAvailableListBoxLabelKey("AVAILABLE_TABLES_LIST_BOX_LABEL");
		builder.setSelectedListBoxLabelKey("SELECTED_TABLES_LIST_BOX_LABEL");
		builder.setSelectButtonToolTipKey("ADD_SELECTED_TABLES_BUTTON.toolTipText");
		builder.setDeselectButtonToolTipKey("REMOVE_SELECTED_TABLES_BUTTON.toolTipText");
		builder.setHelpTopicID(this.helpTopicId());
		return builder.buildPanel();
	}

	private ListValueModel buildSortedExternalTableDescriptionsHolderAdapter(CollectionValueModel externalTableDescriptionsHolder) {
		return new SortedListValueModelAdapter(externalTableDescriptionsHolder, EXTERNAL_TABLE_DESCRIPTION_COMPARATOR);
	}

	private DualListSelectorPanel.Adapter buildExternalTableDescriptionSelectionAdapter() {
		return new DualListSelectorPanel.Adapter() {
			public void select(Object item) {
				TableImporterDialog.this.availableExternalTableDescriptionsHolder.removeItem(item);
				TableImporterDialog.this.selectedExternalTableDescriptionsHolder.addItem(item);
			}
			public void deselect(Object item) {
				TableImporterDialog.this.selectedExternalTableDescriptionsHolder.removeItem(item);
				TableImporterDialog.this.availableExternalTableDescriptionsHolder.addItem(item);
			}
		};
	}

	private ListCellRenderer buildExternalTableDescriptionRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return ((ExternalTableDescription) value).getQualifiedName();
			}
		};
	}


	// ********** queries **********

	/**
	 * return the appropriate setting to pass in the call to
	 * java.sql.DatabaseMetaData#getTables(String, String, String, String[])
	 */
	String catalog() {
		String catalog = (String) this.catalogHolder.getValue();
		if (catalog == this.ignoreCatalog) {
			return null;
		}
		if (catalog == this.noCatalog) {
			return "";
		}
		return catalog;
	}

	/**
	 * return the appropriate setting to pass in the call to
	 * java.sql.DatabaseMetaData#getTables(String, String, String, String[])
	 */
	String schemaPattern() {
		String schema = (String) this.schemaPatternHolder.getValue();
		if (schema == this.ignoreSchema) {
			return null;
		}
		if (schema == this.noSchema) {
			return "";
		}
		return schema;
	}
	
	/**
	 * return the appropriate setting to pass in the call to
	 * java.sql.DatabaseMetaData#getTables(String, String, String, String[])
	 */
	String tableNamePattern() {
		return (String) this.tableNamePatternHolder.getValue();
	}
	
	/**
	 * return the appropriate setting to pass in the call to
	 * java.sql.DatabaseMetaData#getTables(String, String, String, String[])
	 * currently we only allow a single table type per query...
	 */
	String[] tableTypes() {
		String tableType = (String) this.tableTypeHolder.getValue();
		if (tableType == this.allTableTypes) {
			return null;
		}
		return new String[] { tableType };
	}

	/**
	 * return whether the tables should be imported with
	 * fully-qualified names
	 */
	public boolean importsTablesFullyQualified() {
		return ((Boolean) this.fullyQualifiedHolder.getValue()).booleanValue();
	}

	public Collection selectedTables() {
		return CollectionTools.list((Iterator) this.selectedExternalTableDescriptionsHolder.getValue());
	}

	// ********** behavior **********

	/**
	 * Fork a thread to get a list of tables from the database
	 * that match the user-entered search criteria and put it
	 * in the list of "available" tables;
	 * leave the list of "selected" tables intact, allowing the
	 * user to build up a list over multiple queries
	 */
	void getTableNamesPressed() {
		Thread thread = new Thread(new TableNameQuery(), "Database Table Name Query");
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}

	/**
	 * callback to allow thread-safe adding of table names
	 */
	void addTableNamesCallback(final Collection tableNames) throws InterruptedException, InvocationTargetException {
		EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				TableImporterDialog.this.availableExternalTableDescriptionsHolder.clear();
				// add all the tables at once to reduce the sorting effort
				TableImporterDialog.this.availableExternalTableDescriptionsHolder.addItems(tableNames);
			}
		});
	}

	/**
	 * query the database for the table names
	 */
	private class TableNameQuery implements Runnable {

		TableNameQuery() {
			super();
		}

		public void run() {
			this.setCursor(CursorConstants.WAIT_CURSOR);
			WaitDialog waitDialog = this.buildWaitDialog();
			launchLater(waitDialog);

			Collection externalTableDescriptions = new ArrayList(1000);	// start sorta big
			try {
				for (Iterator stream = this.externalTableDescriptions(); stream.hasNext(); ) {
					externalTableDescriptions.add(stream.next());
				}
				TableImporterDialog.this.addTableNamesCallback(externalTableDescriptions);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			} finally {
				waitDialog.dispose();
				this.setCursor(CursorConstants.DEFAULT_CURSOR);
			}

		}

		private void setCursor(Cursor cursor) {
			TableImporterDialog.this.getWorkbenchContext().getCurrentWindow().setCursor(cursor);
		}

		private WaitDialog buildWaitDialog() {
			return new WaitDialog(
					TableImporterDialog.this,
					TableImporterDialog.this.resourceRepository().getIcon("database.large"),
					TableImporterDialog.this.resourceRepository().getString("GET_TABLE_NAMES_DIALOG.TITLE"),
					TableImporterDialog.this.resourceRepository().getString("GET_TABLE_NAMES_DIALOG_MESSAGE")
			);
		}

		private Iterator externalTableDescriptions() {
			return TableImporterDialog.this.database.externalTableDescriptions(
					TableImporterDialog.this.catalog(),
					TableImporterDialog.this.schemaPattern(),
					TableImporterDialog.this.tableNamePattern(),
					TableImporterDialog.this.tableTypes()
			);
		}

	}

}
