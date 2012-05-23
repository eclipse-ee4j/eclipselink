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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DefaultSessionLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.JavaLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ComboBoxSelection;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

/**
 * UI test for verifying the SC Logging Config selection with a comboBox widget.
 * The subject is a Session, the property to config is LOG_CONFIG_PROPERTY.
 * Based on the ComboBoxModelAdapterUITest.
 */
public class SCLoggingTypeUITest extends SCSessionUITest {

	private final static String STANDARD = "Standard";
	private final static String JAVA = "Java";
	private static List loggingTypeSelections;

	private PropertyValueModel selectionHolder;
	private ComboBoxModel comboBoxModel;

	public static void main( String[] args) throws Exception {
		new SCLoggingTypeUITest().exec( args);
	}

	private SCLoggingTypeUITest() {
		super();
	}

	protected String windowTitle() {
		return "Select a Logging Type:";
	}

	private void exec( String[] args) throws Exception {

		super.setUp();

		selectionHolder = this.buildSelectionHolder( subjectHolder());

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
		comboBox.setRenderer( buildLoggingListCellRenderer());
		return comboBox;
	}
	
	protected void printModel() {
		System.out.println( "subject.log( "+ subject().getLog().displayString() + " )");
	}

	protected void resetProperty() {

		subject().setDefaultLogging();
	}

	private ListValueModel buildListHolder() {

		return new SimpleListValueModel( getLoggingTypeSelections());
	}
	
	private ListCellRenderer buildLoggingListCellRenderer() {

		return new SimpleListCellRenderer() {

			protected String buildText( Object value) {
				return( value == null) ? "" : (( ComboBoxSelection)value).displayString();
			}
		};
	}

	private PropertyValueModel buildSelectionHolder( ValueModel subjectHolder) {

		return new PropertyAspectAdapter( subjectHolder, SessionAdapter.LOG_CONFIG_PROPERTY) {
			
			protected Object getValueFromSubject() {
				LogAdapter log = (( SessionAdapter)subject).getLog();
				return SCLoggingTypeUITest.this.getComboBoxSelectionFor( log);
			}
			protected void setValueOnSubject( Object value) {
				(( ComboBoxSelection)value).setPropertyOn( subject());

			}
		};
	}
	
	private ComboBoxSelection getComboBoxSelectionFor( LogAdapter log) {

		if( log instanceof DefaultSessionLogAdapter) {
			return getComboBoxSelectionNamed( STANDARD);
		}
		else if( log instanceof JavaLogAdapter) {
			return getComboBoxSelectionNamed( JAVA);
		}
		return null;
	}
	
	private ComboBoxSelection getComboBoxSelectionNamed( String displayString) {

		for( Iterator i = getLoggingTypeSelections().iterator(); i.hasNext(); ) {
			ComboBoxSelection item = ( ComboBoxSelection)i.next();
				
			if( item.displayString().equalsIgnoreCase( displayString))
				return item;
		}
		throw new NoSuchElementException( displayString);
	}
	
	protected List getLoggingTypeSelections() {
		if( loggingTypeSelections == null) {
			loggingTypeSelections = CollectionTools.list( buildLoggingTypes());
		}
		return loggingTypeSelections;
	}
	/**
	 * Returns the possible values for the property logConfig.
	 */
	private Object[] buildLoggingTypes() {

		Object[] loggingTypes = { 
			new ComboBoxSelection( STANDARD) {
					public void setPropertyOn( Object session) {
						(( SessionAdapter)session).setDefaultLogging();		
					}	
				},
			new ComboBoxSelection( JAVA) {
					public void setPropertyOn( Object session) {
						(( SessionAdapter)session).setJavaLogging();		
					}	
				}
			};
		return loggingTypes;
	}

}
