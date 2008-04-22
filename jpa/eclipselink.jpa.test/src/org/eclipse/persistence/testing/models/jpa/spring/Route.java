package org.eclipse.persistence.testing.models.jpa.spring;

import java.util.Collection;
import java.util.Vector;
import javax.persistence.*;

@Entity
@Table(name="Spring_TLE_Route")
public class Route {
    
    private long   id;
    private int    averageTimeMins;
	private Collection<Address> addresses = new Vector<Address>();

    public Route() {}
    
    public Route(int averageTimeMins){
        this.averageTimeMins= averageTimeMins;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAverageTimeMins() {
        return averageTimeMins;
    }

    public void setAverageTimeMins(int averageTimeMins) {
        this.averageTimeMins = averageTimeMins;
    }

    @OneToMany(mappedBy="route", cascade = CascadeType.PERSIST)
    public Collection<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Collection<Address> addresses) {
        this.addresses = addresses;
    }
    
    public void addAddress(Address address){
        this.addresses.add(address);
        address.setRoute(this);
    }
    
    public void removeAddress(Address address){
        this.addresses.remove(address);
        address.setRoute(null);
    }
}
