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

import java.awt.BorderLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.FieldTransformerAssociationEditingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public final class XmlFieldTransformerAssociationEditingPanel
	extends FieldTransformerAssociationEditingPanel
{
	
	// **************** Constructors ******************************************
	
	XmlFieldTransformerAssociationEditingPanel(FieldTransformerAssociationSpec associationSpec, WorkbenchContext context) {
		super(associationSpec, context);
	}
	
	
	// **************** Initialization ****************************************
	
	/** Not really a "field" - we create an xpath chooser panel here */
	protected JPanel buildFieldChooserPanel() {
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, offset.left, 0, offset.right));
		
		JLabel xpathLabel = this.buildLabel("XML_FIELD_TRANSFORMER_ASSOCIATION_PANEL_FIELD_CHOOSER");
		panel.add(xpathLabel, BorderLayout.LINE_START);
		addAlignLeft(xpathLabel);

		XpathChooser chooser = this.buildXpathChooser();
		chooser.setAccessibleLabeler(xpathLabel);
		panel.add(chooser, BorderLayout.CENTER);
		addPaneForAlignment(chooser);
		
		return panel;
	}
	
	private XpathChooser buildXpathChooser() {
		return new XpathChooser(
			this.getWorkbenchContextHolder(),
			this.associationSpec().xmlFieldHolder(),
			this.associationSpec().xpathHolder()
		);
	}
	
	
	// **************** Convenience *******************************************
	
	private XmlFieldTransformerAssociationSpec associationSpec() {
		return (XmlFieldTransformerAssociationSpec) this.associationSpec;
	}
	
	
	// **************** Member classes ****************************************
	
	public static interface XmlFieldTransformerAssociationSpec
		extends FieldTransformerAssociationSpec
	{
		/** Should return an unchanging holder, holding an unchanging value */
		ValueModel xmlFieldHolder();
		
		/** Should return an unchanging holder (only the value may change) */
		PropertyValueModel xpathHolder();
	}
}
