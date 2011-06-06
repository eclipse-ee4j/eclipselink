/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.descriptors.invalidation.*;

public class EmployeeDailyExpiryTestModel extends EmployeeCacheExpiryTestModel {
    public EmployeeDailyExpiryTestModel() {
        setDescription("Test CRUD operations on the Employee model with non expiring Daily Cache Expiry.");
        setName("Employee Daily Expiry Test Model");
    }


    public void setup() {
        super.setup();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), 
                                                                                                               calendar.get(Calendar.MINUTE), 
                                                                                                               calendar.get(Calendar.SECOND), 
                                                                                                               calendar.get(Calendar.MILLISECOND)));
        getSession().getDescriptor(Address.class).setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), 
                                                                                                              calendar.get(Calendar.MINUTE), 
                                                                                                              calendar.get(Calendar.SECOND), 
                                                                                                              calendar.get(Calendar.MILLISECOND)));
        getSession().getDescriptor(Project.class).setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), 
                                                                                                              calendar.get(Calendar.MINUTE), 
                                                                                                              calendar.get(Calendar.SECOND), 
                                                                                                              calendar.get(Calendar.MILLISECOND)));
        getSession().getDescriptor(PhoneNumber.class).setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), 
                                                                                                                  calendar.get(Calendar.MINUTE), 
                                                                                                                  calendar.get(Calendar.SECOND), 
                                                                                                                  calendar.get(Calendar.MILLISECOND)));
        getSession().getDescriptor(LargeProject.class).setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), 
                                                                                                                   calendar.get(Calendar.MINUTE), 
                                                                                                                   calendar.get(Calendar.SECOND), 
                                                                                                                   calendar.get(Calendar.MILLISECOND)));
        getSession().getDescriptor(SmallProject.class).setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), 
                                                                                                                   calendar.get(Calendar.MINUTE), 
                                                                                                                   calendar.get(Calendar.SECOND), 
                                                                                                                   calendar.get(Calendar.MILLISECOND)));
    }

}
