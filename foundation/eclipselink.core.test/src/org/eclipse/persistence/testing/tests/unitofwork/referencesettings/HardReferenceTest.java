package org.eclipse.persistence.testing.tests.unitofwork.referencesettings;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.tests.unitofwork.changeflag.model.ALCTEmployee;

import org.eclipse.persistence.testing.framework.TestErrorException;

public class HardReferenceTest extends AutoVerifyTestCase {
    public void test(){
        UnitOfWork uow = getSession().acquireUnitOfWork();
        int size = uow.readAllObjects(ALCTEmployee.class).size();
        try{
            Long[] arr = new Long[100000];
            for (int i = 0; i< 100000; ++i){
                arr[i] = new Long(i);
            }
        }catch (Error er){
            //ignore
        }
        if (((UnitOfWorkImpl)uow).getCloneMapping().size() != size){
            throw new TestErrorException("Did not hold onto all references.");
        }
    }
}
;