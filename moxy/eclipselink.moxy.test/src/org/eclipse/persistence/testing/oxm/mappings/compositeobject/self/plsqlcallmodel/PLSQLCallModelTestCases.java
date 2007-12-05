package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

import static org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.JDBCTypes.DATE_TYPE;
import static org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.JDBCTypes.NUMERIC_TYPE;
import static org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.JDBCTypes.VARCHAR_TYPE;
import static org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.OraclePLSQLTypes.PLSQLBoolean;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class PLSQLCallModelTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/PLSQLCallResult.xml";

    public PLSQLCallModelTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new PLSQLCallModelTestProject());
    }

    protected Object getControlObject() {
        PLSQLrecord r1 = new PLSQLrecord();
        r1.setRecordName("EMPLOYEE RECORD");
        r1.setTypeName("emp%ROWTYPE");
        r1.addField("EMPNO", NUMERIC_TYPE, 4, 0);
        r1.addField("ENAME", VARCHAR_TYPE, 10);
        r1.addField("JOB", VARCHAR_TYPE, 9);
        r1.addField("MGR", NUMERIC_TYPE, 4, 0);
        r1.addField("HIREDATE", DATE_TYPE);
        r1.addField("SAL", NUMERIC_TYPE, 7, 2);
        r1.addField("COMM", NUMERIC_TYPE, 7, 2);
        r1.addField("DEPTNO", NUMERIC_TYPE, 2, 0);

        // PROCEDURE REC_TEST(Z IN EMP%ROWTYPE)
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("REC_TEST");
        call.addNamedArgument("Z", r1);
        call.addNamedArgument("AA", VARCHAR_TYPE, 40);
        call.addNamedArgument("BB", PLSQLBoolean);
        
        return call;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.PLSQLCallModelTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
