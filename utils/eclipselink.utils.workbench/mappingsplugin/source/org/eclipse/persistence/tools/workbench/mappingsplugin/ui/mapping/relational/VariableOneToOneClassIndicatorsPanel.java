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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.RelationalClassIndicatorDictionarySubPanel;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class VariableOneToOneClassIndicatorsPanel extends ScrollablePropertiesPage {

	private PropertyValueModel classIndicatorPolicyHolder;
	private ValueModel parentDescriptorHolder;

	VariableOneToOneClassIndicatorsPanel(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.classIndicatorPolicyHolder = buildClassIndicatorPolicyHolder();
		this.parentDescriptorHolder = buildParentDescriptorHolder();		
	}

	private PropertyValueModel buildClassIndicatorPolicyHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWVariableOneToOneMapping) subject).getClassIndicatorPolicy();
			}
		};
	}
	
	protected String helpTopicId() {
		return "mapping.variableOneToOne.classIndicators";
	}
	
	protected Component buildPage() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JComboBox classIndicatorField = buildClassIndicatorFieldChooser();
		new ComponentEnabler(buildClassIndicatorFieldChooserEnablerModel(), Collections.singleton(classIndicatorField));

		JComponent classIndicatorWidgets = buildLabeledComponent(
			"VARIABLE_ONE_TO_ONE_CLASS_INDICATOR_FIELD_LABEL",
			classIndicatorField
		);
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);
		panel.add(classIndicatorWidgets, constraints);

		RelationalClassIndicatorDictionarySubPanel classIndicatorDictionaryPanel = 
			new RelationalClassIndicatorDictionarySubPanel(this.classIndicatorPolicyHolder, getWorkbenchContextHolder());
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 0, 0, 0);
		panel.add(classIndicatorDictionaryPanel, constraints);
		addPaneForAlignment(classIndicatorDictionaryPanel);
		
		addHelpTopicId(panel, helpTopicId());

		return panel;
	}

	private ValueModel buildClassIndicatorFieldChooserEnablerModel() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(!((MWVariableOneToOneMapping) subject).parentDescriptorIsAggregate());
			}
		};
	}

	private ListChooser buildClassIndicatorFieldChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
					buildExtendedColumnComboBoxModel(buildClassIndicatorFieldChooserPropertyAdapter()),
					getWorkbenchContextHolder(),
					RelationalMappingComponentFactory.buildColumnNodeSelector(getWorkbenchContextHolder()),
					buildColumnChooserDialogBuilder()
			);
		listChooser.setRenderer(RelationalMappingComponentFactory.buildColumnListRenderer(this.parentDescriptorHolder, resourceRepository()));
		return listChooser;
	}

	public static DefaultListChooserDialog.Builder buildColumnChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("DATABASE_FIELD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("DATABASE_FIELD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(RelationalMappingComponentFactory.buildColumnStringConverter());
		return builder;
	}	

	
	private PropertyValueModel buildClassIndicatorFieldChooserPropertyAdapter() {
		return new PropertyAspectAdapter(this.classIndicatorPolicyHolder, MWRelationalClassIndicatorFieldPolicy.FIELD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalClassIndicatorFieldPolicy)subject).getField();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWRelationalClassIndicatorFieldPolicy)subject).setField((MWColumn)value);
			}	
		};	
	}
	
	public CachingComboBoxModel buildExtendedColumnComboBoxModel(PropertyValueModel databaseFieldHolder) {
		return new ExtendedComboBoxModel(buildColumnComboBoxModel(databaseFieldHolder));
	}
	
	public CachingComboBoxModel buildColumnComboBoxModel(PropertyValueModel databaseFieldHolder) {
		return new IndirectComboBoxModel(databaseFieldHolder, buildTableHolder()) {
			protected ListIterator listValueFromSubject(Object subject) {
				return CollectionTools.sort(((MWTable) subject).columns()).listIterator();
			}
			
			protected int listSizeFromSubject(Object subject) {
				return ((MWTable) subject).columnsSize();
			}
		};		
	}
	

	/**  
	*  BUG FIX: PRS item #: 36551, Support item #: 19610
	*  Selecting class indicator field for Inheritance properties should only allow user to select 
	*  fields from the descriptor's primary table.  Previously allowed user to select fields from
	*  all associated database tables.
	*/
	//TODO should the above bug fix apply for var 1-1 mappings?  we inherited panels before so this fix
	//affected inheritance policy as well as var 1-1
	private ValueModel buildTableHolder() {
		return new PropertyAspectAdapter(this.parentDescriptorHolder, MWTableDescriptor.PRIMARY_TABLE_PROPERTY) {
			protected Object getValueFromSubject() {
				return((MWRelationalClassDescriptor) subject).getPrimaryTable();
			}
		};
	}
	
	private PropertyValueModel buildParentDescriptorHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWVariableOneToOneMapping) subject).getParentDescriptor();
			}
		};
	}

}
