package ru.ostrov77.twist;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.Listener;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.Game;
import ru.komiss77.enums.GameState;
import ru.komiss77.events.FigureActivateEntityEvent;
import ru.komiss77.events.FigureClickEvent;
import ru.komiss77.modules.games.GM;
import ru.komiss77.modules.world.Cuboid;
import ru.komiss77.objects.Figure;
import ru.komiss77.utils.TCUtil;


public class TwistLst implements Listener {
    
    
    private static Figure figure;
    private static final Cuboid cuboid;
    private static BukkitTask task;
    private static World world;
    private static final List <Location> locs;
    private static final List <BlockData> bds;
    
    static {
        cuboid = new Cuboid (2, 2, 2);
        locs = new ArrayList<>();
        bds = new ArrayList<>();
        Material mat;
        for (DyeColor dc : DyeColor.values()) {
            mat = TCUtil.changeColor(Material.WHITE_WOOL, dc);
            bds.add(mat.createBlockData());
        }
    }
    
    @EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFigureActivateEntity (final FigureActivateEntityEvent e) {
        if (e.getFigure().getTag().equals("twist")) {
            figure = e.getFigure();
            world = figure.entity.getWorld();
            cuboid.allign(figure.spawnLoc);
            
            if (task!=null) {
                task.cancel();
                task = null;
                locs.clear();
            }
            
            final Location loc = figure.spawnLoc.clone().subtract(0, 1, 0);
            locs.add(loc);
            for (BlockFace bf : BlockFace.values()) {
                if (bf.getModY()==0) {
                    locs.add(loc.getBlock().getRelative(bf).getLocation());
                }
            }
            
            task=new BukkitRunnable() {
                int s=0;
                
                
                @Override
                public void run() {

                    if (figure==null || figure.entity == null || figure.entity.isDead() || !figure.entity.isValid()) {
                        //this.cancel();
                        //Ostrov.log_err("Пандора: фигура потеряна!");
                        return;
                    }
                    
                    for (final Player p : world.getPlayers()) {
                        if (cuboid.contains(p.getLocation())) {
                            GM.randomPlay(p, Game.TW, Ostrov.MOT_D);
                            continue;
                        }
                        for (final Location l : locs) {
                            p.sendBlockChange(l, bds.get(Ostrov.random.nextInt(bds.size() - 1)));
                        }
                    }
                    //if (as==null || !as.isValid() || as.isDead()) {
                   //     //this.cancel();
                    //    if (tick%200==0) Ostrov.log_warn("Пандора: стойка потеряна!");
                    //    as = (ArmorStand) figure.getEntity(); //перепроверим на след.тике
                    //    return;
                   // }
                    //if (s%10==0) {
                        figure.setDisplayName(TCUtil.randomColor() + "ТВИСТ");
                    //}

                    //if (tick%30==0) {
                    //    if (helmet==null) {
                    //        helmet = new ItemStack(head.get(0));
                    //    }
                    //    helmet.setType(head.get(ApiOstrov.randInt(0, 15)));
                    //    as.getEquipment().setHelmet(helmet);
                    //}
                    //if (tick%200==0) {
                    //    Sound sound=Sound.values()[ApiOstrov.randInt(0,  Sound.values().length-1)];
                   //     if( !sound.toString().startsWith("MUSIC_") ) {
                    //         as.getWorld().playSound(as.getLocation(), sound, 0.3F, 2);
                   //     }
                   // }
                   s++;

                }
            }.runTaskTimer(Ostrov.instance, 1, 8);
                    
        }
    }

    
    @EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFigureClick (final FigureClickEvent e) {
        if (e.getFigure().getTag().equals("twist")) {
            e.getPlayer().sendMessage("§bПодходи ближе, поиграем!");
        }
    }     
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player p = (Player) e.getEntity();
            final Arena arena = AM.getArena(p);
            if (arena != null) {
                e.setDamage(0);
                e.setCancelled(true);
            }
        }
    }
    
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemMerge (final ItemMergeEvent e) {
        Arena a = AM.getArenaByWorld(e.getEntity().getWorld().getName());
        if (a!=null && a.state != GameState.ОЖИДАНИЕ) {
             e.setCancelled(true);
        }
    }   
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;
        final Player p = (Player) e.getEntity();
        final Arena arena = AM.getArena(p);

        if (arena != null) {
            e.setDamage(0);
            p.setFallDistance(0);

            switch (arena.state) {
                
                case ОЖИДАНИЕ, СТАРТ ->
                    p.teleport(arena.arenaLobby);
                    
                case ЭКИПИРОВКА ->
                    p.teleport(arena.randomFielldLoc()); //
                    
                case ИГРА -> {
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID || e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        arena.fall(p);
                       if (arena.zero.getBlockY() - p.getLocation().getBlockY() > 5) {
                           p.teleport(arena.randomFielldLoc());
                       } 
                    }
                }
                case ФИНИШ ->
                    p.teleport(arena.randomFielldLoc());
                    
                default ->
                    p.teleport(p.getWorld().getSpawnLocation());
            }
        }

    }

    
    /*
    @EventHandler(priority = EventPriority.HIGH)
    public void onOpenInv (InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();

            if ( AM.isInGame(player) ) {
                e.setCancelled(true);
                player.closeInventory();
                player.sendMessage("Инвентарь заблокирован!");
            }

    }*/


    @EventHandler(ignoreCancelled = true)
    public void monetPickup(EntityPickupItemEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player p = (Player) e.getEntity();
        final Arena arena = AM.getArena(p);
        if (arena==null) return;
        final Item i = e.getItem();
        e.setCancelled(true);
        
        switch (i.getItemStack().getType()) {

            case SUNFLOWER -> {
                if (i.isGlowing()) return;
                i.setVelocity(new Vector(0, 1.0, 0));
                Ostrov.sync( ()->i.remove(), 5);
                i.setGlowing(true);
                i.getWorld().playSound(i.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                i.getWorld().playEffect(i.getLocation(), Effect.BOW_FIRE, 5);
                p.setLevel(p.getLevel() + 1);

            }

            default -> {
                i.remove();
                i.getWorld().playSound(i.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 9.9F);
            }

        }

    }



    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }


    @EventHandler
    public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }

}
