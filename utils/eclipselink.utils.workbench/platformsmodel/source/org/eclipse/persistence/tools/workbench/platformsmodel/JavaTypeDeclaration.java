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
package org.eclipse.persistence.tools.workbench.platformsmodel;

import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.w3c.dom.Node;


/**
 * This class describes a Java type declaration; i.e. a type declaration's
 * Java class and its array depth. The Java class is referenced by name,
 * allowing us to reference classes that are not (or cannot be) loaded.
 * 
 * This class's state is immutable - once an instance is built, its state
 * cannot be changed. If you want a different declaration, you will need
 * to build another instance, which isn't all that painful.
 */
public final class JavaTypeDeclaration
	extends AbstractNodeModel
{

	/**
	 * store the class as a name, so we can reference classes
	 * that are not loaded
	 */
	private String javaClassName;

	/**
	 * non-array classes have an array depth of zero
	 */
	private int arrayDepth;


	// ********** constructors **********

	/**
	 * this constructor is called when the type declaration is read from an XML file
	 */
	JavaTypeDeclaration(AbstractNodeModel parent, Node node) throws CorruptXMLException {
		super(parent);
		this.read(node);
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new type declaration
	 */
	JavaTypeDeclaration(AbstractNodeModel parent, String javaClassName, int arrayDepth) {
		super(parent);
		this.javaClassName = javaClassName;
		this.arrayDepth = arrayDepth;
		this.checkState();
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new type declaration with an array depth of zero
	 */
	JavaTypeDeclaration(AbstractNodeModel parent, String javaClassName) {
		this(parent, javaClassName, 0);
	}


	// ********** accessors **********

	/**
	 * Return the "element" name of the Java type declaration.
	 */
	public String getJavaClassName() {
		return this.javaClassName;
	}

	/**
	 * Return the "array depth" of the Java type declaration.
	 */
	public int getArrayDepth() {
		return this.arrayDepth;
	}


	// ********** queries **********

	/**
	 * there can be multiple instances of the same Java type
	 * declaration, so provide some way of comparing them
	 */
	boolean equals(String otherJavaClassName, int otherArrayDepth) {
		return this.javaClassName.equals(otherJavaClassName)
			&& this.arrayDepth == otherArrayDepth;
	}

	/**
	 * there can be multiple instances of the same Java type
	 * declaration, so provide some way of comparing them
	 */
	boolean equals(JavaTypeDeclaration other) {
		return this.equals(other.javaClassName, other.arrayDepth);
	}


	// ********** behavior **********

	private void checkState() {
		if ((this.javaClassName == null) || (this.javaClassName.length() == 0)) {
			throw new IllegalStateException("Java class name is required");
		}

		if (this.arrayDepth < 0) {
			throw new IllegalStateException("array depth must be greater than or equal to zero: " + this.arrayDepth);
		}
		if (this.javaClassName.equals(void.class.getName()) && (this.arrayDepth != 0)) {
			throw new IllegalStateException("'void' must have an array depth of zero: " + this.arrayDepth);
		}
	}


	// ********** i/o **********

	private void read(Node node) throws CorruptXMLException {
		if (node == null) {
			throw new CorruptXMLException("missing node");
		}
		this.javaClassName = XMLTools.childTextContent(node, "java-class-name", null);
		this.arrayDepth = XMLTools.childIntContent(node, "array-depth", 0);
		try {
			this.checkState();
		} catch (IllegalStateException ex) {
			throw new CorruptXMLException("illegal state: " + this, ex);
		}
	}

	void write(Node node) {
		XMLTools.addSimpleTextNode(node, "java-class-name", this.javaClassName);
		XMLTools.addSimpleTextNode(node, "array-depth", this.arrayDepth, 0);
	}


	// ********** printing and displaying **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.Node#displayString()
	 */
	public String displayString() {
		StringBuffer sb = new StringBuffer();
		this.displayStringOn(sb);
		return sb.toString();
	}

	public void displayStringOn(StringBuffer sb) {
		sb.append(this.javaClassName);
		for (int i = this.arrayDepth; i-- > 0; ) {
			sb.append("[]");
		}
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		this.displayStringOn(sb);
	}

}
