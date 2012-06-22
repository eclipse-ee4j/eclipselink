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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

/**
 * A MWInterfaceAliasPolicy exists to allow the user to choose an interface
 * alias for a class descriptor. This is commonly used in cases such as RMI,
 * EJB, or CORBA, where you want to refer to everything as an interface. The
 * problem is, you really need some underlying class to contain attributes that
 * actually get mapped.
 */
public final class MWDescriptorInterfaceAliasPolicy extends MWAbstractDescriptorPolicy {

	private MWClassHandle interfaceAliasHandle;
		public final static String INTERFACE_ALIAS_PROPERTY = "interfaceAlias";


	// ********** constructors **********

	private MWDescriptorInterfaceAliasPolicy() {
		// for TopLink use only
		super();
	}

	public MWDescriptorInterfaceAliasPolicy(MWMappingDescriptor descriptor) {
		super(descriptor);
	}


	// ********** initialization **********
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.interfaceAliasHandle = new MWClassHandle(this, this.buildInterfaceAliasScrubber());
	}


	// ********** containment hierarchy **********
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.interfaceAliasHandle);
	}

	private NodeReferenceScrubber buildInterfaceAliasScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWDescriptorInterfaceAliasPolicy.this.setInterfaceAlias(null);
			}
			public String toString() {
				return "MWDescriptorInterfaceAliasPolicy.buildInterfaceAliasScrubber()";
			}
		};
	}


	// ********** accessors **********
	
	public MWClass getInterfaceAlias() {
		return this.interfaceAliasHandle.getType();
	}

	public void setInterfaceAlias(MWClass newInterfaceAlias) {
		MWClass oldValue = getInterfaceAlias();
		this.interfaceAliasHandle.setType(newInterfaceAlias);
		// If you use an interface as an alias, it should be removed from the
		// project.
		if (newInterfaceAlias != null) {
			getOwningDescriptor().getProject().removeDescriptorForType(newInterfaceAlias);
		}
		firePropertyChanged(INTERFACE_ALIAS_PROPERTY, oldValue, interfaceAliasHandle.getType());
	}


	// ********** run-time **********
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
		MWClass interfaceAlias = getInterfaceAlias();
		if (interfaceAlias != null) {
			runtimeDescriptor.getInterfacePolicy().addParentInterfaceName(getInterfaceAlias().getName());
		}
	}


	// ********** MWAbstractDescriptorPolicy implementation **********
	
	public MWDescriptorPolicy getPersistedPolicy() {
		return this;
	}

	public boolean isActive() {
		return true;
	}


	// ********** problems **********
	
	protected void addProblemsTo(List problems) {
		super.addProblemsTo(problems);
		this.checkInterfaceAlias(problems);
	}

	private void checkInterfaceAlias(List problems) {
		if (this.getInterfaceAlias() == null) {
			problems.add(this.buildProblem(ProblemConstants.DESCRIPTOR_INTERFACE_ALIAS_INTERFACE_SPECIFIED));
		}
	}


	// ********** misc **********

	public void toString(StringBuffer sb) {
		this.getInterfaceAlias().toString(sb);
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWDescriptorInterfaceAliasPolicy.class);

		XMLCompositeObjectMapping interfaceAliasHandleMapping = new XMLCompositeObjectMapping();
		interfaceAliasHandleMapping.setAttributeName("interfaceAliasHandle");
		interfaceAliasHandleMapping.setSetMethodName("setInterfaceAliasHandleForTopLink");
		interfaceAliasHandleMapping.setGetMethodName("getInterfaceAliasHandleForTopLink");
		interfaceAliasHandleMapping.setReferenceClass(MWClassHandle.class);
		interfaceAliasHandleMapping.setXPath("interface-alias-type-handle");
		descriptor.addMapping(interfaceAliasHandleMapping);
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWClassHandle getInterfaceAliasHandleForTopLink() {
		return (this.interfaceAliasHandle.getType() == null) ? null : this.interfaceAliasHandle;
	}
	private void setInterfaceAliasHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildInterfaceAliasScrubber();
		this.interfaceAliasHandle = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}
}
