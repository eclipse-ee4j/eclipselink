/*
 * Copyright (c) 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.LabeledTextFieldWithDefault;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.UnaryTableSequenceAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;

/**
 *
 * @see UnaryTableSequenceAdapter
 * @see SequencingPane - The parent container
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class UnaryTableSequencePropertyPane extends SequencePropertyPane
{
	/**
	 * Creates a new <code>UnaryTableSequencePropertyPane</code>.
	 *
	 * @param subjectHolder The holder of <code>MWUnaryTableSequence</code>
	 * @param workbenchContextHolder The holder of the <code>WorkbenchContext</code>,
	 * used to retrieve the localized string, active window, etc
	 */
	UnaryTableSequencePropertyPane(ValueModel subjectHolder,
	                               WorkbenchContextHolder workbenchContextHolder)
	{
		super(subjectHolder, workbenchContextHolder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Preallocation Size widgets
		JComponent preallocationSizeWidgets = buildPreallocationSizeWidgets();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(preallocationSizeWidgets, constraints);

		// Counter Field widgets
		CounterFieldPane counterFieldPane = new CounterFieldPane();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		add(counterFieldPane, constraints);
		addPaneForAlignment(counterFieldPane);
	}

	private class CounterFieldPane extends LabeledTextFieldWithDefault
	{
		private CounterFieldPane()
		{
			super(UnaryTableSequencePropertyPane.this.getSubjectHolder(),
			      UnaryTableSequencePropertyPane.this.getWorkbenchContextHolder());
		}

		@Override
		protected PropertyValueModel buildValueHolder()
		{
			return new PropertyAspectAdapter(getSubjectHolder(), UnaryTableSequenceAdapter.COUNTER_FIELD_PROPERTY)
			{
				@Override
				protected void fireAspectChange(Object oldValue, Object newValue)
				{
					// We have to change the oldValue to something in order for
					// the listeners to be notified since we need to show the
					// default value
					if ((oldValue == newValue) && (oldValue == null))
					{
						oldValue = "";
					}

					super.fireAspectChange(oldValue, newValue);
				}

				@Override
				protected Object getValueFromSubject()
				{
					return ((UnaryTableSequenceAdapter)subject).getSequenceCounterField();
				}

				@Override
				protected void setValueOnSubject(Object value)
				{
					((UnaryTableSequenceAdapter)subject).setSequenceCounterField((String)value);
				}
			};
		}

		@Override
		protected String defaultValue()
		{
			return "SEQUENCE";
		}

		@Override
		protected String labelKey()
		{
			return "UNARY_TABLE_SEQUENCE_PANE_COUNTER_FIELD_LABEL";
		}
	}
}