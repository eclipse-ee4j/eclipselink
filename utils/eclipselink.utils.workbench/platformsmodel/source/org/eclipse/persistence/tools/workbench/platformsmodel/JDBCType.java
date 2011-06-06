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

import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.w3c.dom.Node;


/**
 * Associate the Java constant and the JDBC type name.
 * These are typically derived from java.sql.Types.
 * 
 * @see java.sql.Types
 */
public final class JDBCType
	extends AbstractNodeModel
{

	/**
	 * a name uniquely identifying the type within a
	 * JDBC type repository
	 */
	private String name;
		public static final String NAME_PROPERTY = "name";

	/**
	 * the JDBC code used by JDBC drivers
	 */
	private int code;
		public static final String CODE_PROPERTY = "code";


	// ********** constructors **********

	/**
	 * this constructor is called when the type is read from an XML file
	 */
	JDBCType(JDBCTypeRepository repository, Node node) throws CorruptXMLException {
		super(repository);
		this.read(node);
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new type (which shouldn't happen very often,
	 * since all the typical types have already been built...)
	 */
	JDBCType(JDBCTypeRepository repository, String name, int code) {
		super(repository);
		this.name = name;
		this.code = code;
	}


	// ********** accessors **********

	private JDBCTypeRepository getJDBCTypeRepository() {
		return  (JDBCTypeRepository) this.getParent();
	}


	/**
	 * Return the name of the type, as defined in java.sql.Types.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of the type, as defined in java.sql.Types.
	 * No duplicates allowed.
	 */
	public void setName(String name) {
		this.getJDBCTypeRepository().checkJDBCTypeName(name);
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}


	/**
	 * Return the type code, as defined in java.sql.Types.
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Set the type code, as defined in java.sql.Types.
	 * No duplicates allowed.
	 */
	public void setCode(int code) {
		this.getJDBCTypeRepository().checkJDBCTypeCode(code);
		int old = this.code;
		this.code = code;
		this.firePropertyChanged(CODE_PROPERTY, old, code);
	}


	// ********** i/o **********

	private void read(Node node) throws CorruptXMLException {
		if (node == null) {
			throw new CorruptXMLException("missing node");
		}
		this.name = XMLTools.childTextContent(node, "name", null);
		if ((this.name == null) || (this.name.length() == 0)) {
			throw new CorruptXMLException("name is required");
		}
		// we just picked a random, improbable code for the default
		this.code = XMLTools.childIntContent(node, "code", -7777);
	}

	void write(Node node) {
		XMLTools.addSimpleTextNode(node, "name", this.name);
		// we just picked a random, improbable code for the default
		XMLTools.addSimpleTextNode(node, "code", this.code, -7777);
	}


	// ********** printing and displaying **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.Node#displayString()
	 */
	public String displayString() {
		return this.name;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		sb.append(this.name);
	}

}
