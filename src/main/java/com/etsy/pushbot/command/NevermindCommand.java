package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;

public class NevermindCommand
  extends TrainCommand {

  private String member = null;

  public NevermindCommand() {}

  public void setMember(String member) {
    this.member = member;
  }

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
   if(this.member == null) {
     this.member = sender;
   }

    PushTrain tokenToRemove = new PushTrain();
    for(PushToken token : pushTrain) {
      if(token != null && token instanceof MemberList) {
        MemberList toRemove = new MemberList();
        for(Member member : (MemberList)token) {
          if(member.getName().equals(this.member)) {
            toRemove.add(member);
          }
        }
        for(Member member : toRemove) {
          ((MemberList)token).remove(member);
        }
        if(((MemberList)token).size() < 1) {
          tokenToRemove.add(token);
        }
      }
    }
    for(PushToken token : tokenToRemove) {
      pushTrain.remove(token);
    }
  }

  @Override
  public String toString() {
    String s = "unshift";
    return s;
  }
}
