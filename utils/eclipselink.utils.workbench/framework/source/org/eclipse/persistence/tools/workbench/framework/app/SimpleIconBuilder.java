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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.io.Serializable;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Facile implementation of the IconBuilder interface.
 */
public class SimpleIconBuilder
	implements IconBuilder, Cloneable, Serializable
{
	private Icon icon;

	private static final long serialVersionUID = 1L;


	/**
	 * Construct an icon builder that will simply return the specified icon.
	 */
	public SimpleIconBuilder(Icon icon) {
		super();
		this.icon = icon;
	}

	/**
	 * @see IconBuilder#buildIcon()
	 */
	public Icon buildIcon() {
		return this.icon;
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
		if (o instanceof SimpleIconBuilder) {
			return this.equals((SimpleIconBuilder) o);
		}
		return false;
	}

	public boolean equals(SimpleIconBuilder other) {
		return (this.icon == null) ? other.icon == null : this.icon.equals(other.icon);
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return (this.icon == null) ? 0 : this.icon.hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.icon);
	}

}
