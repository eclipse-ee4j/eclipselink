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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Null representation of the descriptor inheritance policy.  This class
 * should be used only as a placeholder for persistence and runtime conversion
 * purposes.  This class is essentially read-only.  Any calls to the implemented
 * setter methods will invoke an <code>UnsupportedOperationException</code>.
 * 
 * @version 10.1.3
 */
public final class MWNullInheritancePolicy 
	extends MWAbstractDescriptorPolicy
	implements MWInheritancePolicy, MWXmlNode
{
	public MWNullInheritancePolicy(MWDescriptor parent) {
		super(parent);
	}
	
	public MWDescriptor getParentDescriptor() {
		return null;
	}
	
	public Iterator candidateParentDescriptors() {
		return NullIterator.instance();
	}
	
	public MWDescriptor getRootDescriptor() {
		return getOwningDescriptor();
	}
    
	public Iterator descriptorLineage() {
		// a lineage always includes the starting descriptor
		return new SingleElementIterator(this.getOwningDescriptor());
	}
	
	public Iterator childDescriptors() {
		return NullIterator.instance();
	}
	
	public Iterator descendentDescriptors() {
		return NullIterator.instance();
	}
	
	public boolean isRoot() {
		return false;
	}
	
	public MWClassIndicatorPolicy getClassIndicatorPolicy() {
		return new MWNullClassIndicatorPolicy(this);
	}
	
	public void buildClassIndicatorValues() {
		//do nothing
	}
	
	public void descriptorInheritanceChanged() {
		// no op
	}
	
	public void parentDescriptorMorphedToAggregate() {
		//do nothing, null policy
	}
	
	public void automap() {
		//do nothing, null policy
	}
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		// Do Nothing.  Null Policy.
	}
	
	
	// **************** MWXmlInheritancePolicy implementation *****************
	
	/** @see MWXmlNode#resolveXpaths() */
	public void resolveXpaths() {
		// Do nothing.  Null policy.
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		// Do nothing.  Null policy.
	}
	
	
	// **************** MWClassIndicatorPolicy.Parent implementation **********
	
	public MWMappingDescriptor getContainingDescriptor() {
		return this.getOwningDescriptor();
	}
}
