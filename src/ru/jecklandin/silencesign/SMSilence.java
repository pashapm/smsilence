package ru.jecklandin.silencesign;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SMSilence extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuitem1 = menu.add(Menu.NONE, 1, Menu.NONE, "About");
		menuitem1.setOnMenuItemClickListener(new MenuToaster(this));
		return super.onCreateOptionsMenu(menu);

	}

	class MenuToaster implements OnMenuItemClickListener {
		Context m_ctx;

		public MenuToaster(Context m_ctx) {
			this.m_ctx = m_ctx;
		}

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			AlertDialog.Builder bu = new AlertDialog.Builder(m_ctx); 
			bu.setIcon(R.drawable.icon);
			bu.setTitle(R.string.app_name);
			bu.setMessage(R.string.help);
			bu.create().show();
			return false;
		}

	}

	static boolean s_isReceive = true ; //Receiver.s_receive;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Log.d("!!!", ""+metrics.density);
 
		TextView descr = (TextView) findViewById(R.id.DescriptionText);
		descr.setText(R.string.description);
   
		TextView example1 = (TextView) findViewById(R.id.Examples1);
		example1.setText(R.string.example1);

		TextView example2 = (TextView) findViewById(R.id.Examples2);
		example2.setText(R.string.example2);


		
		TextView instr = (TextView) findViewById(R.id.TextView01);
		if (!s_isReceive)
			instr.setText(R.string.instruction2);
		else
			instr.setText("");

		Button state_btn = (Button) findViewById(R.id.Button02);
		state_btn.setTextSize(state_btn.getTextSize() - 2);
		state_btn.setText(s_isReceive ? "Stop handling"
				: "Resume handling");
		state_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(v.getContext(), Receiver.class);
				//if (Receiver.s_receive) {
				if (false) {
					
					i.putExtra("smsilence", "stop");
					s_isReceive = false;
					TextView instr = (TextView) findViewById(R.id.TextView01);
					instr.setText(R.string.instruction2);
					Button state_btn = (Button) findViewById(R.id.Button02);
					state_btn.setText("Resume handling");

				} else {
					s_isReceive = true;
					i.putExtra("smsilence", "start");
					TextView instr = (TextView) findViewById(R.id.TextView01);
					instr.setText("");
					Button state_btn = (Button) findViewById(R.id.Button02);
					state_btn.setText("Stop handling");

				}

				sendBroadcast(i); // broadcast - not good

			}
		});

		Button close_btn = (Button) findViewById(R.id.Button01);
		close_btn.setTextSize(close_btn.getTextSize() + 4);
		close_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			} 
		});
		 
		
		setContentView(R.layout.promo);
		((ImageView)findViewById(R.id.ImageView01)).setImageResource(R.drawable.test);
		
//		Typeface hels = Typeface.createFromAsset(getAssets(), "helsinki.ttf");
//		((Button)findViewById(R.id.Button01)).setTypeface(hels); 

		//Log.d("work?", "" + Receiver.s_receive);
	}
	
}