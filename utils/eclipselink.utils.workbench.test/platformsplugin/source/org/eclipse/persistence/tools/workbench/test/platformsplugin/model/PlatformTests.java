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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * These aren't tests so much as they are documentation of what comes
 * back from metadata calls to the various databases (Oracle, DB2, Sybase, etc.).
 */
public abstract class PlatformTests extends TestCase {
	private DatabasePlatformRepository databasePlatformRepository;
	private Driver driver;
	protected Connection connection;
	protected static final String CR = System.getProperty("line.separator");


	protected PlatformTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.databasePlatformRepository = DatabasePlatformRepository.getDefault();

		ClassLoader classLoader = new URLClassLoader(this.classpathEntries());
		Class driverClass = Class.forName(this.driverClassName(), true, classLoader);
		this.driver = (Driver) driverClass.newInstance();

		this.connection = this.buildConnection();
	}

	protected abstract String driverClassName();

	/**
	 * Return the classpath entries required for the JDBC driver.
	 */
	protected URL[] classpathEntries() throws Exception {
		List urls = new ArrayList();
		this.addClasspathEntriesTo(urls);
		return (URL[]) urls.toArray(new URL[urls.size()]);
	}

	/**
	 * Add the URLs needed by a URL class loader to load the JDBC driver
	 * to the specified list.
	 */
	protected void addClasspathEntriesTo(List urls) throws Exception {
		String[] driverJARNames = this.driverJARNames();
		for (int i = 0; i < driverJARNames.length; i++) {
			urls.add(FileTools.resourceFile("/" + driverJARNames[i]).toURL());
		}
	}

	/**
	 * Return the names of the JARs needed to load the JDBC driver.
	 * The JARs must be located in a directory on the classpath.
	 * The JARs themselves should *not* be on the classpath.
	 */
	protected String[] driverJARNames() {
		return new String[0];
	}

	/**
	 * Build the driver directly; skip the DriverManager.
	 */
	protected Connection buildConnection() throws Exception {
		Properties props = new Properties();
		String userName = this.userName();
		if (userName != null) {
			props.put("user", userName);
		}
		String password = this.password();
		if (password != null) {
			props.put("password", password);
		}
		return this.driver.connect(this.connectionURL(), props);
	}

	protected abstract String connectionURL();

	protected String userName() {
		return this.defautlUserName();
	}

	protected String defautlUserName() {
		return System.getProperty("user.name");
	}

	protected String password() {
		return "password";
	}

	protected void tearDown() throws Exception {
		this.connection.close();
		TestTools.clear(this);
		super.tearDown();
	}

	/**
	 * build a table and verify what the metadata says about it afterwards
	 */
	public void testTableTypes() throws Exception {
		// dump all the type information for the current platform to the console
//		this.dumpDriverInformation(this.connection.getMetaData());
//		this.dumpMetaDataMap(this.buildMetaDataMap(this.connection.getMetaData().getTypeInfo(), "TYPE_NAME"));

		this.verifyVersionNumber();

		this.dropDatabaseObjects();
		this.createDatabaseObjects();

		Map metaDataMap = this.buildMetaDataMap(this.connection.getMetaData().getColumns(null, this.schemaPattern(), this.tableName(), null), "COLUMN_NAME");
		// dump all the information about the table to the console
//		this.dumpMetaDataMap(metaDataMap);
		this.verifyTable(metaDataMap);
		this.verifyDatabasePlatformRepository(metaDataMap);
	}

	/**
	 * Verify that we are running the test against the expected db server.
	 * If this assertion fails, the server version has changed; which means we are either
	 * running against the wrong server or we need to tweak the version number
	 * returned by #expectedVersionNumber().  ~bjv
	 */
	protected void verifyVersionNumber() throws Exception {
		String actualVersionNumber = this.connection.getMetaData().getDatabaseProductVersion();
		String expectedVersionNumber = this.expectedVersionNumber();
		String errorMessage = "Expected version number: " + expectedVersionNumber + " but the actual version number was: " + actualVersionNumber;
		assertTrue(errorMessage, actualVersionNumber.indexOf(expectedVersionNumber) != -1);
	}

	protected String expectedVersionNumber() {
		throw new UnsupportedOperationException();
	}

	protected String schemaPattern() {
		return this.userName().toUpperCase();
	}

	protected void dumpDriverInformation(DatabaseMetaData dbMetaData) throws Exception {
		System.out.println("URL: " + this.connectionURL());
		System.out.println("Database Product Name: " + dbMetaData.getDatabaseProductName());
		System.out.println("Database Product Version: " + dbMetaData.getDatabaseProductVersion());
		System.out.println("Driver Name: " + dbMetaData.getDriverName());
		System.out.println("Driver Version: " + dbMetaData.getDriverVersion());
		System.out.println();
	}

	protected String tableName() {
		return this.baseName() + "_TABLE";
	}

	protected String baseName() {
		return "FOO";
	}

	protected void dropDatabaseObjects() throws Exception {
		Statement stmt = this.connection.createStatement();
		try {
			stmt.executeUpdate(this.buildDropTableDDL());
		} catch (SQLException ex) {
//			ex.printStackTrace();	// dump to console, but otherwise ignore
		}
		stmt.close();
	}

	protected String buildDropTableDDL() {
		return "DROP TABLE " + this.tableName();
	}

	protected void createDatabaseObjects() throws Exception {
		Statement stmt = this.connection.createStatement();
		String createTableDDL = this.buildCreateTableDDL();
		// dump the DDL to the console
//		System.out.println(createTableDDL);
		stmt.executeUpdate(createTableDDL);
		stmt.close();
	}

	protected abstract void verifyTable(Map metaDataMap);

	protected void verifyColumnAttribute(Map metaDataMap, String columnName, String attributeName, int expected) {
		Map columnAttributes = (Map) metaDataMap.get(columnName);
		Number columnAttribute = (Number) columnAttributes.get(attributeName);	// Oracle returns BigDecimal
		assertEquals(columnName + ":" + attributeName, expected, columnAttribute.intValue());
	}

	protected void verifyColumnAttribute(Map metaDataMap, String columnName, String attributeName, Object expected) {
		Map columnAttributes = (Map) metaDataMap.get(columnName);
		Object columnAttribute = columnAttributes.get(attributeName);
		assertEquals(columnName + ":" + attributeName, expected, columnAttribute);
	}

	/**
	 * build and return a map of maps; the first key is the value in the specified column name,
	 * the second key is the metadata column name
	 * e.g. NUMBER_COL => (TABLE_NAME => FOO; COLUMN_SIZE => 10; etc.)
	 */
	protected Map buildMetaDataMap(ResultSet rs, String keyColumnName) throws Exception {
		ResultSetMetaData rsMetaData = rs.getMetaData();
//		this.dumpMetaData(rsMetaData);
		Map metaDataMap = new LinkedHashMap();		// maintain the map's order
		int columnCount = rsMetaData.getColumnCount();
		while (rs.next()) {
			Object keyColumnValue = null;
			Map row = new LinkedHashMap(columnCount);		// maintain the map's order
			for (int i = 1; i <= columnCount; i++) {
				String columnName = rsMetaData.getColumnName(i);
				Object columnValue = rs.getObject(i);
				row.put(columnName, columnValue);
				if (columnName.equals(keyColumnName)) {
					keyColumnValue = columnValue;
				}
			}
			metaDataMap.put(keyColumnValue, row);
		}
		rs.close();
		return metaDataMap;
	}

	/**
	 * dump the specified metadata map to the console
	 * @see #buildMetaDataMap(ResultSet, String)
	 */
	protected void dumpMetaDataMap(Map map) {
		DatabasePlatformRepository repository = new DatabasePlatformRepository("foo");
		for (Iterator rows = map.values().iterator(); rows.hasNext(); ) {
			Map row = (Map) rows.next();
			for (Iterator columns = row.keySet().iterator(); columns.hasNext(); ) {
				String columnName = (String) columns.next();
				System.out.print(columnName + ": " + row.get(columnName));
				if (columnName.equals("DATA_TYPE")) {
					int code = ((Number) row.get(columnName)).intValue();
					System.out.print(" - ");
					try {
						System.out.print(repository.getJDBCTypeRepository().jdbcTypeForCode(code).getName());
					} catch (IllegalArgumentException ex) {
						System.out.print("*** UNKNOWN JDBC TYPE ***");
					}
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	protected void dumpMetaData(ResultSetMetaData rsMetaData) throws Exception {
		System.out.println("Database Metadata Result Set Columns");
		System.out.println("====================================");
		int columnCount = rsMetaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			System.out.println("column: " + i);
			System.out.println("catalog name: " + rsMetaData.getCatalogName(i));
			System.out.println("schema name: " + rsMetaData.getSchemaName(i));
			System.out.println("table name: " + rsMetaData.getTableName(i));
			System.out.println("column name: " + rsMetaData.getColumnName(i));

			System.out.println("type: " + rsMetaData.getColumnType(i));
			System.out.println("type name: " + rsMetaData.getColumnTypeName(i));
			System.out.println("class name: " + rsMetaData.getColumnClassName(i));

			System.out.println("label: " + rsMetaData.getColumnLabel(i));
			System.out.println("display size: " + rsMetaData.getColumnDisplaySize(i));

			System.out.println("precision: " + rsMetaData.getPrecision(i));
			System.out.println("scale: " + rsMetaData.getScale(i));

			System.out.println("auto-increment: " + rsMetaData.isAutoIncrement(i));
			System.out.println("case-sensitive: " + rsMetaData.isCaseSensitive(i));
			System.out.println("currency: " + rsMetaData.isCurrency(i));
			System.out.println("nullable: " + rsMetaData.isNullable(i));
			System.out.println("searchable: " + rsMetaData.isSearchable(i));

			System.out.println("read-only: " + rsMetaData.isReadOnly(i));
			System.out.println("writable: " + rsMetaData.isWritable(i));
			System.out.println("definitely writable: " + rsMetaData.isDefinitelyWritable(i));

			System.out.println("signed: " + rsMetaData.isSigned(i));
			System.out.println();
		}
	}

	protected String buildCreateTableDDL() {
		StringBuffer sb = new StringBuffer(2000);
		sb.append("CREATE TABLE ");
		sb.append(this.tableName());
		sb.append(" (");
		sb.append(CR);
		this.appendColumnsToTableDDL(sb);
		sb.append(")");
		return sb.toString();
	}

	protected abstract void appendColumnsToTableDDL(StringBuffer sb);

	protected void verifyDatabasePlatformRepository(Map metaDataMap) {
		DatabasePlatform platform = this.databasePlatformRepository.platformNamed(this.platformName());
		Object catalog = null;
		Object schema = null;
		Object table = null;
		for (Iterator stream = metaDataMap.keySet().iterator(); stream.hasNext(); ) {
			Object columnName = stream.next();
			Map columnAttributes = (Map) metaDataMap.get(columnName);

			// make sure the catalog/schema/table remain the same
			if (catalog == null) catalog = columnAttributes.get("TABLE_CAT");
			if (schema == null) schema = columnAttributes.get("TABLE_SCHEM");
			if (table == null) table = columnAttributes.get("TABLE_NAME");
			assertEquals(catalog, columnAttributes.get("TABLE_CAT"));
			assertEquals(schema, columnAttributes.get("TABLE_SCHEM"));
			assertEquals(table, columnAttributes.get("TABLE_NAME"));

			String nativeTypeName =  (String) columnAttributes.get("TYPE_NAME");
			DatabaseType type = null;
			try {
				type = platform.databaseTypeNamed(nativeTypeName);
			} catch (IllegalArgumentException ex) {
				if (ex.getMessage().indexOf(nativeTypeName) == -1) {
					throw ex;
				}
				if (this.typeIsIgnorable(nativeTypeName)) {
					// System.out.println("ignored data type: " + this.platformName() + ":" + nativeTypeName);
					type = platform.defaultDatabaseType();
				} else {
					throw ex;
				}
			}
			assertNotNull(type);
			if (type.allowsSize()) {
				int size = ((Number) columnAttributes.get("COLUMN_SIZE")).intValue();
				assertTrue(size >= 0);
				if (type.requiresSize()) {
					assertTrue(size > 0);
				}
				if (type.allowsSubSize()) {
					Number ss = (Number) columnAttributes.get("DECIMAL_DIGITS");
					int subSize = (ss == null) ? 0 : ss.intValue();
					assertTrue(subSize >= 0);
				}
			}
			if (type.allowsNull()) {
				assertEquals("YES", columnAttributes.get("IS_NULLABLE"));
			}
		}
	}

	protected abstract String platformName();

	/**
	 * return whether we can ignore the specified datatype
	 * when trying to look it up in the repository;
	 * i.e. we will not find the datatype in the database platform
	 * repository because it is a "user-defined" datatype (e.g.
	 * FOO_TYPE) not a "system" datatype (e.g. VARCHAR2)
	 */
	protected boolean typeIsIgnorable(String nativeTypeName) {
		Collection adtNames = new ArrayList();
		this.addIgnorableTypeNamesTo(adtNames);
		return adtNames.contains(nativeTypeName);
	}

	protected void addIgnorableTypeNamesTo(Collection adtNames) {
		// none by default
	}

}
