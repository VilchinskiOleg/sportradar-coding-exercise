package org.sportradar.lib.service.dao;

import java.util.Collection;

public interface CrudScoreboardDao<T> {

    Collection<T> create(T match);
    Collection<T> findAll();
    Collection<T> delete(String id);
}
