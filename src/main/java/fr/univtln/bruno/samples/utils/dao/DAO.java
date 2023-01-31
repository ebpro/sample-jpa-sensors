package fr.univtln.bruno.samples.utils.dao;

import fr.univtln.bruno.samples.utils.AppConstants;
import fr.univtln.bruno.samples.utils.dao.entities.SimpleEntity;
import fr.univtln.bruno.samples.utils.dao.exceptions.DAOException;
import fr.univtln.bruno.samples.utils.dao.exceptions.PageNotFoundException;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The interface Dao.
 * A generic DAO interface to define the minimal functionalities for entities that only need to implement the SimpleEntity
 * interface (just a getter on a standard id). The class AbstractDAO<T> implements the main methods. The DAO just need
 * an entity manager.
 * <p>
 * If the class Dog implement SimplEntity, i.e. has UUID getUuid() method. The followig snippet generate a DAO.
 * NOTICE the brackets to generate a subclass needed for introspection.
 * <p>
 * <code>
 * DAO<Doc> dogDAO = new AbstractDAO<>(){}
 * </code>
 * <p>
 * @param <E> the class of the entity.
 */
public interface DAO<E extends SimpleEntity<I>, I> extends AutoCloseable {
    /**
     * The default size for batch operations
     */
    int BATCH_SIZE = 1000;

    static List<Method> getInheritedMethods(Class<?> type) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            methods.addAll(Arrays.asList(c.getDeclaredMethods()));
        }
        return methods;
    }

    /**
     * Persist an entity of type T
     *
     * @param e the entity to be persisted
     */
    default void persist(E e) {
        getEntityManager().persist(e);
    }

    /**
     * Return the entity manager.
     *
     * @return the entity manager
     */
    EntityManager getEntityManager();

    /**
     * Starts a transaction.
     */
    default void begin() {
      getEntityManager().getTransaction().begin();
    }

    /**
     * Validates a transaction
     */
    default void commit() {
        getEntityManager().getTransaction().commit();
    }

    /**
     * cancel a transaction
     */
    default void rollback() {
        getEntityManager().getTransaction().rollback();
    }

    /**
     * Merge t.
     *
     * @param e the t
     * @return the t
     */
    default E merge(E e) {
        return getEntityManager().merge(e);
    }

    /**
     * Refresh.
     *
     * @param e the t
     */
    default void refresh(E e) {
        getEntityManager().refresh(e);
    }

    /**
     * Clear int.
     *
     * @return the int
     */
    default int deleteAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaDelete<E> criteriaDelete = cb.createCriteriaDelete(getMyEntityType());
        return getEntityManager().createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Gets the type of the entity
     *
     * @return the type of the managed entity.
     */
    Class<E> getMyEntityType();

    /**
     * Gets the type of the ID of the entity
     *
     * @return the type of the id of the managed entity.
     */
    Class<I> getMyIdType();

    /**
     * Find all list.
     *
     * @return the list
     */
    default List<E> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(getMyEntityType());
        Root<E> rootEntry = cq.from(getMyEntityType());
        CriteriaQuery<E> all = cq.select(rootEntry);

        TypedQuery<E> allQuery = getEntityManager().createQuery(all);
        return allQuery.getResultList();
    }

    default List<E> findWithNativeQuery(String sql, Class type) {
        return getEntityManager().createNativeQuery(sql, type).getResultList();
    }

    @Override
    default void close() {
        getEntityManager().close();
    }

    /**
     * Flush.
     */
    default void flush() {
        getEntityManager().flush();
    }

    /**
     * Gets transaction.
     *
     * @return the transaction
     */
    default EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    default void delete(I id) throws DAOException {
        //todo: replace by a direct query.
        //CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        //CriteriaDelete<T> criteriaDelete = cb.createCriteriaDelete(getMyType());
        //return getEntityManager().createQuery(criteriaDelete).executeUpdate();

        getEntityManager().remove(find(id));
    }

    /**
     * Find t.
     *
     * @param id the id
     * @return the t
     */
    default E find(I id) throws DAOException {
        return getEntityManager().find(getMyEntityType(), id);
    }

    default List<I> getIds() throws DAOException {
        return getIds(false);
    }

    default List<I> getIds(boolean reverse) throws DAOException {
        return getIds(reverse, 0);
    }

    default List<I> getIds(boolean reverse, int first) throws DAOException {
        return getIds(reverse, first, -1);
    }

    default List<I> getIds(boolean reverse, int first, int limit) throws DAOException {
        return getIDsQuery(reverse, first, limit).getResultList();
    }

    private TypedQuery<I> getIDsQuery(boolean reverse, int first, int limit) throws DAOException {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<I> cq = cb.createQuery(getMyIdType());
        Root<E> t = cq.from(getMyEntityType());
        cq.select(t.get(getIdname()));
        cq.orderBy(reverse ? cb.desc(t.get(getIdname())) : cb.asc(t.get(getIdname())));
        TypedQuery<I> q = getEntityManager().createQuery(cq);
        if (first > -1) q.setFirstResult(first);
        if (limit > -1) q.setMaxResults(limit);
        return q;
    }

    private String getIdname() throws DAOException {
        String idname = getInheritedFields(getMyEntityType()).stream().filter(f -> f.isAnnotationPresent(Id.class)).map(Field::getName).findFirst()
                .orElseGet(getInheritedMethods(getMyEntityType()).stream().filter(f -> f.isAnnotationPresent(Id.class)).map(Method::getName).findFirst()::get);
        if (idname == null)
            throw new DAOException(
                    AppConstants.ErrorCode.DAO_EXCEPTION,
                    "No field or method annotated with @Id for class: " + getMyEntityType().getName(),
                    "", "");
        return idname;
    }

    static List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    default long getSize() {
        return getSizeQuery().getSingleResult();
    }

    private TypedQuery<Long> getSizeQuery() {
        CriteriaBuilder qb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(getMyEntityType())));
        return getEntityManager().createQuery(cq);
    }

    default Page<E> findAllByPage(boolean reverse, int pagenumber, int pagesize, int limit) throws DAOException {
        int size = getSizeQuery().getSingleResult().intValue();
        if (limit > 0 && limit < size) size = limit;
        int first = pagenumber * pagesize;
        if (first >= size) throw new PageNotFoundException();
        return queryByPage(pagenumber, pagesize, size, findAllQuery(reverse, first, Integer.min(pagesize, size)));
    }

    default Page<E> queryByPage(int pagenumber, int pagesize, int totalItems, Query contentQuery) {
        return new Page<E>(pagenumber, pagesize, totalItems, (int) Math.ceil(totalItems / (float) pagesize), contentQuery.getResultList());
    }

    private TypedQuery<E> findAllQuery(boolean reverse, int first, int limit) throws DAOException {
        String queryName = getMyEntityType().getSimpleName().toLowerCase() + ".findAll";
        TypedQuery<E> result = getEntityManager().createNamedQuery(queryName, getMyEntityType());
        if (result == null) {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<E> cq = cb.createQuery(getMyEntityType());
            Root<E> t = cq.from(getMyEntityType());
            cq.select(t);
            cq.orderBy(reverse ? cb.desc(t.get(getIdname())) : cb.asc(t.get(getIdname())));
            result = getEntityManager().createQuery(cq);
        } else if (first > -1) result.setFirstResult(first);
        if (limit > -1) result.setMaxResults(limit);

        return result;
    }

    /**
     * Add the collection of items to the underlaying datasource.
     *
     * @param collection : the collection of items to create
     * @return the number created items.
     * @throws DAOException
     */
    default int persist(Collection<E> collection) throws DAOException {
        return persist(collection.stream());
    }

    default int persist(Stream<E> stream) throws DAOException {
        final int[] nbAdded = {0};
        FlushModeType flushmode = getEntityManager().getFlushMode();
        getEntityManager().setFlushMode(FlushModeType.COMMIT);
        stream.forEachOrdered(d -> {
                    getEntityManager().persist(d);
                    if (nbAdded[0]++ % BATCH_SIZE == 0) {
                        //Push data to the database in the same transaction
                        getEntityManager().flush();
                        getEntityManager().clear();
                    }
                }
        );
        getEntityManager().flush();
        getEntityManager().clear();
        getEntityManager().setFlushMode(flushmode);
        return nbAdded[0];
    }

    /**
     * Add the items given by the iterator to the underlaying datasource.
     *
     * @param iterator : the iterator on the items to create.
     * @return the number of created items.
     * @throws DAOException
     */
    default int persist(Iterator<E> iterator) throws DAOException {
        return persist(StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT | Spliterator.NONNULL), false));
    }

    default List<E> findAll(boolean reverse) throws DAOException {
        return findAll(reverse, 0);
    }

    default List<E> findAll(boolean reverse, int first) throws DAOException {
        return findAll(reverse, first, -1);
    }

    default List<E> findAll(boolean reverse, int first, int limit) throws DAOException {
        return findAllQuery(reverse, first, limit).getResultList();
    }

    default List<E> findAll(int first, int limit) throws DAOException {
        return findAll(false, first, limit);
    }

    default List<E> findAll(int first) throws DAOException {
        return findAll(false, first, -1);
    }

    default List<E> findWithNamedQuery(String queryName) throws DAOException {
        return findWithNamedQuery(queryName, -1, 0);
    }

    default List<E> findWithNamedQuery(String queryName, int first, int resultLimit) throws DAOException {
        return findWithNamedQuery(queryName, null, first, resultLimit);
    }

    default List<E> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int first, int limit) {
        return getNamedQuery(namedQueryName, parameters, first, limit).getResultList();
    }

    private TypedQuery<E> getNamedQuery(String namedQueryName, Map<String, Object> parameters, int first, int limit) {
        Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
        TypedQuery<E> query = getEntityManager().createNamedQuery(namedQueryName, getMyEntityType());
        if (limit > 0)
            query.setMaxResults(limit);

        if (first > 0)
            query.setFirstResult(first);

        for (Map.Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }

    default List<E> findWithNamedQuery(String queryName, int resultLimit) throws DAOException {
        return findWithNamedQuery(queryName, null, 0, resultLimit);
    }

    default List<E> findWithNamedQuery(String namedQueryName, Map<String,Object> parameters) throws DAOException {
        return findWithNamedQuery(namedQueryName, parameters, -1, 0);
    }

    default E update(E e) {
        return getEntityManager().merge(e);
    }
}
