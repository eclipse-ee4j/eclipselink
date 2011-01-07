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
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.OXProjectNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema.XmlSchemaNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema.XmlSchemaRepositoryNode;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeAdapter;


public class SchemaPanelTest 
{
	/** Change this to change what schema you're test-loading */
	private final String schemaLocation = "file://C:/Paul/Documents/MappingWorkbench/JipProjects/XMLEmployee/config/employee.xsd";
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
	
	
	public static void main(String[] args) throws Exception {
		new SchemaPanelTest().exec(args);
	}
	
	
	private XmlSchemaNode schemaNode;
	private Component page;
	
	
	private SchemaPanelTest() {
		super();
	}
	
	private void exec(String[] args) 
		throws Exception 
	{
		this.schemaNode = this.buildNode();
		this.page = this.schemaNode.propertiesPage(null);
		
		this.openWindow();
	}
	
	private XmlSchemaNode buildNode()
		throws Exception
	{
		MappingsPlugin mappingsPlugin = (MappingsPlugin) MappingsPluginFactory.instance().createPlugin(this.buildWorkbenchContext().getApplicationContext());
		MWOXProject project = this.buildProject(schemaLocation);
		OXProjectNode projectNode = buildProjectNode(project, mappingsPlugin);
		return this.buildSchemaNode(projectNode);
	}
	
	private MWOXProject buildProject(String schemaLocation)
		throws Exception
	{
		MWOXProject project = new MWOXProject("Test Project", MappingsModelTestTools.buildSPIManager());
		MWXmlSchemaRepository repository = project.getSchemaRepository();
//		repository.createSchemaFromUrl("Test Schema" , schemaLocation);
		return project;
	}
	
	private OXProjectNode buildProjectNode(MWOXProject project, MappingsPlugin mappingsPlugin) {
		return (OXProjectNode) ClassTools.invokeMethod(mappingsPlugin, 
														"projectNode", 
														new Class[] {MWProject.class, WorkbenchContext.class}, 
														new Object[] {project, this.buildWorkbenchContext()} );
	}
	
	private WorkbenchContext buildWorkbenchContext() {
		return new TestWorkbenchContext(null, "org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginIconResourceFileNameMap");
	}
	
	private XmlSchemaNode buildSchemaNode(OXProjectNode projectNode) {
		// add a dummy listener so the models wake up
		projectNode.getChildrenModel().addListChangeListener(ListValueModel.VALUE, new ListChangeAdapter());
		// the last node should be the schema repository
		XmlSchemaRepositoryNode schemaRepositoryNode = (XmlSchemaRepositoryNode) projectNode.getChildrenModel().getItem(projectNode.getChildrenModel().size() - 1);
		// and again ...
		schemaRepositoryNode.getChildrenModel().addListChangeListener(ListValueModel.VALUE, new ListChangeAdapter());
		return (XmlSchemaNode) schemaRepositoryNode.getChildrenModel().getItem(0);
	}
	
		

	private void openWindow() {
		JFrame window = new JFrame(this.getClass().getName());
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setSize(600, 800);
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
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(this.page, BorderLayout.CENTER);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}
	
	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new BorderLayout());
		JTextField nameTextField = new JTextField();
		controlPanel.add(nameTextField, BorderLayout.CENTER);
		controlPanel.add(this.buildRenameButton(nameTextField), BorderLayout.EAST);
		controlPanel.add(this.buildSchemaReloadButton(), BorderLayout.SOUTH);
		return controlPanel;
	}
	
	private JButton buildRenameButton(JTextField renameTextField) {
		return new JButton(this.buildRenameAction(renameTextField));
	}
	
	private Action buildRenameAction(final JTextField renameTextField) {
		Action action = new AbstractAction("Rename Schema") {
			public void actionPerformed(ActionEvent event) {
				SchemaPanelTest.this.renameSchema(renameTextField.getText());
			}
		};
		action.setEnabled(true);
		return action;
	}
	
	private void renameSchema(String newSchemaName) {
		((MWXmlSchema) this.schemaNode.getValue()).setName(newSchemaName);
	}
	
	private JButton buildSchemaReloadButton() {
		return new JButton(this.buildSchemaReloadAction());
	}
	
	private Action buildSchemaReloadAction() {
		Action action = new AbstractAction("ReloadSchema") {
			public void actionPerformed(ActionEvent e) {
				SchemaPanelTest.this.reloadSchema();
			}
		};
		action.setEnabled(true);
		return action;
	}
	
	private void reloadSchema() {
//		try {
//			((MWXmlSchema) this.schemaNode.getValue()).reload();
//		}
//		catch (ResourceException re) {
//			re.fillInStackTrace();
//			System.out.print(re.getMessage());
//		}
	}
}
