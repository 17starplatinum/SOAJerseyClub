package ru.itmo.cs.dandadan.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import ru.itmo.cs.dandadan.exception.CustomBadRequestException;
import ru.itmo.cs.dandadan.exception.NotFoundException;
import ru.itmo.cs.dandadan.mapper.HumanBeingMapper;
import ru.itmo.cs.dandadan.model.entity.Color;
import ru.itmo.cs.dandadan.model.entity.HumanBeing;
import ru.itmo.cs.dandadan.model.entity.Mood;
import ru.itmo.cs.dandadan.model.entity.WeaponType;
import ru.itmo.cs.dandadan.model.view.Filter;
import ru.itmo.cs.dandadan.model.view.FilterCode;
import ru.itmo.cs.dandadan.model.view.Page;
import ru.itmo.cs.dandadan.model.view.Sort;
import ru.itmo.cs.dandadan.repository.api.HumanBeingRepository;
import ru.itmo.cs.dandadan.util.DateTimeConverter;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.itmo.cs.dandadan.config.HibernateSessionFactoryConfig.getSessionFactory;

@ApplicationScoped
public class HumanBeingRepositoryImpl implements HumanBeingRepository {

    @Inject
    private HumanBeingMapper humanBeingMapper;

    @Inject
    private DateTimeConverter dateTimeConverter;

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
            result.setHumanBeingGetResponseDtos(typedQuery.getResultList());
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
            throw new NotFoundException("", "id", String.valueOf(id));
        }
        session.close();
        return humanBeing;
    }

    @Override
    public HumanBeing saveHumanBeing(HumanBeing humanBeing, Long teamId) {
        Session session = getSessionFactory().openSession();
        EntityTransaction transaction = session.getTransaction();
        transaction.begin();
        if (humanBeing.getId() != null) {
            session.merge(humanBeing);
        } else {
            session.persist(humanBeing);
        }
        transaction.commit();
        session.close();
        return humanBeing;
    }

    @Override
    public HumanBeing updateHumanBeing(long id, HumanBeing incoming) {
        Session session = getSessionFactory().openSession();
        EntityTransaction transaction = session.getTransaction();
        transaction.begin();
        HumanBeing humanBeing = session.get(HumanBeing.class, id);
        incoming.setCreationDate(humanBeing.getCreationDate());
        incoming.setId(humanBeing.getId());
        humanBeingMapper.updateFromHumanBeingRequest(incoming, humanBeing);
        transaction.commit();
        session.close();
        return humanBeing;
    }

    @Override
    public void deleteHumanBeing(long id) {
        Session session = getSessionFactory().openSession();
        EntityTransaction transaction = session.getTransaction();
        transaction.begin();
        HumanBeing humanBeing = getHumanBeing(id);
        session.remove(humanBeing);
        transaction.commit();
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
                Object typedValue = getTypedFieldValue(f.getNestedName() == null ? f.getFieldName() : f.getNestedName(), f.getFieldValue());
                Predicate p = buildPredicateForFilter(cb, expr, f.getFilterCode(), typedValue);
                if (p != null) {
                    predicates.add(p);
                }
            } catch (IllegalArgumentException ex) {
                throw new CustomBadRequestException("filter", "filter code should be one of the following: " +
                        Arrays.stream(FilterCode.values()).map(Enum::name).collect(Collectors.joining(", ")));
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
                Class<?> javaType = expr.getJavaType();
                String pattern = value != null ? "%" + value.toString().toLowerCase() + "%" : "%%";
                if (javaType.isEnum()) {
                    if (!(value instanceof String)) {
                        throw new CustomBadRequestException("filter", "LIKE value must be string for enum fields");
                    }

                    Object finalValue = value;
                    List<Predicate> enumMatches = Arrays.stream(javaType.getEnumConstants())
                            .filter(enumConstant -> enumConstant.toString().toLowerCase().contains(finalValue.toString().toLowerCase()))
                            .map(enumConstant -> cb.equal(expr, enumConstant))
                            .collect(Collectors.toList());

                    if (enumMatches.isEmpty()) {
                        return cb.isTrue(cb.literal(true));
                    }
                    return cb.or(enumMatches.toArray(new Predicate[0]));
                } else if (boolean.class.isAssignableFrom(javaType) || Boolean.class.isAssignableFrom(javaType)) {
                    try {
                        Expression<String> boolAsString = expr.as(String.class);
                        return cb.like(cb.lower(boolAsString), pattern.toLowerCase());
                    } catch (Exception ex) {
                        throw new CustomBadRequestException("boolean", "no value matches either 'true' or 'false' via 'like'");
                    }
                } else if (ZonedDateTime.class.isAssignableFrom(javaType)) {
                    Expression<String> dateExpr = cb.function("to_char", String.class,
                            expr, cb.literal("YYYY-MM-DD\"T\"HH24:MI:SS.SSS'Z'"));
                    return cb.like(cb.lower(dateExpr), pattern);
                } else if (Number.class.isAssignableFrom(javaType) ||
                        int.class.isAssignableFrom(javaType) ||
                        long.class.isAssignableFrom(javaType) ||
                        float.class.isAssignableFrom(javaType) ||
                        double.class.isAssignableFrom(javaType)) {
                    return cb.like(
                            expr.as(String.class),
                            pattern
                    );
                } else if (String.class.isAssignableFrom(javaType)) {
                    return cb.like((Expression<String>) expr, pattern);
                } else {
                    throw new CustomBadRequestException("filter",
                            "LIKE operator not supported for " + javaType.getSimpleName());
                }
            case UNDEFINED:
            default:
                throw new IllegalArgumentException("Unsupported operation: " + op);
        }
    }

    private Object getTypedFieldValue(String fieldName, String fieldValue) {
        if (Objects.equals(fieldName, "realHero") || Objects.equals(fieldName, "hasToothpick") || Objects.equals(fieldName, "cool")) {
            if (fieldValue.equalsIgnoreCase("true") ||  fieldValue.equalsIgnoreCase("false")) {
                return Boolean.valueOf(fieldValue);
            } else {
                return fieldValue;
            }
        } else if (Objects.equals(fieldName, "creationDate")) {
            return dateTimeConverter.parseZonedDateTime(fieldValue);
        } else if (Objects.equals(fieldName, "mood")) {
            try {
                return Mood.fromValue(fieldValue);
            } catch (CustomBadRequestException ex) {
                boolean isValid = Arrays.stream(Mood.values()).anyMatch(
                        mood -> mood.toString().toLowerCase().contains(fieldValue.toLowerCase()));
                if (isValid) {
                    return fieldValue;
                }
                throw new CustomBadRequestException("mood", "no value matches one of the given values, directly or via 'like'");
            }
        } else if (Objects.equals(fieldName, "weaponType")){
            WeaponType weaponType = WeaponType.fromValue(fieldValue);
            if (weaponType == null) {
                boolean isValid = Arrays.stream(WeaponType.values()).anyMatch(
                        type -> type.toString().toLowerCase().contains(fieldValue.toLowerCase()));
                if (isValid) {
                    return fieldValue;
                }
                throw new CustomBadRequestException("weaponType", "no value matches one of the given values, directly or via 'like'");
            }
            return weaponType;
        } else if (Objects.equals(fieldName, "color")) {
            try {
                return Color.fromValue(fieldValue);
            } catch (CustomBadRequestException ex) {
                boolean isValid = Arrays.stream(Color.values()).anyMatch(
                        color -> color.toString().toLowerCase().contains(fieldValue.toLowerCase()));
                if (isValid) {
                    return fieldValue;
                }
                return Color.UNDEFINED;
            }
        }
        return fieldValue;
    }
}
