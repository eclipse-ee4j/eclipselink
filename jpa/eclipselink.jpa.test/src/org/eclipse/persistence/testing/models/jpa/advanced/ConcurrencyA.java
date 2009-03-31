package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class ConcurrencyA {
    
    @Id
    @GeneratedValue
    protected int id;
    
    protected String name;
    
    @OneToOne(fetch=FetchType.LAZY)
    protected ConcurrencyB concurrencyB;

    @OneToOne(fetch=FetchType.LAZY)
    protected ConcurrencyC concurrencyC;

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
     * @return the concurrencyB
     */
    public ConcurrencyB getConcurrencyB() {
        return concurrencyB;
    }

    /**
     * @param concurrencyB the concurrencyB to set
     */
    public void setConcurrencyB(ConcurrencyB concurrencyB) {
        this.concurrencyB = concurrencyB;
    }

    /**
     * @return the concurrencyC
     */
    public ConcurrencyC getConcurrencyC() {
        return concurrencyC;
    }

    /**
     * @param concurrencyC the concurrencyC to set
     */
    public void setConcurrencyC(ConcurrencyC concurrencyC) {
        this.concurrencyC = concurrencyC;
    }

}
