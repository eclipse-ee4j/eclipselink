package org.eclipse.persistence.testing.tests.simultaneous;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

public class Runner1 implements Runnable {
    protected Object waitOn;
    protected Object projPK;
    protected Object empPK;
    protected Session session;

    public Runner1(Object waitOn, Object empPK, Object projPK, Session session) {
        this.waitOn = waitOn;
        this.projPK = projPK;
        this.empPK = empPK;
        this.session = session;
    }

    public void run() {
            
            UnitOfWorkImpl uow = (UnitOfWorkImpl)this.session.acquireUnitOfWork();
            ReadObjectQuery roq = new ReadObjectQuery(Employee.class);
            roq.setSelectionCriteria(roq.getExpressionBuilder().get("id").equal(empPK));
            Employee emp = (Employee)uow.executeQuery(roq);
            roq = new ReadObjectQuery(SmallProject.class);
            roq.setSelectionCriteria(roq.getExpressionBuilder().get("id").equal(projPK));
            SmallProject project = (SmallProject)uow.executeQuery(roq);
            emp.getProjects().add(project);

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
            CacheKey cacheKey = uow.getParent().getIdentityMapAccessorInstance().getCacheKeyForObject(emp);
            synchronized (cacheKey.getMutex()) {
                cacheKey.getMutex().notify();
            }
            try {
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException e) {
            }
            uow.mergeClonesAfterCompletion();
    }


}
