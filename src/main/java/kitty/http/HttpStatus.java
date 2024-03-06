/*
 * Copyright 2013-2023 Julian Jupiter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kitty.http;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Julian Jupiter
 */
public enum HttpStatus {
    CONTINUE(100, HttpStatusSeries.INFORMATIONAL, "Continue"),
    SWITCHING_PROTOCOLS(101, HttpStatusSeries.INFORMATIONAL, "Switching Protocols"),
    PROCESSING(102, HttpStatusSeries.INFORMATIONAL, "Processing"),
    EARLY_HINTS(103, HttpStatusSeries.INFORMATIONAL, "Early Hints"),
    OK(200, HttpStatusSeries.SUCCESSFUL, "OK"),
    CREATED(201, HttpStatusSeries.SUCCESSFUL, "Created"),
    ACCEPTED(202, HttpStatusSeries.SUCCESSFUL, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, HttpStatusSeries.SUCCESSFUL, "Non-Authoritative Information"),
    NO_CONTENT(204, HttpStatusSeries.SUCCESSFUL, "No Content"),
    RESET_CONTENT(205, HttpStatusSeries.SUCCESSFUL, "Reset Content"),
    PARTIAL_CONTENT(206, HttpStatusSeries.SUCCESSFUL, "Partial Content"),
    MULTI_STATUS(207, HttpStatusSeries.SUCCESSFUL, "Multi-Status"),
    ALREADY_REPORTED(208, HttpStatusSeries.SUCCESSFUL, "Already Reported"),
    IM_USED(226, HttpStatusSeries.SUCCESSFUL, "IM Used"),
    MULTIPLE_CHOICES(300, HttpStatusSeries.REDIRECTION, "Multiple Choices"),
    MOVED_PERMANENTLY(301, HttpStatusSeries.REDIRECTION, "Moved Permanently"),
    FOUND(302, HttpStatusSeries.REDIRECTION, "Found"),
    SEE_OTHER(303, HttpStatusSeries.REDIRECTION, "See Other"),
    NOT_MODIFIED(304, HttpStatusSeries.REDIRECTION, "Not Modified"),
    TEMPORARY_REDIRECT(307, HttpStatusSeries.REDIRECTION, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, HttpStatusSeries.REDIRECTION, "Permanent Redirect"),
    BAD_REQUEST(400, HttpStatusSeries.CLIENT_ERROR, "Bad Request"),
    UNAUTHORIZED(401, HttpStatusSeries.CLIENT_ERROR, "Unauthorized"),
    PAYMENT_REQUIRED(402, HttpStatusSeries.CLIENT_ERROR, "Payment Required"),
    FORBIDDEN(403, HttpStatusSeries.CLIENT_ERROR, "Forbidden"),
    NOT_FOUND(404, HttpStatusSeries.CLIENT_ERROR, "Not Found"),
    METHOD_NOT_ALLOWED(405, HttpStatusSeries.CLIENT_ERROR, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, HttpStatusSeries.CLIENT_ERROR, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, HttpStatusSeries.CLIENT_ERROR, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, HttpStatusSeries.CLIENT_ERROR, "Request Timeout"),
    CONFLICT(409, HttpStatusSeries.CLIENT_ERROR, "Conflict"),
    GONE(410, HttpStatusSeries.CLIENT_ERROR, "Gone"),
    LENGTH_REQUIRED(411, HttpStatusSeries.CLIENT_ERROR, "Length Required"),
    PRECONDITION_FAILED(412, HttpStatusSeries.CLIENT_ERROR, "Precondition Failed"),
    CONTENT_TOO_LARGE(413, HttpStatusSeries.CLIENT_ERROR, "Content Too Large"),
    URI_TOO_LONG(414, HttpStatusSeries.CLIENT_ERROR, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, HttpStatusSeries.CLIENT_ERROR, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, HttpStatusSeries.CLIENT_ERROR, "Requested range not satisfiable"),
    EXPECTATION_FAILED(417, HttpStatusSeries.CLIENT_ERROR, "Expectation Failed"),
    I_AM_A_TEAPOT(418, HttpStatusSeries.CLIENT_ERROR, "I'm a teapot"),
    MISDIRECTED_REQUEST(421, HttpStatusSeries.CLIENT_ERROR, "Misdirected Request"),
    UNPROCESSABLE_ENTITY(422, HttpStatusSeries.CLIENT_ERROR, "Unprocessable Entity"),
    LOCKED(423, HttpStatusSeries.CLIENT_ERROR, "Locked"),
    FAILED_DEPENDENCY(424, HttpStatusSeries.CLIENT_ERROR, "Failed Dependency"),
    TOO_EARLY(425, HttpStatusSeries.CLIENT_ERROR, "Too Early"),
    UPGRADE_REQUIRED(426, HttpStatusSeries.CLIENT_ERROR, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, HttpStatusSeries.CLIENT_ERROR, "Precondition Required"),
    TOO_MANY_REQUESTS(429, HttpStatusSeries.CLIENT_ERROR, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, HttpStatusSeries.CLIENT_ERROR, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, HttpStatusSeries.CLIENT_ERROR, "Unavailable For Legal Reasons"),
    INTERNAL_SERVER_ERROR(500, HttpStatusSeries.SERVER_ERROR, "Internal Server Error"),
    NOT_IMPLEMENTED(501, HttpStatusSeries.SERVER_ERROR, "Not Implemented"),
    BAD_GATEWAY(502, HttpStatusSeries.SERVER_ERROR, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, HttpStatusSeries.SERVER_ERROR, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, HttpStatusSeries.SERVER_ERROR, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, HttpStatusSeries.SERVER_ERROR, "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES(506, HttpStatusSeries.SERVER_ERROR, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, HttpStatusSeries.SERVER_ERROR, "Insufficient Storage"),
    LOOP_DETECTED(508, HttpStatusSeries.SERVER_ERROR, "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED(509, HttpStatusSeries.SERVER_ERROR, "Bandwidth Limit Exceeded"),
    NOT_EXTENDED(510, HttpStatusSeries.SERVER_ERROR, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, HttpStatusSeries.SERVER_ERROR, "Network Authentication Required");

    private final int value;
    private final HttpStatusSeries series;
    private final String reasonPhrase;

    HttpStatus(int value, HttpStatusSeries series, String reasonPhrase) {
        this.value = value;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return value;
    }

    public HttpStatusSeries series() {
        return series;
    }

    public String reasonPhrase() {
        return reasonPhrase;
    }

    public boolean is1xxInformational() {
        return this.series() == HttpStatusSeries.INFORMATIONAL;
    }

    public boolean is2xxSuccessful() {
        return this.series() == HttpStatusSeries.SUCCESSFUL;
    }

    public boolean is3xxRedirection() {
        return this.series() == HttpStatusSeries.REDIRECTION;
    }

    public boolean is4xxClientError() {
        return this.series() == HttpStatusSeries.CLIENT_ERROR;
    }

    public boolean is5xxServerError() {
        return this.series() == HttpStatusSeries.SERVER_ERROR;
    }

    public boolean isError() {
        return this.is4xxClientError() || this.is5xxServerError();
    }

    @Override
    public String toString() {
        return this.value + " " + this.reasonPhrase;
    }

    public static HttpStatus of(int statusCode) {
        return resolve(statusCode)
                .orElseThrow(() -> new IllegalArgumentException("No matching constant for [" + statusCode + "]"));
    }

    public static Optional<HttpStatus> resolve(int statusCode) {
        return Arrays.stream(values())
                .filter(httpStatus -> httpStatus.value == statusCode)
                .findFirst();
    }
}
