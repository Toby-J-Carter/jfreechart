/* ======================================================
 * JFreeChart : a chart library for the Java(tm) platform
 * ======================================================
 *
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Project Info:  https://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ---------------------
 * CrosshairOverlay.java
 * ---------------------
 * (C) Copyright 2011-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   John Matthews, Michal Wozniak;
 *
 */

package org.jfree.chart.swing;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.api.RectangleInsets;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.api.RectangleAnchor;
import org.jfree.chart.api.RectangleEdge;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.internal.CloneUtils;
import org.jfree.chart.internal.Args;
import org.jfree.chart.api.PublicCloneable;

/**
 * An overlay for a {@link ChartPanel} that draws crosshairs on a chart.  If 
 * you are using the JavaFX extensions for JFreeChart, then you should use
 * the {@code CrosshairOverlayFX} class.
 */
public class CrosshairOverlay extends AbstractOverlay implements Overlay,
        PropertyChangeListener, PublicCloneable, Cloneable, Serializable {

    /** Storage for the crosshairs along the x and yaxis. */
    protected Crosshairs crosshairs;

    /**
     * Creates a new overlay that initially contains no crosshairs.
     */
    public CrosshairOverlay() {
        super();
        this.crosshairs = new Crosshairs();
    }

    /**
     * Adds a crosshair against the domain axis (x-axis) and sends an
     * {@link OverlayChangeEvent} to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @see #removeDomainCrosshair(org.jfree.chart.plot.Crosshair)
     * @see #addRangeCrosshair(org.jfree.chart.plot.Crosshair)
     */
    public void addDomainCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        this.crosshairs.addXCrosshair(crosshair);
        crosshair.addPropertyChangeListener(this);
        fireOverlayChanged();
    }

    /**
     * Removes a domain axis crosshair and sends an {@link OverlayChangeEvent}
     * to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @see #addDomainCrosshair(org.jfree.chart.plot.Crosshair)
     */
    public void removeDomainCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        if (this.crosshairs.removeXCrosshair(crosshair)) {
            crosshair.removePropertyChangeListener(this);
            fireOverlayChanged();
        }
    }

    /**
     * Clears all the domain crosshairs from the overlay and sends an
     * {@link OverlayChangeEvent} to all registered listeners (unless there
     * were no crosshairs to begin with).
     */
    public void clearDomainCrosshairs() {
        if (this.crosshairs.isXCrosshairsEmpty()) {
            return;  // nothing to do - avoids firing change event
        }
        for (Crosshair c : getDomainCrosshairs()) {
            this.crosshairs.removeXCrosshair(c);
            c.removePropertyChangeListener(this);
        }
        fireOverlayChanged();
    }

    /**
     * Returns a new list containing the domain crosshairs for this overlay.
     *
     * @return A list of crosshairs.
     */
    public List<Crosshair> getDomainCrosshairs() {
        return new ArrayList<>(this.crosshairs.getXCrosshairs());
    }

    /**
     * Adds a crosshair against the range axis and sends an
     * {@link OverlayChangeEvent} to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     */
    public void addRangeCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        this.crosshairs.addYCrosshair(crosshair);
        crosshair.addPropertyChangeListener(this);
        fireOverlayChanged();
    }

    /**
     * Removes a range axis crosshair and sends an {@link OverlayChangeEvent}
     * to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @see #addRangeCrosshair(org.jfree.chart.plot.Crosshair)
     */
    public void removeRangeCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        if (this.crosshairs.removeYCrosshair(crosshair)) {
            crosshair.removePropertyChangeListener(this);
            fireOverlayChanged();
        }
    }

    /**
     * Clears all the range crosshairs from the overlay and sends an
     * {@link OverlayChangeEvent} to all registered listeners (unless there
     * were no crosshairs to begin with).
     */
    public void clearRangeCrosshairs() {
        if (this.crosshairs.isYCrosshairsEmpty()) {
            return;  // nothing to do - avoids change notification
        }
        for (Crosshair c : getRangeCrosshairs()) {
            this.crosshairs.removeYCrosshair(c);
            c.removePropertyChangeListener(this);
        }
        fireOverlayChanged();
    }

    /**
     * Returns a new list containing the range crosshairs for this overlay.
     *
     * @return A list of crosshairs.
     */
    public List<Crosshair> getRangeCrosshairs() {
        return this.crosshairs.getYCrosshairs();
    }

    /**
     * Receives a property change event (typically a change in one of the
     * crosshairs).
     *
     * @param e  the event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        fireOverlayChanged();
    }

    /**
     * Renders the crosshairs in the overlay on top of the chart that has just
     * been rendered in the specified {@code chartPanel}.  This method is
     * called by the JFreeChart framework, you won't normally call it from
     * user code.
     *
     * @param g2  the graphics target.
     * @param chartPanel  the chart panel.
     */
    @Override
    public void paintOverlay(Graphics2D g2, ChartPanel chartPanel) {
        Shape savedClip = g2.getClip();
        Rectangle2D dataArea = chartPanel.getScreenDataArea();
        g2.clip(dataArea);
        JFreeChart chart = chartPanel.getChart();
        XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        RectangleEdge xAxisEdge = plot.getDomainAxisEdge();
        for (Crosshair ch : getDomainCrosshairs()) {
            if (ch.isVisible()) {
                double x = ch.getValue();
                double xx = xAxis.valueToJava2D(x, dataArea, xAxisEdge);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    CrosshairHelpers.drawVerticalCrosshair(g2, dataArea, xx, ch);
                } else {
                    CrosshairHelpers.drawHorizontalCrosshair(g2, dataArea, xx, ch);
                }
            }
        }
        ValueAxis yAxis = plot.getRangeAxis();
        RectangleEdge yAxisEdge = plot.getRangeAxisEdge();
        for (Crosshair ch : getRangeCrosshairs()) {
            if (ch.isVisible()) {
                double y = ch.getValue();
                double yy = yAxis.valueToJava2D(y, dataArea, yAxisEdge);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    CrosshairHelpers.drawHorizontalCrosshair(g2, dataArea, yy, ch);
                } else {
                    CrosshairHelpers.drawVerticalCrosshair(g2, dataArea, yy, ch);
                }
            }
        }
        g2.setClip(savedClip);
    }

    /**
     * Tests this overlay for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CrosshairOverlay)) {
            return false;
        }
        CrosshairOverlay that = (CrosshairOverlay) obj;
        if (!this.crosshairs.getXCrosshairs().equals(that.crosshairs.getXCrosshairs())) {
            return false;
        }
        if (!this.crosshairs.getYCrosshairs().equals(that.crosshairs.getYCrosshairs())) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of this instance.
     *
     * @return A clone of this instance.
     *
     * @throws java.lang.CloneNotSupportedException if there is some problem
     *     with the cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        CrosshairOverlay clone = (CrosshairOverlay) super.clone();
        clone.crosshairs = this.crosshairs.clone();
        return clone;
    }

}
