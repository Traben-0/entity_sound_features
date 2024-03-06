![icon](icon.png)

# Entity Sound Features (ESF)



## What is this mod?

ESF is a mod that allows the variation of an entity's sounds based on the ETF / OptiFine .properties format.


## how?

ESF allows a resource pack to place a .properties file in the `assets/<namespace>/esf/` directory to variate entity 
sounds based on conditions set in the .properties file.
This .properties file is setup exactly like in ETF [guide here](https://github.com/Traben-0/Entity_Texture_Features/wiki/random-entity-textures),
and will not be detailed here.

ESF naturally requires ETF to be installed, and will not work without it.

## Example

Lets say we want to make a variant of the Chicken mob that has a different ambient sound, compared to regular chickens.
Perhaps you already have 2 different Chicken textures varied via ETF, such as a duck and a chicken, 
and you want to add sound differences to go with it.

In this case the Chicken ambient sound, like all sounds, has a sound event id, which for this sound is `minecraft:entity.chicken.ambient`.

You can find a list of all the vanilla sound event ids [here](https://minecraft.wiki/w/Sounds.json), note the `minecraft:`
part just refers to the namespace folder the files need to be placed in. Modded sounds will have different namespaces.

With our sound id we can figure out where to place our `.properties` file. 
We need to start in the `assets/<namespace>/esf/` directory of our resource pack, and then for each word seperated by a
`.` in our sound id we make a folder, with the last word being the properties file name.
so for the id `minecraft:entity.chicken.ambient` we would make the folders `entity/chicken/` and then place our 
`ambient.properties` file in there.

The final path would be `assets/<namespace>/esf/entity/chicken/ambient.properties`.

In this same directory we can place our sound variant files, which are just .ogg files with the same name as the properties file.
I.E. `ambient2.ogg` would be the sound file for variant #2 of the `ambient.properties` file.

you can then use the properties and sound variants exactly as you would in ETF / OptiFine.

```
#this properites file makes all ducks that spawned in a river use ambient2.ogg which is a quack sound.
sounds.1=2
biomes.1=river

#this property at the end has no conditions, so all other ducks will use the many regular vanilla ambient sounds.
sounds.2=1
```


## differences to ETF .properties

The #1 & #0 suffixes when used in ESF will refer back to the vanilla sounds, preserving the vanilla behaviour, including all the vanilla sound variants.

Suffixes are declared in the `.properties` file by the id `sounds` as opposed to `skins` or `textures` in ETF.

ESF will calculate the sound to use each time it is called, so there is no need for a `update frquency` setting.

It will still remember the entities spawn conditions for the relevant properties.


