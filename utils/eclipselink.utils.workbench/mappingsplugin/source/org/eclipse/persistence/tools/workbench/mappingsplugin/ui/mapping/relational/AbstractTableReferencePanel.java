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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ReferenceCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.ColumnPairsPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db.NewTableReferenceDialog;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


abstract class AbstractTableReferencePanel 
	extends AbstractSubjectPanel
{

	AbstractTableReferencePanel(PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
		super(subjectHolder, contextHolder);
	}

	private ActionListener buildCreateNewReferenceAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractTableReferencePanel.this.createNewReference();
			}
		};
	}

	protected ColumnPairsPanel buildColumnPairsPanel() {
		return new ColumnPairsPanel(this.getWorkbenchContextHolder(), this.buildTableReferenceHolder());
	}
	
	private ListChooser buildTableReferenceChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildExtendedTableReferenceComboBoxModel(),
				this.getWorkbenchContextHolder(),
                this.buildTableReferenceNodeSelector(),
				this.buildTableReferenceChooserDialogBuilder()
			);
		listChooser.setRenderer(buildReferenceListRenderer());
		return listChooser;
	}
	
	protected CachingComboBoxModel buildExtendedTableReferenceComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildTableReferenceHolder(), this.getSubjectHolder()) {
				protected ListIterator listValueFromSubject(Object subject) {
					return AbstractTableReferencePanel.this.orderedReferenceChoices((MWTableReferenceMapping) subject);
				}
			}
		);
	}
	
	protected ListIterator orderedReferenceChoices(MWTableReferenceMapping mapping) {
		return CollectionTools.sort(this.candidateReferences(mapping)).listIterator();
	}
	
	/**
	 * return a list of the candidate references for the specified mapping;
	 * this need not be a ValueModel because it is used in an
	 * IndirectComboBoxModel, which rebuilds the drop-down list *every* time
	 */
	protected Iterator candidateReferences(MWTableReferenceMapping mapping) {
		return mapping.candidateReferences();
	}

	private DefaultListChooserDialog.Builder buildTableReferenceChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("TABLE_REFERENCE_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("TABLE_REFERENCE_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildTableReferenceStringConverter());
		return builder;
	}
	
	private ListCellRenderer buildReferenceListRenderer() {
		return new AdaptableListCellRenderer(new ReferenceCellRendererAdapter(resourceRepository()));
	}
	
	private StringConverter buildTableReferenceStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWReference) o).getName();
			}
		};
	}

	protected PropertyValueModel buildTableReferenceHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWTableReferenceMapping.REFERENCE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTableReferenceMapping) this.subject).getReference();
			}
			protected void setValueOnSubject(Object value) {
				((MWTableReferenceMapping) this.subject).setReference((MWReference) value);
			}
		};
	}
	
    private NodeSelector buildTableReferenceNodeSelector() {
        return new NodeSelector() {       
            public void selectNodeFor(Object item) {
                RelationalProjectNode projectNode = (RelationalProjectNode) navigatorSelectionModel().getSelectedProjectNodes()[0];
                projectNode.selectReference((MWReference) item, getWorkbenchContext());     
            }
        };
    }
    
	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		// Table Reference chooser
		ListChooser tableReferenceChooser = this.buildTableReferenceChooser();
	
		// New Reference button
		JButton newReferenceButton = this.buildButton("TABLE_REFERENCE_NEW_BUTTON");
		newReferenceButton.addActionListener(this.buildCreateNewReferenceAction());

		// Table Reference widgets
		JComponent tableReferenceWidgets = 
			this.buildLabeledComponent(
				"TABLE_REFERENCE_CHOOSER",
				tableReferenceChooser,
				newReferenceButton
			);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);

		this.add(tableReferenceWidgets, constraints);

		// Field Association
		ColumnPairsPanel columnPairsPanel = this.buildColumnPairsPanel();

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 0, 0, 0);

		this.add(columnPairsPanel, constraints);
		this.addPaneForAlignment(columnPairsPanel);

		JButton button = new JButton("Test");
		int height = button.getPreferredSize().height * 4 + 20;
		columnPairsPanel.setPreferredSize(new Dimension(1, height));

		this.addHelpTopicId(this, "mapping.tableReference");
	}


	// ********** build new reference dialog **********

	void createNewReference() {
		NewTableReferenceDialog dialog = NewTableReferenceDialog.buildReferenceDialogAllowSourceAndTargetSelection(
			this.getWorkbenchContext(),
			this.candidateNewReferenceSourceTables(),
			this.candidateNewReferenceTargetTables()
		);
		dialog.setSourceTable(this.defaultNewReferenceSourceTable());
		dialog.setTargetTable(this.defaultNewReferenceTargetTable());

		dialog.show();
		if (dialog.wasCanceled()) {
			return;
		}

		MWTable sourceTable = dialog.getSourceTable();
		MWReference reference = sourceTable.addReference(dialog.getReferenceName(), dialog.getTargetTable());
		reference.setOnDatabase(dialog.isOnDatabase());
		
		this.setReference(reference);
	}
	
	/**
	 * Return the list of tables from which a new reference's source
	 * table can be chosen (in the NewTableReferenceDialog).
	 */
	protected List candidateNewReferenceSourceTables() {
		return this.allTables();
	}

	/**
	 * Return the default source table for building a new reference
	 * (so it can be pre-selected in the NewTableReferenceDialog).
	 */
	protected abstract MWTable defaultNewReferenceSourceTable();
	
	/**
	 * Return the list of tables from which a new reference's target
	 * table can be chosen (in the NewTableReferenceDialog).
	 */
	protected List candidateNewReferenceTargetTables() {
		return this.allTables();
	}

	/**
	 * Return the default target table for building a new reference
	 * (so it can be pre-selected in the NewTableReferenceDialog).
	 */
	protected abstract MWTable defaultNewReferenceTargetTable();
	
	protected void setReference(MWReference reference) {
		((MWTableReferenceMapping) this.subject()).setReference(reference);
	}

	protected List allTables() {
		return CollectionTools.sort(((MWModel) this.subject()).getDatabase().tables());
	}

}
