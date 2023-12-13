package eclipselink.jpa.bugtest.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TEST_TAB_MASTER")
public class TestEntityMaster {
    @Id
    private long id;

    private String name;

    @OneToMany(mappedBy = "master")
    private List<TestEntityDetail> details = new ArrayList<>();

    public TestEntityMaster() {
    }

    public TestEntityMaster(long id) {
        this.id = id;
    }
    
    public TestEntityMaster(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TestEntityDetail> getDetails() {
        return details;
    }

    public void setDetails(List<TestEntityDetail> details) {
        this.details = details;
    }
}
