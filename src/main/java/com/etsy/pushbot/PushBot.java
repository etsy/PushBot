package com.etsy.pushbot;

import com.etsy.pushbot.command.TrainCommand;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import com.etsy.pushbot.config.ConfigServer;
import com.etsy.pushbot.config.Status;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jibble.pircbot.PircBot;

/**
 * This is the main entry-point for PushBot
 */
public class PushBot extends PircBot
{
  HashMap<String,ChannelInfo> channelInfoMap =
    new HashMap<String,ChannelInfo>();

  private GraphiteLogger graphiteLogger = null;

  private final String primaryChannel;

  private List<String> channels = new LinkedList<String>();

  /**
   * @param name
   * The nick of this bot
   *
   * @param channels
   * A list of channel names (each prefixed with '#')
   *
   * @param ircHost
   * The hostname of the iRCd server to connect to
   *
   * @param ircPort
   * The port that IRCd is listening on
   *
   * @param ircPassword
   * If non-null, the given password will be used when connecting to
   * IRCd
   *
   * @param graphiteEnabled
   * If true, stats will be logged to a graphite server
   *
   * @param graphiteHost
   * An optional hostname where stats will be logged (if enabled)
   *
   * @param graphitePort
   * An optional port that graphite is listening on
   */
  public PushBot(String name, List<String> channels,
      boolean graphiteEnabled, String graphiteHost, int graphitePort) {
    super();

    // Configure pushbot's identity
    setName(name);
    setFinger(name);
    setLogin(name);
    setVerbose(true);

    // Configure pushbot's channels
    this.channels = channels;
    this.primaryChannel = channels.get(0);

    // Configure graphite logging
    if(graphiteEnabled) {
      graphiteLogger = new GraphiteLogger(graphiteHost, graphitePort);
    }
  }


  @Override
  protected void onConnect() {
    for(String channel : channels) {
      joinChannel(channel);
    }
  }

  @Override
  protected void onJoin(String channel, String sender, String login, String hostname) {
  }

  @Override
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

  @Override
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

   if(graphiteLogger != null) {
    graphiteLogger.logToGraphite(channel+".queueSize",
            newPushTrain.size());

    graphiteLogger.logToGraphite(channel+".members",
            newPushTrain.getMemberCount());
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

  @Override
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
      sendMessage(channel, "Sorry, I don't understand the current topic");
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

    log(pushTrain.toString());

    if(!pushTrain.toString().equals(topic)) {
      setTopic(channel, pushTrain.toString());
    }
  }

  @Override
  protected void onPrivateMessage(String sender, String login, String hostname, String message) {
    onMessage(this.primaryChannel, sender, login, hostname, message);
  }

  @Override
  protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHost, String channel) {
      joinChannel(channel);
  }

  public static void main(String[] args)
    throws Exception
  {
    Options options = new Options();
    Option option;

    option = new Option("n", "name",  true, "Name");
    option.setRequired(true);
    options.addOption(option);

    option = new Option("c", "channels",  true, "A comma delimited set of channels to join");
    option.setRequired(true);
    options.addOption(option);

    option = new Option("h", "irc-host",  true, "The IRCD host");
    option.setRequired(true);
    options.addOption(option);

    option = new Option("p", "irc-port",  true, "The IRCD port");
    option.setRequired(true);
    options.addOption(option);

    option = new Option("a", "irc-pass",  true, "IRCd Server password");
    option.setRequired(false);
    options.addOption(option);

    option = new Option("g", "graphite-enabled",  false, "Set to true to log stats to graphite");
    options.addOption(option);

    option = new Option("r", "graphite-host",  true, "Graphite server hostname");
    options.addOption(option);

    option = new Option("t", "graphite-port",  true, "Graphite server port");
    options.addOption(option);

    CommandLineParser parser = new PosixParser();
    CommandLine commandLine = null;
    try {
      commandLine = parser.parse(options, args);
    }
    catch(ParseException exception) {
      System.err.println(exception.getMessage());
      System.err.println("Usage: " + PushBot.class + " ARGS");
      System.err.println("  -n,--name                IRC Nick of the Bot");
      System.err.println("  -c,--channels            Comma delimited list of channels to join");
      System.err.println("  -h,--irc-host            IRCD hostname");
      System.err.println("  -p,--irc-port            IRCD port");
      System.err.println("  -a,--irc-passwod         Optional IRCD server password");
      System.err.println("  -g,--graphite-enabled    Enable graphite logging");
      System.err.println("  -r,--graphite-host       Graphite hostname");
      System.err.println("  -t,--graphite-port       Graphite port");
      System.exit(1);
    }

    List<String> channels = new LinkedList<String>();
    for(String channel : commandLine.getOptionValue('c').split("/\\s*,\\s*/")) {
      channels.add(channel);
    }

    // Build PushBot
    PushBot pushBot =
      new PushBot(commandLine.getOptionValue('n'),
          channels,
          commandLine.hasOption('g'),
          commandLine.getOptionValue('r', null),
          Integer.valueOf(commandLine.getOptionValue('t', "2003")));

    // Connect to IRCD
    pushBot.connect(
        commandLine.getOptionValue('h'),
        Integer.valueOf(commandLine.getOptionValue('p')),
        commandLine.getOptionValue('a', null));

    // Launch the web interface
    ConfigServer configServer = new ConfigServer(pushBot);
  }
}
