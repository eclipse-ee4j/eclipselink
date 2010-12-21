/*
 * @(#)RdbmsOptionsPropertiesPage.java
 *
 * Copyright 2004 by Oracle Corporation,
 * 500 Oracle Parkway, Redwood Shores, California, 94065, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Oracle Corporation.
 */
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.XMLLoginAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;

/**
 * This page shows the Advanced Options for xml login.
 * <p>
 * 
 * Known containers of this page:<br>
 * - {@link XMLOptionsPropertiesPage}
 *
 * @see XMLLoginAdapter
 *
 * @version 11.1.1
 * @author Les Davis
 */
public class XMLOptionsPropertiesPage extends AbstractLoginPropertiesPage
{
		
	/**
	 * Creates a new <code>RdbmsOptionsPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of <code>SessionNode</code>
	 */
	public XMLOptionsPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}
	
	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	@Override
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Create Datasource Platform widgets
		JComponent datasourcePlatformWidgets = buildLabeledComboBox("LOGIN_DATASOURCE_PLATFORM", buildDatasourcePlatformChooserAdater());

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(10, 0, 0, 0);

		panel.add(datasourcePlatformWidgets, constraints);
		addHelpTopicId(datasourcePlatformWidgets, "session.login.xml.options.datasource");

		// Create the Save username check box
		JCheckBox saveUsernameCheckBox = buildSaveUsernameCheckBox();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(saveUsernameCheckBox, constraints);

		// Username widgets
		Component usernameWidgets = buildUserNameWidgets();

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(usernameWidgets, constraints);
		
		new ComponentEnabler(buildSaveUsernameHolder(), usernameWidgets);

		// Create the Save Password check box
		JCheckBox savePasswordCheckBox = buildSavePasswordCheckBox();

		constraints.gridx      = 0;
		constraints.gridy	   = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(savePasswordCheckBox, constraints);

		// Password widgets
		Component passwordWidgets = buildPasswordWidgets();

		constraints.gridx       = 0;
		constraints.gridy       = 4;
		constraints.gridwidth   = 3;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(passwordWidgets, constraints);
		
		new ComponentEnabler(buildSavePasswordHolder(), passwordWidgets);
		
		// Create Equal Namespace Resolvers panel
		JComponent equalNamespaceResolversWidgets = buildCheckBox("LOGIN_EQUAL_NAMESPACE_RESOLVERS", buildEqualNamespaceResolversCheckBoxAdapter());

		constraints.gridx       = 0;
		constraints.gridy       = 5;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(equalNamespaceResolversWidgets, constraints);
		addHelpTopicId(equalNamespaceResolversWidgets, "session.login.xml.options.equalNamespaceResolvers");

		// Create Document Preservation Policy
		JComponent documentPreservationPolicyWidgets = buildLabeledComboBox(
				"LOGIN_DOCUMENT_PRESERVATION_POLICY", 
				buildDocumentPreservationPolicyComboboxAdapter());

		constraints.gridx       = 0;
		constraints.gridy       = 6;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(documentPreservationPolicyWidgets, constraints);
		addHelpTopicId(documentPreservationPolicyWidgets, "session.login.xml.options.documentPreservationPolicy");
		
		// Create Node Ordering Policy
		JComponent nodeOrderingWidgets = buildLabeledComboBox(
				"LOGIN_NODE_ORDERING_POLICY", 
				buildNodeOrderingPolicyComboboxAdapter());

		constraints.gridx       = 0;
		constraints.gridy       = 7;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(nodeOrderingWidgets, constraints);
		addHelpTopicId(nodeOrderingWidgets, "session.login.xml.options.nodeOrderingPolicy");

		addHelpTopicId(panel, "session.login.xml.options");
		return panel;
	}
		
	// ********** Save Password **********
	
	protected JCheckBox buildSavePasswordCheckBox() {
		return buildCheckBox("SAVE_PASSWORD_CHECK_BOX", buildSavePasswordCheckBoxModelAdapter());
	}
	
	private ButtonModel buildSavePasswordCheckBoxModelAdapter() {
		return new CheckBoxModelAdapter(buildSavePasswordPropertyAdapter());
	}

	private PropertyValueModel buildSavePasswordPropertyAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), LoginAdapter.SAVE_PASSWORD_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((LoginAdapter) this.subject).isSavePassword());
			}	
			@Override
			protected void setValueOnSubject(Object value) {
				((LoginAdapter) this.subject).setSavePassword(((Boolean) value).booleanValue());
			}
		};
	}
	
	protected PropertyValueModel buildSavePasswordHolder() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), LoginAdapter.SAVE_PASSWORD_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((LoginAdapter)this.subject).isSavePassword());
			}
		};
	}
	
	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Password value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildPasswordDocumentAdapter()
	{
		return new DocumentAdapter(buildPasswordHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Password property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPasswordHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), LoginAdapter.PASSWORD_PROPERTY)
		{
			@Override
			protected Object getValueFromSubject()
			{
				LoginAdapter adapter = (LoginAdapter) subject;
				return adapter.getPassword();
			}

			@Override
			protected void setValueOnSubject(Object value)
			{
				LoginAdapter adapter = (LoginAdapter) subject;
				adapter.setPassword((String) value);
			}
		};
	}

	/**
	 * Creates the Password label and text field.
	 *
	 * @return The fully initialized widgets
	 */
	protected Component buildPasswordWidgets()
	{
		JPasswordField passwordField = new JPasswordField(buildPasswordDocumentAdapter(), null, 1);
		JComponent pane = buildLabeledComponent("CONNECTION_PASSWORD_FIELD", passwordField);
		JLabel label = (JLabel) pane.getComponent(0);
		label.setBorder(BorderFactory.createEmptyBorder(0, SwingTools.checkBoxIconWidth(), 0, 0));
		return pane;
	}

	// ********** Save Username **********
	
	protected JCheckBox buildSaveUsernameCheckBox() {
		return buildCheckBox("SAVE_USERNAME_CHECK_BOX", buildSaveUsernameCheckBoxModelAdapter());
	}
	
	private ButtonModel buildSaveUsernameCheckBoxModelAdapter() {
		return new CheckBoxModelAdapter(buildSaveUsernamePropertyAdapter());
	}

	private PropertyValueModel buildSaveUsernamePropertyAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), LoginAdapter.SAVE_USERNAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((LoginAdapter) this.subject).isSaveUsername());
			}	
			@Override
			protected void setValueOnSubject(Object value) {
				((LoginAdapter) this.subject).setSaveUsername(((Boolean) value).booleanValue());
			}
		};
	}

	protected PropertyValueModel buildSaveUsernameHolder() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), LoginAdapter.SAVE_USERNAME_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((LoginAdapter)this.subject).isSaveUsername());
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Username value in the model and vice
	 * versa.
	 *
	 * @return A new <code>DocumentAdapter</code>
	 */
	private Document buildUsernameAdapter()
	{
		return new DocumentAdapter(buildUsernameHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Username property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildUsernameHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), LoginAdapter.USER_NAME_PROPERTY)
		{
			@Override
			protected Object getValueFromSubject()
			{
				LoginAdapter adapter = (LoginAdapter) subject;
				return adapter.getUserName();
			}

			@Override
			protected void setValueOnSubject(Object value)
			{
				LoginAdapter adapter = (LoginAdapter) subject;
				adapter.setUserName((String) value);
			}
		};
	}

	/**
	 * Creates the Username label and text field.
	 *
	 * @return The fully initialized widgets
	 */
	protected Component buildUserNameWidgets()
	{
		JComponent pane = buildLabeledTextField("CONNECTION_USER_NAME_FIELD", buildUsernameAdapter());
		JLabel label = (JLabel) pane.getComponent(0);
		label.setBorder(BorderFactory.createEmptyBorder(0, SwingTools.checkBoxIconWidth(), 0, 0));
		return pane;
	}

	private ComboBoxModelAdapter buildDocumentPreservationPolicyComboboxAdapter() {
		return new ComboBoxModelAdapter(buildDocumentPreservationCollectionHolder(), buildDocumentPreservationPolicySelectionHolder());
	}
	
	private CollectionValueModel buildDocumentPreservationCollectionHolder() {
		return new CollectionAspectAdapter(getSelectionHolder()) {
			@Override
			protected Iterator getValueFromSubject() {
				return ((XMLLoginAdapter)subject).getDocumentPreservationPolicyOptions().iterator();
			}
		};
	}

	private PropertyValueModel buildDocumentPreservationPolicySelectionHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), XMLLoginAdapter.DOCUMENT_PRESERVATION_POLICY_PROPERTY) {
			
			@Override
			protected Object getValueFromSubject() {
				return ((XMLLoginAdapter)subject).getDocumentPreservationPolicyType();
			}
			
			@Override
			protected void setValueOnSubject(Object value) {
				((XMLLoginAdapter)subject).setDocumentPreservationPolicy((String)value);
			}
		};
	}

	private ComboBoxModelAdapter buildNodeOrderingPolicyComboboxAdapter() {
		return new ComboBoxModelAdapter(buildNodeOrderingCollectionHolder(), buildNodeOrderingPolicySelectionHolder());
	}
	
	private CollectionValueModel buildNodeOrderingCollectionHolder() {
		return new CollectionAspectAdapter(getSelectionHolder()) {
			@Override
			protected Iterator getValueFromSubject() {
				return ((XMLLoginAdapter)subject).getNodeOrderingPolicyOptions().iterator();
			}
		};
	}
	
	private PropertyValueModel buildNodeOrderingPolicySelectionHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), XMLLoginAdapter.NODE_ORDERING_POLICY_PROPERTY) {
			
			@Override
			protected Object getValueFromSubject() {
				return ((XMLLoginAdapter)subject).getNodeOrderingPolicyType();
			}
			
			@Override
			protected void setValueOnSubject(Object value) {
				((XMLLoginAdapter)subject).setNodeOrderingPolicy((String)value);
			}
		};
	}

	private CheckBoxModelAdapter buildEqualNamespaceResolversCheckBoxAdapter() {
		return new CheckBoxModelAdapter(buildEqualNamespaceResolversValueModel());
	}
	
	private PropertyValueModel buildEqualNamespaceResolversValueModel() {
		return new PropertyAspectAdapter(getSelectionHolder(), XMLLoginAdapter.EQUAL_NAMESPACE_RESOLVERS_PROPERTY) {
			
			@Override
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((XMLLoginAdapter)subject).isEqualNamespaceResolvers());
			}
			
			@Override
			protected void setValueOnSubject(Object value) {
				((XMLLoginAdapter)subject).setEqualNamespaceResolvers(((Boolean)value).booleanValue());
			}
			
		};
	}
	
	private ComboBoxModel buildDatasourcePlatformChooserAdater() {
		return new ComboBoxModelAdapter(buildDatasourcePlatformCollectionHolder(), buildDatasourceSelectionHolder());
	}
	
	private CollectionValueModel buildDatasourcePlatformCollectionHolder() {
		return new CollectionAspectAdapter(getSelectionHolder()) {
			@Override
			protected Iterator getValueFromSubject() {
				return ((XMLLoginAdapter)subject).getPlatformClassOptions().iterator();
			}
		};
	}
	
	private PropertyValueModel buildDatasourceSelectionHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), XMLLoginAdapter.PLATFORM_CLASS_PROPERTY) {
			
			@Override
			protected Object getValueFromSubject() {
				return ((XMLLoginAdapter)subject).getPlatformClass();
			}
			
			@Override
			protected void setValueOnSubject(Object value) {
				((XMLLoginAdapter)subject).setPlatformClass((String)value);
			}
		};
	}
		
	private XMLLoginAdapter login() {
		return ((XMLLoginAdapter)selection());
	}

}