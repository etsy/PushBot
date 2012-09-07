grammar PushTrain;

@header {
package com.etsy.pushbot;

import com.etsy.pushbot.tokens.*;
import java.util.LinkedList;
}

@lexer::header {
    package com.etsy.pushbot;
}

@parser::members {
    @Override
    public void reportError(RecognitionException e) {
        return;
    }
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


push_train returns [ PushTrain pushTrain ]
  : { pushTrain = new PushTrain(); }
    token=push_token { pushTrain.add($token.value); }
   ( WHITESPACE?
      QUEUE_DELIMITER
      WHITESPACE?
      token=push_token { pushTrain.add($token.value); }
    )*
    ( message_string { pushTrain.setMessage($message_string.value); } )?
  | clear? { pushTrain = new PushTrain(); }
    ( message_string { pushTrain.setMessage($message_string.value); } )?
  | WHITESPACE? { pushTrain = new PushTrain(); }
  ;

push_token returns [ PushToken value ]
  : hold { $value = $hold.value; }
  | member_list { $value = $member_list.value; }
  ;

hold returns [ Hold value ]
  : 'HOLD' { $value = new Hold(); }
    ( WHITESPACE? MESSAGE { $value.setMessage($MESSAGE.text); })?
  ;

member_list returns [ MemberList value ]
  : { $value = new MemberList(); }
    ( '<' STRING '>' { $value.setState($STRING.text); } WHITESPACE )?
    m=member { $value.add($m.value); }
    ( WHITESPACE?
      MEMBER_DELIMITER
      WHITESPACE?
      m=member { $value.add($m.value); }
    )*
    ( WHITESPACE MESSAGE { $value.setMessage($MESSAGE.text); })?
  ;

member returns [ Member value ]
  : STRING { $value = new Member($STRING.text); }
    ( status { $value.setStatus($status.text); } )?
    ( WHITESPACE? '(' type ')'  { $value.setType($type.text); } )?
  ;

message_string returns [ String value ]
  :   WHITESPACE
      MESSAGE_DELIMITER
      WHITESPACE
      MESSAGE
      { $value = $MESSAGE.text; }
  ;

clear
  : 'clear'
  ;

status
  : '*'
  | '!'
  | '~'
  ;

type
  : 'config'
  | 'askme'
  | 'hold'
  | 'HOLD'
  ;

WHITESPACE
  : (' '|'\t'|'\n'|'\r')*
  ;

STRING
  : ('a'..'z'|'A'..'Z'|'0'..'9'|'_')+
  ;

MEMBER_DELIMITER
  : ('+'|','|'&')
  ;

QUEUE_DELIMITER
  : '|'
  ;

MESSAGE_DELIMITER
  : '!!'
  ;

MESSAGE
  : '"' .* '"'
  ;


