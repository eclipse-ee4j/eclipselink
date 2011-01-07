/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;


public abstract class IdentityConstraint 
	extends MWModel 
{
	/** An xpath expression that selects the elements within this element that have the constraint */
	private volatile String selector;
	
	/** A list of xpath expressions that determine the components within the selected element that define the constraint */
	private List fields;
	
	/** Toplink use only */
	protected IdentityConstraint() {
		super();
	}
	
	protected /* private-protected */ IdentityConstraint(MWModel parent) {
		super(parent);
	}
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(IdentityConstraint.class);
		
		InheritancePolicy ip = (InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.addClassIndicator(KeyIdentityConstraint.class, "key");
		ip.addClassIndicator(KeyRefIdentityConstraint.class, "key-ref");
		ip.addClassIndicator(UniqueIdentityConstraint.class, "unique");
		
		descriptor.addDirectMapping("selector", "selector/text()");
		
		XMLCompositeDirectCollectionMapping fieldsMapping = new XMLCompositeDirectCollectionMapping();
		fieldsMapping.setAttributeName("fields");
		fieldsMapping.setGetMethodName("getFieldsForToplink");
		fieldsMapping.setSetMethodName("setFieldsForToplink");
		fieldsMapping.setXPath("fields/field/text()");
		descriptor.addMapping(fieldsMapping);

		return descriptor;
	}	
	void reload(org.apache.xerces.impl.xs.identity.IdentityConstraint idConstraint) {
		this.selector = idConstraint.getSelectorStr();
		
		this.fields = new Vector();
		for (int i = 0; i < idConstraint.getFieldCount(); i ++ ) {
			if(idConstraint.getFieldAt(i) != null){
				this.fields.add(idConstraint.getFieldAt(i));
			}
		}
	}
//******************************************Toplink persistence use****************************************/
	private List getFieldsForToplink() {
		return this.fields;
	}
	private void setFieldsForToplink(List list) {
		this.fields = list;
	}

}
