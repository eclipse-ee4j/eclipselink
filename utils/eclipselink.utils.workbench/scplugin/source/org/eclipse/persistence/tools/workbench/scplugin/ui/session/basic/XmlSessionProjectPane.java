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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

// JDK
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

// Mapping Workbench

/**
 * @version 10.1.3
 * @author Pascal Filion
 */
final class XmlSessionProjectPane extends AbstractSessionProjectlPane
{
	/**
	 * Creates a new <code>SessionProjectPane</code>.
	 *
	 * @param nodeHolder The holder of {@link DatabaseSessionAdapter}
	 * @param context The context to be used by this pane
	 */
	XmlSessionProjectPane(ValueModel subjectHolder,
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

			protected void setValueOnSubject(Object value)
			{
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject;
				session.addPrimaryProjectXmlNamed((String) value);
			}
		};
	}

	/**
	 * Creates the Edit button used to edit the primary project.
	 *
	 * @return A new <code>JButton</code>, cannot be <code>null</code>
	 */
	protected JButton buildEditButton()
	{
		JButton button = buildButton("SESSION_PROJECT_BROWSE_BUTTON");
		button.addActionListener(buildEditAction());
		return button;
	}

	/**
	 * Edits the primary project, which is an XML location.
	 */
	private void edit()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new XmlFileFilter());
		fileChooser.setCurrentDirectory(retrieveLastDirectory());
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int result = fileChooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION)
		{
			DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject();

			File selectedFile = fileChooser.getSelectedFile();
			preferences().put("location", selectedFile.getParent());
			String primaryProjectXml = selectedFile.getAbsolutePath();

			if (primaryProjectXml != null)
				primaryProjectXml = primaryProjectXml.replace('\\', '/');

			session.addPrimaryProjectXmlNamed(primaryProjectXml);
		}
	}

	/**
	 * Retrieves the last saved location from the preferences if one exists
	 * otherwise return the user home directory.
	 *
	 * @return The location where the file chooser will be at
	 */
	private File retrieveLastDirectory()
	{
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject();
		TopLinkSessionsAdapter topLinkSessions = (TopLinkSessionsAdapter) session.getParent();
		File saveDirectory = topLinkSessions.getSaveDirectory();

		// This happens when the sessions.xml is an untitled file
		if (saveDirectory == null)
			saveDirectory = FileTools.userHomeDirectory();

		return new File(preferences().get("location", saveDirectory.getPath()));
	}

	/**
	 * The <code>FileFilter</code> used by the File chooser to restrict the
	 * selection to be XML files only.
	 */
	private class XmlFileFilter extends FileFilter
	{
		public boolean accept(File file)
		{
			return file.isDirectory() || ".xml".equalsIgnoreCase(FileTools.extension(file));
		}

		public String getDescription()
		{
			return resourceRepository().getString("SESSION_PROJECT_ADVANCED_FILE_CHOOSER_DESCRIPTION");
		}
	}
}
