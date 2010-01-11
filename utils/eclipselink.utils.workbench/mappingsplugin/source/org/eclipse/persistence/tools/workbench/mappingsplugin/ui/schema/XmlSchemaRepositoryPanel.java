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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Iterator;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.TitledPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableListCellRenderer;


class XmlSchemaRepositoryPanel 
	extends TitledPropertiesPage 
{	
	// this value is queried reflectively during plug-in initialization
	private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
		UiCommonBundle.class,
		UiXmlBundle.class,
		UiSchemaResourceBundle.class
	};


	XmlSchemaRepositoryPanel(WorkbenchContext context) {
		super(context);
	}
	
	protected Component buildPage() {
		JPanel page = new JPanel(new BorderLayout());
		page.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JList schemaList = SwingComponentFactory.buildList();
		schemaList.setBackground(UIManager.getColor("Panel.background"));
		schemaList.setSelectionBackground(UIManager.getColor("Panel.background"));
		schemaList.setCellRenderer(new DisplayableListCellRenderer());
		schemaList.setModel(this.buildSchemasListModel());
		page.add(schemaList, BorderLayout.CENTER);
		
		return page;
	}
	
	private ListModel buildSchemasListModel() {
		return new ListModelAdapter(this.buildUpdatingSortedDisplayableSchemasValueModel());
	}
	
	private ListValueModel buildUpdatingSortedDisplayableSchemasValueModel() {
		return new SortedListValueModelAdapter(this.buildUpdatingDisplayableSchemasValueModel());
	}
	
	private ListValueModel buildUpdatingDisplayableSchemasValueModel() {
		return new ItemPropertyListValueModelAdapter(this.buildDisplayableSchemasValueModel(), Displayable.DISPLAY_STRING_PROPERTY);
	}
	
	private ListValueModel buildDisplayableSchemasValueModel() {
		return new TransformationListValueModelAdapter(this.buildSchemasValueModel()) {
			 protected Object transformItem(Object item) {
				return new XmlSchemaDisplayableAdapter((MWXmlSchema) item, resourceRepository());
			}
		};
	}
	
	private CollectionValueModel buildSchemasValueModel() {
		return new CollectionAspectAdapter(this.getSelectionHolder(), MWXmlSchemaRepository.SCHEMAS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWXmlSchemaRepository) subject).schemas();
			}
			protected int sizeFromSubject() {
				return ((MWXmlSchemaRepository) subject).schemasSize();
			}
		};
	}
}
