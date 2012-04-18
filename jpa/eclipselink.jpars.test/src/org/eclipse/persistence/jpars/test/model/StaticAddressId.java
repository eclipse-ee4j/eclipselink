package org.eclipse.persistence.jpars.test.model;

public class StaticAddressId {

    protected int id;
    protected String type;
    
    public StaticAddressId(){
    }
    
    public StaticAddressId(int id, String type){
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
