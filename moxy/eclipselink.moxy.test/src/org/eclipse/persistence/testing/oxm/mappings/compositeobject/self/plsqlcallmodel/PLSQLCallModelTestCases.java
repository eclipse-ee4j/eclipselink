package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class PLSQLCallModelTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/PLSQLCallResult.xml";

    public PLSQLCallModelTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new PLSQLCallModelTestProject());
    }

    protected Object getControlObject() {
        JDBCObject numericType = new JDBCObject("NUMERIC_TYPE");
        PLSQLObject booleanType = new PLSQLObject("BOOLEAN");

        JDBCObject secondaryType1 = new JDBCObject("VARCHAR_TYPE");
        PLSQLObject secondaryType2 = new PLSQLObject("DATE");
        
        PLSQLCall call = new PLSQLCall();
        call.id = 1;
        call.arguments.add(new PLSQLargument("X", numericType, secondaryType2));
        call.arguments.add(new PLSQLargument("Z", booleanType, secondaryType1));
        return call;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "oracle.toplink.testing.ox.mappings.compositeobject.self.plsqlcallmodel.PLSQLCallModelTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
