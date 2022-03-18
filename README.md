# FabledSkyBlock-Raids

FabledSkyBlock-Raids is a Minecraft fork-plugin which allows players to 'raid' each other's islands.

Before opening the server, an admin must create an island using 2 separate tools they receive from /is admin structure tools. The island will have a 'SpawnZone' which
players can't change.

When a player wants to raid someone, they will get teleported to a random island while invisible and unable to interact with anything. This allows raiders to scout
the island before choosing to raid. Every time a raider scouts a new island, it will cost them RaidSense (currency).

RaidSense is a currency that accumulates over time. When an island reaches a certain amount of RaidSense, they will receive buffs such as potion effects when raiding a new island.

A raid will end when all raiders are dead or if the raiders run the command '/raid end'.

All raids will be saved into the server's database. Players have access to the RaidHistory command which will open a GUI containing information about all recent raids.
This includes the date and time of the raid, the members of the raid, who the leader was and what items were stolen/broken.
The database is stored locally in the server files.

- Raid Commands: 
  - /raid help - _Get all the Raid commands_
  - /raid end - _End the current raid_
  - 
