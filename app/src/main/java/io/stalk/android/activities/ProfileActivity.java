package io.stalk.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import io.xpush.chat.common.Constants;
import io.stalk.android.R;
import io.stalk.android.fragments.ProfileFragment;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = ProfileActivity.class.getSimpleName();

    private TextView mTvNickname;
    private TextView mTvStatusMessage;
    private ProfileFragment f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        f = new ProfileFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, f, TAG).commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null) {

            if( requestCode == Constants.REQUEST_EDIT_NICKNAME ) {
                mTvNickname = (TextView) f.getView().findViewById(R.id.nickname);
                String nickname = data.getStringExtra("nickname");
                mTvNickname.setText(nickname);

                f.setNickName(nickname);
            } else if( requestCode == Constants.REQUEST_EDIT_STATUS_MESSAGE ) {
                mTvStatusMessage = (TextView) f.getView().findViewById(R.id.status_message);
                String statusMessage = data.getStringExtra("statusMessage");
                mTvStatusMessage.setText(statusMessage);

                f.setStatusMessage(statusMessage);
            } else if ( requestCode == Constants.REQUEST_EDIT_IMAGE ){
                Uri selectedImageUri = data.getData();
                f.setImage( selectedImageUri );
            }

        }  else if(resultCode == RESULT_CANCELED){
        }
    }
}
