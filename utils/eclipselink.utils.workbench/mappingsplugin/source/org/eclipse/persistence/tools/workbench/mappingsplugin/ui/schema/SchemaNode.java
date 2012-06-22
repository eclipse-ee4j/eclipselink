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
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListCurator;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


/**
 * This is the node for the schema displayed in the SchemaStructurePane,
 * NOT the schema node displayed in the NavigatorView.
 */
class SchemaNode
	extends AbstractTreeNodeValueModel
	implements Displayable
{
	// **************** Instance Variables ************************************
	
	/** The schema for this node.  Can be reset by setting the value for the ValueModel. */
	private ValueModel 		schemaHolder;
	
	/** Used to refresh the node's appearance if the schema's name changes. */
	private ValueModel 		schemaNameHolder;
	
	/** Listen for the schema's name to change */	
	private PropertyChangeListener	schemaNameListener;
	
	/** The children of this node. */
	private ListValueModel 	childrenModel;
	
	
	// **************** Constructors ******************************************
	
	SchemaNode(ValueModel schemaHolder) {
		super();
		this.initialize(schemaHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	private void initialize(ValueModel schemaHolder) {
		this.schemaHolder = schemaHolder;
		this.schemaNameHolder = this.buildSchemaNameHolder();
		this.schemaNameListener = this.buildSchemaNamePropertyChangeListener();
		this.childrenModel = this.buildChildrenModel();
	}
	
	private ValueModel buildSchemaNameHolder() {
		return new PropertyAspectAdapter(this.schemaHolder, MWXmlSchema.NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWXmlSchema) this.subject).getName();
			}
		};
	}
	
	private PropertyChangeListener buildSchemaNamePropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				SchemaNode.this.firePropertyChanged(Displayable.DISPLAY_STRING_PROPERTY, evt.getOldValue(), evt.getNewValue());
				SchemaNode.this.fireStateChanged();
			}
		};
	}
	
	protected ListValueModel buildChildrenModel() {
		return new SortedListValueModelAdapter(this.buildComponentNodesAdapter());
	}
	
	protected ListValueModel buildComponentNodesAdapter() {
		return new TransformationListValueModelAdapter(this.buildStructuralComponentsAdapter()) {
			protected Object transformItem(Object item) {
				return SchemaNode.this.buildComponentNode((MWNamedSchemaComponent) item);
			}
		};
	}
	
	protected SchemaComponentNode buildComponentNode(MWNamedSchemaComponent component) {
		return new TopLevelSchemaComponentNode(this, component);
	}
	
	protected ListValueModel buildStructuralComponentsAdapter() {
		return new ListCurator(this.schemaHolder) {
			public Iterator getValueForRecord() {
				return ((MWXmlSchema) subject).structuralComponents();
			}
		};
	}
	
	
	// **************** ValueModel contract ***********************************
	
	public Object getValue() {
		return this.schemaHolder.getValue();
	}
	
	
	// **************** TreeNodeValueModel contract ***************************
	
	public TreeNodeValueModel getParent() {
		return null;
	}
	
	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}
	
	
	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		schemaNameHolder.addPropertyChangeListener(ValueModel.VALUE, schemaNameListener);
	}

	protected void disengageValue() {
		schemaNameHolder.removePropertyChangeListener(ValueModel.VALUE, schemaNameListener);
	}


	// **************** Comparable contract ***********************************
	
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}
	
	
	// **************** Displayable contract **********************************
	
	public String displayString() {
		String schemaName = (this.schema() == null) ? "" : this.schema().getName();
		return "schema: " + schemaName;
	}
	
	public Icon icon() {
		return null;
	}
	
	
	// **************** Convenience methods ***********************************
	
	private MWXmlSchema schema() {
		return (MWXmlSchema) this.schemaHolder.getValue();
	}
	
	
	// **************** "Object" methods **************************************
	//  These methods, overwritten in AbstractTreeNodeValueModel, are overwritten
	//  again here to refer directly to the held value model, and not the "value"
	//  of the schema node, since that node can sometimes be null.
	
	/**
	 * @see AbstractTreeNodeValueModel#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o.getClass() != this.getClass()) {
			return false;
		}
		SchemaNode other = (SchemaNode) o;
		return this.schemaHolder.equals(other.schemaHolder);
	}

	/**
	 * @see AbstractTreeNodeValueModel#hashCode()
	 */
	public int hashCode() {
		return this.schemaHolder.hashCode();
	}

	/**
	 * @see AbstractTreeNodeValueModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.schemaHolder);
	}
}
