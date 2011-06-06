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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.uitools.RecentFilesManager;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


/**
 * Preferences page for general (non-plug-in-specific) settings used by
 * the framework.
 */
final class GeneralPreferencesPage extends AbstractPanel {

	private List lafInfos;
	private static final long serialVersionUID = 1L;

	GeneralPreferencesPage(PreferencesContext context) {
		super(new BorderLayout(), context);
		buildLookAndFeelList();
		this.intializeLayout();
	}

	private void buildLookAndFeelList() {
		this.lafInfos = new ArrayList();
		CollectionTools.addAll(this.lafInfos, UIManager.getInstalledLookAndFeels());
		this.lafInfos.add(new UIManager.LookAndFeelInfo("Oracle", "oracle.bali.ewt.olaf2.OracleLookAndFeel"));
		Collections.sort(this.lafInfos, this.buildLookAndFeelComparator());
	}

	private void intializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel scrollPaneView = new JPanel(new GridBagLayout());

		JScrollPane scrollPane = new JScrollPane(scrollPaneView);
		scrollPane.getVerticalScrollBar().setBlockIncrement(20);
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		JPanel container = new JPanel(new GridBagLayout());
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 5, 5, 5);
		scrollPaneView.add(container, constraints);

		// splash screen
		JCheckBox splashScreenCheckBox = this.buildCheckBox("PREFERENCES.GENERAL.DISPLAY_SPLASH_SCREEN", this.buildDisplaySplashScreenModel());
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		container.add(splashScreenCheckBox, constraints);

		// look and feel		
		JComponent lookAndFeelComboBox = this.buildLabeledComboBox("PREFERENCES.GENERAL.LOOK_AND_FEEL", buildLookAndFeelComboBoxModel(), buildLookAndFeelListCellRenderer());
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 5);
		container.add(lookAndFeelComboBox, constraints);

		// recent files size
		JComponent recentFilesSizeSpinner = this.buildRecentFilesSizeSpinner();
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 5);
		container.add(recentFilesSizeSpinner, constraints);

		// proxy host
		JComponent proxyHostTextField = this.buildLabeledTextField("PREFERENCES.GENERAL.HTTP.PROXY.HOST", this.buildProxyHostDocumentAdapter());
		constraints.gridx		= 0;
		constraints.gridy		= 3;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 5);
		container.add(proxyHostTextField, constraints);
		
		// proxy port
		JComponent proxyPortTextField = this.buildLabeledTextField("PREFERENCES.GENERAL.HTTP.PROXY.PORT", this.buildProxyPortDocumentAdapter());
		constraints.gridx		= 0;
		constraints.gridy		= 4;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 5);
		container.add(proxyPortTextField, constraints);
		
		// network connection timeout
		JComponent networkConnectTimeoutTextField = this.buildLabeledTextField("PREFERENCES.GENERAL.NETWORK.CONNECT_TIMEOUT", this.buildNetworkConnectTimeoutDocumentAdapter());
		constraints.gridx		= 0;
		constraints.gridy		= 5;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 0, 5);
		container.add(networkConnectTimeoutTextField, constraints);
		
		// network read timeout
		JComponent networkReadTimeoutTextField = this.buildLabeledTextField("PREFERENCES.GENERAL.NETWORK.READ_TIMEOUT", this.buildNetworkReadTimeoutDocumentAdapter());
		constraints.gridx		= 0;
		constraints.gridy		= 6;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 5, 5);
		container.add(networkReadTimeoutTextField, constraints);
		
		// reopen projects
		JCheckBox reopenCheckBox = this.buildCheckBox("PREFERENCES.GENERAL.REOPEN_PROJECTS", this.buildReopenProjectsModel());
		constraints.gridx		= 0;
		constraints.gridy		= 7;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);
		container.add(reopenCheckBox, constraints);


		
        addHelpTopicId(this, "preferences.general");
		
	}
	
	// ***** display splash screen check box
	private ButtonModel buildDisplaySplashScreenModel() {
		return new CheckBoxModelAdapter(this.buildBufferedDisplaySplashScreenAdapter());
	}

	private PropertyValueModel buildBufferedDisplaySplashScreenAdapter() {
		return new BufferedPropertyValueModel(this.buildDisplaySplashScreenAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildDisplaySplashScreenAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(this.preferences(), FrameworkApplication.DISPLAY_SPLASH_SCREEN_PREFERENCE, FrameworkApplication.DISPLAY_SPLASH_SCREEN_PREFERENCE_DEFAULT);
		adapter.setConverter(BidiStringConverter.BOOLEAN_CONVERTER);
		return adapter;
	}


	// ***** look and feel combo box
	private ComboBoxModel buildLookAndFeelComboBoxModel() {
		return new ComboBoxModelAdapter(buildLookAndFeelCollectionHolder(), buildBufferedLookAndFeelAdapter()); 	
	}
	
	private CollectionValueModel buildLookAndFeelCollectionHolder() {
		return new ReadOnlyCollectionValueModel(this.lafInfos);
	}

	private Comparator buildLookAndFeelComparator() {
		return new Comparator() {
			public int compare(Object object1, Object object2) {
				UIManager.LookAndFeelInfo lafInfo1 = (UIManager.LookAndFeelInfo) object1;
				UIManager.LookAndFeelInfo lafInfo2 = (UIManager.LookAndFeelInfo) object2;
				return Collator.getInstance().compare(lafInfo1.getName(), lafInfo2.getName());
			}
		};
	}

	private PropertyValueModel buildBufferedLookAndFeelAdapter() {
		BufferedPropertyValueModel adapter = new BufferedPropertyValueModel(this.buildLookAndFeelAdapter(), this.getPreferencesContext().getBufferTrigger());
		return new TransformationPropertyValueModel(adapter) {
			protected Object reverseTransform(Object value) {
				UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo) value;
				return info.getClassName();
			}
			protected Object transform(Object value) {
				String className = (String) value;
				return GeneralPreferencesPage.this.getLookAndFeelInfo(className);
			}
		};
	}

	private PropertyValueModel buildLookAndFeelAdapter() {
		String lafClassName = UIManager.getLookAndFeel().getClass().getName();
		return new PreferencePropertyValueModel(this.preferences(), FrameworkApplication.LOOK_AND_FEEL_PREFERENCE, lafClassName);
	}	
	
	private ListCellRenderer buildLookAndFeelListCellRenderer() {
		return new SimpleListCellRenderer() {
			private static final long serialVersionUID = 1L;
			protected String buildText(Object value) {
				UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo) value;
				// This null check is required when the user imports preferences from
				// another OS and the selected LaF is not supported
				return (info == null) ? null : info.getName();
			}
		};
	}
	
	private JComponent buildRecentFilesSizeSpinner() {
		JComponent component = 
			this.buildLabeledSpinnerNumber("PREFERENCES.GENERAL.RECENT_FILES_SIZE", this.buildRecentFilesSizeSpinnerModel());
		JSpinner spinner = (JSpinner) component.getComponent(1);	
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
		return component;
	}
	
	private SpinnerNumberModel buildRecentFilesSizeSpinnerModel() {
		return new NumberSpinnerModelAdapter(
			this.buildBufferedRecentFilesSizeAdapter(),
			0,									// minimum
			RecentFilesManager.MAX_MAX_SIZE,	// maximum
			1,									// step size
			RecentFilesManager.DEFAULT_MAX_SIZE	// default
		);
	}
	
	private PropertyValueModel buildBufferedRecentFilesSizeAdapter() {
		return new BufferedPropertyValueModel(this.buildRecentFilesSizeAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildRecentFilesSizeAdapter() {
		PreferencePropertyValueModel adapter = 
			new PreferencePropertyValueModel(
				this.preferences(), 
				FrameworkNodeManager.RECENT_FILES_MAX_SIZE_PREFERENCE, 
				FrameworkNodeManager.RECENT_FILES_MAX_SIZE_PREFERENCE_DEFAULT
			);
		adapter.setConverter(this.buildRecentFilesSizeConverter());
		return adapter;
	}
	
	private BidiStringConverter buildRecentFilesSizeConverter() {
		return new BidiStringConverter() {
			public String convertToString(Object o) {
				return (o == null) ? null : ((Integer) o).toString();
			}
			
			public Object convertToObject(String s) {
				if (s == null) {
					return new Integer(RecentFilesManager.DEFAULT_MAX_SIZE);
				}
				Integer i = new Integer(s);
				if (i.intValue() < 0 || i.intValue() > RecentFilesManager.MAX_MAX_SIZE) {
					return new Integer(RecentFilesManager.DEFAULT_MAX_SIZE);
				}
				return i;
			}
			
			public String toString() {
				return "RecentFilesManager-IntegerStringConverter";
			}
		};
	}

	// ***** http proxy host text field
	private Document buildProxyHostDocumentAdapter() {
		return new DocumentAdapter(this.buildBufferedProxyHostAdapter());
	}

	private PropertyValueModel buildBufferedProxyHostAdapter() {
		return new BufferedPropertyValueModel(this.buildProxyHostAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildProxyHostAdapter() {
		return new PreferencePropertyValueModel(this.preferences(), FrameworkApplication.HTTP_PROXY_HOST_PREFERENCE, FrameworkApplication.HTTP_PROXY_HOST_PREFERENCE_DEFAULT);
	}

	// ***** http proxy port text field
	private Document buildProxyPortDocumentAdapter() {
		// TODO must be numeric (range?)
		// TODO disable when there is no http proxy host specified
		return new DocumentAdapter(this.buildBufferedProxyPortAdapter());
	}

	private PropertyValueModel buildBufferedProxyPortAdapter() {
		return new BufferedPropertyValueModel(this.buildProxyPortAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildProxyPortAdapter() {
		return new PreferencePropertyValueModel(this.preferences(), FrameworkApplication.HTTP_PROXY_PORT_PREFERENCE, FrameworkApplication.HTTP_PROXY_PORT_PREFERENCE_DEFAULT);
	}

	// ***** network connect timeout text field
	private Document buildNetworkConnectTimeoutDocumentAdapter() {
		// TODO must be numeric (range?)
		return new DocumentAdapter(this.buildBufferedNetworkConnectTimeoutAdapter());
	}

	private PropertyValueModel buildBufferedNetworkConnectTimeoutAdapter() {
		return new BufferedPropertyValueModel(this.buildNetworkConnectTimeoutAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildNetworkConnectTimeoutAdapter() {
		return new PreferencePropertyValueModel(this.preferences(), FrameworkApplication.NETWORK_CONNECT_TIMEOUT_PREFERENCE, FrameworkApplication.NETWORK_CONNECT_TIMEOUT_PREFERENCE_DEFAULT);
	}

	// ***** network read timeout text field
	private Document buildNetworkReadTimeoutDocumentAdapter() {
		// TODO must be numeric (range?)
		return new DocumentAdapter(this.buildBufferedNetworkReadTimeoutAdapter());
	}

	private PropertyValueModel buildBufferedNetworkReadTimeoutAdapter() {
		return new BufferedPropertyValueModel(this.buildNetworkReadTimeoutAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildNetworkReadTimeoutAdapter() {
		return new PreferencePropertyValueModel(this.preferences(), FrameworkApplication.NETWORK_READ_TIMEOUT_PREFERENCE, FrameworkApplication.NETWORK_READ_TIMEOUT_PREFERENCE_DEFAULT);
	}

	// ***** reopen projects check box
	private ButtonModel buildReopenProjectsModel() {
		return new CheckBoxModelAdapter(this.buildBufferedReopenProjectsAdapter());
	}

	private PropertyValueModel buildBufferedReopenProjectsAdapter() {
		return new BufferedPropertyValueModel(this.buildReopenProjectsAdapter(), this.getPreferencesContext().getBufferTrigger());
	}

	private PropertyValueModel buildReopenProjectsAdapter() {
		PreferencePropertyValueModel adapter = new PreferencePropertyValueModel(this.preferences(), FrameworkApplication.REOPEN_PROJECTS_PREFERENCE, FrameworkApplication.REOPEN_PROJECTS_PREFERENCE_DEFAULT);
		adapter.setConverter(BidiStringConverter.BOOLEAN_CONVERTER);
		return adapter;
	}

	// **** Look and Feel
	UIManager.LookAndFeelInfo getLookAndFeelInfo(String className) {
		for (Iterator stream = this.lafInfos.iterator(); stream.hasNext(); ) {
			UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo) stream.next();
			if (info.getClassName().equals(className)) {
				return info;
			}
		}
		return null;
	}

}
