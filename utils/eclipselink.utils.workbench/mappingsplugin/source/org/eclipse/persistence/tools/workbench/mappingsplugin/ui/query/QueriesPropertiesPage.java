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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog.Builder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog.DocumentFactory;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QuickViewPanel.QuickViewItem;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



public abstract class QueriesPropertiesPage 
	extends ScrollablePropertiesPage
{
	private PropertyValueModel queryManagerHolder;
	private PropertyValueModel queryHolder;
	private CollectionValueModel queriesHolder;
	
	
	protected QueriesPropertiesPage(PropertyValueModel relationalDescriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(relationalDescriptorNodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.queryManagerHolder = buildQueryManagerHolder();
		this.queryHolder = new SimplePropertyValueModel();
		this.queriesHolder = buildQueriesHolder();
	}
	
	protected abstract PropertyValueModel buildQueryManagerHolder();
	
	
	// ********** queries ************

	protected ListCellRenderer buildQueriesListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return ((MWQuery) value).signature();
			}
		};
	}

	private ListValueModel buildItemListValueModelAdapter() {
		return new ItemPropertyListValueModelAdapter(buildSortedQueryListValueModelAdapter(), MWQuery.SIGNATURE_PROPERTY);
	}

	private ListValueModel buildSortedQueryListValueModelAdapter() {
		return new SortedListValueModelAdapter(buildItemNameListValueModelAdapter());
	}
	
	private ListValueModel buildItemNameListValueModelAdapter() {
		return new ItemPropertyListValueModelAdapter(this.queriesHolder, MWQuery.NAME_PROPERTY);
	}
	
	private CollectionValueModel buildQueriesHolder() {
		return new CollectionAspectAdapter(this.queryManagerHolder, MWQueryManager.QUERY_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWQueryManager) this.subject).queries();
			}
			protected int sizeFromSubject() {
				return ((MWQueryManager) this.subject).queriesSize();
			}
		};
	}

	protected ListSelectionListener buildQueryListSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					ObjectListSelectionModel listSelectionModel = (ObjectListSelectionModel) e.getSource();
					Object[] values = listSelectionModel.getSelectedValues();
					if (values.length == 1) {
						queryHolder.setValue(values[0]);
					}
					else {
						queryHolder.setValue(null);
					}
				}
			}
		};
	}
	
	protected AddRemoveListPanel buildQueriesListPanel() {
		AddRemoveListPanel queriesListPanel = new AddRemoveListPanel(
			getApplicationContext(),
			buildAddRemoveListPanelAdapter(),
			buildItemListValueModelAdapter(),
			resourceRepository().getString("NAMED_QUERIES_LIST"));
		queriesListPanel.setCellRenderer(buildQueriesListCellRenderer());
		queriesListPanel.addListSelectionListener(buildQueryListSelectionListener());
				
		return queriesListPanel;
	}
	
	protected AddRemoveListPanel.OptionAdapter buildAddRemoveListPanelAdapter() {
		return new AddRemoveListPanel.OptionAdapter() {
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				promptToAddQuery(listSelectionModel);
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				removeSelectedQueries(listSelectionModel);
			}
			
			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
				promptToRenameQuery(listSelectionModel);
			}
			
			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				return listSelectionModel.getSelectedValuesSize() == 1;
			}

			public String optionalButtonKey() {
				return "RENAME_BUTTON";
			}
		};
	}

	protected void promptToAddQuery(ObjectListSelectionModel listSelectionModel) {
		MWMappingDescriptor descriptor = getQueryManager().getOwningDescriptor();
		
		AddQueryDialog dlg;
		dlg = new AddQueryDialog(getWorkbenchContext(), descriptor.getTransactionalPolicy().getQueryManager().supportsReportQueries());
		dlg.show();
		if (dlg.wasConfirmed()) {
			String queryType = dlg.getQueryType();
			String queryName = dlg.getQueryName();
			MWQuery newQuery;
			if (queryType == MWQuery.READ_OBJECT_QUERY) {
				newQuery = getQueryManager().addReadObjectQuery(queryName);
			}
			else if (queryType == MWQuery.READ_ALL_QUERY) {
				newQuery = getQueryManager().addReadAllQuery(queryName);
			}
			else /*(queryType == MWQuery.REPORT_QUERY)*/ {
				newQuery = ((MWRelationalQueryManager) getQueryManager()).addReportQuery(queryName);
			}
			
			listSelectionModel.setSelectedValue(newQuery);
		}
	}
	
	protected void removeSelectedQueries(ObjectListSelectionModel listSelectionModel)  {
		Iterator queries = CollectionTools.iterator(listSelectionModel.getSelectedValues());
				
		while(queries.hasNext()) {
			getQueryManager().removeQuery((MWQuery) queries.next());
		}
	}
			
	public void promptToRenameQuery(ObjectListSelectionModel listSelectionModel) {
		final MWQuery selectedQuery = (MWQuery) listSelectionModel.getSelectedValue();
			Builder builder = new Builder(){
				public String getTitle() {
					return resourceRepository().getString("RENAME_QUERY_DIALOG.title");
				}
				public String getTextFieldDescription() {
					return resourceRepository().getString("RENAME_QUERY_DIALOG.message");
				}
				public String getOriginalName() {
					return selectedQuery.getName();
				}
				public String getHelpTopicId() {
					return "descriptor.queryManager.namedQueries";
				}
				protected DocumentFactory buildDefaultDocumentFactory() {
					return new DocumentFactory() {
						public Document buildDocument() {
							return new RegexpDocument(RegexpDocument.RE_METHOD);
						}
					};
				}
			
			};
			NewNameDialog dialog = builder.buildDialog(getWorkbenchContext());
			dialog.show();
			if (dialog.wasConfirmed()) {
				selectedQuery.setName(dialog.getNewName());
				listSelectionModel.setSelectedValue(selectedQuery);
			}
		
	}

	protected final MWQueryManager getQueryManager() {
		return (MWQueryManager) getQueryManagerHolder().getValue();
	}

	protected String helpTopicId() {
		return "descriptor.queries";
	}
	
	protected PropertyValueModel getQueryManagerHolder() {
		return this.queryManagerHolder;
	}

	protected PropertyValueModel getQueryHolder() {
		return this.queryHolder;
	}
	
	protected QuickViewItem buildQueryParameterQuickViewItem(MWQueryItem queryItem) {
		return new QueryQuickViewItem(queryItem) {
			
			public void select() {
				selectGeneralPanel();
				getQueryGeneralPanel().selectParameter((MWQueryParameter) getValue());
			}
			
			public String displayString() {
				MWQueryParameter parameter = (MWQueryParameter) getValue();

				return resourceRepository().getString(
					"QUICK_VIEW_PARAMETER_LABEL",
					parameter.getName(),
					ClassTools.shortNameForClassNamed(parameter.getType().getName()));
			}
		};
	}	

	
	
	protected abstract QueryGeneralPanel getQueryGeneralPanel();
	
	protected abstract JTabbedPane getQueryTabbedPane();
	
	protected void selectGeneralPanel() {
		getQueryTabbedPane().setSelectedComponent(getQueryGeneralPanel());
	}
    
    protected JComponent buildEmptyPanel() {
        GridBagConstraints constraints = new GridBagConstraints();
        
        JPanel container = new JPanel(new GridBagLayout());

        JPanel emptyPanel = new JPanel();

        constraints.gridx           = 0;
        constraints.gridy           = 0;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 1;
        constraints.fill            = GridBagConstraints.BOTH;
        constraints.anchor      = GridBagConstraints.CENTER;
        constraints.insets      = new Insets(3, 3, 3, 3);

        container.add(emptyPanel, constraints);

        // Add the tabbed pane in order to get the border
        JTabbedPane pane = new JTabbedPane(SwingConstants.TOP);
        pane.setFocusable(false);
        pane.setRequestFocusEnabled(false);

        constraints.gridx           = 0;
        constraints.gridy           = 0;
        constraints.gridwidth   = 1;
        constraints.gridheight  = 1;
        constraints.weightx     = 1;
        constraints.weighty     = 1;
        constraints.fill            = GridBagConstraints.BOTH;
        constraints.anchor      = GridBagConstraints.CENTER;
        constraints.insets      = new Insets(0, 0, 0, 0);

        container.add(pane, constraints);

        return container;
    }

	protected final Document buildQueryVarietyDocumentAdapter() {
		return new DocumentAdapter(buildVarietyTypeHolder());
	}

	private PropertyValueModel buildVarietyTypeHolder() {
		return new TransformationPropertyValueModel(getQueryHolder()) {
			protected Object transform(Object value) {
				MWQuery query = (MWQuery) value;

				if ((query == null) || (getQueryManager() == null)) {
					return resourceRepository().getString("QUERY_VARIETY_NONE_SELECTED");
				}
				return resourceRepository().getString("QUERY_VARIETY_TOPLINK_NAMED_QUERY");
			}
		};
	}
}
