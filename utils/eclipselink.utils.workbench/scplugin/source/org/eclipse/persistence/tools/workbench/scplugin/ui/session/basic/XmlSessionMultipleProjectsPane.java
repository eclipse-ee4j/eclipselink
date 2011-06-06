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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ProjectAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

// Mapping Workbench

/**
 * This pane shows the additional projects contained by a
 * {@link DatabaseSessionAdapter}. The project general pane shows only one
 * entry which represents the primary project, this pane has to be activated
 * by the check box located in the general pane.
 * <p>
 * Here the layout of this pane:
 * <pre>
 * _____________________________________________________
 * | ________________________________ ________________ |
 * | | myPackage.MyClass          |^| |    Add...    | |
 * | | C:/location/myFile.xml     | | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * | | ...                        ||| ________________ |
 * | |                            ||| |    Remove    | |
 * | |                            ||| ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * | |                            |||                  |
 * | |                            | |                  |
 * | |                            |v|                  |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯                  |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @see DatabaseSessionAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class XmlSessionMultipleProjectsPane extends AbstractSessionMultipleProjectsPane
{
	/**
	 * Creates a new <code>SessionAdvancedPane</code>.
	 *
	 * @param subjectHolder The holder of {@link DatabaseSessionAdapter}
	 * @param context
	 */
	XmlSessionMultipleProjectsPane(ValueModel subjectHolder,
											WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Prompts to add an additional XML project.
	 *
	 * @param selectionModel The selection model used by the list
	 */
	protected void addProject(ObjectListSelectionModel selectionModel)
	{
		DatabaseSessionAdapter databaseSession = (DatabaseSessionAdapter) subject();
		SimplePropertyValueModel projectXmlHolder = new SimplePropertyValueModel();

		ProjectXmlEditDialog dialog = new ProjectXmlEditDialog(projectXmlHolder);
		dialog.setVisible(true);

		if (dialog.wasCanceled())
			return;

		String projectXml = (String) projectXmlHolder.getValue();
		String temporaryProjectXml = projectXml.replace('\\', '/');

		// Check to see if the value already exist
		if (CollectionTools.contains(databaseSession.additionalProjectNames(), temporaryProjectXml))
		{
			ProjectAdapter project = databaseSession.projectNamed(temporaryProjectXml);
			selectionModel.setSelectedValue(project);
		}
		else
		{
			ProjectAdapter project = databaseSession.addProjectXmlNamed(projectXml);
			selectionModel.setSelectedValue(project);
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
	 * This dialog edits the project XML.
	 */
	private class ProjectXmlEditDialog extends AbstractValidatingDialog
	{
		private final PropertyValueModel projectXmlHolder;
		private JTextField textField;

		ProjectXmlEditDialog(PropertyValueModel projectXmlHolder)
		{
			super(XmlSessionMultipleProjectsPane.this.getWorkbenchContext(),
					XmlSessionMultipleProjectsPane.this.resourceRepository().getString("PROJECT_TYPE_EDIT_DIALOG_TITLE_ADD"));

			this.projectXmlHolder = projectXmlHolder;
			projectXmlHolder.addPropertyChangeListener(PropertyValueModel.VALUE, buildPropertyChangeListener());
		}

		private void addProjectXML()
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new XmlFileFilter());
			fileChooser.setCurrentDirectory(retrieveLastDirectory());
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int result = fileChooser.showOpenDialog(this);

			if (result == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = fileChooser.getSelectedFile();
				preferences().put("location", selectedFile.getParent());
				String projectXml = selectedFile.getAbsolutePath();
				this.projectXmlHolder.setValue(projectXml);
			}
		}

		protected Component buildMainPanel()
		{
			GridBagConstraints constraints = new GridBagConstraints();

			JPanel container = new JPanel(new GridBagLayout());

			JButton xmlBrowseButton = SwingComponentFactory.buildButton("PROJECT_TYPE_EDIT_DIALOG_XML_BROWSE_BUTTON", resourceRepository());
			xmlBrowseButton.addActionListener(buildXMLBrowseAction());

			this.textField = new JTextField(buildProjectXmlDocumentAdapter(), null, 30);

			JComponent ProjectXmlWidgets = buildLabeledComponent
			(
				"PROJECT_TYPE_EDIT_DIALOG_XML_FIELD",
				this.textField,
				xmlBrowseButton
			);

			constraints.gridx      = 0;
			constraints.gridy      = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(0, 0, 0, 0);

			container.add(ProjectXmlWidgets, constraints);

			return container;
		}

		private Document buildProjectXmlDocumentAdapter()
		{
			return new DocumentAdapter(this.projectXmlHolder);
		}

		private PropertyChangeListener buildPropertyChangeListener()
		{
			return new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent e)
				{
					String projectXml = (String) e.getNewValue();
					boolean valid = !StringTools.stringIsEmpty(projectXml);
					getOKAction().setEnabled(valid);

					if (valid)
					{
						clearErrorMessage();
					}
					else
					{
						setErrorMessageKey("PROJECT_TYPE_EDIT_DIALOG_ERROR_MESSAGE");
					}
				}
			};
		}

		private ActionListener buildXMLBrowseAction()
		{
			return new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addProjectXML();
				}
			};
		}

		protected String helpTopicId()
		{
			return "dialog.session.projectType";
		}

		protected Component initialFocusComponent()
		{
			return this.textField;
		}

		protected void prepareToShow()
		{
			super.prepareToShow();
			getOKAction().setEnabled(false);
		}
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
