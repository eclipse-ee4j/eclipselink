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
package org.eclipse.persistence.tools.workbench.utility.diff;

import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This differentiator compares two map entries.
 * The two entries are "identical" if their keys and values are
 * both "identical". This applies to both #keyDiff(Object, Object)
 * and #diff(Object, Object).
 */
public class MapEntryDifferentiator implements Differentiator {
	private Differentiator keyDifferentiator;
	private Differentiator valueDifferentiator;


	/**
	 * default to "equality" differentiators for both the key and the value
	 */
	public MapEntryDifferentiator() {
		this(EqualityDifferentiator.instance(), EqualityDifferentiator.instance());
	}

	public MapEntryDifferentiator(Differentiator keyDifferentiator, Differentiator valueDifferentiator) {
		super();
		this.keyDifferentiator = keyDifferentiator;
		this.valueDifferentiator = valueDifferentiator;
	}

	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		return this.diff(object1, object2, DifferentiatorAdapter.NORMAL);
	}

	/**
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public Diff keyDiff(Object object1, Object object2) {
		return this.diff(object1, object2, DifferentiatorAdapter.KEY);
	}

	private Diff diff(Object object1, Object object2, DifferentiatorAdapter adapter) {
		if ((object1 == null) || (object2 == null)) {
			// both arguments should be map entries
			throw new NullPointerException();
		}
		Map.Entry entry1 = (Map.Entry) object1;
		Map.Entry entry2 = (Map.Entry) object2;
		Diff keyDiff = adapter.diff(this.keyDifferentiator, entry1.getKey(), entry2.getKey());
		Diff valueDiff = adapter.diff(this.valueDifferentiator, entry1.getValue(), entry2.getValue());
		return new MapEntryDiff(entry1, entry2, keyDiff, valueDiff, this);
	}

	/**
	 * map entries should only belong to a single map
	 * @see Differentiator#comparesValueObjects()
	 */
	public boolean comparesValueObjects() {
		return false;
	}

	public Differentiator getKeyDifferentiator() {
		return this.keyDifferentiator;
	}
	public void setKeyDifferentiator(Differentiator keyDifferentiator) {
		this.keyDifferentiator = keyDifferentiator;
	}

	public Differentiator getValueDifferentiator() {
		return this.valueDifferentiator;
	}
	public void setValueDifferentiator(Differentiator valueDifferentiator) {
		this.valueDifferentiator = valueDifferentiator;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.valueDifferentiator);
	}

}
