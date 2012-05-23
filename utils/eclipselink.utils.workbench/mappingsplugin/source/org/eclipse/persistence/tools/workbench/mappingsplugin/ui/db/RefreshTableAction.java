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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;



class RefreshTableAction extends AbstractEnablableFrameworkAction {

	RefreshTableAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeIcon("synchronize");
		this.initializeTextAndMnemonic("REFRESH");
		this.initializeToolTipText("REFRESH");
	}

	protected void engageValueEnabled(AbstractApplicationNode node) {
		super.engageValueEnabled(node);
		((TableNode) node).getTable().getDatabase().addPropertyChangeListener(MWDatabase.CONNECTED_PROPERTY, this.getEnabledStateListener());
	}

	protected void disengageValueEnabled(AbstractApplicationNode node) {
		super.disengageValueEnabled(node);
		((TableNode) node).getTable().getDatabase().removePropertyChangeListener(MWDatabase.CONNECTED_PROPERTY, this.getEnabledStateListener());
	}

	private Collection selectedTables() {
		Collection selectedTables = new ArrayList();
		ApplicationNode[] selectedTableNodes = this.selectedNodes();
		for (int i = 0; i < selectedTableNodes.length; i++) {
			selectedTables.add(((TableNode) selectedTableNodes[i]).getTable());
		}
		return selectedTables;
	}

	protected void execute() {
		Collection selectedTables = this.selectedTables();
		// grab the database from the first selected table
		MWDatabase database = ((MWTable) selectedTables.iterator().next()).getDatabase();
		Collection externalTableDescriptions = new ArrayList(selectedTables.size());
		Collection nonRefreshedTables = new ArrayList();
		for (Iterator stream = selectedTables.iterator(); stream.hasNext(); ) {
			MWTable selectedTable = (MWTable) stream.next();
			Collection matchingExternalTableDescriptions = CollectionTools.collection(selectedTable.matchingExternalTableDescriptions());
			if (matchingExternalTableDescriptions.size() == 1) {
				externalTableDescriptions.add(matchingExternalTableDescriptions.iterator().next());
			} else if (matchingExternalTableDescriptions.size() > 1) {
				DuplicateTablesDialog dialog = new DuplicateTablesDialog(this.getWorkbenchContext(), selectedTable, matchingExternalTableDescriptions);
				dialog.show();
				if (dialog.wasConfirmed()) {
					externalTableDescriptions.add(dialog.selectedExternalTableDescription());
				} else {
					nonRefreshedTables.add(selectedTable);
				}
			} else {
				nonRefreshedTables.add(selectedTable);
			}
				
		}

		database.refreshQualifiedTablesFor(externalTableDescriptions);

		if (nonRefreshedTables.size() > 0) {
			MissingTablesDialog dialog = new MissingTablesDialog(this.getWorkbenchContext(), nonRefreshedTables);
			dialog.show();
		}
	}

	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		return ((MWTable) ((TableNode) selectedNode).getValue()).getDatabase().isConnected();
	}


	// ********** inner class **********

	private static class MissingTablesDialog extends AbstractDialog {
		private Collection missingTables;
	
		public MissingTablesDialog(WorkbenchContext context, Collection missingTables) {
			super(context);
			this.setTitle(this.resourceRepository().getString("ERROR_REFRESHING_TABLES_DIALOG.title"));
			this.missingTables = new TreeSet(missingTables);
		}

		protected Component buildMainPanel() {
			JPanel mainPanel = new JPanel(new GridBagLayout());
			GridBagConstraints contraints = new GridBagConstraints();
		
			// error label
			JLabel errorLabel = new JLabel(this.resourceRepository().getString("ERROR_REFRESHING_TABLES_DIALOG.message"));
			contraints.gridx = 0;
			contraints.gridy = 0;
			contraints.gridwidth = 1;
			contraints.gridheight = 1;
			contraints.weightx = 1;
			contraints.weighty = 0;
			contraints.fill = GridBagConstraints.HORIZONTAL;
			contraints.anchor = GridBagConstraints.EAST;
			contraints.insets = new Insets(0, 0, 0, 0);
			mainPanel.add(errorLabel, contraints);

			// text area - list of missing tables
			JTextArea textArea = new JTextArea();
			textArea.setBorder(BorderFactory.createEmptyBorder());
			textArea.setFont(new Font("dialog", Font.PLAIN, 12));
			textArea.setEditable(false);
			contraints.gridx = 0;
			contraints.gridy = 1;
			contraints.gridwidth = 1;
			contraints.gridheight = 1;
			contraints.weightx = 1;
			contraints.weighty = 1;
			contraints.fill = GridBagConstraints.BOTH;
			contraints.anchor = GridBagConstraints.CENTER;
			contraints.insets = new Insets(1, 0, 0, 0);
			mainPanel.add(new JScrollPane(textArea), contraints);

			// list the names of the missing tables in the text area
			textArea.setText(null);
			for (Iterator stream = this.missingTables.iterator(); stream.hasNext(); ) {
				textArea.append(((MWTable) stream.next()).qualifiedName());
				if (stream.hasNext()) {
					textArea.append(StringTools.CR);
				}
			}

			return mainPanel;
		}

		protected String helpTopicId() {
			return "dialog.tablesNotFound";
		}

	}


	// ********** member class **********

	/**
	 * Allow the user to select which of the tables returned from the
	 * database should be used to refresh our (typically unqualified) table.
	 */
	private static class DuplicateTablesDialog extends AbstractDialog {
		private MWTable table;
		private Vector duplicateExternalTableDescriptions;
		private JList tableList;

		private static final Comparator EXTERNAL_TABLE_DESCRIPTION_COMPARATOR =
			new Comparator() {
				public int compare(Object o1, Object o2) {
					return Collator.getInstance().compare(((ExternalTableDescription) o1).getQualifiedName(), ((ExternalTableDescription) o2).getQualifiedName());
				}
			};
	

		DuplicateTablesDialog(WorkbenchContext context, MWTable table, Collection duplicateExternalTableDescriptions) {
			super(context);
			this.table = table;
			this.setTitle(this.resourceRepository().getString("DUPLICATE_TABLES_DIALOG.title", table.qualifiedName()));
			this.duplicateExternalTableDescriptions = new Vector(duplicateExternalTableDescriptions);
			Collections.sort(this.duplicateExternalTableDescriptions, EXTERNAL_TABLE_DESCRIPTION_COMPARATOR);
			this.getOKAction().setEnabled(false);
		}
		
		ExternalTableDescription selectedExternalTableDescription() {
			return (ExternalTableDescription) this.tableList.getSelectedValue();
		}

		protected Component buildMainPanel() {
			JPanel mainPanel = new JPanel(new GridBagLayout());
			GridBagConstraints contraints = new GridBagConstraints();
		
			// error label
			JLabel errorLabel = new JLabel(this.resourceRepository().getString("DUPLICATE_TABLES_DIALOG.message", this.table.qualifiedName()));
			contraints.gridx = 0;
			contraints.gridy = 0;
			contraints.gridwidth = 1;
			contraints.gridheight = 1;
			contraints.weightx = 1;
			contraints.weighty = 0;
			contraints.fill = GridBagConstraints.HORIZONTAL;
			contraints.anchor = GridBagConstraints.EAST;
			contraints.insets = new Insets(0, 0, 0, 0);
			mainPanel.add(errorLabel, contraints);

			// text area - list of missing tables
			this.tableList = SwingComponentFactory.buildList(this.duplicateExternalTableDescriptions);
			this.tableList.setBorder(BorderFactory.createEmptyBorder());
			this.tableList.setFont(new Font("dialog", Font.PLAIN, 12));
			this.tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.tableList.setCellRenderer(this.buildExternalTableDescriptionCellRenderer());
			this.tableList.addListSelectionListener(this.buildExternalTableDescriptionListSelectionListener());
			contraints.gridx = 0;
			contraints.gridy = 1;
			contraints.gridwidth = 1;
			contraints.gridheight = 1;
			contraints.weightx = 1;
			contraints.weighty = 1;
			contraints.fill = GridBagConstraints.BOTH;
			contraints.anchor = GridBagConstraints.CENTER;
			contraints.insets = new Insets(1, 0, 0, 0);
			mainPanel.add(new JScrollPane(this.tableList), contraints);

			return mainPanel;
		}
		
		private ListCellRenderer buildExternalTableDescriptionCellRenderer() {
			return new SimpleListCellRenderer() {
				protected String buildText(Object value) {
					return ((ExternalTableDescription) value).getQualifiedName();
				}
			};
		}

		void externalTableDescriptionSelectionChanged() {
			this.getOKAction().setEnabled(this.tableList.getSelectedIndex() != -1);
		}

		private ListSelectionListener buildExternalTableDescriptionListSelectionListener() {
			return new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting()) {
						DuplicateTablesDialog.this.externalTableDescriptionSelectionChanged();
					}
				}
			};
		}

		protected String helpTopicId() {
			return "dialog.duplicateTables";
		}

	}
}
