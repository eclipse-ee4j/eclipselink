/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
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
	
	public static ClassDescriptor legacy50BuildDescriptor(){
		ClassDescriptor descriptor = new deprecated.xml.XMLDescriptor();
		descriptor.descriptorIsAggregate();
	
		descriptor.setJavaClass(MWAttributeHandle.class);
		descriptor.setTableName("attribute-handle");
	
		OneToOneMapping attributeDeclaringTypeMapping = new OneToOneMapping();
		attributeDeclaringTypeMapping.setAttributeName("attributeDeclaringType");
		attributeDeclaringTypeMapping.setGetMethodName("legacy50GetAttributeDeclaringTypeForTopLink");
		attributeDeclaringTypeMapping.setSetMethodName("legacySetAttributeDeclaringTypeForTopLink");
		attributeDeclaringTypeMapping.setReferenceClass(MWClass.class);
		attributeDeclaringTypeMapping.setForeignKeyFieldName("attribute-declaring-type");
		attributeDeclaringTypeMapping.dontUseIndirection();
		descriptor.addMapping(attributeDeclaringTypeMapping);
		
		descriptor.addDirectMapping("attributeName", "getAttributeNameForTopLink", "setAttributeNameForTopLink", "attribute-name");
	
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

	/**
	 * This legacy method will only be used by toplink for reading 5.x projects 
	 * in which there was an attribute declaring type.
	 */
	private void legacySetAttributeDeclaringTypeForTopLink(MWClass attributeDeclaringType) {
		if (attributeDeclaringType != null) {
			this.attributeDeclaringTypeName = attributeDeclaringType.getName();
		}
	}
	/**
	 * it is necessary to return something here because in 5.0 writes are performed
	 * during the read for the class override stuff
	 */
	private MWClass legacy50GetAttributeDeclaringTypeForTopLink() {
		if(this.getAttribute() == null)
			return null;
		return this.getAttribute().getDeclaringType();
	}

	/**
	 * These legacy methods are used when importing a 3.x or 4.x project.
	 * The containing object will call these methods when
	 * TopLink calls the containing object's similarly-named methods.
	 */
	public void legacy45SetAttributeDeclaringType(MWClass attributeDeclaringType){
		if (attributeDeclaringType != null) {
			this.attributeDeclaringTypeName = attributeDeclaringType.getName();
		}
	}

	public void legacy45SetAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

}
