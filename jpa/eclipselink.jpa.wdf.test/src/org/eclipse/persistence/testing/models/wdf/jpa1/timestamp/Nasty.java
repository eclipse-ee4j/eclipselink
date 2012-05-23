package org.eclipse.persistence.testing.models.wdf.jpa1.timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "TMP_NASTY")
public class Nasty {
    @Id
    private Long id;

    @PrePersist
    public void prePersist() {
        setId(Long.valueOf(1000));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
