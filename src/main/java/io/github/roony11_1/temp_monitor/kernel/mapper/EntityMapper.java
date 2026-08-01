package io.github.roony11_1.temp_monitor.kernel.mapper;

/**
 * 
 * EntityMapper
 * @param <E> Entity
 * @param <SR> Entity Summary Response
 */
public interface EntityMapper<E, SR>
{
    public SR toSummaryResponse(E entity);
}
