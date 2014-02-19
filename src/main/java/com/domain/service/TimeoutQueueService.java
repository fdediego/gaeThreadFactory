package com.domain.service;

/**
 * Author: wge
 * Date: 21/10/2013
 * Time: 17:52
 */

public class TimeoutQueueService extends CoreRfqQueueService
{

    public TimeoutQueueService(int maxQueueCapacity, long initialDelaySeconds, long scanPeriodSeconds, long itemLifetimeInSeconds)
    {
        super("TimeoutScanThread", maxQueueCapacity, initialDelaySeconds, scanPeriodSeconds, itemLifetimeInSeconds);
    }


}
