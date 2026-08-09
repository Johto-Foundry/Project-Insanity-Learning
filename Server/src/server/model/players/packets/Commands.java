package server.model.players.packets;

import server.Config;
import server.Connection;
import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;
import server.util.Misc;
import server.world.WorldMap;


/**
 * Commands
 **/
public class Commands implements PacketType {

	
	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
	String playerCommand = c.getInStream().readString();
	if(Config.SERVER_DEBUG)
		Misc.println(c.playerName+" playerCommand: "+playerCommand);
		if (playerCommand.startsWith("/") && playerCommand.length() > 1) {
			if (c.clanId >= 0) {
				System.out.println(playerCommand);
				playerCommand = playerCommand.substring(1);
				Server.clanChat.playerMessageToClan(c.playerId, playerCommand, c.clanId);
			} else {
				if (c.clanId != -1)
					c.clanId = -1;
				c.sendMessage("You are not in a clan.");
			}
			return;
		}
		if(c.playerRights >= 0) {
			
			if (playerCommand.equalsIgnoreCase("players")) {
				c.sendMessage("There are currently "+PlayerHandler.getPlayerCount()+ " players online.");
			}
			/*if (playerCommand.startsWith("shop")) {
				c.getShops().openShop(Integer.parseInt(playerCommand.substring(5)));
			}*/
			if (playerCommand.startsWith("changepassword") && playerCommand.length() > 15) {
				c.playerPass = playerCommand.substring(15);
				c.sendMessage("Your password is now: " + c.playerPass);			
			}
			
			if (playerCommand.startsWith("ioi")) {
				String[] args = playerCommand.split(" ");
				c.getItems().itemOnInterface(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			}
			
			
			
			/*if (playerCommand.startsWith("setlevel")) {
				if (c.inWild())
					return;
				for (int j = 0; j < c.playerEquipment.length; j++) {
					if (c.playerEquipment[j] > 0) {
						c.sendMessage("Take off your shit idiot..");
						return;
					}
				}
				try {
				String[] args = playerCommand.split(" ");
				int skill = Integer.parseInt(args[1]);
				int level = Integer.parseInt(args[2]);
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level)+5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				} catch (Exception e){}
			}
			if (playerCommand.equals("spec")) {
				if (!c.inWild())
					c.specAmount = 10.0;
			}
			if (playerCommand.startsWith("object")) {
				String[] args = playerCommand.split(" ");				
				c.getPA().object(Integer.parseInt(args[1]), c.absX, c.absY, 0, 10);
			}
			if (playerCommand.equals("gwd")) {
				c.getPA().movePlayer(2905, 3611, 4);			
			}
			if (playerCommand.equals("gwd2")) {
				c.getPA().movePlayer(2905, 3611, 8);			
			}
			if (playerCommand.equals("gwd3")) {
				c.getPA().movePlayer(2905, 3611, 12);			
			}
			
			if (playerCommand.startsWith("tele")) {
				String[] arg = playerCommand.split(" ");
				if (arg.length > 3)
					c.getPA().movePlayer(Integer.parseInt(arg[1]),Integer.parseInt(arg[2]),Integer.parseInt(arg[3]));
				else if (arg.length == 3)
					c.getPA().movePlayer(Integer.parseInt(arg[1]),Integer.parseInt(arg[2]),c.heightLevel);
			}
			
			if (playerCommand.equalsIgnoreCase("mypos")) {
				c.sendMessage("X: "+c.absX);
				c.sendMessage("Y: "+c.absY);
			}
			if (playerCommand.startsWith("item")) {
				if (c.inWild())
					return;
				try {
				String[] args = playerCommand.split(" ");
				if (args.length == 3) {
					int newItemID = Integer.parseInt(args[1]);
					int newItemAmount = Integer.parseInt(args[2]);
					if ((newItemID <= 20000) && (newItemID >= 0)) {
						c.getItems().addItem(newItemID, newItemAmount);
						System.out.println("Spawned: " + newItemID + " by: " + c.playerName);
					} else {
						c.sendMessage("No such item.");
					}
				} else {
					c.sendMessage("Use as ::item 995 200");
				}
				} catch (Exception e) {
				
				}*/
			}
			
		
		
		
		if(c.playerRights >= 3) {

			if (playerCommand.equalsIgnoreCase("restore")) {
				for (int skill = 0; skill < c.playerLevel.length; skill++) {
					c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
					c.getPA().refreshSkill(skill);
				}

				c.specAmount = 10.0;
				c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]);

				c.sendMessage("Your health, Prayer, stats and special attack have been restored.");
			}

			if (playerCommand.equalsIgnoreCase("gear")
					|| playerCommand.toLowerCase().startsWith("gear ")) {

				String[] args = playerCommand.trim().split("\\s+");

				if (args.length != 2) {
					c.sendMessage("Use as ::gear melee, ::gear range or ::gear mage");
					return;
				}

				String style = args[1].toLowerCase();

				if (!style.equals("melee")
						&& !style.equals("range")
						&& !style.equals("mage")) {
					c.sendMessage("Use as ::gear melee, ::gear range or ::gear mage");
					return;
				}

				c.getCombat().resetPlayerAttack();
				c.getPA().resetAutocast();

				/*
				 * Permanently delete all currently equipped items.
				 * Inventory items are not affected.
				 */
				for (int slot = 0; slot < c.playerEquipment.length; slot++) {
					if (c.playerEquipment[slot] >= 0) {
						c.getItems().deleteEquipment(
								c.playerEquipment[slot],
								slot
						);
					}
				}

				if (style.equals("melee")) {
					c.getItems().wearItem(10828, 1, c.playerHat);      // Helm of neitiznot
					c.getItems().wearItem(6570, 1, c.playerCape);      // Fire cape
					c.getItems().wearItem(6585, 1, c.playerAmulet);    // Amulet of fury
					c.getItems().wearItem(4151, 1, c.playerWeapon);    // Abyssal whip
					c.getItems().wearItem(11724, 1, c.playerChest);    // Bandos chestplate
					c.getItems().wearItem(11283, 1, c.playerShield);   // Dragonfire shield
					c.getItems().wearItem(11726, 1, c.playerLegs);     // Bandos tassets
					c.getItems().wearItem(7462, 1, c.playerHands);     // Barrows gloves
					c.getItems().wearItem(11732, 1, c.playerFeet);     // Dragon boots
					c.getItems().wearItem(773, 1, c.playerRing);       // Testing ring

					c.sendMessage("Melee testing loadout equipped.");

				} else if (style.equals("range")) {
					c.getItems().wearItem(11664, 1, c.playerHat);      // Void ranger helm
					c.getItems().wearItem(10499, 1, c.playerCape);     // Ava's accumulator
					c.getItems().wearItem(6585, 1, c.playerAmulet);    // Amulet of fury
					c.getItems().wearItem(9185, 1, c.playerWeapon);    // Rune crossbow
					c.getItems().wearItem(8839, 1, c.playerChest);     // Void knight top
					c.getItems().wearItem(3842, 1, c.playerShield);    // Unholy book
					c.getItems().wearItem(8840, 1, c.playerLegs);      // Void knight robe
					c.getItems().wearItem(8842, 1, c.playerHands);     // Void knight gloves
					c.getItems().wearItem(2577, 1, c.playerFeet);      // Ranger boots
					c.getItems().wearItem(773, 1, c.playerRing);       // Testing ring
					c.getItems().wearItem(9244, 10000, c.playerArrows);// Dragon bolts (e)

					c.sendMessage("Ranged testing loadout equipped.");

				} else if (style.equals("mage")) {
					c.getItems().wearItem(10342, 1, c.playerHat);      // 3rd age mage hat
					c.getItems().wearItem(2414, 1, c.playerCape);      // Zamorak cape
					c.getItems().wearItem(10344, 1, c.playerAmulet);   // 3rd age amulet
					c.getItems().wearItem(4675, 1, c.playerWeapon);    // Ancient staff
					c.getItems().wearItem(4712, 1, c.playerChest);     // Ahrim's robetop
					c.getItems().wearItem(6889, 1, c.playerShield);    // Mage's book
					c.getItems().wearItem(4714, 1, c.playerLegs);      // Ahrim's robeskirt
					c.getItems().wearItem(7462, 1, c.playerHands);     // Barrows gloves
					c.getItems().wearItem(6920, 1, c.playerFeet);      // Infinity boots
					c.getItems().wearItem(773, 1, c.playerRing);       // Testing ring

					// Ice Barrage supplies.
					c.getItems().addItem(560, 10000); // Death runes
					c.getItems().addItem(565, 10000); // Blood runes
					c.getItems().addItem(555, 10000); // Water runes

					// Switch to Ancient Magicks for the Ancient staff.
					c.playerMagicBook = 1;
					c.setSidebarInterface(6, 12855);

					c.sendMessage("Magic testing loadout equipped.");
				}

				c.specAmount = 10.0;
				c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]);
			}
			
			/*if (playerCommand.startsWith("task")) {
				c.taskAmount = -1;
				c.slayerTask = 0;
			}
			
			if (playerCommand.startsWith("starter")) {
				c.getDH().sendDialogues(100, 945);			
			}*/
			if (playerCommand.equalsIgnoreCase("mypos")) {
				c.sendMessage("X: "+c.absX);
				c.sendMessage("Y: "+c.absY);
			}
			if (playerCommand.startsWith("reloaddrops")) {
				Server.npcDrops = null;
				Server.npcDrops = new server.model.npcs.NPCDrops();
				/*for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Client c2 = (Client)Server.playerHandler.players[j];
						c2.sendMessage("[" + c.playerName + "] " + "NPC Drops have been reloaded.");
					}
				}*/
			}

			if (playerCommand.toLowerCase().startsWith("announce ")) {
				for (int j = 0; j < Server.playerHandler.players.length; j++) {
					if (Server.playerHandler.players[j] != null) {
						Client c2 = (Client) Server.playerHandler.players[j];
						c2.sendMessage("[" + c.playerName + "]: "
								+ playerCommand.substring(9));
					}
				}
			}
			if (playerCommand.startsWith("reloadshops")) {
				Server.shopHandler = new server.world.ShopHandler();
			}
			
			if (playerCommand.startsWith("fakels")) {
				int item = Integer.parseInt(playerCommand.split(" ")[1]);
				Server.clanChat.handleLootShare(c, item, 1);
			}
			
			if (playerCommand.startsWith("interface")) {
				String[] args = playerCommand.split(" ");
				c.getPA().showInterface(Integer.parseInt(args[1]));
			}
			if (playerCommand.startsWith("gfx")) {
				String[] args = playerCommand.split(" ");
				c.gfx0(Integer.parseInt(args[1]));
			}
			if (playerCommand.startsWith("update")) {
				String[] args = playerCommand.split(" ");
				int a = Integer.parseInt(args[1]);
				PlayerHandler.updateSeconds = a;
				PlayerHandler.updateAnnounced = false;
				PlayerHandler.updateRunning = true;
				PlayerHandler.updateStartTime = System.currentTimeMillis();
			}

			if (playerCommand.startsWith("item")) {
				try {
					String[] args = playerCommand.split(" ");
					if (args.length == 3) {
						int newItemID = Integer.parseInt(args[1]);
						int newItemAmount = Integer.parseInt(args[2]);
						if ((newItemID <= 20000) && (newItemID >= 0)) {
							c.getItems().addItem(newItemID, newItemAmount);
							c.updateWeight();
						} else {
							c.sendMessage("No such item.");
						}
					} else {
						c.sendMessage("Use as ::pickup 995 200");
					}
				} catch(Exception e) {
					
				}
			}
			
			if (playerCommand.equals("Vote")) {
				for (int j = 0; j < Server.playerHandler.players.length; j++)
					if (Server.playerHandler.players[j] != null) {
						Client c2 = (Client)Server.playerHandler.players[j];
						c2.getPA().sendFrame126("www.google.ca", 12000);
					}
			}


			if (playerCommand.equalsIgnoreCase("debug")) {
				Server.playerExecuted = true;
			}
			
			if (playerCommand.startsWith("interface")) {
				try {	
					String[] args = playerCommand.split(" ");
					int a = Integer.parseInt(args[1]);
					c.getPA().showInterface(a);
				} catch(Exception e) {
					c.sendMessage("::interface ####"); 
				}
			}
			
			if(playerCommand.startsWith("www")) {
				c.getPA().sendFrame126(playerCommand,0);			
			}
			

		
			
			
			
			if (playerCommand.startsWith("xteleto")) {
				String name = playerCommand.substring(8);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (Server.playerHandler.players[i] != null) {
						if (Server.playerHandler.players[i].playerName.equalsIgnoreCase(name)) {
							c.getPA().movePlayer(Server.playerHandler.players[i].getX(), Server.playerHandler.players[i].getY(), Server.playerHandler.players[i].heightLevel);
						}
					}
				}			
			}
			

			
			if(playerCommand.startsWith("npc") && c.playerName.equalsIgnoreCase("Sanity")) {
				try {
					int newNPC = Integer.parseInt(playerCommand.substring(4));
					if(newNPC > 0) {
						Server.npcHandler.spawnNpc(c, newNPC, c.absX, c.absY, 0, 0, 120, 7, 70, 70, false, false);
						c.sendMessage("You spawn a Npc.");
					} else {
						c.sendMessage("No such NPC.");
					}
				} catch(Exception e) {
					
				}			
			}
			
			
			if (playerCommand.startsWith("ipban")) { // use as ::ipban name
				try {
					String playerToBan = playerCommand.substring(6);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Connection.addIpToBanList(Server.playerHandler.players[i].connectedFrom);
								Connection.addIpToFile(Server.playerHandler.players[i].connectedFrom);
								c.sendMessage("You have IP banned the user: "+Server.playerHandler.players[i].playerName+" with the host: "+Server.playerHandler.players[i].connectedFrom);
								Server.playerHandler.players[i].disconnected = true;
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}
			
			if (playerCommand.startsWith("ban") && playerCommand.charAt(3) == ' ') { // use as ::ban name
				try {	
					String playerToBan = playerCommand.substring(4);
					Connection.addNameToBanList(playerToBan);
					Connection.addNameToFile(playerToBan);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Server.playerHandler.players[i].disconnected = true;
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}
			
			if (playerCommand.startsWith("unban")) {
				try {	
					String playerToBan = playerCommand.substring(6);
					Connection.removeNameFromBanList(playerToBan);
					c.sendMessage(playerToBan + " has been unbanned.");
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}
			}
			if (playerCommand.startsWith("anim")) {
				String[] args = playerCommand.split(" ");
				c.startAnimation(Integer.parseInt(args[1]));
				c.getPA().requestUpdates();
			}
			
			if (playerCommand.startsWith("mute")) {
				try {	
					String playerToBan = playerCommand.substring(5);
					Connection.addNameToMuteList(playerToBan);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Client c2 = (Client)Server.playerHandler.players[i];
								c2.sendMessage("You have been muted by: " + c.playerName);
								break;
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}			
			}
			if (playerCommand.startsWith("ipmute")) {
				try {	
					String playerToBan = playerCommand.substring(7);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Connection.addIpToMuteList(Server.playerHandler.players[i].connectedFrom);
								c.sendMessage("You have IP Muted the user: "+Server.playerHandler.players[i].playerName);
								Client c2 = (Client)Server.playerHandler.players[i];
								c2.sendMessage("You have been muted by: " + c.playerName);
								break;
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}			
			}
			if (playerCommand.startsWith("unipmute")) {
				try {	
					String playerToBan = playerCommand.substring(9);
					for(int i = 0; i < Config.MAX_PLAYERS; i++) {
						if(Server.playerHandler.players[i] != null) {
							if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(playerToBan)) {
								Connection.unIPMuteUser(Server.playerHandler.players[i].connectedFrom);
								c.sendMessage("You have Un Ip-Muted the user: "+Server.playerHandler.players[i].playerName);
								break;
							} 
						}
					}
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}			
			}
			if (playerCommand.startsWith("unmute")) {
				try {	
					String playerToBan = playerCommand.substring(7);
					Connection.unMuteUser(playerToBan);
				} catch(Exception e) {
					c.sendMessage("Player Must Be Offline.");
				}			
			}

		}
	}
}
		
		
		
		
		
		
		

