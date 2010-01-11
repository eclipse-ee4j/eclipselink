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

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DefaultSessionLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

/**
 * UI test for verifying the SC Logging Config selection with a comboBox widget.
 * The subject is a Session, the property to config is LOG_CONFIG_PROPERTY.
 */
public class SCLoggingLevelUITest extends SCSessionUITest {

	private PropertyValueModel selectionHolder;
	private ComboBoxModel comboBoxModel;
	private PropertyValueModel logHolder;
	
	public static void main( String[] args) throws Exception {
		new SCLoggingLevelUITest().exec( args);
	}

	private SCLoggingLevelUITest() {
		super();
	}

	protected String windowTitle() {
		return "Select a Logging Level:";
	}

	private void exec( String[] args) throws Exception {

		super.setUp();

		logHolder = new SimplePropertyValueModel( subject().getLog());
		
		selectionHolder = this.buildSelectionHolder( logHolder);

		comboBoxModel =
			this.buildComboBoxModelAdapter( this.buildListHolder(), selectionHolder);

		this.openWindow();
	}

	private ComboBoxModel buildComboBoxModelAdapter( ListValueModel listHolder, PropertyValueModel selectionHolder) {

		return new ComboBoxModelAdapter( listHolder, selectionHolder);
	}

	protected Component buildPropertyTestingPanel() {

		JPanel propertyListPanel = new JPanel( new GridLayout( 1, 0));
		propertyListPanel.add( this.buildComboBox());
		propertyListPanel.add( this.buildComboBox());
		return propertyListPanel;
	}

	private JComboBox buildComboBox() {

		JComboBox comboBox = new JComboBox( comboBoxModel);
		return comboBox;
	}

	protected void printModel() {
		LogAdapter log = subject().getLog();
		String level = ( log instanceof  DefaultSessionLogAdapter) ?
				(( DefaultSessionLogAdapter)log).getLogLevel() : "";

		System.out.println( "subject.log( "+ level + " )");
	}

	protected void resetProperty() {

		(( DefaultSessionLogAdapter)subject().getLog()).setLogLevel( DefaultSessionLogAdapter.INFO_LOG_LEVEL);
	}
	protected void clearModel() {
		logHolder.setValue( null);
	}
	protected void restoreModel() {
		logHolder.setValue( subject().getLog());
	}

	private ListValueModel buildListHolder() {

		return new SimpleListValueModel( CollectionTools.list( 
			DefaultSessionLogAdapter.VALID_LOG_LEVEL));
	}

	private PropertyValueModel buildSelectionHolder( ValueModel subjectHolder) {

		return new PropertyAspectAdapter( subjectHolder, DefaultSessionLogAdapter.LOG_LEVEL_PROPERTY) {
		
			protected Object getValueFromSubject() {
				return (( DefaultSessionLogAdapter)subject).getLogLevel();
			}
			protected void setValueOnSubject( Object value) {
					(( DefaultSessionLogAdapter)subject).setLogLevel(( String)value);
			}
		};
	}

}
