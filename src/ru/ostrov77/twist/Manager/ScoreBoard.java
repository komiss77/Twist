package ru.ostrov77.twist.Manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.World;

import ru.ostrov77.twist.Main;
import ru.ostrov77.twist.Objects.Arena;



public class ScoreBoard {
 
     //private static HashMap < UUID, Scoreboard > scoreStore = new HashMap<>();
     //private static List <String> state = new ArrayList<>();
     
     
     
     
public static void StartScore () {     
         
    
    (new BukkitRunnable() {
            @Override
            public void run() {
                
                
                for ( World w: Bukkit.getWorlds()) {

                    Scoreboard score = Get_score(AM.getArenaByWorld(w.getName()));
                    
                    for ( Player p : w.getPlayers() ) {
                       p.setScoreboard(score); 
                   }
                }
      
            }}).runTaskTimer(Main.GetInstance(), 20L, 20L);  
    
    
    
  /*  
     (new BukkitRunnable() {
            @Override
            public void run() {
                
            state.clear();
            AM.getAllArenas().entrySet().stream().forEach((e) -> {
                state.add ( "§e" + e.getKey()+" : "+ e.getValue().getStateAsString()   );
            });
 
            }
        }).runTaskTimer(Main.GetInstance(), 19L, 100L);  
    
*/
     
}
 





  private static Scoreboard Get_score ( Arena ar ) {  
     
        
            final Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            final Objective registerNewObjective = newScoreboard.registerNewObjective("vote", "dummy");
            registerNewObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            
            registerNewObjective.setDisplayName(Main.Prefix);
            
            registerNewObjective.getScore("§a-----------------").setScore(16); 
            
            
            
            
            
            
            
            if ( ar==null ) {        //если в лобби
                
                int pos=15;
                
                //state.clear();
                //AM.getAllArenas().entrySet().stream().forEach((e) -> {
                //    state.add ( "§e" + e.getKey()+" : "+ e.getValue().getStateAsString()   );
                //});

                for (Arena arena:AM.getAllArenas().values()) { 
                    registerNewObjective.getScore("§e" + arena.getName()+" : "+ arena.state.displayColor+arena.state.name() ).setScore(pos); 
                    pos--;
                }
                
                registerNewObjective.getScore("").setScore(pos); 
                pos--;
                registerNewObjective.getScore("§a------------------").setScore(pos); 
                
            
            
            
            
            } else {                                                    //если в мир игры
                
                
                
            //registerNewObjective.getScore( ar.getScoreTimer() ).setScore(999);
            
            //registerNewObjective.getScore("").setScore(998); 
            
            //int pos=13;
            
            for (Player p : ar.getLobby().getWorld().getPlayers()) {
                
                registerNewObjective.getScore(ar.GetScoreStatus(p)).setScore(p.getLevel());
                //pos--;
                
            }

            
            //registerNewObjective.getScore("").setScore(-1); 
            //pos--;
            registerNewObjective.getScore("§a------------------").setScore(-1); 
            registerNewObjective.getScore( ar.getScoreTimer() ).setScore(-2);
                
                
            }

           
            return newScoreboard;
          // scoreStore.put(p.getUniqueId(), newScoreboard);
    
}
 
 
 

 
 
 

 
 
 
 
 
 
 
 
 
}
    














/*        
	public void updateScoreboard(String arena) {
		try {
			ScoreboardManager manager = Bukkit.getScoreboardManager();

			int count = 0;
			for (Player p_ : arenap.keySet()) {
				if (arenap.get(p_).equalsIgnoreCase(arena)) {
					count++;
				}
			}

			int lostcount = 0;
			for (Player p : arenap.keySet()) {
				if (arenap.get(p).equalsIgnoreCase(arena)) {
					if (lost.containsKey(p)) {
						lostcount++;
					}
				}
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (arenap.containsKey(p)) {
					if (arenap.get(p).equalsIgnoreCase(arena)) {
						Scoreboard board = manager.getNewScoreboard();

						Objective objective = board.registerNewObjective("test", "dummy");
						objective.setDisplaySlot(DisplaySlot.SIDEBAR);

						objective.setDisplayName("§cТ§3В§dИ§5С§6Т§e!"); // <- ColorMatch

						try {
							objective.getScore(Bukkit.getOfflinePlayer(" §8-  ")).setScore(5);
							objective.getScore(Bukkit.getOfflinePlayer("§aАрена:")).setScore(4);
							objective.getScore(Bukkit.getOfflinePlayer("§d" + arena)).setScore(3);
							objective.getScore(Bukkit.getOfflinePlayer(" §8- ")).setScore(2);
							objective.getScore(Bukkit.getOfflinePlayer("§aИграют:")).setScore(1);
							objective.getScore(Bukkit.getOfflinePlayer(Integer.toString(count - lostcount) + " из " + Integer.toString(count))).setScore(0);
						} catch (Exception e) {
							//
						}

						p.setScoreboard(board);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

        
        
	public void removeScoreboard(String arena) {
		try {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard sc = manager.getNewScoreboard();

			sc.clearSlot(DisplaySlot.SIDEBAR);

			getLogger().info("Removing scoreboard.");

			for (Player p : Bukkit.getOnlinePlayers()) {
				p.setScoreboard(sc);
				if (arenap.containsKey(p)) {
					if (arenap.get(p).equalsIgnoreCase(arena)) {
						getLogger().info(p.getName());
						p.setScoreboard(sc);
						p.setScoreboard(null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeScoreboard(String arena, Player p) {
		try {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard sc = manager.getNewScoreboard();

			sc.clearSlot(DisplaySlot.SIDEBAR);
			p.setScoreboard(sc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

        
   */     
        