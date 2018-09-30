package org.diorite.config.impl.naming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.diorite.commons.ParserContext;

public final class StandardPropertyNamingStrategies
{
    private static final Map<String, PropertyNameStrategy> PROPERTY_NAMING_STRATEGIES = new HashMap<>();
    private static final char[]                            SPLIT_CHARS = {' ', '-', '.', ',', '_', '/', '\\', '=', '+', ':', ';'};

    public static final String                             IDENTITY          = "IDENTITY";
    public static final String                             LOWER_CASE        = "LOWER_CASE";
    public static final String                             UPPER_CASE        = "UPPER_CASE";
    public static final String                             CAMEL_CASE        = "CAMEL_CASE";
    public static final String                             UPPER_CAMEL_CASE  = "UPPER_CAMEL_CASE";
    public static final String                             SNAKE_CASE        = "SNAKE_CASE";
    public static final String                             UPPER_SNAKE_CASE  = "UPPER_SNAKE_CASE";
    public static final String                             HYPHEN_CASE       = "HYPHEN_CASE";
    public static final String                             UPPER_HYPHEN_CASE = "UPPER_HYPHEN_CASE";

    static
    {
        Arrays.sort(SPLIT_CHARS);

        registerStrategy(IDENTITY, propertyName -> propertyName);

        registerStrategy(LOWER_CASE, String::toLowerCase);
        registerStrategy(UPPER_CASE, String::toUpperCase);

        registerStrategy(CAMEL_CASE, propertyName -> transform(propertyName, '\0', false, true));
        registerStrategy(UPPER_CAMEL_CASE, propertyName -> transform(propertyName, '\0', true, true));

        registerStrategy(SNAKE_CASE, propertyName -> transform(propertyName, '_', false, false));
        registerStrategy(UPPER_SNAKE_CASE, propertyName -> transform(propertyName, '_', true, true));

        registerStrategy(HYPHEN_CASE, propertyName -> transform(propertyName, '-', false, false));
        registerStrategy(UPPER_HYPHEN_CASE, propertyName -> transform(propertyName, '-', true, true));
    }

    public static void registerStrategy(String strategyName, PropertyNameStrategy propertyNameStrategy)
    {
        PROPERTY_NAMING_STRATEGIES.put(strategyName, propertyNameStrategy);
    }

    public static PropertyNameStrategy byName(String strategyName)
    {
        PropertyNameStrategy nameStrategy = PROPERTY_NAMING_STRATEGIES.get(strategyName);

        if (nameStrategy == null)
        {
            throw new IllegalArgumentException("could not find name strategy with given name: " + strategyName);
        }

        return nameStrategy;
    }

    private static String transform(String rawName, char wordSeparator, boolean capitalizeFirstWord, boolean capitalizeWords)
    {
        List<String> words = splitToWords(rawName);
        StringBuilder nameBuilder = new StringBuilder();

        boolean first = true;
        for (int wordIndex = 0, wordsSize = words.size(); wordIndex < wordsSize; wordIndex++)
        {
            if (wordIndex != 0 && wordSeparator != '\0')
            {
                nameBuilder.append(wordSeparator);
            }
            String word = words.get(wordIndex);
            if ((first && capitalizeFirstWord) || (!first && capitalizeWords))
            {
                nameBuilder.append(WordUtils.capitalize(word));
            }
            else
            {
                nameBuilder.append(word.toLowerCase());
            }

            first = false;
        }

        // append additional underscores used in name
        int underscoresStart = 0;
        int underscoresEnd = 0;
        for (int i = 0; i < rawName.length(); i++)
        {
            if (rawName.charAt(i) == '_')
            {
                underscoresStart += 1;
            }
            else
            {
                break;
            }
        }
        for (int i = rawName.length() - 1; i >= 0; i--)
        {
            if (rawName.charAt(i) == '_')
            {
                underscoresEnd += 1;
            }
            else
            {
                break;
            }
        }

        return StringUtils.repeat('_', underscoresStart) + nameBuilder.toString() + StringUtils.repeat('_', underscoresEnd);
    }

    private static List<String> splitToWords(String rawName)
    {
        List<String> words = new ArrayList<>(5);
        StringBuilder wordBuilder = new StringBuilder();
        int i = 0;

        String lastOneLetter = null;
        ParserContext parserContext = new ParserContext(rawName);
        // skip special
        while (parserContext.hasNext())
        {
            char c = parserContext.next();
            if (Arrays.binarySearch(SPLIT_CHARS, c) < 0)
            {
                parserContext.previous();
                break;
            }
        }
        boolean endWord = false;
        while (parserContext.hasNext())
        {
            char c = parserContext.next();
            if (Arrays.binarySearch(SPLIT_CHARS, c) >= 0 || Character.isUpperCase(c))
            {
                endWord = true;
                if (! Character.isUpperCase(c))
                {
                    continue;
                }
            }
            if (endWord)
            {
                if (wordBuilder.length() == 1 && Character.isUpperCase(c))
                {
                    if (lastOneLetter == null)
                    {
                        lastOneLetter = wordBuilder.toString();
                    }
                    else
                    {
                        lastOneLetter = wordBuilder.toString();
                        words.set(words.size() - 1, words.get(words.size() - 1) + lastOneLetter);
                        wordBuilder = new StringBuilder();
                        wordBuilder.append(c);
                        endWord = false;
                        continue;
                    }
                }
                else
                {
                    lastOneLetter = null;
                }
                endWord = false;
                if (wordBuilder.length() != 0)
                {
                    words.add(wordBuilder.toString());
                }
                wordBuilder = new StringBuilder();
                wordBuilder.append(c);
            }
            else
            {
                wordBuilder.append(c);
            }
        }
        if (wordBuilder.length() > 0)
        {
            if (wordBuilder.length() == 1 && Character.isUpperCase(wordBuilder.charAt(0)) && (lastOneLetter != null))
            {
                lastOneLetter = wordBuilder.toString();
                words.set(words.size() - 1, words.get(words.size() - 1) + lastOneLetter);
            }
            else
            {
                words.add(wordBuilder.toString());
            }
        }
        return words;
    }
}
