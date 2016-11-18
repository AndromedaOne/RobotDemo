package robotdemo;
import robots.SimpleRobot;
import static robots.SimpleRobot.TurnDirection.*;


public class RobotDemo {
	
	static double moveValue = 0.0001;
	
	public static void main(String[] args) {
		SimpleRobot myRobot = new SimpleRobot();
		myRobot.standUp();
		turnAround(myRobot);
		System.out.print("Moving to midpoint ");
		moveToPosition(myRobot,  9.6855);
		myRobot.extendArm();
		myRobot.retractArm();
		myRobot.turn(CLOCKWISE);
		System.out.println("Searching for Desk");
		int count = 0;
		while(!myRobot.isTouchSensorPressed()) {
			myRobot.Move(moveValue);
			++count;
			if((count % 10000) == 0) {
				System.out.print(".");
			}
		}
		System.out.println("");
		myRobot.extendArm();
		myRobot.retractArm();
		System.out.println("Found desk");
		System.out.println("Going home");
		turnAround(myRobot);
		System.out.println("Searching for west wall");
		moveToPosition(myRobot,  0.01);
		myRobot.turn(COUNTERCLOCKWISE);
		System.out.println("Searching for north wall");
		moveToPosition(myRobot,  0.01);
		myRobot.sitDown();
	}
	
	static double averageDistance(SimpleRobot robot)
	{
		int numbOfSamples = 100;
		double avg = 0;
		for(int i = 0; i < numbOfSamples; ++i) {
			avg += robot.readDistanceSensor();
		}
		avg /= numbOfSamples;
		return(avg);
	}
	
	static void turnAround(SimpleRobot robot)
	{
		for(int i = 0; i < 2; ++i) {
			robot.turn(CLOCKWISE);
		}
	}
	
	static void moveToPosition(SimpleRobot robot, double position)
	{
		int count = 0;
		while(averageDistance(robot) > position) {
			robot.Move(moveValue);
			++count;
			if((count % 10000) == 0) {
				System.out.print(".");
			}
		}
		System.out.println("");
	}

}
