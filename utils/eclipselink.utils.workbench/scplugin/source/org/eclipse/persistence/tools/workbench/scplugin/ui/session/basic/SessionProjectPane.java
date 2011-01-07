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

// JDK
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;

// Mapping Workbench

/**
 * @version 10.1.3
 * @author Pascal Filion
 */
final class SessionProjectPane extends AbstractSessionProjectlPane
{
	/**
	 * Creates a new <code>SessionProjectPane</code>.
	 *
	 * @param subjectHolder The holder of {@link DatabaseSessionAdapter}
	 * @param context The context to be used by this pane
	 */
	SessionProjectPane(ValueModel subjectHolder,
							 WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates a new <code>ActionListener</code> that edits the primary project.
	 *
	 * @return A new <code>ActionListener</code>
	 */
	private ActionListener buildEditAction()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				edit();
			}
		};
	}

	/**
	 * Creates a new <code>JButton</code> that edits the primary project.
	 *
	 * @return A new <code>JButton</code>
	 */
	protected JButton buildEditButton()
	{
		JButton button = buildButton("SESSION_PROJECT_EDIT_BUTTON");
		button.addActionListener(buildEditAction());
		return button;
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Primary Project (Class) property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	protected PropertyValueModel buildPrimaryProjectNameHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), DatabaseSessionAdapter.PRIMARY_PROJECT_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject;
				return session.getPrimaryProjectName();
			}
		};
	}

	/**
	 * Creates a new <code>JTextField</code> that uses the given <code>Document</code>.
	 *
	 * @param document The <code>Document</code> to be used by the newly created
	 * text field
	 * @return A new non-editable <code>JTextField</code>
	 */
	protected JTextField buildPrimaryProjectTextField(Document document)
	{
		JTextField textField = super.buildPrimaryProjectTextField(document);
		textField.setEditable(false);
		return textField;
	}

	/**
	 * Edits the primary project.
	 */
	private void edit()
	{
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject();
		ProjectType projectType = new ProjectType(session);

		ProjectTypeEditDialog dialog = new ProjectTypeEditDialog
		(
			getWorkbenchContext(),
			resourceRepository().getString("PROJECT_TYPE_EDIT_DIALOG_TITLE_EDIT"),
			session,
			projectType, 
			"dialog.session.projectType"
		);

		dialog.setVisible(true);
	}

	/**
	 * This <code>ProjectType</code> applies the information from
	 * <code>ProjectTypeEditDialog</code> to the <code>DatabaseSessionAdapter</code>.
	 */
	private static class ProjectType extends AbstractModel
												implements ProjectTypeEditDialog.ProjectTypeUpdater
	{
		private DatabaseSessionAdapter databaseSession;
		private String projectName;
		private boolean projectTypeXml;

		public static final String PROJECT_CLASS_PROPERTY = "ProjectClass";
		public static final String PROJECT_TYPE_XML_PROPERTY = "projectTypeXml";
		public static final String PROJECT_XML_PROPERTY = "ProjectXml";

		public ProjectType(DatabaseSessionAdapter databaseSession)
		{
			super();
			initialize(databaseSession);
		}

		public void apply(boolean projectTypeXml, String projectName)
		{
			if (projectTypeXml)
			{
		   	this.databaseSession.addPrimaryProjectXmlNamed(projectName);
			}
			else
			{
				this.databaseSession.addPrimaryProjectClassNamed(projectName);
			}
		}

		public String getProjectName()
		{
			return this.projectName;
		}

		private void initialize(DatabaseSessionAdapter databaseSession)
		{
			this.databaseSession = databaseSession;
			this.projectTypeXml = databaseSession.isPrimaryProjectXml();
			this.projectName = databaseSession.getPrimaryProjectName();
		}

		public boolean isProjectXml()
		{
			return this.projectTypeXml;
		}
	}
}
