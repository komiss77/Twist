package ru.ostrov77.twist;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.komiss77.utils.LocationUtil;
import ru.ostrov77.minigames.MG;


public class AM {

    public static HashMap <String, Arena> arenas;
    static File customYml = new File( Twist.instance.getDataFolder() + "/arenas.yml" );
    static FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml); 

    
    public static void Init() {
        arenas = new HashMap();
        load_arenas();
    }
   
    


    public static void load_arenas() {
        try {
            customConfig.options().copyDefaults(true);
            Twist.instance.getConfig().options().copyDefaults(true);
            
            if (customConfig.getConfigurationSection("Arenas") ==null)    return;
            
            ConfigurationSection cconf = customConfig.getConfigurationSection("Arenas");
            
            cconf.getKeys(false).stream().forEach( (name) -> {
                
                final Location arenaLobby = LocationUtil.stringToLoc(cconf.getString ( name + ".arenalobby" ) , true, true);
                
                if (arenaLobby!=null) {
                    
                     Arena arena = new Arena ( name, 
                             LocationUtil.stringToLoc ( cconf.getString ( name + ".zeropoint" ), false, true ),
                             arenaLobby, 
                             cconf.getString(name + ".mode" ),
                             cconf.getInt( name + ".size_x" ),
                             cconf.getInt( name + ".size_z" ),
                             cconf.getInt( name + ".down_id" ),
                             cconf.getInt( name + ".show" ),
                             cconf.getInt( name + ".difficulty" ),
                             cconf.getInt( name + ".round" ),
                             cconf.getInt( name + ".minPlayers" ),
                             cconf.getInt( name + ".playersForForcestart" )
                     );
                    arenas.put(name,arena);
                    MG.arenas.put(name, arena);
                }
                
            });
                
            
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
    
        
    public static void saveAll() {
        Arena arena;

        for (Entry <String, Arena> e : AM.getAllArenas().entrySet()) {

            arena = e.getValue();

            customConfig.set( "Arenas." + arena.arenaName+ ".zeropoint" , LocationUtil.toString(arena.zero) );
            customConfig.set( "Arenas." + arena.arenaName+ ".arenalobby" , LocationUtil.toDirString(arena.arenaLobby) );
           // customConfig.set( "Arenas." + arena.getName()+ ".mode", arena.getMode());
            customConfig.set( "Arenas." + arena.arenaName+ ".size_x", arena.size_x);
            customConfig.set( "Arenas." + arena.arenaName+ ".size_z", arena.size_z);
            //customConfig.set( "Arenas." + arena.arenaName+ ".down_id", arena.getDownId());
            customConfig.set( "Arenas." + arena.arenaName+ ".show", arena.show);
            customConfig.set( "Arenas." + arena.arenaName+ ".difficulty", arena.difficulty);
            customConfig.set( "Arenas." + arena.arenaName+ ".round", arena.maxRound);
            //customConfig.set( "Arenas." + arena.getName()+ ".minPlayers", arena.getMinPlayers());
           // customConfig.set( "Arenas." + arena.getName()+ ".playersForForcestart", arena.getForce());

        }

        saveCustomYml(customConfig, customYml);
    }
    

    


    public static void createArena( String name, Location player_pos, String mode, byte size_x, byte size_z, byte down  ) {

                                // имя  коорд.угла  коорд.лобби   матер.  размер x * z4 id низ  показ   сложность  раунды  мин.игроков  быстр.старт
        Arena arena = new Arena ( name, player_pos, player_pos,   mode,  size_x, size_z, down,  0, 0, 0,   0,    0 );
        arenas.put(name,arena);
        MG.arenas.put(name, arena);
    }




    public static boolean CanCreate ( Player p) {
        boolean can=true;
        for (Entry <String, Arena> e : arenas.entrySet()) {
            if (e.getValue().zero.getWorld().getName().equals(p.getWorld().getName())) can=false;
        } 
        return can;
    }


    public static HashMap<String,Arena> getAllArenas() { 
        return arenas;
    }
    

    public static Arena getArena(String arenaName) {
        return arenas.get(arenaName);
    }
     
     
    public static boolean ArenaExist (String s) {
        return arenas.containsKey(s);
    }
    
 
    public static void startArenaByName(Player p, String s) {
        Arena arena = AM.getArena(s);
        arena.ForceStart(p);
    }

    public static Arena getArenaByWorld(String w) {
        for (Entry <String, Arena> e : arenas.entrySet()) {
            if ( e.getValue().zero.getWorld().getName().equals(w)) return e.getValue();
        }
      return null;
    }
    
    
    public static void ResetArena(String s, Player p) {
        if (AM.getArena(s) != null)  {
            AM.getArena(s).resetGame();
            p.sendMessage("Arena "+s+" reset succes!");
        }
    }

    public static void DeleteArena(String arenaName, Player p) {
        final Arena a = arenas.remove(arenaName);
        if (a != null)  {
            a.resetGame();
            a.ResetFloor();
            if (customConfig.getConfigurationSection("Arenas") != null) {
                customConfig.set("Arenas." + arenaName,  null);
                saveCustomYml(customConfig, customYml);
            }
            MG.arenas.remove(arenaName);
            p.sendMessage("Arena "+arenaName+" delete succes!");
        }
    }


    public static void stopAllArena() {
        arenas.entrySet().stream().forEach((e) -> {
            e.getValue().resetGame();
        });
    }
   
    

    

    public static Arena getArena(Player p) {
        for (Arena a : arenas.values()) {
            if (a.hasPlayer(p)) return a;
        }
        return null;
    }


    public static void setArenaLobby(Location location, String s) {
        Arena arena = AM.getArena(s);
        arena.arenaLobby = location;
    }
    

    
     public static void saveCustomYml(FileConfiguration fileconfiguration, File file) {
        try {
            fileconfiguration.save(file);
        } catch (IOException ioexception) {
           // ioexception.printStackTrace();
        }

    }    
    
    
}
