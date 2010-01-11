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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.DefaultingContainerClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;



public final class DirectMapContainerPolicyPanel extends AbstractSubjectPanel {

	// **************** Variables *********************************************
	
	private PropertyValueModel directMapContainerPolicyHolder;
    private PropertyValueModel defaultingContainerClassHolder;
	
	// **************** Constructors ******************************************
	
	public DirectMapContainerPolicyPanel(PropertyValueModel directMapMappingHolder,
	                                     WorkbenchContextHolder contextHolder) {
		super(directMapMappingHolder, contextHolder);
	}
    
    protected void initialize(ValueModel subjectHolder) {
        super.initialize(subjectHolder);
        this.directMapContainerPolicyHolder = this.buildDirectMapContainerPolicyHolder();
        this.defaultingContainerClassHolder = buildDefaultingContainerClassHolder();
    }
    
    private PropertyValueModel buildDefaultingContainerClassHolder() {
        return new PropertyAspectAdapter(this.directMapContainerPolicyHolder) {
            protected Object getValueFromSubject() {
                return ((MWDirectMapContainerPolicy) this.subject).getDefaultingContainerClass(); 
            }
        };
    }
    
	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return ((MWModel) DirectMapContainerPolicyPanel.this.subject()).getRepository();
			}
		};
	}

	private ItemListener buildContainerClassChooserEnabler(final ClassChooserPanel containerClassChooserPanel)
	{
		return new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				containerClassChooserPanel.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		};
	}
	
	private PropertyValueModel buildDirectMapContainerClassHolder() {
		return new PropertyAspectAdapter(this.defaultingContainerClassHolder, DefaultingContainerClass.CONTAINER_CLASS_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((DefaultingContainerClass) this.subject).getContainerClass();
			}
			
			protected void setValueOnSubject(Object value) {
				((DefaultingContainerClass) this.subject).setContainerClass((MWClass) value);
			}
		};
	}
	
	private PropertyValueModel buildDirectMapContainerPolicyHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWContainerMapping.CONTAINER_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWRelationalDirectMapMapping) this.subject).getContainerPolicy();
			}
		};
	}
	
	private ButtonModel buildOverrideDefaultClassCheckBoxAdapter() {
		return new CheckBoxModelAdapter(buildOverrideDefaultClassHolder());
	}

	private PropertyValueModel buildOverrideDefaultClassHolder() {
		return new TransformationPropertyValueModel(this.buildUseDefaultContainerClassHolder()) {
			
			private Boolean oppositeValue(Boolean value) {
				return (value == null) ? null : Boolean.valueOf(! value.booleanValue());
			}
			
			protected Object reverseTransform(Object value) {
				return this.oppositeValue((Boolean) value);
			}
			protected Object transform(Object value) {
				return this.oppositeValue((Boolean) value);
			}
		};
	}
	
	private PropertyValueModel buildUseDefaultContainerClassHolder() {
		return new PropertyAspectAdapter(this.defaultingContainerClassHolder, DefaultingContainerClass.USES_DEFAULT_CONTAINER_CLASS_PROPERTY) {
			protected Object getValueFromSubject() {
				// return the opposite of usesDefaultContainerClass
				return Boolean.valueOf(((DefaultingContainerClass) this.subject).usesDefaultContainerClass());
			}
			
			protected void setValueOnSubject(Object value) {
				// set the opposite value for useDefaultContainerClass
				((DefaultingContainerClass) this.subject).setUseDefaultContainerClass(((Boolean) value).booleanValue());
			}
		};
	}
	
	// **************** Initialization ****************************************
	protected void initializeLayout() {
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();

		// Custom Class check box
		JCheckBox customClassCheckBox = 
			buildCheckBox("COLLECTION_CONTAINER_POLICY_OVERRIDE_DEFAULT_CLASS_CHECK_BOX", buildOverrideDefaultClassCheckBoxAdapter());
		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);
		add(customClassCheckBox, constraints);
		addAlignLeft(customClassCheckBox);

		// Custom Class chooser
		ClassChooserPanel containerClassChooserPanel = ClassChooserTools.buildPanel(
			this.buildDirectMapContainerClassHolder(),
			this.buildClassRepositoryHolder(),
			ClassChooserTools.buildDeclarableReferenceFilter(),
			new JLabel(resourceRepository().getString("COLLECTION_CONTAINER_POLICY_OVERRIDE_DEFAULT_CLASS_CHECK_BOX")),
			this.getWorkbenchContextHolder()
		);
		containerClassChooserPanel.setEnabled(customClassCheckBox.isSelected());
		constraints.gridx       = 1;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);
		add(containerClassChooserPanel, constraints);
		addPaneForAlignment(containerClassChooserPanel);
		customClassCheckBox.addItemListener(buildContainerClassChooserEnabler(containerClassChooserPanel));

		addHelpTopicId(this, "mapping.containerPolicy");
	}
}
