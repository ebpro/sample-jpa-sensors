package fr.univtln.bruno.samples.utils.dao;

import fr.univtln.bruno.samples.utils.dao.entities.SimpleEntity;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.lang.reflect.ParameterizedType;

/**
 * The type Abstract dao.
 *
 * @param <E> the type parameter
 */
@Getter
@RequiredArgsConstructor
@Log
public class AbstractDAO<E extends SimpleEntity<I>, I> implements DAO<E, I> {

    private final Class<I> myIdType;

    private final Class<E> myEntityType;

    private final EntityManager entityManager;

    public AbstractDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
        Class c = getClass();
        //In case of use with implementation class using raw types (mandatory with EJBs).
        while (!(c.getGenericSuperclass() instanceof ParameterizedType))
            c = c.getSuperclass();
        myEntityType = (Class<E>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
        myIdType = (Class<I>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[1];
    }
}

