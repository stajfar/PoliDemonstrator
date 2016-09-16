package it.polimi.polidemonstrator.businesslogic.businessrules;

/**
 * Created by saeed on 6/13/2016.
 */
import java.util.Map;
import java.util.Stack;

public class LessThan extends Operation
{
    public LessThan()
    {
        super("<");
    }

    @Override
    public LessThan copy()
    {
        return new LessThan();
    }

    @Override
    public int parse(final String[] tokens, int pos, Stack<Expression> stack)
    {
        if (pos-1 >= 0 && tokens.length >= pos+1)
        {
            String var = tokens[pos-1];

            this.leftOperand = new Variable(var);
            this.rightOperand = BaseType.getBaseType(tokens[pos+1]);
            stack.push(this);

            return pos+1;
        }
        throw new IllegalArgumentException("Cannot assign value to variable");
    }

    @Override
    public boolean interpret(Map<String, ?> bindings)
    {
        Variable v = (Variable)this.leftOperand;
        Object obj = bindings.get(v.getName());
        if (obj == null)
            return false;

        BaseType<?> type = (BaseType<?>)this.rightOperand;
        if (type.getType().equals(Float.class) && obj.getClass().equals(String.class))
        {
            if (Float.parseFloat(obj.toString()) < Float.parseFloat(type.getValue().toString()))
                return true;
        }
        return false;
    }
}