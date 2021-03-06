package ru.ostrov77.twist.Manager;

import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ostrov77.twist.Main;
import ru.ostrov77.twist.UniversalListener;


public class Commands extends JavaPlugin {

    private static final String c_pref = "tw";
    
    
    
public static boolean handleCommand(CommandSender sender, Command cmd, String s, String[] args) {
        
        if ( !(sender instanceof Player) ) { sender.sendMessage("Not console commad!"); return false; }
        if ( !cmd.getName().equalsIgnoreCase("tw") && !cmd.getName().equalsIgnoreCase("twist")) return false;
        
        Player p= (Player) sender;
        

        if (args.length == 0) {
            Help(p);
            return false;
        }
        
        
        
        
        
    switch (args[0]) {
            
            
            case "join":
                if (!CheckArgs (p, args, 2, false, true)) break;
                    AM.addPlayer((Player) p, args[1]);
                break;
            
            case "leave":
                AM.GlobalPlayerExit((Player) p);
                UniversalListener.lobbyJoin(p, Bukkit.getWorld("lobby").getSpawnLocation() );
                break;
            
                

            case "create":
                if (CheckArgs (p, args, 6, true, false)) {
                    if ( !Main.isNumber(args[3]) || !Main.isNumber(args[4]) || !Main.isNumber(args[5]) ) {
                        p.sendMessage( "§c<size_x>, <size_z> and <down> must be Integer! Use 0 for default walue!");
                        return false;
                    }
                    if ( p.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) {
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
                    if ( p.getLocation().getY() < 10) {
                        p.sendMessage("§cArena location(Y) must be highter, than 10!");
                        return false;
                    }
                    if ( p.getLocation().getY() >250) {
                        p.sendMessage("§cArena location(Y) must be lowest, than 250!");
                        return false;
                    }
                    if ( !args[2].equals("clay") && !args[2].equals("glass") ) {
                        args[2]="wool";
                        p.sendMessage("§aUse default field material - wool");
                    }
                    if ( args[3].equals("0")) {
                        args[3]="16";
                        p.sendMessage("§aUse default x-size (16)");
                    }
                    if ( args[4].equals("0")) {
                        args[4]="16";
                        p.sendMessage("§aUse default z-size (16)");
                    }
                    if ( args[5].equals("0")) {
                        args[5]="89";
                        p.sendMessage("§aUse down default material - GLOWSTONE (89)");
                    }
                    if ( !net.minecraft.world.level.block.Block.getByCombinedId( Integer.valueOf(args[5])).getMaterial().isSolid()  ) {
                        p.sendMessage("§cThe down material (bu ID) must be SOLID!!");
                        return false;
                    }
                        p.sendMessage("§eGenerate arena field.. Please, wait..");
                        AM.createArena( args[1], ((Player) sender).getLocation(), args[2], Byte.valueOf(args[3]), Byte.valueOf(args[4]), Byte.valueOf(args[5]) );
                        sender.sendMessage("§fArena §b"+args[1]+" §fcreated! You can save it anytime with command §b/tw saveall");

                } else {
                    p.sendMessage( "§cInsufficiant Arguments! Proper use of the command goes like/ this: ");
                    p.sendMessage( "§b"+c_pref+" create <name> <mode> <size_x> <size_z> <down_id>");
                    p.sendMessage( "§b<mode> §emust be string §bwool, glass or clay §7Input any chars for default value (wool)");
                    p.sendMessage( "§b<size_x> <size_z> §emust be number §bfrom 2 to 64 §7Set to 0 for default value (16)");
                    p.sendMessage( "§b<down_id> §emust be number §bfrom 1 to 300 §7Set to 0 for default mat.GLOWSTONE (89)");
                }
                break;
            
                
                
                
                
            case "delete":
                if (!CheckArgs (p, args, 2, false, true)) break;
                    p.sendMessage( "§cTry to delete Arena...");
                    AM.DeleteArena (args[1], (Player) p);
                break;
            
                
                
            case "list":
                p.sendMessage(Messages.GetMsg("arenas_list_command_result") + AM.getAllArenas().size() );
                AM.getAllArenas().entrySet().stream().forEach((e) -> {
                    sender.sendMessage( "§e" + e.getKey()+" :§5"+ e.getValue().state.name()   );
                });
                break;
            
                
                
            case "reset":
                if (!CheckArgs (p, args, 2, false, true)) break;
                    p.sendMessage( "§bTry to reset Arena...");
                    AM.ResetArena(args[1], (Player) sender);
                break;
            
                
                
            case "start":
                if (!CheckArgs (p, args, 2, false, true)) break;
                        AM.startArenaByName( p, args[1] );
                break;
            
                
                
            case "setlobby":
                if ( !CheckArgs (p, args, 2, true, true) ) break;
                    if (AM.getArena(args[1]).getPlayers().size() > 0) {
                        p.sendMessage( "§cThat arena has players in it. Please perform reset command before this operation.");
                        return false;
                    }
                    AM.setArenaLobby(((Player) sender).getLocation(), args[1]);
                    p.sendMessage( "§bSuccessfully set arena lobby!");
                break;
            
                
                
            case "setminpl":
                if ( !CheckArgs (p, args, 3, true, true) ) break;
                    if ( !Main.isNumber(args[2]) ) {p.sendMessage( "§c"+args[2]+" must be Integer!");return false;}
                    AM.getArena(args[1]).setMinPlayers( Byte.valueOf( args[2] ) );
                sender.sendMessage( "§bSuccessfully set the min players to " + args[2] + "!");
                break;
            
                
                
            case "setround":
                if ( !CheckArgs (p, args, 3, true, true) ) break;
                    if ( !Main.isNumber(args[2]) ) {p.sendMessage( "§c"+args[2]+" must be Integer!");return false;}
                    AM.getArena(args[1]).setMaxRound( Byte.valueOf( args[2] ) );
                sender.sendMessage( "§bSuccessfully set the round count to " + args[2] + "!");
                break;
            
                
                
            case "setdiff":
                if ( !CheckArgs (p, args, 3, true, true) ) break;
                    if ( !Main.isNumber(args[2]) ) {p.sendMessage( "§c"+args[2]+" must be Integer!");return false;}
                    AM.getArena(args[1]).setDifficulty( Byte.valueOf( args[2] ) );
                sender.sendMessage( "§bSuccessfully set the Difficulty to " + args[2] + "!");
                break;
            
                
                
            case "setforce":
                if ( !CheckArgs (p, args, 3, true, true) ) break;
                    if ( !Main.isNumber(args[2]) ) {p.sendMessage( "§c"+args[2]+" must be Integer!");return false;}
                    AM.getArena(args[1]).setForce( Byte.valueOf( args[2] ) );
                sender.sendMessage( "§bSuccessfully set the players for force start to " + args[2] + "!");
                break;
            
                
                
            case "setshow":
                if ( !CheckArgs (p, args, 3, true, true) ) break;
                    if ( !Main.isNumber(args[2]) ) {p.sendMessage( "§c"+args[2]+" must be Integer!");return false;}
                    AM.getArena(args[1]).setShow(Byte.valueOf( args[2] ) );
                sender.sendMessage( "§bSuccessfully set the starting show time to " + args[2] + "!");
                break;
            
                
                
            case "saveall":
                sender.sendMessage( "§bTry to save data to disk...");
                Files.saveAll();
                sender.sendMessage( "§2Successfully save arenas to disk!");
                break;
            
            
            
            
            
            default:
                Help(p);
                break;
                
        }
        
                return true;
        
    }    
        




private static void Help (Player p ) {
    
                p.sendMessage("§a-- "+Main.Prefix+" §acommands help --");
                p.sendMessage(""+c_pref+" join <name>");
            if ( p.isOp()) {
                p.sendMessage(""+c_pref+" create <name> <mode> <size_x> <size_z> <down_id>");
                p.sendMessage(""+c_pref+" delete <name>");
                p.sendMessage(""+c_pref+" list");
                p.sendMessage(""+c_pref+" setlobby <name>");
                p.sendMessage(""+c_pref+" setdiff <name> <integer>");
                p.sendMessage(""+c_pref+" setround <name> <integer>");
                p.sendMessage(""+c_pref+" setminpl <name> <integer>");
                p.sendMessage(""+c_pref+" setforce <name> <integer>");
                p.sendMessage(""+c_pref+" start <name>");
                p.sendMessage(""+c_pref+" reset <name>"); 
                p.sendMessage(""+c_pref+" saveall"); 
            }

}
 





private static boolean CheckArgs (Player p, String[] args, int num, Boolean op, Boolean err) {
    
                if ( op && !p.isOp() ) {
                    p.sendMessage("§cThis command only for OP!");
                    return false;
                }
    
                //if ( args.length < num ) { 
                //    p.sendMessage(Messages.GetMsg("arg_err"));
                //    return false; 
                //}
                
                if ( args.length < num && num ==2 ) { 
                    p.sendMessage(Messages.GetMsg("arg_err"));
                    if (err) p.sendMessage( "§aexample: §f/"+c_pref+" "+args[0]+" MyArena");
                    return false; 
                }
                
                if ( args.length < num && num ==3 && err ) { 
                    p.sendMessage(Messages.GetMsg("arg_err"));
                    if (err) p.sendMessage( "§aexample: §f/"+c_pref+" "+args[0]+" MyArena 3");
                    return false; 
                }
                
                if ( args.length < num && num >3 ) {    //фикс для create
                    p.sendMessage(Messages.GetMsg("arg_err"));
                    return false; 
                }
                
                if ( AM.getArena(args[1] ) == null && num <3 ) {       //&& num <3 - фикс для create
                   p.sendMessage(Messages.GetMsg("arena_not_exist"));
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
   
                        
                        
        
                
            

    
    
    
    
    
    
    

