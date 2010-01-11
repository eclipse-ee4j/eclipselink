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
package org.eclipse.persistence.tools.workbench.test.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.ReverseComparator;

public class ReverseComparatorTests extends TestCase {
	private Comparator naturalReverseComparator;
	private Comparator customComparator;
	private Comparator customReverseComparator;

	public static Test suite() {
		return new TestSuite(ReverseComparatorTests.class);
	}

	public ReverseComparatorTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.naturalReverseComparator = new ReverseComparator();
		this.customComparator = this.buildCustomComparator();
		this.customReverseComparator = new ReverseComparator(this.customComparator);
	}

	private Comparator buildCustomComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				String s1 = (String) o1;
				String s2 = (String) o2;
				String lower1 = s1.toLowerCase();
				String lower2 = s2.toLowerCase();
				int result = lower1.compareTo(lower2);
				if (result == 0) {
					return s1.compareTo(s2);	// use case to differentiate "equal" strings
				}
				return result;
			}
		};
	}

	private List buildUnsortedList() {
		List result = new ArrayList();
		result.add("T");
		result.add("Z");
		result.add("Y");
		result.add("M");
		result.add("m");
		result.add("a");
		result.add("B");
		result.add("b");
		result.add("A");
		return result;
	}

	private List buildNaturallySortedList() {
		List result = new ArrayList(this.buildUnsortedList());
		Collections.sort(result);
		return result;
	}

	private List buildCustomSortedList() {
		List result = new ArrayList(this.buildUnsortedList());
		Collections.sort(result, this.customComparator);
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testNatural() {
		List list = this.buildUnsortedList();
		Collections.sort(list, this.naturalReverseComparator);
		this.verifyList(this.buildNaturallySortedList(), list);
	}

	public void testCustom() {
		List list = this.buildUnsortedList();
		Collections.sort(list, this.customReverseComparator);
		this.verifyList(this.buildCustomSortedList(), list);
	}

	private void verifyList(List normal, List reverse) {
		int size = normal.size();
		int max = size - 1;
		for (int i = 0; i < size; i++) {
			assertEquals(normal.get(i), reverse.get(max - i));
		}
	}
}
