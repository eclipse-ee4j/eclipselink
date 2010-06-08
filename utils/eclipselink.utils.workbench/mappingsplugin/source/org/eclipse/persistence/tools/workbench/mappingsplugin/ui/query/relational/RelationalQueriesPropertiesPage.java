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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ListIterator;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBatchReadItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWGroupingItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWJoinedItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWOrderableQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.Ordering;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.AbstractQuickViewSection;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.ParametersQuickViewSection;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueriesPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryGeneralPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QuickViewPanel;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


public final class RelationalQueriesPropertiesPage 
	extends QueriesPropertiesPage
{
	private JComponent emptyPanel;
	private RelationalReadAllQueryPanel readAllQueryPanel;
	private RelationalReadObjectQueryPanel readObjectQueryPanel;
	private ReportQueryPanel reportQueryPanel;

	public RelationalQueriesPropertiesPage(PropertyValueModel relationalDescriptorNodeHolder, WorkbenchContextHolder contextHolder) {
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
		
		// Named Queries list
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

		// Variety label
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

		// Variety label
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
		this.readAllQueryPanel = new RelationalReadAllQueryPanel(getQueryHolder(), queriesListPanel.getSelectionModel(), getWorkbenchContextHolder());
		this.readObjectQueryPanel = new RelationalReadObjectQueryPanel(getQueryHolder(), queriesListPanel.getSelectionModel(), getWorkbenchContextHolder());
		this.reportQueryPanel = new ReportQueryPanel(getQueryHolder(), queriesListPanel.getSelectionModel(), getWorkbenchContextHolder());
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

		addHelpTopicId(panel, helpTopicId());		
		return panel;
	}

	private QuickViewPanel buildQuickViewPanel() {
		return new RelationalQuickViewPanel(getQueryHolder(), getWorkbenchContextHolder());
	}
	
	protected PropertyValueModel buildQueryManagerHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWTableDescriptor) this.subject).getQueryManager();
			}
		};
	}

	private Transformer buildQueryTypeTransformer() {
		return new Transformer() {
			public Object transform(Object o) {
				if (o instanceof MWReportQuery) {
					return RelationalQueriesPropertiesPage.this.reportQueryPanel;
				}
				else if (o instanceof MWRelationalReadAllQuery) {
					return RelationalQueriesPropertiesPage.this.readAllQueryPanel;
				}
				else if (o instanceof MWRelationalReadObjectQuery) {
					return RelationalQueriesPropertiesPage.this.readObjectQueryPanel;
				}
				return RelationalQueriesPropertiesPage.this.emptyPanel;
			}
		};
	}
	
	protected QuickViewPanel.QuickViewItem buildReportAttributeQuickViewItem(MWReportAttributeItem queryItem) {
		return this.reportQueryPanel.buildReportAttributeQuickViewItem(queryItem);
	}
	
	protected QuickViewPanel.QuickViewItem buildBatchReadAttributeQuickViewItem(MWBatchReadItem queryItem) {
		return this.readAllQueryPanel.buildBatchReadAttributeQuickViewItem(queryItem);		
	}
	
	protected QuickViewPanel.QuickViewItem buildJoinedAttributeQuickViewItem(MWJoinedItem queryItem) {
		if (getQueryHolder().getValue() instanceof MWReadAllQuery) {
			return this.readAllQueryPanel.buildJoinedAttributeQuickViewItem(queryItem);
		}
		else if(getQueryHolder().getValue() instanceof MWReadObjectQuery) {
			return this.readObjectQueryPanel.buildJoinedAttributeQuickViewItem(queryItem);
		}
		throw new IllegalArgumentException();
	}
	
	protected QuickViewPanel.QuickViewItem buildOrderingAttributeQuickViewItem(Ordering queryItem) {
		if (getQueryHolder().getValue() instanceof MWReadAllQuery) {
			return this.readAllQueryPanel.buildOrderingAttributeQuickViewItem(queryItem);
		}
		else if(getQueryHolder().getValue() instanceof MWReportQuery) {
			return this.reportQueryPanel.buildOrderingAttributeQuickViewItem(queryItem);
		}
		throw new IllegalArgumentException();
	}
	
	protected QuickViewPanel.QuickViewItem buildGroupingAttributeQuickViewItem(MWGroupingItem queryItem) {
		return this.reportQueryPanel.buildGroupingAttributeQuickViewItem(queryItem);		
	}
	
		
	protected void selectReportQueryAttributesTab() {
		this.reportQueryPanel.selectReportQueryAttributesTab();
	}
	
	protected void selectReportQueryGroupingOrderingTab() {
		this.reportQueryPanel.selectReportQueryGroupingOrderingTab();
	}	
	
	protected void selectBatchReadAttributesTab() {
		this.readAllQueryPanel.selectQueryOptimizationTab();
	}
	
	protected void selectJoinedAttributesTab() {
		if (getQueryHolder().getValue() instanceof MWReadAllQuery) {
			this.readAllQueryPanel.selectQueryOptimizationTab();
		}
		else {
			this.readObjectQueryPanel.selectQueryOptimizationTab();			
		}
	}
	
	protected void selectOrderingTab() {
		if (getQueryHolder().getValue() instanceof MWReadAllQuery) {
			this.readAllQueryPanel.selectQueryOrderingTab();
		}
		else if (getQueryHolder().getValue() instanceof MWReportQuery) {
			this.reportQueryPanel.selectReportQueryGroupingOrderingTab();
		}
	}
	
	protected QueryGeneralPanel getQueryGeneralPanel() {
		if (getQueryHolder().getValue() instanceof MWReadAllQuery) {
			return this.readAllQueryPanel.getQueryGeneralPanel();
		}
		else if (getQueryHolder().getValue() instanceof MWReadObjectQuery) {
			return this.readObjectQueryPanel.getQueryGeneralPanel();
		}
		else {
			return this.reportQueryPanel.getQueryGeneralPanel();
		}
	}
	
	protected JTabbedPane getQueryTabbedPane() {
		if (getQueryHolder().getValue() instanceof MWReadAllQuery) {
			return this.readAllQueryPanel.getQueryTabbedPane();
		}
		else if (getQueryHolder().getValue() instanceof MWReadObjectQuery) {
			return this.readObjectQueryPanel.getQueryTabbedPane();
		}
		else {
			return this.reportQueryPanel.getQueryTabbedPane();
		}
	}
	
	
	// **************** inner classes *********************

	private class RelationalQuickViewPanel extends QuickViewPanel {

		RelationalQuickViewPanel(ValueModel subjectHolder, WorkbenchContextHolder contextHolder) {
			super(subjectHolder, contextHolder);
		}

		protected QuickViewSectionFactory buildSectionFactory(Node node) {
			if (node instanceof MWReportQuery)
				return buildReportQueryQuickViewSectionFactory();

			if (node instanceof MWRelationalReadAllQuery)
				return buildReadAllQueryQuickViewSectionFactory();

			if (node instanceof MWRelationalReadObjectQuery)
				return buildReadObjectQueryQuickViewSectionFactory();

			throw new IllegalArgumentException();
		}
		
		private PropertyValueModel buildAbstractRelationalReadQueryHolder() {
			return new TransformationPropertyValueModel((PropertyValueModel) getSubjectHolder()) {
				protected Object transform(Object value) {
					return value instanceof MWAbstractRelationalReadQuery ? value : null;
				}
			};
		}

		private PropertyValueModel buildOrderableQueryHolder() {
			return new TransformationPropertyValueModel((PropertyValueModel) getSubjectHolder()) {
				protected Object transform(Object value) {
					return value instanceof MWOrderableQuery ? value : null;
				}
			};
		}

		private PropertyValueModel buildRelationalReadAllQueryHolder() {
			return new TransformationPropertyValueModel((PropertyValueModel) getSubjectHolder()) {
				protected Object transform(Object value) {
					return value instanceof MWRelationalReadAllQuery ? value : null;
				}
			};
		}

		private PropertyValueModel buildReportQueryHolder() {
			return new TransformationPropertyValueModel((PropertyValueModel) getSubjectHolder()) {
				protected Object transform(Object value) {
					return value instanceof MWReportQuery ? value : null;
				}
			};
		}

		
		private QuickViewSectionFactory buildReadAllQueryQuickViewSectionFactory() {
			return new QuickViewSectionFactory() {
				public QuickViewSection[] buildSections() {
					return new QuickViewSection[] {
							new ParametersQuickViewSection(
									RelationalQueriesPropertiesPage.this, 
									resourceRepository(), 
									getSubjectHolder()),
							new QuickViewOrderingSection(), 
							new QuickViewBatchReadAttributesSection(), 
							new QuickViewJoinedAttributesSection() 
					};
				}
			};
		}

		private QuickViewSectionFactory buildReadObjectQueryQuickViewSectionFactory() {
			return new QuickViewSectionFactory() {
				public QuickViewSection[] buildSections() {
					return new QuickViewSection[] {
							new ParametersQuickViewSection(
									RelationalQueriesPropertiesPage.this, 
									resourceRepository(), 
									getSubjectHolder()),
							new QuickViewJoinedAttributesSection() 
					};
				}
			};
		}

		private QuickViewSectionFactory buildReportQueryQuickViewSectionFactory() {
			return new QuickViewSectionFactory() {
				public QuickViewSection[] buildSections() {
					return new QuickViewSection[] {
							new ParametersQuickViewSection(
									RelationalQueriesPropertiesPage.this, 
									resourceRepository(), 
									getSubjectHolder()),
							new QuickViewAttributesSection(), 
							new QuickViewGroupingSection(), 
							new QuickViewOrderingSection() 
					};
				}
			};
		}

		
		private class QuickViewAttributesSection extends AbstractQuickViewSection {
			QuickViewAttributesSection() {
				super(resourceRepository(), "QUICK_VIEW_ATTRIBUTES_LABEL", "QUICK_VIEW_ATTRIBUTES_LABEL_ACCESSIBLE");
			}

			public ListValueModel buildItemsHolder() {
				return new TransformationListValueModelAdapter(buildAttributesFunctionAdapter()) {
					protected Object transformItem(Object item) {
						return RelationalQueriesPropertiesPage.this.buildReportAttributeQuickViewItem((MWReportAttributeItem) item);
					}
				};
			}

			private ListValueModel buildAttributesFunctionAdapter() {
				return 
					new ItemPropertyListValueModelAdapter(
							buildAttributesListHolder(), 
							MWReportAttributeItem.FUNCTION_PROPERTY);
			}

			private ListValueModel buildAttributesListHolder() {
				return new ListAspectAdapter(buildReportQueryHolder(), MWReportQuery.ATTRIBUTE_ITEMS_LIST) {
					protected ListIterator getValueFromSubject() {
						return ((MWReportQuery) this.subject).attributeItems();
					}

					protected int sizeFromSubject() {
						return ((MWReportQuery) this.subject).attributeItemsSize();
					}
				};
			}

			public void select() {
				selectReportQueryAttributesTab();
			}
		}

		
		private class QuickViewBatchReadAttributesSection extends AbstractQuickViewSection {
			QuickViewBatchReadAttributesSection() {
				super(resourceRepository(), "QUICK_VIEW_BATCH_READ_ATTRIBUTES_LABEL", "QUICK_VIEW_BATCH_READ_ATTRIBUTES_LABEL_ACCESSIBLE");
			}

			public ListValueModel buildItemsHolder() {
				return new TransformationListValueModelAdapter(buildAttributesListHolder()) {
					protected Object transformItem(Object item) {
						return buildBatchReadAttributeQuickViewItem((MWBatchReadItem) item);
					}
				};
			}

			private ListValueModel buildAttributesListHolder() {
				return new ListAspectAdapter(buildRelationalReadAllQueryHolder(), MWRelationalReadAllQuery.BATCH_READ_ITEMS_LIST) {
					protected ListIterator getValueFromSubject() {
						return ((MWRelationalReadAllQuery) this.subject).batchReadItems();
					}

					protected int sizeFromSubject() {
						return ((MWRelationalReadAllQuery) this.subject).batchReadItemsSize();
					}
				};
			}

			public void select() {
				selectBatchReadAttributesTab();
			}
		}

		private class QuickViewGroupingSection extends AbstractQuickViewSection {
			QuickViewGroupingSection() {
				super(resourceRepository(), "QUICK_VIEW_GROUPING_ATTRIBUTES_LABEL", "QUICK_VIEW_GROUPING_ATTRIBUTES_LABEL_ACCESSIBLE");
			}

			public ListValueModel buildItemsHolder() {
				return new TransformationListValueModelAdapter(buildGroupingListHolder()) {
					protected Object transformItem(Object item) {
						return buildGroupingAttributeQuickViewItem((MWGroupingItem) item);
					}
				};
			}

			private ListValueModel buildGroupingListHolder() {
				return new ListAspectAdapter(buildReportQueryHolder(), MWReportQuery.GROUPING_ITEMS_LIST) {
					protected ListIterator getValueFromSubject() {
						return ((MWReportQuery) this.subject).groupingItems();
					}

					protected int sizeFromSubject() {
						return ((MWReportQuery) this.subject).groupingItemsSize();
					}
				};
			}

			public void select() {
				selectReportQueryGroupingOrderingTab();
			}
		}

		private class QuickViewJoinedAttributesSection extends AbstractQuickViewSection {
			QuickViewJoinedAttributesSection() {
				super(resourceRepository(), "QUICK_VIEW_JOINED_ATTRIBUTES_LABEL", "QUICK_VIEW_JOINED_ATTRIBUTES_LABEL_ACCESSIBLE");
			}

			public ListValueModel buildItemsHolder() {
				return new TransformationListValueModelAdapter(buildAttributesListHolder()) {
					protected Object transformItem(Object item) {
						return buildJoinedAttributeQuickViewItem((MWJoinedItem) item);
					}
				};
			}

			private ListValueModel buildAttributesListHolder() {
				return new ListAspectAdapter(buildAbstractRelationalReadQueryHolder(), MWAbstractRelationalReadQuery.JOINED_ITEMS_LIST) {
					protected ListIterator getValueFromSubject() {
						return ((MWAbstractRelationalReadQuery) this.subject).joinedItems();
					}

					protected int sizeFromSubject() {
						return ((MWAbstractRelationalReadQuery) this.subject).joinedItemsSize();
					}
				};
			}

			public void select() {
				selectJoinedAttributesTab();
			}
		}

		private class QuickViewOrderingSection extends AbstractQuickViewSection {
			QuickViewOrderingSection() {
				super(resourceRepository(), "QUICK_VIEW_ORDERING_ATTRIBUTES_LABEL", "QUICK_VIEW_ORDERING_ATTRIBUTES_LABEL_ACCESSIBLE");
			}

			public ListValueModel buildItemsHolder() {
				return new TransformationListValueModelAdapter(buildOrderingListAscendingAdapter()) {
					protected Object transformItem(Object item) {
						return buildOrderingAttributeQuickViewItem((Ordering) item);
					}
				};
			}

			private ListValueModel buildOrderingListAscendingAdapter() {
				return 
					new ItemPropertyListValueModelAdapter(
							buildOrderingListHolder(), 
							Ordering.ASCENDING_PROPERTY);
			}
			
			protected ListValueModel buildOrderingListHolder() {
				return new ListAspectAdapter(buildOrderableQueryHolder(), MWOrderableQuery.ORDERING_ITEMS_LIST) {
					protected ListIterator getValueFromSubject() {
						return ((MWOrderableQuery) this.subject).orderingItems();
					}

					protected int sizeFromSubject() {
						return ((MWOrderableQuery) this.subject).orderingItemsSize();
					}
				};
			}

			public void select() {
				selectOrderingTab();
			}
		}
	}
}
