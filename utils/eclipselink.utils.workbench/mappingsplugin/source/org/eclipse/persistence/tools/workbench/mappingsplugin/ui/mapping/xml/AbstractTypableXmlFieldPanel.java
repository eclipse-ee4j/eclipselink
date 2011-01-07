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
import javax.swing.JCheckBox;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;


public abstract class AbstractTypableXmlFieldPanel 
	extends AbstractXmlFieldPanel
{
	// **************** Construction ******************************************
	
	protected AbstractTypableXmlFieldPanel(ValueModel xmlFieldHolder, WorkbenchContextHolder contextHolder) {
		super(xmlFieldHolder, contextHolder);	
	}
	
	
	// **************** Initialization ****************************************
	
	protected JCheckBox buildTypedCheckBox() {
		JCheckBox checkBox = this.buildCheckBox("XML_FIELD_TYPED_CHECK_BOX", this.buildTypedButtonModel());
		this.createCheckBoxEnabler(checkBox);
		this.addHelpTopicId(checkBox, "mapping.xmlField.typed");
		return checkBox;
	}
	
	private ButtonModel buildTypedButtonModel() {
		return new CheckBoxModelAdapter(this.buildTypedValueHolder());
	}
	
	private PropertyValueModel buildTypedValueHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWXmlField.TYPED_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWXmlField) this.subject).isTyped());
			}
			
			protected void setValueOnSubject(Object value) {
				((MWXmlField) this.subject).setTyped(((Boolean) value).booleanValue());
			}
		};
	}
	
	private void createCheckBoxEnabler(JCheckBox checkBox) {
		// set enabled if xml data field is text field
		new ComponentEnabler(this.buildIsTextXpathHolder(), checkBox);
	}
	
	private ValueModel buildIsTextXpathHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWXmlField.XPATH_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWXmlField) this.subject).isTextXpath());
			}
		};
	}
}
