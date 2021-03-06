package zixun.xcb.example.sdk.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import zixun.xcb.example.sdk.AppManager;
import zixun.xcb.example.sdk.R;
import zixun.xcb.example.sdk.global.GlobalApplication;
import zixun.xcb.example.sdk.utils.AppUtils;
import zixun.xcb.example.sdk.widgets.WaitProgressDialog;


public abstract class BaseCompatActivity extends SupportActivity {
    protected GlobalApplication mApplication;
    protected WaitProgressDialog mWaitProgressDialog;
    protected Context mContext;
    protected boolean isTransAnim;

    static {
        //5.0一下兼容vector
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator(){
        //fragment切换使用默认Vertical动画
        return new DefaultVerticalAnimator();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                AppUtils.hideKeyboard(ev,view,this);
                //调用方法判断是否需要隐藏键盘
                break;
                default:
                    break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void init(Bundle savedInstanceState){
        setTheme(ThemeUtils.themeArr[SpUtils.getThemeIndex(this)][SpUtils.getNightModel(this)?1:0]);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        StatusBarUtils.setTransparent(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initData();
        initView(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
    }

    public void reload(){
        Intent intent = getIntent();
        overridePendingTransition(0,0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
    }

    /*
        初始化数据
        子类可以复写此方法初始化子类数据
     */
    protected void initData(){
        mContext = AppUtils.getContext();
        mApplication  =(GlobalApplication)getApplication();
        mWaitProgressDialog = new WaitProgressDialog(this);
        isTransAnim = true;
    }

    /*
        初始化View
        子类实现 控件绑定 视图初始化等内容
        @param savedInstanceState savedInstanceState
     */
    protected abstract void initView(Bundle savedInsatanceState);

    /*
        获取当前layout的布局ID，用于设置当前布局
        交由子类实现
        @return layout ID
     */
    protected abstract int getLayoutId();

    /*
        显示提示框

        @param msg 提示框内容字符串
     */
    protected void showProgressDialog(String msg){
        mWaitProgressDialog.setMessage(msg);
        mWaitProgressDialog.show();
    }

    /*
        隐藏提示框
     */
    protected void hideProgressDialog(){
        if(mWaitProgressDialog!=null){
            mWaitProgressDialog.dismiss();
        }
    }

    /*
        页面跳转
        @param clz 要跳转的Activity
     */
    public void startActivity(Class<?> clz){
        startActivity(new Intent(this,clz));
        if(isTransAnim)
            overridePendingTransition(R.anim.activity_start_zoom_in,R.anim.activity_start_zoom_out);
    }

    /*
        页面跳转
        @param clz 要跳转的Activity
        @param intent intent
     */
    public void startActivity(Class<?> clz,Intent intent){
        intent.setClass(this,clz);
        startActivity(intent);
        if(isTransAnim)
            overridePendingTransition(R.anim.activity_start_zoom_in,R.anim.activity_start_zoom_out);
    }

    /*
        携带数据的页面跳转

        @param clz 要跳转的Activity
        @param bundle
     */
    public void startActivity(Class<?> clz,Bundle bundle){
        Intent intent=new Intent();
        intent.setClass(this,clz);
        if(bundle!=null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if(isTransAnim)
            overridePendingTransition(R.anim.activity_start_zoom_in,R.anim.activity_start_zoom_out);
    }

    /*
         含有Bundle通过Class打开编辑界面
         @param clz
         @param bundle
         @param requestCode
     */
    public void startActivityForResult(Class<?> clz,Bundle bundle,int requestCode){
        Intent intent = new Intent();
        intent.setClass(this,clz);
        if(bundle!=null){
            intent.putExtras(bundle);
        }
        startActivityForResult(intent,requestCode);
        if(isTransAnim)
            overridePendingTransition(R.anim.activity_start_zoom_in,R.anim.activity_start_zoom_out);
    }

    public  void finish(){
        super.finish();
        if(isTransAnim)
            overridePendingTransition(R.anim.activity_finish_trans_in,R.anim.activity_finish_trans_out);
    }

    /*
        隐藏键盘
        @return 隐藏键盘结果
     */
    protected boolean hidenKeyboard(){
        //点击空白位置，隐藏软键盘
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
    }

    protected void initTitleBar(Toolbar toolbar,String title){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setNavigationIcon();
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onBackPressedSupport();
            }
        });
    }

    /*
       是否使用overridePendingTransition过渡动画
       @return 是否使用overridePendingTransition过度动画，默认使用
     */
    protected boolean IsTransAnim(boolean b){
        return isTransAnim;
    }

    /*
       设置是否使用overridePendingTransition过度动画
     */
    protected void setIsTransAnim(boolean b){
        isTransAnim=b;
    }
}
