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
package org.eclipse.persistence.tools.workbench.mappingsmodel.handles;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWAttributeHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWClassAttributes.
 * 
 * Since a MWClassAttribute is nested within the XML file
 * for a MWClass, we need to store a reference to a particular
 * attribute as a pair of instance variables:
 *   - the name of the declaring MWClass
 *   - the name of the attribute
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 */
public final class MWAttributeHandle extends MWHandle {

	/**
	 * This is the actual attribute.
	 * It is built from the declaring type and attribute names, below.
	 */
	private volatile MWClassAttribute attribute;

	/**
	 * The declaring type and attribute names are transient. They
	 * are used only to hold their values until postProjectBuild()
	 * is called and we can resolve the actual attribute.
	 * We do not keep these in synch with the attribute itself because
	 * we cannot know when the attribute has been renamed etc.
	 */
	private volatile String attributeDeclaringTypeName;
	private volatile String attributeName;


	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	private MWAttributeHandle() {
		super();
	}
	
	public MWAttributeHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWAttributeHandle(MWModel parent, MWClassAttribute attribute, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.attribute = attribute;
	}


	// ********** instance methods **********

	public MWClassAttribute getAttribute() {
		return this.attribute;
	}

	public void setAttribute(MWClassAttribute attribute) {
		this.attribute = attribute;
	}

	protected Node node() {
		return getAttribute();
	}

	public MWAttributeHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void postProjectBuild() {
		super.postProjectBuild();
		if (this.attributeDeclaringTypeName != null && this.attributeName != null) {
			// the type will never be null - the repository will auto-generate one if necessary
			this.attribute = this.typeNamed(this.attributeDeclaringTypeName).attributeNamedFromCombinedAll(this.attributeName);
		}
		// Ensure attributeDeclaringTypeName and attributeName are not
		// used by setting them to null....
		// If the XML is corrupt and only one of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.attributeDeclaringTypeName = null;
		this.attributeName = null;
	}

	/**
	 * Override to delegate comparison to the attribute itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER attribute should be null.
	 */
	public int compareTo(Object o) {
		return this.attribute.compareTo(((MWAttributeHandle) o).attribute);
	}

	public void toString(StringBuffer sb) {
		if (this.attribute == null) {
			sb.append("null");
		} else {
			this.attribute.toString(sb);
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor(){
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWAttributeHandle.class);
	
		descriptor.addDirectMapping("attributeDeclaringTypeName", "getAttributeDeclaringTypeNameForTopLink", "setAttributeDeclaringTypeNameForTopLink", "attribute-declaring-type-name/text()");	
		descriptor.addDirectMapping("attributeName", "getAttributeNameForTopLink", "setAttributeNameForTopLink", "attribute-name/text()");
	
		return descriptor;
	}
	
	private String getAttributeDeclaringTypeNameForTopLink(){
		return (this.attribute == null) ? null : this.attribute.getDeclaringType().getName();
	}

	private void setAttributeDeclaringTypeNameForTopLink(String attributeDeclaringTypeName){
		this.attributeDeclaringTypeName = attributeDeclaringTypeName;
	}

	private String getAttributeNameForTopLink() {
		return (this.attribute == null) ? null : attribute.getName();
	}

	private void setAttributeNameForTopLink(String attributeName) {
		this.attributeName = attributeName;
	}
}
