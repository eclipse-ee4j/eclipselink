package org.eclipse.persistence.testing.models.jpa.cascadedeletes;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: LeafA
 * 
 */
@Entity
public class LeafA implements Serializable, PersistentIdentity {

    @Id
    @GeneratedValue
    protected int id;
    @ManyToOne(fetch = FetchType.LAZY)
    protected BranchA branchA;

    public LeafA() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BranchA getBranchA() {
        return this.branchA;
    }

    public void setBranchA(BranchA branchA) {
        this.branchA = branchA;
    }

    public boolean checkTreeForRemoval(EntityManager em) {
        boolean exists = em.find(this.getClass(), this.getId()) != null;
        if (!exists) {
            exists = exists || (this.branchA != null && this.branchA.checkTreeForRemoval(em));
        }
        return exists;
    }
}
