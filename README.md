# Tile Marker Metronome

This plugin lets you use your marked tiles as a visual metronome.

Default settings can be found in the settings of the plugin.
Settings specific to a group of tiles can be found in the sidebar.

<a href="https://www.flaticon.com/free-icons/tiles" title="tiles icons">Tiles icons created by Design Circle - Flaticon</a>

# Animation types

## Disabled

This type has no animation; each tile added is drawn in the next color in the list, if there are more tiles than colors, the chosen colors start from the beginning again.
<img width="232" height="311" alt="disabled-config" src="https://github.com/user-attachments/assets/1d6961c3-b429-4543-a1aa-c5fb2ba54ade" />
<img width="256" height="104" alt="disabled-example" src="https://github.com/user-attachments/assets/cb583900-ca92-440c-95e3-cec73bc7db31" />

## Synced 

This will cause all tiles to loop through the configured colors at the same time.
<img width="230" height="312" alt="synced-config" src="https://github.com/user-attachments/assets/848a788f-cf87-4548-ae5e-ff76f7f37cba" />
<video src="https://github.com/user-attachments/assets/8ed17368-4eb0-4c4c-9df4-9c2f4f2aa52d"/>

## Train

This will color each tile in the next configured colors, moving over one color each cycle.
<img width="225" height="314" alt="train-config" src="https://github.com/user-attachments/assets/cb09c6da-d135-41f7-8845-3e45241ec86e" />
<video src="https://github.com/user-attachments/assets/974ec017-f34f-4a26-a0bd-b225206abe0a"/>

# Roadmap

- Fix colors being used in reverse order
- Move opacity from group to color
- Add export of groups
- Add export of group as ground markers
- Add import of groups
- Add import of ground markers into a group
- Add animation type that does not loop colors. e.g. 4 marked tiles with 3 configured colors only marks three tiles per tick
