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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaComplexTypeChooser;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooser;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


public abstract class AbstractAggregatableXmlFieldPanel extends AbstractXmlFieldPanel {
	
	
	private ValueModel mappingHolder;
	
	private String mappingPropertyString;
	
	protected AbstractAggregatableXmlFieldPanel(ValueModel mappingHolder, ValueModel xmlFieldHolder, WorkbenchContextHolder contextHolder, String mappingPropertyString) {
		super(xmlFieldHolder, contextHolder);
		this.mappingHolder = mappingHolder;
		this.mappingPropertyString = mappingPropertyString;
		initializeLayoutWithAggregatable();
	}
	
	@Override
	protected void initializeLayout() {
		super.initializeLayout();
		//Do nothing else here as we want to assure the mapping holders are initialized first
	}
	
	protected abstract void initializeLayoutWithAggregatable();
	
	protected JLabel buildElementTypeLabel() {
		return XmlMappingComponentFactory.buildElementTypeLabel(resourceRepository());
	}
	
	protected SchemaComplexTypeChooser buildElementTypeChooser(JLabel label) {
		return XmlMappingComponentFactory.buildElementTypeChooser(this.mappingHolder, getWorkbenchContextHolder(), label, this.mappingPropertyString);
	}

	protected JRadioButton buildAggregateRadioButton() {
		JRadioButton button = this.buildRadioButton("XML_FIELD_AGGREGATE_RADIO_BUTTON", this.buildAggregateButtonModel());
		this.addHelpTopicId(button, "mapping.xmlField.aggregate");
		return button;
	}
	
	protected ButtonModel buildAggregateButtonModel() {
		return new RadioButtonModelAdapter(this.buildAggregatedValue(), Boolean.TRUE);
	}
	
	protected PropertyValueModel buildAggregatedValue() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWXmlField.AGGREGATED_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWXmlField) this.subject).isAggregated());
			}
			
			protected void setValueOnSubject(Object value) {
				((MWXmlField) this.subject).setAggregated(((Boolean) value).booleanValue());
			}
		};
	}

	protected JRadioButton buildXpathRadioButton() {
		JRadioButton button = this.buildRadioButton("XML_FIELD_XPATH_RADIO_BUTTON", this.buildXpathButtonModel());
		this.addHelpTopicId(button, "mapping.xmlField.xpath");
		return button;
	}
	
	private ButtonModel buildXpathButtonModel() {
		return new RadioButtonModelAdapter(this.buildAggregatedValue(), Boolean.FALSE);
	}
	
	protected XpathChooser buildXpathChooser() {
		XpathChooser chooser = super.buildXpathChooser();
		this.createXpathChooserEnabler(chooser);
		return chooser;
	}
	
	private void createXpathChooserEnabler(XpathChooser chooser) {
		// set enabled if xml data field is not aggregated
		new ComponentEnabler(this.buildSpecifyXpathHolder(), chooser);
	}
	
	private ValueModel buildSpecifyXpathHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWXmlField.AGGREGATED_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(! ((MWXmlField) this.subject).isAggregated());
			}
		};
	}
}
