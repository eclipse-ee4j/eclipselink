/*******************************************************************************
 * Copyright (c) 1998-2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.visit;

public class PublisherDefaultListener implements PublisherListener {

    public void beginPackage(String packageName) {
    }
    public void endPackage() {
    }

    public void beginPlsqlTable(String tableName, String targetTypeName) {
    }
    public void endPlsqlTable(String tableName) {
    }

    public void beginPlsqlRecord(String recordName, String targetTypeName , int numFields) {
    }
    public void beginPlsqlRecordField(String fieldName, int idx) {
    }
    public void endPlsqlRecordField(String fieldName, int idx) {
    }
    public void endPlsqlRecord(String recordName) {
    }

    public void beginMethod(String methodName, int numArgs) {
    }
    public void handleMethodReturn(String returnTypeName) {
    }
    public void beginMethodArg(String argName, String direction, int idx) {
    }
    public void endMethodArg(String argName) {
    }
    public void endMethod(String methodName) {
    }

    public void handleObjectType(String objectTypeName, String targetTypeName) {
    }

    public void handleSqlType(String sqlTypeName, int typecode, String targetTypeName) {
    }

    public static interface ListenerHelper {
        public String targetTypeName();
        public boolean isComplex();
        public boolean isTable();
        public boolean isRecord();
    }

    public static class DefaultListenerHelper implements ListenerHelper {
        protected String targetTypeName;
        public DefaultListenerHelper(String targetTypeName) {
            this.targetTypeName = targetTypeName;
        }
        public String targetTypeName() {
            return targetTypeName;
        }
        public boolean isComplex() {
            return false;
        }
        public boolean isTable() {
            return false;
        }
        public boolean isRecord() {
            return false;
        }
    }
    
    public static final class SqltypeHelper extends DefaultListenerHelper {
        protected String sqlTypeName;
        public SqltypeHelper(String sqlTypeName) {
            super(null);
            this.sqlTypeName = sqlTypeName;
        }
        public String sqlTypeName() {
            return sqlTypeName;
        }
        @Override
        public String toString() {
            return "{" + sqlTypeName + "}";
        }
    }
    
    public static final class ObjectTypeHelper extends DefaultListenerHelper {
        protected String objectTypename;
        public ObjectTypeHelper(String objectTypename, String targetTypeName) {
            super(targetTypeName);
            this.objectTypename = objectTypename;
        }
        public String objectTypename() {
            return objectTypename;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append(objectTypename);
            sb.append("/");
            sb.append(targetTypeName);
            sb.append("}");
            return sb.toString();
        }
    }
    
    public static final class TableHelper extends DefaultListenerHelper {
        protected String tableName;
        protected String tableAlias;
        protected boolean nestedIsComplex = false;
        public TableHelper(String tableName, String tableAlias, String targetTypeName) {
            super(targetTypeName);
            this.tableName = tableName;
            this.tableAlias = tableAlias;
        }
        public String tableName() {
            return tableName;
        }
        public String tableAlias() {
            return tableAlias;
        }
        public boolean isComplex() {
            return true;
        }
        public boolean isTable() {
            return true;
        }
        public void nestedIsComplex() {
            nestedIsComplex = true;
        }
        public boolean isNestedComplex() {
            return nestedIsComplex;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            if (nestedIsComplex) {
                sb.append("[nC]");
            }
            sb.append(tableAlias);
            sb.append("/");
            sb.append(targetTypeName);
            sb.append("}");
            return sb.toString();
        }
    }
    
    public static final class RecordHelper extends DefaultListenerHelper {
        protected String recordName;
        protected int numFields;
        public RecordHelper(String recordName, String targetTypeName, int numFields) {
            super(targetTypeName);
            this.recordName = recordName;
            this.numFields = numFields;
        }
        public String recordName() {
            return recordName;
        }
        public int numFields() {
            return numFields;
        }
        public boolean isComplex() {
            return true;
        }
        public boolean isRecord() {
            return true;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{[");
            sb.append(numFields);
            sb.append("]");
            sb.append(recordName);
            sb.append("/");
            sb.append(targetTypeName);
            sb.append("}");
            return sb.toString();
        }
    }
}