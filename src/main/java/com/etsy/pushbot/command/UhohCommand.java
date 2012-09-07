package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;

public class UhohCommand
  extends TrainCommand {

  public UhohCommand() {}

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    for(PushToken token : pushTrain) {
        if(token == null || !(token instanceof MemberList)) {
            continue;
        }
        for(Member member : ((MemberList)token)) {
            if(sender.equals(member.getName())) {
                member.setStatus("!");
                return;
            }
        }
     }
  }

  @Override
  public String toString() {
    return "uhoh";
  }
}
