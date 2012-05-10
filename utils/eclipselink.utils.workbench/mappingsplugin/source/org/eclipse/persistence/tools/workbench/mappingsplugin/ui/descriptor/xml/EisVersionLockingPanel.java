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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.VersionLockingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;



final class EisVersionLockingPanel 
	extends VersionLockingPanel 
{
	EisVersionLockingPanel(ValueModel lockingPolicyHolder, WorkbenchContextHolder contextHolder) {
		super(lockingPolicyHolder, contextHolder);
	}
	
	protected JPanel buildVersionLockingFieldChooser() {		
		GridBagConstraints constraints = new GridBagConstraints();
		Pane lockingPanel = new Pane(new GridBagLayout());
		
		JLabel lockingFieldLabel = buildLabel("EIS_LOCKING_POLICY_XPATH");
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		lockingPanel.add(lockingFieldLabel, constraints);
		
		XpathChooser xpathChooser = 
			new XpathChooser(
				this.getWorkbenchContextHolder(),
				buildLockingFieldSelectionHolder()
			);
		xpathChooser.setAccessibleLabeler(lockingFieldLabel);
		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);
		lockingPanel.add(xpathChooser, constraints);
		
		return lockingPanel;
	}
	
	private PropertyValueModel buildLockingFieldSelectionHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder()) {
			protected Object getValueFromSubject() {
				return ((MWEisDescriptorLockingPolicy) this.subject).getVersionLockField();
			}
		};
	}
}
