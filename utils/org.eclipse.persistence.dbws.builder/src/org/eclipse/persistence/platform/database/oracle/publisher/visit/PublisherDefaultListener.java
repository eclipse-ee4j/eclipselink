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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.visit;

import java.util.ArrayList;
import java.util.List;

public class PublisherDefaultListener implements PublisherListener {

    public void beginPackage(String packageName) {
    }
    public void endPackage() {
    }

    public void beginPlsqlTable(String tableName, String targetTypeName) {
    }
    public void endPlsqlTable(String tableName, String typeDDL, String typeDropDDL) {
    }

    public void beginPlsqlRecord(String recordName, String targetTypeName , int numFields) {
    }
    public void beginPlsqlRecordField(String fieldName, int idx) {
    }
    public void endPlsqlRecordField(String fieldName, int idx) {
    }
    public void endPlsqlRecord(String recordName, String typeDDL, String typeDropDDL) {
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

    public void beginObjectType(String objectTypeName) {
    }
    public void handleObjectType(String objectTypeName, String targetTypeName, int numAttributes) {
    }
    public void endObjectType(String objectTypeName) {
    }

    public void handleSqlType(String sqlTypeName, int typecode, String targetTypeName) {
    }

    public void handleSqlArrayType(String name, String targetTypeName) {
    }

    public void handleSqlTableType(String tableTypeName, String targetTypeName) {
    }

    public void handleAttributeField(String attributeFieldName, int idx) {
    }

    public String trimDotPrefix(String prefix) {
        String trimmedPrefix = prefix;
        int dotIdx = trimmedPrefix.indexOf('.');
        if (dotIdx > -1) {
            trimmedPrefix = trimmedPrefix.substring(dotIdx+1);
        }
        return trimmedPrefix;
    }

    public static interface ListenerHelper {
        public String targetTypeName();
        public boolean isComplex();
        public boolean isTable();
        public boolean isRecord();
        public boolean isObject();
        public boolean isArray();
        public boolean isAttribute();
        public boolean isMethod();
        public boolean isMethodArg();
        public boolean isReturnArg();
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
        public boolean isObject() {
            return false;
        }
        public boolean isArray() {
            return false;
        }
        public boolean isAttribute() {
            return false;
        }
        public boolean isMethod() {
            return false;
        }
        public boolean isMethodArg() {
            return false;
        }
        public boolean isReturnArg() {
            return false;
        }
    }

    public static class SqltypeHelper extends DefaultListenerHelper {
        protected String sqlTypeName;
        public SqltypeHelper(String sqlTypeName) {
            super(null);
            this.sqlTypeName = sqlTypeName;
        }
        public String sqlTypeName() {
            return sqlTypeName;
        }
        public void setSqlTypeName(String sqlTypeName) {
            this.sqlTypeName = sqlTypeName;
        }
        @Override
        public String toString() {
            return "{" + sqlTypeName + "}";
        }
    }
    public static class SqlArrayTypeHelper extends DefaultListenerHelper {
        protected String arrayTypename;
        public SqlArrayTypeHelper(String arrayTypename, String targetTypeName) {
            super(targetTypeName);
            this.arrayTypename = arrayTypename;
        }
        public String arrayTypename() {
            return arrayTypename;
        }
        public void setArrayTypename(String arrayTypename) {
            this.arrayTypename = arrayTypename;
        }
        @Override
        public boolean isArray() {
            return true;
        }
        @Override
        public String toString() {
            return "{" + arrayTypename + "}";
        }
    }

    public static class ObjectTypeHelper extends DefaultListenerHelper {
        protected String objectTypename;
        protected int numAttributes;
        public ObjectTypeHelper(String objectTypename, String targetTypeName, int numAttributes) {
            super(targetTypeName);
            this.objectTypename = objectTypename;
            this.numAttributes = numAttributes;
        }
        public String objectTypename() {
            return objectTypename;
        }
        public String targetTypeName() {
            return targetTypeName;
        }
        public void setTargetTypeName(String targetTypeName) {
           this.targetTypeName = targetTypeName;
        }
        public int numAttributes() {
            return numAttributes;
        }
        public void setNumAttributes(int numAttributes) {
            this.numAttributes = numAttributes;
         }
        @Override
        public boolean isObject() {
            return true;
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
        public int decrNumAttributes() {
            return --numAttributes;
        }
    }

    public static class TableHelper extends DefaultListenerHelper {
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

    public static class RecordHelper extends DefaultListenerHelper {
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

    public static class AttributeFieldHelper extends SqltypeHelper {
        String attributeFieldName;
        public AttributeFieldHelper(String attributeFieldName, String sqlTypeName) {
            super(sqlTypeName);
            this.attributeFieldName = attributeFieldName;
        }
        public String attributeFieldName() {
            return attributeFieldName;
        }
        @Override
        public boolean isAttribute() {
            return true;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append(attributeFieldName);
            sb.append(":");
            sb.append(sqlTypeName);
            sb.append("}");
            return sb.toString();
        }
    }

    public static class MethodHelper extends DefaultListenerHelper {
        int numArgs;
        boolean isFunc;
        List<MethodArgHelper> args;
        public MethodHelper(String methodName, int numArgs) {
            super(methodName);
            this.numArgs = numArgs;
            args = new ArrayList<MethodArgHelper>(numArgs+1);
        }
        public String methodName() {
            return targetTypeName;
        }
        public int numArgs() {
            return numArgs;
        }
        public boolean isFunc() {
            return isFunc;
        }
        public void setFunc(boolean isFunc) {
            this.isFunc = isFunc;
        }
        public List<MethodArgHelper> args() {
            return args;
        }
        @Override
        public boolean isMethod() {
            return true;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int idx = 0;
            int len = args.size();
            if (isFunc) {
                if (len > 0) {
                    idx++;
                }
                sb.append("function ");
            }
            else {
                sb.append("procedure ");
            }
            sb.append(targetTypeName);
            sb.append("(");
            for (int i = idx; i < len; i++) {
                sb.append(args.get(i));
                if (i < len -1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            if (isFunc) {
                sb.append(" returns ");
                if (len > 0) {
                    MethodArgHelper returnArg = args.get(0);
                    sb.append(returnArg.sqlTypeName());
                    if (returnArg.isComplex) {
                        sb.append("{C}");
                    }
                }
            }
            return sb.toString();
        }
    }

    public static class MethodArgHelper extends SqltypeHelper {
        String argName;
        String direction = null;
        int typecode;
        String typeName;
        String nestedType = null;
        int nestedTypecode;
        String nestedTypeName;
        boolean isComplex = false;
        public MethodArgHelper(String argName, String sqlTypeName) {
            super(sqlTypeName);
            this.argName = argName;
        }
        public String argName() {
            return argName;
        }
        public void setDirection(String direction) {
            this.direction = direction;
        }
        public String direction() {
            return direction;
        }
        public int typecode() {
            return typecode;
        }
        public void setTypecode(int typecode) {
            this.typecode = typecode;
        }
        public String typeName() {
            return typeName;
        }
        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
        public void setNestedType(String nestedType) {
            this.nestedType = nestedType;
        }
        public String nestedType() {
            return nestedType;
        }
        public int nestedTypecode() {
            return nestedTypecode;
        }
        public void setNestedTypecode(int nestedTypecode) {
            this.nestedTypecode = nestedTypecode;
        }
        public String nestedTypeName() {
            return nestedTypeName;
        }
        public void setNestedTypeName(String nestedTypeName) {
            this.nestedTypeName = nestedTypeName;
        }
        @Override
        public boolean isComplex() {
            return isComplex;
        }
        public void setIsComplex(boolean isComplex) {
            this.isComplex = isComplex;
        }
        @Override
        public boolean isMethodArg() {
            return true;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (argName != null && argName.length() > 0) {
                sb.append(argName);
            }
            sb.append(" ");
            if (direction != null) {
                sb.append(direction);
                sb.append(" ");
            }
            sb.append(sqlTypeName);
            if (isComplex) {
                sb.append("{C}");
            }
            return sb.toString();
        }
    }

    public static class ReturnArgHelper extends MethodArgHelper {
        public ReturnArgHelper(String argName, String sqlTypeName) {
            super(argName, sqlTypeName);
        }
        @Override
        public boolean isMethodArg() {
            return false;
        }
        @Override
        public boolean isReturnArg() {
            return true;
        }
    }
}