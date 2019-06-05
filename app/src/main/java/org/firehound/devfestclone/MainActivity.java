package org.firehound.devfestclone;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomappbar.BottomAppBar;

import org.firehound.devfestclone.fragments.NavigationBottomSheetFragment;
import org.firehound.devfestclone.fragments.PinFragment;
import org.firehound.devfestclone.fragments.QRFragment;
import org.firehound.devfestclone.fragments.VoteFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import me.jfenn.attribouter.Attribouter;


public class MainActivity extends AppCompatActivity implements NavigationBottomSheetFragment.NavClickListener {
    private NavigationBottomSheetFragment navigationBottomSheetFragment = new NavigationBottomSheetFragment();
    public static int selectedFragment = 1;
    private static final String TAG = "MainActivity";
    public static final String QR_KEY = "org.firehound.timetable.QR_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        bottomAppBar.setNavigationOnClickListener(l -> {
            navigationBottomSheetFragment.show(getSupportFragmentManager(), null);
        });
        updateFragment(0);
    }

    @Override
    public void onNavItemClicked(int index) {
        try {
            navigationBottomSheetFragment.dismiss();
            selectedFragment = index;
            updateFragment(index);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateFragment(int index) {
        Fragment selFrag = null;
        switch (index){

            case 1:
                selFrag = new QRFragment();
                Log.d(TAG, String.valueOf(index));
                break;
            case 2:
                selFrag = new PinFragment();
                Log.d(TAG, String.valueOf(index));
                break;
            case 3:
                selFrag = new VoteFragment();
                Log.d(TAG, String.valueOf(index));
                break;
            case 4:
                selFrag = Attribouter.from(this).toFragment();
                Log.d(TAG, String.valueOf(index));
                break;
            default:
                selFrag = new QRFragment();



        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selFrag).commit();
    }
}
