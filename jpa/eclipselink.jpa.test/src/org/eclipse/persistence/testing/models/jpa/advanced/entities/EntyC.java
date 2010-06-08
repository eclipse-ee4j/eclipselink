package org.eclipse.persistence.testing.models.jpa.advanced.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ADV_ENTYC")
public class EntyC {
    
    @Id
    @GeneratedValue
    protected int id;
    
    protected String name;
    
    @OneToOne(optional=false, mappedBy="entyC")
    protected EntyA entyA;
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the entyA
     */

    public EntyA getEntyA() {
        return entyA;
    }

    /**
     * @param entyA the entyA to set
     */

    public void setEntyA(EntyA entyA) {
        this.entyA = entyA;
    }
}
