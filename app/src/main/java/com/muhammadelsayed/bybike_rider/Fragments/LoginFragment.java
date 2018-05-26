package com.muhammadelsayed.bybike_rider.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muhammadelsayed.bybike_rider.MainActivity;
import com.muhammadelsayed.bybike_rider.Model.User;
import com.muhammadelsayed.bybike_rider.Model.UserModel;
import com.muhammadelsayed.bybike_rider.Network.RetrofitClientInstance;
import com.muhammadelsayed.bybike_rider.Network.UserClient;
import com.muhammadelsayed.bybike_rider.R;
import com.muhammadelsayed.bybike_rider.Utils.CustomToast;
import com.muhammadelsayed.bybike_rider.Utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";
    private View view;
    private FragmentManager mFragmentManager;
    private Animation mShakeAnimation;
    private EditText mEmail, mPassword;
    private CheckBox mShowHidePassword;
    private TextView mForgotPassword, mSignUp;
    private Button mLoginButton;
    private LinearLayout mLoginLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);
        initViews();
        setListeners();
        return view;
    }

    private void initViews() {
        Log.d(TAG, "initViews: initializing the view...");
        mFragmentManager = getActivity().getSupportFragmentManager();

        mEmail = view.findViewById(R.id.input_email);
        mPassword = view.findViewById(R.id.input_password);
        mLoginButton = view.findViewById(R.id.btn_login);
        mLoginLayout = view.findViewById(R.id.login_layout);
        mForgotPassword = view.findViewById(R.id.forgot_password);
        mSignUp = view.findViewById(R.id.createAccount);
        mShowHidePassword = view.findViewById(R.id.show_hide_password);
        // load shake animation
        mShakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
    }

    private void setListeners() {
        Log.d(TAG, "setListeners: setting listeners for corresponding widgets");
        mLoginButton.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);

        mShowHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mShowHidePassword.setText(R.string.hide_pwd);
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                } else {
                    mShowHidePassword.setText(R.string.show_pwd);

                    mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPassword.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Log.d(TAG, "onClick: validating input...");
                if (checkValidation()) {
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();


                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();

                    final User currentUser = new User(email, password);

                    UserClient service = RetrofitClientInstance.getRetrofitInstance()
                            .create(UserClient.class);

                    Call<UserModel> call = service.loginRider(currentUser);

                    call.enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(@NonNull Call<UserModel> call, Response<UserModel> response) {

                            if (response.body() != null) {

                                User currentUser = response.body().getUser();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra("user", currentUser);
                                startActivity(intent);

                                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                                prefEditor.putString("USER_TOKEN", response.body().getToken());
                                prefEditor.apply();

                                getActivity().finish();

                            } else {
                                Toast.makeText(getActivity(), "I have no Idea what's happening\nbut, something is terribly wrong !!", Toast.LENGTH_SHORT).show();
                            }

                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {

                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "network error !!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.createAccount:
                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUpFragment(), Utils.SignUpFragment)
                        .commit();
                break;
            case R.id.forgot_password:
                Log.d(TAG, "onClick: navigating to ForgotPasswordFragment...");

                mFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new ForgotPasswordFragment(), Utils.ForgotPasswordFragment)
                        .commit();

                break;
        }
    }

    /**
     * Validates user input before logging him in
     *
     * @return false if input is not valid, true if valid
     */

    private boolean checkValidation() {
        boolean isValid = true;
        Log.d(TAG, "checkValidation: validating user input...");
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        // checking email pattern
        Pattern pattern = Pattern.compile(Utils.regEx);
        Matcher matcher = pattern.matcher(email);

        // Check for both field is empty or not
        if (email.equals("") || email.length() == 0
                || password.equals("") || password.length() == 0) {
            mLoginLayout.startAnimation(mShakeAnimation);

            isValid = false;

            new CustomToast().showToast(getActivity(), view,
                    "Enter both credentials.");
        }

        // Check if email id is valid or not
        else if (!matcher.find()) {

            isValid = false;

            new CustomToast().showToast(getActivity(), view,
                    "Your Email Id is Invalid.");
        }

        return isValid;
    }
}