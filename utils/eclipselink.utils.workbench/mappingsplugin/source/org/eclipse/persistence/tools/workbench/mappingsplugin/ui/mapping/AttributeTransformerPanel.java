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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyCollectionValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;


public final class AttributeTransformerPanel 
	extends AbstractSubjectPanel
{
	// **************** Constructors ******************************************
	
	/** Expects a MWTransformationMapping object */
	public AttributeTransformerPanel(ValueModel transformationMappingHolder, WorkbenchContextHolder contextHolder) {
		super(transformationMappingHolder, contextHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initializeLayout() {
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// label
		JLabel label = this.buildAttributeTransformerLabel();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		this.add(label, constraints);
		this.addAlignLeft(label);
		
		// transformer component
		Component component = this.buildAttributeTransformerComponent();
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		this.add(component, constraints);
		
		// edit button
		JButton button = this.buildAttributeTransformerEditButton();
		constraints.gridx		= 2;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);
		this.add(button, constraints);
		this.addAlignRight(button);
		label.setLabelFor(button);
		SwingComponentFactory.updateButtonAccessibleName(label, button);
		
		this.addHelpTopicId(this, "mapping.transformation.attributeTransformer");
	}
	
	private JLabel buildAttributeTransformerLabel() {
		return SwingComponentFactory.buildLabel("ATTRIBUTE_TRANSFORMER_LABEL", this.resourceRepository());
	}
	
	private Component buildAttributeTransformerComponent() {
		// Using a single element list in order to use JList's rendering ability
		JList list = SwingComponentFactory.buildList(buildAttributeTransformerListModel());
		list.setCellRenderer(buildTransformerListCellRenderer());
		list.setDoubleBuffered(true);
		list.setVisibleRowCount(1);
		list.setPreferredSize(new Dimension(0, 0));
		// wrap in a scrollpane so that the border looks like a text field's border
		return new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	private ListModel buildAttributeTransformerListModel() {
		// Using a single element list model
		return new ListModelAdapter(buildAttributeTransformerCollectionValue());
	}
	
	private CollectionValueModel buildAttributeTransformerCollectionValue() {
		// Using a single element collection model
		return new PropertyCollectionValueModelAdapter(buildAttributeTransformerPropertyValue());
	}
	
	private PropertyValueModel buildAttributeTransformerPropertyValue() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWTransformationMapping.ATTRIBUTE_TRANSFORMER_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWTransformationMapping) this.subject).getAttributeTransformer();
			}
		};
	}
	
	private ListCellRenderer buildTransformerListCellRenderer() {
		return new AdaptableListCellRenderer(new TransformerCellRendererAdapter(this.resourceRepository()));
	}
	
	private JButton buildAttributeTransformerEditButton() {
		JButton button = new JButton(this.resourceRepository().getString("ATTRIBUTE_TRANSFORMER_EDIT_BUTTON"));
		button.addActionListener(buildAttributeTransformerEditAction());
		return button;
	}
	
	private ActionListener buildAttributeTransformerEditAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				MWTransformationMapping transformationMapping = 
					(MWTransformationMapping) AttributeTransformerPanel.this.subject();
				WorkbenchContext context = 
					AttributeTransformerPanel.this.getWorkbenchContext();
				TransformerEditingDialog.promptToEditAttributeTransformer(transformationMapping, context);
			}
		};
	}
}
