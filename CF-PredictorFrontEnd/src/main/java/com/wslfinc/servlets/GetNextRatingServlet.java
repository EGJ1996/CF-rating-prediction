package com.wslfinc.servlets;

import com.wslfinc.cf.RatingGetter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Wsl_F
 */
public class GetNextRatingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Request received!");
        System.out.println("Parameter: " + request.getParameter("contestId"));
        String json;
        try {
            int contestId = Integer.valueOf(request.getParameter("contestId"));
            json = RatingGetter.getNewRatingString(contestId);
        } catch (NumberFormatException ex) {
            json = "{ \"status\": \"FAIL\" }";
        }

        try {
            PrintWriter out = response.getWriter();
            out.write(json);
            out.flush();
            response.setStatus(200);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

}
