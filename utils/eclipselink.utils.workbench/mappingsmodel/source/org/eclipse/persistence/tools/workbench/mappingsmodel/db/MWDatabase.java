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
package org.eclipse.persistence.tools.workbench.mappingsmodel.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProjectSubFileComponentContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalDatabaseFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;


public final class MWDatabase
	extends MWModel
	implements ProjectSubFileComponentContainer
{
	/** the database platform should never be null */
	private volatile DatabasePlatform databasePlatform;
		public static final String DATABASE_PLATFORM_PROPERTY = "databasePlatform" ;

	private Collection loginSpecs;
		public static final String LOGIN_SPECS_COLLECTION = "loginSpecs";

	private MWLoginSpecHandle deploymentLoginSpecHandle;
		public static final String DEPLOYMENT_LOGIN_SPEC_PROPERTY = "deploymentLoginSpec";
	private MWLoginSpecHandle developmentLoginSpecHandle;
		public static final String DEVELOPMENT_LOGIN_SPEC_PROPERTY = "developmentLoginSpec";
	
	private Collection tables;
		public static final String TABLES_COLLECTION = "tables";

	/**
	 * the "external" database that supplies the
	 * "external" tables used to build MWTables
	 */
	private volatile ExternalDatabase externalDatabase;

	/**
	 * transient - java.sql.Driver used to connect to the database;
	 * this field is always null when executing under jdev
	 */
	private volatile Driver driver;

	/**
	 * transient - java.sql.Connection used for collecting meta-data;
	 * this field is always null when executing under jdev
	 */
	private volatile Connection connection;
		// virtual property
		public static final String CONNECTED_PROPERTY = "connected";

	/**
	 * transient - org.eclipse.persistence.tools.schemaframework.SchemaManager
	 * used for generating tables on the database;
	 * this field is always null when executing under jdev
	 */
	private volatile SchemaManager schemaManager;


	/**
	 * these table names are read in by TopLink and are then
	 * used and managed by the IOManager;
	 * DO NOT use them for anything else  ~bjv
	 */
	private Collection tableNames;
		private static final String TABLE_NAMES_COLLECTION = "tableNames";


	/** this setting queried reflectively by the I/O Manager; so don't remove it */
	private static final String SUB_DIRECTORY_NAME = "tables";


	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWDatabase() {
		super();
	}

	public MWDatabase(MWRelationalProject project, DatabasePlatform databasePlatform) {
		super(project);
		this.databasePlatform = databasePlatform;
	}


	// ********** initialization **********

	/**
	 * initialize transient state
	 */
	public void initialize() {
		super.initialize();
		// the tables are not mapped directly
		this.tables = new Vector();
	}

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.loginSpecs = new Vector();
		// 'deploymentLoginSpec' and 'developmentLoginSpec'
		// are handled directly in #loginSpecRemoved(MWLoginSpec)
		this.deploymentLoginSpecHandle = new MWLoginSpecHandle(this, NodeReferenceScrubber.NULL_INSTANCE);
		this.developmentLoginSpecHandle = new MWLoginSpecHandle(this, NodeReferenceScrubber.NULL_INSTANCE);
		this.tableNames = new HashSet();
	}


	// ********** database platform **********

	public DatabasePlatform getDatabasePlatform() {
		return this.databasePlatform;
	}

	public void setDatabasePlatform(DatabasePlatform databasePlatform) {
		if (databasePlatform == null) {
			throw new NullPointerException();
		}
		Object old = this.databasePlatform;
		this.databasePlatform = databasePlatform;
		this.firePropertyChanged(DATABASE_PLATFORM_PROPERTY, old, databasePlatform);
		if (this.attributeValueHasChanged(old, databasePlatform)) {
			this.databasePlatformChanged();
		}
	}

	/**
	 * cascade to the database fields so they can update their types
	 */
	private void databasePlatformChanged() {
		synchronized (this.tables) {
			for (Iterator stream = this.tables.iterator(); stream.hasNext(); ) {
				((MWTable) stream.next()).databasePlatformChanged();
			}
		}
	}


	// ********** login specs **********

	public Iterator loginSpecs() {
		return new CloneIterator(this.loginSpecs) {
			protected void remove(Object current) {
				MWDatabase.this.removeLoginSpec((MWLoginSpec) current);
			}
		};
	}

	public int loginSpecsSize() {
		return this.loginSpecs.size();
	}	

	public MWLoginSpec addLoginSpec(String loginSpecName) {
		this.checkLoginSpecName(loginSpecName);
		return this.addLoginSpec(new MWLoginSpec(this, loginSpecName));
	}

	private MWLoginSpec addLoginSpec(MWLoginSpec loginSpec) {
		this.addItemToCollection(loginSpec, this.loginSpecs, LOGIN_SPECS_COLLECTION);
		if (this.loginSpecs.size() == 1) {
			this.setDeploymentLoginSpec(loginSpec);
			this.setDevelopmentLoginSpec(loginSpec);
		}
		return loginSpec;
	}
	
	public void removeLoginSpec(MWLoginSpec loginSpec) {
		if (this.removeItemFromCollection(loginSpec, this.loginSpecs, LOGIN_SPECS_COLLECTION)) {
			this.loginSpecRemoved(loginSpec);
		}
	}

	public boolean containsLoginSpecNamed(String loginSpecName) {
		return this.loginSpecNamed(loginSpecName) != null;
	}

	public MWLoginSpec loginSpecNamed(String loginSpecName) {
		synchronized (this.loginSpecs) {
			for (Iterator stream = this.loginSpecs.iterator(); stream.hasNext(); ) {
				MWLoginSpec spec = (MWLoginSpec) stream.next();
				if (spec.getName().equals(loginSpecName)) {
					return spec;
				}
			}
		}
		return null;
	}

	public Iterator loginSpecNames() {
		return new TransformationIterator(this.loginSpecs()) {
			protected Object transform(Object next) {
				return ((MWLoginSpec) next).getName();
			}
		};
	}


	// ********** deployment login spec **********

	public MWLoginSpec getDeploymentLoginSpec() {
		return this.deploymentLoginSpecHandle.getLoginSpec();
	}

	public void setDeploymentLoginSpec(MWLoginSpec loginSpec) {
		Object old = this.deploymentLoginSpecHandle.getLoginSpec();
		this.deploymentLoginSpecHandle.setLoginSpec(loginSpec);
		this.firePropertyChanged(DEPLOYMENT_LOGIN_SPEC_PROPERTY, old, loginSpec);
	}


	// ********** development login spec **********

	public MWLoginSpec getDevelopmentLoginSpec() {
		return this.developmentLoginSpecHandle.getLoginSpec();
	}

	public void setDevelopmentLoginSpec(MWLoginSpec loginSpec) {
		Object old = this.developmentLoginSpecHandle.getLoginSpec();
		this.developmentLoginSpecHandle.setLoginSpec(loginSpec);
		this.firePropertyChanged(DEVELOPMENT_LOGIN_SPEC_PROPERTY, old, loginSpec);
	}


	// ********** tables **********

	public Iterator tables() {
		return new CloneIterator(this.tables) {
			protected void remove(Object current) {
				MWDatabase.this.removeTable((MWTable) current);
			}
		};
	}

	public int tablesSize() {
		return this.tables.size();
	}

	public MWTable addTable(String shortName) {
		return this.addTable(null, shortName);	
	}

	public MWTable addTable(String schema, String shortName) {
		return this.addTable(null, schema, shortName);
	}

	public MWTable addTable(String catalog, String schema, String shortName) {
		this.checkTableName(catalog, schema, shortName, null);
		return this.addTable(new MWTable(this, catalog, schema, shortName));
	}

	public MWTable addTableWithFullyQualifiedName(String fullyQualifiedName) {
   		String[] parsedName= (String[]) CollectionTools.removeAllOccurrences(fullyQualifiedName.split("\\."), "");
   		if (parsedName.length == 3) {
   			return addTable(parsedName[0], parsedName[1], parsedName[2]);
   		}
   		else if (parsedName.length == 2) {
   			return addTable(parsedName[0], parsedName[1]);
   		}
   		else {
  			return addTable(parsedName[0]);
  		}
   	}

	private MWTable addTable(MWTable table) {
		this.addItemToCollection(table, this.tables, TABLES_COLLECTION);
		return table;
	}

	public void removeTable(MWTable table) {
		this.removeNodeFromCollection(table, this.tables, TABLES_COLLECTION);
	}

	public boolean containsTableNamed(String catalog, String schema, String shortName) {
		return this.tableNamed(catalog, schema, shortName) != null;
	}

	public MWTable tableNamed(String catalog, String schema, String shortName) {
		synchronized (this.tables) {
			for (Iterator stream = this.tables.iterator(); stream.hasNext(); ) {
				MWTable table = (MWTable) stream.next();
				if (table.nameMatches(catalog, schema, shortName)) {
					return table;
				}
			}
		}
		return null;
	}

	public boolean containsTableNamedIgnoreCase(String catalog, String schema, String shortName) {
		return this.tableNamedIgnoreCase(catalog, schema, shortName) != null;
	}

	public MWTable tableNamedIgnoreCase(String catalog, String schema, String shortName) {
		synchronized (this.tables) {
			for (Iterator stream = this.tables.iterator(); stream.hasNext(); ) {
				MWTable table = (MWTable) stream.next();
				if (table.nameMatchesIgnoreCase(catalog, schema, shortName)) {
					return table;
				}
			}
		}
		return null;
	}

	public boolean containsTableNamed(String qualifiedName) {
		return this.tableNamed(qualifiedName) != null;
	}

	public MWTable tableNamed(String qualifiedName) {
		synchronized (this.tables) {
			for (Iterator stream = this.tables.iterator(); stream.hasNext(); ) {
				MWTable table = (MWTable) stream.next();
				if (table.qualifiedName().equals(qualifiedName)) {
					return table;
				}
			}
		}
		return null;
	}

	/**
	 * used to prevent adding a duplicate table
	 */
	public Iterator tableNames() {
		return new TransformationIterator(this.tables()) {
			protected Object transform(Object next) {
				return ((MWTable) next).getName();
			}
		};
	}

	public MWColumn columnNamed(String qualifiedName) {
		MWTable table = this.tableNamed(MWColumn.parseTableNameFromQualifiedName(qualifiedName));
		if (table == null) {
			return null;
		}
		return table.columnNamed(MWColumn.parseColumnNameFromQualifiedName(qualifiedName));
	}


	// ********** external database **********

	/**
	 * PRIVATE - no one should need direct access to the external database
	 */
	private ExternalDatabase getExternalDatabase() {
		if (this.externalDatabase == null) {
			this.externalDatabase = this.buildExternalDatabase();
		}
		return this.externalDatabase;
	}
	
	private ExternalDatabase buildExternalDatabase() {
		// when executing under jdev, the connection will be null
		return this.externalDatabaseFactory().buildDatabase(this.connection);
	}
	

	// ********** connection **********

	/**
	 * this method is not called when executing under jdev
	 */
	public boolean isConnected() {
		return this.connection != null;
	}


	// ********** schema manager **********

	/**
	 * call #isConnected()/#login() before calling this method or you
	 * might get an IllegalStateException;
	 * this method is not called when executing under jdev
	 */
	private SchemaManager getSchemaManager() {
		if (this.schemaManager == null) {
			throw new IllegalStateException("not connected");
		}
		return this.schemaManager;
	}


	// ********** queries **********

	/**
	 * the external database factory is supplied by client code
	 */
	private ExternalDatabaseFactory externalDatabaseFactory() {
		return this.getProject().getSPIManager().getExternalDatabaseFactory();
	}
	
	boolean supportsIdentityClause() {
		return this.databasePlatform.supportsIdentityClause();
	}


	// ********** miscellaneous behavior **********

	/**
	 * 'connected' is a virtual property
	 */
	protected void addTransientAspectNamesTo(Set transientAspectNames) {
		super.addTransientAspectNamesTo(transientAspectNames);
		transientAspectNames.add(CONNECTED_PROPERTY);
	}

	/**
	 * disallow duplicate login info names
	 */
	void checkLoginSpecName(String loginSpecName) {
		if ((loginSpecName == null) || (loginSpecName.length() == 0)) {
			throw new IllegalArgumentException();
		}
		if (this.containsLoginSpecNamed(loginSpecName)) {
			throw new IllegalArgumentException("duplicate login spec name: " + loginSpecName);
		}
	}

	/**
	 * disallow duplicate table names
	 */
	void checkTableName(String catalog, String schema, String shortName, MWTable table) {
		this.checkTableNameQualifier(catalog);
		this.checkTableNameQualifier(schema);
		if ((shortName == null) || (shortName.length() == 0)) {
			throw new IllegalArgumentException();
		}
		MWTable match = this.tableNamed(catalog, schema, shortName);
		if (match != null) {
			throw new IllegalArgumentException("duplicate table name: " + match.qualifiedName());
		}
		MWTable matchIgnoreCase = this.tableNamedIgnoreCase(catalog, schema, shortName);
		if ((matchIgnoreCase != null) && (matchIgnoreCase != table)) {
			throw new IllegalArgumentException("duplicate table name: " + matchIgnoreCase.qualifiedName());
		}
	}

	/**
	 * table qualifiers must be null or non-empty
	 */
	private void checkTableNameQualifier(String qualifier) {
		if (qualifier == null) {
			return;
		}
		if (qualifier.length() == 0) {
			throw new IllegalArgumentException();
		}
	}


	// ********** model synchronization **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.loginSpecs) { children.addAll(this.loginSpecs); }
		children.add(this.deploymentLoginSpecHandle);
		children.add(this.developmentLoginSpecHandle);
		synchronized (this.tables) { children.addAll(this.tables); }
	}

	private void loginSpecRemoved(MWLoginSpec loginSpec) {
		if (this.getDeploymentLoginSpec() == loginSpec) {
			this.setDeploymentLoginSpec(null);
		}
		if (this.getDevelopmentLoginSpec() == loginSpec) {
			this.setDevelopmentLoginSpec(null);
		}
	}

	/**
	 * performance tuning: override this method and assume
	 * the database's descendants have NO references (handles)
	 * to any models other than other descendants of the database
	 */
	public void nodeRemoved(Node node) {
		if (node.isDescendantOf(this)) {
			super.nodeRemoved(node);
		}
	}

	/**
	 * performance tuning: override this method and assume
	 * the database's descendants have NO references (handles)
	 * to any models other than other descendants of the database
	 */
	public void nodeRenamed(Node node) {
		if (node.isDescendantOf(this)) {
			super.nodeRenamed(node);
			// we handle a renamed table directly in #tableRenamed()
		}
	}

	void tableRenamed() {
		// if a table has been renamed, we need to fire an "internal"
		// change event so the database is marked dirty
		this.fireCollectionChanged(TABLE_NAMES_COLLECTION);
	}

	/**
	 * performance tuning: ignore this method - assume there are no
	 * references to mappings in the database or its descendants
	 */
	public void mappingReplaced(MWMapping oldMapping, MWMapping newMapping) {
		// do nothing
	}

	/**
	 * performance tuning: ignore this method - assume there are no
	 * references to descriptors in the database or its descendants
	 */
	public void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		// do nothing
	}

	/**
	 * performance tuning: ignore this method - assume there are no
	 * references to mappings in the database or its descendants
	 */
	public void descriptorUnmapped(Collection mappings) {
		// do nothing
	}
	

	// ********** login/logout **********

	/**
	 * you must log in before accessing the connection or
	 * schema manager;
	 * we instantiate and connect the JDBC Driver manually, ignoring
	 * the stupid JDBC DriverManager;
	 * this method is not called when executing under jdev
	 */
	public void login() throws SQLException, ClassNotFoundException {
		if (this.isConnected()) {
			throw new IllegalStateException("already connected");
		}
		MWLoginSpec loginSpec = this.getDevelopmentLoginSpec();
		if (loginSpec == null) {
			throw new IllegalStateException("missing development login spec");
		}

		try {
			this.driver = loginSpec.buildDriver();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}

		String url = loginSpec.getURL();
		if ((url == null) || (url.length() == 0)) {
			throw new IllegalStateException("missing database URL");
		}

		// store the user name and password in a dictionary
		Properties props = new Properties();
		String userName = loginSpec.getUserName();
		if (userName != null) {
			props.put("user", userName);
		}
		String password = loginSpec.getPassword();
		if (password != null) {
			props.put("password", password);
		}
				
		this.connection = this.driver.connect(url, props);
		
		// once we are connected we can build the schema manager
		this.schemaManager = this.buildSchemaManager();
		this.firePropertyChanged(CONNECTED_PROPERTY, false, true);
	}

	/**
	 * disconnect from the database and clear out any state that relies
	 * on the connection;
	 * this method is not called when executing under jdev
	 */
	public void logout() throws SQLException {
		if ( ! this.isConnected()) {
			throw new IllegalStateException("not connected");
		}
		this.connection.close();
		this.schemaManager = null;
		this.connection = null;
		this.driver = null;
		this.externalDatabase = null;
		this.firePropertyChanged(CONNECTED_PROPERTY, true, false);
	}

	private SchemaManager buildSchemaManager() {
		return new SchemaManager(this.buildRuntimeDatabaseSession());
	}

	/**
	 * this db session will use the *development* login spec;
	 * this method is not called when executing under jdev
	 */
	private DatabaseSession buildRuntimeDatabaseSession() {
		DatabaseSession session = this.buildRuntimeProject().createDatabaseSession();
		session.dontLogMessages();
		session.login();
		return session;
	}
   	
	/**
	 * this project will use the *development* login spec
	 * this method is not called when executing under jdev
	 */
	private Project buildRuntimeProject() {
		return new Project(this.getDevelopmentLoginSpec().buildDevelopmentRuntimeDatabaseLogin());
	}

	/**
	 * build a connector that will use the database's connection;
	 * this method is not called when executing under jdev
	 */
	Connector buildRuntimeConnector() {
		return new LocalConnectorAdapter(this.connection);
	}


	// ********** table importing/refreshing **********

	/**
	 * Return the "catalog" names from the current database.
	 * @see java.sql.DatabaseMetaData#getCatalogs()
	 */
	public Iterator catalogNames() {
		return new ArrayIterator(this.getExternalDatabase().getCatalogNames());
	}

	/**
	 * Return the "schema" names from the current database.
	 * @see java.sql.DatabaseMetaData#getSchemas()
	 */
	public Iterator schemaNames() {
		return new ArrayIterator(this.getExternalDatabase().getSchemaNames());
	}

	/**
	 * Return the "table type" names from the current database.
	 * @see java.sql.DatabaseMetaData#getTableTypes()
	 */
	public Iterator tableTypeNames() {
		return new ArrayIterator(this.getExternalDatabase().getTableTypeNames());
	}

	/**
	 * return the "external" table descriptions corresponding to the specified
	 * search criteria; these can then be used to import and/or refresh tables
	 * @see #externalTableDescriptions()
	 * @see #importQualifiedTablesFor(java.util.Collection)
	 * @see #importUnqualifiedTablesFor(java.util.Collection)
	 * @see #refreshQualifiedTablesFor(java.util.Collection)
	 * @see java.sql.DatabaseMetaData#getTables(String, String, String, String[])
	 */
	public Iterator externalTableDescriptions(String catalog, String schemaPattern, String tableNamePattern, String[] types) {
		return new ArrayIterator(this.getExternalDatabase().getTableDescriptions(catalog, schemaPattern, tableNamePattern, types));
	}

	/**
	 * return the all the "external" table descriptions;
	 * these can then be used to import and/or refresh tables
	 * @see #externalTableDescriptions(String, String, String, String[])
	 * @see #importQualifiedTablesFor(java.util.Collection)
	 * @see #importUnqualifiedTablesFor(java.util.Collection)
	 * @see #refreshQualifiedTablesFor(java.util.Collection)
	 */
	public Iterator externalTableDescriptions() {
		return new ArrayIterator(this.getExternalDatabase().getTableDescriptions());
	}

	/**
	 * import the tables corresponding to the specified
	 * "external" table descriptions, using their fully-qualified names;
	 * this is a two-step process: all the fields must be in place before
	 * we can build or refresh the references
	 */
	public void importQualifiedTablesFor(Collection externalTableDescriptions) {	
		for (Iterator stream = externalTableDescriptions.iterator(); stream.hasNext(); ) {
			ExternalTableDescription externalTableDescription = (ExternalTableDescription) stream.next();
			this.qualifiedTableFor(externalTableDescription).refreshColumns(externalTableDescription.getTable());
		}
		for (Iterator stream = externalTableDescriptions.iterator(); stream.hasNext(); ) {
			ExternalTableDescription externalTableDescription = (ExternalTableDescription) stream.next();
			this.qualifiedTableFor(externalTableDescription).refreshReferences(externalTableDescription.getTable());
		}
	}

	/**
	 * refresh the tables corresponding to the specified "external" table descriptions, using their fully
	 * qualified names; this is a two-step process: all the fields must be in place before
	 * we can refresh the references;
	 * we want different behavior here because we don't want to 
	 * create them if they don't exist and we must handle unqualified tables
	 */
	public void refreshQualifiedTablesFor(Collection externalTableDescriptions) {
		for (Iterator stream = externalTableDescriptions.iterator(); stream.hasNext(); ) {
			ExternalTableDescription externalTableDescription = (ExternalTableDescription) stream.next();
			MWTable tableToRefresh = this.tableNamed(externalTableDescription.getQualifiedName());
			if (tableToRefresh == null) {
				// the table's name may be unqualified
				tableToRefresh = this.tableNamed(externalTableDescription.getName());
			} 
			if (tableToRefresh != null) {
				tableToRefresh.refreshColumns(externalTableDescription.getTable());
			}
		}
		for (Iterator stream = externalTableDescriptions.iterator(); stream.hasNext(); ) {
			ExternalTableDescription externalTableDescription = (ExternalTableDescription) stream.next();
			MWTable tableToRefresh = this.tableNamed(externalTableDescription.getQualifiedName());
			if (tableToRefresh == null) {
				// the table's name may be unqualified
				tableToRefresh = this.tableNamed(externalTableDescription.getName());
			} 
			if (tableToRefresh != null) {
				tableToRefresh.refreshReferences(externalTableDescription.getTable());
			}
		}
	}

	/**
	 * return the table corresponding to the specified "external" table description,
	 * creating it if necessary; use the table's fully-qualified name
	 */
	private MWTable qualifiedTableFor(ExternalTableDescription externalTableDescription) {
		return this.tableNamedForImport(externalTableDescription.getCatalogName(), externalTableDescription.getSchemaName(), externalTableDescription.getName());
	}
	
	/**
	 * return the requested table, creating it if necessary
	 */
	private MWTable tableNamedForImport(String catalog, String schema, String shortName) {
		MWTable table = this.tableNamed(catalog, schema, shortName);
		if (table == null) {
			table = this.addTable(catalog, schema, shortName);
		}
		return table;
	}

	/**
	 * import and/or refresh the tables corresponding to the specified
	 * "external" table descriptions, using their "short" names;
	 * this is a two-step process: all the fields must be in place before
	 * we can build or refresh the references
	 */
	public void importUnqualifiedTablesFor(Collection externalTableDescriptions) {
		for (Iterator stream = externalTableDescriptions.iterator(); stream.hasNext(); ) {
			ExternalTableDescription externalTableDescription = (ExternalTableDescription) stream.next();
			this.unqualifiedTableFor(externalTableDescription).refreshColumns(externalTableDescription.getTable());
		}
		for (Iterator stream = externalTableDescriptions.iterator(); stream.hasNext(); ) {
			ExternalTableDescription externalTableDescription = (ExternalTableDescription) stream.next();
			this.unqualifiedTableFor(externalTableDescription).refreshReferences(externalTableDescription.getTable());
		}
	}

	/**
	 * return the table corresponding to the specified "external" table description,
	 * creating it if necessary; use the table's "short" name
	 */
	private MWTable unqualifiedTableFor(ExternalTableDescription externalTableDescription) {
		return this.tableNamedForImport(null, null, externalTableDescription.getName());
	}


	// ********** runtime conversion **********

	private String runtimePlatformClassName() {
		return this.getDatabasePlatform().getRuntimePlatformClassName();
	}

	public DatabaseLogin buildDeploymentRuntimeDatabaseLogin() {
		MWLoginSpec deploySpec = this.getDeploymentLoginSpec();
		if (deploySpec == null) {
			DatabaseLogin login = new DatabaseLogin();
			login.setPlatformClassName(this.runtimePlatformClassName());
			return login;
		}
		return deploySpec.buildDeploymentRuntimeDatabaseLogin();
	}

	public Iterator runtimeTableDefinitions() {
		return new TransformationIterator(this.tables()) {
			protected Object transform(Object next) {
				return ((MWTable) next).buildRuntimeTableDefinition();
			}
		};
	}


	// ********** table generation **********

	/**
	 * generate all the database's tables on the current
	 * database
	 */
	public void generateTables() {
		Collection tablesCopy;
		synchronized (this.tables) {
			tablesCopy = new ArrayList(this.tables);
		}
		this.generateTables(tablesCopy);
	}

	public void generateTables(Collection selectedTables) throws EclipseLinkException {
		this.getSchemaManager().outputDDLToDatabase();
		this.buildTables(selectedTables);
	}

	public String ddlFor(Collection selectedTables) {
		Writer writer = new StringWriter(2000);
		this.writeDDLOn(selectedTables, writer);
		return writer.toString();
	}

	/**
	 * write the DDL to generate all the database's tables on
	 * the specified writer
	 */
	public void writeDDLOn(Writer writer) {
		Collection tablesCopy;
		synchronized (this.tables) {
			tablesCopy = new ArrayList(this.tables);
		}
		this.writeDDLOn(tablesCopy, writer);
	}

	/**
	 * write the DDL to generate the specified tables on
	 * the specified writer
	 */
	public void writeDDLOn(Collection selectedTables, Writer writer) {
		this.getSchemaManager().outputDDLToWriter(writer);
		this.buildTables(selectedTables);
	}

	private void buildTables(Collection selectedTables) throws EclipseLinkException {
		Collection runtimeTableDefs = new ArrayList(selectedTables.size());
		for (Iterator stream = selectedTables.iterator(); stream.hasNext(); ) {
			runtimeTableDefs.add(((MWTable) stream.next()).buildRuntimeTableDefinition());
		}
		SchemaManager sm = this.getSchemaManager();
		for (Iterator stream = runtimeTableDefs.iterator(); stream.hasNext(); ) {
			sm.replaceObject((TableDefinition) stream.next());
		}
		for (Iterator stream = runtimeTableDefs.iterator(); stream.hasNext(); ) {
			sm.createConstraints((TableDefinition) stream.next());
		}
	}


	// ********** printing and displaying **********

	public void toString(StringBuffer sb) {
		sb.append(this.getDatabasePlatform().getName());
		sb.append(" : ");
		sb.append(this.tables.size());
		sb.append(" tables");
	}

	public String displayString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Database (");
		sb.append(this.getDatabasePlatform().getName());
		sb.append(")");
		return sb.toString();
	}


	// ********** SubComponentContainer implementation **********

	public Iterator projectSubFileComponents() {
		return this.tables();
	}

	public void setProjectSubFileComponents(Collection subComponents) {
		this.tables = subComponents;
	}

	public Iterator originalProjectSubFileComponentNames() {
		return this.tableNames.iterator();
	}

	public void setOriginalProjectSubFileComponentNames(Collection originalSubComponentNames) {
		this.tableNames = originalSubComponentNames;
	}

	public boolean hasChangedMainProjectSaveFile() {
		if (this.isDirty()) {
			// the database itself is dirty
			return true;
		}
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			if (this.childHasChangedTheProjectSaveFile(stream.next())) {
				return true;
			}
		}
		// the tables might be dirty
		return false;
	}

	/**
	 * return whether the specified child of the database is dirty AND
	 * is written to the .mwp file
	 */
	private boolean childHasChangedTheProjectSaveFile(Object child) {
		if (this.tables.contains(child)) {
			// tables are written to separate files
			return false;
		}
		// the child is NOT a table,
		// so all of its state is written to the .mwp file
		return ((Node) child).isDirtyBranch();
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor(){
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWDatabase.class);

		descriptor.addDirectMapping("databasePlatformName", "getDatabasePlatformNameForTopLink", "setDatabasePlatformNameForTopLink", "platform-name/text()");

		XMLCompositeCollectionMapping loginSpecsMapping = new XMLCompositeCollectionMapping();
		loginSpecsMapping.setAttributeName("loginSpecs");
		loginSpecsMapping.setGetMethodName("getLoginSpecsForTopLink");
		loginSpecsMapping.setSetMethodName("setLoginSpecsForTopLink");
		loginSpecsMapping.setReferenceClass(MWLoginSpec.class);
		loginSpecsMapping.setXPath("login-infos/login-info");	// TODO rename in next release
		descriptor.addMapping(loginSpecsMapping);

		XMLCompositeObjectMapping deploymentLoginSpecMapping = new XMLCompositeObjectMapping();
		deploymentLoginSpecMapping.setAttributeName("deploymentLoginSpecHandle");
		deploymentLoginSpecMapping.setGetMethodName("getDeploymentLoginSpecHandleForTopLink");
		deploymentLoginSpecMapping.setSetMethodName("setDeploymentLoginSpecHandleForTopLink");
		deploymentLoginSpecMapping.setReferenceClass(MWLoginSpecHandle.class);
		deploymentLoginSpecMapping.setXPath("deployment-login-spec-handle");
		descriptor.addMapping(deploymentLoginSpecMapping);

		XMLCompositeObjectMapping developmentLoginSpecMapping = new XMLCompositeObjectMapping();
		developmentLoginSpecMapping.setAttributeName("developmentLoginSpecHandle");
		developmentLoginSpecMapping.setGetMethodName("getDevelopmentLoginSpecHandleForTopLink");
		developmentLoginSpecMapping.setSetMethodName("setDevelopmentLoginSpecHandleForTopLink");
		developmentLoginSpecMapping.setReferenceClass(MWLoginSpecHandle.class);
		developmentLoginSpecMapping.setXPath("development-login-spec-handle");
		descriptor.addMapping(developmentLoginSpecMapping);

		XMLCompositeDirectCollectionMapping tableNamesMapping = new XMLCompositeDirectCollectionMapping();
		tableNamesMapping.setAttributeName("tableNames");
		tableNamesMapping.setGetMethodName("getTableNamesForTopLink");
		tableNamesMapping.setSetMethodName("setTableNamesForTopLink");
		tableNamesMapping.useCollectionClass(HashSet.class);
		tableNamesMapping.setXPath("table-names/name/text()");
		descriptor.addMapping(tableNamesMapping);

		return descriptor;
	}

	private String getDatabasePlatformNameForTopLink() {
		return this.databasePlatform.getName();
	}
	private void setDatabasePlatformNameForTopLink(String databasePlatformName) {
		// TODO fetch the repository from an "environment" setting?
		this.databasePlatform = DatabasePlatformRepository.getDefault().platformNamed(databasePlatformName);
	}

	/**
	 * sort the login specs for TopLink
	 */
	private Collection getLoginSpecsForTopLink() {
		synchronized (this.loginSpecs) {
			return new TreeSet(this.loginSpecs);
		}
	}
	private void setLoginSpecsForTopLink(Collection loginSpecs) {
		this.loginSpecs = loginSpecs;
	}

	/**
	 * check for null
	 */
	private MWLoginSpecHandle getDeploymentLoginSpecHandleForTopLink() {
		return (this.deploymentLoginSpecHandle.getLoginSpec() == null) ? null : this.deploymentLoginSpecHandle;
	}
	private void setDeploymentLoginSpecHandleForTopLink(MWLoginSpecHandle deploymentLoginSpecHandle) {
		NodeReferenceScrubber scrubber = NodeReferenceScrubber.NULL_INSTANCE;
		this.deploymentLoginSpecHandle = ((deploymentLoginSpecHandle == null) ? new MWLoginSpecHandle(this, scrubber) : deploymentLoginSpecHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWLoginSpecHandle getDevelopmentLoginSpecHandleForTopLink() {
		return (this.developmentLoginSpecHandle.getLoginSpec() == null) ? null : this.developmentLoginSpecHandle;
	}
	private void setDevelopmentLoginSpecHandleForTopLink(MWLoginSpecHandle developmentLoginSpecHandle) {
		NodeReferenceScrubber scrubber = NodeReferenceScrubber.NULL_INSTANCE;
		this.developmentLoginSpecHandle = ((developmentLoginSpecHandle == null) ? new MWLoginSpecHandle(this, scrubber) : developmentLoginSpecHandle.setScrubber(scrubber));
	}

	/**
	 * sort the table names for TopLink
	 */
	private Collection getTableNamesForTopLink() {
		List names = new ArrayList(this.tables.size());
		CollectionTools.addAll(names, this.tableNames());
		return CollectionTools.sort(names, Collator.getInstance());
	}
	/**
	 * TopLink sets this value, which is then used by the
	 * ProjectIOManager to read in the actual tables
	 */
	private void setTableNamesForTopLink(Collection tableNames) {
		this.tableNames = tableNames;
	}

	// ********** inner classes **********

	/**
	 * Adapt the database to the TopLink run-time Connector interface.
	 */
	private static class LocalConnectorAdapter implements Connector {
		private Connection connection;
		LocalConnectorAdapter(Connection connection) {
			super();
			this.connection = connection;
		}
		/** this is the only method of note */
		public Connection connect(Properties properties, Session session) {
			return this.connection;
		}
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException ex) {
				throw new InternalError();
			}
		}
		public String getConnectionDetails() {
			return "MWDatabase.LocalConnectorAdapter";
		}
		public void toString(PrintWriter writer) {
			writer.print(this.getConnectionDetails());
		}
	}

}
