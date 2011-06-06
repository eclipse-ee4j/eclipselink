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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.SwingConstants;

import org.eclipse.persistence.tools.workbench.framework.app.ActiveIconBuilder;
import org.eclipse.persistence.tools.workbench.framework.app.CompositeIconBuilder;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.IconBuilder;
import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultIconRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPluginIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


/**
 * Listen to the descriptor's 'active' property and
 * enhance the icon to display whether mapping is active and/or inherited.
 * Also listen to the instance variable associated with the mapping
 * and keep the mapping's name in synch with the instance variable's name.
 */
public abstract class MappingNode 
	extends MappingsApplicationNode
{
	/** keeps the mapping's name in synch with the attribute's name */
	private PropertyChangeListener attributeNameListener;

	/** keeps the mapping's label in synch with the attribute's type declaration */
	private PropertyChangeListener attributeTypeDeclarationListener;

	/** used for building menus */
	private SelectionActionsPolicy mappingNodeTypePolicy;

	protected static final String[] MAPPING_DISPLAY_STRING_PROPERTY_NAMES = {MWMapping.NAME_PROPERTY};

	/** cache the inherited icon **/
	private static final Icon INHERITED_ICON = new DefaultIconRepository(new MappingsPluginIconResourceFileNameMap()).getIcon("inherited");


	// ********** constructors/initialization **********

	protected MappingNode(MWModel value, ApplicationContext context, SelectionActionsPolicy mappingNodeTypePolicy, MappingDescriptorNode parent) {
		super(value, parent, parent.getPlugin(), context);
		this.mappingNodeTypePolicy = mappingNodeTypePolicy;
	}

	protected MappingNode(MWModel value, SelectionActionsPolicy mappingNodeTypePolicy, MappingDescriptorNode parent) {
		this(value, parent.getApplicationContext(), mappingNodeTypePolicy, parent);
	}

	protected void initialize() {
		super.initialize();
		this.attributeNameListener = this.buildAttributeNameListener();
		this.attributeTypeDeclarationListener = this.buildAttributeTypeDeclarationListener();
	}

	private PropertyChangeListener buildAttributeNameListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				MappingNode.this.attributeNameChanged();
			}
			public String toString() {
				return "attribute name listener";
			}
		};
	}

	private PropertyChangeListener buildAttributeTypeDeclarationListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				MappingNode.this.attributeTypeDeclarationChanged();
			}
			public String toString() {
				return "attribute type declaration listener";
			}
		};
	}


	// ********** AbstractApplicationNode overrides **********

	protected void engageValue() {
		super.engageValue();
		this.descriptor().addPropertyChangeListener(MWDescriptor.ACTIVE_PROPERTY, this.getValueIconListener());
		this.descriptor().addPropertyChangeListener(MWDescriptor.ACTIVE_PROPERTY, this.getValuePropertiesPageTitleIconListener());
		this.instanceVariable().addPropertyChangeListener(MWClassAttribute.NAME_PROPERTY, this.attributeNameListener);
		this.instanceVariable().addPropertyChangeListener(MWClassAttribute.DECLARATION_PROPERTY, this.attributeTypeDeclarationListener);
	}

	protected void disengageValue() {
		this.instanceVariable().removePropertyChangeListener(MWClassAttribute.DECLARATION_PROPERTY, this.attributeTypeDeclarationListener);
		this.instanceVariable().removePropertyChangeListener(MWClassAttribute.NAME_PROPERTY, this.attributeNameListener);
		this.descriptor().removePropertyChangeListener(MWDescriptor.ACTIVE_PROPERTY, this.getValuePropertiesPageTitleIconListener());
		this.descriptor().removePropertyChangeListener(MWDescriptor.ACTIVE_PROPERTY, this.getValueIconListener());
		super.disengageValue();
	}

	protected String[] displayStringPropertyNames() {
		return MAPPING_DISPLAY_STRING_PROPERTY_NAMES;
	}

	/**
	 * add an up-arrow to the normal icon if the mapping is inherited and
	 * dim the icon if the mapping's descriptor is inactive
	 */
	protected IconBuilder buildIconBuilder() {
		IconBuilder inheritedIconBuilder = new CompositeIconBuilder(
				super.buildIconBuilder(),
				this.mappingIsInherited(),
				INHERITED_ICON,
				-3,	// gap
				SwingConstants.HORIZONTAL,	// orientation
				SwingConstants.CENTER,	// alignment
				null	// description
		);
		return new ActiveIconBuilder(inheritedIconBuilder, this.descriptor().isActive());
	}

	/**
	 * display the attribute type, to make it easier for the user
	 * to map the attribute
	 */
	protected String buildPropertiesPageTitleText() {
		return this.getMapping().nameWithShortType();
	}


	// ********** ApplicationNode implementation **********

	public GroupContainerDescription buildMenuDescription(WorkbenchContext workbenchContext) {
		WorkbenchContext localContext = buildLocalWorkbenchContext(workbenchContext);
		GroupContainerDescription desc =  this.mappingNodeTypePolicy.buildMenuDescription(localContext);
		desc.add(this.buildOracleHelpMenuGroup(localContext));
		return desc;
	}

	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
		return this.mappingNodeTypePolicy.buildToolBarDescription(buildLocalWorkbenchContext(workbenchContext));
	}


	// ********** MappingsApplicationNode overrides **********

	public String candidatePackageName() {
		return this.getDescriptorNode().candidatePackageName();
	}

	public boolean isAutoMappable() {
		return false;
	}

	public Iterator descriptors() {
		return new SingleElementIterator(this.descriptor());
	}

	public void addDescriptorsTo(Collection descriptors) {
		descriptors.add(this.descriptor());
	}


	// ********** convenience methods **********

	public MWMapping getMapping() {
		return (MWMapping) this.getValue();
	}

	public MWClassAttribute instanceVariable() {
		return this.getMapping().getInstanceVariable();
	}

	public MappingDescriptorNode getDescriptorNode() {
		return (MappingDescriptorNode) this.getParent();
	}

	public MWMappingDescriptor descriptor() {
		// we don't always have a mapping
		return this.getDescriptorNode().getMappingDescriptor();
	}

	public boolean mappingIsInherited() {
		return this.getMapping().isInherited();
	}

	public boolean isMapped() {
		return true;
	}

	public boolean isUnmapped() {
		return ! this.isMapped();
	}

	public MWDatabase database() {
		// we don't always have a mapping
		return this.descriptor().getDatabase();
	}


	// ********** behavior **********

	protected void attributeNameChanged() {
		if (this.isMapped()) {
			this.getMapping().setName(this.instanceVariable().getName());
		}
	}

	protected void attributeTypeDeclarationChanged() {
		this.propertiesPageTitleTextChanged();
	}

	void remove() {
		MWMapping mapping = this.getMapping();
		this.descriptor().removeMapping(mapping);
		this.removeInstanceVariable();
	}

	void removeInstanceVariable() {
		MWClassAttribute iv = this.instanceVariable();
		if (this.mappingIsInherited()) {
			this.descriptor().removeInheritedAttribute(iv);
		} else {
			if (iv.isEjb20Attribute()) {
				iv.getDeclaringType().removeEjb20Attribute(iv);
			} else {
				iv.getDeclaringType().removeAttribute(iv);
			}
		}
	}

}
