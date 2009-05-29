package org.eclipse.persistence.testing.tests.collections.map;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.models.collections.map.DirectEntity1MMapHolder;
import org.eclipse.persistence.testing.tests.queries.report.ReportQueryTestCase;

public class MapEntryDirectEntity1MReportQueryTest extends ReportQueryTestCase{

    protected void buildExpectedResults() {
        Vector holders = getSession().readAllObjects(DirectEntity1MMapHolder.class);

        for (Enumeration e = holders.elements(); e.hasMoreElements(); ) {
            DirectEntity1MMapHolder holder = (DirectEntity1MMapHolder)e.nextElement();
            Iterator<Map.Entry> i = holder.getDirectToEntityMap().entrySet().iterator();
            while (i.hasNext()){
                Object[] result = new Object[1];
                Map.Entry entry = i.next();
                result[0] = new Association(entry.getKey(), entry.getValue());
                addResult(result, null);
            }
        }
    }

    protected void removeFromResult(ReportQueryResult result, Vector expected) {
        for (Enumeration e = expected.elements(); e.hasMoreElements();) {
            ReportQueryResult expectedResult = (ReportQueryResult)e.nextElement();
            Association expectedAssocication = (Association)expectedResult.getByIndex(0);
            Association resultAssocication = (Association)result.getByIndex(0); 
            if (expectedAssocication.getKey().equals(resultAssocication.getKey()) && expectedAssocication.getValue().equals(resultAssocication.getValue())) {
                expected.removeElement(expectedResult);
                return;
            }
        }
        getSession().logMessage("missing element: " + result);
    }
    
    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(DirectEntity1MMapHolder.class);
        reportQuery.addAttribute("entry", reportQuery.getExpressionBuilder().anyOf("directToEntityMap").mapEntry());
    }
}