package org.eclipse.persistence.tools.dbws.oracle;

//javase imports
import java.util.HashMap;
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.FieldType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCollectionType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLPackageType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLRecordType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLType;
import org.eclipse.persistence.tools.oracleddl.metadata.SizedType;
import org.eclipse.persistence.tools.oracleddl.metadata.VarChar2Type;
import org.eclipse.persistence.tools.oracleddl.metadata.visit.BaseDatabaseTypeVisitor;
import static org.eclipse.persistence.internal.helper.Helper.NL;
import static org.eclipse.persistence.tools.dbws.Util.NUMERIC_STR;
import static org.eclipse.persistence.tools.dbws.Util.getJDBCTypeFromTypeName;
import static org.eclipse.persistence.tools.dbws.Util.getJDBCTypeNameFromType;

public class ShadowDDLGenerator {

    protected Map<String, DDLWrapper> createDDLs = new HashMap<String, DDLWrapper>();
    protected Map<String, DDLWrapper> dropDDLs = new HashMap<String, DDLWrapper>();
    protected PLSQLPackageType plsqlPackageType;

    public ShadowDDLGenerator(PLSQLPackageType plsqlPackageType) {
        this.plsqlPackageType = plsqlPackageType;
        // visit
        ddlWrapperGeneratorVisitor visitor = new ddlWrapperGeneratorVisitor();
        plsqlPackageType.accept(visitor);
    }

    public String getCreateDDLFor(String typeName) {
        return createDDLs.get(typeName).ddl;
    }

    public String getDropDDLFor(String typeName) {
        return dropDDLs.get(typeName).ddl;
    }

    class ddlWrapperGeneratorVisitor extends BaseDatabaseTypeVisitor {
        static final char COMMA = ',';
        static final char SEMICOLON = ';';
        static final char UNDERSCORE = '_';
        static final char SINGLE_SPACE = ' ';
        static final String COR_TYPE = "CREATE OR REPLACE TYPE ";
        static final String DROP_TYPE = "DROP TYPE ";
        static final String FORCE = " FORCE";
        static final String LBRACKET = "(";
        static final String RBRACKET = ")";
        static final String SPACES = "      ";
        static final String AS_OBJECT = " AS OBJECT (";
        static final String AS_TABLE_OF = " AS TABLE OF ";
        static final String END_OBJECT = ");";
        protected PLSQLRecordType currentRecordType;
        @Override
        public void beginVisit(PLSQLRecordType recordType) {
            DDLWrapper ddlWrapper = createDDLs.get(recordType.getTypeName());
            if (ddlWrapper == null) {
                ddlWrapper = new DDLWrapper();
                currentRecordType = recordType;
                ddlWrapper.ddl = COR_TYPE + recordType.getParentType().getPackageName() + UNDERSCORE +
                    recordType.getTypeName().toUpperCase() + AS_OBJECT;
                createDDLs.put(recordType.getTypeName(), ddlWrapper);
            }
            else {
                currentRecordType = null;
            }
        }
        @Override
        public void endVisit(PLSQLRecordType recordType) {
            DDLWrapper ddlWrapper = createDDLs.get(recordType.getTypeName());
            if (ddlWrapper != null && !ddlWrapper.finished) {
                ddlWrapper.ddl += END_OBJECT;
                ddlWrapper.finished = true;
            }
            ddlWrapper = new DDLWrapper();
            ddlWrapper.ddl = DROP_TYPE + recordType.getParentType().getPackageName() + UNDERSCORE +
                recordType.getTypeName().toUpperCase() + FORCE + SEMICOLON;
            ddlWrapper.finished = true;
            dropDDLs.put(recordType.getTypeName(), ddlWrapper);
        }

        @Override
        public void beginVisit(FieldType fieldType) {
            if (currentRecordType != null) {
                DDLWrapper ddlWrapper = createDDLs.get(currentRecordType.getTypeName());
                if (!ddlWrapper.finished) {
                    String fieldName = fieldType.getFieldName();
                    for (int i = 0, len = currentRecordType.getFields().size(); i < len; i++) {
                        FieldType f = currentRecordType.getFields().get(i);
                        if (fieldName.equals(f.getFieldName())) {
                            ddlWrapper.ddl += SPACES + fieldName + SINGLE_SPACE;
                            DatabaseType fieldDataType = f.getDataType();
                            String fieldShadowName = null;
                            if (fieldDataType instanceof PLSQLType) {
                                fieldShadowName = ((PLSQLType)fieldDataType).getParentType().
                                    getPackageName() + UNDERSCORE + fieldDataType.
                                    getTypeName().toUpperCase();
                            }
                            else {
                                fieldShadowName = getShadowTypeName(f.getDataType().getTypeName());
                                if (fieldDataType instanceof VarChar2Type) {
                                    fieldShadowName = VarChar2Type.TYPENAME;
                                }
                                if (fieldDataType instanceof SizedType) {
                                    SizedType sFieldDataType = (SizedType)fieldDataType;
                                    long defaultSize = sFieldDataType.getDefaultSize();
                                    long size = sFieldDataType.getSize();
                                    if (size != defaultSize) {
                                        fieldShadowName += LBRACKET + size + RBRACKET;
                                    }
                                }
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

        @Override
        public void beginVisit(PLSQLCollectionType collectionType) {
            DDLWrapper ddlWrapper = createDDLs.get(collectionType.getTypeName());
            if (ddlWrapper == null) {
                ddlWrapper = new DDLWrapper();
            }
            if (!ddlWrapper.finished) {
                ddlWrapper.ddl = COR_TYPE + collectionType.getParentType().getPackageName() +
                    UNDERSCORE + collectionType.getTypeName().toUpperCase() + AS_TABLE_OF +
                    getShadowTypeName(collectionType.getNestedType().getTypeName()) + SEMICOLON;
                ddlWrapper.finished = true;
                createDDLs.put(collectionType.getTypeName(),ddlWrapper);
            }
        }
        @Override
        public void endVisit(PLSQLCollectionType collectionType) {
            DDLWrapper ddlWrapper = new DDLWrapper();
            ddlWrapper.ddl = DROP_TYPE + collectionType.getParentType().getPackageName() + UNDERSCORE +
                collectionType.getTypeName().toUpperCase() + FORCE + SEMICOLON;
            ddlWrapper.finished = true;
            dropDDLs.put(collectionType.getTypeName(), ddlWrapper);
        }

        protected String getShadowTypeName(String typeName) {
            int typ = getJDBCTypeFromTypeName(typeName);
            String shadowTypeName = getJDBCTypeNameFromType(typ);
            if (NUMERIC_STR.equals(shadowTypeName)) {
                shadowTypeName = "NUMBER";
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