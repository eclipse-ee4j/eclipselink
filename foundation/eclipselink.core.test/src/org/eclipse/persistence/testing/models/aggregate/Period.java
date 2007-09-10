/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import java.sql.Date;
import org.eclipse.persistence.internal.helper.Helper;

public class Period implements Serializable {
    public Date startDate;
    public Date endDate;

    public static Period example1() {
        Period example = new Period();

        example.setStartDate(Helper.dateFromYearMonthDate(1993, 1, 1));
        example.setEndDate(Helper.dateFromYearMonthDate(1993, 2, 1));

        return example;
    }

    public static Period example2() {
        Period example = new Period();

        example.setStartDate(Helper.dateFromYearMonthDate(1993, 1, 1));
        example.setEndDate(Helper.dateFromYearMonthDate(1994, 2, 1));

        return example;
    }

    public static Period example3() {
        Period example = new Period();

        example.setStartDate(Helper.dateFromYearMonthDate(1993, 1, 1));
        example.setEndDate(Helper.dateFromYearMonthDate(1995, 2, 1));

        return example;
    }

    public static Period example4() {
        Period example = new Period();

        example.setStartDate(Helper.dateFromYearMonthDate(1991, 1, 1));
        example.setEndDate(Helper.dateFromYearMonthDate(1995, 2, 1));

        return example;
    }

    public static Period example5() {
        Period example = new Period();

        example.setStartDate(Helper.dateFromYearMonthDate(1990, 1, 1));
        example.setEndDate(Helper.dateFromYearMonthDate(1995, 2, 1));

        return example;
    }

    public static Period example6() {
        Period example = new Period();

        example.setStartDate(Helper.dateFromYearMonthDate(1994, 1, 1));
        example.setEndDate(Helper.dateFromYearMonthDate(1995, 2, 1));

        return example;
    }

    public static Period example7() {
        Period example = new Period();

        example.setStartDate(Helper.dateFromYearMonthDate(1999, 1, 1));
        example.setEndDate(Helper.dateFromYearMonthDate(1999, 2, 2));

        return example;
    }

    public void setEndDate(Date aDate) {
        endDate = aDate;
    }

    public void setStartDate(Date aDate) {
        startDate = aDate;
    }
}