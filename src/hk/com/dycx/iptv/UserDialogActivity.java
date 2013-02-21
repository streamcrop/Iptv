package hk.com.dycx.iptv;

import hk.com.dycx.iptv.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author zhangping e-mail:zp@dycx.com.hk
 * @date 2013-2-21 下午2:57:33
 * @version 1.0
 */
public class UserDialogActivity extends Activity implements OnClickListener {
	private EditText et_user_name;
	private EditText et_user_password;
	private Button btn_user_confirm;
	private Button btn_user_cancle;
	private SharedPreferences mSharedPreferences;
	private Intent startIntent;
	private String userName;
	private String userpassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_dialog_activity);
		mSharedPreferences = getSharedPreferences(Utils.SP_SPNAME, Context.MODE_PRIVATE);
		startIntent = getIntent();
		if (startIntent != null) {
			userName = startIntent.getStringExtra(Utils.SP_USER_NAME);
			userpassword = startIntent.getStringExtra(Utils.SP_USER_PASSWORD);
		}
		findView();
		setViewListener();
	}

	private void findView() {
		et_user_name = (EditText) findViewById(R.id.et_user_name);
		et_user_password = (EditText) findViewById(R.id.et_user_password);
		if (userName != null) {
			et_user_name.setText(userName);
		}
		if (userpassword != null) {
			et_user_password.setText(userpassword);
		}
		btn_user_confirm = (Button) findViewById(R.id.btn_user_confirm);
		btn_user_cancle = (Button) findViewById(R.id.btn_user_cancle);
	}
	
	private void setViewListener() {
		btn_user_confirm.setOnClickListener(this);
		btn_user_cancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_user_confirm:
			String userName = et_user_name.getText().toString();
			String userPassword = et_user_password.getText().toString();
			if (userName == null || userName.isEmpty() || userPassword == null || userPassword.isEmpty()) {
				
			}else {
				Editor edit = mSharedPreferences.edit();
				edit.putString(Utils.SP_USER_NAME, userName);
				edit.putString(Utils.SP_USER_PASSWORD, userPassword);
				edit.commit();
				Intent data = new Intent();
				setResult(10, data);
				finish();
			}
			break;
		case R.id.btn_user_cancle:
			setResult(1);
			finish();
			break;

		}
	}

}
