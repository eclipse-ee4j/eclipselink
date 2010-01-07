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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.ComponentAligner;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

// Mapping Workbench

/**
 * @version 10.1.3
 * @author Pascal Filion
 */
final class ProjectTypeEditDialog extends AbstractDialog
{
	/**
	 * The <code>DatabaseSessionAdapter</code> where a project is either edited
	 * or added.
	 */
	private DatabaseSessionAdapter databaseSession;

	/**
	 * Keeps a reference in order to set the initial focus.
	 */
	private JComponent projectClassWidgets;

	/**
	 * 
	 */
	private ProjectTypeEditor projectType;
	
	private String helpTopicId;

	/**
	 * Keeps a reference in order to set the initial focus.
	 */
	private JComponent projectXmlWidgets;

	/**
	 * Creates a new <code>ProjectTypeEditDialog</code>.
	 *
	 * @param context The context to be used by this pane
	 * @param title
	 * @param databaseSession
	 * @param projectType
	 */
	ProjectTypeEditDialog(WorkbenchContext context,
								 String title,
								 DatabaseSessionAdapter databaseSession,
								 ProjectTypeUpdater projectType, String helpTopicId)
	{
		super(context, title);
		initialize(databaseSession, projectType, helpTopicId);
	}

	/**
	 * Shows the file chooser in order to choose an XML file.
	 */
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
			projectType.setProjectXml(selectedFile.getAbsolutePath());
		}
	}

	/**
	 * Creates the <code>ActionListener</code> responsible to show the class
	 * chooser dialog in order to add an additional project class.
	 *
	 * @return A new <code>ActionListener</code>
	 */
	private ActionListener buildClassBrowseAction()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ClassChooserTools.promptForType
				(
					getWorkbenchContext(),
					databaseSession.getClassRepository(),
					buildProjectClassHolder()
				);
			}
		};
	}

	private void buildClassComponentEnabler(JComponent component)
	{
		new ComponentEnabler(buildProjectTypeClassHolder(), component.getComponents());
	}

	/**
	 * Creates a container that has a label and the given chooser.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param component The component that is labeled
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @return A new component containing a label and the chooser
	 */
	private JComponent buildLabeledComponent(String key,
														  JComponent component,
														  JComponent rightComponent,
														  ComponentAligner leftAligner,
														  ComponentAligner rightAligner)
	{
		Pane pane = new Pane(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// Left component
		JLabel label = SwingComponentFactory.buildLabel(key, resourceRepository());
		pane.add(label, constraints);
		leftAligner.add(label);

		// Center component
		constraints.weightx = 1;
		constraints.fill    = GridBagConstraints.HORIZONTAL;
		constraints.insets  = new Insets(0, 5, 0, 0);

		pane.add(component, constraints);
		component.setName(key);
		label.setLabelFor(component);

		// Right component
		if (rightComponent != null)
		{
			constraints.weightx = 0;
			constraints.fill    = GridBagConstraints.NONE;
	
			pane.add(rightComponent, constraints);
			rightComponent.setName(key);
			rightAligner.add(rightComponent);
		}

		return pane;
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed
	 * @return A new component containing a label and a text field
	 */
	private JComponent buildLabeledTextField(String key,
														  Document document,
														  JComponent rightComponent,
														  ComponentAligner leftAligner,
														  ComponentAligner rightAligner)
	{
		JTextField textField = new JTextField(document, null, 40);
		return buildLabeledComponent(key, textField, rightComponent, leftAligner, rightAligner);
	}

	/**
	 * Creates the widgets and layout of the main pane.
	 *
	 * @return The container with all its widgets
	 */
	protected Component buildMainPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		ComponentAligner leftAligner = new ComponentAligner();
		ComponentAligner rightAligner = new ComponentAligner();

		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(SwingComponentFactory.buildStandardEmptyBorder());

		// XML Project Type widgets
		JRadioButton xmlFileButton = SwingComponentFactory.buildRadioButton
		(
			"PROJECT_TYPE_EDIT_DIALOG_XML_RADIO_BUTTON",
			buildProjectTypeButtonModel(Boolean.TRUE),
			resourceRepository()
		);

		JButton xmlBrowseButton = SwingComponentFactory.buildButton("PROJECT_TYPE_EDIT_DIALOG_XML_BROWSE_BUTTON", resourceRepository());
		xmlBrowseButton.addActionListener(buildXMLBrowseAction());

		projectXmlWidgets = buildLabeledTextField
		(
			"PROJECT_TYPE_EDIT_DIALOG_XML_FIELD",
			buildProjectXmlDocumentAdapter(),
			xmlBrowseButton,
			leftAligner,
			rightAligner
		);

		GroupBox groupBox = new GroupBox(xmlFileButton, projectXmlWidgets);
		buildXmlFileComponentEnabler(projectXmlWidgets);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		container.add(groupBox, constraints);

		// Class Project Type widgets
		JRadioButton classTypeButton = SwingComponentFactory.buildRadioButton
		(
			"PROJECT_TYPE_EDIT_DIALOG_CLASS_RADIO_BUTTON",
			buildProjectTypeButtonModel(Boolean.FALSE),
			resourceRepository()
		);

		JButton classBrowseButton = SwingComponentFactory.buildButton("PROJECT_TYPE_EDIT_DIALOG_CLASS_BROWSE_BUTTON", resourceRepository());
		classBrowseButton.addActionListener(buildClassBrowseAction());

		projectClassWidgets = buildLabeledTextField
		(
			"PROJECT_TYPE_EDIT_DIALOG_CLASS_FIELD",
			buildProjectClassDocumentAdapter(),
			classBrowseButton,
			leftAligner,
			rightAligner
		);

		groupBox = new GroupBox(classTypeButton, projectClassWidgets);
		buildClassComponentEnabler(projectClassWidgets);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		container.add(groupBox, constraints);

		return container;
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the  Project value in the model and vice
	 * versa.
	 *
	 * @return A new <code>Document</code>
	 */
	private Document buildProjectClassDocumentAdapter()
	{
		return new DocumentAdapter(buildProjectClassHolder(), RegexpDocument.buildDocument(RegexpDocument.RE_FULLY_QUALIFIED_CLASS_NAME));
	}

	private PropertyValueModel buildProjectClassHolder()
	{
		return new PropertyAspectAdapter(ProjectTypeEditor.PROJECT_CLASS_PROPERTY, projectType)
		{
			protected Object getValueFromSubject()
			{
				ProjectTypeEditor projectType = (ProjectTypeEditor) subject;
				return projectType.getProjectClass();
			}

			protected void setValueOnSubject(Object value)
			{
				ProjectTypeEditor projectType = (ProjectTypeEditor) subject;
				projectType.setProjectClass((String) value);
			}
		};
	}

	private ButtonModel buildProjectTypeButtonModel(Object buttonValue)
	{
		return new RadioButtonModelAdapter(buildProjectTypeHolder(),
													  buttonValue);
	}

	private PropertyValueModel buildProjectTypeClassHolder()
	{
		return new TransformationPropertyValueModel(buildProjectTypeHolder())
		{
			protected Object transform(Object value)
			{
				return Boolean.valueOf(! Boolean.TRUE.equals(value));
			}
		};
	}

	private PropertyValueModel buildProjectTypeHolder()
	{
		return new PropertyAspectAdapter(ProjectTypeEditor.PROJECT_TYPE_XML_PROPERTY, projectType)
		{
			protected Object getValueFromSubject()
			{
				ProjectTypeEditor projectType = (ProjectTypeEditor) subject;
				return Boolean.valueOf(projectType.isProjectTypeXml());
			}

			protected void setValueOnSubject(Object value)
			{
				ProjectTypeEditor projectType = (ProjectTypeEditor) subject;
				projectType.setProjectTypeXml(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the  Project value in the model and vice
	 * versa.
	 *
	 * @return A new <code>Document</code>
	 */
	private Document buildProjectXmlDocumentAdapter()
	{
		return new DocumentAdapter(buildProjectXmlHolder());
	}

	private PropertyValueModel buildProjectXmlHolder()
	{
		return new PropertyAspectAdapter(ProjectTypeEditor.PROJECT_XML_PROPERTY, projectType)
		{
			protected Object getValueFromSubject()
			{
				ProjectTypeEditor projectType = (ProjectTypeEditor) subject;
				return projectType.getProjectXml();
			}

			protected void setValueOnSubject(Object value)
			{
				ProjectTypeEditor projectType = (ProjectTypeEditor) subject;
				projectType.setProjectXml((String) value);
			}
		};
	}

	/**
	 * Creates the <code>ActionListener</code> responsible to show the file
	 * chooser dialog in order to add an additional project XML.
	 *
	 * @return A new <code>ActionListener</code>
	 */
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

	/**
	 * Creates
	 *
	 * @param component
	 */
	private void buildXmlFileComponentEnabler(JComponent component)
	{
		new ComponentEnabler(buildProjectTypeHolder(), component.getComponents());
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	protected String helpTopicId()
	{
		return helpTopicId;
	}

	/**
	 * Returns the component that should receive the initial focus. Depending on
	 * the selected project type, the corresponding text field will be returned.
	 *
	 * @return Either the text field of the XML or the Class widgets
	 */
	protected Component initialFocusComponent()
	{
		if (databaseSession.isPrimaryProjectXml())
			return projectXmlWidgets.getComponent(1);

		return projectClassWidgets.getComponent(1);
	}

	/**
	 * Initializes this <code>ProjectTypeEditDialog</code>.
	 *
	 * @param databaseSession The session to be edited for its  project
	 * @param projectType
	 */
	private void initialize(DatabaseSessionAdapter databaseSession,
									ProjectTypeUpdater updater, String helpTopicId)
	{
		this.databaseSession = databaseSession;
		this.helpTopicId = helpTopicId;
		projectType = new ProjectTypeEditor(updater);
	}

	/**
	 * 
	 */
	public void okConfirmed()
	{
		super.okConfirmed();
		projectType.apply();
	}

	/**
	 * Retrieves the last saved location from the preferences if one exists
	 * otherwise return the user home directory.
	 *
	 * @return The location where the file chooser will be at
	 */
	private File retrieveLastDirectory()
	{
		TopLinkSessionsAdapter topLinkSessions = (TopLinkSessionsAdapter) databaseSession.getParent();
		File saveDirectory = topLinkSessions.getSaveDirectory();

		// This happens when the sessions.xml is an untitled file
		if (saveDirectory == null)
			saveDirectory = FileTools.userHomeDirectory();

		return new File(preferences().get("location", saveDirectory.getPath()));
	}

	/**
	 * 
	 */
	private static class ProjectTypeEditor extends AbstractModel
	{
		private String projectClass;
		private boolean projectTypeXml;
		private String projectXml;
		private ProjectTypeUpdater updater;

		public static final String PROJECT_CLASS_PROPERTY = "ProjectClass";
		public static final String PROJECT_TYPE_XML_PROPERTY = "projectTypeXml";
		public static final String PROJECT_XML_PROPERTY = "ProjectXml";

		public ProjectTypeEditor(ProjectTypeUpdater updater)
		{
			super();
			initialize(updater);
		}

		public void apply()
		{
			updater.apply(projectTypeXml, projectTypeXml ? projectXml : projectClass);
		}

		public String getProjectClass()
		{
			return projectClass;
		}

		public String getProjectXml()
		{
			return projectXml;
		}

		private void initialize(ProjectTypeUpdater updater)
		{
			this.updater = updater;
			setProjectTypeXml(updater.isProjectXml());

			if (projectTypeXml)
			{
				setProjectXml(updater.getProjectName());
			}
			else
			{
				setProjectClass(updater.getProjectName());
			}
		}

		public boolean isProjectTypeXml()
		{
			return projectTypeXml;
		}

		public void setProjectClass(String projectClass)
		{
			String oldProjectClass = getProjectClass();
			this.projectClass = projectClass;
			firePropertyChanged(PROJECT_CLASS_PROPERTY, oldProjectClass, projectClass);
		}

		public void setProjectTypeXml(boolean projectTypeXml)
		{
			boolean oldProjectTypeXml = isProjectTypeXml();
			this.projectTypeXml = projectTypeXml;
			firePropertyChanged(PROJECT_TYPE_XML_PROPERTY, oldProjectTypeXml, projectTypeXml);
		}

		public void setProjectXml(String projectXml)
		{
			String oldProjectXml = getProjectXml();
			this.projectXml = projectXml;
			firePropertyChanged(PROJECT_XML_PROPERTY, oldProjectXml, projectXml);
		}
	}

	public interface ProjectTypeUpdater
	{
		public void apply(boolean projectTypeXml, String projectName);
		public String getProjectName();
		public boolean isProjectXml();
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
