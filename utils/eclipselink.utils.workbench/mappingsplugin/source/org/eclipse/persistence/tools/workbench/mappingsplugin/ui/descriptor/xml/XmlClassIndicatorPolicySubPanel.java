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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledBorder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ClassIndicatorPolicySubPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


/**
 * 
 */
public class XmlClassIndicatorPolicySubPanel
	extends ClassIndicatorPolicySubPanel 
	implements SpecifyFieldListener 
{
	private PropertyValueModel useXSITypeModel;
		
	private JRadioButton useXSITypeRadioButton;
	
	private JRadioButton specifyFieldRadioButton;
	
	private XpathChooser xpathChooser;

	private boolean isSpecifyField;
	
	public XmlClassIndicatorPolicySubPanel(PropertyValueModel descriptorHolder, PropertyValueModel inheritancePolicyHolder, WorkbenchContextHolder contextHolder, Collection isRootListeners) {
		super(descriptorHolder, inheritancePolicyHolder, contextHolder, isRootListeners);
		initialize(descriptorHolder);
	}
	
	protected void initialize(PropertyValueModel descriptorHolder) {
		PropertyValueModel schemaContextHolder = new PropertyAspectAdapter(descriptorHolder, MWXmlDescriptor.SCHEMA_CONTEXT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWXmlDescriptor) this.subject).getSchemaContext();
			}
		};
		schemaContextHolder.addPropertyChangeListener(buildSchemaContextPropertyChangeListener());		
	}
		
	private PropertyChangeListener buildSchemaContextPropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				//this looks funny, but forces the regeneration of the indicator type values only when use xsi type
				//is selected so that user defined values do not get blown away.
				if (getUseXSITypeModel().getValue() == Boolean.TRUE) {
					getUseXSITypeModel().setValue(Boolean.TRUE);
				}
			}
		};
	}
	
	private RadioButtonModelAdapter buildXSITypeAsIndicatorRadioButtonModel(PropertyValueModel useXSITypeHolder) {
		return new RadioButtonModelAdapter(useXSITypeHolder, Boolean.TRUE);
	}

	private ValueModel buildClassIndicatorFieldSelectionHolder() {
		return new PropertyAspectAdapter(getClassIndicatorFieldPolicyHolder()) {
			protected Object getValueFromSubject() {
				return ((MWXmlClassIndicatorFieldPolicy) this.subject).getField();
			}
		};
	}
	
	private RadioButtonModelAdapter buildSpecifyTypeAsIndicatorRadioButtonModel(PropertyValueModel useXSITypeHolder) {
		return new RadioButtonModelAdapter(useXSITypeHolder, Boolean.FALSE);
	}

	private JPanel buildUseClassIndicatorPolicyPanel(Collection rootListeners) {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel useClassIndicatorFieldPanel = new AccessibleTitledPanel(new GridBagLayout());
		
		JPanel useClassIndicatorFieldSubPanel = buildUseClassIndicatorFieldSubPanel(rootListeners);		

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
		
		useClassIndicatorFieldPanel.add(useClassIndicatorFieldSubPanel, constraints);
		
		addHelpTopicId(useClassIndicatorFieldPanel, helpTopicId() + ".useClassIndicator");

		return useClassIndicatorFieldPanel;
	}

	private JPanel buildUseClassIndicatorFieldSubPanel(Collection rootListeners) {
		JPanel useClassIndicatorFieldPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		initializeUseXSITypeAsIndicatorHolder();
		
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
		
			// use xsi type radio button
			this.useXSITypeRadioButton = buildRadioButton("USE_XSITYPE_AS_INDICATOR", buildXSITypeAsIndicatorRadioButtonModel(getUseXSITypeModel()));
			addHelpTopicId(this.useXSITypeRadioButton, helpTopicId() + ".useXSIType");

			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 1;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, 0, 0, 0);

			fieldSelectionSubPanel.add(this.useXSITypeRadioButton, constraints);
				
			// Class indicator field combo box
			this.specifyFieldRadioButton = buildRadioButton("SPECIFY_FIELD", buildSpecifyTypeAsIndicatorRadioButtonModel(getUseXSITypeModel()));
			addHelpTopicId(this.specifyFieldRadioButton, helpTopicId() + ".specifyField");

			constraints.gridx		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(0, 0, 0, 0);			
			
			fieldSelectionSubPanel.add(this.specifyFieldRadioButton, constraints);
			
			this.xpathChooser = 
				new XpathChooser(
					getWorkbenchContextHolder(),
					this.buildClassIndicatorFieldSelectionHolder()
				);
			this.xpathChooser.setAccessibleLabeler(this.specifyFieldRadioButton);
			constraints.gridx		= 0;
			constraints.gridy		= 2;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, SwingTools.checkBoxIconWidth(), 5, 5);

			fieldSelectionSubPanel.add(this.xpathChooser, constraints);
			addPaneForAlignment(this.xpathChooser);
			
		addHelpTopicId(fieldSelectionSubPanel, helpTopicId() + ".fieldSelection");

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
		
			// use class name as indicator radio button
			this.useClassNameAsIndicatorRadioButton = buildRadioButton("USE_CLASS_NAME_AS_INDICATOR", buildClassNameAsIndicatorRadioButtonModel(getUseNameModel()));
			this.useXSITypeModel.addPropertyChangeListener(PropertyValueModel.VALUE, buildUseXSITypeDictionaryListener(this.useClassNameAsIndicatorRadioButton));
			addHelpTopicId(this.useClassNameAsIndicatorRadioButton, helpTopicId() + ".classNameAsIndicator");

			// use class indicator dictionary
			this.useClassIndicatorDictionaryRadioButton = buildRadioButton("USE_CLASS_INDICATOR_DICTIONARY", buildClassIndicatorDictionaryRadioButtonModel(getUseNameModel()));
			this.useXSITypeModel.addPropertyChangeListener(PropertyValueModel.VALUE, buildUseXSITypeDictionaryListener(this.useClassIndicatorDictionaryRadioButton));
			addHelpTopicId(this.useClassIndicatorDictionaryRadioButton, helpTopicId() + ".classIndicatorDictionary");
		
		XmlClassIndicatorDictionarySubPanel classIndicatorDictionaryPanel = new XmlClassIndicatorDictionarySubPanel(getClassIndicatorFieldPolicyHolder(), getUseXSITypeModel(), getWorkbenchContextHolder());
		classIndicatorDictionaryPanel.setBorder(new AccessibleTitledBorder(useClassIndicatorDictionaryRadioButton.getText()));
		rootListeners.add(classIndicatorDictionaryPanel);
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

		addHelpTopicId(indicatorSelectionSubPanel, helpTopicId() + ".indicatorSelection");
		
		addHelpTopicId(useClassIndicatorFieldPanel, helpTopicId() + ".useClassIndicator");
	
		return useClassIndicatorFieldPanel;
	}

	protected void initializeLayout(Collection isRootListeners) {
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

		add(buildUseClassExtractionMethodPanel(isRootListeners), constraints);

		// Use Class Indicator field radio button
		this.useClassIndicatorFieldRadioButton = buildRadioButton("USE_CLASS_INDICATOR_FIELD", buildClassIndicatorPolicyRadioButtonModel(MWClassIndicatorPolicy.CLASS_INDICATOR_FIELD_TYPE));

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		add(useClassIndicatorFieldRadioButton, constraints);

		// Use Class Indicator field radio button
		JPanel useClassIndicatorFieldPanel = buildUseClassIndicatorPolicyPanel(isRootListeners);

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, SwingTools.checkBoxIconWidth(), 0, 0);

		add(useClassIndicatorFieldPanel, constraints);

		addHelpTopicId(this, helpTopicId());

		addIndicatorFieldListener(this);
		addSpecifyFieldListener(this);
	}

	private PropertyChangeListener buildUseXSITypeDictionaryListener(final Component component) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(evt.getNewValue() == Boolean.FALSE);
			}
		};
	}

	protected void initializeUseXSITypeAsIndicatorHolder() {
		this.useXSITypeModel = new PropertyAspectAdapter(getClassIndicatorFieldPolicyHolder(), MWXmlClassIndicatorFieldPolicy.USE_XSITYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWXmlClassIndicatorFieldPolicy) this.subject).isUseXSIType());
			}
			protected void setValueOnSubject(Object value) {
				((MWXmlClassIndicatorFieldPolicy) this.subject).setUseXSIType(((Boolean)value).booleanValue());
			}			
		};
	}
	
	public PropertyValueModel getUseXSITypeModel() {
		return this.useXSITypeModel;
	}

	private void addSpecifyFieldListener(final SpecifyFieldListener listener) {
		getUseXSITypeModel().addPropertyChangeListener(
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					boolean enabled = evt.getNewValue() == Boolean.FALSE;
					listener.updateSpecifyFieldStatus(enabled);
				}
			}
		);
	}

	public void updateSpecifyFieldStatus(boolean newValue) {
		this.isSpecifyField = newValue;
		updateEnablementStatus();	
	}
	
	public void updateEnablementStatus() {
		super.updateEnablementStatus();
		this.useXSITypeRadioButton.setEnabled(this.isRoot() && this.isIndicatorType());
		this.specifyFieldRadioButton.setEnabled(this.isRoot() && this.isIndicatorType());
		this.xpathChooser.setEnabled(this.isRoot() && this.isIndicatorType() && this.isSpecifyField());
	}

	public boolean isSpecifyField() {
		return this.isSpecifyField;
	}

}
