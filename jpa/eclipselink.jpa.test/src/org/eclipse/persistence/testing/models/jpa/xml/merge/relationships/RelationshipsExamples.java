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
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
package org.eclipse.persistence.testing.models.jpa.xml.merge.relationships;

public class RelationshipsExamples
{
  public static Customer customerExample1(){
    Customer customer1 = new Customer();
    customer1.setName("John Smith");
    customer1.setCity("Ottawa");
    return customer1;
  }
  public static Customer customerExample2(){
    Customer customer2 = new Customer();
    customer2.setName("Jane Smith");
    customer2.setCity("Orleans");
    return customer2;
  }
  public static Customer customerExample3(){
    Customer customer3 = new Customer();
    customer3.setName("Karen McDonald");
    customer3.setCity("Nepean");
    return customer3;
  }
  public static Customer customerExample4(){
    Customer customer4 = new Customer();
    customer4.setName("Robert Sampson");
    customer4.setCity("Manotick");
    return customer4;
  }
  public static Item itemExample1(){
    Item item = new Item();
    item.name = "item1";
    item.setDescription("Item1 description");
    return item;
  }
  public static Item itemExample2(){
    Item item = new Item();
    item.name = "item2";
    item.setDescription("Item2 description");
    return item;
  }
  public static Item itemExample3(){
    Item item = new Item();
    item.name= "item3";
    item.setDescription("Item3 description");
    return item;
  }
  public static Item itemExample4(){
    Item item = new Item();
    item.name = "item4";
    item.setDescription("Item4 description");
    return item;
  }
  public static Order orderExample1(){
    Order order = new Order();
    order.setQuantity(70);
    order.setShippingAddress("100 Argyle Street");
    return order;
  }
  public static Order orderExample2(){
    Order order = new Order();
    order.setQuantity(680);
    order.setShippingAddress("500 Oracle Parkway");
    return order;
  }
  public static Order orderExample3(){
    Order order = new Order();
    order.setQuantity(22);
    order.setShippingAddress("240 Queen Street");
    return order;
  }
  public static Order orderExample4(){
    Order order = new Order();
    order.setQuantity(1);
    order.setShippingAddress("50 O'Connor");
    return order;
  }
}
