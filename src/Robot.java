
package org.usfirst.frc.team3470.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
/*import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;*/
//import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.CameraServer;

import org.usfirst.frc.team3470.robot.commands.ExampleCommand;
//import org.usfirst.frc.team3470.robot.subsystems.ExampleSubsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	//public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	Spark m_frontRight= new Spark(0);
	Spark m_rearRight = new Spark(1);
	SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontRight, m_rearRight);

	Spark m_frontLeft = new Spark(3);
	Spark m_rearLeft = new Spark(2);
	SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontLeft, m_rearLeft);
	
	Victor m_scissorlift1 = new Victor(4);
	Victor m_scissorlift2 = new Victor(5);
	Victor m_scissorlift3 = new Victor(6);
	SpeedControllerGroup scissorLift = new SpeedControllerGroup(m_scissorlift1, m_scissorlift2, m_scissorlift3);
	
	Spark m_claw1 = new Spark(7);
	Spark m_claw2 = new Spark(8);
	Spark m_clawLift = new Spark(9);
	
	DifferentialDrive myRobot;
	Joystick joystick;
	XboxController controller;
    Timer timer;
    Timer shooterTimer;
    Timer autoTimer;
    
    DigitalInput light;
    DigitalInput light2;
    Spark armOne;
    Spark armTwo;
    
    Victor shooter;
    Victor loader;
	
	@Override
	public void robotInit() {
		try{
			oi = new OI();
		
			chooser.addDefault("Default Auto", new ExampleCommand());
			// chooser.addObject("My Auto", new MyAutoCommand());
			SmartDashboard.putData("Auto mode", chooser);
			myRobot = new DifferentialDrive(m_left, m_right);
			controller = new XboxController(0);
			joystick = new Joystick(1);
			timer = new Timer();
			timer.start();
			shooterTimer = new Timer();
			shooterTimer.start();
			
			autoTimer = new Timer();
			autoTimer.start();
		
			light = new DigitalInput(0);
			light2 = new DigitalInput(1);
			//armOne = new Spark(4);
			//armTwo = new Spark(5);
			
			//shooter = new Victor(6);
			//loader = new Victor(7);
			
			CameraServer.getInstance().startAutomaticCapture();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	boolean driveStraight = false;
	@Override
	public void autonomousInit() {
		autonomousCommand = chooser.getSelected();
		autoTimer.reset();
		driveStraight = false;

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	double step = 0;
	int autoDir = 1;
	boolean driveYet = false;
	@Override
	public void autonomousPeriodic() {
		myRobot.arcadeDrive(1,0);
		
	}

	boolean shotToggle = false;

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		timer.reset();
		shooterTimer.reset();
		shotToggle = false;
		if (autonomousCommand != null)
			autonomousCommand.cancel();
	}

	/**
	 * This function is called periodically during operator control
	 */
	double LX = 0;
	double LY = 0;
	double RY = 0;
	double strafeSpeed = 0;
	double DeadZone = 0;
	double LDist = 0;
	double RDist = 0;
	boolean LB = false;
	boolean RB = false;
	//double speedValue = 0.5;
	//double speedIncrement = 0;
	//boolean rJoystick = false;
	boolean spinning = false;
	boolean spinning90 = false;
	//boolean sFwd = false;
	//int climbSpeed = 0;
	//int dPadAngle = 0;
	//boolean AB = false;
	//boolean XB = false;
	//double[] wheelSpeeds = new double[4];
	double shootSpeed = 1;
	int loadDir = -1;
	boolean shotButton = false;
	double RT = 0;
	double LT = 0;
	double speedValue = 0;
	double scissorSpeed = 0;
	boolean start = false;

	@Override
	public void teleopPeriodic() {
		try {
		//Scheduler.getInstance().run();
		DeadZone = SmartDashboard.getNumber("DeadZone", 0.15);
		LX = controller.getX(GenericHID.Hand.kLeft);
		LY = controller.getY(GenericHID.Hand.kLeft);
		RY = controller.getY(GenericHID.Hand.kRight);
		//strafeSpeed = controller.getTriggerAxis(GenericHID.Hand.kRight) - controller.getTriggerAxis(GenericHID.Hand.kLeft);
		//AB = controller.getAButton();
		//XB = controller.getXButton();
		start = controller.getStartButton();
		RT = controller.getTriggerAxis(GenericHID.Hand.kRight);
		LT = controller.getTriggerAxis(GenericHID.Hand.kLeft);
		//LDist = Math.sqrt( Math.pow(LX, 2) + Math.pow(LY, 2) );
		//RDist = Math.sqrt( Math.pow(RX, 2) + Math.pow(RY, 2) );
		//rJoystick = controller.getStickButton(GenericHID.Hand.kRight);
		
		//Speed Controls
		if (controller.getBumper(GenericHID.Hand.kLeft)) {
			LB = true;
			if (!LB && speedValue > 0.25) {
				speedValue = speedValue - 0.25;
			}
		}
		else LB = false;
		if (controller.getBumper(GenericHID.Hand.kRight)) {
			RB = true;
			if (!RB && speedValue < 1) {
				speedValue = speedValue + 0.25;
			}
		}
		else RB = false;
		//speedIncrement = speedValue + (controller.getTriggerAxis(GenericHID.Hand.kRight) - controller.getTriggerAxis(GenericHID.Hand.kLeft))*0.1;
		//SmartDashboard.putNumber("speedValue", speedValue);
		
		//Drive Controls
		if (Math.abs(LY) < DeadZone) LY = 0;
		if (Math.abs(LX) < DeadZone) LX = 0;
		if (Math.abs(RY) < DeadZone) RY = 0;
		//if (LY + LX + RX != 0) {
		//	myRobot.mecanumDrive_Cartesian(LY, LX, RX, 0);
		//}
		
		if (LY + LX != 0) {
			myRobot.arcadeDrive(-LY, LX);
		}
		
		if (LT + RT != 0) {
			scissorSpeed = LT - RT;
			scissorLift.set(scissorSpeed);
		}
		
		if (start = true) {
			
		}

		/*// Here is your spinny code. There we go~
		if (controller.getStickButton(GenericHID.Hand.kRight)) {
			timer.reset();
			spinning = true;
		}
		if (spinning == true) {
			
		}
		if (timer.get() > 0.15) {
			spinning = false;
		}
		// Here is your spinny code. There we go~
		if (controller.getStickButton(GenericHID.Hand.kLeft)) {
			timer.reset();
			spinning90 = true;
		}				
		if (spinning90 == true) {
			
		}
		if (timer.get() > 0.075) {					
			spinning90 = false;
		}
		
		SmartDashboard.putBoolean("Light", light.get());
		SmartDashboard.putBoolean("Light 2", light2.get());

		//Arm Controls
		if (controller.getStartButton()) {
			armOne.set(-1);
			armTwo.set(-1);
		}
		else if (controller.getBackButton()) {
			armOne.set(-0.5);
			armTwo.set(-0.5);
		}
		else {
			armOne.set(0);
			armTwo.set(0);
		}*/
		
		/*//Shooter Controls
		shootSpeed = -joystick.getThrottle()/4+0.75;
		
		if (joystick.getTrigger()) {
			loadDir = -1;
		}
		else {
			loadDir = 0;
		}
		if (joystick.getRawButton(2)) {
			if (!shotButton) {
				if (shotToggle) shotToggle = false;
				else shotToggle = true;
				shotButton = true;
			}
		}
		else shotButton = false;
		if (!shotToggle) {
			if (joystick.getRawButton(3)) {
				shootSpeed = -1;
				loadDir = 1;
			}
			else shootSpeed = 0;
		}
		shooter.set(shootSpeed);
		loader.set(loadDir);*/
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		//LiveWindow.run();
	}
} 


