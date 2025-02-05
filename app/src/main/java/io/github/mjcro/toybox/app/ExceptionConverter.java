package io.github.mjcro.toybox.app;

import java.io.PrintStream;

public class ExceptionConverter {
    public String getInformation(Throwable e) {
        if (e == null) {
            return "";
        }

        Throwable ex = findMeaningfulCause(e);

        return ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage();
    }

    public void writeDetailedInformation(PrintStream s, Throwable e) {
        if (e == null || s == null) {
            return;
        }

        Throwable ex = findMeaningfulCause(e);

        s.println(ex.getMessage());
        s.println(ex.getClass());
        ex.printStackTrace(s);
    }

    private Throwable findMeaningfulCause(Throwable e) {
        if (e.getClass() == RuntimeException.class && e.getCause() != null && e.getCause() != e) {
            return findMeaningfulCause(e.getCause());
        }

        return e;
    }
}
