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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryItem;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * This class is a parameter for a descriptor level query
 */
public final class MWQueryParameter 
	extends MWModel
	implements MWQueryItem
{
	private volatile String name;
		public static final String NAME_PROPERTY = "name";

	private MWClassHandle typeHandle;
		public static final String TYPE_PROPERTY = "type";


	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWQueryParameter() {
		super();
	}

	MWQueryParameter(MWAbstractQuery query, String name, MWClass type) {
		super(query);
		this.name = name;
		this.typeHandle.setType(type);
	}

	protected void initialize(Node parentNode) {
		super.initialize(parentNode);
		this.typeHandle = new MWClassHandle(this, this.buildTypeScrubber());
	}


	//	********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.typeHandle);
	}

	private NodeReferenceScrubber buildTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWQueryParameter.this.setType(null);
			}
			public String toString() {
				return "MWQueryParameter.buildTypeScrubber()";
			}
		};
	}


	//	********** accessors **********

	public MWQuery getQuery() {
		return (MWQuery) this.getParent();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		if (this.attributeValueHasChanged(old, name)) {
			this.firePropertyChanged(NAME_PROPERTY, old, name);
			this.getProject().nodeRenamed(this);
		}
	}

	public MWClass getType() {
		return this.typeHandle.getType();
	}

	public void setType(MWClass type) {
		if (type == null) {
			throw new NullPointerException();
		}
		Object old = this.typeHandle.getType();
		this.typeHandle.setType(type);
		if (this.attributeValueHasChanged(old, type)) {
			this.firePropertyChanged(TYPE_PROPERTY, old, type);
			this.getQuery().signatureChanged();
		}
	}


	//	********** MWQueryItem implementation **********

	public void removeSelfFromParent() {
		this.getQuery().removeParameter(this);
	}


	//	********** runtime conversion **********

	void convertToRuntime(ObjectLevelReadQuery runtimeQuery) {
		runtimeQuery.addArgumentByTypeName(this.getName(), this.getType().getName());
	}


	// ********** display methods **********

	public String displayString() {
		return this.getName();
	}

	public void toString(StringBuffer sb) {
		sb.append(this.getName());
	}


	//	********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWQueryParameter.class);

		descriptor.addDirectMapping("name", "name/text()");

		XMLCompositeObjectMapping typeHandleMapping = new XMLCompositeObjectMapping();
		typeHandleMapping.setAttributeName("typeHandle");
		typeHandleMapping.setGetMethodName("getTypeHandleForTopLink");
		typeHandleMapping.setSetMethodName("setTypeHandleForTopLink");
		typeHandleMapping.setReferenceClass(MWClassHandle.class);
		typeHandleMapping.setXPath("type-handle");
		descriptor.addMapping(typeHandleMapping);

		// mapping hack to support DP4 projects :-(
		DatabaseMapping typeNameMapping = descriptor.addDirectMapping("typeName", "legacyDP4GetTypeName", "legacyDP4SetTypeName", "type/text()");
		typeNameMapping.setIsReadOnly(true);

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWClassHandle getTypeHandleForTopLink() {
		return (this.typeHandle.getType() == null) ? null : this.typeHandle;
	}
	private void setTypeHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildTypeScrubber();
		this.typeHandle = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	private String legacyDP4GetTypeName() {
		throw new UnsupportedOperationException();
    }
    private void legacyDP4SetTypeName(String typeName) {
    	if (typeName != null) {
    		// if 'typeName' is not null, we have a DP4 project
	        this.typeHandle = new MWClassHandle(this, this.buildTypeScrubber());
	        this.typeHandle.legacySetTypeNameForTopLink(typeName);
    	}
    }

}
