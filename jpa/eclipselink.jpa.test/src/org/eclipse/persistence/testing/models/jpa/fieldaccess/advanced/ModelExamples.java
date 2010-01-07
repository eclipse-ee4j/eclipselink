/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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


package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.sql.Date;

public class ModelExamples  {

     public static Employee employeeExample1(){
        Employee emp = new Employee();
        emp.setFirstName("Brady");
        emp.setLastName("Bowaster");
        emp.setPeriod(new EmploymentPeriod());
        emp.getPeriod().setStartDate(new Date(System.currentTimeMillis()-1000000));
        emp.getPeriod().setEndDate(new Date(System.currentTimeMillis()+1000000));
        emp.setSalary(15000);
        return emp;
    }

    public static Employee employeeExample2(){
        Employee emp = new Employee();
        emp.setFirstName("Sassly");
        emp.setLastName("Soosly");
        emp.setPeriod(new EmploymentPeriod());
        emp.getPeriod().setStartDate(new Date(System.currentTimeMillis()-3000000));
        emp.getPeriod().setEndDate(new Date(System.currentTimeMillis()-10000));
        emp.setSalary(1000);
        return emp;
    }
    
    public static Employee employeeExample3(){
        Employee emp = new Employee();
        emp.setFirstName("Lacy");
        emp.setLastName("Lowry");
        emp.setPeriod(new EmploymentPeriod());
        emp.getPeriod().setStartDate(new Date(System.currentTimeMillis()-48000000));
        emp.getPeriod().setEndDate(new Date(System.currentTimeMillis()+10000000));
        emp.setSalary(2);
        return emp;
    }
    
    public static Employee employeeExample4(){
        Employee emp = new Employee();
        emp.setFirstName("Ralf");
        emp.setLastName("Guedder");
        emp.setPeriod(new EmploymentPeriod());
        emp.getPeriod().setStartDate(new Date(System.currentTimeMillis()-15000000));
        emp.getPeriod().setEndDate(new Date(System.currentTimeMillis()+15000000));
        emp.setSalary(100);
        return emp;
    }
    
    public static Project projectExample1(){
        Project project = new Project();
        project.setDescription("To undertake and evaluate the effecency of the companies farmers.");
        project.setName("Farmer effecency evaluations");
        return project;
    }
    
    public static Project projectExample2(){
        LargeProject project = new LargeProject();
        project.setDescription("To assess the changing demographics of the feline world");
        project.setName("Feline Demographics Assesment");
        project.setBudget(3654563.0);
        return project;
    }
    public static Project projectExample3(){
        SmallProject project = new SmallProject();
        project.setDescription("To carefully watch the grass grow.");
        project.setName("Horticulture Quantification");
        return project;
    }
    public static Address addressExample1(){
        Address address = new Address();
        address.setCity("Washabuc");
        address.setCountry("Canada");
        address.setPostalCode("K2T3A4");
        address.setProvince("Ontario");
        address.setStreet("1734 Wallywoo Drive");
        return address;
    }

    public static Address addressExample2(){
        Address address = new Address();
        address.setCity("Ekumseekum");
        address.setCountry("Canada");
        address.setPostalCode("B2N 2C0");
        address.setProvince("Nova Scotia");
        address.setStreet("2 Main");
        return address;
    }

    public static Address addressExample3(){
        Address address = new Address();
        address.setCity("Shoolee");
        address.setCountry("Canada");
        address.setPostalCode("B1M 1C2");
        address.setProvince("Nova Scotia");
        address.setStreet("3 Main");
        return address;
    }

    public static Address addressExample4(){
        Address address = new Address();
        address.setCity("Gander");
        address.setCountry("Canada");
        address.setPostalCode("A2C1B1");
        address.setProvince("Newfoundland");
        address.setStreet("324 Bay Street");
        return address;
    }
    
    public static Address addressExample5(){
        Address address = new Address();
        address.setCity("Goose Bay");
        address.setCountry("Canada");
        address.setPostalCode("A0P1S0");
        address.setProvince("Newfoundland");
        address.setStreet("360 Hamilton Rd");
        return address;
    }
    
    public static PhoneNumber phoneExample1(){
        return new PhoneNumber("Work", "613", "6544545");
    }

    public static PhoneNumber phoneExample2(){
        return new PhoneNumber("Work", "613", "8885875");
    }

    public static PhoneNumber phoneExample3(){
        return new PhoneNumber("Home", "613", "8457451");
    }

    public static PhoneNumber phoneExample4(){
        return new PhoneNumber("Cell", "613", "3656856");
    }

    public static PhoneNumber phoneExample5(){
        return new PhoneNumber("Cell2", "613", "1254525");
    }

    public static PhoneNumber phoneExample6(){
        return new PhoneNumber("Office", "613", "7854652");
    }

    public static PhoneNumber phoneExample7(){
        return new PhoneNumber("Reception", "613", "6352145");
    }

    public static PhoneNumber phoneExample8(){
        return new PhoneNumber("NextOfKin", "613", "8974562");
    }

    public static PhoneNumber phoneExample9(){
        return new PhoneNumber("Old", "613", "3232323");
    }

}
