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
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.IteratorEnumeration;

public class IteratorEnumerationTests extends TestCase {

	public static Test suite() {
		return new TestSuite(IteratorEnumerationTests.class);
	}
	
	public IteratorEnumerationTests(String name) {
		super(name);
	}
	
	public void testHasMoreElements() {
		int i = 0;
		for (Enumeration stream = this.buildEnumeration(); stream.hasMoreElements(); ) {
			stream.nextElement();
			i++;
		}
		assertEquals(this.buildVector().size(), i);
	}
	
	public void testNextElement() {
		Iterator iterator = this.buildIterator();
		for (Enumeration stream = this.buildEnumeration(); stream.hasMoreElements(); ) {
			assertEquals("bogus element", iterator.next(), stream.nextElement());
		}
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		Enumeration stream = this.buildEnumeration();
		String string = null;
		while (stream.hasMoreElements()) {
			string = (String) stream.nextElement();
		}
		try {
			string = (String) stream.nextElement();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + string, exCaught);
	}
	
	private Enumeration buildEnumeration() {
		return this.buildEnumeration(this.buildIterator());
	}
	
	private Enumeration buildEnumeration(Iterator iterator) {
		return new IteratorEnumeration(iterator);
	}
	
	private Iterator buildIterator() {
		return this.buildVector().iterator();
	}
	
	private Vector buildVector() {
		Vector v = new Vector();
		v.addElement("one");
		v.addElement("two");
		v.addElement("three");
		v.addElement("four");
		v.addElement("five");
		v.addElement("six");
		v.addElement("seven");
		v.addElement("eight");
		return v;
	}

}
