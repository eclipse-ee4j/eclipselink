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
package org.eclipse.persistence.tools.workbench.utility.filters;

import java.io.Serializable;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This filter will "accept" any object that is NOT accepted by
 * the specified wrapped filter.
 */
public class NOTFilter
	implements Filter, Cloneable, Serializable
{
	protected Filter filter;

	private static final long serialVersionUID = 1L;


	/**
	 * Construct a filter that will "accept" any object that is NOT accepted
	 * by the specified wrapped filter.
	 */
	public NOTFilter(Filter filter) {
		super();
		if (filter == null) {
			throw new NullPointerException();
		}
		this.filter = filter;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.Filter#accept(Object)
	 */
	public boolean accept(Object o) {
		return ! this.filter.accept(o);
	}

	/**
	 * Return filter.
	 */
	public Filter getFilter() {
		return this.filter;
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
		if ( ! (o instanceof NOTFilter)) {
			return false;
		}
		return this.filter.equals(((NOTFilter) o).filter);
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.filter.hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.filter);
	}

}
