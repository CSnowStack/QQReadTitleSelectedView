package com.cq.qqreadtitleselectedview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cq.qqreadtitleselectedview.demo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager= (ViewPager) findViewById(R.id.viewPager);
        QQReadTitleSelectedView qqtsv=(QQReadTitleSelectedView)findViewById(R.id.qrtsv);

        viewPager.setAdapter(new SimpleAdapter(getSupportFragmentManager()));
        qqtsv.setViewPager(viewPager);
    }

    public static class SimpleAdapter extends FragmentPagerAdapter {

        public SimpleAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SimpleFragment.getInstance("Fragment " + position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class SimpleFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View contentView = inflater.inflate(R.layout.fragment_simle, container, false);
            TextView txt = (TextView) contentView.findViewById(R.id.txt);
            txt.setText(getArguments().getString("content"));
            return contentView;
        }

        public static SimpleFragment getInstance(String text) {
            SimpleFragment fragment = new SimpleFragment();
            Bundle b = new Bundle();
            b.putString("content", text);
            fragment.setArguments(b);
            return fragment;
        }
    }
}
