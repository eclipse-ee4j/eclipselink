package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

public class DatabaseTypeWrapper {

    protected DatabaseType wrappedDatabaseType;

    public DatabaseTypeWrapper() {
    }

    public DatabaseTypeWrapper(DatabaseType wrappedDatabaseType) {
        this.wrappedDatabaseType = wrappedDatabaseType;
    }

    public DatabaseType getWrappedType() {
        return wrappedDatabaseType;
    }
    public void setWrappedDatabaseType(DatabaseType wrappedDatabaseType) {
        this.wrappedDatabaseType = wrappedDatabaseType;
    }
    
    public boolean equals(Object obj) {
        DatabaseTypeWrapper dWrap = null;
        try {
            dWrap = (DatabaseTypeWrapper) obj;
        } catch (ClassCastException ccex) {
            return false;
        }
        DatabaseType dType = dWrap.getWrappedType();
        if (dType == null) {
            return this.getWrappedType() == null;
        }
        if (this.getWrappedType() == null) {
            return false;
        }
        if (dType.isComplexDatabaseType()) {
            if (!this.getWrappedType().isComplexDatabaseType()) {
                return false;
            }
        } else {
            if (this.getWrappedType().isComplexDatabaseType()) {
                return false;
            }
        }
        return dType.equals(this.getWrappedType());
    }
}