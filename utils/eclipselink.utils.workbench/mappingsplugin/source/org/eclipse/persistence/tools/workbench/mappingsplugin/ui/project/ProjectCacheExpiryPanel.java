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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCacheExpiry;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.UiDescriptorBundle;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DateSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


/**
 * @version 10.1.3
 */
//TODO this is almost a copy of CacheExpiryPanel, seems like we should be able to refactor them
final class ProjectCacheExpiryPanel extends AbstractSubjectPanel
{
	ProjectCacheExpiryPanel(ApplicationContext context,
	                        PropertyValueModel cachingExpiryHolder)
	{
		super(cachingExpiryHolder, context.buildExpandedResourceRepositoryContext(UiDescriptorBundle.class));
	}

	private PropertyValueModel buildCacheExpiryTypHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWCacheExpiry.CACHE_EXPIRY_TYPE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				return ((MWCacheExpiry) this.subject).getExpiryType();
			}

			protected void setValueOnSubject(Object value)
			{
                ((MWCacheExpiry) this.subject).setExpiryType((String) value);
			}
		};
	}

	private void buildDailyExpiryComponentEnabler(Component component)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(buildCacheExpiryTypHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				return Boolean.valueOf(MWCacheExpiry.CACHE_EXPIRY_DAILY_EXPIRY.equals(value));
			}
		};

		new ComponentEnabler(booleanHolder, component);
	}

	private PropertyValueModel buildDailyExpiryHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWCacheExpiry.DAILY_EXPIRY_TIME_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				return ((MWCacheExpiry) this.subject).getDailyExpiryTime();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWCacheExpiry) this.subject).setDailyExpiryTime((Date) value);
			}
		};
	}

	private ButtonModel buildDailyExpiryRadioButtonAdapter()
	{
		return new RadioButtonModelAdapter
		(
			buildCacheExpiryTypHolder(),
            MWCacheExpiry.CACHE_EXPIRY_DAILY_EXPIRY
		);
	}

	private SpinnerDateModel buildDailyExpirySpinnerAdapter()
	{
		Date MIN = new Date(0, 0, 1, 0, 0, 0);
		Date MAX = new Date(300, 0, 1, 0, 0, 0);

		return new DateSpinnerModelAdapter
		(
			buildDailyExpiryHolder(),
			MIN,             // Minimum value
			MAX,             // Maximum value
			Calendar.MINUTE, // Step
			MIN              // Default value
		);
	}

	private ButtonModel buildNoCacheExpiryRadioButtonAdapter()
	{
		return new RadioButtonModelAdapter
		(
			buildCacheExpiryTypHolder(),
            MWCacheExpiry.CACHE_EXPIRY_NO_EXPIRY
		);
	}

	private ComponentEnabler buildTimeToLiveExpiryComponentEnabler(Component component)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(buildCacheExpiryTypHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				return Boolean.valueOf(MWCacheExpiry.CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY.equals(value));
			}
		};

		return new ComponentEnabler(booleanHolder, component);
	}

	private PropertyValueModel buildTimeToLiveExpiryHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWCacheExpiry.TIME_TO_LIVE_EXPIRY_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				return ((MWCacheExpiry) this.subject).getTimeToLiveExpiry();
			}

			protected void setValueOnSubject(Object value)
			{
                ((MWCacheExpiry) this.subject).setTimeToLiveExpiry((Long) value);
			}
		};
	}

	private ButtonModel buildTimeToLiveExpiryRadioButtonAdapter()
	{
		return new RadioButtonModelAdapter
		(
			buildCacheExpiryTypHolder(),
            MWCacheExpiry.CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY
		);
	}

	private SpinnerNumberModel buildTimeToLiveExpirySpinnerAdapter() 
	{
		return new NumberSpinnerModelAdapter
		(
			buildTimeToLiveExpiryHolder(),
			new Long(0),              // Minimum value
			new Long(Long.MAX_VALUE), // Maximum value
			new Long(1),              // Step size
			new Long(0)               // Default value
		);
	}

	private ButtonModel buildUpdateReadTimeOnUpdateCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter
		(
			buildUpdateReadTimeOnUpdateHolder()
		);
	}

	private ComponentEnabler buildUpdateReadTimeOnUpdateComponentEnabler(Component component)
	{
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(buildCacheExpiryTypHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				// We want to enable the component for any expiry type except "no expiry"
				return Boolean.valueOf(! MWCacheExpiry.CACHE_EXPIRY_NO_EXPIRY.equals(value));
			}
		};

		return new ComponentEnabler(booleanHolder, component);
	}

	private PropertyValueModel buildUpdateReadTimeOnUpdateHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWCacheExpiry.UPDATE_READ_TIME_ON_UPDATE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				return Boolean.valueOf(((MWCacheExpiry) this.subject).getUpdateReadTimeOnUpdate());
			}

			protected void setValueOnSubject(Object value)
			{
				((MWCacheExpiry) this.subject).setUpdateReadTimeOnUpdate(((Boolean) value).booleanValue());
			}
		};
	}

	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("CACHING_POLICY_CACHE_EXPIRY"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		// No expiry radio button
		JRadioButton noCacheExpiryButton = buildRadioButton
		(
			"CACHING_POLICY_NO_EXPIRY",
			buildNoCacheExpiryRadioButtonAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(noCacheExpiryButton, constraints);

		// Time-to-live expiry radio button
		JRadioButton timeToLiveExpiryButton = buildRadioButton
		(
			"CACHING_POLICY_TIME_TO_LIVE_EXPIRY",
			buildTimeToLiveExpiryRadioButtonAdapter()
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

		add(timeToLiveExpiryButton, constraints);

		// Create the "expire after" label
		JLabel expireAfterLabel = buildLabel("CACHING_POLICY_TIME_TO_LIVE_EXPIRY_PREFIX_LABEL");

		constraints.gridx      = 1;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 25, 0, 0);

		add(expireAfterLabel, constraints);

		// Time To Live expiry spinner
		JSpinner timeToLiveExpirySpinner = SwingComponentFactory.
			buildSpinnerNumber(buildTimeToLiveExpirySpinnerAdapter());
		SwingComponentFactory.attachDateSpinnerCommiter(timeToLiveExpirySpinner, getSubjectHolder());

		constraints.gridx      = 2;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 5, 0, 0);

		add(timeToLiveExpirySpinner, constraints);
		expireAfterLabel.setLabelFor(timeToLiveExpirySpinner);
		buildTimeToLiveExpiryComponentEnabler(timeToLiveExpirySpinner);

		// Create the "milliseconds" label
		JLabel millisecondsLabel = buildLabel("CACHING_POLICY_TIME_TO_LIVE_EXPIRY_POSTFIX_LABEL");

		constraints.gridx      = 3;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 5, 0, 0);

		add(millisecondsLabel, constraints);

		// Daily Expiry radio button
		JRadioButton dailyExpiryButton = buildRadioButton
		(
			"CACHING_POLICY_DAILY_EXPIRY",
			buildDailyExpiryRadioButtonAdapter()
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

		add(dailyExpiryButton, constraints);
		millisecondsLabel.setLabelFor(dailyExpiryButton);

		// Create the "expire at" label
		JLabel expireAtLabel = buildLabel("CACHING_POLICY_DAILY_EXPIRY_PREFIX_LABEL");

		constraints.gridx      = 1;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 25, 0, 0);

		add(expireAtLabel, constraints);

		// Daily expiry spinner
		JSpinner dailyExpirySpinner = buildSpinnerDate(buildDailyExpirySpinnerAdapter());
		dailyExpirySpinner.setEditor(new JSpinner.DateEditor(dailyExpirySpinner, "HH : mm : ss"));
		SwingComponentFactory.attachDateSpinnerCommiter(dailyExpirySpinner, getSubjectHolder());

		int width = SwingUtilities.computeStringWidth(dailyExpirySpinner.getFontMetrics(dailyExpirySpinner.getFont()), "00 : 00 : 00");
		dailyExpirySpinner.setPreferredSize(new Dimension(width + 10, dailyExpirySpinner.getPreferredSize().height));

		constraints.gridx      = 2;
		constraints.gridy      = 2;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 5, 5, 0);

		add(dailyExpirySpinner, constraints);
		expireAtLabel.setLabelFor(dailyExpirySpinner);
		buildDailyExpiryComponentEnabler(dailyExpirySpinner);

		// Create the update-on-read checkbox
		JCheckBox updateReadTimeOnUpdateCheckBox = buildCheckBox
		(
			"CACHING_POLICY_UPDATE_READ_TIME_ON_UPDATE",
			buildUpdateReadTimeOnUpdateCheckBoxAdapter()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 4;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(10, 0, 5, 0);

		add(updateReadTimeOnUpdateCheckBox, constraints);
		buildUpdateReadTimeOnUpdateComponentEnabler(updateReadTimeOnUpdateCheckBox);
	}
}
