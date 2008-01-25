/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.util.ListIterator;
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaComponent;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;


abstract class SchemaComponentNodeStructure
	extends AbstractModel
	implements Displayable
{
	private MWSchemaComponent component;
	
	
	// **************** Constructors ******************************************
	
	SchemaComponentNodeStructure(MWSchemaComponent component) {
		super();
		this.component = component;
	}
	
	
	// **************** SchemaComponentNodeStructure contract *****************
	
	protected MWSchemaComponent getComponent() {
		return this.component;
	}
	
	void disengageComponent() {
		this.component = null;
	}
	
	ListIterator details() {
		if (this.component == null) {
			return NullListIterator.instance();
		}
		else {
			return this.componentDetails();
		}
	}
	
	protected ListIterator componentDetails() {
		return NullListIterator.instance();
	}
	
	
	// **************** Displayable contract **********************************
	
	/** No icons here */
	public Icon icon() {
		return null;
	}
	
	
	// **************** Comparable contract ***********************************
	
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}
}
