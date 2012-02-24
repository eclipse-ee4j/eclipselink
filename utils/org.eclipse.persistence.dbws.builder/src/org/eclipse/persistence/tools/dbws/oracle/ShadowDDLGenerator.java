/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman: Jan 2012 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

//EclipseLink imports
import org.eclipse.persistence.tools.oracleddl.metadata.CharType;
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.FieldType;
import org.eclipse.persistence.tools.oracleddl.metadata.NumericType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCollectionType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLPackageType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLRecordType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLType;
import org.eclipse.persistence.tools.oracleddl.metadata.PrecisionType;
import org.eclipse.persistence.tools.oracleddl.metadata.RawType;
import org.eclipse.persistence.tools.oracleddl.metadata.ScalarDatabaseTypeEnum;
import org.eclipse.persistence.tools.oracleddl.metadata.SizedType;
import org.eclipse.persistence.tools.oracleddl.metadata.VarChar2Type;
import org.eclipse.persistence.tools.oracleddl.metadata.VarCharType;
import org.eclipse.persistence.tools.oracleddl.metadata.visit.BaseDatabaseTypeVisitor;
import static org.eclipse.persistence.internal.helper.Helper.NL;
import static org.eclipse.persistence.tools.dbws.Util.FLOAT_STR;
import static org.eclipse.persistence.tools.dbws.Util.INTEGER_STR;
import static org.eclipse.persistence.tools.dbws.Util.NUMERIC_STR;
import static org.eclipse.persistence.tools.dbws.Util.OTHER_STR;
import static org.eclipse.persistence.tools.dbws.Util.getJDBCTypeFromTypeName;
import static org.eclipse.persistence.tools.dbws.Util.getJDBCTypeNameFromType;

public class ShadowDDLGenerator {

    protected Map<PLSQLType, DDLWrapper> createDDLs = new HashMap<PLSQLType, DDLWrapper>();
    protected Map<PLSQLType, DDLWrapper> dropDDLs = new HashMap<PLSQLType, DDLWrapper>();

    public ShadowDDLGenerator(PLSQLPackageType plsqlPackageType) {
        // visit all the PLSQLType's
        DDLWrapperGeneratorVisitor visitor = new DDLWrapperGeneratorVisitor();
        plsqlPackageType.accept(visitor);
    }

    public String getCreateDDLFor(PLSQLType plsqlType) {
        return createDDLs.get(plsqlType).ddl;
    }
    public List<String> getAllCreateDDLs() {
        List<String> allDDLs = new ArrayList<String>();
        for (Map.Entry<PLSQLType, DDLWrapper> me : createDDLs.entrySet()) {
            allDDLs.add(me.getValue().ddl);
        }
        return allDDLs;
    }

    public String getDropDDLFor(PLSQLType plsqlType) {
        return dropDDLs.get(plsqlType).ddl;
    }
    public List<String> getAllDropDDLs() {
        List<String> allDDLs = new ArrayList<String>();
        for (Map.Entry<PLSQLType, DDLWrapper> me : dropDDLs.entrySet()) {
            allDDLs.add(me.getValue().ddl);
        }
        return allDDLs;
    }

    class DDLWrapperGeneratorVisitor extends BaseDatabaseTypeVisitor {
        static final String COMMA = ",";
        static final String SEMICOLON = ";";
        static final String UNDERSCORE = "_";
        static final String SINGLE_SPACE = " ";
        static final String COR_TYPE = "CREATE OR REPLACE TYPE ";
        static final String DROP_TYPE = "DROP TYPE ";
        static final String FORCE = " FORCE";
        static final String LBRACKET = "(";
        static final String RBRACKET = ")";
        static final String SPACES = "      ";
        static final String AS_OBJECT = " AS OBJECT (";
        static final String AS_TABLE_OF = " AS TABLE OF ";
        static final String END_OBJECT = ");";
        static final String NUMBER_STR = "NUMBER";
        static final String ROWID_STR = "VARCHAR2(256)";
        static final String NUMBER_DEFAULTSIZE = "38";
        static final String FLOAT_DEFAULTSIZE = "63";
        static final String DOUBLE_DEFAULTSIZE = "126";
        static final String LONG_DEFAULTSIZE = "32760";
        protected Stack<PLSQLRecordType> recordTypes = new Stack<PLSQLRecordType>();
        @Override
        public void beginVisit(PLSQLRecordType recordType) {
            if (recordTypes.isEmpty()) {
                recordTypes.push(recordType);
            }
            else if (!recordTypes.peek().equals(recordType)) {
                recordTypes.push(recordType);
            }
            DDLWrapper ddlWrapper = createDDLs.get(recordType);
            if (ddlWrapper == null) {
                ddlWrapper = new DDLWrapper();
                ddlWrapper.ddl = COR_TYPE + recordType.getParentType().getPackageName() + UNDERSCORE +
                    recordType.getTypeName().toUpperCase() + AS_OBJECT;
                createDDLs.put(recordType, ddlWrapper);
            }
        }
        @Override
        public void endVisit(PLSQLRecordType recordType) {
            recordTypes.pop();
            DDLWrapper ddlWrapper = createDDLs.get(recordType);
            if (ddlWrapper != null && !ddlWrapper.finished) {
                ddlWrapper.ddl += END_OBJECT;
                ddlWrapper.finished = true;
            }
            ddlWrapper = dropDDLs.get(recordType);
            if (ddlWrapper == null) {
                ddlWrapper = new DDLWrapper();
                ddlWrapper.ddl = DROP_TYPE + recordType.getParentType().getPackageName() + UNDERSCORE +
                    recordType.getTypeName().toUpperCase() + FORCE + SEMICOLON;
                ddlWrapper.finished = true;
                dropDDLs.put(recordType, ddlWrapper);
            }
        }
        @Override
        public void beginVisit(FieldType fieldType) {
        	//skip fields from ObjectType's
        	if (!recordTypes.isEmpty()) {
        		PLSQLRecordType currentRecordType = recordTypes.peek();
	            if (currentRecordType != null) {
	                DDLWrapper ddlWrapper = createDDLs.get(currentRecordType);
	                if (!ddlWrapper.finished) {
	                    String fieldName = fieldType.getFieldName();
	                    for (int i = 0, len = currentRecordType.getFields().size(); i < len; i++) {
	                        FieldType f = currentRecordType.getFields().get(i);
	                        if (fieldName.equals(f.getFieldName())) {
	                            ddlWrapper.ddl += SPACES + fieldName + SINGLE_SPACE;
	                            DatabaseType fieldDataType = f.getEnclosedType();
	                            String fieldShadowName = null;
	                            if (fieldDataType.isPLSQLType()) {
	                                fieldShadowName = ((PLSQLType)fieldDataType).getParentType().
	                                    getPackageName() + UNDERSCORE + fieldDataType.
	                                    getTypeName().toUpperCase();
	                            }
	                            else {
	                                fieldShadowName = getShadowTypeName(fieldDataType);
	                            }
	                            ddlWrapper.ddl += fieldShadowName;
	                            if (i < len -1) {
	                                ddlWrapper.ddl += COMMA + NL;
	                            }
	                            break;
	                        }
	                    }
	                }
	            }
        	}
        }
        @Override
        public void beginVisit(PLSQLCollectionType collectionType) {
            DDLWrapper ddlWrapper = createDDLs.get(collectionType);
            if (ddlWrapper == null) {
                ddlWrapper = new DDLWrapper();
            }
            if (!ddlWrapper.finished) {
                String shadowNestedTypeName = null;
                DatabaseType nestedType = collectionType.getEnclosedType();
                if (nestedType.isNumericType()) {
                    NumericType nDataType = (NumericType)nestedType;
                    if (nDataType.isNumberSynonym()) {
                        shadowNestedTypeName = NUMBER_STR;
                    }
                    else {
                        shadowNestedTypeName = NUMERIC_STR;
                    }
                    PrecisionType pDataType = (PrecisionType)nestedType;
                    long defaultPrecision = pDataType.getDefaultPrecision();
                    long precision = pDataType.getPrecision();
                    long scale = pDataType.getScale();
                    if (precision != defaultPrecision) {
                        shadowNestedTypeName += LBRACKET + precision;
                        if (scale != 0) {
                            shadowNestedTypeName += COMMA + SINGLE_SPACE + scale;
                        }
                        shadowNestedTypeName += RBRACKET;
                    }
                }
                else {
                    shadowNestedTypeName = getShadowTypeName(nestedType);
                }
                ddlWrapper.ddl = COR_TYPE + collectionType.getParentType().getPackageName() +
                    UNDERSCORE + collectionType.getTypeName().toUpperCase() + AS_TABLE_OF +
                        shadowNestedTypeName + SEMICOLON;
                ddlWrapper.finished = true;
                createDDLs.put(collectionType, ddlWrapper);
            }
        }
        @Override
        public void endVisit(PLSQLCollectionType collectionType) {
            DDLWrapper ddlWrapper = new DDLWrapper();
            ddlWrapper.ddl = DROP_TYPE + collectionType.getParentType().getPackageName() + UNDERSCORE +
                collectionType.getTypeName().toUpperCase() + FORCE + SEMICOLON;
            ddlWrapper.finished = true;
            dropDDLs.put(collectionType, ddlWrapper);
        }
        protected String getShadowTypeName(DatabaseType dataType ) {
            String shadowTypeName = null;
            if (dataType == ScalarDatabaseTypeEnum.INTEGER_TYPE ||
                dataType == ScalarDatabaseTypeEnum.SMALLINT_TYPE) {
                shadowTypeName = NUMBER_STR + LBRACKET + NUMBER_DEFAULTSIZE + RBRACKET;
            }
            else if (dataType == ScalarDatabaseTypeEnum.BINARY_INTEGER_TYPE ||
                dataType == ScalarDatabaseTypeEnum.BOOLEAN_TYPE ||
                dataType == ScalarDatabaseTypeEnum.NATURAL_TYPE ||
                dataType == ScalarDatabaseTypeEnum.PLS_INTEGER_TYPE ||
                dataType == ScalarDatabaseTypeEnum.POSITIVE_TYPE ||
                dataType == ScalarDatabaseTypeEnum.SIGN_TYPE ||
                dataType == ScalarDatabaseTypeEnum.SIMPLE_INTEGER_TYPE) {
                shadowTypeName = INTEGER_STR;
            }
            else if (dataType == ScalarDatabaseTypeEnum.ROWID_TYPE) {
                shadowTypeName = ROWID_STR;
            }
            else if (dataType == ScalarDatabaseTypeEnum.SIMPLE_DOUBLE_TYPE) {
                shadowTypeName = ScalarDatabaseTypeEnum.BINARY_DOUBLE_TYPE.getTypeName();
            }
            else if (dataType == ScalarDatabaseTypeEnum.SIMPLE_FLOAT_TYPE) {
                shadowTypeName = ScalarDatabaseTypeEnum.BINARY_FLOAT_TYPE.getTypeName();
            }
            else if (dataType.isPrecisionType()) {
                PrecisionType pDataType = (PrecisionType)dataType;
                long defaultPrecision = pDataType.getDefaultPrecision();
                String defaultPrecisionStr = NUMBER_DEFAULTSIZE;
                if (dataType.isNumericType() || dataType.isDecimalType()) {
                    shadowTypeName = NUMBER_STR;
                }
                else {
                    shadowTypeName = FLOAT_STR;
                    defaultPrecisionStr = FLOAT_DEFAULTSIZE;
                    if (dataType.isFloatType() || dataType.isDoubleType()) {
                        defaultPrecisionStr = DOUBLE_DEFAULTSIZE;
                    }
                }
                if (!(dataType.isNumericType() && ((NumericType)dataType).isNumberSynonym())) {
                    long precision = pDataType.getPrecision();
                    long scale = pDataType.getScale();
                    if (precision != defaultPrecision) {
                        shadowTypeName += LBRACKET + Long.toString(precision);
                        if (scale != 0) {
                            shadowTypeName += COMMA + SINGLE_SPACE + scale;
                        }
                    }
                    else {
                        shadowTypeName += LBRACKET + defaultPrecisionStr;
                    }
                    shadowTypeName += RBRACKET;
                }
            }
            else if (dataType.isVarCharType()) {
                shadowTypeName = VarChar2Type.TYPENAME;
                VarCharType vcharType = (VarCharType)dataType;
                long defaultSize = vcharType.getDefaultSize();
                long size = vcharType.getSize();
                String sizeStr = Long.toString(size);
                if (dataType.isLongType()) {
                    shadowTypeName = VarChar2Type.TYPENAME;
                    if (size == defaultSize) {
                        sizeStr = LONG_DEFAULTSIZE;
                    }
                }
                shadowTypeName += LBRACKET + sizeStr + RBRACKET;
            }
            else if (dataType.isCharType()) {
                shadowTypeName = CharType.TYPENAME;
                long size = ((CharType)dataType).getSize();
                shadowTypeName += LBRACKET + size + RBRACKET;
            }
            else if (dataType.isSizedType()) {
                shadowTypeName = dataType.getTypeName();
                SizedType sDataType = (SizedType)dataType;
                long defaultSize = sDataType.getDefaultSize();
                String sizeStr = null;
                long size = sDataType.getSize();
                if (dataType.isLongType()) {
                    shadowTypeName = VarChar2Type.TYPENAME;
                    if (size == defaultSize) {
                        sizeStr = LONG_DEFAULTSIZE;
                    }
                    else {
                        sizeStr = Long.toString(size);
                    }
                    shadowTypeName += LBRACKET + sizeStr + RBRACKET;
                }
                else if (dataType.isLongRawType()) {
                    shadowTypeName = RawType.TYPENAME;
                    if (size == defaultSize) {
                        sizeStr = LONG_DEFAULTSIZE;
                    }
                    else {
                        sizeStr = Long.toString(size);
                    }
                    shadowTypeName += LBRACKET + sizeStr + RBRACKET;
                }
                else if (size != defaultSize) {
                    shadowTypeName += LBRACKET + size + RBRACKET;
                }
            }
            else if (dataType.isPLSQLType()) {
                shadowTypeName = ((PLSQLType)dataType).getParentType().getPackageName() +
                    UNDERSCORE + dataType.getTypeName();
            }
            else {
                String typeName = dataType.getTypeName();
                int typ = getJDBCTypeFromTypeName(typeName);
                shadowTypeName = getJDBCTypeNameFromType(typ);
                if (OTHER_STR.equals(shadowTypeName)) {
                    shadowTypeName = typeName;
                }
            }
            return shadowTypeName;
        }
    }
    class DDLWrapper {
        String ddl;
        boolean finished = false;
        @Override
        public String toString() {
            return ddl;
        }
    }
}