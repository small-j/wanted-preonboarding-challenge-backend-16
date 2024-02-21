package com.wanted.preonboarding.observer;

public interface IObserver<T> {
    public boolean update(T msg);
}
