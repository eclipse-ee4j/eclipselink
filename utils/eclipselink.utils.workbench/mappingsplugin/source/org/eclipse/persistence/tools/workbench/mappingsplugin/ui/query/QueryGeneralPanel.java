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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;




/**
 * QueryGeneralPanel is one of the nested tabs found on the NamedQueries tab of a descriptor.
 * The user chooses the query type on this tab.  They can also add and remove query 
 * parameters on the QueryParametersPanel
 */
final public class QueryGeneralPanel
	extends AbstractPanel
{
	private PropertyValueModel queryHolder;
	private QueryParametersPanel queryParametersPanel;
	
	public QueryGeneralPanel(PropertyValueModel queryHolder,
	                         ObjectListSelectionModel querySelectionModel,
	                         WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.queryHolder = queryHolder;
		initializeLayout(querySelectionModel);
	}


	private MWQuery getQuery() {
		return (MWQuery) this.queryHolder.getValue();
	}
	
	public String helpTopicId() {
		return "descriptor.queries.general";
	}
		
	private void initializeLayout(ObjectListSelectionModel querySelectionModel) {

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		initializeQueryTypePanel(querySelectionModel);
		initializeParametersPanel();
		
		/*GridBagConstraints constraints = new GridBagConstraints();
		
		this.partialAttributeQueryCheckBox = new CheckBox("Partial Attribute Query");
		this.partialAttributeQueryCheckBox.addActionListener(new SetPartialAttributeQueryAction());
		
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.NORTHWEST;
		constraints.insets		= new Insets(5, 5, 0, 5);
		
		add(this.partialAttributeQueryCheckBox, constraints);*/
		addHelpTopicId(this, helpTopicId());
	}

	private void initializeParametersPanel() {
		GridBagConstraints constraints = new GridBagConstraints();

		this.queryParametersPanel = new QueryParametersPanel(this.queryHolder, getWorkbenchContextHolder());
		this.queryParametersPanel.setBorder(buildTitledBorder("QUERY_PARAMETERS_TABLE_TITLE"));

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(10, 0, 0, 0);

		add(this.queryParametersPanel, constraints);
	}
	
	private void initializeQueryTypePanel(ObjectListSelectionModel querySelectionModel) {
		GridBagConstraints constraints = new GridBagConstraints();

		// Query Type widgets
		JPanel queryTypePanel = new JPanel(new GridBagLayout());

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);

		add(queryTypePanel, constraints);

		// Query type label
		JLabel typeLabel = this.buildQueryTypeLabel();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		queryTypePanel.add(typeLabel, constraints);

		// Query type combo box
		JComboBox queryTypeComboBox = buildQueryTypeComboBox(querySelectionModel);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 5, 0, 0);

		queryTypePanel.add(queryTypeComboBox, constraints);
		typeLabel.setLabelFor(queryTypeComboBox);

		addHelpTopicId(queryTypePanel, helpTopicId() + ".type");
	}
	
	private JLabel buildQueryTypeLabel() {
		JLabel label = buildLabel("QUERY_TYPE_COMBO_BOX_LABEL");
		this.queryHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildQueryTypeLabelListener(label));
		return label;
	}
	
	private PropertyChangeListener buildQueryTypeLabelListener(final JLabel label) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				label.setEnabled(getQuery() != null);
			}
		};
	}
	
	private JComboBox buildQueryTypeComboBox(ObjectListSelectionModel querySelectionModel) {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(buildQueryTypeValueModel(), buildQueryTypePropertyAdapter(querySelectionModel)));
		comboBox.setEnabled(false);
		comboBox.setEditable(false);
		this.queryHolder.addPropertyChangeListener(buildQueryTypeChooserListener(comboBox));
		return comboBox;
	}

	private CollectionValueModel buildQueryTypeValueModel() {
		return new CollectionAspectAdapter(this.queryHolder) {
			protected Iterator getValueFromSubject() {
				return new TransformationIterator(((MWQuery) this.subject).queryTypes()) {
					protected Object transform(Object next) {
						return resourceRepository().getString((String) next);
					}
				};
			}
		};
	}	

	private boolean queryTypeCanChange() {
		String promptValue = TriStateBoolean.UNDEFINED.toString();
		String value = preferences().get(MappingsPlugin.CHANGE_QUERY_TYPE_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE, promptValue);
		boolean changeQueryType;

		if (value.equals(promptValue)) {
			changeQueryType = promptToChangeQueryType();
		}
		else {
			changeQueryType = TriStateBoolean.TRUE.toString().equals(value);
			if (!changeQueryType) {
				JOptionPane.showMessageDialog(
						this.getWorkbenchContext().getCurrentWindow(),
						this.resourceRepository().getString("QUERY_TYPE_CHANGE_DISSALLOWED"));
			}
		}

		return changeQueryType;
	}

	private boolean promptToChangeQueryType() {
		if (this.preferences().getBoolean(MappingsPlugin.CHANGE_QUERY_TYPE_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE, false)) {
			return true;
		}

		// build dialog panel
		String title = this.resourceRepository().getString("QUERY_CHANGE_QUERY_TYPE_TITLE");
		String message = this.resourceRepository().getString("QUERY_CHANGE_QUERY_TYPE_MESSAGE");
		PropertyValueModel dontAskAgainHolder = new SimplePropertyValueModel(new Boolean(false));
		JComponent dontAskAgainPanel = 
			SwingComponentFactory.buildDoNotAskAgainPanel(message, dontAskAgainHolder, this.resourceRepository());
		
		JOptionPane queryChangePane = new JOptionPane(dontAskAgainPanel, JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
		JDialog queryChangeDialog = queryChangePane.createDialog(this.getWorkbenchContext().getCurrentWindow(), title);
		queryChangeDialog.setSize(400, 150);
		queryChangeDialog.setResizable(false);
		queryChangeDialog.show();
		queryChangeDialog.dispose();
		int response = -1;
		Object value = queryChangePane.getValue();
		if (value != null && value instanceof Integer) {
			response = ((Integer)value).intValue();
		}

		// prompt user for response
//		int response = 
//			JOptionPane.showConfirmDialog(
//				this.getWorkbenchContext().getCurrentWindow(),
//				dontAskAgainPanel,
//				title,
//				JOptionPane.YES_NO_OPTION,
//				JOptionPane.WARNING_MESSAGE
//			);

		if (dontAskAgainHolder.getValue().equals(Boolean.TRUE)) {
			if (response == JOptionPane.YES_OPTION) {
				this.preferences().putBoolean(MappingsPlugin.CHANGE_QUERY_TYPE_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE, true);
			}
			else if (response == JOptionPane.NO_OPTION) {
				this.preferences().putBoolean(MappingsPlugin.CHANGE_QUERY_TYPE_DO_NOT_THIS_SHOW_AGAIN_PREFERENCE, false);
			}
		}

		return (response == JOptionPane.OK_OPTION);
	}

	private PropertyValueModel buildQueryTypePropertyAdapter(final ObjectListSelectionModel querySelectionModel) {
		return new PropertyAspectAdapter(this.queryHolder) {
			protected Object getValueFromSubject() {
				return resourceRepository().getString(((MWQuery) this.subject).queryType());
			}
		
			protected void setValueOnSubject(Object value) {
				if (!QueryGeneralPanel.this.queryTypeCanChange())
					return;

				MWQuery newQuery;
				if (value == resourceRepository().getString(MWQuery.REPORT_QUERY)) {
					newQuery = getQuery().asReportQuery();
				}
				else if (value == resourceRepository().getString(MWQuery.READ_ALL_QUERY)) {
					newQuery = getQuery().asReadAllQuery();
				}			
				else {//if (value == MWQuery.READ_OBJECT_QUERY) 
					newQuery = getQuery().asReadObjectQuery();
				}
				querySelectionModel.setSelectedValue(newQuery);
			}	
		};	
	}
	
	private PropertyChangeListener buildQueryTypeChooserListener(final JComboBox comboBox) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (getQuery() == null) {
					comboBox.setEnabled(false);
				} else {
					comboBox.setEnabled(true);
				}
			}
		};
	}
		
	protected void selectParameter(MWQueryParameter parameter) {
		this.queryParametersPanel.selectParameter(parameter);
	}
}
