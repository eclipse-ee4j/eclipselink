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
package org.eclipse.persistence.tools.workbench.utility;

import java.io.Serializable;

/**
 * This class provides three states: true, false, and undefined.
 */
public class TriStateBoolean
	implements Cloneable, Serializable
{
	/** The state of the tri-state boolean. */
	private final Boolean value;


	private static final long serialVersionUID = 1L;


	// ********** pre-defined constants **********

	/** A tri-state boolean with a false state. */
	public static final TriStateBoolean FALSE = new TriStateBoolean(Boolean.FALSE);

	/** A tri-state boolean with a true state. */
	public static final TriStateBoolean TRUE = new TriStateBoolean(Boolean.TRUE);

	/** A tri-state boolean with an undefined state. */
	public static final TriStateBoolean UNDEFINED = new TriStateBoolean((Boolean) null);


	// ********** static methods **********

	/**
	 * Convert the specified boolean into a corresponding tri-state boolean.
	 */
	public static TriStateBoolean valueOf(Boolean value) {
		return (value == null) ? UNDEFINED : (value.booleanValue() ? TRUE : FALSE);
	}

	/**
	 * Convert the specified string into a corresponding tri-state boolean.
	 * 	- null or "undefined" => null
	 * 	- "true" => TRUE
	 * 	- anything else => FALSE
	 * Strings are case-insensitive
	 */
	public static TriStateBoolean valueOf(String value) {
		return valueOf(toBoolean(value));
	}

	/**
	 * Convert the specified boolean into a corresponding tri-state boolean.
	 */
	public static TriStateBoolean valueOf(boolean value) {
		return value ? TRUE : FALSE;
	}

	/**
	 * Return the tri-state boolean corresponding to the specified
	 * system property.
	 */
	public static TriStateBoolean getTriStateBoolean(String name) {
		try {
			return valueOf(System.getProperty(name));
		} catch (IllegalArgumentException ex) {	// return UNDEFINED
		} catch (NullPointerException ex) {	// return UNDEFINED
		}
		return UNDEFINED;
	}

	/**
	 * Convert the specified string into a corresponding boolean.
	 * null or "undefined" => null
	 * "true" => TRUE
	 * anything else => FALSE
	 * Strings are case-insensitive
	 */
	private static Boolean toBoolean(String value) {
		return ((value == null) || value.equalsIgnoreCase("undefined")) ? null : Boolean.valueOf(value);
	}


	// ********** constructors **********

	/**
	 * Construct a tri-state boolean corresponding to the specified boolean.
	 * If the boolean is null, the tri-state boolean will be undefined.
	 */
	public TriStateBoolean(Boolean value) {
		super();
		// always resolve to the standard Boolean constants
		this.value = (value == null) ? null : (value.booleanValue() ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * Construct a tri-state boolean corresponding to the specified boolean.
	 */
	public TriStateBoolean(boolean value) {
		super();
		// always resolve to the standard Boolean constants
		this.value = Boolean.valueOf(value);
	}

	/**
	 * Construct a tri-state boolean corresponding to the specified string.
	 * 	- null or "undefined" => null
	 * 	- "true" => TRUE
	 * 	- anything else => FALSE
	 * Strings are case-insensitive
	 */
	public TriStateBoolean(String value) {
		this(toBoolean(value));
	}


	// ********** queries **********

	/**
	 * Return the value of the tri-state boolean:
	 * null if the tri-state boolean is undefined;
	 * Boolean.TRUE if the tri-state boolean is true;
	 * Boolean.FALSE if the tri-state boolean is false.
	 */
	public Boolean getValue() {
		return this.value;
	}

	/**
	 * Return the boolean value of the tri-state boolean. If the tri-state
	 * boolean is undefined, throw an exception.
	 */
	public boolean booleanValue() {
		if (this.value == null) {
			throw new IllegalStateException();
		}
		return this.value.booleanValue();
	}

	/**
	 * Return whether the tri-state boolean has the specified boolean value.
	 */
	public boolean valueIs(boolean b) {
		return (this.value == null) ? false : (this.value.booleanValue() == b);
	}

	/**
	 * Return whether the tri-state boolean is false.
	 */
	public boolean isFalse() {
		return this.valueIs(false);
	}

	/**
	 * Return whether the tri-state boolean is true.
	 */
	public boolean isTrue() {
		return this.valueIs(true);
	}

	/**
	 * Return whether the tri-state boolean is undefined.
	 */
	public boolean isUndefined() {
		return this.value == null;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		return (o instanceof TriStateBoolean) &&
			this.value == ((TriStateBoolean) o).value;
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return (this.value == null) ? 0 : this.value.hashCode();
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
	 * Return "undefined", "true", or "false", as appropriate.
	 * @see Object#toString()
	 */
	public String toString() {
		return (this.value == null) ? "undefined" : this.value.toString();
	}

}
