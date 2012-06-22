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

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaComponent;

abstract class SchemaComponentQNamedDetail 
	extends SchemaComponentDetail 
{
	public SchemaComponentQNamedDetail(MWSchemaComponent component) {
		super(component);
	}
	
	protected void initialize(MWSchemaComponent component) {
		super.initialize(component);
		this.engageComponent(this.getQNamedComponent());
	}
	
	private void engageComponent(MWNamedSchemaComponent qNamedComponent) {
		if (qNamedComponent != null) {
			qNamedComponent.getTargetNamespace().addPropertyChangeListener(MWNamespace.NAMESPACE_PREFIX_PROPERTY, this.buildNamespacePrefixChangeListener());
		}
	}
	
	private PropertyChangeListener buildNamespacePrefixChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				SchemaComponentQNamedDetail.this.valueChanged();
			}
		};
	}
	
	protected String getValueFromComponent() {
		return this.getQName();
	}
	
	private String getQName() {
		MWNamedSchemaComponent qNamedComponent = this.getQNamedComponent();
		return (qNamedComponent == null) ? null : qNamedComponent.qName();
	}
	
	protected abstract MWNamedSchemaComponent getQNamedComponent();
}
