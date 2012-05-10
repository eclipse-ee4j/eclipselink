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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * A package pool node holds a collection of package nodes, which each
 * hold on to a collection of class description nodes, which each wrap a
 * user-supplied "class description".
 * 
 * Although the package pool node's list of package nodes may change, the
 * package pool node's name is static and will never change.
 * 
 * This node is used in the MultipleClassChooserDialog.
 */
public final class ClassDescriptionPackagePoolNode
	extends AbstractTreeNodeValueModel
	implements Displayable, ClassDescriptionNodeContainer
{
	/** create a new dummy value with each node, or the nodes will be equal */
	private Object value;

	/** the package pool name - not really used, except in debugging */
	private String name;

	/** the package pool's packages */
	private ListValueModel packageNodesHolder;
	private ListValueModel sortedPackageNodesHolder;

	/** cache the icons */
	private Icon packageIcon;
	private Icon classIcon;

	/** Collator used to sort the various lists. */
	private Collator collator;


	// ********** constructors **********

	/** construct a package pool with the specified name and user "class descriptions" */
	public ClassDescriptionPackagePoolNode(String name, Iterator userClassDescriptions, ClassDescriptionAdapter adapter, WorkbenchContext context) {
		this(name, userClassDescriptions, adapter, Collections.EMPTY_SET, context);
	}

	/** construct a package pool with the specified name and user "class descriptions" */
	public ClassDescriptionPackagePoolNode(String name, Iterator userClassDescriptions, ClassDescriptionAdapter adapter, Collection excludedUserClassDescriptions, WorkbenchContext context) {
		super();
		this.value = new Object();
		this.name = name;
		this.packageIcon = context.getApplicationContext().getResourceRepository().getIcon("package");
		this.classIcon = context.getApplicationContext().getResourceRepository().getIcon("class.public");
		this.collator = Collator.getInstance();
		this.initialize(userClassDescriptions, adapter, excludedUserClassDescriptions);
	}


	// ********** initialization **********

	private void initialize(Iterator userClassDescriptions, ClassDescriptionAdapter adapter, Collection excludedUserClassDescriptions) {
		this.packageNodesHolder = new SimpleListValueModel(new ArrayList(1000));
		this.addUserClassDescriptions(userClassDescriptions, adapter, excludedUserClassDescriptions);
		this.removeEmptyPackageNodes();

		// wait until things are settled down to sort the packages
		this.sortedPackageNodesHolder = new SortedListValueModelAdapter(this.packageNodesHolder);
	}

	private void addUserClassDescriptions(Iterator userClassDescriptions, ClassDescriptionAdapter adapter, Collection excludedUserClassDescriptions) {
		Map packageNodeNames = new HashMap(1000);
		while (userClassDescriptions.hasNext()) {
			Object userClassDescription = userClassDescriptions.next();
			if ( ! excludedUserClassDescriptions.contains(userClassDescription)) {
				this.addClassNode(new ClassDescriptionNode(userClassDescription, adapter), packageNodeNames);
			}
		}
	}

	private void removeEmptyPackageNodes() {
		// gather them up so we don't get a concurrent modification exception
		List emptyPackageNodes = CollectionTools.list(this.emptyPackageNodes());
		for (Iterator stream = emptyPackageNodes.iterator(); stream.hasNext(); ) {
			this.removePackageNode((ClassDescriptionPackageNode) stream.next());
		}
	}

	private Iterator emptyPackageNodes() {
		return new FilteringIterator(this.packageNodes()) {
			public boolean accept(Object next) {
				return ((ClassDescriptionPackageNode) next).isEmpty();
			}
		};
	}


	// ********** accessors **********

	ListIterator packageNodes() {
		return (ListIterator) this.packageNodesHolder.getValue();
	}

	int packageNodesSize() {
		return this.packageNodesHolder.size();
	}

	void addClassNode(ClassDescriptionNode classNode) {
		this.addClassNode(classNode, null);
	}

	void addClassNode(ClassDescriptionNode classNode, Map packageNodeNames) {
		this.getPackageNodeFor(classNode, packageNodeNames).addClassNode(classNode);
	}

	void removeClassNode(ClassDescriptionNode classNode) {
		ClassDescriptionPackageNode packageNode = this.getPackageNodeFor(classNode, null);
		packageNode.removeClassNode(classNode);
		if (packageNode.isEmpty()) {
			this.removePackageNode(packageNode);
		}
	}

	private void removePackageNode(ClassDescriptionPackageNode packageNode) {
		this.packageNodesHolder.removeItem(this.indexOfPackageNode(packageNode));
		packageNode.setPoolNode(null);	// ???
	}

	private int indexOfPackageNode(ClassDescriptionPackageNode packageNode) {
		int size = this.packageNodesHolder.size();
		for (int i = 0; i < size; i++) {
			if (this.packageNodesHolder.getItem(i) == packageNode) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * a bit of hackery here: on the first pass, the package node names map
	 * will be present - use it when building the first generation of package
	 * nodes, but it is not used when package nodes are created later
	 */
	private ClassDescriptionPackageNode getPackageNodeFor(ClassDescriptionNode classNode, Map packageNodeNames) {
		ClassDescriptionPackageNode packageNode = null;
		for (Iterator stream = this.packageNodes(); stream.hasNext(); ) {
			ClassDescriptionPackageNode next = (ClassDescriptionPackageNode) stream.next();
			if (classNode.belongsInPackageNode(next)) {
				packageNode = next;
				break;
			}
		}
		if (packageNode == null) {
			packageNode = classNode.buildPackageNode(this);

			if (packageNodeNames != null) {
				// if we have 2 packages with the same name, configure them to display their descriptions
				ClassDescriptionPackageNode prev = (ClassDescriptionPackageNode) packageNodeNames.put(packageNode.getName(), packageNode);
				if (prev != null) {
					prev.setDisplaysAdditionalInfo(true);
					packageNode.setDisplaysAdditionalInfo(true);
				}
			}

			this.packageNodesHolder.addItem(this.packageNodesHolder.size(), packageNode);
		}
		return packageNode;
	}

	Icon getPackageIcon() {
		return this.packageIcon;
	}

	Icon getClassIcon() {
		return this.classIcon;
	}

	Collator getCollator() {
		return this.collator;
	}


	// ********** ValueModel implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.ValueModel#getValue()
	 */
	public Object getValue() {
		// answer this dummy value so we aren't equal to another package pool node
		return this.value;
	}


	// ********** TreeNodeValueModel implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel#getParent()
	 */
	public TreeNodeValueModel getParent() {
		return null;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel#getChildrenModel()
	 */
	public ListValueModel getChildrenModel() {
		return this.sortedPackageNodesHolder;
	}


	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		// the package pool is static - do nothing
	}

	protected void disengageValue() {
		// the package pool is static - do nothing
	}


	// ********** ClassDescriptionNodeContainer implementation **********

	/**
	 * @see ClassDescriptionNodeContainer#addClassNodesTo(java.util.Collection)
	 */
	public void addClassDescriptionNodesTo(Collection classDescriptionNodes) {
		for (Iterator stream = this.packageNodes(); stream.hasNext(); ) {
			((ClassDescriptionPackageNode) stream.next()).addClassDescriptionNodesTo(classDescriptionNodes);
		}
	}


	// ********** Comparable implementation **********

	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}


	// ********** Displayable implementation **********

	public String displayString() {
		return this.name;
	}

	public Icon icon() {
		return null;
	}


	// ********** queries **********

	Iterator classNodes() {
		return new CompositeIterator(
			new TransformationIterator(this.packageNodes()) {
				protected Object transform(Object next) {
					return ((ClassDescriptionPackageNode) next).classNodes();
				}
			}
		);
	}

	Iterator userClassDescriptions() {
		return new TransformationIterator(this.classNodes()) {
			protected Object transform(Object next) {
				return ((ClassDescriptionNode) next).getUserClassDescription();
			}
		};
	}


	// ********** standard methods **********

	public void toString(StringBuffer sb) {
		sb.append(this.name);
	}

}
