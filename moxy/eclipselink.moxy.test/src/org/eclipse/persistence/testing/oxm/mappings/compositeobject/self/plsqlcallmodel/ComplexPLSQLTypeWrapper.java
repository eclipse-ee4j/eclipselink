package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public class ComplexPLSQLTypeWrapper extends DatabaseTypeWrapper {

    public ComplexPLSQLTypeWrapper() {
        super();
    }

    public ComplexPLSQLTypeWrapper(DatabaseType wrappedType) {
        super(wrappedType);
    }

    public ComplexDatabaseType getWrappedType() {
        return (ComplexDatabaseType)wrappedDatabaseType;
    }
}