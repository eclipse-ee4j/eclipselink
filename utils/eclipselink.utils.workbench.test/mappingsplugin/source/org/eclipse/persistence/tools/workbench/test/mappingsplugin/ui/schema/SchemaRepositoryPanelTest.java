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
package org.eclipse.persistence.tools.workbench.test.mappingsplugin.ui.schema;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.OXProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema.XmlSchemaRepositoryNode;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeAdapter;


public final class SchemaRepositoryPanelTest
{
	/** Change these to change what schemas you're test-loading */
		/**
		 choices:
		 	http://www.w3.org/2001/XMLSchema.xsd
		 	file://C:/Paul/XMLSpy/Examples/ipo.xsd
		 	file://C:/Paul/XMLSpy/Examples/OrgChart.xsd
		 	file://C:/Paul/Documents/MappingWorkbench/JipProjects/XMLEmployee/config/employee.xsd
		 	file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/BasicAttributeGroup.xsd
		 	file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/BasicContentModels.xsd
		 	file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/BasicSubstitutionGroup.xsd
			file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/SchemaWithIdentityConstraints.xsd
			file://C:/Development/TopLink/Main/mwdev-schema/resource/schema/TempForTesting.xsd
		*/
	private final static String[] SCHEMA_LOCATIONS_A = new String[] {
			"file://C:/Paul/XMLSpy/Examples/ipo.xsd",
		 	"file://C:/Paul/XMLSpy/Examples/OrgChart.xsd",
		 	"file://C:/Paul/Documents/MappingWorkbench/JipProjects/XMLEmployee/config/employee.xsd"};
	
	private final static String[] SCHEMA_LOCATIONS_B = new String[] {
			"http://www.w3.org/2001/XMLSchema.xsd"};
	
		
	
	public static void main(String[] args) throws Exception {
		new SchemaRepositoryPanelTest().exec(args);
	}
	
	
	// **************** Instance variables ************************************
	
	private XmlSchemaRepositoryNode[] nodes;
	private int index;
	private Component page;
	private boolean pageIsCleared;
	
	private JPanel mainPanel;
	
	
	private SchemaRepositoryPanelTest() {
		super();
	}
	
	private void exec(String[] args) 
		throws Exception 
	{
		this.nodes = this.buildNodes();
		this.index = 0;
		this.page = nodes[index].propertiesPage(null);
		this.pageIsCleared = false;
		
		this.openWindow();
	}
	
	private XmlSchemaRepositoryNode[] buildNodes() 
		throws Exception
	{
		MappingsPlugin mappingsPlugin = (MappingsPlugin) MappingsPluginFactory.instance().createPlugin(this.buildWorkbenchContext().getApplicationContext());
		
		return new XmlSchemaRepositoryNode[] {
			this.buildSchemaRepositoryNode("Repository A", SCHEMA_LOCATIONS_A, mappingsPlugin),
			this.buildSchemaRepositoryNode("Repository B", SCHEMA_LOCATIONS_B, mappingsPlugin)
		};
	}
	
	private XmlSchemaRepositoryNode buildSchemaRepositoryNode(String projectName, String[] schemaLocations, MappingsPlugin mappingsPlugin) 
		throws Exception
	{
		MWOXProject project = this.buildProject(projectName, schemaLocations);
		OXProjectNode projectNode = buildProjectNode(project, mappingsPlugin);
		return this.buildSchemaRepositoryNode(projectNode);
	}
	
	private MWOXProject buildProject(String projectName, String[] schemaLocations)
		throws Exception
	{
		MWOXProject project = new MWOXProject(projectName, MappingsModelTestTools.buildSPIManager());
		MWXmlSchemaRepository repository = project.getSchemaRepository();
				
		return project;
	}
	
	private OXProjectNode buildProjectNode(MWOXProject project, MappingsPlugin mappingsPlugin) {
		return (OXProjectNode) ClassTools.invokeMethod(mappingsPlugin, 
														"projectNode", 
														new Class[] {MWOXProject.class, WorkbenchContext.class}, 
														new Object[] { project, this.buildWorkbenchContext()} );
	}
	
	
	private WorkbenchContext buildWorkbenchContext() {
		return (WorkbenchContext) ClassTools.invokeMethod(this.buildApplication(), "getApplicationContext");
	}

	private Object buildApplication() {
		try {
			return ClassTools.newInstance("org.eclipse.persistence.tools.workbench.framework.internal.FrameworkApplication");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private XmlSchemaRepositoryNode buildSchemaRepositoryNode(OXProjectNode projectNode) {
		// add a dummy listener so the models wake up
		projectNode.getChildrenModel().addListChangeListener(ListValueModel.VALUE, new ListChangeAdapter());
		// the last node should be the schema repository
		return (XmlSchemaRepositoryNode) projectNode.getChildrenModel().getItem(projectNode.getChildrenModel().size() - 1);
	}
	
	private void openWindow() {
		JFrame window = new JFrame(this.getClass().getName());
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setSize(600, 500);
		window.setLocation(300, 300);
		window.setVisible(true);
	}
	
	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				System.exit(0);
			}
		};
	}
	
	private Component buildMainPanel() {
		this.mainPanel = new JPanel(new BorderLayout());
		this.mainPanel.add(this.page, BorderLayout.CENTER);
		this.mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return this.mainPanel;
	}
	
	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(1, 0));
		controlPanel.add(this.buildPreviousButton());
		controlPanel.add(this.buildNextButton());
		controlPanel.add(this.buildClearButton());
		controlPanel.add(this.buildPrintButton());
		return controlPanel;
	}
	
	private JButton buildPreviousButton() {
		return new JButton(this.buildPreviousAction());
	}
	
	private Action buildPreviousAction() {
		Action action = new AbstractAction("previous") {
			public void actionPerformed(ActionEvent event) {
				SchemaRepositoryPanelTest.this.previous();
			}
		};
		action.setEnabled(true);
		return action;
	}
	
	private void previous() {
		if (this.index > 0) {
			int oldIndex = this.index;
			this.index --;
			this.installNewPage(oldIndex, this.index);
		}
	}
	
	private JButton buildNextButton() {
		return new JButton(this.buildNextAction());
	}
	
	private Action buildNextAction() {
		Action action = new AbstractAction("next") {
			public void actionPerformed(ActionEvent event) {
				SchemaRepositoryPanelTest.this.next();
			}
		};
		action.setEnabled(true);
		return action;
	}
	
	private void next() {
		if (this.index < this.nodes.length - 1) {
			int oldIndex = this.index;
			this.index ++;
			this.installNewPage(oldIndex, this.index);
		}
	}
	
	private void installNewPage(int oldIndex, int newIndex) {
		// remove old page...
		this.mainPanel.remove(this.page);
		this.nodes[oldIndex].releasePropertiesPage(this.page);
		
		// ...add new page
		this.page = this.nodes[newIndex].propertiesPage(null);
		this.mainPanel.add(this.page, BorderLayout.CENTER);
		
		// since the panel is already displayed, we need to refresh it
		this.mainPanel.revalidate();
		this.mainPanel.repaint();
	}
	
	private JButton buildClearButton() {
		return new JButton(this.buildClearAction());
	}
	
	private Action buildClearAction() {
		Action action = new AbstractAction("clear") {
			public void actionPerformed(ActionEvent event) {
				SchemaRepositoryPanelTest.this.clear();
			}
		};
		action.setEnabled(true);
		return action;
	}
	
	private void clear() {
		if (this.pageIsCleared) {
			this.nodes[this.index].propertiesPage(null);		// ignore what is returned
			this.pageIsCleared = false;
		} else {
			this.nodes[this.index].releasePropertiesPage(this.page);
			this.pageIsCleared = true;
		}
	}
	
	private JButton buildPrintButton() {
		return new JButton(this.buildPrintAction());
	}
	
	private Action buildPrintAction() {
		Action action = new AbstractAction("print") {
			public void actionPerformed(ActionEvent event) {
				SchemaRepositoryPanelTest.this.print();
			}
		};
		action.setEnabled(true);
		return action;
	}
	
	protected void print() {
		MWXmlSchemaRepository currentSchemaRepository = this.currentSchemaRepository();
		
		if (currentSchemaRepository == null) {
			return;
		}
		
		System.out.println("current schema repository: " + currentSchemaRepository);
		System.out.println("\tproject: " + currentSchemaRepository.getProject().getName());
		
		for (Iterator stream = currentSchemaRepository.schemas(); stream.hasNext(); ) {
			MWXmlSchema schema = (MWXmlSchema) stream.next();
			System.out.println("\tschema: " + schema.getName());
		}
	}
	
	private MWXmlSchemaRepository currentSchemaRepository() {
		return (MWXmlSchemaRepository) this.nodes[this.index].getValue();
	}
}
