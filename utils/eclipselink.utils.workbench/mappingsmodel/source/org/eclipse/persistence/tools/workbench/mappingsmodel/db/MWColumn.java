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
package org.eclipse.persistence.tools.workbench.mappingsmodel.db;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JavaTypeDeclaration;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

public final class MWColumn
	extends MWModel
	implements MWDataField
{
	/** the name should never be null or empty */
	private volatile String name;
		public static final String NAME_PROPERTY = "name";
		public static final String QUALIFIED_NAME_PROPERTY = "qualifiedName";

	/** the database type should never be null */
	private DatabaseTypeHandle databaseTypeHandle;	// pseudo-final
		public static final String DATABASE_TYPE_PROPERTY = "databaseType";

	private volatile int size;
		public static final String SIZE_PROPERTY = "size";

	/** sub-size is used for scale on numeric types */
	private volatile int subSize;
		public static final String SUB_SIZE_PROPERTY = "subSize";

	private volatile boolean allowsNull;
		public static final String ALLOWS_NULL_PROPERTY = "allowsNull";

	private volatile boolean unique;
		public static final String UNIQUE_PROPERTY = "unique";

	/** this is typically short-hand for 'NOT NULL' and 'UNIQUE' */
	private volatile boolean primaryKey;
		public static final String PRIMARY_KEY_PROPERTY = "primaryKey";

	/** SQL Server variants allow the IDENTITY clause */
	private volatile boolean identity;
		public static final String IDENTITY_PROPERTY = "identity";


	// ********** static methods **********

	/**
	 * Parse the table name from a possibly "qualified" column name:
	 * "ACTG.EMPLOYEE.F_NAME" -> "ACCTG.EMPLOYEE"
	 * "ACTG..F_NAME" -> "ACCTG.."
	 * "EMPLOYEE.F_NAME" -> "EMPLOYEE"
	 * "F_NAME" -> ""
	 */
	public static String parseTableNameFromQualifiedName(String name) {
		int index = name.lastIndexOf('.');
		if (index == -1) {
			return "";
		}
		return name.substring(0, index);
	}

	/**
	 * Parse the column name from a possibly "qualified" column name:
	 * "ACCT.EMPLOYEE.F_NAME" -> "F_NAME"
	 * "ACCT..F_NAME" -> "F_NAME"
	 * "EMPLOYEE.F_NAME" -> "F_NAME"
	 * "F_NAME" -> "F_NAME"
	 */
	public static String parseColumnNameFromQualifiedName(String name) {
		int index = name.lastIndexOf('.');
		if (index == -1) {
			return name;
		}
		return name.substring(index + 1);
	}

	public static boolean nameIsQualified(String columnName) {
		return columnName.indexOf(".") != -1;
	}


	// ********** constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWColumn() {
		super();
	}

	MWColumn(MWTable table, String name) {
		super(table);
		this.name = name;
	}


	// ********** initialization **********

	/**
	 * the database type handle is not mapped directly
	 */
	protected void initialize() {
		super.initialize();
		this.databaseTypeHandle = new DatabaseTypeHandle(this);
	}

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		DatabaseType dbType = this.defaultDatabaseType();
		this.databaseTypeHandle.setDatabaseType(dbType);
		this.size = dbType.requiresSize() ? dbType.getInitialSize() : 0;
		this.subSize = 0;
		this.allowsNull = dbType.allowsNull();
		this.unique = false;
		this.primaryKey = false;
		this.identity = false;
	}


	// ********** accessors **********

	public MWTable getTable() {
		return (MWTable) this.getParent();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.getTable().checkColumnName(name);
		Object old = this.name;
		this.name = name;
		if (this.attributeValueHasChanged(old, name)) {
			this.firePropertyChanged(NAME_PROPERTY, old, name);
			this.qualifiedNameChanged();
			this.getProject().nodeRenamed(this);
		}
	}

	void qualifiedNameChanged() {
		String qName = this.qualifiedName();
		this.firePropertyChanged(QUALIFIED_NAME_PROPERTY, qName);
		this.firePropertyChanged(FIELD_NAME_PROPERTY, qName);		
	}
	
	public DatabaseType getDatabaseType() {
		return this.databaseTypeHandle.getDatabaseType();
	}

	public void setDatabaseType(DatabaseType databaseType) {
		if (databaseType == null) {
			throw new NullPointerException();
		}
		Object old = this.databaseTypeHandle.getDatabaseType();
		this.databaseTypeHandle.setDatabaseType(databaseType);
		this.firePropertyChanged(DATABASE_TYPE_PROPERTY, old, databaseType);
		// align various settings with the new database type
		if (this.attributeValueHasChanged(old, databaseType)) {
			this.synchronizeWithNewDatabaseType();
		}
	}		

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		if (( ! this.getDatabaseType().allowsSize()) && (size != 0)) {
			throw new IllegalArgumentException("size must be 0 when size is not allowed");
		}
		int old = this.size;
		this.size = size;
		this.firePropertyChanged(SIZE_PROPERTY, old, size);
	}

	public int getSubSize() {
		return this.subSize;
	}

	public void setSubSize(int subSize) {
		if (( ! this.getDatabaseType().allowsSubSize()) && (subSize != 0)) {
			throw new IllegalArgumentException("sub-size must be 0 when sub-size is not allowed");
		}
		int old = this.subSize;
		this.subSize = subSize;
		this.firePropertyChanged(SUB_SIZE_PROPERTY, old, subSize);
	}

	public boolean allowsNull() {
		return this.allowsNull;
	}

	public void setAllowsNull(boolean allowsNull) {
		if (( ! this.getDatabaseType().allowsNull()) && (allowsNull)) {
			throw new IllegalArgumentException("allows null must be false when allows null is not allowed");
		}
		boolean old = this.allowsNull;
		this.allowsNull = allowsNull;
		this.firePropertyChanged(ALLOWS_NULL_PROPERTY, old, allowsNull);
		if (allowsNull) {
			this.setPrimaryKey(false);
			this.setIdentity(false);
		}
	}

	public boolean isUnique() {
		return this.unique;
	}

	public void setUnique(boolean unique) {
		boolean old = this.unique;
		this.unique = unique;
		this.firePropertyChanged(UNIQUE_PROPERTY, old, unique);
		if ( ! unique) {
			this.setPrimaryKey(false);
		}
	}

	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	/**
	 * primary key is typically equivalent to
	 * 'NOT NULL' and 'UNIQUE'
	 */
	public void setPrimaryKey(boolean primaryKey) {
		boolean old = this.primaryKey;
		this.primaryKey = primaryKey;
		this.firePropertyChanged(PRIMARY_KEY_PROPERTY, old, primaryKey);
		if (primaryKey) {
			this.setAllowsNull(false);
			this.setUnique(true);
		}
	}

	public boolean isIdentity() {
		return this.identity;
	}

	/**
	 * 'IDENTITY' requires 'NOT NULL'
	 */
	public void setIdentity(boolean identity) {
		if (( ! this.supportsIdentityClause()) && (identity)) {
			throw new IllegalArgumentException("the current platform does not support the IDENTITY clause");
		}
		boolean old = this.identity;
		this.identity = identity;
		this.firePropertyChanged(IDENTITY_PROPERTY, old, identity);
		if (identity) {
			this.setAllowsNull(false);
		}
	}


	// ********** queries **********

	public DatabasePlatform databasePlatform() {
		 return this.getTable().databasePlatform();
	}

	public String qualifiedName() {
		return this.getTable().qualifiedName() + '.' + this.getName();
	}

	/**
	 * this is the initial value of our database type
	 */
	private DatabaseType defaultDatabaseType() {
		return this.databasePlatform().defaultDatabaseType();
	}

	/**
	 * return the Java type declaration corresponding to the column's database type;
	 * this is used to generate instance variables from database columns when
	 * generating classes from tables
	 */
	public JavaTypeDeclaration javaTypeDeclaration() {
		return this.getDatabaseType().javaTypeDeclaration();
	}

	/**
	 * return whether the column can be defined with an IDENTITY
	 * clause (SQL Server variants only)
	 */
	public boolean supportsIdentityClause() {
		return this.getDatabase().supportsIdentityClause();
	}


	// ********** behavior **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.databaseTypeHandle);
	}

	/**
	 * replace our database type (from our old database platform) with
	 * a database type from our new database platform that is a reasonable
	 * match
	 */
	void databasePlatformChanged() {
		this.setDatabaseType(this.databasePlatform().databaseTypeFor(this.getDatabaseType()));
	}

	private void synchronizeWithNewDatabaseType() {
		DatabaseType dbType = this.getDatabaseType();

		// synchronize size/sub-size
		if (dbType.allowsSize()) {
			if (dbType.allowsSubSize()) {
				// leave sub-size unchanged - it will be zero unless we are converting
				// from one numeric type to another numeric type and a "scale" was specified
			} else {
				this.setSubSize(0);
			}
			if (dbType.requiresSize()) {
				if (this.size == 0) {
					this.setSize(dbType.getInitialSize());
				} else {
					// take the previously-assigned size
				}
			} else {
				if (this.subSize == 0) {
					// if size is not required and a sub-size was not specified,
					// we probably want to clear out size: VARCHAR2(20) => NUMBER
					this.setSize(0);
				} else {
					// we will only get here when converting from one numeric type
					// to another numeric type and a "scale" was specified - keep
					// the same size/sub-size combination
				}
			}
		} else {
			this.setSize(0);
			this.setSubSize(0);
		}

		// synchronize allows null
		if (dbType.allowsNull()) {
			// leave allows null unchanged
		} else {
			this.setAllowsNull(false);
		}

		// IDENTITY
		if (dbType.getPlatform().supportsIdentityClause()) {
			// leave identity unchanged
		} else {
			this.setIdentity(false);
		}
	}

	void copySettingsFrom(MWColumn original) {
		this.setDatabaseType(original.getDatabaseType());
		this.setSize(original.getSize());
		this.setSubSize(original.getSubSize());
		this.setAllowsNull(original.allowsNull());
		this.setUnique(original.isUnique());
		this.setPrimaryKey(original.isPrimaryKey());
		this.setIdentity(original.isIdentity());
	}


	// ********** problems **********

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		if (this.getDatabaseType().requiresSize() && (this.size == 0)) {
			currentProblems.add(this.buildProblem(ProblemConstants.COLUMN_SIZE_REQUIRED, this.qualifiedName()));
		}
	}


	// ********** DataField implementation **********

	/**
	 * used by UI components common to O-R and O-X
	 * (mappings, locking policy, etc.)
	 */
	public String fieldName() {
		return this.qualifiedName();
	}

	/**
	 * used by runtime conversions common to O-R and O-X
	 * (mappings, locking policy, etc.)
	 */
	public DatabaseField runtimeField() {
		return new DatabaseField(this.qualifiedName());
	}


	// ********** importing/refreshing **********

	/**
	 * refresh with the data from the specified "external" column
	 */
	void refresh(ExternalColumn externalColumn) {
		this.setDatabaseType(this.databaseTypeFrom(externalColumn));
		this.setSize(this.sizeFrom(externalColumn));
		this.setSubSize(this.subSizeFrom(externalColumn));
		this.setAllowsNull(this.allowsNullFrom(externalColumn));
		this.setPrimaryKey(externalColumn.isPrimaryKey());
		// leave 'unique' unchanged - I don't think we can get it from the database
		// 'primaryKey' will be set later by our parent table
	}

	/**
	 * first try to get the platform-specific datatype;
	 * if that fails, get the platform-specific datatype most
	 * closely associated with the JDBC type;
	 * if that fails, get the default datatype
	 */
	private DatabaseType databaseTypeFrom(ExternalColumn externalColumn) {
		try {
			return this.databasePlatform().databaseTypeNamed(externalColumn.getTypeName());
		} catch (IllegalArgumentException ex) {
			return this.databaseTypeFromJDBCTypeFrom(externalColumn);
		}
	}

	private DatabaseType databaseTypeFromJDBCTypeFrom(ExternalColumn externalColumn) {
		try {
			return this.databasePlatform().databaseTypeForJDBCTypeCode(externalColumn.getJDBCTypeCode());
		} catch (Exception ex) {
			// defensive - when all else fails, use the default type
			return this.databasePlatform().defaultDatabaseType();
		}
	}

	private int sizeFrom(ExternalColumn externalColumn) {
		if ( ! this.getDatabaseType().allowsSize()) {
			// ignore the size in the "external" column - it cannot be used in the column's declaration
			return 0;
		}
		return externalColumn.getSize();
	}

	private int subSizeFrom(ExternalColumn externalColumn) {
		if ( ! this.getDatabaseType().allowsSubSize()) {
			// ignore the sub-size in the "external" column - it cannot be used in the column's declaration
			return 0;
		}
		return externalColumn.getScale();
	}

	private boolean allowsNullFrom(ExternalColumn externalColumn) {
		if ( ! this.getDatabaseType().allowsNull()) {
			// ignore the flag in the "external" column - the column cannot be null, given its current datatype
			return false;
		}
		return externalColumn.isNullable();
	}


	// ********** runtime conversion **********

	/**
	 * Not to be confused with #runtimeField(), this method
	 * 	creates a field definition for use in 
	 * 	- table creator source generation
	 * 	- project conversion
	 *     (and the only reason it's there is so we can convert *back* - grumble, grumble)
	 */
	FieldDefinition buildRuntimeFieldDefinition() {
		FieldDefinition fd = new FieldDefinition(this.name, this.getDatabaseType().getName());
		fd.setSize(this.size);
		fd.setSubSize(this.subSize);

		if (this.primaryKey) {
			fd.setIsPrimaryKey(true);
			// if the column is part of a the primary key, we do not need to specify the
			// implied 'UNIQUE NOT NULL' clause;
			// in fact, it will cause an exception on Oracle (ORA-02261)
		} else {
			fd.setIsPrimaryKey(false);
			fd.setShouldAllowNull(this.allowsNull);
			fd.setUnique(this.unique);
		}

		fd.setIsIdentity(this.identity);

		return fd;
	}


	// ********** displaying and printing **********

	public String displayString() {
		return this.qualifiedName();
	}

	public void toString(StringBuffer sb) {
		sb.append(this.qualifiedName());
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWColumn.class);
	
		descriptor.addDirectMapping("name", "name/text()");
		descriptor.addDirectMapping("databaseTypeName", "getDatabaseTypeNameForTopLink", "setDatabaseTypeNameForTopLink", "type/text()");

		((XMLDirectMapping) descriptor.addDirectMapping("size", "size/text()")).setNullValue(new Integer(0));
		((XMLDirectMapping) descriptor.addDirectMapping("subSize", "sub-size/text()")).setNullValue(new Integer(0));

		((XMLDirectMapping) descriptor.addDirectMapping("allowsNull", "allows-null/text()")).setNullValue(Boolean.FALSE);
		((XMLDirectMapping) descriptor.addDirectMapping("unique", "unique/text()")).setNullValue(Boolean.FALSE);
		((XMLDirectMapping) descriptor.addDirectMapping("primaryKey", "primary-key/text()")).setNullValue(Boolean.FALSE);
		((XMLDirectMapping) descriptor.addDirectMapping("identity", "identity/text()")).setNullValue(Boolean.FALSE);

		return descriptor;
	}

	private String getDatabaseTypeNameForTopLink() {
		return this.databaseTypeHandle.getDatabaseTypeNameForTopLink();
	}
	private void setDatabaseTypeNameForTopLink(String databaseTypeName) {
		this.databaseTypeHandle.setDatabaseTypeNameForTopLink(databaseTypeName);
	}

	/**
	 * fix any possible "corruption"
	 */
	public void postProjectBuild() {
		super.postProjectBuild();
		if (this.primaryKey) {
			this.allowsNull = false;
			this.unique = true;
		}
		if (this.identity) {
			this.allowsNull = false;
		}
	}
}
