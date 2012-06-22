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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyAttributeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlCollectionReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFragmentMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlObjectReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UnmappedMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.AnyAttributeMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.AnyCollectionMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.AnyObjectMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OXCompositeCollectionMappingPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OXCompositeObjectMappingPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OXMappingSelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OXTransformationMappingPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OxDirectCollectionMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.OxDirectMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.UiMappingXmlBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.XmlCollectionReferenceMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.XmlFragmentCollectionMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.XmlFragmentMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.XmlMappingSelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.XmlObjectReferenceMappingNode;


public final class OXDescriptorNode extends XmlDescriptorNode {


	// ********** constructors/initialization **********
	
	public OXDescriptorNode(MWOXDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}
		
	protected XmlMappingSelectionActionsPolicy buildMappingSelectionActionsPolicy() {
		return new OXMappingSelectionActionsPolicy(getMappingsPlugin());
	}
	
	
	// ********** ApplicationNode implementation **********

	public String helpTopicID() {
		return "descriptor.ox";
	}

	public String buildIconKey() {
		return "descriptor.ox";
	}

	// ********** DescriptorNode implementation **********

	protected boolean supportsDescriptorMorphing() {
		return false;
	}

	protected String accessibleNameKey() {
		return "ACCESSIBLE_XML_DESCRIPTOR_NODE";
	}

	// ********** MWApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return OXDescriptorTabbedPropertiesPage.class;
	}

	
	// ********** DescriptorNode overrides **********
	
	public boolean supportsEventsPolicy() {
		return false;
	}
	
	protected MappingNode buildMappingNode(MWMapping mapping) {
		if (mapping instanceof MWAnyObjectMapping) {
			return new AnyObjectMappingNode((MWAnyObjectMapping) mapping, this.buildMappingSelectionActionsPolicy(), this);
		}
		else if (mapping instanceof MWAnyCollectionMapping) {
			return new AnyCollectionMappingNode((MWAnyCollectionMapping) mapping, this.buildMappingSelectionActionsPolicy(), this);
		}
		else if (mapping instanceof MWAnyAttributeMapping) {
			return new AnyAttributeMappingNode((MWAnyAttributeMapping) mapping, this.buildMappingSelectionActionsPolicy(), this);
		}
		else if (mapping instanceof MWXmlObjectReferenceMapping) {
			return new XmlObjectReferenceMappingNode((MWXmlObjectReferenceMapping) mapping, this.buildMappingSelectionActionsPolicy(), this);
		}
		else if (mapping instanceof MWXmlCollectionReferenceMapping) {
			return new XmlCollectionReferenceMappingNode((MWXmlCollectionReferenceMapping) mapping, this.buildMappingSelectionActionsPolicy(), this);
		}
		else if (mapping instanceof MWXmlFragmentMapping) {
			return new XmlFragmentMappingNode((MWXmlFragmentMapping) mapping, this.buildMappingSelectionActionsPolicy(), this);
		}
		else if (mapping instanceof MWXmlFragmentCollectionMapping) {
			return new XmlFragmentCollectionMappingNode((MWXmlFragmentCollectionMapping) mapping, this.buildMappingSelectionActionsPolicy(), this);
		}
		else if (mapping instanceof MWXmlDirectCollectionMapping) {
			return new OxDirectCollectionMappingNode((MWXmlDirectCollectionMapping) mapping, this.buildMappingSelectionActionsPolicy(), this);
		}		
		else if (mapping instanceof MWXmlDirectMapping) {
			return new OxDirectMappingNode((MWXmlDirectMapping) mapping, this.buildMappingSelectionActionsPolicy(), this);
		}		
		else {
			return super.buildMappingNode(mapping);
		}
	}
	
	protected MappingNode buildUnmappedMappingNode(MWClassAttribute attribute) {
		ApplicationContext ctx2 = this.getApplicationContext().buildExpandedResourceRepositoryContext(UiMappingXmlBundle.class);
		return new UnmappedMappingNode(attribute, ctx2, new OXMappingSelectionActionsPolicy(getMappingsPlugin()), this);
	}

	// ********** XmlDescriptorNode overrides **********

	public Class propertiesPageClassForCompositeCollectionMapping() {
		return OXCompositeCollectionMappingPropertiesPage.class;
	}

	@Override
	public Class propertiesPageClassForCompositeObjectMapping() {
		return OXCompositeObjectMappingPropertiesPage.class;
	}

	public Class propertiesPageClassForTransformationMapping() {
		return OXTransformationMappingPropertiesPage.class;
	}
}
