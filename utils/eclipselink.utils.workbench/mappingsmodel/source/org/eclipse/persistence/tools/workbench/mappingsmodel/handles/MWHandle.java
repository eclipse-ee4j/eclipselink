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
package org.eclipse.persistence.tools.workbench.mappingsmodel.handles;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Handles are used to isolate the painful bits of code
 * necessary to correctly handle references to model objects.
 * 
 * All handles should subclass this abstract class.  
 */
public abstract class MWHandle  implements MWNode {

	/** Containment hierarchy. */
	private MWNode parent;		// pseudo-final

	/** This is used to synchronize the model when a node is removed. */
	private NodeReferenceScrubber scrubber;		// pseudo-final

	/** A handle is dirty when the path to its node changes. */
	private volatile boolean dirty;


	// ********** constructors **********

	/**
	 * default constructor - for TopLink use only
	 */
	protected MWHandle() {
		super();
		// a new object is dirty, by definition
		this.dirty = true;
	}

	protected MWHandle(MWNode parent, NodeReferenceScrubber scrubber) {
		this();
		this.setParent(parent);
		this.setScrubberInternal(scrubber);
	}


	// ********** containment hierarchy (parent/children) **********

	public final Node getParent() {
		return this.parent;
	}
	
	/**
	 * Set the object's parent in the containment hierarchy.
	 * Most objects must have a parent.
	 */
	public final void setParent(Node parent) {
		if (parent == null) {
			throw new NullPointerException();
		}
		this.parent = (MWNode) parent;
	}

	// handles do not have children
	public final Iterator children() {
		return NullIterator.instance();
	}
	
	// handles do not have children
	public final void setChildBackpointers() {
		// do nothing
	}
	
	public final boolean isDescendantOf(Node node) {
		return (this == node) || this.parent.isDescendantOf(node);
	}

	public final void addBranchReferencesTo(Collection branchReferences) {
		Node node = this.node();
		if (node != null) {
			branchReferences.add(new SimpleReference(this, node));
		}
	}

	// handles do not have children
	public final void addAllNodesTo(Collection nodes) {
		nodes.add(this);
	}

	
	// ********** dirty flag support **********

	public final boolean isDirtyBranch() {
		return this.dirty;
	}

	public final void markBranchDirty() {
		throw new IllegalStateException("handles shouldn't have children");
	}

	public final void markEntireBranchDirty() {
		this.markDirty();
	}

	public final void cascadeMarkEntireBranchClean() {
		this.dirty = false;
	}

	public final void markBranchCleanIfPossible() {
		throw new IllegalStateException("handles shouldn't have children");
	}

	private void markDirty() {
		this.dirty = true;
		this.parent.markBranchDirty();
	}


	// ********** change support **********

	public final ChangeNotifier getChangeNotifier() {
		return this.parent.getChangeNotifier();
	}

	public final void setChangeNotifier(ChangeNotifier changeNotifier) {
		throw new UnsupportedOperationException("Only root nodes implement #setChangeNotifier(ChangeNotifier): " + this);
	}


	// ********** problems **********

	public final Node.Validator getValidator() {
		return this.parent.getValidator();
	}

	public final void setValidator(Node.Validator validator) {
		throw new UnsupportedOperationException("Only root nodes implement #setValidator(Node.Validator): " + this);
	}

	// handles will never have any problems, nor do they have any descendants
	public final ListIterator branchProblems() {
		return NullListIterator.instance();
	}

	// handles will never have any branch problems, nor do they have any descendants
	public final boolean hasBranchProblems() {
		return false;
	}

	// handles will never have any branch problems, nor do they have any descendants
	public final boolean containsBranchProblem(Problem problem) {
		return false;
	}

	// handles will never have any problems, their parents have the problems
	public final void validateBranch() {
		// do nothing
	}

	// handles will never have any problems, their parents have the problems
	public final boolean validateBranchInternal() {
		return false;
	}

	// handles will never have any branch problems, their parents have the problems
	public final void rebuildBranchProblems() {
		// do nothing
	}

	// handles will never have any problems, their parents have the problems
	public final void addBranchProblemsTo(List branchProblems) {
		// do nothing
	}

	// handles will never have any problems, their parents have the problems
	public final int branchProblemsSize() {
		return 0;
	}

	// handles will never have any problems, their parents have the problems
	public final void clearAllBranchProblems() {
		// do nothing
	}

	// handles will never have any problems, their parents have the problems
	public final boolean clearAllBranchProblemsInternal() {
		return false;
	}


	// ********** convenience methods **********

	public final MWNode getMWParent() {
		return this.parent;
	}

	/**
	 * Do NOT override this method.
	 * Every model object should be able to return the project.
	 */
	// If Java ever allows us to override with a different return type, we can call this
	// getProject() and have MWRModel override and return an MWRProject.
	// Supposedly, this will happen in jdk 1.5....
	public final MWProject getProject() {
		return this.getMWParent().getProject();
	}
	
	/**
	 * Do NOT override this method.
	 * Every model object in a relational project should be able to return the database.
	 */
	public final MWDatabase getDatabase() {
		return this.getProject().getDatabase();
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
	 * Return the node referenced by the handle.
	 */
	protected abstract Node node();
	
	/**
	 * If the handle's node has been renamed, or it is a descendant of
	 * a node that has been renamed, the handle must mark its branch
	 * dirty so that the handle is saved with the new name.
	 */
	public void nodeRenamed(Node node) {
		if ((this.node() != null) && this.node().isDescendantOf(node)) {
			this.markDirty();
		}
	}

	/**
	 * If the handle's node has been removed, or it is a descendant of
	 * a node that has been removed, notify the scrubber.
	 */
	public final void nodeRemoved(Node removedNode) {
		if ((this.node() != null) && this.node().isDescendantOf(removedNode)) {
			this.scrubber.nodeReferenceRemoved(this.node(), this);
		}
	}

	// synchronization is handled by the parent object, do not override
	public final void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		// do nothing
	}

	// synchronization is handled by the parent object, do not override
	public final void mappingReplaced(MWMapping oldMapping, MWMapping newMapping) {
		// do nothing
	}

	// synchronization is handled by the parent object, do not override
	public final void descriptorUnmapped(Collection mappings) {
		// do nothing
	}

	/**
	 * Subclasses will probably implement something like
	 * #setScrubber(NodeReferenceScrubber) that returns 'this'
	 */
	protected final void setScrubberInternal(NodeReferenceScrubber scrubber) {
		if (scrubber == null) {
			throw new NullPointerException();
		}
		this.scrubber = scrubber;
	}


	// ********** post-read methods **********
	
	/** 
	 * Override this method if there are objects in the hierarchy
	 * 	that depend on this handle being resolved before postProjectBuild().
	 * Do not override unless the handle is for a class or class sub-object
	 * 	(attribute, method, etc.)
	 */
	public void resolveClassHandles() {
		// do nothing
	}
	
	/** 
	 * Override this method if there are objects in the hierarchy
	 *  that depend on this handle being resolved before postProjectBuild().
	 * Do not override unless the handle is for a descriptor or descriptor sub-object
	 * 	(mapping, xml data field, etc.)
	 */
	public void resolveDescriptorHandles() {
		// do nothing
	}
	
	/** 
	 * Override this method if there are objects in the hierarchy
	 *  that depend on this handle being resolved before postProjectBuild().
	 * Do not override unless the handle is for a meta data object or sub-object
	 * 	(field, schema component, etc.)
	 */
	public void resolveMetadataHandles() {
		// do nothing
	}
	
	/** 
	 * Override this method if there are objects in the hierarchy
	 *  that depend on this handle being resolved before postProjectBuild().
	 * Do not override unless the handle is for a meta data object or sub-object
	 * 	(column, schema component, etc.)
	 */
	public void resolveColumnHandles() {
		// do nothing
	}
	
	/** 
	 * Override this method if there are objects in the hierarchy
	 *  that depend on this handle being resolved before postProjectBuild().
	 * Do not override unless the handle is for a meta data object or sub-object
	 * 	(field, schema component, etc.)
	 */
	public void resolveReferenceHandles() {
		// do nothing
	}

	/** 
	 * Override this method if there are objects in the hierarchy
	 *  that depend on this handle being resolved before postProjectBuild().
	 * Do not override unless the handle is for a meta data object or sub-object
	 * 	(field, schema component, etc.)
	 */
	public void resolveMethodHandles() {
		// do nothing
	}

	public void postProjectBuild() {
		if (this.scrubber == null) {
			throw new NullPointerException("This handle's 'scrubber' should have been set by its parent upon creation.");
		}
	}
	
	
	// ********** display methods **********
	
	/**
	 * handles are not displayed
	 */
	public final String displayString() {
		throw new UnsupportedOperationException();
	}

	// ********** standard methods **********

	public String toString() {
		StringBuffer sb = new StringBuffer();
		StringTools.buildSimpleToStringOn(this, sb);
		sb.append(" (");
		this.toString(sb);
		sb.append(')');
		return sb.toString();
	}

	public abstract void toString(StringBuffer sb);


	// ********** member interface **********

	/**
	 * This interface defines the method called by a handle when the node the
	 * handle references has been removed from the project. Typically the
	 * handle's parent will implement an adapter that will call the appropriate
	 * method to either remove or clear the handle. The handle itself will
	 * continue to hold the reference node - it is up to the parent to
	 * synchronize appropriately.
	 */
	public interface NodeReferenceScrubber {

		/**
		 * The specified node is no longer referenced by the specified handle.
		 */
		void nodeReferenceRemoved(Node node, MWHandle handle);


		NodeReferenceScrubber NULL_INSTANCE = new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				// do nothing
			}
			public String toString() {
				return "NullReferenceScrubber";
			}
		};
	}

}
