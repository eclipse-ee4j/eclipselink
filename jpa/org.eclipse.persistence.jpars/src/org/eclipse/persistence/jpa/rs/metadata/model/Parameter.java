package org.eclipse.persistence.jpa.rs.metadata.model;

public class Parameter {

    private String value = null;
    private String typeName = null;
    
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
