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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.ChainIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TreeIterator;


public class TreeAspectAdapterTests extends TestCase {
	private TestSubject subject1;
	private PropertyValueModel subjectHolder1;
	private TreeAspectAdapter aa1;
	private TreeChangeEvent event1;
	private TreeChangeListener listener1;

	private TestSubject subject2;

	public static Test suite() {
		return new TestSuite(TreeAspectAdapterTests.class);
	}
	
	public TreeAspectAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.subject1 = new TestSubject();
		TestNode root, node;

		root = this.subject1.getRootNameNode();
		node = this.subject1.addName(root, "name 1.1");
		this.subject1.addName(node, "name 1.1.1");
		this.subject1.addName(node, "name 1.1.2");
		node = this.subject1.addName(root, "name 1.2");
		this.subject1.addName(node, "name 1.2.1");
		node = this.subject1.addName(root, "name 1.3");

		root = this.subject1.getRootDescriptionNode();
		node = this.subject1.addDescription(root, "description 1.1");
		this.subject1.addDescription(node, "description 1.1.1");
		this.subject1.addDescription(node, "description 1.1.2");
		node = this.subject1.addDescription(root, "description 1.2");
		this.subject1.addDescription(node, "description 1.2.1");
		node = this.subject1.addDescription(root, "description 1.3");

		this.subjectHolder1 = new SimplePropertyValueModel(this.subject1);
		this.aa1 = this.buildAspectAdapter(this.subjectHolder1);
		this.listener1 = this.buildValueChangeListener1();
		this.aa1.addTreeChangeListener(ValueModel.VALUE, this.listener1);
		this.event1 = null;

		this.subject2 = new TestSubject();

		root = this.subject2.getRootNameNode();
		node = this.subject2.addName(root, "name 2.1");
		this.subject2.addName(node, "name 2.1.1");
		this.subject2.addName(node, "name 2.1.2");
		node = this.subject2.addName(root, "name 2.2");
		this.subject2.addName(node, "name 2.2.1");
		node = this.subject2.addName(root, "name 2.3");

		root = this.subject2.getRootDescriptionNode();
		node = this.subject2.addDescription(root, "description 2.1");
		this.subject2.addDescription(node, "description 2.1.1");
		this.subject2.addDescription(node, "description 2.1.2");
		node = this.subject2.addDescription(root, "description 2.2");
		this.subject2.addDescription(node, "description 2.2.1");
		node = this.subject2.addDescription(root, "description 2.3");
	}

	private TreeAspectAdapter buildAspectAdapter(ValueModel subjectHolder) {
		return new TreeAspectAdapter(subjectHolder, TestSubject.NAMES_TREE) {
			// this is not a typical aspect adapter - the value is determined by the aspect name
			protected Iterator getValueFromSubject() {
				if (this.treeName == TestSubject.NAMES_TREE) {
					return ((TestSubject) this.subject).namePaths();
				} else if (this.treeName == TestSubject.DESCRIPTIONS_TREE) {
					return ((TestSubject) this.subject).descriptionPaths();
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.treeName);
				}
			}
			public void addNode(Object[] parentPath, Object node) {
				TestNode parent = (TestNode) parentPath[parentPath.length - 1];
				if (this.treeName == TestSubject.NAMES_TREE) {
					((TestSubject) this.subject).addName(parent, (String) node);
				} else if (this.treeName == TestSubject.DESCRIPTIONS_TREE) {
					((TestSubject) this.subject).addDescription(parent, (String) node);
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.treeName);
				}
			}
			public void removeNode(Object[] path) {
				TestNode node = (TestNode) path[path.length - 1];
				if (this.treeName == TestSubject.NAMES_TREE) {
					((TestSubject) this.subject).removeNameNode(node);
				} else if (this.treeName == TestSubject.DESCRIPTIONS_TREE) {
					((TestSubject) this.subject).removeDescriptionNode(node);
				} else {
					throw new IllegalStateException("invalid aspect name: " + this.treeName);
				}
			}
		};
	}

	private TreeChangeListener buildValueChangeListener1() {
		return new TreeChangeListener() {
			public void nodeAdded(TreeChangeEvent e) {
				TreeAspectAdapterTests.this.value1Changed(e);
			}
			public void nodeRemoved(TreeChangeEvent e) {
				TreeAspectAdapterTests.this.value1Changed(e);
			}
			public void treeChanged(TreeChangeEvent e) {
				TreeAspectAdapterTests.this.value1Changed(e);
			}
		};
	}

	void value1Changed(TreeChangeEvent e) {
		this.event1 = e;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSubjectHolder() {
		assertNull(this.event1);

		this.subjectHolder1.setValue(this.subject2);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getTreeName());
		assertNull(this.event1.getPath());
		
		this.event1 = null;
		this.subjectHolder1.setValue(null);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getTreeName());
		assertNull(this.event1.getPath());
		
		this.event1 = null;
		this.subjectHolder1.setValue(this.subject1);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getTreeName());
		assertNull(this.event1.getPath());
	}

	public void testTreeStructureChange() {
		assertNull(this.event1);

		this.subject1.addTwoNames(this.subject1.getRootNameNode(), "jam", "jaz");
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getTreeName());
		Object[] path = this.event1.getPath();
		assertEquals(this.subject1.getRootNameNode(), path[path.length - 1]);
		assertTrue(this.subject1.containsNameNode("jam"));
		assertTrue(this.subject1.containsNameNode("jaz"));
	}

	public void testAddNode() {
		assertNull(this.event1);

		TestNode node = this.subject1.nameNode("name 1.1.2");
		this.subject1.addName(node, "jam");
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getTreeName());
		Object[] path = this.event1.getPath();
		assertEquals("jam", ((TestNode) path[path.length - 1]).getText());

		this.event1 = null;
		this.aa1.addNode(node.path(), "jaz");
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getTreeName());
		path = this.event1.getPath();
		assertEquals("jaz", ((TestNode) path[path.length - 1]).getText());
	}

	public void testRemoveNode() {
		assertNull(this.event1);

		TestNode node = this.subject1.nameNode("name 1.1.2");
		this.subject1.removeNameNode(node);
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getTreeName());
		Object[] path = this.event1.getPath();
		assertEquals("name 1.1.2", ((TestNode) path[path.length - 1]).getText());

		this.event1 = null;
		node = this.subject1.nameNode("name 1.3");
		this.aa1.removeNode(node.path());
		assertNotNull(this.event1);
		assertEquals(this.aa1, this.event1.getSource());
		assertEquals(ValueModel.VALUE, this.event1.getTreeName());
		path = this.event1.getPath();
		assertEquals("name 1.3", ((TestNode) path[path.length - 1]).getText());
	}

	public void testGetValue() {
		assertEquals(this.convertToNames(this.subject1.namePaths()), this.convertToNames((Iterator) this.aa1.getValue()));
	}

	private Collection convertToNames(Iterator namePaths) {
		Collection result = new HashBag();
		while (namePaths.hasNext()) {
			Object[] path = (Object[]) namePaths.next();
			StringBuffer sb = new StringBuffer();
			sb.append('[');
			for (int i = 0; i < path.length; i++) {
				sb.append(((TestNode) path[i]).getText());
				if (i < path.length - 1) {
					sb.append(':');
				}
			}
			sb.append(']');
			result.add(sb.toString());
		}
		return result;
	}

	public void testHasListeners() {
		assertTrue(this.aa1.hasAnyTreeChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyTreeChangeListeners(TestSubject.NAMES_TREE));
		this.aa1.removeTreeChangeListener(ValueModel.VALUE, this.listener1);
		assertFalse(this.subject1.hasAnyTreeChangeListeners(TestSubject.NAMES_TREE));
		assertFalse(this.aa1.hasAnyTreeChangeListeners(ValueModel.VALUE));

		TreeChangeListener listener2 = this.buildValueChangeListener1();
		this.aa1.addTreeChangeListener(listener2);
		assertTrue(this.aa1.hasAnyTreeChangeListeners(ValueModel.VALUE));
		assertTrue(this.subject1.hasAnyTreeChangeListeners(TestSubject.NAMES_TREE));
		this.aa1.removeTreeChangeListener(listener2);
		assertFalse(this.subject1.hasAnyTreeChangeListeners(TestSubject.NAMES_TREE));
		assertFalse(this.aa1.hasAnyTreeChangeListeners(ValueModel.VALUE));
	}

	// ********** inner classes **********
	
	private class TestSubject extends AbstractModel {
		private TestNode rootNameNode;
		public static final String NAMES_TREE = "names";
		private TestNode rootDescriptionNode;
		public static final String DESCRIPTIONS_TREE = "descriptions";
	
		public TestSubject() {
			this.rootNameNode = new TestNode("root name");
			this.rootDescriptionNode = new TestNode("root description");
		}
		public TestNode getRootNameNode() {
			return this.rootNameNode;
		}
		public Iterator nameNodes() {
			return new TreeIterator(this.rootNameNode) {
				public Iterator children(Object next) {
					return ((TestNode) next).children();
				}
			};
		}
		public Iterator namePaths() {
			return new TransformationIterator(this.nameNodes()) {
				protected Object transform(Object next) {
					return ((TestNode) next).path();
				}
			};
		}
		public TestNode addName(TestNode parent, String name) {
			TestNode child = new TestNode(name);
			parent.addChild(child);
			this.fireNodeAdded(NAMES_TREE, child.path());
			return child;
		}
		public void addTwoNames(TestNode parent, String name1, String name2) {
			parent.addChild(new TestNode(name1));
			parent.addChild(new TestNode(name2));
			this.fireTreeStructureChanged(NAMES_TREE, parent.path());
		}
		public void removeNameNode(TestNode nameNode) {
			nameNode.getParent().removeChild(nameNode);
			this.fireNodeRemoved(NAMES_TREE, nameNode.path());
		}
		public boolean containsNameNode(String name) {
			return this.nameNode(name) != null;
		}
		public TestNode nameNode(String name) {
			for (Iterator stream = this.nameNodes(); stream.hasNext(); ) {
				TestNode node = (TestNode) stream.next();
				if (node.getText().equals(name)) {
					return node;
				}
			}
			return null;
		}
		public TestNode getRootDescriptionNode() {
			return this.rootDescriptionNode;
		}
		public Iterator descriptionNodes() {
			return new TreeIterator(this.rootDescriptionNode) {
				public Iterator children(Object next) {
					return ((TestNode) next).children();
				}
			};
		}
		public Iterator descriptionPaths() {
			return new TransformationIterator(this.descriptionNodes()) {
				protected Object transform(Object next) {
					return ((TestNode) next).path();
				}
			};
		}
		public TestNode addDescription(TestNode parent, String description) {
			TestNode child = new TestNode(description);
			parent.addChild(child);
			this.fireNodeAdded(DESCRIPTIONS_TREE, child.path());
			return child;
		}
		public void removeDescriptionNode(TestNode descriptionNode) {
			descriptionNode.getParent().removeChild(descriptionNode);
			this.fireNodeRemoved(DESCRIPTIONS_TREE, descriptionNode.path());
		}
		public boolean containsDescriptionNode(String name) {
			for (Iterator stream = this.descriptionNodes(); stream.hasNext(); ) {
				TestNode node = (TestNode) stream.next();
				if (node.getText().equals(name)) {
					return true;
				}
			}
			return false;
		}
	}
	
	private class TestNode {
		private String text;
		private TestNode parent;
		private Collection children;
	
		public TestNode(String text) {
			this.text = text;
			this.children = new HashBag();
		}
		public String getText() {
			return this.text;
		}
		public TestNode getParent() {
			return this.parent;
		}
		private void setParent(TestNode parent) {
			this.parent = parent;
		}
		public Iterator children() {
			return new ReadOnlyIterator(this.children);
		}
		public void addChild(TestNode child) {
			this.children.add(child);
			child.setParent(this);
		}
		public void removeChild(TestNode child) {
			this.children.remove(child);
		}
		public Object[] path() {
			return CollectionTools.reverseList(this.buildAntiPath()).toArray();
		}
		private Iterator buildAntiPath() {
			return new ChainIterator(this) {
				protected Object nextLink(Object currentLink) {
					return ((TestNode) currentLink).getParent();
				}
			};
		}
		public String toString() {
			return "TestNode(" + this.text + ")";
		}
	}
	
}
