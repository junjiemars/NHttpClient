package com.xw.http.web;

import com.xw.http.H;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Author: junjie
 * Date: 5/22/15.
 * Target: <>
 */
public class EchoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        final int timeout = H.str_to_int(System.getProperty("echo.timeout"), 1000);
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            _l.error(e);
        }

        final String s = Core.get_post_data(req);
        Core.output_str(resp, s);
    }

    private static final Logger _l = LogManager.getLogger(EchoServlet.class);
}
