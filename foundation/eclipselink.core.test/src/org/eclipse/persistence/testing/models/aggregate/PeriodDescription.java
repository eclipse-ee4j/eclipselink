/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;

public class PeriodDescription implements Serializable {
    public Period period;
    public Period endPeriod;

    public static PeriodDescription example1() {
        PeriodDescription example = new PeriodDescription();

        example.setPeriod(Period.example1());
        example.setEndPeriod(Period.example2());
        return example;
    }

    public static PeriodDescription example2() {
        PeriodDescription example = new PeriodDescription();

        example.setPeriod(Period.example2());
        example.setEndPeriod(Period.example3());

        return example;
    }

    public static PeriodDescription example3() {
        PeriodDescription example = new PeriodDescription();

        example.setPeriod(Period.example3());
        example.setEndPeriod(Period.example4());

        return example;
    }

    public static PeriodDescription example4() {
        PeriodDescription example = new PeriodDescription();

        example.setPeriod(Period.example4());
        example.setEndPeriod(Period.example5());
        return example;
    }

    public static PeriodDescription example5() {
        PeriodDescription example = new PeriodDescription();

        example.setPeriod(Period.example5());
        example.setEndPeriod(Period.example6());

        return example;
    }

    public static PeriodDescription example6() {
        PeriodDescription example = new PeriodDescription();

        example.setPeriod(Period.example6());
        example.setEndPeriod(Period.example1());

        return example;
    }

    public Period getEndPeriod() {
        return endPeriod;
    }

    public Period getPeriod() {
        return period;
    }

    public void setEndPeriod(Period aPeriod) {
        endPeriod = aPeriod;
    }

    public void setPeriod(Period aPeriod) {
        period = aPeriod;
    }
}
