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
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWDescriptorQueryParameterHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWQueryParameters.
 * 
 * Since a MWQueryParameters is nested within a MWQuery which is nested
 * within the XML file for a MWClassDescriptor, we need to store a reference
 * to a particular parameter as a set of instance variables:
 *   - the name of the MWClassDescriptor
 *   - the signature of the containing query
 *   - the name of the query parameter
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 */
public final class MWDescriptorQueryParameterHandle extends MWHandle {

	/**
	 * This is the actual query parameter.
	 * It is built from the class descriptor name, query signature,
	 * and query parameter name, below.
	 */
	private volatile MWQueryParameter queryParameter;

	/**
	 * The class descriptor name, query signature, and query
	 * parameter name are transient. They
	 * are used only to hold their values until postProjectBuild()
	 * is called and we can resolve the actual query parameter.
	 * We do not keep these in synch with the query parameter itself because
	 * we cannot know when the query parameter has been renamed etc.
	 */
	private volatile String classDescriptorName;
	private volatile String querySignature;
	private volatile String queryParameterName;
	
	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	private MWDescriptorQueryParameterHandle() {
		super();
	}

	public MWDescriptorQueryParameterHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWDescriptorQueryParameterHandle(MWModel parent, MWQueryParameter queryParameter, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.queryParameter = queryParameter;
	}


	// ********** instance methods **********
	
	public MWQueryParameter getQueryParameter() {
		return this.queryParameter;
	}

	public void setQueryParameter(MWQueryParameter queryParameter) {
		this.queryParameter = queryParameter;
	}

	protected Node node() {
		return getQueryParameter();
	}
	
	public MWDescriptorQueryParameterHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void postProjectBuild() {
		super.postProjectBuild();
		if (this.classDescriptorName != null && this.querySignature != null && this.queryParameterName != null) {
			MWTableDescriptor classDescriptor = (MWTableDescriptor) this.getProject().descriptorNamed(this.classDescriptorName);
			if (classDescriptor != null) {
				MWQuery query = classDescriptor.getQueryManager().queryWithSignature(this.querySignature);
				if (query != null) {
					this.queryParameter = query.getParameterNamed(this.queryParameterName);
				}
			}
		}
		// Ensure classDesriptorName, querySignature, and queryParameter name 
		// are not used by setting them to null....
		// If the XML is corrupt and only some of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.classDescriptorName = null;
		this.querySignature = null;
		this.queryParameterName = null;
	}

	/**
	 * Override to delegate comparison to the query parameter itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER query parameter should be null.
	 */
	public int compareTo(Object o) {
		return this.queryParameter.compareTo(((MWDescriptorQueryParameterHandle) o).queryParameter);
	}

	public void toString(StringBuffer sb) {
		if (this.queryParameter == null) {
			sb.append("null");
		} else {
			this.queryParameter.toString(sb);
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWDescriptorQueryParameterHandle.class);

		descriptor.addDirectMapping("classDescriptorName", "getClassDescriptorNameForTopLink", "setClassDescriptorNameForTopLink", "class-descriptor-name/text()");
		descriptor.addDirectMapping("querySignature", "getQuerySignatureForTopLink", "setQuerySignatureForTopLink", "query-signature/text()");
		descriptor.addDirectMapping("queryParameterName", "getQueryParameterNameForTopLink", "setQueryParameterNameForTopLink", "query-parameter-name/text()");

		return descriptor;
	}

	private String getClassDescriptorNameForTopLink() {
		return (this.queryParameter == null) ? null : this.queryParameter.getQuery().getOwningDescriptor().getName();
	}
	
	private void setClassDescriptorNameForTopLink(String classDescriptorName) {
		this.classDescriptorName = classDescriptorName;
	}

	private String getQuerySignatureForTopLink() {
		return (this.queryParameter == null) ? null : this.queryParameter.getQuery().signature();
	}

	private void setQuerySignatureForTopLink(String querySignature) {
		this.querySignature = querySignature;
	}

	private String getQueryParameterNameForTopLink() {
		return (this.queryParameter == null) ? null : this.queryParameter.getName();
	}

	private void setQueryParameterNameForTopLink(String queryParameterName) {
		this.queryParameterName = queryParameterName;
	}

}
