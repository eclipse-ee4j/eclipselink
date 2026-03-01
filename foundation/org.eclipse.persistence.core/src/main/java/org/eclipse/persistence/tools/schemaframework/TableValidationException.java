/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     11/29/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.tools.schemaframework;

import java.util.List;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * Thrown when database table validation fails.
 * Instances of this {@link Exception} are passed as {@code jakarta.persistence.SchemaValidationException#getFailures()}
 * for each problem found during the schema validation process.
 */
public abstract class TableValidationException extends Exception {
    private final String table;
    private final ValidationFailure failure;

    private TableValidationException(String message, String table, ValidationFailure type) {
        super(message);
        this.table = table;
        this.failure = type;
    }

    /**
     * Name of the database table which failed the validation.
     *
     * @return name of the database table
     */
    public String getTable() {
        return table;
    }

    /**
     * Validation failure.
     * Specific failure type, e.g. missing table, missing column, surplus column, column definition differs.
     * Specific {@link TableValidationException} instance is returned for each failure type.
     *
     * @return the validation failure
     */
    public ValidationFailure getFailure() {
        return failure;
    }

    /**
     * Unwrap {@link TableValidationException} as specific subtype.
     * The specific {@link TableValidationException} subtype depends on the {@link ValidationFailure} type.
     *
     * @param type the {@link TableValidationException} specific subtype matching the {@link ValidationFailure} type.
     * @return the  {@link TableValidationException} cast to the specific subtype
     * @param <T> the {@link TableValidationException} subtype class
     */
    public abstract <T extends TableValidationException> T unwrap(Class<T> type);

    /**
     * Thrown when database table validation fails with the table missing in the database.
     * This {@link TableValidationException} specific subtype is matching the {@link ValidationFailure#MISSING_TABLE} type.
     */
    public static final class MissingTable extends TableValidationException {

        MissingTable(String table) {
            super(ExceptionLocalization.buildMessage(
                    "schema_validation_missing_table", new String[] {table}),
                  table, ValidationFailure.MISSING_TABLE);
        }

        @Override
        public <T extends TableValidationException> T unwrap(Class<T> cls) {
            if (cls == MissingTable.class) {
                return cls.cast(this);
            }
            throw new IllegalArgumentException(
                    String.format("Cannot cast this TableValidationException.MissingTable implementation as %s", cls.getName()));
        }

    }

    /**
     * Thrown when database table validation fails with one or more table's columns missing in the database.
     * This {@link TableValidationException} specific subtype is matching the {@link ValidationFailure#MISSING_COLUMNS} type.
     */
    public static final class MissingColumns extends TableValidationException {

        private final List<String> columns;

        MissingColumns(String table, List<String> columns) {
            super(ExceptionLocalization.buildMessage(
                          "schema_validation_table_missing_columns", new String[] {table}),
                  table, ValidationFailure.MISSING_COLUMNS);
            this.columns = columns;
        }

        /**
         * List of the missing database columns names.
         *
         * @return missing columns names
         */
        public List<String> getColumns() {
            return columns;
        }

        @Override
        public <T extends TableValidationException> T unwrap(Class<T> cls) {
            if (cls == MissingColumns.class) {
                return cls.cast(this);
            }
            throw new IllegalArgumentException(
                    String.format("Cannot cast this TableValidationException.MissingColumns implementation as %s", cls.getName()));
        }

    }

    /**
     * Thrown when database table validation fails with one or more table's surplus columns in the database.
     * This {@link TableValidationException} specific subtype is matching the {@link ValidationFailure#SURPLUS_COLUMNS} type.
     */
    public static final class SurplusColumns extends TableValidationException {

        private final List<String> columns;

        SurplusColumns(String table, List<String> columns) {
            super(ExceptionLocalization.buildMessage(
                          "schema_validation_table_surplus_columns", new String[] {table}),
                  table, ValidationFailure.SURPLUS_COLUMNS);
            this.columns = columns;
        }

        /**
         * List of the surplus database columns names.
         *
         * @return surplus columns names
         */
        public List<String> getColumns() {
            return columns;
        }

        @Override
        public <T extends TableValidationException> T unwrap(Class<T> cls) {
            if (cls == SurplusColumns.class) {
                return cls.cast(this);
            }
            throw new IllegalArgumentException(
                    String.format("Cannot cast this TableValidationException.SurplusColumns implementation as %s", cls.getName()));
        }

    }

    /**
     * Thrown when database table validation fails with one or more table's columns have different definition in the database.
     * This {@link TableValidationException} specific subtype is matching the {@link ValidationFailure#DIFFERENT_COLUMNS} type.
     */
    public static final class DifferentColumns extends TableValidationException {

        private final List<Difference> differences;

        DifferentColumns(String table, List<Difference> columns) {
            super(ExceptionLocalization.buildMessage(
                          "schema_validation_table_different_columns", new String[] {table}),
                  table, ValidationFailure.DIFFERENT_COLUMNS);
            this.differences = columns;
        }

        /**
         * List of columns differences description.
         *
         * @return columns differences description
         */
        public List<Difference> getDifferences() {
            return differences;
        }

        @Override
        public <T extends TableValidationException> T unwrap(Class<T> cls) {
            if (cls == DifferentColumns.class) {
                return cls.cast(this);
            }
            throw new IllegalArgumentException(
                    String.format("Cannot cast this TableValidationException.DifferentColumns implementation as %s", cls.getName()));
        }

        /**
         * Column difference description.
         */
        public static abstract class Difference {

            private final String columnName;
            private final Type type;

            private Difference(String columnName, Type type) {
                this.columnName = columnName;
                this.type = type;
            }

            /**
             * Unwrap {@link Difference} as specific subtype.
             * The specific {@link Difference} subtype depends on the {@link Type}.
             *
             * @param type the {@link Difference} specific subtype matching the {@link Type}.
             * @return the {@link Difference} cast to the specific subtype
             * @param <T> the {@link Difference} subtype class
             */
            public abstract <T extends Difference> T unwrap(Class<T> type);

            /**
             * Name of the database column.
             *
             * @return name of the database column
             */
            public String getColumnName() {
                return columnName;
            }

            /**
             * Type of the difference in the column definition.
             * E.g. E.g. type difference or nullable difference. Each specific difference has its own {@link Difference} subtype.
             *
             * @return type of the difference
             */
            public Type getType() {
                return type;
            }

        }

        /**
         * Column type difference description.
         */
        public static class TypeDifference extends Difference {

            private final String modelValue;
            private final String dbValue;

            TypeDifference(String columnName, String modelValue, String dbValue) {
                super(columnName, Type.TYPE_DIFFERENCE);
                this.dbValue = dbValue;
                this.modelValue = modelValue;
            }

            @Override
            public <T extends Difference> T unwrap(Class<T> cls) {
                if (cls == TypeDifference.class) {
                    return cls.cast(this);
                }
                throw new IllegalArgumentException(
                        String.format("Cannot cast this Difference.TypeDifference implementation as %s", cls.getName()));
            }

            /**
             * Column type name from the database.
             *
             * @return column type name
             */
            public String getDbValue() {
                return dbValue;
            }

            /**
             * Column type name from the {@code jakarta.persistence.Entity} model.
             *
             * @return column type name
             */
            public String getModelValue() {
                return modelValue;
            }

        }

        public static class NullableDifference extends Difference {

            private final boolean modelNullable;
            private final boolean dbNullable;

            NullableDifference(String columnName, boolean modelNullable, boolean dbNullable) {
                super(columnName, Type.NULLABLE_DIFFERENCE);
                this.dbNullable = dbNullable;
                this.modelNullable = modelNullable;
            }

            @Override
            public <T extends Difference> T unwrap(Class<T> cls) {
                if (cls == NullableDifference.class) {
                    return cls.cast(this);
                }
                throw new IllegalArgumentException(
                        String.format("Cannot cast this Difference.NullableDifference implementation as %s", cls.getName()));
            }

            /**
             * Whether database column type definition allows {@code NULL} values.
             *
             * @return type definition allows {@code NULL} values then {@code true}
             */
            public boolean isDbNullable() {
                return dbNullable;
            }

            /**
             * Whether {@code jakarta.persistence.Entity} model column type definition allows {@code NULL} values.
             *
             * @return type definition allows {@code NULL} values then {@code true}
             */
            public boolean isModelNullable() {
                return modelNullable;
            }

        }

        /**
         * Type of the difference.
         * E.g. type difference or nullable difference.
         */
        public enum Type {
            /** Type difference. Type definition differs in SQL type. */
            TYPE_DIFFERENCE(TypeDifference.class),
            /** Nullable difference. Type definition differs in allowing {@code NULL} values. */
            NULLABLE_DIFFERENCE(NullableDifference.class);

            private final Class<? extends Difference> differenceClass;

            Type(Class<? extends Difference> differenceClass) {
                this.differenceClass = differenceClass;
            }

            /**
             * Returns {@link Difference} subtype matching this {@link Type}.
             *
             * @return {@link Difference} subtype
             */
            public Class<? extends Difference> differenceClass() {
                return differenceClass;
            }

        }

    }

    /**
     * Validation failure.
     */
    public enum ValidationFailure {
        /** Missing table in the schema. */
        MISSING_TABLE(MissingTable.class),
        /** Table with missing columns in the schema. */
        MISSING_COLUMNS(MissingColumns.class),
        /** Table with surplus columns in the schema. */
        SURPLUS_COLUMNS(SurplusColumns.class),
        /** Table with different columns in the schema. */
        DIFFERENT_COLUMNS(DifferentColumns.class);

        private final Class<? extends TableValidationException> exceptionClass;

        ValidationFailure(Class<? extends TableValidationException> exceptionClass) {
            this.exceptionClass = exceptionClass;
        }

        /**
         * Returns {@link TableValidationException} subtype matching this {@link ValidationFailure}.
         *
         * @return {@link TableValidationException} subtype
         */
        public  Class<? extends TableValidationException> exceptionClass() {
            return exceptionClass;
        }

    }

}
