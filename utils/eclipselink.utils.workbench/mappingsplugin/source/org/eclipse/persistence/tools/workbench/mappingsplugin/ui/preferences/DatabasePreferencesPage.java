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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.preferences;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


/**
 * Preferences page for general (non-plug-in-specific) settings used by
 * the framework.
 */
final class DatabasePreferencesPage extends AbstractPanel {
	
	DatabasePreferencesPage(PreferencesContext context) {
		super(context);
		this.intializeLayout();
	}
	
	private void intializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();


		//database driver class
		JComponent dbDriverClassTextField = this.buildLabeledTextField("PREFERENCES.MAPPINGS.DATABASE.DBDRIVER", this.buildDbDriverClassDocumentAdapter());
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 10, 5, 10);
		add(dbDriverClassTextField, constraints);
		
		//database url
		JComponent dbUrlTextField = this.buildLabeledTextField("PREFERENCES.MAPPINGS.DATABASE.URL", this.buildDbConnectionUrlDocumentAdapter());
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 10, 5, 10);
		add(dbUrlTextField, constraints);
		
		addHelpTopicId(this, "preferences.database");
	}
	// ***** db driver class text field
	private Document buildDbDriverClassDocumentAdapter() {
		return new DocumentAdapter(this.buildBufferedDbDriverClassAdapter());
	}

	private PropertyValueModel buildBufferedDbDriverClassAdapter() {
		return new BufferedPropertyValueModel(this.buildDbDriverClassAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildDbDriverClassAdapter() {
		return new PreferencePropertyValueModel(this.preferences(), MWLoginSpec.DB_DRIVER_CLASS_PREFERENCE);
	}
	// ***** db connection url text field
	private Document buildDbConnectionUrlDocumentAdapter() {
		return new DocumentAdapter(this.buildBufferedDbConnectionUrlAdapter());
	}

	private PropertyValueModel buildBufferedDbConnectionUrlAdapter() {
		return new BufferedPropertyValueModel(this.buildDbConnectionUrlAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildDbConnectionUrlAdapter() {
		return new PreferencePropertyValueModel(this.preferences(), MWLoginSpec.DB_CONNECTION_URL_PREFERENCE);
	}

}
