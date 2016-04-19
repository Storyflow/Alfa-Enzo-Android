package com.thirdandloom.storyflow.utils;

import android.graphics.Point;

public class MathUtils extends BaseUtils {

    /** Euclidean algorithm
     *
     *  @param x first number
     *  @param y second number
     *  @return greatest common divisior (GCD)
     */
    public static int calculateGreatestCommonDivisior(int x, int y) {
        while (y != 0 ) {
            int temp = x%y;
            x = y;
            y = temp;
        }
        return x;
    }

    public static float calculateMinScaleRatio(int realWidth, int realHeight, int boxWidth, int boxHeight) {
        float scaleHeight = (float) boxWidth / realWidth;
        float scaleWidth = (float) boxHeight / realHeight;
        return Math.min(scaleHeight, scaleWidth);
    }

    public static float calculateMaxScaleRatio(int realWidth, int realHeight, int boxWidth, int boxHeight) {
        float scaleHeight = (float) boxWidth / realWidth;
        float scaleWidth = (float) boxHeight / realHeight;
        return Math.max(scaleHeight, scaleWidth);
    }

    /** Calculate intersection point Y
     *
     * @param start Point(X0, Y0) *X0 - start X; *Y0 - start Y: start straight line point
     * @param end Point(X1, Y1) *X1 - end X; *Y1 - end Y: end straight line point
     *            Also consider in this way:
     *            X0 - is 2d variable end value
     *            X1 - is 2d variable start value
     *            Y0 - is 1st variable start value
     *            Y1 - is 1st variable end value
     *            For example if you have: 1st: [96..100] and 2d: [300..0]
     *              point will looks like:
     *                      start Point:(0, 96)
     *                        end Point:(300, 100)
     *
     * @param X intersection point X
     *          X must be in 2d value interval, otherwise return point Y is invalid
     * @return intersection point Y
     *              it shows how is 1st parameter is changing, when 2d parameter was changed
     */
    public static float getPointY(Point start, Point end, float X) {
        return (((X - start.x)*(end.y-start.y))/(end.x-start.x))+start.y;
    }

    /** Calculate intersection point X
     *
     * @param start Point(X0, Y0) *X0 - start X; *Y0 - start Y: start straight line point
     * @param end Point(X1, Y1) *X1 - end X; *Y1 - end Y: end straight line point
     *            Also consider in this way:
     *            X0 - is 2d variable end value
     *            X1 - is 2d variable start value
     *            Y0 - is 1st variable start value
     *            Y1 - is 1st variable end value
     *            For example if you have: 1st: [96..100] and 2d: [300..0]
     *              point will looks like:
     *                      start Point:(0, 96)
     *                        end Point:(300, 100)
     *
     * @param Y intersection point Y
     *          Y must be in 1st value interval, otherwise return point X is invalid
     * @return intersection point X
     *              it shows how is 2d parameter is changing, when 1st parameter was changed
     */
    public static float getPointX(Point start, Point end, float Y) {
        return (((Y-start.y)*(end.x-start.x))/(end.y-start.y))+start.x;
    }

    /**
     *
     * @param firstVisiblePosition - first visible position in list
     * @param lastVisiblePosition - last visible position in list
     * @return first visible position in list + 1, if 3+ items are visible
     *
     *          for example in list: 0|1|2|3|4|5..
     *          this method getIncrementedFirstPosition(0, 5) return 1
     *          BUT
     *          in list: ..4|5..
     *          this method getIncrementedFirstPosition(0, 5) return 4
     */
    public static int getIncrementedFirstPosition(int firstVisiblePosition, int lastVisiblePosition) {
        int position = lastVisiblePosition - firstVisiblePosition == 0
                ? firstVisiblePosition
                : firstVisiblePosition + 1;
        return position;
    }
}
