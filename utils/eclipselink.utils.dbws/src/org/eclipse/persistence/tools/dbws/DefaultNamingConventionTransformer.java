package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.tools.dbws.NamingConventionTransformer.ElementStyle.ELEMENT;

public class DefaultNamingConventionTransformer implements NamingConventionTransformer {

    protected NamingConventionTransformer nextTransformer = null;

    public NamingConventionTransformer getNextTransformer() {
        return nextTransformer;
    }
    public void setNextTransformer(NamingConventionTransformer nextTransformer) {
        this.nextTransformer = nextTransformer;
    }

    protected boolean isDefaultTransformer() {
        return false;
    }

    public String generateSchemaAlias(String tableName) {
        NamingConventionTransformer nct = getNextTransformer();
        if (nct == null) {
            return tableName;
        }
        else {
            return nct.generateSchemaAlias(tableName);
        }
    }

    public String generateElementAlias(String originalElementName) {
        NamingConventionTransformer nct = getNextTransformer();
        if (nct == null) {
            return originalElementName;
        }
        else {
            return nct.generateElementAlias(originalElementName);
        }
    }

    public ElementStyle styleForElement(String elementName) {
        return ELEMENT;
    }

    public String getOptimisticLockingField() {
        return null;
    }
}