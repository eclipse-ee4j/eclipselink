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
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWProxyIndirectionMapping;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


public final class ProxyIndirectionPanel extends AbstractSubjectPanel
{
	public ProxyIndirectionPanel(ValueModel proxyIndirectionMappingHolder,
														  ApplicationContext context)
	{
		super(new BorderLayout(10, 0), proxyIndirectionMappingHolder, context);
	}

	private JComponent buildIndirectionPane()
	{
		JPanel pane = new JPanel(new BorderLayout(10, 0));
		PropertyValueModel valueHolder = buildIndirectionTypeBooleanHolder();

		JRadioButton valueHolderRadioButton = buildRadioButton
		(
			"PROXY_INDIRECTION_PANEL_VALUE_HOLDER_RADIO_BUTTON",
			buildValueHolderRadioButtonAdapter(valueHolder)
		);

		JRadioButton proxyRadioButton = buildRadioButton
		(
			"PROXY_INDIRECTION_PANEL_PROXY_RADIO_BUTTON",
			buildTransparentRadioButtonAdapter(valueHolder)
		);

		pane.add(valueHolderRadioButton, BorderLayout.LINE_START);
		pane.add(proxyRadioButton, BorderLayout.CENTER);

		new ComponentEnabler(buildIndirectionTypeEnablerHolder(), pane.getComponents());

		return pane;
	}

	private PropertyValueModel buildIndirectionTypeBooleanHolder()
	{
		return new TransformationPropertyValueModel(buildIndirectionTypeHolder())
		{
			protected Object transform(Object value)
			{
				if (MWIndirectableMapping.VALUE_HOLDER_INDIRECTION.equals(value))
					return Boolean.TRUE;

				if (MWProxyIndirectionMapping.PROXY_INDIRECTION.equals(value))
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
				return Boolean.valueOf(! MWIndirectableMapping.NO_INDIRECTION.equals(value));
			}
		};
	}

	private PropertyValueModel buildIndirectionTypeHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWIndirectableMapping.INDIRECTION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
                MWProxyIndirectionMapping mapping = (MWProxyIndirectionMapping) this.subject;

				if (mapping.usesValueHolderIndirection())
					return MWIndirectableMapping.VALUE_HOLDER_INDIRECTION;

				if (mapping.usesProxyIndirection())
					return MWProxyIndirectionMapping.PROXY_INDIRECTION;

				return MWIndirectableMapping.NO_INDIRECTION;
			}

			protected void setValueOnSubject(Object value)
			{
                MWProxyIndirectionMapping mapping = (MWProxyIndirectionMapping) this.subject;

				if (Boolean.TRUE.equals(value))
					mapping.setUseValueHolderIndirection();
				else
					mapping.setUseProxyIndirection();
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
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSubjectHolder(), MWIndirectableMapping.INDIRECTION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
                MWProxyIndirectionMapping mapping = (MWProxyIndirectionMapping) this.subject;

				if (mapping.usesValueHolderIndirection())
					return MWIndirectableMapping.INDIRECTION_PROPERTY;

				if (mapping.usesProxyIndirection())
					return MWProxyIndirectionMapping.PROXY_INDIRECTION;

				return MWIndirectableMapping.NO_INDIRECTION;
			}

			protected void setValueOnSubject(Object value)
			{
                MWProxyIndirectionMapping mapping = (MWProxyIndirectionMapping) this.subject;

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
				return Boolean.valueOf(! MWIndirectableMapping.NO_INDIRECTION.equals(value));
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
		addHelpTopicId(this, "mapping.proxy.indirection");
	}
}
