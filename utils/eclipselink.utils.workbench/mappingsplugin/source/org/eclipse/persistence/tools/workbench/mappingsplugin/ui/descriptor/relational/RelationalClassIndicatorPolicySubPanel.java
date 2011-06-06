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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledBorder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ClassIndicatorPolicySubPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;


/**
 * 
 */
public class RelationalClassIndicatorPolicySubPanel
	extends ClassIndicatorPolicySubPanel {
			
	private JComboBox classIndicatorFieldComboBox;
	
	public RelationalClassIndicatorPolicySubPanel(PropertyValueModel descriptorHolder, PropertyValueModel inheritancePolicyHolder, WorkbenchContextHolder contextHolder, Collection isRootListeners) {
		super(descriptorHolder, inheritancePolicyHolder, contextHolder, isRootListeners);
	}

	/**  
	*  BUG FIX: PRS item #: 36551, Support item #: 19610
	*  Selecting class indicator field for Inheritance properties should only allow user to select 
	*  fields from the descriptor's primary table.  Previously allowed user to select fields from
	*  all associated database tables.
	*/
	private ValueModel buildTableHolder() {
		return new PropertyAspectAdapter(getDescriptorHolder(), MWTableDescriptor.PRIMARY_TABLE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalClassDescriptor) subject).getPrimaryTable();
			}
		};
	}

	//TODO make this a chooser
	private JComboBox buildClassIndicatorFieldChooser() {
		JComboBox fieldChooser = new JComboBox(new ComboBoxModelAdapter(buildClassIndicatorFieldChooserValueModel(buildTableHolder()), buildClassIndicatorFieldChooserPropertyAdapter()));
		fieldChooser.setRenderer(buildClassIndicatorFieldChooserRenderer());
		
		return fieldChooser;
	}

	private ListCellRenderer buildClassIndicatorFieldChooserRenderer() {
		return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(resourceRepository()));
	}
	
	private CollectionValueModel buildClassIndicatorFieldChooserValueModel(ValueModel tableHolder) {
		return new CollectionAspectAdapter(tableHolder, MWTable.COLUMNS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWTable) subject).columns();
			}
		};
	}
	
	private PropertyValueModel buildClassIndicatorFieldChooserPropertyAdapter() {
		return new PropertyAspectAdapter(getClassIndicatorFieldPolicyHolder(), MWRelationalClassIndicatorFieldPolicy.FIELD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalClassIndicatorFieldPolicy) subject).getField();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWRelationalClassIndicatorFieldPolicy)subject).setField((MWColumn)value);
			}	
		};	
	}

	private JPanel buildUseClassIndicatorPolicyPanel(Collection isRootListeners) {
		GridBagConstraints constraints = new GridBagConstraints();
	
		//use class indicator field panel
		JPanel useClassIndicatorFieldPanel = new AccessibleTitledPanel(new GridBagLayout());
		
			// Class Indicator Field radio button
			useClassIndicatorFieldRadioButton = buildRadioButton("USE_CLASS_INDICATOR_FIELD", buildClassIndicatorPolicyRadioButtonModel(MWClassIndicatorPolicy.CLASS_INDICATOR_FIELD_TYPE));
	
			constraints.gridx      = 0;
			constraints.gridy      = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(5, 0, 0, 0);
	
			useClassIndicatorFieldPanel.add(useClassIndicatorFieldRadioButton, constraints);

			// Class Indicator Field pane
			JPanel useClassIndicatorFieldSubPanel = buildUseClassIndicatorFieldSubPanel(isRootListeners);
			useClassIndicatorFieldSubPanel.setBorder(new AccessibleTitledBorder(useClassIndicatorFieldRadioButton.getText()));

			constraints.gridx      = 0;
			constraints.gridy      = 1;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 1;
			constraints.fill       = GridBagConstraints.BOTH;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(0, SwingTools.checkBoxIconWidth(), 0, 0);
			
			useClassIndicatorFieldPanel.add(useClassIndicatorFieldSubPanel, constraints);
			useClassIndicatorFieldSubPanel.putClientProperty("labeledBy", useClassIndicatorFieldRadioButton);
	
		addHelpTopicId(useClassIndicatorFieldPanel, helpTopicId() + ".useClassIndicator");
		
		return useClassIndicatorFieldPanel;
	}

	private ValueModel buildUseClassIndicatorFieldHolder() {
		return new PropertyAspectAdapter(getClassIndicatorPolicyHolder()) {
			protected Object buildValue() {
				return subject == null ? Boolean.FALSE : Boolean.TRUE;
			}
		};
	}
	
	private JPanel buildUseClassIndicatorFieldSubPanel(Collection isRootListeners) {
		JPanel useClassIndicatorFieldPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// field selection sub panel
		JPanel fieldSelectionSubPanel = new JPanel(new GridBagLayout());
		fieldSelectionSubPanel.setBorder(buildTitledBorder("FIELD_SELECTION"));

		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		useClassIndicatorFieldPanel.add(fieldSelectionSubPanel, constraints);

			// Class indicator field combo box
			classIndicatorFieldComboBox = buildClassIndicatorFieldChooser();
			classIndicatorFieldComboBox.putClientProperty("labeledBy", new JLabel(" "));
			addHelpTopicId(classIndicatorFieldComboBox, helpTopicId() + ".classIndicatorField");
			
			constraints.gridx      = 0;
			constraints.gridy      = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 1;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(0, 0, 0, 0);
	
			fieldSelectionSubPanel.add(classIndicatorFieldComboBox, constraints);

		// indicator selection sub panel
		JPanel indicatorSelectionSubPanel = new JPanel(new GridBagLayout());
		indicatorSelectionSubPanel.setBorder(buildTitledBorder("INDICATOR_SELECTION"));

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		useClassIndicatorFieldPanel.add(indicatorSelectionSubPanel, constraints);

			// Use Class Name as Indicator radio button
			useClassNameAsIndicatorRadioButton = buildRadioButton("USE_CLASS_NAME_AS_INDICATOR", buildClassNameAsIndicatorRadioButtonModel(getUseNameModel()));
			addHelpTopicId(useClassNameAsIndicatorRadioButton, helpTopicId() + ".classNameAsIndicator");
	
			// Use Class Indicator Dictionary radio button
			useClassIndicatorDictionaryRadioButton = buildRadioButton("USE_CLASS_INDICATOR_DICTIONARY", buildClassIndicatorDictionaryRadioButtonModel(getUseNameModel()));
			addHelpTopicId(useClassIndicatorDictionaryRadioButton, helpTopicId() + ".classIndicatorDictionary");
	
			// Use Class Indicator Dictionary pane
			RelationalClassIndicatorDictionarySubPanel classIndicatorDictionaryPanel = new RelationalClassIndicatorDictionarySubPanel(getClassIndicatorPolicyHolder(), getWorkbenchContextHolder());
			isRootListeners.add(classIndicatorDictionaryPanel);
			addIndicatorFieldListener(classIndicatorDictionaryPanel);
			addIndicatorDictionaryListener(classIndicatorDictionaryPanel);
	
			// Add everything to the container
			GroupBox groupBox = new GroupBox(
				useClassNameAsIndicatorRadioButton,
				useClassIndicatorDictionaryRadioButton,
				classIndicatorDictionaryPanel
			);
	
			constraints.gridx      = 0;
			constraints.gridy      = 0;
			constraints.gridwidth  = 2;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 1;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(5, 0, 0, 0);
	
			indicatorSelectionSubPanel.add(groupBox, constraints);

		addHelpTopicId(useClassIndicatorFieldPanel, helpTopicId() + ".useClassIndicator");
		
		return useClassIndicatorFieldPanel;
	}
	
	protected void initializeLayout(Collection isRootListerners) {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(buildUseClassExtractionMethodPanel(isRootListerners), constraints);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		add(buildUseClassIndicatorPolicyPanel(isRootListerners), constraints);
		
		addHelpTopicId(this, helpTopicId());
		
		addIndicatorFieldListener(this);
	}

	public void updateEnablementStatus() {
		super.updateEnablementStatus();
		classIndicatorFieldComboBox.setEnabled(this.isRoot() && this.isIndicatorType() && !this.isAggregate());
	}

	   public boolean isAggregate() {
    	return ((MWRelationalDescriptor)this.getDescriptorHolder().getValue()).isAggregateDescriptor();
    }

}
