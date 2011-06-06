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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.TextField;
import java.util.Iterator;

import javax.swing.JPanel;


import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ProjectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;

/**
 * UI test for verifying the mappingProject class list with a AddRemoveListPanel.
 * The subject is a Session, the property to config is PROJECT_CLASS_COLLECTION.
 */
public class SCProjectClassListUITest extends SCSessionUITest {
	
	private TextField textField;

	public static void main( String[] args) throws Exception {
		new SCProjectClassListUITest().exec( args);
	}
	
	public SCProjectClassListUITest() {
		super();
	}

	protected String windowTitle() {
		return "Enter a Mapping Project Class:";
	}

	private void exec( String[] args) throws Exception {

		super.setUp();

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
		
		return new CollectionAspectAdapter( subjectHolder(), DatabaseSessionAdapter.ADDITIONAL_PROJECTS_COLLECTION) 		{
			protected Iterator getValueFromSubject() {

				return (( DatabaseSessionAdapter)subject).additionalProjects();
			}
		};
	}
	
	private AddRemoveListPanel.Adapter buildAddRemoveListPanelAdapter()
	{
		return new AddRemoveListPanel.Adapter() {
			
			public void addNewItem( ObjectListSelectionModel listSelectionModel) {
	
				String text =  SCProjectClassListUITest.this.textField().getText();
				if( text.length() > 0) 
					(( DatabaseSessionAdapter)subject()).addProjectClassNamed( text);
			}

			public void removeSelectedItems( ObjectListSelectionModel listSelectionModel) {
				
				DatabaseSessionAdapter session = ( DatabaseSessionAdapter)subject();
				Object[] selectedValues = listSelectionModel.getSelectedValues();
				for (int index = 0; index < selectedValues.length; index++) {
					
					session.removeProject((ProjectAdapter) selectedValues[index]);
				}
			}
		};
	}
	// - - - - - - - - -	
	protected TextField textField() {
		return this.textField;
	}
	
	protected void initialize() {
		super.initialize();
		
		windowH = 200;
		windowW = 500;
	}
	
	protected void printModel() {
		System.out.println( "subject.classMappingProject( ");
		for( Iterator i = (( DatabaseSessionAdapter)subject()).additionalProjects(); i.hasNext(); ) {
			System.out.println( "\t" + i.next());
		}
		System.out.println( " )");
	}
	
	protected void resetProperty() {

	}

}
