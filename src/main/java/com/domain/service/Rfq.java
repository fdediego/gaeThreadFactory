package com.domain.service;


import java.util.concurrent.atomic.AtomicLong;

public class Rfq
{
    private static final  AtomicLong idGenerator = new AtomicLong(0);

    private final Long id = idGenerator.getAndIncrement();
    private long creationTimeMs = System.currentTimeMillis();//dont care about accuracy too much.

    public Long getId()
    {
        return id;
    }


    private Rfq()
    {
    }

    public long getCreationTimeMs()
    {
        return creationTimeMs;
    }

    public static Rfq create()
    {
        return new Rfq();
    }
}
