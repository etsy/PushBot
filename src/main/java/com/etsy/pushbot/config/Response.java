package com.etsy.pushbot.config;

import java.io.IOException;
import java.util.Collection;
import org.codehaus.jackson.map.ObjectMapper;

public class Response<T>
{
  private static final ObjectMapper mapper 
    = new ObjectMapper();

  private int errorId = 0;
  private String errorMessage = null;
  private T response = null;

  public Response(T response)
  {
    this.response = response;
  }

  public Response(int errorId, String errorMessage)
  {
    this.errorId = errorId;
    this.errorMessage = errorMessage;
  }

  public Response(T response, 
                  int errorId, String errorMessage)
  {
    this.errorId = errorId;
    this.errorMessage = errorMessage;
    this.response = response;
  }

  public int getErrorId() { return errorId; }
  public String getErrorMessage() { return errorMessage; }
  public T getResponse() { return response; }

  public int getCount() {
    if(response instanceof Collection)
      return ((Collection)response).size();
    if(response == null)
      return 0;
    return 1;
  }

  public String toString()
  {
    try {
      return mapper.writeValueAsString(this);
    } catch (IOException e) {
      System.err.println("IO errors are not possible here");
      System.err.println(e.getMessage());
    }

    return "";
  }
}
