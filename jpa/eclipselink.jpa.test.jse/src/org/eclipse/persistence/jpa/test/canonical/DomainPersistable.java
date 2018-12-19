package org.eclipse.persistence.jpa.test.canonical;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DomainPersistable implements DomainInterface {

    // Holds all standard fields, mapped to something that is going to be persisted in the database

    @Id
    private Long id;
    private String name;

    @Override
    public Long getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }
}
