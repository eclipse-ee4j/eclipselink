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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class ReadOnlyListValueModelTests extends TestCase {
	private ListValueModel listHolder;
	private static List list;

	public static Test suite() {
		return new TestSuite(ReadOnlyListValueModelTests.class);
	}
	
	public ReadOnlyListValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.listHolder = this.buildListHolder();
	}

	private ListValueModel buildListHolder() {
		return new AbstractReadOnlyListValueModel() {
			public Object getValue() {
				return ReadOnlyListValueModelTests.list();
			}
		};
	}

	static ListIterator list() {
		return getList().listIterator();
	}

	private static List getList() {
		if (list == null) {
			list = buildList();
		}
		return list;
	}

	private static List buildList() {
		List result = new ArrayList();
		result.add("foo");
		result.add("bar");
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetItem() {
		List expected = buildList();
		for (int i = 0; i < this.listHolder.size(); i++) {
			assertEquals(expected.get(i), this.listHolder.getItem(i));
		}
	}

	public void testGetValue() {
		assertEquals(buildList(), CollectionTools.list((Iterator) this.listHolder.getValue()));
	}

	public void testSize() {
		assertEquals(buildList().size(), this.listHolder.size());
	}

}
