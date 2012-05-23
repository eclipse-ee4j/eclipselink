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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.platform;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;


/**
 * this is the General Properties tab on the
 * database platform tabbed properties page;
 * nothing special...
 */
final class DatabasePlatformGeneralPropertiesPage extends ScrollablePropertiesPage {


	public DatabasePlatformGeneralPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// short file name
		JLabel shortFileNameLabel = new JLabel(this.resourceRepository().getString("DATABASE_PLATFORM_SHORT_FILE_NAME_TEXT_FIELD"));
		shortFileNameLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("DATABASE_PLATFORM_SHORT_FILE_NAME_TEXT_FIELD"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(shortFileNameLabel, constraints);

		JTextField shortFileNameTextField = this.buildShortFileNameTextField();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(shortFileNameTextField, constraints);

		shortFileNameLabel.setLabelFor(shortFileNameTextField);

		// run-time platform class name
		JLabel runtimePlatformClassNameLabel = new JLabel(this.resourceRepository().getString("DATABASE_PLATFORM_RUNTIME_PLATFORM_CLASS_NAME_TEXT_FIELD"));
		runtimePlatformClassNameLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("DATABASE_PLATFORM_RUNTIME_PLATFORM_CLASS_NAME_TEXT_FIELD"));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(runtimePlatformClassNameLabel, constraints);

		JTextField runtimePlatformClassNameTextField = this.buildRuntimePlatformClassNameTextField();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(runtimePlatformClassNameTextField, constraints);

		runtimePlatformClassNameLabel.setLabelFor(runtimePlatformClassNameTextField);

		// supports native sequencing
		JCheckBox supportsNativeSequencingCheckBox = this.buildSupportsNativeSequencingCheckBox();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(supportsNativeSequencingCheckBox, constraints);

		// supports native returning
		JCheckBox supportsNativeReturningCheckBox = this.buildSupportsNativeReturningCheckBox();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(supportsNativeReturningCheckBox, constraints);

		// supports IDENTITY clause
		JCheckBox supportsIdentityClauseCheckBox = this.buildSupportsIdentityClauseCheckBox();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(supportsIdentityClauseCheckBox, constraints);
		
		// comment		
		JLabel commentLabel = new JLabel(this.resourceRepository().getString("DATABASE_PLATFORM_COMMENT_TEXT_FIELD"));
		commentLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("DATABASE_PLATFORM_COMMENT_TEXT_FIELD"));
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(commentLabel, constraints);

		JTextField commentTextField = this.buildCommentTextField();
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(commentTextField, constraints);

		commentLabel.setLabelFor(commentTextField);

		// filler
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(new JPanel(), constraints);

		return panel;
	}


	// ********** short file name **********

	private JTextField buildShortFileNameTextField() {
		JTextField textField = new JTextField();
		textField.setDocument(this.buildShortFileNameDocumentAdapter());
		return textField;
	}

	private Document buildShortFileNameDocumentAdapter() {
		return new DocumentAdapter(this.buildShortFileNameAdapter());
	}

	private PropertyValueModel buildShortFileNameAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabasePlatform.SHORT_FILE_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((DatabasePlatform) this.subject).getShortFileName();
			}
			protected void setValueOnSubject(Object value) {
				((DatabasePlatform) this.subject).setShortFileName((String) value);
			}
		};
	}


	// ********** runtime platform class name **********

	private JTextField buildRuntimePlatformClassNameTextField() {
		JTextField textField = new JTextField();
		textField.setDocument(this.buildRuntimePlatformClassNameDocumentAdapter());
		return textField;
	}

	private Document buildRuntimePlatformClassNameDocumentAdapter() {
		return new DocumentAdapter(this.buildRuntimePlatformClassNameAdapter());
	}

	private PropertyValueModel buildRuntimePlatformClassNameAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabasePlatform.RUNTIME_PLATFORM_CLASS_NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((DatabasePlatform) this.subject).getRuntimePlatformClassName();
			}
			protected void setValueOnSubject(Object value) {
				((DatabasePlatform) this.subject).setRuntimePlatformClassName((String) value);
			}
		};
	}

	// ********** native returning **********
	
	private JCheckBox buildSupportsNativeReturningCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setModel(this.buildSupportsNativeReturningCheckBoxModel());
		checkBox.setText(this.resourceRepository().getString("DATABASE_PLATFORM_SUPPORTS_NATIVE_RETURNING_CHECK_BOX"));
		checkBox.setMnemonic(this.resourceRepository().getMnemonic("DATABASE_PLATFORM_SUPPORTS_NATIVE_RETURNING_CHECK_BOX"));
		return checkBox;
	}

	private ButtonModel buildSupportsNativeReturningCheckBoxModel() {
		return new CheckBoxModelAdapter(this.buildSupportsNativeReturningAdapter());
	}

	private PropertyValueModel buildSupportsNativeReturningAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabasePlatform.SUPPORTS_NATIVE_RETURNING_PROPERTY) {
			protected Object buildValue() {
				if (this.subject == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(((DatabasePlatform) this.subject).supportsNativeReturning());
			}
			protected void setValueOnSubject(Object value) {
				((DatabasePlatform) this.subject).setSupportsNativeReturning(((Boolean) value).booleanValue());
			}
		};
	}	

	// ********** native sequencing **********

	private JCheckBox buildSupportsNativeSequencingCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setModel(this.buildSupportsNativeSequencingCheckBoxModel());
		checkBox.setText(this.resourceRepository().getString("DATABASE_PLATFORM_SUPPORTS_NATIVE_SEQUENCING_CHECK_BOX"));
		checkBox.setMnemonic(this.resourceRepository().getMnemonic("DATABASE_PLATFORM_SUPPORTS_NATIVE_SEQUENCING_CHECK_BOX"));
		return checkBox;
	}

	private ButtonModel buildSupportsNativeSequencingCheckBoxModel() {
		return new CheckBoxModelAdapter(this.buildSupportsNativeSequencingAdapter());
	}

	private PropertyValueModel buildSupportsNativeSequencingAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabasePlatform.SUPPORTS_NATIVE_SEQUENCING_PROPERTY) {
			protected Object buildValue() {
				if (this.subject == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(((DatabasePlatform) this.subject).supportsNativeSequencing());
			}
			protected void setValueOnSubject(Object value) {
				((DatabasePlatform) this.subject).setSupportsNativeSequencing(((Boolean) value).booleanValue());
			}
		};
	}


	// ********** IDENTITY clause **********

	private JCheckBox buildSupportsIdentityClauseCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setModel(this.buildSupportsIdentityClauseCheckBoxModel());
		checkBox.setText(this.resourceRepository().getString("DATABASE_PLATFORM_SUPPORTS_IDENTITY_CLAUSE_CHECK_BOX"));
		checkBox.setMnemonic(this.resourceRepository().getMnemonic("DATABASE_PLATFORM_SUPPORTS_IDENTITY_CLAUSE_CHECK_BOX"));
		return checkBox;
	}

	private ButtonModel buildSupportsIdentityClauseCheckBoxModel() {
		return new CheckBoxModelAdapter(this.buildSupportsIdentityClauseAdapter());
	}

	private PropertyValueModel buildSupportsIdentityClauseAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabasePlatform.SUPPORTS_IDENTITY_CLAUSE_PROPERTY) {
			protected Object buildValue() {
				if (this.subject == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(((DatabasePlatform) this.subject).supportsIdentityClause());
			}
			protected void setValueOnSubject(Object value) {
				((DatabasePlatform) this.subject).setSupportsIdentityClause(((Boolean) value).booleanValue());
			}
		};
	}


	// ********** comment **********

	private JTextField buildCommentTextField() {
		JTextField textField = new JTextField();
		textField.setDocument(this.buildCommentDocumentAdapter());
		return textField;
	}

	private Document buildCommentDocumentAdapter() {
		return new DocumentAdapter(this.buildCommentAdapter());
	}

	private PropertyValueModel buildCommentAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), AbstractNodeModel.COMMENT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((DatabasePlatform) this.subject).getComment();
			}
			protected void setValueOnSubject(Object value) {
				((DatabasePlatform) this.subject).setComment((String) value);
			}
		};
	}

}
