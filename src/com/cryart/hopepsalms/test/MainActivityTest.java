/*
 * The MIT License
 * Copyright (c) 2013 Vitalik Lim (lim.vitaliy@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.cryart.hopepsalms.test;

import com.cryart.hopepsalms.MainActivity;
import com.cryart.hopepsalms.ScoresActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.cryart.hopepsalms.SimpleFilterableAdapter;
import com.cryart.hopepsalms.R;

@SuppressLint("NewApi")
public class MainActivityTest extends
ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity mActivity;
	private Instrumentation mInstrumentation;

	private TextView actionBar;
	private TextView hymnalText;

	private ListView hymnalList;
	private SimpleFilterableAdapter hymnalListAdapter;

	private EditText hymnalSearchBox;

	public MainActivityTest() {
		super("com.vitalik.myfirstapp", MainActivity.class);
	}

	public MainActivityTest(Class<MainActivity> activityClass) {
		super(activityClass);
	}

	/*
	 * Testing some pre-conditions
	 * for good measure
	 * 
	 */
	public void testPreConditions(){
		assertTrue(actionBar.getText().toString().equalsIgnoreCase("№ 1"));
		assertTrue(hymnalText.getText().toString().toLowerCase().contains("Коль славен".toLowerCase()));
		assertTrue(hymnalSearchBox.getText().toString().equalsIgnoreCase(""));
	}

	/*
	 * Testing list of hymnals in
	 * terms of number of hymns in the list and etc.
	 * 
	 */
	public void testHymnalList(){
		assertTrue(hymnalListAdapter.getItem(0).toString().equalsIgnoreCase("1 Коль славен"));
		assertTrue(hymnalListAdapter.getCount() == 385);
	}


	/*
	 * Testing if appropriate score
	 * url is being loaded to the webview
	 * after clicking on scores menu item
	 */
	public void testScore(){
		ActivityMonitor am = mInstrumentation.addMonitor(ScoresActivity.class.getName(), null, false);
		mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		mInstrumentation.invokeMenuActionSync(mActivity, R.id.menu_scores, 0);

		// Let it load from web
		// for 10s
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Activity a = mInstrumentation.waitForMonitorWithTimeout(am, 5);
		final WebView s = (WebView) a.findViewById(R.id.scores_view);
		a.runOnUiThread(new Runnable() {
			public void run(){
				// Kol Slaven
				assertTrue(s.getUrl().toString().equalsIgnoreCase("http://hopepsalms.com/android/1"));
			}
		});

		// See if scores activity was loaded
		assertEquals(true, mInstrumentation.checkMonitorHit(am, 1));
		a.finish();

		// Let's sleep for another 2s
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mInstrumentation.waitForIdleSync();
		assertEquals(true, true);
	}

	/*
	 * Testing each hymnal click and see if the title
	 * is found inside hymnal
	 * 
	 */
	public void testEachHymnal(){
		mActivity.runOnUiThread(new Runnable() {
			private String getCurrentHymnalText(){
				return hymnalText.getText().toString();
			}

			private String getHymnalListItem(int position){
				return hymnalListAdapter.getItem(position).toString();
			}

			public void run() {
				int hymnalCount = hymnalListAdapter.getCount();
				for(int i = 0; i < hymnalCount; i++){
					hymnalList.performItemClick(null, i, 0);
					assertTrue(getCurrentHymnalText().toLowerCase().contains(getHymnalListItem(i).toLowerCase()));
				}
				hymnalList.performItemClick(null, 0, 0);

			}
		});
		mInstrumentation.waitForIdleSync();
		assertEquals(true, true);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		mInstrumentation = getInstrumentation();
		actionBar = (TextView) mActivity.findViewById(R.id.action_bar_style);
		hymnalText = (TextView) mActivity.findViewById(R.id.hymnal_content);
		hymnalList = (ListView) mActivity.findViewById(R.id.id_list_view);
		hymnalListAdapter = (SimpleFilterableAdapter) hymnalList.getAdapter();
		hymnalSearchBox = (EditText) mActivity.findViewById(R.id.search_box);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
