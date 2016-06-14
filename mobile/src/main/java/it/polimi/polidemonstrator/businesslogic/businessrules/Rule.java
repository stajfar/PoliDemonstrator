package it.polimi.polidemonstrator.businesslogic.businessrules;

/**
 * Created by saeed on 6/13/2016.
 */
import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rule
{
    private List<Expression> expressions;
    private ActionDispatcher dispatcher;

    public static class Builder
    {
        private List<Expression> expressions = new ArrayList<>();
        private ActionDispatcher dispatcher = new NullActionDispatcher();

        public Builder withExpression(Expression expr)
        {
            expressions.add(expr);
            return this;
        }

        public Builder withDispatcher(ActionDispatcher dispatcher)
        {
            this.dispatcher = dispatcher;
            return this;
        }

        public Rule build()
        {
            return new Rule(expressions, dispatcher);
        }

        private class NullActionDispatcher implements ActionDispatcher {
            @Override
            public void fire(Context context, String myMessage) {

            }
        }
    }

    private Rule(List<Expression> expressions, ActionDispatcher dispatcher)
    {
        this.expressions = expressions;
        this.dispatcher = dispatcher;
    }

    public boolean eval(Map<String, ?> bindings, Context context, String myMessage)
    {
        boolean eval = false;
        for (Expression expression : expressions)
        {
            eval = expression.interpret(bindings);
            if (eval)
                dispatcher.fire(context,myMessage);
        }
        return eval;
    }
}
