package ru.ostrov77.twist;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.komiss77.enums.Game;
import ru.komiss77.enums.GameState;
import ru.komiss77.modules.games.GM;

public class TW extends JavaPlugin implements Listener {

    public static TW plugin;
    public static String Prefix;
    
    static {
        Prefix = "§2[§bТвист§2] §f";
    }

    public static List<DyeColor> allowedColors = List.of(
            DyeColor.BLACK,
            DyeColor.BLUE,
            DyeColor.BROWN,
            DyeColor.CYAN,
            DyeColor.GRAY,
            DyeColor.GREEN,
            DyeColor.LIGHT_BLUE,
            DyeColor.LIGHT_GRAY,
            DyeColor.LIME,
            DyeColor.MAGENTA,
            DyeColor.ORANGE,
            DyeColor.PINK,
            DyeColor.PURPLE,
            DyeColor.RED,
            DyeColor.WHITE,
            DyeColor.YELLOW
    );

    @Override
    public void onEnable() {
        plugin = this;
        AM.load_arenas();
        plugin.getCommand("twist").setExecutor(new TwistCmd());
        Bukkit.getServer().getPluginManager().registerEvents(new TwistLst(), this);
        Bukkit.getLogger().info("Twist ready!");
    }

    
    @Override
    public void onDisable() {
        if (AM.save) {
            AM.saveAll();
        }

        AM.arenas.values().stream().forEach(ar -> {
            ar.resetGame();
            GM.sendArenaData(
                    Game.TW,
                    ar.arenaName,
                    GameState.ВЫКЛЮЧЕНА,
                    0,
                    "§4█████████",
                    "§2§l§oTWIST",
                    "§5" + ar.arenaName,
                    "§4█████████"
            );
        });
    }
    

    public static void log(String s) {
        Bukkit.getConsoleSender().sendMessage(Prefix + s);
    }

    public static String getTime(final long n) {
        final long sec = TimeUnit.SECONDS.toSeconds(n) - TimeUnit.SECONDS.toMinutes(n) * 60L;
        final long min = TimeUnit.SECONDS.toMinutes(n) - TimeUnit.SECONDS.toHours(n) * 60L;
        // return  ( n2>0 ? n+":"+n2 : "00:"+n );
        return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }

}






/*    
    
public static Color  DyeToBukkitColor ( byte num ) {
    
     switch (num) {
        case 0: return Color.WHITE;     //+++бел
        case 1: return Color.ORANGE;
        case 2: return Color.PURPLE;
        case 3: return Color.BLUE;
        case 4: return Color.YELLOW;
        case 5: return Color.LIME;
        case 6: return Color.RED;
        case 7: return Color.SILVER;
        case 8: return Color.GRAY;
        case 9: return Color.TEAL;
        case 10: return Color.FUCHSIA;
        case 11: return Color.NAVY;
        case 12: return Color.OLIVE;
        case 13: return Color.GREEN;
        case 14: return Color.MAROON;
        case 15: return Color.BLACK;
            default: return Color.WHITE;
    }
} 
 */
