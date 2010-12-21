package org.eclipse.persistence.testing.tests.collections.map;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.models.collections.map.AggregateEntity1MMapHolder;
import org.eclipse.persistence.testing.tests.queries.report.ReportQueryTestCase;

public class MapKeyAggregateEntity1MReportQueryTestCase extends ReportQueryTestCase{

    protected void buildExpectedResults() {
        Vector holders = getSession().readAllObjects(AggregateEntity1MMapHolder.class);

        for (Enumeration e = holders.elements(); e.hasMoreElements(); ) {
            AggregateEntity1MMapHolder holder = (AggregateEntity1MMapHolder)e.nextElement();
            Iterator i = holder.getAggregateToEntityMap().keySet().iterator();
            while (i.hasNext()){
                Object[] result = new Object[1];
                result[0] = i.next();
                addResult(result, null);
            }
        }
    }

    protected void setup() throws Exception {
        super.setup();
        reportQuery = new ReportQuery(new ExpressionBuilder());

        reportQuery.setReferenceClass(AggregateEntity1MMapHolder.class);
        reportQuery.addAttribute("key", reportQuery.getExpressionBuilder().anyOf("aggregateToEntityMap").mapKey());
    }
}