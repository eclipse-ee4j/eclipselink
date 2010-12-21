/*
 * Copyright (c) 2007, 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.TextFieldWithDefaultHandler;

/**
 * This pane shows a labeled text field where when the value is null, the
 * default value is shown. The text is grayed out when showing the default
 * value. When the text field receives the focus, the default value is removed
 * and if the text field does not have any input and it loses the focus, the
 * default value is reshown.
 * 
 * @see TextFieldWithDefault
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public abstract class LabeledTextFieldWithDefault extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>LabeledTextFieldWithDefault</code>.
	 *
	 * @param subjectHolder The holder of T
	 * @param workbenchContextHolder The holder of the <code>WorkbenchContext</code>,
	 * used to retrieve the localized string, active window, etc
	 */
	public LabeledTextFieldWithDefault(ValueModel subjectHolder,
	                                   WorkbenchContextHolder workbenchContextHolder)
	{
		super(new BorderLayout(), subjectHolder, workbenchContextHolder);
	}

	private ValueModel buildDefaultValueHolder()
	{
		return new SimplePropertyValueModel()
		{
			@Override
			public Object getValue()
			{
				return defaultValue();
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to push the value
	 * to the model or to retrieve the value.
	 *
	 * @return The <code>PropertyValueModel</code> listening to the property
	 */
	protected abstract PropertyValueModel buildValueHolder();

	/**
	 * Returns the default value to be shown when no value can be shown.
	 *
	 * @return A non-<code>null</code> value representing the default value
	 */
	protected abstract String defaultValue();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void initializeLayout()
	{
		JTextField textField = new JTextField(0);
		installTextFieldWithDefaultHandler(textField);

		JComponent widgets = buildLabeledComponent(labelKey(), textField);
		add(widgets, BorderLayout.CENTER);
	}

	private void installTextFieldWithDefaultHandler(JTextField textField)
	{
		new TextFieldWithDefaultHandler
		(
			textField,
			getSubjectHolder(),
			buildValueHolder(),
			buildDefaultValueHolder()
		);
	}

	/**
	 * Returns the key used to retrieve the localized string used to label the
	 * text field.
	 *
	 * @return The label key
	 */
	protected abstract String labelKey();

	/**
	 * Wraps the given value with parenthesis.
	 *
	 * @param value The value to be wrapped with parenthesis
	 * @return "(" + value + ")"
	 */
	protected final String wrapDefaultValue(String value)
	{
		StringBuilder sb = new StringBuilder(value.length() + 2);
		sb.append('(');
		sb.append(value);
		sb.append(')');
		return sb.toString();
	}
}