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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStringQueryFormat;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;



/**
 * This panel appears on the Named Queries->QueryFormatPanel, if the user chooses SQL or EJBQL
 * for the query format.  It contains a panel with textArea for editing the query string
 */
final class StringQueryFormatSubPanel 
	extends AbstractSubjectPanel
{
				
	StringQueryFormatSubPanel(ValueModel queryFormatHolder, WorkbenchContextHolder contextHolder) {
		super(queryFormatHolder, contextHolder);
	}
	
	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		// Query string format label
		JLabel queryStringLabel = buildLabel("QUERY_STRING_PANEL_LABEL");
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);
		add(queryStringLabel, constraints);
		
		JTextArea queryStringTextArea = buildQueryStringTextArea();
		queryStringLabel.setLabelFor(queryStringTextArea);
		JScrollPane stringAreaPane = new JScrollPane(queryStringTextArea);
		stringAreaPane.setPreferredSize(new Dimension(10, 75));
		stringAreaPane.setMinimumSize(new Dimension(10, 75));
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(1, 0, 0, 0);
		add(stringAreaPane, constraints);

//		new ComponentEnabler(buildEnablementHolder(), new Component[] { queryStringLabel, queryStringTextArea});
	}

//	private PropertyValueModel buildEnablementHolder() {
//		return new TransformationPropertyValueModel(getSubjectHolder()) {
//			protected Object transform(Object value) {
//				return (value != null) ? Boolean.TRUE : Boolean.FALSE;
//			}
//		};
//	}
	
	// ************ query string ***********
	private JTextArea buildQueryStringTextArea() {
		JTextArea textArea =  new JTextArea(buildQueryStringDocument());
		textArea.setFont(UIManager.getFont("Label.font"));
		if (textArea.getFont() == null) // Make sure LaF doesn't have a null font
			textArea.setFont(new Font("dialog", Font.PLAIN, 12));
		
		return textArea;
	}
	
	private Document buildQueryStringDocument() {
		return new DocumentAdapter(buildQueryStringHolder());
	}
	
	private PropertyValueModel buildQueryStringHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWStringQueryFormat.QUERY_STRING_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWStringQueryFormat) subject).getQueryString();
			}
			protected void setValueOnSubject(Object value) {
				((MWStringQueryFormat) subject).setQueryString((String) value);
			}
		};
	}

}
