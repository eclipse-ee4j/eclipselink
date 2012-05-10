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
package org.eclipse.persistence.tools.workbench.platformsmodel;

import org.w3c.dom.Node;

/**
 * Map a Java type declaration to the appropriate JDBC type.
 * These are used to generate tables from classes.
 * The Java type declaration cannot be changed.
 */
public final class JavaTypeDeclarationToJDBCTypeMapping
	extends AbstractJDBCTypeToJavaTypeDeclarationMapping
{
	// the Java type declaration can never be replaced once the mapping is built
	public static final String JDBC_TYPE_PROPERTY = "jdbcType";


	// ********** constructors **********

	/**
	 * this constructor is called when the mapping is read from an XML file
	 */
	JavaTypeDeclarationToJDBCTypeMapping(JDBCTypeRepository repository, Node node) throws CorruptXMLException {
		super(repository, node);
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new mapping (which shouldn't happen very often,
	 * since all the typical mappings have already been built...)
	 */
	JavaTypeDeclarationToJDBCTypeMapping(JDBCTypeRepository repository, String javaClassName, int arrayDepth, JDBCType jdbcType) {
		super(repository, jdbcType, javaClassName, arrayDepth);
	}


	// ********** accessors **********

	/**
	 * Set the JDBC type corresponding to the Java type declaration.
	 */
	public void setJDBCType(JDBCType jdbcType) {
		if (jdbcType == null) {
			throw new NullPointerException();
		}
		Object old = this.jdbcType;
		this.jdbcType = jdbcType;
		this.firePropertyChanged(JDBC_TYPE_PROPERTY, old, jdbcType);
	}


	// ********** queries **********

	boolean maps(String javaClassName, int arrayDepth) {
		return this.javaTypeDeclaration.equals(javaClassName, arrayDepth);
	}


	// ********** behavior **********

	/**
	 * sort by Java type declaration, there should be no duplicates
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		JavaTypeDeclarationToJDBCTypeMapping other = (JavaTypeDeclarationToJDBCTypeMapping) o;
		return this.javaTypeDeclaration.compareTo(other.javaTypeDeclaration);
	}


	// ********** i/o **********

	void write(Node node) {
		this.writeJavaTypeDeclaration(node);
		this.writeJDBCType(node);
		this.writeComment(node);
	}


	// ********** printing and displaying **********

	/**
	 * @see AbstractJDBCTypeToJavaTypeDeclarationMapping#displayStringOn(StringBuffer)
	 */
	public void displayStringOn(StringBuffer sb) {
		this.javaTypeDeclaration.displayStringOn(sb);
		sb.append(" => ");
		sb.append(this.jdbcType.getName());
	}

}
