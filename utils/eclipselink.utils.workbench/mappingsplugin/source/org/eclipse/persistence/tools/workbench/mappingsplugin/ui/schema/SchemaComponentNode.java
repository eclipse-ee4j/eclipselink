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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.ListIterator;
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaComponent;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListCurator;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


abstract class SchemaComponentNode
	extends AbstractTreeNodeValueModel
	implements Displayable
{
	// **************** Instance Variables ************************************
	
	/** The parent for this node */
	private AbstractTreeNodeValueModel parent;
	
	/** The schema component represented by this node (no need for value models here). */
	protected MWSchemaComponent component;
	
	/** The children of this node. */
	private ListValueModel childrenModel;
	
	/** Controls displaying and details of node */
	protected SchemaComponentNodeStructure structure;
	
	
	// **************** Constructors ******************************************
	
	SchemaComponentNode(AbstractTreeNodeValueModel parent, MWSchemaComponent component) {
		super();
		this.parent = parent;
		this.initialize(component);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(MWSchemaComponent component) {
		this.component = component;
		this.childrenModel = this.buildChildrenModel();
		this.structure = this.buildStructure(component);
	}
	
	protected ListValueModel buildChildrenModel() {
		return new TransformationListValueModelAdapter(this.buildStructuralComponentsAdapter()) {
			protected Object transformItem(Object item) {
				return SchemaComponentNode.this.buildLocalComponentNode((MWSchemaComponent) item);
			}
		};
	}
	
	protected ListValueModel buildStructuralComponentsAdapter() {
		return new ListCurator(this.component) {
			public Iterator getValueForRecord() {
				return ((MWSchemaComponent) subject).structuralComponents();
			}
		};
	}
	
	protected SchemaComponentNode buildLocalComponentNode(MWSchemaComponent component) {
		return new LocalSchemaComponentNode(this, component);
	}
	
	protected abstract SchemaComponentNodeStructure buildStructure(MWSchemaComponent component);
	
	protected PropertyChangeListener buildDisplayStringChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				SchemaComponentNode.this.fireStateChanged();
			}
		};
	}
	
	
	// **************** SchemaComponentNode contract **************************
	
	protected ListIterator details() {
		return this.structure.details();
	}
	
	
	// **************** TreeNodeValueModel contract ***************************
	
	public TreeNodeValueModel getParent() {
		return this.parent;
	}
	
	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}
	
	
	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {}
	
	protected void disengageValue() {
		this.structure.disengageComponent();
	}


	// **************** ValueModel contract ***********************************
	
	public Object getValue() {
		return this.component;
	}
	
	
	// **************** Displayable contract **********************************
	
	public String displayString() {
		return this.structure.displayString();
	}
	
	public Icon icon() {
		return null;
	}
	
	
	// **************** Comparable contract ***********************************
	
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}
}
