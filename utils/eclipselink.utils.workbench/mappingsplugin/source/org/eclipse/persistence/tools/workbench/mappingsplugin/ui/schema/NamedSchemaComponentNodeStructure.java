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
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementListIterator;


abstract class NamedSchemaComponentNodeStructure
	extends SchemaComponentNodeStructure
{
	/** cache the qName of the component */
	private String qName;
	
	/** cache the PCL */
	private PropertyChangeListener qNameListener;
	
	
	// **************** Constructors ******************************************
	
	NamedSchemaComponentNodeStructure(MWNamedSchemaComponent component) {
		super(component);
		this.initialize(component);
	}
	
	
	// **************** Initialization ****************************************
	
	private void initialize(MWNamedSchemaComponent component) {
		this.qName = component.qName();
		this.qNameListener = this.buildQNameListener();
		component.getTargetNamespace().addPropertyChangeListener(MWNamespace.NAMESPACE_PREFIX_PROPERTY, this.qNameListener);
	}
	
	private PropertyChangeListener buildQNameListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				NamedSchemaComponentNodeStructure.this.qNameChanged();
			}
		};
	}
	
	
	// **************** SchemaComponentNodeStructure contract *****************
	
	void disengageComponent() {
		((MWNamedSchemaComponent) this.getComponent()).getTargetNamespace().removePropertyChangeListener(MWNamespace.NAMESPACE_PREFIX_PROPERTY, this.qNameListener);
		super.disengageComponent();
	}
	
	
	// **************** Internal **********************************************
	
	private void qNameChanged() {
		String oldQName = this.qName;
		if (this.getComponent() != null) {
			this.qName = ((MWNamedSchemaComponent) this.getComponent()).qName();
			this.firePropertyChanged(DISPLAY_STRING_PROPERTY, oldQName, this.qName);
		}
	}
	
	
	protected ListIterator nameDetails() {
		return new SingleElementListIterator(this.buildNameDetail());
	}
	
	SchemaComponentDetail buildNameDetail() {
		return new SchemaComponentQNamedDetail(this.getComponent()) {
			protected String getName() {
				return "name";
			}
			
			protected MWNamedSchemaComponent getQNamedComponent() {
				return (MWNamedSchemaComponent) this.component;
			}
		};
	}
	
	
	// **************** NamedSchemaComponentNodeStructure contract ************
	
	abstract Integer topLevelOrderIndex();
	
	
	// **************** Displayable contract **********************************
	
	public String displayString() {
		MWNamedSchemaComponent namedComponent = (MWNamedSchemaComponent) this.getComponent();
		String designator = (namedComponent.isReference()) ? "ref" : "name";
		return namedComponent.componentTypeName() + ": " + designator + "=\"" + namedComponent.qName() + "\"";
	}
}
