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

/**
 * This filter will "accept" any object that is accept by both
 * of the specified wrapped filters. The first filter will always
 * be evaluated, while the second will only be evaluated if
 * necessary.
 */
public class ANDFilter
	extends CompoundFilter
{
	private static final long serialVersionUID = 1L;


	/**
	 * Construct a filter that will "accept" any object that is accept by all
	 * of the specified filters.
	 */
	public static Filter and(Filter[] filters) {
		int len = filters.length;
		if (len == 0) {
			return Filter.NULL_INSTANCE;
		}
		if (len == 1) {
			return filters[0];
		}
		ANDFilter filter = new ANDFilter(filters[0], filters[1]);
		for (int i = 2; i < len; i++) {
			filter = new ANDFilter(filter, filters[i]);
		}
		return filter;
	}

	/**
	 * Construct a filter that will "accept" any object that is accept by both
	 * of the specified wrapped filters.
	 */
	public ANDFilter(Filter filter1, Filter filter2) {
		super(filter1, filter2);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.Filter#accept(Object)
	 */
	public boolean accept(Object o) {
		return this.filter1.accept(o) && this.filter2.accept(o);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.CompoundFilter#operatorString()
	 */
	protected String operatorString() {
		return "AND";
	}

}
