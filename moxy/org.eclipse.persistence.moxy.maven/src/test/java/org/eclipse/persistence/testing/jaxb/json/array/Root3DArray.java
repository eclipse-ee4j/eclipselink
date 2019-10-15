package org.eclipse.persistence.testing.jaxb.json.array;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class Root3DArray {

    public Integer[][][] array3d;

    @XmlElement(name="array3d")
    public Integer[][][] getArray3d() {
        return array3d;
    }

    public void setArray3d(Integer[][][] array3d) {
        this.array3d = array3d;
    }
}
