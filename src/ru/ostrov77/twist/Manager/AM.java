package ru.ostrov77.twist.Manager;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.komiss77.enums.GameState;
import ru.ostrov77.twist.Objects.Arena;






public class AM {

    public static HashMap <String, Arena> arenas;
    //public static ItemStack bonus;
    public static ItemStack no_mat;
    
    
    
    
    
    
    public static void Init() {
        arenas = new HashMap();
        Files.load_arenas();
    
        //bonus = new ItemStack(Material.SUNFLOWER, 1 );
        
        no_mat = new ItemStack ( Material.STONE_BUTTON, 1);
        ItemMeta m = no_mat.getItemMeta();
        m.setDisplayName(  "§8<<<" );
        no_mat.setItemMeta(m);
        //Set_name(bonus, "§6Бонус");
        //manager = ProtocolLibrary.getProtocolManager(); 
    }
   
    






    public static void createArena( String name, Location player_pos, String mode, byte size_x, byte size_z, byte down  ) {

                                // имя  коорд.угла  коорд.лобби   матер.  размер x * z4 id низ  показ   сложность  раунды  мин.игроков  быстр.старт
        Arena arena = new Arena ( name, player_pos, player_pos,   mode,  size_x, size_z, down,  (byte)0, (byte)0, (byte)0,   (byte)0,    (byte)0 );
        arenas.put(name,arena);

    }


    
    public static void LoadArena(String name, Location zero, Location arenaLobby, String mode, byte size_x, byte size_z, byte down,  byte show, byte diff, byte round, byte minpl, byte force ) {
                                // имя  коорд.угла  коорд.лобби   матер.  размер x * z4 id низ показ сложность  раунды  мин.игроков  быстр.старт
        Arena arena = new Arena ( name,  zero,       arenaLobby,   mode,  size_x, size_z, down, show, diff,    round,    minpl,       force );
        arenas.put(name,arena);
//System.out.println("+++++ LoadArena"+name+" arena="+arena);
        
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
    

    public static Arena getArena(String s) {
         if (  !arenas.containsKey(s) ) return null;
         else return arenas.get(s);
    }
     
     
    public static boolean ArenaExist (String s) {
        return arenas.containsKey(s);
    }
    
 
    public static void startArenaByName(Player p, String s) {
        Arena arena = getArena(s);
        arena.ForceStart(p);
    }

    public static Arena getArenaByWorld(String w) {
        for (Entry <String, Arena> e : arenas.entrySet()) {
            if ( e.getValue().zero.getWorld().getName().equals(w)) return e.getValue();
        }
      return null;
    }
    
    
    public static void ResetArena(String s, Player p) {
        if (getArena(s) != null)  {
            getArena(s).resetGame();
            p.sendMessage("Arena "+s+" reset succes!");
        }
    }

    public static void DeleteArena(String s, Player p) {
        if (getArena(s) != null)  {
            getArena(s).stopShedulers();
            getArena(s).ResetFloor();
            Files.Delete(s);
            arenas.remove(s);
            p.sendMessage("Arena "+s+" delete succes!");
        }
    }


    public static void stopAllArena() {
        arenas.entrySet().stream().forEach((e) -> {
            e.getValue().resetGame();
        });
    }
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void tryJoin(Player player, String arenaName) {
    
        Arena arena = getArena(arenaName);

        if (arena == null)  {
            player.sendMessage("§cНет арены с названием "+arenaName);
            return;
        }
        if (getPlayersArena(player)!=null) {
            player.sendMessage("§cВы уже на арене !");
            return;
        }
        if (arena.state == GameState.ОЖИДАНИЕ || arena.state == GameState.СТАРТ ) {
            //player.sendMessage(Messages.GetMsg("arena_ingame"));
            arena.addPlayers(player);
        } else {
            arena.spectate(player);
        }
        //else if (arena.IsInGame(player))   player.sendMessage(Messages.GetMsg("you_ingame"));
            
                  //  else  arena.addPlayers(player);
                    
    }

    
 
    
    public static boolean isInGame(Player p) {
        return arenas.entrySet().stream().anyMatch((e) -> (e.getValue().IsInGame(p)));
    }

  //  public static boolean isLooser(String nik) {
 //       return arenas.entrySet().stream().anyMatch((e) -> (e.getValue().IsLooser(nik)));
 //   }

  //  public static boolean isLooserLock(Player p) {
  //      return arenas.entrySet().stream().anyMatch((e) -> (e.getValue().IsLooserLock(p)));
 //   }


    
    
    
    
    public static void GlobalPlayerExit(Player p ) {
        arenas.values().stream().forEach( (a) -> {  a.PlayerExit(p); });
    }



    public static Arena getPlayersArena(Player p) {
        for (Arena a : arenas.values()) {
            if (a.IsInGame(p)) return a;
        }
        return null;
    }

    

    
    
      
    
    
    
    
 
    
    
    
    
    
    
    
    
 
    
    


 



    public static void setArenaLobby(Location location, String s) {
        Arena arena = getArena(s);
        arena.setLobby(location);
    }
    


  /*  
    
    
    public static void setItemName(final ItemStack item, final String name) {
        final ItemMeta m = item.getItemMeta();
        m.setDisplayName(name);
        item.setItemMeta(m);
    }

    public static void Set_name (ItemStack is, String name) {
        ItemMeta m = is.getItemMeta();
        m.setDisplayName(name);
        is.setItemMeta( m );
        }

    public static ItemStack Set_lore (ItemStack is, String lore1, String lore2, String lore3, String lore4 ) {
        ItemMeta m = is.getItemMeta();
        m.setLore(Arrays.asList( lore1, lore2, lore3, lore4 ) );
        is.setItemMeta( m );
        return is;
        }

    public  static void Add_lore(ItemStack itemstack, String s) {
        ItemMeta itemmeta = itemstack.getItemMeta();
        List lores = new ArrayList();
        if (itemmeta.getLore() != null) {
            lores = itemmeta.getLore();
        }
        lores.add(s);
        itemmeta.setLore(lores);
        itemstack.setItemMeta(itemmeta);
        }

    public  static void Set_lore(ItemStack itemstack, String lore0) {
        ItemMeta itemmeta = itemstack.getItemMeta();
        itemmeta.setLore(Arrays.asList( lore0 ));
        itemstack.setItemMeta(itemmeta);
        }

*/
    }
