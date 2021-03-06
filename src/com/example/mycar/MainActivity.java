package com.example.mycar;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	public ImageButton btnup = null;
	public ImageButton btndown = null;
	public ImageButton btnright = null;
	public ImageButton btnleft = null;
	private TextView Text = null;
	private int direct = 0;
	public LinearLayout list = null;
	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	// private IntentFilter intentFilter = null;
	private BluetoothSocket socket = null;
	private PrintStream mPrintStream = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		list = (LinearLayout) findViewById(R.id.list);
		btnup = (ImageButton) findViewById(R.id.Up);
		btnup.setOnTouchListener(listener);
		btnup.getBackground().setAlpha(35);
		btndown = (ImageButton) findViewById(R.id.Down);
		btndown.setOnTouchListener(listener);
		btndown.getBackground().setAlpha(35);
		btnright = (ImageButton) findViewById(R.id.Right);
		btnright.setOnTouchListener(listener);
		btnright.getBackground().setAlpha(35);
		btnleft = (ImageButton) findViewById(R.id.Left);
		btnleft.setOnTouchListener(listener);
		btnleft.getBackground().setAlpha(35);
		Text = (TextView) findViewById(R.id.text);

		if (adapter != null) {
			if (!adapter.isEnabled()) {
				Intent intent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(intent);
				Log.v("Car", "+++ Enabling +++");
			}
			Log.v("Car", "+++ Enabled +++");
		} else {
			System.out.println("bluetooth error !");
		}
		
		Button btn = new Button(this);
		btn.setText("连接蓝牙设备");
		list.addView(btn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				list.removeAllViews();
				Set<BluetoothDevice> device = adapter.getBondedDevices();
				if (device.size() > 0) {
					for (Iterator<BluetoothDevice> iterator = device.iterator(); iterator
							.hasNext();) {
						BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator
								.next();
						Button btn = new Button(MainActivity.this);
						btn.setText(bluetoothDevice.getName());
						btn.setOnClickListener(new BluetoothDeviceListener(bluetoothDevice));
						list.addView(btn);
					}
				}
			}
		});
		

		Text.setText("初始化成功");

	}

	class BluetoothDeviceListener implements OnClickListener {

		private BluetoothDevice device;

		public BluetoothDeviceListener(BluetoothDevice device) {
			this.device = device;
		}

		@Override
		public void onClick(View arg0) {
			try {
				socket = device.createRfcommSocketToServiceRecord(UUID
						.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"));
				socket.connect();
				if (!socket.isConnected())
					Log.v("Car", "not connected!");

				mPrintStream = new PrintStream(socket.getOutputStream(), true);
				if (mPrintStream != null)
					Log.v("Car", "+++ socket built +++");
				Text.setText("连接成功");
				list.removeAllViews();
			} catch (Exception e) {
				socket = null;
				Text.setText("连接失败");
			}
			// TODO Auto-generated method stub

		}

	}

	private OnTouchListener listener = new OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// TODO Auto-generated method stub
			if (socket == null) return false; 
			ImageButton btn = (ImageButton) view;
			boolean alter = false;
			switch (btn.getId()) {
			case R.id.Up:
				if (event.getAction() == MotionEvent.ACTION_DOWN && direct == 0) {
					direct = 1;
					alter = true;
				}
				break;
			case R.id.Right:
				if (event.getAction() == MotionEvent.ACTION_DOWN && direct == 0) {
					direct = 2;
					alter = true;
				}
				break;
			case R.id.Left:
				if (event.getAction() == MotionEvent.ACTION_DOWN && direct == 0) {
					direct = 3;
					alter = true;
				}
				break;
			case R.id.Down:
				if (event.getAction() == MotionEvent.ACTION_DOWN && direct == 0) {
					direct = 4;
					alter = true;
				}
				break;
			default:
				break;
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				direct = 0;
				// alter = true;
			}
			byte[] bs = String.valueOf(direct).getBytes();
			try {
				if (alter) {
					Log.v("Car", String.valueOf(direct));

					mPrintStream.write(bs);
				}
			} catch (Exception e) {
				Log.v("Car", "+++ Error send+++");
			}

			mPrintStream.flush();
			Text.setText(String.valueOf(direct));
			return false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
