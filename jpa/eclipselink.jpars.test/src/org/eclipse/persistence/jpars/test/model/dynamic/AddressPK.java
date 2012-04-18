package org.eclipse.persistence.jpars.test.model.dynamic;

public class AddressPK {
    protected int id;
    protected String type;
    
    public AddressPK(){
    }
    
    public AddressPK(int id, String type){
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
