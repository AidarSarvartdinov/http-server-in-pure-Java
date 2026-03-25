package com.server.http.server.common;

public enum HttpStatus {
    OK(200, Series.SUCCESSFUL, "OK"),
    CREATED(201, Series.SUCCESSFUL, "Created"),
    NOT_FOUND(404, Series.CLIENT_ERROR, "Not Found");

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public int getCode() {
        return code;
    }

    public Series getSeries() {
        return series;
    }

    public boolean is1xxInformational() {
        return this.series == Series.INFORMATIONAL;
    }

    public boolean is2xxSuccessful() {
        return this.series == Series.SUCCESSFUL;
    }

    public boolean is3xxRedirection() {
        return this.series == Series.REDIRECTION;
    }

    public boolean is4xxClientError() {
        return this.series == Series.CLIENT_ERROR;
    }

    public boolean is5xxServerError() {
        return this.series == Series.SERVER_ERROR;
    }

    public boolean isError() {
        return is4xxClientError() || is5xxServerError();
    }

    private final int code;
    private final Series series;
    private final String reasonPhrase;

    HttpStatus(int code, Series series, String reasonPhrase) {
        this.code = code;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }
    
    public enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;

        Series(int value) {
            this.value = value;
        }
    }
}
