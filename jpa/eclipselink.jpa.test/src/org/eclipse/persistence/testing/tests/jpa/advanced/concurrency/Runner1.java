package org.eclipse.persistence.testing.tests.jpa.advanced.concurrency;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Equipment;

public class Runner1 implements Runnable {
    protected Object waitOn;
    protected Object equipPK;
    protected Object deptPK;
    protected EntityManagerFactory emf;

    public Runner1(Object waitOn, Object deptPK, Object equipPK, EntityManagerFactory emf) {
        this.waitOn = waitOn;
        this.equipPK = equipPK;
        this.deptPK = deptPK;
        this.emf = emf;
    }

    public void run() {
        try {

            
            EntityManager em =emf.createEntityManager();
            Department dept = em.find(Department.class, deptPK);
            Equipment equip = em.find(Equipment.class, equipPK);
            dept.getEquipment().put(equip.getId(), equip);
            UnitOfWorkImpl uow = ((EntityManagerImpl) em).getActivePersistenceContext(null);
            try {
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException e) {
           }
            uow.issueSQLbeforeCompletion(true);
            synchronized (this.waitOn) {
                try {
                    this.waitOn.notify();
                    this.waitOn.wait();
                } catch (InterruptedException e) {
                }
            }
            CacheKey cacheKey = uow.getParent().getParent().getIdentityMapAccessorInstance().getCacheKeyForObject(dept);
            synchronized (cacheKey) {
                cacheKey.notify();
            }
            try {
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException e) {
            }
            uow.mergeClonesAfterCompletion();
        } catch (Exception ex) {
        }
    }


}
