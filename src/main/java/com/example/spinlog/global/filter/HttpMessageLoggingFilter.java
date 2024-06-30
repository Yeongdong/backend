package com.example.spinlog.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
public class HttpMessageLoggingFilter extends AbstractRequestLoggingFilter {
    private final String temporaryAuthHeader;

    private static final Predicate<String> responseHeaderPredicate = headerName -> {
        List<String> headers = List.of(
                "content-type",
                "content-length",
                "location",
                "set-cookie"
        );
        return headers.contains(headerName.toLowerCase());
    };

    public void init() {
        setIncludePayload(true);
        setIncludeQueryString(true);
        setIncludeHeaders(true);
        setMaxPayloadLength(1000);

        setHeaderPredicate(headerName -> {
            List<String> headers = List.of(
                    "host",
                    "user-agent",
                    "referer",
                    "accept",
                    "accept-encoding",
                    "x-forwarded-for",
                    "x-forwarded-proto",
                    "x-real-ip",
                    "request-id",
                    "authorization",
                    "content-type",
                    "content-length",
                    temporaryAuthHeader.toLowerCase()
            );
            return headers.contains(headerName.toLowerCase());
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = getRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);

        super.doFilterInternal(requestWrapper, responseWrapper, filterChain);

        String requestMessage = createMessage(requestWrapper, "\nREQUEST :\n", "\n");
        String responseMessage = createMessage(responseWrapper, "\nRESPONSE :\n", "\n");
        log.info("{}{}", requestMessage, responseMessage);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {

    }

    protected String createMessage(ContentCachingRequestWrapper request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append(request.getMethod()).append(' ');
        msg.append(request.getRequestURI());

        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }

        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append("\nclient = ").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append("\nsession = ").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append("\nuser = ").append(user);
            }
        }

        if (isIncludeHeaders()) {
            HttpHeaders headers = new ServletServerHttpRequest(request).getHeaders();
            StringBuilder headersString = new StringBuilder();
            if (getHeaderPredicate() != null) {
                Enumeration<String> names = request.getHeaderNames();
                while (names.hasMoreElements()) {
                    String header = names.nextElement();
                    if (!getHeaderPredicate().test(header)) {
                        headers.remove(header);
                    } else {
                        headersString.append("\t").append(header).append(": ").append(headers.get(header)).append("\n");
                    }
                }
            }
            msg.append("\nheaders = {\n").append(headersString.toString()).append("}");
        }

        if (isIncludePayload()) {
            String payload = getReuqestBody(request);
            msg.append("\npayload = ").append(payload);
        }

        msg.append(suffix);
        return msg.toString();
    }

    private String createMessage(ContentCachingResponseWrapper response, String prefix, String suffix) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append(response.getStatus());

        // {'Content-Type', 'Content-Length', 'Location', 'Set-Cookie'} headers , response body logging
        HttpHeaders headers = new ServletServerHttpResponse(response).getHeaders();
        Iterator<String> names = response.getHeaderNames().iterator();
        StringBuilder headersString = new StringBuilder();
        while (names.hasNext()) {
            String header = names.next();
            if (!responseHeaderPredicate.test(header)) {
                headers.remove(header);
            } else {
                headersString.append("\t").append(header).append(": ").append(headers.get(header)).append("\n");
            }
        }
        msg.append("\nheaders = {\n").append(headersString.toString()).append("}");

        String payload = getResponseBody(response);
        msg.append("\npayload = ").append(payload);

        msg.append(suffix);
        return msg.toString();
    }

    private String getReuqestBody(ContentCachingRequestWrapper requestWrapper) {
        try {
            return new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper responseWrapper) throws IOException {
        try {
            String s = new String(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());
            responseWrapper.copyBodyToResponse();
            return s;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private ContentCachingRequestWrapper getRequestWrapper(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper)request;
        }
        return new ContentCachingRequestWrapper(request);
    }

    private ContentCachingResponseWrapper getResponseWrapper(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper)response;
        }
        return new ContentCachingResponseWrapper(response);
    }
}
