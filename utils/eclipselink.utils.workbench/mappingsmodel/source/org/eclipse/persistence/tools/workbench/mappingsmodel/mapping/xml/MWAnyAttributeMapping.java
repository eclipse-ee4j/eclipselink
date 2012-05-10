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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWAttributeHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

/**
 * This mapping represents the Any Attribute mapping defined in JAXB 2.0.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class MWAnyAttributeMapping extends MWAbstractAnyMapping {
	
	private MWClassHandle mapClass;
		public static final String MAP_CLASS_PROPERTY = "mapClass";
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWAnyAttributeMapping() {
		super();
	}

	MWAnyAttributeMapping(MWXmlDescriptor descriptor, MWClassAttribute attribute, String name) {
		super(descriptor, attribute, name);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.mapClass = new MWClassHandle(this, this.buildClassScrubber());
	}
	
	@Override
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.mapClass);
	}
	
	@SuppressWarnings("deprecation")
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAnyAttributeMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWAbstractAnyMapping.class);
		
		XMLCompositeObjectMapping mapClassHandleMapping = new XMLCompositeObjectMapping();
		mapClassHandleMapping.setAttributeName("mapClass");
		mapClassHandleMapping.setGetMethodName("getMapClassHandleForTopLink");
		mapClassHandleMapping.setSetMethodName("setMapClassHandleForTopLink");
		mapClassHandleMapping.setReferenceClass(MWClassHandle.class);
		mapClassHandleMapping.setXPath("map-class-handle");
		descriptor.addMapping(mapClassHandleMapping);

		return descriptor;
	}

	public MWAnyAttributeMapping asAnyAttributeMapping() {
		return this;
	}

	@Override
	protected DatabaseMapping buildRuntimeMapping() {
		return new XMLAnyAttributeMapping();
	}
	
	@Override
	public DatabaseMapping runtimeMapping() {
		XMLAnyAttributeMapping runtimeMapping = new XMLAnyAttributeMapping();
		runtimeMapping.setAttributeName(this.getInstanceVariable().getName());
		
		MWXmlField field = this.getXmlField();
		if (field != null) {
			runtimeMapping.setXPath(field.getXpath());
		}
		
		MWClass mapClass = this.getMapClass();
		if (mapClass != null) {
			runtimeMapping.useMapClassName(mapClass.fullName());
		}
		
		return runtimeMapping;
	}

	@Override
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWAnyAttributeMapping(this);
	}
	
	@Override
	protected boolean mayUseCollectionData() {
		return false;
	}

	public MWClass getMapClass() {
		return mapClass.getType();
	}

	public void setMapClass(MWClass mapClass) {
		MWClass oldValue = this.mapClass.getType();
		this.mapClass.setType(mapClass);
		firePropertyChanged(MWAnyAttributeMapping.MAP_CLASS_PROPERTY, oldValue, mapClass);
	}
	
	/**
	 * check for null
	 */
	@SuppressWarnings("unused")
	private MWClassHandle getMapClassHandleForTopLink() {
		return (this.mapClass.getType() == null) ? null : this.mapClass;
	}
	
	@SuppressWarnings("unused")
	private void setMapClassHandleForTopLink(MWClassHandle classHandle) {
		NodeReferenceScrubber scrubber = this.buildClassScrubber();
		this.mapClass = ((classHandle == null) ? new MWClassHandle(this, scrubber) : classHandle.setScrubber(scrubber));
	}

	private NodeReferenceScrubber buildClassScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAnyAttributeMapping.this.setMapClass(null);
			}
			@Override
			public String toString() {
				return "MWAnyAttributeMapping.buildClassScrubber()";
			}
		};
	}
	
	@Override
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		addAttributeNotMapProblem(newProblems);
	}

	protected void addAttributeNotMapProblem(List<Problem> newProblem) {
		if (!this.getInstanceVariable().getType().isAssignableToMap()) {
			newProblem.add(buildProblem(ProblemConstants.MAPPING_ATTRIBUTE_NOT_ASSIGNABLE_TO_MAP));
		}
	}
}
