![icon](icon.png)

# Entity Sound Features (ESF)



## What is this page?

This is a guide and documentation for how to use the added sound related utilities added by ESF to ETF and EMF

These utilities are either additional random properties (ETF & EMF) or animation functions (EMF).

___

## Random properties

ESF adds these random properties to be used in variating: sounds (ESF), textures (ETF) and models (EMF).

#### soundSuffix.<n>
The sound suffix property can be used to match against the last sound suffix that ESF set for the given entity. 
i.e. if the last sound that ESF attempted to modify was changed to its second variant then this would match for "2".
By default it will match for "0".
This value only gets set when the played sound actually has variants set to be variated by ESF, 
sounds played by the entity that are not set to be changed by ESF will not affect this.

#### soundRule.<n>
Exact same as above except this property will match against the last met sound rule, set in an ESF .properties file for a sound,
that was met by this entity

#### playingSound.<n>  v0.4+
Matches the given strings or regex/pattern against each of the currently playing sound events found in the sound engine.
Modded sound events should include the mods namespace but vanilla sound events will not include the minecraft: namespace.
Optionally if this property text starts with 'print:' it will print all found sound event ids to the log every time it checks.
Note not all sounds active in the sound engine are actually in range of the client player, so this may not always work as expected.


### Property Examples

```properties
#uses model2 for every entity that last had a sound modified to their 6th variant, i.e sound6.json
models.1= 2
soundSuffix.1=6

#uses model3 for every entity that last had a sound variation match with rule #3 in an ESF .properties file
models.2=3
soundRule.2=3

#uses model4 when the games sound engine is actively playing 'entity.chicken.death' or 'music_disc.pigstep'
models.3=4
playingSound.3=entity.chicken.death music_disc.pigstep

#uses model5 when the games sound engine is actively playing a sound that regex matches 'music_disc.*' which is all music discs
models.4=5
playingSound.4=regex:music_disc.*

models.5=1
```

___

## Animation functions

ESF adds these animation functions to be used in EMF custom model animations.

#### playsound(...)  v0.4+
`playsound(id, cond, x, y, z, a)` or `playsound(id, cond, x)`
Is a boolean function that will trigger the given sound event to play.
Returns true if the sound was triggered or false otherwise.

It can have either 3 or 6 arguments depending on your needs
- id = the sound event id e.g. entity.chicken.death
Modded sound events should include the mods namespace but vanilla sound events will not include the minecraft: namespace.
cond= boolean condition that must be true to trigger the sound
- x = minimum delay between sound plays in game ticks, minimum of 1
- y = (optional) volume 0-1, default is 1.0
- z = (optional) pitch 0.5-2, default is 1.0
- a = (optional) distance 0-128 the distance the sound can be heard from, default is 16

This function has a very simple limitation, it will only trigger the sound when the animations play. 
This may seem obvious, but you may not expect the end result of this limitation, such as mobs making constant short noises
such as custom timed footsteps suddenly going quiet when you turn your camera away. 



#### playingsound(...)  v0.4+
`playingsound(id)`
Is a boolean function that checks if the given sound event id is currently playing in the sound engine,
returns true if the sound is currently playing or false otherwise.
- id= the sound event id e.g. entity.chicken.death,
Modded sound events should include the mods namespace but vanilla sound events will not include the `minecraft:` namespace.

This function also has a limitation, it simply checks for active sounds in the sound engine, 
this does not guarantee the sound will be audible to the player. You will find many sounds are playing just out of reach
of the player just in case the player was to walk into the area and hear the sound finish.

### Animation Examples

Play a sound and bob the mobs head when it plays
- play a sound (`entity.chicken.death`)
- when the mob is walking (`limb_speed > 0`)
- with a delay of 1 second between plays (`20` ticks)
- with half volume (`0.5`)
- with normal pitch (`1.0`)
- and a distance of 64 blocks (`64`)

also Bobs the mobs head up 6 pixels every time a sound plays
```json
{
  "varb.did_play_sound": "playsound(entity.chicken.death, limb_speed > 0, 20, 0.5, 1.0, 64)",
  "var.set_to_1_then_reduce_to_0_over_1_second": "clamp(if(varb.did_play_sound, 1, var.set_to_1_then_reduce_to_0_over_1_second - 1 * frame_time), 0, 1)",
  "head.ty": "DEFAULT - var.set_to_1_then_reduce_to_0_over_1_second * 6"
}

```

set a variable when `music_disc.pigstep` is playing
```json
{
  "varb.is_pigstep_playing": "playingsound(music_disc.pigstep)"
}
```
