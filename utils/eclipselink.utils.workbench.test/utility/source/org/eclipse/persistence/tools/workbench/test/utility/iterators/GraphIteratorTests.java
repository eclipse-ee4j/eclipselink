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
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.GraphIterator;


public class GraphIteratorTests extends TestCase
{/** this will be populated with all the nodes created for the test */
	Collection nodes = new ArrayList();
	
	public static Test suite() {
		return new TestSuite(GraphIteratorTests.class);
	}
	
	public GraphIteratorTests(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testHasNext1() {
		this.verifyHasNext(this.buildGraphIterator1());
	}
	
	public void testHasNext2() {
		this.verifyHasNext(this.buildGraphIterator2());
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
		this.verifyNext(this.buildGraphIterator1());
	}
	
	public void testNext2() {
		this.verifyNext(this.buildGraphIterator2());
	}
	
	private void verifyNext(Iterator iterator) {
		while (iterator.hasNext()) {
			assertTrue("bogus element", this.nodes.contains(iterator.next()));
		}
	}
	
	public void testNoSuchElementException1() {
		this.verifyNoSuchElementException(this.buildGraphIterator1());
	}
	
	public void testNoSuchElementException2() {
		this.verifyNoSuchElementException(this.buildGraphIterator2());
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
	
	public void testSize1() {
		this.verifySize(this.buildGraphIterator1());
	}
	
	public void testSize2() {
		this.verifySize(this.buildGraphIterator2());
	}
	
	private void verifySize(Iterator iterator) {
		int iteratorSize = CollectionTools.size(iterator);
		int actualSize = this.nodes.size();
		assertTrue("Too few items in iterator.", iteratorSize >= actualSize);
		assertTrue("Too many items in iterator.", iteratorSize <= actualSize);
	}

	/**
	 * build a graph iterator with an explicit misterRogers
	 */
	private Iterator buildGraphIterator1() {
		return new GraphIterator(this.buildGraph(), this.buildMisterRogers());
	}
	
	private GraphIterator.MisterRogers buildMisterRogers() {
		return new GraphIterator.MisterRogers() {
			public Iterator neighbors(Object next) {
				return ((GraphNode) next).neighbors();
			}
		};
	}

	/**
	 * build a graph iterator with an override
	 */
	private Iterator buildGraphIterator2() {
		return new GraphIterator(this.buildGraph()) {
			public Iterator neighbors(Object next) {
				return ((GraphNode) next).neighbors();
			}
		};
	}

	private Object buildGraph() {
		GraphNode ncNode = new GraphNode("North Carolina");
		GraphNode vaNode = new GraphNode("Virginia");
		GraphNode scNode = new GraphNode("South Carolina");
		GraphNode gaNode = new GraphNode("Georgia");
		GraphNode flNode = new GraphNode("Florida");
		GraphNode alNode = new GraphNode("Alabama");
		GraphNode msNode = new GraphNode("Mississippi");
		GraphNode tnNode = new GraphNode("Tennessee");
		
		ncNode.setNeighbors(new Object[] {vaNode, scNode, gaNode, tnNode});
		vaNode.setNeighbors(new Object[] {ncNode, tnNode});
		scNode.setNeighbors(new Object[] {ncNode, gaNode});
		gaNode.setNeighbors(new Object[] {ncNode, scNode, flNode, alNode, tnNode});
		flNode.setNeighbors(new Object[] {gaNode});
		alNode.setNeighbors(new Object[] {gaNode, msNode, tnNode});
		msNode.setNeighbors(new Object[] {alNode, tnNode});
		tnNode.setNeighbors(new Object[] {vaNode, ncNode, gaNode, alNode, msNode});
		
		return ncNode;
	}	

	private class GraphNode {	
		private String name;
		
		private Collection neighbors = new ArrayList();
		
		public GraphNode(String name) {
			super();
			GraphIteratorTests.this.nodes.add(this);	// log node
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		void setNeighbors(Object[] neighbors) {
			this.neighbors = CollectionTools.list(neighbors);
		}
		
		public Iterator neighbors() {
			return this.neighbors.iterator();
		}
		
		public int neighborsSize() {
			return this.neighbors.size();
		}
		
		public String toString() {
			return "GraphNode(" + this.name + ")";
		}
	}
}

