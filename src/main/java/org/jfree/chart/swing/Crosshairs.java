package org.jfree.chart.swing;

import org.jfree.chart.internal.CloneUtils;
import org.jfree.chart.plot.Crosshair;

import java.util.ArrayList;
import java.util.List;

public class Crosshairs {
    /** Storage for the crosshairs along the x-axis. */
    private List<Crosshair> xCrosshairs;

    /** Storage for the crosshairs along the y-axis. */
    private List<Crosshair> yCrosshairs;

    public Crosshairs() {
        this.xCrosshairs = new ArrayList<Crosshair>();
        this.yCrosshairs = new ArrayList<Crosshair>();
    }

    public void addXCrosshair(Crosshair crosshair) {
        this.xCrosshairs.add(crosshair);
    }

    public void addYCrosshair(Crosshair crosshair) {
        this.yCrosshairs.add(crosshair);
    }

    public void setXCrosshairs(List<Crosshair> crosshairs) {
        this.xCrosshairs = crosshairs;
    }

    public void setYCrosshairs(List<Crosshair> crosshairs) {
        this.yCrosshairs = crosshairs;
    }

    public boolean removeXCrosshair(Crosshair crosshair) {
        return this.xCrosshairs.remove(crosshair);
    }

    public boolean removeYCrosshair(Crosshair crosshair) {
        return this.yCrosshairs.remove(crosshair);
    }

    public boolean isXCrosshairsEmpty() {
        return this.xCrosshairs.isEmpty();
    }

    public boolean isYCrosshairsEmpty() {
        return this.yCrosshairs.isEmpty();
    }

    public List<Crosshair> getXCrosshairs() {
        return new ArrayList<>(xCrosshairs);
    }

    public List<Crosshair> getYCrosshairs() {
        return new ArrayList<>(yCrosshairs);
    }

    public Crosshairs clone() {
        Crosshairs clone = new Crosshairs();
        clone.xCrosshairs = (List) CloneUtils.cloneList(this.xCrosshairs);
        clone.yCrosshairs = (List) CloneUtils.cloneList(this.yCrosshairs);
        return clone;
    }
}
