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
package org.eclipse.persistence.tools.workbench.framework.help;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


/**
 * This is the panel displayed by the help topic ID window.
 * It displays a label with the current Help Topic ID and an
 * icon indicating whether the Help Topic ID is valid.
 * The value models holding the topic ID and its validity
 * are maintained by the DevelopmentHelpManager.
 * 
 * This should only be used during "development" mode.
 */
final class HelpTopicIDPanel extends JPanel {
	private JLabel label;


	HelpTopicIDPanel(ValueModel helpTopicIDHolder, ValueModel helpTopicIDIsValidHolder) {
		super(new BorderLayout());
		this.initialize(helpTopicIDHolder, helpTopicIDIsValidHolder);
	}

	private void initialize(ValueModel helpTopicIDHolder, ValueModel helpTopicIDIsValidHolder) {
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		this.label = new JLabel();

		helpTopicIDHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildHelpTopicIDListener());
		helpTopicIDIsValidHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildHelpTopicIDIsValidHolderListener());

		this.setLabelText(helpTopicIDHolder.getValue());
		this.setLabelIcon(helpTopicIDIsValidHolder.getValue());

		this.add(this.label, BorderLayout.CENTER);
	}

	void setLabelText(Object value) {
		this.label.setText((String) value);
	}

	void setLabelIcon(Object value) {
		boolean helpTopicIDIsValid = ((Boolean) value).booleanValue();
		if (helpTopicIDIsValid) {
			this.label.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
		} else {
			this.label.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
		}
	}

	private PropertyChangeListener buildHelpTopicIDListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				HelpTopicIDPanel.this.setLabelText(evt.getNewValue());
			}
		};
	}

	private PropertyChangeListener buildHelpTopicIDIsValidHolderListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				HelpTopicIDPanel.this.setLabelIcon(evt.getNewValue());
			}
		};
	}

}
