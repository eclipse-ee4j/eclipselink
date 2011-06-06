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
package org.eclipse.persistence.tools.workbench.scplugin.ui.login;

// JDK
import java.awt.Component;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;


/**
 * This is the abstract page all Login panels should subclass. It shows the
 * information regarding the Database login which is specific for
 * {@link LoginAdapter}.
 * <p>
 * Known subclasses:<br>
 * - {@link AbstractRdbmsLoginPane}
 * - {@link EisLoginPane}
 * 
 * @see LoginAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
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
	protected final ValueModel buildClassRepositoryHolder()
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
		return new PropertyAspectAdapter(getSubjectHolder(), LoginAdapter.PASSWORD_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				LoginAdapter adapter = (LoginAdapter) subject;
				return adapter.getPassword();
			}

			protected void setValueOnSubject(Object value)
			{
				LoginAdapter adapter = (LoginAdapter) subject;
				adapter.setPassword((String) value);
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
		return new PropertyAspectAdapter(this.getSubjectHolder(), LoginAdapter.SAVE_PASSWORD_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((LoginAdapter) this.subject).isSavePassword());
			}	
			protected void setValueOnSubject(Object value) {
				((LoginAdapter) this.subject).setSavePassword(((Boolean) value).booleanValue());
			}
		};
	}
	
	protected PropertyValueModel buildSavePasswordHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), LoginAdapter.SAVE_PASSWORD_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((LoginAdapter)this.subject).isSavePassword());
			}
		};
	}
	
	// ********** Save Username **********
	
	protected JCheckBox buildSaveUsernameCheckBox() {
		return buildCheckBox("SAVE_USERNAME_CHECK_BOX", buildSaveUsernameCheckBoxModelAdapter());
	}
	
	private ButtonModel buildSaveUsernameCheckBoxModelAdapter() {
		return new CheckBoxModelAdapter(buildSaveUsernamePropertyAdapter());
	}

	private PropertyValueModel buildSaveUsernamePropertyAdapter() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), LoginAdapter.SAVE_USERNAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((LoginAdapter) this.subject).isSaveUsername());
			}	
			protected void setValueOnSubject(Object value) {
				((LoginAdapter) this.subject).setSaveUsername(((Boolean) value).booleanValue());
			}
		};
	}

	protected PropertyValueModel buildSaveUsernameHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), LoginAdapter.SAVE_USERNAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((LoginAdapter)this.subject).isSaveUsername());
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
		return new PropertyAspectAdapter(getSubjectHolder(), LoginAdapter.USER_NAME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				LoginAdapter adapter = (LoginAdapter) subject;
				return adapter.getUserName();
			}

			protected void setValueOnSubject(Object value)
			{
				LoginAdapter adapter = (LoginAdapter) subject;
				adapter.setUserName((String) value);
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
