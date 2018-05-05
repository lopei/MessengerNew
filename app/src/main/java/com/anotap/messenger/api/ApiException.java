package com.anotap.messenger.api;

import com.anotap.messenger.api.model.Error;

/**
 * Created by admin on 19.03.2017.
 * phoenix
 */
public class ApiException extends Exception {

    private final Error error;

    public ApiException(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
