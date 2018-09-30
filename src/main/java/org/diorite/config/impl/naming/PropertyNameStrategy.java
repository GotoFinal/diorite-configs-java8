package org.diorite.config.impl.naming;

@FunctionalInterface
public interface PropertyNameStrategy
{
    String applyStrategy(String propertyName);
}
