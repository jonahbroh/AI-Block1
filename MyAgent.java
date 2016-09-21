
package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;


public class MyAgent extends BasicMarioAIAgent implements Agent
{

	public MyAgent()
	{
		super("MyAgent");
		reset();
	}

	// Does (row, col) contain an enemy?
	public boolean hasEnemy(int row, int col) {
		return enemies[row][col] != 0;
	}

	// Is (row, col) empty?
	public boolean isEmpty(int row, int col) {
		return (levelScene[row][col] == 0);
	}

	// Is (row, col) specifically a wall?
	public boolean isWall(int row, int col) {
		if (levelScene[row][col]  == 1 || levelScene[row][col]  == -60)
			return true;
		else
			return false;
	}

	//Is an anemy nearby?
	public boolean beast_near;

	// Is (row, col) either a wall or an enemy?
	public boolean wallOrBeast(int row, int col) {
		if (isWall(row, col) ||  hasEnemy(row, col))
			return true;
		else
			return false;
	}


	// Display Mario's view of the world
	public void printObservation() {
		System.out.println("**********OBSERVATIONS**************");
		for (int i = 0; i < mergedObservation.length; i++) {
			for (int j = 0; j < mergedObservation[0].length; j++) {
				System.out.print(levelScene[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("************************");
	}


	public boolean doJump()
	{
			beast_near = false;
			boolean jump = false;
			for (int c=9; c<11; c++)	//for each column in front
			{
					if (isWall(9, c ))	//make sure it is safe
						jump = true;
					if (hasEnemy(9,c))
					{
						beast_near = true;
						jump = true;
					}
			}

			for (int c=11; c<19; c++)
			{
				if (hasEnemy(9,c))
					beast_near = true;
			}
			if (isEmpty(10, 10) && isMarioOnGround)	//if mario is on a cliff
				jump = true;
			else if (!isMarioOnGround)
			{
					if ((isEmpty(10,10) || isEmpty(10,9)))
					{
						jump = true;
					}
			 }
			return jump;
	}

	//Is there an enemy below? If so don't fall down
	public boolean beastBelow()
	{
		for (int i = 10; i<13; i++) {
			if (hasEnemy(i,9)) {
				return true;
			}
		}
		return false;
	}

	//Is there an enemy ahead? If so don't walk forward
	public boolean beastBeside()
	{
		for (int r = 9; r<=13; r++){
			if (hasEnemy(9,r))
				return true;
		}
		return false;
	}

	//Is there an enemy above? If so stop moving
	//Bug that turned out fairly effective
	public boolean beastAbove()
	{
		for (int r = 7; r<=9; r++){
			for (int c = 0; c<= 18; c++){	//avoid all things above
				if (hasEnemy(r,c))
					return true;
			}
		}
		return false;
	}

	// Actually perform an action by setting a slot in the action array to be true
	public boolean[] getAction()
	{
		action[Mario.KEY_SPEED] = isMarioAbleToShoot || beast_near;
		action[Mario.KEY_RIGHT] = true;
		if (beastAbove())
		{
			action[Mario.KEY_RIGHT] = false;
			//Attempt to flee if there's an enemy above and beside, turned out not to work particularly well
			// if(beastBeside()){
			// 	action[Mario.KEY_LEFT]=true;
			// }
		}
		if (beastBelow() && !isWall(9,10))
		{
			action[Mario.KEY_JUMP] = false;
			// action[Mario.KEY_RIGHT] = false;
		}
		else
			action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = (doJump()) && (isMarioAbleToJump || !isMarioOnGround);
        //printObservation();

		return action;
	}

	// Do the processing necessary to make decisions in getAction
	public void integrateObservation(Environment environment)
	{
		super.integrateObservation(environment);
    	levelScene = environment.getLevelSceneObservationZ(2);
	}

	// Clear out old actions by creating a new action array
	public void reset()
	{
		action = new boolean[Environment.numberOfKeys];
	}
}
