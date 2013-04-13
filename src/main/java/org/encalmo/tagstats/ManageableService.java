package org.encalmo.tagstats;

/**
 * Interface of manageable components, i.e. able to be started and stopped
 *
 * @see SingleThreadService
 * @see TagStatsService
 */
public interface ManageableService {

    void start() throws Exception;

    void stop() throws Exception;
}
