/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

// Mapping Workbench

/**
 * This page shows the primary project.
 * <p>
 * Here the layout:
 * <pre>
 * ________________________________________________________
 * |                                                      |
 * |                  _______________________ ___________ |
 * | Primary Project: | I                   | | Edit... | |
 * |                  ----------------------- ----------- |
 * |                                                      |
 * | x Use Multiple Projects                              |
 * |                                                      |
 * --------------------------------------------------------</pre>
 *
 * @see DatabaseSessionAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
abstract class AbstractSessionProjectlPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>AbstractSessionProjectlPane</code>.
	 *
	 * @param nodeHolder The holder of {@link DatabaseSessionAdapter}
	 * @param context The context to be used by this pane
	 */
	AbstractSessionProjectlPane(ValueModel subjectHolder,
										 WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates the Edit button used to edit the primary project.
	 *
	 * @return A new <code>JButton</code>, cannot be <code>null</code>
	 */
	protected abstract JButton buildEditButton();

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Primary Project value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildPrimaryProjectDocumentAdapter()
	{
		return new DocumentAdapter(buildPrimaryProjectNameHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Primary Project (Class or XML) property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	protected abstract PropertyValueModel buildPrimaryProjectNameHolder();

	/**
	 * Creates a new <code>JTextField</code> that will display the primary
	 * project name.
	 *
	 * @param document The document to be used by the text field
	 * @return A new <code>JTextField</code>
	 */
	protected JTextField buildPrimaryProjectTextField(Document document)
	{
		JTextField textField = SwingComponentFactory.buildTextField(document);
		textField.setColumns(1);
		return textField;
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildUseMultipleProjectsCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildUseMultipleProjectsHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Use Multiple Projects property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildUseMultipleProjectsHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), DatabaseSessionAdapter.USE_ADDITIONAL_PROJECTS_COLLECTION)
		{
			protected Object getValueFromSubject()
			{
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject;
				return Boolean.valueOf(session.usesAdditionalProjects());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) subject;
				session.setUseAdditionalProjects(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Primary Project widgets
		JComponent pane = buildLabeledComponent
		(
			"SESSION_PROJECT_PRIMARY_PROJECT_FIELD",
			buildPrimaryProjectTextField(buildPrimaryProjectDocumentAdapter()),
			buildEditButton()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(pane, constraints);

		// Use Multiple Projects check box
		JCheckBox useMultipleProjectsCheckBox = buildCheckBox
		(
			"SESSION_PROJECT_USE_MULTIPLE_PROJECTS_BUTTON",
			buildUseMultipleProjectsCheckBoxAdapter()
		);
		useMultipleProjectsCheckBox.setMargin(new Insets(0, 0, 0, 0));

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		add(useMultipleProjectsCheckBox, constraints);
	}
}
