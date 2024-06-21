package org.eclipse.persistence.testing.helidon.provider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.persistence.testing.helidon.dao.MasterDao;
import org.eclipse.persistence.testing.helidon.models.MasterEntity;

import java.util.List;

@ApplicationScoped
public class MasterProvider {

    @Inject
    private MasterDao masterDao;

    public MasterEntity getMasterOne() {
        MasterEntity masterEntity = masterDao.find(MasterEntity.class, 1L);
        return masterEntity;
    }

}
