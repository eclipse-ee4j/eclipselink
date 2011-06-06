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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

// Mapping Workbench
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ProjectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This pane shows the additional projects contained by a
 * {@link DatabaseSessionAdapter}. The project general pane shows only one
 * entry which represents the primary project, this pane has to be activated
 * by the check box located in the general pane.
 * <p>
 * Here the layout of this pane:
 * <pre>
 * _______________________________________________
 * | ________________________________ __________ |
 * | | myPackage.MyClass          |^| | Add... | |
 * | | C:/location/myFile.xml     | | ¯¯¯¯¯¯¯¯¯¯ |
 * | | ...                        ||| __________ |
 * | |                            ||| | Remove | |
 * | |                            | | ¯¯¯¯¯¯¯¯¯¯ |
 * | |                            |v|            |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯            |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @see DatabaseSessionAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class SessionMultipleProjectsPane extends AbstractSessionMultipleProjectsPane
{
	/**
	 * Creates a new <code>SessionMultipleProjectsPane</code>.
	 *
	 * @param subjectHolder The holder of {@link DatabaseSessionAdapter}
	 * @param context The context to be used by this pane
	 */
	SessionMultipleProjectsPane(ValueModel subjectHolder,
										WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Prompts to add an additional project (Class or XML).
	 *
	 * @param selectionModel
	 */
	protected void addProject(ObjectListSelectionModel selectionModel)
	{
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject();
		ProjectType projectType = new ProjectType(session, selectionModel);

		ProjectTypeEditDialog dialog = new ProjectTypeEditDialog
		(
			getWorkbenchContext(),
			resourceRepository().getString("PROJECT_TYPE_EDIT_DIALOG_TITLE_ADD"),
			session,
			projectType,
			"session.project.advanced"
		);

		dialog.setVisible(true);
	}

	/**
	 * 
	 */
	private class ProjectType extends AbstractModel
									  implements ProjectTypeEditDialog.ProjectTypeUpdater
	{
		private DatabaseSessionAdapter databaseSession;
		private String projectName;
		private final ObjectListSelectionModel selectionModel;

		public static final String PROJECT_CLASS_PROPERTY = "projectClass";
		public static final String PROJECT_XML_PROPERTY = "projectXml";
		public static final String PROJECT_TYPE_XML_PROPERTY = "projectTypeXml";

		public ProjectType(DatabaseSessionAdapter databaseSession,
								 ObjectListSelectionModel selectionModel)
		{
			super();
			this.selectionModel = selectionModel;
			this.databaseSession = databaseSession;
		}

		public void apply(boolean projectTypeXml, String projectName)
		{
			if (projectName == null)
			{
				ProjectAdapter project = (ProjectAdapter) this.selectionModel.getSelectedValue();
				this.databaseSession.removeProject(project);
			}
			else if (projectTypeXml)
			{
				// Check to see if the value already exist
				if (CollectionTools.contains(this.databaseSession.additionalProjectNames(), projectName))
				{
					ProjectAdapter project = this.databaseSession.projectNamed(projectName);
					this.selectionModel.setSelectedValue(project);
				}
				else
				{
					this.databaseSession.addProjectXmlNamed(projectName);
				}
			}
			else
			{
				this.databaseSession.addProjectClassNamed(projectName);
			}
		}

		public String getProjectName()
		{
			return this.projectName;
		}

		public boolean isProjectXml()
		{
			return true;
		}
	}
}
