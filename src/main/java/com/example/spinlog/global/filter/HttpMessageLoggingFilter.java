package com.example.spinlog.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class HttpMessageLoggingFilter extends OncePerRequestFilter {
    private final String temporaryAuthHeader;

    private final Predicate<String> requestHeaderPredicate;
    private final Predicate<String> responseHeaderPredicate;
    private int maxPayloadLength = 5000;

    public HttpMessageLoggingFilter(String temporaryAuthHeader) {
        this.temporaryAuthHeader = temporaryAuthHeader;
        requestHeaderPredicate = headerName -> {
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
        };
        responseHeaderPredicate = headerName -> {
            List<String> headers = List.of(
                    "content-type",
                    "content-length",
                    "location",
                    "set-cookie"
            );
            return headers.contains(headerName.toLowerCase());
        };
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = getRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);

        String requestMessage = createMessage(requestWrapper, "\n[REQUEST]\n", "\n");
        String responseMessage = createMessage(responseWrapper, "\n[RESPONSE]\n", "\n");
        log.info("{}{}", requestMessage, responseMessage);
    }

    protected String createMessage(ContentCachingRequestWrapper request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append(request.getMethod()).append(' ');
        msg.append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            msg.append("\nsession = ").append(session.getId());
        }
        String user = request.getRemoteUser();
        if (user != null) {
            msg.append("\nuser = ").append(user);
        }

        StringBuilder headersString = new StringBuilder();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String header = names.nextElement();
            if (requestHeaderPredicate.test(header)) {
                headersString.append("\t").append(header).append(": ").append(request.getHeader(header)).append("\n");
            }
        }
        msg.append("\nheaders = {\n").append(headersString.toString()).append("}");

        String payload = getReuqestBody(request);
        if(payload != null && !payload.isEmpty()) {
            msg.append("\npayload = ").append(payload);
        }

        msg.append(suffix);
        return msg.toString();
    }

    private String createMessage(ContentCachingResponseWrapper response, String prefix, String suffix) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append(response.getStatus());

        // already copy response body, because of body related headers
        String payload = getResponseBody(response);

        // {'Content-Type', 'Content-Length', 'Location', 'Set-Cookie'} headers , response body logging
        Iterator<String> names = response.getHeaderNames().iterator();
        StringBuilder headersString = new StringBuilder();
        while (names.hasNext()) {
            String header = names.next();
            if (responseHeaderPredicate.test(header)) {
                headersString.append("\t").append(header).append(": ").append(response.getHeader(header)).append("\n");
            }
        }
        msg.append("\nheaders = {\n").append(headersString.toString()).append("}");

        if(payload != null && !payload.isEmpty()) {
            msg.append("\npayload = ").append(payload);
        }

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
        return new ContentCachingRequestWrapper(request, maxPayloadLength);
    }

    private ContentCachingResponseWrapper getResponseWrapper(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper)response;
        }
        return new ContentCachingResponseWrapper(response);
    }
}
