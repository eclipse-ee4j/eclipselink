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

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.w3c.dom.Node;


/**
 * Common behavior(?).
 * Neither instance variable should ever be null.
 */
abstract class AbstractJDBCTypeToJavaTypeDeclarationMapping
	extends AbstractNodeModel
{
	JDBCType jdbcType;
	JavaTypeDeclaration javaTypeDeclaration;


	// ********** constructors **********

	/**
	 * this constructor is called when the mapping is read from an XML file
	 */
	AbstractJDBCTypeToJavaTypeDeclarationMapping(JDBCTypeRepository repository, Node node) throws CorruptXMLException {
		super(repository);
		this.read(node);
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new mapping (which shouldn't happen very often,
	 * since all the typical mappings have already been built...)
	 */
	AbstractJDBCTypeToJavaTypeDeclarationMapping(JDBCTypeRepository repository, JDBCType jdbcType, String javaClassName, int arrayDepth) {
		super(repository);
		if (jdbcType == null) {
			throw new NullPointerException();
		}
		this.jdbcType = jdbcType;
		this.javaTypeDeclaration = new JavaTypeDeclaration(this, javaClassName, arrayDepth);
	}


	// ********** accessors **********

	private JDBCTypeRepository getJDBCTypeRepository() {
		return  (JDBCTypeRepository) this.getParent();
	}

	public JDBCType getJDBCType() {
		return this.jdbcType;
	}

	public JavaTypeDeclaration getJavaTypeDeclaration() {
		return this.javaTypeDeclaration;
	}


	// ********** queries **********

	private JDBCType jdbcTypeNamed(String jdbcTypeName) {
		return this.getJDBCTypeRepository().jdbcTypeNamed(jdbcTypeName);
	}


	// ********** i/o **********

	private void read(Node node) throws CorruptXMLException {
		try {
			this.jdbcType = this.jdbcTypeNamed(XMLTools.childTextContent(node, "jdbc-type", null));
		} catch (IllegalArgumentException ex) {
			throw new CorruptXMLException(ex);
		}
		this.javaTypeDeclaration = new JavaTypeDeclaration(this, XMLTools.child(node, "java-type-declaration"));
		ClassTools.setFieldValue(this, "comment", XMLTools.childTextContent(node, "comment", ""));
	}

	/**
	 * Subclasses decide the order the child nodes are written.
	 */
	abstract void write(Node node);

	void writeJDBCType(Node node) {
		XMLTools.addSimpleTextNode(node, "jdbc-type", this.jdbcType.getName());
	}

	void writeJavaTypeDeclaration(Node node) {
		this.javaTypeDeclaration.write(node.appendChild(node.getOwnerDocument().createElement("java-type-declaration")));
	}

	void writeComment(Node node) {
		XMLTools.addSimpleTextNode(node, "comment", (String) ClassTools.getFieldValue(this, "comment"), "");
	}


	// ********** printing and displaying **********

	public abstract void displayStringOn(StringBuffer sb);

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.Node#displayString()
	 */
	public String displayString() {
		StringBuffer sb = new StringBuffer();
		this.displayStringOn(sb);
		return sb.toString();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(java.lang.StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		this.displayStringOn(sb);
	}

}
