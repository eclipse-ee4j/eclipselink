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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import java.net.URISyntaxException;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


/**
 * factory for various databases
 */
public class TestDatabases {


	// ********** common settings **********

	private static MWRelationalProject project() {
		return new MWRelationalProject("test", MappingsModelTestTools.buildSPIManager(), null);
	}

	public static String userName() {
		return System.getProperty("user.name");
	}

	public static String password() {
		return "password";
	}

	private static void addDriverClasspathEntriesTo(String[] driverJARNames, MWLoginSpec loginSpec) {
		for (int i = 0; i < driverJARNames.length; i++) {
			try {
				loginSpec.addDriverClasspathEntry(FileTools.resourceFile("/" + driverJARNames[i]).getPath());
			} catch (URISyntaxException ex) {
				throw new RuntimeException(ex);
			} catch (NullPointerException ex) {
				continue;
			}
		}
	}


	// ********** Oracle **********

	public static MWDatabase oracleDatabase() {
		return oracleDatabase(project());
	}

	public static MWDatabase oracleDatabase(MWRelationalProject project) {
		MWDatabase database = project.getDatabase();
		configureOracleDatabase(database);
		return database;
	}

	public static void configureOracleDatabase(MWDatabase database) {
		database.setDatabasePlatform(oraclePlatform());
		MWLoginSpec loginSpec = oracleLoginSpec(database);
		database.setDevelopmentLoginSpec(loginSpec);
		database.setDeploymentLoginSpec(loginSpec);
	}

	public static DatabasePlatform oraclePlatform() {
		return DatabasePlatformRepository.getDefault().platformNamed("Oracle");
	}

	public static MWLoginSpec oracleLoginSpec(MWDatabase database) {
		MWLoginSpec loginSpec = database.addLoginSpec("Oracle");
		loginSpec.setDriverClassName(oracleDriverClassName());
		loginSpec.setUserName(userName());
		loginSpec.setPassword(password());
		loginSpec.setSavePassword(true);
		loginSpec.setURL(oracleServerURL());
		addDriverClasspathEntriesTo(oracleDriverClasspathEntries(), loginSpec);
		return loginSpec;
	}

	public static String oracleDriverClassName() {
		return "oracle.jdbc.OracleDriver";
	}

	public static String oracleServerURL() {
		return oracleServerURL(oracleServerName());
	}

	private static String oracleServerURL(String serverName) {
		return "jdbc:oracle:thin:@" + serverName + ":1521:" + oracleInstanceName();
	}

	/**
	 * default to the most recent version
	 */
	public static String oracleServerName() {
		return oracle10gR2ServerName();
	}

	public static String oracle10gR2ServerName() {
		return "tlsvrdb3.ca.oracle.com";
	}

	public static String oracle10gServerName() {
		return "tlsvrdb5.ca.oracle.com";
	}

	public static String oracle9iServerName() {
		return "tlsvrdb1.ca.oracle.com";
	}

	public static String oracle8iServerName() {
		return "tlsvrdb4.ca.oracle.com";
	}

	public static String oracleInstanceName() {
		return "TOPLINK";
	}

	public static String[] oracleDriverClasspathEntries() {
		return new String[] {"OracleThinJDBC_jdk14_10.2.0.1.0.jar"};
	}


	// ********** DB2 **********

	public static MWDatabase db2Database() {
		return db2Database(project());
	}

	public static MWDatabase db2Database(MWRelationalProject project) {
		MWDatabase database = project.getDatabase();
		configureDB2Database(database);
		return database;
	}

	public static void configureDB2Database(MWDatabase database) {
		database.setDatabasePlatform(db2Platform());
		MWLoginSpec loginSpec = db2LoginSpec(database);
		database.setDevelopmentLoginSpec(loginSpec);
		database.setDeploymentLoginSpec(loginSpec);
	}

	public static DatabasePlatform db2Platform() {
		return DatabasePlatformRepository.getDefault().platformNamed("IBM DB2");
	}

	public static MWLoginSpec db2LoginSpec(MWDatabase database) {
		MWLoginSpec loginSpec = database.addLoginSpec("DB2");
		loginSpec.setDriverClassName(db2DriverClassName());
		loginSpec.setUserName(userName());
		loginSpec.setPassword(password());
		loginSpec.setSavePassword(true);
		loginSpec.setURL(db2ServerURL());
		addDriverClasspathEntriesTo(db2DriverClasspathEntries(), loginSpec);
		return loginSpec;
	}

	public static String db2DriverClassName() {
		return "COM.ibm.db2.jdbc.net.DB2Driver";
	}

	public static String db2ServerURL() {
		return "jdbc:db2://" + db2ServerName() + "/" + db2DatabaseName();
	}

	public static String db2ServerName() {
		return "tlsvrdb2.ca.oracle.com";
	}

	public static String db2DatabaseName() {
		return "TOPLINK";
	}

	public static String[] db2DriverClasspathEntries() {
		return new String[] {"db2java_8.1.zip", "db2jcc_8.1.jar"};
	}


	// ********** Sybase **********

	public static MWDatabase sybaseDatabase() {
		return sybaseDatabase(project());
	}

	public static MWDatabase sybaseDatabase(MWRelationalProject project) {
		MWDatabase database = project.getDatabase();
		configureSybaseDatabase(database);
		return database;
	}

	public static void configureSybaseDatabase(MWDatabase database) {
		database.setDatabasePlatform(sybasePlatform());
		MWLoginSpec loginSpec = sybaseLoginSpec(database);
		database.setDevelopmentLoginSpec(loginSpec);
		database.setDeploymentLoginSpec(loginSpec);
	}

	public static DatabasePlatform sybasePlatform() {
		return DatabasePlatformRepository.getDefault().platformNamed("Sybase");
	}

	public static MWLoginSpec sybaseLoginSpec(MWDatabase database) {
		MWLoginSpec loginSpec = database.addLoginSpec("Sybase");
		loginSpec.setDriverClassName(sybaseDriverClassName());
		loginSpec.setUserName(sybaseUserName());
		loginSpec.setPassword(password());
		loginSpec.setSavePassword(true);
		loginSpec.setURL(sybaseServerURL());
		addDriverClasspathEntriesTo(sybaseDriverClasspathEntries(), loginSpec);
		return loginSpec;
	}

	public static String sybaseDriverClassName() {
		return "com.sybase.jdbc2.jdbc.SybDriver";
	}

	public static String sybaseServerURL() {
		return "jdbc:sybase:Tds:" + sybaseServerName() + ":5001/" + sybaseDatabaseName();
	}

	public static String sybaseServerName() {
		return "tlsvrdb2.ca.oracle.com";
	}

	public static String sybaseDatabaseName() {
		return sybaseUserName();
	}

	/**
	 * there are no individual accounts on the Sybase server
	 */
	public static String sybaseUserName() {
		return "MWDEV1";
	}

	public static String[] sybaseDriverClasspathEntries() {
		return new String[] {"jconn2.jar"};
	}


	// ********** MySQL **********

	public static MWDatabase mySQLDatabase() {
		return mySQLDatabase(project());
	}

	public static MWDatabase mySQLDatabase(MWRelationalProject project) {
		MWDatabase database = project.getDatabase();
		configureMySQLDatabase(database);
		return database;
	}

	public static void configureMySQLDatabase(MWDatabase database) {
		database.setDatabasePlatform(mySQLPlatform());
		MWLoginSpec loginSpec = mySQLLoginSpec(database);
		database.setDevelopmentLoginSpec(loginSpec);
		database.setDeploymentLoginSpec(loginSpec);
	}

	public static DatabasePlatform mySQLPlatform() {
		return DatabasePlatformRepository.getDefault().platformNamed("MySQL");
	}

	public static MWLoginSpec mySQLLoginSpec(MWDatabase database) {
		MWLoginSpec loginSpec = database.addLoginSpec("MySQL");
		loginSpec.setDriverClassName(mySQLDriverClassName());
		loginSpec.setUserName(mySQLUserName());
		loginSpec.setPassword(password());
		loginSpec.setSavePassword(true);
		loginSpec.setURL(mySQLServerURL());
		addDriverClasspathEntriesTo(mySQLDriverClasspathEntries(), loginSpec);
		return loginSpec;
	}

	public static String mySQLDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	public static String mySQLServerURL() {
		return "jdbc:mysql://" + mySQLServerName() + "/" + mySQLDatabaseName();
	}

	public static String mySQLServerName() {
		return "tlsvrdb4.ca.oracle.com";
	}

	public static String mySQLDatabaseName() {
		return mySQLUserName();
	}

	/**
	 * there are no individual accounts on the MySQL server
	 */
	public static String mySQLUserName() {
		return "COREDEV1";
	}

	public static String[] mySQLDriverClasspathEntries() {
		return new String[] {"mysql-connector-java-3.1.7-bin.jar"};
	}


	// ********** Microsoft Access **********

	public static MWDatabase accessDatabase() {
		return accessDatabase(project());
	}

	public static MWDatabase accessDatabase(MWRelationalProject project) {
		MWDatabase database = project.getDatabase();
		configureAccessDatabase(database);
		return database;
	}

	public static void configureAccessDatabase(MWDatabase database) {
		database.setDatabasePlatform(accessPlatform());
		MWLoginSpec login = accessLoginSpec(database);
		database.setDevelopmentLoginSpec(login);
		database.setDeploymentLoginSpec(login);
	}

	public static DatabasePlatform accessPlatform() {
		return DatabasePlatformRepository.getDefault().platformNamed("MS Access");
	}

	public static MWLoginSpec accessLoginSpec(MWDatabase database) {
		MWLoginSpec loginSpec = database.addLoginSpec("Access");
		loginSpec.setDriverClassName(accessDriverClassName());
		loginSpec.setUserName(userName());
		loginSpec.setPassword(password());
		loginSpec.setSavePassword(true);
		loginSpec.setURL(accessServerURL());
		return loginSpec;
	}

	public static String accessDriverClassName() {
		return "sun.jdbc.odbc.JdbcOdbcDriver";
	}

	public static String accessServerURL() {
		return "jdbc:odbc:" + accessDataSourceName();
	}

	public static String accessDataSourceName() {
		return "MW";
	}


	// ********** constructor **********

	private TestDatabases() {
		throw new UnsupportedOperationException();
	}

}
