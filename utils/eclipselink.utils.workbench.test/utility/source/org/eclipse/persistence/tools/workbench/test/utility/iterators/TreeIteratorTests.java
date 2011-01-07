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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TreeIterator;


public class TreeIteratorTests extends TestCase {
	/** this will be populated with all the nodes created for the test */
	Collection nodes = new ArrayList();

	public static Test suite() {
		return new TestSuite(TreeIteratorTests.class);
	}
	
	public TreeIteratorTests(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testHasNext1() {
		this.verifyHasNext(this.buildTreeIterator1());
	}
	
	public void testHasNext2() {
		this.verifyHasNext(this.buildTreeIterator2());
	}
	
	private void verifyHasNext(Iterator iterator) {
		int i = 0;
		while (iterator.hasNext()) {
			iterator.next();
			i++;
		}
		assertEquals(this.nodes.size(), i);
	}
	
	public void testNext1() {
		this.verifyNext(this.buildTreeIterator1());
	}
	
	public void testNext2() {
		this.verifyNext(this.buildTreeIterator2());
	}
	
	private void verifyNext(Iterator iterator) {
		while (iterator.hasNext()) {
			assertTrue("bogus element", this.nodes.contains(iterator.next()));
		}
	}
	
	public void testNoSuchElementException1() {
		this.verifyNoSuchElementException(this.buildTreeIterator1());
	}
	
	public void testNoSuchElementException2() {
		this.verifyNoSuchElementException(this.buildTreeIterator2());
	}
	
	private void verifyNoSuchElementException(Iterator iterator) {
		boolean exCaught = false;
		while (iterator.hasNext()) {
			iterator.next();
		}
		try {
			iterator.next();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown", exCaught);
	}
	
	public void testRemove1() {
		this.verifyRemove(this.buildTreeIterator1());
	}
	
	public void testRemove2() {
		this.verifyRemove(this.buildTreeIterator2());
	}
	
	private void verifyRemove(Iterator iterator) {
		String parentName = "child 2";
		String childName = "grandchild 2A";
		int startSize = this.childrenSize(parentName);
		while (iterator.hasNext()) {
			TreeNode node = (TreeNode) iterator.next();
			if (node.getName().equals(childName)) {
				iterator.remove();
			}
		}
		int endSize = this.childrenSize(parentName);
		assertEquals(startSize - 1, endSize);
	}

	private int childrenSize(String nodeName) {
		for (Iterator stream = this.nodes.iterator(); stream.hasNext(); ) {
			TreeNode node = (TreeNode) stream.next();
			if (node.getName().equals(nodeName)) {
				return node.childrenSize();
			}
		}
		throw new IllegalArgumentException(nodeName);
	}

	/**
	 * build a tree iterator with an explicit midwife
	 */
	private Iterator buildTreeIterator1() {
		return new TreeIterator(this.buildTree(), this.buildMidwife());
	}

	private TreeIterator.Midwife buildMidwife() {
		return new TreeIterator.Midwife() {
			public Iterator children(Object next) {
				return ((TreeNode) next).children();
			}
		};
	}

	/**
	 * build a tree iterator with an override
	 */
	private Iterator buildTreeIterator2() {
		return new TreeIterator(this.buildTree()) {
			public Iterator children(Object next) {
				return ((TreeNode) next).children();
			}
		};
	}

	private Object buildTree() {
		TreeNode root = new TreeNode("root");
			TreeNode child1 = new TreeNode(root, "child 1");
				new TreeNode(child1, "grandchild 1A");
			TreeNode child2 = new TreeNode(root, "child 2");
				new TreeNode(child2, "grandchild 2A");
				TreeNode grandchild2B = new TreeNode(child2, "grandchild 2B");
					new TreeNode(grandchild2B, "great-grandchild 2B1");
					new TreeNode(grandchild2B, "great-grandchild 2B2");
				TreeNode grandchild2C = new TreeNode(child2, "grandchild 2C");
					new TreeNode(grandchild2C, "great-grandchild 2C1");
			new TreeNode(root, "child 3");
		return root;
	}	

	private class TreeNode {
		private String name;
		private Collection children = new ArrayList();
		public TreeNode(String name) {
			super();
			TreeIteratorTests.this.nodes.add(this);	// log node
			this.name = name;
		}
		public TreeNode(TreeNode parent, String name) {
			this(name);
			parent.addChild(this);
		}
		public String getName() {
			return this.name;
		}
		private void addChild(TreeNode child) {
			this.children.add(child);
		}
		public Iterator children() {
			return this.children.iterator();
		}
		public int childrenSize() {
			return this.children.size();
		}
		public String toString() {
			return "TreeNode(" + this.name + ")";
		}
	}

}
