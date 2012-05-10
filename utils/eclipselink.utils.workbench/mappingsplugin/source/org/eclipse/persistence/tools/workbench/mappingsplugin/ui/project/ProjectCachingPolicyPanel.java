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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy.CacheCoordinationOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy.CacheIsolationOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy.ExistenceCheckingOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectCachingPolicy;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;


final class ProjectCachingPolicyPanel extends AbstractSubjectPanel
{

	ProjectCachingPolicyPanel(PropertyValueModel subjectHolder,
	                          WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	private ComboBoxModel buildCacheCoordinationComboBoxModel()
	{
		return new ComboBoxModelAdapter(buildCacheCoordinationListHolder(),
												  buildCacheCoordinationSelectionHolder());
	}

	private void buildCacheCoordinationComponentEnabler(JComboBox comboBox)
	{
		new ComponentEnabler(buildCacheCoordinationEnablerHolder(), comboBox);
	}

	private PropertyValueModel buildCacheCoordinationEnablerHolder()
	{
		return new TransformationPropertyValueModel(buildCacheIsolationSelectionHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;
	
				return Boolean.valueOf(((CacheIsolationOption) value).getMWModelOption() == MWCachingPolicy.CACHE_ISOLATION_SHARED);
			}
		};
	}

	private ListValueModel buildCacheCoordinationListHolder()
	{
        return new AbstractReadOnlyListValueModel() {
            public Object getValue() {
                return MWTransactionalProjectCachingPolicy.cacheCoordinationOptions().toplinkOptions();
            }
        };
    }

	private PropertyValueModel buildCacheCoordinationSelectionHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWCachingPolicy.CACHE_COORDINATION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWCachingPolicy policy = (MWCachingPolicy) subject;
				return policy.getCacheCoordination();
			}
	
			protected void setValueOnSubject(Object value)
			{
				MWCachingPolicy policy = (MWCachingPolicy) subject;
				policy.setCacheCoordination((CacheCoordinationOption) value);
			}
		};
	}

	private ComboBoxModel buildCacheIsolationComboBoxModel()
	{
		return new ComboBoxModelAdapter(buildCacheIsolationListHolder(),
												  buildCacheIsolationSelectionHolder());
	}

	private ListValueModel buildCacheIsolationListHolder()
	{
        return new AbstractReadOnlyListValueModel() {
            public Object getValue() {
                return MWTransactionalProjectCachingPolicy.cacheIsolationOptions().toplinkOptions();
            }
        };
	}

	private PropertyValueModel buildCacheIsolationSelectionHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWCachingPolicy.CACHE_ISOLATION_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWCachingPolicy policy = (MWCachingPolicy) subject;
				return policy.getCacheIsolation();
			}
	
			protected void setValueOnSubject(Object value)
			{
				MWCachingPolicy policy = (MWCachingPolicy) subject;
				policy.setCacheIsolation((CacheIsolationOption) value);
			}
		};
	}

	private PropertyValueModel buildCacheSizeHolder() 
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWCachingPolicy.CACHE_SIZE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				return new Integer(((MWCachingPolicy) subject).getCacheSize());
			}
	
			protected void setValueOnSubject(Object value)
			{
				((MWCachingPolicy) subject).setCacheSize(((Number) value).intValue());
			}
		};
	}

	private NumberSpinnerModelAdapter buildCacheSizeSpinnerModel() 
	{
		return new NumberSpinnerModelAdapter
		(
			buildCacheSizeHolder(),
			new Integer(0),
			new Integer(99999),
			new Integer(1), 
			new Integer(MWCachingPolicy.DEFAULT_CACHE_SIZE)
		);
	}

	private ComboBoxModel buildCacheTypeComboBoxModel()
	{
		return new ComboBoxModelAdapter(buildCacheTypeListHolder(),
												  buildCacheTypeSelectionHolder());
	}

	private ListValueModel buildCacheTypeListHolder()
	{
        return new AbstractReadOnlyListValueModel() {
            public Object getValue() {
                return MWTransactionalProjectCachingPolicy.cacheTypeOptions().toplinkOptions();
            }
        };
	}

	private PropertyValueModel buildCacheTypeSelectionHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWCachingPolicy.CACHE_TYPE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				return ((MWCachingPolicy) subject).getCacheType();
			}
	
			protected void setValueOnSubject(Object value)
			{
				((MWCachingPolicy) subject).setCacheType((MWCachingPolicy.CacheTypeOption)value);
			}
		};
	}

	private ComboBoxModel buildExistenceCheckingComboBoxModel()
	{
		return new ComboBoxModelAdapter(buildExistenceCheckingListHolder(),
												  buildExistenceCheckingSelectionHolder());
	}

	private ListValueModel buildExistenceCheckingListHolder()
	{
        return new AbstractReadOnlyListValueModel() {
            public Object getValue() {
                return MWTransactionalProjectCachingPolicy.existenceCheckingOptions().toplinkOptions();
            }
        };
	}

	private PropertyValueModel buildExistenceCheckingSelectionHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWCachingPolicy.EXISTENCE_CHECKING_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				return ((MWCachingPolicy) this.subject).getExistenceChecking();
			}
	
			protected void setValueOnSubject(Object value)
			{
                ((MWCachingPolicy) this.subject).setExistenceChecking((ExistenceCheckingOption) value);
			}
		};
	}

	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		offset.left += 5; offset.right += 5;

		// Cache Type widgets
		JComponent cacheType = buildLabeledComboBox
		(
			"CACHING_POLICY_CACHE_TYPE_CHOOSER",
			buildCacheTypeComboBoxModel(),
            buildToplinkOptionRenderer()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, offset.left, 0, offset.right);

		add(cacheType, constraints);
		helpManager().addTopicID(cacheType, "project.defaults.cacheType");

		// Cache Size widgets
		JComponent cachingSiseWidgets = buildLabeledSpinnerNumber
		(
			"CACHING_POLICY_CACHE_SIZE_SPINNER",
			buildCacheSizeSpinnerModel()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, offset.left, 0, offset.right);

		add(cachingSiseWidgets, constraints);
		helpManager().addTopicID(cachingSiseWidgets, "project.defaults.cacheSize");

		// Cache Isolation widgets
		JComponent cacheIsolationWidgets = buildLabeledComboBox
		(
			"CACHING_POLICY_CACHE_ISOLATION_CHOOSER",
			buildCacheIsolationComboBoxModel(),
            buildToplinkOptionRenderer()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, offset.left, 0, offset.right);

		add(cacheIsolationWidgets, constraints);
		helpManager().addTopicID(cacheIsolationWidgets, "project.defaults.cacheIsolation");

		// Cache Coordination
		JComboBox cacheCoodinationComboBox = new JComboBox(buildCacheCoordinationComboBoxModel());
		cacheCoodinationComboBox.setRenderer(buildToplinkOptionRenderer());

		JComponent cacheCoordinationWidgets = buildLabeledComponent
		(
			"CACHING_POLICY_CACHE_COORDINATION_CHOOSER",
			cacheCoodinationComboBox
		);

		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, offset.left, 0, offset.right);

		add(cacheCoordinationWidgets, constraints);
		buildCacheCoordinationComponentEnabler(cacheCoodinationComboBox);
		helpManager().addTopicID(cacheCoordinationWidgets, "project.defaults.cacheCoord");

		// Cache Expiry panel
		ProjectCacheExpiryPanel cacheExpiryPanel = new ProjectCacheExpiryPanel
		(
			getApplicationContext(),
			buildCacheExpiryHolder()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 4;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		add(cacheExpiryPanel, constraints);
		addHelpTopicId(cacheExpiryPanel, "project.defaults.cacheExpiry");

		// Existence Checking widgets
		JComponent existenceCheckingWidgets = buildLabeledComboBox
		(
			"CACHING_POLICY_EXISTENCE_CHECKING_CHOOSER",
			buildExistenceCheckingComboBoxModel(),
            buildToplinkOptionRenderer()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 5;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, offset.left, 0, offset.right);

		add(existenceCheckingWidgets, constraints);
		addHelpTopicId(existenceCheckingWidgets, "project.defaults.existenceChecking");


		addHelpTopicId(this, "project.defaults.caching");
	}
    
    private ListCellRenderer buildToplinkOptionRenderer() {
        return new SimpleListCellRenderer() {
            protected String buildText(Object value) {
                return resourceRepository().getString(((TopLinkOption) value).resourceKey());
            }
        };
    }
    
    private PropertyValueModel buildCacheExpiryHolder() {
        return new PropertyAspectAdapter(getSubjectHolder()) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getCacheExpiry();
            }
        };
    }
}
