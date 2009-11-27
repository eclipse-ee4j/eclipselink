package org.eclipse.persistence.testing.models.jpa.cascadedeletes;

import static javax.persistence.CascadeType.ALL;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
public class MachineState {

    @Id
    private long id;
@PrivateOwned
    @OneToMany(cascade = ALL)
    private List<ThreadInfo> threads;

    public MachineState() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ThreadInfo> getThreads() {
        return threads;
    }

    public void setThreads(List<ThreadInfo> threads) {
        this.threads = threads;
    }
}
