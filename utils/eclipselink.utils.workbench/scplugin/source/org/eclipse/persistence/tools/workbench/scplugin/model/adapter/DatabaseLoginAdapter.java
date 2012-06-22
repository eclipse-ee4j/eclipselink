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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject_11_1_1;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.StructConverterConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequencingConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class DatabaseLoginConfig
 * 
 * @see DatabaseLoginConfig
 * 
 * @author Tran Le
 */
public final class DatabaseLoginAdapter extends LoginAdapter {
	// property change
	public final static String DRIVER_CLASS_PROPERTY = "driverClass";
	public final static String CONNECTION_URL_PROPERTY = "connectionURL";
	public final static String DATA_SOURCE_PROPERTY = "dataSource";

	public final static String NATIVE_SEQUENCING_PROPERTY = "nativeSequencing";
	public final static String CONNECTION_HEALTH_VALIDATE_ON_ERROR_PROPERTY = "connectionHealthValidateOnError";
	public final static String DELAY_BETWEEN_CONNECTION_ATTEMPTS_PROPERTY = "delayBetweenConnectionAttempts";
	public final static String QUERY_RETRY_ATTEMPT_COUNT_PROPERTY = "queryRetryAttemptCount";
	public final static String PING_SQL_PROPERTY = "pingSQL";
	public final static String BIND_ALL_PARAMETERS_PROPERTY = "bindAllParameters";
	public final static String CACHE_ALL_STATEMENTS_PROPERTY = "cacheAllStatements";
	public final static String BYTE_ARRAY_BINDING_PROPERTY = "byteArrayBinding";
	public final static String STRING_BINDING_PROPERTY = "stringBinding";
	public final static String STREAMS_FOR_BINDING_PROPERTY = "streamsForBinding";
	public final static String FORCE_FIELD_NAMES_TO_UPPER_CASE_PROPERTY = "forceFieldNamesToUppercase";
	public final static String OPTIMIZE_DATA_CONVERSION_PROPERTY = "optimizeDataConversion";
	public final static String TRIM_STRINGS_PROPERTY = "trimStrings";
	public final static String BATCH_WRITING_PROPERTY = "batchWriting";
	public final static String JDBC_BATCH_WRITING_PROPERTY = "jdbcBatchWriting";
	public final static String MAX_BATCH_WRITING_SIZE_PROPERTY = "maxBatchWritingSize";
	public final static String NATIVE_SQL_PROPERTY = "nativeSQL";
	public final static String LOOKUP_TYPE_PROPERTY = "lookup";
	public final static String STRUCT_CONVERTER_COLLECTION_PROPERTY = "structConverters";
	
	private volatile boolean useDriverManager;
	public final static String USE_DRIVER_MANAGER_PROPERTY = "useDriverManager";
	private volatile boolean useProperties;
	public final static String USE_PROPERTIES_PROPERTY = "useProperties";

	private static Map driverClassesURLTable;

	/**
	 * Creates a new DatabaseLoginAdapter for the specified model object.
	 */
	DatabaseLoginAdapter( SCAdapter parent, DatabaseLoginConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new DatabaseLoginAdapter.
	 */
	protected DatabaseLoginAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		DatabaseLoginConfig dBLoginConfig = new DatabaseLoginConfig();
		dBLoginConfig.setStructConverterConfig(new StructConverterConfig());
		return dBLoginConfig;
	}
	protected StructConverterConfig buildStructConverters() {
		
		StructConverterConfig config = new StructConverterConfig();
		this.login().setStructConverterConfig(config);
		
		return config;
	}

	/**
	 * Factory method for building a child default SequencingAdapter.
	 */
	protected SequencingAdapter buildSequencing() {
		SequencingAdapter sequencing = super.buildSequencing();

		this.login().setSequencingConfig(( SequencingConfig)sequencing.getModel());
		
		return sequencing;
	}
	/**
	 * Returns this driverClass.
	 */
	public String getDriverClassName() {
		
		return this.login().getDriverClass();
	}
	/**
	 * Sets this driverClass and the config model.
	 */
	public void setDriverClassName( String className) {
	    if( !this.useDriverManager)
	        throw new IllegalStateException();
		
		Object old = this.login().getDriverClass();
		
		this.login().setDriverClass( className);
		this.firePropertyChanged( DRIVER_CLASS_PROPERTY, old, className);
	}
	
	/**
	 * Returns this config model property.
	 */
	public String getConnectionURL() {
		
		return this.login().getConnectionURL();
	}
	/**
	 * Sets this config model property.
	 */
	public void setConnectionURL( String value) {
		
		Object old = this.login().getConnectionURL();

		this.login().setConnectionURL( value);
		this.firePropertyChanged( CONNECTION_URL_PROPERTY, old, value);
	}
	/**
	 * Returns this dataSourceName.
	 */
	public String getDataSourceName() {
		
		return this.login().getDatasource();
	}
	/**
	 * Sets this dataSourceName and the config model.
	 */
	public void setDataSourceName( String name) {
	    if( this.useDriverManager)
	        throw new IllegalStateException();
	    
		Object old = this.login().getDatasource();
		
		this.login().setDatasource( name);
		this.firePropertyChanged( DATA_SOURCE_PROPERTY, old, name);
	}
	/**
	 * Returns whether the Database Driver is Driver Manager.
	 */
	public boolean databaseDriverIsDriverManager() {
		return this.useDriverManager;
	}
	/**
	 * Returns whether the Database Driver is Data Source.
	 */
	public boolean databaseDriverIsDataSource() {
		return !this.useDriverManager;
	}
	/**
	 * Sets this login Database Driver as Data Source.
	 */
	public void setDatabaseDriverAsDataSource() {
		
		setDatabaseDriverType( false);
	}
	/**
	 * Sets this login Database Driver as Driver Manager.
	 */
	public void setDatabaseDriverAsDriverManager() {
		
		setDatabaseDriverType( true);
	}
	/**
	 * Sets this Database Driver type
	 * @param useDriverManager is true when the type is Driver Manager, and false when the type is Data Source.
	 */
	private void setDatabaseDriverType( boolean useDriverManager) {
	    
		boolean old = this.useDriverManager;
		if( old == useDriverManager)
			return;
		
	    clearDatabaseDriverData();

		this.useDriverManager = useDriverManager;
		this.firePropertyChanged( USE_DRIVER_MANAGER_PROPERTY, old, useDriverManager);
	}
	
	private void clearDatabaseDriverData() {
	    
		if( this.useDriverManager) {
			setDriverClassName( null);
			setConnectionURL( null);
			(( LoginHandler)getParent()).setExternalConnectionPooling( true);
		}
		else {
			setDataSourceName( null);
		}
	}
	
	public void toString( StringBuffer sb) {
		super.toString( sb);
		
		sb.append( ", ").append( this.getDriverClassName());
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final DatabaseLoginConfig login() {
		
		return ( DatabaseLoginConfig)this.getModel();
	}

	protected void initializeDefaults() {
		super.initializeDefaults();

		this.useDriverManager = true;

		setByteArrayBinding( XMLSessionConfigProject.BYTE_ARRAY_BINDING_DEFAULT);
		setOptimizeDataConversion( XMLSessionConfigProject.OPTIMIZE_DATA_CONVERSION_DEFAULT);
		setTrimStrings( XMLSessionConfigProject.TRIM_STRINGS_DEFAULT);
		setMaxBatchWritingSize( XMLSessionConfigProject.MAX_BATCH_WRITING_SIZE_DEFAULT);
		setJdbcBatchWriting( XMLSessionConfigProject.JDBC20_BATCH_WRITING_DEFAULT);
		setLookupType( new Integer( XMLSessionConfigProject.DATASOURCE_LOOKUP_TYPE_DEFAULT));
		setBindAllParameters( XMLSessionConfigProject_11_1_1.BIND_ALL_PARAMETERS_DEFAULT);
	}
	
	@Override
	protected void initialize(Object newConfig) {
		super.initialize(newConfig);
		
		buildStructConverters();
	}
	
	/**
	 * Initializes this <code>DatabaseLoginAdapter</code>.
	 */
	protected void initializeFromModel(Object scConfig) {
		super.initializeFromModel( scConfig);

		if( usesBatchWriting() && ( login().getMaxBatchWritingSize() == null)) {
			login().setMaxBatchWritingSize( new Integer( 0));
		}
		this.useDriverManager = ( StringTools.stringIsEmpty( getDataSourceName())) ? true : false;

		Vector properties = login().getPropertyConfigs();
		useProperties = (( properties != null) && !properties.isEmpty());

		if( login().getLookupType() == null) {
			login().setLookupType( new Integer( XMLSessionConfigProject.DATASOURCE_LOOKUP_TYPE_DEFAULT));
		}
		
		if (login().getStructConverterConfig() == null) {
			buildStructConverters();
		}
	}
	/**
	 * Completes the initialization of the config model.
	 */
	protected void postInitializationFromModel() {
		super.postInitializationFromModel();

		String platformClass = convertToNewPlatformClass( getPlatformClass());

		if( platformClass != null)
			setPlatformClass( platformClass);
	}
	/**
	 * Returns the datasource platform class from user's preference.
	 */
	protected String getDefaultPlatformClassName() {
	    
        String platformName = this.preferences().get( SCPlugin.DATABASE_PLATFORM_PREFERENCE, SCPlugin.DATABASE_PLATFORM_PREFERENCE_DEFAULT);
        DatabasePlatform platform = DatabasePlatformRepository.getDefault().platformNamed( platformName);

	    return DataSource.getClassNameForDatabasePlatform( platform);
	}
	/**
	 * Converts the given platform class from the old version to the new version
	 * if it is required otherwise the given class name is returned.
	 */
	private String convertToNewPlatformClass( String platformClass) {

		if( platformClass == null)
			return null;

		return ( String)oldPlatformClasses().get( platformClass);
	}
	/**
	 * Returns a map of the old and new Database Platform classes.
	 */
	private Map oldPlatformClasses() {

		Map oldClasses = new Hashtable();

		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.AttunityPlatform",    "org.eclipse.persistence.platform.database.AttunityPlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.CloudscapePlatform",  "org.eclipse.persistence.platform.database.CloudscapePlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.DB2Platform",         "org.eclipse.persistence.platform.database.DB2Platform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.DBasePlatform",       "org.eclipse.persistence.platform.database.DBasePlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.HSQLPlatform",        "org.eclipse.persistence.platform.database.HSQLPlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.InformixPlatform",    "org.eclipse.persistence.platform.database.InformixPlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.AccessPlatform",      "org.eclipse.persistence.platform.database.AccessPlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.OraclePlatform",      "org.eclipse.persistence.platform.database.oracle.OraclePlatform");
		oldClasses.put("org.eclipse.persistence.oraclespecific.Oracle8Platform",              "org.eclipse.persistence.platform.database.oracle.Oracle8Platform");
		oldClasses.put("org.eclipse.persistence.oraclespecific.Oracle9Platform",              "org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.DatabasePlatform",    "org.eclipse.persistence.platform.database.DatabasePlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.PointBasePlatform",   "org.eclipse.persistence.platform.database.PointBasePlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.SQLAnyWherePlatform", "org.eclipse.persistence.platform.database.SQLAnywherePlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.SQLServerPlatform",   "org.eclipse.persistence.platform.database.SQLServerPlatform");
		oldClasses.put("org.eclipse.persistence.internal.databaseaccess.SybasePlatform",      "org.eclipse.persistence.platform.database.SybasePlatform");
		oldClasses.put("org.eclipse.persistence.platform.database.SQLAnyWherePlatform", "org.eclipse.persistence.platform.database.SQLAnywherePlatform");

		return oldClasses;
	}
	boolean platformIsRdbms() {
		
		return true;
	}
	/**
	 * Returns this config model property..
	 */
	public boolean bindsAllParameters() {
		
		return this.login().getBindAllParameters();
	}
	/**
	 * Sets this config model property.
	 */
	public void setBindAllParameters( boolean value) {
		
		boolean old = this.login().getBindAllParameters();

		this.login().setBindAllParameters( value);
		this.firePropertyChanged( BIND_ALL_PARAMETERS_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property..
	 */
	public boolean cachesAllStatements() {
		
		return this.login().getCacheAllStatements();
	}
	/**
	 * Sets this config model property.
	 */
	public void setCacheAllStatements( boolean value) {
		
		boolean old = this.login().getCacheAllStatements();

		this.login().setCacheAllStatements( value);
		this.firePropertyChanged( CACHE_ALL_STATEMENTS_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property..
	 */
	public boolean usesByteArrayBinding() {
		
		return this.login().getByteArrayBinding();
	}
	/**
	 * Sets this config model property.
	 */
	public void setByteArrayBinding( boolean value) {
		
		boolean old = this.login().getByteArrayBinding();

		this.login().setByteArrayBinding( value);
		this.firePropertyChanged( BYTE_ARRAY_BINDING_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property..
	 */
	public boolean usesStringBinding() {
		
		return this.login().getStringBinding();
	}
	/**
	 * Sets this config model property.
	 */
	public void setStringBinding( boolean value) {
		
		boolean old = this.login().getStringBinding();

		if( old == value)
			return;

		this.login().setStringBinding( value);
		this.firePropertyChanged( STRING_BINDING_PROPERTY, old, value);

		setDefaultMaxBatchWritingSize();
	}
	
	protected void setDefaultMaxBatchWritingSize() {
		if (this.usesStringBinding()) {
			setMaxBatchWritingSize(100);
		} else {
			setMaxBatchWritingSize(XMLSessionConfigProject.MAX_BATCH_WRITING_SIZE_DEFAULT);
		}
	}
	/**
	 * Returns this config model property..
	 */
	public boolean usesStreamsForBinding() {
		
		return this.login().getStreamsForBinding();
	}
	/**
	 * Sets this config model property.
	 */
	public void setStreamsForBinding( boolean value) {
		
		boolean old = this.login().getStreamsForBinding();

		this.login().setStreamsForBinding( value);
		this.firePropertyChanged( STREAMS_FOR_BINDING_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property..
	 */
	public boolean forcesFieldNamesToUppercase() {
		
		return this.login().getForceFieldNamesToUppercase();
	}
	/**
	 * Sets this config model property.
	 */
	public void setForceFieldNamesToUppercase( boolean value) {
		
		boolean old = this.login().getForceFieldNamesToUppercase();

		this.login().setForceFieldNamesToUppercase( value);
		this.firePropertyChanged( FORCE_FIELD_NAMES_TO_UPPER_CASE_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property..
	 */
	public boolean optimizesDataConversion() {
		
		return this.login().getOptimizeDataConversion();
	}
	/**
	 * Sets this config model property.
	 */
	public void setOptimizeDataConversion( boolean value) {
		
		boolean old = this.login().getOptimizeDataConversion();

		this.login().setOptimizeDataConversion( value);
		this.firePropertyChanged( OPTIMIZE_DATA_CONVERSION_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property..
	 */
	public boolean trimsStrings() {
		
		return this.login().getTrimStrings();
	}
	/**
	 * Sets this config model property.
	 */
	public void setTrimStrings( boolean value) {
		
		boolean old = this.login().getTrimStrings();

		this.login().setTrimStrings( value);
		this.firePropertyChanged( TRIM_STRINGS_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property..
	 */
	public boolean usesBatchWriting() {
		
		return this.login().getBatchWriting();
	}
	/**
	 * Sets this config model property.
	 */
	public void setBatchWriting( boolean value) {
		
		boolean old = this.login().getBatchWriting();

		this.login().setBatchWriting( value);
		this.firePropertyChanged( BATCH_WRITING_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property..
	 */
	public boolean usesJdbcBatchWriting() {
		
		return this.login().getJdbcBatchWriting();
	}
	/**
	 * Sets this config model property.
	 */
	public void setJdbcBatchWriting( boolean value) {
		
		boolean old = this.login().getJdbcBatchWriting();

		this.login().setJdbcBatchWriting( value);
		this.firePropertyChanged( JDBC_BATCH_WRITING_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property.
	 */
	public int getMaxBatchWritingSize() {
		Integer size = this.login().getMaxBatchWritingSize();
		return (size != null) ? size.intValue() : 0;
	}
	/**
	 * Sets this config model property.
	 */
	public void setMaxBatchWritingSize( int value) {
		
		int old = getMaxBatchWritingSize();

		this.login().setMaxBatchWritingSize( new Integer( value));
		this.firePropertyChanged( MAX_BATCH_WRITING_SIZE_PROPERTY, old, value);
	}
	
	public String getPingSQL() {
		return this.login().getPingSQL();
	}

	public void setPingSQL(String value) {
		String old = this.login().getPingSQL();
		
		this.login().setPingSQL(value);
		this.firePropertyChanged(PING_SQL_PROPERTY, old, value);
	}
	
	public Integer getQueryRetryAttemptCount() {
		return this.login().getQueryRetryAttemptCount();
	}
	
	public void setQueryRetryAttemptCount(Integer value) {
		Integer old = this.login().getQueryRetryAttemptCount();
		
		this.login().setQueryRetryAttemptCount(value);
		this.firePropertyChanged(QUERY_RETRY_ATTEMPT_COUNT_PROPERTY, old, value);
	}
	
	public Integer getDelayBetweenConnectionAttempts() {
		return this.login().getDelayBetweenConnectionAttempts();
	}
	
	public void setDelayBetweenConnectionAttempts(Integer value) {
		Integer old = this.login().getDelayBetweenConnectionAttempts();
		
		this.login().setDelayBetweenConnectionAttempts(value);
		this.firePropertyChanged(DELAY_BETWEEN_CONNECTION_ATTEMPTS_PROPERTY, old, value);
	}
	
	/**
	 * Returns this config model property..
	 */
	public boolean usesNativeSQL() {
		
		return this.login().getNativeSQL();
	}
	/**
	 * Sets this config model property.
	 */
	public void setNativeSQL( boolean value) {
		
		boolean old = this.login().getNativeSQL();

		this.login().setNativeSQL( value);
		this.firePropertyChanged( NATIVE_SQL_PROPERTY, old, value);
	}
	
	public boolean isNativeSequencing() {
		return this.login().getNativeSequencing();
	}
	
	public void setIsNativeSequencing(boolean value) {
		boolean old = this.login().getNativeSequencing();
		
		this.login().setNativeSequencing(value);
		this.firePropertyChanged(NATIVE_SEQUENCING_PROPERTY, old, value);
	}
	
	public boolean isConnectionHealthValidatedOnError() {
		Boolean value = this.login().isConnectionHealthValidatedOnError();
		if (value == null) {
			return false;
		} else {
			return value.booleanValue();
		}
	}
	
	public void setConnectionHealthValidatedOnError(boolean value) {
		Boolean old = this.login().isConnectionHealthValidatedOnError();
		Boolean newValue = Boolean.valueOf(value);
		this.login().setConnectionHealthValidatedOnError(newValue);
		this.firePropertyChanged(CONNECTION_HEALTH_VALIDATE_ON_ERROR_PROPERTY, old, newValue);
	}

	/**
	 * Returns the cached driver classes.
	 */
	public static Iterator driverClasses() {

		return getDriverClassesURLTable().keySet().iterator();
	}

	/**
	 * Returns the cached driver URLs associated with the given driver class.
	 */
	public static Iterator driverURLs(String driverClass) {

		String[] urls = (String[]) getDriverClassesURLTable().get(driverClass);

		if (urls == null)
			return NullIterator.instance();

		return new ArrayIterator(urls);
	}

	/**
	 * Builds the dictionary of driver classes associated with driver URLs.
	 */
	private static Map buildDriverClassURLTable() {

		Hashtable table = new Hashtable();
	
		table.put("com.neon.jdbc.Driver",                          new String[] { "jdbc:neon:" });
		table.put("com.pointbase.jdbc.jdbcUniversalDriver",        new String[] { "jdbc:pointbase:" });
		table.put("com.sybase.jdbc3.jdbc.SybDriver",               new String[] { "jdbc:sybase:Tds:" });
		table.put("com.sybase.jdbc2.jdbc.SybDriver",               new String[] { "jdbc:sybase:Tds:" });
		table.put("com.sybase.jdbc.SybDriver",                     new String[] { "jdbc:sybase:Tds:" });
		table.put("COM.ibm.db2.jdbc.app.DB2Driver",                new String[] { "jdbc:db2:" });
		table.put("COM.ibm.db2.jdbc.net.DB2Driver",                new String[] { "jdbc:db2:" });
		table.put("com.ibm.db2.jcc.DB2Driver",                     new String[] { "jdbc:db2://" });
		table.put("com.mysql.jdbc.Driver",                     new String[] { "jdbc:mysql://" });
		table.put("borland.jdbc.Bridge.LocalDriver",               new String[] { "jdbc:BorlandBridge:" });
		table.put("borland.jdbc.Broker.RemoteDriver",              new String[] { "jdbc:BorlandBridge:" });
		table.put("intersolv.jdbc.sequelink.SequeLinkDriver",      new String[] { "jdbc:sequelink:" });

		String[] oracleURLs = 
				new String[] {
					"jdbc:oracle:thin:@<HOST>:<PORT>:<SID>",
					"jdbc:oracle:oci:@<HOST>:<PORT>:<SID>",
					"jdbc:oracle:oci7:@<HOST>:<PORT>:<SID>",
					"jdbc:oracle:oci8:@<HOST>:<PORT>:<SID>"
				};
		table.put("oracle.jdbc.OracleDriver",               oracleURLs);
		table.put("oracle.jdbc.driver.OracleDriver",               oracleURLs);
		table.put("com.oracle.ias.jdbc.db2.DB2Driver",             new String[] { "jdbc:oracle:db2://" });
		table.put("com.oracle.ias.jdbc.sqlserver.SQLServerDriver", new String[] { "jdbc:oracle:sqlserver://" });
		table.put("com.oracle.ias.jdbc.sybase.SybaseDriver",       new String[] { "jdbc:oracle:sybase://" });
		table.put("org.hsqldb.jdbcDriver",                         new String[] { "jdbc:hsqldb:" });
		table.put("sun.jdbc.odbc.JdbcOdbcDriver",                  new String[] { "jdbc:odbc:" });
		table.put("weblogic.jdbc.oci.Driver",                      new String[] { "jdbc:weblogic:oracle:" });
		table.put("weblogic.jdbc.dblib.Driver",                    new String[] { "jdbc:weblogic:mssqlserver:",
		                                                                          "jdbc:weblogic:sybase" });
		table.put("weblogic.jdbc.informix4.Driver",                new String[] { "jdbc:weblogic:informix4:" });
		table.put("weblogic.jdbc.jts.Driver",                      new String[] { "jdbc:weblogic:jts:" });
		table.put("weblogic.jdbc.mssqlserver4.Driver",             new String[] { "jdbc:weblogic:mssqlserver4:" });
		table.put("weblogic.jdbc.pool.Driver",                     new String[] { "jdbc:weblogic:pool:" });
		table.put("weblogic.jdbc.t3client.Driver",                 new String[] { "jdbc:weblogic:t3Client:" });
		table.put("weblogic.jdbc.t3.Driver",                       new String[] { "jdbc:weblogic:t3:" });
		table.put("com.timesten.jdbc.TimesTenDriver",                       new String[] { "jdbc:timesten:direct:<SID>" });

		return table;
	}

	/**
	 * Returns the cached driver classes, builds it the first time.
	 */
	private static Map getDriverClassesURLTable() {

		if (driverClassesURLTable == null)
			driverClassesURLTable = buildDriverClassURLTable();

		return driverClassesURLTable;
	}

	/**
	 * Add any problems from this adapter to the given set.
	 */
	protected void addProblemsTo( List branchProblems) {

		super.addProblemsTo(branchProblems);

		if( this.databaseDriverIsDriverManager()) {
			verifyProblemDriverClass( branchProblems);
			verifyProblemDriverURL( branchProblems);
		}
		else {
			verifyProblemDataSourceName( branchProblems);
		}
	}
	
	private void verifyProblemDriverClass( List branchProblems) {

		if( StringTools.stringIsEmpty( getDriverClassName())) {
			branchProblems.add(buildProblem( SCProblemsConstants.DATABASE_LOGIN_DRIVER_CLASS, getParent().displayString()));
		}
	}

	private void verifyProblemDriverURL( List branchProblems) {

		if( StringTools.stringIsEmpty( getConnectionURL())) {
			branchProblems.add(buildProblem( SCProblemsConstants.DATABASE_LOGIN_CONNECTION_URL, getParent().displayString()));
		}
	}

	private void verifyProblemDataSourceName( List branchProblems) {

		if( StringTools.stringIsEmpty( getDataSourceName())) {
			branchProblems.add(buildProblem( SCProblemsConstants.DATABASE_LOGIN_DATA_SOURCE_NAME, getParent().displayString()));
		}
	}

	public boolean usesProperties() {
		return useProperties;
	}

	public void setUseProperties( boolean useProperties) {

		boolean old = this.usesProperties();
		this.useProperties = useProperties;
		this.firePropertyChanged( USE_PROPERTIES_PROPERTY, old, useProperties);

		if(! usesProperties())
			removeAllProperties();
	}

	public void setLookupType( Integer lookupType) {

		Integer oldLookupType = this.getLookupType();
		this.login().setLookupType( lookupType);
		this.firePropertyChanged(LOOKUP_TYPE_PROPERTY, oldLookupType, lookupType);
	}

	public Integer getLookupType() {

		return this.login().getLookupType();
	}
	
	private StructConverterConfig structConverterConfig() {
		return this.login().getStructConverterConfig();
	}
	
	public ListIterator<String> structConvertersClasses() {
		return this.structConverterConfig().getStructConverterClasses().listIterator();
	}
	
	public void addStructConverterClass(String className) {
		this.structConverterConfig().addStructConverterClass(className);
		int index = this.structConverterConfig().getStructConverterClasses().indexOf(className);
		this.fireItemAdded(STRUCT_CONVERTER_COLLECTION_PROPERTY, index, className);
	}
	
	public void removeStructConverterClass(String className) {
		Vector<String> oldStructConverterClasses = this.structConverterConfig().getStructConverterClasses();
		int index = oldStructConverterClasses.indexOf(className);
		oldStructConverterClasses.remove(className);
		this.structConverterConfig().setStructConverterClasses(oldStructConverterClasses);
		this.fireItemRemoved(STRUCT_CONVERTER_COLLECTION_PROPERTY, index, className);
	}
}
