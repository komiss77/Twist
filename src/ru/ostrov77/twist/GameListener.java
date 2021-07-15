package ru.ostrov77.twist;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import ru.ostrov77.twist.Manager.AM;






public class GameListener implements Listener {
 
    
  /*  
@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        
        JoinPlayer(e.getPlayer());
        e.getPlayer().teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        
    }
    
 
        


    
    
    
    public void JoinPlayer (final Player p) {
        if (!p.isOp()) p.setGameMode(GameMode.ADVENTURE);
        p.setFireTicks(0);
        p.getInventory().clear();
        p.setFlying(false);
        p.setAllowFlight(false);
        p.setFoodLevel(20);
        p.setHealth(20);
        p.setLevel(0);
        p.setExp(0);
        p.setWalkSpeed(MainConfig.speed);
        p.getInventory().clear();
        //if (Main.ostrov) Ostrov.Give_pipboy(p);
        ItemStack is = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName("§4§lВ лобби");
        is.setItemMeta(meta);
        p.getInventory().setItem(0, is);
        p.updateInventory();
    }

    
    
    
    
@EventHandler(priority = EventPriority.MONITOR)
    public void PlayerQuitEvent (PlayerQuitEvent e) {
        AM.GlobalPlayerExit(e.getPlayer());
    }
        

        
        
        
        
        
@EventHandler
    public void onInteraction(PlayerInteractEvent e) {
                   
        if ( e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK ){
            
            if (AM.isInGame(e.getPlayer())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("Во время игры недоступно!");
                return;
            }
                  
            switch (e.getMaterial()) {
                
                case SLIME_BALL:
                    e.setCancelled(true);
                    if (AM.isLooser(e.getPlayer().getName())) {
                        e.getPlayer().teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
                    } else {
                        AM.GlobalPlayerExit (e.getPlayer());
                    }
                        //Arenas.GlobalPlayerExit (e.getPlayer());
                    break;
                    
                case BARRIER:
                    e.setCancelled(true);
                    ApiOstrov.sendToServer( e.getPlayer(), "lobby0", "");
                    break;
                    
                default:
                    break;
                
            }      
        

        }
    }*/
               


    @EventHandler ( priority = EventPriority.HIGH )
    public void EntityDamageByEntityEvent (EntityDamageByEntityEvent e){
        if(! (e.getEntity() instanceof Player) ) return;
        Player p = (Player) e.getEntity();
        if (AM.getPlayersArena(p)==null) return;
        
            if ( e.getDamager().getType() == EntityType.ZOMBIFIED_PIGLIN) {
                e.setDamage( e.getDamage()/1.5);                                                    //чтобы мучался дольше
        
                        if ( p.getHealth() - e.getDamage() <= 0 ) {                              // если  гибель 
                            e.setCancelled(true);
                            e.setDamage(0);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1));
                            UniversalListener.spectatorPrepare(p);
                            //JoinPlayer(p);
                            if ( AM.getPlayersArena(p)!= null) {
                                AM.getPlayersArena(p).PlayerExit(p);
                            }
                             //   if ( AM.getArenaByWorld( p.getWorld().getName())!= null && !AM.getArenaByWorld( p.getWorld().getName()).IsJonable() )   //если на арене игрока еще игра, в лобби игры, если нет-в глобальное лобби
                            //        p.teleport(AM.getArenaByWorld( p.getWorld().getName()).getLobby());
                                
                           //     else p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
                        }
                        
                        
            } else if ( e.getDamager() instanceof Player) {
                e.setDamage(0);
                e.setCancelled(true);                                               
            }

    }
    
    
     
    
    
    @EventHandler
        public void onEntityDamage(EntityDamageEvent e) { 
        
            if ( !(e.getEntity() instanceof Player) ) { 
                e.setCancelled(true);
                return; 
            }
            
            final Player p = (Player) e.getEntity();
            if (AM.getPlayersArena(p)==null) return;
            
            switch (e.getCause()) {
                
                case VOID:
                    if ( AM.isInGame(p) )  {
                        AM.GlobalPlayerExit(p);  //если где-то играет
                    } else { 
                        e.setDamage(0);
                        p.setFallDistance(0);
                        p.teleport(p.getWorld().getSpawnLocation()); 
                    }
                    break;
                    
                    
                case FALL:
                    e.setDamage(0);
                    break;
                    
                case ENTITY_ATTACK:
                    if ( e.getEntityType() == EntityType.ZOMBIFIED_PIGLIN ) {  //напали на хрюшу
                        e.setCancelled(true);
                        e.setDamage(0);
                    }  
                    break;
                    
                default:
                    e.setCancelled(true);
                    break;
            }
    }      
  

        
        
     /*   
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void ArenaJoin(final BungeeDataRecieved e) {  
        //final Oplayer op = ru.komiss77.Managers.PM.getOplayer(e.getPlayer().getName());
//System.out.println( "ArenaJoinEvent ->"+e.getOplayer().getBungeeData(Data.WANT_ARENA_JOIN) );
        new BukkitRunnable() {
            @Override
            public void run() {
                AM.addPlayer(e.getPlayer(), e.getOplayer().getDataString(Data.WANT_ARENA_JOIN));
            }
        }.runTaskLater(Main.instance, 10);

    }

        
    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBsignLocalArenaClick (final BsignLocalArenaClick e) {
//System.out.println(" ---- BsignLocalArenaClick --- "+e.player.getName()+" "+e.arenaName);
         AM.addPlayer(e.player, e.arenaName );
    }
    */    
        
        
    @EventHandler
    public void onPlayerMove (PlayerMoveEvent e) { 
        
        if (e.getFrom().getBlockX()==e.getTo().getBlockX() && e.getFrom().getBlockY()==e.getTo().getBlockY() && e.getFrom().getBlockZ()==e.getTo().getBlockZ()  ) return;

             if ( AM.isInGame(e.getPlayer()) ) {
//System.out.println("fllor:"+ AM.getPlayersArena(e.getPlayer()).removeFloor+"     looser:"+AM.getPlayersArena(e.getPlayer()).looser.contains(e.getPlayer().getName())+
 //       "   d:"+(AM.getPlayersArena(e.getPlayer()).zero.getBlockY()-e.getTo().getBlockY()));
                 
                  if ( AM.getPlayersArena(e.getPlayer()).zero.getBlockY()-e.getTo().getBlockY() >=3 ) {
                      AM.getPlayersArena(e.getPlayer()).Loose(e.getPlayer());
                    //  return;
                  }
//System.out.println("rem_floor:"+AM.getPlayersArena(e.getPlayer()).removeFloor+"   Y:"+e.getTo().getBlockY()+ "   block_from-1:"+e.getFrom().clone().subtract(0, 1, 0).getBlock().getType()+ "   block_from-2:"+e.getFrom().clone().subtract(0, 2, 0).getBlock().getType() + "   block_to-1:"+e.getTo().clone().subtract(0, 1, 0).getBlock().getType()+ "   block_to-2:"+e.getTo().clone().subtract(0, 2, 0).getBlock().getType() );            
//                  if (  AM.getPlayersArena(e.getPlayer()).removeFloor && 
//                          e.getFrom().clone().subtract(0, 1, 0).getBlock().getType()==Material.AIR && e.getFrom().clone().subtract(0, 2, 0).getBlock().getType()==Material.AIR &&
//                                    e.getTo().clone().subtract(0, 1, 0).getBlock().getType()==Material.AIR && e.getTo().clone().subtract(0, 2, 0).getBlock().getType()==Material.AIR ) {
//                                        AM.getPlayersArena(e.getPlayer()).Loose(e.getPlayer());
//                                                e.getPlayer().sendMessage("§fчит?");
                                    //return;
//                  }
             
             } else if ( AM.isLooserLock(e.getPlayer()) ) {
                 e.setTo(e.getFrom());
             }  //не давать бегать род полом

    
    }
       
    
/*    
@EventHandler
    public void sss(PlayerInteractEvent e) {
        //Player player = e.getPlayer();
    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        //ItemStack is = AM.bonus.clone();
       // Item item = e.getPlayer().getWorld().dropItem(e.getClickedBlock().getLocation().clone().add( 0 , 2.5, 0 ), is ); 
        //item.setCustomName(item.getUniqueId().toString());
        //item.setCustomNameVisible(false);
       // item.setVelocity(new Vector(0, 0, 0));
System.out.println("!!!spawn");
     //   item.setPickupDelay(1);
     //   item.setGravity(false);
        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), "twist.start", 10, 1);
                }
    
    }    
    
 */   
    
    
    
    @EventHandler (ignoreCancelled = true)
    public void sugarPickupEventSpeed(EntityPickupItemEvent e) {
        if (e.getEntityType()!=EntityType.PLAYER) return;
        Player p = Bukkit.getPlayer(e.getEntity().getName());

        //if (e.getItem().getItemStack()!=null && e.getItem().getItemStack().hasItemMeta() && e.getItem().getItemStack().getItemMeta().)    
            switch (e.getItem().getItemStack().getType()) {
                
                case SUNFLOWER:
//System.out.println("подобрал бонус level:"+e.getPlayer().getLevel());                    
                    e.setCancelled(true);
                    if (e.getItem().isGlowing()) return;
                    Pickup_efect(e.getItem());
                    e.getItem().setGlowing(true);
                    if  ( AM.isInGame(p) ) {
                        //p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 20F, 1F);
                        //e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), "twist.bonus_pickup", 10, 1);
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                        p.getWorld().playEffect(p.getLocation(), Effect.BOW_FIRE, 5);
                        p.setLevel(p.getLevel()+1);
                        
                    }
                    break;
                    
                default:
                    e.setCancelled(true);
                    e.getItem().remove();
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 9.9F);
                    break;
                    
                
            }

    }
        
    private static void Pickup_efect (Item item) {
        item.setVelocity(new Vector(0, 1.0, 0));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
            item.remove();
        }, 5L);


    }

    
    
    
    
    @EventHandler
    public void onHostileSpawn(CreatureSpawnEvent e) {
        
        if ( e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM ) {
            e.setCancelled(true);
        }

    }
   

    
    
    /*
    @EventHandler(  priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != null) {
            
            if (AM.isInGame((Player) e.getWhoClicked())) {
                e.setCancelled(true);
            } else if ( e.getCurrentItem().getType()==Material.SLIME_BALL) {
                e.setCancelled(true);
            }
            
        }
    }*/

        
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if ( event.getPlayer().isOp() || AM.getPlayersArena(event.getPlayer())==null) return;
            event.setCancelled(true);
            event.getItemDrop().remove();
            ItemStack droped = event.getPlayer().getItemInHand().clone();
            droped.setAmount(1);
            event.getPlayer().setItemInHand(droped);
            event.getPlayer().updateInventory();
    }     
    
                
   
/*    
@EventHandler
    public void cancelMove(InventoryDragEvent event) {
       event.setCancelled(true);
        ((Player) event.getWhoClicked()).updateInventory();
    }
 */   
    

        
        
        
        
    
    
   /* 
        
@EventHandler
    public void WorldChange (PlayerChangedWorldEvent e) {
        if (e.getPlayer().getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) {
            JoinPlayer (e.getPlayer());
            e.getPlayer().teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        }
    }
*/


    
        
        
        
    
    
    @EventHandler
    public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent event) {
        if (event.getOffHandItem() != null  )  event.setCancelled(true);
    }    
  

    
    
    
    
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
@EventHandler
    public void blockGrow(BlockGrowEvent event) {
         event.setCancelled(true);
    }    
    
    
    @EventHandler
    public void strucGrow(StructureGrowEvent event) {
          event.setCancelled(true);
    }    
    
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
          event.setCancelled(true);
    }    

    
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        boolean rain = event.toWeatherState();
        if(rain)
            event.setCancelled(true);
    }
 
    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        boolean storm = event.toThunderState();
        if(storm)
            event.setCancelled(true);
    } 

    
    @EventHandler
    public void onBlockFade(BlockFadeEvent event)
    {
        if(event.getBlock().getType() == Material.ICE || event.getBlock().getType() == Material.PACKED_ICE || event.getBlock().getType() == Material.SNOW || event.getBlock().getType() == Material.SNOW_BLOCK) 
            event.setCancelled(true);
    }
  

        
    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) { event.setCancelled(true); }

        
        
    @EventHandler
    public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent e){
        if (!e.getPlayer().isOp()) e.setCancelled(true);
    }    
     
  


}
