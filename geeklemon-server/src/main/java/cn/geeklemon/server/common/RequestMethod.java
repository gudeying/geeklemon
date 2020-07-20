package cn.geeklemon.server.common;

import io.netty.handler.codec.http.HttpMethod;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 10:49
 * Modified by : kavingu
 */
public enum RequestMethod {
    /**
     * GET
     */
    GET(HttpMethod.GET),
    /**
     * HEAD
     */
    HEAD(HttpMethod.HEAD),
    /**
     * POST
     */
    POST(HttpMethod.POST),
    /**
     * PUT
     */
    PUT(HttpMethod.PUT),
    /**
     * PATCH
     */
    PATCH(HttpMethod.PATCH),
    /**
     * DELETE
     */
    DELETE(HttpMethod.DELETE),
    /**
     * OPTIONS
     */
    OPTIONS(HttpMethod.OPTIONS),
    /**
     * TRACE
     */
    TRACE(HttpMethod.TRACE),

    ANY;

    HttpMethod httpMethod;

    RequestMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    RequestMethod() {
        
    }

    public static HttpMethod getHttpMethod(RequestMethod requestMethod) {
        for (RequestMethod method : values()) {
            if (requestMethod == method) {
                return method.httpMethod;
            }
        }
        return null;
    }
}
