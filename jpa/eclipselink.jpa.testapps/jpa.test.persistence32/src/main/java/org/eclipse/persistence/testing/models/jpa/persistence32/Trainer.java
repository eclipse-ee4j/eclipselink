package org.eclipse.persistence.testing.models.jpa.persistence32;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name="PERSISTENCE32_TRAINER")
@NamedQuery(name="Trainer.get", query="SELECT t FROM Trainer t WHERE t.id = :id")
@NamedNativeQuery(name="Trainer.deleteAll", query="DELETE FROM PERSISTENCE32_TRAINER")
public class Trainer {

    // ID is assigned in tests to avoid collisions
    @Id
    private int id;

    private String name;

    public Trainer() {
    }

    public Trainer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return id == ((Trainer) obj).id
                && Objects.equals(name, ((Trainer) obj).name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name);
        return result;
    }

}
