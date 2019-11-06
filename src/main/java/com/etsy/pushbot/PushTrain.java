package com.etsy.pushbot;

import com.etsy.pushbot.tokens.Hold;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;
import java.util.LinkedList;
import com.google.common.base.Joiner;
import com.etsy.ChannelNotFoundException;

public class PushTrain extends LinkedList<PushToken>
{
  private String message;

  public PushTrain() {
    super();
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getHeadMember() {
    if(size() > 0 && get(0) instanceof MemberList) {
      MemberList memberList = (MemberList)get(0);
      String memberListString = "";
      for(Member member : memberList) {
        memberListString += "<@" + member.getName() + ">" + " ";
      }
      return memberListString;
    }
    return "clear";
  }

  public int getMemberCount() {
    int memberCount = 0;
    for(PushToken token : this) {
        if(!(token instanceof MemberList)) {
            continue;
        }
        for(Member member : (MemberList)token) {
            ++memberCount;
        }
    }
    return memberCount;
  }

  public Member getDriver() {
    PushToken head = get(0);
    if(head instanceof MemberList) {
        return ((MemberList)head).getDriver();
    }
    return null;
  }

  /**
   * @return
   * True if the head of the queue is a
   * member list and everyone in it is
   * ready to continue on
   */
  public Boolean isHeadReady() {
    PushToken head = get(0);
    if(head instanceof MemberList) {
        return ((MemberList)head).isEveryoneReady();
    }
    return false;
  }

  /**
   * @return
   * The state of the head (princess, prod, etc.) if
   * the head is a member list, else null
   */
  public String getHeadState() {
    PushToken head = get(0);
    if(head instanceof MemberList) {
      return ((MemberList)head).getState();
    }
    return null;
  }

  public Boolean isQuietPush() {
    PushToken head = get(0);
    if(head instanceof MemberList) {
        Member driver = ((MemberList)head).getDriver();
        return driver.getConfig().isQuietDrive();
    }
    return false;
  }

  public Boolean isHold() {
    PushToken head = get(0);
    return (head instanceof Hold);
  }

  public void onNewHead(PushBot pushBot, String channel, String sender) {
      PushToken head = get(0);
      if(head != null && head instanceof MemberList) {
          try {
              pushBot.sendMessage(channel, getHeadMember() + ": You're up");
          }
          catch (ChannelNotFoundException cne_exception) {
              System.err.println(cne_exception.getMessage());
          }

          if(isQuietPush()) {
              try {
                  pushBot.sendMessage(channel, getDriver().getName() + " has asked me to be quiet for this push");
              }
              catch (ChannelNotFoundException cne_exception){
                  System.err.println(cne_exception.getMessage());
              }
          }

          for(Member member : ((MemberList)head)) {
              member.onHeadOfQueue(pushBot, this, channel);
          }
      }
  }

  @Override
  public String toString() {
    String s = "";
    if(size() < 1) {
      s = "clear";
    }
    else {
      s = Joiner.on(" | ").join(this);
    }
    if(this.message != null) {
      s += " !! " + this.message;
    }
    return s;
  }
}
