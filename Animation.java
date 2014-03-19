package edu.canisius.warexhibit;

import java.util.Arrays;

import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

/*
 * Author: Erik Taheri
 * 
 * Description: Displays animation/screen on device based on location
 * device is on table
 * 
 *	TODO: Add remaining animations
 *
 */


public class Animation extends Activity {
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
	private String[][] mNFCTechLists;
	VideoView videoView;
	AnimationDrawable animation;
	// no back button
	@Override
	public void onBackPressed() {
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animation);
		Intent intent = getIntent();
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter != null) {
			//view.setText("Read an NFC tag");
		} else {
			//view.setText("This phone is not NFC enabled.");
		}

		// create an intent with tag data and deliver to this activity
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// set an intent filter for all MIME data
		IntentFilter ndefIntent = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefIntent.addDataType("*/*");
			mIntentFilters = new IntentFilter[] { ndefIntent };
		} catch (Exception e) {
			Log.e("TagDispatch", e.toString());
		}
		mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };
		
		// get value of location on table from NFC
		// 0 is top left location
		ImageView map = (ImageView) findViewById(R.id.map);
		int val = intent.getExtras().getInt("val");
		
		if(val==0){
			map.setImageDrawable(getResources().getDrawable(R.drawable.zero));
		}
		if(val==1){
			map.setImageDrawable(getResources().getDrawable(R.drawable.one));
		}
		if(val==2){
			map.setImageDrawable(getResources().getDrawable(R.drawable.two));
		}
		if(val==3){
			map.setImageDrawable(getResources().getDrawable(R.drawable.three));
		}
		if(val==4){
			map.setImageDrawable(getResources().getDrawable(R.drawable.four));
		}
		if(val==5){
			map.setImageDrawable(getResources().getDrawable(R.drawable.five));
			
			// frame by frame animation
			animation = new AnimationDrawable();
			animation.addFrame(getResources().getDrawable(R.drawable.stepblank), 300);
			animation.addFrame(getResources().getDrawable(R.drawable.stepblank), 300);
			animation.addFrame(getResources().getDrawable(R.drawable.stepblank), 300);
			animation.addFrame(getResources().getDrawable(R.drawable.stepblank), 300);
			animation.addFrame(getResources().getDrawable(R.drawable.stepblank), 300);
	        animation.addFrame(getResources().getDrawable(R.drawable.step1), 300);
	        animation.addFrame(getResources().getDrawable(R.drawable.step2), 300);
	        animation.addFrame(getResources().getDrawable(R.drawable.step3), 300);
	        animation.addFrame(getResources().getDrawable(R.drawable.step4), 300);
	        animation.addFrame(getResources().getDrawable(R.drawable.step5), 300);
	        animation.addFrame(getResources().getDrawable(R.drawable.step6), 300);
	        animation.addFrame(getResources().getDrawable(R.drawable.step7), 300);
	        animation.addFrame(getResources().getDrawable(R.drawable.step8), 300);
	        animation.addFrame(getResources().getDrawable(R.drawable.step9), 300);
	        animation.setOneShot(false);
	        
	        ImageView imageView = (ImageView) findViewById(R.id.steps);
	        imageView.setImageDrawable(animation);
	        animation.start();
		}
		if(val==6){
			map.setImageDrawable(getResources().getDrawable(R.drawable.six));
		}
		if(val==7){
			map.setImageDrawable(getResources().getDrawable(R.drawable.seven));
		}
		if(val==8){
			map.setImageDrawable(getResources().getDrawable(R.drawable.eight));
		}
		if(val==9){
			map.setImageDrawable(getResources().getDrawable(R.drawable.nine));
		}
		if(val==10){
			map.setImageDrawable(getResources().getDrawable(R.drawable.ten));
		}
		if(val==11){
			map.setImageDrawable(getResources().getDrawable(R.drawable.eleven));
		}
		
		findViewById(R.id.helpframe).setVisibility(View.INVISIBLE);
		findViewById(R.id.exithelp).setVisibility(View.INVISIBLE);
		findViewById(R.id.contimg).setVisibility(View.INVISIBLE);
		findViewById(R.id.help).setVisibility(View.INVISIBLE);
		
       videoView = (VideoView)findViewById(R.id.helpvideo);

	 videoView.setMediaController(new MediaController(this));
	 Uri video = Uri.parse("android.resource://" + getPackageName() + "/" 
	 + R.raw.tutorialvideo); //do not add any extension
	 videoView.setVideoURI(video);
	 
	 // User has picked up a device and pressed begin
	 // load mandatory intro video
	 ImageView begin = (ImageView)findViewById(R.id.beginbtnmain);
	 begin.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
				fadeIn.setInterpolator(new AccelerateInterpolator()); // and this
				fadeIn.setStartOffset(500);
				fadeIn.setDuration(500);
				findViewById(R.id.helpframe).startAnimation(fadeIn);
				findViewById(R.id.helpframe).setVisibility(View.VISIBLE);	
				findViewById(R.id.helpvideo).startAnimation(fadeIn);
				findViewById(R.id.helpvideo).setVisibility(View.VISIBLE);
				findViewById(R.id.exithelp).startAnimation(fadeIn);
				findViewById(R.id.exithelp).setVisibility(View.VISIBLE);
				videoView.start();
				
				AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
				fadeIn.setInterpolator(new AccelerateInterpolator()); // and this
				fadeIn.setStartOffset(500);
				fadeIn.setDuration(500);
				findViewById(R.id.beginbtnmain).startAnimation(fadeOut);
				findViewById(R.id.beginbtnmain).setVisibility(View.INVISIBLE);
			}
		});
	   
	 	// continue button action
		 ImageView cont = (ImageView) findViewById(R.id.contimg);
		   cont.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					Intent begin = new Intent(Animation.this, Exhibit_explore_view.class);
				    startActivity(begin);
				}
			});
		// close video on completion
		   videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			    public void onCompletion(MediaPlayer mp) {
			    	Intent begin = new Intent(Animation.this, Exhibit_explore_view.class);
				    startActivity(begin);
			    }
			});
		   
			ImageView exit = (ImageView) findViewById(R.id.exithelp);
			
			exit.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					videoView.pause();
					Intent begin = new Intent(Animation.this, Exhibit_explore_view.class);
				    startActivity(begin);	
				}
			
			});
		}
	
	// Set images 
	// TODO: Can i remove this duplicate
	public void nfctable(String nfc){
		ImageView map = (ImageView) findViewById(R.id.map);
		 findViewById(R.id.help).setVisibility(View.INVISIBLE);
		 findViewById(R.id.contimg).setVisibility(View.INVISIBLE);
		if(nfc.equals("0")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.zero));
		}
		if(nfc.equals("1")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.one));
		}
		if(nfc.equals("2")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.two));
		}
		if(nfc.equals("3")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.three));
		}
		if(nfc.equals("4")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.four));
		}
		if(nfc.equals("5")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.five));
		}
		if(nfc.equals("6")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.six));
		}
		if(nfc.equals("7")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.seven));
		}	
		if(nfc.equals("8")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.eight));
		}
		if(nfc.equals("9")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.nine));
		}
		if(nfc.equals("10")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.ten));
		}
		if(nfc.equals("11")){
			map.setImageDrawable(getResources().getDrawable(R.drawable.eleven));
		}

	}
	
	// NFC Interaction
	// s is the only value used
	public void onNewIntent(Intent intent) {

		String s = null;

		// parse through all NDEF messages and their records and pick text type
		// only
		Parcelable[] data = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (data != null) {
			try {
				for (int i = 0; i < data.length; i++) {
					NdefRecord[] recs = ((NdefMessage) data[i]).getRecords();
					for (int j = 0; j < recs.length; j++) {
						if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN
								&& Arrays.equals(recs[j].getType(),
										NdefRecord.RTD_TEXT)) {
							byte[] payload = recs[j].getPayload();
							String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8"
									: "UTF-16";
							int langCodeLen = payload[0] & 0077;

							s = (new String(payload, langCodeLen + 1,
									payload.length - langCodeLen - 1,
									textEncoding));
						}
					}
				}
			} catch (Exception e) {
				Log.e("TagDispatch", e.toString());
			}
		}

		nfctable(s);
	}
	

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {

			// findViewById(R.id.testbut).setVisibility(View.INVISIBLE);

			// mSystemUiHider.hide();
		}
	};

	public void onResume() {
		super.onResume();

		if (mNfcAdapter != null)
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,
					mIntentFilters, mNFCTechLists);
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mNfcAdapter != null)
			mNfcAdapter.disableForegroundDispatch(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.animation, menu);
		return true;
	}

}
