package scannerexample;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application
{
    public static void main(String[] args)
	{
        Application app = new Application();
		app.fight();
    }

	/**
	 * Ask the player to fight
	 */
	private void fight()
	{
		boolean wantsToQuit = false;
		List<String> combatOptions = getCombatOptions();
		while (!wantsToQuit)
		{
			showCombatOptions(combatOptions);
			int choiceID = getCombatChoice();
			String choice = combatOptions.get(choiceID);
			if ("quit".equalsIgnoreCase(choice))
			{
				wantsToQuit = true;
			}
			else if ("attack".equalsIgnoreCase(choice))
			{
				//--- We could ask them for an attack but i'm lazy
				//---	so just force them to use hyperbeam
				System.out.println("You use Hyperbeam");
			}
			else
			{
				System.out.println("You " + choice.toLowerCase());
			}

			showEnemyMove();
		}
	}

	private List<String> getCombatOptions()
	{
		List<String> options = new ArrayList<>();
		options.add("Quit");
		options.add("Attack");
		options.add("Switch");
		options.add("Item");
		options.add("Run");
		return options;
	}

	private void showCombatOptions(List<String> combatOptions)
	{
		System.out.println("What do you want to do?");
		for (int i = 0; i < combatOptions.size(); i++)
		{
			String option = combatOptions.get(i);
			System.out.println(i+". "+option);
		}
	}

	private int getCombatChoice()
	{
		Scanner keyboard = new Scanner(System.in);
		System.out.print("Choice: ");
		int choice = keyboard.nextInt();
		return choice;
	}

	private void showEnemyMove()
	{
		//--- Let's make sure we win
		System.out.println("You enemy curls in on itself and whimpers");
	}



}
