package org.eclipse.persistence.testing.models.jpa.cascadedeletes;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
public class BranchB implements Serializable, PersistentIdentity{
    @Id
    @GeneratedValue
    protected int id;

    @PrivateOwned
    @OneToMany(fetch = LAZY, cascade = REMOVE)
    protected List<BranchB> branchBs;
    
    @ManyToMany(fetch = LAZY, cascade = REMOVE)
    protected List<LeafB> leafBs;

    public BranchB() {
        this.branchBs = new ArrayList<BranchB>();
        this.leafBs = new ArrayList<LeafB>();
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the branchBs
     */
    public List<BranchB> getBranchBs() {
        return branchBs;
    }

    /**
     * @param branchBs
     *            the branchBs to set
     */
    public void setBranchBs(List<BranchB> branchBs) {
        this.branchBs = branchBs;
    }

    /**
     * @return the leafBs
     */
    public List<LeafB> getLeafBs() {
        return leafBs;
    }

    /**
     * @param leafBs
     *            the leafBs to set
     */
    public void setLeafBs(List<LeafB> leafBs) {
        this.leafBs = leafBs;
    }
    
    public boolean checkTreeForRemoval(EntityManager em){
        boolean exists = em.find(this.getClass(), this.getId())!= null;
        if (! exists){
            for (BranchB subBs: this.branchBs){
                exists = exists || subBs.checkTreeForRemoval(em);
            }
            for (LeafB leafB : this.leafBs){
                exists = exists || leafB.checkTreeForRemoval(em);
            }
        }
        return exists;
    }


}
