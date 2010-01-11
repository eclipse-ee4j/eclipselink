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
import java.util.Collection;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.NullListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;


/**
 * A node for a user-supplied "class description".
 * 
 * This node is used in the MultipleClassChooserDialog.
 */
public final class ClassDescriptionNode
	extends AbstractTreeNodeValueModel
	implements Displayable, ClassDescriptionNodeContainer
{

	/** the package the class belongs to - which can change */
	private ClassDescriptionPackageNode packageNode;

	/** the user-supplied "class description" */
	private Object userClassDescription;

	private String packageName;
	private String shortClassName;
	private String additionalInfo;

	private CollationKey collationKey;


	// ********** constructors **********

	/**
	 * construct a node for the client-supplied "class description"
	 */
	ClassDescriptionNode(Object userClassDescription, ClassDescriptionAdapter adapter) {
		super();
		this.userClassDescription = userClassDescription;
		this.packageName = adapter.packageName(userClassDescription);
		this.shortClassName = adapter.shortClassName(userClassDescription).replace('$', '.');
		this.additionalInfo = adapter.additionalInfo(userClassDescription);
	}


	// ********** accessors **********

	ClassDescriptionPackageNode getPackageNode() {
		return this.packageNode;
	}

	/** the class node can change its package node */
	void setPackageNode(ClassDescriptionPackageNode packageNode) {
		if (this.packageNode == null) {
			// build our collation key once we have visibility to the collator
			this.collationKey = packageNode.collator().getCollationKey(this.shortClassName);
		}
		this.packageNode = packageNode;
	}

	/** return the user-supplied "class description" */
	public Object getUserClassDescription() {
		return this.userClassDescription;
	}

	String getPackageName() {
		return this.packageName;
	}

	String getShortClassName() {
		return this.shortClassName;
	}

	String getAdditionalInfo() {
		return this.additionalInfo;
	}


	// ********** queries **********

	boolean belongsInPackageNode(ClassDescriptionPackageNode pkgNode) {
		// first check that the package names match...
		if ( ! this.packageName.equals(pkgNode.getName())) {
			return false;
		}
		// ...then check that the descriptions match
		return this.valuesAreEqual(this.additionalInfo, pkgNode.getAdditionalInfo());
	}


	// ********** behavior **********

	/**
	 * if we are building a new package node when we already have one,
	 * that means we are going to be *moved* to the new package node;
	 * configure the new package node to display in the same fashion as
	 * our current package node
	 */
	ClassDescriptionPackageNode buildPackageNode(ClassDescriptionPackagePoolNode projectNode) {
		ClassDescriptionPackageNode pkgNode = new ClassDescriptionPackageNode(projectNode, this.packageName, this.additionalInfo);
		if (this.packageNode != null) {
			pkgNode.setDisplaysAdditionalInfo(this.packageNode.displaysAdditionalInfo());
		}
		return pkgNode;
	}


	// ********** ValueModel implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.ValueModel#getValue()
	 */
	public Object getValue() {
		return this.userClassDescription;
	}


	// ********** TreeNodeValueModel implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel#getParent()
	 */
	public TreeNodeValueModel getParent() {
		return this.packageNode;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel#getChildrenModel()
	 */
	public ListValueModel getChildrenModel() {
		return NullListValueModel.instance();
	}


	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		// the class is static - do nothing
	}

	protected void disengageValue() {
		// the class is static - do nothing
	}


	// ********** ClassNodeContainer implementation **********

	/**
	 * @see ClassDescriptionNodeContainer#addClassDescriptionNodesTo(java.util.Collection)
	 */
	public void addClassDescriptionNodesTo(Collection classDescriptionNodes) {
		classDescriptionNodes.add(this);
	}


	// ********** Comparable implementation **********

	/**
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		return this.collationKey.compareTo(((ClassDescriptionNode) o).collationKey);
	}


	// ********** Displayable implementation **********

	public String displayString() {
		return this.shortClassName;
	}

	/**
	 * the icon is cached in the package pool node
	 */
	public Icon icon() {
		return this.packageNode.classIcon();
	}


	// ********** standard methods **********

	public void toString(StringBuffer sb) {
		sb.append(this.shortClassName);
	}

}
