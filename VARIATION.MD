![icon](icon.png)

# Entity Sound Features (ESF)



## What is this page?

This is a guide and documentation for how to variate an entity's sounds with ESF, based on the ETF / OptiFine .properties format.


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

In this same directory we can place our sound variant files, which are just .json files with the same name as the properties file.
I.E. `ambient2.json` would be the sound file for variant #2 of the `ambient.properties` file.

These files are a single instance of a sound event, just as they are declared in the regular sounds.json file.


e.g
for a sound stored in either:
- `assets/<namespace>/esf/entity/chicken/sound_to_use.ogg`
- `assets/<namespace>/sounds/entity/chicken/sound_to_use.ogg`


example #1
```json
{
	"sounds": [
		{
			"name": "entity/chicken/sound_to_use",
			"volume": 1,
			"weight": 10
		}
	]
}
```
example #2
```json
{
	"sounds": [
		{
			"name": "entity/chicken/sound_to_use",
			"volume": 0.55,
			"weight": 10
		},
		{
			"name": "entity/chicken/other_sound_to_use",
			"volume": 0.8,
			"weight": 30
		}
	]
}
```

this means that `ambient2.json` could refer to multiple sound files, including vanilla ones, either in the esf folder or the regular /sounds/ folder.

Optionally you can also have a sound file by itself instead of the .json such as `ambient2.ogg` but then you only get the 1 sound and no volume control, or file size benefits for repeated sounds.


you can then use the properties and sound variants exactly as you would in ETF / OptiFine.

```properties
#this properites file makes all ducks in a river use ambient2.ogg which is a quack sound.
sounds.1=2
biomes.1=river

#this property at the end has no conditions, so all other ducks will use the many regular vanilla ambient sounds.
sounds.2=1
```


## differences to ETF .properties

The #1 & #0 suffixes when used in ESF will refer back to the vanilla sounds, preserving the vanilla behaviour, including all the vanilla sound variants.

Suffixes are declared in the `.properties` file by the id `sounds` as opposed to `skins` or `textures` in ETF.

ESF will calculate the sound to use each time it is called, so there is no `update frquency` setting,
nor does ESF keep track of what sounds it used previously.

As such ESF will not remember the entities initial spawn conditions for properties such as `biome`, like textures and models do. *(there is a tip below that adresses this)*

## Tips

If you just want your entity, that already has a varied Model or Texture, to have a sound to match with the model or texture.
Then you can use the `modelSuffix` or `textureSuffix` properties in the `.properties` file to match the sound to the entities already set model or texture.

Following the duck example this would look like
```properties
#uses sound 2 for every entity using their 6th texture variant, i.e texture6.png
sounds.1= 2
textureSuffix.1=6

#uses sound 3 for every entity using their 3rd model variant, i.e model3.jem
sounds.2=3
modelSuffix.2=3

sounds.3=1
```

There is also the `textureRule` & `modelRule` properties that can be used to match the specific rule in the texture or models .properties file that was last matched.

ESF itself also adds a `soundSuffix` & `soundRule` property if you ever wanted the entities last played sound to alter textures and models :)

## Example pack

an example resource-pack using the "chicken as ducks" example given in the above tutorial.
Requires `ETF`, `EMF`, & `ESF` to be installed.

> [_download_](ducks.zip)
