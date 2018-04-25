package com.etsy.pushbot;

import com.etsy.pushbot.command.TrainCommand;
import com.etsy.pushbot.config.ConfigServer;
import com.etsy.pushbot.config.Status;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import com.etsy.PlackBot;
import com.etsy.ChannelNotFoundException;

/**
 * This is the main entry-point for PushBot
 */
public class PushBot extends PlackBot
{
  HashMap<String,ChannelInfo> channelInfoMap =
    new HashMap<String,ChannelInfo>();

  /**
   * @param apiToken 
   * The api token for this bot
   */
  public PushBot(String apiToken) {
    super(apiToken);
  }

  protected void onConnect() {
    System.out.println("Connected");
  }

  protected void onJoin(String channel, String sender, String login, String hostname) {
    System.out.println("Joined #" + channel + " from @" + sender);
  }

  protected void onDisconnect() {
    try {
      reconnect();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   */
  public Status getStatus(String channel)
    throws Exception {

    Status status = new Status();

    String topic = getTopic(channel);
    PushTrain pushTrain = PushTrainReader.parse(topic);

    status.isHold = pushTrain.isHold();
    if(!status.isHold) {
      status.driver = pushTrain.getDriver().toString();
      status.head = pushTrain.getHeadMember().trim();
    }
    status.memberCount = pushTrain.getMemberCount();

    status.isEveryoneReady = pushTrain.isHeadReady();

    status.headState = pushTrain.getHeadState();

    return status;
  }

  protected void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
    ChannelInfo channelInfo = channelInfoMap.get(channel);
    if(channelInfo == null) {
      channelInfoMap.put(channel, new ChannelInfo(channel));
      channelInfo = channelInfoMap.get(channel);
    }

    PushTrain previousPushTrain = channelInfo.getPushTrain();

    PushTrain newPushTrain;
    try {
        newPushTrain = PushTrainReader.parse(topic);
        if(newPushTrain == null) {
            channelInfo.setHasBadTopic(true);
            sendMessage(channel, "Sorry, I don't understand the current topic");
            sendMessage(channel, "I won't accept new commands until the topic is fixed.");
            return;
        }
        channelInfo.setHasBadTopic(false);
    } catch(Throwable t) {
        t.printStackTrace();
        return;
    }

    if(previousPushTrain != null && newPushTrain != null
        && !newPushTrain.getHeadMember()
              .equals(previousPushTrain.getHeadMember())
        && !newPushTrain.getHeadMember().equals("clear")) {

        newPushTrain.onNewHead(this, channel, setBy);
   }

   channelInfo.setTopic(topic);
 }

  protected String getTopic(String channel) throws RuntimeException {
    if(channelInfoMap.get(channel) == null) {
      return null;
    }
    ChannelInfo channelInfo = channelInfoMap.get(channel);
    if(channelInfo.getHasBadTopic()) {
      throw new RuntimeException("Bad Topic");
    }
    return channelInfo.getTopic();
  }

  protected synchronized void onMessage(String channel, String sender, String login, String hostname, String message) {

    List<TrainCommand> trainCommands;
    try {
        trainCommands = CommandReader.parse(message);
        if(trainCommands == null || trainCommands.size() == 0) {
            return;
        }
    } catch(Throwable t) {
      t.printStackTrace();
      return;
    }

    String topic = null;
    try {
      topic = getTopic(channel);
    }
    catch(Exception exception) {
      try {
          sendMessage(channel, "Sorry, I don't understand the current topic");
      }
      catch (ChannelNotFoundException cne_exception) {
          System.err.println(cne_exception.getMessage());
      }
      return;
    }

    PushTrain pushTrain;
    try {
        pushTrain = PushTrainReader.parse(topic);
        if(pushTrain == null) {
            sendMessage(channel, "Sorry, I don't understand the current topic");
            return;
        }
    } catch(Throwable t) {
        t.printStackTrace();
        return;
    }

    for(TrainCommand trainCommand : trainCommands) {
      trainCommand.onCommand(this, pushTrain, channel, sender);
    }


    if(!pushTrain.toString().equals(topic)) {
      setTopic(channel, pushTrain.toString());
    }
  }

  protected void onPrivateMessage(String sender, String login, String hostname, String message) {
    System.out.println("Got direct message from @" + sender + ": " + message);
  }

  public static void main(String[] args)
    throws Exception
  {
    Options options = new Options();
    Option option;

    option = new Option("k", "key",  true, "Key");
    option.setRequired(true);
    options.addOption(option);

    CommandLineParser parser = new PosixParser();
    CommandLine commandLine = null;
    try {
      commandLine = parser.parse(options, args);
    }
    catch(ParseException exception) {
      System.err.println(exception.getMessage());
      System.err.println("Usage: " + PushBot.class + " ARGS");
      System.err.println("  -k,--key                   Bot API Token");
      System.exit(1);
    }

    String apiToken = null;

    if (commandLine.hasOption('k')) {
      apiToken = commandLine.getOptionValue('k');
    }

    // Build PushBotSlack
    PushBot pushBot =
      new PushBot(apiToken);

    pushBot.connect();

    // Launch the web interface
    ConfigServer configServer = new ConfigServer(pushBot);
  }
}
