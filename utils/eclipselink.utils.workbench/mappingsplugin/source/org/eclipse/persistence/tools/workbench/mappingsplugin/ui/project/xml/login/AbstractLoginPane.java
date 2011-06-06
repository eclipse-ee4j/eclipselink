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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.login;

import java.awt.Component;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


abstract class AbstractLoginPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>AbstractLoginPane</code>.
	 *
	 * @param subjectHolder The holder of {@link LoginAdapter}
	 * @param context The plug-in context to be used, such as <code>ResourceRepository</code>
	 */
	protected AbstractLoginPane(PropertyValueModel subjectHolder,
										 WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to return the
	 * Class Repository.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	protected final ClassRepositoryHolder buildClassRepositoryHolder()
	{
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return ((MWEisProject) subject()).getClassRepository();
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Password value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildPasswordDocumentAdapter()
	{
		return new DocumentAdapter(buildPasswordHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Password property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPasswordHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWEisLoginSpec.PASSWORD_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
			    MWEisLoginSpec loginSpec = ((MWEisProject) subject).getEisLoginSpec();
				return loginSpec.getPassword();
			}

			protected void setValueOnSubject(Object value)
			{
			    MWEisLoginSpec loginSpec = ((MWEisProject) subject).getEisLoginSpec();
			    loginSpec.setPassword((String) value);
			}
		};
	}

	/**
	 * Creates the Password label and text field.
	 *
	 * @return The fully initialized widgets
	 */
	protected Component buildPasswordWidgets()
	{
		JPasswordField passwordField = new JPasswordField(buildPasswordDocumentAdapter(), null, 1);
		return buildLabeledComponent("CONNECTION_PASSWORD_FIELD",
											  passwordField);
	}
	
	// ********** Save Password **********
	
	protected JCheckBox buildSavePasswordCheckBox() {
		return buildCheckBox("SAVE_PASSWORD_CHECK_BOX", buildSavePasswordCheckBoxModelAdapter());
	}
	
	private ButtonModel buildSavePasswordCheckBoxModelAdapter() {
		return new CheckBoxModelAdapter(buildSavePasswordPropertyAdapter());
	}

	private PropertyValueModel buildSavePasswordPropertyAdapter() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWEisLoginSpec.SAVE_PASSWORD_PROPERTY) {
			protected Object getValueFromSubject() {
			    MWEisLoginSpec loginSpec = ((MWEisProject) subject).getEisLoginSpec();
				return Boolean.valueOf(loginSpec.isSavePassword());
			}	
			protected void setValueOnSubject(Object value) {
			    MWEisLoginSpec loginSpec = ((MWEisProject) subject).getEisLoginSpec();
				loginSpec.setSavePassword(((Boolean) value).booleanValue());
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Username value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildUsernameAdapter()
	{
		return new DocumentAdapter(buildUsernameHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Username property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildUsernameHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWEisLoginSpec.USER_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
			    MWEisLoginSpec loginSpec = ((MWEisProject) subject).getEisLoginSpec();
				return loginSpec.getUserName();
			}

			protected void setValueOnSubject(Object value)
			{
			    MWEisLoginSpec loginSpec = ((MWEisProject) subject).getEisLoginSpec();
				loginSpec.setUserName((String) value);
			}
		};
	}

	/**
	 * Creates the Username label and text field.
	 *
	 * @return The fully initialized widgets
	 */
	protected Component buildUserNameWidgets()
	{
		return buildLabeledTextField("CONNECTION_USER_NAME_FIELD",
											  buildUsernameAdapter());
	}

	/**
    * Sets whether or not this component is enabled. Disabling this pane will
    * also disable its children through {@link #updateEnableStateOfChildren(boolean)}.
    *
    * @param enabled <code>true<code> if this component and its children should
    * be enabled, <code>false<code> otherwise
    */
	public final void setEnabled(boolean enabled)
	{
		if (isEnabled() == enabled)
			return;

		super.setEnabled(enabled);
		updateEnableStateOfChildren(enabled);
	}

	/**
	 * Updates the enable state of the children of this pane.
	 *
    * @param enabled <code>true<code> if this pane's children should be enabled,
    * <code>false<code> otherwise
	 */
	protected void updateEnableStateOfChildren(boolean enabled)
	{
		for (int index = getComponentCount(); --index >= 0;)
			getComponent(index).setEnabled(enabled);
	}
}
