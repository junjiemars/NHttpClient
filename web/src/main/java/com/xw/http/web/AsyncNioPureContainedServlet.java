package com.xw.http.web;

import com.xw.http.H;
import com.xw.http.NioHttpClient;
import com.xw.http.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by junjie on 8/25/15.
 */
public final class AsyncNioPureContainedServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);

        final AsyncContext ctx = req.startAsync(req, resp);
        ctx.setTimeout(C.http_nio_timeout());

        final String uri = C.http_url();
        if (H.is_null_or_empty(uri)) {
            _l.info("#%s:<ENV:http.url> is null/empty", AsyncNioServlet.class.getSimpleName());
            return;
        }

        try {
            final NioHttpClient n = new NioHttpClient()
                    .to(uri)
                    .post(C.get_post_data(req))
                    .onReceive(new Receiver() {
                        @Override
                        public void onReceive(final String s) {
//                            _l.debug(s);

                            ctx.start(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ctx.getResponse().getWriter().write(s);
                                    } catch (Exception e) {
                                        _l.error(e.getMessage(), e);
                                    } finally {
                                        ctx.complete();
                                    }
                                }
                            });
                        }
                    });

        } catch (Exception e) {
            _l.error(e.getMessage(), e);
        }

    }

    private static final Logger _l = LoggerFactory.getLogger(AsyncNioPureContainedServlet.class);
}
