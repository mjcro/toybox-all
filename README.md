# ToyBox

A Java Swing **base layer** for building your own collection of small desktop
utilities. ToyBox provides the application shell — window manager, navigation
tree, FlatLaf theming, settings storage, logging, event bus and a Spring-based
runtime — so all you need to build is the panel for each tool ("toy").

The library ships a handful of generic toys (string converter, regex replace,
hashing, encryption, JSON viewer, ...) which can be reused, ignored or replaced
in your distribution.

## Modules

| Module      | Java | What you get                                                                     |
|-------------|------|----------------------------------------------------------------------------------|
| `api`       | 8    | Core interfaces — `Toy`, `Context`, `Environment`, `Menu`, `Label`, events.      |
| `swing`     | 11   | Swing toolkit — widgets, layouts, prefabs, FlatLaf themes, embedded Noto fonts.  |
| `templates` | 8    | String template engine with typed bindings (number, date, file, enum, ...).     |
| `app`       | 17   | Spring-based application shell + a set of generic, reusable toys.                |
| `all`       | 17   | Aggregate dependency that pulls everything in.                                   |

## Maven

Add the aggregate dependency, or pick the modules you actually need:

```xml
<dependency>
    <groupId>io.github.mjcro.toybox</groupId>
    <artifactId>all</artifactId>
    <version>0.4.5</version>
</dependency>
```

## Quick start

### 1. Implement a toy

A `Toy` is a panel with a place in the navigation menu.

```java
package com.example.mytools;

import io.github.mjcro.toybox.api.*;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HelloToy implements Toy {

    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        // Where the toy appears in the menu tree.
        // Use Menu.TOYBOX_BASIC_TOOLS_MENU, your own Menu.text("..."), etc.
        return List.of(Menu.text("My Tools"));
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName("fam://emoticon_smile", "Hello");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Hello from my toy!"), BorderLayout.CENTER);
        return panel;
    }
}
```

### 2. Register it via Java SPI

Create `src/main/resources/META-INF/services/io.github.mjcro.toybox.api.Toy`:

```
com.example.mytools.HelloToy
```

Every line is a fully-qualified toy class name. ToyBox discovers them at
startup through `ServiceLoader`.

### 3. Bootstrap the application

The simplest entry point reuses ToyBox's default Spring configuration, which
already wires in the window manager and the bundled toys:

```java
package com.example.mytools;

import io.github.mjcro.toybox.app.Application;
import io.github.mjcro.toybox.app.config.MainConfiguration;

public class Main {
    public static void main(String[] args) {
        Application.MAIN_TITLE = "My Tools";
        Application.MAIN_ICON  = "toybox-64";   // or your own classpath icon
        Application.VERSION    = "v1.0.0";

        Application.startSpringApplication(args, MainConfiguration.class);
    }
}
```

### 4. Run

Add your jar (and `io.github.mjcro.toybox:all`) to the classpath and launch
`com.example.mytools.Main`. The window opens with your toy installed under
its declared menu path.

## Branding & runtime configuration

`io.github.mjcro.toybox.app.Application` exposes static fields you can set
before calling `startSpringApplication(...)`:

| Field             | Purpose                                                |
|-------------------|--------------------------------------------------------|
| `MAIN_TITLE`      | Window title.                                          |
| `MAIN_ICON`       | Icon URI (classpath resource name or `fam://...`).     |
| `VERSION`         | Version string shown in the *About* toy.               |
| `WINDOW`          | `"tabWindow"` (default) or `"mdiWindow"`.              |
| `DARK_MODE`       | Force dark FlatLaf theme.                              |
| `DEBUG`           | Verbose logging through SLF4J.                         |

Command-line flags consumed automatically by `Application.main`:

| Flag                     | Effect                              |
|--------------------------|-------------------------------------|
| `-d`, `--debug`          | Enable debug logging                |
| `--dark` / `--light`     | Force dark or light Look-and-Feel   |
| `--mdi` / `--tab`        | MDI window or tabbed window mode    |
| `-t<ToyClassName>`       | Open the given toy on startup       |

## Customising the Spring context

If you need additional beans (services, executors, settings), pass your own
`@Configuration` class — it can `@Import(MainConfiguration.class)` to keep the
default app wiring, or replace it entirely.

```java
@Configuration
@Import(MainConfiguration.class)
@ComponentScan("com.example.mytools")
public class MyAppConfiguration {
    @Bean
    public MyService myService() {
        return new MyService();
    }
}

// in main:
Application.startSpringApplication(args, MyAppConfiguration.class);
```

Toys are plain classes — they're instantiated by `ServiceLoader`, not by
Spring. If you prefer Spring-managed toys, scan their package the same way
and register them as `@Component` (and skip the SPI file).

## Building blocks worth knowing

- **`Context`** — passed to `Toy#build`. Gives access to the `Environment`,
  initial data, the event bus and helpers like `context.show(otherToy, data)`
  to chain toys together.
- **`Environment`** — clipboard, file dialogs, settings storage and a thread
  pool (`environment.execute(Runnable)` for background work).
- **`swing.prefab.*`** — opinionated factories: `ToyBoxButtons`,
  `ToyBoxLabels`, `ToyBoxPanels`, `ToyBoxTextComponents`, `ToyBoxIcons`,
  `ToyBoxLaF`. Use them instead of raw `JButton` / `JPanel` to inherit the
  ToyBox look and behaviour (hints, monospaced fonts, dark-mode-aware icons).
- **`swing.layouts.InlineBlockLayout` / `RowsLayout`** — layout managers
  geared towards form-style toys.
- **`templates`** — string template + typed bindings; useful for toys that
  produce text from structured input (SQL, code snippets, requests, ...).
- **Menus** — `Menu.TOYBOX_*` constants give you a stable place in the
  hierarchy; or build your own with `Menu.text(...)` / `Menu.iconText(...)`.

## Bundled toys

Reuse them as-is, or take them out by replacing `MainConfiguration`:
About, Cipher List, Data View, Encryption (AES/GCM), Hashing, Instant
Analyzer, Logs, Regex Replace, Settings, ShowRoom Components, String
Analyzer, String Converter, String List, UI Properties.

## Build

```shell
mvn clean install
```