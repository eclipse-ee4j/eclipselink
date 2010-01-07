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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * A package node holds a collection of class description nodes
 * that wrap user-supplied "class descriptions".
 * 
 * Although the package node's list of class description nodes may change, the
 * package node's name and description are static and will never change.
 * 
 * This node is used in the MultipleClassChooserDialog.
 */
public final class ClassDescriptionPackageNode
	extends AbstractTreeNodeValueModel
	implements Displayable, ClassDescriptionNodeContainer
{

	/** the project the package belongs to */
	private ClassDescriptionPackagePoolNode poolNode;

	/** the package name */
	private String name;
	private CollationKey nameCollationKey;

	/** the package "additional info", which is determined by the class descriptions */
	private String additionalInfo;

	/** whether we should include the "additional info" in the display string */
	private boolean displaysAdditionalInfo;

	/** cache the display string */
	private String displayString;

	/** the package's classes */
	private ListValueModel classNodesHolder;
	private ListValueModel sortedClassNodesHolder;


	// ********** constructor **********

	/** construct a package with the specified name */
	ClassDescriptionPackageNode(ClassDescriptionPackagePoolNode poolNode, String name, String additionalInfo) {
		super();
		this.poolNode = poolNode;
		this.name = name;
		this.nameCollationKey = this.poolNode.getCollator().getCollationKey(name);
		this.additionalInfo = additionalInfo;
		this.displaysAdditionalInfo = false;
		this.displayString = this.buildDisplayString();
		this.classNodesHolder = new SimpleListValueModel(new ArrayList(100));
		this.sortedClassNodesHolder = new SortedListValueModelAdapter(this.classNodesHolder);
	}


	// ********** accessors **********

	ClassDescriptionPackagePoolNode getPoolNode() {
		return this.poolNode;
	}

	/** the package node can change its project node */
	void setPoolNode(ClassDescriptionPackagePoolNode poolNode) {
		this.poolNode = poolNode;
	}

	String getName() {
		return this.name;
	}

	String getAdditionalInfo() {
		return this.additionalInfo;
	}

	boolean displaysAdditionalInfo() {
		return this.displaysAdditionalInfo;
	}

	void setDisplaysAdditionalInfo(boolean displaysAdditionalInfo) {
		if (this.displaysAdditionalInfo == displaysAdditionalInfo) {
			return;
		}
		this.displaysAdditionalInfo = displaysAdditionalInfo;
		// rebuild the display string
		this.displayString = this.buildDisplayString();
	}

	ListIterator classNodes() {
		return (ListIterator) this.classNodesHolder.getValue();
	}

	int classNodesSize() {
		return this.classNodesHolder.size();
	}

	void addClassNode(ClassDescriptionNode classNode) {
		classNode.setPackageNode(this);
		this.classNodesHolder.addItem(this.classNodesHolder.size(), classNode);
	}

	void removeClassNode(ClassDescriptionNode classNode) {
		this.classNodesHolder.removeItem(this.indexOfClassNode(classNode));
		// leave the backpointer in place; @see ClassDescriptionNode#buildPackageNode()
		// classNode.setPackageNode(null);
	}

	private int indexOfClassNode(ClassDescriptionNode classNode) {
		int size = this.classNodesHolder.size();
		for (int i = 0; i < size; i++) {
			if (this.classNodesHolder.getItem(i) == classNode) {
				return i;
			}
		}
		return -1;
	}

	boolean isEmpty() {
		return this.classNodesSize() == 0;
	}

	Icon classIcon() {
		return this.poolNode.getClassIcon();
	}

	Collator collator() {
		return this.poolNode.getCollator();
	}


	// ********** ValueModel implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.ValueModel#getValue()
	 */
	public Object getValue() {
		return this.displayString;
	}


	// ********** TreeNodeValueModel implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel#getParent()
	 */
	public TreeNodeValueModel getParent() {
		return this.poolNode;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel#getChildrenModel()
	 */
	public ListValueModel getChildrenModel() {
		return this.sortedClassNodesHolder;
	}


	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		// the package is static - do nothing
	}

	protected void disengageValue() {
		// the package is static - do nothing
	}


	// ********** ClassNodeContainer implementation **********

	/**
	 * @see ClassDescriptionNodeContainer#addClassDescriptionNodesTo(java.util.Collection)
	 */
	public void addClassDescriptionNodesTo(Collection classDescriptionNodes) {
		CollectionTools.addAll(classDescriptionNodes, this.classNodes());
	}


	// ********** Comparable implementation **********

	public int compareTo(Object o) {
		// sort by name first...
		int result = this.nameCollationKey.compareTo(((ClassDescriptionPackageNode) o).nameCollationKey);
		if (result != 0) {
			return result;
		}
		// ...then description
		return this.poolNode.getCollator().compare(this.displayString, ((ClassDescriptionPackageNode) o).displayString);
	}


	// ********** Displayable implementation **********

	public String displayString() {
		return this.displayString;
	}

	private String buildDisplayString() {
		if (( ! this.displaysAdditionalInfo) || (this.additionalInfo == null) || (this.additionalInfo.length() == 0)) {
			return this.name;
		}
		return this.name + " - " + this.additionalInfo;
	}

	/**
	 * the icon is cached in the package pool node
	 */
	public Icon icon() {
		return this.poolNode.getPackageIcon();
	}


	// ********** standard methods **********

	public void toString(StringBuffer sb) {
		sb.append(this.displayString);
	}

}
