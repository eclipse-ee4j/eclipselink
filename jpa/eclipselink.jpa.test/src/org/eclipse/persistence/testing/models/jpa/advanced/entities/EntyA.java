package org.eclipse.persistence.testing.models.jpa.advanced.entities;

import java.util.Collection;

import static javax.persistence.CascadeType.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "ADV_ENTYA")
public class EntyA{
    
    @Id
    @GeneratedValue
    protected int id;
    
    protected String name;
    
    @OneToOne(cascade=PERSIST)
    protected EntyB entyB;

    @OneToOne(optional=false)
    @JoinColumn(name="ENTYC_ID", unique=true, nullable=false, updatable=false)
    protected EntyC entyC;

    @OneToMany(cascade={PERSIST})
    protected Collection<EntyD> entyDs;

    @ManyToMany(cascade={PERSIST, MERGE})
    protected Collection<EntyE> entyEs;

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
     * @return the entyB
     */
    public EntyB getEntyB() {
        return entyB;
    }

    /**
     * @param entyB the entyB to set
     */
    public void setEntyB(EntyB entyB) {
        this.entyB = entyB;
    }

    /**
     * @return the entyC
     */

    public EntyC getEntyC() {
        return entyC;
    }

    /**
     * @param entyC the entyC to set
     */

    public void setEntyC(EntyC entyC) {
        this.entyC = entyC;
    }

    /**
     * @return the entyD
     */
    public Collection<EntyD> getEntyDs() {
        return entyDs;
    }

    public void addEntyD(EntyD entyD) {
        getEntyDs().add(entyD);
    }

    public void removeEntyD(EntyD entyD) {
        getEntyDs().remove(entyD);
    }

    /**
     * @param entyD the entyD to set
     */
    public void setEntyDs(Collection<EntyD> entyDs) {
        this.entyDs = entyDs;
    }

    /**
     * @return the entyE
     */
    public Collection<EntyE> getEntyEs() {
        return entyEs;
    }

    public void addEntyE(EntyE entyE) {
        getEntyEs().add(entyE);
    }

    /**
     * @param entyE the entyE to set
     */
    public void setEntyEs(Collection<EntyE> entyEs) {
        this.entyEs = entyEs;
    }

}
