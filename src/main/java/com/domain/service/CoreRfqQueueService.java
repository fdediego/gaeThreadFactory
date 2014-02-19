package com.domain.service;


import java.util.concurrent.*;
import java.util.logging.Logger;


abstract class CoreRfqQueueService implements IRunnableService
{
    private static final Logger log = Logger.getLogger(CoreRfqQueueService.class.getName());

    private final ScheduledExecutorService coreQueueScanner;
    private ScheduledFuture<?> scannerHandle;
    private volatile boolean running = false;
    private final long scanPeriodSeconds;
    private final long MAX_QUEUE_LIFETIME_MILLISECONDS;
    public  final long initialDelaySeconds;
    private final BlockingQueue<Rfq> rfqQueue;
    private final String threadName;
    private IRfqListener rfqListener;

    public void setRfqListener(IRfqListener rfqListener)
    {
        this.rfqListener = rfqListener;
    }



    public CoreRfqQueueService(String threadName, int maxQueueCapacity, long initialDelaySeconds, long scanPeriodSeconds, long itemLifetimeInSeconds)
    {
        this.threadName = threadName;
        //@ToDo appears to work in production, but not in local unit tests.
        ThreadFactory factory =  com.google.appengine.api.ThreadManager.currentRequestThreadFactory();
        coreQueueScanner = Executors.newScheduledThreadPool(1, factory);

        rfqQueue = new ArrayBlockingQueue<Rfq>(maxQueueCapacity);
        this.scanPeriodSeconds = scanPeriodSeconds;
        this.initialDelaySeconds = initialDelaySeconds;
        MAX_QUEUE_LIFETIME_MILLISECONDS = itemLifetimeInSeconds * 1000;
        log.info(" scanPeriodSeconds = " + scanPeriodSeconds + "\nitemLifetimeInSeconds = " + itemLifetimeInSeconds);
    }

    public void start()
    {
        log.info(threadName + " started");
        running = true;
        scannerHandle =  coreQueueScanner.scheduleAtFixedRate(this, initialDelaySeconds, scanPeriodSeconds, TimeUnit.SECONDS);
    }

    public void shutdown()
    {
        log.info(threadName + "called shutdown");
        running = false;

        purgeQueueOfAllRemainingItems();

        scannerHandle.cancel(true);
    }


    public void add(Rfq rfq)
    {
        if(running)
        {
            rfqQueue.add(rfq);
            log.info(threadName + " added rfq id = " + rfq.getId());
        }
        else
            log.warning(threadName + " not adding this Rfq, because we are stopping");
    }



    @Override
    public void run()
    {
        log.info(threadName + " seeking timeout");

        if(running)
        {
            long now = System.currentTimeMillis();
            long elapsedTimeMs;
            for (Rfq rfq : rfqQueue)
            {
                elapsedTimeMs = now - rfq.getCreationTimeMs();
                if(elapsedTimeMs > MAX_QUEUE_LIFETIME_MILLISECONDS)
                {
                    log.info(threadName + " found a timedout rfqId = " + rfq.getId());
                    if(rfqListener !=null)
                        rfqListener.onTrigger(rfq);
                }
            }
        }
    }

    private void purgeQueueOfAllRemainingItems()
    {
        for (Rfq currentRfq : rfqQueue)
        {
            log.info("purging rfqId = " + currentRfq.getId());
            rfqQueue.remove(currentRfq);
        }
    }

    public int size()
    {
        return rfqQueue.size();
    }
}
