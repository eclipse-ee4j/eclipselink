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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarButtonGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.UnmappedMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisCompositeCollectionMappingPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisCompositeObjectMappingPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisDirectCollectionMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisDirectMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisMappingSelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisOneToManyMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisOneToOneMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisTransformationMappingPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.UiMappingXmlBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.XmlMappingSelectionActionsPolicy;


abstract class EisDescriptorNode extends XmlDescriptorNode 
{	
	
	protected EisDescriptorNode(MWEisDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}

	protected XmlMappingSelectionActionsPolicy buildMappingSelectionActionsPolicy() {
		return new EisMappingSelectionActionsPolicy(getMappingsPlugin());
	}
	
	protected MappingNode buildMappingNode(MWMapping mapping) {
		if (mapping instanceof MWEisOneToOneMapping) {
			return new EisOneToOneMappingNode(
				(MWEisOneToOneMapping) mapping, 
				buildMappingSelectionActionsPolicy(), 
				this
			);
		}
		else if (mapping instanceof MWEisOneToManyMapping) {
			return new EisOneToManyMappingNode(
				(MWEisOneToManyMapping) mapping, 
				buildMappingSelectionActionsPolicy(), 
				this
			);
		}
		else if (mapping instanceof MWXmlDirectCollectionMapping) {
			return new EisDirectCollectionMappingNode(
				(MWXmlDirectCollectionMapping) mapping, 
				buildMappingSelectionActionsPolicy(), 
				this
			);
		}
		else if (mapping instanceof MWXmlDirectMapping) {
			return new EisDirectMappingNode(
				(MWXmlDirectMapping) mapping, 
				buildMappingSelectionActionsPolicy(), 
				this
			);
		}

		return super.buildMappingNode(mapping);
	}

	public String mappingHelpTopicPrefix() {
		return "mapping.eis";
	}

	public Class propertiesPageClassForCompositeCollectionMapping() {
		return EisCompositeCollectionMappingPropertiesPage.class;
	}
	
	@Override
	public Class propertiesPageClassForCompositeObjectMapping() {
		return EisCompositeObjectMappingPropertiesPage.class;
	}

	public Class propertiesPageClassForTransformationMapping() {
		return EisTransformationMappingPropertiesPage.class;
	}

	protected boolean supportsDescriptorMorphing() {
		return true;
	}
	
	protected MenuGroupDescription buildDescriptorTypeMenuGroupDescription(WorkbenchContext workbenchContext) {
		MenuGroupDescription typeDesc = new MenuGroupDescription();
		typeDesc.add(this.getEisRootDescriptorAction(workbenchContext));
		typeDesc.add(this.getEisCompositeDescriptorAction(workbenchContext));
		return typeDesc;
	}
	
	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext)
	{
		WorkbenchContext wrappedContext = buildLocalWorkbenchContext(workbenchContext);

		GroupContainerDescription desc = super.buildToolBarDescription(workbenchContext);
		ToolBarButtonGroupDescription eisDescTypeGroup = new ToolBarButtonGroupDescription();
		eisDescTypeGroup.add(getEisRootDescriptorAction(wrappedContext));
		eisDescTypeGroup.add(getEisCompositeDescriptorAction(wrappedContext));
		desc.add(eisDescTypeGroup);
		
		return desc;
	}
	
	private ToggleFrameworkAction getEisRootDescriptorAction(WorkbenchContext workbenchContext)
	{
		return new EisRootDescriptorAction(workbenchContext);
	}
	
	private ToggleFrameworkAction getEisCompositeDescriptorAction(WorkbenchContext workbenchContext)
	{
		return new EisCompositeDescriptorAction(workbenchContext);
	}
	
	protected MappingNode buildUnmappedMappingNode(MWClassAttribute attribute) {
		ApplicationContext ctx2 = this.getApplicationContext().buildExpandedResourceRepositoryContext(UiMappingXmlBundle.class);
		return new UnmappedMappingNode(attribute, ctx2, new EisMappingSelectionActionsPolicy(getMappingsPlugin()), this);
	}
	
	
	public boolean isRootDescriptor() {
		return false;
	}
	
	public boolean isCompositeDescriptor() {
		return false;
	}
}
