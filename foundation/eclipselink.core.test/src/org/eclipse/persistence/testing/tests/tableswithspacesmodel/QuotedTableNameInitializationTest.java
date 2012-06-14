package org.eclipse.persistence.testing.tests.tableswithspacesmodel;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test that a database table with spaces is properly delimited
 * EL Bug 382420
 */
public class QuotedTableNameInitializationTest extends TestCase {
    
    public QuotedTableNameInitializationTest() {
        super();
        setDescription("Test that a database table with spaces is properly quoted");
    }
    
    public void test() {
        // symfoware does not support tables with spaces
        if (getSession().getPlatform().isSymfoware()) {
            return;
        }
        
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Employee.class);
        String tableName = "SPACED EMPLOYEE TABLE";
        DatabaseTable table = new DatabaseTable();
        table.setName(tableName);
        table.setUseDelimiters(true);
        descriptor.addTable(table);
        descriptor.addPrimaryKeyFieldName("EMP_ID");
        
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMP_ID");
        descriptor.addMapping(idMapping);
        
        descriptor.preInitialize(getAbstractSession());
        
        DatasourcePlatform plaf = (DatasourcePlatform)getAbstractSession().getDatasourcePlatform();
        
        String expectedTableName = plaf.getStartDelimiter() + tableName + plaf.getEndDelimiter();
        String newTableName = table.getNameDelimited(plaf);
        
        assertEquals("Table name should be between the platform delimiters", expectedTableName, newTableName);
    }
    
}
