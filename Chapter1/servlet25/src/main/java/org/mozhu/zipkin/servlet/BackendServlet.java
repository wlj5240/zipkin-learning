package org.mozhu.zipkin.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class BackendServlet extends HttpServlet {

    private final static Logger LOGGER = LoggerFactory.getLogger(BackendServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("backend receive request");
        String username = req.getHeader("user-name");
        String result;
        if (username != null) {
            result = new Date().toString() + " " + username;
        } else {
            result = new Date().toString();
        }
        PrintWriter writer = resp.getWriter();
        writer.write(result);
        writer.flush();
        writer.close();
    }

}
