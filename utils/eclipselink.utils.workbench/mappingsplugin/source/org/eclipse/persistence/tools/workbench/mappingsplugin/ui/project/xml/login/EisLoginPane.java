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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.login;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


class EisLoginPane extends AbstractLoginPane
{
	/**
	 * Creates a new <code>EisLoginPane</code>.
	 *
	 * @param subjectHolder The holder of {@link EISLoginAdapter}
	 * @param context The plug-in context to be used, such as <code>ResourceRepository</code>
	 */
	EisLoginPane(PropertyValueModel subjectHolder,
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
		return new PropertyAspectAdapter(buildLoginSpecHolder(), MWEisLoginSpec.CONNECTION_FACTORY_URL_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
			    MWEisLoginSpec loginSpec = (MWEisLoginSpec) subject;
				return loginSpec.getConnectionFactoryURL();
			}

			protected void setValueOnSubject(Object value)
			{
			    MWEisLoginSpec loginSpec = (MWEisLoginSpec) subject;
				loginSpec.setConnectionFactoryURL((String) value);
			}
		};
	}

	/**
	 * Creates a Browse button that will take care to show the class chooser.
	 *
	 * @return A new browse button
	 */
	private ClassChooserPanel buildConnectionSpecClassChooserPanel(JLabel connectionSpecClassLabel)
	{
	    return ClassChooserTools.buildPanel(
	            buildConnectionSpecClassHolder(),
	            buildClassRepositoryHolder(), 
	            ClassChooserTools.buildDeclarableFilter(), 
	            connectionSpecClassLabel,
	            getWorkbenchContextHolder());
	}


	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Connection Spec Class Name property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildConnectionSpecClassHolder()
	{
		return new PropertyAspectAdapter(buildLoginSpecHolder(), MWEisLoginSpec.CONNECTION_SPEC_CLASS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
			    MWEisLoginSpec loginSpec = (MWEisLoginSpec) subject;
				return loginSpec.getConnectionSpecClass();
			}

			protected void setValueOnSubject(Object value)
			{
			    MWEisLoginSpec loginSpec = (MWEisLoginSpec) subject;
			    loginSpec.setConnectionSpecClass((MWClass) value);
			}
		};
}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Platform label
		JComponent platformLabeledCombo = buildLabeledComboBox("EIS_PLATFORM_LABEL", buildJ2CAdaptersComboBoxModel());

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(platformLabeledCombo, constraints);
		addAlignLeft(platformLabeledCombo);
		addHelpTopicId(platformLabeledCombo, "project.eis.platform");

		// Connection specification class label
		JLabel connectionSpecClassLabel = buildLabel("CONNECTION_EIS_CONNECTION_SPEC_CLASS_NAME_FIELD");

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(connectionSpecClassLabel, constraints);
		addAlignLeft(connectionSpecClassLabel);

		// Connection specification class chooser panel
		ClassChooserPanel connectionSpecClassWidgets = buildConnectionSpecClassChooserPanel(connectionSpecClassLabel);

		constraints.gridx       = 1;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 5, 0, 0);

		add(connectionSpecClassWidgets, constraints);
		addPaneForAlignment(connectionSpecClassWidgets);

		// Connection Factory URL widgets
		Component connectionFactoryURLWidgets = buildLabeledTextField
		(
			"CONNECTION_EIS_CONNECTION_FACTORY_URL_FIELD",
			buildConnectionFactoryURLDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 2;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(connectionFactoryURLWidgets, constraints);

		// Username widgets
		Component usernameWidgets = buildUserNameWidgets();

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 2;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(usernameWidgets, constraints);

		// Password widgets
		Component passwordWidgets = buildPasswordWidgets();

		constraints.gridx       = 0;
		constraints.gridy       = 4;
		constraints.gridwidth   = 2;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		add(passwordWidgets, constraints);
		
		// Create the Save Password check box
		JCheckBox savePasswordCheckBox = buildSavePasswordCheckBox();

		constraints.gridx		= 0;
		constraints.gridy		= 5;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(2, 0, 0, 0);

		add(savePasswordCheckBox, constraints);
	}

    private PropertyAspectAdapter buildLoginSpecHolder()
    {
	    return new PropertyAspectAdapter(getSubjectHolder())
	    {
	        protected Object getValueFromSubject()
	        {
	            return ((MWEisProject) subject).getEisLoginSpec();
	        }
	    };
    }

    private PropertyAspectAdapter buildJ2CAdapterHolder()
    {
	    return new PropertyAspectAdapter(buildLoginSpecHolder(), MWEisLoginSpec.J2C_ADAPTER_NAME_PROPERTY)
	    {
	        protected Object getValueFromSubject()
	        {
	            return ((MWEisLoginSpec) subject).getJ2CAdapterName();
	        }
	        
            protected void setValueOnSubject(Object value)
            {
                MWEisLoginSpec loginSpec = (MWEisLoginSpec) subject;
                loginSpec.setJ2CAdapterName((String) value);
            }
	    };
    }

	private ComboBoxModel buildJ2CAdaptersComboBoxModel() {
	    return new ComboBoxModelAdapter(buildJ2CAdaptersCollectionAdapter(), buildJ2CAdapterHolder());
	}
	
	private CollectionValueModel buildJ2CAdaptersCollectionAdapter()
	{
	    return new ReadOnlyCollectionValueModel(CollectionTools.sort(MWEisLoginSpec.j2CAdapterNames()));
	}
}
