package it.polimi.polidemonstrator.businesslogic.businessrules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by saeed on 6/13/2016.
 */
public class Rules {
    private   List<Rule> rules=new ArrayList<>();

    public List<Rule> getRules() {
        return rules;
    }





    public void addRule(Rule rule) {
        this.rules.add(rule);
    }


}
