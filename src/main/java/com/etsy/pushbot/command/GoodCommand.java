package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;
import com.etsy.ChannelNotFoundException;

public class GoodCommand
  extends TrainCommand {

  public GoodCommand() {}

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {

    if(pushTrain.isQuietPush()) {
        return;
    }

    for(PushToken token : pushTrain) {
        if(token == null || !(token instanceof MemberList)) {
            continue;
        }
        for(Member member : ((MemberList)token)) {
            if(sender.equals(member.getName())) {
                member.setStatus("*");
                if(((MemberList)token).isEveryoneReady()) {
                    try {
                        pushBot.sendMessage(channel, pushTrain.getHeadMember() + ": everyone is ready");
                    }
                    catch (ChannelNotFoundException cne_exception){
                        System.err.println(cne_exception.getMessage());
                    }
                }
                return;
            }
        }
     }
  }

  public boolean isEveryoneReady(MemberList memberList) {
    for(Member member : memberList) {
        if(!"*".equals(member.getStatus()) && !member.isDark()) {
            return false;
        }
    }
    return true;
  }

  @Override
  public String toString() {
    return "uhoh";
  }
}
