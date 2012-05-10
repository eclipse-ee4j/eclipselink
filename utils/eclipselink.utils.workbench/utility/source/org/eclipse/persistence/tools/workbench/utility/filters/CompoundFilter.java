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
package org.eclipse.persistence.tools.workbench.utility.filters;

import java.io.Serializable;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This filter provides a simple framework for combining the behavior
 * of a pair of filters.
 */
public abstract class CompoundFilter
	implements Filter, Cloneable, Serializable
{
	protected Filter filter1;
	protected Filter filter2;

	private static final long serialVersionUID = 1L;


	/**
	 * Construct a filter that will "accept" any object that is accept by both
	 * of the specified wrapped filters.
	 */
	public CompoundFilter(Filter filter1, Filter filter2) {
		super();
		if ((filter1 == null) || (filter2 == null)) {
			throw new NullPointerException();
		}
		this.filter1 = filter1;
		this.filter2 = filter2;
	}

	/**
	 * Return filter1.
	 */
	public Filter getFilter1() {
		return this.filter1;
	}

	/**
	 * Return filter2.
	 */
	public Filter getFilter2() {
		return this.filter2;
	}

	/**
	 * @see Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if ( ! (o instanceof CompoundFilter)) {
			return false;
		}
		CompoundFilter other = (CompoundFilter) o;
		return (this.filter1.equals(other.filter1) && this.filter2.equals(other.filter2)) ||
				(this.filter1.equals(other.filter2) && this.filter2.equals(other.filter1));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.filter1.hashCode() ^ this.filter2.hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.filter1 + " " + this.operatorString() + " " + this.filter2);
	}

	/**
	 * Return a string representation of the filter's operator.
	 */
	protected abstract String operatorString();

}
