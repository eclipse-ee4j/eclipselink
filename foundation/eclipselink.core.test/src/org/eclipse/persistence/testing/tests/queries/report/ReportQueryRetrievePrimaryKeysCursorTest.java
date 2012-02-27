package org.eclipse.persistence.testing.tests.queries.report;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test retrieving primary keys and other values with a ReportQuery, using a Cursor
 * Bug TO-DO
 */
public class ReportQueryRetrievePrimaryKeysCursorTest extends TestCase {

    protected ReportQuery reportQuery;
    protected HashMap<Object, ReportQueryResult> expectedResults;
    protected HashMap<Object, ReportQueryResult> cursoredResults;
    
    public ReportQueryRetrievePrimaryKeysCursorTest() {
        super();
        setDescription("Test retrieving primary keys with a ReportQuery, using a Cursor");
    }
    
    protected void queryForExpectedResults() {
        expectedResults = new HashMap<Object, ReportQueryResult>();
        ReportQuery expectedResultsReportQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
        ExpressionBuilder builder = expectedResultsReportQuery.getExpressionBuilder();
        
        // required
        expectedResultsReportQuery.retrievePrimaryKeys();

        expectedResultsReportQuery.addAttribute("idResult", builder.get("id")); // number
        expectedResultsReportQuery.addAttribute("firstNameResult", builder.get("firstName")); // string
        expectedResultsReportQuery.addAttribute("lastNameResult", builder.get("lastName")); // string 
        expectedResultsReportQuery.addAttribute("addressResult", builder.get("address").get("id")); // number
        
        Vector<ReportQueryResult> someResults = (Vector<ReportQueryResult>)getSession().executeQuery(expectedResultsReportQuery);
        for (ReportQueryResult result : someResults) {
            expectedResults.put(result.getId(), result);
        }
    }
    
    protected void buildCursoredResultsReportQuery() {
        cursoredResults = new HashMap<Object, ReportQueryResult>();
        reportQuery = new ReportQuery(Employee.class, new ExpressionBuilder());
        ExpressionBuilder builder = reportQuery.getExpressionBuilder();
        
        // required
        reportQuery.retrievePrimaryKeys();

        reportQuery.addAttribute("idResult", builder.get("id")); // number
        reportQuery.addAttribute("firstNameResult", builder.get("firstName")); // string
        reportQuery.addAttribute("lastNameResult", builder.get("lastName")); // string 
        reportQuery.addAttribute("addressResult", builder.get("address").get("id")); // number

        // required
        reportQuery.useCursoredStream(5, 5);
    }

    @Override
    protected void setup() throws Throwable {
        if (getSession().isRemoteSession()) {
            throwWarning("Report queries with objects are not supported on remote session.");
        }
        super.setup();
        queryForExpectedResults();
        buildCursoredResultsReportQuery();
    }

    @Override
    public void test() {
        CursoredStream stream = (CursoredStream) getSession().executeQuery(reportQuery);
        while (!stream.atEnd()) {
            ReportQueryResult result = (ReportQueryResult) stream.read();
            cursoredResults.put(result.getId(), result);
			stream.releasePrevious();
        }
        stream.close();
    }
    
    @Override
    public void verify() {
        assertNotNull("Expected results should be non-null", expectedResults);
        assertFalse("Expected results should not be zero", expectedResults.size() == 0);
        
        assertNotNull("Cursored results should be non-null", cursoredResults);
        assertFalse("Cursored results should not be zero", cursoredResults.size() == 0);
        
        assertEquals("Cursored results differs from expected results", expectedResults.size(), cursoredResults.size());
        
        for (Object id : expectedResults.keySet()) {
            if (!cursoredResults.containsKey(id)) {
                fail("Cursored results does not contain entry for id: " + id);
            }
        }
    }
    
    @Override
    public void reset() {
        this.cursoredResults = null;
        this.expectedResults = null;
        this.reportQuery = null;
    }
    
}
