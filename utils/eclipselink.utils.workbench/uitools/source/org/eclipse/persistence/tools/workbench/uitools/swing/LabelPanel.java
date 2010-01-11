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
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.eclipse.persistence.tools.workbench.uitools.app.NullPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


/**
 * This simple panel is a workaround for JLabels not being model-driven.
 */
public class LabelPanel
	extends JPanel
{
	/**
	 * JLabels are not model-driven, so we need to hold on to the label here
	 * and set its icon and text explicitly, whenever the models change.
	 */
	private JLabel label;


	/**
	 * Construct a label (panel) that updates its icon and text in response
	 * to changes to the specified models.
	 */
	public LabelPanel(PropertyValueModel iconHolder, PropertyValueModel textHolder) {
		super(new BorderLayout());
		this.initialize(iconHolder, textHolder);
	}

	/**
	 * Construct a label (panel) that updates its text in response
	 * to changes to the specified model. The label will not have an icon.
	 */
	public LabelPanel(PropertyValueModel textHolder) {
		this(NullPropertyValueModel.instance(), textHolder);
	}

	protected void initialize(PropertyValueModel iconHolder, PropertyValueModel textHolder) {
		if ((iconHolder == null) || (textHolder == null)) {
			throw new NullPointerException();
		}
		iconHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildIconListener());
		textHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildTextListener());

		this.initializeLayout();

		this.setIcon((Icon) iconHolder.getValue());
		this.setText((String) textHolder.getValue());
	}

	protected PropertyChangeListener buildIconListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				LabelPanel.this.setIcon((Icon) e.getNewValue());
			}
			public String toString() {
				return "icon listener";
			}
		};
	}

	protected PropertyChangeListener buildTextListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				LabelPanel.this.setText((String) e.getNewValue());
			}
			public String toString() {
				return "text listener";
			}
		};
	}

	protected void initializeLayout() {
		this.label = this.buildLabel();
		this.label.setBorder(this.buildBorder());
		this.add(this.label, BorderLayout.LINE_START);
	}

	protected JLabel buildLabel() {
		return new JLabel();
	}

	protected Border buildBorder() {
		return BorderFactory.createEmptyBorder();
	}

	protected JLabel getLabel() {
		return this.label;
	}

	protected void setText(String text) {
		this.getLabel().setText(text);
	}

	protected void setIcon(Icon icon) {
		this.getLabel().setIcon(icon);
	}

}
