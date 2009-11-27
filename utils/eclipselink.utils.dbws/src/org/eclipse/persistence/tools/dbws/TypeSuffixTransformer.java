package org.eclipse.persistence.tools.dbws;

public class TypeSuffixTransformer extends DefaultNamingConventionTransformer {

    @Override
    protected boolean isDefaultTransformer() {
        return true;
    }

    @Override
    public String generateSchemaAlias(String tableName) {
        return super.generateSchemaAlias(tableName.concat("Type"));
    }

    @Override
    public String toString() {
        return "TypeSuffixTransformer adds suffix 'Type' to high-level schema type";
    }
}