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
package org.eclipse.persistence.tools.workbench.test.platformsplugin;

import java.sql.Types;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeRepository;
import org.eclipse.persistence.tools.workbench.platformsmodel.JDBCTypeToDatabaseTypeMapping;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * Test infrastructure class that can be instantiated and used to build
 * test database platform repositories.
 */
public class TestDatabasePlatformRepositoryFactory {

	// singleton
	private static TestDatabasePlatformRepositoryFactory INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized TestDatabasePlatformRepositoryFactory instance() {
		if (INSTANCE == null) {
			INSTANCE = new TestDatabasePlatformRepositoryFactory();
		}
		return INSTANCE;
	}

	public static String platformsDirectoryName() {
		return (String) ClassTools.getStaticFieldValue(DatabasePlatformRepository.class, "PLATFORMS_DIRECTORY_NAME");
	}

	/**
	 * Ensure non-instantiability.
	 */
	private TestDatabasePlatformRepositoryFactory() {
		super();
	}

	public DatabasePlatformRepository createRepository() {
		DatabasePlatformRepository repository = this.createSimpleRepository();
		repository.setComment("This a test repository.");
		this.addPlatformsTo(repository);
		return repository;
	}

	protected DatabasePlatformRepository createSimpleRepository() {
		return new DatabasePlatformRepository("Test Repository");
	}

	protected void addPlatformsTo(DatabasePlatformRepository repository) {
		this.initializeFooPlatform(repository.addPlatform("Foo Platform", "fooplatform.xml"));
		this.initializeBarPlatform(repository.addPlatform("Bar Platform", "barplatform.xml"));
		this.initializeBazPlatform(repository.addPlatform("Baz Platform", "bazplatform.xml"));
	}

	/**
	 * Oracle: native sequencing but no IDENTITY clause support
	 */
	protected void initializeFooPlatform(DatabasePlatform platform) {
		platform.setRuntimePlatformClassName("com.foo.FooPlatform");
		platform.setComment("native sequencing, but no IDENTITY clause support (Oracle)");
		platform.setSupportsNativeSequencing(true);

		DatabaseType numberType = platform.addDatabaseType("NUMBER");
		numberType.setAllowsSubSize(true);

		DatabaseType stringType = platform.addDatabaseType("STRING");

		DatabaseType dateType = platform.addDatabaseType("DATE");
		dateType.setAllowsSize(false);

		this.initializeJDBCMappings(platform, numberType, stringType, dateType);
	}

	/**
	 * Sybase: native sequencing via IDENTITY clause
	 */
	protected void initializeBarPlatform(DatabasePlatform platform) {
		platform.setRuntimePlatformClassName("com.bar.BarPlatform");
		platform.setComment("native sequencing via IDENTITY clause (Sybase)");
		platform.setSupportsNativeSequencing(true);
		platform.setSupportsIdentityClause(true);

		DatabaseType numberType = platform.addDatabaseType("DECIMAL");
		numberType.setAllowsSubSize(true);

		DatabaseType stringType = platform.addDatabaseType("VARCHAR");

		DatabaseType dateType = platform.addDatabaseType("TIMESTAMP");
		dateType.setAllowsSize(false);

		DatabaseType blobType = platform.addDatabaseType("BLOB");
		dateType.setAllowsSize(false);

		this.initializeJDBCMappings(platform, numberType, stringType, dateType);
	}

	/**
	 * dBASE: no native sequencing
	 */
	protected void initializeBazPlatform(DatabasePlatform platform) {
		platform.setRuntimePlatformClassName("com.baz.BazPlatform");
		platform.setComment("no native sequencing (dBASE)");

		DatabaseType numberType = platform.addDatabaseType("INT");
		numberType.setAllowsSubSize(true);

		DatabaseType stringType = platform.addDatabaseType("CHAR");

		DatabaseType dateType = platform.addDatabaseType("DATE");
		dateType.setAllowsSize(false);

		this.initializeJDBCMappings(platform, numberType, stringType, dateType);
	}

	protected void initializeJDBCMappings(DatabasePlatform platform, DatabaseType numberType, DatabaseType stringType, DatabaseType dateType) {
		JDBCTypeRepository jdbcRepository = platform.getRepository().getJDBCTypeRepository();
		numberType.setJDBCType(jdbcRepository.jdbcTypeForCode(Types.DECIMAL));
		stringType.setJDBCType(jdbcRepository.jdbcTypeForCode(Types.VARCHAR));
		dateType.setJDBCType(jdbcRepository.jdbcTypeForCode(Types.DATE));

		for (Iterator stream = platform.jdbcTypeToDatabaseTypeMappings(); stream.hasNext(); ) {
			JDBCTypeToDatabaseTypeMapping mapping = (JDBCTypeToDatabaseTypeMapping) stream.next();
			switch (mapping.getJDBCType().getCode()) {
				case Types.BIGINT:			mapping.setDatabaseType(numberType); break;
				case Types.BIT:				mapping.setDatabaseType(numberType); break;
				case Types.DATE:			mapping.setDatabaseType(dateType); break;
				case Types.DECIMAL:		mapping.setDatabaseType(numberType); break;
				case Types.DOUBLE:		mapping.setDatabaseType(numberType); break;
				case Types.FLOAT:			mapping.setDatabaseType(numberType); break;
				case Types.INTEGER:		mapping.setDatabaseType(numberType); break;
				case Types.NUMERIC:		mapping.setDatabaseType(numberType); break;
				case Types.REAL:			mapping.setDatabaseType(numberType); break;
				case Types.SMALLINT:	mapping.setDatabaseType(numberType); break;
				case Types.TIME:			mapping.setDatabaseType(dateType); break;
				case Types.TIMESTAMP:	mapping.setDatabaseType(dateType); break;
				case Types.TINYINT:		mapping.setDatabaseType(numberType); break;
				default:						mapping.setDatabaseType(stringType); break;
			}
		}
	}

}
