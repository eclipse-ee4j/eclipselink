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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GridLayout;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ProjectAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ProjectXMLAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;

// Mapping Workbench

/**
 * This pane shows the multiple projects contained by a
 * {@link DatabaseSessionAdapter}. The {@link SessionProjectPane} shows only one
 * entry which represents the primary project, this pane has to be activated
 * by the check box located in the project pane.
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
abstract class AbstractSessionMultipleProjectsPane extends AbstractSubjectPanel
{
	/**
	 * The model responsible to keep track of the selected items.
	 */
	private ObjectListSelectionModel selectionModel;

	/**
	 * Creates a new <code>AbstractSessionMultipleProjectsPane</code>.
	 *
	 * @param subjectHolder The holder of {@link DatabaseSessionAdapter}
	 * @param context The context to be used by this pane
	 */
	AbstractSessionMultipleProjectsPane(ValueModel subjectHolder,
													WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Prompts to add an additional project (Class or XML).
	 *
	 * @param selectionModel The selection model used by the list
	 */
	protected abstract void addProject(ObjectListSelectionModel selectionModel);

	/**
	 * Creates a new <code>ActionListener</code> that adds a new project to the
	 * list.
	 *
	 * @return A new <code>ActionListener</code>
	 */
	private ActionListener buildAddAction()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addProject(AbstractSessionMultipleProjectsPane.this.selectionModel);
			}
		};
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing all the items to
	 * be shown in the Additional Projects list.
	 *
	 * @return A new <code>CollectionValueModel</code>
	 */
	private CollectionValueModel buildAddionalProjectsCollectionHolder()
	{
		return new CollectionAspectAdapter(getSubjectHolder(), DatabaseSessionAdapter.ADDITIONAL_PROJECTS_COLLECTION)
		{
			protected Iterator getValueFromSubject()
			{
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject;
				return session.additionalProjects();
			}
		};
	}

	/**
	 * Creates the <code>ListModel</code> containing all the items to be shown in
	 * the list.
	 *
	 * @return A new <code>ListModel</code>
	 */
	private ListModel buildAddionalProjectsListAdapter()
	{
		return new ListModelAdapter
		(
			new SortedListValueModelAdapter
			(
				buildAddionalProjectsCollectionHolder()
			)
		);
	}

	/**
	 * Creates a new <code>ListCellRenderer</code> that will render either a
	 * class name or an xml file.
	 *
	 * @return A new <code>ListCellRenderer</code>
	 */
	private ListCellRenderer buildAddiontalProjectsCellRenderer()
	{
		return new AdaptableListCellRenderer
		(
			new CellRendererAdapter()
		);
	}

	/**
	 * Creates the <code>ActionListener</code> responsible to remove the selected
	 * items from the list.
	 *
	 * @return A new <code>ActionListener</code>
	 */
	private ActionListener buildRemoveAction()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object[] selectedValues = AbstractSessionMultipleProjectsPane.this.selectionModel.getSelectedValues();
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject();

				for (int index = 0; index < selectedValues.length; index++)
				{
					session.removeProject((ProjectAdapter) selectedValues[index]);
				}
			}
		};
	}

	/**
	 * Keeps the enable state of the given button in sync with the list selection.
	 *
	 * @param The button that has its enable state kept in sync based on the new
	 * selection
	 */
	private void buildRemoveButtonEnabler(final JButton removeButton)
	{
		this.selectionModel.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting())
					return;

				removeButton.setEnabled(AbstractSessionMultipleProjectsPane.this.selectionModel.getSelectedValues().length > 0);
			}
		});
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// List label
		JLabel additionalProjectsLabel = buildLabel("SESSION_MULTIPLE_PROJECTS_LIST");

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(additionalProjectsLabel, constraints);

		// Additional Projects list
		JList additionalProjectsList = SwingComponentFactory.buildList(buildAddionalProjectsListAdapter());
		additionalProjectsList.setCellRenderer(buildAddiontalProjectsCellRenderer());

		this.selectionModel = new ObjectListSelectionModel(additionalProjectsList.getModel());
		additionalProjectsList.setSelectionModel(this.selectionModel);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(1, 0, 0, 0);

		add(new JScrollPane(additionalProjectsList), constraints);
		additionalProjectsLabel.setLabelFor(additionalProjectsList);

		// Button panel
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 5));

		constraints.gridx      = 1;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(1, 5, 0, 0);

		add(buttonPanel, constraints);

		// Add button
		JButton addButton = buildButton("SESSION_MULTIPLE_PROJECTS_ADD_BUTTON");
		addButton.addActionListener(buildAddAction());
		buttonPanel.add(addButton);

		// Remove button
		JButton removeButton = buildButton("SESSION_MULTIPLE_PROJECTS_REMOVE_BUTTON");
		removeButton.setEnabled(false);
		removeButton.addActionListener(buildRemoveAction());
		buildRemoveButtonEnabler(removeButton);
		buttonPanel.add(removeButton);
	}

	/**
	 * This <code>LabelDecorator</code> takes care to show on screen the path
	 * based on the system the MW is ran. In the file, the file separator is
	 * always '/' since a sessions.xml created on Windows with '\' as the file
	 * separator will not work on a Unix system.
	 */
	private class CellRendererAdapter extends AbstractCellRendererAdapter
	{
		public Icon buildFileIcon(String path)
		{
			File file = new File(path);
			return resourceRepository().getIcon(file.exists() ? (file.isDirectory() ? "folder" : "file") : "file");
		}

		public Icon buildIcon(Object value)
		{
			if (value instanceof ProjectXMLAdapter)
				return buildFileIcon(((ProjectXMLAdapter) value).getName());

			return resourceRepository().getIcon("class.public");
		}

		public String buildText(Object value)
		{
			String name = ((ProjectAdapter) value).getName();
			return name.replace('/', File.separatorChar);
		}
	}
}
