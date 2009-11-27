package org.eclipse.persistence.testing.tests.collections.map;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.models.collections.map.EntityEntity1MMapHolder;
import org.eclipse.persistence.testing.tests.queries.report.ReportQueryTestCase;

public class MapKeyEntityEntity1MReportQueryTestCase extends ReportQueryTestCase{

    protected void buildExpectedResults() {
        Vector holders = getSession().readAllObjects(EntityEntity1MMapHolder.class);

        for (Enumeration e = holders.elements(); e.hasMoreElements(); ) {
            EntityEntity1MMapHolder holder = (EntityEntity1MMapHolder)e.nextElement();
            Iterator i = holder.getEntityToEntityMap().keySet().iterator();
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

        reportQuery.setReferenceClass(EntityEntity1MMapHolder.class);
        reportQuery.addAttribute("key", reportQuery.getExpressionBuilder().anyOf("entityToEntityMap").mapKey());
    }
}