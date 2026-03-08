# Simple Sit

`Simple Sit` is a minimal Fabric mod focused on clean sitting interactions. It allows players to sit on common blocks with a right click or a command, without requiring OP for normal use.

Chinese documentation: [README.md](README.md)

## Features

- Sit on carpets with right click
- Sit on stairs with right click
- Optional support for slabs and beds
- Hidden seat entity implementation
- Occupancy detection to prevent multiple players from sitting in the same spot
- Stand up with `Shift`
- Automatically stand up when moving or jumping
- `/sit` command for sitting in place
- Simple admin configuration through commands and config file
- Server-first design for multiplayer servers

## Dependencies

This mod requires:

- `Fabric API`
- `Fabric Language Kotlin`

## Installation

### Dedicated server

Place the following files into the server `mods` directory:

- `simple-sit-<version>-fabricmc<mc-version>.jar`
- `fabric-api`
- `fabric-language-kotlin`

Clients usually do not need to install this mod to join and use it.

### Single-player

Single-player runs an integrated server, so the mod and its dependencies must also be installed on the local client.

## Usage

### Player actions

- Right click a supported block to sit
- Press `Shift` to stand up
- Move or jump to stand up automatically
- Use `/sit` to sit in place

### Supported blocks

Enabled by default:

- Stairs
- Carpets
- Slabs
- Beds

Each category can be enabled or disabled by the administrator.

## Commands

### Player command

| Command | Description |
| --- | --- |
| `/sit` | Sit at the current position |

### Admin commands

| Command | Description |
| --- | --- |
| `/sitadmin reload` | Reload the configuration file |
| `/sitadmin set enabled <true\|false>` | Enable or disable the mod |
| `/sitadmin set requireSneakRightClick <true\|false>` | Require sneak + right click |
| `/sitadmin set requireEmptyHand <true\|false>` | Require an empty hand |
| `/sitadmin set allowStairs <true\|false>` | Toggle stair sitting |
| `/sitadmin set allowCarpets <true\|false>` | Toggle carpet sitting |
| `/sitadmin set allowSlabs <true\|false>` | Toggle slab sitting |
| `/sitadmin set allowBeds <true\|false>` | Toggle bed sitting |
| `/sitadmin set allowCommandSit <true\|false>` | Toggle `/sit` |

## Configuration

Config file path:

`config/simple-sit.json`

Default content:

```json
{
  "enabled": true,
  "requireSneakRightClick": false,
  "requireEmptyHand": true,
  "allowStairs": true,
  "allowCarpets": true,
  "allowSlabs": true,
  "allowBeds": true,
  "allowCommandSit": true
}
```

## Implementation notes

- A hidden `ArmorStand` is used as the seat entity to avoid custom network entity complexity
- All core logic runs on the server side
- Main-hand interaction is used to reduce accidental triggers
- `requireEmptyHand` and `requireSneakRightClick` are available for simple server-side tuning

## Build

Requirements:

- Java 21

Build command:

```powershell
.\gradlew.bat build
```

Artifact name example:

```text
simple-sit-1.0.0-fabricmc1.21.11.jar
```

## Release

This repository supports two GitHub Release flows:

### Automatic release by tag

```bash
git tag v1.0.0
git push origin v1.0.0
```

### Manual release

Run the `release` workflow from the GitHub `Actions` page and provide the target tag.
