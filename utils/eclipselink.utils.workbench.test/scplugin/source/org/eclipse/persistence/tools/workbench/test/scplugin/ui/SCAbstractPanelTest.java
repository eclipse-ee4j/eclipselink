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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui;

// JDK
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.Application;
import org.eclipse.persistence.tools.workbench.framework.NodeManager;
import org.eclipse.persistence.tools.workbench.framework.Plugin;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.RedirectedPreferencesApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.SimpleWorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.internal.FrameworkResourceBundle;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.UIToolsIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.uitools.UIToolsResourceBundle;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.SCPluginFactory;
import org.eclipse.persistence.tools.workbench.scplugin.SCPluginResourceBundle;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.test.scplugin.AllSCTests;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * @author Tran Le
 * @author Pascal Filion
 * @version 1.0a
 */
public abstract class SCAbstractPanelTest extends AbstractPanelTest
{
	private PropertyValueModel nodeHolder;
	private ProjectNode projectNode;
	private PropertyValueModel selectionHolder;
	protected int windowX, windowW, windowH;

	public SCAbstractPanelTest(SCAbstractPanelTest parentTest)
	{
		super(parentTest);
	}

	public SCAbstractPanelTest(String name)
	{
		super(name);
	}

	private WorkbenchContext buildWorkbenchContext()
	{
		WorkbenchContext context = buildFrameworkWorkbenchContext();
		context = buildUIToolsContext(context);
		context = buildSCContext(context);
		return context;
	}

	private Action buildClearModelAction() {
		Action action = new AbstractAction("clear model") {
			public void actionPerformed(ActionEvent event) {
				SCAbstractPanelTest.this.clearModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	protected JButton buildClearModelButton() {
		return new JButton( this.buildClearModelAction());
	}

	protected JDialog buildCloneWindow() {

		try
		{
			JDialog cloneWindow = new JDialog(this.window, windowTitle() + " - Clone");
			cloneWindow.getContentPane().add(buildPane(), "Center");
			cloneWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			cloneWindow.setLocation(this.window.getWidth(), 0);
			cloneWindow.setModal(false);
			cloneWindow.setLocation(windowX + this.window.getWidth(), 0);
			cloneWindow.setSize(this.window.getWidth(), this.window.getHeight());
			return cloneWindow;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private Action buildCloneWindowAction() {
		Action action = new AbstractAction("Clone") {
			public void actionPerformed(ActionEvent event) {
				openCloneWindow();
			}
		};
		return action;
	}

	protected JButton buildCloneWindowButton() {
		return new JButton(buildCloneWindowAction());
	}

	protected Component buildControlPanel() {
		
		JPanel controlPanel = new JPanel( new GridLayout( 1, 5, 5, 0));
		controlPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
		controlPanel.add( this.buildResetPropertyButton());
		controlPanel.add( this.buildClearModelButton());
		controlPanel.add( this.buildRestoreModelButton());
		controlPanel.add( this.buildPrintModelButton());
		controlPanel.add( this.buildCloneWindowButton());
		return controlPanel;
	}

	private WorkbenchContext buildFrameworkWorkbenchContext()
	{
		ResourceRepository frameworkRepository = new DefaultResourceRepository(FrameworkResourceBundle.class, new FrameworkIconResourceFileNameMap());
		return new SimpleWorkbenchContext(new ApplicationTest(), null, frameworkRepository, buildNodeManager(), buildHelpManager(), this.window, null, null, null);
	}

	protected HelpManager buildHelpManager() {
		try {
			return (HelpManager) ClassTools.newInstance("org.eclipse.persistence.tools.workbench.framework.help.NullHelpManager");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected abstract PropertyValueModel buildNodeHolder( ApplicationNode projectNode);

	private NodeManager buildNodeManager()
	{
		return new NodeManager()
		{
			public void addProjectNode(ApplicationNode node)
			{
			}

			public TreeNodeValueModel getRootNode()
			{
				return null;
			}

			public NavigatorSelectionModel getTreeSelectionModel()
			{
				return new NavigatorSelectionModel()
				{

					public void addTreeSelectionListener(TreeSelectionListener listener)
					{
					}
					public ApplicationNode[] getSelectedNodes()
					{
						return null;
					}

					public ApplicationNode[] getSelectedProjectNodes()
					{
						return null;
					}

					public TreePath[] getSelectionPaths()
					{
						return null;
					}

					public void popAndRestoreExpansionState()
					{
					}

					public void popAndRestoreExpansionState(ApplicationNode oldNode, ApplicationNode morphedNode) {

					}
					public void pushExpansionState()
					{
					}

					public void removeTreeSelectionListener(TreeSelectionListener listener)
					{
					}

					public void setSelectedNode(ApplicationNode node)
					{
					}
					public void expandNode(ApplicationNode node)
					{
					}

					public void setSelectionPaths(TreePath[] pPaths)
					{
					}
				};
			}

			public ApplicationNode[] projectNodesFor(Plugin plugin)
			{
				return new ApplicationNode[] {(ApplicationNode) getNodeHolder().getValue()};
			}
			
			public boolean save(ApplicationNode node, WorkbenchContext workbenchContext) {
				// TODO Auto-generated method stub
				return false;
			}

			public void removeProjectNode(ApplicationNode node)
			{
			}
		};
	}

	protected final JComponent buildPage(Class className, PropertyValueModel subjectHolder) throws Exception
	{
		return buildPage(className.getName(), subjectHolder);
	}

	protected final JComponent buildPage(String className, PropertyValueModel subjectHolder) throws Exception
	{
		Class page = Class.forName(className);
		Constructor constructor = page.getDeclaredConstructor(new Class[] { PropertyValueModel.class });
		constructor.setAccessible(true);
		return (JComponent) constructor.newInstance(new Object[] { subjectHolder });
	}

	protected final JComponent buildPane(Class className, PropertyValueModel subjectHolder) throws Exception
	{
		return buildPane(className.getName(), subjectHolder);
	}

	protected final JComponent buildPane(String className, PropertyValueModel subjectHolder) throws Exception
	{
		Class page = Class.forName(className);
		Constructor constructor = page.getDeclaredConstructor(new Class[] { PropertyValueModel.class, WorkbenchContextHolder.class });
		constructor.setAccessible(true);
		return (JComponent) constructor.newInstance(new Object[] { subjectHolder, new DefaultWorkbenchContextHolder(buildWorkbenchContext()) });
	}

	private Action buildPrintModelAction() {
		Action action = new AbstractAction( "print model") {
			public void actionPerformed( ActionEvent event) {
				SCAbstractPanelTest.this.printModel();
			}
		};
		action.setEnabled( true);
		return action;
	}

	protected JButton buildPrintModelButton() {
		return new JButton( this.buildPrintModelAction());
	}

	private ProjectNode buildProjectNode(TopLinkSessionsAdapter topLinkSessions,
													 SCPlugin scPlugin,
													 ApplicationContext context)
	{
		return new ProjectNode
		(
			topLinkSessions,
			context.getNodeManager().getRootNode(),
			scPlugin,
			context
		);
	}

	private Action buildResetPropertyAction() {
		Action action = new AbstractAction( "reset property") {
			public void actionPerformed (ActionEvent event) {
				SCAbstractPanelTest.this.resetProperty();
			}
		};
		action.setEnabled( true);
		return action;
	}

	protected JButton buildResetPropertyButton() {
		return new JButton(this.buildResetPropertyAction());
	}

	private Action buildRestoreModelAction() {
		Action action = new AbstractAction("restore model") {
			public void actionPerformed(ActionEvent event) {
				SCAbstractPanelTest.this.restoreModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	protected JButton buildRestoreModelButton() {
		return new JButton( this.buildRestoreModelAction());
	}

	private WorkbenchContext buildSCContext(WorkbenchContext context) {
		return context.buildExpandedApplicationContextWorkbenchContext(wrap(context.getApplicationContext()));
	}

	private ApplicationContext wrap( ApplicationContext context) {
		return new RedirectedPreferencesApplicationContext(context, "sc");
	}

	protected abstract SCAdapter buildSelection();

	protected PropertyValueModel buildSelectionHolder( SCAdapter selection) {
		
		return new SimplePropertyValueModel( selection);
	}

	private TreeModelListener buildTreeModelListener()
	{
		return new TreeModelListener()
		{
			public void treeNodesChanged(TreeModelEvent e)
			{
			}

			public void treeNodesInserted(TreeModelEvent e)
			{
			}

			public void treeNodesRemoved(TreeModelEvent e)
			{
			}

			public void treeStructureChanged(TreeModelEvent e)
			{
			}
		};
	}

	private WorkbenchContext buildUIToolsContext(WorkbenchContext context)
	{
		return context.buildExpandedResourceRepositoryContext(UIToolsResourceBundle.class, new UIToolsIconResourceFileNameMap());
	}

	protected abstract void clearModel();

	protected void execute(String[] arguments) throws Exception
	{
		super.execute(arguments);
		openCloneWindow();
	}

	protected PropertyValueModel getNodeHolder() {
		return nodeHolder;
	}

	protected ProjectNode getProjectNode()
	{
		return projectNode;
	}

	protected ResourceRepository getResourceRepository()
	{
		return getProjectNode().getApplicationContext().getResourceRepository();
	}

	protected Class getResourceRepositoryClass()
	{
		return SCPluginResourceBundle.class;
	}

	protected SCAdapter getSelection() {
		return (SCAdapter) selectionHolder.getValue();
	}

	protected PropertyValueModel getSelectionHolder() {
		return selectionHolder;
	}

	protected TopLinkSessionsAdapter getTopLinkSessions()
	{
		return (TopLinkSessionsAdapter) getProjectNode().getValue();
	}

	protected void initialize() {

		try
		{
			InetAddress address = InetAddress.getLocalHost();

			if (TRAN_COMPUTER_IP_ADDRESS.equals(address.getHostAddress()))
				windowX = 1600;
		}
		catch (UnknownHostException e)
		{
		}

		windowW = 500;
		windowH = 400;
	}

	protected void initializeWindow() throws Exception
	{
		super.initializeWindow();

		if (this.parentTest == null)
		{
			this.window.getContentPane().add(buildControlPanel(), "South");
			this.window.pack();
			this.window.setLocation(windowX, 0);
			this.window.setSize(Math.max(windowW, this.window.getWidth()), Math.max(windowH, this.window.getHeight()));
		}
	}

	protected void openCloneWindow()
	{
		JDialog dialog = buildCloneWindow();
		dialog.setVisible(true);
	}

	protected abstract void printModel();

	protected abstract void resetProperty();

	protected abstract void restoreModel();

	protected final ApplicationNode retrieveNode(ApplicationNode parentNode,
																SCAdapter childModel)
	{
		for (int index = parentNode.childrenSize(); --index >= 0; )
		{
			ApplicationNode childNode = (ApplicationNode) parentNode.getChild(index);

			if (childNode.getValue() == childModel)
				return childNode;
		}
		
		throw new NullPointerException("Could not find the child node for " + childModel + " from " + parentNode);
	}

	protected void setSelectionHolder(PropertyValueModel selectionHolder)
	{
		this.selectionHolder = selectionHolder;
	}

	protected void setUp() throws Exception
	{
		initialize();
		buildWindow();

		if (this.parentTest == null)
		{
			File scXmlLocation = FileTools.resourceFile("/SessionsXMLTestModel/XMLSchemaSessions.xml", getClass());

			SCPlugin scPlugin = (SCPlugin) SCPluginFactory.instance().createPlugin(buildWorkbenchContext().getApplicationContext());
			projectNode = buildProjectNode(AllSCTests.loadSessions(scXmlLocation), scPlugin, buildWorkbenchContext().getApplicationContext());
//			projectNode = (ProjectNode) scPlugin.open(scXmlLocation, buildWorkbenchContext());
		}
		else
		{
			SCAbstractPanelTest test = (SCAbstractPanelTest) this.parentTest;
			projectNode = test.projectNode;
		}

		// Create the proper tree data so that the session nodes will be available
		TreeModelAdapter treeModel = new TreeModelAdapter(projectNode);
		treeModel.addTreeModelListener(buildTreeModelListener());

		nodeHolder = buildNodeHolder(projectNode);
		selectionHolder = buildSelectionHolder(buildSelection());

		super.setUp();
	}

	/**
	 * Nullified everything that was initialized.
	 *
	 * @throws Exception
	 */
	protected void tearDown() throws Exception
	{
		selectionHolder.setValue(null);
		nodeHolder.setValue(null);

		nodeHolder = null;
		selectionHolder = null;
		windowX = windowW = windowH = 0;

		super.tearDown();
	}

	private class ApplicationTest implements Application
	{
		public String  getBuildNumber()      { return null; }
		public String getReleaseDesignation() { return null; }
		public String  getFullProductName()  { return null; }
		public String  getProductName()  { return null; }
		public String getFullProductNameAndVersionNumber() { return null; }
		public String  getShortProductName() { return null; }
		public String  getVersionNumber()    { return null; }
		public boolean isDevelopmentMode()   { return true; }
		public boolean isFirstExecution()   { return false; }
	}
}
