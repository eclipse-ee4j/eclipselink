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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemListener;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCacheExpiry;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCacheExpiry;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DateSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;



//TODO one thing left that doesn't work, these don't update if you have 2 windows open and change the project defaults, grr
final class CacheExpiryPanel extends AbstractSubjectPanel {

    private PropertyValueModel cacheExpiryHolder;
    private PropertyValueModel cacheExpiryTypeHolder;
    
    
	CacheExpiryPanel(ApplicationContext context,
	                 PropertyValueModel cachingPolicyHolder,
	                 String helpId)
	{
		super(cachingPolicyHolder, context.buildExpandedResourceRepositoryContext(UiDescriptorBundle.class));
		addHelpTopicId(this, helpId);
	}

    protected void initialize(ValueModel subjectHolder) {
        super.initialize(subjectHolder);
        this.cacheExpiryHolder = buildCacheExpiryAdapter();
        this.cacheExpiryTypeHolder = buildCacheExpiryTypeAdapter();
    }
    
    private PropertyValueModel buildCacheExpiryAdapter() {
        return new PropertyAspectAdapter(getSubjectHolder(), MWDescriptorCachingPolicy.CACHE_EXPIRY_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getCacheExpiry();
            }
            
            protected void setValueOnSubject(Object value) {
                //Do nothing here, this is handled in the projectDefaultAdapter
            }
        };        
    }
    
    private PropertyValueModel buildCacheExpiryTypeAdapter() {
        return new PropertyAspectAdapter(this.cacheExpiryHolder, MWCacheExpiry.CACHE_EXPIRY_TYPE_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCacheExpiry) this.subject).getExpiryType();
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWCacheExpiry) this.subject).setExpiryType((String) value);
            }
        };        
    }    
    
    
    protected void initializeLayout() {
        GridBagConstraints constraints = new GridBagConstraints();

        setBorder(BorderFactory.createCompoundBorder(
            buildTitledBorder("CACHING_POLICY_CACHE_EXPIRY"),
            BorderFactory.createEmptyBorder(0, 5, 5, 5)));

        // Project Default check box
        JCheckBox projectDefaultCheckBox = 
            buildCheckBox(
                    "CACHING_POLICY_PROJECT_DEFAULT_CHECK_BOX",
                    buildProjectDefaultCheckBoxAdapter());

        constraints.gridx      = 0;
        constraints.gridy      = 0;
        constraints.gridwidth  = 4;
        constraints.gridheight = 1;
        constraints.weightx    = 0;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(0, 0, 0, 0);

        add(projectDefaultCheckBox, constraints);

        // No expiry radio button
        JRadioButton noCacheExpiryButton = 
            buildRadioButton(
                    "CACHING_POLICY_NO_EXPIRY",
                    buildNoCacheExpiryRadioButtonAdapter());

//        noCacheExpiryButton.addItemListener(buildCacheExpiryActionListener(projectDefaultCheckBox));
        constraints.gridx      = 0;
        constraints.gridy      = 1;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 0;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(10, 0, 0, 0);

        add(noCacheExpiryButton, constraints);

        // Time-to-live expiry radio button
        JRadioButton timeToLiveExpiryButton = 
            buildRadioButton(
                    "CACHING_POLICY_TIME_TO_LIVE_EXPIRY",
                    buildTimeToLiveExpiryRadioButtonAdapter());
//        timeToLiveExpiryButton.addItemListener(buildCacheExpiryActionListener(projectDefaultCheckBox));

        constraints.gridx      = 0;
        constraints.gridy      = 2;
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
        constraints.gridy      = 2;
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
        SwingComponentFactory.attachDateSpinnerCommiter(timeToLiveExpirySpinner, cacheExpiryHolder);
//        timeToLiveExpirySpinner.addChangeListener(buildCacheExpiryActionListener(projectDefaultCheckBox));

        constraints.gridx      = 2;
        constraints.gridy      = 2;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 0;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(0, 5, 0, 0);

        add(timeToLiveExpirySpinner, constraints);
        expireAfterLabel.setLabelFor(timeToLiveExpirySpinner);

        
        // Create the "milliseconds" label
        JLabel millisecondsLabel = buildLabel("CACHING_POLICY_TIME_TO_LIVE_EXPIRY_POSTFIX_LABEL");

        constraints.gridx      = 3;
        constraints.gridy      = 2;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 0;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(0, 5, 0, 0);

        add(millisecondsLabel, constraints);
        buildTimeToLiveExpiryComponentEnabler(new Component[] {timeToLiveExpirySpinner, expireAfterLabel, millisecondsLabel});

        // Daily Expiry radio button
        JRadioButton dailyExpiryButton = 
            buildRadioButton(
                    "CACHING_POLICY_DAILY_EXPIRY",
                    buildDailyExpiryRadioButtonAdapter() );
//        dailyExpiryButton.addItemListener(buildCacheExpiryActionListener(projectDefaultCheckBox));

        constraints.gridx      = 0;
        constraints.gridy      = 3;
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
        constraints.gridy      = 3;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 0;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(0, 25, 0, 0);

        add(expireAtLabel, constraints);

        // Daily expiry spinner
        SpinnerDateModel dateModel = buildDailyExpirySpinnerAdapter();
        JSpinner dailyExpirySpinner = buildSpinnerDate(dateModel);
        dailyExpirySpinner.setEditor(new JSpinner.DateEditor(dailyExpirySpinner, "HH : mm : ss"));
        SwingComponentFactory.attachDateSpinnerCommiter(dailyExpirySpinner, cacheExpiryHolder);
 
        constraints.gridx      = 2;
        constraints.gridy      = 3;
        constraints.gridwidth  = 2;
        constraints.gridheight = 1;
        constraints.weightx    = 0;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(0, 5, 5, 0);

        add(dailyExpirySpinner, constraints);
        expireAtLabel.setLabelFor(dailyExpirySpinner);
        buildDailyExpiryComponentEnabler(new Component[] {dailyExpirySpinner, expireAtLabel});

        // Create the update-on-read checkbox
        JCheckBox updateReadTimeOnUpdateCheckBox = buildCheckBox(
            "CACHING_POLICY_UPDATE_READ_TIME_ON_UPDATE",
            buildUpdateReadTimeOnUpdateCheckBoxAdapter()
        );
//        updateReadTimeOnUpdateCheckBox.addItemListener(buildCacheExpiryActionListener(projectDefaultCheckBox));
 
        constraints.gridx      = 0;
        constraints.gridy      = 4;
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

    private ButtonModel buildProjectDefaultCheckBoxAdapter() {
        return new CheckBoxModelAdapter(buildProjectDefaultAdapter());
    }
    
    private PropertyValueModel buildProjectDefaultAdapter() {
        return new TransformationPropertyValueModel(this.cacheExpiryHolder) {
            protected Object transform(Object value) {
                return (value instanceof MWDescriptorCacheExpiry) ? Boolean.FALSE : Boolean.TRUE;               
            }
            protected Object reverseTransform(Object value) {
                MWCachingPolicy cachingPolicy = (MWCachingPolicy) getSubjectHolder().getValue();
                if (cachingPolicy == null) {
                    return null;
                }
                cachingPolicy.setUseProjectDefaultCacheExpiry(((Boolean) value).booleanValue());
                return cachingPolicy.getCacheExpiry();               
            }
        };
    }
    
    private ButtonModel buildNoCacheExpiryRadioButtonAdapter() {
        return new RadioButtonModelAdapter(
             this.cacheExpiryTypeHolder,
             MWCacheExpiry.CACHE_EXPIRY_NO_EXPIRY
         );
     }
    
    private ButtonModel buildTimeToLiveExpiryRadioButtonAdapter() {
        return new RadioButtonModelAdapter(
                this.cacheExpiryTypeHolder,
                MWCacheExpiry.CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY);
    }
    



  private SpinnerNumberModel buildTimeToLiveExpirySpinnerAdapter() {
      return new NumberSpinnerModelAdapter(
          buildTimeToLiveAdapter(),
          new Long(0),              // Minimum value
          new Long(Long.MAX_VALUE), // Maximum value
          new Long(1),              // Step size
          new Long(0)               // Default value
      );
  }
    
    private PropertyValueModel buildTimeToLiveAdapter() {
        return new PropertyAspectAdapter(this.cacheExpiryHolder, MWCacheExpiry.TIME_TO_LIVE_EXPIRY_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCacheExpiry) this.subject).getTimeToLiveExpiry();
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWCacheExpiry) this.subject).setTimeToLiveExpiry((Long) value);
            }
        };
    }
    
    private ButtonModel buildDailyExpiryRadioButtonAdapter() {
        return new RadioButtonModelAdapter(
                this.cacheExpiryTypeHolder,
                MWCacheExpiry.CACHE_EXPIRY_DAILY_EXPIRY);
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
    
    private PropertyValueModel buildDailyExpiryHolder() {
        return new PropertyAspectAdapter(this.cacheExpiryHolder, MWCacheExpiry.DAILY_EXPIRY_TIME_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCacheExpiry) this.subject).getDailyExpiryTime();
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWCacheExpiry) this.subject).setDailyExpiryTime((Date) value);
            }
        };
    }
    
    
    private ButtonModel buildUpdateReadTimeOnUpdateCheckBoxAdapter() {
        return new CheckBoxModelAdapter(buildUpdateReadTimeOnUpdateHolder());
    }
      
    private PropertyValueModel buildUpdateReadTimeOnUpdateHolder() {
        return new PropertyAspectAdapter(this.cacheExpiryHolder, MWCacheExpiry.UPDATE_READ_TIME_ON_UPDATE_PROPERTY) {
            protected Object getValueFromSubject() {
                return Boolean.valueOf(((MWCacheExpiry) this.subject).getUpdateReadTimeOnUpdate());
            }
              
            protected void setValueOnSubject(Object value) {
                ((MWCacheExpiry) this.subject).setUpdateReadTimeOnUpdate(((Boolean) value).booleanValue());
            }
        };
    }
    
    private ComponentEnabler buildUpdateReadTimeOnUpdateComponentEnabler(Component component) {
        ValueModel noExpiryHolder = new TransformationPropertyValueModel(this.cacheExpiryTypeHolder) {
            protected Object transform(Object value) {
                return Boolean.valueOf(value != MWCacheExpiry.CACHE_EXPIRY_NO_EXPIRY); 
            }
        };
        return new ComponentEnabler(noExpiryHolder, component);
    }
    
    private ComponentEnabler buildTimeToLiveExpiryComponentEnabler(Component[] components) {
        ValueModel noExpiryHolder = new TransformationPropertyValueModel(this.cacheExpiryTypeHolder) {
            protected Object transform(Object value) {
                return Boolean.valueOf(value == MWCacheExpiry.CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY); 
            }
        };
        return new ComponentEnabler(noExpiryHolder, components);
    }
    
    private ComponentEnabler buildDailyExpiryComponentEnabler(Component[] components) {
        ValueModel noExpiryHolder = new TransformationPropertyValueModel(this.cacheExpiryTypeHolder) {
            protected Object transform(Object value) {
                return Boolean.valueOf(value == MWCacheExpiry.CACHE_EXPIRY_DAILY_EXPIRY); 
            }
        };
        return new ComponentEnabler(noExpiryHolder, components);
    }
    
    
}
