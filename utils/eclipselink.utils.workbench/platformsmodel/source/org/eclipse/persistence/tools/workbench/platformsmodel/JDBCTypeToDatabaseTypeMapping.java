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

import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.w3c.dom.Node;


/**
 * Map a JDBC type to the appropriate platform-specific database type.
 * Every platform must specify a database type for every possible
 * JDBC type.
 * The JDBC type cannot be changed.
 */
public final class JDBCTypeToDatabaseTypeMapping
	extends AbstractNodeModel
{

	/** The JDBC type is set upon construction and is immutable. */
	private JDBCType jdbcType;

	/** The platform-specific database type corresponding to the JDBC type. */
	private DatabaseType databaseType;
		public static final String DATABASE_TYPE_PROPERTY = "databaseType";


	// ********** constructors **********

	/**
	 * this constructor is called when the mapping is read from an XML file
	 */
	JDBCTypeToDatabaseTypeMapping(DatabasePlatform platform, Node node) throws CorruptXMLException {
		super(platform);
		this.read(node);
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new mapping (which shouldn't happen very often,
	 * since all the typical mappings have already been built...)
	 */
	JDBCTypeToDatabaseTypeMapping(DatabasePlatform platform, JDBCType jdbcType) {
		super(platform);
		if (jdbcType == null) {
			throw new NullPointerException();
		}
		this.jdbcType = jdbcType;
	}


	// ********** accessors **********

	private DatabasePlatform getPlatform() {
		return (DatabasePlatform) this.getParent();
	}

	public JDBCType getJDBCType() {
		return this.jdbcType;
	}

	public DatabaseType getDatabaseType() {
		return this.databaseType;
	}

	public void setDatabaseType(DatabaseType databaseType) {
		Object old = this.databaseType;
		this.databaseType = databaseType;
		this.firePropertyChanged(DATABASE_TYPE_PROPERTY, old, databaseType);
	}


	// ********** queries **********

	boolean maps(JDBCType type) {
		return this.jdbcType == type;
	}

	boolean maps(int jdbcTypeCode) {
		return this.jdbcType.getCode() == jdbcTypeCode;
	}

	private JDBCType jdbcTypeNamed(String jdbcTypeName) {
		return this.getPlatform().jdbcTypeNamed(jdbcTypeName);
	}

	private DatabaseType databaseTypeNamed(String databaseTypeName) {
		return this.getPlatform().databaseTypeNamed(databaseTypeName);
	}


	// ********** behavior **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractNodeModel#addProblemsTo(java.util.Set)
	 */
	protected void addProblemsTo(List currentProblems) {
		if (this.databaseType == null) {
			currentProblems.add(this.buildProblem("002", this.getJDBCType().getName()));
		}
		super.addProblemsTo(currentProblems);
	}

	/**
	 * copy all the settings from the original platform
	 * to this, newly-created, platform
	 */
	void cloneFrom(JDBCTypeToDatabaseTypeMapping originalMapping) {
		// the jdbcType has been set by the time we get here
		DatabaseType originalDatabaseType = originalMapping.getDatabaseType();
		if (originalDatabaseType != null) {
			this.databaseType = this.databaseTypeNamed(originalDatabaseType.getName());
		}
	}

	/**
	 * sort by JDBC type, there should be no duplicates
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object o) {
		JDBCTypeToDatabaseTypeMapping other = (JDBCTypeToDatabaseTypeMapping) o;
		return this.jdbcType.compareTo(other.jdbcType);
	}


	// ********** i/o **********

	private void read(Node node) throws CorruptXMLException {
		try {
			this.jdbcType = this.jdbcTypeNamed(XMLTools.childTextContent(node, "jdbc-type", null));
		} catch (IllegalArgumentException ex) {
			throw new CorruptXMLException("platform: " + this.getPlatform().getName(), ex);
		}
		String databaseTypeName = XMLTools.childTextContent(node, "database-type", null);
		if (databaseTypeName != null) {
			try {
				this.databaseType = this.databaseTypeNamed(databaseTypeName);
			} catch (IllegalArgumentException ex) {
				throw new CorruptXMLException(ex);
			}
		}
	}

	void write(Node node) {
		XMLTools.addSimpleTextNode(node, "jdbc-type", this.jdbcType.getName());
		if (this.databaseType != null) {
			XMLTools.addSimpleTextNode(node, "database-type", this.databaseType.getName());
		}
	}


	// ********** printing and displaying **********

	private void displayStringOn(StringBuffer sb) {
		sb.append(this.jdbcType.getName());
		sb.append(" => ");
		sb.append(this.databaseType == null ? "null" : this.databaseType.getName());
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.Node#displayString()
	 */
	public String displayString() {
		StringBuffer sb = new StringBuffer();
		this.displayStringOn(sb);
		return sb.toString();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#toString(StringBuffer)
	 */
	public void toString(StringBuffer sb) {
		this.displayStringOn(sb);
	}

}
