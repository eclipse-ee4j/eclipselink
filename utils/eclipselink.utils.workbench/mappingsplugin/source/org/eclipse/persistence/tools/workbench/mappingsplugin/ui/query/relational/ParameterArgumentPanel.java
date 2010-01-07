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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryParameterArgument;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QueryParameterDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.NameTools;




/**
 * This panel is used on the ExpressionBuilderDialog SecondArgumentPanel
 * If the user chooses to create a parameter argument for their expression, this panel is shown.
 * They must then select a query parameter from the combo box
 */
final class ParameterArgumentPanel 
	extends ArgumentPanel 
{
    private PropertyValueModel queryArgumentHolder;
	
	ParameterArgumentPanel(PropertyValueModel argumentHolder, WorkbenchContextHolder contextHolder, Collection enablingComponents) {
		super(argumentHolder, contextHolder);
		initialize(enablingComponents);
	}

	private void initialize(Collection enablingComponents) {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
	
		JComboBox parameterComboBox = buildParameterComboBox();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(parameterComboBox, constraints);
		
		enablingComponents.add(parameterComboBox);
		
		JButton addButton = buildButton("ADD_PARAMETER_BUTTON");
		addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addParameter();
            }
        });
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(addButton, constraints);
		
	}

	private void addParameter() {
	    MWQuery query = (MWQuery) getQueryHolder().getValue();
		String newName = NameTools.uniqueNameFor(MWQuery.PARAMETER_NAME_PREFIX, query.parameterNames());
		
		QueryParameterDialog dialog = 
			new QueryParameterDialog(query,
			        query.typeFor(String.class),        
					newName, 
					(AbstractDialog) getWorkbenchContext().getCurrentWindow(),
					getWorkbenchContext(),
					resourceRepository().getString("QUERY_PARAMETER_DIALOG.title"));
		
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			MWClass type = dialog.getParameterType();
			String name = dialog.getParameterName();
			MWQueryParameter parameter = query.addParameter(type);
			parameter.setName(name);

			((MWQueryParameterArgument) this.getArgumentHolder().getValue()).setQueryParameter(parameter);
		}
	}

	// *********** parameters ************
	
	private JComboBox buildParameterComboBox() {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(buildParametersCollectionHolder(), buildParameterHolder()));
		comboBox.setRenderer(buildParametersListCellRenderer());
		return comboBox;
	}
	
	private ListValueModel buildParametersCollectionHolder() {
		return new ListAspectAdapter(getQueryHolder(), MWAbstractQuery.PARAMETERS_LIST) {
			protected ListIterator getValueFromSubject() {
				return ((MWAbstractQuery) subject).parameters();
			}
			protected int sizeFromSubject() {
				return ((MWAbstractQuery) subject).parametersSize();
			}
		};
	}
	
	private PropertyValueModel buildParameterHolder() {
		return new PropertyAspectAdapter(getArgumentHolder(), MWQueryParameterArgument.QUERY_PARAMETER_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWQueryParameterArgument) subject).getQueryParameter();
			}
			protected void setValueOnSubject(Object value) {
				((MWQueryParameterArgument) subject).setQueryParameter((MWQueryParameter) value);
			}

		};
	}
	
	protected PropertyValueModel buildQueryArgumentHolder(PropertyValueModel argumentHolder) {
		return new FilteringPropertyValueModel(argumentHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWQueryParameterArgument;
			}
		};		
	}
	
	private ListCellRenderer buildParametersListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				// need null check for combo-box
				return value == null ? resourceRepository().getString("NONE_SELECTED") : ((MWQueryParameter) value).getName();
			}
		};
	}
}
