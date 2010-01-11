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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


public final class XpathChooser
	extends AbstractPanel
{
	/** This holds the xml field on which the xpath is being specified */
	private ValueModel xmlFieldHolder;
	
	/** This holds the current xml field's xpath */
	private PropertyValueModel xpathHolder;
	
	/** This is the accessible object used for "reading" my components */
	private Accessible labeler;
	
	
	// **************** Constructors ******************************************
	
	/** This is the preferred constructor */
	public XpathChooser(WorkbenchContextHolder contextHolder, ValueModel xmlFieldHolder) {
		super(contextHolder);
		this.initialize(xmlFieldHolder);
	}
	
	/** 
	 * Use this constructor if you wish to substitute some other sort of PVM for the 
	 * xpath holder (such as a buffered PVM)
	 */
	public XpathChooser(WorkbenchContextHolder contextHolder, ValueModel xmlFieldHolder, 
						PropertyValueModel xpathHolder) {
		super(contextHolder);
		this.initialize(xmlFieldHolder, xpathHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	private void initialize(ValueModel xmlFieldHolder) {
		this.initialize(xmlFieldHolder, this.buildXpathHolder(xmlFieldHolder));
	}
	
	private void initialize(ValueModel xmlFieldHolder, PropertyValueModel xpathHolder) {
		this.xmlFieldHolder = xmlFieldHolder;
		this.xpathHolder = xpathHolder;
		this.initializeLayout();
	}
	
	private PropertyValueModel buildXpathHolder(ValueModel xmlFieldHolder) {
		return new PropertyAspectAdapter(xmlFieldHolder, MWXmlField.XPATH_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWXmlField) this.subject).getXpath();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWXmlField) this.subject).setXpath((String) value);
			}
		};
	}
	
	private void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		
		JTextField textField = this.buildTextField();
		constraints.gridx 		= 1;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		this.add(textField, constraints);
		
		JButton browseButton = this.buildButton();
		constraints.gridx 		= 2;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.insets 		= new Insets(0, 5, 0, 0);
		this.add(browseButton, constraints);
		this.addAlignRight(browseButton);
	}
	
	private JTextField buildTextField() {
		JTextField textField = new JTextField();
		textField.setDocument(this.buildXpathDocument());
		return textField;
	}
	
	private DocumentAdapter buildXpathDocument() {
		return new DocumentAdapter(this.xpathHolder);
	}
	
	private JButton buildButton() {
		JButton button = this.buildBrowseButton("BROWSE_BUTTON_1", this.buildLabeler());
		button.addActionListener(this.buildBrowseAction());
		return button;
	}
	
	private Accessible buildLabeler() {
		return new Accessible() {
			public AccessibleContext getAccessibleContext() {
				if (XpathChooser.this.labeler != null) {
					return XpathChooser.this.labeler.getAccessibleContext();
				}
				else {
					return null;
				}
			}
		};
	}
	
	private ActionListener buildBrowseAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				XpathChooser.this.promptToSelectXpath();
			}
		};
	}
	
	
	// **************** Exposed ***********************************************
	
	/** The accessible object is used for JAWS reading of the text field and browse button */
	public void setAccessibleLabeler(Accessible labeler) {
		this.labeler = labeler;
		
		for (int i = this.getComponentCount(); i > 0; i --) {
			JComponent component = (JComponent) this.getComponent(i - 1);
			component.putClientProperty("labeledBy", labeler);
		}
	}
	
	
	// **************** Behavior **********************************************
	
	private void promptToSelectXpath() {
		XpathChooserDialog.promptToSelectXpath(this.xmlFieldHolder, this.xpathHolder, this.getWorkbenchContext());
	}
	
	private MWXmlField xmlField() {
		return (MWXmlField) this.xmlFieldHolder.getValue();
	}
	
	/**
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 * Overwritten to allow dissabling of the browse button and text field
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		for (int i = this.getComponentCount() - 1; i >= 0; i --) {
			this.getComponent(i).setEnabled(enabled);
		}
	}
}
