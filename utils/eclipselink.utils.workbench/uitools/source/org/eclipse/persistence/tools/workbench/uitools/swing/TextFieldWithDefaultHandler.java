/*
 * Copyright (c) 2007, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * This handler is responsible to update the text field by showing a default
 * value if one is specified and when there is no entry. The default value is
 * either removed on focus gained or added on focus lost if and only if there is
 * no entry. The default value is shown as grayed out and within parenthesis.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class TextFieldWithDefaultHandler
{
	/**
	 * This <code>CellRendererAdapter</code> is responsible to format the string
	 * holded by the <code>PropertyValueModel</code>.
	 */
	private CellRendererAdapter cellRendererAdapter;

	/**
	 * The holder of the default value.
	 */
	private ValueModel defaultValueHolder;

	/**
	 * Flag used to prevent this widget to push any value to the value holder
	 * when the default value has to be set or cleared from the text field.
	 */
	private boolean locked;

	/**
	 * Flag used to show the default value when the spinner loses the focus or
	 * when the subject holder's value changes and receives null.
	 */
	private boolean showDefaultValue;

	/**
	 * The holder of the subject, which is used to either clear the text field or
	 * show the default value.
	 */
	private ValueModel subjectHolder;

	/**
	 * The text field that will be used to show the default value when the value
	 * holder contains <code>null</code>.
	 */
	private JTextField textField;

	/**
	 * This flat is used to determine if the property change listener listening
	 * to the subject should update the spinner or not. Sometimes, depending on
	 * the other of the events, the spinner is synchronized before the listener
	 * is notified. This will make sure the listener isn't overriding the value
	 * set by the spinner.
	 */
	private boolean valueSet;

	/**
	 * Creates a new <code>TextFieldWithDefault</code>.
	 *
	 * @param textField The text field that will be used to show the default
	 * value when the value holder contains <code>null</code>
	 * @param subjectHolder The holder of the subject, which is used to either
	 * clear the text field or show the default value
	 * @param valueHolder The <code>PropertyValueModel</code> listening to the
	 * property, or <code>null</code> if the value does not need to be updated
	 * through the text field's document
	 * @param defaultValueHolder The holder of the default value
	 */
	public TextFieldWithDefaultHandler(JTextField textField,
	                                   ValueModel subjectHolder,
		                                PropertyValueModel valueHolder,
	                                   ValueModel defaultValueHolder)
	{
		this(textField, subjectHolder, valueHolder, defaultValueHolder, null);
	}

	/**
	 * Creates a new <code>TextFieldWithDefault</code>.
	 *
	 * @param textField The text field that will be used to show the default
	 * value when the value holder contains <code>null</code>
	 * @param subjectHolder The holder of the subject, which is used to either
	 * clear the text field or show the default value
	 * @param valueHolder The <code>PropertyValueModel</code> listening to the
	 * property, or <code>null</code> if the value does not need to be updated
	 * through the text field's document
	 * @param defaultValueHolder The holder of the default value
	 * @param cellRendererAdapter The <code>CellRendererAdapter</code> is
	 * responsible to format the string holded by the <code>PropertyValueModel</code>
	 */
	public TextFieldWithDefaultHandler(JTextField textField,
	                                   ValueModel subjectHolder,
		                                PropertyValueModel valueHolder,
	                                   ValueModel defaultValueHolder,
	                                   CellRendererAdapter cellRendererAdapter)
	{
		super();

		initialize
		(
			textField,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			cellRendererAdapter
		);
	}

	private CellRendererAdapter buildCellRendererAdapter()
	{
		return new AbstractCellRendererAdapter()
		{
			@Override
			public String buildText(Object value)
			{
				return (String)value;
			}
		};
	}

	private Document buildDocument(PropertyValueModel valueHolder,
	                               Document originalDocument)
	{
		return new DocumentAdapter
		(
			buildValueHolder(valueHolder),
			originalDocument
		);
	}

	private FocusListener buildFocusListener()
	{
		return new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				if (!e.isTemporary())
				{
					updateTextFieldOnFocusGained();
				}
			}

			public void focusLost(FocusEvent e)
			{
				if (!e.isTemporary())
				{
					updateTextFieldOnFocusLost();
				}
			}
		};
	}

	private PropertyChangeListener buildPropertyChangeListener()
	{
		return new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				updateTextFieldForegroundColor((String) e.getNewValue());
			}
		};
	}

	private PropertyChangeListener buildSubjectPropertyChangeListener()
	{
		return new PropertyChangeListener() { public void propertyChange(PropertyChangeEvent e)
		{
			if (locked)
				return;

			try
			{
				locked = true;
				showDefaultValue = (subjectHolder.getValue() == null);

				if (showDefaultValue)
				{
					valueSet = false;
				}

				if (!valueSet)
				{
					String defaultValue = (String)defaultValue();
					textField.setText(defaultValue);
					updateTextFieldForegroundColor(defaultValue);
				}
			}
			finally
			{
				locked           = false;
				valueSet         = false;
				showDefaultValue = false;
			}
		}};
	}

	private PropertyValueModel buildValueHolder(PropertyValueModel valueHolder)
	{
		PropertyValueModel holder = new InternalPropertyValueModel(valueHolder);
		holder.addPropertyChangeListener(ValueModel.VALUE, buildPropertyChangeListener());
		return holder;
	}

	/**
	 * Returns the default value stored in the default value holder.
	 *
	 * @return The default value returned by the default value holder,
	 * <code>null</code> is never returned
	 */
	private Object defaultValue()
	{
		// The subject has been disconnected, don't use the default value
		if (subjectHolder.getValue() == null)
		{
			return "";
		}

		Object defaultValue = defaultValueHolder.getValue();

		// No default value specified
		if (StringTools.stringIsEmpty((String)defaultValue))
		{
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(defaultValue);
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Returns the <code>JTextField</code> that was given to this handler.
	 *
	 * @return The widget being used to show a default value
	 */
	public final JTextField getTextField()
	{
		return textField;
	}

	/**
	 * Initializes this text field.
	 *
	 * @param textField The text field that will be used to show the default
	 * value when the value holder contains <code>null</code>
	 * @param subjectHolder
	 * @param valueHolder The <code>PropertyValueModel</code> listening to the
	 * property, or <code>null</code> if the value does not need to be updated
	 * through the text field's document
	 * @param defaultValueHolder The holder of the default value
	 * @param cellRendererAdapter The <code>CellRendererAdapter</code>
	 * responsible to format the string holded by the <code>PropertyValueModel</code>
	 */
	private void initialize(JTextField textField,
	                        ValueModel subjectHolder,
	                        PropertyValueModel valueHolder,
	                        ValueModel defaultValueHolder,
	                        CellRendererAdapter cellRendererAdapter)
	{
		this.textField           = textField;
		this.defaultValueHolder  = defaultValueHolder;
		this.subjectHolder       = subjectHolder;
		this.cellRendererAdapter = (cellRendererAdapter != null) ? cellRendererAdapter : buildCellRendererAdapter();
		this.subjectHolder.addPropertyChangeListener(ValueModel.VALUE, buildSubjectPropertyChangeListener());

		if (valueHolder != null)
		{
			// The document needs to be detached from the text field before the
			// DocumentAdapter can be set since it's using the old document as the
			// delegate and this will cause a dead lock
			Document document = buildDocument(valueHolder, textField.getDocument());
			textField.setDocument(new PlainDocument());
			textField.setDocument(document);
		}

		textField.addFocusListener(buildFocusListener());
		updateTextFieldForegroundColor(textField.getText());
	}

	/**
	 * Updates the foreground color based on the given text. If the text is the
	 * default value then the foreground color is changed to grey.
	 *
	 * @param text The current value
	 */
	private void updateTextFieldForegroundColor(String text)
	{
		if (defaultValue().equals(text))
		{
			textField.setForeground(Color.GRAY);
		}
		else
		{
			textField.setForeground(UIManager.getColor("TextField.foreground"));
		}
	}

	/**
	 * Clears the default value if the text field contains it.
	 */
	private void updateTextFieldOnFocusGained()
	{
		if (textField.getText().equals(defaultValue()))
		{
			locked = true;

			try
			{
				textField.setText("");
				textField.setForeground(UIManager.getColor("TextField.foreground"));
			}
			finally
			{
				locked = false;
			}
		}
	}

	/**
	 * Show the default value if the text field contains an empty entry.
	 */
	private void updateTextFieldOnFocusLost()
	{
		if (textField.getText().trim().length() == 0)
		{
			locked = true;

			try
			{
				textField.setText((String)defaultValue());
				textField.setForeground(Color.GRAY);
			}
			finally
			{
				locked = false;
			}
		}
	}

	/**
	 * This <code>PropertyValueModel</code> is responsible to format the actual
	 * value before returning it. The value will not be given to the nested
	 * <code>PropertyValueModel</code> when the lock is on.
	 */
	private final class InternalPropertyValueModel extends TransformationPropertyValueModel
	{
		/**
		 * Creates a new <code>InternalPropertyValueModel</code>.
		 *
		 * @param valueHolder The nested <code>PropertyValueModel</code>
		 */
		InternalPropertyValueModel(PropertyValueModel valueHolder)
		{
			super(valueHolder);
		}

		private String formatNewValue(String value)
		{
			return (value == null) ? null : cellRendererAdapter.buildText(value);
		}

		/*
		 * (non-Javadoc)
		 */
		@Override
		protected Object reverseTransform(Object value)
		{
			return defaultValue().equals(value) ? null : value;
		}

		/*
		 * (non-Javadoc)
		 */
		@Override
		public void setValue(Object value)
		{
			if (!locked)
			{
				super.setValue(value);
			}
		}

		/*
		 * (non-Javadoc)
		 */
		@Override
		protected Object transform(Object value)
		{
			// Show the default value
			if (showDefaultValue)
			{
				return defaultValue();
			}

			// During editing, don't change the null value to the default value
			return (textField.hasFocus() || (value != null)) ?
			        formatNewValue((String)value) : defaultValue();
		}

		/*
		 * (non-Javadoc)
		 */
		@Override
		protected void valueChanged(PropertyChangeEvent e)
		{
			valueSet = true;
			super.valueChanged(e);
		}
	}
}