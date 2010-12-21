/*
 * Copyright (c) 2007, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;

/**
 * This <code>ComboBoxEditor</code> uses <code>TextFieldWithDefault</code> in
 * order to push the value to the value holder during editing. Default Swing
 * behavior only pushes the value when the focus is lost or if an action command
 * is fired. This editor also supports showing a default value if no entry is
 * set.
 *
 * @see TextFieldWithDefault
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ComboBoxEditorWithDefault implements ComboBoxEditor
{
	/**
	 * The editor created by the active look and feel.
	 */
	private ComboBoxEditor defaultComboBoxEditor;

	/**
	 * Creates a new <code>ComboBoxEditorWithDefault</code>.
	 *
	 * @param defaultComboBoxEditor The <code>ComboBoxEditor</code> set by the
	 * look and feel
	 * @param subjectHolder The holder of the subject, which is used to either
	 * clear the text field or show the default value
	 * @param valueHolder The <code>PropertyValueModel</code> listening to the
	 * property
	 * @param defaultValueHolder The holder of the default value
	 */
	public ComboBoxEditorWithDefault(ComboBoxEditor defaultComboBoxEditor,
	                                 ValueModel subjectHolder,
	                                 PropertyValueModel valueHolder,
	                                 ValueModel defaultValueHolder)
	{
		this(defaultComboBoxEditor,
		     subjectHolder,
		     valueHolder,
		     defaultValueHolder,
		     null);
	}

	/**
	 * Creates a new <code>ComboBoxEditorWithDefault</code>.
	 *
	 * @param defaultComboBoxEditor The <code>ComboBoxEditor</code> set by the
	 * look and feel
	 * @param subjectHolder The holder of the subject, which is used to either
	 * clear the text field or show the default value
	 * @param valueHolder The <code>PropertyValueModel</code> listening to the
	 * property
	 * @param defaultValueHolder The holder of the default value
	 * @param cellRendererAdapter
	 */
	public ComboBoxEditorWithDefault(ComboBoxEditor defaultComboBoxEditor,
	                                 ValueModel subjectHolder,
	                                 PropertyValueModel valueHolder,
	                                 ValueModel defaultValueHolder,
	                                 CellRendererAdapter cellRendererAdapter)
	{
		super();

		initialize(defaultComboBoxEditor,
		           subjectHolder,
		           valueHolder,
		           defaultValueHolder,
		           cellRendererAdapter);
	}

	/*
	 * (non-Javadoc)
	 */
	public void addActionListener(ActionListener actionListener)
	{
		// No need to add the listener since the value is pushed to the model
		// automatically
	}

	/*
	 * (non-Javadoc)
	 */
	public Component getEditorComponent()
	{
		return defaultComboBoxEditor.getEditorComponent();
	}

	/*
	 * (non-Javadoc)
	 */
	public Object getItem()
	{
		return getTextField().getText();
	}

	private JTextField getTextField()
	{
		return (JTextField) defaultComboBoxEditor.getEditorComponent();
	}

	/**
	 * Initializes this <code>ComboBoxEditorWithDefault</code>.
	 *
	 * @param defaultComboBoxEditor The <code>ComboBoxEditor</code> set by the
	 * look and feel
	 * @param subjectHolder The holder of the subject, which is used to either
	 * clear the text field or show the default value
	 * @param valueHolder The <code>PropertyValueModel</code> listening to the
	 * property
	 * @param defaultValueHolder The holder of the default value
	 * @param cellRendererAdapter
	 */
	private void initialize(ComboBoxEditor defaultComboBoxEditor,
	                        ValueModel subjectHolder,
	                        PropertyValueModel valueHolder,
	                        ValueModel defaultValueHolder,
	                        CellRendererAdapter cellRendererAdapter)
	{
		this.defaultComboBoxEditor = defaultComboBoxEditor;

		new TextFieldWithDefaultHandler
		(
			getTextField(),
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			cellRendererAdapter
		);
	}

	/*
	 * (non-Javadoc)
	 */
	public void removeActionListener(ActionListener actionListener)
	{
		// No need to remove the listener since it never was added
	}

	/*
	 * (non-Javadoc)
	 */
	public void selectAll()
	{
		defaultComboBoxEditor.selectAll();
	}

	/*
	 * (non-Javadoc)
	 */
	public void setItem(Object value)
	{
		JTextField textField = getTextField();

		// When the text field has the focus, the value is set
		// through the value holder
		if (textField.hasFocus())
		{
			return;
		}

		// JTextField never returns null so use an empty string to test the
		// equality of the current text and the value
		if (value == null)
		{
			value = "";
		}

		// Only update the value if it has changed
		if (!textField.getText().equals(value))
		{
			defaultComboBoxEditor.setItem(value);
		}
	}
}