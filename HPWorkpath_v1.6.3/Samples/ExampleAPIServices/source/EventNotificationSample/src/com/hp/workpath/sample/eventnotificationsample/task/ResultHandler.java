package com.hp.workpath.sample.eventnotificationsample.task;

public interface ResultHandler {
    public void handleComplete();
    public void handleException(final Throwable t);
    public void handleUpdate(String updateData);
}
