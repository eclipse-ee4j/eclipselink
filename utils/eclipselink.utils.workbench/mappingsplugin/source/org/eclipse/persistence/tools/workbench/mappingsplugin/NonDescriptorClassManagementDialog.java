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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.MultipleClassChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.ExternalClassLoadFailureContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ClassCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ExternalClassDescriptionClassDesciptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ExternalClassDescriptionClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ExternalClassLoadFailuresDialog;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


public final class NonDescriptorClassManagementDialog 
	extends AbstractDialog
{
	// **************** Variables *********************************************
	
	/** 
	 * This is passed in - from it we have access to the class repository
	 * and the descriptors (so that we may filter them out)
	 */
	private MWProject project;
	
	/** The list model */
	private DefaultListModel classesListModel;
	
	/** The list selection model */
	ObjectListSelectionModel classesListSelectionModel;
	
	
	// **************** Constructors ******************************************
	
	public NonDescriptorClassManagementDialog(WorkbenchContext context, MWProject project) {
		super(context);
		this.project = project;
		this.buildModels();
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		String egg = "";
		
		if (System.getProperty("user.name", "").equalsIgnoreCase("dmahar")) {
			egg = "Denise's ";
		}
		
		this.setTitle(this.resourceRepository().getString("NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.TITLE", egg));
	}
	
	private void buildModels() {
		this.classesListModel = new DefaultListModel();
		this.rebuildClassesListModel();
		this.classesListSelectionModel = this.buildClassesListSelectionModel();
	}
	
	private void rebuildClassesListModel() {
		Set descriptorClasses = CollectionTools.set(this.descriptorClasses());
		List types = new ArrayList(this.project.getClassRepository().userTypesSize());
		for (Iterator stream = this.project.getClassRepository().userTypes(); stream.hasNext(); ) {
			Object type = stream.next();
			if ( ! descriptorClasses.contains(type)) {
				types.add(type);
			}
		}
		Collections.sort(types);
		this.classesListModel.clear();
		for (Iterator stream = types.iterator(); stream.hasNext(); ) {
			this.classesListModel.addElement(stream.next());
		}
	}
	
	private ObjectListSelectionModel buildClassesListSelectionModel() {
		return new ObjectListSelectionModel(this.classesListModel);
	}
	
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		
        LabelArea label = new LabelArea();
		label.setText(this.resourceRepository().getString("NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.EXPLANATION", StringTools.CR));
		label.setBorder(BorderFactory.createEmptyBorder(3, 3, 5, 3));
		panel.add(label, BorderLayout.NORTH);
		
		JList list = SwingComponentFactory.buildList(this.classesListModel);
		list.setSelectionModel(this.classesListSelectionModel);
		list.setCellRenderer(this.buildCellRenderer());
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(500, 400));
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}
	
	private ListCellRenderer buildCellRenderer() {
		ClassCellRendererAdapter adapter = 
			new ClassCellRendererAdapter(this.resourceRepository()) {
				public boolean showDetailedIcon() {
					return false;
				}
			};
		
		return new AdaptableListCellRenderer(adapter);
	}
	
	protected Iterator buildCustomActions() {
		Collection actions = new ArrayList();
		actions.add(this.buildAddAction());
		actions.add(this.buildRemoveAction());
		actions.add(this.buildRefreshAction());
		return actions.iterator();
	}
	
	private Action buildAddAction() {
		return new AbstractAction(this.resourceRepository().getString("NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.ADD_ACTION")) {
			public void actionPerformed(ActionEvent e) {
				NonDescriptorClassManagementDialog.this.addClasses();
			}
		};
	}
	
	void addClasses() {
		this.repository().refreshExternalClassDescriptions();
		
		MultipleClassChooserDialog dialog = 
			new MultipleClassChooserDialog(
				this.getWorkbenchContext(), 
				this.buildMultiClassChooserDialogClassDescriptionRepository(), 
				ExternalClassDescriptionClassDesciptionAdapter.instance(),
				this
			);
		dialog.show();
		
		if (dialog.wasCanceled()) {
			// try to force all the objects generated by the dialog to be garbage-collected
			dialog = null;
			MultipleClassChooserDialog.gc();
			return;
		}
		
		ExternalClassLoadFailureContainer failures = 
			this.repository().refreshTypesFor(dialog.selectedClassDescriptions());
		
		// try to force all the objects generated by the dialog to be garbage-collected
		dialog = null;
		MultipleClassChooserDialog.gc();

		if (failures.containsFailures()) {
			new ExternalClassLoadFailuresDialog(this.getWorkbenchContext(), this, failures).show();
		}
		this.rebuildClassesListModel();
	}
	
	private ClassDescriptionRepository buildMultiClassChooserDialogClassDescriptionRepository() {
		return new NonDescriptorExternalClassDescriptionClassDescriptionRepository(this.project.getClassRepository());
	}
	
	private Action buildRemoveAction() {
		final Action action = 
			new AbstractAction(this.resourceRepository().getString("NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.REMOVE_ACTION")) {
				public void actionPerformed(ActionEvent e) {
					NonDescriptorClassManagementDialog.this.removeSelectedClasses();
				}
			};
		action.setEnabled(false);
		this.classesListSelectionModel.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting()) {
						action.setEnabled(! NonDescriptorClassManagementDialog.this.classesListSelectionModel.isSelectionEmpty());
					}
				}
			}
		);
		return action;
	}
	
	void removeSelectedClasses() {
		for (Iterator stream = CollectionTools.iterator(this.classesListSelectionModel.getSelectedValues()); stream.hasNext(); ) {
			MWClass type = ((MWClass) stream.next());
			this.classesListModel.removeElement(type);
			type.clear();
		}
	}
	
	private Action buildRefreshAction() {
		final Action action = 
			new AbstractAction(this.resourceRepository().getString("NON_DESCRIPTOR_CLASS_MANAGEMENT_DIALOG.REFRESH_ACTION")) {
				public void actionPerformed(ActionEvent e) {
					NonDescriptorClassManagementDialog.this.refreshSelectedClasses();
				}
			};
		action.setEnabled(false);
		this.classesListSelectionModel.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if ( ! e.getValueIsAdjusting()) {
						action.setEnabled( ! NonDescriptorClassManagementDialog.this.classesListSelectionModel.isSelectionEmpty());
					}
				}
			}
		);
		return action;
	}
	
	void refreshSelectedClasses() {
		this.repository().refreshExternalClassDescriptions();
		
		ExternalClassLoadFailureContainer failures = 
			this.repository().refreshTypes(CollectionTools.iterator(this.classesListSelectionModel.getSelectedValues()));
		
		if (failures.containsFailures()) {
			new ExternalClassLoadFailuresDialog(this.getWorkbenchContext(), this, failures).show();
		}
	}
	
	
	// **************** Convenience *******************************************
	
	private MWClassRepository repository() {
		return this.project.getClassRepository();
	}
	
	Iterator descriptorClasses() {
		return new TransformationIterator(this.project.descriptors()) {
			protected Object transform(Object next) {
				return ((MWDescriptor) next).getMWClass();
			}
		};
	}
	
	
	// **************** AbstractDialog implementation/overrides ***************
	
	protected boolean cancelButtonIsVisible() {
		return false;
	}
	
	protected String helpTopicId() {
		return "dialog.nonDescriptorManagement";
	}
	
	
	// **************** Member classes ****************************************
	
	/**
	 * This repository will return "external class descriptions" for all the classes found
	 * on the "project" classpath, except those that are already mapped
	 * (and their superclasses and super-interfaces)
	 */
	private class NonDescriptorExternalClassDescriptionClassDescriptionRepository
		extends ExternalClassDescriptionClassDescriptionRepository
	{
		/** descriptor classes and their superclasses and super-interfaces */
		Collection descriptorClassNames;
		/** all the classes found on the "project" classpath */
		Collection userClassNames;
		
		
		private NonDescriptorExternalClassDescriptionClassDescriptionRepository(MWClassRepository repository) {
			super(repository);
			this.descriptorClassNames = this.buildDescriptorClassNames();
			this.userClassNames = this.buildUserClassNames();
		}
		
		private Collection buildDescriptorClassNames() {
			return CollectionTools.set(this.allDescriptorClassNames());
		}
		
		private Iterator allDescriptorClassNames() {
			return new TransformationIterator(NonDescriptorClassManagementDialog.this.descriptorClasses()) {
				protected Object transform(Object next) {
					return ((MWClass) next).getName();
				}
			};
		}
		
		private Collection buildUserClassNames() {
			Classpath cp = new Classpath(CollectionTools.list(this.repository.fullyQualifiedClasspathEntries()));
			Collection result = new HashSet(1000);		// start sorta big
			cp.addClassNamesTo(result);
			return result;
		}
		
		protected boolean accept(String externalClassDescriptionName) {
			return super.accept(externalClassDescriptionName) &&
					this.userClassNames.contains(externalClassDescriptionName) &&
					! this.descriptorClassNames.contains(externalClassDescriptionName);
		}

	}

}
