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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWRootEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.ParametersQuickViewSection;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueriesPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryGeneralPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QuickViewPanel;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


public final class EisQueriesPropertiesPage 
	extends QueriesPropertiesPage
{

	private EisQueryPanel queryPropertiesPanel;
    private JComponent emptyPanel;
	
	public EisQueriesPropertiesPage(PropertyValueModel relationalDescriptorNodeHolder,
	                                WorkbenchContextHolder contextHolder) {
		super(relationalDescriptorNodeHolder, contextHolder);
	}


	protected Component buildPage() {
		setName(resourceRepository().getString("QUERIES_PANEL_NAME"));

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();
						
		JLabel queriesLabel = buildLabel("QUERIES_LIST");

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(queriesLabel, constraints);

		// Create the list
		AddRemoveListPanel queriesListPanel = buildQueriesListPanel();
	
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 2;
		constraints.weightx    = 0;
		constraints.weighty    = .3;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		panel.add(queriesListPanel, constraints);

		// Quick View
		QuickViewPanel quickView = buildQuickViewPanel();

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = .7;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(quickView, constraints);

		// Query Type label
		JLabel queryVarietyLabel = buildLabel("QUERY_VARIETY_LABEL");

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 5, 0, 0);

		panel.add(queryVarietyLabel, constraints);

		// Query Type label
		JTextField queryVarietyField = new JTextField(buildQueryVarietyDocumentAdapter(), null, 1);
		queryVarietyField.setEditable(false);

		constraints.gridx      = 2;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 5, 0, 0);

		panel.add(queryVarietyField, constraints);
		queryVarietyLabel.setLabelFor(queryVarietyField);

        // Properties pane
        this.queryPropertiesPanel = new EisQueryPanel(getQueryHolder(), queriesListPanel.getSelectionModel(), getWorkbenchContextHolder());
        this.emptyPanel = buildEmptyPanel();

        SwitcherPanel queriesPanel = new SwitcherPanel(getQueryHolder(), buildQueryTypeTransformer());

        constraints.gridx      = 1;
        constraints.gridy      = 1;
        constraints.gridwidth  = 2;
        constraints.gridheight = 3;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.anchor     = GridBagConstraints.CENTER;
        constraints.insets     = new Insets(10, 5, 0, 0);

        panel.add(queriesPanel, constraints);
        
        // Properties panel

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 3;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(10, 5, 0, 0);

		
		addHelpTopicId(panel, helpTopicId());
		return panel;
	}		

	protected PropertyValueModel buildQueryManagerHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWRootEisDescriptor) this.subject).getQueryManager();
			}
		};
	}
	
	protected QueryGeneralPanel getQueryGeneralPanel() {
		return this.queryPropertiesPanel.getQueryGeneralPanel();
	}
	
	protected JTabbedPane getQueryTabbedPane() {
		return this.queryPropertiesPanel.getQueryTabbedPane();
	}
	
	private QuickViewPanel buildQuickViewPanel() {
		return new QuickViewPanel(getQueryHolder(), getWorkbenchContextHolder()) {			
			protected QuickViewSectionFactory buildSectionFactory(Node node) {
				return new QuickViewSectionFactory() {
					public QuickViewSection[] buildSections() {
						return new QuickViewSection[] {
								new ParametersQuickViewSection(
										EisQueriesPropertiesPage.this, 
										resourceRepository(), 
										getQueryHolder())
						};
					}
				};
			}
		};
	}
    
    private Transformer buildQueryTypeTransformer() {
        return new Transformer() {
            public Object transform(Object o) {
                if (o == null) {
                    return EisQueriesPropertiesPage.this.emptyPanel;
                }
                return EisQueriesPropertiesPage.this.queryPropertiesPanel;
            }
        };
    }	
}
