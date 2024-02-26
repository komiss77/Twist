package ru.ostrov77.twist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.komiss77.enums.GameState;
import ru.ostrov77.minigames.MG;


public class TwistCmd implements CommandExecutor, TabCompleter {

    private static final String PREF = "tw";
    public static List<String> subCommands = Arrays.asList("join", "leave", "create", "delete", "list", "reset", "start");

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String command, String[] args) {
        final List<String> sugg = new ArrayList<>();
//System.out.println("l="+strings.length+" 0="+strings[0]);
        switch (args.length) {

            case 1 -> {
                //0- пустой (то,что уже введено)
                for (String s : subCommands) {
                    if (s.startsWith(args[0])) {
                        sugg.add(s);
                    }
                }
            }

            case 2 -> {
                //1-то,что вводится (обновляется после каждой буквы
//System.out.println("l="+strings.length+" 0="+strings[0]+" 1="+strings[1]);
                //if (strings[0].equalsIgnoreCase("build") || strings[0].equalsIgnoreCase("destroy") ) {
                for (Arena a : AM.arenas.values()) {
                    sugg.add(a.arenaName);
                }
                //   sugg.add("loni");
                //    sugg.add("permission");
                //   sugg.add("group");
                //  sugg.add("exp");
                //  sugg.add("reputation");
                ///}
            }

        }

        return sugg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Not console commad!");
            return false;
        }
        if (!cmd.getName().equalsIgnoreCase("tw") && !cmd.getName().equalsIgnoreCase("twist")) {
            return false;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            Help(p);
            return false;
        }

        switch (args[0]) {

            case "join" -> {
                if (!CheckArgs(p, args, 2, false, true)) {
                    break;
                }
                final Arena arena = AM.getArena(args[1]);

                if (arena == null)  {
                    p.sendMessage("§cНет арены с названием "+args[1]);
                    return true;
                }
                if (AM.getArena(p)!=null) {
                    p.sendMessage("§cВы уже на арене !");
                    return true;
                }
                if (arena.state == GameState.ОЖИДАНИЕ || arena.state == GameState.СТАРТ ) {
                    arena.addPlayer(p);
                } else {
                    arena.spectate(p);
                }
            }

            case "leave" -> {
                final Arena a = AM.getArena(p);
                if (a!=null) {
                    a.removePlayer(p);
                }
                MG.lobbyJoin(p);
            }

            case "create" -> {
                if (CheckArgs(p, args, 6, true, false)) {
                    if (!Twist.isNumber(args[3]) || !Twist.isNumber(args[4])) {
                        p.sendMessage("§c<size_x>, <size_z> must be Integer! Use 0 for default walue!");
                        return false;
                    }
                    if (p.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) {
                        p.sendMessage("§cIn this world can`t create arena! This world is globall lobby");
                        return false;
                    }
                    if (!AM.CanCreate((Player) sender)) {
                        p.sendMessage("§cIn this world allready exist arena!");
                        return false;
                    }
                    if (AM.ArenaExist(args[1])) {
                        p.sendMessage("§cArena with this name allready exist!");
                        return false;
                    }
                    if (p.getLocation().getY() < 10) {
                        p.sendMessage("§cArena location(Y) must be highter, than 10!");
                        return false;
                    }
                    if (p.getLocation().getY() > 250) {
                        p.sendMessage("§cArena location(Y) must be lowest, than 250!");
                        return false;
                    }
                    if (!args[2].equals("clay") && !args[2].equals("glass")) {
                        args[2] = "wool";
                        p.sendMessage("§aUse default field material - wool");
                    }
                    if (args[3].equals("0")) {
                        args[3] = "16";
                        p.sendMessage("§aUse default x-size (16)");
                    }
                    if (args[4].equals("0")) {
                        args[4] = "16";
                        p.sendMessage("§aUse default z-size (16)");
                    }
                    Material downMat = Material.matchMaterial(args[5]);
                    if (downMat == null) {
                        args[5] = Material.GLOWSTONE.name();//"89";
                        p.sendMessage("§aUse down default material - GLOWSTONE");
                    }
                    //if ( !net.minecraft.world.level.block.Block.a( Integer.valueOf(args[5])).getMaterial().isSolid()  ) {
                    if (downMat.isSolid()) {
                        p.sendMessage("§cThe down material must be SOLID!!");
                        return false;
                    }
                    p.sendMessage("§eGenerate arena field.. Please, wait..");
                    AM.createArena(args[1], ((Player) sender).getLocation(), args[2], Byte.valueOf(args[3]), Byte.valueOf(args[4]), Byte.valueOf(args[5]));
                    sender.sendMessage("§fArena §b" + args[1] + " §fcreated! You can save it anytime with command §b/tw saveall");

                } else {
                    p.sendMessage("§cInsufficiant Arguments! Proper use of the command goes like/ this: ");
                    p.sendMessage("§b" + PREF + " create <name> <mode> <size_x> <size_z> <down_id>");
                    p.sendMessage("§b<mode> §emust be string §bwool, glass or clay §7Input any chars for default value (wool)");
                    p.sendMessage("§b<size_x> <size_z> §emust be number §bfrom 2 to 64 §7Set to 0 for default value (16)");
                    p.sendMessage("§b<down_id> §emust be number §bfrom 1 to 300 §7Set to 0 for default mat.GLOWSTONE (89)");
                }
            }

            case "delete" -> {
                if (!CheckArgs(p, args, 2, false, true)) {
                    break;
                }
                p.sendMessage("§cTry to delete Arena...");
                AM.DeleteArena(args[1], (Player) p);
            }

            case "list" -> {
                p.sendMessage("§b§lАрен найдено: " + AM.getAllArenas().size());
                AM.getAllArenas().entrySet().stream().forEach((e) -> {
                    sender.sendMessage("§e" + e.getKey() + " :§5" + e.getValue().state.name());
                });
            }

            case "reset" -> {
                if (!CheckArgs(p, args, 2, false, true)) {
                    break;
                }
                p.sendMessage("§bTry to reset Arena...");
                AM.ResetArena(args[1], (Player) sender);
            }

            case "start" -> {
                if (!CheckArgs(p, args, 2, false, true)) {
                    break;
                }
                AM.startArenaByName(p, args[1]);
            }

            case "setlobby" -> {
                if (!CheckArgs(p, args, 2, true, true)) {
                    break;
                }
                if (AM.getArena(args[1]).getPlayers().size() > 0) {
                    p.sendMessage("§cThat arena has players in it. Please perform reset command before this operation.");
                    return false;
                }
                AM.setArenaLobby(((Player) sender).getLocation(), args[1]);
                p.sendMessage("§bSuccessfully set arena lobby!");
            }

            case "setround" -> {
                if (!CheckArgs(p, args, 3, true, true)) {
                    break;
                }
                if (!Twist.isNumber(args[2])) {
                    p.sendMessage("§c" + args[2] + " must be Integer!");
                    return false;
                }
                AM.getArena(args[1]).maxRound = Integer.parseInt(args[2]);
                sender.sendMessage("§bSuccessfully set the round count to " + args[2] + "!");
            }

            case "setdiff" -> {
                if (!CheckArgs(p, args, 3, true, true)) {
                    break;
                }
                if (!Twist.isNumber(args[2])) {
                    p.sendMessage("§c" + args[2] + " must be Integer!");
                    return false;
                }
                AM.getArena(args[1]).difficulty = Integer.parseInt(args[2]);
                sender.sendMessage("§bSuccessfully set the Difficulty to " + args[2] + "!");
            }

            case "setshow" -> {
                if (!CheckArgs(p, args, 3, true, true)) {
                    break;
                }
                if (!Twist.isNumber(args[2])) {
                    p.sendMessage("§c" + args[2] + " must be Integer!");
                    return false;
                }
                AM.getArena(args[1]).show = Integer.parseInt(args[2]);
                sender.sendMessage("§bSuccessfully set the starting show time to " + args[2] + "!");
            }

            case "saveall" -> {
                sender.sendMessage("§bTry to save data to disk...");
                AM.saveAll();
                sender.sendMessage("§2Successfully save arenas to disk!");
            }

            default ->
                Help(p);

        }
        /*  case "setminpl":
        if ( !CheckArgs (p, args, 3, true, true) ) break;
        if ( !Twist.isNumber(args[2]) ) {p.sendMessage( "§c"+args[2]+" must be Integer!");return false;}
        AM.getArena(args[1]).setMinPlayers( Byte.valueOf( args[2] ) );
        sender.sendMessage( "§bSuccessfully set the min players to " + args[2] + "!");
        break;*/
 /* case "setforce":
        if ( !CheckArgs (p, args, 3, true, true) ) break;
        if ( !Twist.isNumber(args[2]) ) {p.sendMessage( "§c"+args[2]+" must be Integer!");return false;}
        AM.getArena(args[1]).setForce( Byte.valueOf( args[2] ) );
        sender.sendMessage( "§bSuccessfully set the players for force start to " + args[2] + "!");
        break;*/

        return true;

    }

    private static void Help(Player p) {

        p.sendMessage("§a-- " + Twist.Prefix + " §acommands help --");
        p.sendMessage("" + PREF + " join <name>");
        if (p.isOp()) {
            p.sendMessage("" + PREF + " create <name> <mode> <size_x> <size_z> <down_material>");
            p.sendMessage("" + PREF + " delete <name>");
            p.sendMessage("" + PREF + " list");
            p.sendMessage("" + PREF + " setlobby <name>");
            p.sendMessage("" + PREF + " setdiff <name> <integer>");
            p.sendMessage("" + PREF + " setround <name> <integer>");
            p.sendMessage("" + PREF + " setminpl <name> <integer>");
            p.sendMessage("" + PREF + " setforce <name> <integer>");
            p.sendMessage("" + PREF + " start <name>");
            p.sendMessage("" + PREF + " reset <name>");
            p.sendMessage("" + PREF + " saveall");
        }

    }

    private static boolean CheckArgs(Player p, String[] args, int num, Boolean op, Boolean err) {

        if (op && !p.isOp()) {
            p.sendMessage("§cThis command only for OP!");
            return false;
        }

        //if ( args.length < num ) { 
        //    p.sendMessage(Messages.GetMsg("arg_err"));
        //    return false; 
        //}
        if (args.length < num && num == 2) {
            p.sendMessage("§cArgument error!");
            if (err) {
                p.sendMessage("§aexample: §f/" + PREF + " " + args[0] + " MyArena");
            }
            return false;
        }

        if (args.length < num && num == 3 && err) {
            p.sendMessage("§cArgument error!");
            if (err) {
                p.sendMessage("§aexample: §f/" + PREF + " " + args[0] + " MyArena 3");
            }
            return false;
        }

        if (args.length < num && num > 3) {    //фикс для create
            p.sendMessage("§cArgument error!");
            return false;
        }

        if (AM.getArena(args[1]) == null && num < 3) {       //&& num <3 - фикс для create
            p.sendMessage("§cArena not exist");
            return false;
        }

        return true;

    }

}

/* if (args[0].equalsIgnoreCase("join") ) {
            if (args.length == 2) {
                if (AM.getArena(args[1]) == null) {
                   p.sendMessage(Messages.GetMsg("arena_not_exist"));
                    return true;
                }

                AM.addPlayer((Player) p, args[1]);
            } else {
                    p.sendMessage(Messages.GetMsg("arg_err"));
            }

            return true;
            
        }    
            
            
            
            
            
     if ( !p.isOp()) return false; 
 */
 /*  if (args[0].equalsIgnoreCase("create")) {
               
                if ((args.length != 6 )) {
                    sender.sendMessage( "§cInsufficiant Arguments! Proper use of the command goes like this: ");
                    sender.sendMessage( "§b/tw create <name> <mode> <size_x> <size_z> <down_id>");
                    sender.sendMessage( "§b<mode> §emust be string §bwool, glass or clay §7Input any chars for default value (wool)");
                    sender.sendMessage( "§b<size_x> <size_z> §emust be number §bfrom 2 to 64 §7Set to 0 for default value (16)");
                    sender.sendMessage( "§b<down_id> §emust be number §bfrom 1 to 300 §7Set to 0 for default mat.GLOWSTONE (89)");
                    return false;
                    
                } else if ( !Main.isNumber(args[3]) || !Main.isNumber(args[4]) || !Main.isNumber(args[5]) ) {
                            sender.sendMessage( "§c<size_x>,<size_z> фтв <down_id> must be Integer! Set to 0 for default walue!");
                            return false;
                } else {
                
                    if (AM.ArenaExist(args[1])) 
                        sender.sendMessage("Arena with this name allready exist!");
                    else if (!AM.CanCreate((Player) sender))
                        sender.sendMessage("In this world allready exist arena!");
                    else if ( p.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName()))
                        sender.sendMessage("In this world can`t create arena! This world is globall lobby");
                    else if ( p.getLocation().getY() < 10)
                        sender.sendMessage("Arena location(Y) mus bee highter, than 10!");
                    else if ( p.getLocation().getY() >250)
                        sender.sendMessage("Arena location(Y) mus bee lowest, than 250!");
                    else {
                                
                        AM.createArena( args[1], ((Player) sender).getLocation(), args[2], Byte.valueOf(args[3]), Byte.valueOf(args[4]), Byte.valueOf(args[5]) );
                        sender.sendMessage("§fArena §b"+args[1]+" §fcreated! You can save it anytime with command §b/tw saveall");
                    }
                    
                }

                return true;
                
              
                
                
                
                
                
                
    } else  if ((args[0].equalsIgnoreCase("delete") )) {
                
                    if ((args.length != 2 )) {
                        sender.sendMessage( "§cInsufficiant Arguments! Proper use of the command goes like this: /tw delete <arena name>");
                    } else {
                        if (AM.getArena(args[1]) == null) {
                            p.sendMessage(Messages.GetMsg("arena_not_exist"));
                            return true;
                            
                        } else {
                            sender.sendMessage( "§cTry to delete Arena...");
                            AM.DeleteArena (args[1], (Player) sender);
                        }
                    }
                    return true;
                    

                   
                
                
                
                
                
                
                    
    } else  if ((args[0].equalsIgnoreCase("list") )) {
                
                sender.sendMessage(Messages.GetMsg("arenas_list_command_result") + AM.getAllArenas().size() );
                
                for ( Entry<String, Arena> e : AM.getAllArenas().entrySet() ) {
                    sender.sendMessage( "§e" + e.getKey()+" :§5"+ e.getValue().getStateAsString()   );
                }
                
                return true;
                    
                
                
                
                
                
                
                    
    } else  if (args[0].equalsIgnoreCase("reset") ) {
                    
                    if ((args.length != 2 )) {
                        sender.sendMessage( "§cInsufficiant Arguments! Proper use of the command goes like this: /tw delete <arena name>");
                    } else {
                        if (AM.getArena(args[1]) == null) {
                            p.sendMessage(Messages.GetMsg("arena_not_exist"));
                            return true;
                            
                        } else  {
                            sender.sendMessage( "§bTry to reset Arena...");
                            AM.ResetArena(args[1], (Player) sender);
                        }
                    }
                    return true;
                    
                     
                    
                                
                        
                        
                        
                        
                        
    } else if (args[0].equalsIgnoreCase("start")) {
                        
                        if ((args.length != 2  )) {
                            sender.sendMessage( "§cInsufficiant Arguments. Proper use of the command goes like this: /tw start <arena name>");
                        } else {
                            if (AM.getArena(args[1]) == null) {
                                p.sendMessage(Messages.GetMsg("arena_not_exist"));
                                return true;
                            }
                            if (!AM.getArena(args[1]).IsJonable()) {
                                sender.sendMessage( "§cYou can't start an arena - arena in game!");
                                return true;
                            }
                            AM.startArenaByName(args[1]);
                            sender.sendMessage( Messages.GetMsg("arenas_start_command_result"));
                        }
                        return true;
                        
                        
                        
                        
                        
   }  else if (args[0].equalsIgnoreCase("setlobby") ) {
                            if ((args.length != 2 )) {
                                sender.sendMessage( "§cInsufficiant Arguments. Proper use of the command goes like this: /tw setlobby <arena name>");
                            } else {
                                if (AM.getArena(args[1]) == null) {
                                    p.sendMessage(Messages.GetMsg("arena_not_exist"));
                                    return true;
                                }
                                if (AM.getArena(args[1]).getPlayers().size() > 0) {
                                    sender.sendMessage( "§cThat arena has players in it. Please stop the game before performing this operation.");
                                    return true;
                                }
                                AM.setArenaLobby(((Player) sender).getLocation(), args[1]);
                                sender.sendMessage( "§bSuccessfully set arena lobby!");
                            }
                            return true;
                            
                            
                            
                            
                            
    }  else if (args[0].equalsIgnoreCase("setminpl")) {
                            
            if (args.length == 3) {
                if (AM.getArena(args[1]) == null) {
                    p.sendMessage(Messages.GetMsg("arena_not_exist"));
                    return true;
                }
                byte i;

                try { i = Byte.valueOf(args[2]); } 
                catch (Exception exception) {  sender.sendMessage( "§cThe third argument is not a valid int!"); return true; }

                AM.getArena(args[1]).setMinPlayers(i);
                sender.sendMessage( "§bSuccessfully set the min players to " + args[2] + "!");
            } else {
                sender.sendMessage( "§cProper usage of the command goes like this: /tw setminpl <arena name> <int>");
            }
            return true;
            
            
           
            
            
    }else if (args[0].equalsIgnoreCase("setround")) {
                            
            if (args.length == 3) {
                if (AM.getArena(args[1]) == null) {
                    p.sendMessage(Messages.GetMsg("arena_not_exist"));
                    return true;
                }
                byte i;

                try { i = Byte.valueOf(args[2]); } 
                catch (Exception exception) {  sender.sendMessage( "§cThe third argument is not a valid int!"); return true; }

                AM.getArena(args[1]).setMaxRound(i);
                sender.sendMessage( "§bSuccessfully set the setround count to " + args[2] + "!");
            } else {
                sender.sendMessage( "§cProper usage of the command goes like this: /tw setround <arena name> <int>");
            }
            return true;
            
            
            
            
            
            
    }else if (args[0].equalsIgnoreCase("setdiff")) {
                            
            if (args.length == 3) {
                if (AM.getArena(args[1]) == null) {
                    p.sendMessage(Messages.GetMsg("arena_not_exist"));
                    return true;
                }
                byte i;

                try { i = Byte.valueOf(args[2]); } 
                catch (Exception exception) {  sender.sendMessage( "§cThe third argument is not a valid int from 1 to 3!"); return true; }

                AM.getArena(args[1]).setDifficulty(i);
                sender.sendMessage( "§bSuccessfully set the Difficulty to " + args[2] + "!");
            } else {
                sender.sendMessage( "§cProper usage of the command goes like this: /tw setdiff <arena name> <int>");
            }
            return true;
            
             
            
            
            
            
    } else if (args[0].equalsIgnoreCase("setforce")) {
                            
            if (args.length == 3) {
                if (AM.getArena(args[1]) == null) {
                    p.sendMessage(Messages.GetMsg("arena_not_exist"));
                    return true;
                }
                byte i;

                try { i = Byte.valueOf(args[2]); } 
                catch (Exception exception) {  sender.sendMessage( "§cThe third argument is not a valid int!"); return true; }

                AM.getArena(args[1]).setForce(i);
                sender.sendMessage( "§bSuccessfully set the players to forcestart" + args[2] + "!");
            } else {
                sender.sendMessage( "§cProper usage of the command goes like this: /tw setforce <arena name> <int>");
            }
            return true;
            
           
            
            
            
            
    } else if (args[0].equalsIgnoreCase("saveall")) {

                sender.sendMessage( "§bTry to save data to disk...");
                Files.saveAll();
                sender.sendMessage( "§2Successfully save arenas to disk!");
                
            return true;
            
    }  else Help(p);
            
            
            
            
            
 */
