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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog.DocumentFactory;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWModifiable;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ZeroArgConstructorPreference;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NameTools;



final class MethodsPropertiesPage 
	extends ScrollablePropertiesPage
{
	private PropertyValueModel mwClassHolder;
	
	PropertyValueModel methodSelectionHolder;

	private MethodPropertiesPanel methodPropertiesPanel;
    
    private AddRemoveListPanel methodListPanel;
	
	MethodsPropertiesPage(PropertyValueModel descriptorNodeHolder, WorkbenchContextHolder contextHolder) {
		super(descriptorNodeHolder, contextHolder);
	}
	
	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.mwClassHolder = buildMWClassHolder();
		this.methodSelectionHolder = new SimplePropertyValueModel();
	}
	
	private PropertyValueModel buildMWClassHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWDescriptor) this.subject).getMWClass();
			}
		};
	}

	protected String helpTopicId() {
		return "descriptor.classInfo.methods";
	}
	
	private CollectionValueModel buildMethodsHolder() {
		return new CollectionAspectAdapter(this.mwClassHolder, MWClass.METHODS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWClass) this.subject).methods();
			}
			protected int sizeFromSubject() {
				return ((MWClass) this.subject).methodsSize();
			}
		};
	}
	
	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		panel.setBorder(BorderFactory.createEmptyBorder());
		
		
		//
		// Create the list
		//
	
		this.methodListPanel = new AddRemoveListPanel(
			getApplicationContext(),
			buildAddRemoveListPanelAdapter(),
			buildMethodsListModel(),
			resourceRepository().getString("METHODS_LIST"));
        this.methodListPanel.setBorder(buildStandardEmptyBorder());
        this.methodListPanel.setCellRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(resourceRepository())));
        this.methodListPanel.addListSelectionListener(buildListSelectionListener(this.methodListPanel));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 5, 0);
		panel.add(this.methodListPanel, constraints);
		
		//
		// Create the Properties panel
		//
		this.methodPropertiesPanel = new MethodPropertiesPanel(this.methodSelectionHolder, getWorkbenchContextHolder());
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 3;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(this.methodPropertiesPanel, constraints);

		addHelpTopicId(panel, helpTopicId());
		
		return panel;
	}

	private ListSelectionListener buildListSelectionListener(final AddRemoveListPanel methodsListPanel) {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				if (methodsListPanel.getSelectedValues().length > 1) {
					MethodsPropertiesPage.this.methodSelectionHolder.setValue(null);			
				}
				else {
					MethodsPropertiesPage.this.methodSelectionHolder.setValue(methodsListPanel.getSelectedValue());
				}
			}
		};
	}
	private ListValueModel buildMethodsListModel() {
		return new ItemPropertyListValueModelAdapter(buildSortedMethodsListValueModelAdapter(), MWModifiable.MODIFIER_ACCESS_LEVEL_PROPERTY, MWMethod.SIGNATURE_PROPERTY);
	}	

	private SortedListValueModelAdapter buildSortedMethodsListValueModelAdapter() {
		return new SortedListValueModelAdapter(buildMethodNameAdapter());
	}
	
	private ItemPropertyListValueModelAdapter buildMethodNameAdapter() {
		return new ItemPropertyListValueModelAdapter(buildMethodsHolder(), MWMethod.NAME_PROPERTY);
	}

	private AddRemoveListPanel.OptionAdapter buildAddRemoveListPanelAdapter() {
		return new AddRemoveListPanel.OptionAdapter() {	
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addNewMethod(listSelectionModel);
			}
		
			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				removeMethods(CollectionTools.iterator(listSelectionModel.getSelectedValues()));
			}
			
			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
				renameMethod(listSelectionModel);
			}
			
			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				return listSelectionModel.getSelectedValuesSize() == 1;
            }
			
			public String optionalButtonKey(){
				return "RENAME_BUTTON";
			}
		};
	}
	
	void addNewMethod(ObjectListSelectionModel listSelectionModel) {
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setIllegalNames(NameTools.javaReservedWords());
		builder.setTextFieldDescription(resourceRepository().getString("enterTheNameOfTheNewMethod.message"));
		builder.setTitle(resourceRepository().getString("addNewMethod.title"));
		builder.setHelpTopicId("dialog.newMethod");
		builder.setDocumentFactory(
				new DocumentFactory() {
					public Document buildDocument() {
						return new RegexpDocument(RegexpDocument.RE_METHOD);
					}
				});
				
		NewNameDialog dialog = builder.buildDialog(getWorkbenchContext());
		dialog.show();
			
		if (dialog.wasCanceled())
			return;
				
		MWMethod newMethod = getMWClass().addMethod(dialog.getNewName());
		listSelectionModel.setSelectedValue(newMethod);
	}
	
	void removeMethods(Iterator selectedMethods) {
		while (selectedMethods.hasNext())
			getMWClass().removeMethod((MWMethod) selectedMethods.next());			
	}
	
	void renameMethod(ObjectListSelectionModel listSelectionModel) {
		MWMethod method = (MWMethod) listSelectionModel.getSelectedValue();

		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setIllegalNames(NameTools.javaReservedWords());
		builder.setTextFieldDescription(resourceRepository().getString("EDIT_METHOD_DIALOG_MESSAGE"));
		builder.setTitle(resourceRepository().getString("EDIT_METHOD_DIALOG_TITLE"));
		builder.setHelpTopicId("dialog.newMethod");
		builder.setOriginalName(method.getName());
		builder.setDocumentFactory(
				new DocumentFactory() {
					public Document buildDocument() {
						return new RegexpDocument(RegexpDocument.RE_METHOD);
					}
				});
				
		NewNameDialog dialog = builder.buildDialog(getWorkbenchContext());
		dialog.show();

		if (dialog.wasCanceled())
			return;

		String newMethodName = dialog.getNewName();

		if (method.isZeroArgumentConstructor()) {
			method.setName(newMethodName);
			ZeroArgConstructorPreference.optionallyAddZeroArgumentConstructor(getMWClass(), getWorkbenchContext());
		}
		else {
			method.setName(newMethodName);
		}
		
		listSelectionModel.setSelectedValue(method);
	}


	
	
	private MWClass getMWClass() {
		return (MWClass) this.mwClassHolder.getValue();
	}
    
    void selectMethod(MWMethod method) {
        this.methodListPanel.setSelectedValue(method, true);
    }
}
