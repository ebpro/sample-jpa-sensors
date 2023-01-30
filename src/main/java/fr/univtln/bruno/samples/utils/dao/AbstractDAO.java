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
 * @param <T> the type parameter
 */
@Getter
@RequiredArgsConstructor
@Log
public class AbstractDAO<T extends SimpleEntity<I>, I> implements DAO<T, I> {

    private final Class<T> myType;

    private final EntityManager entityManager;

    public AbstractDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
        Class c = getClass();
        //In case of use with implementation class using raw types (mandatory with EJBs).
        while (!(c.getGenericSuperclass() instanceof ParameterizedType))
            c = c.getSuperclass();
        myType = (Class<T>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
    }
}

