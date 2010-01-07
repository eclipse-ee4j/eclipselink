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
package org.eclipse.persistence.tools.workbench.scplugin.ui.login;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.EISLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

// Mapping Workbench

/**
 * ...
 * <p>
 * Here the layout of this pane:
 * <pre>
 * ______________________________________________________________________
 * |                                 ____________________ _____________ |
 * | Connection Specification Class: | I                | | Browse... | |
 * |                                 ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ ¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * |                                 ____________________               |
 * | Connection Factory URL:         | I                |               |
 * |                                 ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯               |
 * |                                 ____________________               |
 * | Username (Optional):            | I                |               |
 * |                                 ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯               |
 * |                                 ____________________               |
 * | Password (Optional):            | I                |               |
 * |                                 ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯               |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * Known containers of this pane:<br>
 * - {@link EisPoolLoginPropertiesPage}
 *
 * @see EISLoginAdapter
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public class EisLoginPane extends AbstractLoginPane
{
	/**
	 * Creates a new <code>EisLoginPane</code>.
	 *
	 * @param subjectHolder The holder of {@link EISLoginAdapter}
	 * @param context The plug-in context to be used, such as <code>ResourceRepository</code>
	 */
	public EisLoginPane(PropertyValueModel subjectHolder,
							  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Connection Factory URL value in the model
	 * and vice versa.
	 * 
	 * @return A new <code>Document</code>
	 */
	private Document buildConnectionFactoryURLDocumentAdapter()
	{
		return new DocumentAdapter(buildConnectionFactoryURLHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Connection Factory URL property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildConnectionFactoryURLHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), EISLoginAdapter.CONNECTION_FACTORY_URL_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				EISLoginAdapter login = (EISLoginAdapter) subject;
				return login.getConnectionFactoryURL();
			}

			protected void setValueOnSubject(Object value)
			{
				EISLoginAdapter login = (EISLoginAdapter) subject;
				login.setConnectionFactoryURL((String) value);
			}
		};
	}

	/**
	 * Creates a Browse button that will take care to show the class chooser.
	 *
	 * @return A new browse button
	 */
	private JButton buildConnectionSpecClassBrowseButton()
	{
		return ClassChooserTools.buildBrowseButton
		(
			getWorkbenchContextHolder(),
			"CONNECTION_EIS_DRIVER_CLASS_BROWSE_BUTTON",
			buildClassRepositoryHolder(),
			buildConnectionSpecClassHolder()
		);
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Connection Spec Class Name value in the model
	 * and vice versa.
	 * 
	 * @return A new <code>Document</code>
	 */
	private Document buildConnectionSpecClassDocumentAdapter()
	{
		return new DocumentAdapter(buildConnectionSpecClassHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Connection Spec Class Name property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildConnectionSpecClassHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), EISLoginAdapter.CONNECTION_SPEC_CLASS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				EISLoginAdapter login = (EISLoginAdapter) subject;
				return login.getConnectionSpecClassName();
			}

			protected void setValueOnSubject(Object value)
			{
				EISLoginAdapter login = (EISLoginAdapter) subject;
				login.setConnectionSpecClassName((String) value);
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Connection Spec Class label
		JComponent connectionSpecClassWidgets = buildLabeledTextField
		(
			"CONNECTION_EIS_CONNECTION_SPEC_CLASS_NAME_FIELD",
			buildConnectionSpecClassDocumentAdapter(),
			buildConnectionSpecClassBrowseButton()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		add(connectionSpecClassWidgets, constraints);
		helpManager().addTopicID(connectionSpecClassWidgets, "session.login.eis.connectionSpec");

		// Connection Factory URL widgets
		Component connectionFactoryURLWidgets = buildLabeledTextField
		(
			"CONNECTION_EIS_CONNECTION_FACTORY_URL_FIELD",
			buildConnectionFactoryURLDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(connectionFactoryURLWidgets, constraints);
		helpManager().addTopicID(connectionFactoryURLWidgets, "session.login.eis.connectionSpec");

		// Create the Save Password check box
		JCheckBox saveUsernameCheckBox = buildSaveUsernameCheckBox();

		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(2, 0, 0, 0);

		add(saveUsernameCheckBox, constraints);
		
		// Username widgets
		Component usernameWidgets = buildUserNameWidgets();

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 10, 0, 0);

		add(usernameWidgets, constraints);
		helpManager().addTopicID(usernameWidgets, "session.login.userName");

		new ComponentEnabler(buildSaveUsernameHolder(), usernameWidgets);

		// Create the Save Password check box
		JCheckBox savePasswordCheckBox = buildSavePasswordCheckBox();

		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(2, 0, 0, 0);

		add(savePasswordCheckBox, constraints);

		// Password widgets
		Component passwordWidgets = buildPasswordWidgets();

		constraints.gridx       = 0;
		constraints.gridy       = 5;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 10, 0, 0);

		add(passwordWidgets, constraints);
		helpManager().addTopicID(passwordWidgets, "session.login.password");
				
		new ComponentEnabler(buildSavePasswordHolder(), passwordWidgets);

		helpManager().addTopicID(savePasswordCheckBox, "session.login.savePassword");
	}
}
