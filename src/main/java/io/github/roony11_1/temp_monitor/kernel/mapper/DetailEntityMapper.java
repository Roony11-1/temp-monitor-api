package io.github.roony11_1.temp_monitor.kernel.mapper;

/**
 * 
 * DetailEntityMapper
 * @param <E> Entity
 * @param <R> Entity Response (detalle)
 */
public interface DetailEntityMapper<E, R>
{
    public R toResponse(E entity);
}
