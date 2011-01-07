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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveTablePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel.UpDownOptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTableCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;


/**
 * This panel is added to the General Panel of the Named Queries property panel 
 * It is used to add and remove parameters from a query
 */
public final class QueryParametersPanel 
	extends AbstractPanel
{
	private PropertyValueModel queryHolder;

    private AddRemoveTablePanel paramatersPanel;
    
	public QueryParametersPanel(PropertyValueModel queryHolder,
	                            WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.queryHolder = queryHolder;
		initializeLayout();
	}

	private String helpTopicId() {
		return "descriptor.queryManager.general.parameters";
	}	
	
	protected void initializeLayout() {	
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.paramatersPanel = this.buildQueryParametersTable();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 5, 5, 5);
		this.add(this.paramatersPanel, constraints);
		
		// Create the button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2, 5, 0));

//			// Create the Add button
//			this.addParameterButton = new JButton(resourceRepository().getString("ADD_PARAMETER_BUTTON"));
//			this.addParameterButton.setMnemonic(resourceRepository().getMnemonic("ADD_PARAMETER_BUTTON"));
//			this.addParameterButton.addActionListener(this.buildAddParameterAction());
//			this.addParameterButton.setEnabled(false);
//			buttonPanel.add(this.addParameterButton);
//			
//			// Create the Remove button
//			this.removeParameterButton = new JButton(resourceRepository().getString("REMOVE_PARAMETER_BUTTON"));
//			this.removeParameterButton.setMnemonic(resourceRepository().getMnemonic("REMOVE_PARAMETER_BUTTON"));
//			this.removeParameterButton.addActionListener(this.buildRemoveParameterAction());
//			this.removeParameterButton.setEnabled(false);
//			buttonPanel.add(this.removeParameterButton);
//			
//			// Create the Edit button
//			this.editParameterButton = new JButton(resourceRepository().getString("EDIT_PARAMETER_BUTTON"));
//			this.editParameterButton.setMnemonic(resourceRepository().getMnemonic("EDIT_PARAMETER_BUTTON"));
//			this.editParameterButton.addActionListener(this.buildEditParameterAction());
//			this.editParameterButton.setEnabled(false);
//			buttonPanel.add(this.editParameterButton);
		
		buttonPanel.setBorder(BorderFactory.createEmptyBorder());
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.insets = new Insets(5, 5, 5, 5);
		this.add(buttonPanel, constraints);

		addHelpTopicId(this, helpTopicId());
	}
	
	// ************ Parameters ************
	private AddRemoveTablePanel buildQueryParametersTable() {
        AddRemoveTablePanel tablePanel = 
            new AddRemoveTablePanel(
                    getApplicationContext(),
                    buildParametersPanelAdapter(),
                    buildParametersNameAdapter(),
                    buildQueryParametersTableColumnAdapter(),
                    AddRemovePanel.RIGHT) {
          
            protected void updateOptionalButton(JButton optionalButton) {
                boolean paramsSelected = getSelectionModel().getSelectedValuesSize() == 1;
                updateButton(optionalButton, paramsSelected);
            }
            
            protected void updateRemoveButton(JButton removeButton) {
                boolean paramsSelected = getSelectionModel().getSelectedValue() != null;
                updateButton(removeButton, paramsSelected);
            }
            
            protected void updateAddButton(JButton addButton) {
            }
            
            @Override
            protected String removeButtonKey() {
            	return "REMOVE_PARAMETER_BUTTON";
            }
            
            @Override
            protected String addButtonKey() {
            	return "ADD_PARAMETER_BUTTON";
            }
            
            private void updateButton(JButton button, boolean paramsSelected) {
                boolean canRemoveParams = true;
                button.setEnabled(paramsSelected && canRemoveParams);               
            }
        };
        
        JTable table = (JTable) tablePanel.getComponent();
        table.getColumnModel().getColumn(QueryParametersColumnAdapter.TYPE_COLUMN).setCellRenderer(this.buildTypeRenderer());

		return tablePanel;
	}
	
	protected TableCellRenderer buildTypeRenderer() {
		return new SimpleTableCellRenderer() {
			protected String buildText(Object value) {
				return value == null ? null : ((MWClass) value).getName();
			}
		};
	}
	
	private UpDownOptionAdapter buildParametersPanelAdapter() {
        return new UpDownOptionAdapter() {
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    getQuery().moveParameterDown((MWQueryParameter) items[i]);
                }
            }
        
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    getQuery().moveParameterUp((MWQueryParameter) items[i]);
                }
            }
        
            public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
                removeSelectedQueryParameters(listSelectionModel);
            }
        
            public void addNewItem(ObjectListSelectionModel listSelectionModel) {
                addQueryParameter();
            }
        
            public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
                editSelectedQueryParameter((MWQueryParameter) listSelectionModel.getSelectedValue());
            }
            
            public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				return listSelectionModel.getSelectedValuesSize() == 1;
            }
            
            public String optionalButtonKey() {
                return "EDIT_PARAMETER_BUTTON";
            }
        };
    }
	
	// the list will need to be re-sorted if a name changes
	private ListValueModel buildParametersNameAdapter() {
		return new ItemPropertyListValueModelAdapter(buildQueryParametersHolder(), MWQueryParameter.NAME_PROPERTY);
	}

	private ListValueModel buildQueryParametersHolder() {
		return new ListAspectAdapter(this.queryHolder, MWQuery.PARAMETERS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWAbstractQuery) this.subject).parameters();
			}
			
			protected int sizeFromSubject() {
				return ((MWAbstractQuery) this.subject).parametersSize();
			}
		};
	}
	
	private ColumnAdapter buildQueryParametersTableColumnAdapter() {
		return new QueryParametersColumnAdapter(resourceRepository());
	}

	protected void removeSelectedQueryParameters(ObjectListSelectionModel model) {					
		getQuery().removeParameters(CollectionTools.iterator(model.getSelectedValues()));
	}
	
	void addQueryParameter() {

		String newName = NameTools.uniqueNameFor(MWQuery.PARAMETER_NAME_PREFIX, getQuery().parameterNames());
		
		QueryParameterDialog dialog = 
			new QueryParameterDialog(getQuery(),
					getQuery().typeFor(String.class), 
					newName, 
					getWorkbenchContext(),
					resourceRepository().getString("QUERY_PARAMETER_DIALOG.title"));
		
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			MWClass type = dialog.getParameterType();
			String name = dialog.getParameterName();
			MWQueryParameter parameter = getQuery().addParameter(type, name);

			selectParameter(parameter);	
		}
	}
	
	void editSelectedQueryParameter(MWQueryParameter selectedParameter) {
		QueryParameterDialog dialog = 
			new QueryParameterDialog(
			        getQuery(),
					selectedParameter.getType(), 
					selectedParameter.getName(), 
					getWorkbenchContext(),
					resourceRepository().getString("QUERY_PARAMETER_DIALOG_EDIT.title"));
		
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			selectedParameter.setType(dialog.getParameterType());
			selectedParameter.setName(dialog.getParameterName());	
		}	
	}

	MWAbstractQuery getQuery() {
		return (MWAbstractQuery) this.queryHolder.getValue();
	}

	public void selectParameter(MWQueryParameter parameter) {
        this.paramatersPanel.setSelectedValue(parameter, true);
	}
	

	// ********** classes **********

	public static class QueryParametersColumnAdapter implements ColumnAdapter {
		
		private ResourceRepository resourceRepository;
		
		public static final int COLUMN_COUNT = 2;

		public static final int TYPE_COLUMN = 0;
		public static final int NAME_COLUMN = 1;

		private static final String[] COLUMN_NAME_KEYS = new String[] {
			"TYPE_COLUMN_HEADER",
			"NAME_COLUMN_HEADER",
		};

		protected QueryParametersColumnAdapter(ResourceRepository repository) {
			super();
			this.resourceRepository = repository;
		}
		
		public int getColumnCount() {
			return COLUMN_COUNT;
		}

		public String getColumnName(int index) {
			return this.resourceRepository.getString(COLUMN_NAME_KEYS[index]);
		}

		public Class getColumnClass(int index) {
			switch (index) {
				case TYPE_COLUMN:				return Object.class;
				case NAME_COLUMN:				return String.class;

				default: 						return Object.class;
			}
		}

		public boolean isColumnEditable(int index) {
			return false;
		}

		public PropertyValueModel[] cellModels(Object subject) {
			MWQueryParameter queryParameter = (MWQueryParameter) subject;
			PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

			result[TYPE_COLUMN]	= this.buildTypeAdapter(queryParameter);
			result[NAME_COLUMN]	= this.buildNameAdapter(queryParameter);

			return result;
		}

		private PropertyValueModel buildTypeAdapter(MWQueryParameter queryParameter) {
			PropertyValueModel adapter = new PropertyAspectAdapter(MWQueryParameter.TYPE_PROPERTY, queryParameter) {
				protected Object getValueFromSubject() {
					return ((MWQueryParameter) this.subject).getType();
				}
				protected void setValueOnSubject(Object value) {
					((MWQueryParameter) this.subject).setType((MWClass) value);
				}
			};
			return new ValuePropertyPropertyValueModelAdapter(adapter, MWClass.NAME_PROPERTY);
		}
		
		private PropertyValueModel buildNameAdapter(MWQueryParameter queryParameter) {
			return new PropertyAspectAdapter(MWQueryParameter.NAME_PROPERTY, queryParameter) {
				protected Object getValueFromSubject() {
					return ((MWQueryParameter) this.subject).getName();
				}
				protected void setValueOnSubject(Object value) {
					((MWQueryParameter) this.subject).setName((String) value);
				}
			};
		}		
	}
}
