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


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.InheritancePolicyPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


/**
 * 
 */
public final class RelationalInheritancePolicyPropertiesPage
							extends InheritancePolicyPropertiesPage {

	private PropertyValueModel readSubclassesOnQueryHolder;

	private PropertyValueModel outerJoinAllSubclassesHolder;

	public RelationalInheritancePolicyPropertiesPage(PropertyValueModel node, WorkbenchContextHolder contextHolder) {
		super(node, contextHolder);
	}
		
	private ListChooser buildViewChooser() {
		return RelationalProjectComponentFactory.
					buildTableChooser(
						getSelectionHolder(), 
						buildViewChooserPropertyAdapter()	, 
						buildViewChooserDialogBuilder(), 
						getWorkbenchContextHolder()
					);
		
	}

	private DefaultListChooserDialog.Builder buildViewChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("VIEW_CHOOSER_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("VIEW_CHOOSER_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildTableStringConverter());
		return builder;
	}
	
	private StringConverter buildTableStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWTable) o).getName();
			}
		};
	}	
	
	private PropertyValueModel buildViewChooserPropertyAdapter() {
		return new PropertyAspectAdapter(getInheritancePolicyHolder(), MWRelationalDescriptorInheritancePolicy.READ_ALL_SUBCLASSES_VIEW_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalDescriptorInheritancePolicy) subject).getReadAllSubclassesView();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWRelationalDescriptorInheritancePolicy)subject).setReadAllSubclassesView((MWTable)value);
			}	
		};	
	}

	private PropertyValueModel buildReadSubclassesOnQueryBooleanHolder() {
	
		return new PropertyAspectAdapter(getInheritancePolicyHolder(), MWRelationalDescriptorInheritancePolicy.READ_SUBCLASSES_ON_QUERY_PROPERTY) {
			protected Object getValueFromSubject(){
				return Boolean.valueOf(((MWRelationalDescriptorInheritancePolicy) subject).isReadSubclassesOnQuery());
			}

			protected void setValueOnSubject(Object value) {
				((MWRelationalDescriptorInheritancePolicy)subject).setReadSubclassesOnQuery(((Boolean)value).booleanValue());
			}
		};
	}

	private PropertyValueModel buildOuterJoinAllSubclassesBooleanHolder() {
		
		return new PropertyAspectAdapter(getInheritancePolicyHolder(), MWRelationalDescriptorInheritancePolicy.OUTER_JOIN_ALL_SUBCLASSES) {
			protected Object getValueFromSubject(){
				return Boolean.valueOf(((MWRelationalDescriptorInheritancePolicy) subject).isOuterJoinAllSubclasses());
			}

			protected void setValueOnSubject(Object value) {
				((MWRelationalDescriptorInheritancePolicy)subject).setOuterJoinAllSubclasses(((Boolean)value).booleanValue());
			}
		};
	}

	private JPanel buildIsRootPanel(Collection isRootListeners) {
		classIndicatorPolicyPanel = new RelationalClassIndicatorPolicySubPanel(getSelectionHolder(), getInheritancePolicyHolder(), getWorkbenchContextHolder(), isRootListeners);
		return classIndicatorPolicyPanel;
	}
	
	protected Component buildPage() {
		JPanel contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints constraints = new GridBagConstraints();

		//Read subclasses check box
		readSubclassesOnQueryHolder = buildReadSubclassesOnQueryBooleanHolder();
		JCheckBox readSubclassOnQueryCheckBox = buildCheckBox("READ_SUBCLASSES_ON_QUERY", new CheckBoxModelAdapter(readSubclassesOnQueryHolder));

		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);

		contentPanel.add(readSubclassOnQueryCheckBox, constraints);
		addHelpTopicId(readSubclassOnQueryCheckBox, helpTopicId() + ".readSubclassesView");

		Collection components = new ArrayList();
		//Read subclasses check box sub panel
		JPanel readSubclassOnQueryPanel = new JPanel(new BorderLayout());
		readSubclassOnQueryPanel.setEnabled(readSubclassesOnQueryHolder.getValue() == Boolean.TRUE);
		ListChooser viewChooser = buildViewChooser();
		components.add(viewChooser);
		JLabel viewLabel = buildLabel("READ_SUBCLASSES_VIEW_OPTIONAL");
		viewLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("READ_SUBCLASSES_VIEW_OPTIONAL"));
		components.add(viewLabel);
		viewLabel.setLabelFor(viewChooser);

		readSubclassOnQueryPanel.add(viewChooser, BorderLayout.CENTER);
		readSubclassOnQueryPanel.add(viewLabel, BorderLayout.LINE_START);
		addHelpTopicId(readSubclassOnQueryPanel, helpTopicId() + ".readSubclassesView");

		Insets borderInsets = BorderFactory.createTitledBorder("m").getBorderInsets(this);

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, SwingTools.checkBoxIconWidth() + 5, 0, borderInsets.right + 5);

		contentPanel.add(readSubclassOnQueryPanel, constraints);
		new ComponentEnabler(readSubclassesOnQueryHolder, components);

		//outer join all subclasses check box
		outerJoinAllSubclassesHolder = buildOuterJoinAllSubclassesBooleanHolder();
		JCheckBox outerJoinAllSubclassCheckBox = buildCheckBox("OUTER_JOIN_ALL_SUBCLASSES", new CheckBoxModelAdapter(outerJoinAllSubclassesHolder));

		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);

		contentPanel.add(outerJoinAllSubclassCheckBox, constraints);
		addHelpTopicId(outerJoinAllSubclassCheckBox, helpTopicId() + ".outerJoinAllSubclasses");

		// Is Child widgets
		JRadioButton isChildRadioButton = buildRadioButton("IS_CHILD_DESC", buildIsChildDescriptorRadioButtonModel(getIsRootHolder()));
		addHelpTopicId(isChildRadioButton, helpTopicId() + ".isChild");

		JPanel isChildPanel = buildIsChildPanel(getIsRootHolder().getValue() == Boolean.FALSE);
		getIsRootHolder().addPropertyChangeListener(buildIsChildListener(isChildPanel));
		addHelpTopicId(isChildPanel, helpTopicId() + ".isChild");
		
		// Is Root widgets
		JRadioButton isRootRadioButton = buildRadioButton("IS_ROOT_DESC", buildIsRootParentDescriptorRadioButtonModel(getIsRootHolder()));
		addHelpTopicId(isRootRadioButton, helpTopicId() + ".isRoot");

		Collection isRootListeners = new Vector();
		JPanel isRootPanel = buildIsRootPanel(isRootListeners);
		isRootListeners.add(classIndicatorPolicyPanel);

		// Add everything to the container
		GroupBox isRootGroupBox = new GroupBox
		(
			isChildRadioButton, isChildPanel,
			isRootRadioButton, isRootPanel
		);

		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		contentPanel.add(isRootGroupBox, constraints);

		addHelpTopicId(getClassIndicatorPolicyPanel(), helpTopicId() + ".isRoot");
		addHelpTopicId(contentPanel, helpTopicId());
		addAllRootListeners(isRootListeners);

		return contentPanel;
	}

	protected String helpTopicIdPrefix() {
		return "descriptor.relational";
	}
}
