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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
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
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.TableCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;


public class NewTableReferenceDialog 
	extends AbstractValidatingDialog
{

	//focus component
	private JTextField referenceNameTextField;
	
	private PropertyValueModel onDatabaseHolder;
	
	private ComboBoxModel sourceTablesModel;
	private ComboBoxModel targetTablesModel;

	private boolean allowSourceTableSelection;
	private boolean allowTargetTableSelection;
	
	
	// ************* Public static factory methods ************

	public static NewTableReferenceDialog buildReferenceDialogAllowSourceAndTargetSelection(WorkbenchContext context, List sourceTables, List targetTables) {
		return new NewTableReferenceDialog(context, sourceTables, targetTables);
	}
	
	public static NewTableReferenceDialog buildReferenceDialogDisallowSourceTableSelection(WorkbenchContext context, List sourceTables, List targetTables) {
		return new NewTableReferenceDialog(context, sourceTables, targetTables, false);
	}
	
	public static NewTableReferenceDialog buildReferenceDialogDisallowTargetTableSelection(WorkbenchContext context, List sourceTables, List targetTables) {
		return new NewTableReferenceDialog(context, sourceTables, targetTables, true, false);
	}
	
	
	// ************* Constructors ************
	
	private NewTableReferenceDialog(WorkbenchContext context, List sourceTables, List targetTables) {
		this(context, sourceTables, targetTables, true);
	}
	
	private NewTableReferenceDialog(WorkbenchContext context, List sourceTables, List targetTables, boolean allowSourceTableSelection) {
		this(context, sourceTables, targetTables, allowSourceTableSelection, true);
	}
	
	private NewTableReferenceDialog(WorkbenchContext context, List sourceTables, List targetTables, boolean allowSourceTableSelection, boolean allowTargetTableSelection) {
		super(context);
		this.sourceTablesModel = new DefaultComboBoxModel(sourceTables.toArray());
		this.targetTablesModel = new DefaultComboBoxModel(targetTables.toArray());
		this.allowSourceTableSelection = allowSourceTableSelection;
		this.allowTargetTableSelection = allowTargetTableSelection;
	}
	
	
	// ************ Initialization *************
	
	protected void initialize() {
		super.initialize();		
		setTitle(resourceRepository().getString("NEW_REFERENCE_DIALOG.title"));
		getOKAction().setEnabled(false);
		//setPreferredSize(275, 300);		
	}
	
	protected Component initialFocusComponent() {
		return this.referenceNameTextField;
	}
	
	protected String helpTopicId() {
		return "dialog.newReference";
	}

	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
			
		//	reference name
		JLabel referenceNameLabel = SwingComponentFactory.buildLabel("enterNameOfNewReference", resourceRepository());
		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		panel.add(referenceNameLabel, constraints);
		
		this.referenceNameTextField = buildReferenceNameTextField();
		helpManager().addTopicID(this.referenceNameTextField, helpTopicId() + ".name");
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(1, 0, 0, 0);
		panel.add(this.referenceNameTextField, constraints);
		referenceNameLabel.setLabelFor(this.referenceNameTextField);
		
		//	source table
		JLabel sourceTableLabel = SwingComponentFactory.buildLabel("selectTheSourceTable", resourceRepository());
		constraints.gridx 		= 0;
		constraints.gridy 		= 2;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(10, 0, 0, 0);
		panel.add(sourceTableLabel, constraints);
		
		JComboBox sourceTableComboBox = buildSourceTableComboBox();
		helpManager().addTopicID(sourceTableComboBox, helpTopicId() + ".sourceTable");
		sourceTableLabel.setLabelFor(sourceTableComboBox);
		constraints.gridx 		= 0;
		constraints.gridy 		= 3;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(1, 0, 0, 0);
		panel.add(sourceTableComboBox, constraints);
		
		//	target table
		JLabel targetTableLabel = SwingComponentFactory.buildLabel("selectTheTargetTable", resourceRepository());
		constraints.gridx 		= 0;
		constraints.gridy 		= 4;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(10, 0, 0, 0);
		panel.add(targetTableLabel, constraints);
		
		JComboBox targetTableComboBox = buildTargetTableComboBox();
		helpManager().addTopicID(targetTableComboBox, helpTopicId() + ".targetTable");
		targetTableLabel.setLabelFor(targetTableComboBox);
		constraints.gridx 		= 0;
		constraints.gridy 		= 5;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(1, 0, 0, 0);
		panel.add(targetTableComboBox, constraints);
		
		// on database check box
		JCheckBox onDatabaseCheckBox = buildOnDatabaseCheckBox();
		helpManager().addTopicID(onDatabaseCheckBox, helpTopicId() + ".onDatabase");
		constraints.gridx 		= 0;
		constraints.gridy 		= 6;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		panel.add(onDatabaseCheckBox, constraints);
		
		return panel;	
	}
	



	// ********** Reference name **********
	
	private JTextField buildReferenceNameTextField() {
		JTextField textField = new JTextField();
		Document document = new RegexpDocument(RegexpDocument.RE_SQL_RELATED);
		document.addDocumentListener(buildReferenceNameDocumentListener());
		textField.setDocument(document);
		
		return textField;		
	}

	private DocumentListener buildReferenceNameDocumentListener() {
		return new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateOKButton();
				updateErrorMessage();
			}
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
		};
	}


	// ********** source table **********

	private JComboBox buildSourceTableComboBox() {
		ListChooser listChooser = new ListChooser(this.sourceTablesModel);
		listChooser.setRenderer(buildTableListCellRenderer());
		listChooser.addActionListener(buildTableListener());
		listChooser.setEnabled(this.allowSourceTableSelection);

		return listChooser;
	}
	
	private ActionListener buildTableListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateReferenceName();
				updateOKButton();
			}
		};
	}
	
	
	// ********** target table **********

	private JComboBox buildTargetTableComboBox() {
		ListChooser listChooser = new ListChooser(this.targetTablesModel);		
		listChooser.setRenderer(buildTableListCellRenderer());
		listChooser.addActionListener(buildTableListener());
		listChooser.setEnabled(this.allowTargetTableSelection);
		
		return listChooser;
	}
	
	private ListCellRenderer buildTableListCellRenderer() {
		return new AdaptableListCellRenderer(new TableCellRendererAdapter(this.resourceRepository()));
	}


	// ********** on database **********
	
	private JCheckBox buildOnDatabaseCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setModel(buildOnDatabaseCheckBoxModel());
		checkBox.setText(resourceRepository().getString("onDatabase"));
		checkBox.setMnemonic(resourceRepository().getMnemonic("onDatabase"));
		return checkBox;
	}
	
	private ButtonModel buildOnDatabaseCheckBoxModel() {
		return new CheckBoxModelAdapter(buildOnDatabaseHolder());
	}
	
	private PropertyValueModel buildOnDatabaseHolder() {
		this.onDatabaseHolder = new SimplePropertyValueModel(Boolean.FALSE);
		return this.onDatabaseHolder;
	}
	

	// ************ API ************
	
	public String getReferenceName() {
		return this.referenceNameTextField.getText();
	}

	public MWTable getSourceTable() {
		return (MWTable) this.sourceTablesModel.getSelectedItem();
	}
	
	public void setSourceTable(MWTable initialSourceTable) {
		this.sourceTablesModel.setSelectedItem(initialSourceTable);
	}
	
	public MWTable getTargetTable() {
		return (MWTable) this.targetTablesModel.getSelectedItem();
	}
	
	public void setTargetTable(MWTable initialTargetTable) {
		this.targetTablesModel.setSelectedItem(initialTargetTable);
	}

	public boolean isOnDatabase() {
		return ((Boolean) this.onDatabaseHolder.getValue()).booleanValue();
	}
		
	
	// ************* Behavior ***********
	
	void updateOKButton() {
		boolean enableOKButton = 
			referenceNameIsSpecified()
			&& referenceNameIsValid()
			&& getTargetTable() != null 
			&& getSourceTable() != null;
		
		getOKAction().setEnabled(enableOKButton);
	}
	
	void updateErrorMessage() {
		if (referenceNameIsValid()) {
			clearErrorMessage();
		}
		else {
			setErrorMessageKey("REFERENCE_NAME_MUST_BE_UNIQUE_ERROR");
		}
	}
	
	void updateReferenceName() {
		String referenceName = this.getReferenceName();
		MWTable sourceTable = getSourceTable();
		MWTable targetTable = getTargetTable();
		
		if (sourceTable != null) 
		{
			referenceName = sourceTable.getShortName() + "_";
			
			if (targetTable != null)
				referenceName = referenceName + targetTable.getShortName();
		}
		else if (targetTable != null)
			referenceName = "_" + targetTable.getShortName();
		
		this.referenceNameTextField.setText(referenceName);
		this.referenceNameTextField.selectAll();
		this.referenceNameTextField.requestFocus();
	}
	
	private boolean referenceNameIsSpecified() {
		return getReferenceName() != null && getReferenceName().length() != 0;
	}
	
	private boolean referenceNameIsValid() {
		if (getSourceTable() == null)
			return true;
		
		for (Iterator references = getSourceTable().references(); references.hasNext(); ) {
			MWReference reference = (MWReference) references.next();
			String name = reference.getName();
			Collator ignoreCaseCollator = Collator.getInstance();
			ignoreCaseCollator.setStrength(Collator.PRIMARY);
			
			if (ignoreCaseCollator.compare(name, getReferenceName()) == 0) 
				return false;
		}
		
		return true;
	}
	
	protected void prepareToShow() {
		updateReferenceName();
		super.prepareToShow();
	}

}
