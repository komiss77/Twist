package ru.ostrov77.twist.Manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.komiss77.ApiOstrov;

import ru.ostrov77.twist.Main;
import ru.ostrov77.twist.Objects.Arena;

/*

public class Signs implements Listener {

    static File signFile = new File( Main.GetInstance().getDataFolder() + "/signs.yml" );
    static FileConfiguration signConfig = YamlConfiguration.loadConfiguration(Signs.signFile);
    
    private static HashMap < String, String > Sign = new HashMap <>(); //координата + назв арены

    
    
    
    

    
    
    
@EventHandler
    public static void SignCreate(SignChangeEvent e) {

        if (!e.getPlayer().isOp()) return;


        if ( e.getLine(0).equalsIgnoreCase("twist") && !e.getLine(1).isEmpty() ) {
            
            if (!e.getBlock().getType().toString().contains("WALL_SIGN")) {
                e.getPlayer().sendMessage( "§cPlease, use the Waal Sign!");
                e.setCancelled(true);
                return;
            }

            if (AM.ArenaExist(e.getLine(1))) {   
            
                Arena arena = AM.getArena(e.getLine(1) );

                Sign.put( StringFromBlock (e.getBlock()) , arena.getName() );
                
                e.setLine(0, "§2§l"+arena.getName());
                e.setLine(1, "§10 " );
                e.setLine(2, "§b§lОжидание..");
                
                        try {
                            signConfig.set("signs." + StringFromBlock (e.getBlock()), arena.getName() );
                            signConfig.save(Signs.signFile);
                        } catch (IOException ex) { Main.log("§4Не удалось сохранить табличку в файл!!"); }
                
            } else {
                e.getPlayer().sendMessage( "Нет такой арены!");
            }
        }

    }

    
    
    
    
    
    
    
@EventHandler
    public void onInteraction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        
                if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                     Block block = e.getClickedBlock();

                    if (block.getState() instanceof Sign) {
                        Sign sign = (Sign) e.getClickedBlock().getState();
                        
                        
                            if (Sign.containsKey( StringFromBlock (sign.getBlock()) ) ) {
                                e.setCancelled(true);
                                    if (Main.hasCooldown(player.getName())) player.sendMessage("§8Подождите 2 сек. до следующего использования таблички!"); 
                                    else {
                                        Main.addCooldown(player.getName());
                                         AM.addPlayer(player, Sign.get( StringFromBlock(sign.getBlock()) ) );
                                    }
                            }
                    }
                }
    
    }    
    
    
    
    

@EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        
            if (e.getBlock().getState() instanceof Sign ) {
                
                if ( !e.getPlayer().isOp() ) {
                    e.setCancelled(true);
                    return;
                }
                
                String loc = StringFromBlock (e.getBlock());
                    if (Sign.containsKey(loc)) {
                       e.getPlayer().sendMessage(Main.Prefix + "§6Табличка арены §b" + Sign.get(loc)+" / " + loc +  "§4 разбита!");
                        Sign.remove(loc);
                        
                        try {
                            signConfig.set("signs." + loc, null);
                            signConfig.save(Signs.signFile);
                        } catch (IOException ex) { Main.log("§4Не удалось удалить табличку из файла!!"); }
                        
                    }
                
            }
    }

    
    
    
    
public static void SignsUpdate(String name, String line1, String line2, String line3) {
    
    
    
           Iterator<String> it = Sign.keySet().iterator();
               while (it.hasNext())
                    {
                        String key = it.next();
                        
                        if (Sign.get(key).equals(name) ) {
                        Block block = BlockFromString (key);
                        
                           //if (block != null && (block.getType().equals(Material.SIGN) || block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN))) {
                           if (block != null && ApiOstrov.isSign(block.getType())) {
                            Sign sign = (Sign) block.getState();
                    
                            sign.setLine(1, line1 );
                            sign.setLine(2, line2 );
                            sign.setLine(3, line3);
                            
                            sign.update(true);
                            }
                        }
                    }
    
                }





public static void Load() {
        try {
                if (Signs.signConfig.getConfigurationSection("signs") !=null)   { 
                    Signs.signConfig.getConfigurationSection("signs").getKeys(false).stream().forEach((s) -> {
                    Sign.put( s, Signs.signConfig.getString("signs." + s) ) ;}); //координата + название арены 
                }
                
                Main.log( "Загружено табличек: "+Sign.size() );

        } catch (NullPointerException e) { Main.log("§4Не удалось загрузить таблички из файла!!");}

    }








public static Block BlockFromString (String l) {
    String[] tmp = l.split("x");
    return Bukkit.getWorlds().get(0).getBlockAt(Integer.valueOf(tmp[0]),Integer.valueOf(tmp[1]),Integer.valueOf(tmp[2]));
}    
    
    
    
public static String StringFromBlock (Block b) {
    return (int) b.getLocation().getX() + "x" + (int) b.getLocation().getY() + "x" + (int) b.getLocation().getZ();
}      



}
*/