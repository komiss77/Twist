package ru.ostrov77.twist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.bukkit.DyeColor;







public class MainConfig {


    private static File file; 
    private static CustomConfiguration config; 
    
    
    
// ----------------------   переменные    -----------------
    
    public static String cmd = "";
    public static int money_reward = 300;
    public static float speed = 0.1F;
    public static String pack_url = "";
        

// ---------------------------------------------------------       

        
        
        
        
public static void Load () {
    
       file = new File(Main.instance.getDataFolder(), "twist.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            copy(Main.instance.getResource("twist.yml"), file);
            System.out.println("----------> Config file doesn't exist yet.");
            System.out.println("----------> Creating Config File and loading it.");
        }
    
    config = CustomConfiguration.loadConfiguration(file);


    
    
    //config.addDefault("#----- if use Vault, money_reward wil be deposit to player balance ----- ",0);
    config.addDefault("money_reward", 300);
    config.addDefault("resoucepack_url", "http://ostrov77.ru/uploads/resourcepacks/twist.zip");
    
    config.addDefault("walk_speed", "0.3F");
    //config.addDefault("#----- if not use Vault, colsole dispatch this comand for every winer ----- ",0);
    config.addDefault("console_command_reward_if_not_vault", "say %p you win!");
    //config.addDefault("#----- Be careful! This is byte color! From 0 to 15! ----- ",0);
    config.addDefault("allowed_colors", Arrays.asList( 0, 1, 2, 4, 5, 6, 7, 9, 11, 12, 13, 14, 15 ) );

    
    
    
    
    
    
    
    saveConfig();
                
    LoadConfigVars();
                        
                        
    }    
    
    
 




public static void LoadConfigVars() {
            

    money_reward = config.getInt("money_reward");
    speed = Float.valueOf(config.getString("walk_speed"));
    cmd = config.getString("console_command_reward_if_not_vault");
    pack_url = config.getString("resoucepack_url");
    
    //if (config.getStringList("allowed_colors").size()>1) {
        Main.allowedColors.clear();
//System.out.println(" --------- LoadConfigVars 1");
        
        for (String s : config.getStringList("allowed_colors")) {
//System.out.println(" --------- LoadConfigVars s="+s);
            for (DyeColor dc : DyeColor.values()) {
                if (dc.toString().equalsIgnoreCase(s)) Main.allowedColors.add(dc);
            }
        }
//System.out.println(" --------- allowedColors="+Main.allowedColors);
        
        //Main.allowedColors.addAll(config.getByteList("allowed_colors"));
        
    //}
    
                

    }
   
    
    
    
    
    
    
    
private static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);

            out.close();
            in.close();
        } catch (Exception e) {
           // e.printStackTrace();
        }
    }

    
public static void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            //e.printStackTrace();
        }
     }
    
    
    
    
}
