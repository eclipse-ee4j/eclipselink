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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering;

// JDK
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.UserDefinedTransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

// Mapping Workbench

/**
 * This page shows the information about the {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.UserDefinedTransportManagerAdapter
 * UserDefinedTransportManagerAdapter}.
 * <p>
 * Here the layout:</pre>
 * ____________________________________________________
 * |                  ___________________ ___________ |
 * | Transport Class: | I               | |Browse...| |
 * |                  ------------------- ----------- |
 * ----------------------------------------------------</pre>
 *
 * Known container of this pane:<br>
 * - {@link RemoteCommandManagerPane}
 *
 * @see UserDefinedTransportManagerAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class RCMUserDefinedPane extends AbstractTransportManagerPane
{
	/**
	 * Creates a new <code>RCMUserDefinedPane</code>.
	 *
	 * @param subjectHolder The holder of {@link UserDefinedTransportManagerAdapter}
	 * @param context
	 */
	RCMUserDefinedPane(PropertyValueModel subjectHolder,
							 WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates the <code>Document</code> that keeps the value from the text
	 * field in sync with the Transport Class value in the model and vice versa.
	 *
	 * @return A new <code>Document</code>
	 */
	private Document buildTransportClassDocumentAdapter()
	{
		return new DocumentAdapter(buildTransportClassHolder(),
											new RegexpDocument(RegexpDocument.RE_FULLY_QUALIFIED_CLASS_NAME));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Transport Class property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildTransportClassHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), UserDefinedTransportManagerAdapter.TRANSPORT_CLASS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				UserDefinedTransportManagerAdapter adapter = (UserDefinedTransportManagerAdapter) subject;
				return adapter.getTransportClass();
			}

			protected void setValueOnSubject(Object value)
			{
				UserDefinedTransportManagerAdapter adapter = (UserDefinedTransportManagerAdapter) subject;
				adapter.setTransportClass((String) value);
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to return the
	 * Class Repository.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private ValueModel buildClassRepositoryHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), "")
		{
			protected Object getValueFromSubject()
			{
				SCAdapter adapter = (SCAdapter) subject;
				return adapter.getClassRepository();
			}
		};
	}

	/**
	 * Creates a Browse button that will take care to show the class chooser.
	 *
	 * @return A new <code>JButton</code>
	 */
	private JButton buildUserDefinedBrowseButton()
	{
		return ClassChooserTools.buildBrowseButton
		(
			getWorkbenchContextHolder(),
			"RCM_USER_DEFINED_BROWSE_BUTTON",
			buildClassRepositoryHolder(),
			buildTransportClassHolder()
		);
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Transport Class widgets
		JComponent transportClassWidgets = buildLabeledTextField
		(
			"RCM_USER_DEFINED_TRANSPORT_CLASS_FIELD",
			buildTransportClassDocumentAdapter(),
			buildUserDefinedBrowseButton()
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

		add(transportClassWidgets, constraints);
		addHelpTopicId(transportClassWidgets, "session.clustering.rcm.userDefined.transportClass");

		// Remove Connection On Error check box
		JCheckBox removeConnectionOnErrorCheckBox = buildRemoveConnectionOnError();

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(removeConnectionOnErrorCheckBox, constraints);

		addHelpTopicId(this, "session.clustering.rcm.userDefined");

	}
}
