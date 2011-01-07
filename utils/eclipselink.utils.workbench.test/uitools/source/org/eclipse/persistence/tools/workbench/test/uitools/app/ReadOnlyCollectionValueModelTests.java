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

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;


public class ReadOnlyCollectionValueModelTests extends TestCase {
	private CollectionValueModel collectionHolder;
	private static Collection collection;

	public static Test suite() {
		return new TestSuite(ReadOnlyCollectionValueModelTests.class);
	}
	
	public ReadOnlyCollectionValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.collectionHolder = this.buildCollectionHolder();
	}

	private CollectionValueModel buildCollectionHolder() {
		return new AbstractReadOnlyCollectionValueModel() {
			public Object getValue() {
				return ReadOnlyCollectionValueModelTests.collection();
			}
		};
	}

	static Iterator collection() {
		return getCollection().iterator();
	}

	private static Collection getCollection() {
		if (collection == null) {
			collection = buildCollection();
		}
		return collection;
	}

	private static Collection buildCollection() {
		Collection result = new HashBag();
		result.add("foo");
		result.add("bar");
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		assertEquals(buildCollection(), CollectionTools.bag((Iterator) this.collectionHolder.getValue()));
	}

	public void testSize() {
		assertEquals(buildCollection().size(), this.collectionHolder.size());
	}

}
