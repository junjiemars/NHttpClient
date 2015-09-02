package com.xw.http.web.spring;

import com.xw.http.H;
import com.xw.http.NioHttpClient;
import com.xw.http.Receiver;
import com.xw.http.web.A;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by junjie on 9/2/15.
 */
@Controller
public class AsyncPooledSpringController {

    @RequestMapping(value = "pooled", method = RequestMethod.POST,
            headers = "Accept=*/*",
            produces = "text/plain")
    public
    @ResponseBody
    DeferredResult<String> async() {

        final DeferredResult<String> result = new DeferredResult<String>(A.http_nio_timeout());

        // nio http client call
        final String uri = A.http_url();
        if (H.is_null_or_empty(uri)) {
            _l.info("#%s:<ENV:http.url> is null/empty", AsyncSpringController.class.getSimpleName());
            result.setErrorResult(String.format("#invalid url:%s", uri));
            return result;
        }

        try {
            final NioHttpClient n = new NioHttpClient()
                    .to(uri)
                    .get()
                    .headers(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                    .headers(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                    .onReceive(new Receiver<String>() {
                        @Override
                        public void onReceive(final String s) {
//                            _l.debug(s);
                            _pooled.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        result.setResult(s);
                                    } catch (Exception e) {
                                        _l.error(e.getMessage(), e);
                                    } finally {
                                        _l.info("#DeferredResult<String> completed");
                                    }
                                }
                            });
                        }
                    });

        } catch (Exception e) {
            _l.error(e.getMessage(), e);
        }

        return result;
    }


    private static final ExecutorService _pooled = Executors.newWorkStealingPool();

    private static final Logger _l = LoggerFactory.getLogger(AsyncPooledSpringController.class);
}
