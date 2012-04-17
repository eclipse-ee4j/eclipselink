package org.eclipse.persistence.jpa.rs.metadata.model;

public class Attribute {

    protected String name;
    protected String type;
    
    public Attribute(){}
    
    public Attribute(String name, String type){
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
