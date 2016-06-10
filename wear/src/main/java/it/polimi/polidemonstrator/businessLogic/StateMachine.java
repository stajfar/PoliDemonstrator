package it.polimi.polidemonstrator.businessLogic;

/**
 * Created by saeed on 6/1/2016.
 */
public class StateMachine {

   public enum State { //the first one shows floor and the second one show room presence
        FF ,
        TT ,
        TF ;
        static public final Integer length = 1 + TF.ordinal();


    }

   public enum Symbols{
        Elv_in,
        Elv_out,
        Rm_in,
        Rm_out;
        static public final Integer length = 1 + Rm_out.ordinal();
    }

  public static  State transition[][] ={//FF means Floor=false,Room=False, etc.
        //      Elv_out    Elv_in      Room_in    Room_out
       /*FF*/ {State.FF,  State.FF,   State.TT,    State.FF },
       /*TT*/ {State.TT,  State.FF,   State.TT,    State.TF },
       /*TF*/ {State.FF,  State.FF,   State.TT,    State.TF }

    };



}
