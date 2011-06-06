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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ClassCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class ClassPropertiesPage extends ScrollablePropertiesPage {
	
	private PropertyValueModel mwClassHolder;

	ClassPropertiesPage(PropertyValueModel descriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(descriptorNodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel descriptorNodeHolder) {
		super.initialize(descriptorNodeHolder);
		this.mwClassHolder = buildMWClassHolder();
	}
	
	private PropertyValueModel buildMWClassHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWDescriptor) this.subject).getMWClass();
			}
		};
	}
	
	protected Component buildPage() {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());
		
		// Class Name widgets
		JComponent classNameWidgets = buildLabeledComponent(
			"CLASS_NAME_LABEL",
			buildClassNameTextField()
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 5, 0, 5);

		panel.add(classNameWidgets, constraints);
		addHelpTopicId(classNameWidgets, helpTopicId() + ".name");

		// Superclass label
		JLabel superclassLabel = buildLabel("SUPER_CLASS_LABEL");

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(5, 5, 0, 0);

		panel.add(superclassLabel, constraints);
		addAlignLeft(superclassLabel);

		// Superclass chooser
		ClassChooserPanel superClassChooserPanel = ClassChooserTools.buildPanel(
						this.buildSuperClassHolder(),
						this.buildClassRepositoryHolder(),
						ClassChooserTools.buildDeclarableReferenceFilter(),
						superclassLabel,
						this.getWorkbenchContextHolder()
		);

		constraints.gridx      = 1;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, 5, 0, 5);

		panel.add(superClassChooserPanel, constraints);
		addPaneForAlignment(superClassChooserPanel);
		addHelpTopicId(superClassChooserPanel, helpTopicId() + ".superclass");

		// Create the Properties panel
		JPanel propertiesPanel = buildPropertiesPanel();

		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.NORTH;
		constraints.insets		= new Insets(0, 0, 0, 0);

		panel.add(propertiesPanel, constraints);

		addHelpTopicId(panel, helpTopicId());
		return panel;
	}

	protected JPanel buildPropertiesPanel() {
		JPanel propertiesPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		// Access modifiers
		ClassModifiersPanel modifiersPanel = new ClassModifiersPanel(this.mwClassHolder, getApplicationContext());
		addHelpTopicId(modifiersPanel, helpTopicId() + ".modifiers");
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.NORTH;
		constraints.insets		= new Insets(5, 5, 0, 0);
		propertiesPanel.add(modifiersPanel, constraints);
	
		// Interfaces implemented list
		AddRemoveListPanel interfacesListPanel = buildInterfacesListPanel();
		addHelpTopicId(interfacesListPanel, helpTopicId() + ".interfaces");
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 3;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 0, 5);
		propertiesPanel.add(interfacesListPanel, constraints);
		
		return propertiesPanel;
	}

	
	// **************** convenience *******************************************
	
	ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return ClassPropertiesPage.this.getMWClass().getRepository();
			}
		};
	}

	// increase visibility for inner classes
	WorkbenchContext workbenchContext() {
		return this.getWorkbenchContext();
	}


	//*********** class name **********
	
	private JTextField buildClassNameTextField() {
		JTextField textField = SwingComponentFactory.buildTextField(buildClassNameTextFieldDocument());
		textField.setEditable(false);
		
		return textField;
	}
	
	private Document buildClassNameTextFieldDocument() {
		return new DocumentAdapter(buildClassNameHolder(), new RegexpDocument(RegexpDocument.RE_FULLY_QUALIFIED_CLASS_NAME));
	}
	
	private PropertyValueModel buildClassNameHolder() {
		return new PropertyAspectAdapter(this.mwClassHolder, MWClass.NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClass) this.subject).getName();
			}
		};
	}


	//*********** super class **********
	
	private PropertyValueModel buildSuperClassHolder() {
		return new PropertyAspectAdapter(this.mwClassHolder, MWClass.SUPERCLASS_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClass) this.subject).getSuperclass();
			}
			protected void setValueOnSubject(Object value) {
				((MWClass) this.subject).setSuperclass((MWClass) value);
			}
		};
	}


	// *********** interfaces ************
	
	private AddRemoveListPanel buildInterfacesListPanel() {
		TitledBorder interfacesImplementedTitle = new TitledBorder(resourceRepository().getString("INTERFACES_IMPLEMENTED_LIST"));

		AddRemoveListPanel interfacesListPanel = new AddRemoveListPanel(
			getApplicationContext(),
			buildAddRemoveListPanelAdapter(),
			buildSortedInterfacesListModel(),
			AddRemovePanel.BOTTOM
		);
		interfacesListPanel.setBorder(BorderFactory.createCompoundBorder(interfacesImplementedTitle, BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		interfacesListPanel.setCellRenderer(buildInterfacesListCellRenderer());
		interfacesListPanel.setName(resourceRepository().getString("INTERFACES_LIST_NAME"));
			
		return interfacesListPanel;
	}
	
	private ListCellRenderer buildInterfacesListCellRenderer() {
		return new AdaptableListCellRenderer(new ClassCellRendererAdapter(resourceRepository()));
	}

	private AddRemoveListPanel.Adapter buildAddRemoveListPanelAdapter() {
		return new AddRemoveListPanel.Adapter() {
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
					MWClass type = ClassChooserTools.promptForType(
							ClassPropertiesPage.this.getMWClass().getRepository(),
							ClassChooserTools.buildDeclarableReferenceFilter(),
							ClassPropertiesPage.this.workbenchContext()
					);
					if (type == null) {
						return;
					}
					ClassPropertiesPage.this.getMWClass().addInterface(type);		
			}
			
			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				getMWClass().removeInterfaces(CollectionTools.collection(listSelectionModel.getSelectedValues()));
			}
		};	
	}

	
	private ListValueModel buildSortedInterfacesListModel() {
		return new SortedListValueModelAdapter(buildItemListValueModelAdapter());
	}
	
	private ItemPropertyListValueModelAdapter buildItemListValueModelAdapter() {
		return new ItemPropertyListValueModelAdapter(buildInterfacesCollectionValueModel(), MWClass.NAME_PROPERTY);
	}
	
	private CollectionValueModel buildInterfacesCollectionValueModel() {
		return new CollectionAspectAdapter(this.mwClassHolder, MWClass.INTERFACES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWClass) this.subject).interfaces();
			}	
			protected int sizeFromSubject() {
				return ((MWClass) this.subject).interfacesSize();
			}
		};
	}


	MWClass getMWClass() {
		return (MWClass) this.mwClassHolder.getValue();
	}
	
	protected String helpTopicId() {
		return "descriptor.classInfo.class";
	}
	
}
