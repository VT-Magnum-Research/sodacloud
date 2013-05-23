package org.magnum.soda.example.maint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.magnum.soda.Callback;
import org.magnum.soda.android.AndroidSoda;
import org.magnum.soda.android.AndroidSodaListener;
import org.magnum.soda.android.SodaInvokeInUi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.annotation.TargetApi;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements AndroidSodaListener {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"alice@gmail.com:aaaaa", "bob@gmail.com:bbbbb" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;


	//host
	private String mHost="172.31.55.100";
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	Context ctx_ = this;
	// UI references.
	private Button bypassButton;
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	
	private List<User> mUserList = new ArrayList<User>();
	private AndroidSodaListener asl_ = null;
	private AndroidSoda as = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		
		Properties prop = new Properties();
		 
    	try {
    		 InputStream rawResource = getResources().openRawResource(R.raw.connection);
    		 prop.load(rawResource);
    		  System.out.println("The properties are now loaded");
    		  System.out.println("properties: " + prop);
    		
    		mHost=prop.getProperty("host");
    	}
    	catch(IOException e)
    	{
    		Log.e("Property File not found",e.getLocalizedMessage());
    	}


		// Set up the login form.
    	bypassButton = (Button)findViewById(R.id.button_Bypass);
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.username_edit);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password_edit);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

	//	mLoginFormView = findViewById(R.id.login_form);
	//	mLoginStatusView = findViewById(R.id.login_status);
		//mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.signin_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		bypassButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						SharedPreferences sharedPref = ctx_.getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putString("username", "test_Username");
						editor.commit();
						
						Intent i =new Intent(ctx_, MainActivity.class);
						startActivity(i);
					}
				});
		
		
		
	}

	@Override
	public void connected(final AndroidSoda s) {
		Log.d("SODA", "connected() in LoginActivity being called.");
		this.as=s;
		authenticUser();

	}
	private void authenticUser() {
		List<Future> list = new ArrayList<Future>();

		Future<?> Result = AndroidSoda.async(new Runnable() {
			@Override
			@SodaInvokeInUi
			public void run() {
				if (as != null) {

					Log.e("SODA", "conected");
					
					Users userHandle = as.get(
							Users.class,
							Users.SVC_NAME);
					userHandle.getUsers(new Callback<List<User>>() {
								//@SodaInvokeInUi
								public void handle(List<User> arg0) {
									mUserList = arg0;
								boolean success=false;
									Iterator itr=arg0.iterator();
									while(itr.hasNext()){
										User u=(User)itr.next();
										if(u.getUsername_().equals(mEmail)&&u.getPwd_().equals(mPassword))
											success = true;
										 executeIntent(success);
									}
									
								}
							});
					Log.e("SODA", "end of authenticUser");


				}
			}
		});

		list.add(Result);
		for (Future f : list) {
			try {
				while (!f.isDone()) {

				}
				f.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		

	}
	
	public void executeIntent(final boolean success)
	{
		runOnUiThread(new Runnable()
		{
			
		@Override
		public void run() {
			Log.e("SODA", "inside executeIntent");
			// TODO Auto-generated method stub
			if (success) {
				SharedPreferences sharedPref = ctx_.getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("username", mEmail);
				editor.commit();
				
				Intent i =new Intent(ctx_, MainActivity.class);
				startActivity(i);
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			//mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			//showProgress(true);
			AndroidSoda.init(this, mHost, 8081, this);
			
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	/*@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}*/

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private Context mcontext;
		
		public UserLoginTask(Context context) {
			mcontext = context;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			//showProgress(false);

			if (success) {
				Intent i =new Intent(mcontext, MainActivity.class);
				startActivity(i);
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			//showProgress(false);
		}
	}
}
