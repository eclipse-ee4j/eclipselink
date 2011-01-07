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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.security.JCEEncryptor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;

public final class MWLoginSpec
	extends MWModel
{
	/** the name should never be null or empty */
	private volatile String name;
		public static final String NAME_PROPERTY = "name";

	private volatile String driverClassName;
		public static final String DRIVER_CLASS_NAME_PROPERTY = "driverClassName";
		// virtual collection
		public static final String CANDIDATE_URLS_COLLECTION = "candidateURLs";
		
	private volatile String url;
		public static final String URL_PROPERTY = "url";
		
	private volatile String userName;
		public static final String USER_NAME_PROPERTY = "userName";
		
	private volatile String password;
		public static final String PASSWORD_PROPERTY = "password";

	private volatile boolean savePassword;
		public static final String SAVE_PASSWORD_PROPERTY = "savePassword";

	private List driverClasspathEntries;
		public static final String DRIVER_CLASSPATH_ENTRIES_LIST = "driverClasspathEntries";


	/** the JDBC drivers we know about and their URLs */
	private static Collection driverSpecs;

	/** cache this - it's expensive to instantiate */
	private static JCEEncryptor encryptor;

	/** preferences */
	public static final String DB_DRIVER_CLASS_PREFERENCE = "database driver class";
		
	public static final String DB_CONNECTION_URL_PREFERENCE = "database connection url";	
	
	// ********** static methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWLoginSpec.class);

		descriptor.addDirectMapping("name", "name/text()");

		((XMLDirectMapping) descriptor.addDirectMapping("driverClassName", "driver-class/text()")).setNullValue("");
		((XMLDirectMapping) descriptor.addDirectMapping("url", "url/text()")).setNullValue("");

		((XMLDirectMapping) descriptor.addDirectMapping("userName", "user-name/text()")).setNullValue("");
		descriptor.addDirectMapping("password", "getPasswordForTopLink", "setPasswordForTopLink", "password/text()");
		((XMLDirectMapping) descriptor.addDirectMapping("savePassword", "save-password/text()")).setNullValue(Boolean.FALSE);

		XMLCompositeDirectCollectionMapping driverClasspathMapping = new XMLCompositeDirectCollectionMapping();
		driverClasspathMapping.setAttributeName("driverClasspathEntries");
		driverClasspathMapping.setXPath("driver-classpath-entries/entry/text()");
		descriptor.addMapping(driverClasspathMapping);

		return descriptor;
	}
	
	/**
	 * return the driver class names we know about
	 */
	public static Iterator commonDriverClassNames() {
		return new TransformationIterator(driverSpecs()) {
			protected Object transform(Object next) {
				return ((DriverSpec) next).getDriverClassName();
			}
		};
	}

	public static int commonDriverClassNamesSize() {
		return getDriverSpecs().size();
	}

	/**
	 * return the URLs typically associated with the specified
	 * driver class name
	 */
	private static Iterator urlsForDriverClassNamed(String driverClassName) {
		DriverSpec ds = driverSpecFor(driverClassName);
		return (ds == null) ? NullIterator.instance() : ds.urls();
	}

	private static int urlsForDriverClassNamedSize(String driverClassName) {
		DriverSpec ds = driverSpecFor(driverClassName);
		return (ds == null) ? 0 : ds.urlsSize();
	}

	private static boolean urlsForDriverClassNamedContains(String driverClassName, String url) {
		DriverSpec ds = driverSpecFor(driverClassName);
		return (ds == null) ? false : ds.containsURL(url);
	}

	/**
	 * return the URL most commonly used with the specified
	 * driver class name
	 */
	private static String defaultURLForDriverClassNamed(String driverClassName) {
		DriverSpec ds = driverSpecFor(driverClassName);
		return (ds == null) ? null : ds.defaultURL();
	}

	private static DriverSpec driverSpecFor(String driverClassName) {
		for (Iterator stream = driverSpecs(); stream.hasNext(); ) {
			DriverSpec ds = (DriverSpec) stream.next();
			if (ds.getDriverClassName().equals(driverClassName)) {
				return ds;
			}
		}
		return null;
	}

	private static Iterator driverSpecs() {
		return getDriverSpecs().iterator();
	}

	private synchronized static Collection getDriverSpecs() {
		if (driverSpecs == null) {
			driverSpecs = buildDriverSpecs();
		}
		return driverSpecs;
	}

	// TODO move to DatabasePlatform and store in its XML file?
	private static Collection buildDriverSpecs() {
		Collection specs = new ArrayList(30);
		specs.add(new DriverSpec("com.neon.jdbc.Driver", "jdbc:neon:"));
		specs.add(new DriverSpec("com.pointbase.jdbc.jdbcUniversalDriver", "jdbc:pointbase:"));
		specs.add(new DriverSpec("com.sybase.jdbc3.jdbc.SybDriver", "jdbc:sybase:Tds:"));
		specs.add(new DriverSpec("com.sybase.jdbc2.jdbc.SybDriver", "jdbc:sybase:Tds:"));
		specs.add(new DriverSpec("com.sybase.jdbc.SybDriver", "jdbc:sybase:Tds:"));
		specs.add(new DriverSpec("COM.ibm.db2.jdbc.app.DB2Driver", "jdbc:db2:"));
		specs.add(new DriverSpec("COM.ibm.db2.jdbc.net.DB2Driver", "jdbc:db2:"));
		specs.add(new DriverSpec("com.ibm.db2.jcc.DB2Driver", "jdbc:db2://"));
		specs.add(new DriverSpec("com.mysql.jdbc.Driver", "jdbc:mysql://"));
		specs.add(new DriverSpec("borland.jdbc.Bridge.LocalDriver", "jdbc:BorlandBridge:"));
		specs.add(new DriverSpec("borland.jdbc.Broker.RemoteDriver", "jdbc:BorlandBridge:"));
		specs.add(new DriverSpec("intersolv.jdbc.sequelink.SequeLinkDriver", "jdbc:sequelink:"));
		String[] oracleURLs = 
				new String[] {
					"jdbc:oracle:thin:@<HOST>:<PORT>:<SID>",
					"jdbc:oracle:oci:@<HOST>:<PORT>:<SID>",
					"jdbc:oracle:oci7:@<HOST>:<PORT>:<SID>",
					"jdbc:oracle:oci8:@<HOST>:<PORT>:<SID>"
				};
		specs.add(new DriverSpec("oracle.jdbc.OracleDriver", oracleURLs));
		specs.add(new DriverSpec("oracle.jdbc.driver.OracleDriver", oracleURLs));
		specs.add(new DriverSpec("com.oracle.ias.jdbc.db2.DB2Driver", "jdbc:oracle:db2://"));
		specs.add(new DriverSpec("com.oracle.ias.jdbc.sqlserver.SQLServerDriver", "jdbc:oracle:sqlserver://"));
		specs.add(new DriverSpec("com.oracle.ias.jdbc.sybase.SybaseDriver", "jdbc:oracle:sybase://"));
		specs.add(new DriverSpec("org.hsqldb.jdbcDriver", "jdbc:hsqldb:"));
		specs.add(new DriverSpec("sun.jdbc.odbc.JdbcOdbcDriver", "jdbc:odbc:"));
		specs.add(new DriverSpec("weblogic.jdbc.oci.Driver", "jdbc:weblogic:oracle:"));
		specs.add(new DriverSpec("weblogic.jdbc.dblib.Driver", new String[] { "jdbc:weblogic:mssqlserver:", "jdbc:weblogic:sybase" }));
		specs.add(new DriverSpec("weblogic.jdbc.informix4.Driver", "jdbc:weblogic:informix4:"));
		specs.add(new DriverSpec("weblogic.jdbc.jts.Driver", "jdbc:weblogic:jts:"));
		specs.add(new DriverSpec("weblogic.jdbc.mssqlserver4.Driver", "jdbc:weblogic:mssqlserver4:"));
		specs.add(new DriverSpec("weblogic.jdbc.pool.Driver", "jdbc:weblogic:pool:"));
		specs.add(new DriverSpec("weblogic.jdbc.t3client.Driver", "jdbc:weblogic:t3Client:"));
		specs.add(new DriverSpec("weblogic.jdbc.t3.Driver", "jdbc:weblogic:t3:"));
		specs.add(new DriverSpec("com.timesten.jdbc.TimesTenDriver", "jdbc:timesten:direct:<SID>"));
		return specs;
	}

	private static JCEEncryptor getEncryptor() {
		if (encryptor == null) {
			try {
				encryptor = new JCEEncryptor();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return encryptor;
	}


	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWLoginSpec() {
		super();
	}

	MWLoginSpec(MWDatabase database, String name) {
		super(database);
		this.name = name;
	}


	// ********** initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.savePassword = false;	
		this.driverClasspathEntries = new Vector();
	}


	// ********** accessors **********

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
		if (this.attributeValueHasChanged(old, name)) {
			this.getProject().nodeRenamed(this);
		}
	}

	public String getDriverClassName() {
		return this.driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		String old = this.driverClassName;
		this.driverClassName = driverClassName;
		this.firePropertyChanged(DRIVER_CLASS_NAME_PROPERTY, old, driverClassName);
		if (this.attributeValueHasChanged(old, driverClassName)) {
			// only change the URL if it is easy to re-create
			if ((this.url == null) || urlsForDriverClassNamedContains(old, this.url)) {
				this.setURL(defaultURLForDriverClassNamed(driverClassName));
			}
			this.fireCollectionChanged(CANDIDATE_URLS_COLLECTION);
		}
	}

	public String getURL() {
		return this.url;
	}

	public void setURL(String url) {
		Object old = this.url;
		this.url = url;
		this.firePropertyChanged(URL_PROPERTY, old, url);
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		Object old = this.userName;
		this.userName = userName;
		this.firePropertyChanged(USER_NAME_PROPERTY, old, userName);
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		Object old = this.password;
		this.password = password;
		this.firePropertyChanged(PASSWORD_PROPERTY, old, password);
	}

	public boolean isSavePassword() {
		return this.savePassword;
	}

	public void setSavePassword(boolean savePassword) {
		boolean old = this.savePassword;
		this.savePassword = savePassword;
		this.firePropertyChanged(SAVE_PASSWORD_PROPERTY, old, savePassword);
	}


	/* NOTE: Driver classpath entries are Strings */

	public ListIterator driverClasspathEntries() {
		return new CloneListIterator(this.driverClasspathEntries);
	}

	public int driverClasspathEntriesSize() {
		return this.driverClasspathEntries.size();
	}

	public String getDriverClasspathEntry(int index) {
		return (String) this.driverClasspathEntries.get(index);
	}
	
	public void addDriverClasspathEntry(int index, String entry) {
		this.addItemToList(index, entry, this.driverClasspathEntries, DRIVER_CLASSPATH_ENTRIES_LIST);
	}
	
	public void addDriverClasspathEntry(String entry) {
		this.addDriverClasspathEntry(this.driverClasspathEntriesSize(), entry);
	}

	public void addDriverClasspathEntries(int index, List entries) {
		this.addItemsToList(index, entries, this.driverClasspathEntries, DRIVER_CLASSPATH_ENTRIES_LIST);
	}

	public void addDriverClasspathEntries(List entries) {
		this.addDriverClasspathEntries(this.driverClasspathEntriesSize(), entries);
	}

	public void addDriverClasspathEntries(ListIterator entries) {
		this.addDriverClasspathEntries(CollectionTools.list(entries));
	}

	public String removeDriverClasspathEntry(int index) {
		return (String) this.removeItemFromList(index, this.driverClasspathEntries, DRIVER_CLASSPATH_ENTRIES_LIST);
	}

	public List removeDriverClasspathEntries(int index, int length) {
		return this.removeItemsFromList(index, length, this.driverClasspathEntries, DRIVER_CLASSPATH_ENTRIES_LIST);
	}

	public String replaceDriverClasspathEntry(int index, String newEntry) {
		return (String) this.setItemInList(index, newEntry, this.driverClasspathEntries, DRIVER_CLASSPATH_ENTRIES_LIST);
	}


	// ********** problems **********

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);

		if (StringTools.stringIsEmpty(this.getURL())) {
			currentProblems.add(this.buildProblem(ProblemConstants.LOGIN_URL_NOT_SPECIFIED, getName()));
		}

		if (StringTools.stringIsEmpty(this.getDriverClassName())) {
			currentProblems.add(this.buildProblem(ProblemConstants.LOGIN_DRIVER_CLASS_NOT_SPECIFIED, getName()));
		}
	}


	// ********** queries **********

	public String defaultURL() {
		return defaultURLForDriverClassNamed(this.driverClassName);
	}

	public Iterator candidateURLs() {
		return urlsForDriverClassNamed(this.driverClassName);
	}

	public int candidateURLsSize() {
		return urlsForDriverClassNamedSize(this.driverClassName);
	}

	/**
	 * candidate URLs are hard-coded
	 */
	protected void addTransientAspectNamesTo(Set transientAspectNames) {
		super.addTransientAspectNamesTo(transientAspectNames);
		transientAspectNames.add(CANDIDATE_URLS_COLLECTION);
	}

	/**
	 * return the directory used to convert relative
	 * driver classpath entries to fully qualified files
	 */
	File driverClasspathBaseDirectory() {
		return this.getProject().getSaveDirectory();
	}

	/**
	 * return the driver classpath with the entries converted to
	 * fully qualified files (any relative entries will be
	 * resolved relative to the project save directory)
	 */
	public Iterator fullyQualifiedDriverClasspathFiles() {
		return new TransformationIterator(this.driverClasspathEntries()) {
			protected Object transform(Object next) {
				File file = new File((String) next);
				if ( ! file.isAbsolute()) {
					file = new File(MWLoginSpec.this.driverClasspathBaseDirectory(), file.getPath());
				}
				return file;
			}
		};
	}

	private URL[] driverClasspathURLs() {
		List urls = new ArrayList(this.driverClasspathEntriesSize());
		for (Iterator stream = this.fullyQualifiedDriverClasspathFiles(); stream.hasNext(); ) {
			File file = (File) stream.next();
			try {
				file = file.getCanonicalFile();
			} catch (IOException ioexception) {
				// just use the non-canonical file name
			}
			try {
				urls.add(file.toURL());
			} catch (IOException ioexception) {
				// just don't add it to the list
			}
		}
		return (URL[]) urls.toArray(new URL[urls.size()]);
	}


	// ********** behavior **********

	Driver buildDriver() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if ((this.driverClassName == null) || (this.driverClassName.length() == 0)) {
			throw new IllegalStateException("missing database driver class name");
		}
		// we should be able to load the driver with only a minimum classpath
		ClassLoader classLoader = new URLClassLoader(this.driverClasspathURLs());
		Class driverClass = Class.forName(this.driverClassName, true, classLoader);
		return (Driver) driverClass.newInstance();
	}

	/**
	 * Build and return a TopLink runtime connector for the spec's driver.
	 */
	public Connector buildConnector() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return new MWConnector(this.buildDriver(), this.getURL());
	}


	// ********** runtime conversion **********

	private DatabasePlatform databasePlatform() {
		return this.getDatabase().getDatabasePlatform();
	}

	/**
	 * this is used to log in to the database in the UI
	 */
	DatabaseLogin buildDevelopmentRuntimeDatabaseLogin() {
		DatabaseLogin login = this.buildRuntimeDatabaseLogin();
		login.setConnector(this.getDatabase().buildRuntimeConnector());
		return login;
	}

	/**
	 * this is used to generate deployment XML
	 */
	DatabaseLogin buildDeploymentRuntimeDatabaseLogin() {
		DatabaseLogin login = this.buildRuntimeDatabaseLogin();
		if ( ! this.savePassword) {
			login.setPassword(null);
		}
		return login;
	}

	private DatabaseLogin buildRuntimeDatabaseLogin() {
		DatabaseLogin login = new DatabaseLogin();

		login.setDriverClassName(this.driverClassName);
		login.setDriverURLHeader("");
		login.setDatabaseURL(this.url);
		login.setUserName(this.userName);
		login.setPassword(this.password);

		login.setPlatformClassName(this.databasePlatform().getRuntimePlatformClassName());

		return login;
	}	 
   	

	// ********** displaying and printing **********

	public void toString(StringBuffer sb) {
		sb.append(this.name);
	}

	public String displayString() {
		return this.name;
	}
		
			
	// ********** TopLink methods **********

	private String getPasswordForTopLink() {
		return (this.savePassword) ? this.encryptedPassword() : null;
	}

	private String encryptedPassword() {
		return (this.password == null) ? null : getEncryptor().encryptPassword(password);
	}

	private void setPasswordForTopLink(String password) {
		this.password = (password == null) ? null : getEncryptor().decryptPassword(password);
	}

	/**
	 * passwords were not encrypted in 4.5 and earlier
	 */
	private void legacySetPasswordForTopLink(String password) {
		this.password = password;
	}

	/**
	 * Pair popular JDBC drivers with the typical URLs used
	 * with them.
	 */
	// TODO need a better class name
	private static final class DriverSpec {
		private String driverClassName;
		private String[] urls;

		DriverSpec(String driverClassName, String[] urls) {
			super();
			if ((driverClassName == null) || (driverClassName.length() == 0)) {
				throw new IllegalArgumentException();
			}
			this.driverClassName = driverClassName;
			if ((urls == null) || (urls.length == 0)) {
				throw new IllegalArgumentException();
			}
			this.urls = urls;
		}

		DriverSpec(String driverClassName, String url) {
			this(driverClassName, new String[] {url});
		}

		public String getDriverClassName() {
			return this.driverClassName;
		}

		public String[] getUrls() {
			return this.urls;
		}

		public Iterator urls() {
			return new ArrayIterator(this.urls);
		}

		public int urlsSize() {
			return this.urls.length;
		}

		public boolean containsURL(String url) {
			for (int i = this.urls.length; i-- > 0; ) {
				if (this.urls[i].equals(url)) {
					return true;
				}
			}
			return false;
		}

		public String defaultURL() {
			return this.urls[0];
		}

		public String toString() {
			return StringTools.buildToStringFor(this, this.driverClassName);
		}

	}


	/**
	 * Implement a TopLink runtime Connector that ignores the JDBC
	 * DriverManager and uses a specified JDBC Driver. This allows us
	 * to load Drivers dynamically without worrying about which class
	 * loader loaded the driver and whether it is the same class loader
	 * that loaded the driver's client.
	 * 
	 * @see java.sql.DriverManager
	 */
	private static class MWConnector implements Connector {
		private Driver driver;
		private String url;

		public MWConnector(Driver driver, String url) {
			super();
			this.driver = driver;
			this.url = url;
		}

		public Connection connect(Properties properties, Session session) {
			try {
				return this.driver.connect(this.url, properties);
			} catch (SQLException ex) {
				throw DatabaseException.sqlException(ex);
			}
		}

		public Object clone() {
	        try {
	            return super.clone();
	        } catch (Exception exception) {
	            throw new InternalError("clone failed");
	        }
		}

		public String getConnectionDetails() {
			return "MWConnector: " + this.url;
		}

		public void toString(PrintWriter writer) {
			writer.println(this.getConnectionDetails());
		}

	}
}
