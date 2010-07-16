package org.eclipse.persistence.testing.framework.wdf.customizer;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;

public class AdjustArrayTypeCustomizer implements DescriptorCustomizer {

	private static final String VARCHAR = "VARCHAR";
	private static final String BINARY = "BINARY";
	private static Class<? extends DatabasePlatform> databasePlatformClass;

	public static void setDatabasePlatformClass(
			Class<? extends DatabasePlatform> databasePlatformClass) {
		AdjustArrayTypeCustomizer.databasePlatformClass = databasePlatformClass;
	}

	@Override
	public void customize(ClassDescriptor descriptor) throws Exception {

		Iterable<DatabaseMapping> mappings = descriptor.getMappings();
		for (DatabaseMapping mapping : mappings) {
			if (mapping.isDirectToFieldMapping()) {
				adjustDatabaseField(mapping.getField());
			}
			if (mapping.isOneToOneMapping()) {
				OneToOneMapping oneToOneMapping = (OneToOneMapping) mapping;
				for (DatabaseField field : oneToOneMapping
						.getForeignKeyFields()) {
					adjustDatabaseField(field);
				}
			} else if (mapping.isManyToManyMapping()) {
				ManyToManyMapping manyToManyMapping = (ManyToManyMapping) mapping;
				for (DatabaseField field : manyToManyMapping
						.getSourceRelationKeyFields()) {
					adjustDatabaseField(field);
				}
				for (DatabaseField field : manyToManyMapping
						.getTargetRelationKeyFields()) {
					adjustDatabaseField(field);
				}
			}
		}
	}

	private void adjustDatabaseField(DatabaseField field) {
		String columnDefintion = field.getColumnDefinition();
		if (columnDefintion != null) {
			if (columnDefintion.startsWith(BINARY)) {
				adjustBinaryType(field, columnDefintion);
			} else if (columnDefintion.startsWith(VARCHAR)) {
				adjustVarcharType(field, columnDefintion);
			}

		}
	}

	private void adjustBinaryType(DatabaseField field, String columnDefintion) {
		final String newDefinition;
		if (OraclePlatform.class.isAssignableFrom(databasePlatformClass)) {
			newDefinition = columnDefintion.replace(BINARY, "RAW");
		} else if (MySQLPlatform.class.isAssignableFrom(databasePlatformClass)) {
			newDefinition = columnDefintion.replace(BINARY, "binary");
		} else if (databasePlatformClass.getName().endsWith("MaxDBPlatform")) {
			newDefinition = columnDefintion.replace(BINARY, "CHAR") + " BYTE";
		} else {
			return;
		}
		field.setColumnDefinition(newDefinition);
	}

	private void adjustVarcharType(DatabaseField field, String columnDefintion) {
		final String newDefinition;
		if (OraclePlatform.class.isAssignableFrom(databasePlatformClass)) {
			newDefinition = columnDefintion.replace(VARCHAR, "VARCHAR2");
		} else if (databasePlatformClass.getName().endsWith("MaxDBPlatform")) {
			newDefinition = columnDefintion + " UNICODE";
		} else {
			return;
		}
		field.setColumnDefinition(newDefinition);
	}

}
