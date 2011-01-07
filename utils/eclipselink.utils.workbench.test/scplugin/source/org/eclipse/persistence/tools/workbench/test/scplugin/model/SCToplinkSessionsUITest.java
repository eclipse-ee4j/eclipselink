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
package org.eclipse.persistence.tools.workbench.test.scplugin.model;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.test.scplugin.app.swing.SCAbstractUITest;

import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DataSource;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;

/**
 * Test Adding and Removing Sessions using an AddRemoveListPanel
 * @author Tran Le
 */
public class SCToplinkSessionsUITest extends SCAbstractUITest {
	private TopLinkSessionsAdapter subject;
	private PropertyValueModel subjectHolder;
	private TextField textField;

	public static void main( String[] args) throws Exception {
		new SCToplinkSessionsUITest().exec( args);
	}
	
	public SCToplinkSessionsUITest() {
		super();
	}

	protected String windowTitle() {
		return "Enter a Database Session Name:";
	}

	private void exec( String[] args) throws Exception {

		setUp();

		this.openWindow();
	}
	
	protected Component buildPropertyTestingPanel() {
		
		JPanel propertyTestingPanel = new JPanel( new BorderLayout());

		propertyTestingPanel.add( this. buildProjectClassPanel(), BorderLayout.NORTH);
		propertyTestingPanel.add( this.buildPropertyEntryPanel());
	
		return propertyTestingPanel;
	}

	private AddRemoveListPanel buildProjectClassPanel() {
		
		return this.buildAddRemoveListPanel( 
			buildAddRemoveListPanelAdapter(), this.buildProjectClassListAdapter());
	}
	
	private Component buildPropertyEntryPanel() {
		JPanel addRemoveTaskPanel = new JPanel( new GridLayout( 1, 0));
		addRemoveTaskPanel.add( this.buildTextField());

		return addRemoveTaskPanel;
	}
	
	private TextField buildTextField() {
		this.textField = new TextField( 50);
		return this.textField;
	}
	
	private AddRemoveListPanel buildAddRemoveListPanel( AddRemoveListPanel.Adapter panelAdapter, ListValueModel listAdapter) {
		
		AddRemoveListPanel addRemoveListPanel = new AddRemoveListPanel(
			workbenchContext().getApplicationContext(),
			panelAdapter,
			listAdapter,
			AddRemoveListPanel.RIGHT
		);
		addRemoveListPanel.setCellRenderer( new SimpleListCellRenderer());

		return addRemoveListPanel;
	}
	
	private ListValueModel buildProjectClassListAdapter() {
		
		return new CollectionListValueModelAdapter( buildClassCollectionAdapter());
	}
	// - - - - - - - - -
	private CollectionValueModel buildClassCollectionAdapter() {
		
		return new CollectionAspectAdapter( subjectHolder(), TopLinkSessionsAdapter.SESSIONS_COLLECTION) 		{
			protected Iterator getValueFromSubject() {

				return (( TopLinkSessionsAdapter)subject).sessions();
			}
			protected int sizeFromSubject() {
				return (( TopLinkSessionsAdapter)subject).sessionsSize();
			}
		};
	}
	
	private AddRemoveListPanel.Adapter buildAddRemoveListPanelAdapter()
	{
		return new AddRemoveListPanel.Adapter() {
			
			public void addNewItem( ObjectListSelectionModel listSelectionModel) {
	
				String text =  SCToplinkSessionsUITest.this.textField().getText();
				if( text.length() > 0) {
				    DataSource ds  = new DataSource( "EISPlatform");
					subject().addDatabaseSessionNamed( text, noServerPlatform(), ds);
				}
			}

			public void removeSelectedItems( ObjectListSelectionModel listSelectionModel) {
				
				Object[] selectedValues = listSelectionModel.getSelectedValues();
				for (int index = 0; index < selectedValues.length; index++) {
					
					subject().removeSessionNamed((( SessionAdapter)selectedValues[index]).getName());
				}
			}
		};
	}
	// - - - - - - - - -	
	
	private TopLinkSessionsAdapter subject() {
		return this.subject;
	}	
	
	private PropertyValueModel subjectHolder() {
		return this.subjectHolder;
	}

	protected void setUp() {
		
		super.setUp();

		this.subject = getTopLinkSessions();
		
		this.subjectHolder = new SimplePropertyValueModel( this.subject);
	}
	
	protected TextField textField() {
		return this.textField;
	}
	
	protected void initialize() {
		super.initialize();
		
		windowH = 200;
		windowW = 500;
	}
	
	protected void printModel() {
		
		System.out.println( this.subject.toString());
	}
	
	protected void resetProperty() {}
	protected void clearModel() {}
	protected void restoreModel() {}
	
	protected void saveModel() {

		try {
			getTopLinkSessions().save();
		}
		catch( IOException e) {
			// TODO  - exeception handling
			e.printStackTrace();
		}							
	}
	
	private File promptForSaveDirectory() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle( "Save " + getTopLinkSessions().displayString());
				
		int buttonChoice = fileChooser.showDialog( window, "Select");
		if (buttonChoice != JFileChooser.APPROVE_OPTION) {
			return null;
		}
//		if( isValidSaveDirectory(fileChooser.getSelectedFile())) {
//			return fileChooser.getSelectedFile();
//		}
//		else {
//			showNeedEmptyDirectoryDialog();
//			return promptForSaveDirectory();
//		}
		return fileChooser.getSelectedFile();
	}
	/**
	 * Re-Use Restore button for Saving
	 */
	protected JButton buildRestoreModelButton() {
		return new JButton( this.buildSaveAction());
	}
	private Action buildSaveAction() {
		Action action = new AbstractAction("Save...") {
			public void actionPerformed( ActionEvent event) {
				SCToplinkSessionsUITest.this.saveModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

//	public File saveLocation() {
//		return getProject().getSaveDirectory();
//	}
	
}
