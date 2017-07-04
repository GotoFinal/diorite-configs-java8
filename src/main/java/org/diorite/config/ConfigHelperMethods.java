package org.diorite.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.diorite.config.exceptions.ConfigLoadException;
import org.diorite.config.exceptions.ConfigSaveException;

final class ConfigHelperMethods
{
    private ConfigHelperMethods() {}

    static OutputStreamWriter createOutputStreamWriter(Config config, File file)
    {
        try
        {
            if (! file.exists())
            {
                File absoluteFile = file.getAbsoluteFile();
                absoluteFile.getParentFile().mkdirs();
                absoluteFile.createNewFile();
            }
        }
        catch (IOException e)
        {
            throw new ConfigSaveException(config.template(), file, "can't create a file.", e);
        }
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            return new OutputStreamWriter(fileOutputStream, config.encoder());
        }
        catch (IOException e)
        {
            throw new ConfigSaveException(config.template(), file, e.getMessage(), e);
        }
    }

    static InputStreamReader createInputStreamReader(Config config, File file)
    {
        try
        {
            if (! file.exists())
            {
                File absoluteFile = file.getAbsoluteFile();
                absoluteFile.getParentFile().mkdirs();
                absoluteFile.createNewFile();
            }
        }
        catch (IOException e)
        {
            throw new ConfigLoadException(config.template(), file, "can't create a file.", e);
        }
        try
        {
            FileInputStream fileInputStream = new FileInputStream(file);
            return new InputStreamReader(fileInputStream, config.decoder());
        }
        catch (IOException e)
        {
            throw new ConfigLoadException(config.template(), file, e.getMessage(), e);
        }
    }

    static InputStreamReader createInputStreamReader(ConfigTemplate<?> template, File file)
    {
        try
        {
            if (! file.exists())
            {
                File absoluteFile = file.getAbsoluteFile();
                absoluteFile.getParentFile().mkdirs();
                absoluteFile.createNewFile();
            }
        }
        catch (IOException e)
        {
            throw new ConfigLoadException(template, file, "can't create a file.", e);
        }
        try
        {
            FileInputStream fileInputStream = new FileInputStream(file);
            return new InputStreamReader(fileInputStream, template.getDefaultDecoder());
        }
        catch (IOException e)
        {
            throw new ConfigLoadException(template, file, e.getMessage(), e);
        }
    }
}
