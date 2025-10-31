package ru.itmo.cs.dandadan.repository.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;
import ru.itmo.cs.dandadan.exception.NotFoundException;
import ru.itmo.cs.dandadan.model.entity.Color;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.model.entity.Mood;
import ru.itmo.cs.dandadan.model.entity.WeaponType;
import ru.itmo.cs.dandadan.model.view.Filter;
import ru.itmo.cs.dandadan.model.view.FilterCode;
import ru.itmo.cs.dandadan.model.view.Page;
import ru.itmo.cs.dandadan.model.view.Sort;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ru.itmo.cs.dandadan.config.HibernateSessionFactoryConfig.getSessionFactory;

@Stateless
public class HumanBeingRepositoryImpl implements HumanBeingRepository {

    @Override
    public Page<HumanBeing> getSortedAndFilteredPage(List<Sort> sortList, List<Filter> filtersList, Integer page, Integer size) {
        try (Session session = getSessionFactory().openSession();
             EntityManager entityManager = session.getEntityManagerFactory().createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<HumanBeing> query = cb.createQuery(HumanBeing.class);
            Root<HumanBeing> root = query.from(HumanBeing.class);
            query.select(root);
            List<Predicate> predicates = buildPredicates(filtersList, root, cb);

            if (!predicates.isEmpty()) {
                query.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            if (sortList != null && !sortList.isEmpty()) {
                List<Order> orders = new ArrayList<>();
                for (Sort sort : sortList) {
                    Expression<?> expression = resolvePath(root, sort.getFieldName(), sort.getNestedName());
                    orders.add(sort.isDescendingOrder() ? cb.desc(expression) : cb.asc(expression));
                }
                query.orderBy(orders);
            }

            TypedQuery<HumanBeing> typedQuery = entityManager.createQuery(query);
            Page<HumanBeing> result = new Page<>();
            int totalCount;

            if (page != null && size != null) {
                typedQuery.setFirstResult((page - 1) * size);
                typedQuery.setMaxResults(size);

                CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
                Root<HumanBeing> countRoot = countCq.from(HumanBeing.class);
                countCq.select(cb.count(countRoot));
                List<Predicate> countPredicates = buildPredicates(filtersList, countRoot, cb);
                if (!countPredicates.isEmpty()) {
                    countCq.where(cb.and(countPredicates.toArray(new Predicate[0])));
                }
                totalCount = Math.toIntExact(entityManager.createQuery(countCq).getSingleResult());

                result.setPage(page);
                result.setPageSize(size);
                result.setTotalCount(totalCount);
                result.setTotalPages((int) Math.ceil((double) totalCount / size));
            }
            result.setContent(typedQuery.getResultList());
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HumanBeing getHumanBeing(long id) {
        HumanBeing humanBeing;
        Session session = getSessionFactory().openSession();
        humanBeing = session.get(HumanBeing.class, id);
        if (humanBeing == null) {
            throw new NotFoundException("id", "not found", String.valueOf(id));
        }
        session.close();
        return humanBeing;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public HumanBeing saveHumanBeing(HumanBeing humanBeing, Long teamId) {
        if (humanBeing == null) {
            return null;
        }
        Session session = getSessionFactory().openSession();
        session.persist(humanBeing);
        session.close();
        return humanBeing;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public HumanBeing updateHumanBeing(long id, HumanBeing incoming, Long oldTeamId) {
        Session session = getSessionFactory().openSession();
        session.merge(incoming);
        session.close();
        return session.get(HumanBeing.class, id);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteHumanBeing(long id) {
        Session session = getSessionFactory().openSession();
        HumanBeing humanBeing = getHumanBeing(id);
        session.remove(humanBeing);
        session.close();
    }

    @Override
    public List<Integer> getUniqueImpactSpeeds() {
        Session session = getSessionFactory().openSession();
        EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
        return entityManager.createQuery("SELECT DISTINCT hb.impactSpeed FROM human_beings hb", Integer.class).getResultList();
    }

    private List<Predicate> buildPredicates(List<Filter> filters, Root<HumanBeing> root, CriteriaBuilder cb) {
        if (filters == null || filters.isEmpty()) {
            return Collections.emptyList();
        }
        List<Predicate> predicates = new ArrayList<>();
        for (Filter f : filters) {
            try {
                Expression<?> expr = resolvePath(root, f.getFieldName(), f.getNestedName());
                Object typedValue = getTypedFieldValue(f.getFieldName(), f.getFieldValue());
                Predicate p = buildPredicateForFilter(cb, expr, f.getFilterCode(), typedValue);
                if (p != null) {
                    predicates.add(p);
                }
            } catch (IllegalArgumentException ex) {
                throw new CustomBadRequestException("filter", "filter code should be one of the following: 'eq', 'neq', 'gt', 'lt', 'gte', 'lte', 'like'");
            }
        }
        return predicates;
    }

    private Path<?> resolvePath(From<?, ?> root, String fieldName, String nestedName) {
        if (fieldName == null || fieldName.isBlank()) {
            throw new IllegalArgumentException("fieldName is required");
        }
        if (nestedName != null && !nestedName.isBlank()) {
            return root.get(fieldName).get(nestedName);
        }
        if (fieldName.contains(".")) {
            String[] parts = fieldName.split("\\.");
            Path<?> p = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                p = p.get(parts[i]);
            }
            return p;
        }
        return root.get(fieldName);
    }

    private Predicate buildPredicateForFilter(CriteriaBuilder cb, Expression<?> expr,
                                              FilterCode op, Object value) {
        if (op == null || op == FilterCode.UNDEFINED) return null;

        switch (op) {
            case EQ:
                return cb.equal(expr, value);
            case NEQ:
                return cb.notEqual(expr, value);
            case GT:
                return cb.greaterThan((Expression<? extends Comparable>) expr, (Comparable) value);
            case GTE:
                return cb.greaterThanOrEqualTo((Expression<? extends Comparable>) expr, (Comparable) value);
            case LT:
                return cb.lessThan((Expression<? extends Comparable>) expr, (Comparable) value);
            case LTE:
                return cb.lessThanOrEqualTo((Expression<? extends Comparable>) expr, (Comparable) value);
            case LIKE:
                return cb.like((Expression<String>) expr, (String) value);
            case UNDEFINED:
            default:
                throw new IllegalArgumentException("Unsupported operation: " + op);
        }
    }
    private Object getTypedFieldValue(String fieldName, String fieldValue) {
        if (Objects.equals(fieldName, "realHero") || Objects.equals(fieldName, "hasToothpick") || Objects.equals(fieldName, "cool")) {
            return Boolean.valueOf(fieldValue);
        } else if (Objects.equals(fieldName, "mood")) {
            return Mood.fromValue(fieldValue);
        } else if (Objects.equals(fieldName, "weaponType")){
            return WeaponType.fromValue(fieldValue);
        } else if (Objects.equals(fieldName, "color")) {
            return Color.fromValue(fieldValue);
        }
        return fieldValue;
    }
}
