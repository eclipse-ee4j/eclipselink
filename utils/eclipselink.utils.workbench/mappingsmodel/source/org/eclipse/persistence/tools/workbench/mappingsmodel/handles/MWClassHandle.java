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
package org.eclipse.persistence.tools.workbench.mappingsmodel.handles;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWClassHandle is used to handle a reference to a MWClass
 * via a type name.
 * 
 * This allows us to better control which classes are written out.
 * This also allows us to fabricate classes when a project is read.
 * 
 * NOTE: MWClassHandle is DIFFERENT from the other handles.
 * Since many of the other handles are dependent on class handles,
 * we resolve the class handles first, via #resolveClassHandles(),
 * while the other handles are resolved via #postProjectBuild().
 */
public final class MWClassHandle extends MWHandle {

	/**
	 * This is the actual type.
	 * It is built from the type name, below.
	 */
	private volatile MWClass type;

	/**
	 * The type name is transient. It is used only to hold its value
	 * until resolveClassHandles() is called and we can resolve
	 * the actual type. We do not keep it in synch with the type
	 * itself because we cannot know when the type has been renamed etc.
	 */
	private volatile String typeName;


	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	private MWClassHandle() {
		super();
	}

	public MWClassHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWClassHandle(MWModel parent, MWClass type, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.type = type;
	}


	// ********** instance methods **********

	public MWClass getType() {
		return this.type;
	}

	public void setType(MWClass type) {
		this.type = type;
	}

	protected Node node() {
		return getType();
	}

	public MWClassHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	/**
	 * Class handles are resolved here - BEFORE all the other handles
	 */
	public void resolveClassHandles() {
		super.resolveClassHandles();
		if (this.typeName != null) {
			// the type will never be null - the repository will auto-generate one if necessary
			this.type = this.typeNamed(this.typeName);
			// Ensure typeName is not used by setting it to null....
			this.typeName = null;
		}
	}
	
	/**
	 * Override to delegate comparison to the type itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER type should be null.
	 */
	public int compareTo(Object o) {
		return this.type.compareTo(((MWClassHandle) o).type);
	}

	public void toString(StringBuffer sb) {
		if (this.type == null) {
			sb.append("null");
		} else {
			this.type.toString(sb);
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWClassHandle.class);

		descriptor.addDirectMapping("typeName", "getTypeNameForTopLink", "setTypeNameForTopLink", "type-name/text()");

		return descriptor;
	}
	
	private String getTypeNameForTopLink() {
		return (this.type == null) ? null : type.getName();
	}

	private void setTypeNameForTopLink(String typeName) {
		this.typeName = typeName;
	}

	// ********** TopLink methods *********
	
	public static XMLDescriptor legacy60BuildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWClassHandle.class);

		descriptor.addDirectMapping("typeName", "legacyGetTypeNameForTopLink", "legacySetTypeNameForTopLink", "type-name/text()");

		return descriptor;
	}
	
	public String legacyGetTypeNameForTopLink() {
		return getType().getName();
	}

	public void legacySetTypeNameForTopLink(String typeName) {
		this.typeName = MWModel.legacyReplaceToplinkDeprecatedClassReferences(typeName);
	}

}
