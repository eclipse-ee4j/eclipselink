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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ProjectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

/**
 * UI test for verifying the SC Project Class list with a list widget.
 * The subject is a DatabaseSession, the property to config is PROJECT_CLASS_PROPERTY.
 */
public class SCProjectClassUITest extends SCSessionUITest {

	private PropertyValueModel classListHolder;

	private ListModel stringListModel;
	private ListModel classListModel;
	
	private TextField textField;

	public static void main( String[] args) throws Exception {
		new SCProjectClassUITest().exec(args);
	}

	private SCProjectClassUITest() {
		super();
	}

	protected String windowTitle() {
		return "Session Project Classes:";
	}

	private void exec( String[] args) throws Exception {

		super.setUp();

		this.classListHolder = new SimplePropertyValueModel( new ClassList());

		CollectionValueModel stringListAdapter = buildStringListAdapter();
		CollectionValueModel classListAdapter = buildClassListAdapter();

		this.stringListModel = this.buildListModel( stringListAdapter);
		this.classListModel = this.buildListModel( classListAdapter); // new SortedListValueModelAdapter( classListAdapter));

		this.openWindow();
	}
	private String getTextField() {
		return this.textField.getText();
	}

	private CollectionValueModel buildStringListAdapter() {
		
		return new CollectionAspectAdapter( DatabaseSessionAdapter.ADDITIONAL_PROJECTS_COLLECTION, stringList()) {
			protected Iterator getValueFromSubject() {
				return ( stringList()).additionalProjects();
			}
		};
	}
	
	private CollectionValueModel buildClassListAdapter() {
		
		return new CollectionAspectAdapter( ClassList.CLASS_LIST, classList()) {
			protected Iterator getValueFromSubject() {
				return classList().list();
			}
		};
	}

	private ListModel buildListModel( CollectionValueModel listValueModel) {
		return new ListModelAdapter( listValueModel); // for sorted list wrap it in a SortedListValueModelAdapter
	}
	
	protected Component buildPropertyTestingPanel() {
		
		JPanel propertyListPanel = new JPanel( new GridLayout( 0, 1));
		propertyListPanel.add( this.buildPropertyListPanel());
		propertyListPanel.add( this.buildPropertyEntryPanel());
		return propertyListPanel;
	}
	
	protected Component buildPropertyListPanel() {
		
		JPanel taskListPanel = new JPanel( new GridLayout( 1, 0));
		taskListPanel.add( this.buildStringListPanel());
		taskListPanel.add( this.buildClassListPanel());
		return taskListPanel;
	}

	private Component buildStringListPanel() {
		return this.buildListPanel( " Project Class Name", this.stringListModel);
	}

	private Component buildClassListPanel() {
		return this.buildListPanel(" Project Class", this.classListModel);
	}

	private Component buildListPanel( String label, ListModel listModel) {
		
		JPanel listPanel = new JPanel( new BorderLayout());
		JLabel listLabel = new JLabel( label);
		listPanel.add( listLabel, BorderLayout.NORTH);

		JList listBox = new JList();
		listBox.setModel( listModel);
		listBox.setDoubleBuffered( true);
		listLabel.setLabelFor( listBox);
		listPanel.add( new JScrollPane( listBox), BorderLayout.CENTER);
		return listPanel;
	}
	protected Component buildMainPanel() {
		
		JPanel mainPanel = new JPanel( new BorderLayout());
		mainPanel.add( this.buildPropertyTestingPanel(), BorderLayout.CENTER);
		mainPanel.add( this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private Component buildPropertyEntryPanel() {
		JPanel addRemoveTaskPanel = new JPanel( new GridLayout( 3, 0));
		addRemoveTaskPanel.add( this.buildTextField());
//		addRemoveTaskPanel.add( this.buildAddButton());
//		addRemoveTaskPanel.add( this.buildRemoveButton());
		return addRemoveTaskPanel;
	}
	protected Component buildControlPanel() {
		
		JPanel controlPanel = new JPanel( new GridLayout( 1, 0));

		controlPanel.add( this.buildAddClassButton());
		controlPanel.add( this.buildRemoveButton());
//		controlPanel.add( this.buildResetPropertyButton());
//		controlPanel.add( this.buildClearModelButton());
//		controlPanel.add( this.buildRestoreModelButton());
		controlPanel.add( this.buildPrintModelButton());
		return controlPanel;
	}
	private TextField buildTextField() {
		this.textField = new TextField( 50);
		return this.textField;
	}
	private ClassList classList() {

		return ( ClassList)this.classListHolder.getValue();
	}
	private DatabaseSessionAdapter stringList() {

		return ( DatabaseSessionAdapter)subjectHolder().getValue();
	}

	private JButton buildAddClassButton() {
		return new JButton( this.buildAddClassAction());
	}

	private Action buildAddClassAction() {
		Action action = new AbstractAction( "add class") {
			public void actionPerformed( ActionEvent event) {
				SCProjectClassUITest.this.addProjectClass();
			}
		};
		action.setEnabled( true);
		return action;
	}

	private JButton buildRemoveButton() {
		return new JButton(this.buildRemoveAction());
	}

	private Action buildRemoveAction() {
		Action action = new AbstractAction( "remove class") {
			public void actionPerformed( ActionEvent event) {
				SCProjectClassUITest.this.removeProjectClass();
			}
		};
		action.setEnabled( true);
		return action;
	}

	private void addProjectClass() {

		String projectClass = this.getTextField();
		if( projectClass.length() != 0) {
			this.classList().add( projectClass);
			this.stringList().addProjectClassNamed( projectClass);
		}
	}
	private void removeProjectClass() {

		String projectClass = this.getTextField();
		if( projectClass.length() != 0) {
			ProjectAdapter project = (( DatabaseSessionAdapter)subject()).projectNamed(projectClass);
			this.classList().remove( projectClass);
			this.stringList().removeProject( project);
		}
	}

	protected void resetProperty() {
		
		Iterator i = (( DatabaseSessionAdapter)subject()).additionalProjects();
		
//		(( DatabaseSessionAdapter)subject()).removeProjectClassNamed( name);
	}
	
	protected void initialize() {
		super.initialize();
		
		windowW = 800;
		 windowH = 300;
	}
	protected void printModel() {
		System.out.println( "subject.projectClasses[");

		for( Iterator i = (( DatabaseSessionAdapter)subject()).additionalProjects(); i.hasNext(); ) {
			
			String className = ( String)i.next();
			System.out.println( "\t" + className);
		}
		System.out.println( "]");
	}
	

	private class ClassList extends AbstractModel {

		private List classList;

		public static final String CLASS_LIST = "classList";

		protected void initialize() {
			super.initialize();
			
			this.classList = new ArrayList();
			this.addAll( CollectionTools.list((( DatabaseSessionAdapter)subject()).additionalProjectNames()));
		}

		public ListIterator list() {
			return this.classList.listIterator();
		}
		public void add( String className) {
			Class aClass =  ClassTools.classForName( className);
			int index = this.classList.size();
			this.classList.add( index, aClass);
			this.fireItemAdded( CLASS_LIST, index, aClass);
		}
		public void addAll( Collection items) {
			Collection addedItems = new ArrayList();
			for( Iterator i = items.iterator(); i.hasNext(); ) {
				String className = ( String)i.next();
				addedItems.add( ClassTools.classForName( className));
			}
			this.addItemsToCollection( addedItems, this.classList, CLASS_LIST);
		}		

		public void remove( String className) {
			Class aClass =  ClassTools.classForName( className);
			int index = this.classList.indexOf( aClass);
			if (index != -1) {
				Object removedClass = this.classList.remove(index);
				this.fireItemRemoved( CLASS_LIST, index, removedClass);
			}
		}
		public void clearClassList() {
			this.classList.clear();
			this.fireListChanged( CLASS_LIST);
		}
	}

}
