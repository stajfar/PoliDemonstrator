package it.polimi.polidemonstrator.businesslogic.businessrules;

import android.app.Service;
import android.util.StringBuilderPrinter;

/**
 * Created by saeed on 6/15/2016.
 */
public class JSON_Ruler {

    private String ruleExpression;
    private String actionClass;
    private String actionMessageTitle;
    private String actionMessageText;


    public JSON_Ruler(String actionClass, String ruleExpression ,String actionMessageTitle,String actionMessageText) {
        this.actionClass = actionClass;
        this.actionMessageTitle = actionMessageTitle;
        this.actionMessageText = actionMessageText;
        this.ruleExpression = ruleExpression;
    }




    public String getActionClass() {
        return actionClass;
    }

    public String getActionMessageText() {
        return actionMessageText;
    }

    public String getActionMessageTitle() {
        return actionMessageTitle;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }







}
