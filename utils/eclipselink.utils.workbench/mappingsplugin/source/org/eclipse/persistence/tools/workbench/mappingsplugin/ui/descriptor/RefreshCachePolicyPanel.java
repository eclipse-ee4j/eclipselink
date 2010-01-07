/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWRefreshCachePolicy;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;


final class RefreshCachePolicyPanel extends AbstractPanel {
	
	private PropertyValueModel refreshCachePolicyHolder;	
	
	RefreshCachePolicyPanel(ApplicationContext context, PropertyValueModel refreshCachePolicyHolder) {
		super(context);
		this.refreshCachePolicyHolder = refreshCachePolicyHolder;
		initializeLayout();
	}
	
	private void initializeLayout() {
		setBorder(BorderFactory.createCompoundBorder(
			buildTitledBorder("REFRESH_CACHE_POLICY_PANEL_BORDER_TITLE"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));
		
		GridBagConstraints constraints = new GridBagConstraints();


		JCheckBox alwaysRefreshCacheCheckBox = buildAlwaysRefreshCacheCheckBox();
	
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(alwaysRefreshCacheCheckBox, constraints);
		
		JCheckBox onlyRefreshCacheIfNewerVersionCheckBox = buildOnlyRefreshCacheIfNewerVersionCheckBox();
	
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(onlyRefreshCacheIfNewerVersionCheckBox, constraints);
		
		JCheckBox shouldDisableCacheHits = buildDisableCacheHitsCheckBox();
		
		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(shouldDisableCacheHits, constraints);

		addHelpTopicId(this, helpTopicId());
	}

	private String helpTopicId() {
		return "descriptor.transactional.refreshCache";
	}
	//********** Always Refresh Cache  *********
	
	private JCheckBox buildAlwaysRefreshCacheCheckBox() {
		JCheckBox checkBox = buildCheckBox("REFRESH_CACHE_POLICY_PANEL_ALWAYS_REFRESH_CACHE_CHECK_BOX", buildAlwaysRefreshCacheCheckBoxModel());
		addHelpTopicId(checkBox, helpTopicId() + "alwaysRefresh");
		return checkBox;
	}
	
	private ButtonModel buildAlwaysRefreshCacheCheckBoxModel() {
		return new CheckBoxModelAdapter(buildAlwaysRefreshCacheAdapter());
	}
	
	private PropertyValueModel buildAlwaysRefreshCacheAdapter() {
		return new PropertyAspectAdapter(refreshCachePolicyHolder, MWRefreshCachePolicy.ALWAYS_REFRESH_CACHE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWRefreshCachePolicy) subject).isAlwaysRefreshCache());
			}

			protected void setValueOnSubject(Object value) {
				((MWRefreshCachePolicy) subject).setAlwaysRefreshCache(((Boolean) value).booleanValue());
			}
		};
	}

	//********** Always Refresh Cache  *********
	
	private JCheckBox buildOnlyRefreshCacheIfNewerVersionCheckBox() {
		JCheckBox checkBox = buildCheckBox("REFRESH_CACHE_POLICY_PANEL_ONLY_REFRESH_IF_NEWER_VERSION_CHECK_BOX", buildOnlyRefreshCacheIfeCheckBoxModel());
		addHelpTopicId(checkBox, helpTopicId() + "onlyRefreshIfNewerVersion");
		return checkBox;
	}
	
	private ButtonModel buildOnlyRefreshCacheIfeCheckBoxModel() {
		return new CheckBoxModelAdapter(buildOnlyRefreshCacheIfNewerVersionAdapter());
	}
	
	private PropertyValueModel buildOnlyRefreshCacheIfNewerVersionAdapter() {
		return new PropertyAspectAdapter(refreshCachePolicyHolder, MWRefreshCachePolicy.ONLY_REFRESH_IF_NEWER_VERSION_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWRefreshCachePolicy) subject).isOnlyRefreshCacheIfNewerVersion());
			}

			protected void setValueOnSubject(Object value) {
				((MWRefreshCachePolicy) subject).setOnlyRefreshCacheIfNewerVersion(((Boolean) value).booleanValue());
			}
		};
	}

	//********** Disable Cache Hits  *********
	
	private JCheckBox buildDisableCacheHitsCheckBox() {
		JCheckBox checkBox = buildCheckBox("REFRESH_CACHE_POLICY_PANEL_SHOULD_DISABLE_CACHE_HITS_CHECK_BOX", buildDisableCacheHitsCheckBoxModel());
		addHelpTopicId(checkBox, helpTopicId() + "disableCacheHits");
		return checkBox;
	}
	
	private ButtonModel buildDisableCacheHitsCheckBoxModel() {
		return new CheckBoxModelAdapter(buildDisableCacheHitsAdapter());
	}
	
	private PropertyValueModel buildDisableCacheHitsAdapter() {
		return new PropertyAspectAdapter(refreshCachePolicyHolder, MWRefreshCachePolicy.DISABLE_CACHE_HITS_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWRefreshCachePolicy) subject).isDisableCacheHits());
			}

			protected void setValueOnSubject(Object value) {
				((MWRefreshCachePolicy) subject).setDisableCacheHits(((Boolean) value).booleanValue());
			}
		};
	}
}
