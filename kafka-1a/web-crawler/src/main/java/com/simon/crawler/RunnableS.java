package com.simon.crawler;

public interface RunnableS extends Runnable{
    @Override
    public void run();

    public void shutdown();    
}
