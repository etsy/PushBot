PushBot is an IRC bot that manages the topic in an IRC channel that has a push train.

USAGE
=====

    .join                      -- Join the queue for a normal push wherever its convenient
    .join config               -- Join the queue for a config push
    .join HOLD                 -- A HOLD that is queued and named
    .join HOLD "message"       -- You can set a message when you join
    .join askme                -- Join the queue and suggest that people ask you before joining along
    .join before USER          -- Join before the given user
    .join with USER            -- Join the queue at the first position with [username]
    .join config with USER     -- You can string join commands together
    .join last                 -- Join at the end of the queue
    .at {commit,dev,...}       -- Set the state of your push to being at the given value
    .in                        -- Mark yourself as having your code checked in
    .good                      -- Mark yourself as all-good in the current push state
    .uhoh                      -- Mark yourself as not-all-good in the current push state
    .done                      -- Mark the head of the push queue as done
    .nevermind                 -- Hop out of the queue
    .pop                       -- Remove your last entry in the queue
    .hold "message"            -- Set a hold with a message. Don't forget the quotes.
    .unhold                    -- Release the hold
    .message "message"         -- Set a message. Don't forget the quotes.
    .message -                 -- Remove the message
    .kick username             -- Punt someone from the queue
    .drive                     -- Make yourself the leader of the first push group you're in
    .config                    -- Get a link to the PushBot settings page
    .help                      -- Show Help Information

Join #pushbot to play around with pushbot and see how it works.

Examples
========

Let's say you're in an IRC channel named #push and it has the initial topic "clear". PushBot
can help organize a push queue.

    TOPIC: clear

    asm> .join
    gio> .join with asm
    pushbot> asm, gio: You're up

    TOPIC: asm + gio

    gio> .good
    asm> .good

    TOPIC: asm* + gio*

    pushbot> gio, asm: Everyone is ready

    adam> .join

    TOPIC: asm* + gio* | adam

    asm> .at preprod

    TOPIC: <preprod> asm + gio | adam

    asm> .good
    gio> .good
    pushbot> asm, gio: Everyone is ready

    asm> .at prod
    asm> .good
    gio> .good
    pushbot> asm, gio: Everyone is ready
    asm> .done

    TOPIC adam

    pushbot> adam: You're up


Configuring PushBot For Your Handle
===================================

You can modify a few settings within PushBot with respect to your IRC handle.

* You can tell PushBot to try to be quiet when you're driving
* You can have PushBot send you Notifo notifications when you're at the head of the queue

To configure PushBot, head to http://[pushbot-hostname]:8080/

Tricks For Suppressing Pushbot Topic Change Spam
================================================

### Colloquy

Edit the CSS for your chosen style. If you're suing "DecafBland - Inverted", for instance, you should open the file

    /Applications/Colloquy.app/Contents/Resources/Styles/DecafBland.colloquyStyle/Contents/Resources/Variants/Inverted.css

and add the line

    .event { display: none; }

That'll get rid of all event messages (joins, parts, topic changes), and could be too much, so you might want to make a variant of your style just for #push.

### Limechat

Edit the CSS for your chosen style, for instance

    /Applications/LimeChat.app/Contents/Themes/Limelight.css

and add the lines

    html[channelname="#push"] div[type=topic] {
        position: fixed;
        top: 0;
        left: 0;
        padding-left: 0 !important;
        background: #000;
        width: 100%;
    }

That'll move all topics in the #push channel to a line on the top of the channel, with new topics covering up old topics.

### IRSSI

    /ignore -channels #push * TOPICS

### WeeChat

    /filter add hush_pushbot irc.host.#push irc_topic pushbot

### Other Clients

If you have instructions for other clients, send them to me and I'll add them.


Hacking
=======

* The bot and its config lives in [src/main/java/com/etsy/pushbot/PushBot.java](https://github.com/Etsy/PushBot/blob/master/src/main/java/com/etsy/pushbot/PushBot.java "PushBot.java")
* The topic grammar lives in [src/main/antlr3/com/etsy/pushbot/PushTrain.g](https://github.com/Etsy/PushBot/blob/master/src/main/antlr3/com/etsy/pushbot/PushTrain.g "PushTrain.g")
* The command grammar lives in [src/main/antlr3/com/etsy/pushbot/Command.g](https://github.com/Etsy/PushBot/blob/master/src/main/antlr3/com/etsy/pushbot/Command.g "Command.g")

To build and run PushBot, run

    > cd PushBot
    > mvn test
    > mvn package
    > java -jar target/PushBot.jar --name pushbot --channels "#push,#pushbot" --irc-host "irc.network.net" --irc-port 6667 --irc-pass "password"
