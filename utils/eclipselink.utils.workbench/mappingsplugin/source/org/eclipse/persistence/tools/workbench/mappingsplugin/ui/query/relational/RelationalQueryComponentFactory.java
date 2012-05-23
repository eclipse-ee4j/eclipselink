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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.TriStateBooleanCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalSpecificQueryOptions;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;



class RelationalQueryComponentFactory extends QueryComponentFactory {

	
	//************ cache statement **********
	
	static JComboBox buildCacheStatementComboBox(PropertyValueModel relationalQueryHolder, final ResourceRepository resourceRepository) {
        PropertyValueModel relationalOptionsHolder = buildRelationalOptionsHolder(relationalQueryHolder);
        final PropertyValueModel projectCacheStatementHolder = buildProjectCacheStatementHolder(relationalOptionsHolder);
		JComboBox comboBox = 
			new JComboBox(
					new ComboBoxModelAdapter(
							buildTriStateBooleanValueModel(), 
							buildCacheStatementAdapter(relationalOptionsHolder)));
        comboBox.setRenderer(new AdaptableListCellRenderer(
                new TriStateBooleanCellRendererAdapter(resourceRepository) {
                    protected String undefinedString() {
                        Boolean projectCacheStatement = (Boolean) projectCacheStatementHolder.getValue();
                        return resourceRepository.getString("QUERY_DESCRIPTOR_DEFAULT_VALUE", projectCacheStatement);
                    }
                }));
        
        projectCacheStatementHolder.addPropertyChangeListener(
                ValueModel.VALUE, 
                buildProjectDefaultListener(comboBox));

        return comboBox;
	}

	private static PropertyValueModel buildCacheStatementAdapter(PropertyValueModel relationalOptionsHolder) {
		return new PropertyAspectAdapter(relationalOptionsHolder, MWRelationalQuery.CACHE_STATEMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalQuery) this.subject).isCacheStatement();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWRelationalQuery) this.subject).setCacheStatement((TriStateBoolean) value);
			}
		};	
	}

    private static PropertyChangeListener buildProjectDefaultListener(final JComboBox comboBox) {
        return new PropertyChangeListener(){ 
            public void propertyChange(PropertyChangeEvent evt) {
                //repainting the comboBox because the rendering changes when 
                //the project value is changed.
                comboBox.repaint();           
            }
        };
    }
    
    private static PropertyValueModel buildProjectCacheStatementHolder(PropertyValueModel relationalOptionsHolder) {
        return new PropertyAspectAdapter(relationalOptionsHolder) {
            protected Object getValueFromSubject() {
                return new Boolean(((MWRelationalProjectDefaultsPolicy) ((MWRelationalSpecificQueryOptions) this.subject).getProject().getDefaultsPolicy()).shouldQueriesCacheAllStatements());
            }
        };
    }

    
	//************ bind parameters **********
	
	static JComboBox buildBindParametersComboBox(PropertyValueModel relationalQueryHolder, final ResourceRepository resourceRepository) {
        PropertyValueModel relationalOptionsHolder = buildRelationalOptionsHolder(relationalQueryHolder);
        final PropertyValueModel projectBindParametersHolder = buildProjectBindParametersHolder(relationalOptionsHolder);
        JComboBox comboBox = 
            new JComboBox(
                    new ComboBoxModelAdapter(
                            buildTriStateBooleanValueModel(), 
                            buildBindParametersPropertyAdapter(relationalOptionsHolder)));
        comboBox.setRenderer(new AdaptableListCellRenderer(
                new TriStateBooleanCellRendererAdapter(resourceRepository) {
                    protected String undefinedString() {
                        Boolean projectBindParameters = (Boolean) projectBindParametersHolder.getValue();
                        return resourceRepository.getString("QUERY_DESCRIPTOR_DEFAULT_VALUE", projectBindParameters);
                    }
                }));
        
        projectBindParametersHolder.addPropertyChangeListener(
                ValueModel.VALUE, 
                buildProjectDefaultListener(comboBox));

        return comboBox;
	}

	private static PropertyValueModel buildBindParametersPropertyAdapter(PropertyValueModel relationalOptionsHolder) {
		return new PropertyAspectAdapter(relationalOptionsHolder, MWRelationalQuery.BIND_ALL_PARAMETERS_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalQuery) this.subject).isBindAllParameters();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWRelationalQuery) this.subject).setBindAllParameters((TriStateBoolean) value);
			}	
		};	
	}
    
    private static PropertyValueModel buildProjectBindParametersHolder(PropertyValueModel relationalOptionsHolder) {
        return new PropertyAspectAdapter(relationalOptionsHolder) {
            protected Object getValueFromSubject() {
                return new Boolean(((MWRelationalProjectDefaultsPolicy) ((MWRelationalSpecificQueryOptions) this.subject).getProject().getDefaultsPolicy()).shouldQueriesBindAllParameters());
            }
        };
    }
    
	
	private static PropertyValueModel buildRelationalOptionsHolder(PropertyValueModel queryHolder) {
		return new PropertyAspectAdapter(queryHolder) {
			protected Object getValueFromSubject() {
				return ((MWRelationalQuery) this.subject).getRelationalOptions();
			}
		};
	}
	
	
	private static CollectionValueModel buildTriStateBooleanValueModel() {
		return new AbstractReadOnlyCollectionValueModel() {
			public Object getValue() {
				return triStateBooleanOptions();
			}
		};			
	}
	
	private static List triStateBooleanOptions;

	public static Iterator triStateBooleanOptions() {
		if (triStateBooleanOptions == null) {	
			triStateBooleanOptions = new ArrayList();
			triStateBooleanOptions.add(TriStateBoolean.UNDEFINED);
			triStateBooleanOptions.add(TriStateBoolean.TRUE);
			triStateBooleanOptions.add(TriStateBoolean.FALSE);
		}
		
		return triStateBooleanOptions.listIterator();
	}

}
