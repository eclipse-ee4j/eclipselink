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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalDescriptor;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



//TODO Search for SESSION
public abstract class ReturningPolicyPropertiesPage extends ScrollablePropertiesPage
{
	
	/**
	 * Creates a new <code>ReturningPolicyPropertiesPage</code>.
	 */
	protected ReturningPolicyPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super( nodeHolder, contextHolder);
	}
	
	private PropertyValueModel buildReturningPolicyHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWTransactionalDescriptor.RETURNING_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				MWDescriptorPolicy policy = ((MWTransactionalDescriptor) this.subject).getReturningPolicy();
				return policy.isActive() ? policy : null;
			}

			protected void setValueOnSubject(Object value) {
				((MWTransactionalDescriptor) value).addReturningPolicy();
			}
		};
	}
	
	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		PropertyValueModel returningPolicyHolder = buildReturningPolicyHolder();

		// Create the container
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5));

		JPanel insertListPanel = buildInsertFieldsPanel( returningPolicyHolder);
		insertListPanel.setPreferredSize( new Dimension( 1, 1));
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		container.add( insertListPanel, constraints);

		JPanel updateListPane = buildUpdateFieldsPanel( returningPolicyHolder);
		updateListPane.setPreferredSize( new Dimension( 1, 1));
		
		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add( updateListPane, constraints);

		return container;
	}
	
	private JPanel buildInsertFieldsPanel( PropertyValueModel returningPolicyHolder)
	{
		JPanel insertFieldPanel = new JPanel( new GridBagLayout());
		insertFieldPanel.setBorder(BorderFactory.createCompoundBorder(
			buildTitledBorder("RETURNING_POLICY_INSERT_FIELD_LABEL"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		AbstractPanel insertFieldsPanel = insertFieldsPanel(returningPolicyHolder);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets     = new Insets( 0, 0, 0, 0);
		
		insertFieldPanel.add( insertFieldsPanel, constraints);
		addPaneForAlignment( insertFieldsPanel);

		return insertFieldPanel;
	}
	
	protected abstract AbstractPanel insertFieldsPanel(PropertyValueModel returningPolicyHolder);
	
	private JPanel buildUpdateFieldsPanel( PropertyValueModel returningPolicyHolder)
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel updateFieldPanel = new JPanel( new GridBagLayout());
		updateFieldPanel.setBorder(BorderFactory.createCompoundBorder(
			buildTitledBorder("RETURNING_POLICY_UPDATE_FIELD_LABEL"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));
		
		AbstractPanel updateFieldsPanel = updateFieldsPanel(returningPolicyHolder);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill			= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets     = new Insets( 0, 0, 0, 0);
		
		updateFieldPanel.add(updateFieldsPanel, constraints);
		addPaneForAlignment(updateFieldsPanel);

		return updateFieldPanel;
	}
	
	protected abstract AbstractPanel updateFieldsPanel(PropertyValueModel returningPolicyHolder);

}
