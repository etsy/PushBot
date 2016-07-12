grammar Command;

@header {
package com.etsy.pushbot;

import com.etsy.pushbot.command.*;
import java.util.LinkedList;
import org.apache.log4j.Logger;
}

@lexer::header {
package com.etsy.pushbot;
import java.util.LinkedList;
}

@parser::members {
    @Override
    public void reportError(RecognitionException e) {
        logger.error("Error Reported", e);
        return;
    }
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("com.etsy.pushbot.parser");
}

@lexer::members {
    protected void mismatch(IntStream input, int ttype, BitSet follow)
        throws RecognitionException
    {
        throw new MismatchedTokenException(ttype, input);
    }

    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow)
        throws RecognitionException
    {
        throw e;
    }
}

@rulecatch {
    catch (RecognitionException e) {
        throw e;
    }
}

commands returns [ List<TrainCommand> value ]
  : { value = new LinkedList<TrainCommand>(); }
    token=command WHITESPACE? { value.add($token.value); }
   ( COMMAND_DELIMITER
     WHITESPACE?
     token=command { value.add($token.value); } )*
  ;

command returns [ TrainCommand value ]
  : PB join { $value = new JoinCommand(); }
    ( WHITESPACE type { ((JoinCommand)$value).setType($type.text); } )?
    ( WHITESPACE with WHITESPACE w=member { ((JoinCommand)$value).setWith($w.text); } )?
    ( WHITESPACE before WHITESPACE b=member { ((JoinCommand)$value).setBefore($b.text); } )?
    ( WHITESPACE last { ((JoinCommand)$value).setLast(true); } )?
    ( WHITESPACE MESSAGE_STRING { ((JoinCommand)$value).setMessage($MESSAGE_STRING.text); })?
  | PB done
    { $value = new DoneCommand(); }
  | PB 'pop'
    { $value = new PopCommand(); }
  | PB 'hold' { $value = new HoldCommand(); }
    (WHITESPACE MESSAGE_STRING { ((HoldCommand)$value).setMessage($MESSAGE_STRING.text); })?
  | PB 'unhold'
    { $value = new UnholdCommand(); }
  | PB nevermind
    { $value = new NevermindCommand(); }
  | PB 'message' WHITESPACE MESSAGE_STRING
    { $value = new MessageCommand($MESSAGE_STRING.text); }
  | PB 'message' WHITESPACE MESSAGE_CLEAR
    { $value = new MessageCommand($MESSAGE_CLEAR.text); }
  | PB 'at' WHITESPACE state
    { $value = new AtCommand($state.text, null); }
  | PB good
    { $value = new GoodCommand(); }
  | PB 'say' { $value = new SayCommand(); }
    (WHITESPACE MESSAGE_STRING { ((SayCommand)$value).setMessage($MESSAGE_STRING.text); })?
  | PB 'drive'
    { $value = new DriveCommand(); }
  | PB uhoh
    { $value = new UhohCommand(); }
  | PB 'help'
    { $value = new HelpCommand(); }
  | PB 'config'
    { $value = new ConfigCommand(); }
  | PB 'kick' WHITESPACE member
    { $value = new NevermindCommand(); ((NevermindCommand)$value).setMember($member.text); }
  | 'STAGING deployed by '
    {
      $value = new MultiCommand();
      ((MultiCommand)$value).addCommand(new AtCommand("staging"));
      ((MultiCommand)$value).addCommand(new AnnounceCommand("Your code is on staging"));
    }
  | 'web production deploy started by '
    { $value = new AtCommand("prod"); }
  | 'PRODUCTION deployed by '
    {
    $value = new AnnounceCommand("Your code is live. Time to watch graphs: https://app.datadoghq.com/dash/151383/deploy-dashboard");
    }
  ;

member
  : STRING
  ;

with
  : 'with'
  | 'con'
  ;

before
  : 'before'
  | 'antes'
  ;

last
  : 'last'
  | 'ultimo'
  ;

good
  : 'good'
  | 'great'
  | 'in'
  | 'go'
  | 'si'
  | 'bueno'
  | 'listo'
  ;

state
  : 'commit'
  | 'push'
  | 'trunk'
  | 'qa'
  | 'dev'
  | 'princess'
  | 'preprod'
  | 'prod'
  | 'production'
  ;

type
  : 'config'
  | 'askme'
  | 'hold'
  | 'HOLD'
  ;

done
  : 'done'
  | 'finito'
  ;

join
  : 'join'
  | 'unir'
  ;

uhoh
  : 'uhoh'
  | 'notgood'
  | 'not_good'
  | 'bad'
  | 'fml'
  | 'fuck'
  | 'fucked'
  ;

nevermind
  : 'nevermind'
  | 'nm'
  | 'olvidate'
  ;

WHITESPACE
  : (' '|'\t'|'\n'|'\r')*
  ;

STRING
  : ('a'..'z'|'A'..'Z'|'0'..'9'|'_')+
  ;

MESSAGE_STRING
  : '"' .* '"'
  ;

MESSAGE_CLEAR
  : '-'
  ;

MEMBER_DELIMITER
  : ('+'|',')
  ;

QUEUE_DELIMITER
  : ('|')
  ;

COMMAND_DELIMITER
  : ';'
  ;

PB : '.' ;
