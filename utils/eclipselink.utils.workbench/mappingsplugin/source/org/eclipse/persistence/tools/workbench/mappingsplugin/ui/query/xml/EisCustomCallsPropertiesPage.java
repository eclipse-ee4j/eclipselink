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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ListIterator;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisQueryManager;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;


public final class EisCustomCallsPropertiesPage 
	extends AbstractSubjectPanel
{
	private PropertyValueModel selectedInteractionHolder;
	
	public EisCustomCallsPropertiesPage(ValueModel queryManagerHolder, WorkbenchContextHolder contextHolder) {
		super(queryManagerHolder, contextHolder);
	}
	
	protected void initialize(ValueModel subjectHolder) {
		super.initialize(subjectHolder);
		this.selectedInteractionHolder = new SimplePropertyValueModel();
	}
	
	protected void initializeLayout() {
		// TODO - wtf?
		this.setName("Calls");
		
		this.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(this.buildPage());
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		add(scrollPane, BorderLayout.CENTER);
	}
	
	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// interaction list
		JPanel interactionListPanel = this.buildInteractionListPanel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0.3;
		constraints.weighty 	= 1;
		constraints.fill 		= GridBagConstraints.BOTH;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(5, 5, 5, 5);
		panel.add(interactionListPanel, constraints);
		
		// interaction panel
		JPanel interactionPanel = this.buildInteractionPanel();
		constraints.gridx 		= 1;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0.7;
		constraints.weighty 	= 1;
		constraints.fill 		= GridBagConstraints.BOTH;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		panel.add(interactionPanel, constraints);
		
		return panel;
	}
	
	
	// **************** Interaction list **************************************
	
	private JPanel buildInteractionListPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(this.buildInteractionList());
		scrollPane.setMinimumSize(new Dimension(1, 1));
		scrollPane.setPreferredSize(new Dimension(1, 1));
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}
	
	private JList buildInteractionList() {
		JList list = SwingComponentFactory.buildList(this.buildInteractionListModel());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this.buildInteractionListSelectionListener(list));
		list.setCellRenderer(this.buildInteractionListCellRenderer());
		return list;
	}
	
	private ListModel buildInteractionListModel() {
		return new ListModelAdapter(this.buildInteractionListValue());
	}
	
	private ListValueModel buildInteractionListValue() {
		return new ListAspectAdapter(this.getSubjectHolder()) {
			protected ListIterator getValueFromSubject() {
				return ((MWEisQueryManager) this.subject).interactions();
			}
		};
	}
	
	private ListSelectionListener buildInteractionListSelectionListener(final JList interactionList) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				else {
					EisCustomCallsPropertiesPage.this.selectedInteractionHolder.setValue(interactionList.getSelectedValue());
				}
			}
		};
	}
	
	private ListCellRenderer buildInteractionListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return EisCustomCallsPropertiesPage.this.buildDisplayText((MWEisInteraction) value);
			}
		};
	}
	
	private String buildDisplayText(MWEisInteraction interaction) {
		MWEisQueryManager queryManager = (MWEisQueryManager) this.subject();
		String key = null;
		
		if (interaction == queryManager.getInsertInteraction()) {
			key = "INSERT_INTERACTION_NAME";
		}
		else if (interaction == queryManager.getUpdateInteraction()) {
			key = "UPDATE_INTERACTION_NAME";
		}
		else if (interaction == queryManager.getDeleteInteraction()) {
			key = "DELETE_INTERACTION_NAME";
		}
		else if (interaction == queryManager.getReadObjectInteraction()) {
			key = "READ_OBJECT_INTERACTION_NAME";
		}
		else if (interaction == queryManager.getReadAllInteraction()) {
			key = "READ_ALL_INTERACTION_NAME";
		}
		else if (interaction == queryManager.getDoesExistInteraction()) {
			key = "DOES_EXIST_INTERACTION_NAME";
		}
		else {
			throw new IllegalArgumentException("Illegal interaction: " + interaction);
		}
		
		return this.resourceRepository().getString(key);
	}
	
	
	// **************** Interaction panel *************************************
	
	private JPanel buildInteractionPanel() {
		return new InteractionPanel(this.getApplicationContext(), this.selectedInteractionHolder, this.buildEnablerHolder());
	}

	private PropertyValueModel buildEnablerHolder() {
		return new TransformationPropertyValueModel(this.selectedInteractionHolder) {
			protected Object transform(Object value) {
				if (value == null)
					return null;

				return Boolean.valueOf(value != null);
			}
		};
	}

	protected String helpTopicId() {
		return "descriptor.queryManager.customSQL";
	}
}
