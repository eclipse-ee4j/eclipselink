/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is a non-synchronized, reusable implementation of 
 * StringBuffer.
 * @author mmacivor
 */

public class StrBuffer implements CharSequence {
	private int numChar; //The number of characters currently in this buffer
	private char[] myBuf;
	
	public StrBuffer() {
		this(80);
	}
	
	public StrBuffer(int length) {
		myBuf = new char[length];
		numChar = 0;
	}
	
	/*
	 * Clears the StringBuffer to be reused.
	 */
	public void reset() {
		numChar = 0;
	}
	
	private void increaseCapacity(int minStorage) {
		int newStorage = (myBuf.length * 2) + 5;
		if(newStorage < minStorage) {
			newStorage = minStorage;
		}
		char[] newBuf = new char[newStorage];
		System.arraycopy(this.myBuf, 0, newBuf, 0, this.numChar);
		this.myBuf = newBuf;
	}
	
	public StrBuffer append(String str) {
		int strlen = str.length();
		int newLength = this.numChar + strlen;
		if(newLength > this.myBuf.length) {
			increaseCapacity(newLength);
		}
		str.getChars(0, strlen, this.myBuf, this.numChar);
		this.numChar = newLength;
		return this;
	}
	
	public StrBuffer append(char[] chars, int start, int length) {
		int newLength = this.numChar + length;
		if(newLength > this.myBuf.length) {
			increaseCapacity(newLength);
		}
		System.arraycopy(chars, start, this.myBuf, numChar, length);
		this.numChar = newLength;
		return this;
	}
	public int length() {
		return numChar;
	}
	
	public String toString() {
		return new String(this.myBuf, 0, this.numChar);
	}

    public char charAt(int index) {
        return myBuf[index];
    }

    public CharSequence subSequence(int start, int end) {
        return new String(this.myBuf, start, end);
    }

}