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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.DoubleClickMouseListener;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel.UpDownOptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalSpecificQueryOptions;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;


abstract class AbstractAttributeItemsPanel extends AbstractPanel {

	private PropertyValueModel queryHolder;
	private AddRemovePanel attributesPanel;
	
	AbstractAttributeItemsPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.queryHolder = buildQueryHolder(queryHolder);
		initializeLayout();
	}

	protected PropertyValueModel buildQueryHolder(PropertyValueModel queryHolder) {
		return queryHolder;
	}
	
	protected abstract String helpTopicId();
	
	private void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.attributesPanel = buildAddRemovePanel();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 5, 5);
		add(this.attributesPanel, constraints);
        
        Collection components = new ArrayList();
        //components.add(this.attributesPanel.getBorder());
        components.add(this.attributesPanel.getComponent());
        components.add(this.attributesPanel);
        new ComponentEnabler(buildAttributesEnabler(), components);		
	}
	
    private ValueModel buildAttributesEnabler() {
        return new PropertyAspectAdapter(buildRelationalSpecificOptionsHolder(), MWRelationalQuery.QUERY_FORMAT_TYPE_PROPERTY) {
            protected Object getValueFromSubject() {
                return Boolean.valueOf(panelEnabled(((MWRelationalSpecificQueryOptions) this.subject).getQueryFormat()));
            }            
        };
    }
        
    private PropertyValueModel buildRelationalSpecificOptionsHolder() {
        return new PropertyAspectAdapter(this.queryHolder) {
            protected Object getValueFromSubject() {
                return ((MWRelationalQuery) this.subject).getRelationalOptions();
            }
        };
    }

    protected abstract boolean panelEnabled(MWQueryFormat queryFormat);
    
    
    
	protected AddRemovePanel buildAddRemovePanel() {
		AddRemoveListPanel panel = 
			new AddRemoveListPanel(
					getApplicationContext(), 
					buildAttributesPanelAdapter(),
					buildAttributesHolder(),
					AddRemovePanel.RIGHT,
					resourceRepository().getString(listTitleKey()));
		panel.setBorder(buildTitledBorder(listTitleKey()));
		panel.setCellRenderer(buildQueryItemCellRenderer());
		SwingComponentFactory.addDoubleClickMouseListener(panel.getComponent(),
		        new DoubleClickMouseListener() {
                    public void mouseDoubleClicked(MouseEvent e) {
                        editSelectedAttribute((MWAttributeItem) AbstractAttributeItemsPanel.this.attributesPanel.getSelectionModel().getSelectedValue());
                    }
                });
		addHelpTopicId(panel, helpTopicId());
		return panel;
	}

	void editSelectedAttribute(MWAttributeItem item) {
	    AttributeItemDialog dialog = buildAttributeItemDialog(item);
	    dialog.show();
    }
	
	abstract AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item);
	
	abstract String listTitleKey();
	
	abstract UpDownOptionAdapter buildAttributesPanelAdapter();

	protected abstract ListValueModel buildAttributesHolder();
	

	private ListCellRenderer buildQueryItemCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return ((MWAttributeItem) value).displayString();
			}
		};
	}
	
	protected PropertyValueModel getQueryHolder() {
		return this.queryHolder;
	}	
	
	protected MWQuery getQuery() {
		return (MWQuery) this.queryHolder.getValue();
	}
	
	
	protected void select(MWQueryItem queryItem) {
		this.attributesPanel.setSelectedValue(queryItem, true);	
	}

}
