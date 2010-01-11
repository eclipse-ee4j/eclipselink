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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public final class BasicXmlFieldPanel 
	extends AbstractXmlFieldPanel
{
	// **************** Construction ******************************************
	
	BasicXmlFieldPanel(ValueModel xmlFieldHolder, WorkbenchContextHolder contextHolder) {
		super(xmlFieldHolder, contextHolder);	
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initializeLayout() {
		super.initializeLayout();
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// xpath label 
		JLabel xpathLabel = this.buildXpathLabel();
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		this.add(xpathLabel, constraints);
		this.addAlignLeft(xpathLabel);
		
		// xpath chooser
		XpathChooser chooser = this.buildXpathChooser();
		chooser.setAccessibleLabeler(xpathLabel);
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);
		this.add(chooser, constraints);
		this.addPaneForAlignment(chooser);
	}
}
