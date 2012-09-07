package com.etsy.pushbot.tokens;

import com.etsy.pushbot.*;

public class Hold implements PushToken {

  private String message;

  public Hold() {}

  public void setMessage(String message) {
    if("<missing MESSAGE>".equals(message)) {
        return;
    }
    this.message = message;
  }

  @Override
  public String toString() {
    String s ="HOLD";
    if(message != null) {
      s += " " + message;
    }
    return s;
  }
}
