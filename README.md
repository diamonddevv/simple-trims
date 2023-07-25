# Simple Trims
### Trims are way too complicated. Let's change that.

This Project is licensed under GNU GPLv3.\
(C) DiamondDev, 2023.

_Inspired by (and assisted by the code of) Benjamin Norton's [All The Trims](https://github.com/benjamin-norton/AllTheTrims). This project would not have been possible without it, so thank you!_

This project was orignally a fork of All the Trims, until I realized I did not need most of the things in that mod, and I realised
what I _did_ need I was better off writing myself. All the Trims was heavily used as a reference for some features, however. Some code
may appear very similar, as the base concept of what All the Trims does is similar to what Simple Trims does for material types.

---

## Include in Project
_To embed SimpleTrims in your mod (JiJ'd):_

### In `build.gradle`
#### top `repositories` block
```
repositories {
  maven { url "https://jitpack.io"  } // Fetch from jitpack
}
```
#### `dependencies` block
```
repositories {
  include(implementation("com.github.diamonddevv:simple-trims:${project.simple_trims_version}"));
}
```
### In `gradle.properties`
```
simple_trims_version=<Insert version you wish to use here!>
```

---

## **This is a work in Progress!**
_Here be dragons_
- There are a parts of this mod incomplete:
  - Item Model Trim Tinting 
    - I can't figure out how to make this dynamic. If anyone knows how to add a texture to a BakedModel or something, _please_ open a pull request.
    - For now, items using simple trim materials are displayed in inventories as not having a trim at all. The tooltip remains, however.
  - Simple Armor Trim Templates

### Features:
- Simplifies the process of adding a new trim material or pattern, in contrast to the original project adding support for all items
- Makes all armor eligible for trims, including modded armor. (This feature is functionally identical to that of All The Trims)
- Works parallel to vanilla's system

#### For those who wish to create custom trims using this mod:
- Once the system is functional, I will be creating a wiki on how to use this system.
- Vanilla Packs for armor trims _should_ still work.

---

# Credits
- [Benjamin Norton](https://github.com/Benjamin-Norton) for their mod All the Trims, of which this is heavily inspired and referenced from.
