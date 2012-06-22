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
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.diff.CompositeDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ContainerDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffWrapper;
import org.eclipse.persistence.tools.workbench.utility.diff.MapEntryDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.NullDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.OrderedContainerDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.OrderedContainerElementDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveFieldDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.SimpleDiff;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;

/**
 * lots of hacking here to pull out the various leaf diffs;
 * might be able to delegate to the diffs themselves, but
 * we would probably need another interface (e.g. TestDiff)
 * and it would still be a bit hacky
 */
final class DiffTestTools {

	/**
	 * return all the "leaf" diffs contained by the specified diff
	 * (i.e. all the diffs that do not contain other diffs)
	 */
	static Iterator leafDiffs(Diff diff) {
		Collection leafDiffs = new ArrayList();
		addLeafDiffsTo(diff, leafDiffs);
		return leafDiffs.iterator();
	}

	private static void addLeafDiffsTo(Diff diff, Collection leafDiffs) {
		if (diff instanceof CompositeDiff) {
			addChildrenTo((CompositeDiff) diff, leafDiffs);
		} else if (diff instanceof DiffWrapper) {
			addChildTo((DiffWrapper) diff, leafDiffs);
		} else if (diff instanceof MapEntryDiff) {
			addChildTo((MapEntryDiff) diff, leafDiffs);
		} else if (diff instanceof SimpleDiff) {
			leafDiffs.add(diff);
		} else if (diff instanceof NullDiff) {
			leafDiffs.add(diff);
		} else {
			throw new IllegalArgumentException("this method needs to be updated to handle new Diff implementations");
		}
	}

	private static void addChildrenTo(CompositeDiff diff, Collection leafDiffs) {
		Diff[] children = diff.getDiffs();
		for (int i = 0; i < children.length; i++) {
			addLeafDiffsTo(children[i], leafDiffs);
		}
		if (diff instanceof ContainerDiff) {
			ContainerDiff containerDiff = (ContainerDiff) diff;
			if ((containerDiff.getAddedElements().length != 0) || (containerDiff.getRemovedElements().length != 0)) {
				// if we have any adds or removes, we treat this field as a leaf AND a branch
				leafDiffs.add(containerDiff);
			}
		}
	}

	private static void addChildTo(DiffWrapper diff, Collection leafDiffs) {
		addLeafDiffsTo(diff.getDiff(), leafDiffs);
	}

	private static void addChildTo(MapEntryDiff diff, Collection leafDiffs) {
		addLeafDiffsTo(diff.getKeyDiff(), leafDiffs);
		addLeafDiffsTo(diff.getValueDiff(), leafDiffs);
	}

	static Iterator differentLeafDiffs(Diff diff) {
		return new FilteringIterator(leafDiffs(diff)) {
			protected boolean accept(Object next) {
				return ((Diff) next).different();
			}
		};
	}

	static List differentLeafDiffList(Diff diff) {
		return CollectionTools.list(differentLeafDiffs(diff));
	}


	/**
	 * return all the "leaf" reflective field diffs contained by the specified diff
	 * (i.e. all the reflective field diffs that do not contain other reflective
	 * field diffs)
	 */
	static Iterator leafReflectiveFieldDiffs(Diff diff) {
		Collection leafDiffs = new ArrayList();
		addLeafReflectiveFieldDiffsTo(diff, leafDiffs);
		return leafDiffs.iterator();
	}

	private static void addLeafReflectiveFieldDiffsTo(Diff diff, Collection leafDiffs) {
		if (diff instanceof CompositeDiff) {
			addReflectiveFieldChildrenTo((CompositeDiff) diff, leafDiffs);
		} else if (diff instanceof ReflectiveFieldDiff) {
			addReflectiveFieldChildTo((ReflectiveFieldDiff) diff, leafDiffs);
		} else if (diff instanceof OrderedContainerElementDiff) {
			// this should probably only contain ReflectiveDiffs...
			addLeafReflectiveFieldDiffsTo(((OrderedContainerElementDiff) diff).getDiff(), leafDiffs);
		} else if (diff instanceof MapEntryDiff) {
			addLeafReflectiveFieldDiffsTo(((MapEntryDiff) diff).getKeyDiff(), leafDiffs);
			addLeafReflectiveFieldDiffsTo(((MapEntryDiff) diff).getValueDiff(), leafDiffs);
		} else if (diff instanceof NullDiff) {
			// occurs when comparing containers of "primitives"
		} else {
			throw new IllegalArgumentException("hmmm - something's wrong...");
		}
	}

	private static void addReflectiveFieldChildrenTo(CompositeDiff diff, Collection leafDiffs) {
		Diff[] children = diff.getDiffs();
		for (int i = 0; i < children.length; i++) {
			addLeafReflectiveFieldDiffsTo(children[i], leafDiffs);
		}
	}

	private static void addReflectiveFieldChildTo(ReflectiveFieldDiff diff, Collection leafDiffs) {
		Diff childDiff = diff.getDiff();
		if (childDiff instanceof ContainerDiff) {
			ContainerDiff containerChildDiff = (ContainerDiff) childDiff;
			if ((containerChildDiff.getAddedElements().length != 0) || (containerChildDiff.getRemovedElements().length != 0)) {
				// if we have any adds or removes, we treat this field as a leaf AND a branch
				leafDiffs.add(diff);
			}
			addReflectiveFieldChildrenTo((CompositeDiff) childDiff, leafDiffs);
		} else if (childDiff instanceof OrderedContainerDiff) {
			OrderedContainerDiff orderedContainerChildDiff = (OrderedContainerDiff) childDiff;
			if (containsAnyAddedOrRemovedChildren(orderedContainerChildDiff)) {
				// if we have any adds or removes, we treat this field as a leaf AND a branch
				leafDiffs.add(diff);
			}
			if (containsAnyModifiedChildren(orderedContainerChildDiff)) {
				addReflectiveFieldChildrenTo((CompositeDiff) childDiff, leafDiffs);
			}
		} else if (childDiff instanceof ReflectiveDiff) {
			addReflectiveFieldChildrenTo((CompositeDiff) childDiff, leafDiffs);
		} else if (childDiff instanceof CompositeDiff) {
			addReflectiveFieldChildrenTo((CompositeDiff) childDiff, leafDiffs);
		} else if (childDiff instanceof SimpleDiff) {
			leafDiffs.add(diff);
		} else if (childDiff instanceof NullDiff) {
			leafDiffs.add(diff);
		} else {
			throw new IllegalArgumentException("hmmm - something's wrong...");
		}
	}

	private static boolean containsAnyAddedOrRemovedChildren(OrderedContainerDiff orderedContainerChildDiff) {
		Diff[] children = orderedContainerChildDiff.getDiffs();
		for (int i = 0; i < children.length; i++) {
			OrderedContainerElementDiff child = (OrderedContainerElementDiff) children[i];
			if (child.elementWasAdded() || child.elementWasRemoved()) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsAnyModifiedChildren(OrderedContainerDiff orderedContainerChildDiff) {
		Diff[] children = orderedContainerChildDiff.getDiffs();
		for (int i = 0; i < children.length; i++) {
			OrderedContainerElementDiff child = (OrderedContainerElementDiff) children[i];
			if (child.elementWasModified()) {
				return true;
			}
		}
		return false;
	}

	static Iterator differentLeafReflectiveFieldDiffs(Diff diff) {
		return new FilteringIterator(leafReflectiveFieldDiffs(diff)) {
			protected boolean accept(Object next) {
				return ((Diff) next).different();
			}
		};
	}

	static List differentLeafReflectiveFieldDiffList(Diff diff) {
		return CollectionTools.list(differentLeafReflectiveFieldDiffs(diff));
	}

}
