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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.CompositeCollectionMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.CompositeObjectMappingNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.XmlMappingSelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.XmlTransformationMappingNode;


public abstract class XmlDescriptorNode 
	extends MappingDescriptorNode 
{

	protected XmlDescriptorNode(MWXmlDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}

	public GroupContainerDescription buildMenuDescription(WorkbenchContext context) {
		GroupContainerDescription desc = super.buildMenuDescription(context);
		context = buildLocalWorkbenchContext(context);
		
		MenuGroupDescription classActionGroup =  new MenuGroupDescription();
		classActionGroup.add(getMappingsPlugin().getRefreshClassesAction(context));
		classActionGroup.add(getMappingsPlugin().getAddOrRefreshClassesAction(context));
		classActionGroup.add(getMappingsPlugin().getCreateNewClassAction(context));
		desc.add(classActionGroup);
				
		MenuGroupDescription removeRenameGroup = new MenuGroupDescription();
		removeRenameGroup.add(getRemoveDescriptorAction(context));
		removeRenameGroup.add(getRenameDescriptorAction(context));
		removeRenameGroup.add(getMoveDescriptorAction(context));
		desc.add(removeRenameGroup);
		
		MenuGroupDescription mappingGroup = new MenuGroupDescription();
		mappingGroup.add(buildMapInheritedAttributesMenuDescription(context));
		mappingGroup.add(buildUnmapMenuDescription(context));
		desc.add(mappingGroup);
		
		MenuGroupDescription exportJavaGroup = new MenuGroupDescription();
		exportJavaGroup.add(getMappingsPlugin().getExportSpecificDescriptorModelJavaSourceAction(context));
		desc.add(exportJavaGroup);
		
		desc.add(buildOracleHelpMenuGroup(context));
		
		return desc;		
	}

	// ************** MappingDescriptorNode implementation ************

	protected abstract XmlMappingSelectionActionsPolicy buildMappingSelectionActionsPolicy();

	protected MappingNode buildMappingNode(MWMapping mapping) {
		if (mapping instanceof MWCompositeObjectMapping) {
			return new CompositeObjectMappingNode(
				(MWCompositeObjectMapping) mapping,
				buildMappingSelectionActionsPolicy(), 
				this
			);
		}
		else if (mapping instanceof MWCompositeCollectionMapping) {
			return new CompositeCollectionMappingNode(
				(MWCompositeCollectionMapping) mapping,
				buildMappingSelectionActionsPolicy(), 
				this
			);
		}
		else if (mapping instanceof MWXmlTransformationMapping) {
			return new XmlTransformationMappingNode(
				(MWXmlTransformationMapping) mapping,
				buildMappingSelectionActionsPolicy(), 
				this
			);
		}
		else {
			throw new IllegalArgumentException(mapping.toString());
		}
	}

	public String mappingHelpTopicPrefix() {
		return "mapping.xml";
	}

	public abstract Class propertiesPageClassForCompositeCollectionMapping();
	
	public abstract Class propertiesPageClassForCompositeObjectMapping();

	public abstract Class propertiesPageClassForTransformationMapping();
	
}
