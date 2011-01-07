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
package org.eclipse.persistence.tools.workbench.platformsmodel;

import org.w3c.dom.Node;

/**
 * Map a JDBC type to the appropriate Java type declaration.
 * These are used to generate classes from tables.
 * The JDBC type cannot be changed.
 */
public final class JDBCTypeToJavaTypeDeclarationMapping
	extends AbstractJDBCTypeToJavaTypeDeclarationMapping
{
	// the JDBC type can never be replaced once the mapping is built
	public static final String JAVA_TYPE_DECLARATION_PROPERTY = "javaTypeDeclaration";


	// ********** constructors **********

	/**
	 * this constructor is called when the mapping is read from an XML file
	 */
	JDBCTypeToJavaTypeDeclarationMapping(JDBCTypeRepository repository, Node node) throws CorruptXMLException {
		super(repository, node);
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new mapping (which shouldn't happen very often,
	 * since all the typical mappings have already been built...)
	 */
	JDBCTypeToJavaTypeDeclarationMapping(JDBCTypeRepository repository, JDBCType jdbcType, String javaClassName, int arrayDepth) {
		super(repository, jdbcType, javaClassName, arrayDepth);
	}


	// ********** accessors **********

	/**
	 * Set the Java type declaration corresponding to the JDBC type.
	 */
	public void setJavaTypeDeclaration(String javaClassName, int arrayDepth) {
		Object old = this.javaTypeDeclaration;
		this.javaTypeDeclaration = new JavaTypeDeclaration(this, javaClassName, arrayDepth);
		this.firePropertyChanged(JAVA_TYPE_DECLARATION_PROPERTY, old, this.javaTypeDeclaration);
	}


	// ********** queries **********

	boolean maps(int jdbcTypeCode) {
		return this.jdbcType.getCode() == jdbcTypeCode;
	}

	boolean maps(JDBCType type) {
		return this.jdbcType == type;
	}


	// ********** behavior **********

	/**
	 * sort by JDBC type, there should be no duplicates
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		JDBCTypeToJavaTypeDeclarationMapping other = (JDBCTypeToJavaTypeDeclarationMapping) o;
		return this.jdbcType.compareTo(other.jdbcType);
	}


	// ********** i/o **********

	void write(Node node) {
		this.writeJDBCType(node);
		this.writeJavaTypeDeclaration(node);
		this.writeComment(node);
	}


	// ********** printing and displaying **********

	/**
	 * @see AbstractJDBCTypeToJavaTypeDeclarationMapping#displayStringOn(StringBuffer)
	 */
	public void displayStringOn(StringBuffer sb) {
		sb.append(this.jdbcType.getName());
		sb.append(" => ");
		this.javaTypeDeclaration.displayStringOn(sb);
	}

}
