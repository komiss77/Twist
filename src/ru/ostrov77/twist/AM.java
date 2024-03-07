package ru.ostrov77.twist;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.komiss77.enums.GameState;
import ru.komiss77.objects.CaseInsensitiveMap;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.OstrovConfig;
import ru.komiss77.utils.OstrovConfigManager;
import ru.ostrov77.minigames.MG;

public class AM {

    public static final OstrovConfigManager manager;
    private static final OstrovConfig config;
    public static CaseInsensitiveMap<Arena> arenas;
    public static CaseInsensitiveMap<Arena> arenasByWorld;
    public static boolean save;

    static {
        manager = new OstrovConfigManager(TW.plugin);
        config = manager.getNewConfig("config.yml");
        arenas = new CaseInsensitiveMap();
        arenasByWorld = new CaseInsensitiveMap();
    }

    public static void load_arenas() {

        if (config.getConfigurationSection("arenas") != null) {
            config.getConfigurationSection("arenas").getKeys(false).stream().forEach(arenaName -> {

                final Location arenaLobby = LocationUtil.stringToLoc(config.getString("arenas." + arenaName + ".arenalobby"), true, true);

                Arena arena = new Arena(
                        arenaName,
                        LocationUtil.stringToLoc(config.getString("arenas." + arenaName + ".zeropoint"), false, true),
                        arenaLobby,
                        config.getInt("arenas." + arenaName + ".size_x"),
                        config.getInt("arenas." + arenaName + ".size_z"),
                        config.getInt("arenas." + arenaName + ".show"),
                        config.getInt("arenas." + arenaName + ".difficulty"),
                        config.getInt("arenas." + arenaName + ".maxRound")
                );
                //if (spawnPoints.isEmpty() || arena.arenaLobby==null || arena.boundsLow==null || arena.boundsHigh==null) {
                //TW.log_err("Арена "+arenaName+" - проблема с локациями.");
                //} else {
                arena.state = GameState.ОЖИДАНИЕ;
                //}
                arena.sendArenaData();
                arenas.put(arenaName, arena);
                arenasByWorld.put(arena.arenaLobby.getWorld().getName(), arena);
                MG.arenas.put(arenaName, arena);
            });
        }

    }

    public static void saveAll() {
        for (Arena a : AM.arenas.values()) {
            config.set("arenas." + a.arenaName + ".zeropoint", LocationUtil.toString(a.zero));
            config.set("arenas." + a.arenaName + ".arenalobby", LocationUtil.toDirString(a.arenaLobby));
            config.set("arenas." + a.arenaName + ".size_x", a.size_x);
            config.set("arenas." + a.arenaName + ".size_z", a.size_z);
            config.set("arenas." + a.arenaName + ".show", a.show);
            config.set("arenas." + a.arenaName + ".difficulty", a.difficulty);
            config.set("arenas." + a.arenaName + ".maxRound", a.maxRound);
        }
        config.saveConfig();
        save = false;
    }

    public static void DeleteArena(String arenaName, Player p) {
        final Arena a = arenas.remove(arenaName);
        if (a != null) {
            arenasByWorld.remove(a.arenaLobby.getWorld().getName());
            MG.arenas.remove(arenaName);
            a.resetGame();
            a.ResetFloor();
            config.removeKey("arenas." + a.arenaName);
            config.saveConfig();
            p.sendMessage("Arena " + arenaName + " delete succes!");
        }
    }

    public static void createArena(String name, Location player_pos, byte size_x, byte size_z) {
        Arena arena = new Arena(name, player_pos, player_pos, size_x, size_z, 0, 0, 0);
        arenas.put(name, arena);
        arenasByWorld.put(player_pos.getWorld().getName(), arena);
        MG.arenas.put(name, arena);
        save = true;
    }

    public static Arena getArena(String arenaName) {
        return arenas.get(arenaName);
    }

    public static Arena getArenaByWorld(String worldName) {
        return arenasByWorld.get(worldName);
    }

    public static Arena getArena(Player p) {
        for (Arena a : arenas.values()) {
            if (a.hasPlayer(p)) {
                return a;
            }
        }
        return null;
    }

}
