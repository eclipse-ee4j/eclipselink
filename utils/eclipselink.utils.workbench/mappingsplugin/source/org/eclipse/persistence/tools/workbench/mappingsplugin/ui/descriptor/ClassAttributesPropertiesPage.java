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
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog.DocumentFactory;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWModifiable;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ClassAttributeCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;



final class ClassAttributesPropertiesPage 
	extends ScrollablePropertiesPage
{
	
	private PropertyValueModel mwClassHolder;
	
	PropertyValueModel selectedAttributeHolder;
	
	
	ClassAttributesPropertiesPage(PropertyValueModel descriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(descriptorNodeHolder, contextHolder);
	}
	
	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.mwClassHolder = buildMWClassHolder();
		this.selectedAttributeHolder = new SimplePropertyValueModel();
	}
	
	private PropertyValueModel buildMWClassHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWDescriptor) this.subject).getMWClass();
			}
		};
	}

	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		//
		// Action:	Create the panel
		//
		//panel.setBorder(BorderFactory.createEmptyBorder());
		GridBagConstraints constraints = new GridBagConstraints();
		//
		// Action:	Create the attributes list
		//
	
		AddRemoveListPanel attributesListPanel = new AddRemoveListPanel(
			getApplicationContext(),
			buildAddRemoveListPanelAdapter(),
			buildAttributeModifierAdapter(),
            AddRemovePanel.BOTTOM,
			resourceRepository().getString("ATTRIBUTES_LIST"), 
            buildAttributeSelector());
		attributesListPanel.setCellRenderer(buildAttributesListCellRenderer());		
		attributesListPanel.setBorder(buildStandardEmptyBorder());
		attributesListPanel.addListSelectionListener(buildListSelectionListener(attributesListPanel));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 5, 0);
		panel.add(attributesListPanel, constraints);
		//
		// Action:	Create the attribute properties panel
		//
		JPanel attributePropertiesPanel = new ClassAttributePanel(this.selectedAttributeHolder, this.getSelectionHolder(), getWorkbenchContextHolder());
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 10, 5, 5);
		panel.add(attributePropertiesPanel, constraints);
		
		addHelpTopicId(panel, helpTopicId());
		return panel;
	}
	
    private NodeSelector buildAttributeSelector() {
        return new NodeSelector() {
            public void selectNodeFor(Object item) {
                ProjectNode projectNode = (ProjectNode) navigatorSelectionModel().getSelectedProjectNodes()[0];
                projectNode.selectMappingNodeFor((MWClassAttribute) item, navigatorSelectionModel());       
              
            }
        };
    }
	// ************** convenience methods ************
	
	private MWClass getMWClass() {
		return (MWClass) this.mwClassHolder.getValue();
	}
	

	private ListCellRenderer buildAttributesListCellRenderer(){
		return new AdaptableListCellRenderer(new ClassAttributeCellRendererAdapter(resourceRepository()));
	}
	
	private ListSelectionListener buildListSelectionListener(final AddRemoveListPanel attributesListPanel) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				if (attributesListPanel.getSelectedValues().length > 1) {
					ClassAttributesPropertiesPage.this.selectedAttributeHolder.setValue(null);			
				}
				else {
					ClassAttributesPropertiesPage.this.selectedAttributeHolder.setValue(attributesListPanel.getSelectedValue());
				}
			}
		};
	}
	
	private AddRemoveListPanel.OptionAdapter buildAddRemoveListPanelAdapter() {
		return new AddRemoveListPanel.OptionAdapter() {
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addNewAttribute(listSelectionModel);
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				removeSelectedAttributes(listSelectionModel);
			}
			
			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
				renameSelectedAttribute(listSelectionModel);
			}
			
			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				return listSelectionModel.getSelectedValuesSize() == 1;
            }

			public String optionalButtonKey() {
				return "RENAME_BUTTON";
			}
		};
	}

	void addNewAttribute(ObjectListSelectionModel listSelectionModel) {
		NewNameDialog.Builder newNameDialogBuilder =new NewNameDialog.Builder();
		newNameDialogBuilder.setExistingNames(getMWClass().attributeNames());
		newNameDialogBuilder.setIllegalNames(NameTools.javaReservedWords());
		newNameDialogBuilder.setTitle(resourceRepository().getString("addNewAttribute.title"));
		newNameDialogBuilder.setTextFieldDescription(resourceRepository().getString("enterTheNameOfTheNewAttribute.message"));
		newNameDialogBuilder.setHelpTopicId("dialog.newAttribute");
		newNameDialogBuilder.setDocumentFactory(
				new DocumentFactory() {
					public Document buildDocument() {
						return new RegexpDocument(RegexpDocument.RE_FIELD);
					}
				});
		
		NewNameDialog newNameDialog = newNameDialogBuilder.buildDialog(getWorkbenchContext());
		newNameDialog.show();
		if (!newNameDialog.wasConfirmed())
			return;
			
		MWClassAttribute newAttribute = getMWClass().addAttribute(newNameDialog.getNewName());
		listSelectionModel.setSelectedValue(newAttribute);
	}
	
	private CollectionValueModel buildAttributesHolder() {
		return new CollectionAspectAdapter(this.mwClassHolder, MWClass.ATTRIBUTES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWClass) this.subject).attributes();
			}
			protected int sizeFromSubject() {
				return ((MWClass) this.subject).attributesSize();
			}
		};
	}
	
	private ItemPropertyListValueModelAdapter buildAttributeModifierAdapter() {
		return new ItemPropertyListValueModelAdapter(buildSortedAttributesListValueModelAdapter(), MWModifiable.MODIFIER_ACCESS_LEVEL_PROPERTY, MWClassAttribute.DECLARATION_PROPERTY);
		
	}
	private SortedListValueModelAdapter buildSortedAttributesListValueModelAdapter() {
		return new SortedListValueModelAdapter(buildAttributeNameAdapter());
	}
	
	private ItemPropertyListValueModelAdapter buildAttributeNameAdapter() {
		return new ItemPropertyListValueModelAdapter(buildAttributesHolder(), MWClassAttribute.NAME_PROPERTY);
	}
	

	void removeSelectedAttributes(ObjectListSelectionModel listSelectionModel) {
		getMWClass().removeAttributes(CollectionTools.collection(listSelectionModel.getSelectedValues()));
	}
	
	void renameSelectedAttribute(ObjectListSelectionModel listSelectionModel) {
		final MWClassAttribute selectedAttribute = (MWClassAttribute) listSelectionModel.getSelectedValue();
	
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setExistingNames(selectedAttribute.getDeclaringType().attributeNames());
		builder.setOriginalName(selectedAttribute.getName());
		builder.setIllegalNames(NameTools.javaReservedWords());
		builder.setTextFieldDescription(resourceRepository().getString("renameAttribute.message"));
		builder.setTitle(resourceRepository().getString("renameAttribute.title"));
		builder.setHelpTopicId("dialog.attributeRename");
		builder.setDocumentFactory(
				new DocumentFactory() {
					public Document buildDocument() {
						return new RegexpDocument(RegexpDocument.RE_FIELD);
					}
				});
		
		NewNameDialog newNameDialog = builder.buildDialog(getWorkbenchContext());
		newNameDialog.show();
		if (newNameDialog.wasCanceled()) {
			return;
		}
		String newName = newNameDialog.getNewName();
		//save the expanded state because renaming an attribute causes the mapping node to change
		navigatorSelectionModel().pushExpansionState();
		selectedAttribute.setName(newName);		
		listSelectionModel.setSelectedValue(selectedAttribute);		
		navigatorSelectionModel().popAndRestoreExpansionState();
	}


	// ************** help ************

	private String helpTopicId() {
		return "descriptor.classInfo.attributes";
	}

}
