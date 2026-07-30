# NotSoFullFullscreen

A Fabric mod for Minecraft that adds a "borderless fullscreen" display mode.

## How it works

When you enable NotSoFullFullscreen, the mod does **not** call Minecraft's normal
fullscreen toggle. Instead it talks to GLFW directly:

1. It removes the window's OS decorations (title bar / border).
2. It resizes and repositions the window to exactly match your monitor's resolution, at (0,0).
3. The window stays in GLFW "windowed" mode the whole time (no monitor handle is ever attached to it).

Because of step 3, `Window#isFullscreen()` and the `fullscreen` option in
`options.txt` never change — Minecraft genuinely believes it's windowed — while
visually the window fills the entire screen with no border, which looks identical
to real fullscreen. This is the same trick used by "borderless windowed" modes in
other games (fast alt-tabbing, no display-mode switch, etc.).

## Important: version note

You asked for **1.21.11**. As of writing, that's real but very new — Sodium only
has a **beta** build (`0.8.13-beta.x`) for it. This project targets the stable
**1.21.1** line by default (`gradle.properties`), since that's what Sodium
officially supports with a stable release. If you're actually on 1.21.11 and want
to build against it, bump `minecraft_version` / `yarn_mappings` / `fabric_version`
in `gradle.properties` to the 1.21.11 equivalents from
https://fabricmc.net/develop and rebuild — the mod code itself doesn't depend on
anything version-specific enough to need changes for a same-generation bump like
that.

## How you use it in-game

- **Keybind**: Options > Controls > NotSoFullFullscreen. Unbound by default, so
  bind a key first.
- **Settings screen**: if you have [Mod Menu](https://modrinth.com/mod/modmenu)
  installed, there's a NotSoFullFullscreen entry in the mods list with an ON/OFF
  toggle.
- The setting is saved to `config/notsofullfullscreen.json` and reapplied
  automatically next time you launch.

## Why it's not literally a row inside Sodium's own Video Settings page

Sodium replaces the vanilla Video Settings screen with its own, and that internal
screen structure changes between Sodium versions — a mixin hard-coded against it
tends to break on the next Sodium update. Recent Sodium (0.8.12+ for 1.21.1, which
includes the backported Config API) actually exposes an official
`net.caffeinemc.mods.sodium.api.config` package for exactly this — third-party
mods adding their own pages/options to Sodium's screen without mixins. See:
https://github.com/CaffeineMC/sodium/wiki/CaffeineMC-Maven-&-Config-API

I didn't wire that up here because I can't compile/test against your exact Sodium
build in this environment, and getting the builder API subtly wrong is worse than
not using it. What's here (keybind + Mod Menu screen) is guaranteed to work
regardless of Sodium version. If you want, tell me your exact Sodium version and
I can write the direct integration against `net.caffeinemc.mods.sodium.api.config`
for that specific build — just know it'll need a real compile-test on your end
since I can't fetch Sodium's Maven from this sandbox.

## Building

Requires JDK 21 and internet access (this project wasn't compiled/tested in this
sandbox — it can only reach a handful of package registries, not Mojang's or
Fabric's servers).

```
./gradlew build
```

The built jar will be in `build/libs/`. Drop it into your `mods/` folder along
with Fabric API (and Sodium, if you want the borderless mode to coexist with it —
Sodium doesn't need to be present for this mod to work at all).

## Files

- `WindowModeHelper.java` — the actual GLFW logic (the important part).
- `NotSoFullFullscreenMod.java` — client entrypoint, keybind, startup restore.
- `NsfsConfig.java` — persists the on/off state.
- `gui/NsfsConfigScreen.java` — the settings screen shown via Mod Menu.
- `compat/ModMenuIntegration.java` — soft dependency, only loaded if Mod Menu is present.
