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

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * This repository holds the JDBC Types that correspond to the data types
 * defined in java.sql.Types. It also holds the mappings between JDBC
 * types and Java type declarations, which are stored in an XML file.
 * 
 * @see java.sql.Types
 * 
 * For information on mapping JDBC and Java types,
 * see "Getting Started with JDBC API"
 * 	http://java.sun.com/j2se/1.4.2/docs/guide/jdbc/getstart/GettingStartedTOC.fm.html
 */
public final class JDBCTypeRepository extends AbstractNodeModel {

	/**
	 * All the data type constants in java.sql.Types (e.g. INTEGER: 4);
	 * by default, these are calculated via reflection on java.sql.Types.
	 * This will always contain at least one type, so we always have a default.
	 * @see java.sql.Types
	 */
	private Collection jdbcTypes;
		public static final String JDBC_TYPES_COLLECTION = "jdbcTypes";

	// these aren't really JDBC types, although they are defined in java.sql.Types
	private static final int[] DEFAULT_UNUSED_JDBC_TYPE_CODES = {Types.NULL};

	/**
	 * The default JDBC type used to determine the default database type
	 * used when generating tables.
	 */
	private JDBCType defaultJDBCType;
		public static final String DEFAULT_JDBC_TYPE_PROPERTY = "defaultJDBCType";

	private static final int DEFAULT_DEFAULT_JDBC_TYPE_CODE = Types.VARCHAR;

	/**
	 * Mappings of the JDBC types to Java type declarations
	 * (e.g. java.sql.Types.INTEGER maps to int).
	 * These are used to generate classes from tables.
	 * This collection is kept in synch with the jdbcTypes collection.
	 */
	private Collection jdbcTypeToJavaTypeDeclarationMappings;
		public static final String JDBC_TYPE_TO_JAVA_TYPE_DECLARATION_MAPPINGS_COLLECTION = "jdbcTypeToJavaTypeDeclarationMappings";

	/**
	 * Mappings of typical Java type declarations to the JDBC types
	 * (e.g. int and java.lang.Integer map to java.sql.Types.INTEGER).
	 * These are used to generate tables from classes.
	 */
	private Collection javaTypeDeclarationToJDBCTypeMappings;
		public static final String JAVA_TYPE_DECLARATION_TO_JDBC_TYPE_MAPPINGS_COLLECTION = "javaTypeDeclarationToJDBCTypeMappings";

	private static final boolean JDK16 = jdkIsVersion("1.6");

	private static boolean jdkIsVersion(String version) {
		return System.getProperty("java.version").indexOf(version) != -1;
	}


	// ********** constructors **********

	/**
	 * this constructor is called when the repository is read from an XML file
	 */
	JDBCTypeRepository(DatabasePlatformRepository platformRepository, Node node) throws CorruptXMLException {
		super(platformRepository);
		this.read(node);
	}

	/**
	 * this constructor is called when the user (or a test case)
	 * creates a new repository (which shouldn't happen very often...)
	 */
	JDBCTypeRepository(DatabasePlatformRepository platformRepository) {
		super(platformRepository);
		this.initializeDefaults();
	}


	// ********** initialization **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractNodeModel#initialize()
	 */
	protected void initialize() {
		super.initialize();
		this.jdbcTypes = new Vector();
		this.jdbcTypeToJavaTypeDeclarationMappings = new Vector();
		this.javaTypeDeclarationToJDBCTypeMappings = new Vector();
	}

	/**
	 * Initialize our attributes to some reasonable defaults.
	 */
	private void initializeDefaults() {
		this.initializeDefaultJDBCTypes();
		this.defaultJDBCType = this.jdbcTypeForCode(DEFAULT_DEFAULT_JDBC_TYPE_CODE);

		this.initializeDefaultJDBCToJavaMappings();
		// TODO change this conditional to
		//     if (JDK16) {
		// and uncomment Types in #initializeDefaultJDBCToJavaMappings()
		// when we start compiling with jdk1.6
		if ( ! JDK16) {
			this.checkJDBCTypeToJavaTypeDeclarationMappings();
		}

		this.initializeDefaultJavaToJDBCMappings();
	}

	private void checkJDBCTypeToJavaTypeDeclarationMappings() {
		// make sure we have mapped ALL the JDBC types
		if (this.jdbcTypeToJavaTypeDeclarationMappings.size() != this.jdbcTypes.size()) {
			throw new IllegalStateException("the JDBC type mappings were not initialized properly");
		}
	}

	/**
	 * build up the default JDBC types via reflection
	 * @see java.sql.Types
	 */
	private void initializeDefaultJDBCTypes() {
		Field[] fields = Types.class.getDeclaredFields();
		for (int i = fields.length; i-- > 0; ) {
			String name = fields[i].getName();
			int code;
			try {
				code = ((Integer) fields[i].get(null)).intValue();
			} catch (IllegalAccessException ex) {
				throw new RuntimeException(ex);	// shouldn't happen...
			}
			if ( ! CollectionTools.contains(DEFAULT_UNUSED_JDBC_TYPE_CODES, code)) {
				this.checkJDBCType(name, code);	// check for duplicates
				this.jdbcTypes.add(new JDBCType(this, name, code));
			}
		}
	}

	/**
	 * hard code the default mappings from the JDBC types to the
	 * appropriate Java type declarations
	 * @see java.sql.Types
	 * see "Getting Started with the JDBC API"
	 * http://java.sun.com/javase/6/docs/technotes/guides/jdbc/getstart/GettingStartedTOC.fm.html
	 *     or
	 * 	http://java.sun.com/j2se/1.4.2/docs/guide/jdbc/getstart/GettingStartedTOC.fm.html
	 */
	// TODO uncomment 6 new Types when we start compiling with jdk1.6
	private void initializeDefaultJDBCToJavaMappings() {
		this.addJDBCToJavaMapping(Types.ARRAY,				java.sql.Array.class);
		this.addJDBCToJavaMapping(Types.BIGINT,				long.class);
		this.addJDBCToJavaMapping(Types.BINARY,				byte.class, 1);	// byte[]
		this.addJDBCToJavaMapping(Types.BIT,					boolean.class);
		this.addJDBCToJavaMapping(Types.BLOB,				java.sql.Blob.class);
		this.addJDBCToJavaMapping(Types.BOOLEAN,			boolean.class);
		this.addJDBCToJavaMapping(Types.CHAR,				java.lang.String.class);
		this.addJDBCToJavaMapping(Types.CLOB,				java.sql.Clob.class);
		this.addJDBCToJavaMapping(Types.DATALINK,			java.net.URL.class);
		this.addJDBCToJavaMapping(Types.DATE,				java.sql.Date.class);
		this.addJDBCToJavaMapping(Types.DECIMAL,			java.math.BigDecimal.class);
		this.addJDBCToJavaMapping(Types.DISTINCT,			java.lang.Object.class);
		this.addJDBCToJavaMapping(Types.DOUBLE,			double.class);
		this.addJDBCToJavaMapping(Types.FLOAT,				double.class);
		this.addJDBCToJavaMapping(Types.INTEGER,			int.class);
		this.addJDBCToJavaMapping(Types.JAVA_OBJECT,	java.lang.Object.class);
//		this.addJDBCToJavaMapping(Types.LONGNVARCHAR,					java.lang.String.class); // JDK1.6
		this.addJDBCToJavaMapping(Types.LONGVARBINARY,	byte.class, 1);	// byte[]
		this.addJDBCToJavaMapping(Types.LONGVARCHAR,	java.lang.String.class);
//		this.addJDBCToJavaMapping(Types.NCHAR,					java.lang.String.class); // JDK1.6
//		this.addJDBCToJavaMapping(Types.NCLOB,					java.sql.NClob.class); // JDK1.6
//		this.addJDBCToJavaMapping(Types.NVARCHAR,					java.lang.String.class); // JDK1.6
		// not sure why this is defined in java.sql.Types
//		this.addJDBCToJavaMapping(Types.NULL,					java.lang.Object.class);
		this.addJDBCToJavaMapping(Types.NUMERIC,			java.math.BigDecimal.class);
		this.addJDBCToJavaMapping(Types.OTHER,				java.lang.Object.class);	// ???
		this.addJDBCToJavaMapping(Types.REAL,				float.class);
		this.addJDBCToJavaMapping(Types.REF,					java.sql.Ref.class);
//		this.addJDBCToJavaMapping(Types.ROWID,					java.sql.RowId.class); // JDK1.6
		this.addJDBCToJavaMapping(Types.SMALLINT,			short.class);
//		this.addJDBCToJavaMapping(Types.SQLXML,					java.sql.SQLXML.class); // JDK1.6
		this.addJDBCToJavaMapping(Types.STRUCT,			java.sql.Struct.class);
		this.addJDBCToJavaMapping(Types.TIME,				java.sql.Time.class);
		this.addJDBCToJavaMapping(Types.TIMESTAMP,		java.sql.Timestamp.class);
		this.addJDBCToJavaMapping(Types.TINYINT,			byte.class);
		this.addJDBCToJavaMapping(Types.VARBINARY,		byte.class, 1);	// byte[]
		this.addJDBCToJavaMapping(Types.VARCHAR,			java.lang.String.class);
	}

	private void addJDBCToJavaMapping(int jdbcTypeCode, Class javaClass) {
		this.addJDBCToJavaMapping(jdbcTypeCode, javaClass, 0);
	}

	private void addJDBCToJavaMapping(int jdbcTypeCode, Class javaClass, int arrayDepth) {
		JDBCType jdbcType = this.jdbcTypeForCode(jdbcTypeCode);
		this.checkJDBCToJavaMapping(jdbcType);	// check for duplicates
		this.jdbcTypeToJavaTypeDeclarationMappings.add(new JDBCTypeToJavaTypeDeclarationMapping(this, jdbcType, javaClass.getName(), arrayDepth));
	}

	/**
	 * hard code the default mappings from the typical Java type declarations to the
	 * appropriate JDBC types
	 * @see java.sql.Types
	 * see "Getting Started with JDBC API"
	 * 	http://java.sun.com/j2se/1.4.2/docs/guide/jdbc/getstart/GettingStartedTOC.fm.html
	 */
	private void initializeDefaultJavaToJDBCMappings() {
		this.addJavaToJDBCMapping(java.lang.String.class,			Types.VARCHAR);

		this.addJavaToJDBCMapping(boolean.class,					Types.BOOLEAN);
		this.addJavaToJDBCMapping(java.lang.Boolean.class,		Types.BOOLEAN);

		this.addJavaToJDBCMapping(char.class,							Types.CHAR);
		this.addJavaToJDBCMapping(java.lang.Character.class,		Types.CHAR);

		this.addJavaToJDBCMapping(byte.class,							Types.TINYINT);
		this.addJavaToJDBCMapping(java.lang.Byte.class,			Types.TINYINT);

		this.addJavaToJDBCMapping(short.class,						Types.SMALLINT);
		this.addJavaToJDBCMapping(java.lang.Short.class,			Types.SMALLINT);

		this.addJavaToJDBCMapping(int.class,							Types.INTEGER);
		this.addJavaToJDBCMapping(java.lang.Integer.class,			Types.INTEGER);

		this.addJavaToJDBCMapping(long.class,							Types.BIGINT);
		this.addJavaToJDBCMapping(java.lang.Long.class,			Types.BIGINT);

		this.addJavaToJDBCMapping(float.class,							Types.REAL);
		this.addJavaToJDBCMapping(java.lang.Float.class,			Types.REAL);

		this.addJavaToJDBCMapping(double.class,						Types.DOUBLE);
		this.addJavaToJDBCMapping(java.lang.Double.class,			Types.DOUBLE);

		this.addJavaToJDBCMapping(java.lang.Number.class,		Types.NUMERIC);
		this.addJavaToJDBCMapping(java.math.BigDecimal.class,	Types.NUMERIC);
		this.addJavaToJDBCMapping(java.math.BigInteger.class,	Types.NUMERIC);

		this.addJavaToJDBCMapping(java.util.Date.class,				Types.DATE);
		this.addJavaToJDBCMapping(java.sql.Date.class,				Types.DATE);
		this.addJavaToJDBCMapping(java.sql.Time.class,				Types.TIME);
		this.addJavaToJDBCMapping(java.sql.Timestamp.class,		Types.TIMESTAMP);
		this.addJavaToJDBCMapping(java.util.Calendar.class,		Types.TIMESTAMP);

		this.addJavaToJDBCMapping(java.sql.Blob.class,				Types.BLOB);
		this.addJavaToJDBCMapping(java.sql.Clob.class,				Types.CLOB);

		this.addJavaToJDBCMapping(java.sql.Array.class,				Types.ARRAY);
		this.addJavaToJDBCMapping(java.sql.Struct.class,			Types.STRUCT);
		this.addJavaToJDBCMapping(java.sql.Ref.class,				Types.REF);
		this.addJavaToJDBCMapping(java.net.URL.class,				Types.DATALINK);

		this.addJavaToJDBCMapping(byte.class, 1,						Types.VARBINARY);	// byte[]
		this.addJavaToJDBCMapping(char.class, 1,						Types.VARCHAR);	// char[]

		this.addJavaToJDBCMapping(java.lang.Class.class,			Types.VARCHAR);
	}

	private void addJavaToJDBCMapping(Class javaClass, int jdbcTypeCode) {
		this.addJavaToJDBCMapping(javaClass, 0, jdbcTypeCode);
	}

	private void addJavaToJDBCMapping(Class javaClass, int arrayDepth, int jdbcTypeCode) {
		String javaClassName = javaClass.getName();
		this.checkJavaToJDBCMapping(javaClassName, arrayDepth);	// check for duplicates
		this.javaTypeDeclarationToJDBCTypeMappings.add(new JavaTypeDeclarationToJDBCTypeMapping(this, javaClassName, arrayDepth, this.jdbcTypeForCode(jdbcTypeCode)));
	}


	// ********** accessors **********

	private DatabasePlatformRepository getPlatformRepository() {
		return  (DatabasePlatformRepository) this.getParent();
	}


	// JDBC types
	public Iterator jdbcTypes() {
		return new CloneIterator(this.jdbcTypes) {
			protected void remove(Object current) {
				JDBCTypeRepository.this.removeJDBCType((JDBCType) current);
			}
		};
	}

	public int jdbcTypesSize() {
		return this.jdbcTypes.size();
	}

	public JDBCType addJDBCType(String name, int code) {
		this.checkJDBCType(name, code);
		return this.addJDBCType(new JDBCType(this, name, code));
	}

	private JDBCType addJDBCType(JDBCType jdbcType) {
		this.addItemToCollection(jdbcType, this.jdbcTypes, JDBC_TYPES_COLLECTION);
		this.jdbcTypeAdded(jdbcType);
		return jdbcType;
	}

	private void jdbcTypeAdded(JDBCType addedJDBCType) {
		// synchronize the mappings
		this.addJDBCTypeToJavaTypeDeclarationMapping(addedJDBCType, java.lang.Object.class.getName(), 0);	// hmmm...
		// add the new JDBC type to all the platforms' mappings
		this.getPlatformRepository().jdbcTypeAdded(addedJDBCType);
	}

	public void removeJDBCType(JDBCType jdbcType) {
		if (jdbcType == this.defaultJDBCType) {
			throw new IllegalArgumentException("the default JDBC type may not be removed: " + jdbcType);
		}
		this.removeItemFromCollection(jdbcType, this.jdbcTypes, JDBC_TYPES_COLLECTION);
		this.jdbcTypeRemoved(jdbcType);
	}

	private void jdbcTypeRemoved(JDBCType removedJDBCType) {
		for (Iterator stream = this.jdbcTypeToJavaTypeDeclarationMappings(); stream.hasNext(); ) {
			if (((JDBCTypeToJavaTypeDeclarationMapping) stream.next()).getJDBCType() == removedJDBCType) {
				stream.remove();
				break;	// there should be only one...
			}
		}
		for (Iterator stream = this.javaTypeDeclarationToJDBCTypeMappings(); stream.hasNext(); ) {
			JavaTypeDeclarationToJDBCTypeMapping mapping = (JavaTypeDeclarationToJDBCTypeMapping) stream.next();
			if (mapping.getJDBCType() == removedJDBCType) {
				mapping.setJDBCType(this.defaultJDBCType);		// hmmm...
			}
		}
		this.getPlatformRepository().nodeRemoved(removedJDBCType);
	}

	public void removeJDBCTypes(Collection types) {
		if (types.contains(this.defaultJDBCType)) {
			throw new IllegalArgumentException("the default JDBC type may not be removed: " + this.defaultJDBCType);
		}
		this.removeItemsFromCollection(types, this.jdbcTypes, JDBC_TYPES_COLLECTION);
		this.jdbcTypesRemoved(types);
	}

	private void jdbcTypesRemoved(Collection removedJDBCTypes) {
		for (Iterator stream = removedJDBCTypes.iterator(); stream.hasNext(); ) {
			this.jdbcTypeRemoved((JDBCType) stream.next());
		}
	}

	public void removeJDBCTypes(Iterator types) {
		this.removeJDBCTypes(CollectionTools.collection(types));
	}


	// default JDBC type
	/**
	 * this will only be null when we have no JDBC types
	 */
	public JDBCType getDefaultJDBCType() {
		return this.defaultJDBCType;
	}

	/**
	 * the default cannot be set to null
	 */
	public void setDefaultJDBCType(JDBCType defaultJDBCType) {
		if (defaultJDBCType == null) {
			throw new NullPointerException();
		}
		Object old = this.defaultJDBCType;
		this.defaultJDBCType = defaultJDBCType;
		this.firePropertyChanged(DEFAULT_JDBC_TYPE_PROPERTY, old, defaultJDBCType);
	}


	// JDBC => Java mappings
	public Iterator jdbcTypeToJavaTypeDeclarationMappings() {
		return new CloneIterator(this.jdbcTypeToJavaTypeDeclarationMappings) {
			protected void remove(Object current) {
				JDBCTypeRepository.this.removeJDBCTypeToJavaTypeDeclarationMapping((JDBCTypeToJavaTypeDeclarationMapping) current);
			}
		};
	}

	public int jdbcTypeToJavaTypeDeclarationMappingsSize() {
		return this.jdbcTypeToJavaTypeDeclarationMappings.size();
	}

	public JDBCTypeToJavaTypeDeclarationMapping addJDBCTypeToJavaTypeDeclarationMapping(JDBCType jdbcType, String javaClassName, int arrayDepth) {
		this.checkJDBCToJavaMapping(jdbcType);
		return this.addJDBCTypeToJavaTypeDeclarationMapping(new JDBCTypeToJavaTypeDeclarationMapping(this, jdbcType, javaClassName, arrayDepth));
	}

	private JDBCTypeToJavaTypeDeclarationMapping addJDBCTypeToJavaTypeDeclarationMapping(JDBCTypeToJavaTypeDeclarationMapping mapping) {
		this.addItemToCollection(mapping, this.jdbcTypeToJavaTypeDeclarationMappings, JDBC_TYPE_TO_JAVA_TYPE_DECLARATION_MAPPINGS_COLLECTION);
		return mapping;
	}

	public void removeJDBCTypeToJavaTypeDeclarationMapping(JDBCTypeToJavaTypeDeclarationMapping mapping) {
		this.removeItemFromCollection(mapping, this.jdbcTypeToJavaTypeDeclarationMappings, JDBC_TYPE_TO_JAVA_TYPE_DECLARATION_MAPPINGS_COLLECTION);
	}

	public void removeJDBCTypeToJavaTypeDeclarationMappings(Collection mappings) {
		this.removeItemsFromCollection(mappings, this.jdbcTypeToJavaTypeDeclarationMappings, JDBC_TYPE_TO_JAVA_TYPE_DECLARATION_MAPPINGS_COLLECTION);
	}

	public void removeJDBCTypeToJavaTypeDeclarationMappings(Iterator mappings) {
		this.removeItemsFromCollection(mappings, this.jdbcTypeToJavaTypeDeclarationMappings, JDBC_TYPE_TO_JAVA_TYPE_DECLARATION_MAPPINGS_COLLECTION);
	}


	// Java => JDBC mappings
	public Iterator javaTypeDeclarationToJDBCTypeMappings() {
		return new CloneIterator(this.javaTypeDeclarationToJDBCTypeMappings) {
			protected void remove(Object current) {
				JDBCTypeRepository.this.removeJavaTypeDeclarationToJDBCTypeMapping((JavaTypeDeclarationToJDBCTypeMapping) current);
			}
		};
	}

	public int javaTypeDeclarationToJDBCTypeMappingsSize() {
		return this.javaTypeDeclarationToJDBCTypeMappings.size();
	}

	public JavaTypeDeclarationToJDBCTypeMapping addJavaTypeDeclarationToJDBCTypeMapping(String javaClassName, int arrayDepth, JDBCType jdbcType) {
		this.checkJavaToJDBCMapping(javaClassName, arrayDepth);
		return this.addJavaTypeDeclarationToJDBCTypeMapping(new JavaTypeDeclarationToJDBCTypeMapping(this, javaClassName, arrayDepth, jdbcType));
	}

	private JavaTypeDeclarationToJDBCTypeMapping addJavaTypeDeclarationToJDBCTypeMapping(JavaTypeDeclarationToJDBCTypeMapping mapping) {
		this.addItemToCollection(mapping, this.javaTypeDeclarationToJDBCTypeMappings, JAVA_TYPE_DECLARATION_TO_JDBC_TYPE_MAPPINGS_COLLECTION);
		return mapping;
	}

	public void removeJavaTypeDeclarationToJDBCTypeMapping(JavaTypeDeclarationToJDBCTypeMapping mapping) {
		this.removeItemFromCollection(mapping, this.javaTypeDeclarationToJDBCTypeMappings, JAVA_TYPE_DECLARATION_TO_JDBC_TYPE_MAPPINGS_COLLECTION);
	}

	public void removeJavaTypeDeclarationToJDBCTypeMappings(Collection mappings) {
		this.removeItemsFromCollection(mappings, this.javaTypeDeclarationToJDBCTypeMappings, JAVA_TYPE_DECLARATION_TO_JDBC_TYPE_MAPPINGS_COLLECTION);
	}

	public void removeJavaTypeDeclarationToJDBCTypeMappings(Iterator mappings) {
		this.removeItemsFromCollection(mappings, this.javaTypeDeclarationToJDBCTypeMappings, JAVA_TYPE_DECLARATION_TO_JDBC_TYPE_MAPPINGS_COLLECTION);
	}


	// ********** queries **********

	/**
	 * @see java.sql.Types
	 */
	public JDBCType jdbcTypeForCode(int jdbcTypeCode) {
		synchronized (this.jdbcTypes) {
			for (Iterator stream = this.jdbcTypes.iterator(); stream.hasNext(); ) {
				JDBCType jdbcType = (JDBCType) stream.next();
				if (jdbcType.getCode() == jdbcTypeCode) {
					return jdbcType;
				}
			}
			throw new IllegalArgumentException("missing JDBC type for code: " + jdbcTypeCode);
		}
	}

	/**
	 * case sensitive
	 * @see java.sql.Types
	 */
	JDBCType jdbcTypeNamed(String jdbcTypeName) {
		synchronized (this.jdbcTypes) {
			for (Iterator stream = this.jdbcTypes.iterator(); stream.hasNext(); ) {
				JDBCType jdbcType = (JDBCType) stream.next();
				if (jdbcType.getName().equals(jdbcTypeName)) {
					return jdbcType;
				}
			}
			throw new IllegalArgumentException("missing JDBC type named: " + jdbcTypeName);
		}
	}

	/**
	 * return whether the specified JDBC type has a corresponding
	 * Java type declaration; this is used for validation
	 */
	private boolean jdbcTypeCanBeMappedToJavaTypeDeclaration(JDBCType jdbcType) {
		synchronized (this.jdbcTypeToJavaTypeDeclarationMappings) {
			for (Iterator stream = this.jdbcTypeToJavaTypeDeclarationMappings.iterator(); stream.hasNext(); ) {
				if (((JDBCTypeToJavaTypeDeclarationMapping) stream.next()).maps(jdbcType)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * return whether the specified Java type declaration has a corresponding
	 * JDBC datatype; this can be used to determine whether a
	 * Java class can be mapped with a direct-to-field mapping
	 * (or a direct collection mapping), as opposed to needing its
	 * own descriptor
	 */
	boolean javaTypeDeclarationCanBeMappedToJDBCType(String javaClassName, int arrayDepth) {
		synchronized (this.javaTypeDeclarationToJDBCTypeMappings) {
			for (Iterator stream = this.javaTypeDeclarationToJDBCTypeMappings.iterator(); stream.hasNext(); ) {
				if (((JavaTypeDeclarationToJDBCTypeMapping) stream.next()).maps(javaClassName, arrayDepth)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * return the JDBC type for the specified Java type declaration;
	 * used to generate tables from classes
	 */
	JDBCType jdbcTypeForJavaTypeDeclaration(String javaClassName, int arrayDepth) {
		synchronized (this.javaTypeDeclarationToJDBCTypeMappings) {
			for (Iterator stream = this.javaTypeDeclarationToJDBCTypeMappings.iterator(); stream.hasNext(); ) {
				JavaTypeDeclarationToJDBCTypeMapping mapping = (JavaTypeDeclarationToJDBCTypeMapping) stream.next();
				if (mapping.maps(javaClassName, arrayDepth)) {
					return mapping.getJDBCType();
				}
			}
			throw new IllegalArgumentException("missing JDBC type mapping for Java type declaration: " + javaClassName + this.brackets(arrayDepth));
		}
	}

	/**
	 * return the Java type declaration for the specified JDBC type;
	 * used to generate classes from tables
	 */
	JavaTypeDeclaration javaTypeDeclarationFor(JDBCType jdbcType) {
		synchronized (this.jdbcTypeToJavaTypeDeclarationMappings) {
			for (Iterator stream = this.jdbcTypeToJavaTypeDeclarationMappings.iterator(); stream.hasNext(); ) {
				JDBCTypeToJavaTypeDeclarationMapping mapping = (JDBCTypeToJavaTypeDeclarationMapping) stream.next();
				if (mapping.maps(jdbcType)) {
					return mapping.getJavaTypeDeclaration();
				}
			}
			throw new IllegalArgumentException("missing Java type declaration mapping for JDBC type: " + jdbcType);
		}
	}

	/**
	 * return the Java type declaration for the specified JDBC type;
	 * used to generate classes from tables
	 */
	JavaTypeDeclaration javaTypeDeclarationForJDBCTypeCode(int jdbcTypeCode) {
		return this.javaTypeDeclarationFor(this.jdbcTypeForCode(jdbcTypeCode));
	}

	private Iterator jdbcTypeNames() {
		return new TransformationIterator(this.jdbcTypes()) {
			protected Object transform(Object next) {
				return ((JDBCType) next).getName();
			}
		};
	}


	// ********** behavior **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.utility.AbstractNodeModel#addChildrenTo(java.util.List)
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.jdbcTypes) { children.addAll(this.jdbcTypes); }
		synchronized (this.jdbcTypeToJavaTypeDeclarationMappings) { children.addAll(this.jdbcTypeToJavaTypeDeclarationMappings); }
		synchronized (this.javaTypeDeclarationToJDBCTypeMappings) { children.addAll(this.javaTypeDeclarationToJDBCTypeMappings); }
	}

	/**
	 * disallow duplicate JDBC type names or codes
	 */
	private void checkJDBCType(JDBCType jdbcType) {
		this.checkJDBCType(jdbcType.getName(), jdbcType.getCode());
	}

	/**
	 * disallow duplicate JDBC type names or codes
	 */
	private void checkJDBCType(String jdbcTypeName, int jdbcTypeCode) {
		this.checkJDBCTypeName(jdbcTypeName);
		this.checkJDBCTypeCode(jdbcTypeCode);
	}

	/**
	 * disallow duplicate JDBC type names
	 */
	void checkJDBCTypeName(String jdbcTypeName) {
		if (jdbcTypeName == null) {
			throw new NullPointerException();
		}
		if (CollectionTools.contains(this.jdbcTypeNames(), jdbcTypeName)) {
			throw new IllegalArgumentException("duplicate JDBC type name: " + jdbcTypeName);
		}
	}

	/**
	 * disallow duplicate JDBC type codes
	 */
	void checkJDBCTypeCode(int jdbcTypeCode) {
		synchronized (this.jdbcTypes) {
			for (Iterator stream = this.jdbcTypes.iterator(); stream.hasNext(); ) {
				if (((JDBCType) stream.next()).getCode() ==  jdbcTypeCode) {
					throw new IllegalArgumentException("duplicate JDBC type code: " + jdbcTypeCode);
				}
			}
		}
	}

	/**
	 * disallow duplicate JDBC type mappings
	 */
	private void checkJDBCToJavaMapping(JDBCType jdbcType) {
		if (this.jdbcTypeCanBeMappedToJavaTypeDeclaration(jdbcType)) {
			throw new IllegalArgumentException("duplicate mapping: " + jdbcType.getName());
		}
	}

	/**
	 * disallow duplicate Java type declaration mappings
	 */
	private void checkJavaToJDBCMapping(String javaClassName, int arrayDepth) {
		if (this.javaTypeDeclarationCanBeMappedToJDBCType(javaClassName, arrayDepth)) {
			throw new IllegalArgumentException("duplicate mapping: " + javaClassName + this.brackets(arrayDepth));
		}
	}

	/**
	 * disallow duplicate Java type declaration mappings
	 */
	private void checkJavaToJDBCMapping(JavaTypeDeclaration javaTypeDeclaration) {
		this.checkJavaToJDBCMapping(javaTypeDeclaration.getJavaClassName(), javaTypeDeclaration.getArrayDepth());
	}


	// ********** i/o **********

	private void read(Node node) throws CorruptXMLException {
		if (node == null) {
			throw new CorruptXMLException("missing node");
		}

		this.readJDBCTypeNodes(XMLTools.child(node, "jdbc-types"));
		if (this.jdbcTypes.isEmpty()) {
			throw new CorruptXMLException("the JDBC type repository is empty");
		}

		String defaultJDBCTypeName = XMLTools.childTextContent(node, "default-jdbc-type", null);
		try {
			this.defaultJDBCType = this.jdbcTypeNamed(defaultJDBCTypeName);
		} catch (IllegalArgumentException ex) {
			throw new CorruptXMLException("default JDBC type", ex);
		}

		this.readJDBCToJavaMappingNodes(XMLTools.child(node, "jdbc-type-to-java-type-declaration-mappings"));
		// make sure we have mapped ALL the JDBC types (there are no duplicates at this point)
		if (this.jdbcTypeToJavaTypeDeclarationMappings.size() != this.jdbcTypes.size()) {
			throw new CorruptXMLException("all the JDBC types must be mapped to Java type declarations");
		}

		this.readJavaToJDBCMappingNodes(XMLTools.child(node, "java-type-declaration-to-jdbc-type-mappings"));
	}

	private void readJDBCTypeNodes(Node jdbcTypesNode) throws CorruptXMLException {
		Node[] jdbcTypeNodes = XMLTools.children(jdbcTypesNode, "jdbc-type");
		int len = jdbcTypeNodes.length;
		for (int i = 0; i < len; i++) {
			JDBCType jdbcType = new JDBCType(this, jdbcTypeNodes[i]);
			try {
				this.checkJDBCType(jdbcType);	// check for duplicates
			} catch (IllegalArgumentException ex) {
				throw new CorruptXMLException(ex);
			}
			this.jdbcTypes.add(jdbcType);
		}
	}

	private void readJDBCToJavaMappingNodes(Node mappingsNode) throws CorruptXMLException {
		Node[] mappingNodes = XMLTools.children(mappingsNode, "jdbc-type-to-java-type-declaration-mapping");
		int len = mappingNodes.length;
		for (int i = 0; i < len; i++) {
			JDBCTypeToJavaTypeDeclarationMapping mapping = new JDBCTypeToJavaTypeDeclarationMapping(this, mappingNodes[i]);
			try {
				this.checkJDBCToJavaMapping(mapping.getJDBCType());	// check for duplicates
			} catch (IllegalArgumentException ex) {
				throw new CorruptXMLException(ex);
			}
			this.jdbcTypeToJavaTypeDeclarationMappings.add(mapping);
		}
	}

	private void readJavaToJDBCMappingNodes(Node mappingsNode) throws CorruptXMLException {
		Node[] mappingNodes = XMLTools.children(mappingsNode, "java-type-declaration-to-jdbc-type-mapping");
		int len = mappingNodes.length;
		for (int i = 0; i < len; i++) {
			JavaTypeDeclarationToJDBCTypeMapping mapping = new JavaTypeDeclarationToJDBCTypeMapping(this, mappingNodes[i]);
			try {
				this.checkJavaToJDBCMapping(mapping.getJavaTypeDeclaration());	// check for duplicates
			} catch (IllegalArgumentException ex) {
				throw new CorruptXMLException(ex);
			}
			this.javaTypeDeclarationToJDBCTypeMappings.add(mapping);
		}
	}

	void write(Node node) {
		Document document = node.getOwnerDocument();
		XMLTools.addSimpleTextNode(node, "default-jdbc-type", this.defaultJDBCType.getName());
		this.writeJDBCTypeNodes(node.appendChild(document.createElement("jdbc-types")));
		this.writeJDBCToJavaMappingNodes(node.appendChild(document.createElement("jdbc-type-to-java-type-declaration-mappings")));
		this.writeJavaToJDBCMappingNodes(node.appendChild(document.createElement("java-type-declaration-to-jdbc-type-mappings")));
	}

	private void writeJDBCTypeNodes(Node jdbcTypesNode) {
		Document document = jdbcTypesNode.getOwnerDocument();
		synchronized (this.jdbcTypes) {
			for (Iterator stream = new TreeSet(this.jdbcTypes).iterator(); stream.hasNext(); ) {
				Node jdbcTypeNode = document.createElement("jdbc-type");
				jdbcTypesNode.appendChild(jdbcTypeNode);
				((JDBCType) stream.next()).write(jdbcTypeNode);
			}
		}
	}

	private void writeJDBCToJavaMappingNodes(Node mappingsNode) {
		Document document = mappingsNode.getOwnerDocument();
		synchronized (this.jdbcTypeToJavaTypeDeclarationMappings) {
			for (Iterator stream = new TreeSet(this.jdbcTypeToJavaTypeDeclarationMappings).iterator(); stream.hasNext(); ) {
				Node mappingNode = document.createElement("jdbc-type-to-java-type-declaration-mapping");
				mappingsNode.appendChild(mappingNode);
				((JDBCTypeToJavaTypeDeclarationMapping) stream.next()).write(mappingNode);
			}
		}
	}

	private void writeJavaToJDBCMappingNodes(Node mappingsNode) {
		Document document = mappingsNode.getOwnerDocument();
		synchronized (this.javaTypeDeclarationToJDBCTypeMappings) {
			for (Iterator stream = new TreeSet(this.javaTypeDeclarationToJDBCTypeMappings).iterator(); stream.hasNext(); ) {
				Node mappingNode = document.createElement("java-type-declaration-to-jdbc-type-mapping");
				mappingsNode.appendChild(mappingNode);
				((JavaTypeDeclarationToJDBCTypeMapping) stream.next()).write(mappingNode);
			}
		}
	}


	// ********** printing and displaying **********

	private String brackets(int arrayDepth) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arrayDepth; i++) {
			sb.append("[]");
		}
		return sb.toString();
	}

	public String displayString() {
		return "";
	}

	public void toString(StringBuffer sb) {
		sb.append(this.jdbcTypes.size() + " JDBC types");
	}

}
