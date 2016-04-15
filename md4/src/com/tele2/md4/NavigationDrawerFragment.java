package com.tele2.md4;

import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import ru.yandex.mAng.R;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class NavigationDrawerFragment extends Fragment {
	DrawerLayout mdrawerLayout;
	ActionBarDrawerToggle mActionToggle;

	public NavigationDrawerFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		Log.v("NavigationDrawerFragment", " NavigationDrawerFragment onCreateView ");
		return inflater.inflate(R.layout.fragment_navigation_drawer, container,
				false);
	}

	public void setUp(DrawerLayout drawerLayout, Toolbar toolbar) {
		mdrawerLayout = drawerLayout;
		mActionToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
				toolbar, R.string.drawer_open, R.string.drawer_close){
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				Toast.makeText(getActivity(),
						drawerView.getClass().getName() + " open drawerView= " + drawerView.toString(),
						Toast.LENGTH_SHORT).show();
			}
			@Override
					public void onDrawerClosed(View drawerView) {
						super.onDrawerClosed(drawerView);
						Toast.makeText(getActivity(),
								drawerView.getClass().getName() + " close drawerView= " + drawerView.toString(),
								Toast.LENGTH_SHORT).show();
					}
		};
		mdrawerLayout.setDrawerListener(mActionToggle);
		mdrawerLayout.post(new Runnable() {
			
			@Override
			public void run() {
				mActionToggle.syncState();
			}
		});
		//drawerLayout.openDrawer(R.id)
	}

}
