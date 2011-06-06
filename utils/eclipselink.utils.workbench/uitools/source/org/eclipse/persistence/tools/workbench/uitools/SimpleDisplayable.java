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
package org.eclipse.persistence.tools.workbench.uitools;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;


/**
 * This implementation of Displayable converts any Object
 * to a Displayable. Subclass it to override #displayString() and
 * #icon() if necessary. Change notification will be fired if the
 * object is changed.
 * 
 * This can be used for Strings - the display string
 * will simply be the String itself.
 */
public class SimpleDisplayable
	extends AbstractModel
	implements Displayable
{
	/** The object to be converted to a Displayable. */
	protected Object object;


	/**
	 * Construct a displayable for the specified object.
	 */
	public SimpleDisplayable(Object object) {
		super();
		this.object = object;
	}

	public SimpleDisplayable(boolean b) {
		this(Boolean.valueOf(b));
	}

	public SimpleDisplayable(char c) {
		this(new Character(c));
	}

	public SimpleDisplayable(byte b) {
		this(new Byte(b));
	}

	public SimpleDisplayable(short s) {
		this(new Short(s));
	}

	public SimpleDisplayable(int i) {
		this(new Integer(i));
	}

	public SimpleDisplayable(long l) {
		this(new Long(l));
	}

	public SimpleDisplayable(float f) {
		this(new Float(f));
	}

	public SimpleDisplayable(double d) {
		this(new Double(d));
	}


	// ********** Displayable implementation **********

	/**
	 * @see Displayable#displayString()
	 */
	public String displayString() {
		return this.object.toString();
	}

	/**
	 * @see Displayable#icon()
	 */
	public Icon icon() {
		return null;
	}


	// ********** Comparable implementation **********

	/**
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}


	// ********** accessors **********

	public Object getObject() {
		return this.object;
	}

	public void setObject(Object object) {
		String oldDisplayString = this.displayString();
		Icon oldIcon = this.icon();
		this.object = object;
		this.firePropertyChanged(DISPLAY_STRING_PROPERTY, oldDisplayString, this.displayString());
		this.firePropertyChanged(ICON_PROPERTY, oldIcon, this.icon());
	}

	public boolean getBoolean() {
		return ((Boolean) this.object).booleanValue();
	}

	public void setBoolean(boolean b) {
		this.setObject(Boolean.valueOf(b));
	}

	public char getChar() {
		return ((Character) this.object).charValue();
	}

	public void setChar(char c) {
		this.setObject(new Character(c));
	}

	public byte getByte() {
		return ((Byte) this.object).byteValue();
	}

	public void setByte(byte b) {
		this.setObject(new Byte(b));
	}

	public short getShort() {
		return ((Short) this.object).shortValue();
	}

	public void setShort(short s) {
		this.setObject(new Short(s));
	}

	public int getInt() {
		return ((Integer) this.object).intValue();
	}

	public void setInt(int i) {
		this.setObject(new Integer(i));
	}

	public long getLong() {
		return ((Long) this.object).longValue();
	}

	public void setLong(long l) {
		this.setObject(new Long(l));
	}

	public float getFloat() {
		return ((Float) this.object).floatValue();
	}

	public void setFloat(float f) {
		this.setObject(new Float(f));
	}

	public double getDouble() {
		return ((Double) this.object).doubleValue();
	}

	public void setDouble(double d) {
		this.setObject(new Double(d));
	}


	// ********** override methods **********

	public void toString(StringBuffer sb) {
		sb.append(this.object);
	}

}
