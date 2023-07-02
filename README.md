# ServerPanelManager

## About

A plugin that allows you to manage your Panel's game servers from within minecraft.

Feel free to create an issue/PR if:

- you find a bug
- you need to port this mod/plugin to an unsupported platform/version. Older MC implementations are built on an as-needed basis.
- your panel isn't supported yet. We can do some digging to see if it's possible to add support for it.
- the game event you want to listen for hasn't been implemented yet.

## Commands

### /psm

`/psmb` for BungeeCord

`/psmc` for Client-side

```
# General
start <server>
stop <server>
restart <server>
kill <server>
send <server> <command>
status <server>

# AMP only
sleep <server>
players <server>
backup <server> [name] [description] [sticky <- true or false]
```

## Permissions

```
psm -- Allows access to all commands
```

## Configuration

### CubeCoders AMP

#### ADS
```yaml
---
panels:
  <panel name>:
    type: cubecodersamp
    host: http://localhost:8080/
    username: admin
    password: admin
```

#### Servers

```yaml
servers:
  # Server using the ADS for authentication
  <server name>:
    panel: <panel name>
    # Instance Name -- found in AMP
    name: Minecraft01
    # instance ID -- right click and manage in new tab, this will be the bit after the ?id= in the URL
    # The plugin can also fetch this automatically if you leave it blank
    id: 1ty3j38u

  # Server using the instance's web port for authentication
  <server name 2>:
    panel: ampinstance
    host: http://localhost:8081/
    username: admin
    password: admin
```

### Groups

```yaml
groups:
  <group name>:
    name: Group1
    servers:
      - <server name>
      - <server name>
```

#### Placeholders

- playercount -- the number of players on the server

## TODO:

Contributions and suggestions are welcome! Just open an issue or a pull request, and I'll get to it as soon as I can.

### General

- [ ] Add a command to reload the config
- [ ] Fabric client side chat mixin -- do it like this? https://www.reddit.com/r/fabricmc/comments/wg7jrx/onchat/
- [ ] Add webhook support here and there

### Permissions

- [ ] Design a permission schema -- `spm.<command>.<server>` - `spm.<command>.<group>`
- [ ] Add permissions for each command -- `spm.command.<command>`
- [ ] Add permissions for each command for each server -- `spm.<command>.<server>`
- [ ] Add permissions for each command for each group -- `spm.<command>.<group>`

- [ ] Set up dynamic permissions checks
  - [ ] Bukkit and BungeeCord will be easy, just use the `hasPermission` method
  - [ ] Fabric will be more fun to implement -- either look into how Fabric does it, or require LuckPerms to doll out specific permissions
  - [ ] Similar dealio for Forge

### Misc

- [ ] various in-game event triggers
  - [ ] integrate with the AMP scheduler
  - [ ] set up webhook support

- [ ] Update mods/plugins from url? -- maybe add a `update` command to the group system?
  - [ ] Optional: include regex to delete old files

- [ ] Server console regex trigger -- maybe add a `regex` command to the group system?

- [ ] No-start status fix -- plop a proper `server started` message in the console -- Fix for Forge 1.12.2 v14.23.5.2858 and FTB Revelation

- [ ] Add the ability to sync the config with a database? -- would need one AMPServerManager process to act as a main process and handle all the group tasks.
  - [ ] hot reload the config when it changes -- how? -- LuckPerms does it, maybe subscribe to database changes?
