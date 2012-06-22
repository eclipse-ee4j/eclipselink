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

}
