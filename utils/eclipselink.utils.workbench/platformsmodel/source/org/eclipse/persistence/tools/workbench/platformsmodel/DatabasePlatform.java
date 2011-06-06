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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * A database platform holds all the settings for a specific database platform
 * (e.g. Oracle, MS SQL Server). This includes whether the platform supports
 * certain features used by TopLink (e.g. native sequencing) and the
 * various database types and their corresponding JDBC types.
 */
public final class DatabasePlatform
	extends AbstractNodeModel
{

	/**
	 * a name uniquely identifying the platform within a
	 * database platform repository
	 */
	private String name;
		public static final String NAME_PROPERTY = "name";

	/**
	 * this is the short file name - the directory is determined
	 * by the parent repository;
	 * like the name, above, this must also be unique within the repository
	 */
	private String shortFileName;
		public static final String SHORT_FILE_NAME_PROPERTY = "shortFileName";

	/**
	 * store this as a string (as opposed to the actual class) because users
	 * can supply their own and it might not be on the classpath
	 */
	private String runtimePlatformClassName;
		public static final String RUNTIME_PLATFORM_CLASS_NAME_PROPERTY = "runtimePlatformClassName";

	/**
	 * whether the platform supports "native" sequencing, as opposed to
	 * using a "sequence" table
	 * (Oracle variants have SEQUENCEs; SQL Server variants have
	 * IDENTITY fields; Informix has SERIAL fields)
	 */
	private boolean supportsNativeSequencing;
		public static final String SUPPORTS_NATIVE_SEQUENCING_PROPERTY = "supportsNativeSequencing";

	/**
	 * whether the platform supports the IDENTITY clause in a column definition
	 * (SQL Server variants only)
	 */
	private boolean supportsIdentityClause;
		public static final String SUPPORTS_IDENTITY_CLAUSE_PROPERTY = "supportsIdentityClause";

	/**
	 * whether the platform supports returning on an update/insert natively
	 */
	private boolean supportsNativeReturning;
		public static final String SUPPORTS_NATIVE_RETURNING_PROPERTY = "supportsNativeReturning";

	/**
	 * collection of the database platform-specific datatypes
	 * (e.g. VARCHAR2, NUMBER)
	 */
	private Collection databaseTypes;
		public static final String DATABASE_TYPES_COLLECTION = "databaseTypes";

	/**
	 * mappings of all the JDBC types to the database platform-specific datatypes
	 * (e.g. java.sql.Types.INTEGER maps to the Oracle NUMBER)
	 */
	private Collection jdbcTypeToDatabaseTypeMappings;
		public static final String JDBC_TYPE_TO_DATABASE_TYPE_MAPPINGS_COLLECTION = "jdbcTypeToDatabaseTypeMappings";


	// ********** constructors **********

	/**
	 * this constructor is called when the platform is read from an XML file
	 */
	DatabasePlatform(DatabasePlatformRepository repository, File file) throws CorruptXMLException {
		super(repository);
		this.read(file);
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new platform (which shouldn't happen very often,
	 * since all the typical platforms have already been built and
	 * stored in XML files...)
	 */
	DatabasePlatform(DatabasePlatformRepository repository, String name, String shortFileName) {
		super(repository);
		this.name = name;
		this.shortFileName = shortFileName;
		this.initializeJDBCTypeToDatabaseTypeMappings();
	}


	// ********** initialization **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#initialize()
	 */
	protected void initialize() {
		super.initialize();
		this.runtimePlatformClassName = this.defaultRuntimePlatformClassName();
		this.supportsNativeSequencing = false;
		this.supportsNativeReturning = false;
		this.supportsIdentityClause = false;
		this.databaseTypes = new Vector();
		this.jdbcTypeToDatabaseTypeMappings = new Vector();
	}

	private String defaultRuntimePlatformClassName() {
		return "org.eclipse.persistence.platform.database.DatabasePlatform";	// this is the runtime default
	}

	/**
	 * build an empty mapping for every JDBC type in the JDBC type repository
	 */
	private void initializeJDBCTypeToDatabaseTypeMappings() {
		for (Iterator stream = this.jdbcTypeRepository().jdbcTypes(); stream.hasNext(); ) {
			this.jdbcTypeToDatabaseTypeMappings.add(new JDBCTypeToDatabaseTypeMapping(this, (JDBCType) stream.next()));
		}
	}


	// ********** accessors **********

	public DatabasePlatformRepository getRepository() {
		return  (DatabasePlatformRepository) this.getParent();
	}


	// ***** name
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if ((name != null) && name.equals(this.name)) {
			return;
		}
		this.getRepository().checkPlatformName(name);
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}


	// ***** short file name
	public String getShortFileName() {
		return this.shortFileName;
	}

	public void setShortFileName(String shortFileName) {
		if ((shortFileName != null) && shortFileName.equals(this.shortFileName)) {
			return;
		}
		this.getRepository().checkPlatformShortFileName(shortFileName);
		Object old = this.shortFileName;
		this.shortFileName = shortFileName;
		this.firePropertyChanged(SHORT_FILE_NAME_PROPERTY, old, shortFileName);
	}


	// ***** runtime platform class name
	public String getRuntimePlatformClassName() {
		return this.runtimePlatformClassName;
	}

	public void setRuntimePlatformClassName(String runtimePlatformClassName) {
		if ((runtimePlatformClassName == null) || (runtimePlatformClassName.length() == 0)) {
			throw new IllegalArgumentException("run-time platform class name is required");
		}
		Object old = runtimePlatformClassName;
		this.runtimePlatformClassName = runtimePlatformClassName;	
		this.firePropertyChanged(RUNTIME_PLATFORM_CLASS_NAME_PROPERTY, old, runtimePlatformClassName);
	}
	
	// ***** supports native returning
	public boolean supportsNativeReturning() {
		return this.supportsNativeReturning;
	}
	
	public void setSupportsNativeReturning(boolean supportNativeReturning) {
		boolean old = this.supportsNativeReturning;
		this.supportsNativeReturning = supportNativeReturning;
		this.firePropertyChanged(SUPPORTS_NATIVE_RETURNING_PROPERTY, old, supportNativeReturning);
	}
	
	// ***** supports native sequencing
	public boolean supportsNativeSequencing() {
		return this.supportsNativeSequencing;
	}

	public void setSupportsNativeSequencing(boolean supportsNativeSequencing) {
		boolean old = this.supportsNativeSequencing;
		this.supportsNativeSequencing = supportsNativeSequencing;
		this.firePropertyChanged(SUPPORTS_NATIVE_SEQUENCING_PROPERTY, old, supportsNativeSequencing);

		// if we don't support "native" sequencing, then we can't support the IDENTITY clause
		if ( ! supportsNativeSequencing) {
			this.setSupportsIdentityClause(false);
		}
	}


	// ***** supports IDENTITY clause
	public boolean supportsIdentityClause() {
		return this.supportsIdentityClause;
	}

	public void setSupportsIdentityClause(boolean supportsIdentityClause) {
		boolean old = this.supportsIdentityClause;
		this.supportsIdentityClause = supportsIdentityClause;
		this.firePropertyChanged(SUPPORTS_IDENTITY_CLAUSE_PROPERTY, old, supportsIdentityClause);

		// if we support the IDENTITY clause, then we must support "native" sequencing
		if (supportsIdentityClause) {
			this.setSupportsNativeSequencing(true);
		}
	}


	// ***** database types
	public Iterator databaseTypes() {
		return new CloneIterator(this.databaseTypes) {
			protected void remove(Object current) {
				DatabasePlatform.this.removeDatabaseType((DatabaseType) current);
			}
		};
	}

	public int databaseTypesSize() {
		return this.databaseTypes.size();
	}

	public DatabaseType addDatabaseType(String typeName) {
		this.checkDatabaseTypeName(typeName);
		return this.addDatabaseType(new DatabaseType(this, typeName));
	}

	private DatabaseType addDatabaseType(DatabaseType type) {
		this.addItemToCollection(type, this.databaseTypes, DATABASE_TYPES_COLLECTION);
		return type;
	}

	public void removeDatabaseType(DatabaseType type) {
		this.removeItemFromCollection(type, this.databaseTypes, DATABASE_TYPES_COLLECTION);
		this.databaseTypeRemoved(type);
	}

	private void databaseTypeRemoved(DatabaseType removedType) {
		synchronized (this.jdbcTypeToDatabaseTypeMappings) {
			for (Iterator stream = this.jdbcTypeToDatabaseTypeMappings.iterator(); stream.hasNext(); ) {
				JDBCTypeToDatabaseTypeMapping mapping = (JDBCTypeToDatabaseTypeMapping) stream.next();
				if (mapping.getDatabaseType() == removedType) {
					mapping.setDatabaseType(null);
				}
			}
		}
	}

	public void removeDatabaseTypes(Collection types) {
		this.removeItemsFromCollection(types, this.databaseTypes, DATABASE_TYPES_COLLECTION);
		this.databaseTypesRemoved(types);
	}

	private void databaseTypesRemoved(Collection removedTypes) {
		for (Iterator stream = removedTypes.iterator(); stream.hasNext(); ) {
			this.databaseTypeRemoved((DatabaseType) stream.next());
		}
	}

	public void removeDatabaseTypes(Iterator types) {
		this.removeDatabaseTypes(CollectionTools.collection(types));
	}


	// ***** JDBC type to database type mappings
	public Iterator jdbcTypeToDatabaseTypeMappings() {
		return new CloneIterator(this.jdbcTypeToDatabaseTypeMappings);
	}

	public int jdbcTypeToDatabaseTypeMappingsSize() {
		return this.jdbcTypeToDatabaseTypeMappings.size();
	}

	/**
	 * adding and removing mappings is PRIVATE;
	 * these are done only in response to changes to the JDBC type repository
	 */
	private JDBCTypeToDatabaseTypeMapping addJDBCTypeToDatabaseTypeMapping(JDBCType jdbcType) {
		return this.addJDBCTypeToDatabaseTypeMapping(new JDBCTypeToDatabaseTypeMapping(this, jdbcType));
	}

	/**
	 * adding and removing mappings is PRIVATE;
	 * these are done only in response to changes to the JDBC type repository
	 */
	private JDBCTypeToDatabaseTypeMapping addJDBCTypeToDatabaseTypeMapping(JDBCTypeToDatabaseTypeMapping mapping) {
		this.addItemToCollection(mapping, this.jdbcTypeToDatabaseTypeMappings, JDBC_TYPE_TO_DATABASE_TYPE_MAPPINGS_COLLECTION);
		return mapping;
	}

	/**
	 * adding and removing mappings is PRIVATE;
	 * these are done only in response to changes to the JDBC type repository
	 */
	private Iterator internalJDBCTypeToDatabaseTypeMappings() {
		return new CloneIterator(this.jdbcTypeToDatabaseTypeMappings) {
			protected void remove(Object current) {
				DatabasePlatform.this.removeJDBCTypeToDatabaseTypeMapping((JDBCTypeToDatabaseTypeMapping) current);
			}
		};
	}

	/**
	 * adding and removing mappings is PRIVATE;
	 * these are done only in response to changes to the JDBC type repository
	 */
	void removeJDBCTypeToDatabaseTypeMapping(JDBCTypeToDatabaseTypeMapping mapping) {
		this.removeItemFromCollection(mapping, this.jdbcTypeToDatabaseTypeMappings, JDBC_TYPE_TO_DATABASE_TYPE_MAPPINGS_COLLECTION);
	}


	// ********** queries **********

	/**
	 * this is how a database field resolves its database type
	 * when the database field is read in from an XML file
	 */
	public DatabaseType databaseTypeNamed(String databaseTypeName) {
		synchronized (this.databaseTypes) {
			for (Iterator stream = this.databaseTypes.iterator(); stream.hasNext(); ) {
				DatabaseType databaseType = (DatabaseType) stream.next();
				if (databaseType.getName().equals(databaseTypeName)) {
					return databaseType;
				}
			}
			throw new IllegalArgumentException("missing database type named: "
					+ databaseTypeName + " (platform: " + this.name + ")");
		}
	}

	public boolean containsDatabaseTypeNamed(String databaseTypeName) {
		synchronized (this.databaseTypes) {
			for (Iterator stream = this.databaseTypes.iterator(); stream.hasNext(); ) {
				DatabaseType databaseType = (DatabaseType) stream.next();
				if (databaseType.getName().equals(databaseTypeName)) {
					return true;
				}
			}
		}
		return false;
	}

	private JDBCTypeRepository jdbcTypeRepository() {
		return this.getRepository().getJDBCTypeRepository();
	}

	private JDBCType jdbcTypeForCode(int jdbcTypeCode) {
		return this.jdbcTypeRepository().jdbcTypeForCode(jdbcTypeCode);
	}

	JDBCType jdbcTypeNamed(String jdbcTypeName) {
		return this.jdbcTypeRepository().jdbcTypeNamed(jdbcTypeName);
	}

	public DatabaseType defaultDatabaseType() {
		return this.databaseTypeFor(this.jdbcTypeRepository().getDefaultJDBCType());
	}

	/**
	 * return the database type from "this" platform that is a reasonable
	 * facsimile of the specified database type from some "other" platform;
	 * used for converting database tables from one platform to another
	 */
	public DatabaseType databaseTypeFor(DatabaseType otherType) {
		// if there is a type on this platform with the exact same name, use it;
		// the platforms might simply be variants of each other (e.g. Sybase vs. MS SQL Server)
		try {
			return this.databaseTypeNamed(otherType.getName());
		} catch (IllegalArgumentException ex) {
			// if there is no direct match, convert to a JDBC type then to a database type for this platform
			return this.databaseTypeFor(otherType.getJDBCType());
		}
	}

	private Iterator databaseTypeNames() {
		return new TransformationIterator(this.databaseTypes()) {
			protected Object transform(Object next) {
				return ((DatabaseType) next).getName();
			}
		};
	}

	/**
	 * this is only used during a read, when we are checking for duplicates
	 */
	private boolean maps(JDBCType type) {
		for (Iterator stream = this.jdbcTypeToDatabaseTypeMappings.iterator(); stream.hasNext(); ) {
			if (((JDBCTypeToDatabaseTypeMapping) stream.next()).getJDBCType() == type) {
				return true;
			}
		}
		return false;
	}


	// ********** queries: return a database type for a given JDBC type or code **********

	/**
	 * every platform will have every JDBC type mapped to a database type
	 */
	private JDBCTypeToDatabaseTypeMapping jdbcTypeToDatabaseTypeMappingFor(JDBCType jdbcType) {
		synchronized (this.jdbcTypeToDatabaseTypeMappings) {
			for (Iterator stream = this.jdbcTypeToDatabaseTypeMappings.iterator(); stream.hasNext(); ) {
				JDBCTypeToDatabaseTypeMapping mapping = (JDBCTypeToDatabaseTypeMapping) stream.next();
				if (mapping.maps(jdbcType)) {
					return mapping;
				}
			}
			throw new IllegalStateException("JDBC type to database type mapping is missing: "
					+ jdbcType.getName() + " (platform: " + this.name + ")");
		}
	}

	/**
	 * every platform will have every JDBC type mapped to a database type
	 */
	private DatabaseType databaseTypeFor(JDBCType jdbcType) {
		return this.jdbcTypeToDatabaseTypeMappingFor(jdbcType).getDatabaseType();
	}

	/**
	 * every platform will have every JDBC type mapped to a database type
	 */
	public DatabaseType databaseTypeForJDBCTypeCode(int jdbcTypeCode) {
		return this.databaseTypeFor(this.jdbcTypeForCode(jdbcTypeCode));
	}


	// ********** queries: return a database type for a given Java type declaration **********

	/**
	 * return whether the specified Java type declaration has a corresponding
	 * datatype; this can be used to determine whether a
	 * Java class can be mapped with a direct-to-field mapping
	 * (or a direct collection mapping), as opposed to needing its
	 * own descriptor
	 */
	public boolean javaTypeDeclarationCanBeMappedToDatabaseType(String javaClassName, int arrayDepth) {
		// if there is a JDBC type for the Java type declaration, then we have a database type...
		return this.jdbcTypeRepository().javaTypeDeclarationCanBeMappedToJDBCType(javaClassName, arrayDepth);
	}

	/**
	 * get the JDBC type for the specified type declaration, then
	 * get the database type for that JDBC type
	 */
	public DatabaseType databaseTypeForJavaTypeDeclaration(String javaClassName, int arrayDepth) {
		return this.databaseTypeFor(this.jdbcTypeRepository().jdbcTypeForJavaTypeDeclaration(javaClassName, arrayDepth));
	}


	// ********** behavior **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractNodeModel#addChildrenTo(java.util.List)
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.databaseTypes) { children.addAll(this.databaseTypes); }
		synchronized (this.jdbcTypeToDatabaseTypeMappings) { children.addAll(this.jdbcTypeToDatabaseTypeMappings); }
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractNodeModel#nodeRemoved(org.eclipse.persistence.tools.workbench.utility.Node)
	 */
	public void nodeRemoved(org.eclipse.persistence.tools.workbench.utility.node.Node node) {
		super.nodeRemoved(node);
		for (Iterator stream = this.internalJDBCTypeToDatabaseTypeMappings(); stream.hasNext(); ) {
			if (((JDBCTypeToDatabaseTypeMapping) stream.next()).getJDBCType() == node) {
				stream.remove();
				break;	// there should be only one...
			}
		}
	}

	void jdbcTypeAdded(JDBCType newJDBCType) {
		this.addJDBCTypeToDatabaseTypeMapping(newJDBCType);
	}

	/**
	 * copy all the settings from the original platform
	 * to this, newly-created, platform
	 */
	void cloneFrom(DatabasePlatform originalPlatform) {
		// the name and shortFileName have been set by the time we get here
		this.setComment(originalPlatform.getComment());
		this.runtimePlatformClassName = originalPlatform.getRuntimePlatformClassName();
		this.supportsNativeSequencing = originalPlatform.supportsNativeSequencing();
		this.supportsNativeReturning = originalPlatform.supportsNativeReturning();
		this.supportsIdentityClause = originalPlatform.supportsIdentityClause();
		for (Iterator stream = originalPlatform.databaseTypes(); stream.hasNext(); ) {
			DatabaseType originalType = (DatabaseType) stream.next();
			DatabaseType cloneType = this.addDatabaseType(originalType.getName());
			cloneType.cloneFrom(originalType);
		}
		// the JDBC mappings have been pre-built by the time we get here
		for (Iterator stream = originalPlatform.jdbcTypeToDatabaseTypeMappings(); stream.hasNext(); ) {
			JDBCTypeToDatabaseTypeMapping originalMapping = (JDBCTypeToDatabaseTypeMapping) stream.next();
			JDBCTypeToDatabaseTypeMapping cloneMapping = this.jdbcTypeToDatabaseTypeMappingFor(originalMapping.getJDBCType());
			cloneMapping.cloneFrom(originalMapping);
		}
	}

	/**
	 * disallow duplicate type names within a single platform
	 */
	private void checkDatabaseType(DatabaseType databaseType) {
		this.checkDatabaseTypeName(databaseType.getName());
	}

	/**
	 * disallow duplicate type names within a single platform
	 */
	void checkDatabaseTypeName(String databaseTypeName) {
		if ((databaseTypeName == null) || (databaseTypeName.length() == 0)) {
			throw new IllegalArgumentException("database type name is required");
		}
		if (CollectionTools.contains(this.databaseTypeNames(), databaseTypeName)) {
			throw new IllegalArgumentException("duplicate database type name: " + databaseTypeName);
		}
	}


	// ********** i/o **********

	// ***** read
	private void read(File file) throws CorruptXMLException {
		this.shortFileName = file.getName();
		Document doc = XMLTools.parse(file);
		Node root = XMLTools.child(doc, "database-platform");
		if (root == null) {
			throw new CorruptXMLException("missing root node: database-platform (" + file.getPath() + ")");
		}

		this.name = XMLTools.childTextContent(root, "name", null);
		if ((this.name == null) || (this.name.length() == 0)) {
			throw new CorruptXMLException("name is required (" + file.getPath() + ")");
		}

		ClassTools.setFieldValue(this, "comment", XMLTools.childTextContent(root, "comment", ""));

		this.runtimePlatformClassName = XMLTools.childTextContent(root, "runtime-platform-class", null);
		if ((this.runtimePlatformClassName == null) || (this.runtimePlatformClassName.length() == 0)) {
			throw this.buildCorruptXMLException("run-time platform class name is required");
		}

		this.supportsNativeSequencing = XMLTools.childBooleanContent(root, "supports-native-sequencing", false);
		this.supportsNativeReturning = XMLTools.childBooleanContent(root, "supports-native-returning", false);
		this.supportsIdentityClause = XMLTools.childBooleanContent(root, "supports-identity-clause", false);
		if (this.supportsIdentityClause && ( ! this.supportsNativeSequencing)) {
			throw this.buildCorruptXMLException("platform must support native sequencing if it supports the IDENTITY clause");
		}

		this.readDatabaseTypeNodes(XMLTools.child(root, "database-types"));

		// wait until the database types have been read in before building the JDBC mappings
		this.readJDBCMappingNodes(XMLTools.child(root, "jdbc-mappings"));
	}

	private void readDatabaseTypeNodes(Node databaseTypesNode) throws CorruptXMLException {
		Node[] databaseTypeNodes = XMLTools.children(databaseTypesNode, "database-type");
		int len = databaseTypeNodes.length;
		for (int i = 0; i < len; i++) {
			DatabaseType databaseType = new DatabaseType(this, databaseTypeNodes[i]);
			try {
				this.checkDatabaseType(databaseType);	// check for duplicates
			} catch (IllegalArgumentException ex) {
				throw this.buildCorruptXMLException(ex);
			}
			this.databaseTypes.add(databaseType);
		}
	}

	private void readJDBCMappingNodes(Node mappingsNode) throws CorruptXMLException {
		Node[] mappingNodes = XMLTools.children(mappingsNode, "jdbc-mapping");
		int len = mappingNodes.length;
		for (int i = 0; i < len; i++) {
			JDBCTypeToDatabaseTypeMapping mapping = new JDBCTypeToDatabaseTypeMapping(this, mappingNodes[i]);
			// check for duplicates
			if (this.maps(mapping.getJDBCType())) {
				throw this.buildCorruptXMLException("duplicate JDBC to database type mapping: " + mapping);
			}
			this.jdbcTypeToDatabaseTypeMappings.add(mapping);
		}
		// at this point, the mappings all have legitimate JDBC types and
		// there are no duplicates; so we just need to make sure *every* JDBC type has been mapped
		if (this.jdbcTypeToDatabaseTypeMappings.size() != this.jdbcTypeRepository().jdbcTypesSize()) {
			throw this.buildCorruptXMLException("missing JDBC to database type mappings (number of mappings: " + this.jdbcTypeToDatabaseTypeMappings.size()
					+ " - number of JDBC types: " + this.jdbcTypeRepository().jdbcTypesSize() + ")");
		}
	}

	/**
	 * tack the platform on to the message
	 */
	private CorruptXMLException buildCorruptXMLException(String message) {
		return new CorruptXMLException(message + " (platform: " + this.name + ")");
	}

	/**
	 * tack the platform on to the message
	 */
	private CorruptXMLException buildCorruptXMLException(Throwable t) {
		return new CorruptXMLException("platform: " + this.name, t);
	}

	// ***** write
	void write(File platformsDirectory) {
		if (this.isCleanBranch()) {
			return;
		}
		Document document = XMLTools.newDocument();
		Node root = document.createElement("database-platform");
		document.appendChild(root);
		XMLTools.addSimpleTextNode(root, "name", this.name);
		XMLTools.addSimpleTextNode(root, "comment", (String) ClassTools.getFieldValue(this, "comment"), "");
		XMLTools.addSimpleTextNode(root, "runtime-platform-class", this.runtimePlatformClassName);
		XMLTools.addSimpleTextNode(root, "supports-native-sequencing", this.supportsNativeSequencing, false);
		XMLTools.addSimpleTextNode(root, "supports-native-returning", this.supportsNativeReturning, false);
		XMLTools.addSimpleTextNode(root, "supports-identity-clause", this.supportsIdentityClause, false);

		this.writeDatabaseTypeNodes(root.appendChild(document.createElement("database-types")));
		this.writeJDBCMappingNodes(root.appendChild(document.createElement("jdbc-mappings")));

		XMLTools.print(document, new File(platformsDirectory, this.shortFileName));
		this.markEntireBranchClean();
	}

	private void writeDatabaseTypeNodes(Node databaseTypesNode) {
		Document document = databaseTypesNode.getOwnerDocument();
		synchronized (this.databaseTypes) {
			for (Iterator stream = new TreeSet(this.databaseTypes).iterator(); stream.hasNext(); ) {
				Node databaseTypeNode = document.createElement("database-type");
				databaseTypesNode.appendChild(databaseTypeNode);
				((DatabaseType) stream.next()).write(databaseTypeNode);
			}
		}
	}

	private void writeJDBCMappingNodes(Node mappingsNode) {
		Document document = mappingsNode.getOwnerDocument();
		synchronized (this.jdbcTypeToDatabaseTypeMappings) {
			for (Iterator stream = new TreeSet(this.jdbcTypeToDatabaseTypeMappings).iterator(); stream.hasNext(); ) {
				Node mappingNode = document.createElement("jdbc-mapping");
				mappingsNode.appendChild(mappingNode);
				((JDBCTypeToDatabaseTypeMapping) stream.next()).write(mappingNode);
			}
		}
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
