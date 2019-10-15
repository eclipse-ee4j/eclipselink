/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink


package org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties;

public class ModelExamples  {

     public static Customer customerExample1(){
         Customer cust = new Customer();
         cust.setCity("Ottawa");
         cust.setName("George W.");
         return cust;
    }
     public static Customer customerExample2(){
         Customer cust = new Customer();
         cust.setCity("Toronto");
         cust.setName("Dianel Cooker");
         return cust;
    }
     public static Customer customerExample3(){
         Customer cust = new Customer();
         cust.setCity("Montreal");
         cust.setName("Gulley Lapage");
         return cust;
    }

    public static Order orderExample1(){
        Order order = new Order();
        order.setQuantity(1000);
        order.setShippingAddress("600 Bridlewood Ave");
        order.setItem(itemExample1());
        return order;
    }


    public static Order orderExample2(){
        Order order = new Order();
        order.setQuantity(100);
        order.setShippingAddress("50 Oconner");
        order.setItem(itemExample2());
        return order;
    }

    public static Order orderExample3(){
        Order order = new Order();
        order.setQuantity(10);
        order.setShippingAddress("501 Stonehaven");
        order.setItem(itemExample3());
        return order;
    }

    public static Item itemExample1(){
        Item item = new Item();
        item.setName("Computer LCD");
        item.setDescription("19 inch computer LCD");
        return item;
    }

    public static Item itemExample2(){
        Item item = new Item();
        item.setName("computer CPU");
        item.setDescription("P4G Dual Core ");
        return item;
    }

    public static Item itemExample3(){
        Item item = new Item();
        item.setName("Laser Printer");
        item.setDescription("300 dpi HP laser printer 2000");
        return item;
    }


    public static SalesPerson SPExample1(){
        SalesPerson sp = new SalesPerson();
        sp.setName("Linda Stanfer");
        return sp;
    }

    public static SalesPerson SPExample2(){
        SalesPerson sp = new SalesPerson();
        sp.setName("Dorias");
        return sp;
    }
    public static SalesPerson SPExample3(){
        SalesPerson sp = new SalesPerson();
        sp.setName("Radhika");
        return sp;
    }

}
