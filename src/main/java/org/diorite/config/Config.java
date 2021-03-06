/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.diorite.config;

import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.diorite.config.exceptions.ConfigLoadException;
import org.diorite.config.exceptions.ConfigSaveException;

/**
 * Config interface with basic operations, all config file instances must implementing this.
 */
@SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
public interface Config
{
    /**
     * Returns config template for this config object.
     *
     * @return config template for this config object.
     */
    ConfigTemplate<?> template();

    /**
     * Returns name of config file, class name if not provided. <br>
     *
     * @return name of config file, class name if not provided.
     */
    default String name()
    {
        return this.template().getName();
    }

    /**
     * Clear all values and replace them with default ones.
     */
    void fillWithDefaults();

    /**
     * Remove all config values.
     */
    void clear();

    /**
     * Check if config contains given key, note that value on that key still might be null!
     *
     * @param key
     *         key to check, you can use dots to access nested values like other config instances or maps.
     *
     * @return true if config contains given key.
     */
    boolean contains(String key);

    /**
     * Check if config contains given key, note that value on that key still might be null!
     *
     * @param key
     *         key to check, each string in next nested level so it can be used to access nested values of other config instances or maps.
     *
     * @return true if config contains given key.
     */
    boolean contains(String... key);

    /**
     * Returns set of keys.
     *
     * @return set of keys.
     */
    Set<String> keys();

    /**
     * Returns collection of values.
     *
     * @return collection of values.
     */
    Collection<Object> values();

    /**
     * Returns set of entries.
     *
     * @return set of entries.
     */
    Set<Entry<String, Object>> entries();

    /**
     * Returns this config as normal map.
     *
     * @return this config as normal map.
     */
    Map<String, Object> asMap();

    /**
     * Returns true if this config does not contains any entries.
     *
     * @return true if this config does not contains any entries.
     */
    boolean isEmpty();

    /**
     * Returns size of this config.
     *
     * @return size of this config.
     */
    int size();

    /**
     * Returns metadata map. <br>
     * This map isn't saved, can be used by config implementations to store temporary data.
     *
     * @return metadata map.
     */
    Map<String, Object> metadata();

    /**
     * Get selected value from config.
     *
     * @param key
     *         key to get, you can use dots to access nested values like other config instances or maps.
     *
     * @return value on that key.
     */
    @Nullable
    <T> T get(String key);

    /**
     * Get selected value from config.
     *
     * @param key
     *         key to get, each string in next nested level so it can be used to access nested values of other config instances or maps.
     *
     * @return value on that key.
     */
    @Nullable
    <T> T get(String[] key);

    /**
     * Get selected value from config or default one.
     *
     * @param key
     *         key to get, you can use dots to access nested values like other config instances or maps.
     * @param def
     *         default value to use.
     *
     * @return value on that key.
     */
    @Nullable
    <T> T get(String key, @Nullable T def);

    /**
     * Get selected value from config or default one.
     *
     * @param key
     *         key to get, you can use dots to access nested values like other config instances or maps.
     * @param def
     *         default value to use.
     *
     * @return value on that key.
     */
    @Nullable
    <T> T get(String[] key, @Nullable T def);

    /**
     * Get selected value from config (or default value) as given type, library will try convert types where possible. (like from String to Integer, List of
     * strings to int array
     * etc)
     *
     * @param key
     *         key to get, you can use dots to access nested values like other config instances or maps.
     * @param def
     *         default value to use.
     * @param type
     *         type of value.
     * @param <T>
     *         type of value.
     *
     * @return value on that key.
     *
     * @exception ClassCastException
     *         if type can't be converted.
     */
    @Nullable
    <T> T get(String key, @Nullable T def, Class<T> type);

    /**
     * Get selected value from config (or default value) as given type, library will try convert types where possible. (like from String to Integer, List of
     * strings to int array
     * etc)
     *
     * @param key
     *         key to get, each string in next nested level so it can be used to access nested values of other config instances or maps.
     * @param def
     *         default value to use.
     * @param type
     *         type of value.
     * @param <T>
     *         type of value.
     *
     * @return value on that key.
     *
     * @exception ClassCastException
     *         if type can't be converted.
     */
    @Nullable
    <T> T get(String[] key, @Nullable T def, Class<T> type);

    /**
     * Get selected value from config as given type, library will try convert types where possible. (like from String to Integer, List of strings to int array
     * etc)
     *
     * @param key
     *         key to get, you can use dots to access nested values like other config instances or maps.
     * @param type
     *         type of value.
     * @param <T>
     *         type of value.
     *
     * @return value on that key.
     *
     * @exception ClassCastException
     *         if type can't be converted.
     */
    @Nullable
    <T> T get(String key, Class<T> type);

    /**
     * Get selected value from config as given type, library will try convert types where possible. (like from String to Integer, List of strings to int array
     * etc)
     *
     * @param key
     *         key to get, each string in next nested level so it can be used to access nested values of other config instances or maps.
     * @param type
     *         type of value.
     * @param <T>
     *         type of value.
     *
     * @return value on that key.
     *
     * @exception ClassCastException
     *         if type can't be converted.
     */
    @Nullable
    <T> T get(String[] key, Class<T> type);

    /**
     * Set value on given key to given value. <br>
     * Null values are allowed, note that key isn't removed on null value!
     *
     * @param key
     *         key to set, you can use dots to access nested values like other config instances or maps.
     * @param value
     *         value to set.
     */
    void set(String key, @Nullable Object value);

    /**
     * Set value on given key to given value. <br>
     * Null values are allowed, note that key isn't removed on null value!
     *
     * @param key
     *         key to set, each string in next nested level so it can be used to access nested values of other config instances or maps.
     * @param value
     *         value to set.
     */
    void set(String[] key, @Nullable Object value);

    /**
     * Removes given key from config file.
     *
     * @param key
     *         key to remove, you can use dots to access nested values like other config instances or maps.
     *
     * @return removed value.
     */
    @Nullable
    Object remove(String key);

    /**
     * Removes given key from config file.
     *
     * @param key
     *         key to remove, each string in next nested level so it can be used to access nested values of other config instances or maps.
     *
     * @return removed value.
     */
    @Nullable
    Object remove(String... key);

    /**
     * Returns encoder used by this config file.
     *
     * @return encoder used by this config file.
     */
    CharsetEncoder encoder();

    /**
     * Set encoder for this config file.
     *
     * @param encoder
     *         new encoder.
     */
    void encoder(Supplier<CharsetEncoder> encoder);

    /**
     * Returns decoder used by this config file.
     *
     * @return decoder used by this config file.
     */
    CharsetDecoder decoder();

    /**
     * Set decoder for this config file.
     *
     * @param decoder
     *         new decoder.
     */
    void decoder(Supplier<CharsetDecoder> decoder);

    /**
     * Select charset for loading this config file.
     *
     * @param charset
     *         charset to use.
     */
    default void encoding(Charset charset)
    {
        this.encoder(() -> charset.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT));
        this.decoder(() -> charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT));
    }

    @Nullable
    ClassLoader contextClassLoader();

    void contextClassLoader(@Nullable ClassLoader classLoader);

    /**
     * Returns bound file if exists.
     *
     * @return bound file if exists.
     */
    @Nullable
    File bindFile();

    /**
     * Binds config to file, allowing for simple saving and reloading.
     *
     * @param file
     *         file to bind.
     */
    void bindFile(@Nullable File file);

    /**
     * Save config to bound file/stream and custom handler if exists.
     */
    void save();

    /**
     * Save config to selected file.
     *
     * @param file
     *         file to use.
     */
    default void save(File file)
    {
        try (OutputStreamWriter outputStreamWriter = ConfigHelperMethods.createOutputStreamWriter(this, file))
        {
            this.save(outputStreamWriter);
        }
        catch (IOException e)
        {
            throw new ConfigSaveException(this.template(), file, e.getMessage(), e);
        }
    }

    /**
     * Save config to selected output stream. <br>
     * Stream isn't automatically closed here!
     *
     * @param outputStream
     *         output to use.
     */
    default void save(@WillNotClose OutputStream outputStream)
    {
        this.save(new OutputStreamWriter(outputStream, this.encoder()));
    }

    /**
     * Save config to selected writer. <br>
     * Writer isn't automatically closed here!
     *
     * @param writer
     *         writer to use.
     */
    void save(@WillNotClose Writer writer);

    /**
     * Reloads config from bound file/stream or custom handler if exists.
     */
    void load();

    /**
     * Reloads config from given file.
     *
     * @param file
     *         file to use.
     */
    default void load(File file)
    {
        try (InputStreamReader inputStreamReader = ConfigHelperMethods.createInputStreamReader(this, file))
        {
            this.load(inputStreamReader);
        }
        catch (IOException e)
        {
            throw new ConfigLoadException(this.template(), file, e.getMessage(), e);
        }
    }

    /**
     * Reloads config from selected input stream. <br>
     * Stream isn't automatically closed here!
     *
     * @param inputStream
     *         input to use.
     */
    default void load(@WillNotClose InputStream inputStream)
    {
        this.load(new InputStreamReader(inputStream, this.decoder()));
    }

    /**
     * Reloads config from selected reader. <br>
     * Reader isn't automatically closed here!
     *
     * @param reader
     *         reader to use.
     */
    void load(@WillNotClose Reader reader);

    /**
     * Returns clone of this config object.
     *
     * @return clone of this config object.
     */
    Config clone();

    /**
     * Perform operation for each config entry.
     *
     * @param action
     *         action to perform.
     */
    default void forEach(BiConsumer<? super String, ? super Object> action)
    {
        for (Map.Entry<String, Object> entry : this.entries())
        {
            String k;
            Object v;
            try
            {
                k = entry.getKey();
                v = entry.getValue();
            }
            catch (IllegalStateException ise)
            {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }
}
