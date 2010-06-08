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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;

/**
 * UI test for verifying the SC Login ExternalConnectionPooling with a test checkbox widget.
 * The subject is a Login, the property to config is EXTERNAL_CONNECTION_POOLING.
 */
public class SCExternalConnectionPoolingUITest extends SCDatabaseLoginUITest {

	private PropertyValueModel booleanHolder;
	private ButtonModel buttonModel;

	public static void main(String[] args) throws Exception {
		new SCExternalConnectionPoolingUITest().exec(args);
	}

	private SCExternalConnectionPoolingUITest() {
		super();
	}
	
	protected String windowTitle() {
		return "Setup:";
	}

	private void exec( String[] args) throws Exception {

		setUp();
		
		booleanHolder = this.buildBooleanHolder( subjectHolder());
		
		buttonModel = this.buildCheckBoxModelAdapter( booleanHolder);
		
		this.openWindow();
	}

	private PropertyValueModel buildBooleanHolder( ValueModel subjectHolder) {
		
		return new PropertyAspectAdapter( subjectHolder, DatabaseSessionAdapter.EXTERNAL_CONNECTION_POOLING_PROPERTY) {
			protected Object getValueFromSubject() {
				LoginAdapter login = (LoginAdapter) subject;
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) login.getParent();
				
				return Boolean.valueOf(session.usesExternalConnectionPooling());
			}
			protected void setValueOnSubject( Object value) {
				LoginAdapter login = (LoginAdapter) subject;
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) login.getParent();
				
				session.setExternalConnectionPooling((( Boolean)value).booleanValue());
			}
		};
	}

	private ButtonModel buildCheckBoxModelAdapter( PropertyValueModel booleanHolder) {
		
		return new CheckBoxModelAdapter( booleanHolder);
	}

	protected Component buildPropertyTestingPanel() {
		
		JPanel taskListPanel = new JPanel( new GridLayout( 1, 0));
		taskListPanel.add( this.buildCheckBox());
		taskListPanel.add( this.buildCheckBox());
		return taskListPanel;
	}

	private JCheckBox buildCheckBox() {
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setText( "External Connection Pooling");
		checkBox.setModel( buttonModel);
		return checkBox;
	}
	
	protected void resetProperty() {
		
		subject().setUserName( "");
	}
}
