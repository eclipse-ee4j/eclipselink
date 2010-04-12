package org.eclipse.persistence.testing.models.jpa.xml.advanced;

/**
 * This class is used to test the extended orm named-access setting.
 * 
 * It is mapped as a mapped superclass in the following resource file:
 * 
 *  resource/eclipselinkorm/eclipselink-xml-extended-model/eclipselink-orm.xml
 *  
 * @author gpelleti
 */
public class ShovelPerson {
    // id
    private Integer id;
    
    // basic
    private String name;

    public Object get(String attribute) {
        try {
            return ShovelPerson.class.getDeclaredField(attribute).get(this);
        } catch (Exception e) {
            throw new RuntimeException("Error occured getting the value of attributee: " + attribute);
        }
    }
    
    public void set(String attribute, Object value) {
        try {
            ShovelPerson.class.getDeclaredField(attribute).set(this, value);
        } catch (Exception e) {
            throw new RuntimeException("Error occured set the attribute: " + attribute + ", with value: " + value);
        }
    }
}
