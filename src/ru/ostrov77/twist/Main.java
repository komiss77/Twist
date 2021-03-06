package ru.ostrov77.twist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.GameState;
import ru.ostrov77.twist.Manager.AM;
import ru.ostrov77.twist.Manager.Commands;
import ru.ostrov77.twist.Manager.Files;
import ru.ostrov77.twist.Manager.Messages;
import ru.ostrov77.twist.Manager.ScoreBoard;




public class Main extends JavaPlugin implements Listener {

    public static Main instance;
    public static String Prefix="§5Твист: §f";
    
    public static boolean shutdown = false;
    
    public static boolean noteblock = false;
    

        
    public static ArrayList<DyeColor> allowedColors = new ArrayList<>(Arrays.asList( DyeColor.BLACK ));
    public static HashMap <String, Long> cooldown = new HashMap();
    
    
         
        
        

       
@Override
	public void onEnable() {
            instance = this;  
               
            MainConfig.Load();
           // Signs.Load();
            Messages.Load();

        Prefix = Messages.GetMsg("prefix");

        if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {noteblock=true; log("NoteBlockAPI found! Enable music for arena. Please, put .nbt songs in plugins/Twist/songs folser!");}

        
        AM.Init();
        ScoreBoard.StartScore();
        //if (bsign) AM.MySign();
                

                
        Bukkit.getServer().getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new UniversalListener(this), this);
        //Bukkit.getServer().getPluginManager().registerEvents(new Signs(), this);

        Bukkit.getLogger().info("Twist ready!");
        
	}

        
       
        
        
        
        
@Override
	public void onDisable() {
             shutdown = true;
  
////////////////////////////////////////////////////////////////////////////////
            AM.getAllArenas().keySet().stream().forEach((arenaName) -> {
            ApiOstrov.sendArenaData(
                    arenaName,
                    GameState.ВЫКЛЮЧЕНА,
                    "§4█████████",
                    "§2§l§oTWIST",
                    "§cПерезапуск...",
                    "§4█████████",
                    "",
                    0
            );
              /*  Bsign.Send_data(true, false, 0, ar,
                        "§4█████████",
                        "§2§l§oTWIST",
                        "§c§lПерезапуск...",
                        "§4█████████",
                        (short)14
                );*/
            });
                
////////////////////////////////////////////////////////////////////////////////
            
            //this.saveConfig();
            Files.saveAll();
            AM.stopAllArena();
	}




        
        
@Override
    public boolean onCommand(CommandSender cs, Command comm, String s, String[] arg) {
            return Commands.handleCommand(cs, comm, s, arg);
    }

    
    
    
    
    public static void sendBsignMysql(final String name, final String line2, final String line3, final GameState state) {
        ////////////////////////////////////////////////////////////////////////////////
            ApiOstrov.sendArenaData(
                    name,
                    state,
                    "§2§l§oTWIST",
                    "§5"+name,
                    line2,
                    line3,
                    "",
                    0
            );
        ////////////////////////////////////////////////////////////////////////////////

    }
    public static void sendBsignChanel(final String name, final int players, final String line2, final String line3, final GameState state) {
        ////////////////////////////////////////////////////////////////////////////////
            ApiOstrov.sendArenaData(
                    name,
                    state,
                    "§2§l§oTWIST",
                    "§5"+name,
                    line2,
                    line3,
                    "",
                    players
            );
        ////////////////////////////////////////////////////////////////////////////////

    }

    
    

    
        
public static void log(String s) { Bukkit.getConsoleSender().sendMessage(Prefix + s);}        
        
public static final Main GetInstance() {
        return Main.instance;
    }  




        
        
        
        
       
        
        
        
        
public static boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }    




public static boolean hasCooldown(String nik) {
    
	if(cooldown.containsKey(nik)) {
            
		if( System.currentTimeMillis() - cooldown.get(nik) >  3000 ) {
		    cooldown.remove (nik);
			return false;
		} else {return true;}
        } 
            return false;
}
		
	
public static void addCooldown(String nik) {
	if(!cooldown.containsKey(nik)) {
		cooldown.put(nik, System.currentTimeMillis());
	}
}
        
        
        
   
       /*


public static String[]  DyeToString ( byte num ) {
    
        String[] c =  {"",""};
        
     switch (num) {
        case 0: c[0]="§f"; c[1]=Messages.GetMsg("wt"); break;    //+++бел
        case 1: c[0]="§6"; c[1]=Messages.GetMsg("or"); break;
        case 2: c[0]="§d"; c[1]=Messages.GetMsg("pu"); break;
        case 3: c[0]="§9"; c[1]=Messages.GetMsg("bl"); break;
        case 4: c[0]="§e"; c[1]=Messages.GetMsg("ye"); break;
        case 5: c[0]="§a"; c[1]=Messages.GetMsg("la"); break;
        case 6: c[0]="§c"; c[1]=Messages.GetMsg("ro"); break;
        case 7: c[0]="§8"; c[1]=Messages.GetMsg("dg"); break;
        case 8: c[0]="§7"; c[1]=Messages.GetMsg("gr"); break;
        case 9: c[0]="§3"; c[1]=Messages.GetMsg("aq");
        case 10: c[0]="§d"; c[1]=Messages.GetMsg("dp");
        case 11: c[0]="§1"; c[1]=Messages.GetMsg("db"); break;
        case 12: c[0]="§6"; c[1]=Messages.GetMsg("br"); break;
        case 13: c[0]="§2"; c[1]=Messages.GetMsg("gn"); break;
        case 14: c[0]="§4"; c[1]=Messages.GetMsg("ba"); break;
        case 15: c[0]="§0"; c[1]=Messages.GetMsg("bk"); break;
            default: c[0]="§f"; c[1]=Messages.GetMsg("wt"); break;
    }
    return  c;
} 
    */
    
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
    



public static String getTime(final long n) {
    
        final long sec = TimeUnit.SECONDS.toSeconds(n) - TimeUnit.SECONDS.toMinutes(n) * 60L;
        final long min = TimeUnit.SECONDS.toMinutes(n) - TimeUnit.SECONDS.toHours(n) * 60L;

       // return  ( n2>0 ? n+":"+n2 : "00:"+n );
       return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }




  


}
