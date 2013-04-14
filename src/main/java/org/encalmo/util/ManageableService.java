package org.encalmo.util;

/**
 * Interface of manageable components, i.e. able to be started and stopped
 *
 * @see org.encalmo.util.SingleThreadService
 * @see org.encalmo.tagstats.GenericTagStatsService
 */
public interface ManageableService {

    void start() throws Exception;

    void stop() throws Exception;
}
