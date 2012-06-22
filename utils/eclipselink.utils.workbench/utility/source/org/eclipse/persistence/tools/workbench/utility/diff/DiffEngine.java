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
package org.eclipse.persistence.tools.workbench.utility.diff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Given a pair of object graphs to compare, use a variety of user-
 * specified differentiators to compare the objects in the two graphs.
 * The differentiators are determined by the classes of the objects
 * being compared.
 * 
 * The diff engine logs all of its diffs. By default this log does nothing;
 * but a client-supplied log can be used to debug any unexpected diffs.
 */
public class DiffEngine implements Differentiator {

	/**
	 * This is the differentiator passed to the reflective differentiators
	 * for comparing field values.
	 */
	private final RecordingDifferentiator recordingDifferentiator;

	/**
	 * These are the user-supplied differentiators, keyed by class.
	 * This also holds the default differentiator at the key Object.class.
	 */
	private final Map userDifferentiators;

	/**
	 * This is a temporary cache of the reflective differentiators that
	 * is built up during a diff, keyed by the class of the
	 * objects being compared.
	 * It is cleared out at the end of the diff.
	 */
	private final Map reflectiveDifferentiatorCache;

	/**
	 * This is a temporary cache of the differentiators that
	 * is built up during a diff, keyed by the class of the
	 * objects being compared.
	 * It is cleared out at the end of the diff.
	 */
	private final Map differentiatorCache;

	/**
	 * This flag prevents the same thread from re-starting
	 * the diff engine while it is currently executing a diff.
	 */
	private boolean diffInProgress;

	/**
	 * All the diffs are logged here. By default this is a "null" log.
	 */
	private Log log;


	private static final Class OBJECT_CLASS = Object.class;
	private static final String[] EMPTY_STRING_ARRAY = new String[0];


	// ********** constructors/initialization **********

	public DiffEngine() {
		super();
		this.recordingDifferentiator = new RecordingDifferentiator();
		this.userDifferentiators = new HashMap();
		// the "equality" differentiator works best with primitives, strings, etc.
		this.userDifferentiators.put(OBJECT_CLASS, EqualityDifferentiator.instance());
		this.reflectiveDifferentiatorCache = new HashMap();
		this.differentiatorCache = new HashMap();
		this.diffInProgress = false;
		this.log = Log.NULL_INSTANCE;
	}


	// ********** Differentiator implementation **********

	/**
	 * synchronized so diffs are single-threaded
	 * @see Differentiator#diff(Object, Object)
	 */
	public synchronized Diff diff(Object object1, Object object2) {
		return this.diff(object1, object2, DifferentiatorAdapter.NORMAL);
	}

	/**
	 * synchronized so diffs are single-threaded
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public synchronized Diff keyDiff(Object object1, Object object2) {
		return this.diff(object1, object2, DifferentiatorAdapter.KEY);
	}

	private Diff diff(Object object1, Object object2, DifferentiatorAdapter adapter) {
		this.setUp();
		Diff diff = adapter.diff(this.recordingDifferentiator, object1, object2);
		this.tearDown();
		return diff;
	}

	/**
	 * @see Differentiator#comparesValueObjects()
	 */
	public boolean comparesValueObjects() {
		return false;
	}


	// ********** queries **********

	Differentiator differentiatorFor(Object object) {
		return this.differentiatorForClass((object == null) ? OBJECT_CLASS : object.getClass());
	}

	private Differentiator differentiatorForClass(Class javaClass) {
		// first look for a cached differentiator
		Differentiator differentiator = (Differentiator) this.differentiatorCache.get(javaClass);
		if (differentiator != null) {
			return differentiator;
		}
		// then look for a user differentiator for the class or its superclass
		Class tempClass = javaClass;
		while (tempClass != null) {
			differentiator = (Differentiator) this.userDifferentiators.get(tempClass);
			if (differentiator != null) {
				if (differentiator instanceof ReflectiveDifferentiator) {
					// if we find a reflective differentiator higher in the hierarchy,
					// extend it down to the current class
					this.expandReflectiveDifferentiatorCache(javaClass, differentiator.comparesValueObjects());
					return (Differentiator) this.differentiatorCache.get(javaClass);
				}
				this.differentiatorCache.put(javaClass, differentiator);
				return differentiator;
			}
			tempClass = tempClass.getSuperclass();
		}
		// this shouldn't happen - we should always get a hit on Object.class...
		throw new IllegalStateException("missing differentiator: " + javaClass.getName());
	}

	public synchronized Differentiator getUserDifferentiator(Class javaClass) {
		return (Differentiator) this.userDifferentiators.get(javaClass);
	}

	public synchronized Differentiator getDefaultDifferentiator() {
		return (Differentiator) this.userDifferentiators.get(OBJECT_CLASS);
	}

	public synchronized Differentiator getRecordingDifferentiator() {
		return this.recordingDifferentiator;
	}

	// ********** behavior **********

	private void setUp() {
		if (this.diffInProgress) {
			throw new IllegalStateException("Recursive calls to a DiffEngine are not allowed.");
		}
		this.diffInProgress = true;

		this.checkUserReflectiveDifferentiators();
		this.expandReflectiveDifferentiatorCache();
		this.recordingDifferentiator.setUp();
	}

	/**
	 * verify that none of the user-supplied reflective differentiators
	 * are beneath a user-supplied non-reflective differentiator in
	 * the class hierarchy; also copy the reflective differentiators
	 * to the reflective differentiator cache and the non-reflective
	 * differentiators to the differentiator cache
	 */
	private void checkUserReflectiveDifferentiators() {
		for (Iterator stream = this.userDifferentiators.entrySet().iterator(); stream.hasNext(); ) {
			Map.Entry entry = (Map.Entry) stream.next();
			Class javaClass = (Class) entry.getKey();
			Differentiator differentiator = (Differentiator) entry.getValue();
			if (differentiator instanceof ReflectiveDifferentiator) {
				this.checkUserReflectiveDifferentiator(javaClass.getSuperclass());
				this.reflectiveDifferentiatorCache.put(javaClass, differentiator);
			} else {
				this.differentiatorCache.put(javaClass, differentiator);
			}
		}
	}

	/**
	 * verify that all the user-specified differentiators for the superclasses
	 * of the specified class are reflective (excepting Object.class)
	 */
	private void checkUserReflectiveDifferentiator(Class javaClass) {
		if (javaClass == OBJECT_CLASS) {
			return;		// Object.class is a special case and is ignored
		}
		Differentiator differentiator = (Differentiator) this.userDifferentiators.get(javaClass);
		if ((differentiator == null) || (differentiator instanceof ReflectiveDifferentiator)) {
			this.checkUserReflectiveDifferentiator(javaClass.getSuperclass());		// recurse
		} else {
			throw new IllegalStateException("The differentiator for " + javaClass.getName() + " must be reflective: " + differentiator);
		}
	}

	/**
	 * go through the reflective differentiator cache, adding
	 * reflective differentiators for any superclasses that don't
	 * have one already
	 */
	private void expandReflectiveDifferentiatorCache() {
		// clone the map because we are going to modify it while we are looping over its entries
		for (Iterator stream = new HashMap(this.reflectiveDifferentiatorCache).entrySet().iterator(); stream.hasNext(); ) {
			Map.Entry entry = (Map.Entry) stream.next();
			Class javaClass = (Class) entry.getKey();
			boolean comparesValueObjects = ((ReflectiveDifferentiator) entry.getValue()).comparesValueObjects();
			this.expandReflectiveDifferentiatorCache(javaClass, comparesValueObjects);
		}
	}

	private void expandReflectiveDifferentiatorCache(Class javaClass, boolean comparesValueObjects) {
		if (javaClass == OBJECT_CLASS) {
			return;		// Object.class is a special case and is ignored
		}
		ReflectiveDifferentiator rd = (ReflectiveDifferentiator) this.reflectiveDifferentiatorCache.get(javaClass);
		if (rd == null) {
			rd = new ReflectiveDifferentiator(javaClass, this.recordingDifferentiator);
			rd.setComparesValueObjects(comparesValueObjects);
			this.reflectiveDifferentiatorCache.put(javaClass, rd);
		}
		this.expandReflectiveDifferentiatorCache(javaClass.getSuperclass(), comparesValueObjects);		// recurse
		// once all the superclass reflective differentiators are in place
		// build the composite differentiator and put it in the cache
		this.setUpDifferentiatorCache(javaClass);
	}

	/**
	 * gather up all the differentiators for the specified class;
	 * there should be one reflective differentiator per class in the
	 * class's hierarchy (except for Object.class) - add them if
	 * necessary;
	 */
	private void setUpDifferentiatorCache(Class javaClass) {
		if (this.differentiatorCache.get(javaClass) != null) {
			return;
		}
		List differentiators = new ArrayList();
		for (Class tempClass = javaClass; tempClass != OBJECT_CLASS; tempClass = tempClass.getSuperclass()) {
			differentiators.add(this.reflectiveDifferentiatorCache.get(tempClass));
		}
		Differentiator differentiator = null;
		if (differentiators.size() == 1) {
			differentiator = (Differentiator) differentiators.get(0);
		} else {
			// put the top of the hierarchy first
			differentiator =  new CompositeDifferentiator(CollectionTools.reverse(differentiators));
		}
		this.differentiatorCache.put(javaClass, differentiator);
	}

	public synchronized void setDefaultDifferentiator(Differentiator defaultDifferentiator) {
		this.userDifferentiators.put(OBJECT_CLASS, defaultDifferentiator);
	}

	public synchronized Differentiator setUserDifferentiator(Class javaClass, Differentiator userDifferentiator) {
		if (userDifferentiator == null) {
			throw new NullPointerException();
		}
		if (this.userDifferentiators.put(javaClass, userDifferentiator) != null) {
			throw new IllegalArgumentException("duplicate differentiator: " + javaClass.getName());
		}
		return userDifferentiator;
	}

	public synchronized Differentiator removeUserDifferentiator(Class javaClass) {
		if (javaClass == OBJECT_CLASS) {
			throw new IllegalArgumentException("The differentiator for java.lang.Object cannot be removed.");
		}
		return (Differentiator) this.userDifferentiators.remove(javaClass);
	}

	public synchronized ReflectiveDifferentiator addReflectiveDifferentiator(Class javaClass) {
		return this.addReflectiveDifferentiator(javaClass, EMPTY_STRING_ARRAY);
	}

	public synchronized ReflectiveDifferentiator addReflectiveDifferentiator(Class javaClass, String ignoredFieldName) {
		return this.addReflectiveDifferentiator(javaClass, new String[] {ignoredFieldName});
	}

	public synchronized ReflectiveDifferentiator addReflectiveDifferentiator(Class javaClass, String ignoredFieldName1, String ignoredFieldName2) {
		return this.addReflectiveDifferentiator(javaClass, new String[] {ignoredFieldName1, ignoredFieldName2});
	}

	public synchronized ReflectiveDifferentiator addReflectiveDifferentiator(Class javaClass, String ignoredFieldName1, String ignoredFieldName2, String ignoredFieldName3) {
		return this.addReflectiveDifferentiator(javaClass, new String[] {ignoredFieldName1, ignoredFieldName2, ignoredFieldName3});
	}

	public synchronized ReflectiveDifferentiator addReflectiveDifferentiator(Class javaClass, String ignoredFieldName1, String ignoredFieldName2, String ignoredFieldName3, String ignoredFieldName4) {
		return this.addReflectiveDifferentiator(javaClass, new String[] {ignoredFieldName1, ignoredFieldName2, ignoredFieldName3, ignoredFieldName4});
	}

	public synchronized ReflectiveDifferentiator addReflectiveDifferentiator(Class javaClass, String[] ignoredFieldNames) {
		return this.addReflectiveDifferentiator(javaClass, ignoredFieldNames, EMPTY_STRING_ARRAY);
	}

	public synchronized ReflectiveDifferentiator addReflectiveDifferentiator(Class javaClass, String[] ignoredFieldNames, String[] keyFieldNames) {
		ReflectiveDifferentiator rd = new ReflectiveDifferentiator(javaClass, this.recordingDifferentiator);
		rd.ignoreFieldsNamed(ignoredFieldNames);
		rd.addKeyFieldsNamed(keyFieldNames);
		this.setUserDifferentiator(javaClass, rd);
		return rd;
	}

	public void setLog(String fileName) throws FileNotFoundException {
		this.setLog(new WriterLog(fileName));
	}
		
	public void setLog(Log log) {
		this.log = log;
	}
		
	void log(Diff diff) {
		this.log.log(diff);
	}

	private void tearDown() {
		this.recordingDifferentiator.tearDown();
		this.differentiatorCache.clear();
		this.reflectiveDifferentiatorCache.clear();
		this.diffInProgress = false;
		this.log.close();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this);
	}


	// ******************** pluggable interface ********************

	/**
	 * Interface allowing for a pluggable log.
	 */
	public interface Log {

		/**
		 * Log the specified diff as desired.
		 */
		void log(Diff diff);

		/**
		 * Close the log.
		 */
		void close();

		Log NULL_INSTANCE =
			new Log() {
				// nothing is logged
				public void log(Diff diff) {
					// do nothing
				}
				public void close() {
					// do nothing
				}
				public String toString() {
					return "NullLog";
				}
			};
	}


	/**
	 * Implementation of Log that dumps the diffs to either an
	 * output stream or a writer. The log can be configured to
	 * log "value" diffs; by default it will NOT log "value" diffs.
	 */
	public static class WriterLog implements Log {
		private IndentingPrintWriter writer;
		private boolean logsValueDiffs;

		public WriterLog() {
			this(System.out);
		}
		public WriterLog(String fileName) throws FileNotFoundException {
			this(new File(fileName));
		}
		public WriterLog(File file) throws FileNotFoundException {
			this(new FileOutputStream(file));
		}
		public WriterLog(OutputStream stream) {
			this(new OutputStreamWriter(stream));
		}
		public WriterLog(Writer writer) {
			this(new IndentingPrintWriter(new BufferedWriter(writer, 32768)));
		}
		public WriterLog(IndentingPrintWriter writer) {
			super();
			this.writer = writer;
			this.logsValueDiffs = false;
		}
		public void log(Diff diff) {
			if (diff.getDifferentiator().comparesValueObjects() && ! this.logsValueDiffs) {
				return;
			}
			if (diff.identical()) {
				this.writer.println(Diff.NO_DIFFERENCE_DESCRIPTION);
				this.writer.print("object 1: ");
				this.writer.println(diff.getObject1());
				this.writer.print("object 2: ");
				this.writer.println(diff.getObject2());
			} else {
				diff.appendDescription(this.writer);
			}
			this.writer.println();
		}
		public void close() {
			this.writer.close();
		}
		public boolean logsValueDiffs() {
			return this.logsValueDiffs;
		}
		public void setLogsValueDiffs(boolean logsValueDiffs) {
			this.logsValueDiffs = logsValueDiffs;
		}
	}


	// ******************** helper class ********************

	/**
	 * This differentiator records diffs as they occur and throws an
	 * exception if a "reference object" is compared twice. Though
	 * multiple "key diffs" are allowed.
	 */
	private class RecordingDifferentiator implements Differentiator {
		private IdentityHashMap previousDiffs1;
		private IdentityHashMap previousDiffs2;


		// ********** constructors **********

		RecordingDifferentiator() {
			super();
			this.previousDiffs1 = new IdentityHashMap();
			this.previousDiffs2 = new IdentityHashMap();
		}


		// ********** Differentiator implementation **********

		/**
		 * @see Differentiator#diff(Object, Object)
		 */
		public Diff diff(Object object1, Object object2) {
			Differentiator differentiator = DiffEngine.this.differentiatorFor(object1);
			if ( ! differentiator.comparesValueObjects()) {
				// we should only hit "reference objects" once in each object graph
				this.checkDiff(object1, this.previousDiffs1);
				this.checkDiff(object2, this.previousDiffs2);
			}
			Diff diff = differentiator.diff(object1, object2);
			DiffEngine.this.log(diff);
			return diff;
		}

		/**
		 * @see Differentiator#keyDiff(Object, Object)
		 */
		public Diff keyDiff(Object object1, Object object2) {
			Differentiator differentiator = DiffEngine.this.differentiatorFor(object1);
			Diff diff = differentiator.keyDiff(object1, object2);
			DiffEngine.this.log(diff);
			return diff;
		}

		/**
		 * @see Differentiator#comparesValueObjects()
		 */
		public boolean comparesValueObjects() {
			return false;
		}


		// ********** behavior **********

		void setUp() {
			// do nothing for now
		}

		private void checkDiff(Object object, IdentityHashMap previousDiffs) {
			// "reference objects" should only be diffed once...
			Object prev = previousDiffs.put(object, object);	// "identity set"
			if (prev != null) {
				// if this exception is thrown that means you probably have two
				// fields that claim to "own" the same object - only one of them
				// can be a "composite" field, the other must be changed to a
				// "reference" field;
				// @see ReflectiveDifferentiator#addReferenceFieldNamed(String)
				throw new IllegalArgumentException("duplicate diff: " + object);
			}
		}

		void tearDown() {
			// System.out.println("total reference diffs: " + this.previousDiffs1.size());
			this.previousDiffs1.clear();
			this.previousDiffs2.clear();
		}

		/**
		 * @see Object#toString()
		 */
		public String toString() {
			return StringTools.buildToStringFor(this);
		}

	}

}
