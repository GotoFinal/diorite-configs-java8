package org.diorite.config.impl;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final class MethodOrderRetrieveHelper
{
    static Method[] getDeclaredMethodsInOrder(Class<?> clazz)
    {
        Method[] methods;

        try
        {
            String resource = clazz.getName().replace('.', '/') + ".class";

            methods = clazz.getDeclaredMethods();

            InputStream is = clazz.getClassLoader()
                    .getResourceAsStream(resource);

            if (is == null)
            {
                return methods;
            }

            java.util.Arrays.sort(methods, (o1, o2) -> o2.getName().length() - o1.getName().length());
            List<byte[]> blocks = new ArrayList<>();
            int length = 0;

            while (true)
            {
                byte[] block = new byte[16 * 1024];
                int n = is.read(block);

                if (n > 0)
                {
                    if (n < block.length)
                    {
                        block = java.util.Arrays.copyOf(block, n);
                    }
                    length += block.length;
                    blocks.add(block);
                }
                else
                {
                    break;
                }
            }

            byte[] data = new byte[length];
            int offset = 0;

            for (byte[] block : blocks)
            {
                System.arraycopy(block, 0, data, offset, block.length);
                offset += block.length;
            }

            String sdata = new String(data, java.nio.charset.Charset.forName("UTF-8"));
            int lnt = sdata.indexOf("LineNumberTable");

            if (lnt != - 1)
            {
                sdata = sdata.substring(lnt + "LineNumberTable".length() + 3);
            }

            int cde = sdata.lastIndexOf("SourceFile");

            if (cde != - 1)
            {
                sdata = sdata.substring(0, cde);
            }

            MethodOffset[] methodOffsets = new MethodOffset[methods.length];

            for (int i = 0; i < methods.length; ++ i)
            {
                int pos = -1;

                while (true)
                {
                    pos = sdata.indexOf(methods[i].getName(), pos);
                    if (pos == -1)
                    {
                        break;
                    }
                    boolean subset = false;
                    for (int j = 0; j < i; ++ j)
                    {
                        if (methodOffsets[j].offset >= 0
                                && methodOffsets[j].offset <= pos
                                && pos < methodOffsets[j].offset + methodOffsets[j].method.getName().length())
                        {
                            subset = true;
                            break;
                        }
                    }
                    if (subset)
                    {
                        pos += methods[i].getName().length();
                    }
                    else
                    {
                        break;
                    }
                }

                methodOffsets[i] = new MethodOffset(methods[i], pos);
            }

            java.util.Arrays.sort(methodOffsets);

            for (int i = 0; i < methodOffsets.length; ++ i)
            {
                methods[i] = methodOffsets[i].method;
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException("could not retrieve method order", ex);
        }

        return methods;
    }

    private static class MethodOffset implements Comparable<MethodOffset>
    {
        private final Method method;
        private final int offset;

        MethodOffset(Method method, int offset)
        {
            this.method = method;
            this.offset = offset;
        }

        @Override
        public int compareTo(@NotNull MethodOffset target)
        {
            return this.offset - target.offset;
        }
    }
}
