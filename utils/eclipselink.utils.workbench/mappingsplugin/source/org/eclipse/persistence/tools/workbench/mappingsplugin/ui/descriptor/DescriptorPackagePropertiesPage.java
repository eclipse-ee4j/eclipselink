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

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.DescriptorCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.NullListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;


/**
 * the properties page displayed when a descriptor package node
 * is selected simply lists the descriptors in a list box
 */
final class DescriptorPackagePropertiesPage
	extends TitledPropertiesPage
{
	private ListModelAdapter descriptorListModel;

	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiDescriptorBundle.class
	};


	DescriptorPackagePropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		nodeHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildNodeListener());
		this.descriptorListModel = this.buildDescriptorListModel();
	}

	private PropertyChangeListener buildNodeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				DescriptorPackagePropertiesPage.this.nodeChanged();
			}
		};
	}

	private ListModelAdapter buildDescriptorListModel() {
		return new ListModelAdapter(this.buildDescriptorLVM());
	}

	protected Component buildPage() {
		JPanel page = new JPanel(new BorderLayout());
		page.setBorder(new EmptyBorder(5, 5, 5, 5));

		JList descriptorListBox = SwingComponentFactory.buildList(this.descriptorListModel);
		descriptorListBox.setBackground(UIManager.getColor("Panel.background"));
		descriptorListBox.setSelectionBackground(UIManager.getColor("Panel.background"));
		descriptorListBox.setCellRenderer(this.buildDescriptorListCellRenderer());
		page.add(descriptorListBox, BorderLayout.CENTER);

		return page;
	}
	
	private ListCellRenderer buildDescriptorListCellRenderer() {
		return new AdaptableListCellRenderer(
				new DescriptorCellRendererAdapter(this.resourceRepository()) {
					// no need for the package name
					protected String buildNonNullValueText(Object value) {
						return ((MWDescriptor) value).displayString();
					}
					public String toString() {
						return "descriptor cell renderer";
					}
				}
			);
	}

	/**
	 * when the node changes, swap in a new list of descriptors
	 */
	void nodeChanged() {
		this.descriptorListModel.setModel(this.buildDescriptorLVM());
	}

	private ListValueModel buildDescriptorLVM() {
		return new TransformationListValueModelAdapter(this.buildDescriptorNodeLVM()) {
			protected Object transformItem(Object item) {
				return ((DescriptorNode) item).getDescriptor();
			}
		};
	}

	private ListValueModel buildDescriptorNodeLVM() {
		ApplicationNode node = this.getNode();
		return (node == null) ? NullListValueModel.instance() : node.getChildrenModel();
	}

}
