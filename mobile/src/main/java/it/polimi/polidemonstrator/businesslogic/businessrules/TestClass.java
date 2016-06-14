package it.polimi.polidemonstrator.businesslogic.businessrules;

/**
 * Created by saeed on 6/13/2016.
 */

import java.util.HashMap;
import java.util.Map;

public class TestClass {
    public TestClass() {
        // create a singleton container for operations
        Operations operations = Operations.INSTANCE;

        // register new operations with the previously created container
        operations.registerOperation(new And());
        operations.registerOperation(new Equals());
        operations.registerOperation(new Not());

        // defines the triggers when a rule should fire
        Expression ex3 = ExpressionParser.fromString("PATIENT_TYPE = 'A' AND NOT ADMISSION_TYPE = 'O'");
        Expression ex1 = ExpressionParser.fromString("PATIENT_TYPE = 'A' AND ADMISSION_TYPE = 'O'");
        Expression ex2 = ExpressionParser.fromString("PATIENT_TYPE = 'B'");

        // define the possible actions for rules that fire
        ActionDispatcher notificationDispatcher = new NotificationDispatcher();
        // ActionDispatcher outPatient = new OutPatientDispatcher();

    // create the rules and link them to the accoridng expression and action
        Rule rule1 = new Rule.Builder()
            .withExpression(ex1)
            .withDispatcher(notificationDispatcher)
            .build();


        Rule rule2 = new Rule.Builder()
                .withExpression(ex2)
                .withExpression(ex3)
                .withExpression(ex1)
                .withDispatcher(notificationDispatcher)
                .build();

        // add all rules to a single container
        Rules rules = new Rules();
        rules.addRule(rule1);
        rules.addRule(rule2);




        // for test purpose define a variable binding ...
        Map<String, String> bindings = new HashMap<>();
        bindings.put("PATIENT_TYPE", "'A'");
        bindings.put("ADMISSION_TYPE", "'O'");
        // ... and evaluate the defined rules with the specified bindings
        for(Rule rule : rules.getRules()) {
            boolean triggered = rule.eval(bindings);
            System.out.println("Action triggered: " + triggered);
        }
    }




}
