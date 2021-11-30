package ru.ostrov77.twist.Objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkSection;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.GameState;
import ru.komiss77.enums.Stat;
import ru.komiss77.utils.ColorUtils;
import ru.komiss77.utils.DonatEffect;
import ru.ostrov77.twist.Main;
import ru.ostrov77.twist.Manager.AM;
import ru.ostrov77.twist.Manager.Messages;
import ru.ostrov77.twist.UniversalListener;
//import ru.ostrov77.twist.Manager.Signs;





public class Arena {

    private String name;
    public Location arenaLobby;
    public Location zero;
    private String mode;    
    private Material mat;    
    private final byte size_x;
    private final byte size_z;
    private final byte down;
    private byte show;
    private byte difficulty;
    private byte maxRound;
    private byte minPlayers;
    private byte playersForForcestart;
    
    private BukkitTask CoolDown;
    private byte cdCounter;
    private BukkitTask PreStart;
    private byte prestart;
    private BukkitTask GameTimer;
    private short playtime;
    private BukkitTask EndGame;
    private byte ending;
    private BukkitTask DysplayColor;
    private BukkitTask RemoveFloor;
    
    private boolean canreset;
    
    private byte round;
    private DyeColor curr_color;

    private boolean displayColor;
    public boolean removeFloor;


    public Set<String> players = new HashSet();
    public Set<String> looser = new HashSet<>();
    private ArrayList<DyeColor> colormap = new ArrayList<>();    

    //private RadioSongPlayer songPlayer;    
    private static Random random;
    public GameState state; //ОЖИДАНИЕ СТАРТ ЭКИПИРОВКА ИГРА ФИНИШ
    
    private final WorldServer nmsWorldServer;
    private final IBlockData ibdDataAir;
    private final IBlockData ibdDataDown;
    
    
    
    public Arena( String name, Location zero, Location arenaLobby,  String material, byte size_x, byte size_z, byte down, byte show, byte difficulty, byte maxRound, byte minPlayers, byte playersForForcestart ) {
        
        
        this.name = name;
        //try {
            this.arenaLobby = arenaLobby;
            this.zero = zero;
        //} catch (NullPointerException ex) {}
        
        nmsWorldServer = ((CraftWorld) arenaLobby.getWorld()).getHandle();
        
        switch (material) {
            case "clay" : mat = Material.WHITE_CONCRETE;  this.mode="clay"; break;
            case "glass": mat = Material.WHITE_STAINED_GLASS; this.mode="glass"; break;
            default     : mat = Material.WHITE_WOOL;          this.mode="wool"; break;
        }
        
        if ( size_x>=2 && size_x<=64 )  this.size_x = size_x; else  this.size_x = 16; 
        if ( size_z>=2 && size_z<=64 ) this.size_z = size_z; else this.size_z = 16;
        if ( down>=1 && down<=300 )  this.down = down; else  this.down = 89; 
        if ( show>=5 && show<=100 )  this.show = show; else  this.show = 25; 
        if ( difficulty>=1 && difficulty <=3 ) this.difficulty = difficulty; else this.difficulty = 1;
        if ( maxRound>=1 && maxRound<=64 ) this.maxRound = maxRound; else this.maxRound = 10; 
        if ( minPlayers>=2 && minPlayers<=64 ) this.minPlayers = minPlayers; else this.minPlayers = 2;
        if ( playersForForcestart>=2 && playersForForcestart<minPlayers ) this.playersForForcestart = playersForForcestart; else this.playersForForcestart = 12;

        ibdDataAir = net.minecraft.world.level.block.Block.getByCombinedId( 0 );
        ibdDataDown = net.minecraft.world.level.block.Block.getByCombinedId( this.down );
//System.out.println("Создана арена "+name+"   размер "+this.size_x+"*"+this.size_z+
        //" diff "+this.difficulty+" раунды "+this.maxRound+" игроки/быстро "+this.minPlayers+"/"+this.playersForForcestart);
        //if (AM.ArenaExist(name)) return; //не создаём дубль!!
        
        this.cdCounter = 40; //ожид в лобби арены
        this.prestart = 7; //ожид на арене
        this.ending = 7; //салюты,награждения
        this.playtime = 0;
        
        this.canreset = true;
        
        this.round = 1; 
        this.curr_color = DyeColor.BLACK;
        this.displayColor = true;
        this.removeFloor = false;
        random = new Random();
        
       // no_mat = new ItemStack ( Material.STONE_BUTTON, 1);
       // ItemMeta m = no_mat.getItemMeta();
      //  m.setDisplayName(  "§8<<<" );
      //  no_mat.setItemMeta(m);

        GenerateNewFloor();

        state=GameState.ОЖИДАНИЕ;
        Main.sendBsignMysql(name, state.displayColor+state.name(), "", GameState.ОЖИДАНИЕ);
    }

    
    
    
    
 
    public void resetGame() {  

        canreset=false;

        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {  
            UniversalListener.lobbyJoin(p, Bukkit.getWorld("lobby").getSpawnLocation());
            //p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation()); 
        });

        stopShedulers();    

        arenaLobby.getWorld().getEntities().stream().forEach((e) -> { 
            if (e.getType()!=EntityType.PLAYER) e.remove();
        });

        players.clear();
        looser.clear();

        round=1;

        cdCounter=40;
        prestart = 7;
        ending=10;
        playtime = 0;

        BackFloor();

        round = 1; 
        curr_color = DyeColor.BLACK;

        displayColor = true;
        removeFloor = false;

        state = GameState.ОЖИДАНИЕ;
        canreset=true;
        Main.sendBsignMysql(name, state.displayColor+state.name(), "", GameState.ОЖИДАНИЕ);
      //  if (Main.noteblock) StopMusic();
    }


    
    
    
    
    
    
    
    
    
    
    
    
 
 
    
    public void startCountdown() {                            //ожидание в лобби
            if (state != GameState.ОЖИДАНИЕ) return;
            state=GameState.СТАРТ;

            SendTitle( Messages.GetMsg("prestart_title"), Messages.GetMsg("prestart_subtitle").replace("%s", String.valueOf(cdCounter)) );
         //   if (Main.noteblock) StartMusic ();

            CoolDown = (new BukkitRunnable() {
                @Override
                public void run() {

                    if (cdCounter == 0) {
                            cdCounter = 40;
                            this.cancel();
                            PrepareToStart();

                    } else if ( players.size() < minPlayers ) {
                        SendAB(Messages.GetMsg("no_enough_players"));
                        state=GameState.ОЖИДАНИЕ;
                        cdCounter = 40;
                        this.cancel();

                    } else if ( players.size() == playersForForcestart && cdCounter > 10 ) {
                        SendAB(Messages.GetMsg("fast_start"));
                        cdCounter = 10;

                    } else if (cdCounter > 0) {
                            --cdCounter;
                            //Signs.SignsUpdate(name, Messages.GetMsg("signs_line_3_prefix")+ players.size(), getStateAsString(), "§1"+cdCounter );
                            Main.sendBsignChanel(name, players.size(), Messages.GetMsg("signs_line_3_prefix")+ players.size(), state.displayColor+state.name()+" §4"+cdCounter, GameState.СТАРТ);
                            if (cdCounter <= 5 && cdCounter > 0) {
                                SendTitle("", "§b"+cdCounter+" !");
                                SendSound(Sound.BLOCK_COMPARATOR_CLICK);
                            }
                    } 

                }
            }).runTaskTimer(Main.GetInstance(), 0L, 20L);
        }



    public void ForceStart(Player p) {
         if ( !IsJonable() ) {
            p.sendMessage( "§cYou can't start an arena - arena in game!");
            return;
         }
         if ( state != GameState.СТАРТ ) {
            p.sendMessage( "§cForce start is possible when min.player reached!");
            return;
         }
            if (CoolDown != null && cdCounter>3)  cdCounter=3;
            p.sendMessage( Messages.GetMsg("arenas_start_command_result"));
            PrepareToStart();
        }






    public void PrepareToStart() {   
        if (state != GameState.СТАРТ) return;
        state=GameState.ЭКИПИРОВКА;
        if (CoolDown != null)  CoolDown.cancel();


            getPlayers().stream().forEach((p) -> {  //всех игроков в мире добавля в список и на арену  
                //players.add(p.getName());
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 1));
                p.getInventory().clear();
                p.teleport( zero.clone().add(  (4 + random.nextInt(this.size_x-2)*4) , 1, (4 + random.nextInt(this.size_z-2)*4) ) );
                //p.playSound(p.getLocation(), "twist.start", 1, 1);
                p.playSound(p.getLocation(), Sound.BLOCK_BELL_RESONATE, 1, 1);
            });


                PreStart = (new BukkitRunnable() {         //тут уже таймер с игроками
                @Override
                public void run() {

                        if ( players.isEmpty() && canreset ) resetGame();

                            if ( players.size() < minPlayers ) {
                                SendAB(Messages.GetMsg("too_few_cancel_start"));
                                this.cancel();
                                if (canreset) resetGame();

                            } else if (prestart==0) {
                                prestart = 7;
                                this.cancel();
                                GameProgress();

                            } else {
                                SendAB(Messages.GetMsg("prepare_to_game").replace("%s", String.valueOf( prestart )));
                                //SendSound(Sound.ENTITY_CAT_PURR);
                                --prestart;
                            }

                }
                }).runTaskTimer(Main.GetInstance(), 0L, 20L);
        }
    
 


















    public void GameProgress() {
        if (state != GameState.ЭКИПИРОВКА) return;
        state=GameState.ИГРА;
        if (PreStart != null)  PreStart.cancel();



        SendAB(Messages.GetMsg("start_game"));
            SendSound(Sound.ENTITY_CAT_AMBIENT);

            displayColor = true;
            removeFloor = false;


            GameTimer = (new BukkitRunnable() {
                @Override
                public void run() {

                    if (displayColor) {
                        if (RemoveFloor != null)  RemoveFloor.cancel();
                        DysplayColor();                                             //показываем инфо
                        Bonus_spawn();
                    } else if (removeFloor) {
                        if (DysplayColor != null)  DysplayColor.cancel();
                        MustStayOne();                                              //удаляем все вроме текущего цвета
                        round++;                                                    //эта же функция возвращает новый пол через 3 сек
                        Main.sendBsignChanel(name, players.size(), state.displayColor+state.name(), Messages.GetMsg("signs_round_prefix") + Arena.this.round + "§7/§b§l" + Arena.this.maxRound, state);
                    }
                    
                    //тут тикает только во время раундов!!
                    for (Player p : getPlayers()) {
                        if ( zero.getBlockY()-p.getLocation().getBlockY() >=3 ) { //ниже полотна на 3 и более блока - упал
                            loose(p);
                        }
                    }
                    
                    //this.show - ( this.difficulty * this.round )
                    if ( players.isEmpty() || playtime >  maxRound*show+20 && canreset) {
                        SendTitle(Messages.GetMsg("end_cause_timelinit_title"), Messages.GetMsg("end_cause_timelinit_subtitle"));
                        resetGame();
                    } else if ( players.size()<=1 || round >= maxRound ) { //последний раунд и есть выжившие - победитель
                        this.cancel();
                        endGame();
                    }

                    playtime++;

                }
            }).runTaskTimer(Main.GetInstance(), 0L, 20L);

    }




    private void Bonus_spawn () {

            int ammount=0;
            //this.arenaLobby.getWorld().getEntities().stream().forEach((e) -> {
            for (Entity e:arenaLobby.getWorld().getEntities()) {
                if ( e.getType()!=EntityType.PLAYER && e.getTicksLived()>300) e.remove();
                if (e.getType() == EntityType.DROPPED_ITEM) ammount++;
            }
            //});
    //System.out.println("Bonus_spawn amm:"+ammount+"  limit:"+(this.size_x*this.size_z/8));   
        //for (int i=0; i<(this.size_x*this.size_z/8); i++) {
        for (int i=ammount; i<(this.size_x*this.size_z/8); i++) {

            final ItemStack is = new ItemStack(Material.SUNFLOWER, 1 );// AM.bonus.clone();
            //AM.Set_name(is, String.valueOf(random.nextInt(999)));
            final ItemMeta im = is.getItemMeta();
            im.setDisplayName(String.valueOf(random.nextInt(999)));
            is.setItemMeta(im);
            Item item = this.arenaLobby.getWorld().dropItem( this.zero.clone().add( (4 + random.nextInt(this.size_x-2)*4) , 2, (4 + random.nextInt(this.size_z-2)*4) ), is ); 
            item.setVelocity(new Vector(0, 0, 0));
    //System.out.println("!!!spawn "+item.getLocation());
            item.setPickupDelay(1);
            item.setGravity(false);
        }

    }





/*
    private void Check_cheat() {

        getPlayers().stream().forEach( (check)-> {
            /
            
            
    //System.out.println("Check_cheat "+check.getName()+" loc-1:"+check.getLocation().clone().subtract(0, 1, 0).getBlock().getType()+" loc-2"+check.getLocation().clone().subtract(0, 2, 0).getBlock().getType());
            if ( players.contains(check) && check.getLocation().clone().subtract(0, 1, 0).getBlock().getType()==Material.AIR && check.getLocation().clone().subtract(0, 1, 0).getBlock().getType()==Material.AIR ) {
                Loose(check);
                check.sendMessage("§fчит?");
            }
        });

    }*/



























    public void endGame() {   
        if (state != GameState.ИГРА) return;
        state=GameState.ФИНИШ;
        if (GameTimer != null)  GameTimer.cancel();
        if (DysplayColor != null)  DysplayColor.cancel();
        if (RemoveFloor != null)  RemoveFloor.cancel();

        if (!players.isEmpty())  {
           SendMessage("");
           SendMessage("");
           SendMessage("");
           SendMessage("§fПобедители: ");
           
           getPlayers().stream().forEach((win) -> {
                win.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 150, 0));
                ApiOstrov.sendTitle(win, (Messages.GetMsg("you_win_title")), (Messages.GetMsg("you_win_subtitle")),5,20,5);
                win.getWorld().playSound(win.getLocation(), "twist.win", 10, 1);
                win.getWorld().playSound(win.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 1);
                SendMessage( "§e" + win.getName()+ (ApiOstrov.hasGroup( win.getName(), "gamer")? " §8(игроман)§2✔ ":" §8(игроман)§c✖ ") + " за игру§7: " 
                        + (ApiOstrov.hasGroup(win.getName(), "gamer")? "§a+200§7, ":"§a+150§7, ")
                        + " за монетки: " + (ApiOstrov.hasGroup( win.getName(), "gamer")? "§a"+win.getLevel()*5*2:"§a"+win.getLevel()*5) );
                firework(win);
                ApiOstrov.addStat(win, Stat.TW_game);
                ApiOstrov.addStat(win, Stat.TW_win);
                for (int m=0; m<win.getLevel();m++) {
                    ApiOstrov.addStat(win, Stat.TW_gold);
                }
                win.setLevel(0);
           });
        }

            this.EndGame = (new BukkitRunnable() {
                @Override
                public void run() {
                    //if ( ending <=0 || Arena.this.players.isEmpty() ) {
                    if ( ending <=0 ) {
                         this.cancel();
                         resetGame();
                    } else {
                        for (Player p: getPlayers()) {
                            UniversalListener.spawnRandomFirework(p.getLocation());
                        }
                    }
                    --ending;
                }
            }).runTaskTimer(Main.GetInstance(), 0L, 20L);

       // Signs.SignsUpdate ( getName(), "§1---", getStateAsString(), "§1 - / -" );
        Main.sendBsignChanel(getName(), players.size(), "§1 - / -", state.displayColor+state.name(), state);


    }






    public void stopShedulers() {  
        if (this.CoolDown != null)  this.CoolDown.cancel();
        if (this.PreStart != null)  this.PreStart.cancel();
        if (this.GameTimer != null)  this.GameTimer.cancel();
        if (this.EndGame != null)  this.EndGame.cancel();
        if (this.DysplayColor != null)  this.DysplayColor.cancel();
        if (this.RemoveFloor != null)  this.RemoveFloor.cancel();
    }
    
 







    public List<Player> getPlayers() {
        final List<Player>list=new ArrayList<>();
        for (String nik:players) {
            if (Bukkit.getPlayer(nik)!=null) list.add(Bukkit.getPlayer(nik));
        }
        return list;
    }










 
    
    
    

    
 
    
    












    public void DysplayColor () {                           //показать новый цвет, звуки, таймер ->> разрешение на стирание 

       // Check_cheat();

        DyeColor new_col = GenRandColor(curr_color);
        this.curr_color = new_col;

        this.displayColor=false;
        this.removeFloor =false;

        //ItemStack item = new ItemStack(mat, 1, curr_color );
        ItemStack item = new ItemStack(mat, 1 );
        item = ColorUtils.changeColor(item, curr_color);
        ItemMeta m = item.getItemMeta();
        //m.setDisplayName( ColorUtils.DyeToString(curr_color).substring(0, 2) );
        m.setDisplayName( ColorUtils.DyeToString(curr_color) );
        item.setItemMeta(m);

        //getPlayers().stream().forEach((p) -> {
        for (Player p : getPlayers()) {
            for (byte i=0; i<9; i++) {
                p.getInventory().setItem(i, item);
                p.updateInventory();
            }
        }
        //});


        int sw = this.show - ( this.difficulty * this.round ) ;
        if ( sw < 5 ) sw = 5; else if ( sw > 80) sw=80;

            this.DysplayColor = (new BukkitRunnable() {

                byte i=8;
                @Override
                public void run() {

                    getPlayers().stream().forEach((p) -> {


                    /*    BossBarAPI.removeAllBars(p);
                        BossBarAPI.addBar( p,  new TextComponent( Main.DyeToString(curr_col)[1]+ Main.DyeToString(curr_col)[2] + "!"),
                            BossBarAPI.Color.YELLOW,  BossBarAPI.Style.NOTCHED_20,  1.0f,  20,  2 );*/

                        p.getInventory().setItem(i, AM.no_mat.clone() );
                        p.updateInventory();

                        switch (i) {
                            case 8: p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP , 1, 0);
                            case 5: p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP , 1, 0);
                            case 2: p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP , 1, 0);
                        }

                    });

                        i--;
                        if ( i < 0 ) {
                            displayColor = false;
                            removeFloor = true;
                            this.cancel();
                        }

                }
            }).runTaskTimer( Main.GetInstance(), 20L , sw );


        }
















// ------------------------------------  удалить квадраты/ вернуть квадраты ------------------------------------------------

    private void MustStayOne() {                     //удал все цвета, кроме текущего
    //long test1=System.currentTimeMillis();
    //List <Integer> time = new ArrayList<>();

        this.displayColor=false;
        this.removeFloor =false;


        int curr_plot=0;

            for (byte x=0; x<this.size_x; x++) {
                for (byte z=0; z<this.size_z; z++) {

                   if ( this.colormap.get(curr_plot) != this.curr_color ) {
    //long test2=System.currentTimeMillis();
                       FillPlotAir( x, z );
    //time.add( (int)(System.currentTimeMillis()-test2) );
                   }
                   curr_plot++;

                }
            }            
    //System.out.println("§eMustStayOne "+(System.currentTimeMillis()-test1));   
    //System.out.println(time);   

     //   UpdateChunks();

            this.RemoveFloor = (new BukkitRunnable() {
                @Override
                public void run() {
                    Arena.this.BackFloor();
                    Arena.this.displayColor=true;
                    Arena.this.removeFloor =false;
                }
            }).runTaskLater(Main.GetInstance(), 60 );

        }






    private void BackFloor() {                     //вернуть пол
    //long test1=System.currentTimeMillis();
    //List <Integer> time = new ArrayList<>();

        ArrayList <DyeColor> temp = new ArrayList<>();
        int curr_plot=0;
        DyeColor previos = DyeColor.BLACK;

            for (byte x=0; x<this.size_x; x++) {
                for (byte z=0; z<this.size_z; z++) {

                    DyeColor col = GenRandColor(previos);
                    previos = col;

                   if ( this.colormap.get(curr_plot) == this.curr_color ) {         //если плот был оставлен
                       temp.add ( curr_plot, curr_color );              //прописываем на след.раунд cтарый цвет - чтобы его же и восстановило
                   } else {
    //long test2=System.currentTimeMillis();
                       FillPlotMat ( x , z, col );
    //time.add( (int)(System.currentTimeMillis()-test2) );
                      temp.add ( curr_plot, col );
                   }
                   curr_plot++;

                }
            }            

       this.colormap = temp;
    //System.out.println("§eBackFloor "+(System.currentTimeMillis()-test1));   
    //System.out.println(time);   
       // UpdateChunks();


        }




    private void FillPlotMat ( byte plot_x, byte plot_z, DyeColor color ) {
        
        

        int x = this.zero.getBlockX() + plot_x * 4;
        int y = this.zero.getBlockY();
        int z = this.zero.getBlockZ() + plot_z * 4;

        final String mat_base = ColorUtils.getItemNameBaseWithOutColor(mat.toString());
        mat = Material.valueOf(color.toString()+"_"+mat_base);
//System.out.println("FillPlotMat color="+color+"mat="+mat);        
        final BlockData data=mat.createBlockData();
        final IBlockData ibdColoredWool = ((CraftBlockData)data).getState(); //((CraftBlock)block).getNMS();
        final IBlockData ibdAir = ((CraftBlockData)Material.AIR.createBlockData()).getState(); //((CraftBlock)block).getNMS();
//System.out.println("ibdColoredWool "+ibdColoredWool);        
        
        //IBlockData ibdColoredWool = net.minecraft.server.v1_16_R3.Block.getByCombinedId( mat.getId() + (c << 12) );
        //BlockPosition bps = new BlockPosition( x, y, z );
        //net.minecraft.server.v1_16_R3.World c_world = ((CraftWorld) arenaLobby.getWorld()).getHandle();
        //ChunkSection chunksection = new ChunkSection(bps.getY() >> 4 << 4, c_world.worldProvider.m());
        //ChunkSection chunksection = new ChunkSection(bps.getY() >> 4 << 4, ((CraftWorld) arenaLobby.getWorld()).getHandle().worldProvider.m());

        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {

                final BlockPosition bps = new BlockPosition( x+x_, y, z+z_ );
//System.out.println(" bps= "+bps);        
                net.minecraft.world.level.chunk.Chunk nmsChunk = nmsWorldServer.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 );
//System.out.println((x+x_)+":"+y+":"+(z+z_)+" nmsChunk= "+nmsChunk+" getSections="+nmsChunk.getSections().length+" need"+(y>>4)+"="+nmsChunk.getSections()[y>>4]);        
                final ChunkSection chunksection = nmsChunk.getSections()[y>>4];
                if (chunksection==null) {
                    arenaLobby.getWorld().getBlockAt(x+x_, y, z+z_).setType(mat);
                } else {
                    chunksection.setType(bps.getX() & 15, bps.getY() & 15, bps.getZ() & 15, ibdColoredWool);
                    nmsWorldServer.s(bps);
                }
                //chunksection.setType(bps.getX() & 15, bps.getY() & 15, bps.getZ() & 15, ibdColoredWool);
                //nmsWorldServer.s(bps);

                //((CraftWorld)arenaLobby.getWorld()).getHandle().getChunkAt( (x+x_) >> 4, (z+z_) >> 4 ).a( bp, ibd );
                //((CraftWorld) arenaLobby.getWorld()).getHandle().setTypeAndData(bps, ibd, 2); //if (applyPhysics) 3 else 2
                //((CraftWorld) arenaLobby.getWorld()).getHandle().notify( bps, ibdColoredWool, Blocks.AIR.getBlockData(),  3);
                ((CraftWorld) arenaLobby.getWorld()).getHandle().notify( bps, ibdColoredWool, ibdAir,  3);

            }
        }

    }



    private void FillPlotAir ( byte plot_x, byte plot_z ) {

        int x = this.zero.getBlockX() + plot_x * 4;
        int y = this.zero.getBlockY();
        int z = this.zero.getBlockZ() + plot_z * 4;

        //IBlockData ibd = net.minecraft.server.v1_16_R3.Block.getByCombinedId( 0 );

        //BlockPosition bps = new BlockPosition( x, y, z );
        //net.minecraft.server.v1_16_R3.World c_world = ((CraftWorld) arenaLobby.getWorld()).getHandle();
        //ChunkSection chunksection = new ChunkSection(bps.getY() >> 4 << 4, c_world.worldProvider.m());
        //ChunkSection chunksection = new ChunkSection(bps.getY() >> 4 << 4, ((CraftWorld) arenaLobby.getWorld()).getHandle().worldProvider.m());

        
        
        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {
                final BlockPosition bps = new BlockPosition( (x+x_), y, (z+z_) );

                final ChunkSection chunksection = nmsWorldServer.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 ).getSections()[bps.getY()>>4];
                final IBlockData current = chunksection.getType(bps.getX() & 15, bps.getY() & 15, bps.getZ() & 15);
                
                chunksection.setType(bps.getX() & 15, bps.getY() & 15, bps.getZ() & 15, ibdDataAir);
                nmsWorldServer.s(bps); 
                //((CraftWorld) arenaLobby.getWorld()).getHandle().getChunkAt( (x+x_) >> 4, (z+z_) >> 4 ).a( bps, ibd );
                //((CraftWorld) arenaLobby.getWorld()).getHandle().setTypeAndData(bp, ibd, 2); //if (applyPhysics) 3 else 2
                //nmsWorldServer.notify( bps , Blocks.WOOL.getBlockData(), ibdDataAir, 3);
                nmsWorldServer.notify( bps , current, ibdDataAir, 3);
            }
        }

    }
// -----------------------------------------------------------------------------------------------


 //  private void UpdateChunks() {
       
       // this.players.stream().forEach(p -> this.players.stream().forEach(p2 -> p.hidePlayer(p2)));
       
      //  this.chunks.stream().forEach(ch_coord -> 
    //            this.arenaLobby.getWorld().refreshChunk( Integer.valueOf(ch_coord.split(":")[0]), Integer.valueOf(ch_coord.split(":")[1]) )
     //   );
        
      //  if (!Main.shutdown) {
      //      new BukkitRunnable() {
     //           public void run() {
      //              Arena.this.players.stream().forEach(p -> Arena.this.players.stream().forEach(p2 -> p.showPlayer(p2)));
      //          }
     //       }.runTaskLater((Plugin)Main.GetInstance(), 15L);
     //   }
        
  //  }




// ------------------------------------  Генерация ------------------------------------------------

    private void GenerateNewFloor() {

        //this.chunks.clear();
       //this.chunksHash.clear();
        this.colormap.clear();
        int curr_plot=0;
        DyeColor previos = DyeColor.BLACK;

        //for (byte x=0; x<this.size_x; x++) {
        //        for (byte z=0; z<this.size_z; z++) {
        //           LoadChunk ( x , z );          //!!! создание карты чанков для обновления и их загрузка !!!
        //           curr_plot++;
        //        }
        //    }            

        curr_plot = 0;
    //System.out.println("GenerateNewFloor 111111 x:"+this.size_x+" z:"+this.size_z);               

        for (byte x=0; x<this.size_x; x++) {
                for (byte z=0; z<this.size_z; z++) {

                   LoadChunk ( x , z );          //!!! создание карты чанков для обновления и их загрузка !!!

                   DyeColor col = GenRandColor(previos);
                   previos = col;
                   this.colormap.add(curr_plot, col );
    //System.out.println("GenerateNewFloor x:"+x+" z:"+z);               
                   FillPlotMat ( x , z, col );  //заполняем плоты случ цветом
                   FillDown ( x , z );          //заполняем низ
                   curr_plot++;

                }
            }            

     //   UpdateChunks();

        }


    private void LoadChunk ( byte plot_x, byte plot_z ) {

        int x = this.zero.getBlockX() + plot_x * 4;  //координата блока Х на углу плота
        int z = this.zero.getBlockZ() + plot_z * 4; //координата блока Z на углу плота


        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {

                final Chunk ch = this.zero.getWorld().getChunkAt( (x+x_) >> 4, (z+z_) >> 4 );      //берёт стандартный чанк на этой же координате
                //String t = String.valueOf(ch.getX()) + "x" + String.valueOf(ch.getZ());

                    //if ( !this.chunksHash.contains(t) ) {
                    //       this.chunksHash.add(t);
                         //   this.chunks.add(ch.getX()+":"+ch.getZ());
                            if (!ch.isLoaded()) ch.load();
                   // }

            }
        }

    }



    public void ResetFloor() {

            for (byte x=0; x<this.size_x; x++) {
                for (byte z=0; z<this.size_z; z++) {
                   FillPlotAir(x , z );
                   FillDownAir(x, z);  
                }
            }

        //UpdateChunks();

            //this.chunks.clear();
           // this.chunksHash.clear();
            this.colormap.clear();

    }    






    private void FillDown ( byte plot_x, byte plot_z ) {

        int x = this.zero.getBlockX() + plot_x * 4;  //координата блока Х на углу плота
        int y = this.zero.getBlockY() - 5;           //координата высоты (для пола -5)
        int z = this.zero.getBlockZ() + plot_z * 4; //координата блока Z на углу плота


        //net.minecraft.server.v1_16_R3.World world = ( (CraftWorld) arenaLobby.getWorld() ).getHandle(); //берёт NMS мир
        //IBlockData ibd = net.minecraft.server.v1_16_R3.Block.getByCombinedId( this.down );     //создаёт ibd код для GLOWSTONE

        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {
                //net.minecraft.server.v1_16_R3.Chunk c = c_world.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 );   //берёт NMS чанк
                //nmsWorldServer.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 ).a( new BlockPosition( (x+x_), y, (z+z_) ) , ibdDataDown );                                    //вносит в него ibd по blockposition
                nmsWorldServer.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 ).setType(new BlockPosition( (x+x_), y, (z+z_) ) , ibdDataDown , false, false );                                    //вносит в него ibd по blockposition

            }
        }

    }



    private void FillDownAir ( byte plot_x, byte plot_z ) {

        int x = this.zero.getBlockX() + plot_x * 4;
        int y = this.zero.getBlockY() - 5;
        int z = this.zero.getBlockZ() + plot_z * 4;


        //net.minecraft.server.v1_16_R3.World world = ( (CraftWorld) arenaLobby.getWorld() ).getHandle(); //берёт NMS мир
        //IBlockData ibd = net.minecraft.server.v1_16_R3.Block.getByCombinedId( Material.AIR.getId() );     //создаёт ibd код для GLOWSTONE

        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {
                //net.minecraft.server.v1_16_R3.Chunk c = c_world.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 );   //берёт NMS чанк
                //nmsWorldServer.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 ).a( new BlockPosition( (x+x_), y, (z+z_) ) , ibdDataAir );                                    //вносит в него ibd по blockposition
                nmsWorldServer.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 ).setType(new BlockPosition( (x+x_), y, (z+z_) ) , ibdDataAir , false, false );                                    //вносит в него ibd по blockposition
            }
        }

    }
// -----------------------------------------------------------------------------------------

















// xxxxxxxxxxxxxxxxxxxxxxxxxxx  Обработчик игроков xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

    public void addPlayers(Player p) {
        if ( IsJonable() ) {
            p.teleport(getLobby());
            players.add(p.getName());
            if ( players.size() < minPlayers ) {
                SendAB ( Messages.GetMsg("players_need_for_start").replace("%n", String.valueOf( minPlayers-players.size() )) ); 
            }
            
            //p.getInventory().clear();
            p.getInventory().setItem(0, new ItemStack(Material.AIR));
            p.getInventory().setItem(7, new ItemStack(Material.AIR));
            p.getInventory().setItem(8, UniversalListener.leaveArena.clone());
            p.updateInventory();
            //Signs.SignsUpdate(name, Messages.GetMsg("signs_line_3_prefix")+ players.size(), getStateAsString(), "§1 - / -" );
            Main.sendBsignChanel(name, players.size(), Messages.GetMsg("signs_line_3_prefix")+ players.size(), state.displayColor+state.name(), GameState.ОЖИДАНИЕ);
            if ( players.size()>=minPlayers ) startCountdown();
        }
    }



    public void PlayerExit (Player p) {

        if ( IsJonable() ) {                //если ожидание или первый таёмер, т.е. не внесён в players
            players.remove(p.getName());         //перестраховка
                if (players.size() < minPlayers && this.CoolDown != null) {      //если был запущен таймер
                    this.CoolDown.cancel();
                    Arena.this.cdCounter = 40;
                    SendAB(Messages.GetMsg("no_enough_players"));
                    state=GameState.ОЖИДАНИЕ;
                }
                //new ActionbarTitleObject (Messages.GetMsg("arena_exit")).send(p);
                //ApiOstrov.sendActionBarDirect(p, Messages.GetMsg("arena_exit"));
                //p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
                //Signs.SignsUpdate( getName(),Messages.GetMsg("signs_line_3_prefix")+ players.size(), getStateAsString(), "§1 - / -" );
                Main.sendBsignChanel(name, players.size(), Messages.GetMsg("signs_line_3_prefix")+ players.size(), state.displayColor+state.name(), state);

        } else {                                            //выход во время игры-возможно только через отключение
            if ( players.remove(p.getName()) ) {
               // players.remove(p.getName());
                //if ( players.size() ==1 ) endGame();    
                //Signs.SignsUpdate( getName(),  Messages.GetMsg("signs_line_3_prefix")+ players.size(), getStateAsString(), Messages.GetMsg("signs_round_prefix") + this.round + "§7/§b§l" + this.maxRound  );
                Main.sendBsignChanel(name, players.size(), state.displayColor+state.name(), Messages.GetMsg("signs_round_prefix") + this.round + "§7/§b§l" + this.maxRound,  state);
            }
        }


        }



    public void loose (final Player p) {

        if (players.remove(p.getName())) {
            looser.add(p.getName());

            if (canreset) {
                //p.getInventory().clear();
                //p.updateInventory();
                spectate(p);
                DonatEffect.spawnRandomFirework(p.getLocation());
                
                p.getWorld().playSound(p.getLocation(), "twist.fall_down", 10, 1);
                ApiOstrov.sendTitle(p,  Messages.GetMsg("you_loose_title"), Messages.GetMsg("you_loose_subtitle"),5,10,5);
                ApiOstrov.addStat(p, Stat.TW_game);
                ApiOstrov.addStat(p, Stat.TW_loose);

                //p.getWorld().playSound(p.getLocation(), Sound.ENTITY_DOLPHIN_DEATH, 10, 1);
        //System.err.println( "  p.getLocation().getBlockY() "+p.getLocation().getBlockY()+ " zero.getBlockY() "+(zero.getBlockY()) );

                   /* new BukkitRunnable() {
                        @Override
                        public void run() {
                            if ( p.getLocation().getBlockY() < (zero.getBlockY()) && state==GameState.ИГРА ) { //фикс от случайного спавна на арене
                                for (byte z= 0; z<=6; z++) {
                                    PigZombie pz = p.getWorld().spawn( p.getLocation().clone().add( z-3,1,z-3), PigZombie.class);
                                    pz.setAngry(true);
                                    pz.setCustomName(Messages.GetMsg("pig_zombie_name"));
                                    pz.getEquipment().setItemInMainHand(new ItemStack(Material.CARROT, 1));
                                    pz.setTarget(p);
                                }
                            }
                        }
                    }.runTaskLater(Main.GetInstance(), 2L); */
            }     
        }
   /* if ( !players.contains(p.getName()) ) return;

        players.remove(p.getName());
        looser.add(p.getName());

        if (canreset) {
            p.getInventory().clear();
            p.updateInventory();
            p.getWorld().playSound(p.getLocation(), "twist.fall_down", 10, 1);
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_DOLPHIN_DEATH, 10, 1);
    //System.err.println( "  p.getLocation().getBlockY() "+p.getLocation().getBlockY()+ " zero.getBlockY() "+(zero.getBlockY()) );

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if ( p.getLocation().getBlockY() < (zero.getBlockY()) && state==GameState.ИГРА ) { //фикс от случайного спавна на арене
                            for (byte z= 0; z<=6; z++) {
                                PigZombie pz = p.getWorld().spawn( p.getLocation().clone().add( z-3,1,z-3), PigZombie.class);
                                pz.setAngry(true);
                                pz.setCustomName(Messages.GetMsg("pig_zombie_name"));
                                pz.getEquipment().setItemInMainHand(new ItemStack(Material.CARROT, 1));
                                pz.setTarget(p);
                            }
                        }
                    }
                }.runTaskLater(Main.GetInstance(), 2L); 
        }
        ApiOstrov.sendTitle(p,  Messages.GetMsg("you_loose_title"), Messages.GetMsg("you_loose_subtitle"),5,10,5);
        ApiOstrov.addStat(p, Stat.TW_game);
        ApiOstrov.addStat(p, Stat.TW_loose);*/

    }

    
// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

    public void spectate (final Player p) {
        UniversalListener.spectatorPrepare(p);
        p.teleport(zero.clone().add(0, 5, 0));
    }





























// **************************  GET / SET *******************************

    
    
    public boolean IsInGame(Player p) {
        return players.contains(p.getName());
    }
    
   // public boolean IsLooser(String nik) {
  //      return looser.contains(nik);
 //   }
    
   // public boolean IsLooserLock(Player p) {
//System.out.println("IsLooserLock looser:"+this.looser.contains(p.getName())+" p(y):"+p.getLocation().getBlockY()+" arena(y)");        
  //      return this.looser.contains(p.getName()) && p.getLocation().getBlockY() < this.zero.getBlockY();
  //  }
    
    public boolean IsJonable() {
        return ( state == GameState.ОЖИДАНИЕ || state == GameState.СТАРТ );
    }
    

    


    public String getName() {
        return this.name;
    }



   
    public String getScoreTimer() {
        switch (state) {
            case ОЖИДАНИЕ:
                return Messages.GetMsg("score_waiting").replace("%s", String.valueOf(this.minPlayers-this.players.size()));
            case СТАРТ:
                return Messages.GetMsg("score_cooldown").replace("%s", String.valueOf(this.cdCounter));
            case ЭКИПИРОВКА:
                return Messages.GetMsg("score_prestart");
            case ИГРА:
                return Messages.GetMsg("score_ingame").replaceAll("%r", String.valueOf(this.round)).replaceAll("%m", String.valueOf(this.maxRound)).replace("%t", Main.getTime(this.playtime) );
            default:
                return state.displayColor+state.name();
        }
   }
   
    public String GetScoreStatus (Player p) {
        if (state == GameState.ОЖИДАНИЕ || state == GameState.СТАРТ ) {
            return "§f"+p.getName();
        } else if (players.contains(p.getName())) {
            //return "§2§o✔ "+ColorUtils.DyeToString(curr_color).substring(0,2)+p.getName();
            return "§2§o✔ "+ColorUtils.ChatColorfromDyeColor(curr_color)+p.getName();
        } else if (this.looser.contains(p.getName())) {
            return "§4§o✖ §7"+p.getName();
        } else {
            return "§7(зритель) "+p.getName();
        }
    }
    
    
    
    
    
    public Location getZero() {
        return this.zero;
    }

    public Location getLobby() {
        return this.arenaLobby;
    }

    public void setLobby(Location location) {
        this.arenaLobby = location;
    }
    
    public String getMode() {
        return this.mode;
    }
   
    public byte getSize_x() {
	return this.size_x;
    }

    public byte getSize_z() {
	return this.size_z;
    }

    public byte getDownId() {
	return this.down;
    }
    
    public byte getShow() {
	return this.show;
    }

    public void setShow( byte d ) {
	this.show = d;
    }
    
    public byte getDifficulty() {
	return this.difficulty;
    }

    public void setDifficulty( byte d ) {
	this.difficulty = d;
    }

    public byte getMaxRound() {
	return this.maxRound;
    }

    public void setMaxRound( byte d ) {
	this.maxRound = d;
    }
    
    public byte getMinPlayers() {
        return this.minPlayers;
    }
    
    public void setMinPlayers( byte i ) {
         this.minPlayers=i;
    }

    public byte getForce() {
	return this.playersForForcestart;
    }

    public void setForce( byte d ) {
	this.playersForForcestart = d;
    }


// *********************************************************************



    
    
    
    














// -------------------------------  Вспомогательные --------------------------
    
 


    public void SendAB(String text) {
            arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
                //new ActionbarTitleObject (text).send(p);
                ApiOstrov.sendActionBarDirect(p, text);
            });
        }

    public void SendMessage(String text) {
            arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
                p.sendMessage(text);
            });
        }

    public void SendSound(Sound s) {
            arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
                p.playSound(p.getLocation(), s , 5.0F, 5.0F);
            });
        }

    public void SendSound(String s) {
            arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
                p.playSound(p.getLocation(), s , 1, 1);
            });
        }

    public void SendTitle(String t, String st) {
            arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
                ApiOstrov.sendTitle(p,  t, st, 2,10,2);
    //(new TitleObject ((t), TitleObject.TitleType.TITLE)).setFadeIn(20).setStay(20).setFadeOut(5).send(p);
    //(new TitleObject ((st), TitleObject.TitleType.SUBTITLE)).setFadeIn(20).setStay(20).setFadeOut(5).send(p);
            });
        }




    private DyeColor  GenRandColor (DyeColor old) {
        byte pos = 0;
        for (byte i=0; i<15; i++) {                                         //делаем 15 попыток подобрать уникальный цвет
            pos = (byte) random.nextInt( Main.allowedColors.size()-1 );     //генерируем позицию цвета в списке разрешенных
            if ( old != Main.allowedColors.get(pos) ) return Main.allowedColors.get(pos);   //если старый цвет не совпадает с новым, отдаём
        }
        return Main.allowedColors.get( pos );                               //если не подобрали, отдаём, что есть
    } 





    ///салютики
        private static void firework (Player p) {

            for (int i = 0; i < 6; ++i) {                           //салютики
                new BukkitRunnable() {
                    @Override
                    public void run() {
                Random random = new Random();
                Firework firework = (Firework) p.getWorld().spawn(p.getLocation().clone().add(0, 5, 0), Firework.class);
                FireworkMeta fireworkmeta = firework.getFireworkMeta();
                FireworkEffect fireworkeffect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256))).withFade(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256))).with(FireworkEffect.Type.STAR).trail(true).build();

                fireworkmeta.addEffect(fireworkeffect);
                firework.setFireworkMeta(fireworkmeta);   
                    }}.runTaskLater(Main.GetInstance(), (long)(i * 5));}
        }


/*

     private void StartMusic () {

        try {
            if (Main.noteblock) {

                File[] files = new File(Main.GetInstance().getDataFolder().getPath() + "/songs/").listFiles();
                List<File> songs = new ArrayList<>();
                for (File f : files)
                    if (f.getName().contains(".nbs")) songs.add(f);
                File song = songs.get(new Random().nextInt(songs.size()));
                Song s = NBSDecoder.parse(song);

                songPlayer = new RadioSongPlayer(s);
                songPlayer.setAutoDestroy(true);

                songPlayer.setPlaying(true);

                players.stream().forEach((p) -> { songPlayer.addPlayer(p); });

                songPlayer.setVolume((byte) 60);
                songPlayer.setFadeStart((byte) 25);
            }
        } catch (NullPointerException e){}

    }


    private void StopMusic () {
        try {
            if (Main.noteblock) {
                songPlayer.setPlaying(false);
                this.songPlayer.destroy(); 
            }
        } catch (NullPointerException e){}
    }

   */
 















    
}
