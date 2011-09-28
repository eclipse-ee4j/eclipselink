package org.eclipse.persistence.testing.oxm.inheritance;


import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class InheritanceCarNoPrefixTestCases extends XMLWithJSONMappingTestCases {
    public InheritanceCarNoPrefixTestCases(String name) throws Exception {
        super(name);
        setProject(new InheritanceProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/inheritance/car_no_prefix.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/inheritance/car_no_prefix.json");
        setWriteControlDocument("org/eclipse/persistence/testing/oxm/inheritance/car.xml");              
        setControlJSONWrite("org/eclipse/persistence/testing/oxm/inheritance/car.json");
    }

    public Object getControlObject() {
        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 30;
        car.model = "Grand Am";
        car.manufacturer = "Pontiac";
        car.topSpeed = 220;

        return car;
    }

}