/*
 *  Copyright Nguyen Duong Kim Hao @ 2014
 */
package laserbot_galileo;

import java.util.logging.Level;
import static laserbot_galileo.Common.BACKWARD;
import static laserbot_galileo.Common.FORWARD;
import static laserbot_galileo.Common.HIGH;
import static laserbot_galileo.Common.LOW;
import static laserbot_galileo.Common.PULSE_DELAY;
import static laserbot_galileo.Common.STEPPER_X_CLK;
import static laserbot_galileo.Common.STEPPER_Y_CLK;
import static laserbot_galileo.Common.delayMicros;
import laserbot_galileo.io.GPIO;
import static laserbot_galileo.io.GPIO.digitalWrite;
import laserbot_galileo.io.StepperControl;
import static laserbot_galileo.io.StepperControl.STEP_DRAWLINE_XY_DELAY;
import static laserbot_galileo.io.StepperControl.curX;
import static laserbot_galileo.io.StepperControl.curY;
import static laserbot_galileo.io.StepperControl.dirXY;
import static laserbot_galileo.io.StepperControl.isNotLimitX;
import static laserbot_galileo.io.StepperControl.isNotLimitY;

/**
 *
 * @author KimHao
 */
public class CalibrateXY {

    private static final int MAX_X = 14000;
    private static final int MAX_Y = 6000;

    public static void main(String[] args) {
        FileLog.begin();
        GPIO.begin();
        StepperControl.begin();
        StepperControl.findHome();

        StepperControl.STEP_DRAWLINE_X_DELAY = 750;
        StepperControl.STEP_DRAWLINE_Y_DELAY = 750;
        StepperControl.STEP_DRAWLINE_XY_DELAY = 600;
        FileLog.log(Level.INFO, "Begin CalibrateXY !");
        System.out.println("Begin CalibrateXY !");
        for (int i = 0; i < 1000; i++) {
            StepperControl.drawLine(MAX_X, MAX_Y);
            System.out.println("Reach MAX XY");
            findHome();
        }
    }

    public static void findHome() {
        dirXY(BACKWARD);
        boolean isRunX = true;
        boolean isRunY = true;
        int countX = 0, countY = 0;
        while (isRunX || isRunY) {
            if (isRunX && isNotLimitX()) {
                digitalWrite(STEPPER_X_CLK, HIGH);
            } else {
                isRunX = false;
            }
            if (isRunY && isNotLimitY()) {
                digitalWrite(STEPPER_Y_CLK, HIGH);
            } else {
                isRunY = false;
            }
            delayMicros(PULSE_DELAY);
            if (isRunX) {
                digitalWrite(STEPPER_X_CLK, LOW);
                countX++;
            }
            if (isRunY) {
                digitalWrite(STEPPER_Y_CLK, LOW);
                countY++;
            }
            if (!(isRunX && isRunY)) {
                delayMicros(STEP_DRAWLINE_XY_DELAY);
            }
        }
        curX = curY = 0;
        dirXY(FORWARD);
        FileLog.log(Level.CONFIG, "Calibrate Complete", new String[][]{
            {"countX", String.valueOf(countX)},
            {"countY", String.valueOf(countY)},
            {"MAX_X", String.valueOf(MAX_X)},
            {"MAX_Y", String.valueOf(MAX_Y)}
        });
        System.out.println(String.format("X: %d/%d\tY: %d/%d", countX, MAX_X, countY, MAX_Y));
    }

}
