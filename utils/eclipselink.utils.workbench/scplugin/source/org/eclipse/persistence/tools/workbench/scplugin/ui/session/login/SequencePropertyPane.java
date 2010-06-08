/*
 * Copyright (c) 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SequenceAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;

/**
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
abstract class SequencePropertyPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>SequencePropertyPane</code>.
	 *
	 * @param subjectHolder The holder of <code>SequenceAdapter</code>
	 * @param workbenchContextHolder The holder of the <code>WorkbenchContext</code>,
	 * used to retrieve the localized string, active window, etc
	 */
	SequencePropertyPane(ValueModel subjectHolder,
	                     WorkbenchContextHolder workbenchContextHolder)
	{
		super(subjectHolder, workbenchContextHolder);
	}

	private PropertyValueModel buildPreallocationSizeHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), SequenceAdapter.PREALLOCATION_SIZE_PROPERTY)
		{
			@Override
			protected Object getValueFromSubject()
			{
				return Integer.valueOf(((SequenceAdapter)subject).getPreallocationSize());
			}

			@Override
			protected void setValueOnSubject(Object value)
			{
				if (value == null) {
					((SequenceAdapter)subject).setPreallocationSize(0);
				} else {
					((SequenceAdapter)subject).setPreallocationSize(((Integer)value).intValue());
				}
			}
		};
	}

	final JComponent buildPreallocationSizeWidgets()
	{
		return SwingComponentFactory.buildLabeledSpinnerNumber
		(
			"SEQUENCE_PANE_PREALLOCATION_SIZE_LABEL",
			getSubjectHolder(),
			buildPreallocationSizeHolder(),
			new SimplePropertyValueModel(new Integer(50)),
			new Integer(0),
			Integer.MAX_VALUE,
			new Integer(1),
			6,
			resourceRepository(),
			getAlignLeftGroup(),
			getAlignRightGroup()
		);
	}
}