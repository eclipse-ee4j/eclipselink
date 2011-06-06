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
package org.eclipse.persistence.tools.workbench.utility.node;

import java.util.Arrays;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This class is a straightforward implementation of the Problem interface.
 */
public class DefaultProblem
	implements Problem
{
	private final Node source;
	private final String messageKey;
	private final Object[] messageArguments;


	DefaultProblem(Node source, String messageKey, Object[] messageArguments) {
		super();
		this.source = source;
		this.messageKey = messageKey;
		this.messageArguments = messageArguments;
	}


	// ********** Problem implementation **********

	/**
	 * @see Problem#getSource()
	 */
	public Node getSource() {
		return this.source;
	}

	/**
	 * @see Problem#getMessageKey()
	 */
	public String getMessageKey() {
		return this.messageKey;
	}

	/**
	 * @see Problem#getMessageArguments()
	 */
	public Object[] getMessageArguments() {
		return this.messageArguments;
	}


	// ********** Object overrides **********

	/**
	 * We implement #equals(Object) because problems are repeatedly
	 * re-calculated and the resulting problems merged with the existing
	 * set of problems; and we want to keep the original problems and
	 * ignore any freshly-generated duplicates.
	 * Also, problems are not saved to disk....
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if ( ! (o instanceof Problem)) {
			return false;
		}
		Problem other = (Problem) o;
		return this.source == other.getSource()
				&& this.messageKey.equals(other.getMessageKey())
				&& Arrays.equals(this.messageArguments, other.getMessageArguments());
	}

	/**
	 * @see #equals(Object)
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.source.hashCode() ^ this.messageKey.hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.messageKey);
	}

}
