package org.eclipse.persistence.tools.dbws;

public class ToLowerTransformer extends DefaultNamingConventionTransformer {

    @Override
    protected boolean isDefaultTransformer() {
        return true;
    }

    @Override
    public String generateSchemaAlias(String tableName) {
        return super.generateSchemaAlias(tableName.toLowerCase());
    }

    @Override
    public String generateElementAlias(String originalElementName) {
        return super.generateElementAlias(originalElementName.toLowerCase());
    }

    @Override
    public String toString() {
        return "ToLowerTransformer converts strings to their lowercase";
    }
}