# BackSlotForge
BackSlotForge adds extra slots which are easily accessible for storing your sword on your back.

### Installation
BackSlotForge is a mod built for the Forge. It requires [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) to be installed separately; all other dependencies are installed with the mod.

### License
BackSlotForge is licensed under GLPv3 and from [BackSlot](https://github.com/Globox1997/BackSlot).

### Datapacks
Add items which can be worn on the back or belt via a datapack. There are two backslot item tags given called `backslot_items` and `beltslot_items`. To adjust the position and size of the rendered items, a resource pack is needed. BackSlot uses the head renderer which can be set or configured in the models folder and looks like this:

```json
"head": {
			"rotation": [ 0, 0, 270 ],
			"translation": [ 0, 3.5, 0.05],
			"scale":[ 1.05, 1.05, 1.0 ]
		}
```

A full example can be found below:
```json
{
    "parent": "item/generated",
    "textures": {
        "layer0": "minecraft:item/iron_sword"
    },
    "display": {
        "head": {
			"rotation": [ 0, 0, 270 ],
			"translation": [ 0, 3.5, 0.05],
			"scale":[ 1.05, 1.05, 1.0 ]
		}
    }
}
```
