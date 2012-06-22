/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.utility.classfile.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.classfile.ClassFile;


/**
 * This class can be used to build a dependency graph
 * for all the classes in the specified classpath entry
 * (either a directory or a JAR)
 * or the specified collection of class files.
 * 
 * @see ClassFile
 */
public class ClassDependencyGraph {

	/**
	 * nodes for the class files found in the specified
	 * classpath entry or collection of class files;
	 * this will not hold ALL the nodes, since some nodes
	 * will be for classes not found in the original collection
	 * of class files
	 */
	private Collection nodes;

	/**
	 * ALL the nodes created, keyed by class name
	 */
	private Map allNodes;


	// ********** constructors **********

	public ClassDependencyGraph(String classpathEntry) {
		this(new ClassFileIterator(classpathEntry));
	}

	public ClassDependencyGraph(Iterator classFiles) {
		super();
		this.initialize(classFiles);
	}


	// ********** initialization **********

	private void initialize(Iterator classFiles) {
		Collection tempNames = new ArrayList(500);
		this.allNodes = new HashMap(500);

		while (classFiles.hasNext()) {
			ClassFile classFile = (ClassFile) classFiles.next();
			tempNames.add(classFile.className());
			this.addClassDependencyNode(classFile);
		}

		// directly hold *only* the classes found in the classpath entry;
		// there will be other nodes, but they will only be held by these nodes
		this.nodes = new ArrayList(500);
		for (Iterator stream = tempNames.iterator(); stream.hasNext();) {
			this.nodes.add(this.allNodes.get(stream.next()));
		}

		// now, set the backpointers for the main nodes
		for (Iterator stream = this.nodes.iterator(); stream.hasNext();) {
			((Node) stream.next()).addReferencingNodesFrom(this.nodes);
		}
	}

	private void addClassDependencyNode(ClassFile classFile) {
		String className = classFile.className();
		Node node = (Node) this.allNodes.get(className);
		if (node == null) {
			node = new Node(className);
			this.allNodes.put(className, node);
		}
		node.initialize(classFile, this.allNodes);
	}


	// ********** queries **********

	public Iterator nodes() {
		return this.nodes.iterator();
	}

	public String toString() {
		return ClassTools.shortClassNameForObject(this) + '(' + this.nodes.size() + '/' + this.allNodes.size() + " nodes)";
	}


	// ********** static methods **********

//	public static void main(String[] args) throws Exception {
//		String classpathEntry = null;
//		if (args == null || args.length == 0) {
//			classpathEntry = toplinkJarFileName();
//		} else {
//			classpathEntry = args[0];
//		}
//
//		long startTime = (new Date()).getTime();
//		ClassDependencyGraph tree = new ClassDependencyGraph(classpathEntry);
//		long endTime = (new Date()).getTime();
//		float elapsedSeconds = (endTime - startTime) / 1000;
//		System.out.println(tree.toString() + " (creation time: " + elapsedSeconds + " seconds)");
//	}
//
//	private static String toplinkJarFileName() {
//		return ClasspathTools.entryForFileName("toplink_g.jar", ClasspathTools.javaClasspath());
//	}
//

// ********** member classes **********

	/**
	 * a node in the class dependency graph;
	 * it holds a pair of collections:
	 * - the nodes that this node references
	 * - the nodes that reference this node
	 */
	public static class Node {
		/** this node's class */
		private String className;
	
		/** classes referenced by this node's class */
		private Map referencedNodes;
	
		/** classes that reference this node's class */
		private Map referencingNodes;
	
		Node(String className) {
			super();
			this.className = className;
			this.referencedNodes = new IdentityHashMap();
			this.referencingNodes = new IdentityHashMap();
		}
	
		/**
		 * build referencedNodes from the class file;
		 * tempNodes maps class names to their nodes
		 */
		void initialize(ClassFile classFile, Map allNodes) {
			// this method should only be called once...
			if ( ! this.referencedNodes.isEmpty()) {
				throw new IllegalStateException();
			}
	
			String[] names = classFile.referencedClassNames();
			for (int i = names.length; i-- > 0;) {
				String name = names[i];
				Node node = (Node) allNodes.get(name);
				if (node == null) {
					node = new Node(name);
					allNodes.put(name, node);
				}
				this.referencedNodes.put(node, node);
			}
		}
	
		/**
		 * add to referencingNodes any of the specified
		 * nodes that actually reference this node
		 */
		void addReferencingNodesFrom(Collection nodes) {
			for (Iterator stream = nodes.iterator(); stream.hasNext();) {
				Node node = (Node) stream.next();
				node.addReferencingNodeTo(this);
			}
		}
	
		/**
		 * if this node references the other node, add this
		 * node to the other node's referencing nodes
		 */
		private void addReferencingNodeTo(Node node) {
			if (this.referencedNodes.containsKey(node)) {
				node.addReferencingNode(this);
			}
		}
	
		void addReferencingNode(Node node) {
			this.referencingNodes.put(node, node);
		}
	
		public String getClassName() {
			return this.className;
		}
	
		public Iterator referencedNodes() {
			return this.referencedNodes.keySet().iterator();
		}
	
		public Iterator referencingNodes() {
			return this.referencingNodes.keySet().iterator();
		}
	
		public String toString() {
			return ClassTools.toStringClassNameForObject(this) + "(" + this.className + ")";
		}
	
	}

}
