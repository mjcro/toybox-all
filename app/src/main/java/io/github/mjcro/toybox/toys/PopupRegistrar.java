package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Action;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Labeled;
import io.github.mjcro.toybox.api.services.EnvironmentModifier;

import javax.swing.text.JTextComponent;
import java.time.Instant;
import java.util.List;

public class PopupRegistrar implements EnvironmentModifier, Environment.PopupHook {
    @Override
    public void modify(Environment environment) {
        environment.addPopupHook(this);
    }

    @Override
    public List<Labeled> onPopup(Context ctx, Object target) {
        if (target instanceof CharSequence) {
            CharSequence x = (CharSequence) target;
            return List.of(
                    Action.ofName("String analyze", () -> ctx.findAndShow(StringAnalyzerToy.class, x, true)),
                    Action.ofName("String conversion", () -> ctx.findAndShow(StringConverterToy.class, x, true))
            );
        }
        if (target instanceof JTextComponent) {
            CharSequence x = ((JTextComponent) target).getText();
            return List.of(
                    Action.ofName("String analyze", () -> ctx.findAndShow(StringAnalyzerToy.class, x, true)),
                    Action.ofName("String conversion", () -> ctx.findAndShow(StringConverterToy.class, x, true))
            );
        }
        if (target instanceof Instant) {
            Instant i = (Instant) target;
            return List.of(
                    Action.ofName("Instant analyze", () -> ctx.findAndShow(InstantAnalyzerToy.class, i, true))
            );
        }
        return null;
    }
}
