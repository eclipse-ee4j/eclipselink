/*
 * Copyright (c) 2006, 2007, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;

import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * This adapter helps to convert the selected value to a string representation
 * before the default combo's editor receives it. Then once the edited value
 * needs to be pushed to the model, then another conversion is performed.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class ComboBoxEditorAdapter implements ComboBoxEditor
{
	/**
	 * The converter used to convert the selected value to a string or from the
	 * edited string to the selected value.
	 */
	private final BidiStringConverter converter;

	/**
	 * The combo's editor used to delegate the behavior.
	 */
	private final ComboBoxEditor delegate;

	/**
	 * This defines what is the replacement of a <code>null</code> value since a
	 * non-<code>null</code> value is required for proper selection in a combo
	 * popup.
	 */
	private final Object nullValue;

	/**
	 * Creates a new <code>ComboBoxEditorAdapter</code>. No value is used to
	 * represent the <code>null</code> value.
	 *
	 * @param delegate The combo's editor used to delegate the behavior
	 * @param converter The <code>BidiStringConverter</code> used to convert the
	 * item from the generic type to a string or vice versa
	 */
	public ComboBoxEditorAdapter(ComboBoxEditor delegate,
	                             BidiStringConverter converter)
	{
		this(delegate, converter, null);
	}

	/**
	 * Creates a new <code>ComboBoxEditorAdapter</code>.
	 *
	 * @param delegate The combo's editor used to delegate the behavior
	 * @param converter The <code>BidiStringConverter</code> used to convert the
	 * item from the generic type to a string or vice versa
	 */
	@SuppressWarnings("unchecked")
	public ComboBoxEditorAdapter(ComboBoxEditor delegate,
	                             BidiStringConverter converter,
	                             Object nullValue)
	{
		super();

		this.delegate = delegate;
		this.converter = (BidiStringConverter) converter;
		this.nullValue = nullValue;
	}

	/*
	 * (non-Javadoc)
	 */
	public void addActionListener(ActionListener listener)
	{
		delegate.addActionListener(listener);
	}

	/*
	 * (non-Javadoc)
	 */
	public Component getEditorComponent()
	{
		return delegate.getEditorComponent();
	}

	/*
	 * (non-Javadoc)
	 */
	public Object getItem()
	{
		String value = (String) delegate.getItem();

		// Make sure there are no leading or trailing spaces
		if (value != null)
		{
			value = value.trim();
		}

		// Empty string was set, convert "null" to the string representation
		// and update the delegate
		if (StringTools.stringIsEmpty(value))
		{
			value = null;
		}
		// null-value, convert it to null before converting it
		// to the object representation
		else if (value == nullValue())
		{
			value = null;
		}

		// Convert the string value to the underlying model's object
		Object objectValue = converter.convertToObject(value);

		// null can't be used, use an empty string otherwise
		// JComboBox will ignore it
		if (objectValue == null)
		{
			objectValue = "";
		}

		return objectValue;
	}

	/**
	 * Returns the value that represents the <code>null</code> item.
	 *
	 * @return The <code>null</code>-value
	 */
	protected Object nullValue()
	{
		return nullValue;
	}

	/*
	 * (non-Javadoc)
	 */
	public void removeActionListener(ActionListener listener)
	{
		delegate.removeActionListener(listener);
	}

	/*
	 * (non-Javadoc)
	 */
	public void selectAll()
	{
		delegate.selectAll();
	}

	/*
	 * (non-Javadoc)
	 */
	public void setItem(Object value)
	{
		// Convert the null-value to null
		if (value == nullValue())
		{
			value = null;
		}

		// Convert the object to its string representation
		value = converter.convertToString(value);

		// Pass the value to the delegate
		delegate.setItem(value);
	}
}