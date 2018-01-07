package ravneet.singh2.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import ravneet.singh2.R;

import static android.content.Context.MODE_PRIVATE;

//Ravneet Singh
//n01148757
//Assignment 2

public class SinSet extends Fragment {
    ToggleButton mToggleButton;
    private SeekBar mSeekBar;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    RelativeLayout rl;
    private String background_color;
    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.sin_set, container, false);
        mToggleButton = mView.findViewById(R.id.mute_tb_3);
        mSeekBar = mView.findViewById(R.id.brightness_control_sb_3);
        radioGroup = mView.findViewById(R.id.radioGroup);
        background_color = "yellow";
        rl = mView.findViewById(R.id.root);
        int brightness = getScreenBrightness();
        mSeekBar.setProgress(brightness);

        SharedPreferences prefs = getActivity().getSharedPreferences("settings_fragment", MODE_PRIVATE);
        String mute = prefs.getString("mute", "no");
        int lol = prefs.getInt("brightness", -1);
        String color = prefs.getString("color", "no");

        if(lol != -1){
            setScreenBrightness(lol);
        }

        if(mute.equals("OFF")){
            AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

        if (mute.equals("ON")){
            AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            mToggleButton.setChecked(true);
        }

        switch(color){
            case "yellow":
                background_color = "yellow";
                radioButton = mView.findViewById(R.id.black_rb_3);
                radioButton.setChecked(true);
                rl.setBackgroundResource(R.color.yellow);
                break;
            case "green":
                background_color = "green";
                radioButton = mView.findViewById(R.id.green_rb_3);
                radioButton.setChecked(true);
                rl.setBackgroundResource(R.color.green);
                break;
            case "purple":
                background_color = "purple";
                radioButton = mView.findViewById(R.id.purple_rb_3);
                radioButton.setChecked(true);
                rl.setBackgroundResource(R.color.purple);
                break;
            default:
                break;
        }

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager notificationManager = (NotificationManager) getActivity() .getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {

                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }
                if(mToggleButton.isChecked()){
                    AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else {

                    AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                    // To set full volume
                    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
                }
            }
        });

        // Set a SeekBar change listener
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(getActivity())) {
                        // Change the screen brightness
                        setScreenBrightness(i);
                    }
                    else {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = mView.findViewById(selectedId);
                String lol = radioButton.getText().toString();
                switch(lol){
                    case "yellow":
                        background_color = "yellow";
                        rl.setBackgroundResource(R.color.yellow);
                        break;
                    case "green":
                        background_color = "green";
                        rl.setBackgroundResource(R.color.green);
                        break;
                    case "purple":
                        background_color = "purple";
                        rl.setBackgroundResource(R.color.purple);
                        break;
                }
            }
        });

        mView.findViewById(R.id.save_btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("settings_fragment", MODE_PRIVATE).edit();
                editor.putString("mute", mToggleButton.getText().toString());
                editor.putInt("brightness", SinSet.this.getScreenBrightness());
                editor.putString("color", background_color);
                editor.apply();
            }
        });
    }

    // Get the screen current brightness
    protected int getScreenBrightness(){
        return Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                0
        );
    }

    // Change the screen brightness
    public void setScreenBrightness(int brightnessValue){
        if(brightnessValue >= 0 && brightnessValue <= 255){
            Settings.System.putInt(
                    getActivity().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue
            );
        }
    }

}
