/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.BorderLayout;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableContainerMapping;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


public final class IndirectableContainerMappingPanel extends AbstractSubjectPanel
{
	public IndirectableContainerMappingPanel(ValueModel subjectHolder,
														  ApplicationContext context)
	{
		super(new BorderLayout(10, 0), subjectHolder, context);
	}

	private JComponent buildIndirectionPane()
	{
		JPanel pane = new JPanel(new BorderLayout(10, 0));
		PropertyValueModel valueHolder = buildIndirectionTypeBooleanHolder();

		JRadioButton valueHolderCheckBox = buildRadioButton
		(
			"INDIRECTABLE_COLLECTION_VALUE_HOLDER_RADIO_BUTTON",
			buildValueHolderRadioButtonAdapter(valueHolder)
		);

		JRadioButton transparentCheckBox = buildRadioButton
		(
			"INDIRECTABLE_COLLECTION_TRANSPARENT_RADIO_BUTTON",
			buildTransparentRadioButtonAdapter(valueHolder)
		);

		pane.add(valueHolderCheckBox, BorderLayout.LINE_START);
		pane.add(transparentCheckBox, BorderLayout.CENTER);

		new ComponentEnabler(buildIndirectionTypeEnablerHolder(), pane.getComponents());

		return pane;
	}

	private PropertyValueModel buildIndirectionTypeBooleanHolder()
	{
		return new TransformationPropertyValueModel(buildIndirectionTypeHolder())
		{
			protected Object transform(Object value)
			{
				if (MWIndirectableContainerMapping.VALUE_HOLDER_INDIRECTION.equals(value))
					return Boolean.TRUE;

				if (MWIndirectableContainerMapping.TRANSPARENT_INDIRECTION.equals(value))
					return Boolean.FALSE;

				return null;
			}
		};
	}

	private PropertyValueModel buildIndirectionTypeEnablerHolder()
	{
		return new TransformationPropertyValueModel(buildIndirectionTypeHolder())
		{
			protected Object transform(Object value)
			{
				return Boolean.valueOf(! MWIndirectableContainerMapping.NO_INDIRECTION.equals(value));
			}
		};
	}

	private PropertyValueModel buildIndirectionTypeHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWIndirectableContainerMapping.INDIRECTION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWIndirectableContainerMapping mapping = (MWIndirectableContainerMapping) subject;

				if (mapping.usesValueHolderIndirection())
					return MWIndirectableContainerMapping.VALUE_HOLDER_INDIRECTION;

				if (mapping.usesTransparentIndirection())
					return MWIndirectableContainerMapping.TRANSPARENT_INDIRECTION;

				return MWIndirectableContainerMapping.NO_INDIRECTION;
			}

			protected void setValueOnSubject(Object value)
			{
				MWIndirectableContainerMapping mapping = (MWIndirectableContainerMapping) subject;

				if (Boolean.TRUE.equals(value))
					mapping.setUseValueHolderIndirection();
				else
					mapping.setUseTransparentIndirection();
			}
		};
	}

	private ButtonModel buildTransparentRadioButtonAdapter(PropertyValueModel valueHolder)
	{
		return new RadioButtonModelAdapter(valueHolder, Boolean.FALSE);
	}

	private ButtonModel buildUseIndirectionCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildUseIndirectionHolder());
	}

	private PropertyValueModel buildUseIndirectionHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSubjectHolder(), MWIndirectableContainerMapping.INDIRECTION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWIndirectableContainerMapping mapping = (MWIndirectableContainerMapping) subject;

				if (mapping.usesValueHolderIndirection())
					return MWIndirectableContainerMapping.INDIRECTION_PROPERTY;

				if (mapping.usesTransparentIndirection())
					return MWIndirectableContainerMapping.TRANSPARENT_INDIRECTION;

				return MWIndirectableContainerMapping.NO_INDIRECTION;
			}

			protected void setValueOnSubject(Object value)
			{
				MWIndirectableContainerMapping mapping = (MWIndirectableContainerMapping) subject;

				if (Boolean.TRUE.equals(value) && mapping.usesNoIndirection())
				{
					mapping.setUseValueHolderIndirection();
				}
				else if (Boolean.FALSE.equals(value) && !mapping.usesNoIndirection())
				{
					mapping.setUseNoIndirection();
				}
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object transform(Object value)
			{
				return Boolean.valueOf(! MWIndirectableContainerMapping.NO_INDIRECTION.equals(value));
			}
		};
	}

	private ButtonModel buildValueHolderRadioButtonAdapter(PropertyValueModel valueHolder)
	{
		return new RadioButtonModelAdapter(valueHolder, Boolean.TRUE);
	}

	protected void initializeLayout()
	{
		JCheckBox useIndirectionCheckBox = buildCheckBox
		(
			"INDIRECTABLE_COLLECTION_USE_INDIRECTION_CHECK_BOX",
			buildUseIndirectionCheckBoxAdapter()
		);

		GroupBox groupBox = new GroupBox
		(
			useIndirectionCheckBox,
			buildIndirectionPane()
		);

		add(groupBox, BorderLayout.CENTER);
		addHelpTopicId(this, "mapping.indirection");
	}
}
