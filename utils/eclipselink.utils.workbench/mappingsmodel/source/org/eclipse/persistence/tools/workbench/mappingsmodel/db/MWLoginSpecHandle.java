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
package org.eclipse.persistence.tools.workbench.mappingsmodel.db;

import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWLoginSpecHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWLoginSpecs.
 * Since a MWLoginSpec is nested within the XML file
 * for a MWDatabase, we need to store a reference to a particular
 * login spec as
 *   - the name of the login spec
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 * 
 * NB: Currently this class is only used by MWDatabase. The model
 * synchronization is performed locally by that class since it holds
 * the child collection of login specs ('loginSpecs') as well as the
 * references to them ('deploymentLoginSpec' and 'developmentLoginSpec').
 * If this handle is ever used in another place in the model, the
 * model synchronization will need to cascade to all model objects
 */
public final class MWLoginSpecHandle extends MWHandle {

	/**
	 * This is the actual login spec.
	 * It is built from the login spec name, below.
	 */
	private volatile MWLoginSpec loginSpec;

	/**
	 * The login spec name is transient. It is used only to hold its value
	 * until #postProjectBuild() is called and we can resolve
	 * the actual login spec. We do not keep it in synch with the login spec
	 * itself because we cannot know when the login spec has been renamed etc.
	 */
	private volatile String loginSpecName;


	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	private MWLoginSpecHandle() {
		super();
	}

	MWLoginSpecHandle(MWDatabase parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	MWLoginSpecHandle(MWDatabase parent, MWLoginSpec loginSpec, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.loginSpec = loginSpec;
	}


	// ********** instance methods **********

	MWLoginSpec getLoginSpec() {
		return this.loginSpec;
	}

	void setLoginSpec(MWLoginSpec loginSpec) {
		this.loginSpec = loginSpec;
	}

	protected Node node() {
		return getLoginSpec();
	}
	
	public MWLoginSpecHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void postProjectBuild() {
		super.postProjectBuild();
		if (this.loginSpecName != null) {
			this.loginSpec = this.getDatabase().loginSpecNamed(this.loginSpecName);
		}
		// Ensure loginSpecName is not used by setting it to null....
		this.loginSpecName = null;
	}


	/**
	 * Override to delegate comparison to the login spec itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER login spec should be null.
	 */
	public int compareTo(Object o) {
		return this.loginSpec.compareTo(((MWLoginSpecHandle) o).loginSpec);
	}

	public void toString(StringBuffer sb) {
		if (this.loginSpec == null) {
			sb.append("null");
		} else {
			this.loginSpec.toString(sb);
		}
	}


	// ********** TopLink methods **********
	
	public static ClassDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWLoginSpecHandle.class);

		descriptor.addDirectMapping("loginSpecName", "getLoginSpecNameForToplink", "setLoginSpecNameForToplink", "login-spec-name/text()");

		return descriptor;
	}
	
	private String getLoginSpecNameForToplink() {
		return (this.loginSpec == null) ? null : this.loginSpec.getName();
	}

	private void setLoginSpecNameForToplink(String loginSpecName) {
		this.loginSpecName = loginSpecName;
	}

}
