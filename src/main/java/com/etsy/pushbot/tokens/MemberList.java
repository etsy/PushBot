package com.etsy.pushbot.tokens;

import com.etsy.pushbot.*;
import java.util.LinkedList;
import com.google.common.base.Joiner;

public class MemberList extends LinkedList<Member> implements PushToken {

  String state = null;
  String message = null;

  public MemberList() {
    super();
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getState() {
    return this.state;
  }

  public Member getDriver() {
    return get(0);
  }

  public String getMemberList() {
      String members = "";
      for(Member member : this) {
          members += member.getName() + " ";
      }
      return members;
  }

  /**
   * @return
   * True if everyone is ready, else false
   */
  public boolean isEveryoneReady() {
    for(Member member : this) {
        if(!"*".equals(member.getStatus()) && !member.isDark()) {
            return false;
        }
    }
    return true;
  }

  @Override
  public String toString() {
    String s = "";

    if(this.state != null) {
      s += "<" + this.state + "> ";
    }

    s += Joiner.on(" + ").join(this);

    if(this.message != null) {
      s += " " + this.message;
    }

    return s;
  }
}
