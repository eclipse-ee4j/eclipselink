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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;

public final class MWNamedSchemaComponentHandle 
	extends MWHandle 
{
	/**
	 * This is the actual component.
	 * It is built from the schema and path information below.
	 */
	private volatile MWNamedSchemaComponent component;
	
	/**
	 * The schema and qname path are transient. They
	 * are used only to hold their values until postProjectBuild()
	 * is called and we can resolve the actual component.
	 * We do not keep these in synch with the component itself because
	 * we cannot know when the components in the path have been renamed etc.
	 */
	private volatile String schemaName;
	private List qNamePath;
	
	
	// ********** constructors **********
	
	/** default constructor - for TopLink use only */
	private MWNamedSchemaComponentHandle() {
		super();
	}

	public MWNamedSchemaComponentHandle(MWNode parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}
	
	public MWNamedSchemaComponentHandle(MWNode parent, MWNamedSchemaComponent component, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.component = component;
	}
	
	
	// ********** instance methods **********
	
	private MWXmlSchemaRepository getSchemaRepository() {
		return ((MWXmlProject) getProject()).getSchemaRepository();
	}
	
	public MWNamedSchemaComponent getComponent() {
		return this.component;
	}
	
	public void setComponent(MWNamedSchemaComponent component) {
		this.component = component;
	}
	
	protected Node node() {
		return getComponent();
	}

	public MWNamedSchemaComponentHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void resolveMetadataHandles() {
		super.resolveMetadataHandles();
		
		if (this.schemaName != null && ! this.qNamePath.isEmpty()) {
			MWXmlSchema schema = this.getSchemaRepository().getSchema(this.schemaName);
			MWNamedSchemaComponent temp;
			
			if (schema != null) {
				Iterator stream = this.qNamePath.iterator();
				QName qName = (QName) stream.next();
				temp = schema.component(qName);
				
				while (stream.hasNext()) {
					qName = (QName) stream.next();
					temp = temp.nestedNamedComponent(qName);
				}
				
				this.component = temp;
			}
		}
		
		// Ensure schemaName and complexTypeQName are not used by setting them to null....
		// If the XML is corrupt and only one of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.schemaName = null;
		this.qNamePath = null;
	}
	
	/**
	 * Override to delegate comparison to the element itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER field should be null.
	 */
	public int compareTo(Object o) {
		return this.component.compareTo(((MWNamedSchemaComponentHandle) o).component);
	}
	
	public void toString(StringBuffer sb) {
		sb.append(this.component.getName());
	}
	
	
	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		
		descriptor.setJavaClass(MWNamedSchemaComponentHandle.class);
		
		descriptor.addDirectMapping("schemaName", "getSchemaNameForTopLink", "setSchemaNameForTopLink", "schema/text()");
		
		XMLCompositeCollectionMapping qNamePathMapping = new XMLCompositeCollectionMapping();
		qNamePathMapping.setAttributeName("qNamePath");
		qNamePathMapping.setReferenceClass(QName.class);
		qNamePathMapping.setGetMethodName("getQNamePathForTopLink");
		qNamePathMapping.setSetMethodName("setQNamePathForTopLink");
		qNamePathMapping.setXPath("qname-path/qname");
		descriptor.addMapping(qNamePathMapping);
		
		return descriptor;
	}
	
	private String getSchemaNameForTopLink(){
		return (this.component == null) ? null : this.component.getSchema().getName();
	}

	private void setSchemaNameForTopLink(String schemaName) {
		this.schemaName = schemaName;
	}

	private List getQNamePathForTopLink() {
		List path = new ArrayList();
		for (MWNamedSchemaComponent temp = this.component; temp != null; ) {
			path.add(0, new QName(temp));
			temp = temp.parentNamedComponent();
		}
		return path;
	}
	
	private void setQNamePathForTopLink(List path) {
		this.qNamePath = path;
	}

}
