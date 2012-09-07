package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;
import java.util.*;

public class DriveCommand
  extends TrainCommand {

  public DriveCommand() {}

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {

    PushTrain tokenToDrive = new PushTrain();
    Map<PushToken, Member> tokenMember = new HashMap<PushToken, Member>();
    outer: for(PushToken token : pushTrain) {
      if(token != null && token instanceof MemberList) {
        for(Member member : (MemberList)token) {
          if(member.getName().equals(sender)) {
            tokenMember.put(token, member);
            break outer;
          }
        }
      }
    }
    for (Map.Entry<PushToken, Member> e : tokenMember.entrySet()) {
      PushToken token = e.getKey();
      Member member = e.getValue();
      ((MemberList)token).remove(member);
      ((MemberList)token).addFirst(member);
    }
  }

  @Override
  public String toString() {
    String s = "drive";
    return s;
  }
}
