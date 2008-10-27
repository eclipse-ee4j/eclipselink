package org.eclipse.persistence.testing.models.optimisticlocking;

public class Animal {

    int id;
    int version;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    
    
}
