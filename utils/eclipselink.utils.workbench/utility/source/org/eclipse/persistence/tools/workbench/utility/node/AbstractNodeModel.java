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
package org.eclipse.persistence.tools.workbench.utility.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;


/**
 * Base class for Node Model classes.
 * Provides support for the following:
 *     initialization
 *     enforced object identity wrt #equals()/#hashCode()
 *     containment hierarchy (parent/child)
 *     user comment
 *     dirty flag
 *     problems
 *     sorting
 * 
 * Typically, subclasses should consider implementing the following methods:
 *     the appropriate constructors
 *         (with the appropriately-restrictive type declaration for parent)
 *     #initialize()
 *     #initialize(Node parentNode)
 *     #checkParent(Node parentNode)
 *     #addChildrenTo(List list)
 *     #nodeRemoved(Node)
 *     #getValidator()
 *     #transientAspectNames() or
 *         #addTransientAspectNamesTo(Set transientAspectNames)
 *     #addProblemsTo(List currentProblems)
 *     #insignificantAspectNames()
 *         #addInsignificantAspectNamesTo(Set insignificantAspectNames)
 *     #displayString()
 *     #toString(StringBuffer sb)
 */
public abstract class AbstractNodeModel 
	extends AbstractModel 
	implements NodeModel 
{

	/** Containment hierarchy. */
	private volatile Node parent;

	/** User comment. */
	private volatile String comment;
		public static final String COMMENT_PROPERTY = "comment";

	/** Track whether the node has changed. */
	private volatile boolean dirty;
	private volatile boolean dirtyBranch;

	/**
	 * The node's problems, as calculated during validation.
	 * This list should only be modified via a ProblemSynchronizer,
	 * allowing for asynchronous modification from another thread.
	 */
	private List problems;		// pseudo-final
		private static final Object[] EMPTY_PROBLEM_MESSAGE_ARGUMENTS = new Object[0];

	/**
	 * Cache the node's "branch" problems, as calculated during validation.
	 * This list should only be modified via a ProblemSynchronizer,
	 * allowing for asynchronous modification from another thread.
	 * This must be recalculated every time this node or one of its
	 * descendants changes it problems.
	 */
	private List branchProblems;		// pseudo-final



	/**
	 * Sets of transient aspect names, keyed by class.
	 * This is built up lazily, as the objects are modified.
	 */
	private static final Map transientAspectNameSets = new Hashtable();

	/**
	 * Sets of "insignificant" aspect names, keyed by class.
	 * This is built up lazily, as the objects are modified.
	 */
	private static final Map insignificantAspectNameSets = new Hashtable();


	// ********** constructors **********

	/**
	 * Default constructor - typically for internal use only.
	 */
	protected AbstractNodeModel() {
		super();
	}

	/**
	 * Most objects must have a parent.
	 * Use this constructor to create a new object - the default
	 * constructor should only be used internally.
	 * @see #initialize(Node)
	 */
	protected AbstractNodeModel(Node parent) {
		this();
		this.initialize(parent);
	}


	// ********** initialization **********

	/**
	 * Initialize a newly-created instance.
	 * @see #initialize(Node)
	 */
	protected void initialize() {
		super.initialize();
		this.comment = "";

		// a new object is dirty, by definition
		this.dirty = true;
		this.dirtyBranch = true;

		this.problems = new Vector();
		this.branchProblems = new Vector();

	// when you override this method, don't forget to include:
	//	super.initialize();
	}

	/**
	 * Initialize a newly-created instance.
	 * @see #initialize()
	 */
	protected void initialize(Node parentNode) {
		this.checkParent(parentNode);
		this.parent = parentNode;
	// when you override this method, don't forget to include:
	//	super.initialize(parentNode);
	}

	/**
	 * Build a change support object that will notify the node
	 * when one of the node's aspects has changed.
	 * @see #aspectChanged(String)
	 */
	protected ChangeSupport buildDefaultChangeSupport() {
		return new ChangeSupport(this) {
			private static final long serialVersionUID = 1L;
			protected ChangeNotifier notifier() {
				return AbstractNodeModel.this.getChangeNotifier();
			}
			protected ChangeSupport buildChildChangeSupport() {
				return AbstractNodeModel.this.buildChildChangeSupport();
			}
			protected void sourceChanged(String aspectName) {
				super.sourceChanged(aspectName);
				AbstractNodeModel.this.aspectChanged(aspectName);
			}
		};
	}

	/**
	 * The aspect-specific change support objects do not need to
	 * notify the node of changes (the parent will take care of that).
	 */
	protected ChangeSupport buildChildChangeSupport() {
		return new ChangeSupport(this) {
			private static final long serialVersionUID = 1L;
			protected ChangeNotifier notifier() {
				return AbstractNodeModel.this.getChangeNotifier();
			}
			protected ChangeSupport buildChildChangeSupport() {
				// return AbstractNodeModel.this.buildChildChangeSupport();
				throw new UnsupportedOperationException();
			}
		};
	}


	// ********** equality **********

	/**
	 * Enforce object identity - do not allow objects to be equal unless
	 * they are the same object.
	 * Do NOT override this method - we rely on object identity extensively.
	 */
	public final boolean equals(Object o) {
		return this == o;
	}

	/**
	 * Enforce object identity - do not allow objects to be equal unless
	 * they are the same object.
	 * Do NOT override this method - we rely on object identity extensively.
	 */
	public final int hashCode() {
		return super.hashCode();
	}


	// ********** containment hierarchy (parent/children) **********

	/**
	 * INTRA-NODE API?
	 * Return the object's parent in the containment hierarchy.
	 * Most objects must have a parent.
	 * @see Node#getParent()
	 */
	public final Node getParent() {
		return this.parent;
	}

	/**
	 * Set the object's parent in the containment hierarchy.
	 * Most objects must have a parent.
	 * @see Node#setParent(Node)
	 */
	public final void setParent(Node parentNode) {
		this.checkParent(parentNode);
		this.parent = parentNode;
	}

	/**
	 * Throw an IllegalArgumentException if the parent is not valid
	 * for the node.
	 * By default require a non-null parent.  Override if other restrictions exist
	 * or the parent should be null
	 */
	protected void checkParent(Node parentNode) {
		if (parentNode == null) {
			throw new IllegalArgumentException("The parent node cannot be null");
		}
	}

	/**
	 * INTRA-NODE API?
	 * Do NOT override this method.
	 * Override #addChildrenTo(List).
	 * @see #addChildrenTo(java.util.List)
	 * @see Node#children()
	 */
	public final Iterator children() {
		List children = new ArrayList();
		this.addChildrenTo(children);
		return children.iterator();
	}

	/**
	 * Subclasses should override this method to add their children
	 * to the specified list.
	 * @see #children()
	 */
	protected void addChildrenTo(List list) {
		// this class has no children, subclasses will...
	// when you override this method, don't forget to include:
	//	super.addChildrenTo(list);
	}

	/**
	 * Loop through the object's children setting their backpointers
	 * to their parent, namely this object; then cascade down
	 * through the tree.
	 * @see Node#setChildBackpointers()
	 */
	public final void setChildBackpointers() {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			child.setParent(this);
			child.setChildBackpointers();
		}
	}

	/**
	 * @see Node#isDescendantOf(Node)
	 */
	public final boolean isDescendantOf(Node node) {
		return (this == node) || this.parentIsDescendantOf(node);
	}

	protected boolean parentIsDescendantOf(Node node) {
		return (this.parent != null) && this.parent.isDescendantOf(node);
	}

	/**
	 * Return a collection holding all the node's "references", and all
	 * the node's descendants' "references". "References" are
	 * objects that are "referenced" by another object, as opposed
	 * to "owned" by another object.
	 */
	public final Iterator branchReferences() {
		Collection branchReferences = new ArrayList(1000);		// start big
		this.addBranchReferencesTo(branchReferences);
		return branchReferences.iterator();
	}

	/**
	 * @see Node#addBranchReferencesTo(java.util.Collection)
	 */
	public final void addBranchReferencesTo(Collection branchReferences) {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			child.addBranchReferencesTo(branchReferences);
		}
	}

	/**
	 * Return all the nodes in the object's branch of the tree,
	 * including the node itself. The nodes will probably returned
	 * in "depth-first" order.
	 * Only really used for testing and debugging.
	 */
	public final Iterator allNodes() {
		Collection nodes = new ArrayList(1000);		// start big
		this.addAllNodesTo(nodes);
		return nodes.iterator();
	}

	/**
	 * INTRA-NODE API?
	 * @see Node#addAllNodesTo(java.util.Collection)
	 */
	public final void addAllNodesTo(Collection nodes) {
		nodes.add(this);
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			child.addAllNodesTo(nodes);
		}
	}


	// ********** model synchronization support **********

	/**
	 * INTRA-NODE API
	 * @see Node#nodeRemoved(Node)
	 */
	public void nodeRemoved(Node node) {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			child.nodeRemoved(node);
		}
	// when you override this method, don't forget to include:
	//	super.nodeRemoved(node);
	}

	/**
	 * convenience method
	 * return whether node1 is a descendant of node2;
	 * node1 can be null
	 */
	protected boolean nodeIsDescendantOf(Node node1, Node node2) {
		return (node1 != null) && node1.isDescendantOf(node2);
	}

	/**
	 * INTRA-NODE API
	 * typically, only handles will implement this method
	 * @see Node#nodeRenamed(Node)
	 */
	public void nodeRenamed(Node node) {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			child.nodeRenamed(node);
		}
	// when you override this method, don't forget to include:
	//	super.nodeRenamed(node);
	}
	
	
	// ********** user comment **********

	/**
	 * Return the object's user comment.
	 */
	public final String getComment() {
		return this.comment;
	}

	/**
	 * Set the object's user comment.
	 */
	public final void setComment(String comment) {
		Object old = this.comment;
		this.comment = comment;
		this.firePropertyChanged(COMMENT_PROPERTY, old, comment);
	}


	// ********** change support **********

	/**
	 * INTRA-NODE API
	 * Return a change notifier that will be used to forward
	 * change notifications to listeners.
	 * Typically only the root node directly holds a notifier.
	 * @see Node#getChangeNotifier()
	 */
	public ChangeNotifier getChangeNotifier() {
		if (this.parent == null) {
			throw new IllegalStateException("This node should not be firing change events during its construction.");
		}
		return this.parent.getChangeNotifier();
	}

	/**
	 * Set a change notifier that will be used to forward
	 * change notifications to listeners.
	 * Typically only the root node directly holds a notifier.
	 * @see Node#setChangeNotifier(ChangeNotifier)
	 */
	public void setChangeNotifier(ChangeNotifier changeNotifier) {
		if (this.parent == null) {
			throw new IllegalStateException("This root node should implement #setChangeNotifier(ChangeNotifier).");
		}
		throw new UnsupportedOperationException("Only root nodes implement #setChangeNotifier(ChangeNotifier).");
	}

	/**
	 * An aspect of the node has changed:
	 * 	- if it is a persistent aspect, mark the object dirty
	 * 	- if it is a significant aspect, validate the object
	 */
	protected void aspectChanged(String aspectName) {
		if (this.aspectIsPersistent(aspectName)) {
//			System.out.println(Thread.currentThread() + " dirty change: " + this + ": " + aspectName);
			this.markDirty();
		}
		if (this.aspectIsSignificant(aspectName)) {
//			System.out.println(Thread.currentThread() + " significant change: " + this + ": " + aspectName);
			this.getValidator().validate();
		}
	}

	/**
	 * INTRA-NODE API
	 * Return a validator that will be invoked whenever a
	 * "significant" aspect of the node tree changes.
	 * Typically only the root node directly holds a validator.
	 * @see Node#getValidator()
	 */
	public Node.Validator getValidator() {
		if (this.parent == null) {
			throw new IllegalStateException("This node should not be firing change events during its construction.");
		}
		return this.parent.getValidator();
	}

	/**
	 * Set a validator that will be invoked whenever a
	 * "significant" aspect of the node tree changes.
	 * Typically only the root node directly holds a validator.
	 * @see Node#setValidator(Node.Validator)
	 */
	public void setValidator(Node.Validator validator) {
		if (this.parent == null) {
			throw new IllegalStateException("This root node should implement #setValidator(Node.Validator).");
		}
		throw new UnsupportedOperationException("Only root nodes implement #setValidator(Node.Validator).");
	}


	// ********** dirty flag support **********

	/**
	 * Return whether any persistent aspects of the object
	 * have changed since the object was last read or saved.
	 * This does NOT include changes to the object's descendants.
	 */
	public final boolean isDirty() {
		return this.dirty;
	}

	/**
	 * Return whether any persistent aspects of the object,
	 * or any of its descendants, have changed since the object and
	 * its descendants were last read or saved.
	 * @see Node#isDirtyBranch()
	 */
	public final boolean isDirtyBranch() {
		return this.dirtyBranch;
	}

	/**
	 * Return whether the object is unmodified
	 * since it was last read or saved.
	 * This does NOT include changes to the object's descendants.
	 */
	public final boolean isClean() {
		return ! this.dirty;
	}

	/**
	 * Return whether the object and all of its descendants
	 * are unmodified since the object and
	 * its descendants were last read or saved.
	 */
	public final boolean isCleanBranch() {
		return ! this.dirtyBranch;
	}

	/**
	 * Set the dirty branch flag setting. This is set to true
	 * when either the object or one of its descendants becomes dirty.
	 */
	private void setIsDirtyBranch(boolean dirtyBranch) {
		boolean old = this.dirtyBranch;
		this.dirtyBranch = dirtyBranch;
		this.firePropertyChanged(DIRTY_BRANCH_PROPERTY, old, dirtyBranch);
	}

	/**
	 * Mark the object as dirty and as a dirty branch.
	 * An object is marked dirty when either a "persistent" attribute
	 * has changed or its save location has changed.
	 */
	private void markDirty() {
		this.dirty = true;
		this.markBranchDirty();
	}

	/**
	 * INTRA-NODE API
	 * Mark the object and its parent as dirty branches
	 * if necessary.
	 * @see Node#markBranchDirty()
	 */
	public void markBranchDirty() {
		// short-circuit any unnecessary propagation
		if (this.dirtyBranch) {
			// if this is already a dirty branch, the parent must be also
			return;
		}

		this.setIsDirtyBranch(true);
		this.markParentBranchDirty();
	}

	protected void markParentBranchDirty() {
		if (this.parent != null) {
			this.parent.markBranchDirty();
		}
	}

	/**
	 * Mark the object and all its descendants as dirty.
	 * This is used when the save location of some
	 * top-level object is changed and the entire
	 * containment tree must be marked dirty so it
	 * will be written out.
	 * @see Node#markEntireBranchDirty()
	 */
	public final void markEntireBranchDirty() {
		this.markDirty();
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			child.markEntireBranchDirty();
		}
	}

	/**
	 * Mark the object and all its descendants as clean.
	 * Then notify the object's parent that it (the parent)
	 * might now be a clean branch also.
	 * Typically used when the object has just been
	 * read in or written out.
	 */
	public final void markEntireBranchClean() {
		this.cascadeMarkEntireBranchClean();
		this.markParentBranchCleanIfPossible();
	}

	protected void markParentBranchCleanIfPossible() {
		if (this.parent != null) {
			this.parent.markBranchCleanIfPossible();
		}
	}

	/**
	 * INTRA-NODE API
	 * Mark the object and all its descendants as clean.
	 * This method is for internal use only; it is not for
	 * client use.
	 * @see Node#cascadeMarkEntireBranchClean()
	 */
	public final void cascadeMarkEntireBranchClean() {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			child.cascadeMarkEntireBranchClean();
		}
		this.dirty = false;
		this.setIsDirtyBranch(false);
	}

	/**
	 * INTRA-NODE API
	 * The object has been marked clean; possibly
	 * its entire branch is now clean, as well as its parent's branch.
	 * @see Node#markBranchCleanIfPossible()
	 */
	public final void markBranchCleanIfPossible() {
		// short-circuit any unnecessary propagation
		if (this.dirty) {
			// if the object is "locally" dirty, it is still a dirty branch
			return;
		}

		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			if (child.isDirtyBranch()) {
				return;
			}
		}

		this.setIsDirtyBranch(false);
		this.markParentBranchCleanIfPossible();
	}

	private boolean aspectIsPersistent(String aspectName) {
		return ! this.aspectIsTransient(aspectName);
	}

	private boolean aspectIsTransient(String aspectName) {
		return this.transientAspectNames().contains(aspectName);
	}

	/**
	 * Return a set of the object's transient aspect names.
	 * These are the aspects that, when they change, will NOT cause the
	 * object to be marked dirty.
	 * If you need instance-based calculation of your transient aspects,
	 * override this method. If class-based calculation is sufficient,
	 * override #addTransientAspectNamesTo(Set).
	 */
	protected final Set transientAspectNames() {
		synchronized (transientAspectNameSets) {
			Set transientAspectNames = (Set) transientAspectNameSets.get(this.getClass());
			if (transientAspectNames == null) {
				transientAspectNames = new HashSet();
				this.addTransientAspectNamesTo(transientAspectNames);
				transientAspectNameSets.put(this.getClass(), transientAspectNames);
			}
			return transientAspectNames;
		}
	}

	/**
	 * Add the object's transient aspect names to the specified set.
	 * These are the aspects that, when they change, will NOT cause the
	 * object to be marked dirty.
	 * If class-based calculation of your transient aspects is sufficient,
	 * override this method. If you need instance-based calculation,
	 * override #transientAspectNames().
	 */
	protected void addTransientAspectNamesTo(Set transientAspectNames) {
		transientAspectNames.add(DIRTY_BRANCH_PROPERTY);
		transientAspectNames.add(BRANCH_PROBLEMS_LIST);
		transientAspectNames.add(HAS_BRANCH_PROBLEMS_PROPERTY);
	// when you override this method, don't forget to include:
	//	super.addTransientAspectNamesTo(transientAspectNames);
	}

	/**
	 * Return the dirty nodes in the object's branch of the tree,
	 * including the node itself (if appropriate).
	 * Only really used for testing and debugging.
	 */
	public final Iterator allDirtyNodes() {
		return new FilteringIterator(this.allNodes()) {
			protected boolean accept(Object o) {
				return (o instanceof AbstractNodeModel) && ((AbstractNodeModel) o).isDirty();
			}
		};
	}


	// ********** problems **********

	/**
	 * Return the node's problems.
	 * This does NOT include the problems of the node's descendants.
	 * @see #branchProblems()
	 */
	public final Iterator problems() {
		return new CloneIterator(this.problems);	// removes are not allowed
	}

	/**
	 * Return the size of the node's problems.
	 * This does NOT include the problems of the node's descendants.
	 * @see #branchProblemsSize()
	 */
	public final int problemsSize() {
		return this.problems.size();
	}

	/**
	 * Return whether the node has problems
	 * This does NOT include the problems of the node's descendants.
	 * @see #hasBranchProblems()
	 */
	public final boolean hasProblems() {
		return ! this.problems.isEmpty();
	}

	/**
	 * Return all the node's problems along with all the
	 * node's descendants' problems.
	 * @see Node#branchProblems()
	 */
	public final ListIterator branchProblems() {
		return new CloneListIterator(this.branchProblems);	// removes are not allowed
	}

	/**
	 * Return the size of all the node's problems along with all the
	 * node's descendants' problems.
	 * @see Node#branchProblemsSize()
	 */
	public final int branchProblemsSize() {
		return this.branchProblems.size();
	}

	/**
	 * Return whether the node or any of its descendants have problems.
	 * @see Node#hasBranchProblems()
	 */
	public final boolean hasBranchProblems() {
		return ! this.branchProblems.isEmpty();
	}

	/**
	 * @see Node#containsBranchProblem(Problem)
	 */
	public final boolean containsBranchProblem(Problem problem) {
		return this.branchProblems.contains(problem);
	}

	protected final Problem buildProblem(String messageKey, Object[] messageArguments) {
		return new DefaultProblem(this, messageKey, messageArguments);
	}

	protected final Problem buildProblem(String messageKey) {
		return this.buildProblem(messageKey, EMPTY_PROBLEM_MESSAGE_ARGUMENTS);
	}

	protected final Problem buildProblem(String messageKey, Object messageArgument) {
		return this.buildProblem(messageKey, new Object[] {messageArgument});
	}

	protected final Problem buildProblem(String messageKey, Object messageArgument1, Object messageArgument2) {
		return this.buildProblem(messageKey, new Object[] {messageArgument1, messageArgument2});
	}

	protected final Problem buildProblem(String messageKey, Object messageArgument1, Object messageArgument2, Object messageArgument3) {
		return this.buildProblem(messageKey, new Object[] {messageArgument1, messageArgument2, messageArgument3});
	}

	/**
	 * Validate the node and all of its descendants,
	 * and update their sets of "branch" problems.
	 * If the node's "branch" problems have changed,
	 * notify the node's parent.
	 * @see Node#validateBranch()
	 */
	public void validateBranch() {
		if (this.validateBranchInternal()) {
			// if our "branch" problems have changed, then
			// our parent must rebuild its "branch" problems also
			this.rebuildParentBranchProblems();
		}
	}

	protected void rebuildParentBranchProblems() {
		if (this.parent != null) {
			this.parent.rebuildBranchProblems();
		}
	}

	/**
	 * INTRA-NODE API
	 * Validate the node and all of its descendants,
	 * and update their sets of "branch" problems.
	 * Return true if the collection of "branch" problems has changed.
	 * This method is for internal use only; it is not for
	 * client use.
	 * @see Node#validateBranchInternal()
	 */
	public boolean validateBranchInternal() {
		// rebuild "branch" problems in children first
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			// ignore the return value because we are going to rebuild our "branch"
			// problems no matter what, to see if they have changed
			child.validateBranchInternal();
		}

		this.problems.clear();
		this.addProblemsTo(this.problems);

		return this.checkBranchProblems();
	}

	/**
	 * Check for any problems and add them to the specified list.
	 * This method should ONLY add problems for this particular node;
	 * it should NOT add problems for any of this node's descendants
	 * or ancestors. (Although there will be times when it is debatable
	 * as to which node a problem "belongs" to....)
	 * 
	 * NB: This method should NOT modify ANY part of the node's state!
	 * It is a READ-ONLY behavior. ONLY the list of current problems
	 * passed in to the method should be modified.
	 */
	protected void addProblemsTo(List currentProblems) {
		// The default is to do nothing.
		// When you override this method, don't forget to include:
	//	super.addProblemsTo(currentProblems);
	}

	/**
	 * Rebuild the "branch" problems and return whether they have
	 * changed.
	 * NB: The entire collection of "branch" problems must be re-calculated
	 * with EVERY "significant" change - we cannot keep it in synch via
	 * change notifications because if a descendant with problems is
	 * removed or replaced we will not receive notification that its
	 * problems were removed from our "branch" problems.
	 */
	private boolean checkBranchProblems() {
		List oldBranchProblems = new Vector(this.branchProblems);
		int oldSize = this.branchProblems.size();

		this.branchProblems.clear();
		this.branchProblems.addAll(this.problems);
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			child.addBranchProblemsTo(this.branchProblems);
		}

		// if the size has changed to or from zero, our virtual flag has changed
		int newSize = this.branchProblems.size();
		if ((oldSize == 0) && (newSize != 0)) {
			this.firePropertyChanged(HAS_BRANCH_PROBLEMS_PROPERTY, false, true);
		} else if ((oldSize != 0) && (newSize == 0)) {
			this.firePropertyChanged(HAS_BRANCH_PROBLEMS_PROPERTY, true, false);
		}

		if (oldBranchProblems.equals(this.branchProblems)) {
			return false;		// our "branch" problems did not change
		}
		// our "branch" problems changed
		this.fireListChanged(BRANCH_PROBLEMS_LIST);
		return true;
	}

	/**
	 * INTRA-NODE API
	 * Add all the problems of the node and all
	 * the problems of its descendants to the
	 * specified collection.
	 * @see Node#addBranchProblemsTo(java.util.List)
	 */
	public final void addBranchProblemsTo(List list) {
		list.addAll(this.branchProblems);
	}

	/**
	 * INTRA-NODE API
	 * A child node's "branch" problems changed;
	 * therefore the node's "branch" problems have changed also and
	 * must be rebuilt.
	 * @see Node#rebuildBranchProblems()
	 */
	public final void rebuildBranchProblems() {
		if ( ! this.checkBranchProblems()) {
			throw new IllegalStateException("we should not get here unless our \"branch\" problems have changed");
		}
		this.rebuildParentBranchProblems();
	}

	/**
	 * Clear the node's "branch" problems and the "branch"
	 * problems of all of its descendants.
	 * If the node's "branch" problems have changed,
	 * notify the node's parent.
	 * @see Node#clearAllBranchProblems()
	 */
	public final void clearAllBranchProblems() {
		if (this.clearAllBranchProblemsInternal()) {
			// if our "branch" problems have changed, then
			// our parent must rebuild its "branch" problems also
			this.rebuildParentBranchProblems();
		}
	}

	/**
	 * INTRA-NODE API
	 * Clear the node's "branch" problems and the "branch"
	 * problems of all of its descendants.
	 * Return true if the collection of "branch" problems has changed.
	 * This method is for internal use only; it is not for
	 * client use.
	 * @see Node#clearAllBranchProblemsInternal()
	 */
	public final boolean clearAllBranchProblemsInternal() {
		if (this.branchProblems.isEmpty()) {
			return false;
		}
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			Node child = (Node) stream.next();		// pull out the child to ease debugging
			// ignore the return value because we are going to clear our "branch"
			// problems no matter what
			child.clearAllBranchProblemsInternal();
		}
		this.problems.clear();
		this.branchProblems.clear();
		this.firePropertyChanged(HAS_BRANCH_PROBLEMS_PROPERTY, true, false);
		this.fireListChanged(BRANCH_PROBLEMS_LIST);
		return true;
	}

	private boolean aspectIsSignificant(String aspectName) {
		return ! this.aspectIsInsignificant(aspectName);
	}

	private boolean aspectIsInsignificant(String aspectName) {
		return this.insignificantAspectNames().contains(aspectName);
	}

	/**
	 * Return a set of the object's "insignificant" aspect names.
	 * These are the aspects that, when they change, will NOT cause the
	 * object (or its containing tree) to be validated, i.e. checked for problems.
	 * If you need instance-based calculation of your "insignificant" aspects,
	 * override this method. If class-based calculation is sufficient,
	 * override #addInsignificantAspectNamesTo(Set).
	 */
	protected final Set insignificantAspectNames() {
		synchronized (insignificantAspectNameSets) {
			Set insignificantAspectNames = (Set) insignificantAspectNameSets.get(this.getClass());
			if (insignificantAspectNames == null) {
				insignificantAspectNames = new HashSet();
				this.addInsignificantAspectNamesTo(insignificantAspectNames);
				insignificantAspectNameSets.put(this.getClass(), insignificantAspectNames);
			}
			return insignificantAspectNames;
		}
	}

	/**
	 * Add the object's "insignificant" aspect names to the specified set.
	 * These are the aspects that, when they change, will NOT cause the
	 * object (or its containing tree) to be validated, i.e. checked for problems.
	 * If class-based calculation of your "insignificant" aspects is sufficient,
	 * override this method. If you need instance-based calculation,
	 * override #insignificantAspectNames().
	 */
	protected void addInsignificantAspectNamesTo(Set insignificantAspectNames) {
		insignificantAspectNames.add(COMMENT_PROPERTY);
		insignificantAspectNames.add(DIRTY_BRANCH_PROPERTY);
		insignificantAspectNames.add(BRANCH_PROBLEMS_LIST);
		insignificantAspectNames.add(HAS_BRANCH_PROBLEMS_PROPERTY);
	// when you override this method, don't forget to include:
	//	super.addInsignificantAspectNamesTo(insignificantAspectNames);
	}


	// ********** display methods **********

	/**
	 * Compare display strings.
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}

	/**
	 * Return a developer-friendly String. If you want something useful for
	 * displaying in a user interface, use #displayString().
	 * If you want to give more information in your #toString(),
	 * override #toString(StringBuffer sb). 
	 * Whatever you add to that string buffer will show up between the parentheses.
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer sb)
	 * @see #displayString()
	 */
	public final String toString() {
		return super.toString();
	}

}
