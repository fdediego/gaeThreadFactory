package com.domain.queue;


import com.domain.service.IRfqListener;
import com.domain.service.Rfq;
import com.domain.service.TimeoutQueueService;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.logging.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath*:/test-config.xml")
public class SimpleQueueTest implements IRfqListener
{
    private static final Logger log = Logger.getLogger(SimpleQueueTest.class.getName());
    private static  final LocalServiceTestHelper helper = new LocalServiceTestHelper();

    @BeforeClass
    public static void initialSetup()
    {
        helper.setUp();
     }

    @AfterClass
   	public static void finalTearDown() {
   		helper.tearDown();
   	}

    private long triggerTimestampMs;


    @Autowired
    private TimeoutQueueService timeoutOutRfqQueueService;


    @Before
    public void setup()
    {
        timeoutOutRfqQueueService.setRfqListener(this);
        timeoutOutRfqQueueService.start();
    }

    @After
    public void stop()
    {
        timeoutOutRfqQueueService.shutdown();
    }

    @Test
    public void startStop()
    {
        log.info("==== called startStop ====");
    }

    @Test
    @Ignore
    //-----------------------
    //     THIS IS IGNORED
    //-----------------------
    public void placeItemOnAndWaitForPurge() throws InterruptedException
    {
        Rfq rfq1 = Rfq.create();

        timeoutOutRfqQueueService.add(rfq1);

        assert(expectedSize(1));

        Thread.sleep(9100);

        long elapsedTimeMs = triggerTimestampMs - rfq1.getCreationTimeMs();

        Assert.assertEquals(9000,elapsedTimeMs,120);

    }


    private boolean expectedSize(int size)
    {
       int actualSize = timeoutOutRfqQueueService.size();
       if(actualSize == size)
           return true;

       log.severe("expected " + size + " found " + actualSize);
       return false;
    }

    @Override
    public void onTrigger(Rfq rfq)
    {
        triggerTimestampMs = System.currentTimeMillis();
        log.info("called");
    }
}
