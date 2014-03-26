package edu.macalester.comp124.hw5;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author baylor
 */
public class Game
{
	public Map map;
	public Agent player, moeTitan, armouredTitan, colossalTitan, femaleTitan;	// change this to whatever subclass player is
    public Item newBlades, food, airPressure;
	//--- A list of all the agents in the game (player, NPCs, monsters, etc.)
	//--- We need to know this so we know who to draw and so that we can ask
	//---	each turn what they want to do
	public List<Agent> agents = new LinkedList<>();
    Random dice = new Random();


	public Game()
	{
		//--- Load a map
		map = new Map("main");
        System.out.println("Map size "+map.getWidth()+","+map.getHeight());

		//--- Create a player, stick him in the top left corner
		player = new Player();
		player.x = 2;
		player.y = 2;

        int min = map.getHeight();
        int max = map.getWidth();
        int randomX = dice.nextInt(max);
        int randomY = dice.nextInt(min)-5;
        moeTitan = new Titan("MoeTitan", "Moe Titan", map, getRandomX(), getRandomY(), false);
        randomX = dice.nextInt(max);
        randomY = dice.nextInt(min);
        armouredTitan = new Titan("ArmouredTitan","Armoured Titan", map, getRandomX(), getRandomY(), false);
        randomX = dice.nextInt(max);
        randomY = dice.nextInt(min);
        femaleTitan = new Titan("FemaleTitan", "Female Titan", map, getRandomX(), getRandomY(), false);
        randomX = dice.nextInt(max);
        randomY = dice.nextInt(min);
        colossalTitan = new Titan("ColossalTitan", "Colossal Titan", map, getRandomX(), getRandomY(), false);

		//--- Add the player to the agents list. This list controls
		agents.add(player);
        agents.add(moeTitan);
        agents.add(armouredTitan);
        agents.add(femaleTitan);
        agents.add(colossalTitan);
	}

    protected int getRandomX()
    {
        int x = dice.nextInt(map.getWidth());
        if (x <= 0)
        {
            x = 0;
        }
        if (x >= map.getWidth()-1)
        {
            x = map.getWidth()-1;
        }
        return x;
    }

    protected int getRandomY()
    {
        int x = dice.nextInt(map.getHeight());
        if (x <= 0)
        {
            x = 0;
        }
        if (x >= map.getHeight()-1)
        {
            x = map.getHeight()-1;
        }
        return x;
    }
	public void movePlayer(int x, int y)
	{
		//--- Don't do anything if the move is illegal
        String newTerrainType = map.terrain[x][y];
        if (!map.passibility.containsKey(newTerrainType))
        {
		//--- Move the player to the new spot
            String newItemType = map.items[x][y];
            if(null!=newItemType)
            {
                if(newItemType.equals("r"))
                {
                    System.out.println("You have received rations of bread");
                    player.health += 8;
                    System.out.println("Your health is now: " + player.health);
                }
                if(newItemType.equals("a"))
                {
                    System.out.println("You have reloaded air pressure");
                    player.health += 10;
                    System.out.println("Your health is now: " + player.health);
                }
                if(newItemType.equals("b"))
                {
                    System.out.println("You have reloaded your blades");
                    player.health += 15;
                    System.out.println("Your health is now: " + player.health);
                }
                map.items[x][y] = null;
            }

		    player.x = x;
		    player.y = y;
        }
		//--- Assuming this is the last thing that happens in the round,
		//---	start a new round. This lets the other agents make their moves.
        nextTurn();
	}

	public void movePlayer(char direction)
	{
		switch(direction)
		{
			case 'n':
				movePlayer(player.x, player.y-1);
				break;
			case 's':
				movePlayer(player.x, player.y+1);
				break;
			case 'e':
				movePlayer(player.x+1, player.y);
				break;
			case 'w':
				movePlayer(player.x-1, player.y);
				break;
		}
	}

	/**
	 * Run a turn. Did the player run into an enemy? An item?
	 * What do the other agents (NPCs, monsters, etc.) want to do?
	 */
	private void nextTurn()
	{
        for (int i=0; i<agents.size(); i++)
        {
            Agent agent = agents.get(i);
            // see if this person and the player are at the same location
            // if they are, see if it's an enemy

            agent.think();
            if(player.x == agent.x && player.y == agent.y)
            {
                if (agent instanceof Titan)    // is this a subclass of Enemy?
                {
                    //Load image of titan
                    System.out.print("You are now fighting " + agent.name);
                    System.out.print("\n");
                    fight(agent.name);
                }
            }
        }
	}

    private Scanner userInput;

    public void fight(String name)
    {
        userInput = new Scanner(System.in);
        //--- Do whatever you do in a turn
        System.out.println("What do you do?: 1)Attack 2)Run 3)Regroup 4)Cry");

        for(int i = 1; i < agents.size(); i++)
        {
            int inChar = userInput.nextInt();
            if (inChar == 1)
            {
                System.out.println("You have decided to attack");
                attack(name);
                break;
            }
            if (inChar == 2)
            {
                System.out.println("You have decided to run");
                break;
            }
            if (inChar == 3)
            {
                System.out.println("You have decided to regroup");
                break;
            }
            if (inChar ==4)
            {
                System.out.println("You have decided to cry");
                player.health -= 2;
                break;
            }
        }
    }

    public void attack(String name)
    {
        System.out.println("Which attack do you want to use?" + "(0-" + (Player.attacks.size()-1) + ")");
        for (int i=0; i <Player.attacks.size(); i++)
        {
            Attack attack = Player.attacks.get(i);
            System.out.print(" (" + i + ") " + attack.name + "\n");
        }
        Scanner userInput = new Scanner(System.in);
        int choice = userInput.nextInt();
        Attack selectedAttack = Player.attacks.get(choice);
        System.out.print("You have decided to " + selectedAttack.name + "\n");
        if(titanDefeat(choice, name))
        {
            System.out.print("You have defeated the " + name + "\n");
            //--take titan out of the agents list because it shouldn't regenerate
        }
        if(counterAttack(choice, name))
        {
            System.out.println("You have served your duty to humanity.");

        }
    }

    public boolean counterAttack(int choice, String name)
    {
        int j = dice.nextInt(Titan.attacks.size());
        boolean dead = false;
        for (int i=0; i <Player.attacks.size(); i++)
        {
            if(!titanDefeat(choice, name))
            {
                Attack attack = Titan.attacks.get(j);
                System.out.println(name + " has decided to ");
                System.out.println(attack.name);
                player.health -= attack.damage;
                System.out.println("Your health is now " + player.health);
                if(player.health <= 0)
                {
                    System.out.println("You died.");
                    dead = true;
                }
                break;
            }
        }
        return dead;
    }

    public boolean titanDefeat(int choice, String name)
    {
        boolean defeat = false;
        Attack selectedAttack = Player.attacks.get(choice);
        if(name.equals("Moe Titan"))
        {
            moeTitan.health -= selectedAttack.damage;
            //System.out.print(moeTitan.health + "\n");
            if(moeTitan.health <= 0)
            {
                defeat = true;
            }
        }
        if(name.equals("Armoured Titan"))
        {
            armouredTitan.health -= selectedAttack.damage;
            //System.out.print(armouredTitan.health + "\n");
            if(armouredTitan.health <= 0)
            {
                defeat = true;
            }
        }
        if(name.equals("Female Titan"))
        {
            femaleTitan.health -= selectedAttack.damage;
            //System.out.print(femaleTitan.health + "\n");
            if(femaleTitan.health <= 0)
            {
                defeat = true;
            }
        }
        if(name.equals("Colossal Titan"))
        {
            colossalTitan.health -= selectedAttack.damage;
            //System.out.print(colossalTitan.health + "\n");
            if(colossalTitan.health <= 0)
            {
                defeat = true;
            }
        }
        return defeat;
    }

}
