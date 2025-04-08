package org.sportradar.lib.service.dao;

import java.util.Collection;

public interface CrudDao<T> {

    T create(T match);
    Collection<T> findAll();
    int delete(String id);
}
