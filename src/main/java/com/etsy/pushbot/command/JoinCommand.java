package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;

public class JoinCommand
    extends TrainCommand {

    private String type = null;
    private String with = null;
    private String before = null;
    private boolean last = false;
    private String message = null;

    public JoinCommand() {}

    public void setType(String type) {
      if("hold".equals(type)) {
        type = "HOLD";
      }
      this.type = type;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public void setWith(String member) {
        this.with = member;
    }

    public void setBefore(String member) {
        this.before = member;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public void onCommand(PushBot pushBot,
                          PushTrain pushTrain,
                          String channel,
                          String sender) {

        Member member = new Member(sender);
        member.setType(this.type);

        if(this.with != null) {
            this.joinWith(pushTrain, member);
            return;
        }

        if(this.before != null) {
            this.joinBefore(pushTrain, member);
            return;
        }

        if(this.last || (this.type != null && !member.isDark())) {
            this.joinLast(pushTrain, member);
            return;
        }

        this.joinWhereConvenient(pushTrain, member);
    }

    protected void joinLast(PushTrain pushTrain,
                            Member member) {
        MemberList memberList = new MemberList();
        this.joinMemberList(memberList, member);
        pushTrain.add(memberList);
        return;
    }

    protected void joinWith(PushTrain pushTrain,
                            Member member) {
        if(this.with == null) {
            return;
        }
        for(PushToken token : pushTrain) {
            if(token == null || !(token instanceof MemberList)) {
                continue;
            }
            for(Member queuedMember : (MemberList)token) {
                if(this.with.toLowerCase().equals(queuedMember.getName().toLowerCase())) {
                  this.joinMemberList((MemberList)token, member);
                  return;
                }
            }
        }
    }

    protected void joinBefore(PushTrain pushTrain,
                              Member member) {
        int i = -1;
        for(PushToken token : pushTrain) {
            ++i;
            if(token == null || !(token instanceof MemberList)) {
                continue;
            }
            for(Member queuedMember : (MemberList)token) {
                if(!queuedMember.getName().equals(this.before)) {
                    continue;
                }
                MemberList memberList = new MemberList();
                this.joinMemberList(memberList, member);
                pushTrain.add(i, memberList);
                return;
            }
        }
    }

    protected void joinWhereConvenient(PushTrain pushTrain,
                                       Member member) {
        boolean first = true;
        for(PushToken token : pushTrain) {
            if(!first && token != null && token instanceof MemberList) {
                boolean isOpen = true;
                for(Member queuedMember : (MemberList)token) {
                    if(queuedMember.isConfig() || (member.getType() == null && queuedMember.getType() != null && !queuedMember.isDark())) {
                        isOpen = false;
                        break;
                    }
                }
                if(((MemberList)token).size() < 5 && isOpen) {
                    this.joinMemberList((MemberList)token, member);
                    return;
                }
            }
            if(token instanceof MemberList) {
                first = false;
            }
        }

        this.joinLast(pushTrain, member);
    }

    /**
     * Add a member to a member list and possibly
     * set a message on that memberlist
     */
    private void joinMemberList(MemberList memberList, Member member) {
      memberList.add(member);

      if(this.message != null) {
        memberList.setMessage(this.message);
      }
    }

    @Override
    public String toString() {
      String s = "join";
      if(type != null) {
        s += " " + type;
      }
      if(with != null) {
        s += " with " + with;
      }

      if(message != null) {
        s += " \"" + message + "\"";
      }

      return s;
    }
}
