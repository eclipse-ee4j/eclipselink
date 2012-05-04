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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ListIterator;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.ClasspathPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.ClassRepository;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ProjectDisplayableTranslatorAdapter;
import org.eclipse.persistence.tools.workbench.uitools.DisplayableAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * This page shows the location of the edited sessions.xml, along with its
 * classpath, which is stored in {@link org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsPropertiesIO#SESSIONS_XML_PROPERTIES_XML
 * SESSIONS_XML_PROPERTIES_XML} and the list of sessions (Database or Server).
 * <p>
 * Here the layout:
 * <pre>
 * __________________________________________________________
 * |                                                        |
 * |   Project Save Location:                               |
 * |   _______________________________ __________________   |
 * |   | C:\MyDirectory              | |    Change...   |   |
 * |   ------------------------------- ------------------   |
 * | _Classpath____________________________________________ |
 * | | _______________________________ __________________ | |
 * | | |                           |^| | Add Entries... | | |
 * | | |                           | | ------------------ | |
 * | | |                           | | __________________ | |
 * | | |                           ||| |     Remove     | | |
 * | | |                           ||| ------------------ | |
 * | | |                           ||| __________________ | |
 * | | |                           ||| |       Up       | | |
 * | | |                           ||| ------------------ | |
 * | | |                           | | __________________ | |
 * | | |                           | | |      Down      | | |
 * | | |                           |v| ------------------ | |
 * | | -------------------------------                    | |
 * | ------------------------------------------------------ |
 * | _Sessions for 'sessions.xml'__________________________ |
 * | | _______________________________ __________________ | |
 * | | | MySession1 (Database)     |^| | Add Session... | | |
 * | | | MySession2 (Server)       | | ------------------ | |
 * | | |                           | | __________________ | |
 * | | |                           ||| | Add Broker...  | | |
 * | | |                           ||| ------------------ | |
 * | | |                           ||| __________________ | |
 * | | |                           ||| |    Remove...   | | |
 * | | |                           ||| ------------------ | |
 * | | |                           | | __________________ | |
 * | | |                           | | |    Rename...   | | |
 * | | |                           |v| ------------------ | |
 * | | -------------------------------                    | |
 * | ------------------------------------------------------ |
 * ----------------------------------------------------------</pre>
 *
 * @see ClassRepository
 * @see TopLinkSessionsAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class ProjectPropertiesPage extends TitledPropertiesPage
{
	/**
	 * Creates a new <code>ProjectPropertiesPage</code>.
	 *
	 * @param node The user object of this page
	 */
	public ProjectPropertiesPage(WorkbenchContext context)
	{
		super(context);
	}

	/**
	 * Creates a new <code>ActionListener</code> that will trigger a save as on
	 * <code>ProjectNode</code> in order to change the location.
	 *
	 * @return A new <code>ActionListener</code>
	 */
	private ActionListener buildChangeLocationAction()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ProjectNode node = (ProjectNode) getNode();
				node.saveAs(null, getWorkbenchContext());
			}
		};
	}

	/**
	 * Creates a new <code>PropertyValueModel</code> responsible to handle
	 * {@link ClassRepository}.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildClasspathHolder()
	{
		return new TransformationPropertyValueModel(getSelectionHolder())
		{
			protected Object transform(Object value)
			{
				SCAdapter adapter = (SCAdapter) value;
				return (value == null) ? null : adapter.getClassRepository();
			}
		};
	}

	/**
	 * Creates the <code>ListValueModel</code> responsible to handle changes made
	 * to the list of classpath entries.
	 *
	 * @return {@link ClasspathListAdapter}
	 */
	private ListValueModel buildClasspathListAdapter()
	{
		return new ClasspathListAdapter();
	}

	protected TopLinkSessionsAdapter getProject()
	{
		return (TopLinkSessionsAdapter) this.getSelectionHolder().getValue();
	}

	private ClasspathPanel.DefaultClasspathDirectoryHolder buildCurrentDirectoryHolder()
	{
		return new ClasspathPanel.DefaultClasspathDirectoryHolder()
		{
			public File getDefaultClasspathDirectory()
			{
				File projectSaveLocation = getProject().getSaveDirectory();

				if (projectSaveLocation != null)
				{
					return projectSaveLocation;
				}

				String savedLocation = preferences().get(ClasspathPanel.MOST_RECENT_CLASSPATH_DIRECTORY_PREFERENCE, null);

				if (savedLocation != null)
				{
					return new File(savedLocation);
				}

				return FileTools.userHomeDirectory();
			}

			public void setDefaultClasspathDirectory(File defaultClasspathDirectory)
			{
				File projectSaveLocation = getProject().getSaveDirectory();

				// Do not persist the user home
				if (defaultClasspathDirectory.equals(FileTools.userHomeDirectory()))
				{
					defaultClasspathDirectory = null;
				}

				// Do not persist the project save location
				else if ((projectSaveLocation != null) && projectSaveLocation.equals(defaultClasspathDirectory))
				{
					defaultClasspathDirectory = null;
				}

				// Persist the new location or simply clear it
				if (defaultClasspathDirectory != null)
				{
					preferences().put(ClasspathPanel.MOST_RECENT_CLASSPATH_DIRECTORY_PREFERENCE, defaultClasspathDirectory.getPath());
				}
				else
				{
					preferences().remove(ClasspathPanel.MOST_RECENT_CLASSPATH_DIRECTORY_PREFERENCE);
				}
			}
		};
	}

	protected DisplayableAdapter buildDisplayableAdapter()
	{
		return new ProjectDisplayableTranslatorAdapter(resourceRepository());
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the sessions.xml file name and vice versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildLocationDocumentAdapter()
	{
		TransformationPropertyValueModel transformation = new TransformationPropertyValueModel(buildLocationHolder())
		{
			protected Object transform(Object value)
			{
				File file = (File) value;

				if (file == null)
					return null;

				File location = file.getParentFile();

				if (location == null)
					return null;

				return FileTools.canonicalFile(location).getPath();
			}
		};

		return new DocumentAdapter(transformation);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * sessions.xml location property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildLocationHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), TopLinkSessionsAdapter.SAVE_PATH_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				TopLinkSessionsAdapter adapter = (TopLinkSessionsAdapter) subject;
				return adapter.getPath();
			}
		};
	}

	/**
	 * Creates the pane that will contains the widgets of this page.
	 *
	 * @return The fully initialized pane
	 */
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		Insets borderInsets = BorderFactory.createTitledBorder("m").getBorderInsets(this);

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Project Save Location label
		JLabel projectSaveLocationLabel = buildLabel("PROJECT_LOCATION_FIELD");

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, borderInsets.left + 5, 0, 0);

		panel.add(projectSaveLocationLabel, constraints);
		addHelpTopicId(projectSaveLocationLabel, "scproject.location");

		// Project Save Location widgets
		JTextField locationField = new JTextField(buildLocationDocumentAdapter(), null, 1);
		locationField.setEditable(false);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(1, borderInsets.left + 5, 0, 0);

		panel.add(locationField, constraints);
		addHelpTopicId(locationField, "scproject.location");
		projectSaveLocationLabel.setLabelFor(locationField);

		// Project Save Location browse button
		JButton changeLocationButton = buildButton("PROJECT_LOCATION_BROWSE_BUTTON");
		changeLocationButton.addActionListener(buildChangeLocationAction());

		constraints.gridx      = 1;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(1, 5, 0, borderInsets.right + 5);

		panel.add(changeLocationButton, constraints);
		addHelpTopicId(changeLocationButton, "scproject.location");
		addAlignRight(changeLocationButton);

		// Create the classpath panel
		ClasspathPanel classpathPanel = new ClasspathPanel
		(
			getApplicationContext(),
			buildClasspathListAdapter(),
			buildRootFileHolder(),
			true
		);
		classpathPanel.setDefaultClasspathDirectoryHolder(buildCurrentDirectoryHolder());

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0.5;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(classpathPanel, constraints);
		addPaneForAlignment(classpathPanel);
		addHelpTopicId(classpathPanel, "scproject.classpath");

		// Create the Sessions list panel
		SessionsListPane sessionListPane = new SessionsListPane(getSelectionHolder(), getWorkbenchContextHolder());

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0.5;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(sessionListPane, constraints);
		addPaneForAlignment(sessionListPane);
		addHelpTopicId(sessionListPane, "scproject.sessions");

		addHelpTopicId(panel, "scproject");
		return panel;
	}

	private ValueModel buildRootFileHolder()
	{
		return new SimplePropertyValueModel(getSelectionHolder())
		{
			public Object getValue()
			{
				ValueModel projectHolder = (ValueModel) super.getValue();
				TopLinkSessionsAdapter sessions = (TopLinkSessionsAdapter) projectHolder.getValue();

//				if (sessions == null)
					return null;

//				return sessions.getSaveDirectory();
			}
		};
	}

	/**
	 * The adapter responsible to manage the list of classpath entries.
	 *
	 * @see ClassRepository
	 */
	private class ClasspathListAdapter extends ListAspectAdapter
	{
		private ClasspathListAdapter()
		{
			super(buildClasspathHolder(), ClassRepository.CLASSPATH_ENTRIES_LIST);
		}

		public void addItem(int index, Object item)
		{
			ClassRepository classpath = (ClassRepository) subject;
			classpath.addClasspathEntry(index, (String) item);
		}

		public Object getItem(int index)
		{
			ClassRepository classpath = (ClassRepository) subject;
			return classpath.getClasspathEntry(index);
		}

		protected ListIterator getValueFromSubject()
		{
			ClassRepository classpath = (ClassRepository) subject;
			return classpath.classpathEntries();
		}

		public Object removeItem(int index)
		{
			ClassRepository classpath = (ClassRepository) subject;
			return classpath.removeClasspathEntry(index);
		}

		public Object replaceItem(int index, Object item)
		{
			ClassRepository classpath = (ClassRepository) subject;
			return classpath.replaceClasspathEntry(index, (String) item);
		}
	}
}
