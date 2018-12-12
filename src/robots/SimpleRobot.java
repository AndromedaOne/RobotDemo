package robots;
import java.util.Random;
import static robots.SimpleRobot.RobotDirection.*;

/**
 * This class implements a simple robot very similar to the human robot we acted out in
 * class.  The actuators on the robot are capable of standing up, sitting down, moving, and
 * extending and retracting an arm.  The robot has two sensors, a distance sensor (which is
 * known to be noisy) and a touch sensor, which indicates that the robot is close enough to
 * touch something.
 * <p>
 * The robot must be programmed to travel across the room, press a button (by extending its
 * arm) on a desk, and return to the chair on which it was sitting at the beginning of the
 * match.
 * <p>
 * Here are some details about the match:
 * <ul>
 * <li> The room is 20 meters long (in the North/South) direction by 20 meters wide (East to West). </li>
 * <li> The desk is somewhere in the middle of the room North to South (i.e. 10 meters from the
 * North wall) but an unknown distance from the West wall.</li>
 * <li> It is extremely easy to crash the robot (for example, by running into a wall), so be careful </li>
 * </ul>
 * Those are all the hints you're going to get.
 *
 */
public class SimpleRobot {

	static int ew_width = 20; // width in East/West direction, increasing x corresponds to heading EAST
	static int ns_length = 20; // length in North/South direction, increasing y corresponds to heading SOUTH

	static Random randomGenerator = new Random(); // Random number generator used by simulation
	
	/**
	 * Directions the robot could be facing
	 */
	enum RobotDirection {
		NORTH,
		WEST,
		SOUTH,
		EAST,
	};

	/**
	 * Directions the robot can turn.
	 */
	public enum TurnDirection {
		CLOCKWISE,
		COUNTERCLOCKWISE,
	};
	
	/**
	 * Create a new instance of the robot.  It starts off in the Northwest corner of the room,
	 * facing the North wall, and sitting down.
	 */
	public SimpleRobot() {
		position = new Position();
		position.x = 0;
		position.y = 0;
		direction = NORTH; // Starts off facing the north wall
		isSitting = true; // starts off sitting down
		isArmExtended = false; // Starts off with arm retracted
		buttonPressed = false; // The button has not been pressed yet
	}

	/**
	 * Move the robot with a certain amount of energy.
	 * <p>
	 * The robot doesn't have any brakes, so moving with more energy will make the robot coast
	 * further.
	 * 
	 * @param energy -  Energy to be used to move the robot should move in the direction it is facing.
	 */
	public void move(double energy) {
		// We overshoot by a random amount related to the distance squared
		double overshoot = randomGenerator.nextDouble() * energy * energy / 10.0;
		double distance = energy + overshoot;
		double distanceToObstacle = 0;
		String nameOfObstacle = "something";
		
		if (isSitting) {
			// Can't move when sitting
			crash("Robot can't move when sitting");
		}
		
		if (isArmExtended) {
			// The robot tips over when moving with the arm extended
			crash("Robot tipped over because the arm was extended");
		}
		
		distanceToObstacle = position.distanceToObstacle(desk);
		nameOfObstacle = "desk";
		Position nextObstacle;
		String nextName;
		switch (direction) {
		case NORTH: nextObstacle = northWall; nextName = "North wall"; break;
		case SOUTH: nextObstacle = southWall; nextName = "South wall"; break;
		case EAST:  nextObstacle = eastWall;  nextName = "East wall";  break;
		default:    nextObstacle = westWall;  nextName = "West wall";  break;
		}
		double distanceToNextObstacle = position.distanceToObstacle(nextObstacle);
		if (distanceToNextObstacle < distanceToObstacle) {
			distanceToObstacle = distanceToNextObstacle;
			nameOfObstacle = nextName;
		}
		if (distanceToObstacle >= distance) {
			switch (direction) {
			case NORTH: position.y -= distance; break;
			case SOUTH: position.y += distance; break;
			case EAST:  position.x += distance; break;
			default:    position.x -= distance; break;
			}
		} else {
			crash("Robot crashed into " + nameOfObstacle);
		}
	}
	
	/**
	 * Command the robot to stand up.
	 */
	public void standUp() {
		if (isSitting) {
			isSitting = false;
			System.out.println("Robot is standing up");
		}
	}
	
	/**
	 * Command the robot to sit down.
	 * <p>
	 * Warning.  If it is not near the chair, it will crash!!!
	 * <p>
	 * If the robot sits down on the chair and the button has been pressed, then
	 * the robot's mission has been accomplished and you're done.
	 */
	public void sitDown() {
		if (!position.isNear(chair)) {
			crash("No chair -- robot crashed to the ground when trying to sit down.");
		}
		if (!isSitting) {
			isSitting = true;
			System.out.println("Robot is sitting down");
		}
		if (buttonPressed) {
			System.out.println("MISSION ACCOMPLISHED!");
		} else {
			System.out.println("Button not pressed, mission incomplete");
		}
	}
	
	/**
	 * Turn the robot 90 degrees
	 * @param turn direction to turn, CLOCKWISE or COUNTERCLOCKWISE
	 */
	public void turn(TurnDirection turn) {
		/*
		 * Be aware... the girl who designed this robot is a brilliant young
		 * mathematician who has never seen an analog clock before in her
		 * life.  She assumed a right handed coordinate system and that the
		 * turn direction corresponded to a rotation about the Z axis.  Her
		 * understanding of CLOCKWISE vs COUNTERCLOCKWISE might be different
		 * than yours.
		 */
		if (turn == TurnDirection.CLOCKWISE) {
			switch (direction) {
			case NORTH: direction = WEST;  break;
			case WEST:  direction = SOUTH; break;
			case SOUTH: direction = EAST;  break;
			case EAST:  direction = NORTH; break;
			}
		} else {
			switch (direction) {
			case NORTH: direction = EAST;  break;
			case EAST:  direction = SOUTH; break;
			case SOUTH: direction = WEST;  break;
			case WEST:  direction = NORTH; break;
			}
		}
		System.out.println("Robot turned to direction " + direction);
	}
	
	/**
	 * Determine if the touch sensor has been pressed.
	 * <p>
	 * The touch sensor is pressed if the robot is near a wall or near the desk.
	 * @return the state of the touch sensor.
	 */
	public boolean isTouchSensorPressed() {
		switch (direction) {
		case NORTH: return position.isNear(northWall) || position.isNear(desk, NORTH);
		case SOUTH: return position.isNear(southWall) || position.isNear(desk, SOUTH);
		case EAST:  return position.isNear(eastWall)  || position.isNear(desk, EAST);
		default:    return position.isNear(westWall)  || position.isNear(desk, WEST);
		}
	}
	
	/**
	 * Read the distance sensor.
	 * <p>
	 * Unfortunately, it was mounted too high to see the desk.
	 * <p>
	 * The distance sensor is very noisy.  You may have to average the readings you get from it in order
     * to get a good reading.
	 *
	 * @return estimate of the distance between the robot and the wall it is facing.
	 */
	public double readDistanceSensor() {
		double noise = 2.0 * randomGenerator.nextGaussian();
		switch (direction) {
		case NORTH: return position.y + noise;
		case SOUTH: return ns_length - position.y + noise;
		case EAST:  return ew_width - position.x + noise;
		default:    return position.x + noise;
		}
	}
	
	/**
	 * Extend the arm on the robot.  If the robot is near the desk, this will press the button.
	 */
	public void extendArm() {
		isArmExtended = true;
		if (position.isNear(desk)) buttonPressed = true;
		System.out.println("Arm extended at (" + position.x + ", " + position.y + "), buttonPressed = " + buttonPressed);
	}
	
	/**
	 * Retract the robot arm.
	 */
	public void retractArm() {
		isArmExtended = false;
	}
	
	/* Some possibly useful getters */
	public double xPosition() { return position.x; }
	public double yPosition() { return position.y; }
	public RobotDirection facingDirection() { return direction; }
	
	/********************************************************************************************************
	 * Internal private implementation details.
	 ********************************************************************************************************/
	private class Position {
		double x;
		double y;
		
		public double distanceToObstacle(Position other) {
			if (canSeeObstacle(other)) {
				switch (direction) {
				case NORTH: return Math.abs(other.y - y);
				case SOUTH: return Math.abs(other.y - y);
				case EAST:  return Math.abs(other.x - x);
				default:    return Math.abs(other.x - x);
				}
			} else {
				return Double.POSITIVE_INFINITY;
			}
		}
		public boolean isNear(Position other) {
			if      (other.x < 0) return Math.abs(other.y - y) < 0.5;
			else if (other.y < 0) return Math.abs(other.x - x) < 0.5;
			else                  return Math.abs(other.x - x) < 0.5 && Math.abs(other.y - y) < 0.5;
		}
			
		public boolean isNear(Position other, RobotDirection facingDirection) {
			switch (facingDirection) {
			case NORTH: return y >= other.y && isNear(other);
			case SOUTH: return y <= other.y && isNear(other);
			case EAST:  return x <= other.x && isNear(other);
			default:    return x >= other.x && isNear(other);
			}
		}
		public boolean canSeeObstacle(Position other) {
			switch (direction) {
			case NORTH: return (y >= other.y) && (other.x <= 0 || (Math.abs(x - other.x) < 0.5));
			case SOUTH: return (y <= other.y) && (other.x <= 0 || (Math.abs(x - other.x) < 0.5));
			case EAST:  return (x <= other.x) && (other.y <= 0 || (Math.abs(y - other.y) < 0.5));
			default:    return (x >= other.x) && (other.y <= 0 || (Math.abs(y - other.y) < 0.5));
			}
		}
	};
	Position position;

	Position desk = new Position()  {{ x = 5 + randomGenerator.nextFloat() * 10; y = 10; }}; // desk is somewhere between 5 and 15 meters in from the West wall
	Position chair = new Position() {{ x =  0; y =  0; }};
	
	Position northWall = new Position() {{ x = -1;       y = 0;         }}; // only y coordinate used
	Position southWall = new Position() {{ x = -1;       y = ns_length; }}; // only y coordinate used
	Position westWall  = new Position() {{ x = 0;        y = -1;        }}; // only x coordinate used
	Position eastWall  = new Position() {{ x = ew_width; y = -1;        }}; // only x coordinate used
	
	RobotDirection direction; // This is the direction the robot is facing
	boolean isSitting;
	boolean isArmExtended;
	boolean buttonPressed;
	
	private void crash(String message) {
		System.out.print("CRASH!!");
		System.out.println(": " + message);

		System.exit(-1);
	}
}
