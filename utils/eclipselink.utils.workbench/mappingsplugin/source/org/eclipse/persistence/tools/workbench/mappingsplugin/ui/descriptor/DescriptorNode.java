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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ActiveIconBuilder;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.IconBuilder;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.RootMenuDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsplugin.AutomappableNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.RemovableNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.RemoveAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.MappingsApplicationNode;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;



// TODO Should a descriptor be "dirty" or indicate it has "problems" if
// its class is "dirty" or has "problems"?
// A visible class repository would eliminate this dilemma.
// Note that when a descriptor appears "dirty" because its class is
// "dirty" the descriptor file won't be written out when we save.
public abstract class DescriptorNode
	extends MappingsApplicationNode
	implements UnmappablePackageNode, AutomappableNode, RemovableNode
{
	protected static final String[] DESCRIPTOR_DISPLAY_STRING_PROPERTY_NAMES = {
			MWDescriptor.NAME_PROPERTY
	};

	protected static final String[] DESCRIPTOR_ICON_PROPERTY_NAMES = {
			Node.HAS_BRANCH_PROBLEMS_PROPERTY,
			MWDescriptor.ACTIVE_PROPERTY
	};


	// ********** constructors/initialization **********

	protected DescriptorNode(MWDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode, parentNode.getPlugin(), parentNode.getApplicationContext());
	}



	//	********** ApplicationNode implementation **********

	private ToggleFrameworkAction getActivateDescriptorAction(WorkbenchContext context) {
		return new ActivateDescriptorAction(context);
	}
	
	protected FrameworkAction getRemoveDescriptorAction(WorkbenchContext context) {
		return new RemoveAction(context, "descriptor.remove");
	}
	
	protected FrameworkAction getRenameDescriptorAction(WorkbenchContext context) {
		return new RenameDescriptorAction(context);
	}

	protected FrameworkAction getMoveDescriptorAction(WorkbenchContext context) {
		return new MoveDescriptorAction(context);
	}


	// ************ AbstractApplicationNode overrides *************

	public String accessibleName() {
		return resourceRepository().getString(accessibleNameKey(), displayString());
	}

	protected abstract String accessibleNameKey();

	protected String[] displayStringPropertyNames() {
		return DESCRIPTOR_DISPLAY_STRING_PROPERTY_NAMES;
	}

	/**
	 * listen to the descriptor's 'active' property in addition
	 * to its 'has branch problems' property
	 */
	protected String[] iconPropertyNames() {
		return DESCRIPTOR_ICON_PROPERTY_NAMES;
	}

	/**
	 * dim the icon if the descriptor is inactive
	 */
	protected IconBuilder buildIconBuilder() {
		return new ActiveIconBuilder(super.buildIconBuilder(), this.getDescriptor().isActive());
	}


	// ************ MWApplicationNode overrides ***********

	public String candidatePackageName() {
		return this.getDescriptor().packageName();
	}

	public boolean isAutoMappable() {
		return true;
	}

	public Iterator descriptors() {
		return new SingleElementIterator(this.getDescriptor());
	}

	public void addDescriptorsTo(Collection descriptors) {
		descriptors.add(this.getDescriptor());
	}


	// ********** AutomappableNode implementation **********

	public String getAutomapSuccessfulStringKey() {
		return "AUTOMAP_DESCRIPTOR_SUCCESSFUL";
	}


	// ********** UnmappablePackageNode implementation **********

	public void unmapEntirePackage() {
		this.getPackageNode().unmap();
	}

	protected DescriptorPackageNode getPackageNode() {
		return (DescriptorPackageNode) this.getParent();
	}

	public void unmap() {
		this.getDescriptor().unmap();
	}
	
	
	// ********** Removable implementation **********
	
	public String getName() {
		return this.getDescriptor().getName();
	}
	
	public void remove() {
		this.getDescriptor().getProject().removeDescriptor(this.getDescriptor());
	}


	// ********** public API ***********

	public MWDescriptor getDescriptor() {
		return (MWDescriptor) this.getValue();
	}

	public MWClass type() {
		return this.getDescriptor().getMWClass();
	}
	
	protected MWTransactionalPolicy transactionalPolicy() {
		return this.getDescriptor().getTransactionalPolicy();
	}

	public GroupContainerDescription buildMenuDescription(WorkbenchContext context) {
		context = this.buildLocalWorkbenchContext(context);
		RootMenuDescription desc = new RootMenuDescription();
		MenuGroupDescription groupDesc = new MenuGroupDescription();
		groupDesc.add(this.getActivateDescriptorAction(context));
		if (this.supportsDescriptorMorphing()) {
			groupDesc.add(this.buildDescriptorTypeMenuDescription(context));
		}
		if (supportsAdvancedProperties()) {
			groupDesc.add(this.getAdvancedPolicyAction(context));
		}
		desc.add(groupDesc);

		return desc;
	}

	protected MenuDescription buildDescriptorTypeMenuDescription(WorkbenchContext workbenchContext) {
		MenuDescription menuDesc = 
			new MenuDescription(
					this.resourceRepository().getString("DESCRIPTOR_TYPE_MENU_ITEM"), 
					this.resourceRepository().getString("DESCRIPTOR_TYPE_MENU_ITEM"), 
					this.resourceRepository().getMnemonic("DESCRIPTOR_TYPE_MENU_ITEM"),
					EMPTY_ICON
			);
		menuDesc.add(this.buildDescriptorTypeMenuGroupDescription(workbenchContext));
	
		return menuDesc;
	}
	
	/** return whether this descriptor supports any advanced descriptor policies**/
	protected abstract boolean supportsAdvancedProperties();

	//Do not cache this action in the plugin.  It is built with different resource
	//strings for relational, eis, and ox descriptors.  Can't cache at the node level
	//because then the WorkbenchContext (ie the current window) will not be correct
	protected FrameworkAction getAdvancedPolicyAction(WorkbenchContext context) {
		return new AdvancedPolicyAction(context);
	}

	/** return whether this descriptor can be morphed to other descriptor type **/
	protected abstract boolean supportsDescriptorMorphing();
	
	/** must override this method if supportsDescriptorMorphing() returns true **/
	protected MenuGroupDescription buildDescriptorTypeMenuGroupDescription(WorkbenchContext workbenchContext) {
		throw new UnsupportedOperationException("This descriptor does not support morphing");
	}

	/** return whether the node's descriptor is active **/
	public boolean isActive() {
		return this.getDescriptor().isActive();
	}

	public boolean supportsEventsPolicy() {
		return true;
	}

	public boolean supportsInterfaceAliasPolicy() {
		return false;	
	}
	
	public boolean supportsTransactionalDescriptorProperties() {
		return false;
	}

	public boolean isAggregateDescriptor() {
		return false;
	}

	public boolean isTableDescriptor() {
		return false;
	}

}
