package org.eclipse.persistence.jpa.test.metadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SimpleMetadataEntity {

    // Only positive values are valid ID numbers
    @Id
    private Integer id;

    private String name;

    public SimpleMetadataEntity(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public SimpleMetadataEntity() {
        this(null, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
