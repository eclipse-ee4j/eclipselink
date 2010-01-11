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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.FieldTransformerAssociationEditingPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class RelationalFieldTransformerAssociationEditingPanel 
	extends FieldTransformerAssociationEditingPanel
{
	// **************** Constructors ******************************************
	
	RelationalFieldTransformerAssociationEditingPanel(RelationalFieldTransformerAssociationSpec associationSpec, WorkbenchContext context) {
		super(associationSpec, context);
	}
	
	
	// **************** Initialization ****************************************
	
	protected JPanel buildFieldChooserPanel() {
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, offset.left, 0, offset.right));
		
		JLabel fieldLabel = this.buildFieldChooserLabel();
		panel.add(fieldLabel, BorderLayout.LINE_START);
		addAlignLeft(fieldLabel);
		
		ListChooser fieldChooser = this.buildFieldChooser();
		fieldLabel.setLabelFor(fieldChooser);
		panel.add(fieldChooser, BorderLayout.CENTER);

		Spacer spacer = new Spacer();
		panel.add(spacer, BorderLayout.LINE_END);
		addAlignRight(spacer);

		return panel;
	}
	
	private JLabel buildFieldChooserLabel() {
		return buildLabel("FIELD_TRANSFORMER_ASSOCIATION_PANEL_FIELD_CHOOSER");
	}
	
	private ListChooser buildFieldChooser() {
		ListChooser chooser = new ListChooser(this.buildFieldChooserModel());
		chooser.setRenderer(this.buildFieldRenderer());
		return chooser;
	}
	
	private ListCellRenderer buildFieldRenderer() {
		return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(this.resourceRepository()));
	}
	
	private ComboBoxModel buildFieldChooserModel() {
		return new ComboBoxModelAdapter(this.buildFieldChoicesValue(), this.associationSpec().fieldHolder());
	}
	
	private ListValueModel buildFieldChoicesValue() {
		return new SortedListValueModelAdapter(
			new ReadOnlyCollectionValueModel(
				CollectionTools.collection(this.associationSpec().candidateFields())
			)
		);
	}
	
	
	// **************** Convenience *******************************************
	
	private RelationalFieldTransformerAssociationSpec associationSpec() {
		return (RelationalFieldTransformerAssociationSpec) this.associationSpec;
	}
	
	
	// **************** Member classes ****************************************
	
	public static interface RelationalFieldTransformerAssociationSpec
		extends FieldTransformerAssociationSpec
	{
		/** Should return an unchanging holder (only the value may change) */
		PropertyValueModel fieldHolder();
		
		/** The fields that may be chosen for this field transformer association */
		Iterator candidateFields();
		
		/** Return true if a selected field will result in two associations having the same field */
		boolean fieldIsDuplicate();
	}
}
