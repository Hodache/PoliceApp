package com.http;

import java.io.InputStream;

class RequestResponse {
    protected int responseCode;
    protected InputStream responseStream;

    protected RequestResponse(int responseCode, InputStream responseStream) {
        this.responseCode = responseCode;
        this.responseStream = responseStream;
    }
}
