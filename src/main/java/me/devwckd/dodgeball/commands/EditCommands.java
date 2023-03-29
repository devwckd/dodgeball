package me.devwckd.dodgeball.commands;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.arena.ArenaManager;
import me.devwckd.dodgeball.edit_session.EditSession;
import me.devwckd.dodgeball.edit_session.EditSessionManager;
import me.devwckd.dodgeball.exception.ArenaException;
import me.devwckd.dodgeball.exception.EditSessionException;
import me.devwckd.dodgeball.utils.Cuboid;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class EditCommands {

    private final EditSessionManager editSessionManager;
    private final ArenaManager arenaManager;

    /**
     * dbe
     */
    @Command(
      name = "dodgeballeditor",
      aliases = {"dbe"},
      target = CommandTarget.PLAYER,
      permission = "dodgeball.admin"
    )
    public void onMainCommand(final @NotNull Context<Player> context) {
    }

    /**
     * dbe create
     */
    @Command(
      name = "dodgeballeditor.create",
      target = CommandTarget.PLAYER
    )
    public void onCreateCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();

        if (editSessionManager.findByPlayer(player) != null) {
            player.sendMessage("§c§l[!] §eYou're already in a session.");
            return;
        }

        final EditSession editSession = editSessionManager.create(player);

        final World world = editSession.getWorld();
        world.getBlockAt(0, 59, 0).setType(Material.BEDROCK);
        player.setGameMode(GameMode.CREATIVE);
        player.teleport(new Location(world, 0, 60, 0));

        player.sendMessage("§a§l[»] §7Session created!");
        player.sendMessage("§a§l[»] §7You can now §eplace blocks §7and §eedit its properties§7!");
        player.sendMessage("§a§l[?] §7Hint: §e/dbe session setid <id>!");
    }

    /**
     * dbe finish
     */
    @Command(
      name = "dodgeballeditor.finish",
      target = CommandTarget.PLAYER
    )
    public void onFinishCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        final World world = Bukkit.getWorlds().stream().filter(w -> !w.equals(editSession.getWorld())).findFirst().get();
        player.teleport(world.getSpawnLocation());

        final Arena arena;
        try {
            arena = editSessionManager.finish(editSession);
        } catch (EditSessionException e) {
            player.sendMessage("§c§l[!] §eError finishing arena creation: §c" + e.getMessage());
            return;
        }

        try {
            arenaManager.insert(arena);
        } catch (ArenaException e) {
            player.sendMessage("§c§l[!] §eError finishing arena creation: §c" + e.getMessage());
            return;
        }

        player.sendMessage("§a§l[»] §7Arena created!");
        player.sendMessage("§a§l[»] §7You can now create a game using it!");
//        player.sendMessage("§a§l[?] §7Hint: §e/dbe session setid <id>!");
    }

    /**
     * dbe session
     */
    @Command(
      name = "dodgeballeditor.session",
      target = CommandTarget.PLAYER
    )
    public void onSessionCommand(final @NotNull Context<Player> context) {

    }

    /**
     * dbe session setid
     */
    @Command(
      name = "dodgeballeditor.session.setid",
      target = CommandTarget.PLAYER
    )
    public void onSessionSetIdCommand(final @NotNull Context<Player> context, final @NotNull String id) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        editSession.setId(id);
        player.sendMessage("§a§l[»] §7ID set to §e" + id);
    }

    /**
     * dbe session setdisplayname
     */
    @Command(
      name = "dodgeballeditor.session.setdisplayname",
      target = CommandTarget.PLAYER
    )
    public void onSessionSetDisplayNameCommand(final @NotNull Context<Player> context, final @NotNull String[] displayName) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        final String joinedDisplayName = String.join(" ", displayName);
        final String coloredDisplayName = ChatColor.translateAlternateColorCodes('&', joinedDisplayName);
        editSession.setDisplayName(coloredDisplayName);
        player.sendMessage("§a§l[»] §7Display name set to §e" + coloredDisplayName);
    }

    /**
     * dbe session setbluespawn
     */
    @Command(
      name = "dodgeballeditor.session.setbluespawn",
      target = CommandTarget.PLAYER
    )
    public void onSessionSetBlueSpawnCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        editSession.setBlueTeamSpawn(player.getLocation());
        player.sendMessage("§a§l[»] §9Blue team §7spawn set to §eyour location§7.");
    }

    /**
     * dbe session setredspawn
     */
    @Command(
      name = "dodgeballeditor.session.setredspawn",
      target = CommandTarget.PLAYER
    )
    public void onSessionSetRedSpawnCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        editSession.setRedTeamSpawn(player.getLocation());
        player.sendMessage("§a§l[»] §CRed team §7spawn set to §eyour location§7.");
    }

    /**
     * dbe session addmiddleball
     */
    @Command(
      name = "dodgeballeditor.session.addmiddleball",
      target = CommandTarget.PLAYER
    )
    public void onSessionAddBallCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        editSession.addBallSpawn(player.getLocation());
        player.sendMessage("§a§l[»] §7Added a middle ball spawn on §eyour location§7.");
    }

    /**
     * dbe session setblueball
     */
    @Command(
      name = "dodgeballeditor.session.setblueball",
      target = CommandTarget.PLAYER
    )
    public void onSessionSetBlueBallCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        editSession.setBlueBallSpawn(player.getLocation());
        player.sendMessage("§a§l[»] §9Blue team §7ball spawn set to §eyour location§7.");
    }

    /**
     * dbe session setredball
     */
    @Command(
      name = "dodgeballeditor.session.setredball",
      target = CommandTarget.PLAYER
    )
    public void onSessionSetRedBallCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        editSession.setRedBallSpawn(player.getLocation());
        player.sendMessage("§a§l[»] §cRed team §7ball spawn set to §eyour location§7.");
    }

    /**
     * dbe session setmiddleline
     */
    @Command(
      name = "dodgeballeditor.session.setmiddleline",
      target = CommandTarget.PLAYER
    )
    public void onSessionSetMiddleLineCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        final LocalSession session = WorldEditPlugin.getInstance().getSession(player);
        if (session == null) {
            player.sendMessage("§c§l[!] §eYou don't have an area selected.");
            player.sendMessage("§c§l[!] §7Select one by clicking opposing corners of a §eCuboid§7.");
            player.sendMessage("§c§l[?] §7Hint: §c//wand");
            return;
        }

        final Region selection = session.getSelection(new BukkitWorld(editSession.getWorld()));
        if (selection == null) {
            player.sendMessage("§c§l[!] §eYou don't have an area selected.");
            player.sendMessage("§c§l[!] §7Select one by clicking opposing corners of a §eCuboid§7.");
            player.sendMessage("§c§l[?] §7Hint: §c//wand");
            return;
        }

        editSession.setMiddleLine(new Cuboid(selection));
        player.sendMessage("§a§l[»] §7Middle line set to §eyour current selection§7.");
    }

    /**
     * dbe session setlobby
     */
    @Command(
      name = "dodgeballeditor.session.setlobby",
      target = CommandTarget.PLAYER
    )
    public void onSessionSetLobbyCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();

        final EditSession editSession = editSessionManager.findByPlayer(player);
        if (editSession == null) {
            player.sendMessage("§c§l[!] §eYou are not in a session.");
            player.sendMessage("§c§l[!] §7Join one by either creating or editing an arena.");
            player.sendMessage("§c§l[?] §7Hint: §c/dbe create or /dbe edit <id>");
            return;
        }

        editSession.setLobbySpawn(player.getLocation());
        player.sendMessage("§a§l[»] §7Lobby set to §eyour location§7.");
    }

}
