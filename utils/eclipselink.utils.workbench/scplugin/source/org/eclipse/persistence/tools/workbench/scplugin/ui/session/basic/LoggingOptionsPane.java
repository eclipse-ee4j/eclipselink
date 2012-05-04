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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ButtonModel;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LogAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;


/**
 * Here the layout of this pane:
 * <pre>
 * ________________________________________
 * |                                      |
 * | x Log Exception Stack Trace          |
 * |                                      |
 * | x Print Connection                   |
 * |                                      |
 * | x Print Date                         |
 * |                                      |
 * | x Print Session                      |
 * |                                      |
 * | X Print Thread                       |
 * |                                      |
 * ----------------------------------------
 * </pre>
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class LoggingOptionsPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>LoggingOptionsPane</code>.
	 *
	 * @param subjectHolder
	 * @param context
	 */
	public LoggingOptionsPane(ValueModel subjectHolder, ApplicationContext context)
	{
		super(subjectHolder, context);
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildLogExceptionStackTraceCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildLogExceptionStackTraceHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Log Exception Stack Trace property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildLogExceptionStackTraceHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), LogAdapter.SHOULD_LOG_EXCEPTION_STACK_TRACE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				LogAdapter adapter = (LogAdapter) subject;
				return Boolean.valueOf(adapter.getShouldLogExceptionStackTrace());
			}

			protected void setValueOnSubject(Object value)
			{
				LogAdapter adapter = (LogAdapter) subject;
				adapter.setShouldLogExceptionStackTrace(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildPrintConnectionCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildPrintConnectionHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Log Exception Stack Trace property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPrintConnectionHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), LogAdapter.SHOULD_PRINT_CONNECTION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				LogAdapter adapter = (LogAdapter) subject;
				return Boolean.valueOf(adapter.getShouldPrintConnection());
			}

			protected void setValueOnSubject(Object value)
			{
				LogAdapter adapter = (LogAdapter) subject;
				adapter.setShouldPrintConnection(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildPrintDateCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildPrintDateHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Log Exception Stack Trace property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPrintDateHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), LogAdapter.SHOULD_PRINT_DATE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				LogAdapter adapter = (LogAdapter) subject;
				return Boolean.valueOf(adapter.getShouldPrintDate());
			}

			protected void setValueOnSubject(Object value)
			{
				LogAdapter adapter = (LogAdapter) subject;
				adapter.setShouldPrintDate(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildPrintSessionCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildPrintSessionHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Log Exception Stack Trace property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPrintSessionHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), LogAdapter.SHOULD_PRINT_SESSION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				LogAdapter adapter = (LogAdapter) subject;
				return Boolean.valueOf(adapter.getShouldPrintSession());
			}

			protected void setValueOnSubject(Object value)
			{
				LogAdapter adapter = (LogAdapter) subject;
				adapter.setShouldPrintSession(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildPrintThreadCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildPrintThreadHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Log Exception Stack Trace property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPrintThreadHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), LogAdapter.SHOULD_PRINT_THREAD_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				LogAdapter adapter = (LogAdapter) subject;
				return Boolean.valueOf(adapter.getShouldPrintThread());
			}

			protected void setValueOnSubject(Object value)
			{
				LogAdapter adapter = (LogAdapter) subject;
				adapter.setShouldPrintThread(Boolean.TRUE.equals(value));
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Log Exception Stack Trace
		JComponent logExceptionStackTraceCheckBox = buildCheckBox
		(
			"LOGGING_OPTIONS_LOG_EXCEPTION_STACK_TRACE_CHECKBOX",
			buildLogExceptionStackTraceCheckBoxAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(logExceptionStackTraceCheckBox, constraints);

		// Print Connection
		JComponent printConnectionCheckBox = buildCheckBox
		(
			"LOGGING_OPTIONS_PRINT_CONNECTION_CHECKBOX",
			buildPrintConnectionCheckBoxAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(printConnectionCheckBox, constraints);

		// Print Date
		JComponent printDateCheckBox = buildCheckBox
		(
			"LOGGING_OPTIONS_PRINT_DATE_CHECKBOX",
			buildPrintDateCheckBoxAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(printDateCheckBox, constraints);

		// Print Session
		JComponent printSessionCheckBox = buildCheckBox
		(
			"LOGGING_OPTIONS_PRINT_SESSION_CHECKBOX",
			buildPrintSessionCheckBoxAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(printSessionCheckBox, constraints);

		// Print Thread
		JComponent printThreadCheckBox = buildCheckBox
		(
			"LOGGING_OPTIONS_PRINT_THREAD_CHECKBOX",
			buildPrintThreadCheckBoxAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(printThreadCheckBox, constraints);
	}
}
