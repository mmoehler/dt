package de.adesso.tools.util.tuple.mutable;

import java.lang.reflect.Method;

/**
 * Created by mohler on 04.12.15.
 */
public class ImmutableHandler extends AbstractInvocationHandler {
    private final Object delegate;


    public ImmutableHandler(Object delegate) {
        this.delegate = delegate;
    }

    private static boolean isSetter(Method m, Object[] args) {

        final String s = m.getName();
        final int code = Integer.parseInt(s.substring(1));
        return (s.charAt(0) == '_'
                && code >= 1
                && code <= 5
                && (args.length > 0));
    }

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        if (isSetter(method, args)) {
            throw new UnsupportedOperationException(String.valueOf(method));
        }
        return method.invoke(delegate, args);
    }
}
