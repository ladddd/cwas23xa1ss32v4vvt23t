package com.jincaizi.kuaiwin.buycenter.sharexicai;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.jincaizi.R;
import com.jincaizi.adapters.PopViewAdapter;
import com.jincaizi.http.JinCaiZiHttpClient;
import com.jincaizi.kuaiwin.utils.Constants;
import com.jincaizi.kuaiwin.utils.Constants.PlsType;
import com.jincaizi.kuaiwin.utils.IntentAction;
import com.jincaizi.kuaiwin.utils.Utils;
import com.jincaizi.vendor.http.AsyncHttpClient;
import com.jincaizi.vendor.http.AsyncHttpResponseHandler;
import com.jincaizi.vendor.http.RequestHandle;
import com.jincaizi.vendor.http.RequestParams;

public class Fcsd extends FragmentActivity implements OnClickListener {
	protected static final String TAG = "Fcsd";
	private ArrayList<Boolean> mChecked = new ArrayList<Boolean>();
	private TextView mTitleView;
	private PopupWindow mPopWindow;
	private GridView mGridView;
	private PopViewAdapter mMyAdapter;
	private boolean isPopWindowShow = false;
	private Fragment mCurrentFragment;
	private TextView mShakeBtn;
	public PlsType plsType = Constants.PlsType.ZHIXUAN;
	int lastIndex = 0;
	private TextView mZhuShuView;
	private int mCount = 0;
	private TextView right_footer_btn;
	public int startType = 0; // 0, normal; 1, continuePiack; 2, selectedAgain
	private TextView clearPick;
	private ImageView mBack;
	private TextView mQihaoView;
	private TextView mTimeDiffView;
	private RequestHandle myRequestHandle;
	private boolean isCanSale = true;
	private String mQihao = "";
	public String mRepickZhixuanStr = "";
	public String mRepickZuliuStr = "";
	private MyCount mc = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pls);
		mChecked.add(true);
		for (int i = 1; i < 6; i++) {
			mChecked.add(false);
		}
		_findViews();
		_setListner();
		_loadFragment();
			_requestData();
	}

	private void _setListner() {
		// TODO Auto-generated method stub
		mTitleView.setOnClickListener(this);
		mShakeBtn.setOnClickListener(this);
		right_footer_btn.setOnClickListener(this);
		clearPick.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mQihaoView.setOnClickListener(this);
	}

	private void _findViews() {
		// TODO Auto-generated method stub
		mTitleView = (TextView) findViewById(R.id.current_lottery);
		mTitleView.setText("福彩3D-直选");
		mTitleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.triangle_white, 0);
		mShakeBtn = (TextView) findViewById(R.id.my_pls_shake_pick);
		mZhuShuView = (TextView) findViewById(R.id.bet_zhushu);
		right_footer_btn = (TextView) findViewById(R.id.right_footer_btn);
		clearPick = (TextView) findViewById(R.id.left_footer_btn);
		mBack = (ImageView) findViewById(R.id.touzhu_leftmenu);
		mQihaoView = (TextView)findViewById(R.id.pre_num_str);
		mTimeDiffView = (TextView)findViewById(R.id.pre_win_num);
		
		findViewById(R.id.sumbit_group_buy).setVisibility(View.GONE);
		findViewById(R.id.right_divider).setVisibility(View.GONE);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	private void _loadFragment() {
		Intent intent = getIntent();
		if (!TextUtils.isEmpty(intent.getAction())
				&& intent.getAction().equals(IntentAction.CONTINUEPICKBALL)) {
			startType = 1;
			_showFragments( FcsdZhixuanFragment.TAG);
			plsType = PlsType.ZHIXUAN;
			mTitleView.setText("福彩3D-直选");
		} else if (!TextUtils.isEmpty(intent.getAction())
				&& intent.getAction().equals(IntentAction.RETRYPICKBALL)) {
			startType = 2;
			String ballStr = intent.getStringExtra(FcsdPick.BALL);
			String betType = intent.getStringExtra(FcsdPick.BETTYPE);
			if (betType.equals(PlsType.ZHIXUAN.toString())) {
				mRepickZhixuanStr = ballStr;
				plsType = PlsType.ZHIXUAN;
				_showFragments( FcsdZhixuanFragment.TAG);
				 //FcsdZhixuanFragment zhixuanFragment = (( FcsdZhixuanFragment) mCurrentFragment);
				//zhixuanFragment.updateBallData(ballStr);
				
				mTitleView.setText("福彩3D-直选");
				mChecked.clear();
				for (int i = 0; i < 6; i++) {
					if(i == 0) {
						mChecked.add(true);
					}
					mChecked.add(false);
				}
			} else if(betType.equals(PlsType.HEZHIZHIXUAN.toString())) {
				mRepickZhixuanStr = ballStr;
				plsType = PlsType.HEZHIZHIXUAN;
				_showFragments(FcsdHZzhixuanFragment.TAG);
				
				mTitleView.setText("福彩3D-直选和值");
				mChecked.clear();
				for (int i = 0; i < 6; i++) {
					if(i == 3) {
						mChecked.add(true);
					}
					mChecked.add(false);
				}
			} else if(betType.equals(PlsType.HEZHIZULIU.toString())) {
				mRepickZuliuStr = ballStr;
				plsType = PlsType.HEZHIZULIU;
				_showFragments(FcsdHZzuliuFragment.TAG);
				
				mTitleView.setText("福彩3D-组六和值");
				mChecked.clear();
				for (int i = 0; i < 6; i++) {
					if(i == 5) {
						mChecked.add(true);
					}
					mChecked.add(false);
				}
			}else if(betType.equals(PlsType.ZULIU.toString())){
				mRepickZuliuStr = ballStr;
				plsType = PlsType.ZULIU;
				_showFragments(FcsdZuliuFragment.TAG);
				//FcsdZuliuFragment zuFragment = ((FcsdZuliuFragment) mCurrentFragment);
				//zuFragment.updateBallData(ballStr);
				mTitleView.setText("福彩3D-组六");
				mChecked.clear();
				for (int i = 0; i < 6; i++) {
					if(i == 2) {
						mChecked.add(true);
					}
					mChecked.add(false);
				}
			}
		} else {
			startType = 0;
			_showFragments( FcsdZhixuanFragment.TAG);
			plsType = PlsType.ZHIXUAN;
		}
	}

	public void setTouzhuResult(int count) {
		mZhuShuView.setText(String.valueOf(count) + "注" + 2*count + "元");
	}

	private void _initBallStr() {
		if (mCurrentFragment.getTag().equals( FcsdZhixuanFragment.TAG)) {
			 FcsdZhixuanFragment zhixuanFragment = (( FcsdZhixuanFragment) mCurrentFragment);
			mCount = zhixuanFragment.mZhushu;
		} else if (mCurrentFragment.getTag().equals(FcsdZusanFragment.TAG)) {
			FcsdZusanFragment zusanFragment = ((FcsdZusanFragment) mCurrentFragment);
			mCount = zusanFragment.mZhushu;
		} else if (mCurrentFragment.getTag().equals(FcsdZuliuFragment.TAG)) {
			FcsdZuliuFragment zuliuFragment = ((FcsdZuliuFragment) mCurrentFragment);
			mCount = zuliuFragment.mZhushu;
		} else if (mCurrentFragment.getTag().equals(FcsdHZzhixuanFragment.TAG)) {
			FcsdHZzhixuanFragment hzzxFragment = ((FcsdHZzhixuanFragment) mCurrentFragment);
			mCount = hzzxFragment.mZhushu;
		} else if (mCurrentFragment.getTag().equals(FcsdHZzusanFragment.TAG)) {
			FcsdHZzusanFragment hzzsFragment = ((FcsdHZzusanFragment) mCurrentFragment);
			mCount = hzzsFragment.mZhushu;
		} else {
			FcsdHZzuliuFragment hzzlFragment = ((FcsdHZzuliuFragment) mCurrentFragment);
			mCount = hzzlFragment.mZhushu;
		}
	}

	private void _clearChoose() {
		if (mCurrentFragment.getTag().equals( FcsdZhixuanFragment.TAG)) {
			 FcsdZhixuanFragment zhixuanFragment = (( FcsdZhixuanFragment) mCurrentFragment);
			zhixuanFragment.clearChoose();
		} else if (mCurrentFragment.getTag().equals(FcsdZusanFragment.TAG)) {
			FcsdZusanFragment zusanFragment = ((FcsdZusanFragment) mCurrentFragment);
			zusanFragment.clearChoose();
		} else if (mCurrentFragment.getTag().equals(FcsdZuliuFragment.TAG)) {
			FcsdZuliuFragment zuliuFragment = ((FcsdZuliuFragment) mCurrentFragment);
			zuliuFragment.clearChoose();
		} else if (mCurrentFragment.getTag().equals(FcsdHZzhixuanFragment.TAG)) {
			FcsdHZzhixuanFragment hzzxFragment = ((FcsdHZzhixuanFragment) mCurrentFragment);
			hzzxFragment.clearChoose();
		} else if (mCurrentFragment.getTag().equals(FcsdHZzusanFragment.TAG)) {
			FcsdHZzusanFragment hzzsFragment = ((FcsdHZzusanFragment) mCurrentFragment);
			hzzsFragment.clearChoose();
		} else {
			FcsdHZzuliuFragment hzzlFragment = ((FcsdHZzuliuFragment) mCurrentFragment);
			hzzlFragment.clearChoose();
		}
	}
	private void _updateShakePick() {
		if (mCurrentFragment.getTag().equals( FcsdZhixuanFragment.TAG)) {
			 FcsdZhixuanFragment zhixuanFragment = (( FcsdZhixuanFragment) mCurrentFragment);
			zhixuanFragment.updateBallData();
		} else if (mCurrentFragment.getTag().equals(FcsdZusanFragment.TAG)) {
			FcsdZusanFragment zusanFragment = ((FcsdZusanFragment) mCurrentFragment);
			zusanFragment.updateBallData();
		} else if (mCurrentFragment.getTag().equals(FcsdZuliuFragment.TAG)) {
			FcsdZuliuFragment zuliuFragment = ((FcsdZuliuFragment) mCurrentFragment);
			zuliuFragment.updateBallData();
		} else if (mCurrentFragment.getTag().equals(FcsdHZzhixuanFragment.TAG)) {
			FcsdHZzhixuanFragment hzzxFragment = ((FcsdHZzhixuanFragment) mCurrentFragment);
			hzzxFragment.updateBallData();
		} else if (mCurrentFragment.getTag().equals(FcsdHZzusanFragment.TAG)) {
			FcsdHZzusanFragment hzzsFragment = ((FcsdHZzusanFragment) mCurrentFragment);
			hzzsFragment.updateBallData();
		} else {
			FcsdHZzuliuFragment hzzlFragment = ((FcsdHZzuliuFragment) mCurrentFragment);
			hzzlFragment.updateBallData();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pre_num_str:
			if(TextUtils.isEmpty(mQihaoView.getText()) || !mQihaoView.getText().toString().equals("正在获取当前期号")) {
			_requestData();
			}
			break;
		case R.id.touzhu_leftmenu:
			if(mc != null) {
				mc.cancel();
			}
			finish();
			break;
		case R.id.left_footer_btn:
			_clearChoose();
			break;
		case R.id.my_pls_shake_pick:
			_updateShakePick();
			break;
		case R.id.current_lottery:
			if (!isPopWindowShow) {
				_setPopWindow();
				isPopWindowShow = true;
			}
			mPopWindow.showAsDropDown(v, -(v.getWidth() / 4), v.getTop());
			break;
		case R.id.right_footer_btn:
			if(!isCanSale) {
				Toast.makeText(getApplicationContext(), "本期销售已停止",Toast.LENGTH_SHORT).show();
				break;
			}
			if(TextUtils.isEmpty(mQihao)) {
				Toast.makeText(getApplicationContext(), "未获取到当前期号",Toast.LENGTH_SHORT).show();
				break;
			}
			_initBallStr();
			if (mCount < 1) {
				Toast.makeText(getApplicationContext(), "请至少选1注",
						Toast.LENGTH_LONG).show();
			} else {
				_startActivity();
				if(mc != null) {
					mc.cancel();
				}
				finish();
			}
			break;
		default:
			break;
		}
	}
	private void _startActivity() {
		Intent fcsdPick = new Intent();
		fcsdPick.putExtra(FcsdPick.BETTYPE, plsType.toString());
		fcsdPick.putExtra("qihao", mQihao);
		if (plsType == PlsType.ZHIXUAN) {
			fcsdPick.putStringArrayListExtra(FcsdPick.BALL,
					(( FcsdZhixuanFragment) mCurrentFragment)
							.getPlsResultList());
			fcsdPick.putStringArrayListExtra("zhushu", ((FcsdZhixuanFragment) mCurrentFragment).mZhushuList);
		} else if (plsType == PlsType.ZUSAN) {
			fcsdPick.putStringArrayListExtra(FcsdPick.BALL,
					((FcsdZusanFragment) mCurrentFragment)
							.getPlsResultList());
			fcsdPick.putStringArrayListExtra("zhushu", ((FcsdZusanFragment) mCurrentFragment).mZhushuList);
		} else if (plsType == PlsType.ZULIU) {
			fcsdPick.putStringArrayListExtra(FcsdPick.BALL,
					((FcsdZuliuFragment) mCurrentFragment)
							.getPlsResultList());
			fcsdPick.putStringArrayListExtra("zhushu", ((FcsdZuliuFragment) mCurrentFragment).mZhushuList);
		} else if (plsType == PlsType.HEZHIZHIXUAN) {
			fcsdPick.putStringArrayListExtra(FcsdPick.BALL,
					((FcsdHZzhixuanFragment) mCurrentFragment)
							.getPlsResultHezhiList());
			fcsdPick.putStringArrayListExtra("zhushu", ((FcsdHZzhixuanFragment) mCurrentFragment).mZhushuList);
		} else if (plsType == PlsType.HEZHIZUSAN) {
			fcsdPick.putStringArrayListExtra(FcsdPick.BALL,
					((FcsdHZzusanFragment) mCurrentFragment)
							.getPlsResultHezhiList());
			fcsdPick.putStringArrayListExtra("zhushu", ((FcsdHZzusanFragment) mCurrentFragment).mZhushuList);
		} else {
			fcsdPick.putStringArrayListExtra(FcsdPick.BALL,
					((FcsdHZzuliuFragment) mCurrentFragment)
							.getPlsResultHezhiList());
			fcsdPick.putStringArrayListExtra("zhushu", ((FcsdHZzuliuFragment) mCurrentFragment).mZhushuList);
		}
		if (startType == 0) {
			fcsdPick.setClass(Fcsd.this, FcsdPick.class);
			startActivity(fcsdPick);
		} else if (startType == 1) {
			setResult(RESULT_OK, fcsdPick);
		} else if (startType == 2) {
			setResult(RESULT_OK, fcsdPick);
		}
		if(mc != null) {
			mc.cancel();
		}
		finish();
	}

	private void _setPopWindow() {
		View view = LayoutInflater.from(this).inflate(R.layout.popview, null);
		mPopWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		mGridView = (GridView) view.findViewById(R.id.pop_gridview);
//		mGridView.setBackgroundColor(this.getResources().getColor(
//				android.R.color.transparent));
		mGridView.setNumColumns(3);
		ArrayList<String> list = new ArrayList<String>();
		list.add("直选");
		list.add("组三");
		list.add("组六");
		list.add("直选和值");
		list.add("组三和值");
		list.add("组六和值");
		mMyAdapter = new PopViewAdapter(this, list, mChecked);
		mGridView.setAdapter(mMyAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0) {

					mTitleView.setText("福彩3D-直选");
					plsType = Constants.PlsType.ZHIXUAN;
					_showFragments( FcsdZhixuanFragment.TAG);
					
				} else if (arg2 == 1) {

					mTitleView.setText("福彩3D-组三");
					plsType = Constants.PlsType.ZUSAN;
					_showFragments(FcsdZusanFragment.TAG);
				} else if (arg2 == 2) {

					mTitleView.setText("福彩3D-组六");
					plsType = Constants.PlsType.ZULIU;
					_showFragments(FcsdZuliuFragment.TAG);
				} else if (arg2 == 3) {

					mTitleView.setText("福彩3D-直选和值");
					plsType = Constants.PlsType.HEZHIZHIXUAN;
					_showFragments(FcsdHZzhixuanFragment.TAG);
				} else if (arg2 == 4) {

					mTitleView.setText("福彩3D-组三和值");
					plsType = Constants.PlsType.HEZHIZUSAN;
					_showFragments(FcsdHZzusanFragment.TAG);
				} else {

					mTitleView.setText("福彩3D-组六和值");
					plsType = Constants.PlsType.HEZHIZULIU;
					_showFragments(FcsdHZzuliuFragment.TAG);
				}
				mChecked.clear();
				for (int i = 0; i < 6; i++) {
					if(i == arg2) {
						mChecked.add(true);
					}
					mChecked.add(false);
				}
				mMyAdapter.notifyDataSetChanged();
				mPopWindow.dismiss();
			}
		});
		mPopWindow.setFocusable(true);
		mPopWindow.setOutsideTouchable(true);
		mPopWindow.update();
		mPopWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				Bitmap.createBitmap(1, 1, Config.ARGB_8888)));
	}

	private void _showFragments(String fragmentTag) {
		FragmentManager mFragManager = getSupportFragmentManager();
		FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
		Fragment mFragment = (Fragment) mFragManager
				.findFragmentByTag(fragmentTag);
		if (mFragment == null) {
			if (fragmentTag.equals( FcsdZhixuanFragment.TAG)) {
				mFragment = new  FcsdZhixuanFragment();
			} else if (fragmentTag.equals(FcsdHZzusanFragment.TAG)) {
				mFragment = new FcsdHZzusanFragment();
			} else if (fragmentTag.equals(FcsdHZzuliuFragment.TAG)) {
				mFragment = new FcsdHZzuliuFragment();
			} else if (fragmentTag.equals(FcsdHZzhixuanFragment.TAG)) {
				mFragment = new FcsdHZzhixuanFragment();
			} else if (fragmentTag.equals(FcsdZusanFragment.TAG)) {
				mFragment = new FcsdZusanFragment();
			} else{
				mFragment = new FcsdZuliuFragment();
			}
			mFragTransaction.add(R.id.fl_pls_pickarea, mFragment, fragmentTag);
		}
		if (mCurrentFragment != null) {
			mFragTransaction.hide(mCurrentFragment);
		}
		mFragTransaction.show(mFragment);
		mCurrentFragment = mFragment;
		mFragTransaction.commit();
	}
		private void _requestData() {
			mQihaoView.setText("正在获取当前期号");
			mTimeDiffView.setText("");
		RequestParams params = new RequestParams();
	    params.add("act", "sellqihao");
	    params.add("lotterytype", "FC3D");
		params.add("datatype", "json");
		params.add("jsoncallback",
				"jsonp" + String.valueOf(System.currentTimeMillis()));
		params.add("_", String.valueOf(System.currentTimeMillis()));
		String url = AsyncHttpClient.getUrlWithQueryString(false,
				JinCaiZiHttpClient.BASE_URL, params);
		myRequestHandle = JinCaiZiHttpClient.post(this, url,
				new AsyncHttpResponseHandler() {
					

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						try {
							String charset;
							if(Utils.isCmwapNet(Fcsd.this)) {
								charset = "utf-8";
							} else {
								charset = "gb2312";
							}
							String jsonData = new String(responseBody, charset);
							Log.d(TAG, "ssq qihao detail = " + jsonData);
							_readQihaoFromJson(jsonData);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							mQihaoView.setText("获取期号失败，点击重新获取");
							mTimeDiffView.setText("");
						} catch (Exception e) {
							e.printStackTrace();
							mQihaoView.setText("获取期号失败，点击重新获取");
							mTimeDiffView.setText("");
						} finally {
							// FIXME reader.close should be here
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						Log.d(TAG, "failure = " + error.toString());
						mQihaoView.setText("获取期号失败，点击重新获取");
						mTimeDiffView.setText("");
						Toast.makeText(Fcsd.this, "期号获取失败", Toast.LENGTH_SHORT).show();
					}
				});
	}
	private void _readQihaoFromJson(String jsonData) throws IOException {
		JsonReader reader = new JsonReader(new StringReader(jsonData));
		reader.beginObject();
		String diff = "-1";
		int result = -1;
		while(reader.hasNext()) {
			String tagName = reader.nextName();
			if(tagName.equals("msg")) {
				mQihao  = reader.nextString();
			} else if(tagName.equals("Diff")){
			    diff = reader.nextString();	
			} else if(tagName.equals("result")) {
				result = reader.nextInt();
			}
		}
		reader.endObject();
		reader.close();
		if(result == 0) {
			mQihaoView.setText("距" + mQihao + "期还有");
			if(diff.startsWith("-")) {
				isCanSale = false;
			} else {
				isCanSale = true;
				mc  = new MyCount(Long.valueOf(diff)*1000, 1000);  
		        mc.start(); 
			}
		}
	}
	   /*定义一个倒计时的内部类*/  
 class MyCount extends CountDownTimer {     
     public MyCount(long millisInFuture, long countDownInterval) {     
         super(millisInFuture, countDownInterval);     
     }     
     @Override     
     public void onFinish() {     
    	// mQihaoView.setText("正在获取当前期号"); 
         _requestData();
     }     
     @Override     
     public void onTick(long millisUntilFinished) {  
     	//Log.d("test", millisUntilFinished+"");
    	 mTimeDiffView.setText(Utils.formatDuring(millisUntilFinished));     
        
     }
 }
@Override
public void onBackPressed() {
	// TODO Auto-generated method stub
	super.onBackPressed();
	if(mc != null) {
		mc.cancel();
	}
	finish();
}
 
 
}
