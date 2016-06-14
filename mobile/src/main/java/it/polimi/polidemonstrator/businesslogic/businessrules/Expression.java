package it.polimi.polidemonstrator.businesslogic.businessrules;

import java.util.Map;

public interface Expression
{
    public boolean interpret(final Map<String, ?> bindings);
}