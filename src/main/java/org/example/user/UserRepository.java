package org.example.user;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.example.common.PageRequest;
import org.example.enums.Gender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
public class UserRepository {

    private static final List<String> ALLOWED_SORT_FIELDS = List.of("username", "gender", "age", "createdAt");
    private static final List<String> ALLOWED_FILTER_FIELDS = List.of("username", "gender", "age");
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String GENDER = "gender";
    private static final String AGE = "age";

    @PersistenceContext
    private EntityManager em;

    public User add(User user) {
        em.persist(user);
        return user;
    }

    public User update(User user) {
        return em.merge(user);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public List<User> findAll(PageRequest pageRequest) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, String> entry : pageRequest.getSearchParams().entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();
            if (ALLOWED_FILTER_FIELDS.contains(field)) {
                switch (field) {
                    case USERNAME:
                        predicates.add(cb.like(cb.lower(user.get(USERNAME)), "%" + value.toLowerCase() + "%"));
                        break;
                    case EMAIL:
                        predicates.add(cb.like(cb.lower(user.get(EMAIL)), "%" + value.toLowerCase() + "%"));
                        break;
                    case GENDER:
                        predicates.add(cb.equal(user.get(GENDER), Gender.valueOf(value)));
                        break;
                    case AGE:
                        predicates.add(cb.equal(user.get(AGE), Integer.valueOf(value)));
                        break;
                }
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        if (pageRequest.getSortBy() != null && ALLOWED_SORT_FIELDS.contains(pageRequest.getSortBy())) {
            Order order = pageRequest.isAsc() ? cb.asc(user.get(pageRequest.getSortBy())) : cb.desc(user.get(pageRequest.getSortBy()));
            cq.orderBy(order);
        }

        TypedQuery<User> query = em.createQuery(cq);
        query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
        query.setMaxResults(pageRequest.getPageSize());

        return query.getResultList();
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id).orElseThrow(NotFoundException::new);
        em.remove(user);
    }

    public boolean existsByUsername(String username) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }

    public Optional<User> findByActivationToken(String token) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.token = :token", User.class);
        query.setParameter("token", token);
        return query.getResultList().stream().findFirst();
    }
}