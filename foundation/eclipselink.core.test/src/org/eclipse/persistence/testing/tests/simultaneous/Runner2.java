package org.eclipse.persistence.testing.tests.simultaneous;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

public class Runner2 implements Runnable {
    protected Object waitOn;
    protected Object projPK;
    protected Object empPK;
    protected Session session;

    public Runner2(Object waitOn, Object empPK, Object projPK, Session session) {
        this.waitOn = waitOn;
        this.projPK = projPK;
        this.empPK = empPK;
        this.session = session;
    }

    public void run() {
        try {

            UnitOfWorkImpl uow = (UnitOfWorkImpl)this.session.acquireUnitOfWork();;
            ReadObjectQuery roq = new ReadObjectQuery(Employee.class);
            roq.setSelectionCriteria(roq.getExpressionBuilder().get("id").equal(empPK));
            Employee emp = (Employee)uow.executeQuery(roq);
            roq = new ReadObjectQuery(SmallProject.class);
            roq.setSelectionCriteria(roq.getExpressionBuilder().get("id").equal(projPK));
            SmallProject project = (SmallProject)uow.executeQuery(roq);
            project.setDescription("To changed");
            emp.setFirstName("Name Change As Well");
            synchronized (this.waitOn) {
                try {
                    this.waitOn.wait();
                } catch (InterruptedException e) {
                }
            }

            uow.issueSQLbeforeCompletion(true);
            uow.mergeClonesAfterCompletion();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
