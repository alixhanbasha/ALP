/*
* TokenType class for ALPlang tokens
* created ( Sat Aug 8 2020 )
* last edited ( Sat Aug 9 2020 )
* author Alixhan Basha
*/

enum TokenType {
    /* Essential tokens */
    LEFT_PAREN , RIGHT_PAREN , LEFT_BRACE , RIGHT_BRACE ,
    COMMA , DOT , MINUS , PLUS , STAR , SLASH , SEMICOLON ,
    DOLAR , BANG/* ! */ , EQ , GT , LT , NOTEQ , EQEQ , GTEQ , LTEQ , MODULO ,

    /* Literals */
    IDENTIFIER , TEXT , NUMBER ,

    /* Keywords */
    DHE , KLASA , PERNDRYSHE , FALSE , FUNKSIONI , NESE , ZBRAZET , OSE , IMPORTO , TJETER /* else if */ ,
    SHKRUAJ , KTHEN , SUPER , AKTUAL /* this */ , VERTET , VAR , PERDERISA , PER , TRASHEGON ,
    SHKRUAJR , LEXO , LEXOF , NET , /* TODO :: add more keywors ! */
    EOF
}
