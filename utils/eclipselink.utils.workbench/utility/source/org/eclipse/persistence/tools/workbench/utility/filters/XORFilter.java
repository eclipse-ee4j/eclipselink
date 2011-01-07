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

/**
 * This filter will "accept" any object that is accepted by either
 * of the specified wrapped filters, but not both. Both filters will
 * always be evaluated.
 */
public class XORFilter
	extends CompoundFilter
{
	private static final long serialVersionUID = 1L;


	/**
	 * Construct a filter that will "accept" any object that is accept by either
	 * of the specified wrapped filters, but not by both.
	 */
	public XORFilter(Filter filter1, Filter filter2) {
		super(filter1, filter2);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.Filter#accept(Object)
	 */
	public boolean accept(Object o) {
		return this.filter1.accept(o) ^ this.filter2.accept(o);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.CompoundFilter#operatorString()
	 */
	protected String operatorString() {
		return "XOR";
	}

}
