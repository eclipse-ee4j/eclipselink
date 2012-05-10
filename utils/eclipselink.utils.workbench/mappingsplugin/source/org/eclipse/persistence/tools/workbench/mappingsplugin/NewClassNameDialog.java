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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * This dialog will prompt the user for a new class name and,
 * hopefully, disallow any invalid class names.
 */
public final class NewClassNameDialog 
	extends AbstractValidatingDialog
{
	private PropertyValueModel shortClassNameHolder;

	private JTextField shortClassNameTextField;

	/** The combo box showing all the existing packages in the project. */
	private JComboBox packageComboBox;

	/** The list of current package names. */
	protected final Collection packageNames;

	/** The initially selected package name */	
	private final String initialPackageName;

	/** The initially entered class name */	
	private final String initialShortClassName;

	/** If this is true the dialog will allow the name of a "stub" type to be entered */
	private boolean allowExistingType;

	private final MWProject project;


	// ********** constructors **********

	NewClassNameDialog(MWProject project, WorkbenchContext context) {
		this(Collections.EMPTY_SET, project, context);
	}

	NewClassNameDialog(Collection packageNames, MWProject project, WorkbenchContext context) {
		this(packageNames, "", project, context);
	}

	public NewClassNameDialog(Collection packageNames, String initialPackageName, MWProject project, WorkbenchContext context) {
		this(packageNames, initialPackageName, buildInitialShortClassName(initialPackageName, project, context), project, context);
	}

	public NewClassNameDialog(Collection packageNames, String initialPackageName, String initialShortClassName, MWProject project, WorkbenchContext context) {
		super(context);
		this.packageNames = packageNames;
		this.initialPackageName = initialPackageName;
		this.initialShortClassName = initialShortClassName;
		this.allowExistingType = true;
		this.project = project;
	}

	private static String buildInitialShortClassName(String packageName, MWProject project, WorkbenchContext context) {
		return NameTools.uniqueNameFor(
			context.getApplicationContext().getResourceRepository().getString("NEW_CLASS_NAME_DIALOG_INITIAL_CLASS_NAME"),
			descriptorNamesInPackage(packageName, project)
		);
	}

	private static Iterator descriptorNamesInPackage(String packageName, MWProject project) {
		Iterator descriptors = project.descriptorsInPackage(packageName);
		return new TransformationIterator(descriptors) {
			protected Object transform(Object next) {
				return ((MWDescriptor) next).shortName();
			}
		};
	}


	// ********** initialization **********

	protected String helpTopicId() {
		return "dialog.createNewClass";
	}

	protected void initialize() {
		super.initialize();
		this.shortClassNameHolder = new SimplePropertyValueModel("");
		this.setTitle(this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_TITLE"));
	}
	
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// package name label
		JLabel packageLabel = SwingComponentFactory.buildLabel("NEW_CLASS_NAME_DIALOG_PACKAGE_NAME_LABEL", resourceRepository());

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(packageLabel, constraints);
		
		// package name combo-box
		this.packageComboBox = this.buildPackageComboBox();

		constraints.gridx       = 1;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 5, 0, 0);

		panel.add(this.packageComboBox, constraints);
		packageLabel.setLabelFor(this.packageComboBox);

		// short class name label
		JLabel shortClassNameLabel = SwingComponentFactory.buildLabel("NEW_CLASS_NAME_DIALOG_CLASS_NAME_LABEL", resourceRepository());

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 0, 0, 0);

		panel.add(shortClassNameLabel, constraints);

		// short class name text field
		this.shortClassNameTextField = new JTextField(buildShortClassNameDocumentAdapter(), null, 40);

		constraints.gridx      = 1;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 5, 0, 0);

		panel.add(this.shortClassNameTextField, constraints);
		shortClassNameLabel.setLabelFor(this.shortClassNameTextField);
		
		return panel;
	}
	
	private JComboBox buildPackageComboBox() {
		JComboBox comboBox = new JComboBox();
		comboBox.setEditable(true);

		JTextField textField = this.packageComboBoxTextField(comboBox);
		textField.setDocument(new RegexpDocument(RegexpDocument.RE_PACKAGE));
		textField.getDocument().addDocumentListener(this.buildShortClassNameDocumentListener());

		comboBox.setModel(
			new ComboBoxModelAdapter(
				new ReadOnlyCollectionValueModel(this.packageNames),
				new SimplePropertyValueModel(this.initialPackageName)
			)
		);

		return comboBox;
	}

	private JTextField packageComboBoxTextField() {
		return this.packageComboBoxTextField(this.packageComboBox);
	}

	private JTextField packageComboBoxTextField(JComboBox comboBox) {
		return (JTextField) comboBox.getEditor().getEditorComponent();
	}

	protected Component initialFocusComponent() {
		return this.shortClassNameTextField;
	}

	private Document buildShortClassNameDocumentAdapter() {
		DocumentAdapter adapter = new DocumentAdapter(this.shortClassNameHolder, this.buildShortClassNameDocument());
		adapter.addDocumentListener(this.buildShortClassNameDocumentListener());
		return adapter;
	}
	
	private RegexpDocument buildShortClassNameDocument() {
		return new RegexpDocument(RegexpDocument.RE_CLASS);
	}

	private DocumentListener buildShortClassNameDocumentListener() {
		return new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				this.validateClassName();
			}
			public void insertUpdate(DocumentEvent e) {
				this.validateClassName();
			}
			public void removeUpdate(DocumentEvent e) {
				this.validateClassName();
			}
			private void validateClassName() {
				if (NewClassNameDialog.this.isVisible()) {
					NewClassNameDialog.this.validateClassName();
				}
			}
		};
	}

	protected void prepareToShow() {
		super.prepareToShow();
		this.shortClassNameHolder.setValue(this.initialShortClassName);
		this.shortClassNameTextField.selectAll();
		this.validateClassName();
	}


	// ********** public API **********

	/**
	 * Set whether the dialog will allow the user to enter the name
	 * of an existing type. An existing type is OK for a new class/descriptor;
	 * but it's not allowed for a renamed class/descriptor.
	 */
	public void setAllowExistingType(boolean allowExistingType) {
		this.allowExistingType = allowExistingType;
	}

	public String packageName() {
		String packageName = this.packageComboBoxTextField().getText().trim();
		if (packageName.endsWith(".")) {
			packageName = packageName.substring(0, packageName.length() - 1);
		}
		return packageName;
	}

	public String shortClassName() {
		return this.shortClassNameTextField.getText().trim();
	}

	public String className() {
		String packageName = this.packageName();
		String className = this.shortClassName();
		return (packageName.length() == 0) ? className : packageName + '.' + className;
	}


	// ********** internal methods **********
	
	void validateClassName() {
		String shortClassName = this.shortClassName();
		String className = this.className();
		String classNameToLowerCase = className.toLowerCase();
		String message = null;

		if (shortClassName.length() == 0) {
			// no short class name specified
			message = this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_NO_CLASS_NAME_SPECIFIED");
		} else if (NameTools.javaReservedWordsContains(shortClassName)) {
			// the class name cannot be a Java reserved word
			message = this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_CLASS_NAME_INVALID", shortClassName);
		} else if (MWClassRepository.coreClassNamesContains(className)) {
			// "core" classes are reserved
			message = this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_CLASS_NAME_RESERVED", className);
		} else {
			String collisionCoreTypeName = MWClassRepository.coreClassNameIgnoreCase(className);
			if (collisionCoreTypeName != null) {
				// "core" classes are reserved
				message = this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_CLASS_NAME_RESERVED_DIFFERENT_CASE", collisionCoreTypeName);
			} else if (className.toLowerCase().equals(this.initialClassName().toLowerCase())) {
				// no problem - the user can change the class name's case
			} else {
				if (CollectionTools.contains(this.descriptorNames(), className) && ! className.equals(this.initialClassName())) {
					// the descriptor already exists
					message = this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_DESCRIPTOR_ALREADY_EXISTS", shortClassName);
				} else if (CollectionTools.contains(this.descriptorNamesToLowerCase(), classNameToLowerCase)) {
					// the descriptor already exists with different case
					message = this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_DESCRIPTOR_ALREADY_EXISTS_DIFFERENT_CASE");
				} else {
					MWClass collisionType = this.project.getRepository().typeNamedIgnoreCase(className);
					if (collisionType != null) {
						if (collisionType.getName().equals(className)) {
							// the class already exists
							if ( ! this.allowExistingType) {
								message = this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_CLASS_ALREADY_EXISTS", className);
							}
						} else {
							// the class already exists with different case
							message = this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_CLASS_ALREADY_EXISTS_DIFFERENT_CASE");
						}
					} else {
						String[] pkgSegments = this.packageName().split("\\.");
						for (int i = 0; i < pkgSegments.length; i++) {
							String pkgSegment = pkgSegments[i];
							if (NameTools.javaReservedWordsContains(pkgSegment)) {
								// the package name cannot contain a Java reserved word
								message = this.resourceRepository().getString("NEW_CLASS_NAME_DIALOG_CLASS_NAME_INVALID", pkgSegment);
								break;
							}
						}
					}
				}
			}
		}

		this.setErrorMessage(message);
		this.getOKAction().setEnabled(message == null);
	}

	private Iterator descriptorNames() {
		return new TransformationIterator(this.project.descriptors()) {
			protected Object transform(Object next) {
				return ((MWDescriptor) next).getName();
			}
		};
	}

	private Iterator descriptorNamesToLowerCase() {
		return new TransformationIterator(this.descriptorNames()) {
			protected Object transform(Object next) {
				return ((String) next).toLowerCase();
			}
		};
	}

	private String initialClassName() {
		return this.initialPackageName + '.' + this.initialShortClassName;
	}

}
