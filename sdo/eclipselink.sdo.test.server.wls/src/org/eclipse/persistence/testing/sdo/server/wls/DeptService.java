package org.eclipse.persistence.testing.sdo.server.wls;

import javax.ejb.Remote;

@Remote
public interface DeptService {
    Dept getDept(Integer deptno);
    boolean updateDept(Dept dept);
}
