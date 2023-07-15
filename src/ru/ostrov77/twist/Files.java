package ru.ostrov77.twist;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import ru.komiss77.utils.LocationUtil;
import ru.ostrov77.twist.Main;
import ru.ostrov77.twist.Arena;

public class Files {

    static Plugin plugin = Main.getPlugin(Main.class);
    static File customYml = new File( Files.plugin.getDataFolder() + "/arenas.yml" );
    static FileConfiguration customConfig = YamlConfiguration.loadConfiguration(Files.customYml);
    
    
    
    
    
    
    


    
    
    
    
    
    
    

    public static void load_arenas() {
        try {
            Files.customConfig.options().copyDefaults(true);
            Files.plugin.getConfig().options().copyDefaults(true);
            //Files.plugin.saveConfig();
            if (Files.customConfig.getConfigurationSection("Arenas") ==null)    return;
            
            ConfigurationSection cconf = Files.customConfig.getConfigurationSection("Arenas");
            
            cconf.getKeys(false).stream().forEach( (name) -> {
                final Location arenaLobby = LocationUtil.LocFromString(cconf.getString ( name + ".arenalobby" ) , true);
//System.out.println("+++++"+name+" arenaLobby="+arenaLobby+" !=null?"+arenaLobby!=null);
                
                if (arenaLobby!=null) {
//System.out.println("------------------------------");
                    
                    AM.LoadArena(
                            name, 
                            LocationUtil.LocFromString ( cconf.getString ( name + ".zeropoint" ) ),
                            arenaLobby,
                            cconf.getString(name + ".mode" ),
                            (byte) cconf.getInt( name + ".size_x" ),
                            (byte) cconf.getInt( name + ".size_z" ),
                            (byte) cconf.getInt( name + ".down_id" ),
                            (byte) cconf.getInt( name + ".show" ),
                            (byte) cconf.getInt( name + ".difficulty" ),
                            (byte) cconf.getInt( name + ".round" ),
                            (byte) cconf.getInt( name + ".minPlayers" ),
                            (byte) cconf.getInt( name + ".playersForForcestart" )
                    );
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
                
                Files.customConfig.set( "Arenas." + arena.getName()+ ".zeropoint" , LocationUtil.toString(arena.getZero()) );
                Files.customConfig.set( "Arenas." + arena.getName()+ ".arenalobby" , LocationUtil.toDirString(arena.getLobby()) );
                Files.customConfig.set( "Arenas." + arena.getName()+ ".mode", arena.getMode());
                Files.customConfig.set( "Arenas." + arena.getName()+ ".size_x", arena.getSize_x());
                Files.customConfig.set( "Arenas." + arena.getName()+ ".size_z", arena.getSize_z());
                Files.customConfig.set( "Arenas." + arena.getName()+ ".down_id", arena.getDownId());
                Files.customConfig.set( "Arenas." + arena.getName()+ ".show", arena.getShow());
                Files.customConfig.set( "Arenas." + arena.getName()+ ".difficulty", arena.getDifficulty());
                Files.customConfig.set( "Arenas." + arena.getName()+ ".round", arena.getMaxRound());
                Files.customConfig.set( "Arenas." + arena.getName()+ ".minPlayers", arena.getMinPlayers());
                Files.customConfig.set( "Arenas." + arena.getName()+ ".playersForForcestart", arena.getForce());
               
            }
 

            //Files.plugin.saveConfig();
            //MainConfig.saveConfig();
            saveCustomYml(Files.customConfig, Files.customYml);
    }
    
    
    
    
 
    
    public static void Delete( String name ) {

        if (Files.customConfig.getConfigurationSection("Arenas") == null)    return;
        
        Files.customConfig.set("Arenas." + name,  null);
        saveCustomYml(Files.customConfig, Files.customYml);
        
    }
    
    
    
    
    
    /*
     public static Location stringToLoc(String s) {
        if (s != null && !s.trim().equals("")) {
            String[] astring = s.split("<>");

            if (astring.length == 4) {
                World world = Bukkit.getServer().getWorld(astring[0]);
                Double d = Double.parseDouble(astring[1]);
                Double double1 = Double.parseDouble(astring[2]);
                Double double2 = Double.parseDouble(astring[3]);

                return new Location(world, d, double1, double2);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
     
     
    public static String locToString(Location location) {
        return location == null ? "" : location.getWorld().getName() + "<>" + location.getBlockX() + "<>" + location.getBlockY() + "<>" + location.getBlockZ();
    }

   */
    
     public static void saveCustomYml(FileConfiguration fileconfiguration, File file) {
        try {
            fileconfiguration.save(file);
        } catch (IOException ioexception) {
           // ioexception.printStackTrace();
        }

    }
   
    
    
    
    
    
}
