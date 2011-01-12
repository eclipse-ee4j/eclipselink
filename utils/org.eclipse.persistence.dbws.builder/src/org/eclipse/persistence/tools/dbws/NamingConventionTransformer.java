package org.eclipse.persistence.tools.dbws;

public interface NamingConventionTransformer {

    public enum ElementStyle {
        ELEMENT, ATTRIBUTE, NONE
    };

    public String generateSchemaAlias(String tableName);

    public String generateElementAlias(String originalElementName);

    public ElementStyle styleForElement(String originalElementName);

    public static final String DEFAULT_OPTIMISTIC_LOCKING_FIELD = "VERSION";
    public String getOptimisticLockingField();
}
