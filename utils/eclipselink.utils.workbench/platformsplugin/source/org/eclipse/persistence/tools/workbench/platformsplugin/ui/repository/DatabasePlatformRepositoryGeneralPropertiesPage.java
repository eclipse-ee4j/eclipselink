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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;


/**
 * 
 */
final class DatabasePlatformRepositoryGeneralPropertiesPage extends ScrollablePropertiesPage {

	public DatabasePlatformRepositoryGeneralPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// file name
		JLabel fileNameLabel = new JLabel(this.resourceRepository().getString("DATABASE_PLATFORM_REPOSITORY_FILE_TEXT_FIELD"));
		fileNameLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("DATABASE_PLATFORM_REPOSITORY_FILE_TEXT_FIELD"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(fileNameLabel, constraints);

		JTextField fileNameTextField = this.buildFileNameTextField();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(fileNameTextField, constraints);

		fileNameLabel.setLabelFor(fileNameTextField);

		// default platform
		JLabel defaultPlatformLabel = new JLabel(this.resourceRepository().getString("DATABASE_PLATFORM_REPOSITORY_DEFAULT_PLATFORM_COMBO_BOX"));
		defaultPlatformLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("DATABASE_PLATFORM_REPOSITORY_DEFAULT_PLATFORM_COMBO_BOX"));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(defaultPlatformLabel, constraints);

		JComboBox defaultPlatformComboBox = this.buildDefaultPlatformComboBox();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(defaultPlatformComboBox, constraints);

		defaultPlatformLabel.setLabelFor(defaultPlatformComboBox);

		// comment		
		JLabel commentLabel = new JLabel(this.resourceRepository().getString("DATABASE_PLATFORM_REPOSITORY_COMMENT_TEXT_FIELD"));
		commentLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("DATABASE_PLATFORM_REPOSITORY_COMMENT_TEXT_FIELD"));
		constraints.gridx = 0;
		constraints.gridy = 2;
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
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(commentTextField, constraints);

		commentLabel.setLabelFor(commentTextField);

		// bottom filler
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(new JPanel(), constraints);

		return panel;
	}


	// ********** file name **********

	private JTextField buildFileNameTextField() {
		JTextField textField = new JTextField();
		textField.setDocument(this.buildFileNameDocumentAdapter());
		return textField;
	}

	private Document buildFileNameDocumentAdapter() {
		return new DocumentAdapter(this.buildFileNameAdapter());
	}

	private PropertyValueModel buildFileNameAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabasePlatformRepository.FILE_PROPERTY) {
			protected Object getValueFromSubject() {
				File file = ((DatabasePlatformRepository) this.subject).getFile();
				return (file == null) ? null : file.getAbsolutePath();
			}
			protected void setValueOnSubject(Object value) {
				((DatabasePlatformRepository) this.subject).setFile(new File((String) value));
			}
		};
	}


	// ********** default platform **********

	private JComboBox buildDefaultPlatformComboBox() {
		JComboBox comboBox = new JComboBox(new ComboBoxModelAdapter(this.buildSortedPlatformsHolder(), this.buildDefaultPlatformAdapter()));
		comboBox.setRenderer(this.buildPlatformRenderer());
		return comboBox;
	}

	private ListCellRenderer buildPlatformRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				// need null check for combo-box
				return (value == null) ? "" : ((DatabasePlatform) value).getName();
			}
		};
	}
	
	private ListValueModel buildSortedPlatformsHolder() {
		return new SortedListValueModelAdapter(buildAllPlatformsAdapter());
	}

	private CollectionValueModel buildAllPlatformsAdapter() {
		return new CollectionAspectAdapter(this.getSelectionHolder()) {
			protected Iterator getValueFromSubject() {
				return ((DatabasePlatformRepository) this.subject).platforms();
			}
			protected int sizeFromSubject() {
				return ((DatabasePlatformRepository) this.subject).platformsSize();
			}
		};
	}

	private PropertyValueModel buildDefaultPlatformAdapter() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), DatabasePlatformRepository.DEFAULT_PLATFORM_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((DatabasePlatformRepository) this.subject).getDefaultPlatform();
			}	
			protected void setValueOnSubject(Object value) {
				((DatabasePlatformRepository) this.subject).setDefaultPlatform((DatabasePlatform) value);
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
				return ((DatabasePlatformRepository) this.subject).getComment();
			}
			protected void setValueOnSubject(Object value) {
				((DatabasePlatformRepository) this.subject).setComment((String) value);
			}
		};
	}

}
