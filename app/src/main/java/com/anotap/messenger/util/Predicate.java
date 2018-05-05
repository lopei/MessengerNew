package com.anotap.messenger.util;

/**
 * Created by ruslan.kolbasa on 01.02.2017.
 * phoenix
 */
public interface Predicate<T> {
    boolean test(T t);
}