package com.kenny.KImageBrowser;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class OnKTouchListener implements View.OnTouchListener
{
	private Matrix matrix = new Matrix();
	public Matrix savedMatrix = new Matrix();
	public PointF start = new PointF();
	public PointF mid = new PointF();
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	float oldDist = 1f;
	private static String TAG = "OnKTouchListener";

	
	public boolean onTouch(View v, MotionEvent event)
	{
		ImageView view = (ImageView) v;
		// matrix = view.getImageMatrix();
		// Dump touch event to log
		dumpEvent(event);
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			// Rect rect = view.getDrawable().getBounds();
			// float[] values = new float[9];
			// matrix.getValues(values);
			// ImageState mapState = new ImageState();
			// mapState.setLeft(values[2]);
			// mapState.setTop(values[5]);
			// mapState.setRight(mapState.getLeft()
			// + rect.width() * values[0]);
			// mapState.setBottom(mapState.getTop()
			// + rect.height() * values[0]);
			//
			// if (mapState.getLeft() < event.getX()
			// && event.getX() < mapState
			// .getRight()
			//
			// && mapState.getTop() < event
			// .getY()
			// && event.getY() < mapState
			// .getBottom())
			// {
			// start.set(event.getX(), event.getY());
			// Log.d(TAG, "mode=DRAG");
			// mode = DRAG;
			// }
			// else
			// {
			// mode = NONE;
			// Log.d(TAG, "mode= No DRAG");
			//
			// return true;
			// }
			// break;
			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f)
			{
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				Log.d(TAG, "mode= ZOOM");
				Log.d(TAG, "mode=ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			if (mode != NONE)
			{
				mode = NONE;
			} else
			{
				return false;
			}
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG)
			{
				Rect rect = view.getDrawable().getBounds();
				float[] values = new float[9];
				matrix.getValues(values);
				ImageState mapState = new ImageState();
				mapState.setLeft(values[2]);
				mapState.setTop(values[5]);
				mapState.setRight(mapState.getLeft() + rect.width() * values[0]);
				mapState.setBottom(mapState.getTop() + rect.height()
						* values[0]);
				float dx = event.getX() - start.x;
				float dy = event.getY() - start.y;
//				P.v("dx="+dx+"dy="+dy);
//				if(0<mapState.getRight()+dx||0<mapState.getBottom()+dy)
//				{
//					P.v("move:false");
//					return false;
//				}
//				P.v("move:true");
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(dx, dy);
			} else if (mode == ZOOM)
			{
				float newDist = spacing(event);
				Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f)
				{
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			} else
			{
				return false;
			}
			break;
		default:
			return false;
		}

		view.setImageMatrix(matrix);
		return true; // indicate event was handled
	}

	public void setMatrix(Matrix matrix)
	{
		this.matrix = matrix;
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event)
	{
		String names[] =
		{ "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN",
				"POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP)
		{
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++)
		{
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d(TAG, sb.toString());
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event)
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event)
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	class ImageState
	{
		private float left;
		private float top;
		private float right;
		private float bottom;

		public float getLeft()
		{
			return left;
		}

		public void setLeft(float left)
		{
			this.left = left;
		}

		public float getTop()
		{
			return top;
		}

		public void setTop(float top)
		{
			this.top = top;
		}

		public float getRight()
		{
			return right;
		}

		public void setRight(float right)
		{
			this.right = right;
		}

		public float getBottom()
		{
			return bottom;
		}

		public void setBottom(float bottom)
		{
			this.bottom = bottom;
		}
	}
}
