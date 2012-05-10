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
package org.eclipse.persistence.tools.workbench.utility.string;

import java.io.Serializable;

/**
 * Implement some behavior with calls to #getString().
 */
public abstract class AbstractStringHolder
	implements StringHolder, Serializable, Cloneable, Comparable
{

	protected AbstractStringHolder() {
		super();
	}

	public abstract String getString();

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	public boolean equals(Object o) {
		if ( ! (o instanceof StringHolder)) {
			return false;
		}
		StringHolder other = (StringHolder) o;
		String s = this.getString();
		return (s == null) ?
				(other.getString() == null)
			:
				s.equals(other.getString());
	}

	public int hashCode() {
		String s = this.getString();
		return (s == null) ? 0 : s.hashCode();
	}

	public int compareTo(Object o) {
		return this.compareTo((StringHolder) o);
	}

	public int compareTo(StringHolder sh) {
		return this.getString().compareTo(sh.getString());
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.getString());
	}

}
