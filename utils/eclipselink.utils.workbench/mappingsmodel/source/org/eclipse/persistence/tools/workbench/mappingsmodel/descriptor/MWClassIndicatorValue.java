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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWClassIndicatorValue extends MWModel {
			
	private volatile Object indicatorValue;
		public final static String INDICATOR_VALUE_PROPERTY = "indicatorValue";
		
	private MWDescriptorHandle descriptorValueHandle;
		public final static String DESCRIPTOR_PROPERTY = "descriptorValue";
	
	// transient
	private volatile boolean include;
		public final static String INCLUDE_PROPERTY = "include";
		
		
	// ********** constructors **********
	
	private MWClassIndicatorValue() {
		// for TopLink use only
		super();
	}
	
	MWClassIndicatorValue(MWClassIndicatorFieldPolicy parent, MWMappingDescriptor descriptorValue, Object indicatorValue) {
		super(parent);
		this.descriptorValueHandle.setDescriptor(descriptorValue);
		this.indicatorValue = indicatorValue;
		//initialize include flag to true if it is a concrete class and instantiable by Toplink
		if (descriptorValue != null) {
			if (descriptorValue.getMWClass().isInstantiable()) {
				if (descriptorValue.getMWClass().zeroArgumentConstructor() != null || descriptorValue.hasActiveInstantiationPolicy()) {
					this.include = true;
				}
			}
		} 
	}
	
		
	// ********** initialization **********
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.descriptorValueHandle = new MWDescriptorHandle(this, this.buildDescriptorValueScrubber());
	}


	// ********** accessors **********

	private MWClassIndicatorFieldPolicy getPolicy() {
		return (MWClassIndicatorFieldPolicy) this.getParent();
	}

	public boolean isInclude() {
		return this.include;
	}
	
	public void setInclude(boolean include) {
		boolean oldInclude = this.include;
		this.include = include;
		if ( !include )
			setIndicatorValue(null);
		firePropertyChanged(INCLUDE_PROPERTY, oldInclude, this.include);
	}
	
	public MWDescriptor getDescriptorValue() {
		return this.descriptorValueHandle.getDescriptor();
	}
	
	public void setDescriptorValue(MWDescriptor descriptor) {
		Object oldValue = getDescriptorValue();
		this.descriptorValueHandle.setDescriptor(descriptor);
		firePropertyChanged(DESCRIPTOR_PROPERTY, oldValue, getDescriptorValue());
	}
	
	public Object getIndicatorValue() {
		return this.indicatorValue;
	}

	public String getIndicatorValueAsString() {
		return (String) ConversionManager.getDefaultManager().convertObject(this.indicatorValue, String.class);
	}

	public void setIndicatorValue(Object newValue) {
		Object oldValue = this.indicatorValue;
		this.indicatorValue = newValue;
		this.firePropertyChanged(INDICATOR_VALUE_PROPERTY, oldValue, newValue);
	}
		

	// ********** containment hierarchy **********
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.descriptorValueHandle);
	}

	private NodeReferenceScrubber buildDescriptorValueScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassIndicatorValue.this.descriptorRemoved();
			}
			public String toString() {
				return "MWClassIndicatorValue.buildDescriptorValueScrubber()";
			}
		};
	}

	void descriptorRemoved() {
		// we don't really need to clear the descriptor;
		// and some listeners would really appreciate it if we kept it around
		// this.setDescriptorValue(null);
		this.getPolicy().removeIndicator(this);
	}

	public void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		super.descriptorReplaced(oldDescriptor, newDescriptor);
		if (this.getDescriptorValue() == oldDescriptor) {
			this.setDescriptorValue(newDescriptor);
		}
	}
	
		
	// ********** behavior **********

	public void toString(StringBuffer sb) {
		sb.append(this.getIndicatorValue());
		sb.append(" -> ");
		if (this.getDescriptorValue() != null) {
			sb.append(this.getDescriptorValue().getMWClass().shortName());
		} else {
			sb.append("null");
		}
	}
	
	
	// ********** problems **********
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkClassIndicatorValue(newProblems);
	}
	
	private void checkClassIndicatorValue(List newProblems) {
		if (this.isInclude() && (this.getIndicatorValue() == null || this.getIndicatorValueAsString().length() == 0)) {
			newProblems.add(this.buildProblem(ProblemConstants.NO_CLASS_INDICATOR_FOR_INCLUDED_CLASS, this.getDescriptorValue().displayStringWithPackage()));
		}
	}


	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWClassIndicatorValue.class);

		XMLDirectMapping indicatorValueMapping = new XMLDirectMapping();
		indicatorValueMapping.setAttributeName("indicatorValue");
		indicatorValueMapping.setGetMethodName("getIndicatorValueForTopLink");
		indicatorValueMapping.setSetMethodName("setIndicatorValueForTopLink");
		indicatorValueMapping.setXPath("indicator/text()");
		descriptor.addMapping(indicatorValueMapping);

		XMLCompositeObjectMapping descriptorValueMapping = new XMLCompositeObjectMapping();
		descriptorValueMapping.setAttributeName("descriptorValueHandle");
		descriptorValueMapping.setGetMethodName("getDescriptorValueHandleForTopLink");
		descriptorValueMapping.setSetMethodName("setDescriptorValueHandleForTopLink");
		descriptorValueMapping.setReferenceClass(MWDescriptorHandle.class);
		descriptorValueMapping.setXPath("descriptor-value-handle");
		descriptor.addMapping(descriptorValueMapping);

		return descriptor;
	}

	private MWDescriptorHandle getDescriptorValueHandleForTopLink() {
		return (this.descriptorValueHandle.getDescriptor() == null) ? null : this.descriptorValueHandle;
	}
	private void setDescriptorValueHandleForTopLink(MWDescriptorHandle handle) {
		NodeReferenceScrubber scrubber = this.buildDescriptorValueScrubber();
		this.descriptorValueHandle = ((handle == null) ? new MWDescriptorHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	private Object getIndicatorValueForTopLink() {
		return this.getIndicatorValueAsString();
	}
	private void setIndicatorValueForTopLink(Object indicatorValue) {
		this.indicatorValue = indicatorValue;
	}
	
	public void postProjectBuild() {
		super.postProjectBuild();
		this.include = true;
	}

}
