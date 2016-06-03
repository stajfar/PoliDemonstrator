package it.polimi.polidemonstrator.businessLogic;

/**
 * Created by saeed on 6/1/2016.
 */
public class StateMachine {

    enum State { //the first one shows floor and the second one show room presence
        FF ,
        TT ,
        TF ;
        static public final Integer length = 1 + TF.ordinal();


    }

    enum Symbols{
        Elv_in,
        Elv_out,
        Rm_in,
        Rm_out;
        static public final Integer length = 1 + Rm_out.ordinal();
    }

    State transition[][] ={
        //      Elv_out    Elv_in      Room_in    Room_out
       /*FF*/ {State.FF,  State.FF,   State.TT,    State.FF },
       /*TT*/ {State.FF,  State.FF,   State.TT,    State.TF },
       /*TF*/ {State.FF,  State.FF,   State.TT,    State.TF }

    };



}