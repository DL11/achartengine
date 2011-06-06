/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine;

import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.RoundChart;
import org.achartengine.chart.XYChart;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.tools.Pan;

import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * A handler implementation for touch events for older platforms. 
 */
public class TouchHandlerOld implements ITouchHandler {
  /** The chart renderer. */
  private DefaultRenderer mRenderer;
  /** The old x coordinate. */
  private float oldX;
  /** The old y coordinate. */
  private float oldY;
  /** The zoom buttons rectangle. */
  private RectF zoomR = new RectF();
  /** The pan tool. */
  private Pan pan;
  /** The graphical view. */
  private GraphicalView graphicalView;
  
  /**
   * Creates an implementation of the old version of the touch handler.
   * 
   * @param view the graphical view
   * @param chart the chart to be drawn
   */
  public TouchHandlerOld(GraphicalView view, AbstractChart chart) {
    graphicalView = view;
    zoomR = graphicalView.getZoomRectangle();
    if (chart instanceof XYChart) {
      mRenderer = ((XYChart) chart).getRenderer();
      if (mRenderer.isPanEnabled()) {
        pan = new Pan((XYChart) chart);
      }
    } else {
      mRenderer = ((RoundChart) chart).getRenderer();
    }
  }

  public void handleTouch(MotionEvent event) {
    int action = event.getAction();
    if (mRenderer != null && action == MotionEvent.ACTION_MOVE) {
      if (oldX >= 0 || oldY >= 0) {
        float newX = event.getX();
        float newY = event.getY();
        if (mRenderer.isPanEnabled()) {
          pan.apply(oldX, oldY, newX, newY);
        }
        oldX = newX;
        oldY = newY;
        graphicalView.repaint();
      }
    } else if (action == MotionEvent.ACTION_DOWN) {
      oldX = event.getX();
      oldY = event.getY();
      if (mRenderer != null && mRenderer.isZoomEnabled() && zoomR.contains(oldX, oldY)) {
        if (oldX < zoomR.left + zoomR.width() / 3) {
          graphicalView.zoomIn();
        } else if (oldX < zoomR.left + zoomR.width() * 2 / 3) {
          graphicalView.zoomOut();
        } else {
          graphicalView.zoomReset();
        }
      }
    } else if (action == MotionEvent.ACTION_UP) {
      oldX = 0;
      oldY = 0;
    }
  }

}