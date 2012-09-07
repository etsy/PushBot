package com.etsy.pushbot.config;

import com.etsy.pushbot.PushBot;
import com.etsy.pushbot.config.Response;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 *
 */
public class StatusServlet extends HttpServlet {
  private PushBot pushbot;

  public StatusServlet(PushBot pushbot) {
    this.pushbot = pushbot;
  }

  /**
   *
   */
  @Override
  protected void doGet(HttpServletRequest request,
                       HttpServletResponse response)
    throws ServletException, IOException {

    String channel = "#" + request.getParameter("channel");
    String callback = request.getParameter("callback");

    response.setContentType("text/javascript");
    response.setHeader("Cache-Control", "no-cache");
    PrintWriter writer = response.getWriter();

    try {
      writer.print(callback + "("
          + new Response<Status>(this.pushbot.getStatus(channel)).toString()
          + ")");
    }
    catch(Exception exception) {
      writer.print(callback + "("
          + new Response<String>(1, exception.getMessage()).toString()
          + ")" );
      System.err.println(exception.getMessage());
      exception.printStackTrace();
    }
    writer.flush();
  }

}
