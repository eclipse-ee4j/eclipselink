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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuDescription;
import org.eclipse.persistence.tools.workbench.framework.app.MenuGroupDescription;
import org.eclipse.persistence.tools.workbench.framework.app.NavigatorSelectionModel;
import org.eclipse.persistence.tools.workbench.framework.app.ToolBarDescription;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWModifiable;
import org.eclipse.persistence.tools.workbench.mappingsplugin.AutomapAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CompositeCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


public abstract class MappingDescriptorNode
	extends DescriptorNode
{
	/**
	 * this holds both mapped and unmapped nodes
	 */
	private ListValueModel childrenModel;

	/**
	 * we control the mapping nodes directly by listening to the
	 * descriptor's 'mappings' collection
	 */
	private SimpleCollectionValueModel mappingNodesHolder;

	/**
	 * we control the unmapped nodes directly by listening to the
	 * 'inherited attributes', 'attributes', and 'EJB 2.0 attributes' collections
	 */
	private SimpleCollectionValueModel unmappedMappingNodesHolder;

	/**
	 * this listens for mappings being added to or removed from
	 * the descriptor
	 */
	private CollectionChangeListener mappingsListener;

	/**
	 * this listens for attributes being added or removed
	 * (to or from 3 different collections...)
	 */
	private CollectionChangeListener attributesListener;

	/**
	 * this listens for an attribute's modifier being set
	 * to static or final
	 */
	private PropertyChangeListener attributeModifierListener;

	/**
	 * this listens for the stupid "unknown primary key thing"
	 */
	private PropertyChangeListener unknownPrimaryKeyListener;


	// ********** constructor/initialization **********

	protected MappingDescriptorNode(MWDescriptor descriptor, DescriptorPackageNode parentNode) {
		super(descriptor, parentNode);
	}

	protected void initialize() {
		super.initialize();
		this.mappingsListener = this.buildMappingsListener();
		this.attributesListener = this.buildAttributesListener();
		this.attributeModifierListener = this.buildAttributeModifierListener();
		this.unknownPrimaryKeyListener = this.buildUnknownPrimaryKeyListener();
		this.mappingNodesHolder = new SimpleCollectionValueModel();
		this.unmappedMappingNodesHolder = new SimpleCollectionValueModel();
		this.childrenModel = this.buildChildrenModel();
	}

	private CollectionChangeListener buildMappingsListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				MappingDescriptorNode.this.mappingsAdded(e);
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				MappingDescriptorNode.this.mappingsRemoved(e);
			}
			public void collectionChanged(CollectionChangeEvent e) {
				MappingDescriptorNode.this.mappingsChanged(e);
			}
			public String toString() {
				return "mappings listener";
			}
		};
	}

	private CollectionChangeListener buildAttributesListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				MappingDescriptorNode.this.attributesAdded(e);
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				MappingDescriptorNode.this.attributesRemoved(e);
			}
			public void collectionChanged(CollectionChangeEvent e) {
				// since we listen to the model directly, and not through an
				// adapter, this "event" should never happen  ~bjv
				throw new UnsupportedOperationException();
			}
			public String toString() {
				return "attributes listener";
			}
		};
	}

	private PropertyChangeListener buildAttributeModifierListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				MappingDescriptorNode.this.attributeModifierChanged(e);
			}
			public String toString() {
				return "attribute modifier listener";
			}
		};
	}

	private PropertyChangeListener buildUnknownPrimaryKeyListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				MappingDescriptorNode.this.unknownPrimaryKeyChanged(e);
			}
			public String toString() {
				return "unknown primary key listener";
			}
		};
	}

	protected ListValueModel buildChildrenModel() {
		return new SortedListValueModelAdapter(this.buildDisplayStringAdapter());
	}

	/**
	 * the display string (name) of each mapping node can change
	 */
	protected ListValueModel buildDisplayStringAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildUnsortedChildrenModel(), DISPLAY_STRING_PROPERTY);
	}

	private CollectionValueModel buildUnsortedChildrenModel() {
		CollectionValueModel container = new SimpleCollectionValueModel();
		container.addItem(this.mappingNodesHolder);
		container.addItem(this.unmappedMappingNodesHolder);
		return new CompositeCollectionValueModel(container, Transformer.NULL_INSTANCE);
	}


	// ********** queries **********

	public MWMappingDescriptor getMappingDescriptor() {
		return (MWMappingDescriptor) this.getDescriptor();
	}

	private Iterator mappingNodes() {
		return (Iterator) this.mappingNodesHolder.getValue();
	}

	private MappingNode mappingNodeFor(MWMapping mapping) {
		for (Iterator stream = this.mappingNodes(); stream.hasNext(); ) {
			MappingNode node = (MappingNode) stream.next();
			if (node.getValue() == mapping) {
				return node;
			}
		}
		return null;
	}

	private MappingNode mappingNodeFor(MWClassAttribute attribute) {
		for (Iterator stream = this.mappingNodes(); stream.hasNext(); ) {
			MappingNode node = (MappingNode) stream.next();
			if (node.instanceVariable() == attribute) {
				return node;
			}
		}
		return null;
	}

	private Iterator unmappedMappingNodes() {
		return (Iterator) this.unmappedMappingNodesHolder.getValue();
	}

	private MappingNode unmappedMappingNodeFor(MWClassAttribute attribute) {
		for (Iterator stream = this.unmappedMappingNodes(); stream.hasNext(); ) {
			MappingNode node = (MappingNode) stream.next();
			if (node.instanceVariable() == attribute) {
				return node;
			}
		}
		return null;
	}

	/**
	 * filter the specified attributes, returning only the attributes that
	 * do NOT have a mapping in the descriptor
	 */
	private Iterator unmappedAttributes(Iterator attributes) {
		return new FilteringIterator(attributes) {
			protected boolean accept(Object o) {
				MWMappingDescriptor descriptor = MappingDescriptorNode.this.getMappingDescriptor();
				return descriptor.mappingForAttribute((MWClassAttribute) o) == null;
			}
		};
	}

	public abstract String mappingHelpTopicPrefix();


	// ********** behavior **********

	/**
	 * mappings were added to the descriptor;
	 * remove the unmapped nodes and add nodes for the new mappings
	 */
	void mappingsAdded(CollectionChangeEvent e) {
		this.removeUnmappedMappingNodesFor(this.attributes(e.items()));
		this.addMappingNodesFor(e.items());
	}

	private void removeUnmappedMappingNodesFor(Iterator attributes) {
		while (attributes.hasNext()) {
			this.removeUnmappedMappingNodeFor((MWClassAttribute) attributes.next());
		}
	}

	private void removeUnmappedMappingNodeFor(MWClassAttribute attribute) {
		this.unmappedMappingNodesHolder.removeItem(this.unmappedMappingNodeFor(attribute));
	}

	private void addMappingNodesFor(Iterator mappings) {
		while (mappings.hasNext()) {
			this.addMappingNodeFor((MWMapping) mappings.next());
		}
	}

	private void addMappingNodeFor(MWMapping mapping) {
		this.mappingNodesHolder.addItem(this.buildMappingNode(mapping));
	}

	/**
	 * mappings were removed from the descriptor;
	 * in each case, either the attribute was removed or it was "unmapped"
	 */
	void mappingsRemoved(CollectionChangeEvent e) {
		this.removeMappingNodesFor(e.items());
		for (Iterator stream = this.attributes(e.items()); stream.hasNext(); ) {
			MWClassAttribute attribute = (MWClassAttribute) stream.next();
			String attributeName = attribute.getName();
			// if the attribute remains, add an "unmapped" mapping node
			if (this.type().containsCombinedAttributeNamed(attributeName) ||
							this.getMappingDescriptor().containsInheritedAttributeNamed(attributeName)) {
				this.addUnmappedMappingNodeFor(attribute);
			}
		}
	}

	private void removeMappingNodesFor(Iterator mappings) {
		while (mappings.hasNext()) {
			this.removeMappingNodeFor((MWMapping) mappings.next());
		}
	}

	private void removeMappingNodeFor(MWMapping mapping) {
		this.mappingNodesHolder.removeItem(this.mappingNodeFor(mapping));
	}

	private void addUnmappedMappingNodeFor(MWClassAttribute attribute) {
		this.unmappedMappingNodesHolder.addItem(this.buildUnmappedMappingNode(attribute));
	}

	/**
	 * return the attributes for the specified mappings
	 */
	private Iterator attributes(Iterator mappings) {
		return new TransformationIterator(mappings) {
			protected Object transform(Object next) {
				return ((MWMapping) next).getInstanceVariable();
			}
		};
	}

	/**
	 * the descriptor's mappings were changed in some unknown fashion;
	 * rebuild all the mapping nodes
	 */
	void mappingsChanged(CollectionChangeEvent e) {
		this.rebuildAllMappingNodes();
	}

	void attributesAdded(CollectionChangeEvent e) {
		this.engageAttributes(e.items());
		this.addUnmappedMappingNodesFor(e.items());
	}

	private void addUnmappedMappingNodesFor(Iterator attributes) {
		while (attributes.hasNext()) {
			this.addUnmappedMappingNodeFor((MWClassAttribute) attributes.next());
		}
	}

	void attributesRemoved(CollectionChangeEvent e) {
		this.removeUnmappedMappingNodesFor(e.items());
		this.disengageAttributes(e.items());
	}

	private void rebuildAllMappingNodes() {
		this.clearAllMappingNodes();
		this.addAllMappingNodes();
	}

	private void clearAllMappingNodes() {
		this.mappingNodesHolder.clear();
		this.unmappedMappingNodesHolder.clear();
	}

	private void addAllMappingNodes() {
		MWMappingDescriptor descriptor = this.getMappingDescriptor();
		MWClass type = descriptor.getMWClass();
		this.addMappingNodesFor(descriptor.mappings());
		this.addUnmappedMappingNodesFor(this.unmappedAttributes(type.attributes()));
		this.addUnmappedMappingNodesFor(this.unmappedAttributes(type.ejb20Attributes()));
		this.addUnmappedMappingNodesFor(this.unmappedAttributes(descriptor.inheritedAttributes()));
	}

	/**
	 * an attribute's modifier changed;
	 * this may affect whether we keep an "unmapped" mapping node;
	 * "mapped" mapping nodes are handled by the properties page directly,
	 * by prompting the user as to whether it is OK to remove a mapping
	 * when the attribute is no longer "mappable"
	 */
	void attributeModifierChanged(PropertyChangeEvent e) {
		MWClassAttribute attribute = (MWClassAttribute) e.getSource();
		if (this.mappingNodeFor(attribute) != null) {
			// the validation thread will tell the user the modifier is invalid
			return;
		}
		int oldCode = ((Integer) e.getOldValue()).intValue();
		int newCode = ((Integer) e.getNewValue()).intValue();
		this.checkModifier(attribute, Modifier.isStatic(oldCode), Modifier.isStatic(newCode));
		this.checkModifier(attribute, Modifier.isFinal(oldCode), Modifier.isFinal(newCode));
	}

	/**
	 * check whether the specified modifier has changed in a way that
	 * allows the specified attribute to be mapped, or vice-versa
	 * (not sure what the best way is to handle all the negative logic...)
	 */
	private void checkModifier(MWClassAttribute attribute, boolean oldModifierIsNotMappable, boolean newModifierIsNotMappable) {
		if (oldModifierIsNotMappable) {
			if ( ! newModifierIsNotMappable) {
				// unmappable -> mappable (e.g. static -> non-static)
				// a previous modifier check might have already added the "unmapped" mapping node
				if (this.unmappedMappingNodeFor(attribute) == null) {
					this.addUnmappedMappingNodeFor(attribute);
				}
			}
		} else {
			if (newModifierIsNotMappable) {
				// mappable -> unmappable (e.g. non-static -> static)
				this.removeUnmappedMappingNodeFor(attribute);
			}
		}
	}

	void unknownPrimaryKeyChanged(PropertyChangeEvent e) {
		if (e.getNewValue() == null) {
			this.removeUnmappedMappingNodeFor((MWClassAttribute) e.getOldValue());
		}
	}

	public void selectMethod(MWMethod method, WorkbenchContext context) {
		((MappingDescriptorTabbedPropertiesPage) context.getPropertiesPage()).selectMethod(method);
	}

	public void selectMappingNodeFor(MWClassAttribute attribute, NavigatorSelectionModel nsm) {
		MWMapping mapping = this.getMappingDescriptor().mappingForAttribute(attribute);
		this.selectMappingNodeFor(mapping, nsm);
	}

	public void selectMappingNodeFor(MWMapping mapping, NavigatorSelectionModel nsm) {
		this.selectDescendantNodeForValue(mapping, nsm); 
	}


	// ********** factories **********

	/**
	 * Build the appropriate mapping node for the specified mapping.
	 */
	protected abstract MappingNode buildMappingNode(MWMapping mapping);

	/**
	 * Build the appropriate mapping node for the specified unmapped attribute.
	 */
	protected abstract MappingNode buildUnmappedMappingNode(MWClassAttribute attribute);


	// ********** AbstractApplicationNode overrides **********

	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}

	protected void engageValue() {
		super.engageValue();

		MWMappingDescriptor descriptor = this.getMappingDescriptor();
		MWClass type = descriptor.getMWClass();

		descriptor.addCollectionChangeListener(MWMappingDescriptor.MAPPINGS_COLLECTION, this.mappingsListener);
		descriptor.addCollectionChangeListener(MWMappingDescriptor.INHERITED_ATTRIBUTES_COLLECTION, this.attributesListener);

		type.addCollectionChangeListener(MWClass.ATTRIBUTES_COLLECTION, this.attributesListener);
		type.addCollectionChangeListener(MWClass.EJB20_ATTRIBUTES_COLLECTION, this.attributesListener);
		type.addPropertyChangeListener(MWClass.UNKNOWN_PK_ATTRIBUTE_PROPERTY, this.unknownPrimaryKeyListener);

		this.engageAttributes(type.combinedAttributes());
		this.engageAttributes(descriptor.inheritedAttributes());
		
		this.addAllMappingNodes();
	}

	protected void engageAttributes(Iterator attributes) {
		while (attributes.hasNext()) {
			this.engageAttribute((MWClassAttribute) attributes.next());
		}
	}
	
	protected void engageAttribute(MWClassAttribute attribute) {
		attribute.addPropertyChangeListener(MWModifiable.MODIFIER_CODE_PROPERTY, this.attributeModifierListener);		
	}
	
	protected void disengageValue() {
		MWMappingDescriptor descriptor = this.getMappingDescriptor();
		MWClass type = descriptor.getMWClass();

		this.clearAllMappingNodes();

		this.disengageAttributes(descriptor.inheritedAttributes());
		this.disengageAttributes(type.combinedAttributes());
		
		type.removePropertyChangeListener(MWClass.UNKNOWN_PK_ATTRIBUTE_PROPERTY, this.unknownPrimaryKeyListener);
		type.removeCollectionChangeListener(MWClass.EJB20_ATTRIBUTES_COLLECTION, this.attributesListener);
		type.removeCollectionChangeListener(MWClass.ATTRIBUTES_COLLECTION, this.attributesListener);

		descriptor.removeCollectionChangeListener(MWMappingDescriptor.INHERITED_ATTRIBUTES_COLLECTION, this.attributesListener);
		descriptor.removeCollectionChangeListener(MWMappingDescriptor.MAPPINGS_COLLECTION, this.mappingsListener);

		super.disengageValue();
	}

	protected void disengageAttributes(Iterator attributes) {
		while (attributes.hasNext()) {
			this.disengageAttribute((MWClassAttribute) attributes.next());
		}
	}	
	
	protected void disengageAttribute(MWClassAttribute attribute) {
		attribute.removePropertyChangeListener(MWModifiable.MODIFIER_CODE_PROPERTY, this.attributeModifierListener);		
	}


	// ********** DescriptorNode implementation **********

	protected boolean supportsAdvancedProperties() {
		return true;
	}


	// ********** ApplicationNode implementation **********

	public GroupContainerDescription buildToolBarDescription(WorkbenchContext workbenchContext) {
		return new ToolBarDescription();
	}
	
	// ********** actions **********
	
	protected MenuDescription buildMapInheritedAttributesMenuDescription(WorkbenchContext workbenchContext) {
		MenuDescription menuDesc = 
			new MenuDescription(
					this.resourceRepository().getString("MAP_INHERITED_ATTRIBUTES_MENU_ITEM"), 
					this.resourceRepository().getString("MAP_INHERITED_ATTRIBUTES_MENU_ITEM"), 
					this.resourceRepository().getMnemonic("MAP_INHERITED_ATTRIBUTES_MENU_ITEM"),
					EMPTY_ICON
			);

		MenuGroupDescription mapGroupDesc = new MenuGroupDescription();
		mapGroupDesc.add(this.getMapInheritedAttributesToSuperClassAction(workbenchContext));
		mapGroupDesc.add(this.getMapInheritedAttributesToRootMinusOneAction(workbenchContext));
		mapGroupDesc.add(this.getMapInheritedAttributesToSelectedClassAction(workbenchContext));
		menuDesc.add(mapGroupDesc);
		
		MenuGroupDescription removeGroupDesc = new MenuGroupDescription();
		removeGroupDesc.add(this.getRemoveInheritedAttributesAction(workbenchContext));
		menuDesc.add(removeGroupDesc);
		
		return menuDesc;
	}
	
	// TODO Should this be moved to DescriptorNode? Will we allow unmap on an interface descriptor?
	protected MenuDescription buildUnmapMenuDescription(WorkbenchContext context) {
		MenuDescription menuDesc =
			new MenuDescription(
					this.resourceRepository().getString("UNMAP_MENU"), 
					this.resourceRepository().getString("UNMAP_MENU"), 
					this.resourceRepository().getMnemonic("UNMAP_MENU"),
					EMPTY_ICON
			);

		MenuGroupDescription groupDesc = new MenuGroupDescription();
		groupDesc.add(this.getUnmapDescriptorAction(context));
		groupDesc.add(this.getUnmapAllDescriptorsInPackageAction(context));
		
		menuDesc.add(groupDesc);
		
		return menuDesc;
	}

	protected AutomapAction getAutomapAction(WorkbenchContext workbenchContext) {
		return this.getMappingsPlugin().getAutomapAction(workbenchContext);
	}

	private FrameworkAction getMapInheritedAttributesToSuperClassAction(WorkbenchContext context) {
		return new MapInheritedAttributesToSuperclassAction(context);
	}
	
	private FrameworkAction getMapInheritedAttributesToRootMinusOneAction(WorkbenchContext context) {
		return new MapInheritedAttributesToRootMinusOneAction(context);
	}
	
	private FrameworkAction getMapInheritedAttributesToSelectedClassAction(WorkbenchContext context) {
		return new MapInheritedAttributesToSelectedClassAction(context);
	}
	
	protected FrameworkAction getRemoveInheritedAttributesAction(WorkbenchContext context) {
		return new RemoveInheritedAttributesAction(context);
	}
	
	// TODO this should probably be in DescriptorNode (InterfaceDescriptors can be unmapped also...)
	private FrameworkAction getUnmapAllDescriptorsInPackageAction(WorkbenchContext context) {
		return new UnmapAllDescriptorsInPackageAction(context);
	}
	
	private FrameworkAction getUnmapDescriptorAction(WorkbenchContext context) {
		return new UnmapDescriptorAction(context);
	}

}
