package com.wanted.preonboarding.observer;

public interface ISubject<T> {
    public boolean register(IObserver newObserver);
    public boolean remove(IObserver observer);
    public boolean sendMessage(T msg);
}
