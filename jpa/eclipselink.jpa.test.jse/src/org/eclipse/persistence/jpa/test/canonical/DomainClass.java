package org.eclipse.persistence.jpa.test.canonical;

import javax.persistence.Entity;

@Entity
public class DomainClass extends DomainPersistable {

    // Holds additional features that one can specify on the DomainInterface
    // Essentially this is an enrichment of the persistable

    @Override
    public Long doSomeBusinessOperation() {

        return null;
    }

}
