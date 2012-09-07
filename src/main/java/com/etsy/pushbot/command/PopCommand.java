package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;

public class PopCommand
  extends TrainCommand {

  private String member = null;

  public PopCommand() {}

  public void setMember(String member) {
    this.member = member;
  }

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender
  ) {

    if(this.member == null) {
      this.member = sender;
    }

    Member targetMember = null;
    PushToken targetToken = null;
    for(PushToken token : pushTrain) {
      if(token != null && token instanceof MemberList) {
        for(Member member : (MemberList)token) {
          if(member.getName().equals(this.member)) {
            // Maintain ref to last instance of member in train
            targetMember = member;
            targetToken = token;
          }
        }
      }
    }

    if (targetMember != null) {
      ((MemberList)targetToken).remove(targetMember);
      if (((MemberList)targetToken).size() < 1) {
        pushTrain.remove(targetToken);
      }
    }

  }

  @Override
  public String toString() {
    String s = "pop";
    return s;
  }
}
