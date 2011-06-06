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

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public abstract class AbstractXmlFieldPanel 
	extends AbstractSubjectPanel
{
	// **************** Construction ******************************************
	
	protected AbstractXmlFieldPanel(ValueModel xmlFieldHolder, WorkbenchContextHolder contextHolder) {
		super(xmlFieldHolder, contextHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initializeLayout() {
		this.setBorder(
			BorderFactory.createCompoundBorder(
				this.buildTitledBorder("XML_FIELD_PANEL_TITLE"),
				BorderFactory.createEmptyBorder(0, 5, 5, 5)
			)
		);
		this.addHelpTopicId(this, "mapping.xmlField");
	}
	
	protected JLabel buildXpathLabel() {
		JLabel label = this.buildLabel("XML_FIELD_XPATH_CHOOSER_LABEL");
		this.addHelpTopicId(label, "mapping.xmlField.xpath");
		return label;
	}
	
	protected XpathChooser buildXpathChooser() {
		XpathChooser chooser = 
			new XpathChooser(
				this.getWorkbenchContextHolder(),
				this.getSubjectHolder()
			);
		this.addHelpTopicId(chooser, "mapping.xmlField.xpath");
		return chooser;
	}
	
	
	// **************** Convenience *******************************************
	
	protected MWXmlField xmlField() {
		return (MWXmlField) this.subject();
	}
	
	protected MWXpathContext xpathContext() {
		return this.xmlField().getXpathContext();
	}
}
