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
package org.eclipse.persistence.tools.workbench.mappingsmodel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ReadAllQuery;


public abstract class MWModel 
	extends AbstractNodeModel 
	implements MWNode
{
	
	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	protected MWModel() {
		super();
	}

	/**
	 * Every object (except MWProject) must have a parent.
	 * Use this constructor to create a new object - the default
	 * constructor should only be used by TopLink.
	 * @see #initialize(Node)
	 */
	protected MWModel(MWNode parent) {
		super(parent);
	}
	
	
	// ********** convenience methods **********

	/**
	 * Return the object's parent in the containment hierarchy.
	 * Every object except the MWProject will have a parent.
	 */
	// TODO this can be renamed to getParent() when we move to jdk 1.5
	public final MWNode getMWParent() {
		return (MWNode) getParent();
	}

	/**
	 * Do NOT override this method.
	 * Every model object should be able to return the project.
	 */
	public final MWProject getProject() {
		if (this.getParent() == null) {
			try {
				return (MWProject) this;
			} catch (ClassCastException ex) {
				throw new IllegalStateException("This object is missing a parent: " + this);
			}
		}
		return (this.getMWParent()).getProject();
	}

	/** 
	 * Do NOT override this method.
	 * Every model object in a relational project should be able to return the database.
	 */
	public final MWDatabase getDatabase() {
		MWRelationalProject project;
		try {
			project = (MWRelationalProject) this.getProject();
		} catch (ClassCastException cce) {
			throw new UnsupportedOperationException("This object is not in a relational project: " + this);
		}
		return project.getTableRepository();
	}

	/**
	 * Do NOT override this method.
	 * Every model object should be able to return the class repository.
	 */
	public final MWClassRepository getRepository() {
		return this.getProject().getClassRepository();
	}
			
	/**
	 * Do NOT override this method.
	 * Every model object should be able to fetch types.
	 * This returns a type that may be only a stub
	 * (i.e. it does not have any attributes, methods, etc. populated).
	 * 
	 * Typically, this is the best method to call when you just need
	 * a class (as opposed to an attribute or method). If you need
	 * a fully-populated type, call this method to get the type,
	 * check whether the type is a stub, and, if it is a stub, refresh it.
	 * This may be require some interaction with the user.
	 * 
	 * You may not *always* want to refresh a non-stub type,
	 * since that would probably drop any changes entered manually
	 * by the user since the last refresh.
	 */
	public final MWClass typeNamed(String typeName) {
		return this.getRepository().typeNamedInternal(typeName);
	}
	
	/**
	 * Do NOT override this method.
	 * Every model object should be able to fetch types.
	 * This returns a type that may be only a stub
	 * (i.e. it does not have any attributes, methods, etc. populated).
	 * 
	 * Typically, this is the best method to call when you just need
	 * a class (as opposed to an attribute or method). If you need
	 * a fully-populated type, call this method to get the type,
	 * check whether the type is a stub, and, if it is a stub, refresh it.
	 * This may be require some interaction with the user.
	 * 
	 * You may not *always* want to refresh a non-stub type,
	 * since that would probably drop any changes entered manually
	 * by the user since the last refresh.
	 */
	public final MWClass typeFor(Class javaClass) {
		return this.typeNamed(javaClass.getName());
	}


	// ********** model synchronization support **********

	/**
	 * Convenience method.
	 * Remove the specified node from the specified bound collection,
	 * firing the appropriate change event if necessary and
	 * launching the cascading #nodeRemoved() mechanism.
	 * Return true if the collection changed - @see java.util.Collection.remove(Object).
	 */
	protected boolean removeNodeFromCollection(Node node, Collection collection, String collectionName) {
		boolean changed = this.removeItemFromCollection(node, collection, collectionName);
		if (changed) {
			this.getProject().nodeRemoved(node);
		}
		return changed;
	}

	/**
	 * Convenience method.
	 * Remove the specified node from the specified collection
	 * and launch the cascading #nodeRemoved() mechanism.
	 * No change event is fired.
	 * Return true if the collection changed - @see java.util.Collection.remove(Object).
	 */
	protected boolean removeNodeFromCollection(Node node, Collection collection) {
		boolean changed = collection.remove(node);
		if (changed) {
			this.getProject().nodeRemoved(node);
		}
		return changed;
	}
	
	/**
	 * Convenience method.
	 * Launch the cascading #nodeRemoved() mechanism for the specified nodes.
	 */
	protected void nodesRemoved(Collection removedNodes) {
		this.nodesRemoved(removedNodes.iterator());
	}

	/**
	 * Convenience method.
	 * Launch the cascading #nodeRemoved() mechanism for the specified nodes.
	 */
	protected void nodesRemoved(Iterator removedNodes) {
		while (removedNodes.hasNext()) {
			this.getProject().nodeRemoved((Node) removedNodes.next());
		}
	}

	/**
	 * Convenience method.
	 * Remove the specified nodes from the specified bound collection,
	 * firing the appropriate change event if necessary and
	 * launching the cascading #nodeRemoved() mechanism.
	 * Return true if the collection changed - @see java.util.Collection.remove(Object).
	 */
	protected boolean removeNodesFromCollection(Collection nodes, Collection collection, String collectionName) {
		return this.removeNodesFromCollection(nodes.iterator(), collection, collectionName);
	}
	
	/**
	 * Convenience method.
	 * Remove the specified nodes from the specified bound collection,
	 * firing the appropriate change event if necessary,
	 * and launch the cascading #nodeRemoved() mechanism.
	 * Return true if the collection changed - @see java.util.Collection.remove(Object).
	 */
	protected boolean removeNodesFromCollection(Iterator nodes, Collection collection, String collectionName) {
		Collection removedNodes = CollectionTools.collection(nodes);
		removedNodes.retainAll(collection);
		boolean changed = this.removeItemsFromCollection(nodes, collection, collectionName);
		this.nodesRemoved(removedNodes);
		return changed;
	}

	/**
	 * Convenience method.
	 * Remove the specified nodes from the specified collection
	 * and launch the cascading #nodeRemoved() mechanism.
	 * No change event is fired.
	 * Return true if the collection changed - @see java.util.Collection.remove(Object).
	 */
	protected boolean removeNodesFromCollection(Collection nodes, Collection collection) {
		return this.removeNodesFromCollection(nodes.iterator(), collection);
	}
	
	/**
	 * Convenience method.
	 * Remove the specified nodes from the specified collection
	 * and launch the cascading #nodeRemoved() mechanism.
	 * No change event is fired.
	 * Return true if the collection changed - @see java.util.Collection.remove(Object).
	 */
	protected boolean removeNodesFromCollection(Iterator nodes, Collection collection) {
		Collection removedNodes = CollectionTools.collection(nodes);
		removedNodes.retainAll(collection);
		boolean changed = collection.removeAll(removedNodes);
		this.nodesRemoved(removedNodes);
		return changed;
	}
	
	/**
	 * Convenience method.
	 * Clear the specified collection, firing the appropriate change event
	 * if necessary and launching the cascading #nodeRemoved() mechanism.
	 */
	protected void clearNodeCollection(Collection nodeCollection, String collectionName) {
		Collection removedNodes = new ArrayList(nodeCollection);
		this.clearCollection(nodeCollection, collectionName);
		this.nodesRemoved(removedNodes);
	}
	
	/**
	 * Convenience method.
	 * Clear the specified collection and launch the cascading #nodeRemoved()
	 * mechanism.
	 * No change event is fired.
	 */
	protected void clearNodeCollection(Collection nodeCollection) {
		Collection removedNodes = new ArrayList(nodeCollection);
		nodeCollection.clear();
		this.nodesRemoved(removedNodes);
	}
	
	/**
	 * Convenience method.
	 * Remove the node specified by index from the specified bound list,
	 * firing the appropriate change event if necessary and
	 * launching the cascading #nodeRemoved() mechanism.
	 * Return the removed object - @see java.util.List.remove(int).
	 */
	protected Object removeNodeFromList(int index, List nodeList, String listName) {
		Node removedNode = (Node) this.removeItemFromList(index, nodeList, listName);
		this.getProject().nodeRemoved(removedNode);
		return removedNode;
	}
	
	/**
	 * Convenience method.
	 * Remove the node specified by index from the specified list
	 * and launch the cascading #nodeRemoved() mechanism.
	 * No change event is fired.
	 * Return the removed object - @see java.util.List.remove(int).
	 */
	protected Object removeNodeFromList(int index, List list) {
		Node removedNode = (Node) list.remove(index);
		this.getProject().nodeRemoved(removedNode);
		return removedNode;
	}
	
	/**
	 * Convenience method.
	 * Clear the specified list, firing the appropriate change event if necessary
	 * and launching the cascading #nodeRemoved() mechanism.
	 */
	protected void clearNodeList(List nodeList, String listName) {
		Collection removedNodes = new ArrayList(nodeList);
		this.clearList(nodeList, listName);
		this.nodesRemoved(removedNodes);
	}

	/**
	 * Convenience method.
	 * Clear the specified list and launch the cascading #nodeRemoved() mechanism.
	 * No change event is fired.
	 */
	protected void clearNodeList(List nodeList) {
		// since no events thrown, same as for collection
		this.clearNodeCollection(nodeList);
	}

	/**
	 * This is called when a mapping is morphed
	 */
	public void mappingReplaced(MWMapping oldMapping, MWMapping newMapping) {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((MWNode) stream.next()).mappingReplaced(oldMapping, newMapping);
		}
	// when you override this method, don't forget to include:
	//	super.mappingReplaced(oldMapping, newMapping);
	}
	
	/**
	 * This called when a descriptor is morphed.
	 */
	public void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((MWNode) stream.next()).descriptorReplaced(oldDescriptor, newDescriptor);
		}
	// when you override this method, don't forget to include:
	//	super.descriptorReplaced(oldDescriptor, newDescriptor);
	}	

	
	/**
	 * Performance tweak:
	 * This is called when a descriptor is unmapped in lieu of 
	 * calling #nodeRemoved(Node) for all the mappings
	 */
	public void descriptorUnmapped(Collection mappings) {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			MWNode child = (MWNode) stream.next();
			child.descriptorUnmapped(mappings);
		}
	// when you override this method, don't forget to include:
	//	super.descriptorUnmapped(mappings);
	}
	
	
	// ********** post-read methods **********
	
	/**
	 * When this method is called, everything in the project has been
	 * read in and initialized by TopLink. Now we can cascade through 
	 * the model and resolve all the class handles and class sub-object
	 * handles. This happens BEFORE the call to #postProjectBuild() below.
	 * @see MWProject#postProjectBuild()
	 */
	public final void resolveClassHandles() {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((MWNode) stream.next()).resolveClassHandles();
		}
	}
	
	/**
	 * When this method is called, everything in the project has been
	 * read in and initialized by TopLink. Now we can cascade through 
	 * the model and resolve all the meta data object (table, schema, 
	 * field, etc.) handles. This happens BEFORE the call to 
	 * #postProjectBuild() below.
	 * @see MWProject#postProjectBuild()
	 */
	public final void resolveMetadataHandles() {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((MWNode) stream.next()).resolveMetadataHandles();
		}
	}
	
	/**
	 * When this method is called, everything in the project has been
	 * read in and initialized by TopLink. Now we can cascade through 
	 * the model and resolve all the meta data object (table, schema, 
	 * column, etc.) handles. This happens BEFORE the call to 
	 * #postProjectBuild() below.
	 * @see MWProject#postProjectBuild()
	 */
	public final void resolveColumnHandles() {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((MWNode) stream.next()).resolveColumnHandles();
		}
	}

	/**
	 * When this method is called, everything in the project has been
	 * read in and initialized by TopLink. Now we can cascade through 
	 * the model and resolve all the meta data object (table, schema, 
	 * field, etc.) handles. This happens BEFORE the call to 
	 * #postProjectBuild() below.
	 * @see MWProject#postProjectBuild()
	 */
	public final void resolveReferenceHandles() {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((MWNode) stream.next()).resolveReferenceHandles();
		}
	}
	
	/**
	 * When this method is called, everything in the project has been
	 * read in and initialized by TopLink. Now we can cascade through 
	 * the model and resolve all the descriptor handles and descriptor
	 * sub-object handles. This happens BEFORE the call to 
	 * #postProjectBuild() below.
	 * @see MWProject#postProjectBuild()
	 */
	public final void resolveDescriptorHandles() {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((MWNode) stream.next()).resolveDescriptorHandles();
		}
	}
	
	/**
	 * When this method is called, everything in the project has been
	 * read in and initialized by TopLink. Now we can cascade through 
	 * the model and resolve all the method handles.
	 * This happens BEFORE the call to #postProjectBuild() below.
	 * @see MWProject#postProjectBuild()
	 */
	public final void resolveMethodHandles() {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((MWNode) stream.next()).resolveMethodHandles();
		}
	}
	
	/**
	 * When this method is called, everything in the project has been
	 * read in and initialized by TopLink, and all the class, table, and
	 * descriptor handles have been resolved.
	 * Now we can cascade through the model, tying everything together.
	 */
	public void postProjectBuild() {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((MWNode) stream.next()).postProjectBuild();
		}
	// when you override this method, don't forget to include:
	//	super.postProjectBuild();
	}


	// ********** display methods **********

	/**
	 * Return something useful for the UI.
	 */
	public String displayString() {
		// the default is to use the developer-friendly string...
		return this.toString();
	}


	// ********** legacy methods **********

	/**
	 * Our "legacy":
	 * These methods are here so we have the ability to perform
	 * version-specific initialization (e.g. initializing handles,
	 * which did not exist before 4.0).
	 */
	// ***** Version 6.0 events *****
	protected void legacy60PostBuild(DescriptorEvent event) {
	// when you override this method, don't forget to include:
	//	super.legacy50PostBuild(event);
	}

	// ********** legacy TopLink support **********

	/**
	 * Build and return a descriptor with the appropriate descriptor events configured.
	 */
	protected static XMLDescriptor legacy60BuildStandardDescriptor() {
		org.eclipse.persistence.oxm.XMLDescriptor descriptor = new org.eclipse.persistence.oxm.XMLDescriptor();
		descriptor.getEventManager().setPostBuildSelector("legacy60PostBuild");
		return descriptor;
	}

	// ********** hacking **********

	// TODO: remove this when there is a better way to build this list
	public SortedSet buildBasicTypes() {
		SortedSet result = new TreeSet(this.buildBasicTypesComparator());
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Boolean.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Byte.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Character.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Double.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Float.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Integer.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Long.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Short.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.String.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.math.BigDecimal.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.math.BigInteger.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.sql.Date.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.sql.Time.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.sql.Timestamp.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.util.Calendar.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.util.Date.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.sql.Blob.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.sql.Clob.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(Number.class)));
		result.add(new MWTypeDeclaration(this, this.typeFor(byte.class), 1));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Byte.class), 1));
		result.add(new MWTypeDeclaration(this, this.typeFor(char.class), 1));
		result.add(new MWTypeDeclaration(this, this.typeFor(java.lang.Character.class), 1));
		return result;
	}

	/**
	 * sort on short class name; if the short name is the same,
	 * sort on the fully-qualified name
	 */
	private Comparator buildBasicTypesComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				MWTypeDeclaration c1 = (MWTypeDeclaration) o1;
				MWTypeDeclaration c2 = (MWTypeDeclaration) o2;
				int result = Collator.getInstance().compare(c1.shortTypeName(), c2.shortTypeName());
				if (result != 0) {
					return result;
				}
				result = Collator.getInstance().compare(c1.typeName(), c2.typeName());
				if (result != 0) {
					return result;
				}
				return c1.getDimensionality() - c2.getDimensionality();
			}
		};
	}

	/**
	 * Some toplink library classes have been replaced with other classes,
	 * on legacy project read the class references should be changed.
	 */
	public static String legacyReplaceToplinkDeprecatedClassReferences(String legacyClassName) {
		if ("org.eclipse.persistence.publicinterface.Descriptor".equals(legacyClassName)) {
			return "org.eclipse.persistence.descriptors.ClassDescriptor";
		} else if ("org.eclipse.persistence.publicinterface.DescriptorEvent".equals(legacyClassName)) {
			return "org.eclipse.persistence.descriptors.DescriptorEvent";
		} else {
			return legacyClassName;
		}
	}

	public static String legacyReplaceToplinkDepracatedClassReferencesFromSignature(String legacyMethodSignature) {
		if (legacyMethodSignature != null) {
			if (legacyMethodSignature.contains("org.eclipse.persistence.publicinterface.Descriptor")) {
				if (legacyMethodSignature.contains("DescriptorEvent")) {
					legacyMethodSignature = legacyMethodSignature.replace("org.eclipse.persistence.publicinterface.DescriptorEvent", "org.eclipse.persistence.descriptors.DescriptorEvent");
				} else {
					legacyMethodSignature = legacyMethodSignature.replace("org.eclipse.persistence.publicinterface.Descriptor", "org.eclipse.persistence.descriptors.ClassDescriptor");
				}
			} 
		}
		return legacyMethodSignature;
	}

}
