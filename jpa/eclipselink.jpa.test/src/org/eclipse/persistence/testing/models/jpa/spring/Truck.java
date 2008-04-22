package org.eclipse.persistence.testing.models.jpa.spring;

import javax.persistence.*;

@Entity
@Table(name="Spring_TLE_Truck")
@NamedQuery (
        name="findTruckByDriverName",
        query="SELECT Object(t) FROM Truck t WHERE t.driverName = ?1"
)
public class Truck {
    private long   id;
    private String driverName;
    private Route  route;

    public Truck() {}

    public Truck(String driver) {
        this.driverName = driver;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driver) {
        this.driverName = driver;
    }

    @OneToOne(cascade = CascadeType.PERSIST)
    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
