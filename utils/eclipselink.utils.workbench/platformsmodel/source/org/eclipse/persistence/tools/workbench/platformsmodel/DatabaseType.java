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

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.w3c.dom.Node;


/**
 * A database type holds all the settings for a platform-specific database type.
 * For example, the Oracle database type VARCHAR2
 * 	- maps to the JDBC type VARCHAR
 * 	- allows and requires a size specification
 * 	- has a initial size of 20 when generating tables
 * 	- does not allow a sub-size specification
 * 	- allows a null value
 */
public final class DatabaseType
	extends AbstractNodeModel
{

	/**
	 * a name uniquely identifying the type within a
	 * database platform
	 */
	private String name;
		public static final String NAME_PROPERTY = "name";

	/**
	 * the JDBC type most closely resembling the
	 * platform-specific database type
	 */
	private JDBCType jdbcType;
		public static final String JDBC_TYPE_PROPERTY = "jdbcType";

	/**
	 * whether the type can be declared with a size
	 */
	private boolean allowsSize;
		public static final String ALLOWS_SIZE_PROPERTY = "allowsSize";

	/**
	 * whether the type *requires* a size...
	 */
	private boolean requiresSize;
		public static final String REQUIRES_SIZE_PROPERTY = "requiresSize";

	/**
	 * ...and, if it does, what the initial size should be
	 */
	private int initialSize;
		public static final String INITIAL_SIZE_PROPERTY = "initialSize";

	/**
	 * whether the type can be declared with a "sub-size";
	 * this is typically a numeric scale
	 */
	private boolean allowsSubSize;
		public static final String ALLOWS_SUB_SIZE_PROPERTY = "allowsSubSize";

	/**
	 * whether the type allows NULL values
	 */
	private boolean allowsNull;
		public static final String ALLOWS_NULL_PROPERTY = "allowsNull";


	// ********** constructors **********

	/**
	 * this constructor is called when the type is read from an XML file
	 */
	DatabaseType(DatabasePlatform platform, Node node) throws CorruptXMLException {
		super(platform);
		this.read(node);
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new type (which shouldn't happen very often,
	 * since all the typical platforms have already been built...)
	 */
	DatabaseType(DatabasePlatform platform, String name) {
		super(platform);
		this.name = name;
		this.jdbcType = this.jdbcTypeRepository().getDefaultJDBCType();
	}


	// ********** initialization **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractNodeModel#initialize()
	 */
	protected void initialize() {
		super.initialize();
		this.allowsSize = true;
		this.requiresSize = false;
		this.initialSize = 0;
		this.allowsSubSize = false;
		this.allowsNull = true;
	}


	// ********** accessors **********

	public DatabasePlatform getPlatform() {
		return  (DatabasePlatform) this.getParent();
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.getPlatform().checkDatabaseTypeName(name);
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}


	public JDBCType getJDBCType() {
		return this.jdbcType;
	}

	public void setJDBCType(JDBCType jdbcType) {
		if (jdbcType == null) {
			throw new NullPointerException();
		}
		Object old = this.jdbcType;
		this.jdbcType = jdbcType;
		this.firePropertyChanged(JDBC_TYPE_PROPERTY, old, jdbcType);
	}


	public boolean allowsSize() {
		return this.allowsSize;
	}

	public void setAllowsSize(boolean allowsSize) {
		boolean old = this.allowsSize;
		this.allowsSize = allowsSize;
		this.firePropertyChanged(ALLOWS_SIZE_PROPERTY, old, allowsSize);

		// if size is not allowed, sub-size is not either and size cannot be required
		if ( ! allowsSize) {
			this.setAllowsSubSize(false);
			this.setRequiresSize(false);
		}
	}


	public boolean requiresSize() {
		return this.requiresSize;
	}

	public void setRequiresSize(boolean requiresSize) {
		boolean old = this.requiresSize;
		this.requiresSize = requiresSize;
		this.firePropertyChanged(REQUIRES_SIZE_PROPERTY, old, requiresSize);

		// if size is required, size must be allowed...
		if (requiresSize) {
			this.setAllowsSize(true);
		} else {
			this.setInitialSize(0);
		}
	}


	public int getInitialSize() {
		return this.initialSize;
	}

	public void setInitialSize(int initialSize) {
		if (initialSize < 0) {
			throw new IllegalArgumentException("initial size must be greater than or equal to zero" + initialSize);
		}
		int old = this.initialSize;
		this.initialSize = initialSize;
		this.firePropertyChanged(INITIAL_SIZE_PROPERTY, old, initialSize);

		// if there is a initial size, it must be required
		if (initialSize != 0) {
			this.setRequiresSize(true);
		}
	}


	public boolean allowsSubSize() {
		return this.allowsSubSize;
	}

	public void setAllowsSubSize(boolean allowsSubSize) {
		boolean old = this.allowsSubSize;
		this.allowsSubSize = allowsSubSize;
		this.firePropertyChanged(ALLOWS_SUB_SIZE_PROPERTY, old, allowsSubSize);

		// if sub-size is allowed, size must be also
		if (allowsSubSize) {
			this.setAllowsSize(true);
		}
	}


	public boolean allowsNull() {
		return this.allowsNull;
	}

	public void setAllowsNull(boolean allowsNull) {
		boolean old = this.allowsNull;
		this.allowsNull = allowsNull;
		this.firePropertyChanged(ALLOWS_NULL_PROPERTY, old, allowsNull);
	}


	// ********** queries **********

	/**
	 * return a Java type declaration that can used for this database type;
	 * this is used for generating classes from tables
	 */
	public JavaTypeDeclaration javaTypeDeclaration() {
		return this.jdbcTypeRepository().javaTypeDeclarationFor(this.getJDBCType());
	}

	private DatabasePlatformRepository platformRepository() {
		return this.getPlatform().getRepository();
	}

	private JDBCTypeRepository jdbcTypeRepository() {
		return this.platformRepository().getJDBCTypeRepository();
	}

	private JDBCType jdbcTypeNamed(String jdbcTypeName) {
		return this.jdbcTypeRepository().jdbcTypeNamed(jdbcTypeName);
	}

	/**
	 * Return all the JDBC types that the database type
	 * could be mapped to. Simplifies UI code....
	 */
	public Iterator jdbcTypes() {
		return this.jdbcTypeRepository().jdbcTypes();
	}

	/**
	 * Return the size of all the JDBC types that the database type
	 * could be mapped to. Simplifies UI code....
	 */
	public int jdbcTypesSize() {
		return this.jdbcTypeRepository().jdbcTypesSize();
	}


	// ********** behavior **********

	protected void addProblemsTo(List currentProblems) {
		if (this.requiresSize() && (this.getInitialSize() == 0)) {
			currentProblems.add(this.buildProblem("003", this.getPlatform().getName(), this.getName()));
		}
		super.addProblemsTo(currentProblems);
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractNodeModel#nodeRemoved(org.eclipse.persistence.tools.workbench.utility.Node)
	 */
	public void nodeRemoved(org.eclipse.persistence.tools.workbench.utility.node.Node node) {
		super.nodeRemoved(node);
		if (this.jdbcType == node) {
			this.setJDBCType(this.jdbcTypeRepository().getDefaultJDBCType());
		}
	}

	/**
	 * copy all the settings from the original type
	 * to this, newly-created, type
	 */
	void cloneFrom(DatabaseType originalType) {
		// the name has been set by the time we get here
		this.setComment(originalType.getComment());
		this.setJDBCType(originalType.getJDBCType());	// the JDBC type should NOT be cloned
		this.setAllowsSize(originalType.allowsSize());
		this.setRequiresSize(originalType.requiresSize());
		this.setInitialSize(originalType.getInitialSize());
		this.setAllowsSubSize(originalType.allowsSubSize());
		this.setAllowsNull(originalType.allowsNull());
	}


	// ********** i/o **********

	private void read(Node node) throws CorruptXMLException {
		if (node == null) {
			throw this.buildCorruptXMLException("missing node");
		}
		this.name = XMLTools.childTextContent(node, "name", null);
		if ((this.name == null) || (this.name.length() == 0)) {
			throw this.buildCorruptXMLException("name is required");
		}

		ClassTools.setFieldValue(this, "comment", XMLTools.childTextContent(node, "comment", ""));

		String jdbcTypeName = XMLTools.childTextContent(node, "jdbc-type", null);
		try {
			this.jdbcType = this.jdbcTypeNamed(jdbcTypeName);
		} catch (IllegalArgumentException ex) {
			throw this.buildCorruptXMLException(ex);
		}

		this.allowsSize = XMLTools.childBooleanContent(node, "allows-size", false);

		this.requiresSize = XMLTools.childBooleanContent(node, "requires-size", false);
		if (( ! this.allowsSize) && this.requiresSize) {
			throw this.buildCorruptXMLException("size cannot be required when it is not allowed");
		}

		this.initialSize = XMLTools.childIntContent(node, "initial-size", 0);
		if (( ! this.requiresSize) && (this.initialSize != 0)) {
			throw this.buildCorruptXMLException("initial size cannot be specified when size is not required");
		}

		this.allowsSubSize = XMLTools.childBooleanContent(node, "allows-sub-size", false);
		if (( ! this.allowsSize) && this.allowsSubSize) {
			throw this.buildCorruptXMLException("sub-size cannot be allowed when size is not allowed");
		}

		this.allowsNull = XMLTools.childBooleanContent(node, "allows-null", false);
	}

	/**
	 * tack on some more information on the message
	 */
	private CorruptXMLException buildCorruptXMLException(String message) {
		return new CorruptXMLException(message + " (" + this.corruptXMLLocation() + ")");
	}

	/**
	 * tack on some more information on the message
	 */
	private CorruptXMLException buildCorruptXMLException(Throwable t) {
		return new CorruptXMLException(this.corruptXMLLocation(), t);
	}

	private String corruptXMLLocation() {
		return this.getPlatform().getName() + ":" + this.name;
	}

	void write(Node node) {
		XMLTools.addSimpleTextNode(node, "name", this.name);
		XMLTools.addSimpleTextNode(node, "comment", (String) ClassTools.getFieldValue(this, "comment"), "");
		XMLTools.addSimpleTextNode(node, "jdbc-type", this.jdbcType.getName());
		XMLTools.addSimpleTextNode(node, "allows-size", this.allowsSize, false);
		XMLTools.addSimpleTextNode(node, "requires-size", this.requiresSize, false);
		XMLTools.addSimpleTextNode(node, "initial-size", this.initialSize, 0);
		XMLTools.addSimpleTextNode(node, "allows-sub-size", this.allowsSubSize, false);
		XMLTools.addSimpleTextNode(node, "allows-null", this.allowsNull, false);
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
