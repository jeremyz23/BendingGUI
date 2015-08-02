package com.strangeone101.bendinggui.menus;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import com.projectkorra.ProjectKorra.BendingPlayer;
import com.projectkorra.ProjectKorra.Element;
import com.projectkorra.ProjectKorra.GeneralMethods;
import com.projectkorra.ProjectKorra.CustomEvents.PlayerChangeElementEvent;
import com.projectkorra.ProjectKorra.CustomEvents.PlayerChangeElementEvent.Result;
import com.strangeone101.bendinggui.BendingGUI;
import com.strangeone101.bendinggui.MenuItem;
import com.strangeone101.bendinggui.MenuBase;
import com.strangeone101.bendinggui.RunnablePlayer;


public class MenuEditElements extends MenuBase 
{
	protected MenuBase prev;
	protected OfflinePlayer player;
	protected Player openPlayer;
	
	public MenuEditElements(OfflinePlayer player, MenuBase previousMenu) 
	{
		super("Edit " + player.getName() + "'" + (player.getName().endsWith("s") ? "" : "s") + " Elements", 3);
		this.prev = previousMenu;
		this.player = player;
		update();
	}

	/**Called to update menu*/
	public void update()
	{
		this.getInventory().clear();
		
		BendingPlayer p = GeneralMethods.getBendingPlayer(player.getName());
		List<Element> list = Arrays.asList(new Element[] {Element.Fire, Element.Water, Element.Chi, Element.Earth, Element.Air});
		
		for (int i = 0; i < list.size(); i++)
		{
			this.addMenuItem(this.getBendingItem(list.get(i)), 9 + 2 + i);
			if (p.hasElement(list.get(i)))
			{
				//this.addGlow(this.getBendingItem(list.get(i)), 9 + 2 + i);
			}
		}
		this.addMenuItem(this.getBackItem(), 18);
		this.addMenuItem(this.getRemoveAllItem(), 26);
	}
	
	public MenuItem getRemoveAllItem()
	{
		final OfflinePlayer p = this.player;
		final MenuConfirm confirm = new MenuConfirm(this, new RunnablePlayer() {

			@Override
			public void run(Player player1) 
			{
				Bukkit.dispatchCommand(openPlayer, "bending remove " + p.getName());
				switchMenu(player1, prev);
			}
			
		}, new RunnablePlayer() {

			@Override
			public void run(Player player) 
			{
				switchMenu(player, getInstance());
			}
			
		}, Arrays.asList(new String[] {ChatColor.GRAY + "Are you sure you want to remove all",ChatColor.GRAY + player.getName() + "'s elements? This can't",ChatColor.GRAY + "be undone"}),
		Arrays.asList(new String[] {ChatColor.GRAY + "Return back to the element menu"}));
		
		MenuItem item = new MenuItem(ChatColor.RED + "Remove All Elements", new MaterialData(Material.BARRIER)) 
		{
			@Override
			public void onClick(Player player) 
			{
				switchMenu(player, confirm);
			}
		};
		item.addDescription(ChatColor.GRAY + "This will remove all elements this");
		item.addDescription(ChatColor.GRAY + "can bend");
		return item;
	}
	
	public MenuEditElements getInstance()
	{
		return this;
	}
	
	public MenuItem getBendingItem(final Element element)
	{
		MenuItem item;
		final OfflinePlayer player = this.player;
		final ChatColor c = element == Element.Air ? ChatColor.GRAY : (element == Element.Chi ? ChatColor.GOLD : (element == Element.Earth ? ChatColor.GREEN : (element == Element.Fire ? ChatColor.RED : (element == Element.Water ? ChatColor.BLUE : ChatColor.LIGHT_PURPLE))));
		item = new MenuItem(ChatColor.BOLD + "" + c + "" + element.toString().toUpperCase(), this.getElementData(element)) {
			@Override
			public void onClick(Player playerwhoclicked) 
			{
				BendingPlayer p = GeneralMethods.getBendingPlayer(player.getName());
				if (!p.hasElement(element))
				{
					if (playerwhoclicked.hasPermission("bending.admin.add"))
					{
						if (player instanceof Player)
							((Player)player).sendMessage(ChatColor.GREEN + "You are now " + (element == Element.Air || element == Element.Earth ? "an " : "a ") + c + element.toString().toLowerCase() + ChatColor.GREEN + (element == Element.Chi ? "!" : " bender!"));
						p.addElement(element);
						GeneralMethods.saveElements(p);
						if (player instanceof Player)
						{
							Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeElementEvent((Player)player, (Player)player, element, Result.ADD));
						}
						playerwhoclicked.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " is now " + (element == Element.Air || element == Element.Earth ? "an " : "a ") + c + element.toString().toLowerCase() + ChatColor.GREEN + (element == Element.Chi ? "!" : " bender!"));
						update();
					}
					else
					{
						playerwhoclicked.sendMessage(ChatColor.RED + "You don't have permission to change people's bending!");
						closeMenu(playerwhoclicked);
					}
				}
	
				update();
			}
		};
		boolean b = GeneralMethods.getBendingPlayer(player.getName()).hasElement(element);
		String notBender = ChatColor.GRAY + "Click to make " + ChatColor.YELLOW + player.getName() + ChatColor.RESET + ChatColor.GRAY + " " + (element == Element.Air || element == Element.Earth ? "an " : "a ") + c + element.toString() + ChatColor.RESET + ChatColor.GRAY + " bender!";
		String isBender = ChatColor.GRAY + "This player is already a " + c + element.toString() + ChatColor.RESET + ChatColor.GRAY + " bender! You must remove all bending before you can add this again!";
		//item.setDescriptions(Arrays.asList((b ? isBender.split("\n") : notBender.split("\n"))));
		item.setDescriptions(BendingGUI.getDescriptions(b ? isBender : notBender, ChatColor.GRAY, 58));
		if (GeneralMethods.getBendingPlayer(player.getName()).hasElement(element))
		{
			item.setEnchanted(true);
		}
		return item;
	}
	
	public MenuItem getBackItem()
	{
		String s = this.prev == null ? "Exit Menu" : "Back";
		MenuItem item = new MenuItem(ChatColor.YELLOW + s, new MaterialData(Material.ARROW)) {
			@Override
			public void onClick(Player player) 
			{
				if (prev != null)
				{
					switchMenu(player, prev);
					return;
				}
				closeMenu(player);
			}
		};
		String s1 = this.prev == null ? "Exit menu and return to your normal inventory" : "Return to the previous menu";
		item.addDescription(ChatColor.DARK_GRAY + s1);
		return item;
	}
	
	public MaterialData getElementData(Element type)
	{
		switch (type)
		{
			case Fire:
				return new MaterialData(Material.BLAZE_POWDER);
			case Water:
				return new MaterialData(Material.WATER_BUCKET);
			case Chi:
				return new MaterialData(Material.STICK);
			case Earth:
				return new MaterialData(Material.GRASS);
			case Air:
				return new MaterialData(Material.STRING);
			default:
				return new MaterialData(Material.REDSTONE);
		}
	}
	
	@Override
	public void openMenu(Player player) 
	{
		this.openPlayer = player;
		super.openMenu(player);
	}
}