package com.my11heros.bottom_nav;

import com.google.android.material.shape.EdgeTreatment;
import com.google.android.material.shape.ShapePath;

public class TopEdgeTreatment extends EdgeTreatment {

    private static final String TAG = TopEdgeTreatment.class.getSimpleName();

    private int menuSize;
    private int fabMenuIndex;

    private Float fabMargin;
    private Float roundedCornerRadius;
    private Float cradleVerticalOffset;

    private Float fabDiameter;
    private Float horizontalOffset;

    public TopEdgeTreatment(int menuSize, int fabMenuIndex, Float fabMargin, Float roundedCornerRadius, Float cradleVerticalOffset) {
        this.menuSize = menuSize;
        this.fabMenuIndex = fabMenuIndex;

        this.fabMargin = fabMargin;
        this.roundedCornerRadius = roundedCornerRadius;
        this.cradleVerticalOffset = cradleVerticalOffset;

        if (cradleVerticalOffset < 0.0f) {
            throw new IllegalArgumentException("cradleVerticalOffset must be positive.");
        }

        this.horizontalOffset = 0.0f;
    }


    public void getEdgePath(float length,  float interpolation, ShapePath shapePath) {
        super.getEdgePath(length,  interpolation, shapePath);

        if (this.fabDiameter == 0){
            // There is no cutout to draw.
            shapePath.lineTo(length, 0);
            return;
        }

        Float cradleDiameter = this.fabMargin * 2.0f + this.fabDiameter;
        Float cradleRadius = cradleDiameter / 2.0f;
        Float roundedCornerOffset = interpolation * this.roundedCornerRadius;
        Float menuItemWidth = length / menuSize;
        Float fabPositionX = fabMenuIndex * menuItemWidth + menuItemWidth / 2;
        //Float middle = center + this.horizontalOffset;

        Float verticalOffset = interpolation * this.cradleVerticalOffset + (1.0f - interpolation) * cradleRadius;
        Float verticalOffsetRatio = verticalOffset / cradleRadius;

        if (verticalOffsetRatio >= 1.0f) {
            // Vertical offset is so high that there's no curve to draw in the edge, i.e., the fab is
            // actually above the edge so just draw a straight line.
            shapePath.lineTo(length, 0.0f);
            return; // Early exit.
        }

        // Calculate the path of the cutout by calculating the location of two adjacent circles. One
        // circle is for the rounded corner. If the rounded corner circle radius is 0 the corner will
        // not be rounded. The other circle is the cutout.

        // Calculate the X distance between the center of the two adjacent circles using pythagorean
        // theorem.
        Float distanceBetweenCenters = cradleRadius + roundedCornerOffset;
        Float distanceBetweenCentersSquared = distanceBetweenCenters * distanceBetweenCenters;
        Float distanceY = verticalOffset + roundedCornerOffset;
        Float distanceX = (float) Math.sqrt(distanceBetweenCentersSquared - distanceY * distanceY);

        // Calculate the x position of the rounded corner circles.
        Float leftRoundedCornerCircleX = fabPositionX - distanceX;
        Float rightRoundedCornerCircleX = fabPositionX + distanceX;

        // Calculate the arc between the center of the two circles.
        Float cornerRadiusArcLength = (float) Math.toDegrees(Math.atan(distanceX / distanceY));
        Float cutoutArcOffset = 90.0f - cornerRadiusArcLength;

        // Draw the starting line up to the left rounded corner.
        shapePath.lineTo(leftRoundedCornerCircleX - roundedCornerOffset, 0.0f);

        // Draw the arc for the left rounded corner circle. The bounding box is the area around the
        // circle's center which is at `(leftRoundedCornerCircleX, roundedCornerOffset)`.
        shapePath.addArc(
                leftRoundedCornerCircleX - roundedCornerOffset,
                0.0f,
                leftRoundedCornerCircleX + roundedCornerOffset,
                roundedCornerOffset * 2.0f,
                270.0f,
                cornerRadiusArcLength
        );

        // Draw the cutout circle.
        shapePath.addArc(
                fabPositionX - cradleRadius,
                -cradleRadius - verticalOffset,
                fabPositionX + cradleRadius,
                cradleRadius - verticalOffset,
                180.0f - cutoutArcOffset,
                cutoutArcOffset * 2.0f - 180.0f
        );

        // Draw an arc for the right rounded corner circle. The bounding box is the area around the
        // circle's center which is at `(rightRoundedCornerCircleX, roundedCornerOffset)`.
        shapePath.addArc(
                rightRoundedCornerCircleX - roundedCornerOffset,
                0.0f,
                rightRoundedCornerCircleX + roundedCornerOffset,
                roundedCornerOffset * 2.0f,
                270.0f - cornerRadiusArcLength,
                cornerRadiusArcLength
        );

        // Draw the ending line after the right rounded corner.
        shapePath.lineTo(length, 0.0f);
    }

    /**
     * Returns current fab diameter in pixels.
     */
    public float getFabDiameter() {
        return fabDiameter;
    }

    /**
     * Sets the fab diameter the size of the fab in pixels.
     */
    public void setFabDiameter(Float fabDiameter){
        this.fabDiameter = fabDiameter;
    }


}
