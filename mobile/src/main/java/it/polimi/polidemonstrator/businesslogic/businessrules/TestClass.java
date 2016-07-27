package it.polimi.polidemonstrator.businesslogic.businessrules;

/**
 * Created by saeed on 6/13/2016.
 */

import android.content.Context;


import java.util.List;
import java.util.Map;

public class TestClass {
    public TestClass(Context context, List<JSON_Ruler> json_rulers, Map<String, String> bindings) {


        // create a singleton container for operations
        Operations operations = Operations.INSTANCE;
        // register new operations with the previously created container
        operations.registerOperation(new And());
        operations.registerOperation(new Equals());
        operations.registerOperation(new Not());


        // add all rules to a single container
        Rules rules = new Rules();

        for(JSON_Ruler json_ruler: json_rulers){
            Expression ex = ExpressionParser.fromString(json_ruler.getRuleExpression());

            if(json_ruler.getActionClass().equals("NotificationDispatcher")){
                // define the possible actions for rules that fire
                ActionDispatcher notificationDispatcher = new NotificationDispatcher(context,
                        json_ruler.getActionMessageTitle(),json_ruler.getActionMessageText());
                // create the rules and link them to the accoridng expression and action
                Rule rule = new Rule.Builder()
                        .withExpression(ex)
                        .withDispatcher(notificationDispatcher)
                        .build();
                //add rules
                rules.addRule(rule);
            }else {
                //other dispatchers to be added for project improvements (sensor Actuators)
            }


        }




        // defines the triggers when a rule should fire
        //Expression ex3 = ExpressionParser.fromString("WINDOWS_STATE = 'O' AND NOT ADMISSION_TYPE = 'O'");
      //  Expression ex1 = ExpressionParser.fromString("WINDOWS_STATE = 'O' AND AIRCONDITIONER = 'O'");
       // Expression ex2 = ExpressionParser.fromString("PATIENT_TYPE = 'B'");
        //Expression ex3=ExpressionParser.fromString("WINDOWS_STATE = 'O'");

        // define the possible actions for rules that fire
       // ActionDispatcher notificationDispatcher = new NotificationDispatcher();
        // ActionDispatcher outPatient = new OutPatientDispatcher();

    /* create the rules and link them to the accoridng expression and action
        Rule rule1 = new Rule.Builder()
                .withExpression(ex1)
                .withDispatcher(notificationDispatcher)
                .build();


      Rule rule2 = new Rule.Builder()
                //.withExpression(ex2)
               // .withExpression(ex3)
                .withExpression(ex1)
                .withDispatcher(notificationDispatcher)
                .build();
*/
        // add all rules to a single container
       // Rules rules = new Rules();
        //rules.addRule(rule1);
        //rules.addRule(rule2);





        // ... and evaluate the defined rules with the specified bindings
        for(Rule rule : rules.getRules()) {
            boolean triggered = rule.eval(bindings);
            System.out.println("Action triggered: " + triggered);
        }
    }




}
