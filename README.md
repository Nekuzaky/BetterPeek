# BetterPeek

Client-side Minecraft mod that previews the contents of containers when you hover them.

Hover a chest, barrel, hopper, dropper, dispenser or shulker box in the world or your
inventory — a small floating window shows what's inside, without opening anything.

## Status

Early development (v0.1.0). Targets Fabric on Minecraft 1.21.11.

## Install

Requires:
- Minecraft 1.21.11
- Fabric Loader 0.18+
- Fabric API
- Java 21

Drop the jar into your `.minecraft/mods/` folder.

## How it works

The mod reads block-entity NBT for the container you're looking at (or hovering in
your inventory) and renders a small grid of item icons next to your crosshair. It's
client-only — works on any vanilla or Fabric server, no installation required on the
server side.

Shulker boxes also work in your inventory: hover one and you'll see the stored items.

## License

[MIT](./LICENSE).
