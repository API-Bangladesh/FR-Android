package com.shamim.frremoteattendence.blink_Detection;



public class Blink_Dectection
{
    private static boolean prevLeftEyeOpen = true;
    private  static boolean prevRightEyeOpen = true;
    private static boolean leftEyeClosed = false;
    private static boolean rightEyeClosed = false;
    private  static int blinkCount = 0;

    public static int check_Blink_Detection(Float leftEyeOpen,Float rightEyeOpen)
    {
        if (leftEyeOpen != null && rightEyeOpen != null) {
            boolean isLeftEyeOpen = leftEyeOpen > 0.3f;
            boolean isRightEyeOpen = leftEyeOpen > 0.3f;
            // Detect a blink when both eyes transition from closed to open
            if (!isLeftEyeOpen && prevLeftEyeOpen && !isRightEyeOpen && prevRightEyeOpen) {
                // Blink detected
                leftEyeClosed = true;
                rightEyeClosed = true;
            }
            // Check if both eyes transition from closed to open again
            if (isLeftEyeOpen && !prevLeftEyeOpen && isRightEyeOpen && !prevRightEyeOpen && leftEyeClosed && rightEyeClosed) {
                // Blink completed
                blinkCount++;
                if (blinkCount==3)
                {
                    blinkCount=0;
                }
                leftEyeClosed = false;
                rightEyeClosed = false;
            }
            // Update previous eye states
            prevLeftEyeOpen = isLeftEyeOpen;
            prevRightEyeOpen = isRightEyeOpen;
        }


    return blinkCount;
    }
}
