package ru.itmo.cs.dandadan.repository.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.model.view.Filter;
import ru.itmo.cs.dandadan.model.view.Page;
import ru.itmo.cs.dandadan.model.view.Sort;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;

import java.time.ZonedDateTime;
import java.util.List;

@Stateless
public class HumanBeingRepositoryImpl implements HumanBeingRepository {

    @Inject
    private EntityManager entityManager;

    @Override
    public Page<HumanBeing> getSortedAndFilteredPage(List<Sort> sortList, List<Filter> filtersList, Integer page, Integer size) {
        return null;
    }

    @Override
    public HumanBeing getHumanBeing(long id) {
        return entityManager.find(HumanBeing.class, id);
    }

    @Override
    public HumanBeing saveHumanBeing(HumanBeing humanBeing) {
        if (humanBeing == null) {
            return null;
        }
        humanBeing.setCreationDate(ZonedDateTime.now());
        return entityManager.merge(humanBeing);
    }

    @Override
    public HumanBeing updateHumanBeing(long id, HumanBeing incoming) {
        if (entityManager.find(HumanBeing.class, id) == null) {
            throw new NotFoundException("HumanBeing not found: " + id);
        }
        incoming.setId(id);
        return entityManager.merge(incoming);
    }

    @Override
    public void deleteHumanBeing(long id) {
        entityManager.remove(entityManager.find(HumanBeing.class, id));
    }

    @Override
    public List<Integer> getUniqueImpactSpeeds() {
        return entityManager.createQuery("SELECT DISTINCT hb.impact_speed from HumanBeing hb", HumanBeing.class).getResultList();
    }
}
