package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public class JDBCTypeWrapper extends DatabaseTypeWrapper {

    public JDBCTypeWrapper() {
        super();
    }

    public JDBCTypeWrapper(DatabaseType wrappedType) {
        super(wrappedType);
    }

    public JDBCType getWrappedType() {
        return (JDBCType)wrappedDatabaseType;
    }
}