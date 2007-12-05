package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public class SimplePLSQLTypeWrapper extends DatabaseTypeWrapper {

    public SimplePLSQLTypeWrapper() {
        super();
    }

    public SimplePLSQLTypeWrapper(DatabaseType wrappedType) {
        super(wrappedType);
    }

    public OraclePLSQLType getWrappedType() {
        return (OraclePLSQLType)wrappedDatabaseType;
    }
}