package org.eclipse.persistence.testing.models.optimisticlocking;

import java.util.List;

public class Animal {

    private int id;
    private int version;
    private List<VetAppointment> appointments;

    public List<VetAppointment> getAppointments(){
        return appointments;
    }
    
    public void setAppointments(List<VetAppointment> appointments){
        this.appointments = appointments;
    }
    
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
