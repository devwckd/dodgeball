# dodgeball

Dodgeball is a plugin that allows admins to create Dodgeball rooms that players can play on.

## Features

- Arena editor.
- Numeric disadvantage counter (Teams in numeric disadvantage get an extra ball on their side).
- Ball recovery (Balls return to either side of the map).
- PlaceholderAPI placeholders.

## Tutorial

### Arena Setup

https://user-images.githubusercontent.com/65056371/228681623-87f30d9e-d5a9-4936-8990-a21970305b9a.mp4

### Creating Room

A room can be created with the command `/dodgeballrooms create <id>`. Example `/dodgeballrooms create default`

### Joining a Room

The command `/dodgeball` lists the rooms, a room item can be clicked in order to be joined.  
![image](https://user-images.githubusercontent.com/65056371/228682938-60bf0515-e880-480e-a204-669bf95c0964.png)  

It also shows the player's stats.  
![image](https://user-images.githubusercontent.com/65056371/228683003-26f80d63-56a0-423f-bb0f-0369ea8bdad1.png)  

## Placeholders

- %dodgeball_team% - Shows the display name of the player's team
- %dodgeball_team_count% - Shows the player count of the player's team
- %dodgeball_blue_team_count% - Shows the player count of the player's room blue team 
- %dodgeball_red_team_count% - Shows the player count of the player's room red team
- %dodgeball_history_kills% - Shows the player's total kills
- %dodgeball_history_deaths% - Shows the player's total deaths
- %dodgeball_history_wins% - Shows the player's total wins
- %dodgeball_history_games% - Shows the player's total games
- %dodgeball_history_wr% - Shows the player's win rate
- %dodgeball_history_kdr% - Shows the player's kill/death ratio
