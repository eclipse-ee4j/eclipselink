/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.db;

import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


/**
 * This handle is a bit different from the other handles.
 * It references an object that is completely outside of the project
 * and is only used by database fields.
 */
final class DatabaseTypeHandle
	extends MWHandle
{

	/**
	 * This is the actual database type.
	 * It is built from the database type name, below.
	 */
	private volatile DatabaseType databaseType;

	/**
	 * The database type name is transient. It
	 * is used only to hold its value until #postProjectBuild()
	 * is called and we can resolve the actual database type.
	 * We do not keep it in synch with the database type itself because
	 * we cannot know when the database type has been renamed etc.
	 */
	private volatile String databaseTypeName;
	private volatile String legacyDatabaseTypeName;


	// ********** constructors **********

	DatabaseTypeHandle(MWColumn parent) {
		// database types are never removed
		super(parent, NodeReferenceScrubber.NULL_INSTANCE);
	}


	// ********** instance methods **********

	DatabaseType getDatabaseType() {
		return this.databaseType;
	}

	void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}

	protected Node node() {
		return getDatabaseType();
	}

	private DatabasePlatform databasePlatform() {
		return this.getDatabase().getDatabasePlatform();
	}

	public void postProjectBuild() {
		super.postProjectBuild();
		if (this.legacyDatabaseTypeName != null) {
			this.databaseTypeName = this.convertLegacyDatabaseTypeName(this.legacyDatabaseTypeName);
			this.legacyDatabaseTypeName = null;
		}
		if (this.databaseTypeName == null) {
			// bug 3856524 - databaseType can be null in legacy projects
			this.databaseType = this.databasePlatform().defaultDatabaseType();
		} else {
			try {
				this.databaseType = this.databasePlatform().databaseTypeNamed(this.databaseTypeName);
			} catch (IllegalArgumentException ex) {
				// we have really hosed up the database types in the past - try
				// to fix them here...
				this.databaseType = this.databasePlatform().defaultDatabaseType();
			}
		}
		// ensure databaseTypeName is not used by setting it to null....
		this.databaseTypeName = null;
	}

	/**
	 * Override to delegate comparison to the database type itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER attribute should be null.
	 */
	public int compareTo(Object o) {
		return this.databaseType.compareTo(((DatabaseTypeHandle) o).databaseType);
	}

	public void toString(StringBuffer sb) {
		if (this.databaseType == null) {
			sb.append("null");
		} else {
			this.databaseType.toString(sb);
		}
	}


	// ********** TopLink methods **********

	String getDatabaseTypeNameForTopLink() {
		return (this.databaseType == null) ? null : this.databaseType.getName();
	}

	void setDatabaseTypeNameForTopLink(String name) {
		this.databaseTypeName = name;
	}

	void legacySetDatabaseTypeNameForTopLink(String name) {
		this.legacyDatabaseTypeName = name;
	}

	/**
	 * we changed the names of some of the database type names
	 */
	private String convertLegacyDatabaseTypeName(String dbTypeName) {
		String platformName = this.databasePlatform().getName();
		if (platformName.equals("Cloudscape")) {
			if (dbTypeName.equals("FLOAT(16)")) {
				return "FLOAT";
			} else if (dbTypeName.equals("FLOAT(32)")) {
				return "FLOAT";
			} else if (dbTypeName.equals("TEXT")) {
				return "CLOB";
			} else if (dbTypeName.equals("BIT default 0")) {
				return "SMALLINT";
			} else if (dbTypeName.equals("IMAGE")) {
				return "BLOB";
			}
			return dbTypeName;
		} else if (platformName.equals("dBASE")) {
			if (dbTypeName.equals("NUMBER")) {
				return "NUMERIC";
			}
			return dbTypeName;
		} else if (platformName.equals("Informix")) {
			if (dbTypeName.equals("FLOAT(16)")) {
				return "FLOAT";
			} else if (dbTypeName.equals("FLOAT(32)")) {
				return "FLOAT";
			}
			return dbTypeName;
		} else if (platformName.equals("PointBase")) {
			if (dbTypeName.equals("CHARACTER")) {
				return "CHAR";
			} else if (dbTypeName.equals("DOUBLE PRECISION")) {
				return "DOUBLE";
			}
			return dbTypeName;
		} else if (platformName.equals("SQL Anywhere")) {
			if (dbTypeName.equals("FLOAT(16)")) {
				return "FLOAT";
			} else if (dbTypeName.equals("FLOAT(32)")) {
				return "FLOAT";
			} else if (dbTypeName.equals("INTEGER")) {
				return "INT";
			} else if (dbTypeName.equals("BIT default 0")) {
				return "BIT";
			}
			return dbTypeName;
		} else if (platformName.equals("Microsoft SQL Server")) {
			if (dbTypeName.equals("FLOAT(16)")) {
				return "FLOAT";
			} else if (dbTypeName.equals("FLOAT(32)")) {
				return "FLOAT";
			} else if (dbTypeName.equals("INTEGER")) {
				return "INT";
			} else if (dbTypeName.equals("BIT default 0")) {
				return "BIT";
			}
			return dbTypeName;
		} else if (platformName.equals("Sybase")) {
			if (dbTypeName.equals("SMALLINT")) {
				return "smallint";
			} else if (dbTypeName.equals("BIT")) {
				return "bit";
			} else if (dbTypeName.equals("INTEGER")) {
				return "int";
			} else if (dbTypeName.equals("DATETIME")) {
				return "datetime";
			} else if (dbTypeName.equals("NUMERIC")) {
				return "numeric";
			} else if (dbTypeName.equals("FLOAT(16)")) {
				return "float";
			} else if (dbTypeName.equals("FLOAT(32)")) {
				return "float";
			} else if (dbTypeName.equals("TEXT")) {
				return "text";
			} else if (dbTypeName.equals("VARCHAR")) {
				return "varchar";
			} else if (dbTypeName.equals("CHAR")) {
				return "char";
			} else if (dbTypeName.equals("IMAGE")) {
				return "image";
			} else if (dbTypeName.equals("BINARY")) {
				return "binary";
			} else if (dbTypeName.equals("VARBINARY")) {
				return "varbinary";
			}
			return dbTypeName;
		}
		// some of the names were not changed
		return dbTypeName;
	}

}
