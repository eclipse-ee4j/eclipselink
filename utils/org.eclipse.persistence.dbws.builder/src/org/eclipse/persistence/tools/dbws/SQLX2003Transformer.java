package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.internal.xr.Util.sqlToXmlName;

public class SQLX2003Transformer extends DefaultNamingConventionTransformer {

    @Override
    protected boolean isDefaultTransformer() {
        return true;
    }

    @Override
    public NamingConventionTransformer getNextTransformer() {
        // always the last in the list
        return null;
    }

    @Override
    public String generateSchemaAlias(String tableName) {
        return super.generateSchemaAlias(sqlToXmlName(tableName));
    }

    @Override
    public String generateElementAlias(String originalElementName) {
        return super.generateElementAlias(sqlToXmlName(originalElementName));
    }

    @Override
    public String toString() {
        return "SQLX2003Transformer converts strings to conform to SQL/X 2003 standards";
    }
}