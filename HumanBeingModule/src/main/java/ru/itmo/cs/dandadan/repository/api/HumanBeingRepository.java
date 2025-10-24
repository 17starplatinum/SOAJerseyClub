package ru.itmo.cs.dandadan.repository.api;

import jakarta.ejb.Local;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.model.view.Filter;
import ru.itmo.cs.dandadan.model.view.Page;
import ru.itmo.cs.dandadan.model.view.Sort;

import java.util.List;

@Local
public interface HumanBeingRepository {
    Page<HumanBeing> getSortedAndFilteredPage(List<Sort> sortList, List<Filter> filtersList, Integer page, Integer size);
    HumanBeing getHumanBeing(long id);
    HumanBeing saveHumanBeing(HumanBeing humanBeing);
    HumanBeing updateHumanBeing(long id, HumanBeing humanBeing);
    void deleteHumanBeing(long id);
    List<Integer> getUniqueImpactSpeeds();
}
