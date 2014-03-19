package edu.canisius.warexhibit;

import java.util.Arrays;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

/*
 * Author: Erik Taheri 
 *
 * Description: Activity that is used when exploring the exhibit.  Manages
 * what artifacts are found by the users.
 * 
 * TODO: Fix verbose methods and duplicate lines, find memory issues
 * 
 * 		January 22, 2013: Images resized, more stable
 * 
 * 		March 1, 2013: Activity switch from explore to animation, need to dumb memory.
 * 
 * 
 */
public class Exhibit_explore_view extends Activity {
	// NFC variables
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
	private String[][] mNFCTechLists;

	// track what user has found
	private boolean perryfound = false;
	private boolean chestfound = false;
	private boolean mclurefound = false;
	private boolean tombfound = false;
	private boolean secordfound = false;
	private boolean pitcherfound = false;
	private boolean chaplinfound = false;
	private boolean torchfound = false;
	private boolean redjacket = false;
	private boolean hatchetfound = false;

	private boolean enable = false;
	// VideoView activity to show videos
	private VideoView videoView;
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	// Hide bottom bar when in viw
	private static final boolean AUTO_HIDE = true;
	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	
	// Prevent back button from being used (if device is not rooted)
	// TODO: Disable bottom bar
	@Override
	public void onBackPressed() {
	//do nothing
	}
	
	//Set up the main layout of the explore activity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//do not show title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exhibit_explore_view);

		findViewById(R.id.videothumb).setVisibility(View.INVISIBLE);
		findViewById(R.id.videoframe).setVisibility(View.INVISIBLE);
		findViewById(R.id.creditbtn).setVisibility(View.INVISIBLE);
		findViewById(R.id.donescreen).setVisibility(View.INVISIBLE);
		findViewById(R.id.endcont).setVisibility(View.INVISIBLE);
		findViewById(R.id.map).setVisibility(View.INVISIBLE);

		ImageView back = (ImageView) findViewById(R.id.backbtn);
		back.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				//fade in/out animation
				AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
				fadeOut.setInterpolator(new AccelerateInterpolator()); // and
				fadeOut.setStartOffset(500);
				fadeOut.setDuration(500);
				AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
				fadeIn.setInterpolator(new AccelerateInterpolator());
				
				// All artifacts have been found. Fade out main UI and show credits
				if (perryfound && chestfound && mclurefound && tombfound
						&& secordfound && pitcherfound && chaplinfound
						&& torchfound && redjacket && hatchetfound) {
					findViewById(R.id.InfoLayout).startAnimation(fadeOut);
					findViewById(R.id.InfoLayout).setVisibility(View.INVISIBLE);
					findViewById(R.id.backbtn).startAnimation(fadeOut);
					findViewById(R.id.backbtn).setVisibility(View.INVISIBLE);
					findViewById(R.id.videothumb).startAnimation(fadeOut);
					findViewById(R.id.videothumb).setVisibility(View.INVISIBLE);

					findViewById(R.id.welcometext).startAnimation(fadeOut);
					findViewById(R.id.welcometext)
							.setVisibility(View.INVISIBLE);
					findViewById(R.id.chars).startAnimation(fadeOut);
					findViewById(R.id.chars).setVisibility(View.INVISIBLE);
					findViewById(R.id.icons).startAnimation(fadeOut);
					findViewById(R.id.icons).setVisibility(View.INVISIBLE);

					ImageView endimg = (ImageView) findViewById(R.id.donescreen);
					endimg.setImageDrawable(getResources().getDrawable(
							R.drawable.winscreen));
					findViewById(R.id.creditbtn).startAnimation(fadeIn);
					findViewById(R.id.donescreen).startAnimation(fadeIn);
					findViewById(R.id.endcont).startAnimation(fadeIn);
					findViewById(R.id.creditbtn).setVisibility(View.VISIBLE);
					findViewById(R.id.donescreen).setVisibility(View.VISIBLE);
					findViewById(R.id.endcont).setVisibility(View.VISIBLE);
				} else {
					// they havent all been found - show main ui
					findViewById(R.id.InfoLayout).startAnimation(fadeOut);
					findViewById(R.id.InfoLayout).setVisibility(View.INVISIBLE);
					findViewById(R.id.backbtn).startAnimation(fadeOut);
					findViewById(R.id.backbtn).setVisibility(View.INVISIBLE);
					findViewById(R.id.videothumb).startAnimation(fadeOut);
					findViewById(R.id.videothumb).setVisibility(View.INVISIBLE);
					findViewById(R.id.map).startAnimation(fadeOut);
					findViewById(R.id.map).setVisibility(View.INVISIBLE);
					fadeIn.setStartOffset(500);
					fadeIn.setDuration(500);
					findViewById(R.id.chars).startAnimation(fadeIn);
					findViewById(R.id.chars).setVisibility(View.VISIBLE);
					findViewById(R.id.icons).startAnimation(fadeIn);
					findViewById(R.id.icons).setVisibility(View.VISIBLE);
					findViewById(R.id.welcometext).startAnimation(fadeIn);
					findViewById(R.id.welcometext).setVisibility(View.VISIBLE);
				}
			}
		});
		
		// Wow some of these names are confusing
		// endcont is the continue button on the congrats screen
		// TODO: Refactor soon!!!!!!!!
		ImageView endcont = (ImageView) findViewById(R.id.endcont);
		// click listener that shows the second credits screen
		endcont.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO: Can I just make this animation global?
				AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
				fadeOut.setInterpolator(new AccelerateInterpolator()); 
				fadeOut.setStartOffset(000);
				fadeOut.setDuration(500);
				
				findViewById(R.id.donescreen).startAnimation(fadeOut);
				findViewById(R.id.donescreen).setVisibility(View.INVISIBLE);
				findViewById(R.id.endcont).startAnimation(fadeOut);
				findViewById(R.id.endcont).setVisibility(View.INVISIBLE);
				findViewById(R.id.creditbtn).startAnimation(fadeOut);
				findViewById(R.id.creditbtn).setVisibility(View.INVISIBLE);

				AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
				fadeIn.setInterpolator(new AccelerateInterpolator());

				fadeIn.setStartOffset(500);
				fadeIn.setDuration(500);
				findViewById(R.id.chars).startAnimation(fadeIn);
				findViewById(R.id.chars).setVisibility(View.VISIBLE);
				findViewById(R.id.icons).startAnimation(fadeIn);
				findViewById(R.id.icons).setVisibility(View.VISIBLE);
				findViewById(R.id.welcometext).startAnimation(fadeIn);
				findViewById(R.id.welcometext).setVisibility(View.VISIBLE);
			}
		});
		// credit screen
		ImageView credits = (ImageView) findViewById(R.id.creditbtn);
		credits.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				ImageView endimg = (ImageView) findViewById(R.id.donescreen);
				endimg.setImageDrawable(getResources().getDrawable(
						R.drawable.credits));
				findViewById(R.id.creditbtn).setVisibility(View.INVISIBLE);
			}
		});
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// NFC Interaction
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter ndefIntent = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefIntent.addDataType("*/*");
			mIntentFilters = new IntentFilter[] { ndefIntent };
		} catch (Exception e) {
			Log.e("TagDispatch", e.toString());
		}
		mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };

		// Video view when video is selected
		videoView = (VideoView) findViewById(R.id.mainvideo);
		videoView.setMediaController(new MediaController(this));
		Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
				+ R.raw.trailer); // do not add any extension
		videoView.setVideoURI(video);
		
		// leaves video activity on completion
		// returns to main ui
		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
		   
			public void onCompletion(MediaPlayer mp) {
		    	AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
				fadeOut.setInterpolator(new AccelerateInterpolator());
				
				findViewById(R.id.videothumb).setVisibility(View.VISIBLE);
				findViewById(R.id.backbtn).setVisibility(View.VISIBLE);
				fadeOut.setStartOffset(500);
				fadeOut.setDuration(500);
				findViewById(R.id.videoframe).startAnimation(fadeOut);
				findViewById(R.id.mainvideo).startAnimation(fadeOut);
				findViewById(R.id.videoframe).setVisibility(View.INVISIBLE);
				
				
				videoView.pause();
		    }
		});
		findViewById(R.id.backbtn).setVisibility(View.INVISIBLE);
		findViewById(R.id.InfoLayout).setVisibility(View.INVISIBLE);

		View chars = findViewById(R.id.chars);
		chars.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
				fadeIn.setInterpolator(new AccelerateInterpolator()); 
				
				fadeIn.setStartOffset(0);
				fadeIn.setDuration(500);
				findViewById(R.id.welcometext).startAnimation(fadeIn);

			}

		});
		
		ImageView thumbpress = (ImageView) findViewById(R.id.videothumb);
		thumbpress.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				if (enable) {
					AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
					fadeIn.setInterpolator(new AccelerateInterpolator());
					
					fadeIn.setStartOffset(500);
					fadeIn.setDuration(500);
					findViewById(R.id.videoframe).startAnimation(fadeIn);
					findViewById(R.id.videoframe).setVisibility(View.VISIBLE);
					findViewById(R.id.mainvideo).startAnimation(fadeIn);

					AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
					fadeOut.setInterpolator(new AccelerateInterpolator()); 
					
					fadeOut.setStartOffset(500);
					fadeOut.setDuration(500);
					findViewById(R.id.videothumb).startAnimation(fadeOut);
					findViewById(R.id.backbtn).startAnimation(fadeOut);
					findViewById(R.id.videothumb).setVisibility(View.INVISIBLE);
					findViewById(R.id.backbtn).setVisibility(View.INVISIBLE);
					videoView.start();
				}
			}
		});
		
		// exit button for video
		// allows viewer to exit early
		
		ImageView exit = (ImageView) findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
				fadeOut.setInterpolator(new AccelerateInterpolator()); // and
																		// this
				findViewById(R.id.videothumb).setVisibility(View.VISIBLE);
				findViewById(R.id.backbtn).setVisibility(View.VISIBLE);
				fadeOut.setStartOffset(500);
				fadeOut.setDuration(500);
				findViewById(R.id.videoframe).startAnimation(fadeOut);
				findViewById(R.id.mainvideo).startAnimation(fadeOut);
				findViewById(R.id.videoframe).setVisibility(View.INVISIBLE);
				
				
				videoView.pause();
			}
		});

		ImageView charinfoimg = (ImageView) findViewById(R.id.charinfo);
		//ImageView infobox = (ImageView) findViewById(R.id.infobox);
		ImageView chariconinfoview = (ImageView) findViewById(R.id.helpframe);
		charinfoimg.setOnClickListener(new OnClickListener() {

			/*
			 * Allow user to view video based on whether they found an item=
			 *  
			 *  if they found the item, the video is loaded
			 *  
			 *  if they did not find video, they are displayed a b&w image
			 */
			public void onClick(View arg0) {
				ImageView charinfoimg = (ImageView) findViewById(R.id.charinfo);
				LinearLayout infobox = (LinearLayout) findViewById(R.id.InfoLayout);
				//ImageView infobox = (ImageView) findViewById(R.id.infobox);
				ImageView chariconinfoview = (ImageView) findViewById(R.id.helpframe);
				ImageView videothumb = (ImageView) findViewById(R.id.videothumb);
				if ((Integer) charinfoimg.getTag() == R.drawable.perrycolor) {
					enable = true;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.perryinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.perryvideo));
					// set image for video
				}
				if ((Integer) charinfoimg.getTag() == R.drawable.perrygreydark) {
					enable = false;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.perryinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.noperryvideo));
					// set image for no video
				}

				if ((Integer) charinfoimg.getTag() == R.drawable.mcclurecolor) {
					enable = true;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.mcclureinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.mcclurevideo));
					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.mcclure); // do not
																		// add
																		// any
																		// extension
					videoView.setVideoURI(video);
					// set image for video
				}
				if ((Integer) charinfoimg.getTag() == R.drawable.mccluregreydark) {
					enable = false;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.mcclureinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.nomcclurevideo));
					// set image for no video
				}

				if ((Integer) charinfoimg.getTag() == R.drawable.secordcolor) {
					enable = true;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.secordinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.secordvideo));
					// set image for video
					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.secord); // do not
																		// add
																		// any
																		// extension
					videoView.setVideoURI(video);
				}
				if ((Integer) charinfoimg.getTag() == R.drawable.secordgreydark) {
					enable = false;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.secordinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.nosecordvideo));
					// set image for no video
				}

				if ((Integer) charinfoimg.getTag() == R.drawable.chaplincolor) {
					enable = true;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.chapininfo));
					videothumb.setImageDrawable(null);
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.chapinvideo));

					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.chapin); // do not
																		// add
																		// any
																		// extension
					videoView.setVideoURI(video);
					// set image for video
				}
				if ((Integer) charinfoimg.getTag() == R.drawable.chaplingreydark) {
					enable = false;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.chapininfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.nochapinvideo));
					// set image for no video

				}

				if ((Integer) charinfoimg.getTag() == R.drawable.redjacketcolor) {
					enable = true;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.redjacketinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.redjacketvideo));
					// set image for video
					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.redjacket); // do
																			// not
																			// add
																			// any
																			// extension
					videoView.setVideoURI(video);
				}
				if ((Integer) charinfoimg.getTag() == R.drawable.redjacketgreydark) {
					enable = false;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.redjacketinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.noredjacketvideo));
					// set image for no video
				}

				if ((Integer) charinfoimg.getTag() == R.drawable.perrycolor) {
					enable = true;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.perryinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.perryvideo));
					// set image for video
					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.perry);
					videoView.setVideoURI(video);
				}
				if ((Integer) charinfoimg.getTag() == R.drawable.perrygreydark) {
					enable = false;
					charinfoimg.setBackground(getResources().getDrawable(
							R.drawable.glow));
					chariconinfoview.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.perryinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.noperryvideo));
					// set image for no video
				}
			}
		});
		
		// layout and images for when items have been found, assets are swapped when user finds item
		// prevents users from viewing videos without finding items
		// set videos if they have been found
		// set to gray if they have not been found
		chariconinfoview.setOnClickListener(new OnClickListener() {
			ImageView charinfoimg = (ImageView) findViewById(R.id.charinfo);
			LinearLayout infobox = (LinearLayout) findViewById(R.id.InfoLayout);
			ImageView chariconinfoview = (ImageView) findViewById(R.id.helpframe);
			ImageView videothumb = (ImageView) findViewById(R.id.videothumb);

			public void onClick(View arg0) {
				// chest not found
				if ((Integer) chariconinfoview.getTag() == R.drawable.chesticongrey) {
					enable = false;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.chestinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.nochestvideo));
				}
				// chest has been found
				if ((Integer) chariconinfoview.getTag() == R.drawable.chesticon) {
					enable = true;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.chestinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.chestvideo));
					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.chest);
					videoView.setVideoURI(video);
				}
				// torch has not been found
				if ((Integer) chariconinfoview.getTag() == R.drawable.torchicongrey) {
					enable = false;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.torchinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.notorchvideo));

				}
				// torch has been found
				if ((Integer) chariconinfoview.getTag() == R.drawable.torchicon) {
					enable = true;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.torchinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.torchvideo));
					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.torch);
					videoView.setVideoURI(video);

				}
				// pitcher is not found 
				if ((Integer) chariconinfoview.getTag() == R.drawable.pitchericongrey) {
					enable = false;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.pitcherinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.nopitchervideo));

				}
				// pitcher is found
				if ((Integer) chariconinfoview.getTag() == R.drawable.pitchericon) {
					enable = true;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.pitcherinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.pitchervideo));

					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.pitcher);
					videoView.setVideoURI(video);

				}
				// tomb not found
				if ((Integer) chariconinfoview.getTag() == R.drawable.tombstoneicongray) {
					enable = false;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.tombinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.notombvideo));

				}
				// tomb has been found
				if ((Integer) chariconinfoview.getTag() == R.drawable.tombstoneicon) {
					enable = true;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.tombinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.tombvideo));
					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.tomb);
					videoView.setVideoURI(video);

				}
				// tomahawk not found
				if ((Integer) chariconinfoview.getTag() == R.drawable.tomahawkicongrey) {
					enable = false;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.hatchetinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.nohatchetvideo));

				}
				// tomahawk found
				if ((Integer) chariconinfoview.getTag() == R.drawable.tomahawkicon) {
					enable = true;
					chariconinfoview.setBackground(getResources().getDrawable(
							R.drawable.glow));
					charinfoimg.setBackground(null);
					infobox.setBackground(getResources().getDrawable(
							R.drawable.hatchetinfo));
					videothumb.setImageDrawable(getResources().getDrawable(
							R.drawable.hatchetvideo));
					Uri video = Uri.parse("android.resource://"
							+ getPackageName() + "/" + R.raw.hatchet); // do not
																		// add
																		// any
																		// extension
					videoView.setVideoURI(video);

				}
			}
		});
	}
	
	// Table interaction
	// get location data from nfc
	// start animation activity based on that data
	// top left of table is 0
	
	public void nfctable(String nfc) {
		if (nfc.equals("0")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 0);
			startActivity(animation);
		}
		if (nfc.equals("1")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 1);
			startActivity(animation);
		}
		if (nfc.equals("2")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 2);
			startActivity(animation);
		}
		if (nfc.equals("3")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 3);
			startActivity(animation);
		}
		if (nfc.equals("4")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 4);
			startActivity(animation);
		}
		if (nfc.equals("5")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 5);
			startActivity(animation);

		}
		if (nfc.equals("6")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 6);
			startActivity(animation);
		}
		if (nfc.equals("7")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 7);
			startActivity(animation);
		}
		if (nfc.equals("8")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 8);
			startActivity(animation);
		}
		if (nfc.equals("9")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 9);
			startActivity(animation);
		}
		if (nfc.equals("10")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 10);
			startActivity(animation);
		}
		if (nfc.equals("11")) {
			Intent animation = new Intent(this, Animation.class);
			animation.putExtra("val", 11);
			startActivity(animation);
		}

	}
	
	// found interaction
	// bring up screen according to what item has been found
	// fade out main screen to info screen
	
	public void nfctrigger(String nfc) {
		AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
		fadeOut.setStartOffset(500);
		fadeOut.setDuration(500);
		findViewById(R.id.welcometext).startAnimation(fadeOut);
		findViewById(R.id.welcometext).setVisibility(View.INVISIBLE);
		findViewById(R.id.chars).startAnimation(fadeOut);
		findViewById(R.id.chars).setVisibility(View.INVISIBLE);
		findViewById(R.id.icons).startAnimation(fadeOut);
		findViewById(R.id.icons).setVisibility(View.INVISIBLE);
		// Fade in the info on char screen
		AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new AccelerateInterpolator()); // and this
		fadeIn.setStartOffset(500);
		fadeIn.setDuration(500);
		findViewById(R.id.InfoLayout).startAnimation(fadeIn);
		findViewById(R.id.InfoLayout).setVisibility(View.VISIBLE);
		findViewById(R.id.backbtn).startAnimation(fadeIn);
		findViewById(R.id.backbtn).setVisibility(View.VISIBLE);
		findViewById(R.id.videothumb).startAnimation(fadeIn);
		findViewById(R.id.videothumb).setVisibility(View.VISIBLE);
		findViewById(R.id.map).startAnimation(fadeIn);
		findViewById(R.id.map).setVisibility(View.VISIBLE);
		ImageView charinfoimg = (ImageView) findViewById(R.id.charinfo);
		ImageView chariconinfoview = (ImageView) findViewById(R.id.helpframe);
		LinearLayout infobox = (LinearLayout) findViewById(R.id.InfoLayout);
		ImageView videothumb = (ImageView) findViewById(R.id.videothumb);

		if (nfc.equals("chest")) {
			enable = true;
			ImageView chesticon = (ImageView) findViewById(R.id.chesticon);
			chesticon.setImageDrawable(getResources().getDrawable(
					R.drawable.chesticon));
			chestfound = true;
			chariconinfoview.setImageDrawable(getResources().getDrawable(
					R.drawable.chesticon));
			chariconinfoview.setTag(R.drawable.chesticon);
			charinfoimg.setBackground(null);
			chariconinfoview.setBackground(getResources().getDrawable(
					R.drawable.glow));
			infobox.setBackground(getResources().getDrawable(
					R.drawable.chestinfo));
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.chestvideo));
			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.chest); // do not add any extension
			videoView.setVideoURI(video);
			if (!perryfound) {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.perrygreydark));
				charinfoimg.setTag(R.drawable.perrygreydark);
			} else {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.perrycolor));
				charinfoimg.setTag(R.drawable.perrycolor);
			}
		}
		if (nfc.equals("tomb")) {
			enable = true;
			ImageView tombicon = (ImageView) findViewById(R.id.tombicon);
			tombicon.setImageDrawable(getResources().getDrawable(
					R.drawable.tombstoneicon));
			tombfound = true;
			charinfoimg.setBackground(null);
			chariconinfoview.setBackground(getResources().getDrawable(
					R.drawable.glow));
			chariconinfoview.setImageDrawable(getResources().getDrawable(
					R.drawable.tombstoneicon));

			chariconinfoview.setTag(R.drawable.tombstoneicon);

			infobox.setBackground(getResources().getDrawable(
					R.drawable.tombinfo));
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.tombvideo));
			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.tomb); // do not add any extension
			videoView.setVideoURI(video);
			if (!chaplinfound) {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.chaplingreydark));
				charinfoimg.setTag(R.drawable.chaplingreydark);
			} else {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.chaplincolor));
				charinfoimg.setTag(R.drawable.chaplincolor);
			}

		}
		if (nfc.equals("pitcher")) {
			enable = true;
			ImageView pitchericon = (ImageView) findViewById(R.id.pitchericon);
			pitchericon.setImageDrawable(getResources().getDrawable(
					R.drawable.pitchericon));
			pitcherfound = true;
			charinfoimg.setBackground(null);
			chariconinfoview.setBackground(getResources().getDrawable(
					R.drawable.glow));
			chariconinfoview.setImageDrawable(getResources().getDrawable(
					R.drawable.pitchericon));

			chariconinfoview.setTag(R.drawable.pitchericon);

			infobox.setBackground(getResources().getDrawable(
					R.drawable.pitcherinfo));
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.pitchervideo));
			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.pitcher); // do not add any extension
			videoView.setVideoURI(video);
			if (!secordfound) {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.secordgreydark));
				charinfoimg.setTag(R.drawable.secordgreydark);
			} else {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.secordcolor));
				charinfoimg.setTag(R.drawable.secordcolor);
			}
		}
		if (nfc.equals("torch")) {
			enable = true;
			ImageView torchicon = (ImageView) findViewById(R.id.torchicon);
			torchicon.setImageDrawable(getResources().getDrawable(
					R.drawable.torchicon));
			torchfound = true;
			charinfoimg.setBackground(null);
			chariconinfoview.setBackground(getResources().getDrawable(
					R.drawable.glow));
			chariconinfoview.setImageDrawable(getResources().getDrawable(
					R.drawable.torchicon));

			chariconinfoview.setTag(R.drawable.torchicon);

			infobox.setBackground(getResources().getDrawable(
					R.drawable.torchinfo));
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.torchvideo));

			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.torch); // do not add any extension
			videoView.setVideoURI(video);
			if (!mclurefound) {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.mccluregreydark));
				charinfoimg.setTag(R.drawable.mccluregreydark);
			} else {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.mcclurecolor));
				charinfoimg.setTag(R.drawable.mcclurecolor);
			}
		}
		if (nfc.equals("hatchet")) {
			enable = true;
			ImageView hatcheticon = (ImageView) findViewById(R.id.tomahawkicon);
			hatcheticon.setImageDrawable(getResources().getDrawable(
					R.drawable.tomahawkicon));
			hatchetfound = true;
			charinfoimg.setBackground(null);
			chariconinfoview.setTag(R.drawable.tomahawkicon);
			chariconinfoview.setBackground(getResources().getDrawable(
					R.drawable.glow));
			chariconinfoview.setImageDrawable(getResources().getDrawable(
					R.drawable.tomahawkicon));
			infobox.setBackground(getResources().getDrawable(
					R.drawable.hatchetinfo));
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.hatchetvideo));
			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.hatchet); // do not add any extension
			videoView.setVideoURI(video);
			if (!redjacket) {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.redjacketgreydark));
				charinfoimg.setTag(R.drawable.redjacketgreydark);
			} else {
				charinfoimg.setImageDrawable(getResources().getDrawable(
						R.drawable.redjacketcolor));
				charinfoimg.setTag(R.drawable.redjacketcolor);
			}
		}
		if (nfc.equals("perry")) {
			enable = true;
		
			ImageView perrymain = (ImageView) findViewById(R.id.perrycolor);
			perrymain.setImageDrawable(getResources().getDrawable(
					R.drawable.perrycolor));
			perryfound = true;
			chariconinfoview.setBackground(null);
			charinfoimg.setImageDrawable(getResources().getDrawable(
					R.drawable.perrycolor));
			charinfoimg.setTag(R.drawable.perrycolor);
			charinfoimg.setBackground(getResources().getDrawable(
					R.drawable.glow));
			infobox.setBackground(getResources().getDrawable(
					R.drawable.perryinfo));
			
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.perryvideo));
			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.perry); // do not add any extension
			videoView.setVideoURI(video);
			if (!chestfound) {
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.chesticongrey));
				chariconinfoview.setTag(R.drawable.chesticongrey);
			} else {
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.chesticon));
				chariconinfoview.setTag(R.drawable.chesticon);
			}
		}
		// mclure
		if (nfc.equals("mcclure")) {
			enable = true;
			ImageView mcluremain = (ImageView) findViewById(R.id.mcclurecolor);
			mcluremain.setImageDrawable(getResources().getDrawable(
					R.drawable.mcclurecolor));
			mclurefound = true;
			charinfoimg.setTag(R.drawable.mcclurecolor);
			charinfoimg.setImageDrawable(getResources().getDrawable(
					R.drawable.mcclurecolor));
			charinfoimg.setBackground(getResources().getDrawable(
					R.drawable.glow));
			chariconinfoview.setBackground(null);
			infobox.setBackground(getResources().getDrawable(
					R.drawable.mcclureinfo));
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.mcclurevideo));
			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.mcclure); // do not add any extension
			videoView.setVideoURI(video);
			if (!torchfound) {
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.torchicongrey));
				chariconinfoview.setTag(R.drawable.torchicongrey);
			} else {
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.torchicon));
				chariconinfoview.setTag(R.drawable.torchicon);
			}
		}
		// secord
		if (nfc.equals("secord")) {
			enable = true;
			ImageView secordmain = (ImageView) findViewById(R.id.secordcolor);
			secordmain.setImageDrawable(getResources().getDrawable(
					R.drawable.secordcolor));
			secordfound = true;
			infobox.setBackground(getResources().getDrawable(
					R.drawable.secordinfo));
			charinfoimg.setBackground(getResources().getDrawable(
					R.drawable.glow));
			chariconinfoview.setBackground(null);
			charinfoimg.setImageDrawable(getResources().getDrawable(
					R.drawable.secordcolor));
			charinfoimg.setTag(R.drawable.secordcolor);
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.secordvideo));
			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.secord); // do not add any extension
			videoView.setVideoURI(video);
			if (!pitcherfound) {
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.pitchericongrey));
				chariconinfoview.setTag(R.drawable.pitchericongrey);
			} else {
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.pitchericon));
				chariconinfoview.setTag(R.drawable.pitchericon);
			}
		}
		// chaplin
		if (nfc.equals("chaplin")) {
			enable = true;
			ImageView chaplinmain = (ImageView) findViewById(R.id.chaplincolor);
			chaplinmain.setImageDrawable(getResources().getDrawable(
					R.drawable.chaplincolor));
			chaplinfound = true;
			charinfoimg.setImageDrawable(getResources().getDrawable(
					R.drawable.chaplincolor));
			charinfoimg.setBackground(getResources().getDrawable(
					R.drawable.glow));
			charinfoimg.setTag(R.drawable.chaplincolor);
			chariconinfoview.setBackground(null);
			infobox.setBackground(getResources().getDrawable(
					R.drawable.chapininfo));
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.chapinvideo));
			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.chapin); // do not add any extension
			videoView.setVideoURI(video);
			if (!tombfound) {
				// Show gray tomb
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.tombstoneicongray));
				chariconinfoview.setTag(R.drawable.tombstoneicongray);
			} else {
				// show color tomb
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.tombstoneicon));
				chariconinfoview.setTag(R.drawable.tombstoneicon);
			}
		}
		// redjacket
		if (nfc.equals("redjacket")) {
			enable = true;
			ImageView redjacketmain = (ImageView) findViewById(R.id.redjacketcolor);
			redjacketmain.setImageDrawable(getResources().getDrawable(
					R.drawable.redjacketcolor));
			charinfoimg.setBackground(getResources().getDrawable(
					R.drawable.glow));
			chariconinfoview.setBackground(null);
			redjacket = true;
			// infobox.setBackground(null);
			charinfoimg.setTag(R.drawable.redjacketcolor);
			charinfoimg.setImageDrawable(getResources().getDrawable(
					R.drawable.redjacketcolor));
			infobox.setBackground(getResources().getDrawable(
					R.drawable.redjacketinfo));
			videothumb.setImageDrawable(getResources().getDrawable(
					R.drawable.redjacketvideo));
			Uri video = Uri.parse("android.resource://" + getPackageName()
					+ "/" + R.raw.redjacket); // do not add any extension
			videoView.setVideoURI(video);
			if (!hatchetfound) {
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.tomahawkicongrey));
				chariconinfoview.setTag(R.drawable.tomahawkicongrey);
			} else {
				chariconinfoview.setImageDrawable(getResources().getDrawable(
						R.drawable.tomahawkicon));
				chariconinfoview.setTag(R.drawable.tomahawkicon);
			}
		}
	}
	// NFC intent
	public void onNewIntent(Intent intent) {
		//string read from NFC
		String s = null;
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
		// view.setText(s);
		nfctrigger(s);
		nfctable(s);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */

	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			// Intent anim = new Intent(Exhibit_explore_view.this,
			// Animation.class);
			// startActivity(anim);

			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

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

	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
