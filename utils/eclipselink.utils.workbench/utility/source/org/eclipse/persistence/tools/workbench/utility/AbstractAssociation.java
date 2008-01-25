/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.utility;

/**
 * Implement some of the methods in Association that can
 * be defined in terms of the other methods.
 */
public abstract class AbstractAssociation
	implements Association
{

	/**
	 * Default constructor.
	 */
	protected AbstractAssociation() {
		super();
	}

	/**
	 * @see Association#equals(Object)
	 */
	public synchronized boolean equals(Object o) {
		if ( ! (o instanceof Association)) {
			return false;
		}
		Association other = (Association) o;
		return (this.getKey() == null ?
					other.getKey() == null : this.getKey().equals(other.getKey()))
			&& (this.getValue() == null ?
					other.getValue() == null : this.getValue().equals(other.getValue()));
	}

	/**
	 * @see Association#hashCode()
	 */
	public synchronized int hashCode() {
		return (this.getKey() == null ? 0 : this.getKey().hashCode())
			^ (this.getValue() == null ? 0 : this.getValue().hashCode());
	}

	/**
	 * @see Object#toString()
	 */
	public synchronized String toString() {
		return this.getKey() + " => " + this.getValue();
	}

}
