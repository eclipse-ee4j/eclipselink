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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionAdapter;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.MultipleClassChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.DescriptorCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.UiDescriptorBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.UiQueryBundle;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;



public class InterfaceDescriptorPropertiesPage extends TitledPropertiesPage {

	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiQueryBundle.class,
		UiDescriptorBundle.class,
		UiDescriptorRelationalBundle.class
	};


	public InterfaceDescriptorPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	@Override
	protected Component buildPage() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();		
		
		// Set up label
		LabelArea label = new LabelArea();
		label.setText(resourceRepository().getString("CHOOSE_DESCRIPTORS_THAT_IMPLEMENT_LABEL"));
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setScrollable(true);
	
		//Label constraints
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridheight = 1;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(label, constraints);
		
		// Implementors
		JLabel implementorsLabel = buildLabel("IMPLEMENTORS_LABEL");	
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5, 5, 0, 5);
		panel.add(implementorsLabel, constraints);
		
		AddRemoveListPanel implementorsPanel = new AddRemoveListPanel(getApplicationContext(), buildAddRemoveAdapter(), buildImplementorsListValueModel());
		implementorsPanel.setCellRenderer(buildImplementorsListCellRenderer());
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.5;
		constraints.weighty = 1;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5, 5, 5, 5);
		addHelpTopicId(implementorsPanel, helpTopicId() + ".implementors");
		panel.add(implementorsPanel, constraints);


		// Query Key list
		JLabel queryKeysLabel = buildLabel("COMMON_QUERY_KEYS_LABEL");	
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5, 5, 0, 5);
		panel.add(queryKeysLabel, constraints);

		JList queryKeyList = SwingComponentFactory.buildList(buildQueryKeyListModel());
		//queryKeyList.setCellRenderer(new BasicLabel());
		queryKeyList.setEnabled(false);
		JScrollPane queryKeyListScrollPane = new JScrollPane(queryKeyList);

		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.5;
		constraints.weighty = 1;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5, 5, 5, 5);
		addHelpTopicId(queryKeyListScrollPane, helpTopicId() + ".commonQueryKeys");
		panel.add(queryKeyListScrollPane, constraints);
		
		// comment text field
		JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(5, 5, 5, 5);
		panel.add(commentPanel, constraints);
		this.addHelpTopicId(commentPanel, "descriptor.general.comment");
		
		addHelpTopicId(panel, helpTopicId());
		
		return panel;
	}
	
	
	// ************ Implementors ***********
	
	private AddRemoveListPanel.Adapter buildAddRemoveAdapter() {
		return new AddRemoveListPanel.Adapter() {
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {

				ClassDescriptionRepository repository = buildClassDescriptionRepository();
				ClassDescriptionAdapter adapter = buildClassDescriptionAdapter();
				MultipleClassChooserDialog dialog = buildMultipleClassChooserDialog(repository, adapter);
				dialog.setTitle(resourceRepository().getString("IMPLEMENTOR_DESCRIPTOR_LIST_BROWSER_DIALOG.title"));
				dialog.show();

				if (dialog.wasConfirmed()) {
					MWInterfaceDescriptor interfaceDescriptor = (MWInterfaceDescriptor) selection();
					for (Iterator iter = dialog.selectedClassDescriptions(); iter.hasNext(); ) {
						MWTableDescriptor selectedDescriptor = (MWTableDescriptor) iter.next();
						interfaceDescriptor.addImplementor(selectedDescriptor);
					}
				}
				// try to force all the objects generated by the dialog to be garbage-collected
				dialog = null;
				MultipleClassChooserDialog.gc();
			}

			private MultipleClassChooserDialog buildMultipleClassChooserDialog(ClassDescriptionRepository repository, ClassDescriptionAdapter adapter) {
				MultipleClassChooserDialog dialog = new MultipleClassChooserDialog(getWorkbenchContext(), repository, adapter) {
					@Override
					protected Iterator buildCustomActions() {
						return NullIterator.instance();
					}
					@Override
					protected String helpTopicId() {
						return "dialog.chooseImplementorDescriptor";
					}
				};
				return dialog;
			}

			private ClassDescriptionAdapter buildClassDescriptionAdapter() {
				ClassDescriptionAdapter adapter = new ClassDescriptionAdapter() {
					public String additionalInfo(Object classDescription) {
						return null;
					}

					public String className(Object classDescription) {
						return ((MWDescriptor) classDescription).getMWClass().getName();
					}

					public String packageName(Object classDescription) {
						return ((MWDescriptor) classDescription).packageName();
					}

					public String shortClassName(Object classDescription) {
						return ((MWDescriptor) classDescription).shortName();
					}
				};
				return adapter;
			}

			private ClassDescriptionRepository buildClassDescriptionRepository() {
				ClassDescriptionRepository repository = new ClassDescriptionRepository() {
					public Iterator classDescriptions() {
						List implementors = new ArrayList();
						MWInterfaceDescriptor interfaceDescriptor = (MWInterfaceDescriptor) selection();
						for (Iterator stream = ((MWRelationalProject) interfaceDescriptor.getProject()).descriptorsThatImplement(interfaceDescriptor); stream.hasNext();) {
							MWDescriptor implementor = (MWDescriptor) stream.next();
							if (!implementor.getMWClass().isInterface()) {
								if (!CollectionTools.contains(interfaceDescriptor.implementors(), implementor)) { 
									implementors.add(implementor);
								}
							}
						}
						return implementors.iterator();
					}

					public void refreshClassDescriptions() {
					}
				};
				return repository;
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				MWInterfaceDescriptor interfaceDescriptor = (MWInterfaceDescriptor) selection();
				
				Iterator selectedValues = CollectionTools.iterator(listSelectionModel.getSelectedValues());
				while (selectedValues.hasNext()) {
					interfaceDescriptor.removeImplementor((MWDescriptor) selectedValues.next());
				}
			}
		};
	}
	
	private ListValueModel buildImplementorsListValueModel() {
		return new CollectionListValueModelAdapter(buildImplementorsCollectionModel());
	}
	
	private CollectionValueModel buildImplementorsCollectionModel() {
		return new CollectionAspectAdapter(getSelectionHolder(), MWInterfaceDescriptor.IMPLEMENTORS_COLLECTION) {
			@Override
			protected Iterator getValueFromSubject() {
				return ((MWInterfaceDescriptor) this.subject).implementors();
			}
			@Override
			protected int sizeFromSubject() {
				return ((MWInterfaceDescriptor) this.subject).implementorsSize();
			}
		};
	}
	
	private ListCellRenderer buildImplementorsListCellRenderer() {
		return new AdaptableListCellRenderer(new DescriptorCellRendererAdapter(resourceRepository()));
	}

	// ************ Query Key Names **********
	
	private ListModel buildQueryKeyListModel() {
		return new ListModelAdapter(buildQueryNamesCollectionModel()) ;
	}
	
	//TODO make the query key names list update properly when multiple windows are opened
	//Should update when :
	//1. an implementor is added or removed
	//2. a query key of any of the implementors is added or removed 
	//		(including auto-generated ones from direct mappings)
	//3. a query key is renamed
	private CollectionValueModel buildQueryNamesCollectionModel() {
		return new CollectionAspectAdapter(getSelectionHolder(), MWInterfaceDescriptor.IMPLEMENTORS_COLLECTION) {
			@Override
			protected CollectionChangeListener buildCollectionChangeListener() {
				return new CollectionChangeListener() {
					public void itemsAdded(CollectionChangeEvent e) {
						fireCollectionChanged(VALUE);					}
					public void itemsRemoved(CollectionChangeEvent e) {
						fireCollectionChanged(VALUE);					}
					public void collectionChanged(CollectionChangeEvent e) {
						fireCollectionChanged(VALUE);					
					}
				};
			}

			@Override
			protected Iterator getValueFromSubject() {
				return ((MWInterfaceDescriptor) this.subject).allQueryKeyNames();
			}
			
		};
	}
	
	public String helpTopicId() {
		return "descriptor.interface";
	}

}
